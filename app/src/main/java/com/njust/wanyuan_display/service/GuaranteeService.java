package com.njust.wanyuan_display.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.njust.wanyuan_display.SerialPort.SerialPort;
import com.njust.wanyuan_display.action.HeartbeatAction;
import com.njust.wanyuan_display.action.OpenMachineAction;
import com.njust.wanyuan_display.action.UploadMachineStatus;
import com.njust.wanyuan_display.activity.AdvertisingActivity;
import com.njust.wanyuan_display.activity.LoginActivity;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.greenDao.MachineState;
import com.njust.wanyuan_display.greenDao.SettingInfo;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.scm.MotorControl2;
import com.njust.wanyuan_display.util.AlarmUtils;
import com.njust.wanyuan_display.util.DateUtil;
import com.njust.wanyuan_display.util.StringUtil;

import org.apache.log4j.Logger;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.njust.wanyuan_display.application.VMApplication.VMMainThreadFlag;
import static com.njust.wanyuan_display.application.VMApplication.VMMainThreadRunning;
import static com.njust.wanyuan_display.application.VMApplication.aisleZNum1;
import static com.njust.wanyuan_display.application.VMApplication.aisleZNum2;
import static com.njust.wanyuan_display.application.VMApplication.mQuery0Flag;
import static com.njust.wanyuan_display.application.VMApplication.mQuery1Flag;
import static com.njust.wanyuan_display.application.VMApplication.mQuery2Flag;
import static com.njust.wanyuan_display.application.VMApplication.mUpdataDatabaseFlag;
import static com.njust.wanyuan_display.application.VMApplication.midZNum;
import static com.njust.wanyuan_display.application.VMApplication.rimZNum1;
import static com.njust.wanyuan_display.application.VMApplication.rimZNum2;

/**
 * 处理耗时操作的服务
 */
public class GuaranteeService extends Service {

	private final Logger log = Logger.getLogger(GuaranteeService.class);
	private SerialPort serialPort485;
	private SerialPort serialPort232;
	private MotorControl2 mMotorControl2 = new MotorControl2();
	private byte[] rec;
	private int[] state = new int[42];//本来是32位，后来更改协议加了10位
	private int[] stateOld = new int[42];
	private int delay = 180;
	private boolean threadStatus = false; //线程状态，为了安全终止线程
	private byte[] test232 = new byte[]{(byte)0xE2,(byte)0x02,(byte)0x03,(byte)0x02,(byte)0xF1};
	private String currentIntent = "";
	private int noDisconnection = 0;//门控消抖
	private int heartbeatCount = 0;//防止线程被杀死
	private int uploadMachineStatusCount = 0;//防止线程被杀死
	private int updateAisleInfoListShopForTempCount = 0;//防止线程被杀死
	private int queryStateCount = 0;//防止线程被杀死
	private Timer timer;
	private TimerTask timerTask_heartbeat;
	private TimerTask timerTask_uploadMachineStatus;
	private TimerTask timerTask_updateAisleInfoListShopForTemp;
	private TimerTask timerTask_queryState;



    @Override
    public void onCreate() {
    	log.info("create GuaranteeService");
    	super.onCreate();
		serialPort485 = new SerialPort(1, 38400, 8, 'n', 1);
		serialPort232 = new SerialPort(3, 9600, 8, 'n', 1);
		timer = new Timer(true);
		guarantee_heartbeat(getApplicationContext());
		guarantee_uploadMachineStatus(getApplicationContext());
		guarantee_updateAisleInfoListShopForTemp(getApplicationContext());
		queryState(getApplicationContext());
		AlarmUtils.setAlarmManager(getApplicationContext());
//		Timer timer = new Timer();
//		timerTask = new TimerTask() {
//			@Override
//			public void run() {
//				timerTask.cancel();
//				doorState(context);
//			}
//		};
//		timer.schedule(timerTask,4000);
    }

	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	log.info("start GuaranteeService");
//    	flags = Service.START_FLAG_REDELIVERY;
//		return super.onStartCommand(intent, flags, startId);
		return START_STICKY;
    }

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	/**
	 * 心跳
	 */
	public void guarantee_heartbeat(final Context context) {
		timerTask_heartbeat = new TimerTask() {
			@Override
			public void run() {
				try {
					if(heartbeatCount > 300){
						heartbeatCount = 0;
						new HeartbeatAction().heartbeatObject(context);
					}
					heartbeatCount++;
				} catch (Exception e) {
					log.error(e.getMessage());
					e.printStackTrace();
				}
			}
		};
		timer.schedule(timerTask_heartbeat,0,100);
	}
	/**
	 * 机器状态上报
	 */
	public void guarantee_uploadMachineStatus(final Context mContext) {
		timerTask_uploadMachineStatus = new TimerTask() {
			@Override
			public void run() {
				try {
					if(uploadMachineStatusCount > 6000){//10*60*1000ms
						uploadMachineStatusCount = 0;
						new UploadMachineStatus().uploadMachineStatusObject(mContext,"");
					}
					uploadMachineStatusCount++;
				} catch (Exception e) {
					log.error(e.getMessage());
					e.printStackTrace();
				}
			}
		};
		timer.schedule(timerTask_uploadMachineStatus,0,100);
	}
	/**
	 * 温度改变后更新冷热显示(暂定半小时刷新一次)
	 */
	public void guarantee_updateAisleInfoListShopForTemp(final Context mContext) {
		timerTask_updateAisleInfoListShopForTemp = new TimerTask() {
			@Override
			public void run() {
				try {
					if(updateAisleInfoListShopForTempCount > 18000){//30*60*1000ms
						updateAisleInfoListShopForTempCount = 0;
						String leftTemp = "";
						String rightTemp = "";
						MachineState machineState = DBManager.getMachineState(mContext);
						if(machineState.getLeftCabinetTemp() != 22222){//非故障
							if(machineState.getLeftCabinetTemp() < 50){
								leftTemp = "1";
							}else if(machineState.getLeftCabinetTemp() > 200){
								leftTemp = "2";
							}else{
								leftTemp = "0";
							}
						}
						if(machineState.getRightCabinetTemp() != 22222){//非故障
							if(machineState.getRightCabinetTemp() < 50){
								rightTemp = "1";
							}else if(machineState.getRightCabinetTemp() > 200){
								rightTemp = "2";
							}else{
								rightTemp = "0";
							}
						}
						DBManager.updateAisleInfoListShopForTemp(mContext,leftTemp,rightTemp);
					}
					updateAisleInfoListShopForTempCount++;
				} catch (Exception e) {
					log.error(e.getMessage());
					e.printStackTrace();
				}
			}
		};
		timer.schedule(timerTask_updateAisleInfoListShopForTemp,0,100);
	}
	/**
	 * 通过232串口短接门控开关，通过发送接收判断通断，从而判断中柜门的开关状态
	 */
	public void doorState(final Context context) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while ( true ) {
						serialPort232.sendData(test232,test232.length);
						Thread.sleep(8);
						byte[] rec = serialPort232.receiveData();
						if(rec !=null){
							StringBuilder str1 = new StringBuilder();
							for (byte aRec : rec) {
								str1.append(Integer.toHexString(aRec & 0xFF)).append(" ");
							}
							Log.w("happy","doorState：" + str1);
							noDisconnection = 0;
						}else{
							Log.w("happy","noDisconnection");
							noDisconnection++;
							if(noDisconnection ==  Integer.MAX_VALUE){
								noDisconnection = 6;
							}
						}
						if(noDisconnection < 6){
							if(!currentIntent.equals("intentLoginActivity")) {
								Intent intentLoginActivity = new Intent(context, LoginActivity.class);
								intentLoginActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
								context.startActivity(intentLoginActivity);
								currentIntent = "intentLoginActivity";
								log.info("232断路，开门");
								Log.w("happy", "232断路，开门");
							}
						}else{
							if(!currentIntent.equals("intentAdvertisingActivity")){
								Intent intentAdvertisingActivity = new Intent(context, AdvertisingActivity.class);
								intentAdvertisingActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
								context.startActivity(intentAdvertisingActivity);
								currentIntent = "intentAdvertisingActivity";
								log.info("232连通，关门");
								Log.w("happy","232连通，关门");
							}
						}
						Thread.sleep(100);
					}
				} catch (Exception e) {
					log.error(e.getMessage());
					e.printStackTrace();
				}
			}
		});
		thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				doorState(context);
			}
		});
		thread.start();
	}


	/**
	 * 发送串口指令（字符串）
	 */
	private void sendData(byte[] buf){
		serialPort485.sendData(buf, buf.length);
	}
	/**
	 * 定时单片机状态查询，用于更新数据库，同时实现自动恒温、自动控制门加热
	 */
	public void queryState(final Context context) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while ( true ) {
						if (VMMainThreadFlag) {
							VMMainThreadRunning = true;
							if (mQuery1Flag) {
								SettingInfo settingInfo = DBManager.getSettingInfo(context);
								if(settingInfo.getLeftTempState() == 3){
									MachineState machineState = DBManager.getMachineState(context);
									if(machineState.getLeftCabinetTemp() == 22222){
										sendData(mMotorControl2.counterQuery(context,1, rimZNum1++, 2, settingInfo.getLeftSetTemp()));
									}else{
										if(machineState.getLeftCabinetTemp() > (settingInfo.getLeftSetTemp() + 50)){
											sendData(mMotorControl2.counterQuery(context,1, rimZNum1++, 0, settingInfo.getLeftSetTemp()));
										}else if(machineState.getLeftCabinetTemp() < (settingInfo.getLeftSetTemp() - 50)){
											sendData(mMotorControl2.counterQuery(context,1, rimZNum1++, 1, settingInfo.getLeftSetTemp()));
										}else if(machineState.getLeftCabinetTemp() > (settingInfo.getLeftSetTemp() - 20) && machineState.getLeftCabinetTemp() < (settingInfo.getLeftSetTemp() + 20)){
											sendData(mMotorControl2.counterQuery(context,1, rimZNum1++, 2, settingInfo.getLeftSetTemp()));
										}
									}
								}else{
									sendData(mMotorControl2.counterQuery(context,1, rimZNum1++, settingInfo.getLeftTempState(), settingInfo.getLeftSetTemp()));
								}
								SystemClock.sleep(delay);
								rec = serialPort485.receiveData();
								if (rec != null && rec.length > 10) {
//                        StringBuilder str1 = new StringBuilder();
//                        for (byte aRec : rec) {
//                            str1.append(Integer.toHexString(aRec & 0xFF)).append(" ");
//                        }
//                        log.info("查询左柜机器状态反馈：" + str1);
									if (rec[0] == (byte) 0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte) 0x0F && rec[rec.length - 2] == (byte) 0xF1 /*&& isVerify(rec)*/) {
										if (rec[6] == (byte) 0x65 && rec[3] == (byte) 0xC0) {
											if (rec[7] == (byte) 0xEF && rec[8] == (byte) 0xFF) {//压缩机温度，有符号数，0xEFFF=故障
												state[0] = 22222;
											} else {
												state[0] = ((rec[7] << 8) | (rec[8] & 0xFF));
											}
											if (rec[9] == (byte) 0xEF && rec[10] == (byte) 0xFF) {//边柜温度，有符号数，0xEFFF=故障
												state[1] = 22222;
											} else {
												state[1] = ((rec[9] << 8) | (rec[10] & 0xFF));
											}
											if (rec[11] == (byte) 0xEF && rec[12] == (byte) 0xFF) {//边柜顶部温度，有符号数，0xEFFF=故障
												state[2] = 22222;
											} else {
												state[2] = ((rec[11] << 8) | (rec[12] & 0xFF));
											}
											state[3] = rec[13] == (byte) 0x11 ? 2 : (int) rec[13];//压缩机直流风扇状态，0x00=未启动，0x01=正常，0x11=异常
											state[4] = rec[14] == (byte) 0x11 ? 2 : (int) rec[14];//边柜直流风扇状态，0x00=未启动，0x01=正常，0x11=异常
											if(rec[16] == (byte) 0xFF){
												state[5] = 22222;
												state[6] = 22222;
											}else{
												state[5] = (int) rec[15];//外部温度测量值，有符号数
												state[6] = (int) rec[16];//湿度测量值，%RH，0xFF=温湿度模块故障
											}
											state[7] = (int) rec[17];//边柜门加热状态，0=关，1=开
											state[8] = (int) rec[18];//照明灯状态，0=关，1=开
											state[9] = (int) rec[19];//下货光栅状态，0=正常，1=故障
											state[10] = (int) rec[20];//X轴出货光栅状态，0=正常，1=故障
											state[11] = (int) rec[21];//出货门开关状态，0=关，1=开，2=半开半关

											state[32] = (int) rec[22];//温控交流总电源状态（0=断开，1=接通）
											state[33] = (int) rec[23];//制冷压缩机工作状态（0=停止，1=运转）
											state[34] = (int) rec[24];//压缩机风扇工作状态（0=停止，1=运转）
											state[35] = (int) rec[25];//制热电热丝工作状态（0=断开，1=接通）
											state[36] = (int) rec[26];//循环风风扇工作状态（0=停止，1=运转）
										}
									}
								}
							}
							if (mQuery2Flag) {
								SettingInfo settingInfo = DBManager.getSettingInfo(context);
								if(settingInfo.getRightTempState() == 3){
									MachineState machineState = DBManager.getMachineState(context);
									if(machineState.getRightCabinetTemp() == 22222){
										sendData(mMotorControl2.counterQuery(context,2, rimZNum2++, 2, settingInfo.getRightSetTemp()));
									}else{
										if(machineState.getRightCabinetTemp() > (settingInfo.getRightSetTemp() + 50)){
											sendData(mMotorControl2.counterQuery(context,2, rimZNum2++, 0, settingInfo.getRightSetTemp()));
										}else if(machineState.getRightCabinetTemp() < (settingInfo.getRightSetTemp() - 50)){
											sendData(mMotorControl2.counterQuery(context,2, rimZNum2++, 1, settingInfo.getRightSetTemp()));
										}else if(machineState.getRightCabinetTemp() > (settingInfo.getRightSetTemp() - 20) && machineState.getRightCabinetTemp() < (settingInfo.getRightSetTemp() + 20)){
											sendData(mMotorControl2.counterQuery(context,2, rimZNum2++, 2, settingInfo.getRightSetTemp()));
										}
									}
								}else{
									sendData(mMotorControl2.counterQuery(context,2, rimZNum2++, settingInfo.getRightTempState(), settingInfo.getRightSetTemp()));
								}
								SystemClock.sleep(delay);
								rec = serialPort485.receiveData();
								if (rec != null && rec.length > 10) {
//                        StringBuilder str1 = new StringBuilder();
//                        for (byte aRec : rec) {
//                            str1.append(Integer.toHexString(aRec & 0xFF)).append(" ");
//                        }
//                        log.info("查询右柜机器状态反馈：" + str1);
									if (rec[0] == (byte) 0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte) 0x0F && rec[rec.length - 2] == (byte) 0xF1 /*&& isVerify(rec)*/) {
										if (rec[6] == (byte) 0x65 && rec[3] == (byte) 0xC1) {
											if (rec[7] == (byte) 0xEF && rec[8] == (byte) 0xFF) {//压缩机温度，有符号数，0xEFFF=故障
												state[12] = 22222;
											} else {
												state[12] = ((rec[7] << 8) | (rec[8] & 0xFF));
											}
											if (rec[9] == (byte) 0xEF && rec[10] == (byte) 0xFF) {//边柜温度，有符号数，0xEFFF=故障
												state[13] = 22222;
											} else {
												state[13] = ((rec[9] << 8) | (rec[10] & 0xFF));
											}
											if (rec[11] == (byte) 0xEF && rec[12] == (byte) 0xFF) {//边柜顶部温度，有符号数，0xEFFF=故障
												state[14] = 22222;
											} else {
												state[14] = ((rec[11] << 8) | (rec[12] & 0xFF));
											}
											state[15] = rec[13] == (byte) 0x11 ? 2 : (int) rec[13];//压缩机直流风扇状态，0x00=未启动，0x01=正常，0x11=异常
											state[16] = rec[14] == (byte) 0x11 ? 2 : (int) rec[14];//边柜直流风扇状态，0x00=未启动，0x01=正常，0x11=异常
											if(rec[16] == (byte) 0xFF){
												state[17] = 22222;
												state[18] = 22222;
											}else{
												state[17] = (int) rec[15];//外部温度测量值，有符号数
												state[18] = (int) rec[16];//湿度测量值，%RH，0xFF=温湿度模块故障
											}
											state[19] = (int) rec[17];//边柜门加热状态，0=关，1=开
											state[20] = (int) rec[18];//照明灯状态，0=关，1=开
											state[21] = (int) rec[19];//下货光栅状态，0=正常，1=故障
											state[22] = (int) rec[20];//X轴出货光栅状态，0=正常，1=故障
											state[23] = (int) rec[21];//出货门开关状态，0=关，1=开，2=半开半关

											state[37] = (int) rec[22];//温控交流总电源状态（0=断开，1=接通）
											state[38] = (int) rec[23];//制冷压缩机工作状态（0=停止，1=运转）
											state[39] = (int) rec[24];//压缩机风扇工作状态（0=停止，1=运转）
											state[40] = (int) rec[25];//制热电热丝工作状态（0=断开，1=接通）
											state[41] = (int) rec[26];//循环风风扇工作状态（0=停止，1=运转）
										}
									}
								}
							}
							if (mQuery0Flag) {
								sendData(mMotorControl2.centerQuery(midZNum++));
								SystemClock.sleep(delay);
								rec = serialPort485.receiveData();
								if (rec != null && rec.length > 5) {
//                        StringBuilder str1 = new StringBuilder();
//                        for (byte aRec : rec) {
//                            str1.append(Integer.toHexString(aRec & 0xFF)).append(" ");
//                        }
//                        log.info("查询中柜机器状态反馈：" + str1);
									if (rec[0] == (byte) 0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte) 0x0F && rec[rec.length - 2] == (byte) 0xF1 /*&& isVerify(rec)*/) {
										if (rec[6] == (byte) 0x6D && rec[3] == (byte) 0xE0) {
											state[24] = (int) rec[7];//照明灯状态，0=关，1=开
											state[25] = (int) rec[8];//门锁状态，0=上锁，1=开锁
											state[26] = (int) rec[9];//门开关状态，0=关门，1=开门
											state[27] = (int) rec[10];//取货光栅状态，0=正常，1=故障
											state[28] = (int) rec[11];//落货光栅状态，0=正常，1=故障
											state[29] = (int) rec[12];//防夹手光栅状态，0=正常，1=故障
											state[30] = (int) rec[13];//取货门开关状态，0=关，1=开，2=半开半关
											state[31] = (int) rec[14];//落货门开关状态，0=关，1=开，2=半开半关
										}
									}
								}
							}
							if (mUpdataDatabaseFlag) {
								if(stateOld[26] != state[26] || stateOld[25] != state[25]){//中间门门开关、中间门门锁状态
									DBManager.updateDoorState(context,state[26],state[25]);
									stateOld[26] = state[26]; stateOld[25] = state[25];
									log.info("更新门开关、中间门门锁状态");
								}
								if(stateOld[0] != state[0] || stateOld[1] != state[1] || stateOld[2] != state[2] || stateOld[5] != state[5] || stateOld[6] != state[6] ||
										stateOld[12] != state[12] || stateOld[13] != state[13] || stateOld[14] != state[14] || stateOld[17] != state[17] || stateOld[18] != state[18]){//压缩机温度、边柜温度、边柜顶部温度、外部温度、湿度
									DBManager.updateMeasureTemp(context,state[0],state[1],state[2],state[5],state[6],state[12],state[13],state[14],state[17],state[18]);
									stateOld[0] = state[0]; stateOld[1] = state[1]; stateOld[2] = state[2]; stateOld[5] = state[5]; stateOld[6] = state[6];
									stateOld[12] = state[12]; stateOld[13] = state[13]; stateOld[14] = state[14]; stateOld[17] = state[17]; stateOld[18] = state[18];
									log.info("更新压缩机温度、边柜温度、边柜顶部温度、外部温度、湿度");
								}
								if(stateOld[3] != state[3] || stateOld[4] != state[4] ||
										stateOld[15] != state[15] || stateOld[16] != state[16]){//压缩机直流风扇状态和边柜直流风扇状态
									DBManager.updateDCfan(context,state[3],state[4],state[15],state[16]);
									stateOld[3] = state[3]; stateOld[4] = state[4]; stateOld[15] = state[15]; stateOld[16] = state[16];
									log.info("更新压缩机直流风扇状态和边柜直流风扇状态");
								}
								if(stateOld[7] != state[7] || stateOld[19] != state[19]){//门加热
									DBManager.updateCounterDoorState(context,state[7],state[19]);
									stateOld[7] = state[7]; stateOld[19] = state[19];
									log.info("更新门加热");
								}
								if(stateOld[8] != state[8] || stateOld[20] != state[20] || stateOld[24] != state[24]){//灯状态有变化
									DBManager.updateLight(context,state[8],state[20],state[24]);
									stateOld[8] = state[8]; stateOld[20] = state[20]; stateOld[24] = state[24];
									log.info("更新灯");
								}
								if(stateOld[9] != state[9] || stateOld[10] != state[10] || stateOld[21] != state[21] || stateOld[22] != state[22] ||
										stateOld[27] != state[27] || stateOld[28] != state[28] || stateOld[29] != state[29]){//边柜下货光栅状态、X轴出货光栅状态，中柜取货光栅状态、落货光栅状态、防夹手光栅状态
									DBManager.updateRasterState(context,state[9],state[10],state[21],state[22],state[27],state[28],state[29]);
									stateOld[9] = state[9]; stateOld[10] = state[10]; stateOld[21] = state[21]; stateOld[22] = state[22];
									stateOld[27] = state[27]; stateOld[28] = state[28]; stateOld[29] = state[29];
									log.info("更新光栅状态");
								}
								if(stateOld[11] != state[11] || stateOld[23] != state[23] || stateOld[30] != state[30] || stateOld[31] != state[31]){//出货门、取货门、落货门开关状态
									DBManager.updateOtherDoorState(context,state[11],state[23],state[30],state[31]);
									stateOld[11] = state[11]; stateOld[23] = state[23]; stateOld[30] = state[30];stateOld[31] = state[31];
									log.info("更新出货门、取货门、落货门开关状态");
								}
								if(stateOld[32] != state[32] || stateOld[33] != state[33] || stateOld[34] != state[34] || stateOld[35] != state[35] || stateOld[36] != state[36] ||
										stateOld[37] != state[37] || stateOld[38] != state[38] || stateOld[39] != state[39] || stateOld[40] != state[40] || stateOld[41] != state[41]){//温控交流总电源状态、制冷压缩机工作状态、压缩机风扇工作状态、制热电热丝工作状态、循环风风扇工作状态
									DBManager.updateFan(context,state[32],state[33],state[34],state[35],state[36],state[37],state[38],state[39],state[40],state[41]);
									stateOld[32] = state[32]; stateOld[33] = state[33]; stateOld[34] = state[34];stateOld[35] = state[35];stateOld[36] = state[36];
									stateOld[37] = state[37]; stateOld[38] = state[38]; stateOld[39] = state[39];stateOld[40] = state[40];stateOld[41] = state[41];
									log.info("温控交流总电源状态、制冷压缩机工作状态、压缩机风扇工作状态、制热电热丝工作状态、循环风风扇工作状态");
								}
							}
							if(!(new BindingManager().getHumidityLimit(context)).equals("")){
								if(state[6] != 22222 && state[18] != 22222){
									if(state[6] > Integer.parseInt(new BindingManager().getHumidityLimit(context))){
										sendData(mMotorControl2.counterCommand(1, rimZNum1++, 3));
									}else{
										sendData(mMotorControl2.counterCommand(1, rimZNum1++, 4));
									}
									if(state[18] > Integer.parseInt(new BindingManager().getHumidityLimit(context))){
										sendData(mMotorControl2.counterCommand(2, rimZNum2++, 3));
									}else{
										sendData(mMotorControl2.counterCommand(2, rimZNum2++, 4));
									}
								}else if(state[6] == 22222 && state[18] != 22222){
									if(state[18] > Integer.parseInt(new BindingManager().getHumidityLimit(context))){
										sendData(mMotorControl2.counterCommand(1, rimZNum1++, 3));
										SystemClock.sleep(delay);
										sendData(mMotorControl2.counterCommand(2, rimZNum2++, 3));
									}else{
										sendData(mMotorControl2.counterCommand(1, rimZNum1++, 4));
										SystemClock.sleep(delay);
										sendData(mMotorControl2.counterCommand(2, rimZNum2++, 4));
									}
								}else if(state[6] != 22222 && state[18] == 22222){
									if(state[6] > Integer.parseInt(new BindingManager().getHumidityLimit(context))){
										sendData(mMotorControl2.counterCommand(1, rimZNum1++, 3));
										SystemClock.sleep(delay);
										sendData(mMotorControl2.counterCommand(2, rimZNum2++, 3));
									}else{
										sendData(mMotorControl2.counterCommand(1, rimZNum1++, 4));
										SystemClock.sleep(delay);
										sendData(mMotorControl2.counterCommand(2, rimZNum2++, 4));
									}
								}
							}
							if(new BindingManager().getAutoLight(context).equals("open")){
								int[] time = new BindingManager().getAutoLightTime(context);
								if(time[0] != -1 && time[1] != -1 && time[2] != -1 && time[3] != -1){
									String openTime = String.valueOf(time[0])+":"+ String.valueOf(time[1])+":"+"00";
									String closeTime = String.valueOf(time[2])+":"+ String.valueOf(time[3])+":"+"00";
									if(DateUtil.isInDate(System.currentTimeMillis(), openTime, closeTime)){//在开灯时间段内
										if(DBManager.getMachineState(context).getLeftLight() == 0){//现在灯是关着的
											sendData(mMotorControl2.counterCommand(1, rimZNum1++, 1));
										}
										SystemClock.sleep(delay);
										if(DBManager.getMachineState(context).getRightLight() == 0){//现在灯是关着的
											sendData(mMotorControl2.counterCommand(2, rimZNum2++, 1));
										}
									}else{
										if(DBManager.getMachineState(context).getLeftLight() == 1){//现在灯是开着的
											sendData(mMotorControl2.counterCommand(1, rimZNum1++, 2));
										}
										SystemClock.sleep(delay);
										if(DBManager.getMachineState(context).getLeftLight() == 1){//现在灯是开着的
											sendData(mMotorControl2.counterCommand(2, rimZNum2++, 2));
										}
									}
								}
							}
							VMMainThreadRunning = false;
							SystemClock.sleep(2*60*1000);
						}
						updateZhen();
					}

				} catch (Exception e) {
					log.error(e.getMessage());
					e.printStackTrace();
				}
			}
		});
		thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				queryState(context);
			}
		});
		thread.start();
	}
	private void updateZhen(){
		if (rimZNum1 >= 256) {
			rimZNum1 = 0;
		}
		if (rimZNum2 >= 256) {
			rimZNum2 = 0;
		}
		if (aisleZNum1 >= 256) {
			aisleZNum1 = 0;
		}
		if (aisleZNum2 >= 256) {
			aisleZNum2 = 0;
		}
		if (midZNum >= 256) {
			midZNum = 0;
		}
	}
	
}
