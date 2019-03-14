package com.njust.wanyuan_display.greenDao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by my187 on 2018/11/20.
 */

/**
 * 售卖商品信息
 */
@Entity
public class GoodsInfo {
    @Id
    private Long id;

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
    private boolean goodColdHot;//有冷热区分
    private String goodImgDownload;//0：下载成功，1：下载失败
    private String updateTime;
    @Generated(hash = 685521973)
    public GoodsInfo(Long id, int goodID, String goodName, int goodTypeID,
            String goodTypeName, String goodDescription, double goodPrice,
            String promotion, double promotionPrice, String goodsImgUrl,
            String goodsImgLocalUrl, boolean goodColdHot, String goodImgDownload,
            String updateTime) {
        this.id = id;
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
        this.goodColdHot = goodColdHot;
        this.goodImgDownload = goodImgDownload;
        this.updateTime = updateTime;
    }
    @Generated(hash = 1227172248)
    public GoodsInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public boolean getGoodColdHot() {
        return this.goodColdHot;
    }
    public void setGoodColdHot(boolean goodColdHot) {
        this.goodColdHot = goodColdHot;
    }
    public String getGoodImgDownload() {
        return this.goodImgDownload;
    }
    public void setGoodImgDownload(String goodImgDownload) {
        this.goodImgDownload = goodImgDownload;
    }
    public String getUpdateTime() {
        return this.updateTime;
    }
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

   


}
