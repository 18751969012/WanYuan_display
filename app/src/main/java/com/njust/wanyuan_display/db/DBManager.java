package com.njust.wanyuan_display.db;


import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njust.wanyuan_display.config.Constant;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.greenDao.Adv;
import com.njust.wanyuan_display.greenDao.AdvDao;
import com.njust.wanyuan_display.greenDao.AisleInfo;
import com.njust.wanyuan_display.greenDao.AisleInfoDao;
import com.njust.wanyuan_display.greenDao.AisleMerge;
import com.njust.wanyuan_display.greenDao.AisleMergeDao;
import com.njust.wanyuan_display.greenDao.GoodsInfo;
import com.njust.wanyuan_display.greenDao.GoodsInfoDao;
import com.njust.wanyuan_display.greenDao.MachineInfo;
import com.njust.wanyuan_display.greenDao.MachineInfoDao;
import com.njust.wanyuan_display.greenDao.MachineState;
import com.njust.wanyuan_display.greenDao.MachineStateDao;
import com.njust.wanyuan_display.greenDao.OrderRecord;
import com.njust.wanyuan_display.greenDao.OrderRecordDao;
import com.njust.wanyuan_display.greenDao.SettingInfo;
import com.njust.wanyuan_display.greenDao.SettingInfoDao;
import com.njust.wanyuan_display.greenDao.Transaction;
import com.njust.wanyuan_display.greenDao.TransactionDao;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.util.DateUtil;
import com.njust.wanyuan_display.util.MachineUtil;
import com.njust.wanyuan_display.util.MySecurity;

import org.apache.log4j.Logger;
import org.greenrobot.greendao.query.QueryBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;


/**
 * 数据库工具类
 */
public class DBManager {
	
    private static final Logger log = Logger.getLogger(DBManager.class);
	private static DecimalFormat df   =   new DecimalFormat("#####0.00");
	/*********************************************** MachineInfo 机器基本信息***********************************************/

	/**
	 * 保存机器信息
	 */
	public static void saveMachineInfo(Context context) {
		try {
			if (CacheDBUtil.getInstance(context).getMachineInfoDao().count() !=1) {
				String MachineID = new BindingManager().getMachineID(context);

				MachineInfo machineInfo = new MachineInfo();;
				machineInfo.setMachineID(MachineID);
				machineInfo.setUserID("");
				machineInfo.setUserName("");
				machineInfo.setUserPassword("");
				machineInfo.setLoginName(MySecurity.md5(Resources.initial_login_name));
				machineInfo.setLoginPassword(MySecurity.md5(Resources.initial_login_password));
				machineInfo.setBoard(new MachineUtil().board());
				machineInfo.setSerial(new MachineUtil().serial());
				machineInfo.setAndroidVersion(new MachineUtil().androidversion());
				machineInfo.setAndroidsdk(new MachineUtil().androidsdk());
				machineInfo.setVersion(new MachineUtil().apkVersion(context));
				machineInfo.setHotline("");
				machineInfo.setSlogan("");
				machineInfo.setCreate_time(DateUtil.getCurrentTime());

				CacheDBUtil.getInstance(context).getMachineInfoDao().insertOrReplace(machineInfo);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 更新机器信息(授权后调用)
	 */
	public static void updateMachineInfo(Context context, String userId, String userName, String userPassword) {
		MachineInfoDao machineInfoDao = CacheDBUtil.getInstance(context).getMachineInfoDao();

		String MachineID = new BindingManager().getMachineID(context);

		QueryBuilder<MachineInfo> qb = machineInfoDao.queryBuilder();
		qb.where(MachineInfoDao.Properties.MachineID.eq(MachineID));

		if ( qb.list().size()>0 ) {
			MachineInfo machineInfo = qb.list().get(0);
			machineInfo.setUserID(userId);
			machineInfo.setUserName(userName);
			machineInfo.setUserPassword(userPassword);
			machineInfoDao.update(machineInfo);
		}
	}
	/**
	 * 获得机器信息
	 */
	public static MachineInfo getMachineInfo(Context context) {
		String MachineID = new BindingManager().getMachineID(context);

		QueryBuilder<MachineInfo> qb = CacheDBUtil.getInstance(context).getMachineInfoDao().queryBuilder();
		qb.where(MachineInfoDao.Properties.MachineID.eq(MachineID));

		return qb.list().size()>0 ?qb.list().get(0) :null;
	}
	/**
	 * 获得用户ID
	 */
	public static String userId(Context context) {
		MachineInfo machineInfo = getMachineInfo(context);
		return machineInfo!=null ?machineInfo.getUserID() :"";
	}
	/**
	 * 获得用户绑定登录名
	 */
	public static String userName(Context context) {
		MachineInfo machineInfo = getMachineInfo(context);

		return machineInfo!=null ?machineInfo.getUserName() :"";
	}
	/**
	 * 获得用户绑定登录密码
	 */
	public static String userPassword(Context context) {
		MachineInfo machineInfo = getMachineInfo(context);

		return machineInfo!=null ?machineInfo.getUserPassword() :"";
	}
/*********************************************** MachineState 机器状态 ***********************************************/

	/**
	 * 初始化机器状态
	 */
	public static void initialMachineState(Context context) {
		try {
			String MachineID = new BindingManager().getMachineID(context);
			MachineStateDao machineStateDao = CacheDBUtil.getInstance(context).getMachineStateDao();
			QueryBuilder<MachineState> qb = machineStateDao.queryBuilder();
			qb.where(MachineStateDao.Properties.MachineID.eq(MachineID));
			if(qb.list().size() > 0){
				MachineState machineState = qb.list().get(0);
				machineState.setMachineID(MachineID);
				machineState.setVmState(1);
				machineState.setLeftState(1);
				machineState.setRightState(1);

				machineState.setLeftCabinetTemp(0);
				machineState.setLeftCabinetTopTemp(0);
				machineState.setLeftCompressorTemp(0);
				machineState.setLeftCompressorDCfanState(0);
				machineState.setLeftCabinetDCfanState(0);
				machineState.setLeftOutCabinetTemp(10);
				machineState.setLeftDoorheat(0);
				machineState.setLeftHumidity(10);
				machineState.setLeftLight(0);
				machineState.setLeftPushGoodsRaster(0);
				machineState.setLeftOutGoodsRaster(0);
				machineState.setLeftOutGoodsDoor(0);

				machineState.setRightCabinetTemp(0);
				machineState.setRightCabinetTopTemp(0);
				machineState.setRightCompressorTemp(0);
				machineState.setRightCompressorDCfanState(0);
				machineState.setRightCabinetDCfanState(0);
				machineState.setRightOutCabinetTemp(10);
				machineState.setRightDoorheat(0);
				machineState.setRightHumidity(10);
				machineState.setRightLight(0);
				machineState.setRightPushGoodsRaster(0);
				machineState.setRightOutGoodsRaster(0);
				machineState.setRightOutGoodsDoor(0);

				machineState.setMidLight(0);
				machineState.setMidDoorLock(0);
				machineState.setMidDoor(1);
				machineState.setMidGetGoodsRaster(0);
				machineState.setMidDropGoodsRaster(0);
				machineState.setMidAntiPinchHandRaster(0);
				machineState.setMidGetDoor(0);
				machineState.setMidDropDoor(0);

				machineState.setLeftTempControlAlternatPower(0);
				machineState.setLeftRefrigerationCompressorState(0);
				machineState.setLeftCompressorFanState(0);
				machineState.setLeftHeatingWireState(0);
				machineState.setLeftRecirculatAirFanState(0);
				machineState.setLeftLiftPlatformDownSwitch(1);
				machineState.setLeftLiftPlatformUpSwitch(0);
				machineState.setLeftLiftPlatformOutGoodsSwitch(1);
				machineState.setLeftOutGoodsRasterImmediately(0);
				machineState.setLeftPushGoodsRasterImmediately(0);
				machineState.setLeftMotorFeedbackState1(0);
				machineState.setLeftMotorFeedbackState2(0);
				machineState.setLeftOutGoodsDoorDownSwitch(1);
				machineState.setLeftOutGoodsDoorUpSwitch(0);

				machineState.setRightTempControlAlternatPower(0);
				machineState.setRightRefrigerationCompressorState(0);
				machineState.setRightCompressorFanState(0);
				machineState.setRightHeatingWireState(0);
				machineState.setRightRecirculatAirFanState(0);
				machineState.setRightLiftPlatformDownSwitch(1);
				machineState.setRightLiftPlatformUpSwitch(0);
				machineState.setRightLiftPlatformOutGoodsSwitch(1);
				machineState.setRightOutGoodsRasterImmediately(0);
				machineState.setRightPushGoodsRasterImmediately(0);
				machineState.setRightMotorFeedbackState1(0);
				machineState.setRightMotorFeedbackState2(0);
				machineState.setRightOutGoodsDoorDownSwitch(1);
				machineState.setRightOutGoodsDoorUpSwitch(0);

				machineState.setMidGetDoorWaitClose(0);
				machineState.setMidGetDoorDownSwitch(0);
				machineState.setMidGetDoorUpSwitch(1);
				machineState.setMidDropDoorDownSwitch(0);
				machineState.setMidDropDoorUpSwitch(0);

				machineStateDao.update(machineState);
			}else{
				MachineState machineState = new MachineState();
				machineState.setMachineID(MachineID);
				machineState.setVmState(1);
				machineState.setLeftState(1);
				machineState.setRightState(1);

				machineState.setLeftCabinetTemp(0);
				machineState.setLeftCabinetTopTemp(0);
				machineState.setLeftCompressorTemp(0);
				machineState.setLeftCompressorDCfanState(0);
				machineState.setLeftCabinetDCfanState(0);
				machineState.setLeftOutCabinetTemp(10);
				machineState.setLeftDoorheat(0);
				machineState.setLeftHumidity(10);
				machineState.setLeftLight(0);
				machineState.setLeftPushGoodsRaster(0);
				machineState.setLeftOutGoodsRaster(0);
				machineState.setLeftOutGoodsDoor(0);

				machineState.setRightCabinetTemp(0);
				machineState.setRightCabinetTopTemp(0);
				machineState.setRightCompressorTemp(0);
				machineState.setRightCompressorDCfanState(0);
				machineState.setRightCabinetDCfanState(0);
				machineState.setRightOutCabinetTemp(10);
				machineState.setRightDoorheat(0);
				machineState.setRightHumidity(10);
				machineState.setRightLight(0);
				machineState.setRightPushGoodsRaster(0);
				machineState.setRightOutGoodsRaster(0);
				machineState.setRightOutGoodsDoor(0);

				machineState.setMidLight(0);
				machineState.setMidDoorLock(0);
				machineState.setMidDoor(1);
				machineState.setMidGetGoodsRaster(0);
				machineState.setMidDropGoodsRaster(0);
				machineState.setMidAntiPinchHandRaster(0);
				machineState.setMidGetDoor(0);
				machineState.setMidDropDoor(0);

				machineState.setLeftTempControlAlternatPower(0);
				machineState.setLeftRefrigerationCompressorState(0);
				machineState.setLeftCompressorFanState(0);
				machineState.setLeftHeatingWireState(0);
				machineState.setLeftRecirculatAirFanState(0);
				machineState.setLeftLiftPlatformDownSwitch(1);
				machineState.setLeftLiftPlatformUpSwitch(0);
				machineState.setLeftLiftPlatformOutGoodsSwitch(1);
				machineState.setLeftOutGoodsRasterImmediately(0);
				machineState.setLeftPushGoodsRasterImmediately(0);
				machineState.setLeftMotorFeedbackState1(0);
				machineState.setLeftMotorFeedbackState2(0);
				machineState.setLeftOutGoodsDoorDownSwitch(1);
				machineState.setLeftOutGoodsDoorUpSwitch(0);

				machineState.setRightTempControlAlternatPower(0);
				machineState.setRightRefrigerationCompressorState(0);
				machineState.setRightCompressorFanState(0);
				machineState.setRightHeatingWireState(0);
				machineState.setRightRecirculatAirFanState(0);
				machineState.setRightLiftPlatformDownSwitch(1);
				machineState.setRightLiftPlatformUpSwitch(0);
				machineState.setRightLiftPlatformOutGoodsSwitch(1);
				machineState.setRightOutGoodsRasterImmediately(0);
				machineState.setRightPushGoodsRasterImmediately(0);
				machineState.setRightMotorFeedbackState1(0);
				machineState.setRightMotorFeedbackState2(0);
				machineState.setRightOutGoodsDoorDownSwitch(1);
				machineState.setRightOutGoodsDoorUpSwitch(0);

				machineState.setMidGetDoorWaitClose(0);
				machineState.setMidGetDoorDownSwitch(0);
				machineState.setMidGetDoorUpSwitch(1);
				machineState.setMidDropDoorDownSwitch(0);
				machineState.setMidDropDoorUpSwitch(0);

				CacheDBUtil.getInstance(context).getMachineStateDao().insertOrReplace(machineState);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 更新机器状态，整机状态，左右柜状态
	 * state:1正常 2存在故障 3停机
	 */
	public static void updateMachineState(Context context,int vmState, int leftState, int rightState) {
		String MachineID = new BindingManager().getMachineID(context);
		MachineStateDao machineStateDao = CacheDBUtil.getInstance(context).getMachineStateDao();
		QueryBuilder<MachineState> qb = machineStateDao.queryBuilder();
		qb.where(MachineStateDao.Properties.MachineID.eq(MachineID));
		if(qb.list().size() > 0) {
			MachineState machineState = qb.list().get(0);
			if(vmState != 0){
				machineState.setVmState(vmState);
			}
			if(leftState != 0){
				machineState.setLeftState(leftState);
			}
			if(rightState != 0){
				machineState.setRightState(rightState);
			}
			machineStateDao.update(machineState);
		}
	}

	/**
	 * 更新中间门锁、门状态 0=关门，1=开门
	 */
	public static void updateDoorState(Context context, int midDoor, int midDoorLock) {
		String MachineID = new BindingManager().getMachineID(context);
		MachineStateDao machineStateDao = CacheDBUtil.getInstance(context).getMachineStateDao();
		QueryBuilder<MachineState> qb = machineStateDao.queryBuilder();
		qb.where(MachineStateDao.Properties.MachineID.eq(MachineID));
		if(qb.list().size() > 0){
			MachineState machineState = qb.list().get(0);
			machineState.setMidDoor(midDoor);
			machineState.setMidDoorLock(midDoorLock);
			machineStateDao.update(machineState);
		}
	}
	/**
	 * 更新压缩机温度、边柜温度、边柜顶部温度、边柜外部温度、湿度测量值
	 */
	public static void updateMeasureTemp(Context context, int leftCompressorTemp, int leftCabinetTemp, int leftCabinetTopTemp, int leftOutCabinetTemp, int leftHumidity,
                                         int rightCompressorTemp, int rightCabinetTemp, int rightCabinetTopTemp, int rightOutCabinetTemp, int rightHumidity) {
		String MachineID = new BindingManager().getMachineID(context);
		MachineStateDao machineStateDao = CacheDBUtil.getInstance(context).getMachineStateDao();
		QueryBuilder<MachineState> qb = machineStateDao.queryBuilder();
		qb.where(MachineStateDao.Properties.MachineID.eq(MachineID));
		if(qb.list().size() > 0){
			MachineState machineState = qb.list().get(0);
			machineState.setLeftCompressorTemp(leftCompressorTemp);
			machineState.setLeftCabinetTemp(leftCabinetTemp);
			machineState.setLeftCabinetTopTemp(leftCabinetTopTemp);
			machineState.setLeftOutCabinetTemp(leftOutCabinetTemp);
			machineState.setLeftHumidity(leftHumidity);

			machineState.setRightCompressorTemp(rightCompressorTemp);
			machineState.setRightCabinetTemp(rightCabinetTemp);
			machineState.setRightCabinetTopTemp(rightCabinetTopTemp);
			machineState.setRightOutCabinetTemp(rightOutCabinetTemp);
			machineState.setRightHumidity(rightHumidity);

			machineStateDao.update(machineState);
		}
	}
	/**
	 * 更新压缩机直流风扇状态和边柜直流风扇状态
	 */
	public static void updateDCfan(Context context, int leftCompressorDCfanState, int leftCabinetDCfanState, int rightCompressorDCfanState, int rightCabinetDCfanState) {
		String MachineID = new BindingManager().getMachineID(context);
		MachineStateDao machineStateDao = CacheDBUtil.getInstance(context).getMachineStateDao();
		QueryBuilder<MachineState> qb = machineStateDao.queryBuilder();
		qb.where(MachineStateDao.Properties.MachineID.eq(MachineID));
		if(qb.list().size() > 0){
			MachineState machineState = qb.list().get(0);
			machineState.setLeftCompressorDCfanState(leftCompressorDCfanState);
			machineState.setLeftCabinetDCfanState(leftCabinetDCfanState);
			machineState.setRightCompressorDCfanState(rightCompressorDCfanState);
			machineState.setRightCabinetDCfanState(rightCabinetDCfanState);
			machineStateDao.update(machineState);
		}
	}
	/**
	 * 更新门加热状态，0=关，1=开
	 */
	public static void updateCounterDoorState(Context context, int leftDoorheat, int rightDoorheat) {
		String MachineID = new BindingManager().getMachineID(context);
		MachineStateDao machineStateDao = CacheDBUtil.getInstance(context).getMachineStateDao();
		QueryBuilder<MachineState> qb = machineStateDao.queryBuilder();
		qb.where(MachineStateDao.Properties.MachineID.eq(MachineID));
		if(qb.list().size() > 0){
			MachineState machineState = qb.list().get(0);
			machineState.setLeftDoorheat(leftDoorheat);
			machineState.setRightDoorheat(rightDoorheat);
			machineStateDao.update(machineState);
		}
	}
	/**
	 * 照明灯状态，0=关，1=开
	 */
	public static void updateLight(Context context, int leftLight, int rightLight, int midLight) {
		String MachineID = new BindingManager().getMachineID(context);
		MachineStateDao machineStateDao = CacheDBUtil.getInstance(context).getMachineStateDao();
		QueryBuilder<MachineState> qb = machineStateDao.queryBuilder();
		qb.where(MachineStateDao.Properties.MachineID.eq(MachineID));
		if(qb.list().size() > 0){
			MachineState machineState = qb.list().get(0);
			machineState.setLeftLight(leftLight);
			machineState.setRightLight(rightLight);
			machineState.setMidLight(midLight);
			machineStateDao.update(machineState);
		}
	}
	/**
	 * 更新边柜下货光栅状态、X轴出货光栅状态，中柜取货光栅状态、落货光栅状态、防夹手光栅状态
	 */
	public static void updateRasterState(Context context, int leftPushGoodsRaster, int leftOutGoodsRaster, int rightPushGoodsRaster,
                                         int rightOutGoodsRaster, int midGetGoodsRaster, int midDropGoodsRaster, int midAntiPinchHandRaster) {
		String MachineID = new BindingManager().getMachineID(context);
		MachineStateDao machineStateDao = CacheDBUtil.getInstance(context).getMachineStateDao();
		QueryBuilder<MachineState> qb = machineStateDao.queryBuilder();
		qb.where(MachineStateDao.Properties.MachineID.eq(MachineID));
		if(qb.list().size() > 0){
			MachineState machineState = qb.list().get(0);
			machineState.setLeftPushGoodsRaster(leftPushGoodsRaster);
			machineState.setLeftOutGoodsRaster(leftOutGoodsRaster);
			machineState.setRightPushGoodsRaster(rightPushGoodsRaster);
			machineState.setRightOutGoodsRaster(rightOutGoodsRaster);
			machineState.setMidGetGoodsRaster(midGetGoodsRaster);
			machineState.setMidDropGoodsRaster(midDropGoodsRaster);
			machineState.setMidAntiPinchHandRaster(midAntiPinchHandRaster);
			machineStateDao.update(machineState);
		}
	}
	/**
	 * 更新出货门、取货门、落货门开关状态
	 */
	public static void updateOtherDoorState(Context context, int leftOutGoodsDoor, int rightOutGoodsDoor, int midGetDoor, int midDropDoor) {
		String MachineID = new BindingManager().getMachineID(context);
		MachineStateDao machineStateDao = CacheDBUtil.getInstance(context).getMachineStateDao();
		QueryBuilder<MachineState> qb = machineStateDao.queryBuilder();
		qb.where(MachineStateDao.Properties.MachineID.eq(MachineID));
		if(qb.list().size() > 0){
			MachineState machineState = qb.list().get(0);
			machineState.setLeftOutGoodsDoor(leftOutGoodsDoor);
			machineState.setRightOutGoodsDoor(rightOutGoodsDoor);
			machineState.setMidGetDoor(midGetDoor);
			machineState.setMidDropDoor(midDropDoor);
			machineStateDao.update(machineState);
		}
	}
	/**
	 * 更新温控交流总电源状态、制冷压缩机工作状态、压缩机风扇工作状态、制热电热丝工作状态、循环风风扇工作状态
	 */
	public static void updateFan(Context context, int leftTempControlAlternatPower, int leftRefrigerationCompressorState, int leftCompressorFanState, int leftHeatingWireState, int leftRecirculatAirFanState,
								 int rightTempControlAlternatPower, int rightRefrigerationCompressorState, int rightCompressorFanState, int rightHeatingWireState, int rightRecirculatAirFanState) {
		String MachineID = new BindingManager().getMachineID(context);
		MachineStateDao machineStateDao = CacheDBUtil.getInstance(context).getMachineStateDao();
		QueryBuilder<MachineState> qb = machineStateDao.queryBuilder();
		qb.where(MachineStateDao.Properties.MachineID.eq(MachineID));
		if(qb.list().size() > 0){
			MachineState machineState = qb.list().get(0);
			machineState.setLeftTempControlAlternatPower(leftTempControlAlternatPower);
			machineState.setLeftRefrigerationCompressorState(leftRefrigerationCompressorState);
			machineState.setLeftCompressorFanState(leftCompressorFanState);
			machineState.setLeftHeatingWireState(leftHeatingWireState);
			machineState.setLeftRecirculatAirFanState(leftRecirculatAirFanState);
			machineState.setRightTempControlAlternatPower(rightTempControlAlternatPower);
			machineState.setRightRefrigerationCompressorState(rightRefrigerationCompressorState);
			machineState.setRightCompressorFanState(rightCompressorFanState);
			machineState.setRightHeatingWireState(rightHeatingWireState);
			machineState.setRightRecirculatAirFanState(rightRecirculatAirFanState);
			machineStateDao.update(machineState);
		}
	}
	/**
	 * 更新Y轴上下止点开关状态
	 */
	public static void updateYState(Context context, int counter, int liftPlatformDownSwitch, int liftPlatformUpSwitch) {
		String MachineID = new BindingManager().getMachineID(context);
		MachineStateDao machineStateDao = CacheDBUtil.getInstance(context).getMachineStateDao();
		QueryBuilder<MachineState> qb = machineStateDao.queryBuilder();
		qb.where(MachineStateDao.Properties.MachineID.eq(MachineID));
		if(qb.list().size() > 0){
			MachineState machineState = qb.list().get(0);
			if(counter == 1){
				machineState.setLeftLiftPlatformDownSwitch(liftPlatformDownSwitch);
				machineState.setLeftLiftPlatformUpSwitch(liftPlatformUpSwitch);
			}else{
				machineState.setRightLiftPlatformDownSwitch(liftPlatformDownSwitch);
				machineState.setRightLiftPlatformUpSwitch(liftPlatformUpSwitch);
			}
			machineStateDao.update(machineState);
		}
	}
	/**
	 * 更新下货光栅即时状态
	 */
	public static void updatePushGoodsRaster(Context context, int counter, int pushGoodsRaster) {
		String MachineID = new BindingManager().getMachineID(context);
		MachineStateDao machineStateDao = CacheDBUtil.getInstance(context).getMachineStateDao();
		QueryBuilder<MachineState> qb = machineStateDao.queryBuilder();
		qb.where(MachineStateDao.Properties.MachineID.eq(MachineID));
		if(qb.list().size() > 0){
			MachineState machineState = qb.list().get(0);
			if(counter == 1){
				machineState.setLeftPushGoodsRasterImmediately(pushGoodsRaster);
			}else{
				machineState.setRightPushGoodsRasterImmediately(pushGoodsRaster);
			}
			machineStateDao.update(machineState);
		}
	}
	/**
	 * 更新出货光栅传感器即时状态
	 */
	public static void updateOutGoodsRaster(Context context, int counter, int outGoodsRaster) {
		String MachineID = new BindingManager().getMachineID(context);
		MachineStateDao machineStateDao = CacheDBUtil.getInstance(context).getMachineStateDao();
		QueryBuilder<MachineState> qb = machineStateDao.queryBuilder();
		qb.where(MachineStateDao.Properties.MachineID.eq(MachineID));
		if(qb.list().size() > 0){
			MachineState machineState = qb.list().get(0);
			if(counter == 1){
				machineState.setLeftOutGoodsRasterImmediately(outGoodsRaster);
			}else{
				machineState.setRightOutGoodsRasterImmediately(outGoodsRaster);
			}
			machineStateDao.update(machineState);
		}
	}
	/**
	 * 更新出货门上下止点开关即时状态
	 */
	public static void updateOutGoodsDoorSwitch(Context context,int counter, int outGoodsDoorDownSwitch, int outGoodsDoorUpSwitch) {
		String MachineID = new BindingManager().getMachineID(context);
		MachineStateDao machineStateDao = CacheDBUtil.getInstance(context).getMachineStateDao();
		QueryBuilder<MachineState> qb = machineStateDao.queryBuilder();
		qb.where(MachineStateDao.Properties.MachineID.eq(MachineID));
		if(qb.list().size() > 0){
			MachineState machineState = qb.list().get(0);
			if(counter == 1){
				machineState.setLeftOutGoodsDoorDownSwitch(outGoodsDoorDownSwitch);
				machineState.setLeftOutGoodsDoorUpSwitch(outGoodsDoorUpSwitch);
			}else{
				machineState.setRightOutGoodsDoorDownSwitch(outGoodsDoorDownSwitch);
				machineState.setRightOutGoodsDoorUpSwitch(outGoodsDoorUpSwitch);
			}
			machineStateDao.update(machineState);
		}
	}
	/**
	 * 更新取货门上下止点开关即时状态
	 */
	public static void updateGetGoodsDoorSwitch(Context context,int getGoodsDoorDownSwitch, int getGoodsDoorUpSwitch) {
		String MachineID = new BindingManager().getMachineID(context);
		MachineStateDao machineStateDao = CacheDBUtil.getInstance(context).getMachineStateDao();
		QueryBuilder<MachineState> qb = machineStateDao.queryBuilder();
		qb.where(MachineStateDao.Properties.MachineID.eq(MachineID));
		if(qb.list().size() > 0){
			MachineState machineState = qb.list().get(0);
			machineState.setMidGetDoorDownSwitch(getGoodsDoorDownSwitch);
			machineState.setMidGetDoorUpSwitch(getGoodsDoorUpSwitch);
			machineStateDao.update(machineState);
		}
	}
	/**
	 * 获得机器状态
	 */
	public static MachineState getMachineState(Context context) {
		String MachineID = new BindingManager().getMachineID(context);
		QueryBuilder<MachineState> qb = CacheDBUtil.getInstance(context).getMachineStateDao().queryBuilder();
		qb.where(MachineStateDao.Properties.MachineID.eq(MachineID));
		return qb.list().size()>0 ?qb.list().get(0) :null;
	}

	/*********************************************** OrderRecord 订单记录 ***********************************************/
	/**
	 * 是否有该订单存在
	 */
	private static int orderRecordCount(Context context, String orderNo) {
		OrderRecordDao orderRecordDao = CacheDBUtil.getInstance(context).getOrderRecordDao();
		QueryBuilder<OrderRecord> qb = orderRecordDao.queryBuilder();
		qb.where(OrderRecordDao.Properties.SysorderNo.eq(orderNo));
		return qb.list().size();
	}
	/**
	 * 保存订单记录
	 */
	public static void saveOrderRecord(Context context, String sysorderNo, int payType, String goodID, String goodName, double goodPrice, String goodQuantity, String goodCounter, String channelNo, String time) {
		try {
			int orderRecordCount = orderRecordCount(context, sysorderNo);
			if ( orderRecordCount==0 ) {
				OrderRecord orderRecord = new OrderRecord();
				orderRecord.setSysorderNo(sysorderNo);
				orderRecord.setPayType(String.valueOf(payType));
				orderRecord.setGoodID(goodID);
				orderRecord.setGoodName(goodName);
				orderRecord.setGoodPrice(String.valueOf(goodPrice));
				orderRecord.setGoodQuantity(goodQuantity);
				orderRecord.setGoodCounter(goodCounter);
				orderRecord.setChannelNo(channelNo);
				orderRecord.setStatus(String.valueOf(Constant.unPay));
				orderRecord.setOrderTime(time);

				CacheDBUtil.getInstance(context).getOrderRecordDao().insertOrReplace(orderRecord);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 订单确定后调用
	 */
	public static void orderHasConfirm(Context context, String orderNo, String time) {
		QueryBuilder<OrderRecord> qb = CacheDBUtil.getInstance(context).getOrderRecordDao().queryBuilder();
		qb.where(OrderRecordDao.Properties.SysorderNo.eq(orderNo));
		if ( qb.list().size()>0 ) {
			for (Iterator<OrderRecord> iterator = qb.list().iterator(); iterator.hasNext();) {
				OrderRecord orderRecord = (OrderRecord) iterator.next();
				orderRecord.setStatus(String.valueOf(Constant.hasPay));
				orderRecord.setPaymentTime(time);
				CacheDBUtil.getInstance(context).getOrderRecordDao().insertOrReplace(orderRecord);
			}
		}
	}
	/**
	 * 获得订单情况，出货完毕后，根据SysorderNo订单号，货道订单列表（一件货一条记录）
	 */
	public static ArrayList<OrderRecord> getOrder(Context context, String sysorderNo) {
		ArrayList<OrderRecord> dataList = new ArrayList<>();
		QueryBuilder<OrderRecord> qb = CacheDBUtil.getInstance(context).getOrderRecordDao().queryBuilder();
		qb.where(OrderRecordDao.Properties.SysorderNo.eq(sysorderNo));
		if ( qb.list().size()>0 ) {
			dataList.addAll(qb.list());
		}
		return dataList;
	}
	/*********************************************** Transaction 出货交易记录 ***********************************************/
	/**
	 * 是否有该出货记录存在
	 */
	private static int deliveryCount(Context context, String orderNo) {
		TransactionDao transactionDao = CacheDBUtil.getInstance(context).getTransactionDao();
		QueryBuilder<Transaction> qb = transactionDao.queryBuilder();
		qb.where(TransactionDao.Properties.SysorderNo.eq(orderNo));
		return qb.list().size();
	}
	/**
	 * 保存出货记录
	 */
	public static void saveDelivery(Context context, String sysorderNo, String positionIDs, String time, int payType) {
		try {
			int deliveryCount = deliveryCount(context, sysorderNo);
			if ( deliveryCount==0 ) {
				Transaction transaction = new Transaction();
				transaction.setSysorderNo(sysorderNo);
				transaction.setPositionIDs(positionIDs);
				transaction.setBeginTime(time);
				transaction.setComplete(String.valueOf(Constant.deliveryUnComplete));
				transaction.setError(String.valueOf(Constant.deliveryUnError));
				transaction.setPayType(String.valueOf(payType));

				CacheDBUtil.getInstance(context).getTransactionDao().insertOrReplace(transaction);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 已出货记录确定后调用
	 */
	public static void deliveryHasConfirm(Context context, String sysorderNo, String error, String time) {
		TransactionDao transactionDao = CacheDBUtil.getInstance(context).getTransactionDao();
		QueryBuilder<Transaction> qb = transactionDao.queryBuilder();
		qb.where(TransactionDao.Properties.SysorderNo.eq(sysorderNo));
		if ( qb.list().size()>0 ) {
			Transaction transaction = qb.list().get(0);
			transaction.setComplete(String.valueOf(Constant.deliveryComplete));
			transaction.setError(error);
			transaction.setEndTime(time);
			transactionDao.update(transaction);
		}
	}
	/**
	 * 根据订单号返回出货订单记录
	 */
	public static Transaction getDelivery(Context context, String sysorderNo) {
		TransactionDao transactionDao = CacheDBUtil.getInstance(context).getTransactionDao();
		QueryBuilder<Transaction> qb = transactionDao.queryBuilder();
		qb.where(TransactionDao.Properties.SysorderNo.eq(sysorderNo));
		return qb.list().size()>0?qb.list().get(0):null;
	}
	/**
	 * 返回最后一笔交易的对象
	 */
	public static Transaction getLastTransaction(Context context) {
		QueryBuilder<Transaction> qb = CacheDBUtil.getInstance(context).getTransactionDao().queryBuilder().orderDesc(TransactionDao.Properties.Id);
		return qb.list().size()>0?qb.list().get(0):null;
	}
	/*********************************************** SettingInfo 设定信息 ***********************************************/
	/**
	 * 初始化设定信息
	 */
	public static void initialSettingInfo(Context context) {
		try {
			String MachineID = new BindingManager().getMachineID(context);
			SettingInfoDao settingInfoDao = CacheDBUtil.getInstance(context).getSettingInfoDao();
			QueryBuilder<SettingInfo> qb = settingInfoDao.queryBuilder();
			qb.where(SettingInfoDao.Properties.MachineID.eq(MachineID));
			if(qb.list().size() > 0){
				SettingInfo settingInfo = qb.list().get(0);
				settingInfo.setMachineID(MachineID);
				settingInfo.setLeftTempState(2);
				settingInfo.setLeftSetTemp(10);
				settingInfo.setRightTempState(2);
				settingInfo.setRightSetTemp(10);

				settingInfo.setLeftSetLight("0");
				settingInfo.setLeftLightStartTime("16:00:00");
				settingInfo.setLeftLightEndTime("10:00:00");
				settingInfo.setRightSetLight("0");
				settingInfo.setRightLightStartTime("16:00:00");
				settingInfo.setRightLightEndTime("10:00:00");

				settingInfo.setLeftOutPosition(null);
				settingInfo.setLeftFlootPosition(null);
				settingInfo.setLeftFlootNo(null);
				settingInfo.setRightOutPosition(null);
				settingInfo.setRightFlootPosition(null);
				settingInfo.setRightFlootNo(null);
				settingInfoDao.update(settingInfo);
			}else{
				SettingInfo settingInfo = new SettingInfo();
				settingInfo.setMachineID(MachineID);
				settingInfo.setLeftTempState(2);
				settingInfo.setLeftSetTemp(15);
				settingInfo.setRightTempState(2);
				settingInfo.setRightSetTemp(15);

				settingInfo.setLeftSetLight("0");
				settingInfo.setLeftLightStartTime("16:00:00");
				settingInfo.setLeftLightEndTime("10:00:00");
				settingInfo.setRightSetLight("0");
				settingInfo.setRightLightStartTime("16:00:00");
				settingInfo.setRightLightEndTime("10:00:00");

				settingInfo.setLeftOutPosition("null");
				settingInfo.setLeftFlootPosition("0,0,0,0,0,0");
				settingInfo.setLeftFlootNo(null);
				settingInfo.setRightOutPosition("null");
				settingInfo.setRightFlootPosition("0,0,0,0,0,0");
				settingInfo.setRightFlootNo(null);
				CacheDBUtil.getInstance(context).getSettingInfoDao().insertOrReplace(settingInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 保存温度状态
	 * @param leftTempState 左柜温控模式：0=制冷1=制热2=常温
	 * @param leftSetTemp 设定温度:-20～70，精度为0.5，所以统一放大10倍进行传输保存，显示时进行缩放
	 * @param rightTempState 右柜温控模式：0=制冷1=制热2=常温
	 * @param rightSetTemp 设定温度:-20～70，精度为0.5，所以统一放大10倍进行传输保存，显示时进行缩放
	 * */
	public static void saveTemp(Context context, int leftTempState, int leftSetTemp, int rightTempState, int rightSetTemp) {
		String MachineID = new BindingManager().getMachineID(context);
		SettingInfoDao settingInfoDao = CacheDBUtil.getInstance(context).getSettingInfoDao();
		QueryBuilder<SettingInfo> qb = settingInfoDao.queryBuilder();
		qb.where(SettingInfoDao.Properties.MachineID.eq(MachineID));
		if(qb.list().size() > 0){
			SettingInfo settingInfo = qb.list().get(0);
			settingInfo.setLeftTempState(leftTempState);
			settingInfo.setLeftSetTemp(leftSetTemp);
			settingInfo.setRightTempState(rightTempState);
			settingInfo.setRightSetTemp(rightSetTemp);
			settingInfoDao.update(settingInfo);
		}
	}
	/**
	 * 设定货柜层数、出货口位置、每层位置（码盘数）
	 * @param counter 货柜号 1=左柜（主柜），2 =右柜（辅柜）
	 * @param flootNo 货柜层数
	 * @param outPosition 出货口位置
	 * @param flootPositions 每层位置，逗号隔开
	 * */
	public static void setFloot(Context context, int counter, String flootNo, String outPosition, String flootPositions) {
		String MachineID = new BindingManager().getMachineID(context);
		SettingInfoDao settingInfoDao = CacheDBUtil.getInstance(context).getSettingInfoDao();
		QueryBuilder<SettingInfo> qb = settingInfoDao.queryBuilder();
		qb.where(SettingInfoDao.Properties.MachineID.eq(MachineID));
		if(qb.list().size() > 0){
			SettingInfo settingInfo = qb.list().get(0);
			if(counter == 1){
				settingInfo.setLeftFlootNo(flootNo);
				settingInfo.setLeftOutPosition(outPosition);
				settingInfo.setLeftFlootPosition(flootPositions);
			}else{
				settingInfo.setRightFlootNo(flootNo);
				settingInfo.setRightOutPosition(outPosition);
				settingInfo.setRightFlootPosition(flootPositions);
			}
			settingInfoDao.update(settingInfo);
		}
	}
	/**
	 * 获得设定信息
	 * */
	public static SettingInfo getSettingInfo(Context context) {
		String MachineID = new BindingManager().getMachineID(context);
		QueryBuilder<SettingInfo> qb = CacheDBUtil.getInstance(context).getSettingInfoDao().queryBuilder();
		qb.where(SettingInfoDao.Properties.MachineID.eq(MachineID));
		return qb.list().size()>0 ?qb.list().get(0) :null;
	}
	/**
	 * 获得指定货道所在层的位置（设定好的码盘数）
	 * @param counter 货柜号 1=左柜（主柜），2 =右柜（辅柜）
	 * @param positionID 货道ID（1-200）
	 * */
	public static int getFlootPosition(Context context, int counter, int positionID) {
		SettingInfo settingInfo = getSettingInfo(context);
		AisleInfo aisleInfo = getAisleInfo(context, counter, positionID);
		if(counter == 1){
			String[] str = settingInfo != null ? settingInfo.getLeftFlootPosition().split(",") : new String[0];
			return Integer.parseInt(str[(aisleInfo.getPosition1()/16 - 1)]);
		}else{
			String[] str = settingInfo != null ? settingInfo.getRightFlootPosition().split(",") : new String[0];
			return Integer.parseInt(str[(aisleInfo.getPosition1()/16 - 1)]);
		}
	}
	/**
	 * 获得指定货柜出货口位置（设定好的码盘数）
	 * @param counter 货柜号 1=左柜（主柜），2 =右柜（辅柜）
	 * */
	public static int getOutPosition(Context context, int counter) {
		SettingInfo settingInfo = getSettingInfo(context);
		if(counter == 1){
			return Integer.parseInt(settingInfo.getLeftOutPosition());
		}else{
			return Integer.parseInt(settingInfo.getRightOutPosition());
		}
	}

	/*********************************************** AisleInfo 货道信息 ***********************************************/
	/**
	 * 初始化200个货道信息
	 */
	public static void initialAisleInfo(Context context) {
		for(int positionID = 1;positionID < 201;positionID++){
			int state = 0;
			int motorType = 1;
			int counter = 0;
			int rows = 0;
			int columns = 0;
			if(positionID <= 100){
				rows = (positionID-1) / 10 + 1;
				columns = (positionID-1) % 10 + 1;
				counter = 1;
				if(positionID <= 60){
					state = 0;
				}else{
					state = 1;
				}
			}else {
				rows = (positionID-101) / 10 + 1;
				columns = (positionID-101) % 10 + 1;
				counter = 2;
				if(positionID <= 160){
					state = 0;
				}else{
					state = 1;
				}
			}
			byte position1 = (byte) (rows*16 + columns);
			byte position2 = position1;
			try {
				AisleInfoDao aisleInfoDao = CacheDBUtil.getInstance(context).getAisleInfoDao();
				QueryBuilder<AisleInfo> qb = aisleInfoDao.queryBuilder();
				qb.where(AisleInfoDao.Properties.PositionID.eq(positionID));
				if (qb.list().size() > 0) {
					AisleInfo aisleInfo = qb.list().get(0);
					aisleInfo.setCounter(counter);
					aisleInfo.setPositionID(positionID);
					aisleInfo.setState(state);
					aisleInfo.setMotorType(motorType);
					aisleInfo.setPosition1(position1);
					aisleInfo.setPosition2(position2);
					aisleInfoDao.update(aisleInfo);
				}else{
					AisleInfo aisleInfo = new AisleInfo();
					aisleInfo.setCounter(counter);
					aisleInfo.setPositionID(positionID);
					aisleInfo.setState(state);
					aisleInfo.setMotorType(motorType);
					aisleInfo.setPosition1(position1);
					aisleInfo.setPosition2(position2);
					aisleInfo.setGoodQuantity("10");
					aisleInfo.setCapacity("10");
					aisleInfo.setGoodID(-1);
					aisleInfo.setGoodColdHot(false);
					aisleInfo.setTemp("0");
					CacheDBUtil.getInstance(context).getAisleInfoDao().insertOrReplace(aisleInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 保存货道的电机类型和状态
	 * positionID 必须为1-200之间，1-100为左柜，101-200为右柜
	 * @param positionID 货道号
	 * @param state 货道状态 （0：正常；1：锁死不让购买；2：故障）
	 * @param motorType 电机类型，1=单弹簧机，2=双弹簧机，3=窄履带机，4=宽履带机
	 */
	public static void saveAisleInfoManager(Context context, int positionID, int state, int motorType) {
		int counter = 0;
		int rows = 0;
		int columns = 0;
		if(positionID <= 100){
			rows = (positionID-1) / 10 + 1;
			columns = (positionID-1) % 10 + 1;
			counter = 1;
		}else {
			rows = (positionID-101) / 10 + 1;
			columns = (positionID-101) % 10 + 1;
			counter = 2;
		}
		byte position1 = (byte) (rows*16 + columns);
		byte position2 = position1;
		try {
			AisleInfoDao aisleInfoDao = CacheDBUtil.getInstance(context).getAisleInfoDao();
			QueryBuilder<AisleInfo> qb = aisleInfoDao.queryBuilder();
			qb.where(AisleInfoDao.Properties.PositionID.eq(positionID));
			if (qb.list().size() > 0) {
				AisleInfo aisleInfo = qb.list().get(0);
				aisleInfo.setCounter(counter);
				aisleInfo.setPositionID(positionID);
				aisleInfo.setState(state);
				aisleInfo.setMotorType(motorType);
				aisleInfo.setPosition1(position1);
				aisleInfo.setPosition2(position2);
				aisleInfoDao.update(aisleInfo);
			}else{
				AisleInfo aisleInfo = new AisleInfo();
				aisleInfo.setCounter(counter);
				aisleInfo.setPositionID(positionID);
				aisleInfo.setState(state);
				aisleInfo.setMotorType(motorType);
				aisleInfo.setPosition1(position1);
				aisleInfo.setPosition2(position2);
				aisleInfo.setGoodQuantity("10");
				aisleInfo.setCapacity("10");
				aisleInfo.setGoodID(-1);
				aisleInfo.setGoodColdHot(false);
				aisleInfo.setTemp("0");
				CacheDBUtil.getInstance(context).getAisleInfoDao().insertOrReplace(aisleInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 一键保存货道的电机类型和状态
	 */
	public static void saveAisleDetaildeManager(Context context, ArrayList<AisleInfo> dataList) {
		for(int i = 0; i<dataList.size(); i++){
			int counter = 0;
			int rows = 0;
			int columns = 0;
			if(dataList.get(i).getPositionID() <= 100){
				rows = (dataList.get(i).getPositionID()-1) / 10 + 1;
				columns = (dataList.get(i).getPositionID()-1) % 10 + 1;
				counter = 1;
			}else {
				rows = (dataList.get(i).getPositionID()-101) / 10 + 1;
				columns = (dataList.get(i).getPositionID()-101) % 10 + 1;
				counter = 2;
			}
			byte position1 = (byte) (rows*16 + columns);
			byte position2 = position1;
			try {
				AisleInfoDao aisleInfoDao = CacheDBUtil.getInstance(context).getAisleInfoDao();
				QueryBuilder<AisleInfo> qb = aisleInfoDao.queryBuilder();
				qb.where(AisleInfoDao.Properties.PositionID.eq(dataList.get(i).getPositionID()));
				if (qb.list().size() > 0) {
					AisleInfo aisleInfo = qb.list().get(0);
					aisleInfo.setCounter(counter);
					aisleInfo.setPositionID(dataList.get(i).getPositionID());
					aisleInfo.setState(dataList.get(i).getState());
					aisleInfo.setMotorType(dataList.get(i).getMotorType());
					aisleInfo.setPosition1(position1);
					aisleInfo.setPosition2(position2);
					aisleInfoDao.update(aisleInfo);
				}else{
					AisleInfo aisleInfo = new AisleInfo();
					aisleInfo.setCounter(counter);
					aisleInfo.setPositionID(dataList.get(i).getPositionID());
					aisleInfo.setState(dataList.get(i).getState());
					aisleInfo.setMotorType(dataList.get(i).getMotorType());
					aisleInfo.setPosition1(position1);
					aisleInfo.setPosition2(position2);
					aisleInfo.setGoodQuantity("10");
					aisleInfo.setCapacity("10");
					aisleInfo.setGoodID(-1);
					aisleInfo.setGoodColdHot(false);
					aisleInfo.setTemp("0");
					CacheDBUtil.getInstance(context).getAisleInfoDao().insertOrReplace(aisleInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 更新货道的电机状态
	 * positionID 必须为1-200之间，1-100为左柜，101-200为右柜
	 * @param positionID 货道号
	 * @param state 货道状态 （0：正常；1：锁死不让购买；2：故障）
	 */
	public static void updateAisleTestManager(Context context, int positionID, int state) {
		try {
			AisleInfoDao aisleInfoDao = CacheDBUtil.getInstance(context).getAisleInfoDao();
			QueryBuilder<AisleInfo> qb = aisleInfoDao.queryBuilder();
			qb.where(AisleInfoDao.Properties.PositionID.eq(positionID));
			if (qb.list().size() > 0) {
				AisleInfo aisleInfo = qb.list().get(0);
				aisleInfo.setState(state);
				aisleInfoDao.update(aisleInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 更新货道的商品信息，服务器端下发补货信息
	 * */
	public static void updateAisleRequestReplenish(Context context, JSONArray aisleInfosArray) {
		SparseArray<AisleInfo> AisleInfoList = getAisleInfoList2(context);
		for (int i = 0; i < aisleInfosArray.size(); i++) {
			JSONObject aisleInfoObject = aisleInfosArray.getJSONObject(i);
			try {
				int counter = 0;
				if(aisleInfoObject.getString("counterNo").contains("货柜")){
					counter = Integer.parseInt(aisleInfoObject.getString("counterNo").substring(2));
				}else{
					counter = Integer.parseInt(aisleInfoObject.getString("counterNo"));
				}
				int PositionID = 0;
				if(aisleInfoObject.getString("channelNo").contains("货道")){
					PositionID = Integer.parseInt(aisleInfoObject.getString("channelNo").substring(2));
				}else{
					PositionID = Integer.parseInt(aisleInfoObject.getString("channelNo"));
				}
				if(counter == 2){
					PositionID = PositionID+100;
				}
				AisleInfoDao aisleInfoDao = CacheDBUtil.getInstance(context).getAisleInfoDao();
				QueryBuilder<AisleInfo> qb = aisleInfoDao.queryBuilder();
				qb.where(AisleInfoDao.Properties.PositionID.eq(PositionID));
				if((PositionID>=1 && PositionID<=60) || (PositionID>=101 && PositionID<=160)){
					AisleInfoList.remove(PositionID);
				}
				if (qb.list().size() > 0) {
					AisleInfo aisleInfo = qb.list().get(0);
					aisleInfo.setCounter(counter);
					aisleInfo.setState(Integer.parseInt(aisleInfoObject.getString("status")));
					aisleInfo.setCapacity(aisleInfoObject.getString("capacity"));
					aisleInfo.setGoodQuantity(aisleInfoObject.getString("goodQuantity"));
					aisleInfo.setGoodID(Integer.parseInt(aisleInfoObject.getString("goodID")));
					aisleInfo.setGoodName(aisleInfoObject.getString("goodName"));
					GoodsInfo goodsInfo = getGoodsInfo(context,Integer.parseInt(aisleInfoObject.getString("goodID")));
					if(goodsInfo != null){
						aisleInfo.setGoodColdHot(goodsInfo.getGoodColdHot());
						aisleInfo.setTemp("0");
					}else{
						aisleInfo.setGoodColdHot(false);
						aisleInfo.setTemp("0");
					}
					int parentId = Integer.parseInt(aisleInfoObject.getString("parentId"));
					String parentName = aisleInfoObject.getString("parentName");
					int catId = Integer.parseInt(aisleInfoObject.getString("catId"));
					String catName = aisleInfoObject.getString("catName");
					if(parentId!=0&&!parentName.equals("")){
						if(parentName.equals("其它")){
							aisleInfo.setGoodTypeID(0);//代表类别 其它
							aisleInfo.setGoodTypeName("其它");
						}else{
							aisleInfo.setGoodTypeID(parentId);
							aisleInfo.setGoodTypeName(parentName);
						}
					}else{
						if(catName.equals("其它")){
							aisleInfo.setGoodTypeID(0);//代表类别 其它
							aisleInfo.setGoodTypeName("其它");
						}else{
							aisleInfo.setGoodTypeID(catId);
							aisleInfo.setGoodTypeName(catName);
						}
					}
					aisleInfo.setGoodDescription("");
					aisleInfo.setGoodPrice(Double.parseDouble(df.format(Double.parseDouble(aisleInfoObject.getString("goodsPrice")))));
					aisleInfo.setPromotion(aisleInfoObject.getString("promotion"));
					aisleInfo.setPromotionPrice(Double.parseDouble(df.format(Double.parseDouble(aisleInfoObject.getString("promotionPrice")))));
					aisleInfo.setGoodsImgUrl(aisleInfoObject.getString("goodUrl"));
					aisleInfo.setGoodsImgLocalUrl("");
					aisleInfo.setUpdateTime(DateUtil.getCurrentTime());
					aisleInfoDao.update(aisleInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(int i = 0; i < AisleInfoList.size(); i++){
			try {
				AisleInfoDao aisleInfoDao = CacheDBUtil.getInstance(context).getAisleInfoDao();
				QueryBuilder<AisleInfo> qb = aisleInfoDao.queryBuilder();
				qb.where(AisleInfoDao.Properties.PositionID.eq(AisleInfoList.valueAt(i).getPositionID()));
				if (qb.list().size() > 0) {
					AisleInfo aisleInfo = qb.list().get(0);
					aisleInfo.setCapacity("10");
					aisleInfo.setGoodQuantity("0");
					aisleInfo.setGoodID(-1);
					aisleInfo.setGoodName("");
					aisleInfo.setGoodTypeID(-1);
					aisleInfo.setGoodTypeName("");
					aisleInfo.setGoodDescription("");
					aisleInfo.setGoodPrice((double)0.01);
					aisleInfo.setPromotion("");
					aisleInfo.setPromotionPrice((double)0.01);
					aisleInfo.setGoodsImgUrl("");
					aisleInfo.setGoodsImgLocalUrl("");
					aisleInfo.setUpdateTime(DateUtil.getCurrentTime());
					aisleInfo.setGoodColdHot(false);
					aisleInfo.setTemp("0");
					aisleInfoDao.update(aisleInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据服务器端下发补货信息开始下载图片，下载完之后调用保存图片本地地址
	 * */
	public static void updateAisleGoodImageLocalUrl(Context context,int goodID, String goodImageLocalUrl) {
		try {
			AisleInfoDao aisleInfoDao = CacheDBUtil.getInstance(context).getAisleInfoDao();
			QueryBuilder<AisleInfo> qb = aisleInfoDao.queryBuilder();
			qb.where(AisleInfoDao.Properties.GoodID.eq(goodID));
			for (Iterator<AisleInfo> iterator = qb.list().iterator(); iterator.hasNext();) {
				AisleInfo aisleInfo = (AisleInfo) iterator.next();
				aisleInfo.setGoodsImgLocalUrl(goodImageLocalUrl);
				aisleInfoDao.update(aisleInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 合并后，更新货道信息
	 * 合并后设置商品编号和positionID小的对应，positionID大的state置0不设置对应商品编号。
	 * @param positionID1 相邻的货道号较小的那个，货道号1-200
	 * @param positionID2 相邻的货道号较大的那个，货道号1-200
	 */
	public static void updateAisleInfoMerge(Context context, int positionID1, int positionID2) {
		try {
			AisleInfoDao aisleInfoDao = CacheDBUtil.getInstance(context).getAisleInfoDao();
			QueryBuilder<AisleInfo> qb = aisleInfoDao.queryBuilder();
			qb.where(AisleInfoDao.Properties.PositionID.eq(positionID1));
			QueryBuilder<AisleInfo> qb2 = aisleInfoDao.queryBuilder();
			qb2.where(AisleInfoDao.Properties.PositionID.eq(positionID2));
			if (qb.list().size() > 0 && qb2.list().size() > 0 ) {
				AisleInfo aisleInfo2 = qb2.list().get(0);
				aisleInfo2.setState(Constant.aisleState_locked);
				aisleInfoDao.update(aisleInfo2);

				AisleInfo aisleInfo = qb.list().get(0);
				aisleInfo.setPosition2(aisleInfo2.getPosition1());
				aisleInfoDao.update(aisleInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 解除合并后，更新货道信息
	 * @param positionID1 相邻的货道号较小的那个，货道号1-200
	 * @param positionID2 相邻的货道号较大的那个，货道号1-200
	 */
	public static void updateAisleInfoRelieveMerge(Context context, int positionID1, int positionID2) {
		try {
			AisleInfoDao aisleInfoDao = CacheDBUtil.getInstance(context).getAisleInfoDao();
			QueryBuilder<AisleInfo> qb = aisleInfoDao.queryBuilder();
			qb.where(AisleInfoDao.Properties.PositionID.eq(positionID1));
			QueryBuilder<AisleInfo> qb2 = aisleInfoDao.queryBuilder();
			qb2.where(AisleInfoDao.Properties.PositionID.eq(positionID2));
			if (qb.list().size() > 0 && qb2.list().size() > 0 ) {
				AisleInfo aisleInfo2 = qb2.list().get(0);
				aisleInfo2.setState(Constant.aisleState_normal);
				aisleInfoDao.update(aisleInfo2);

				AisleInfo aisleInfo = qb.list().get(0);
				aisleInfo.setPosition2(aisleInfo.getPosition1());
				aisleInfoDao.update(aisleInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获得货道信息
	 * @param counter 货柜号 1=左柜（主柜），2 =右柜（辅柜）
	 * @param positionID 货道ID（1-200）
	 * */
	public static AisleInfo getAisleInfo(Context context, int counter, int positionID) {
		QueryBuilder<AisleInfo> qb = CacheDBUtil.getInstance(context).getAisleInfoDao().queryBuilder();
		qb.where(AisleInfoDao.Properties.Counter.eq(counter), AisleInfoDao.Properties.PositionID.eq(positionID));
		return qb.list().size()>0 ?qb.list().get(0) :null;
	}
	/**
	 * 获得左右柜各60个货到信息的列表(ArrayList<AisleInfo>)
	 * */
	public static ArrayList<AisleInfo> getAisleInfoList(Context context) {
		ArrayList<AisleInfo> dataList = new ArrayList<>();
		QueryBuilder<AisleInfo> qb = CacheDBUtil.getInstance(context).getAisleInfoDao().queryBuilder();
		qb.where(qb.or(AisleInfoDao.Properties.PositionID.between(1,60), AisleInfoDao.Properties.PositionID.between(101,160))).orderAsc(AisleInfoDao.Properties.PositionID);
		for (Iterator<AisleInfo> iterator = qb.list().iterator(); iterator.hasNext();) {
			AisleInfo aisleInfo = (AisleInfo) iterator.next();
			dataList.add(aisleInfo);
		}
		return dataList;
	}
	/**
	 * 获得左右柜各60个货到信息的列表(SparseArray<AisleInfo>)
	 * */
	public static SparseArray<AisleInfo> getAisleInfoList2(Context context) {
		SparseArray<AisleInfo> dataList = new SparseArray<>();
		QueryBuilder<AisleInfo> qb = CacheDBUtil.getInstance(context).getAisleInfoDao().queryBuilder();
		qb.where(qb.or(AisleInfoDao.Properties.PositionID.between(1,60), AisleInfoDao.Properties.PositionID.between(101,160))).orderAsc(AisleInfoDao.Properties.PositionID);
		for (Iterator<AisleInfo> iterator = qb.list().iterator(); iterator.hasNext();) {
			AisleInfo aisleInfo = (AisleInfo) iterator.next();
			dataList.append(aisleInfo.getPositionID(),aisleInfo);
		}
		return dataList;
	}
	/**
	 * 获得左右柜各60个货道信息中State位正常（非锁死、非故障）的列表
	 * */
	public static ArrayList<AisleInfo> getAisleInfoListNormal(Context context) {
		ArrayList<AisleInfo> dataList = new ArrayList<>();
		QueryBuilder<AisleInfo> qb = CacheDBUtil.getInstance(context).getAisleInfoDao().queryBuilder();
		qb.where(qb.or(AisleInfoDao.Properties.PositionID.between(1,60), AisleInfoDao.Properties.PositionID.between(101,160)),
				AisleInfoDao.Properties.State.eq(0)).orderAsc(AisleInfoDao.Properties.PositionID);
		for (Iterator<AisleInfo> iterator = qb.list().iterator(); iterator.hasNext();) {
			AisleInfo aisleInfo = (AisleInfo) iterator.next();
			dataList.add(aisleInfo);
		}
		return dataList;
	}
	/**
	 * 更新保存左右柜各60个货道信息中State位正常（非锁死、非故障）的列表，用于本地补货的保存
	 * */
	public static void updateAisleInfoListNormal(Context context, ArrayList<AisleInfo> dataList) {
		for(int i = 0; i<dataList.size(); i++){
			try {
				AisleInfoDao aisleInfoDao = CacheDBUtil.getInstance(context).getAisleInfoDao();
				QueryBuilder<AisleInfo> qb = aisleInfoDao.queryBuilder();
				qb.where(AisleInfoDao.Properties.PositionID.eq(dataList.get(i).getPositionID()));
				if (qb.list().size() > 0) {
					AisleInfo aisleInfo = qb.list().get(0);
					aisleInfo.setGoodID(dataList.get(i).getGoodID());
					aisleInfo.setGoodName(dataList.get(i).getGoodName());
					aisleInfo.setGoodTypeID(dataList.get(i).getGoodTypeID());
					aisleInfo.setGoodTypeName(dataList.get(i).getGoodTypeName());
					aisleInfo.setGoodDescription(dataList.get(i).getGoodDescription());
					aisleInfo.setGoodPrice(dataList.get(i).getGoodPrice());
					aisleInfo.setPromotion(dataList.get(i).getPromotion());
					aisleInfo.setPromotionPrice(dataList.get(i).getPromotionPrice());
					aisleInfo.setGoodsImgUrl(dataList.get(i).getGoodsImgUrl());
					aisleInfo.setGoodsImgLocalUrl(dataList.get(i).getGoodsImgLocalUrl());
					aisleInfo.setUpdateTime(DateUtil.getCurrentTime());
					aisleInfo.setGoodColdHot(dataList.get(i).getGoodColdHot());
					aisleInfo.setTemp(dataList.get(i).getTemp());
					aisleInfoDao.update(aisleInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 获得左右柜各60个货道信息中State位正常和故障（非锁死）的列表(SparseArray<AisleInfo>)
	 * */
	public static SparseArray<AisleInfo> getAisleInfoListUnlocked(Context context) {
		SparseArray<AisleInfo> dataList = new SparseArray<>();
		QueryBuilder<AisleInfo> qb = CacheDBUtil.getInstance(context).getAisleInfoDao().queryBuilder();
		qb.where(qb.or(AisleInfoDao.Properties.PositionID.between(1,60), AisleInfoDao.Properties.PositionID.between(101,160)),
				AisleInfoDao.Properties.State.notEq(1)).orderAsc(AisleInfoDao.Properties.PositionID);
		for (Iterator<AisleInfo> iterator = qb.list().iterator(); iterator.hasNext();) {
			AisleInfo aisleInfo = (AisleInfo) iterator.next();
			dataList.append(aisleInfo.getPositionID(),aisleInfo);
		}
		return dataList;
	}
	/**
	 * 获得左右柜各60个货道信息中State位正常和故障（非锁死）的列表(ArrayList<Integer>)
	 * */
	public static ArrayList<Integer> getAisleInfoListUnlocked2(Context context) {
		ArrayList<Integer> dataList = new ArrayList<>();
		QueryBuilder<AisleInfo> qb = CacheDBUtil.getInstance(context).getAisleInfoDao().queryBuilder();
		qb.where(qb.or(AisleInfoDao.Properties.PositionID.between(1,60), AisleInfoDao.Properties.PositionID.between(101,160)),
				AisleInfoDao.Properties.State.notEq(1)).orderAsc(AisleInfoDao.Properties.PositionID);
		for (Iterator<AisleInfo> iterator = qb.list().iterator(); iterator.hasNext();) {
			AisleInfo aisleInfo = (AisleInfo) iterator.next();
			dataList.add(aisleInfo.getPositionID());
		}
		return dataList;
	}
	/**
	 * 获得物理上一行有几列（根据货道state位的情况）
	 * @param counter 货柜号 1=左柜（主柜），2 =右柜（辅柜）
	 * @param positionID 货道ID（1-200）
	 * */
	public static int queryPositionNo(Context context, int counter, int positionID) {
		int no = 0;
		for(int i = (positionID-1)/10 * 10+1; i<= (positionID-1)/10 * 10+10; i++){
			if(getAisleInfo(context,counter,i).getState() == 0 ||
					(i>101&& getAisleInfo(context,counter,i).getPosition1()==getAisleInfo(context,counter,i-1).getPosition2()) ||
					(i>1&&i<101&& getAisleInfo(context,counter,i).getPosition1()==getAisleInfo(context,counter,i-1).getPosition2())){
				no++;
			}
		}
		return no;
	}
	/**
	 * 清除查询缓存
	 * */
	public static void clearQueryCache(Context context) {
		CacheDBUtil.getInstance(context).clearQueryCache_aisleInfoDao();
	}

	/**
	 * 获得左右柜各60个货道信息中State位正常（非锁死、非故障）,且存在商品的列表，按照商品类型排序
	 * */
	public static ArrayList<AisleInfo> getAisleInfoListShop(Context context) {
		ArrayList<AisleInfo> dataList = new ArrayList<>();
		QueryBuilder<AisleInfo> qb = CacheDBUtil.getInstance(context).getAisleInfoDao().queryBuilder();
		qb.where(qb.or(AisleInfoDao.Properties.PositionID.between(1,60), AisleInfoDao.Properties.PositionID.between(101,160)),
				AisleInfoDao.Properties.State.eq(0), AisleInfoDao.Properties.GoodID.isNotNull());
		qb.where(AisleInfoDao.Properties.GoodID.ge(0)).orderAsc(AisleInfoDao.Properties.GoodTypeID);
		if(qb.list().size()>0){
			dataList.addAll(qb.list());
		}
		return dataList;
	}
	/**
	 * 获得左右柜各60个货道信息中State位正常（非锁死、非故障）,且存在商品的列表，*且商品库存大于0*，按照商品类型排序，用于主售卖页面显示
	 * */
	public static ArrayList<AisleInfo> getAisleInfoListSaleByAisle(Context context) {
		ArrayList<AisleInfo> dataList = new ArrayList<>();
		QueryBuilder<AisleInfo> qb = CacheDBUtil.getInstance(context).getAisleInfoDao().queryBuilder();
		qb.where(qb.or(AisleInfoDao.Properties.PositionID.between(1,60), AisleInfoDao.Properties.PositionID.between(101,160)),
				AisleInfoDao.Properties.State.eq(0), AisleInfoDao.Properties.GoodID.isNotNull(),AisleInfoDao.Properties.GoodID.ge(0)).orderAsc(AisleInfoDao.Properties.GoodID);
		if(qb.list().size()>0){
			for (Iterator<AisleInfo> iterator = qb.list().iterator(); iterator.hasNext();) {
				AisleInfo aisleInfo = (AisleInfo) iterator.next();
				if(Integer.parseInt(aisleInfo.getGoodQuantity())>0){
					dataList.add(aisleInfo);
				}
			}
		}
		Collections.sort(dataList, new Comparator<AisleInfo>() {
			@Override
			public int compare(AisleInfo aisleInfo1, AisleInfo aisleInfo2) {// 返回值为int类型，大于0表示正序，小于0表示逆序
				if(aisleInfo1.getGoodTypeID() < aisleInfo2.getGoodTypeID()){
					return 1;
				}else return -1;
			}
		});
		return dataList;
	}
	/**
	 * 获得左右柜各60个货道信息中State位正常（非锁死、非故障）,且存在商品的列表，且商品库存大于0，且每件商品只取一个货道，卖空后再取下一个货道，按照商品类型排序，用于主售卖页面显示
	 * */
	public static ArrayList<AisleInfo> getAisleInfoListSaleByCommodity(Context context) {
		ArrayList<AisleInfo> dataList = new ArrayList<>();
		QueryBuilder<AisleInfo> qb = CacheDBUtil.getInstance(context).getAisleInfoDao().queryBuilder();
		qb.where(qb.or(AisleInfoDao.Properties.PositionID.between(1,60), AisleInfoDao.Properties.PositionID.between(101,160)),
				AisleInfoDao.Properties.State.eq(0), AisleInfoDao.Properties.GoodID.isNotNull(),AisleInfoDao.Properties.GoodID.ge(0)).orderAsc(AisleInfoDao.Properties.GoodID);
		if(qb.list().size()>0){
			for (Iterator<AisleInfo> iterator = qb.list().iterator(); iterator.hasNext();) {
				AisleInfo aisleInfo = (AisleInfo) iterator.next();
				if(Integer.parseInt(aisleInfo.getGoodQuantity())>0){
					if(dataList.size()>0){
						if(dataList.get(dataList.size()-1).getGoodID() != aisleInfo.getGoodID()){
							dataList.add(aisleInfo);
						}else{
							if(dataList.size() > 1){
								if(dataList.get(dataList.size()-2).getGoodID() == aisleInfo.getGoodID()){
									if(dataList.size() > 2){
										if(dataList.get(dataList.size()-3).getGoodID() != aisleInfo.getGoodID()){
											if(!dataList.get(dataList.size()-1).getTemp().equals(aisleInfo.getTemp()) && !dataList.get(dataList.size()-2).getTemp().equals(aisleInfo.getTemp())){
												dataList.add(aisleInfo);
											}
										}
									}else{
										if(!dataList.get(dataList.size()-1).getTemp().equals(aisleInfo.getTemp()) && !dataList.get(dataList.size()-2).getTemp().equals(aisleInfo.getTemp())){
											dataList.add(aisleInfo);
										}
									}
								}else{
									if(!dataList.get(dataList.size()-1).getTemp().equals(aisleInfo.getTemp())){
										dataList.add(aisleInfo);
									}
								}
							}else{
								if(!dataList.get(dataList.size()-1).getTemp().equals(aisleInfo.getTemp())){
									dataList.add(aisleInfo);
								}
							}
						}
					}else{
						dataList.add(aisleInfo);
					}
				}
			}
		}
		Collections.sort(dataList, new Comparator<AisleInfo>() {
			@Override
			public int compare(AisleInfo aisleInfo1, AisleInfo aisleInfo2) {// 返回值为int类型，大于0表示正序，小于0表示逆序
				if(aisleInfo1.getGoodTypeID() < aisleInfo2.getGoodTypeID()){
					return 1;
				}else return -1;
			}
		});
		return dataList;
	}
	/**
	 * 更新保存左右柜各60个货道信息中State位正常（非锁死、非故障）,且存在商品的货道信息的冷热情况，用于温度改变后更新冷热显示
	 * */
	public static void updateAisleInfoListShopForTemp(Context context, String leftTemp, String rightTemp) {
		if(!leftTemp.equals("") || !leftTemp.equals("")){
			ArrayList<AisleInfo> dataList = getAisleInfoListShop(context);
			for(int i = 0; i<dataList.size(); i++){
				try {
					AisleInfoDao aisleInfoDao = CacheDBUtil.getInstance(context).getAisleInfoDao();
					QueryBuilder<AisleInfo> qb = aisleInfoDao.queryBuilder();
					qb.where(AisleInfoDao.Properties.PositionID.eq(dataList.get(i).getPositionID()));
					if (qb.list().size() > 0) {
						AisleInfo aisleInfo = qb.list().get(0);
						if(aisleInfo.getGoodColdHot()){
							if(aisleInfo.getCounter() == 1){
								if(!leftTemp.equals("")){
									aisleInfo.setUpdateTime(DateUtil.getCurrentTime());
									aisleInfo.setTemp(leftTemp);
									aisleInfoDao.update(aisleInfo);
								}
							}else{
								if(!rightTemp.equals("")){
									aisleInfo.setUpdateTime(DateUtil.getCurrentTime());
									aisleInfo.setTemp(rightTemp);
									aisleInfoDao.update(aisleInfo);
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 出货完成后，更新库存，根据是否出货判断
	 * */
	public static void updateAisleInfoStock(Context context, JSONArray succesAisle, JSONArray errorAisle) {
		if(!succesAisle.isEmpty()){
			for(int i = 0; i<succesAisle.size(); i++){
				try {
					JSONObject aisle = succesAisle.getJSONObject(i);
					AisleInfoDao aisleInfoDao = CacheDBUtil.getInstance(context).getAisleInfoDao();
					QueryBuilder<AisleInfo> qb = aisleInfoDao.queryBuilder();
					qb.where(AisleInfoDao.Properties.PositionID.eq(Integer.parseInt(aisle.getString("positionID"))));
					if (qb.list().size() > 0) {
						AisleInfo aisleInfo = qb.list().get(0);
						aisleInfo.setUpdateTime(DateUtil.getCurrentTime());
						aisleInfo.setGoodQuantity(String.valueOf(Integer.parseInt(aisleInfo.getGoodQuantity())-1));
						aisleInfoDao.update(aisleInfo);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if(!errorAisle.isEmpty()){
			for(int i = 0; i<errorAisle.size(); i++){
				try {
					JSONObject aisle = errorAisle.getJSONObject(i);
					AisleInfoDao aisleInfoDao = CacheDBUtil.getInstance(context).getAisleInfoDao();
					QueryBuilder<AisleInfo> qb = aisleInfoDao.queryBuilder();
					qb.where(AisleInfoDao.Properties.PositionID.eq(Integer.parseInt(aisle.getString("positionID"))));
					if (qb.list().size() > 0) {
						AisleInfo aisleInfo = qb.list().get(0);
						aisleInfo.setUpdateTime(DateUtil.getCurrentTime());
						aisleInfo.setGoodQuantity(String.valueOf(Integer.parseInt(aisleInfo.getGoodQuantity())-1));
						aisleInfoDao.update(aisleInfo);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 关闭货道，State位变故障，出货失败后调用
	 * */
	public static void closeAisle(Context context, String aisles) {
		String[] str = aisles.split(",");
		for(int i = 0; i<str.length; i++){
			try {
				AisleInfoDao aisleInfoDao = CacheDBUtil.getInstance(context).getAisleInfoDao();
				QueryBuilder<AisleInfo> qb = aisleInfoDao.queryBuilder();
				qb.where(AisleInfoDao.Properties.PositionID.eq(Integer.parseInt(str[i])));
				if (qb.list().size() > 0) {
					AisleInfo aisleInfo = qb.list().get(0);
					aisleInfo.setState(2);
					aisleInfoDao.update(aisleInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*********************************************** AisleMerge 货道合并信息 ***********************************************/
	/**
	 * 保存货道合并信息
	 * positionID 必须为1-200之间，1-100为左柜，101-200为右柜
	 * @param positionID1 相邻的货道号较小的那个，货道号1-200
	 * @param positionID2 相邻的货道号较大的那个，货道号1-200
	 */
	public static void saveAisleMerge(Context context, int positionID1, int positionID2) {
		try {
			AisleMerge aisleMerge = new AisleMerge();
			aisleMerge.setPositionID1(positionID1);
			aisleMerge.setPositionID2(positionID2);
			CacheDBUtil.getInstance(context).getAisleMergeDao().insert(aisleMerge);
			updateAisleInfoMerge(context,positionID1,positionID2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 删除货道合并信息
	 * positionID 必须为1-200之间，1-100为左柜，101-200为右柜
	 * @param positionID1 相邻的货道号较小的那个，货道号1-200
	 * @param positionID2 相邻的货道号较大的那个，货道号1-200
	 */
	public static void deleteAisleMerge(Context context, int positionID1, int positionID2) {
		try {
			QueryBuilder<AisleMerge> qb = CacheDBUtil.getInstance(context).getAisleMergeDao().queryBuilder();
			qb.where(AisleMergeDao.Properties.PositionID1.eq(positionID1),AisleMergeDao.Properties.PositionID2.eq(positionID2));
			CacheDBUtil.getInstance(context).getAisleMergeDao().delete(qb.list().get(0));
			updateAisleInfoRelieveMerge(context,positionID1,positionID2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获得货道合并信息
	 * */
	public static ArrayList<AisleMerge> getAisleMerge(Context context) {
		QueryBuilder<AisleMerge> qb = CacheDBUtil.getInstance(context).getAisleMergeDao().queryBuilder().orderAsc(AisleMergeDao.Properties.PositionID1);;
		ArrayList<AisleMerge> merge = new ArrayList<>();
		if(qb.list().size()>0){
			merge.addAll(qb.list());
		}
		return merge;
	}
/*********************************************** GoodsInfo 售卖商品信息 ***********************************************/
	/**
	 * 添加售卖商品信息
	 */
	public static void addGoodsInfo(Context context, int goodID, String goodName, int goodTypeID, String goodTypeName, String goodDescription, Double goodPrice, String promotion,
                                    Double promotionPrice, String goodsImgUrl, String goodsImgLocalUrl, String goodImgDownload, String updateTime, boolean coldHot) {
		try {
			GoodsInfo goodsInfo = new GoodsInfo();
			goodsInfo.setGoodID(goodID);
			goodsInfo.setGoodName(goodName);
			goodsInfo.setGoodTypeID(goodTypeID);
			goodsInfo.setGoodTypeName(goodTypeName);
			goodsInfo.setGoodDescription(goodDescription);
			goodsInfo.setGoodPrice(goodPrice);
			goodsInfo.setPromotion(promotion);
			goodsInfo.setPromotionPrice(promotionPrice);
			goodsInfo.setGoodsImgUrl(goodsImgUrl);
			goodsInfo.setGoodsImgLocalUrl(goodsImgLocalUrl);
			goodsInfo.setGoodImgDownload(goodImgDownload);
			goodsInfo.setUpdateTime(updateTime);

			goodsInfo.setGoodColdHot(coldHot);
			CacheDBUtil.getInstance(context).getGoodsInfoDao().insertOrReplace(goodsInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 测试用，随意添加几个商品信息
	 */
	public static void addGoodsInfoTest(Context context) {
		try {
			addGoodsInfo(context,1,"溜溜梅",1,"零",
					"",(double)1.5,"",(double)1.5,"",
					Environment.getExternalStorageDirectory()+"/njust_display/goods/11.png","","",false);
			addGoodsInfo(context,2,"饼干",1,"零",
					"",(double)1.5,"",(double)1.5,"",
					Environment.getExternalStorageDirectory()+"/njust_display/goods/12.png","","",false);
			addGoodsInfo(context,3,"乐事",1,"零",
					"",(double)1.5,"",(double)1.5,"",
					Environment.getExternalStorageDirectory()+"/njust_display/goods/13.png","","",false);
			addGoodsInfo(context,4,"加多宝",2,"饮料",
					"",(double)1.5,"",(double)1.5,"",
					Environment.getExternalStorageDirectory()+"/njust_display/goods/14.png","","",false);
			addGoodsInfo(context,5,"百事",2,"饮料",
					"",(double)1.5,"",(double)1.5,"",
					Environment.getExternalStorageDirectory()+"/njust_display/goods/15.png","","",false);
			addGoodsInfo(context,6,"雪碧",2,"饮料",
					"",(double)1.5,"",(double)1.5,"",
					Environment.getExternalStorageDirectory()+"/njust_display/goods/16.png","","",false);
			addGoodsInfo(context,7,"百威",3,"酒水水",
					"",(double)1.5,"",(double)1.5,"",
					Environment.getExternalStorageDirectory()+"/njust_display/goods/17.png","","",false);
			addGoodsInfo(context,8,"香槟",3,"酒水水",
					"",(double)1.5,"",(double)1.5,"",
					Environment.getExternalStorageDirectory()+"/njust_display/goods/18.png","","",false);
			addGoodsInfo(context,9,"葡萄酒",3,"酒水水",
					"",(double)1.5,"",(double)1.5,"",
					Environment.getExternalStorageDirectory()+"/njust_display/goods/19.png","","",false);
			addGoodsInfo(context,10,"面包1",4,"面包",
					"",(double)1.5,"",(double)1.5,"",
					Environment.getExternalStorageDirectory()+"/njust_display/goods/20.png","","",false);
			addGoodsInfo(context,11,"面包2",4,"面包",
					"",(double)1.5,"",(double)1.5,"",
					Environment.getExternalStorageDirectory()+"/njust_display/goods/21.png","","",false);
			addGoodsInfo(context,12,"汉堡",4,"面包",
					"",(double)1.5,"",(double)1.5,"",
					Environment.getExternalStorageDirectory()+"/njust_display/goods/22.png","","",false);
			addGoodsInfo(context,13,"橙子",5,"水果水果",
					"",(double)1.5,"",(double)1.5,"",
					Environment.getExternalStorageDirectory()+"/njust_display/goods/23.png","","",false);
			addGoodsInfo(context,14,"柠檬",5,"水果水果",
					"",(double)1.5,"",(double)1.5,"",
					Environment.getExternalStorageDirectory()+"/njust_display/goods/24.png","","",false);
			addGoodsInfo(context,15,"苹果",5,"水果水果",
					"",(double)1.5,"",(double)1.5,"",
					Environment.getExternalStorageDirectory()+"/njust_display/goods/25.png","","",false);
			addGoodsInfo(context,16,"纸抽",6,"日用品用品",
					"",(double)1.5,"",(double)1.5,"",
					Environment.getExternalStorageDirectory()+"/njust_display/goods/26.png","","",false);
			addGoodsInfo(context,17,"筷子",6,"日用品用品",
					"",(double)1.5,"",(double)1.5,"",
					Environment.getExternalStorageDirectory()+"/njust_display/goods/27.png","","",false);
			addGoodsInfo(context,18,"毛巾",6,"日用品用品",
					"",(double)1.5,"",(double)1.5,"",
					Environment.getExternalStorageDirectory()+"/njust_display/goods/28.png","","",false);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获得售卖商品信息
	 * */
	public static ArrayList<GoodsInfo> getGoodsInfo(Context context) {
		QueryBuilder<GoodsInfo> qb = CacheDBUtil.getInstance(context).getGoodsInfoDao().queryBuilder();
		qb.where(GoodsInfoDao.Properties.GoodID.isNotNull(), GoodsInfoDao.Properties.GoodID.ge(0)).orderAsc(GoodsInfoDao.Properties.GoodID);
		ArrayList<GoodsInfo> dataList = new ArrayList<>();
		for (Iterator<GoodsInfo> iterator = qb.list().iterator(); iterator.hasNext();) {
			GoodsInfo g = (GoodsInfo) iterator.next();
			dataList.add(g);
		}
		return dataList;
	}
	/**
	 * 删除全部本地商品信息
	 * */
	public static void deletAllGoodsInfo(Context context) {
		CacheDBUtil.getInstance(context).getGoodsInfoDao().deleteAll();
	}
	/**
	 * 请求商品库，更新本地商品信息
	 * */
	public static void requestGoodsBank(Context context, JSONArray goodsArray) {
		try {
			for (int i = 0; i < goodsArray.size(); i++) {
				JSONObject goodsObject = goodsArray.getJSONObject(i);

				GoodsInfo goodsInfo = new GoodsInfo();
				goodsInfo.setGoodID(Integer.parseInt(goodsObject.getString("goodsId")));
				goodsInfo.setGoodName(goodsObject.getString("goodsName"));

				int parentId = Integer.parseInt(goodsObject.getString("parentId"));
				String parentName = goodsObject.getString("parentName");
				int catId = Integer.parseInt(goodsObject.getString("catId"));
				String catName = goodsObject.getString("catName");
				if(parentId!=0&&!parentName.equals("")){
					if(parentName.equals("其它")){
						goodsInfo.setGoodTypeID(0);//代表类别 其它
						goodsInfo.setGoodTypeName("其它");
					}else{
						goodsInfo.setGoodTypeID(parentId);
						goodsInfo.setGoodTypeName(parentName);
					}
				}else{
					if(catName.equals("其它")){
						goodsInfo.setGoodTypeID(0);//代表类别 其它
						goodsInfo.setGoodTypeName("其它");
					}else{
						goodsInfo.setGoodTypeID(catId);
						goodsInfo.setGoodTypeName(catName);
					}
				}
				goodsInfo.setGoodDescription("");
				goodsInfo.setGoodPrice(Double.parseDouble(df.format(Double.parseDouble(goodsObject.getString("price")))));
				goodsInfo.setPromotion("");
				goodsInfo.setPromotionPrice((double)1);
				goodsInfo.setGoodsImgUrl(goodsObject.getString("imgAddr"));
				goodsInfo.setGoodsImgLocalUrl("");
				goodsInfo.setGoodImgDownload("");
				goodsInfo.setUpdateTime(DateUtil.getCurrentTime());
				goodsInfo.setGoodColdHot(goodsObject.getString("tempState").equals("1"));
				CacheDBUtil.getInstance(context).getGoodsInfoDao().insertOrReplace(goodsInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 下载保存完商品图片后调用，更新商品图片本地地址
	 * */
	public static void updateGoodImageLocalUrl(Context context,int goodID, String goodImageLocalUrl) {
		try {
			GoodsInfoDao goodsInfoDao = CacheDBUtil.getInstance(context).getGoodsInfoDao();
			QueryBuilder<GoodsInfo> qb = goodsInfoDao.queryBuilder();
			qb.where(GoodsInfoDao.Properties.GoodID.eq(goodID));
			if(qb.list().size() > 0){
				GoodsInfo goodsInfo = qb.list().get(0);
				goodsInfo.setGoodsImgLocalUrl(goodImageLocalUrl);
				goodsInfo.setGoodImgDownload(String.valueOf(Constant.download_success));
				goodsInfoDao.update(goodsInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获得商品信息
	 * @param goodID 商品的ID
	 * */
	public static GoodsInfo getGoodsInfo(Context context, int goodID) {
		GoodsInfoDao goodsInfoDao = CacheDBUtil.getInstance(context).getGoodsInfoDao();
		QueryBuilder<GoodsInfo> qb = goodsInfoDao.queryBuilder();
		qb.where(GoodsInfoDao.Properties.GoodID.eq(goodID));
		return qb.list().size()>0 ?qb.list().get(0) :null;
	}
	/*********************************Adv*********************************/
	/**
	 * 下载完广告之后保存到数据库
	 */
	public static void saveAdv(Context context, String adId, String url, String localUrl, int adType, String playTime, String download) {
		QueryBuilder<Adv> qb = CacheDBUtil.getInstance(context).getAdvDao().queryBuilder();
		qb.where(AdvDao.Properties.AdId.eq(adId));
		if ( qb.list().size()>0 ) {
			Adv adv = qb.list().get(0);
			adv.setUrl(url);
			adv.setLocalUrl(localUrl);
			adv.setAdType(adType);
			adv.setPlayTime(playTime);
			if(adType == Constant.adv_video){
				MediaMetadataRetriever mmr = new MediaMetadataRetriever();
				mmr.setDataSource(localUrl);
				String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 播放时长单位为毫秒
				adv.setDuration(Long.parseLong(duration));
			}else{
				adv.setDuration(5000);
			}
			adv.setDownload(download);
			adv.setBuildTime(DateUtil.getCurrentTime());
			CacheDBUtil.getInstance(context).getAdvDao().insertOrReplace(adv);
		}else{
			try {
				Adv adv = new Adv();
				adv.setAdId(adId);
				adv.setUrl(url);
				adv.setLocalUrl(localUrl);
				adv.setAdType(adType);
				adv.setPlayTime(playTime);
				if(adType == Constant.adv_video){
					Log.w("happy",localUrl);
					MediaMetadataRetriever mmr = new MediaMetadataRetriever();
					mmr.setDataSource(localUrl);
					String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 播放时长单位为毫秒
					adv.setDuration(Long.parseLong(duration));
					Log.w("happy","adv.setDuration"+Long.parseLong(duration));
				}else{
					adv.setDuration(5000);
				}
				adv.setDownload(download);
				adv.setBuildTime(DateUtil.getCurrentTime());
				CacheDBUtil.getInstance(context).getAdvDao().insertOrReplace(adv);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 获得广告信息
	 * */
	public static ArrayList<Adv> getAdvsInfo(Context context) {
		QueryBuilder<Adv> qb = CacheDBUtil.getInstance(context).getAdvDao().queryBuilder();
		qb.where(AdvDao.Properties.AdId.isNotNull(), AdvDao.Properties.AdId.notEq(""));
		ArrayList<Adv> dataList = new ArrayList<>();
		for (Iterator<Adv> iterator = qb.list().iterator(); iterator.hasNext();) {
			Adv adv = (Adv) iterator.next();
			dataList.add(adv);
		}
		return dataList;
	}
	/**
	 * 删除广告信息
	 * */
	public static void deletAdvInfo(Context context,String adId) {
		AdvDao advDao = CacheDBUtil.getInstance(context).getAdvDao();
		QueryBuilder<Adv> qb = advDao.queryBuilder();
		qb.where(AdvDao.Properties.AdId.eq(adId));
		for (Iterator<Adv> iterator = qb.list().iterator(); iterator.hasNext();) {
			Adv adv = (Adv) iterator.next();
			advDao.delete(adv);
		}
	}
	/**
	 * 删除所有广告信息
	 * */
	public static void deletAllAdvInfo(Context context) {
		CacheDBUtil.getInstance(context).getAdvDao().deleteAll();
	}
}