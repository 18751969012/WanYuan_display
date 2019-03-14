package com.njust.wanyuan_display.greenDao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by my187 on 2018/11/20.
 */

/**
 * 机器基本信息
 */
@Entity
public class MachineInfo {
    @Id
    private Long id;

    private String machineID; //机器唯一id，绑定后根据安卓板信息生成
    private String userID;//用户id
    private String userName;//用户绑定登录名
    private String userPassword;//用户绑定登录密码
    private String loginName;//用户后台登录名
    private String loginPassword;//用户后台登录密码
    private String board;//主板号
    private String serial;//硬件序列号
    private String androidVersion;//安卓版本
    private String androidsdk;//安卓SDK
    private String version;//当前软件版本
    private String hotline;//热线电话
    private String slogan;//标语口号
    private String create_time;//创建时间
    @Generated(hash = 744373783)
    public MachineInfo(Long id, String machineID, String userID, String userName,
                       String userPassword, String loginName, String loginPassword,
                       String board, String serial, String androidVersion, String androidsdk,
                       String version, String hotline, String slogan, String create_time) {
        this.id = id;
        this.machineID = machineID;
        this.userID = userID;
        this.userName = userName;
        this.userPassword = userPassword;
        this.loginName = loginName;
        this.loginPassword = loginPassword;
        this.board = board;
        this.serial = serial;
        this.androidVersion = androidVersion;
        this.androidsdk = androidsdk;
        this.version = version;
        this.hotline = hotline;
        this.slogan = slogan;
        this.create_time = create_time;
    }
    @Generated(hash = 1515055871)
    public MachineInfo() {
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
    public String getUserID() {
        return this.userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserPassword() {
        return this.userPassword;
    }
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
    public String getLoginName() {
        return this.loginName;
    }
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
    public String getLoginPassword() {
        return this.loginPassword;
    }
    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }
    public String getBoard() {
        return this.board;
    }
    public void setBoard(String board) {
        this.board = board;
    }
    public String getSerial() {
        return this.serial;
    }
    public void setSerial(String serial) {
        this.serial = serial;
    }
    public String getAndroidVersion() {
        return this.androidVersion;
    }
    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }
    public String getAndroidsdk() {
        return this.androidsdk;
    }
    public void setAndroidsdk(String androidsdk) {
        this.androidsdk = androidsdk;
    }
    public String getVersion() {
        return this.version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getHotline() {
        return this.hotline;
    }
    public void setHotline(String hotline) {
        this.hotline = hotline;
    }
    public String getSlogan() {
        return this.slogan;
    }
    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }
    public String getCreate_time() {
        return this.create_time;
    }
    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
