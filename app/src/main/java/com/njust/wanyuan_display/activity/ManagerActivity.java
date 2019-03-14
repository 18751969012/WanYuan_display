package com.njust.wanyuan_display.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.njust.wanyuan_display.R;

import java.lang.reflect.Method;

public class ManagerActivity extends BaseActivity implements View.OnClickListener {

    private TextView returnBack;

    private GridLayout manager_gridLayout;
    private RelativeLayout replenishment;
    private RelativeLayout aisle_setting;
    private RelativeLayout device_state;
    private RelativeLayout aisle_test;
    private RelativeLayout advanced_setting;
    private RelativeLayout software_info;
    private RelativeLayout backtodesktop;
    private RelativeLayout restart_App;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        initView();
    }

    private void initView() {
        returnBack = (TextView) findViewById(R.id.returnBack);
        returnBack.setOnClickListener(this);


        manager_gridLayout = (GridLayout) findViewById(R.id.manager_gridLayout);

        replenishment = (RelativeLayout) findViewById(R.id.replenishment);
        replenishment.setOnClickListener(this);
        aisle_setting = (RelativeLayout) findViewById(R.id.aisle_setting);
        aisle_setting.setOnClickListener(this);
        device_state = (RelativeLayout) findViewById(R.id.device_state);
        device_state.setOnClickListener(this);
        aisle_test = (RelativeLayout) findViewById(R.id.aisle_test);
        aisle_test.setOnClickListener(this);
        advanced_setting = (RelativeLayout) findViewById(R.id.advanced_setting);
        advanced_setting.setOnClickListener(this);
        software_info = (RelativeLayout) findViewById(R.id.software_info);
        software_info.setOnClickListener(this);
        backtodesktop = (RelativeLayout) findViewById(R.id.backtodesktop);
        backtodesktop.setOnClickListener(this);
        restart_App = (RelativeLayout) findViewById(R.id.restart_App);
        restart_App.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.returnBack:
                Intent intent = new Intent(ManagerActivity.this, AdvertisingActivity.class);
                startActivity(intent);
                this.finish();
                break;
            case R.id.replenishment:
                Intent intentReplenishment = new Intent(ManagerActivity.this, ManagerReplenishActivity.class);
                startActivity(intentReplenishment);
                break;
            case R.id.aisle_setting:
                Intent intentAisleSetting = new Intent(ManagerActivity.this, ManagerAisleSettingsActivity.class);
                startActivity(intentAisleSetting);
                break;
            case R.id.device_state:
                Intent intentDeviceState = new Intent(ManagerActivity.this, ManagerDeviceStateAndSettingActivity.class);
                startActivity(intentDeviceState);
                break;
            case R.id.aisle_test:
                Intent intentAisleTest = new Intent(ManagerActivity.this, ManagerAisleTestActivity.class);
                startActivity(intentAisleTest);
                break;
            case R.id.advanced_setting:
                Intent intentAdvancedSetting = new Intent(ManagerActivity.this, ManagerAdvanceSettingsActivity.class);
                startActivity(intentAdvancedSetting);
                break;
            case R.id.software_info:
                Intent intentSoftwareInfo = new Intent(ManagerActivity.this, ManagerSoftwareInfoActivity.class);
                startActivity(intentSoftwareInfo);
                break;
            case R.id.backtodesktop:
                Intent intentBacktodesktop = new Intent();
                intentBacktodesktop.setAction(Intent.ACTION_MAIN);
                intentBacktodesktop.addCategory(Intent.CATEGORY_HOME);
                startActivity(intentBacktodesktop);
                displayBottomNavibar(true);
                break;
            case R.id.restart_App:
                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
                LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(LaunchIntent);
                break;
            default:
                break;
        }
    }

    /**
     * 关闭软键盘
     * */
    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    /**
     * 隐藏、显示底部导航栏
     */
    private void displayBottomNavibar(boolean show){
        try {
            Intent b = new Intent("com.navibardisplay");
            if(show){
//				Class<?> c = Class.forName("android.os.SystemProperties");
//				Method set = c.getMethod("set", String.class, String.class);
//				set.invoke(c, "persist.sys.navibar", "0");//display navi bar
                b.putExtra("dispaly", 1);
            }else{
//				Class<?> c = Class.forName("android.os.SystemProperties");
//				Method set = c.getMethod("set", String.class, String.class);
//				set.invoke(c, "persist.sys.navibar", "1");
                b.putExtra("dispaly", 0);
            }
            getApplicationContext().sendBroadcast(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
