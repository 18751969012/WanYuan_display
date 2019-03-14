package com.njust.wanyuan_display.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.activity.ManagerAdvanceSettingsActivity;
import com.njust.wanyuan_display.config.Constant;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.scm.TestAssistControl;
import com.njust.wanyuan_display.service.HandlerService;
import com.njust.wanyuan_display.thread.PressureTestThread;
import com.njust.wanyuan_display.util.DateUtil;
import com.njust.wanyuan_display.util.SysorderNoUtil;
import com.njust.wanyuan_display.util.ToastManager;


import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.util.Objects;

import static com.njust.wanyuan_display.application.VMApplication.OutGoodsThreadFlag;
import static com.njust.wanyuan_display.application.VMApplication.VMMainThreadFlag;
import static com.njust.wanyuan_display.application.VMApplication.VMMainThreadRunning;
import static com.njust.wanyuan_display.application.VMApplication.mQuery0Flag;
import static com.njust.wanyuan_display.application.VMApplication.mQuery1Flag;
import static com.njust.wanyuan_display.application.VMApplication.mQuery2Flag;
import static com.njust.wanyuan_display.application.VMApplication.mUpdataDatabaseFlag;

public class AdvancePressureTestFragment extends Fragment implements View.OnClickListener {

    private static final Logger log = Logger.getLogger(AdvancePressureTestFragment.class);
    private TextView rec,rec_fault;
    private ScrollView recScrollView,recFaultScrollView;
    private Button start_simulation_pressure_test,stop_simulation_pressure_test,immediately_stop,clear_show;
//    private TestAssistControl mTestAssistControl = new TestAssistControl();
    private PressureTestThread mPressureTestThread;
    private boolean run=false;//用于标识当前是否存在运动状态
    private boolean nextTimeStop = false;//用于标识当次出货完毕后停止自动下一次出货
    private boolean firstTime = true;//标识首次开始，非首次不用再次start线程
    public DetailReceiver mDetailReceiver;
    private String testRange = "both";
    private EditText testCount;
    private int test_count = 0;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //让碎片加载一个布局
        View view = inflater.inflate(R.layout.fragment_advance_simulation_pressure_test, container, false);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        rec = view.findViewById(R.id.rec);
        recScrollView = view.findViewById(R.id.recScrollView);
        rec_fault = view.findViewById(R.id.rec_fault);
        recFaultScrollView = view.findViewById(R.id.recFaultScrollView);
        testCount = view.findViewById(R.id.testCount);
        start_simulation_pressure_test = view.findViewById(R.id.start_simulation_pressure_test);
        start_simulation_pressure_test.setOnClickListener(this);
        stop_simulation_pressure_test = view.findViewById(R.id.stop_simulation_pressure_test);
        stop_simulation_pressure_test.setOnClickListener(this);
        immediately_stop = view.findViewById(R.id.immediately_stop);
        immediately_stop.setOnClickListener(this);
        clear_show = view.findViewById(R.id.clear_show);
        clear_show.setOnClickListener(this);

        RadioGroup rg = view.findViewById(R.id.rg);
        final RadioButton only_left = view.findViewById(R.id.only_left);
        final RadioButton only_right = view.findViewById(R.id.only_right);
        final RadioButton both = view.findViewById(R.id.both);
        both.setChecked(true);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                if(checkedId == only_left.getId()){
                    testRange = "only_left";
                }else if(checkedId == only_right.getId()){
                    testRange = "only_right";
                }else if(checkedId == both.getId()){
                    testRange = "both";
                }
            }
        });
        //重写主activity的onTouchEvent函数，并进行该Fragment的逻辑处理
        ManagerAdvanceSettingsActivity.MyTouchListener myTouchListener = new ManagerAdvanceSettingsActivity.MyTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）  
                    View v = getActivity().getCurrentFocus();
                    if (isShouldHideInput(v, event)) {
                        hideSoftInput(v.getWindowToken());
                    }
                }
            }
        };
        ((ManagerAdvanceSettingsActivity)this.getActivity()).registerMyTouchListener(myTouchListener);
    }
    private void initData(){
//        mTestAssistControl.open();
//        mTestAssistControl.addSendData("all_disconnect");
        VMMainThreadFlag = false;
        mQuery1Flag = false;
        mQuery2Flag = false;
        mQuery0Flag = false;
        mUpdataDatabaseFlag = false;
        OutGoodsThreadFlag = true;
        SystemClock.sleep(20);
        while (VMMainThreadRunning) {
            SystemClock.sleep(20);
        }
        registerDetailReceiver();
        mPressureTestThread = new PressureTestThread(getContext()){
            @Override
            public void OnUpdateListener(String text) {
                super.OnUpdateListener(text);
                update(false,text);
            }
        };
    }
    protected void update(final boolean fault, final String text){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if(fault){
                    rec_fault.append(text+"\n");
                    recFaultScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
                rec.append(text+"\n");
                recScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_simulation_pressure_test:
                if(!run){
                    if(!testCount.getText().toString().trim().equals("")){
                        if(Integer.parseInt(testCount.getText().toString().trim())>=1){
                            test_count = Integer.parseInt(testCount.getText().toString().trim());
                        }else{
                            test_count = 0;
                        }
                    }else{
                        test_count = 100;
                    }
                    if(test_count >= 1){
                        SysorderNoUtil sysorderNoUtil = new SysorderNoUtil();
                        String sysorderNo = sysorderNoUtil.getTestSysorderNo(new BindingManager().getMachineID(getContext()));
                        String positionID = sysorderNoUtil.getTestPositionID(getContext(),testRange);
                        rec.append("\n"+"\n"+"\n"+"*****开始测试*****"+"\n"+"\n"+"\n");
                        rec.append("订单号："+ sysorderNo +"\n" + "商品货道：" + positionID +"\n");
                        rec_fault.append("订单号："+ sysorderNo +"\n" + "商品货道：" + positionID +"\n");
                        new BindingManager().setSysorderNo(getContext(), sysorderNo);
                        DBManager.saveDelivery(getContext(),sysorderNo,positionID, DateUtil.getCurrentTime(),Constant.test);
                        mPressureTestThread.init();
                        if(firstTime){
                            mPressureTestThread.start();
                            firstTime = false;
                        }
                        run = true;
                    }
                }else{
                    ToastManager.getInstance().showToast(getContext(),"请先停止运动，或等待当次运动完毕", Toast.LENGTH_LONG);
                }
                break;
            case R.id.stop_simulation_pressure_test:
                if(run && !nextTimeStop){
                    nextTimeStop = true;
                }
                break;
            case R.id.immediately_stop:
                run = false;
                nextTimeStop = false;
                mPressureTestThread.initData();
                DBManager.deliveryHasConfirm(getContext(),new BindingManager().getSysorderNo(getContext()), String.valueOf(Constant.deliveryUnError), DateUtil.getCurrentTime());
                rec.append("\n"+"\n"+"\n"+"*****立即停止测试*****"+"\n"+"\n"+"\n");
                break;
            case R.id.clear_show:
                rec.setText("");
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
            filter.addAction(Resources.njust_outgoods_complete);

            getActivity().getApplicationContext().registerReceiver(mDetailReceiver, filter);

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
                if (Objects.equals(intent.getAction(), Resources.njust_outgoods_complete)) {
                    String outgoods_status = intent.getStringExtra("outgoods_status");
                    if(outgoods_status.equals("closeMidDoorSuccess")){
                        test_count--;
                        if(test_count <= 0){
                            nextTimeStop = true;
                        }
                        if(nextTimeStop){
                            nextTimeStop = false;
                            run = false;
                            update(false,"\n"+"\n"+"\n"+"*****停止测试*****"+"\n"+"\n");
                            update(true,"*****停止测试*****"+"\n"+"\n");
                        }else{
                            update(false,"\n"+"\n"+"\n"+"*****开始下一轮出货*****"+"\n"+"\n");
                            SysorderNoUtil sysorderNoUtil = new SysorderNoUtil();
                            String sysorderNo = sysorderNoUtil.getTestSysorderNo(new BindingManager().getMachineID(getContext()));
                            String positionID = sysorderNoUtil.getTestPositionID(getContext(),testRange);
                            new BindingManager().setSysorderNo(getContext(), sysorderNo);
                            DBManager.saveDelivery(getContext(),sysorderNo,positionID, DateUtil.getCurrentTime(),Constant.test);
                            mPressureTestThread.init();
                        }
                    }else if(outgoods_status.equals("success")){
                        if (intent.getStringExtra("error_type").equals("justRecord&closeAisle")) {
                            String error_ID_justRecord = intent.getStringExtra("error_ID_justRecord");
                            String error_ID_closeAisle = intent.getStringExtra("error_ID_closeAisle");
                            if (!error_ID_justRecord.equals("")) {
                                update(true,"只做记录的故障：" + parsingFault("justRecord",error_ID_justRecord));
                            }
                            if (!error_ID_closeAisle.equals("")) {
                                update(true,"货道故障：" + parsingFault("closeAisle",error_ID_closeAisle));
                            }
                        }
                    }else if(outgoods_status.equals("fail")){
                        String error_type = intent.getStringExtra("error_type");
                        if(error_type.equals("stopAllCounter")/* || error_type.equals("stopOneCounter")*/){
                            String error_ID = intent.getStringExtra("error_ID");
                            update(true,"停整机故障：" + parsingFault("stopAllCounter",error_ID));//日志记录
                            update(false,"\n"+"\n"+"\n"+"*****存在停整柜错误，停止测试*****"+"\n"+"\n");
                            run = false;
                            nextTimeStop = false;
                        }else{//停单柜
                            String error_ID = intent.getStringExtra("error_ID");
                            update(true,"停单柜故障：" + parsingFault("stopOneCounter",error_ID));//日志记录
                            update(false,"\n"+"\n"+"\n"+"*****存在停单柜错误，停止测试*****"+"\n"+"\n");
                            run = false;
                            nextTimeStop = false;
                            mPressureTestThread.initData();
                            DBManager.deliveryHasConfirm(getContext(),new BindingManager().getSysorderNo(getContext()), String.valueOf(Constant.deliveryUnError), DateUtil.getCurrentTime());
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String parsingFault(String type,String record){
        switch (type){
            case "justRecord":{
                String txt = record +"\n";
                String[] justRecord = record.split(",");
                for(String aStr:justRecord){
                    switch (aStr){
                        case "1":txt = txt + aStr +"：左柜X轴出货光栅未检测到货物"+"\n";break;
                        case "2":txt = txt + aStr +"：右柜X轴出货光栅未检测到货物"+"\n";break;
                        case "3":txt = txt + aStr +"：左柜X轴出货光栅货物遮挡超时"+"\n";break;
                        case "4":txt = txt + aStr +"：右柜X轴出货光栅货物遮挡超时"+"\n";break;
                        case "5":txt = txt + aStr +"：左柜X轴出货光栅故障"+"\n";break;
                        case "6":txt = txt + aStr +"：右柜X轴出货光栅故障"+"\n";break;
                        case "7":txt = txt + aStr +"：取货篮光栅故障"+"\n";break;
                    }
                }
                return txt;
            }
            case "closeAisle":{
                String txt = record;
                String[] closeAisle = record.split(";");
                for(String aStr:closeAisle){
                    String[] Aisle = aStr.split(":");
                    txt = txt + "\n" + "货道" + Aisle[0] + "：";
                    String[] fault = Aisle[1].split(",");
                    for(String bStr:fault){
                        switch (bStr){
                            case "1":
                            case "2":txt = txt + "货道电机过流 ";break;
                            case "3":
                            case "4":txt = txt + "货道电机断路 ";break;
                            case "5":
                            case "6":txt = txt + "弹簧电机1反馈开关故障 ";break;
                            case "7":
                            case "8":txt = txt + "弹簧电机2反馈开关故障 ";break;
                            case "9":
                            case "10":txt = txt + "货道超时（无货物输出的超时） ";break;
                            case "11":
                            case "12":txt = txt + "商品超时（货物遮挡光栅超时） ";break;
                        }
                    }
                }
                return txt;
            }
            case "stopOneCounter":{
                String txt = record +"\n";
                String[] stopOneCounter = record.split(",");
                for(String aStr:stopOneCounter){
                    switch (aStr){
                        case "1":txt = txt + aStr +"：左柜Y轴电机过流"+"\n";break;
                        case "2":txt = txt + aStr +"：右柜Y轴电机过流"+"\n";break;
                        case "3":txt = txt + aStr +"：左柜Y轴电机断路"+"\n";break;
                        case "4":txt = txt + aStr +"：右柜Y轴电机断路"+"\n";break;
                        case "5":txt = txt + aStr +"：左柜Y轴上止点开关故障"+"\n";break;
                        case "6":txt = txt + aStr +"：右柜Y轴上止点开关故障"+"\n";break;
                        case "7":txt = txt + aStr +"：左柜Y轴下止点开关故障"+"\n";break;
                        case "8":txt = txt + aStr +"：右柜Y轴下止点开关故障"+"\n";break;
                        case "9":txt = txt + aStr +"：左柜Y轴电机超时"+"\n";break;
                        case "10":txt = txt + aStr +"：右柜Y轴电机超时"+"\n";break;
                        case "11":txt = txt + aStr +"：左柜Y轴码盘故障"+"\n";break;
                        case "12":txt = txt + aStr +"：右柜Y轴码盘故障"+"\n";break;
                        case "13":txt = txt + aStr +"：左柜X轴电机过流"+"\n";break;
                        case "14":txt = txt + aStr +"：右柜X轴电机过流"+"\n";break;
                        case "15":txt = txt + aStr +"：左柜X轴电机断路"+"\n";break;
                        case "16":txt = txt + aStr +"：右柜X轴电机断路"+"\n";break;
                        case "17":txt = txt + aStr +"：左柜X轴电机超时"+"\n";break;
                        case "18":txt = txt + aStr +"：右柜X轴电机超时"+"\n";break;
                        case "19":txt = txt + aStr +"：左柜货道下货光栅故障"+"\n";break;
                        case "20":txt = txt + aStr +"：右柜货道下货光栅故障"+"\n";break;
                        case "21":txt = txt + aStr +"：左柜边柜板通信故障"+"\n";break;
                        case "22":txt = txt + aStr +"：右柜边柜板通信故障"+"\n";break;
                        case "23":txt = txt + aStr +"：左柜货道板通信故障"+"\n";break;
                        case "24":txt = txt + aStr +"：右柜货道板通信故障"+"\n";break;
                        case "25":txt = txt + aStr +"：中柜板通信故障"+"\n";break;
                        case "26":txt = txt + aStr +"：左柜Y轴触发上限位"+"\n";break;
                        case "27":txt = txt + aStr +"：右柜Y轴触发上限位"+"\n";break;
                    }
                }
                return txt;
            }
            case "stopAllCounter":{
                String txt = record +"\n";
                String[] stopAllCounter = record.split(",");
                for(String aStr:stopAllCounter){
                    switch (aStr){
                        case "1":txt = txt + aStr +"：左柜出货门电机过流"+"\n";break;
                        case "2":txt = txt + aStr +"：右柜出货门电机过流"+"\n";break;
                        case "3":txt = txt + aStr +"：左柜出货门电机断路"+"\n";break;
                        case "4":txt = txt + aStr +"：右柜出货门电机断路"+"\n";break;
                        case "5":txt = txt + aStr +"：左柜出货门前止点开关故障"+"\n";break;
                        case "6":txt = txt + aStr +"：右柜出货门前止点开关故障"+"\n";break;
                        case "7":txt = txt + aStr +"：左柜出货门后止点开关故障"+"\n";break;
                        case "8":txt = txt + aStr +"：右柜出货门后止点开关故障"+"\n";break;
                        case "9":txt = txt + aStr +"：左柜开、关出货门超时"+"\n";break;
                        case "10":txt = txt + aStr +"：右柜开、关出货门超时"+"\n";break;
                        case "11":txt = txt + aStr +"：左柜出货门半开、半关"+"\n";break;
                        case "12":txt = txt + aStr +"：右柜出货门半开、半关"+"\n";break;
                        case "13":txt = txt + aStr +"：取货门电机过流"+"\n";break;
                        case "14":txt = txt + aStr +"：取货门电机断路"+"\n";break;
                        case "15":txt = txt + aStr +"：取货门上止点开关故障"+"\n";break;
                        case "16":txt = txt + aStr +"：取货门下止点开关故障"+"\n";break;
                        case "17":txt = txt + aStr +"：开、关取货门超时"+"\n";break;
                        case "18":txt = txt + aStr +"：取货门半开、半关"+"\n";break;
                        case "19":txt = txt + aStr +"：防夹手光栅故障"+"\n";break;
                    }
                }
                return txt;
            }
            default:break;
        }
        return "";
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mPressureTestThread.initData();
        OutGoodsThreadFlag = false;
        mPressureTestThread.mTimer.cancel();
        mPressureTestThread.mTimerOff.cancel();
        DBManager.deliveryHasConfirm(getContext(),new BindingManager().getSysorderNo(getContext()), String.valueOf(Constant.deliveryUnError), DateUtil.getCurrentTime());

//        mTestAssistControl.close();
        VMMainThreadFlag = true;
        mQuery1Flag = true;
        mQuery2Flag = true;
        mQuery0Flag = true;
        mUpdataDatabaseFlag = true;
        if ( mDetailReceiver!=null ) {
            /**注销广播*/
            getActivity().getApplicationContext().unregisterReceiver(mDetailReceiver);
        }
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            return !(event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom);
        }// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (im != null) {
                im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
