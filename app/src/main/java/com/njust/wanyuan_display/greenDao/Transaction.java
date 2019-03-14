package com.njust.wanyuan_display.greenDao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by my187 on 2018/11/20.
 */

/**
 * 出货交易记录
 */
@Entity
public class Transaction {
    @Id
    private Long id;

    private String sysorderNo;//系统订单号

    private String positionIDs;//出货货道号的集合，空格隔开

    private String beginTime;//出货开始时间

    private String endTime;//出货结束时间

    private String complete;//出货完成标志0=未完成，1=完成

    private String error;//本次出货是否故障，0=故障，1=成功

    private String payType;//支付类型，0=测试，1=支付宝，2=微信

    @Generated(hash = 1270015332)
    public Transaction(Long id, String sysorderNo, String positionIDs,
                       String beginTime, String endTime, String complete, String error,
                       String payType) {
        this.id = id;
        this.sysorderNo = sysorderNo;
        this.positionIDs = positionIDs;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.complete = complete;
        this.error = error;
        this.payType = payType;
    }

    @Generated(hash = 750986268)
    public Transaction() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSysorderNo() {
        return this.sysorderNo;
    }

    public void setSysorderNo(String sysorderNo) {
        this.sysorderNo = sysorderNo;
    }

    public String getPositionIDs() {
        return this.positionIDs;
    }

    public void setPositionIDs(String positionIDs) {
        this.positionIDs = positionIDs;
    }

    public String getBeginTime() {
        return this.beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getComplete() {
        return this.complete;
    }

    public void setComplete(String complete) {
        this.complete = complete;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", sysorderNo='" + sysorderNo + '\'' +
                ", positionIDs='" + positionIDs + '\'' +
                ", beginTime='" + beginTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", complete='" + complete + '\'' +
                ", error='" + error + '\'' +
                '}';
    }

    public String getPayType() {
        return this.payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }
}
