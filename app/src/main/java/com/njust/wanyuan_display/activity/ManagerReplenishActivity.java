package com.njust.wanyuan_display.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.adapter.ReplenishAdapter;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.greenDao.AisleInfo;
import com.njust.wanyuan_display.parcelable.AisleInfoParcelable;
import com.njust.wanyuan_display.service.HandlerService;
import com.njust.wanyuan_display.util.DateUtil;
import com.njust.wanyuan_display.util.ToastManager;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Objects;

public class ManagerReplenishActivity extends BaseExitKeyboardActivity implements View.OnClickListener {

    private static final Logger log = Logger.getLogger(ManagerReplenishActivity.class);

    private Context mContext;
    private TextView returnBack;
    private Button submit_save;
    private Button goods_list_refurbish;
    private GridView replenishment_gridview;
    public ReplenishAdapter mReplenishAdapter;
    private ArrayList<AisleInfo> dataList;
    private ArrayList<AisleInfoParcelable> replenishList = new ArrayList<>();
    public DetailReceiver mDetailReceiver;
    private String startReplenishTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_manager_replenishment);
        startReplenishTime = DateUtil.getCurrentTime();
        initView();
        registerDetailReceiver();
    }

    private void initView() {
        returnBack = (TextView) findViewById(R.id.returnBack);
        returnBack.setOnClickListener(this);

        submit_save = (Button) findViewById(R.id.submit_save);
        submit_save.setOnClickListener(this);
        goods_list_refurbish = (Button) findViewById(R.id.goods_list_refurbish);
        goods_list_refurbish.setOnClickListener(this);


        dataList = DBManager.getAisleInfoListNormal(mContext);
        replenishment_gridview = (GridView)findViewById(R.id.replenishment_gridview);
        mReplenishAdapter = new ReplenishAdapter(dataList,this);
        replenishment_gridview.setAdapter(mReplenishAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.returnBack:
                this.finish();
                DBManager.clearQueryCache(mContext);
                replenishList.clear();
                break;
            case R.id.submit_save:
                for(int i=0;i<dataList.size();i++){
                    AisleInfo aisleInfo =  dataList.get(i);
                    if(aisleInfo.getGoodID()>=0){
                        AisleInfoParcelable aisleInfoParcelable = new AisleInfoParcelable(aisleInfo.getPositionID(),aisleInfo.getCounter(),aisleInfo.getCapacity(),
                                aisleInfo.getGoodQuantity(),aisleInfo.getGoodID(),aisleInfo.getGoodName(),aisleInfo.getGoodPrice(),aisleInfo.getCount(),aisleInfo.getGoodsImgLocalUrl());
                        replenishList.add(aisleInfoParcelable);
                    }
                }
                /*更新本地货道信息*/
                DBManager.updateAisleInfoListNormal(getApplicationContext(),dataList);
                Intent handlerIntent1 = new Intent(mContext, HandlerService.class);
                handlerIntent1.setAction(Resources.local_replenish_goods_upload);
                handlerIntent1.putParcelableArrayListExtra("replenishList",replenishList);
                handlerIntent1.putExtra("startReplenishTime",startReplenishTime);
                mContext.startService(handlerIntent1);
                break;
            case R.id.goods_list_refurbish:
//                Intent handlerIntent = new Intent(mContext, HandlerService.class);
//                handlerIntent.setAction(Resources.request_goods_bank);
//                mContext.startService(handlerIntent);

                DBManager.deletAllGoodsInfo(getApplicationContext());
                DBManager.addGoodsInfoTest(mContext);
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
            filter.addAction(Resources.request_goods_bank_done);
            filter.addAction(Resources.local_replenish_goods_upload_done);
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

                if (Objects.equals(intent.getAction(), Resources.request_goods_bank_done)) {
                    ToastManager.getInstance().showToast(getApplicationContext(),"商品列表刷新完成", Toast.LENGTH_LONG);
                }else if(Objects.equals(intent.getAction(), Resources.local_replenish_goods_upload_done)){
                    ToastManager.getInstance().showToast(getApplicationContext(),"本地补货上传/保存完成", Toast.LENGTH_LONG);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
