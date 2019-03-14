package com.njust.wanyuan_display.greenDao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by my187 on 2018/11/20.
 */

/**
 * 订单记录
 */
@Entity
public class OrderRecord {
    @Id
    private Long id;

    private String sysorderNo;//系统订单号

    private String payType;//支付类型，0=测试，1=支付宝，2=微信

    private String goodID;//商品id（唯一标识）

    private String goodName;//商品名称

    private String goodPrice;//商品价格

    private String goodQuantity;//商品数量(为支持多件出货的部分退款功能，约定：goodQuantity默认为1.即一个订单记录的商品个数约定为1)

    private String goodCounter;//货柜号1=左柜2=右柜

    private String channelNo;//货道号1-200

    private String status;//订单是否支付

    private String orderTime;//订单创建时间

    private String paymentTime;//支付时间

    @Generated(hash = 1902648300)
    public OrderRecord(Long id, String sysorderNo, String payType, String goodID,
                       String goodName, String goodPrice, String goodQuantity, String goodCounter,
                       String channelNo, String status, String orderTime, String paymentTime) {
        this.id = id;
        this.sysorderNo = sysorderNo;
        this.payType = payType;
        this.goodID = goodID;
        this.goodName = goodName;
        this.goodPrice = goodPrice;
        this.goodQuantity = goodQuantity;
        this.goodCounter = goodCounter;
        this.channelNo = channelNo;
        this.status = status;
        this.orderTime = orderTime;
        this.paymentTime = paymentTime;
    }

    @Generated(hash = 1055615028)
    public OrderRecord() {
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

    public String getPayType() {
        return this.payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getGoodID() {
        return this.goodID;
    }

    public void setGoodID(String goodID) {
        this.goodID = goodID;
    }

    public String getGoodName() {
        return this.goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public String getGoodPrice() {
        return this.goodPrice;
    }

    public void setGoodPrice(String goodPrice) {
        this.goodPrice = goodPrice;
    }

    public String getGoodQuantity() {
        return this.goodQuantity;
    }

    public void setGoodQuantity(String goodQuantity) {
        this.goodQuantity = goodQuantity;
    }

    public String getGoodCounter() {
        return this.goodCounter;
    }

    public void setGoodCounter(String goodCounter) {
        this.goodCounter = goodCounter;
    }

    public String getChannelNo() {
        return this.channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderTime() {
        return this.orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getPaymentTime() {
        return this.paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }
}
