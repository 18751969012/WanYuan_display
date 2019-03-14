package com.njust.wanyuan_display.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.adapter.AisleTestAdapter;
import com.njust.wanyuan_display.config.Constant;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.greenDao.AisleInfo;
import com.njust.wanyuan_display.greenDao.Transaction;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.service.OutGoodsService;
import com.njust.wanyuan_display.util.DateUtil;
import com.njust.wanyuan_display.util.SerialPortUtils;
import com.njust.wanyuan_display.util.SysorderNoUtil;
import com.njust.wanyuan_display.util.ToastManager;

import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.njust.wanyuan_display.application.VMApplication.OutGoodsThreadFlag;
import static com.njust.wanyuan_display.application.VMApplication.VMMainThreadFlag;
import static com.njust.wanyuan_display.application.VMApplication.VMMainThreadRunning;
import static com.njust.wanyuan_display.application.VMApplication.mQuery0Flag;
import static com.njust.wanyuan_display.application.VMApplication.mQuery1Flag;
import static com.njust.wanyuan_display.application.VMApplication.mQuery2Flag;
import static com.njust.wanyuan_display.application.VMApplication.mUpdataDatabaseFlag;

public class ManagerAisleTestActivity extends BaseExitKeyboardActivity implements View.OnClickListener {
    private static final Logger log = Logger.getLogger(ManagerAisleTestActivity.class);
    private TextView returnBack;
    private RecyclerView aisleTest;
    private AisleTestAdapter mAisleTestAdapter;
    private SparseArray<AisleInfo> dataList= new SparseArray<>();
    private SparseArray<AisleInfo> allAisle= new SparseArray<>();
    private Button test_by_row,test_by_column,test_all_aisle,simulation_outgoods,stop_test;
    private EditText input_row,input_column,input_positionID;
    private SerialPortUtils serialPortUtils = new SerialPortUtils();
    private Handler handler;
    private boolean mTest_start = false;
    private Intent service = null;
    private Button test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_aisle_test);
        initView();
        initSerial();
    }

    private void initView() {
//        test = findViewById(R.id.test);
//        test.setOnClickListener(this);

        returnBack = (TextView) findViewById(R.id.returnBack);
        returnBack.setOnClickListener(this);

        test_by_row = (Button) findViewById(R.id.test_by_row);
        test_by_row.setOnClickListener(this);
        test_by_column = (Button) findViewById(R.id.test_by_column);
        test_by_column.setOnClickListener(this);
        test_all_aisle = (Button) findViewById(R.id.test_all_aisle);
        test_all_aisle.setOnClickListener(this);
        simulation_outgoods = (Button) findViewById(R.id.simulation_outgoods);
        simulation_outgoods.setOnClickListener(this);
        stop_test = (Button) findViewById(R.id.stop_test);
        stop_test.setOnClickListener(this);

        input_row = (EditText) findViewById(R.id.input_row);
        input_column = (EditText) findViewById(R.id.input_column);
        input_positionID = (EditText) findViewById(R.id.input_positionID);
        dataList.clear();
        allAisle = DBManager.getAisleInfoList2(getApplicationContext());
        dataList = DBManager.getAisleInfoListUnlocked(getApplicationContext());
        aisleTest = (RecyclerView) findViewById(R.id.aisleTest);
        aisleTest.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAisleTestAdapter = new AisleTestAdapter(this,dataList);
        aisleTest.setAdapter(mAisleTestAdapter);

        IntentFilter filter = new IntentFilter();//注册广播，用来接收模拟出货的完成信号
        filter.addAction("njust_outgoods_complete");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(myReceiver, filter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.test:
//                serialPortUtils.addSendData("query_Y");
//                break;
            case R.id.returnBack:
                serialPortUtils.close();
                this.finish();
                if(myReceiver != null){
                    unregisterReceiver(myReceiver);
                    myReceiver = null;
                }
                if (service!=null) {
                    stopService(service);
                    service = null;
                }
                VMMainThreadFlag = true;
                mQuery1Flag = true;
                mQuery2Flag = true;
                mQuery0Flag = true;
                mUpdataDatabaseFlag = true;
                break;
            case R.id.test_by_row:
                if(!mTest_start){
                    String row = input_row.getText().toString().trim();
                    String txtY = input_row.getText().toString();
                    Pattern pY = Pattern.compile("[0-9]*");
                    Matcher mY = pY.matcher(txtY);
                    if(mY.matches() && !row.equals("")){//输入的数字
                        if((Integer.parseInt(row) >= 1 && Integer.parseInt(row) <= 6) || (Integer.parseInt(row) >= 10 && Integer.parseInt(row) <= 16)){
                            serialPortUtils.stopTest();
                            serialPortUtils.addSendData("test_by_row"+","+row+","+"1");
                        }else{
                            ToastManager.getInstance().showToast(getApplicationContext(),"请输入1-6,11-16的数字", Toast.LENGTH_LONG);
                            break;
                        }
                    }else{
                        ToastManager.getInstance().showToast(getApplicationContext(),"请输入数字", Toast.LENGTH_LONG);
                        break;
                    }
                }else{
                    ToastManager.getInstance().showToast(getApplicationContext(),"请等待模拟测试出货完成", Toast.LENGTH_LONG);
                    break;
                }
                break;
            case R.id.test_by_column:
                if(!mTest_start){
                    String column = input_column.getText().toString().trim();
                    String txtY1 = input_column.getText().toString();
                    Pattern pY1 = Pattern.compile("[0-9]*");
                    Matcher mY1 = pY1.matcher(txtY1);
                    if(mY1.matches() && !column.equals("")){//输入的数字
                        if(Integer.parseInt(column) >= 1 && Integer.parseInt(column) <= 10){
                            serialPortUtils.stopTest();
                            serialPortUtils.addSendData("test_by_column"+","+column+","+"1");
                        }else{
                            ToastManager.getInstance().showToast(getApplicationContext(),"请输入1-10的数字", Toast.LENGTH_LONG);
                            break;
                        }
                    }else{
                        ToastManager.getInstance().showToast(getApplicationContext(),"请输入数字", Toast.LENGTH_LONG);
                        break;
                    }
                }else{
                    ToastManager.getInstance().showToast(getApplicationContext(),"请等待模拟测试出货完成", Toast.LENGTH_LONG);
                    break;
                }
                break;
            case R.id.test_all_aisle:
                if(!mTest_start){
                    serialPortUtils.stopTest();
                    serialPortUtils.addSendData("test_all_aisle");
                }else{
                    ToastManager.getInstance().showToast(getApplicationContext(),"请等待模拟测试出货完成", Toast.LENGTH_LONG);
                    break;
                }
                break;
            case R.id.simulation_outgoods:
                String positionID = input_positionID.getText().toString().trim();
                String txtpositionID = input_positionID.getText().toString();
                Pattern ppositionID = Pattern.compile("[0-9[,]]*");
                Matcher mpositionID = ppositionID.matcher(txtpositionID);
                ok:if(mpositionID.matches() && !positionID.equals("")){//输入的数字
                    String[] str = positionID.split(",");
                    int[] pIDs = new int[str.length];
                    for (int i = 0; i < str.length; i++) {
                        pIDs[i] = Integer.parseInt(str[i]);
                        if(pIDs[i] >= 1 && pIDs[i] <= 200){
                            if(DBManager.getAisleInfo(getApplicationContext(),((pIDs[i]>=1&&pIDs[i]<=100)? 1:2),pIDs[i]).getState() == 0) {
                            }else{
                                ToastManager.getInstance().showToast(getApplicationContext(),"请输入正常（非锁死非故障）货道", Toast.LENGTH_LONG);
                                break ok;
                            }
                        }else{
                            ToastManager.getInstance().showToast(getApplicationContext(),"请输入1-200的数字", Toast.LENGTH_LONG);
                            break ok;
                        }
                    }
                    Transaction transaction = DBManager.getLastTransaction(getApplicationContext());
                    if(transaction == null || transaction.getComplete().equals(String.valueOf(Constant.deliveryComplete))){
                        SysorderNoUtil sysorderNoUtil = new SysorderNoUtil();
                        String sysorderNo = sysorderNoUtil.getTestSysorderNo(new BindingManager().getMachineID(getApplicationContext()));;
                        new BindingManager().setSysorderNo(getApplicationContext(), sysorderNo);
                        DBManager.saveDelivery(getApplicationContext(),sysorderNo,positionID, DateUtil.getCurrentTime(),Constant.test);
                        serialPortUtils.stopThreadStatus = true;//停止serialPortUtils中的发送接收
							    /*通知开始出货，屏幕等待完成*/
                        if (service!=null) {
                            stopService(service);
                            service = null;
                        }
                        service = new Intent(getApplicationContext(), OutGoodsService.class);
                        getApplicationContext().startService(service);
                        mTest_start = true;
                        simulation_outgoods.setText("正在出货，请勿进行其他操作");
                    }else{
                        ToastManager.getInstance().showToast(getApplicationContext(),"请等待模拟测试出货完成", Toast.LENGTH_LONG);
                        break;
                    }
                }else{
                    ToastManager.getInstance().showToast(getApplicationContext(),"请输入数字，逗号隔开", Toast.LENGTH_LONG);
                    break;
                }
                break;
            case R.id.stop_test:
                serialPortUtils.stopTest();
                if (service!=null) {
                    stopService(service);
                    service = null;
                }
                simulation_outgoods.setText("模拟出货");
                mTest_start = false;
                break;
            default:
                break;
        }
    }

    public void aisleTest(String positionID){
        if(!mTest_start){
            serialPortUtils.addSendData("aisleTestActivity"+","+positionID);
        }else{
            ToastManager.getInstance().showToast(getApplicationContext(),"请等待模拟测试出货完成", Toast.LENGTH_LONG);
        }
    }

    private void initSerial() {
        VMMainThreadFlag = false;
        mQuery1Flag = false;
        mQuery2Flag = false;
        mQuery0Flag = false;
        mUpdataDatabaseFlag = false;
        OutGoodsThreadFlag = true;
        SystemClock.sleep(20);
        while(VMMainThreadRunning){
            SystemClock.sleep(20);
        }
        serialPortUtils.open(getApplicationContext());
        serialPortUtils.setOnDataReceiveListener(new SerialPortUtils.OnDataReceiveListener() {
            @Override
            public void onDataReceive(String code, byte[] rec, int size) {
                if(rec[0] == (byte)0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte)0x0F && rec[rec.length-2] == (byte)0xF1 && rec[6] == (byte)0x70 && rec[7] == (byte)0x50 && rec[16] == (byte)0x02){
                   if((rec[9]&0xFF) != (byte)0x01){
                       if(allAisle.get(Integer.parseInt(code)).getState() != Constant.aisleState_locked){
                           dataList.get(Integer.parseInt(code)).setState(Constant.aisleState_fault);
                           DBManager.updateAisleTestManager(getApplicationContext(), Integer.parseInt(code),Constant.aisleState_fault);
                       }
                   }else{
                       if(allAisle.get(Integer.parseInt(code)).getState() != Constant.aisleState_locked){
                           dataList.get(Integer.parseInt(code)).setState(Constant.aisleState_normal);
                           DBManager.updateAisleTestManager(getApplicationContext(), Integer.parseInt(code),Constant.aisleState_normal);
                       }
                   }
                }
                update(true);
            }
        });
    }
    protected void update(final boolean updateUI){
        runOnUiThread(new Runnable() {
            public void run() {
                if(updateUI){
                    mAisleTestAdapter.notifyDataSetChanged();
                }else{
                    simulation_outgoods.setText("模拟出货");
                    mTest_start = false;
                }
            }
        });
    }

    /**
     * 接收广播，处理模拟出货完毕
     * */
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String outgoods_status = intent.getStringExtra("outgoods_status");
            if(outgoods_status.equals("closeMidDoorSuccess")){
                VMMainThreadFlag = false;
                mQuery1Flag = false;
                mQuery2Flag = false;
                mQuery0Flag = false;
                mUpdataDatabaseFlag = false;
                SystemClock.sleep(20);
                while(VMMainThreadRunning){
                    SystemClock.sleep(20);
                }
                update(false);
                if (service!=null) {
                    stopService(service);
                    service = null;
                }
                serialPortUtils.stopThreadStatus = false;//重新开启serialPortUtils中的发送接收
            }else if(outgoods_status.equals("fail")){
                String error_type = intent.getStringExtra("error_type");
                if(error_type.equals("stopAllCounter") || error_type.equals("stopOneCounter")){
                    VMMainThreadFlag = false;
                    mQuery1Flag = false;
                    mQuery2Flag = false;
                    mQuery0Flag = false;
                    mUpdataDatabaseFlag = false;
                    SystemClock.sleep(20);
                    while(VMMainThreadRunning){
                        SystemClock.sleep(20);
                    }
                    update(false);
                }
                if (service!=null) {
                    stopService(service);
                    service = null;
                }
                serialPortUtils.stopThreadStatus = false;//重新开启serialPortUtils中的发送接收
            }
        }

    };
}
