package com.njust.wanyuan_display.greenDao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by my187 on 2018/11/20.
 */

/**
 * 机器状态
 */
@Entity
public class MachineState {
    @Id
    private Long id;
    
    private String machineID; //机器唯一id，绑定后根据安卓板信息生成
    private int vmState; //设备状态
    private int leftState;//边柜状态（1正常 2存在故障 3停机）
    private int rightState;

    //各部分状态相关，备注：温度精度为0.5，所以统一放大10倍进行传输保存，显示时进行缩放
    private int leftCabinetTemp;//边柜温度：有符号数（22222为故障）
    private int leftCabinetTopTemp;//边柜顶部温度：有符号数（22222为故障）
    private int leftCompressorTemp;//压缩机温度：有符号数（22222为故障）
    private int leftCompressorDCfanState;//压缩机直流风扇状态：0=未启动1=正常2=异常
    private int leftCabinetDCfanState;//边柜直流风扇状态：0=未启动1=正常2=异常
    private int leftOutCabinetTemp;//外部温度测量值：有符号数（22222为温湿度传感器故障）
    private int leftDoorheat;//边柜门加热状态：0=关1=开
    private int leftHumidity;//湿度测量值：100以内代表百分比（22222为温湿度传感器故障）
    private int leftLight;//照明灯状态：0=关1=开
    private int leftPushGoodsRaster;//下货光栅状态：0=正常1=故障
    private int leftOutGoodsRaster;//X轴出货光栅状态：0=正常1=故障
    private int leftOutGoodsDoor;//出货门开关状态：0=关1=开2=半开半关


    private int rightCabinetTemp;//边柜温度：有符号数（22222为故障）
    private int rightCabinetTopTemp;//边柜顶部温度：有符号数（22222为故障）
    private int rightCompressorTemp;//压缩机温度：有符号数（22222为故障）
    private int rightCompressorDCfanState;//压缩机直流风扇状态：0=未启动1=正常2=异常
    private int rightCabinetDCfanState;//边柜直流风扇状态：0=未启动1=正常2=异常
    private int rightOutCabinetTemp;//外部温度测量值：有符号数（22222为温湿度传感器故障）
    private int rightDoorheat;//边柜门加热状态：0=关1=开
    private int rightHumidity;//湿度测量值：100以内代表百分比（22222为温湿度传感器故障）
    private int rightLight;//照明灯状态：0=关1=开
    private int rightPushGoodsRaster;//下货光栅状态：0=正常1=故障
    private int rightOutGoodsRaster;//X轴出货光栅状态：0=正常1=故障
    private int rightOutGoodsDoor;//出货门开关状态：0=关1=开2=半开半关

    private int midLight;//中间照明灯状态：0=关1=开
    private int midDoorLock;//中间门锁状态：0=上锁1=开锁
    private int midDoor;//中间门开关状态：1=关门0=开门
    private int midGetGoodsRaster;//中间取货光栅状态：0=正常1=故障
    private int midDropGoodsRaster;//中间落货光栅状态：0=正常1=故障
    private int midAntiPinchHandRaster;//中间防夹手光栅状态：0=正常1=故障
    private int midGetDoor;//取货门开关状态：0=关1=开2=半开半关
    private int midDropDoor;//落货门开关状态：0=关1=开2=半开半关

    //更新单片机通讯协议后新加的字段
    private int leftTempControlAlternatPower;//温控交流总电源状态（0=断开，1=接通）
    private int leftRefrigerationCompressorState;//制冷压缩机工作状态（0=停止，1=运转）
    private int leftCompressorFanState;//压缩机风扇工作状态（0=停止，1=运转）
    private int leftHeatingWireState;//制热电热丝工作状态（0=断开，1=接通）
    private int leftRecirculatAirFanState;//循环风风扇工作状态（0=停止，1=运转）

    private int leftLiftPlatformDownSwitch;//升降台下止点开关即时状态（0=开，1=合）
    private int leftLiftPlatformUpSwitch;//升降台上止点开关即时状态（0=开，1=合）
    private int leftLiftPlatformOutGoodsSwitch;//升降台出货口开关即时状态（0=开，1=合）
    private int leftOutGoodsRasterImmediately;//出货光栅传感器即时状态（0=透光，1=遮光）
    private int leftPushGoodsRasterImmediately;//下货光栅即时状态（0=透光，1=遮光）
    private int leftMotorFeedbackState1;//电机1反馈状态（0=凸台位，1=缺口位）
    private int leftMotorFeedbackState2;//电机2反馈状态（0=凸台位，1=缺口位）
    private int leftOutGoodsDoorDownSwitch;//出货门下止点开关即时状态（0=开，1=合）
    private int leftOutGoodsDoorUpSwitch;//出货门上止点开关即时状态（0=开，1=合）

    private int rightTempControlAlternatPower;//温控交流总电源状态（0=断开，1=接通）
    private int rightRefrigerationCompressorState;//制冷压缩机工作状态（0=停止，1=运转）
    private int rightCompressorFanState;//压缩机风扇工作状态（0=停止，1=运转）
    private int rightHeatingWireState;//制热电热丝工作状态（0=断开，1=接通）
    private int rightRecirculatAirFanState;//循环风风扇工作状态（0=停止，1=运转）

    private int rightLiftPlatformDownSwitch;//升降台下止点开关即时状态（0=开，1=合）
    private int rightLiftPlatformUpSwitch;//升降台上止点开关即时状态（0=开，1=合）
    private int rightLiftPlatformOutGoodsSwitch;//升降台出货口开关即时状态（0=开，1=合）
    private int rightOutGoodsRasterImmediately;//出货光栅传感器即时状态（0=透光，1=遮光）
    private int rightPushGoodsRasterImmediately;//下货光栅即时状态（0=透光，1=遮光）
    private int rightMotorFeedbackState1;//电机1反馈状态（0=凸台位，1=缺口位）
    private int rightMotorFeedbackState2;//电机2反馈状态（0=凸台位，1=缺口位）
    private int rightOutGoodsDoorDownSwitch;//出货门下止点开关即时状态（0=开，1=合）
    private int rightOutGoodsDoorUpSwitch;//出货门上止点开关即时状态（0=开，1=合）


    private int midGetDoorWaitClose;//取货门待关门标志（0=正常，1=待关门）
    private int midGetDoorDownSwitch;//取货门下止点开关即时状态（0=开，1=合）
    private int midGetDoorUpSwitch;//取货门上止点开关即时状态（0=开，1=合）
    private int midDropDoorDownSwitch;//落货门下止点开关即时状态（0=开，1=合）
    private int midDropDoorUpSwitch;//落货门上止点开关即时状态（0=开，1=合）
    @Generated(hash = 481296547)
    public MachineState(Long id, String machineID, int vmState, int leftState,
            int rightState, int leftCabinetTemp, int leftCabinetTopTemp,
            int leftCompressorTemp, int leftCompressorDCfanState,
            int leftCabinetDCfanState, int leftOutCabinetTemp, int leftDoorheat,
            int leftHumidity, int leftLight, int leftPushGoodsRaster,
            int leftOutGoodsRaster, int leftOutGoodsDoor, int rightCabinetTemp,
            int rightCabinetTopTemp, int rightCompressorTemp,
            int rightCompressorDCfanState, int rightCabinetDCfanState,
            int rightOutCabinetTemp, int rightDoorheat, int rightHumidity,
            int rightLight, int rightPushGoodsRaster, int rightOutGoodsRaster,
            int rightOutGoodsDoor, int midLight, int midDoorLock, int midDoor,
            int midGetGoodsRaster, int midDropGoodsRaster,
            int midAntiPinchHandRaster, int midGetDoor, int midDropDoor,
            int leftTempControlAlternatPower, int leftRefrigerationCompressorState,
            int leftCompressorFanState, int leftHeatingWireState,
            int leftRecirculatAirFanState, int leftLiftPlatformDownSwitch,
            int leftLiftPlatformUpSwitch, int leftLiftPlatformOutGoodsSwitch,
            int leftOutGoodsRasterImmediately, int leftPushGoodsRasterImmediately,
            int leftMotorFeedbackState1, int leftMotorFeedbackState2,
            int leftOutGoodsDoorDownSwitch, int leftOutGoodsDoorUpSwitch,
            int rightTempControlAlternatPower,
            int rightRefrigerationCompressorState, int rightCompressorFanState,
            int rightHeatingWireState, int rightRecirculatAirFanState,
            int rightLiftPlatformDownSwitch, int rightLiftPlatformUpSwitch,
            int rightLiftPlatformOutGoodsSwitch, int rightOutGoodsRasterImmediately,
            int rightPushGoodsRasterImmediately, int rightMotorFeedbackState1,
            int rightMotorFeedbackState2, int rightOutGoodsDoorDownSwitch,
            int rightOutGoodsDoorUpSwitch, int midGetDoorWaitClose,
            int midGetDoorDownSwitch, int midGetDoorUpSwitch,
            int midDropDoorDownSwitch, int midDropDoorUpSwitch) {
        this.id = id;
        this.machineID = machineID;
        this.vmState = vmState;
        this.leftState = leftState;
        this.rightState = rightState;
        this.leftCabinetTemp = leftCabinetTemp;
        this.leftCabinetTopTemp = leftCabinetTopTemp;
        this.leftCompressorTemp = leftCompressorTemp;
        this.leftCompressorDCfanState = leftCompressorDCfanState;
        this.leftCabinetDCfanState = leftCabinetDCfanState;
        this.leftOutCabinetTemp = leftOutCabinetTemp;
        this.leftDoorheat = leftDoorheat;
        this.leftHumidity = leftHumidity;
        this.leftLight = leftLight;
        this.leftPushGoodsRaster = leftPushGoodsRaster;
        this.leftOutGoodsRaster = leftOutGoodsRaster;
        this.leftOutGoodsDoor = leftOutGoodsDoor;
        this.rightCabinetTemp = rightCabinetTemp;
        this.rightCabinetTopTemp = rightCabinetTopTemp;
        this.rightCompressorTemp = rightCompressorTemp;
        this.rightCompressorDCfanState = rightCompressorDCfanState;
        this.rightCabinetDCfanState = rightCabinetDCfanState;
        this.rightOutCabinetTemp = rightOutCabinetTemp;
        this.rightDoorheat = rightDoorheat;
        this.rightHumidity = rightHumidity;
        this.rightLight = rightLight;
        this.rightPushGoodsRaster = rightPushGoodsRaster;
        this.rightOutGoodsRaster = rightOutGoodsRaster;
        this.rightOutGoodsDoor = rightOutGoodsDoor;
        this.midLight = midLight;
        this.midDoorLock = midDoorLock;
        this.midDoor = midDoor;
        this.midGetGoodsRaster = midGetGoodsRaster;
        this.midDropGoodsRaster = midDropGoodsRaster;
        this.midAntiPinchHandRaster = midAntiPinchHandRaster;
        this.midGetDoor = midGetDoor;
        this.midDropDoor = midDropDoor;
        this.leftTempControlAlternatPower = leftTempControlAlternatPower;
        this.leftRefrigerationCompressorState = leftRefrigerationCompressorState;
        this.leftCompressorFanState = leftCompressorFanState;
        this.leftHeatingWireState = leftHeatingWireState;
        this.leftRecirculatAirFanState = leftRecirculatAirFanState;
        this.leftLiftPlatformDownSwitch = leftLiftPlatformDownSwitch;
        this.leftLiftPlatformUpSwitch = leftLiftPlatformUpSwitch;
        this.leftLiftPlatformOutGoodsSwitch = leftLiftPlatformOutGoodsSwitch;
        this.leftOutGoodsRasterImmediately = leftOutGoodsRasterImmediately;
        this.leftPushGoodsRasterImmediately = leftPushGoodsRasterImmediately;
        this.leftMotorFeedbackState1 = leftMotorFeedbackState1;
        this.leftMotorFeedbackState2 = leftMotorFeedbackState2;
        this.leftOutGoodsDoorDownSwitch = leftOutGoodsDoorDownSwitch;
        this.leftOutGoodsDoorUpSwitch = leftOutGoodsDoorUpSwitch;
        this.rightTempControlAlternatPower = rightTempControlAlternatPower;
        this.rightRefrigerationCompressorState = rightRefrigerationCompressorState;
        this.rightCompressorFanState = rightCompressorFanState;
        this.rightHeatingWireState = rightHeatingWireState;
        this.rightRecirculatAirFanState = rightRecirculatAirFanState;
        this.rightLiftPlatformDownSwitch = rightLiftPlatformDownSwitch;
        this.rightLiftPlatformUpSwitch = rightLiftPlatformUpSwitch;
        this.rightLiftPlatformOutGoodsSwitch = rightLiftPlatformOutGoodsSwitch;
        this.rightOutGoodsRasterImmediately = rightOutGoodsRasterImmediately;
        this.rightPushGoodsRasterImmediately = rightPushGoodsRasterImmediately;
        this.rightMotorFeedbackState1 = rightMotorFeedbackState1;
        this.rightMotorFeedbackState2 = rightMotorFeedbackState2;
        this.rightOutGoodsDoorDownSwitch = rightOutGoodsDoorDownSwitch;
        this.rightOutGoodsDoorUpSwitch = rightOutGoodsDoorUpSwitch;
        this.midGetDoorWaitClose = midGetDoorWaitClose;
        this.midGetDoorDownSwitch = midGetDoorDownSwitch;
        this.midGetDoorUpSwitch = midGetDoorUpSwitch;
        this.midDropDoorDownSwitch = midDropDoorDownSwitch;
        this.midDropDoorUpSwitch = midDropDoorUpSwitch;
    }
    @Generated(hash = 633272336)
    public MachineState() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMachineID() {
        return this.machineID;
    }
    public void setMachineID(String machineID) {
        this.machineID = machineID;
    }
    public int getVmState() {
        return this.vmState;
    }
    public void setVmState(int vmState) {
        this.vmState = vmState;
    }
    public int getLeftState() {
        return this.leftState;
    }
    public void setLeftState(int leftState) {
        this.leftState = leftState;
    }
    public int getRightState() {
        return this.rightState;
    }
    public void setRightState(int rightState) {
        this.rightState = rightState;
    }
    public int getLeftCabinetTemp() {
        return this.leftCabinetTemp;
    }
    public void setLeftCabinetTemp(int leftCabinetTemp) {
        this.leftCabinetTemp = leftCabinetTemp;
    }
    public int getLeftCabinetTopTemp() {
        return this.leftCabinetTopTemp;
    }
    public void setLeftCabinetTopTemp(int leftCabinetTopTemp) {
        this.leftCabinetTopTemp = leftCabinetTopTemp;
    }
    public int getLeftCompressorTemp() {
        return this.leftCompressorTemp;
    }
    public void setLeftCompressorTemp(int leftCompressorTemp) {
        this.leftCompressorTemp = leftCompressorTemp;
    }
    public int getLeftCompressorDCfanState() {
        return this.leftCompressorDCfanState;
    }
    public void setLeftCompressorDCfanState(int leftCompressorDCfanState) {
        this.leftCompressorDCfanState = leftCompressorDCfanState;
    }
    public int getLeftCabinetDCfanState() {
        return this.leftCabinetDCfanState;
    }
    public void setLeftCabinetDCfanState(int leftCabinetDCfanState) {
        this.leftCabinetDCfanState = leftCabinetDCfanState;
    }
    public int getLeftOutCabinetTemp() {
        return this.leftOutCabinetTemp;
    }
    public void setLeftOutCabinetTemp(int leftOutCabinetTemp) {
        this.leftOutCabinetTemp = leftOutCabinetTemp;
    }
    public int getLeftDoorheat() {
        return this.leftDoorheat;
    }
    public void setLeftDoorheat(int leftDoorheat) {
        this.leftDoorheat = leftDoorheat;
    }
    public int getLeftHumidity() {
        return this.leftHumidity;
    }
    public void setLeftHumidity(int leftHumidity) {
        this.leftHumidity = leftHumidity;
    }
    public int getLeftLight() {
        return this.leftLight;
    }
    public void setLeftLight(int leftLight) {
        this.leftLight = leftLight;
    }
    public int getLeftPushGoodsRaster() {
        return this.leftPushGoodsRaster;
    }
    public void setLeftPushGoodsRaster(int leftPushGoodsRaster) {
        this.leftPushGoodsRaster = leftPushGoodsRaster;
    }
    public int getLeftOutGoodsRaster() {
        return this.leftOutGoodsRaster;
    }
    public void setLeftOutGoodsRaster(int leftOutGoodsRaster) {
        this.leftOutGoodsRaster = leftOutGoodsRaster;
    }
    public int getLeftOutGoodsDoor() {
        return this.leftOutGoodsDoor;
    }
    public void setLeftOutGoodsDoor(int leftOutGoodsDoor) {
        this.leftOutGoodsDoor = leftOutGoodsDoor;
    }
    public int getRightCabinetTemp() {
        return this.rightCabinetTemp;
    }
    public void setRightCabinetTemp(int rightCabinetTemp) {
        this.rightCabinetTemp = rightCabinetTemp;
    }
    public int getRightCabinetTopTemp() {
        return this.rightCabinetTopTemp;
    }
    public void setRightCabinetTopTemp(int rightCabinetTopTemp) {
        this.rightCabinetTopTemp = rightCabinetTopTemp;
    }
    public int getRightCompressorTemp() {
        return this.rightCompressorTemp;
    }
    public void setRightCompressorTemp(int rightCompressorTemp) {
        this.rightCompressorTemp = rightCompressorTemp;
    }
    public int getRightCompressorDCfanState() {
        return this.rightCompressorDCfanState;
    }
    public void setRightCompressorDCfanState(int rightCompressorDCfanState) {
        this.rightCompressorDCfanState = rightCompressorDCfanState;
    }
    public int getRightCabinetDCfanState() {
        return this.rightCabinetDCfanState;
    }
    public void setRightCabinetDCfanState(int rightCabinetDCfanState) {
        this.rightCabinetDCfanState = rightCabinetDCfanState;
    }
    public int getRightOutCabinetTemp() {
        return this.rightOutCabinetTemp;
    }
    public void setRightOutCabinetTemp(int rightOutCabinetTemp) {
        this.rightOutCabinetTemp = rightOutCabinetTemp;
    }
    public int getRightDoorheat() {
        return this.rightDoorheat;
    }
    public void setRightDoorheat(int rightDoorheat) {
        this.rightDoorheat = rightDoorheat;
    }
    public int getRightHumidity() {
        return this.rightHumidity;
    }
    public void setRightHumidity(int rightHumidity) {
        this.rightHumidity = rightHumidity;
    }
    public int getRightLight() {
        return this.rightLight;
    }
    public void setRightLight(int rightLight) {
        this.rightLight = rightLight;
    }
    public int getRightPushGoodsRaster() {
        return this.rightPushGoodsRaster;
    }
    public void setRightPushGoodsRaster(int rightPushGoodsRaster) {
        this.rightPushGoodsRaster = rightPushGoodsRaster;
    }
    public int getRightOutGoodsRaster() {
        return this.rightOutGoodsRaster;
    }
    public void setRightOutGoodsRaster(int rightOutGoodsRaster) {
        this.rightOutGoodsRaster = rightOutGoodsRaster;
    }
    public int getRightOutGoodsDoor() {
        return this.rightOutGoodsDoor;
    }
    public void setRightOutGoodsDoor(int rightOutGoodsDoor) {
        this.rightOutGoodsDoor = rightOutGoodsDoor;
    }
    public int getMidLight() {
        return this.midLight;
    }
    public void setMidLight(int midLight) {
        this.midLight = midLight;
    }
    public int getMidDoorLock() {
        return this.midDoorLock;
    }
    public void setMidDoorLock(int midDoorLock) {
        this.midDoorLock = midDoorLock;
    }
    public int getMidDoor() {
        return this.midDoor;
    }
    public void setMidDoor(int midDoor) {
        this.midDoor = midDoor;
    }
    public int getMidGetGoodsRaster() {
        return this.midGetGoodsRaster;
    }
    public void setMidGetGoodsRaster(int midGetGoodsRaster) {
        this.midGetGoodsRaster = midGetGoodsRaster;
    }
    public int getMidDropGoodsRaster() {
        return this.midDropGoodsRaster;
    }
    public void setMidDropGoodsRaster(int midDropGoodsRaster) {
        this.midDropGoodsRaster = midDropGoodsRaster;
    }
    public int getMidAntiPinchHandRaster() {
        return this.midAntiPinchHandRaster;
    }
    public void setMidAntiPinchHandRaster(int midAntiPinchHandRaster) {
        this.midAntiPinchHandRaster = midAntiPinchHandRaster;
    }
    public int getMidGetDoor() {
        return this.midGetDoor;
    }
    public void setMidGetDoor(int midGetDoor) {
        this.midGetDoor = midGetDoor;
    }
    public int getMidDropDoor() {
        return this.midDropDoor;
    }
    public void setMidDropDoor(int midDropDoor) {
        this.midDropDoor = midDropDoor;
    }
    public int getLeftTempControlAlternatPower() {
        return this.leftTempControlAlternatPower;
    }
    public void setLeftTempControlAlternatPower(int leftTempControlAlternatPower) {
        this.leftTempControlAlternatPower = leftTempControlAlternatPower;
    }
    public int getLeftRefrigerationCompressorState() {
        return this.leftRefrigerationCompressorState;
    }
    public void setLeftRefrigerationCompressorState(
            int leftRefrigerationCompressorState) {
        this.leftRefrigerationCompressorState = leftRefrigerationCompressorState;
    }
    public int getLeftCompressorFanState() {
        return this.leftCompressorFanState;
    }
    public void setLeftCompressorFanState(int leftCompressorFanState) {
        this.leftCompressorFanState = leftCompressorFanState;
    }
    public int getLeftHeatingWireState() {
        return this.leftHeatingWireState;
    }
    public void setLeftHeatingWireState(int leftHeatingWireState) {
        this.leftHeatingWireState = leftHeatingWireState;
    }
    public int getLeftRecirculatAirFanState() {
        return this.leftRecirculatAirFanState;
    }
    public void setLeftRecirculatAirFanState(int leftRecirculatAirFanState) {
        this.leftRecirculatAirFanState = leftRecirculatAirFanState;
    }
    public int getLeftLiftPlatformDownSwitch() {
        return this.leftLiftPlatformDownSwitch;
    }
    public void setLeftLiftPlatformDownSwitch(int leftLiftPlatformDownSwitch) {
        this.leftLiftPlatformDownSwitch = leftLiftPlatformDownSwitch;
    }
    public int getLeftLiftPlatformUpSwitch() {
        return this.leftLiftPlatformUpSwitch;
    }
    public void setLeftLiftPlatformUpSwitch(int leftLiftPlatformUpSwitch) {
        this.leftLiftPlatformUpSwitch = leftLiftPlatformUpSwitch;
    }
    public int getLeftLiftPlatformOutGoodsSwitch() {
        return this.leftLiftPlatformOutGoodsSwitch;
    }
    public void setLeftLiftPlatformOutGoodsSwitch(
            int leftLiftPlatformOutGoodsSwitch) {
        this.leftLiftPlatformOutGoodsSwitch = leftLiftPlatformOutGoodsSwitch;
    }
    public int getLeftOutGoodsRasterImmediately() {
        return this.leftOutGoodsRasterImmediately;
    }
    public void setLeftOutGoodsRasterImmediately(
            int leftOutGoodsRasterImmediately) {
        this.leftOutGoodsRasterImmediately = leftOutGoodsRasterImmediately;
    }
    public int getLeftPushGoodsRasterImmediately() {
        return this.leftPushGoodsRasterImmediately;
    }
    public void setLeftPushGoodsRasterImmediately(
            int leftPushGoodsRasterImmediately) {
        this.leftPushGoodsRasterImmediately = leftPushGoodsRasterImmediately;
    }
    public int getLeftMotorFeedbackState1() {
        return this.leftMotorFeedbackState1;
    }
    public void setLeftMotorFeedbackState1(int leftMotorFeedbackState1) {
        this.leftMotorFeedbackState1 = leftMotorFeedbackState1;
    }
    public int getLeftMotorFeedbackState2() {
        return this.leftMotorFeedbackState2;
    }
    public void setLeftMotorFeedbackState2(int leftMotorFeedbackState2) {
        this.leftMotorFeedbackState2 = leftMotorFeedbackState2;
    }
    public int getLeftOutGoodsDoorDownSwitch() {
        return this.leftOutGoodsDoorDownSwitch;
    }
    public void setLeftOutGoodsDoorDownSwitch(int leftOutGoodsDoorDownSwitch) {
        this.leftOutGoodsDoorDownSwitch = leftOutGoodsDoorDownSwitch;
    }
    public int getLeftOutGoodsDoorUpSwitch() {
        return this.leftOutGoodsDoorUpSwitch;
    }
    public void setLeftOutGoodsDoorUpSwitch(int leftOutGoodsDoorUpSwitch) {
        this.leftOutGoodsDoorUpSwitch = leftOutGoodsDoorUpSwitch;
    }
    public int getRightTempControlAlternatPower() {
        return this.rightTempControlAlternatPower;
    }
    public void setRightTempControlAlternatPower(
            int rightTempControlAlternatPower) {
        this.rightTempControlAlternatPower = rightTempControlAlternatPower;
    }
    public int getRightRefrigerationCompressorState() {
        return this.rightRefrigerationCompressorState;
    }
    public void setRightRefrigerationCompressorState(
            int rightRefrigerationCompressorState) {
        this.rightRefrigerationCompressorState = rightRefrigerationCompressorState;
    }
    public int getRightCompressorFanState() {
        return this.rightCompressorFanState;
    }
    public void setRightCompressorFanState(int rightCompressorFanState) {
        this.rightCompressorFanState = rightCompressorFanState;
    }
    public int getRightHeatingWireState() {
        return this.rightHeatingWireState;
    }
    public void setRightHeatingWireState(int rightHeatingWireState) {
        this.rightHeatingWireState = rightHeatingWireState;
    }
    public int getRightRecirculatAirFanState() {
        return this.rightRecirculatAirFanState;
    }
    public void setRightRecirculatAirFanState(int rightRecirculatAirFanState) {
        this.rightRecirculatAirFanState = rightRecirculatAirFanState;
    }
    public int getRightLiftPlatformDownSwitch() {
        return this.rightLiftPlatformDownSwitch;
    }
    public void setRightLiftPlatformDownSwitch(int rightLiftPlatformDownSwitch) {
        this.rightLiftPlatformDownSwitch = rightLiftPlatformDownSwitch;
    }
    public int getRightLiftPlatformUpSwitch() {
        return this.rightLiftPlatformUpSwitch;
    }
    public void setRightLiftPlatformUpSwitch(int rightLiftPlatformUpSwitch) {
        this.rightLiftPlatformUpSwitch = rightLiftPlatformUpSwitch;
    }
    public int getRightLiftPlatformOutGoodsSwitch() {
        return this.rightLiftPlatformOutGoodsSwitch;
    }
    public void setRightLiftPlatformOutGoodsSwitch(
            int rightLiftPlatformOutGoodsSwitch) {
        this.rightLiftPlatformOutGoodsSwitch = rightLiftPlatformOutGoodsSwitch;
    }
    public int getRightOutGoodsRasterImmediately() {
        return this.rightOutGoodsRasterImmediately;
    }
    public void setRightOutGoodsRasterImmediately(
            int rightOutGoodsRasterImmediately) {
        this.rightOutGoodsRasterImmediately = rightOutGoodsRasterImmediately;
    }
    public int getRightPushGoodsRasterImmediately() {
        return this.rightPushGoodsRasterImmediately;
    }
    public void setRightPushGoodsRasterImmediately(
            int rightPushGoodsRasterImmediately) {
        this.rightPushGoodsRasterImmediately = rightPushGoodsRasterImmediately;
    }
    public int getRightMotorFeedbackState1() {
        return this.rightMotorFeedbackState1;
    }
    public void setRightMotorFeedbackState1(int rightMotorFeedbackState1) {
        this.rightMotorFeedbackState1 = rightMotorFeedbackState1;
    }
    public int getRightMotorFeedbackState2() {
        return this.rightMotorFeedbackState2;
    }
    public void setRightMotorFeedbackState2(int rightMotorFeedbackState2) {
        this.rightMotorFeedbackState2 = rightMotorFeedbackState2;
    }
    public int getRightOutGoodsDoorDownSwitch() {
        return this.rightOutGoodsDoorDownSwitch;
    }
    public void setRightOutGoodsDoorDownSwitch(int rightOutGoodsDoorDownSwitch) {
        this.rightOutGoodsDoorDownSwitch = rightOutGoodsDoorDownSwitch;
    }
    public int getRightOutGoodsDoorUpSwitch() {
        return this.rightOutGoodsDoorUpSwitch;
    }
    public void setRightOutGoodsDoorUpSwitch(int rightOutGoodsDoorUpSwitch) {
        this.rightOutGoodsDoorUpSwitch = rightOutGoodsDoorUpSwitch;
    }
    public int getMidGetDoorWaitClose() {
        return this.midGetDoorWaitClose;
    }
    public void setMidGetDoorWaitClose(int midGetDoorWaitClose) {
        this.midGetDoorWaitClose = midGetDoorWaitClose;
    }
    public int getMidGetDoorDownSwitch() {
        return this.midGetDoorDownSwitch;
    }
    public void setMidGetDoorDownSwitch(int midGetDoorDownSwitch) {
        this.midGetDoorDownSwitch = midGetDoorDownSwitch;
    }
    public int getMidGetDoorUpSwitch() {
        return this.midGetDoorUpSwitch;
    }
    public void setMidGetDoorUpSwitch(int midGetDoorUpSwitch) {
        this.midGetDoorUpSwitch = midGetDoorUpSwitch;
    }
    public int getMidDropDoorDownSwitch() {
        return this.midDropDoorDownSwitch;
    }
    public void setMidDropDoorDownSwitch(int midDropDoorDownSwitch) {
        this.midDropDoorDownSwitch = midDropDoorDownSwitch;
    }
    public int getMidDropDoorUpSwitch() {
        return this.midDropDoorUpSwitch;
    }
    public void setMidDropDoorUpSwitch(int midDropDoorUpSwitch) {
        this.midDropDoorUpSwitch = midDropDoorUpSwitch;
    }

}
