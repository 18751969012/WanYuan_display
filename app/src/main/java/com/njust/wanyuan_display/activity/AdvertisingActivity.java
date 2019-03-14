package com.njust.wanyuan_display.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.adapter.AdvertisementViewAdapter;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.greenDao.Adv;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.util.DateUtil;
import com.njust.wanyuan_display.util.FileUtil;
import com.njust.wanyuan_display.util.ToastManager;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.helper.DataUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AdvertisingActivity extends BaseActivity implements View.OnClickListener {

    private final Logger log = Logger.getLogger(AdvertisingActivity.class);

    private static final int UPTATE_VIEWPAGER = 0;
    public ViewPager mViewPager;
    private ArrayList<Adv> list = new ArrayList<>();
    private AdvertisementViewAdapter mAdapter;
    private int autoCurrIndex = 0;//设置当前 第几个图片 被选中
    private Timer timer;
    private TimerTask timerTask;
    private long period = 5000;//轮播图展示时长,默认5秒
    private RelativeLayout click_buy;
    private TextView setting;
    final static int COUNTS = 5;// 点击次数
    final static long DURATION = 700;// 规定有效时间
    long[] mHits = new long[COUNTS];

    private AMapLocationClient locationClient = null;//高德定位sdk
    private AMapLocationClientOption locationOption = null;

    private LocationManager locationManager;
    private String provider;
    private Timer timer1;
    private TimerTask timerTask2;

    //定时轮播图片，需要在主线程里面修改 UI
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPTATE_VIEWPAGER:
                    if (msg.arg1 != 0) {
                        mViewPager.setCurrentItem(msg.arg1);
                    } else {
                        //false 当从末页调到首页时，不显示翻页动画效果，
                        mViewPager.setCurrentItem(msg.arg1, false);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertising);
        initView();
//        //初始化定位
//        initLocation();
        initLocation();
        //初始化广告列表
        initAdvData();
    }
    private void initView() {
        mViewPager = findViewById(R.id.viewPager);
        click_buy = findViewById(R.id.click_buy);
        click_buy.setOnClickListener(this);
        setting = findViewById(R.id.setting);
        setting.setOnClickListener(this);
    }

    @SuppressLint("MissingPermission")
    private void initLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 获取所有可用的位置提供器，如果GPS可以用就用GPS，GPS不能用则用网络
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            ToastManager.getInstance().showToast(getApplicationContext(),"没有可用的位置提供器", Toast.LENGTH_LONG);
            return;
        }
        log.info("位置提供器:" + provider);
        //每隔5秒或者每移动5米，locationListener中会更新一下位置信息
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            showLocation(location);
        }
        locationManager.requestLocationUpdates(provider, 5000, 5, locationListener);

    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
        @Override
        public void onLocationChanged(Location location) {
            // 更新当前设备的位置信息
            showLocation(location);
        }
    };
    //显示经纬度信息
    private void showLocation(final Location location) {
        String currentPosition = "latitude is " + location.getLatitude() + "\n" + "longitude is "
                + location.getLongitude();
        log.info(currentPosition);
        new BindingManager().setLocation(getApplicationContext(),location.getLongitude()+","+location.getLatitude());
        if (locationManager != null) {
            // 关闭程序时将监听器移除
            locationManager.removeUpdates(locationListener);
        }
    }

//    /**
//     * 初始化定位
//     */
//    private void initLocation(){
//        //初始化client
//        locationClient = new AMapLocationClient(this.getApplicationContext());
//        locationOption = getDefaultOption();
//        //设置定位参数
//        locationClient.setLocationOption(locationOption);
//        // 设置定位监听
//        locationClient.setLocationListener(locationListener);
//        // 启动定位
//        locationClient.startLocation();
//    }
//    /**
//     * 默认的定位参数
//     */
//    private AMapLocationClientOption getDefaultOption(){
//        AMapLocationClientOption mOption = new AMapLocationClientOption();
//        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
//        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
//        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
//        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
//        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
//        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
//        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
//        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
//        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
//        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
//        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
//        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
//        return mOption;
//    }
//    /**
//     * 定位监听
//     */
//    AMapLocationListener locationListener = new AMapLocationListener() {
//        @Override
//        public void onLocationChanged(AMapLocation location) {
//            if (null != location) {
//
//                StringBuffer sb = new StringBuffer();
//                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
//                if(location.getErrorCode() == 0){
//                    sb.append("定位成功" + "\n");
//                    sb.append("定位类型: " + location.getLocationType() + "\n");
//                    sb.append("经    度    : " + location.getLongitude() + "\n");
//                    sb.append("纬    度    : " + location.getLatitude() + "\n");
//                    sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
//                    sb.append("提供者    : " + location.getProvider() + "\n");
//
//                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
//                    sb.append("角    度    : " + location.getBearing() + "\n");
//                    // 获取当前提供定位服务的卫星个数
//                    sb.append("星    数    : " + location.getSatellites() + "\n");
//                    sb.append("国    家    : " + location.getCountry() + "\n");
//                    sb.append("省            : " + location.getProvince() + "\n");
//                    sb.append("市            : " + location.getCity() + "\n");
//                    sb.append("城市编码 : " + location.getCityCode() + "\n");
//                    sb.append("区            : " + location.getDistrict() + "\n");
//                    sb.append("区域 码   : " + location.getAdCode() + "\n");
//                    sb.append("地    址    : " + location.getAddress() + "\n");
//                    sb.append("兴趣点    : " + location.getPoiName() + "\n");
//                    //定位完成的时间
//                    sb.append("定位时间: " + DateUtil.getCurrentTime() + "\n");
//
//                    new BindingManager().setLocation(getApplicationContext(),location.getLongitude()+","+location.getLatitude());
//                    // 停止定位
//                    locationClient.stopLocation();
//
//                } else {
//                    //定位失败
//                    sb.append("定位失败" + "\n");
//                    sb.append("错误码:" + location.getErrorCode() + "\n");
//                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
//                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
//                }
//                sb.append("***定位质量报告***").append("\n");
//                sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启":"关闭").append("\n");
//                sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
//                sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
//                sb.append("* 网络类型：" + location.getLocationQualityReport().getNetworkType()).append("\n");
//                sb.append("* 网络耗时：" + location.getLocationQualityReport().getNetUseTime()).append("\n");
//                sb.append("****************").append("\n");
//                //定位之后的回调时间
//                sb.append("回调时间: " + DateUtil.getCurrentTime() + "\n");
//
//                //解析定位结果，
//                String result = sb.toString();
////                log.info(result);
////                log.info(sHA1(getApplicationContext()));
//            } else {
////                ToastManager.getInstance().showToast(getApplicationContext(),"定位失败，loc is null", Toast.LENGTH_LONG);
//            }
//        }
//    };
//    public static String sHA1(Context context) {
//        try {
//            PackageInfo info = context.getPackageManager().getPackageInfo(
//                    context.getPackageName(), PackageManager.GET_SIGNATURES);
//            byte[] cert = info.signatures[0].toByteArray();
//            MessageDigest md = MessageDigest.getInstance("SHA1");
//            byte[] publicKey = md.digest(cert);
//            StringBuffer hexString = new StringBuffer();
//            for (int i = 0; i < publicKey.length; i++) {
//                String appendString = Integer.toHexString(0xFF & publicKey[i])
//                        .toUpperCase(Locale.US);
//                if (appendString.length() == 1)
//                    hexString.append("0");
//                hexString.append(appendString);
//                hexString.append(":");
//            }
//            String result = hexString.toString();
//            return result.substring(0, result.length()-1);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//    /**
//     * 获取GPS状态的字符串
//     */
//    private String getGPSStatusString(int statusCode){
//        String str = "";
//        switch (statusCode){
//            case AMapLocationQualityReport.GPS_STATUS_OK:
//                str = "GPS状态正常";
//                break;
//            case AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER:
//                str = "手机中没有GPS Provider，无法进行GPS定位";
//                break;
//            case AMapLocationQualityReport.GPS_STATUS_OFF:
//                str = "GPS关闭，建议开启GPS，提高定位质量";
//                break;
//            case AMapLocationQualityReport.GPS_STATUS_MODE_SAVING:
//                str = "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量";
//                break;
//            case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
//                str = "没有GPS定位权限，建议开启gps定位权限";
//                break;
//        }
//        return str;
//    }
    /**
     * 广告轮播图测试数据
     */
    public void initAdvData() {
        //背景广告
        Adv background = new Adv();
        background.setLocalUrl(Resources.advURL()+"background.png");
        background.setDuration(5000);
        background.setAdType(1);//图片
        list.add(background);
//        Adv aaa2 = new Adv();
//        aaa2.setLocalUrl(Resources.advURL()+"7.mp4");
//        aaa2.setDuration(10000);
//        aaa2.setAdType(2);
//        list.add(aaa2);
        //服务器端添加的广告
        list.addAll(DBManager.getAdvsInfo(getApplicationContext()));
        //扫描本地文件夹的广告
        List<String> advlist = FileUtil.getAdvFiles(Resources.adv_localURL());
        if(advlist.size() > 0){
            for(int i = 0; i< advlist.size(); i++){
                if ( advlist.get(i).endsWith(".png") || advlist.get(i).endsWith(".jpg") || advlist.get(i).endsWith(".PNG") || advlist.get(i).endsWith(".JPG") ) {
                    Adv picture = new Adv();
                    picture.setLocalUrl(advlist.get(i));
                    picture.setDuration(5000);
                    picture.setAdType(1);//图片
                    list.add(picture);
                }else if(advlist.get(i).endsWith(".mp4") || advlist.get(i).endsWith(".MP4")){
                    Adv video = new Adv();
                    video.setLocalUrl(advlist.get(i));
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(advlist.get(i));
                    String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 播放时长单位为毫秒
                    video.setDuration(Long.parseLong(duration));
                    video.setAdType(2);
                    list.add(video);
                }
            }
        }
        period = list.get(0).getDuration();
        autoBanner();
    }

    private void autoBanner(){
        mAdapter = new AdvertisementViewAdapter(this,list);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(list.get(position).getAdType() == 2){
                    mAdapter.startVideo(position);
                }
                mAdapter.pauseVideo(position);
                autoCurrIndex = position;//动态设定轮播图每一页的停留时间
                period = list.get(position).getDuration();
                if (timer != null) {//每次改变都需要重新创建定时器
                    timer.cancel();
                    timer = null;
                    timer = new Timer();
                    if (timerTask != null) {
                        timerTask.cancel();
                        timerTask = null;
                        createTimerTask();
                    }
                    timer.schedule(timerTask, period, period);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        createTimerTask();//创建定时器

        timer = new Timer();
        timer.schedule(timerTask, period, period);
    }


    public void createTimerTask(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = UPTATE_VIEWPAGER;
                if (autoCurrIndex == list.size() - 1) {
                    autoCurrIndex = -1;
                }
                message.arg1 = autoCurrIndex + 1;
                mHandler.sendMessage(message);
            }
        };
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.click_buy:
                Intent intent = new Intent(AdvertisingActivity.this, ShoppingCartActivity.class);
                startActivity(intent);
                if (timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                this.finish();
                break;
            case R.id.setting:
                //每次点击时，数组向前移动一位
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                //为数组最后一位赋值
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
                    mHits = new long[COUNTS];//重新初始化数组
                    Intent intentLoginActivity = new Intent(AdvertisingActivity.this, LoginActivity.class);
                    startActivity(intentLoginActivity);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyLocation();
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    /**
     * 销毁定位
     */
    private void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            // 停止定位
            locationClient.stopLocation();
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
        if (locationManager != null) {
            // 关闭程序时将监听器移除
            locationManager.removeUpdates(locationListener);
        }

    }

}

