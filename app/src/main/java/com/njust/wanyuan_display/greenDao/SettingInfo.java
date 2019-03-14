package com.njust.wanyuan_display.greenDao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by my187 on 2018/11/20.
 */

/**
 * 设定信息
 */
@Entity
public class SettingInfo {
    @Id
    private Long id;

    private String machineID; //机器唯一id，绑定后根据安卓板信息生成
    
    private int leftTempState;//温控模式：0=制冷1=制热2=常温  3=恒温
    private int leftSetTemp;//设定温度:-20～70，精度为0.5，所以统一放大10倍进行传输保存，显示时进行缩放

    private int rightTempState;//温控模式：0=制冷1=制热2=常温  3=恒温
    private int rightSetTemp;//设定温度:-20～70，精度为0.5，所以统一放大10倍进行传输保存，显示时进行缩放


    private String leftSetLight;//设定灯状态0=关1=开
    private String leftLightStartTime;//灯开启的时间,格式为：07:00:00
    private String leftLightEndTime;//灯关闭的时间,格式为：07:00:00
    private String rightSetLight;//设定灯状态0=关1=开
    private String rightLightStartTime;//灯开启的时间,格式为：07:00:00
    private String rightLightEndTime;//灯关闭的时间,格式为：07:00:00


    //位置相关
    private String leftOutPosition;//Y轴电机出货口位置
    private String leftFlootPosition;//每一层的位置，空格隔开
    private String leftFlootNo;//层数

    private String rightOutPosition;//Y轴电机出货口位置
    private String rightFlootPosition;//每一层的位置，空格隔开
    private String rightFlootNo;//层数

    @Generated(hash = 1456033140)
    public SettingInfo(Long id, String machineID, int leftTempState,
                       int leftSetTemp, int rightTempState, int rightSetTemp,
                       String leftSetLight, String leftLightStartTime, String leftLightEndTime,
                       String rightSetLight, String rightLightStartTime,
                       String rightLightEndTime, String leftOutPosition,
                       String leftFlootPosition, String leftFlootNo, String rightOutPosition,
                       String rightFlootPosition, String rightFlootNo) {
        this.id = id;
        this.machineID = machineID;
        this.leftTempState = leftTempState;
        this.leftSetTemp = leftSetTemp;
        this.rightTempState = rightTempState;
        this.rightSetTemp = rightSetTemp;
        this.leftSetLight = leftSetLight;
        this.leftLightStartTime = leftLightStartTime;
        this.leftLightEndTime = leftLightEndTime;
        this.rightSetLight = rightSetLight;
        this.rightLightStartTime = rightLightStartTime;
        this.rightLightEndTime = rightLightEndTime;
        this.leftOutPosition = leftOutPosition;
        this.leftFlootPosition = leftFlootPosition;
        this.leftFlootNo = leftFlootNo;
        this.rightOutPosition = rightOutPosition;
        this.rightFlootPosition = rightFlootPosition;
        this.rightFlootNo = rightFlootNo;
    }
    @Generated(hash = 598560875)
    public SettingInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getLeftTempState() {
        return this.leftTempState;
    }
    public void setLeftTempState(int leftTempState) {
        this.leftTempState = leftTempState;
    }
    public int getLeftSetTemp() {
        return this.leftSetTemp;
    }
    public void setLeftSetTemp(int leftSetTemp) {
        this.leftSetTemp = leftSetTemp;
    }
    public int getRightTempState() {
        return this.rightTempState;
    }
    public void setRightTempState(int rightTempState) {
        this.rightTempState = rightTempState;
    }
    public int getRightSetTemp() {
        return this.rightSetTemp;
    }
    public void setRightSetTemp(int rightSetTemp) {
        this.rightSetTemp = rightSetTemp;
    }
    public String getLeftSetLight() {
        return this.leftSetLight;
    }
    public void setLeftSetLight(String leftSetLight) {
        this.leftSetLight = leftSetLight;
    }
    public String getLeftLightStartTime() {
        return this.leftLightStartTime;
    }
    public void setLeftLightStartTime(String leftLightStartTime) {
        this.leftLightStartTime = leftLightStartTime;
    }
    public String getLeftLightEndTime() {
        return this.leftLightEndTime;
    }
    public void setLeftLightEndTime(String leftLightEndTime) {
        this.leftLightEndTime = leftLightEndTime;
    }
    public String getRightSetLight() {
        return this.rightSetLight;
    }
    public void setRightSetLight(String rightSetLight) {
        this.rightSetLight = rightSetLight;
    }
    public String getRightLightStartTime() {
        return this.rightLightStartTime;
    }
    public void setRightLightStartTime(String rightLightStartTime) {
        this.rightLightStartTime = rightLightStartTime;
    }
    public String getRightLightEndTime() {
        return this.rightLightEndTime;
    }
    public void setRightLightEndTime(String rightLightEndTime) {
        this.rightLightEndTime = rightLightEndTime;
    }
    public String getLeftOutPosition() {
        return this.leftOutPosition;
    }
    public void setLeftOutPosition(String leftOutPosition) {
        this.leftOutPosition = leftOutPosition;
    }
    public String getLeftFlootPosition() {
        return this.leftFlootPosition;
    }
    public void setLeftFlootPosition(String leftFlootPosition) {
        this.leftFlootPosition = leftFlootPosition;
    }
    public String getLeftFlootNo() {
        return this.leftFlootNo;
    }
    public void setLeftFlootNo(String leftFlootNo) {
        this.leftFlootNo = leftFlootNo;
    }
    public String getRightOutPosition() {
        return this.rightOutPosition;
    }
    public void setRightOutPosition(String rightOutPosition) {
        this.rightOutPosition = rightOutPosition;
    }
    public String getRightFlootPosition() {
        return this.rightFlootPosition;
    }
    public void setRightFlootPosition(String rightFlootPosition) {
        this.rightFlootPosition = rightFlootPosition;
    }
    public String getRightFlootNo() {
        return this.rightFlootNo;
    }
    public void setRightFlootNo(String rightFlootNo) {
        this.rightFlootNo = rightFlootNo;
    }
    public String getMachineID() {
        return this.machineID;
    }
    public void setMachineID(String machineID) {
        this.machineID = machineID;
    }
}
