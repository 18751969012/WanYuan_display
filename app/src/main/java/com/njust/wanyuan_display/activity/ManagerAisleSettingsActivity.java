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
import com.njust.wanyuan_display.fragment.AisleDetailedFragment;
import com.njust.wanyuan_display.fragment.AisleMergeFragment;
import com.njust.wanyuan_display.fragment.AisleRoughlyFragment;

public class ManagerAisleSettingsActivity extends BaseActivity implements View.OnClickListener {

    private TextView returnBack;
    private TabLayout mTvTabs;
    private FragmentManager mFragmentManager;
    private FrameLayout framelayout;
    private FragmentTransaction fragmentTransaction,transaction;
    private Fragment mRoughly, mDetailed, mMerge;
    private Fragment fragmentNow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_aisle_setting);
        initView();
        initTab();
    }

    private void initView() {
        returnBack = (TextView) findViewById(R.id.returnBack);
        returnBack.setOnClickListener(this);

        framelayout = (FrameLayout) findViewById(R.id.framelayout);
        //实例化Fragment
        mRoughly = new AisleRoughlyFragment();
        mDetailed = new AisleDetailedFragment();
        mMerge = new AisleMergeFragment();

        //获取碎片管理者
        mFragmentManager = getSupportFragmentManager();
        //初始化一个fragment
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.framelayout, mRoughly);
        fragmentTransaction.commitNow();
        fragmentNow = mRoughly;
    }

    private void initTab(){
        mTvTabs = (TabLayout) findViewById(R.id.tabLayout);
        mTvTabs.setTabGravity(TabLayout.GRAVITY_CENTER);
        mTvTabs.addTab(mTvTabs.newTab().setText("按层设置"));
        mTvTabs.addTab(mTvTabs.newTab().setText("精确调整"));
        mTvTabs.addTab(mTvTabs.newTab().setText("合并货道"));
        mTvTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                transaction = mFragmentManager.beginTransaction();
                if (tab.getPosition() == 0) {
                    if (!fragmentNow.equals(mRoughly)) {
                        if (mRoughly.isAdded()) {
                            transaction.hide(fragmentNow).show(mRoughly);
                        } else {
                            transaction.hide(fragmentNow).add(R.id.framelayout, mRoughly);
                        }
                        transaction.commitNow();
                        fragmentNow = mRoughly;
                    }
                } else if (tab.getPosition() == 1) {
                    if (!fragmentNow.equals(mDetailed)) {
                        if (mDetailed.isAdded()) {
                            transaction.hide(fragmentNow).show(mDetailed);
                        } else {
                            transaction.hide(fragmentNow).add(R.id.framelayout, mDetailed);
                        }
                        transaction.commitNow();
                        fragmentNow = mDetailed;
                    }
                }else if (tab.getPosition() == 2) {
                    if (!fragmentNow.equals(mMerge)) {
                        if (mMerge.isAdded()) {
                            transaction.hide(fragmentNow).show(mMerge);
                        } else {
                            transaction.hide(fragmentNow).add(R.id.framelayout, mMerge);
                        }
                        transaction.commitNow();
                        fragmentNow = mMerge;
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
                DBManager.clearQueryCache(getApplicationContext());
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
