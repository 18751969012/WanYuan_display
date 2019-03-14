package com.njust.wanyuan_display.greenDao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by my187 on 2018/11/20.
 */

/**
 * 广告
 */
@Entity
public class Adv {
    @Id
    private Long id;
    @NotNull
    @Unique
    private String adId;//广告ID

    private String url;//广告地址

    private String localUrl;//广告本地地址

    private int adType;//广告类型 1：图片2：视频

    private String playTime;//播放时间段 格式为：2018-08-07 07:00:00 - 2018-08-09 17:30:00

    private long duration;//播放时长

    private String download;//0：下载成功，1：下载失败

    private String buildTime;//建立时间

    @Generated(hash = 1113480378)
    public Adv(Long id, @NotNull String adId, String url, String localUrl,
            int adType, String playTime, long duration, String download,
            String buildTime) {
        this.id = id;
        this.adId = adId;
        this.url = url;
        this.localUrl = localUrl;
        this.adType = adType;
        this.playTime = playTime;
        this.duration = duration;
        this.download = download;
        this.buildTime = buildTime;
    }

    @Generated(hash = 209628668)
    public Adv() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdId() {
        return this.adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalUrl() {
        return this.localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public int getAdType() {
        return this.adType;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }

    public String getPlayTime() {
        return this.playTime;
    }

    public void setPlayTime(String playTime) {
        this.playTime = playTime;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getDownload() {
        return this.download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getBuildTime() {
        return this.buildTime;
    }

    public void setBuildTime(String buildTime) {
        this.buildTime = buildTime;
    }

   


}
