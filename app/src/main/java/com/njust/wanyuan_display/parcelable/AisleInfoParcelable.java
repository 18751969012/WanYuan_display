package com.njust.wanyuan_display.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

public class AisleInfoParcelable implements Parcelable {

    private int positionID;//货道号1-200
    private int counter;//1=左柜（主柜），2=右柜（辅柜）
    private String capacity;//货道总容量
    private String goodQuantity;//商品库存数量
    private int goodID;//商品id
    private String goodName;//商品名称
    private double goodPrice;//商品价格
    private int count;//用于记录用户选择的数量
    private String goodsImgLocalUrl;//商品图片本地地址

    public AisleInfoParcelable(int positionID, int counter, String capacity, String goodQuantity, int goodID, String goodName, double goodPrice, int count, String goodsImgLocalUrl) {
        this.positionID = positionID;
        this.counter = counter;
        this.capacity = capacity;
        this.goodQuantity = goodQuantity;
        this.goodID = goodID;
        this.goodName = goodName;
        this.goodPrice = goodPrice;
        this.count = count;
        this.goodsImgLocalUrl = goodsImgLocalUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(positionID);
        dest.writeInt(counter);
        dest.writeString(capacity);
        dest.writeString(goodQuantity);
        dest.writeInt(goodID);
        dest.writeString(goodName);
        dest.writeDouble(goodPrice);
        dest.writeInt(count);
        dest.writeString(goodsImgLocalUrl);
    }

    public static final Creator<AisleInfoParcelable> CREATOR = new Creator<AisleInfoParcelable>() {
        @Override
        public AisleInfoParcelable createFromParcel(Parcel in) {
            return new AisleInfoParcelable(in);
        }

        @Override
        public AisleInfoParcelable[] newArray(int size) {
            return new AisleInfoParcelable[size];
        }
    };

    private AisleInfoParcelable(Parcel in) {
        positionID = in.readInt();
        counter = in.readInt();
        capacity = in.readString();
        goodQuantity = in.readString();
        goodID = in.readInt();
        goodName = in.readString();
        goodPrice = in.readDouble();
        count = in.readInt();
        goodsImgLocalUrl = in.readString();
    }

    public int getPositionID() {
        return positionID;
    }

    public void setPositionID(int positionID) {
        this.positionID = positionID;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getGoodQuantity() {
        return goodQuantity;
    }

    public void setGoodQuantity(String goodQuantity) {
        this.goodQuantity = goodQuantity;
    }

    public int getGoodID() {
        return goodID;
    }

    public void setGoodID(int goodID) {
        this.goodID = goodID;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public double getGoodPrice() {
        return goodPrice;
    }

    public void setGoodPrice(double goodPrice) {
        this.goodPrice = goodPrice;
    }

    public static Creator<AisleInfoParcelable> getCREATOR() {
        return CREATOR;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getGoodsImgLocalUrl() {
        return goodsImgLocalUrl;
    }

    public void setGoodsImgLocalUrl(String goodsImgLocalUrl) {
        this.goodsImgLocalUrl = goodsImgLocalUrl;
    }
}
