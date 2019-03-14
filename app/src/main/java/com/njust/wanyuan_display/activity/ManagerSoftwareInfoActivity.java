package com.njust.wanyuan_display.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.util.MachineUtil;
import com.njust.wanyuan_display.util.NetWorkUtil;
import com.njust.wanyuan_display.util.ScreenUtil;

import org.apache.log4j.Logger;


public class ManagerSoftwareInfoActivity extends BaseActivity implements View.OnClickListener {
    private static final Logger log = Logger.getLogger(ManagerSoftwareInfoActivity.class);
    private TextView returnBack;
    private TextView board,serial,brand,manufacturer,model,sim,ipAddress,macAddress,android_version,android_sdk,app_name,version,screen_density,screen_width,screen_height;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_software_info);
        initView();
        initData();
    }

    private void initView() {
        returnBack = (TextView) findViewById(R.id.returnBack);
        returnBack.setOnClickListener(this);

        board = (TextView) findViewById(R.id.board);
        serial = (TextView) findViewById(R.id.serial);
        brand = (TextView) findViewById(R.id.brand);
        manufacturer = (TextView) findViewById(R.id.manufacturer);
        model = (TextView) findViewById(R.id.model);
        sim = (TextView) findViewById(R.id.sim);
        ipAddress = (TextView) findViewById(R.id.ipAddress);
        macAddress = (TextView) findViewById(R.id.macAddress);
        android_version = (TextView) findViewById(R.id.android_version);
        android_sdk = (TextView) findViewById(R.id.android_sdk);
        app_name = (TextView) findViewById(R.id.app_name);
        version = (TextView) findViewById(R.id.version);
        screen_density = (TextView) findViewById(R.id.screen_density);
        screen_width = (TextView) findViewById(R.id.screen_width);
        screen_height = (TextView) findViewById(R.id.screen_height);
    }
    private void initData() {
        MachineUtil machineUtil = new MachineUtil();
        board.setText(machineUtil.board());
        serial.setText(machineUtil.serial());
        brand.setText(machineUtil.brand());
        manufacturer.setText(machineUtil.manufacturer());
        model.setText(machineUtil.model());
        sim.setText(machineUtil.getIMSI(getApplicationContext()));
        ipAddress.setText(NetWorkUtil.wifiInfo(getApplicationContext()).getString("ipAddress"));
        macAddress.setText(NetWorkUtil.wifiInfo(getApplicationContext()).getString("macAddress"));
        android_version.setText(machineUtil.androidversion());
        android_sdk.setText(machineUtil.androidsdk());
        app_name.setText(getResources().getString(R.string.app_name));
        version.setText(machineUtil.apkVersion(getApplicationContext()));
        screen_density.setText(ScreenUtil.screenDpi(getApplicationContext())+"dpi");
        screen_width.setText(ScreenUtil.screenWidth(getApplicationContext()));
        screen_height.setText(ScreenUtil.screenHeight(getApplicationContext()));
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
}
