package com.njust.wanyuan_display.greenDao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by my187 on 2018/11/20.
 */

/**
 * 货道信息
 */
@Entity
public class AisleInfo {
    @Id
    private Long id;

    private int positionID;//货道号1-200
    private int counter;//1=左柜（主柜），2=右柜（辅柜）
    private int state;//是否能用（0：正常；1：锁死不让购买；2：故障）
    private int motorType;//电机类型，1=单弹簧机，2=双弹簧机，3=窄履带机，4=宽履带机
    private byte position1;//行+列1
    private byte position2;//行+列2，如果没合并货道，则列1列2相同

    private String capacity;//货道总容量
    private String goodQuantity;//商品库存数量

    private int goodID;//商品id
    private String goodName;//商品名称
    private int goodTypeID;//商品类别id
    private String goodTypeName;//商品类别名
    private String goodDescription;//商品描述
    private double goodPrice;//商品价格
    private String promotion;//是否促销（1：是；0：否）
    private double promotionPrice;//促销价格
    private String goodsImgUrl;//商品图片下载地址
    private String goodsImgLocalUrl;//商品图片本地地址

    private String updateTime;

    public int count;//用于记录用户选择的数量
    private boolean goodColdHot;//有冷热区分
    private String temp;//用于商品显示冷热 0=常温（即不显示冷热标识） 1=冷 2=热

    public AisleInfo(int goodTypeID) {
        this.goodTypeID = goodTypeID;
    }
    
    //填充用
    public AisleInfo(AisleInfo aisleInfo) {
        this.positionID = aisleInfo.getPositionID();
        this.counter = aisleInfo.getCounter();
        this.state = aisleInfo.getState();
        this.motorType = aisleInfo.getMotorType();
        this.position1 = aisleInfo.getPosition1();
        this.position2 = aisleInfo.getPosition2();
        this.capacity = aisleInfo.getCapacity();
        this.goodQuantity = aisleInfo.getGoodQuantity();
        this.goodID = -1;
        this.goodName = aisleInfo.getGoodName();
        this.goodTypeID = aisleInfo.getGoodTypeID();
        this.goodTypeName = aisleInfo.getGoodTypeName();
        this.goodDescription = aisleInfo.getGoodDescription();
        this.goodPrice = aisleInfo.getGoodPrice();
        this.promotion = aisleInfo.getPromotion();
        this.promotionPrice = aisleInfo.getPromotionPrice();
        this.goodsImgUrl = aisleInfo.getGoodsImgUrl();
        this.goodsImgLocalUrl = aisleInfo.getGoodsImgLocalUrl();
        this.updateTime = aisleInfo.getUpdateTime();
        this.count = aisleInfo.getCount();
        this.goodColdHot = aisleInfo.getGoodColdHot();
        this.temp = aisleInfo.getTemp();
    }

    @Generated(hash = 627052122)
    public AisleInfo(Long id, int positionID, int counter, int state, int motorType,
            byte position1, byte position2, String capacity, String goodQuantity,
            int goodID, String goodName, int goodTypeID, String goodTypeName,
            String goodDescription, double goodPrice, String promotion,
            double promotionPrice, String goodsImgUrl, String goodsImgLocalUrl,
            String updateTime, int count, boolean goodColdHot, String temp) {
        this.id = id;
        this.positionID = positionID;
        this.counter = counter;
        this.state = state;
        this.motorType = motorType;
        this.position1 = position1;
        this.position2 = position2;
        this.capacity = capacity;
        this.goodQuantity = goodQuantity;
        this.goodID = goodID;
        this.goodName = goodName;
        this.goodTypeID = goodTypeID;
        this.goodTypeName = goodTypeName;
        this.goodDescription = goodDescription;
        this.goodPrice = goodPrice;
        this.promotion = promotion;
        this.promotionPrice = promotionPrice;
        this.goodsImgUrl = goodsImgUrl;
        this.goodsImgLocalUrl = goodsImgLocalUrl;
        this.updateTime = updateTime;
        this.count = count;
        this.goodColdHot = goodColdHot;
        this.temp = temp;
    }

    @Generated(hash = 1224431769)
    public AisleInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPositionID() {
        return this.positionID;
    }

    public void setPositionID(int positionID) {
        this.positionID = positionID;
    }

    public int getCounter() {
        return this.counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getMotorType() {
        return this.motorType;
    }

    public void setMotorType(int motorType) {
        this.motorType = motorType;
    }

    public byte getPosition1() {
        return this.position1;
    }

    public void setPosition1(byte position1) {
        this.position1 = position1;
    }

    public byte getPosition2() {
        return this.position2;
    }

    public void setPosition2(byte position2) {
        this.position2 = position2;
    }

    public String getCapacity() {
        return this.capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getGoodQuantity() {
        return this.goodQuantity;
    }

    public void setGoodQuantity(String goodQuantity) {
        this.goodQuantity = goodQuantity;
    }

    public int getGoodID() {
        return this.goodID;
    }

    public void setGoodID(int goodID) {
        this.goodID = goodID;
    }

    public String getGoodName() {
        return this.goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public int getGoodTypeID() {
        return this.goodTypeID;
    }

    public void setGoodTypeID(int goodTypeID) {
        this.goodTypeID = goodTypeID;
    }

    public String getGoodTypeName() {
        return this.goodTypeName;
    }

    public void setGoodTypeName(String goodTypeName) {
        this.goodTypeName = goodTypeName;
    }

    public String getGoodDescription() {
        return this.goodDescription;
    }

    public void setGoodDescription(String goodDescription) {
        this.goodDescription = goodDescription;
    }

    public double getGoodPrice() {
        return this.goodPrice;
    }

    public void setGoodPrice(double goodPrice) {
        this.goodPrice = goodPrice;
    }

    public String getPromotion() {
        return this.promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }

    public double getPromotionPrice() {
        return this.promotionPrice;
    }

    public void setPromotionPrice(double promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public String getGoodsImgUrl() {
        return this.goodsImgUrl;
    }

    public void setGoodsImgUrl(String goodsImgUrl) {
        this.goodsImgUrl = goodsImgUrl;
    }

    public String getGoodsImgLocalUrl() {
        return this.goodsImgLocalUrl;
    }

    public void setGoodsImgLocalUrl(String goodsImgLocalUrl) {
        this.goodsImgLocalUrl = goodsImgLocalUrl;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean getGoodColdHot() {
        return this.goodColdHot;
    }

    public void setGoodColdHot(boolean goodColdHot) {
        this.goodColdHot = goodColdHot;
    }

    public String getTemp() {
        return this.temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    

   
}
