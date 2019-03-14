package com.njust.wanyuan_display.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.action.InitMachineAction;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.manager.OkHttp3Manager;
import com.njust.wanyuan_display.service.GuaranteeService;
import com.njust.wanyuan_display.service.HandlerService;
import com.njust.wanyuan_display.util.MachineUtil;
import com.njust.wanyuan_display.util.StringUtil;
import com.njust.wanyuan_display.util.ToastManager;

import java.util.ArrayList;
import java.util.List;

public class BindingActivity extends BaseExitKeyboardActivity implements View.OnClickListener{

    private Context context;

    private TextView machine_ID;
    private EditText username, password;
    private Button bt_username_clear;
    private Button bt_pwd_clear;
    private Button bt_pwd_eye;
    private Button binding;
    private boolean isOpen = false;
    private boolean noPermissions = false;//用来判断拒绝权限之后退出程序


    String[] permissions = new String[]{
//            /*日历*/
//            Manifest.permission.READ_CALENDAR,
//            Manifest.permission.WRITE_CALENDAR,
//            /*照相机*/
//            Manifest.permission.CAMERA,
//            /*联系人*/
//            Manifest.permission.READ_CONTACTS,
//            Manifest.permission.WRITE_CONTACTS,
//            Manifest.permission.GET_ACCOUNTS,
//            /*位置*/
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            /*麦克风*/
            Manifest.permission.RECORD_AUDIO,
            /*手机*/
            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.CALL_PHONE,
//            Manifest.permission.READ_CALL_LOG,
//            Manifest.permission.WRITE_CALL_LOG,
//            Manifest.permission.ADD_VOICEMAIL,
//            Manifest.permission.USE_SIP,
//            Manifest.permission.PROCESS_OUTGOING_CALLS,
//            /*传感器*/
//            Manifest.permission.BODY_SENSORS,
//            /*短信*/
//            Manifest.permission.SEND_SMS,
//            Manifest.permission.RECEIVE_SMS,
//            Manifest.permission.READ_SMS,
//            Manifest.permission.RECEIVE_WAP_PUSH,
//            Manifest.permission.RECEIVE_MMS,
             /*存储卡*/
            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    // 声明一个集合，在后面的代码中用来存储用户拒绝授权的权
    List<String> mPermissionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        noPermissions = false;
        String userId = DBManager.userId(context);
        if (StringUtil.isNotNull(userId) && StringUtil.isNotNull(new BindingManager().getMachineID(getApplicationContext()))) {
            this.finish();
            /** 调用开机上报 */
            SystemClock.sleep(50);
            Intent intent = new Intent(BindingActivity.this, AdvertisingActivity.class);
            startActivity(intent);
            Intent handlerIntent = new Intent(context, HandlerService.class);
            handlerIntent.setAction(Resources.open_machine);
            context.startService(handlerIntent);

            SystemClock.sleep(250);

            /** 调用保障机制 */
            Intent service = new Intent(getApplicationContext(), GuaranteeService.class);
            getApplicationContext().startService(service);
        }else{
            setContentView(R.layout.activity_binding);
            initView();
            initPermission();
        }
    }
    private void initPermission() {
        mPermissionList.clear();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(BindingActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
            new MachineUtil().init_machine(getApplicationContext());
            machine_ID.setText(new BindingManager().getMachineID(context));
        } else {//请求权限方法
            String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(BindingActivity.this, permissions, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.w("happy", String.valueOf(requestCode));
        if (requestCode==1){
            for (int i = 0; i < permissions.length; i++) {
                Log.w("happy", String.valueOf(grantResults[i]));
                if (grantResults[i] == -1){
                    noPermissions = true;
                }
            }
        }
        if(noPermissions){
            this.finish();
        }else{
            new MachineUtil().init_machine(getApplicationContext());
            machine_ID.setText(new BindingManager().getMachineID(context));
        }
    }

    private void initView() {
        machine_ID = (TextView) findViewById(R.id.machine_ID);
//        machine_ID.setText(new BindingManager().getMachineID(context));

        bt_username_clear = (Button) findViewById(R.id.bt_username_clear);
        bt_username_clear.setOnClickListener(this);

        bt_pwd_clear = (Button) findViewById(R.id.bt_pwd_clear);
        bt_pwd_clear.setOnClickListener(this);

        bt_pwd_eye = (Button) findViewById(R.id.bt_pwd_eye);
        bt_pwd_eye.setOnClickListener(this);

        binding = (Button) findViewById(R.id.binding);
        binding.setOnClickListener(this);

        // 监听文本框内容变化
        username = (EditText) findViewById(R.id.username);
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String user = username.getText().toString().trim();// 获得文本框中的用户
                if ("".equals(user)) {// 用户名为空,设置按钮不可见
                    bt_username_clear.setVisibility(View.INVISIBLE);
                } else {// 用户名不为空，设置按钮可见
                    bt_username_clear.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 监听文本框内容变化
        password = (EditText) findViewById(R.id.password);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pwd = password.getText().toString().trim();// 获得文本框中的密码
                if ("".equals(pwd)) {// 密码为空,设置按钮不可见
                    bt_pwd_clear.setVisibility(View.INVISIBLE);
                } else {// 密码不为空，设置按钮可见
                    bt_pwd_clear.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    @SuppressLint("NewApi")
    public void onClick(View v) {
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        switch (v.getId()) {
            case R.id.bt_username_clear:
                // 清除登录名
                username.setText("");
                break;
            case R.id.bt_pwd_clear:
                // 清除密码
                password.setText("");
                break;
            case R.id.bt_pwd_eye:
                // 密码可见与不可见的切换
                if (isOpen) {
                    isOpen = false;
                } else {
                    isOpen = true;
                }
                // 默认isOpen是false,密码不可见
                changePwdOpenOrClose(isOpen);
                break;
            case R.id.binding:
                String csrf = OkHttp3Manager.getInstance(context).get_token(context, Resources.get_token);
                Log.w("happy","csrf:"+csrf);
                new BindingManager().setCsrf(context, csrf);
                boolean success = new InitMachineAction().initMachine(context,username.getText().toString(), password.getText().toString());
                if (success) {
                    ToastManager.getInstance().showToast(context, "绑定成功", Toast.LENGTH_LONG);

                    /** 调用开机上报 */
                    Intent handlerIntent = new Intent(context, HandlerService.class);
                    handlerIntent.setAction(Resources.open_machine);
                    context.startService(handlerIntent);

                    SystemClock.sleep(150);

                    /** 调用保障机制 */
                    Intent service = new Intent(getApplicationContext(), GuaranteeService.class);
                    getApplicationContext().startService(service);

                    Intent intent = new Intent(BindingActivity.this, AdvertisingActivity.class);
                    startActivity(intent);
                    this.finish();
                } else {
                    ToastManager.getInstance().showToast(context, "绑定失败，请联系客服人员", Toast.LENGTH_LONG);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 密码可见与不可见的切换
     */
    private void changePwdOpenOrClose(boolean flag) {
        // 第一次过来是false，密码不可见
        if (flag) {
            // 密码可见
            bt_pwd_eye.setBackgroundResource(R.drawable.login_password_open);
            // 设置EditText的密码可见
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            // 密码不接见
            bt_pwd_eye.setBackgroundResource(R.drawable.login_password_close);
            // 设置EditText的密码隐藏
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }



}
