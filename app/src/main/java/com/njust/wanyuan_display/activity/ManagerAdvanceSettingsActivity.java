package com.njust.wanyuan_display.activity;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.fragment.AdvanceGoodsShowTypeFragment;
import com.njust.wanyuan_display.fragment.AdvanceLightAutoFragment;
import com.njust.wanyuan_display.fragment.AdvancePressureTestFragment;
import com.njust.wanyuan_display.fragment.AdvanceRestsFragment;
import com.njust.wanyuan_display.fragment.AdvanceTempFragment;
import com.njust.wanyuan_display.fragment.AisleDetailedFragment;
import com.njust.wanyuan_display.fragment.AisleMergeFragment;
import com.njust.wanyuan_display.fragment.AisleRoughlyFragment;

public class ManagerAdvanceSettingsActivity extends BaseActivity implements View.OnClickListener {

    private TextView returnBack;
    private TabLayout mTvTabs;
    private FragmentManager mFragmentManager;
    private FrameLayout framelayout;
    private FragmentTransaction fragmentTransaction,transaction;
    private Fragment autoLight,temp,showType,rests,pressureTest;
    private Fragment fragmentNow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_advance_setting);
        initView();
        initTab();
    }

    private void initView() {
        returnBack = (TextView) findViewById(R.id.returnBack);
        returnBack.setOnClickListener(this);

        framelayout = (FrameLayout) findViewById(R.id.framelayout);
        //实例化Fragment
        autoLight = new AdvanceLightAutoFragment();
        temp = new AdvanceTempFragment();
        showType = new AdvanceGoodsShowTypeFragment();
        rests = new AdvanceRestsFragment();
        pressureTest = new AdvancePressureTestFragment();


        //获取碎片管理者
        mFragmentManager = getSupportFragmentManager();
        //初始化一个fragment
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.framelayout, autoLight);
        fragmentTransaction.commitNow();
        fragmentNow = autoLight;
    }

    private void initTab(){
        mTvTabs = (TabLayout) findViewById(R.id.tabLayout);
        mTvTabs.setTabGravity(TabLayout.GRAVITY_CENTER);
        mTvTabs.addTab(mTvTabs.newTab().setText("灯开启时间"));
        mTvTabs.addTab(mTvTabs.newTab().setText("温度参数自定义"));
        mTvTabs.addTab(mTvTabs.newTab().setText("售卖商品显示方式"));
        mTvTabs.addTab(mTvTabs.newTab().setText("其他"));
        mTvTabs.addTab(mTvTabs.newTab().setText("压测"));
        mTvTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                transaction = mFragmentManager.beginTransaction();
                if (tab.getPosition() == 0) {
                    if (!fragmentNow.equals(autoLight)) {
                        if (autoLight.isAdded()) {
                            transaction.hide(fragmentNow).show(autoLight);
                        } else {
                            transaction.hide(fragmentNow).add(R.id.framelayout, autoLight);
                        }
                        transaction.commitNow();
                        fragmentNow = autoLight;
                    }
                } else if (tab.getPosition() == 1) {
                    if (!fragmentNow.equals(temp)) {
                        if (temp.isAdded()) {
                            transaction.hide(fragmentNow).show(temp);
                        } else {
                            transaction.hide(fragmentNow).add(R.id.framelayout, temp);
                        }
                        transaction.commitNow();
                        fragmentNow = temp;
                    }
                }else if (tab.getPosition() == 2) {
                    if (!fragmentNow.equals(showType)) {
                        if (showType.isAdded()) {
                            transaction.hide(fragmentNow).show(showType);
                        } else {
                            transaction.hide(fragmentNow).add(R.id.framelayout, showType);
                        }
                        transaction.commitNow();
                        fragmentNow = showType;
                    }
                }else if (tab.getPosition() == 3) {
                    if (!fragmentNow.equals(rests)) {
                        if (rests.isAdded()) {
                            transaction.hide(fragmentNow).show(rests);
                        } else {
                            transaction.hide(fragmentNow).add(R.id.framelayout, rests);
                        }
                        transaction.commitNow();
                        fragmentNow = rests;
                    }
                }else if (tab.getPosition() == 4) {
                    if (!fragmentNow.equals(pressureTest)) {
                        if (pressureTest.isAdded()) {
                            transaction.hide(fragmentNow).show(pressureTest);
                        } else {
                            transaction.hide(fragmentNow).add(R.id.framelayout, pressureTest);
                        }
                        transaction.commitNow();
                        fragmentNow = pressureTest;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.returnBack:
                this.finish();
                break;
            default:
                break;
        }
    }

    /** 
     * 提供给fragment接口
     */
    public static interface MyTouchListener {
        public void onTouchEvent(MotionEvent event);
    }
    public MyTouchListener mMyTouchListener = new MyTouchListener() {
        @Override
        public void onTouchEvent(MotionEvent event) {

        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mMyTouchListener.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
    public void registerMyTouchListener(MyTouchListener listener) {
        mMyTouchListener = listener;
    }
}
