package com.njust.wanyuan_display.thread;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.njust.wanyuan_display.SerialPort.SerialPort;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.greenDao.MachineState;
import com.njust.wanyuan_display.greenDao.SettingInfo;
import com.njust.wanyuan_display.scm.MotorControl;

import org.apache.log4j.Logger;

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

public class VMMainThread extends Thread {
    private final Logger log = Logger.getLogger(VMMainThread.class);
    private Context context;
    private SerialPort serialPort485;
    private MotorControl motorControl;
    private SettingInfo settingInfo;
    private byte[] rec;
    private int[] state = new int[32];
    private int[] stateOld = new int[32];
    private int delay = 60;

    private int oldLeftDoor = 1;//1=关门0=开门
    private int oldRightDoor = 1;
    private int oldMidDoor = 2;

    public VMMainThread(Context context) {
        super();
        this.context = context;
        serialPort485 = new SerialPort(1, 38400, 8, 'n', 1);
        motorControl = new MotorControl(serialPort485, context);
    }


    public void initMainThread() {

        VMMainThreadFlag = true;
        mQuery1Flag = true;
        mQuery2Flag = true;
        mQuery0Flag = true;
        mUpdataDatabaseFlag = true;
        MachineState machineState = DBManager.getMachineState(context);
        if(machineState != null){
            if(machineState.getMidLight() == 1){
                motorControl.centerCommand(midZNum++, 1);
                SystemClock.sleep(5);
            }else{
                motorControl.centerCommand(midZNum++, 2);
                SystemClock.sleep(5);
            }
            if(machineState.getLeftLight() == 1){
                motorControl.counterCommand(1, rimZNum1++, 1);
                SystemClock.sleep(5);
            }else{
                motorControl.counterCommand(1, rimZNum1++, 2);
                SystemClock.sleep(5);
            }
            if(machineState.getRightLight() == 1){
                motorControl.counterCommand(2, rimZNum1++, 1);
                SystemClock.sleep(5);
            }else{
                motorControl.counterCommand(2, rimZNum1++, 2);
                SystemClock.sleep(5);
            }
            if(machineState.getLeftDoorheat() == 1){
                motorControl.counterCommand(1, rimZNum1++, 3);
                SystemClock.sleep(5);
            }else{
                motorControl.counterCommand(1, rimZNum1++, 4);
                SystemClock.sleep(5);
            }
            if(machineState.getRightDoorheat() == 1){
                motorControl.counterCommand(2, rimZNum2++, 3);
                SystemClock.sleep(5);
            }else{
                motorControl.counterCommand(2, rimZNum2++, 4);
                SystemClock.sleep(5);
            }
        }
        log.info("初始化主线程,控制门加热、灯为上次保存到数据库的状态");
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            if (VMMainThreadFlag) {
                VMMainThreadRunning = true;
                if (mQuery1Flag) {
                    settingInfo = DBManager.getSettingInfo(context);
                    motorControl.counterQuery(1, rimZNum1++, settingInfo.getLeftTempState(), settingInfo.getLeftSetTemp());
                    SystemClock.sleep(delay);
                    rec = serialPort485.receiveData();
                    if (rec != null && rec.length > 10) {
//                        StringBuilder str1 = new StringBuilder();
//                        for (byte aRec : rec) {
//                            str1.append(Integer.toHexString(aRec & 0xFF)).append(" ");
//                        }
//                         log.info("查询左柜机器状态反馈：" + str1);
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
                            }
                        }
                    }
                }
                if (mQuery2Flag) {
                    motorControl.counterQuery(2, rimZNum2++, settingInfo.getRightTempState(), settingInfo.getRightSetTemp());
                    SystemClock.sleep(delay);
                    rec = serialPort485.receiveData();
                    if (rec != null && rec.length > 10) {
//                        StringBuilder str1 = new StringBuilder();
//                        for (byte aRec : rec) {
//                            str1.append(Integer.toHexString(aRec & 0xFF)).append(" ");
//                        }
//                         log.info("查询右柜机器状态反馈：" + str1);
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
                            }
                        }
                    }
                }
                if (mQuery0Flag) {
                    motorControl.centerQuery(midZNum++);
                    SystemClock.sleep(delay);
                    rec = serialPort485.receiveData();
                    if (rec != null && rec.length > 5) {
//                        StringBuilder str1 = new StringBuilder();
//                        for (byte aRec : rec) {
//                            str1.append(Integer.toHexString(aRec & 0xFF)).append(" ");
//                        }
//                         log.info("查询中柜机器状态反馈：" + str1);
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
//                         log.info("更新压缩机温度、边柜温度、边柜顶部温度、外部温度、湿度");
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

                    if(oldMidDoor != state[26]){//中柜门
                        if(state[26] == 0){//开门
                            Intent intent = new Intent();
                            intent.setAction("njust_midDoor_status");
                            intent.putExtra("status", "open");
                            context.sendBroadcast(intent);
                             log.info("开门");
                        }else{//关门
                            Intent intent = new Intent();
                            intent.setAction("njust_midDoor_status");
                            intent.putExtra("status", "close");
                            context.sendBroadcast(intent);
                             log.info("关门");
                        }
                        oldMidDoor = state[26];
                    }
                }
                VMMainThreadRunning = false;
                SystemClock.sleep(50);
            }
            updateZhen();
        }
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
