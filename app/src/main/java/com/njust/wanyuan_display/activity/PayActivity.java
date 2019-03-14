package com.njust.wanyuan_display.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.adapter.PayAdapter;
import com.njust.wanyuan_display.adapter.PayDialog;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.greenDao.AisleInfo;
import com.njust.wanyuan_display.greenDao.Transaction;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.parcelable.AisleInfoParcelable;
import com.njust.wanyuan_display.service.HandlerService;
import com.njust.wanyuan_display.util.DateUtil;
import com.njust.wanyuan_display.util.QRcodeUtil;
import com.njust.wanyuan_display.util.SysorderNoUtil;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class PayActivity extends BaseDispatchTouchActivity implements View.OnClickListener {

    private static final Logger log = Logger.getLogger(PayActivity.class);

    private Context mContext;
    private TextView returnBack,countDown,cost,count;
    private TextView weixin_pay,alipay_pay;
    private RelativeLayout header;
    private LinearLayout statisticalState;
    private ImageView qrCodeImage,loadingImage;
    private String currentImg;
    private RecyclerView payDetail;
    private PayAdapter payAdapter;
    public DetailReceiver mDetailReceiver;
    private String weixin_sysorderNo = "";//用于生成每种支付方式最多生成一个订单号，即使点击速度过快，也不会产生新的订单号用于请求二维码
    private String alipay_sysorderNo = "";
    private String weixin_QRcodefilePath = "";//用于生成二维码之后记录二维码本地地址，每次支付页面最多每种支付方式生成一个二维码，防止每次点击都生成新的
    private String alipay_QRcodefilePath = "";

    private boolean openGetGoodsDoor = false;//成功开启取货门
    private String errorAisle_needRefund = "";
    private int hasGoods = 0;//根据购买商品值定义商品分布位置：3：左右柜都购买了商品，1：只买了左柜的商品，2：只买了右柜的商品
    private boolean oneHasBreakDown = false;//用于判断两个货柜相继发生停单机故障的时候

    private PayDialog payDialog;
    private Timer mTimer = new Timer();
    private TimerTask mTimerTask;

    private SparseArray<AisleInfoParcelable> selectedListParelable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        mContext = getApplicationContext();
        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        assert bundle != null;
        selectedListParelable = bundle.getSparseParcelableArray("selectedListParelable");
        initView();
        registerDetailReceiver();
        initData();
        //播放声音
        Intent handlerIntent = new Intent(mContext, HandlerService.class);
        handlerIntent.setAction(Resources.play_voice);
        handlerIntent.putExtra("voiceId", String.valueOf(R.raw.powoa));
        mContext.startService(handlerIntent);
    }

    private void initView() {
        returnBack = findViewById(R.id.returnBack);
        returnBack.setOnClickListener(this);
        countDown = findViewById(R.id.countDown);

        header = findViewById(R.id.header);
        statisticalState = findViewById(R.id.statisticalState);
        cost = findViewById(R.id.cost);
        count = findViewById(R.id.count);

        weixin_pay = findViewById(R.id.weixin_pay);
        weixin_pay.setOnClickListener(this);
        alipay_pay = findViewById(R.id.alipay_pay);
        alipay_pay.setOnClickListener(this);
        qrCodeImage = findViewById(R.id.qrCodeImage);
        loadingImage = findViewById(R.id.loadingImage);

        payDetail = findViewById(R.id.payDetailRecyclerView);
        payDetail.setLayoutManager(new LinearLayoutManager(this));
        payAdapter = new PayAdapter(this,selectedListParelable);
        payDetail.setAdapter(payAdapter);

    }

    private void initData(){
        String totalPrices = this.getIntent().getStringExtra("totalPrices");
        String totalQuantity = this.getIntent().getStringExtra("totalQuantity");
        cost.setText(totalPrices);
        count.setText("共（"+totalQuantity+"）件商品");

        setAction(false);//通过设置标志位取消倒计时的点击重新倒计时的功能
        weixin_sysorderNo = "";
        alipay_sysorderNo = "";
        weixin_QRcodefilePath = "";
        alipay_QRcodefilePath = "";

        openGetGoodsDoor = false;//成功开启取货门
        errorAisle_needRefund = "";
        hasGoods = 0;
        oneHasBreakDown = false;
        for(int i = 0; i < selectedListParelable.size(); i++){
            AisleInfoParcelable goodsItems = selectedListParelable.valueAt(i);
            if(goodsItems.getPositionID() > 0 && goodsItems.getPositionID() <= 100){//判断hasGoods取值
                switch (hasGoods) {
                    case 1:
                        break;
                    case 2:
                        hasGoods = 3;
                        break;
                    case 3:
                        break;
                    default:
                        hasGoods = 1;
                        break;
                }
            }else{
                switch (hasGoods) {
                    case 1:
                        hasGoods = 3;
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    default:
                        hasGoods = 2;
                        break;
                }
            }
        }






        header.setBackground(this.getResources().getDrawable(R.drawable.underpainting_blue_reverse));
        statisticalState.setBackground(this.getResources().getDrawable(R.drawable.underpainting_blue_reverse));
        currentImg = "alipay_pay";
        //把二维码变成等待状态，然后开启微信二维码的请求
        JSONArray goodsInfos = new JSONArray();
        for(int i = 0; i < selectedListParelable.size(); i++){
            AisleInfoParcelable goodsItems = selectedListParelable.valueAt(i);
            for(int j = 0; j < goodsItems.getCount(); j++){
                JSONObject goodsInfo = new JSONObject();
                goodsInfo.put("goodID", goodsItems.getGoodID());
                goodsInfo.put("goodName", goodsItems.getGoodName());
                goodsInfo.put("goodPrice", goodsItems.getGoodPrice());
                goodsInfo.put("goodQuantity", "1");
                goodsInfo.put("counterNo", goodsItems.getCounter());
                goodsInfo.put("channelNo", goodsItems.getPositionID());
                goodsInfos.add(goodsInfo);
            }
        }
        SysorderNoUtil sysorderNoUtil = new SysorderNoUtil();
        String sysorderNo = sysorderNoUtil.getSysorderNo(new BindingManager().getMachineID(getApplicationContext()));
        alipay_sysorderNo = sysorderNo;
        Intent handlerIntent = new Intent(mContext, HandlerService.class);
        handlerIntent.setAction(Resources.alipay_qrcode);
        handlerIntent.putExtra("sysorderNo", sysorderNo);
        handlerIntent.putExtra("goodsInfos", goodsInfos.toJSONString());
        mContext.startService(handlerIntent);
        Glide.with(mContext).load(R.drawable.loading).into(loadingImage);
        loadingImage.setVisibility(View.VISIBLE);
    }

    /**
     * 定时器相关
     */
    @Override
    public void onTickTime(long millisUntilFinished) {
        countDown.setText("剩余"+String.valueOf((int)millisUntilFinished/1000)+"S");
    }
    @Override
    public void reClock() {
        super.reClock();
    }
    @Override
    public void setAction(boolean action) {
        super.setAction(action);
    }
    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.returnBack:
                this.finish();
                break;
            case R.id.weixin_pay:
                if(!currentImg.equals("weixin_pay")){
                    header.setBackground(this.getResources().getDrawable(R.drawable.underpainting_green));
                    statisticalState.setBackground(this.getResources().getDrawable(R.drawable.underpainting_green));
                    currentImg = "weixin_pay";
                    if(!weixin_QRcodefilePath.equals("")){
                        try {
                            qrCodeImage.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(weixin_QRcodefilePath)));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }else{
                        //把二维码变成等待状态，然后开启微信二维码的请求
                        JSONArray goodsInfos = new JSONArray();
                        for(int i = 0; i < selectedListParelable.size(); i++){
                            AisleInfoParcelable goodsItems = selectedListParelable.valueAt(i);
                            for(int j = 0; j < goodsItems.getCount(); j++){
                                JSONObject goodsInfo = new JSONObject();
                                goodsInfo.put("goodID", goodsItems.getGoodID());
                                goodsInfo.put("goodName", goodsItems.getGoodName());
                                goodsInfo.put("goodPrice", goodsItems.getGoodPrice());
                                goodsInfo.put("goodQuantity", "1");
                                goodsInfo.put("counterNo", goodsItems.getCounter());
                                goodsInfo.put("channelNo", goodsItems.getPositionID());
                                goodsInfos.add(goodsInfo);
                            }
                        }
                        Intent handlerIntent = new Intent(mContext, HandlerService.class);
                        handlerIntent.setAction(Resources.weixin_qrcode);
                        handlerIntent.putExtra("goodsInfos", goodsInfos.toJSONString());

                        if(!weixin_sysorderNo.equals("")){
                            handlerIntent.putExtra("sysorderNo", weixin_sysorderNo);
                        }else{
                            SysorderNoUtil sysorderNoUtil = new SysorderNoUtil();
                            String sysorderNo = sysorderNoUtil.getSysorderNo(new BindingManager().getMachineID(getApplicationContext()));
                            weixin_sysorderNo = sysorderNo;
                            handlerIntent.putExtra("sysorderNo", sysorderNo);
                        }

                        mContext.startService(handlerIntent);
                        Glide.with(mContext).load(R.drawable.loading).into(loadingImage);
                        loadingImage.setVisibility(View.VISIBLE);
                        reClock();
                    }
                }
                break;
            case R.id.alipay_pay:
                if(!currentImg.equals("alipay_pay")){
                    header.setBackground(this.getResources().getDrawable(R.drawable.underpainting_blue_reverse));
                    statisticalState.setBackground(this.getResources().getDrawable(R.drawable.underpainting_blue_reverse));
                    currentImg = "alipay_pay";
                    if(!alipay_QRcodefilePath.equals("")){//本地还未生成二维码
                        try {
                            qrCodeImage.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(alipay_QRcodefilePath)));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }else{
                        //把二维码变成等待状态，然后开启支付宝二维码的请求
                        JSONArray goodsInfos = new JSONArray();
                        for(int i = 0; i < selectedListParelable.size(); i++){
                            AisleInfoParcelable goodsItems = selectedListParelable.valueAt(i);
                            for(int j = 0; j < goodsItems.getCount(); j++){
                                JSONObject goodsInfo = new JSONObject();
                                goodsInfo.put("goodID", goodsItems.getGoodID());
                                goodsInfo.put("goodName", goodsItems.getGoodName());
                                goodsInfo.put("goodPrice", goodsItems.getGoodPrice());
                                goodsInfo.put("goodQuantity", "1");
                                goodsInfo.put("counterNo", goodsItems.getCounter());
                                goodsInfo.put("channelNo", goodsItems.getPositionID());
                                goodsInfos.add(goodsInfo);
                            }
                        }
                        Intent handlerIntent = new Intent(mContext, HandlerService.class);
                        handlerIntent.setAction(Resources.alipay_qrcode);
                        handlerIntent.putExtra("goodsInfos", goodsInfos.toJSONString());

                        if(!alipay_sysorderNo.equals("")){
                            handlerIntent.putExtra("sysorderNo", alipay_sysorderNo);
                        }else{
                            SysorderNoUtil sysorderNoUtil = new SysorderNoUtil();
                            String sysorderNo = sysorderNoUtil.getSysorderNo(new BindingManager().getMachineID(getApplicationContext()));
                            alipay_sysorderNo = sysorderNo;
                            handlerIntent.putExtra("sysorderNo", sysorderNo);
                        }

                        mContext.startService(handlerIntent);
                        Glide.with(mContext).load(R.drawable.loading).into(loadingImage);
                        loadingImage.setVisibility(View.VISIBLE);
                        reClock();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 动态注册广播
     */
    public void registerDetailReceiver() {
        try {
            if ( mDetailReceiver==null ) {
                mDetailReceiver = new DetailReceiver();
            }

            IntentFilter filter = new IntentFilter();
            filter.addAction(Resources.out_goods_start);
            filter.addAction(Resources.out_goods_end);
            filter.addAction(Resources.alipay_qrcode_show);
            filter.addAction(Resources.weixin_qrcode_show);
            mContext.registerReceiver(mDetailReceiver, filter);

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }


    public class DetailReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context mContext, Intent intent) {
            try {
                log.info( intent.getAction()+"-----action" );
                if (Objects.equals(intent.getAction(), Resources.out_goods_start)) {
                    String sysorderNo = intent.getStringExtra("sysorderNo");
                    showDialog("out_goods_start");
                }else if (Objects.equals(intent.getAction(), Resources.out_goods_end)) {
                    String sysorderNo = intent.getStringExtra("sysorderNo");
                    String outgoods_status = intent.getStringExtra("outgoods_status");
                    switch (outgoods_status) {
                        case "success":
                            openGetGoodsDoor = true;
                            if (intent.getStringExtra("error_type").equals("justRecord&closeAisle")) {
                                String error_ID_justRecord = intent.getStringExtra("error_ID_justRecord");
                                String error_ID_closeAisle = intent.getStringExtra("error_ID_closeAisle");
                                if (!error_ID_justRecord.equals("")) {
                                    log.info("只做记录的故障：" + error_ID_justRecord);
                                }
                                if (!error_ID_closeAisle.equals("")) {
                                    log.info("货道故障：" + error_ID_closeAisle);
                                    String aisles = "";
                                    String[] str = error_ID_closeAisle.split(";");
                                    for (String aStr : str) {
                                        String[] aisle = aStr.split(":");
                                        aisles = aisles + aisle[0] + ",";
                                        if (aisle[1].contains("9") || aisle[1].contains("10")) {
                                            errorAisle_needRefund = errorAisle_needRefund + aisle[0] + ",";
                                        }
                                    }
                                    DBManager.closeAisle(mContext, aisles);
                                }
                            }
                            Transaction transaction = DBManager.getDelivery(getApplicationContext(), sysorderNo);//出货结果上报服务器

                            String[] str = transaction != null ? transaction.getPositionIDs().split(",") : new String[0];
                            JSONArray succesResult = new JSONArray();
                            JSONArray errorResult = new JSONArray();
                            JSONArray succesAisle = new JSONArray();
                            JSONArray errorAisle = new JSONArray();
                            String[] errorAisle_refund = errorAisle_needRefund.split(",");
                            boolean errorAisle_refund_contains = false;
                            for (String aStr : str) {
                                AisleInfo aisleInfo = DBManager.getAisleInfo(getApplicationContext(), Integer.parseInt(aStr) <= 100 ? 1 : 2, Integer.parseInt(aStr));
                                JSONObject goods = new JSONObject();
                                goods.put("goodId", aisleInfo != null ? String.valueOf(aisleInfo.getGoodID()) : "");
                                JSONObject aisle = new JSONObject();
                                aisle.put("positionID", aStr);
                                errorAisle_refund_contains = false;
                                for (String mStr : errorAisle_refund) {
                                    if(mStr.equals(aStr)){
                                        errorResult.add(goods);
                                        errorAisle_refund_contains = true;
                                        break;
                                    }
                                }
                                if (!errorAisle_refund_contains) {
                                    succesResult.add(goods);
                                    succesAisle.add(aisle);
                                }
                            }
                            startHandlerServiceToSubmitResult(succesResult,errorResult,succesAisle,errorAisle);
                            showDialog("out_goods_complete");
                            break;
                        case "closeMidDoorSuccess":
                            showDialog("closeMidDoorSuccess");
                            break;
                        case "fail":
                            String error_type = intent.getStringExtra("error_type");
                            if (error_type.equals("stopAllCounter")) {
                                String error_ID = intent.getStringExtra("error_ID");
                                log.info("停整机故障：" + error_ID);//日志记录
                                showDialog("out_goods_fail");//更新弹窗
                                DBManager.updateMachineState(mContext, 2, 2, 2);//更新数据库机器状态
                                if (!openGetGoodsDoor) {
                                    Transaction transaction1 = DBManager.getDelivery(getApplicationContext(), sysorderNo);//出货结果上报服务器
                                    String[] str1 = transaction1 != null ? transaction1.getPositionIDs().split(",") : new String[0];
                                    JSONArray succesResult1 = new JSONArray();
                                    JSONArray errorResult1 = new JSONArray();
                                    for (String aStr : str1) {
                                        AisleInfo aisleInfo = DBManager.getAisleInfo(getApplicationContext(), Integer.parseInt(aStr) <= 100 ? 1 : 2, Integer.parseInt(aStr));
                                        JSONObject goods = new JSONObject();
                                        goods.put("goodId", aisleInfo != null ? String.valueOf(aisleInfo.getGoodID()) : "");
                                        errorResult1.add(goods);
                                    }
                                    startHandlerServiceToSubmitResult(succesResult1,errorResult1,new JSONArray(),new JSONArray());
                                }
                            } else if (error_type.equals("stopOneCounter")) {
                                String error_ID = intent.getStringExtra("error_ID");
                                log.info("停单柜故障：" + error_ID);//日志记录
                                String error_counter = intent.getStringExtra("error_counter");
                                if (error_counter.equals("left") || error_counter.equals("right")) {
                                    if (hasGoods == 3) {
                                        if (oneHasBreakDown) {
                                            showDialog("out_goods_fail");//更新弹窗
                                            DBManager.updateMachineState(mContext, 2, 2, 2);//更新数据库机器状态
                                            Transaction transaction2 = DBManager.getDelivery(getApplicationContext(), sysorderNo);//出货结果上报服务器
                                            String[] str2 = transaction2 != null ? transaction2.getPositionIDs().split(",") : new String[0];
                                            JSONArray succesResult2 = new JSONArray();
                                            JSONArray errorResult2 = new JSONArray();
                                            for (String aStr : str2) {
                                                AisleInfo aisleInfo = DBManager.getAisleInfo(getApplicationContext(), Integer.parseInt(aStr) <= 100 ? 1 : 2, Integer.parseInt(aStr));
                                                JSONObject goods = new JSONObject();
                                                goods.put("goodId", aisleInfo != null ? String.valueOf(aisleInfo.getGoodID()) : "");
                                                errorResult2.add(goods);
                                            }
                                            startHandlerServiceToSubmitResult(succesResult2,errorResult2,new JSONArray(),new JSONArray());
                                        } else {
                                            if (error_counter.equals("left")) {
                                                oneHasBreakDown = true;
                                                DBManager.updateMachineState(mContext, 0, 2, 0);//更新数据库机器状态
                                                Transaction transaction3 = DBManager.getDelivery(getApplicationContext(), sysorderNo);//出货结果上报服务器
                                                String[] str3 = transaction3 != null ? transaction3.getPositionIDs().split(",") : new String[0];
                                                for (String aStr : str3) {
                                                    if (Integer.parseInt(aStr) <= 100) {
                                                        errorAisle_needRefund = errorAisle_needRefund + aStr + ",";
                                                    }
                                                }
                                            } else {
                                                oneHasBreakDown = true;
                                                DBManager.updateMachineState(mContext, 0, 0, 2);//更新数据库机器状态
                                                Transaction transaction4 = DBManager.getDelivery(getApplicationContext(), sysorderNo);//出货结果上报服务器
                                                String[] str4 = transaction4 != null ? transaction4.getPositionIDs().split(",") : new String[0];
                                                for (String aStr : str4) {
                                                    if (Integer.parseInt(aStr) > 100) {
                                                        errorAisle_needRefund = errorAisle_needRefund + aStr + ",";
                                                    }
                                                }
                                            }
                                        }
                                    } else if (hasGoods == 1) {//只买了左柜
                                        if (error_counter.equals("left")) {
                                            showDialog("out_goods_fail");//更新弹窗
                                            DBManager.updateMachineState(mContext, 0, 2, 0);//更新数据库机器状态
                                            Transaction transaction5 = DBManager.getDelivery(getApplicationContext(), sysorderNo);//出货结果上报服务器
                                            String[] str5 = transaction5 != null ? transaction5.getPositionIDs().split(",") : new String[0];
                                            JSONArray succesResult5 = new JSONArray();
                                            JSONArray errorResult5 = new JSONArray();
                                            for (String aStr : str5) {
                                                AisleInfo aisleInfo = DBManager.getAisleInfo(getApplicationContext(), Integer.parseInt(aStr) <= 100 ? 1 : 2, Integer.parseInt(aStr));
                                                JSONObject goods = new JSONObject();
                                                goods.put("goodId", aisleInfo != null ? String.valueOf(aisleInfo.getGoodID()) : "");
                                                errorResult5.add(goods);
                                            }
                                            startHandlerServiceToSubmitResult(succesResult5,errorResult5,new JSONArray(),new JSONArray());
                                        }
                                    } else if (hasGoods == 2) {//只买了右柜
                                        if (error_counter.equals("right")) {
                                            showDialog("out_goods_fail");//更新弹窗
                                            DBManager.updateMachineState(mContext, 0, 0, 2);//更新数据库机器状态
                                            Transaction transaction6 = DBManager.getDelivery(getApplicationContext(), sysorderNo);//出货结果上报服务器
                                            String[] str6 = transaction6 != null ? transaction6.getPositionIDs().split(",") : new String[0];
                                            JSONArray succesResult6 = new JSONArray();
                                            JSONArray errorResult6 = new JSONArray();
                                            for (String aStr : str6) {
                                                AisleInfo aisleInfo = DBManager.getAisleInfo(getApplicationContext(), Integer.parseInt(aStr) <= 100 ? 1 : 2, Integer.parseInt(aStr));
                                                JSONObject goods = new JSONObject();
                                                goods.put("goodId", aisleInfo != null ? String.valueOf(aisleInfo.getGoodID()) : "");
                                                errorResult6.add(goods);
                                            }
                                            startHandlerServiceToSubmitResult(succesResult6,errorResult6,new JSONArray(),new JSONArray());
                                        }
                                    }
                                } else {//error_counter.equals("all")通信故障才会有这种情况
                                    showDialog("out_goods_fail");//更新弹窗
                                    DBManager.updateMachineState(mContext, 2, 2, 2);//更新数据库机器状态
                                    if (!openGetGoodsDoor) {
                                        Transaction transaction7 = DBManager.getDelivery(getApplicationContext(), sysorderNo);//出货结果上报服务器
                                        String[] str7 = transaction7 != null ? transaction7.getPositionIDs().split(",") : new String[0];
                                        JSONArray succesResult7 = new JSONArray();
                                        JSONArray errorResult7 = new JSONArray();
                                        for (String aStr : str7) {
                                            AisleInfo aisleInfo = DBManager.getAisleInfo(getApplicationContext(), Integer.parseInt(aStr) <= 100 ? 1 : 2, Integer.parseInt(aStr));
                                            JSONObject goods = new JSONObject();
                                            goods.put("goodId", aisleInfo != null ? String.valueOf(aisleInfo.getGoodID()) : "");
                                            errorResult7.add(goods);
                                        }
                                        startHandlerServiceToSubmitResult(succesResult7,errorResult7,new JSONArray(),new JSONArray());
                                    }
                                }
                            }
                            break;
                    }

                } else if (Objects.equals(intent.getAction(), Resources.alipay_qrcode_show)) {
                    String qrcode = intent.getStringExtra("qrcode");
                    String sysorderNo = intent.getStringExtra("sysorderNo");
                    log.info("alipay pay finish:" + qrcode);
                    Log.w("happy","alipay_qrcode:"+qrcode);
                    String QRcodefilePath = Resources.QRcodeURL() + sysorderNo +".jpeg";
                    boolean success = QRcodeUtil.createQRImage(qrcode,450,450, BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pay_zhifubao),QRcodefilePath);
                    if(success){
                        alipay_QRcodefilePath = QRcodefilePath;
                        if(currentImg.equals("alipay_pay")){
                            qrCodeImage.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(QRcodefilePath)));
                            loadingImage.setVisibility(View.GONE);
                            Glide.clear(loadingImage);
                        }
                    }
                } else if (Objects.equals(intent.getAction(), Resources.weixin_qrcode_show)) {
                    String qrcode = intent.getStringExtra("qrcode");
                    String sysorderNo = intent.getStringExtra("sysorderNo");
                    log.info("weixin pay finish:" + qrcode);
                    Log.w("happy","weixin_qrcode:"+qrcode);
                    String QRcodefilePath = Resources.QRcodeURL() +  sysorderNo +".jpeg";
                    boolean success = QRcodeUtil.createQRImage(qrcode,450,450, BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pay_weixin),QRcodefilePath);
                    if(success){
                        weixin_QRcodefilePath = QRcodefilePath;
                        if(currentImg.equals("weixin_pay")){
                            qrCodeImage.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(QRcodefilePath)));
                            loadingImage.setVisibility(View.GONE);
                            Glide.clear(loadingImage);
                        }
                    }
                }

            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }
    private void showDialog(String state) {
        switch (state){
            case "out_goods_start":{
                payDialog = new PayDialog(this);
                payDialog.setTitle("提示");
                payDialog.setMessage("正在出货，请等待出货完毕");
                payDialog.show();
                onPause();
                payDialog.setConfirmOnclickListener("确定", new PayDialog.confirmOnclickListener() {
                    @Override
                    public void onConfirmClick() {
                        payDialog.dismiss();
                    }
                });
                break;
            }
            case "out_goods_complete":{
                if(payDialog.isShowing()){
                    payDialog.dismiss();
                }
                payDialog = new PayDialog(this);
                payDialog.setTitle("提示");
                payDialog.setMessage("出货完毕，请尽快取走商品。"+"\n"+"勿阻挡取货门关闭，防止夹伤");
                payDialog.show();
                break;
            }
            case "closeMidDoorSuccess":{
                if(payDialog.isShowing()){
                    payDialog.dismiss();
                }
                payDialog = new PayDialog(this);
                payDialog.setTitle("提示");
                payDialog.setMessage("本次交易完毕");
                payDialog.show();
                timeStart(true);
                break;
            }
            case "out_goods_fail":{
                if(payDialog.isShowing()){
                    payDialog.dismiss();
                }
                payDialog = new PayDialog(this);
                payDialog.setTitle("提示");
                payDialog.setMessage("出货失败");
                payDialog.show();
                timeStart(false);
                break;
            }
        }
    }
//    private void showDialog() {
//        final PayDialog payDialog = new PayDialog(this);
//        payDialog.setTitle("提示");
//        payDialog.setMessage("正在出货，请等待出货完毕");
//        payDialog.show();
//
//        payDialog.setConfirmOnclickListener("确定", new PayDialog.confirmOnclickListener() {
//            @Override
//            public void onConfirmClick() {
//                ToastManager.getInstance().showToast(getApplicationContext(),"出货完成", Toast.LENGTH_LONG);
//                payDialog.dismiss();
//
//                Transaction transaction = DBManager.getLastTransaction(getApplicationContext());
//                String[] str = transaction.getPositionIDs().split(",");
//                JSONArray succesResult = new JSONArray();
//                JSONArray errorResult = new JSONArray();
//                JSONArray succesAisle = new JSONArray();
//                JSONArray errorAisle = new JSONArray();
//                for(int i = 0; i < str.length; i++){
//                    AisleInfo aisleInfo = DBManager.getAisleInfo(getApplicationContext(),Integer.parseInt(str[i])<= 100?1:2, Integer.parseInt(str[i]));
//                    JSONObject goods = new JSONObject();
//                    goods.put("goodId", String.valueOf(aisleInfo.getGoodID()));
//                    succesResult.add(goods);
//                    JSONObject aisle = new JSONObject();
//                    aisle.put("positionID", str[i]);
//                    succesAisle.add(aisle);
//                }
//                Intent handlerIntent = new Intent(mContext, HandlerService.class);
//                handlerIntent.setAction(Resources.out_goods_result_submit);
//                handlerIntent.putExtra("succesResult",succesResult.toJSONString());
//                handlerIntent.putExtra("errorResult",errorResult.toJSONString());
//                handlerIntent.putExtra("succesAisle",succesAisle.toJSONString());
//                handlerIntent.putExtra("errorAisle",errorAisle.toJSONString());
//                handlerIntent.putExtra("time", DateUtil.getCurrentTime());
//                mContext.startService(handlerIntent);
//            }
//        });
//    }
    private void timeStart(boolean success){
        if(mTimerTask != null){
            mTimerTask.cancel();
            mTimerTask = null;
        }
        final int[] t = {0};//总是被系统kill，延时放短
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                t[0]++;
                if(t[0] == 10){
                    if(payDialog.isShowing()){
                        payDialog.dismiss();
                    }
                    closeCurrentActivity();
                }
            }
        };
        mTimer.schedule(mTimerTask,200,200);
        if(success){
            log.info("本次交易完毕，"+10*200+"ms后回到广告页面");
        }else{
            log.info("本次交易失败，"+10*200+"ms后回到广告页面");
        }
    }

    private void startHandlerServiceToSubmitResult(JSONArray succesResult, JSONArray errorResult, JSONArray succesAisle, JSONArray errorAisle){
        Intent handlerIntent = new Intent(mContext, HandlerService.class);
        handlerIntent.setAction(Resources.out_goods_result_submit);
        handlerIntent.putExtra("succesResult", succesResult.toJSONString());
        handlerIntent.putExtra("errorResult", errorResult.toJSONString());
        handlerIntent.putExtra("succesAisle", succesAisle.toJSONString());//用来更新数据库的库存
        handlerIntent.putExtra("errorAisle", errorAisle.toJSONString());
        handlerIntent.putExtra("time", DateUtil.getCurrentTime());
        mContext.startService(handlerIntent);
    }


    @Override
    public void onDestroy() {
        log.info( "PayActivity destory" );
        if ( mDetailReceiver!=null ) {
            mContext.unregisterReceiver(mDetailReceiver);//注销广播
        }
        if(mTimerTask != null){
            mTimerTask.cancel();
            mTimerTask = null;
        }
        mTimer.cancel();
        HandlerService.weixin = false;//取消订单的查询
        HandlerService.alipay = false;
        super.onDestroy();
    }

    protected void closeCurrentActivity(){
       runOnUiThread(new Runnable() {
            public void run() {
                Intent intentAdvertisingActivity = new Intent(mContext, AdvertisingActivity.class);
                intentAdvertisingActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intentAdvertisingActivity);
            }
        });
    }
}
