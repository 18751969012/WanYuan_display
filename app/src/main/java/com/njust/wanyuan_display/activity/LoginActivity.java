package com.njust.wanyuan_display.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.njust.wanyuan_display.R;

import java.lang.reflect.Method;

public class LoginActivity extends BaseExitKeyboardActivity implements View.OnClickListener {

    private EditText username, password;
    private Button bt_username_clear;
    private Button bt_pwd_clear;
    private Button bt_pwd_eye;
    private Button login,return_shop,return_desktop;
    private boolean isOpen = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        bt_username_clear = (Button) findViewById(R.id.bt_username_clear);
        bt_username_clear.setOnClickListener(this);

        bt_pwd_clear = (Button) findViewById(R.id.bt_pwd_clear);
        bt_pwd_clear.setOnClickListener(this);

        bt_pwd_eye = (Button) findViewById(R.id.bt_pwd_eye);
        bt_pwd_eye.setOnClickListener(this);

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);

        return_shop = (Button) findViewById(R.id.return_shop);
        return_shop.setOnClickListener(this);

        return_desktop = (Button) findViewById(R.id.return_desktop);
        return_desktop.setOnClickListener(this);

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
    public void onClick(View v) {
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
            case R.id.login:
                Intent intent = new Intent(LoginActivity.this, ManagerActivity.class);
                startActivity(intent);
                this.finish();
                break;
            case R.id.return_shop:
                Intent intentReturnShop = new Intent(LoginActivity.this, AdvertisingActivity.class);
                startActivity(intentReturnShop);
                this.finish();
                break;
            case R.id.return_desktop:
                Intent intentBacktodesktop = new Intent();
                intentBacktodesktop.setAction(Intent.ACTION_MAIN);
                intentBacktodesktop.addCategory(Intent.CATEGORY_HOME);
                startActivity(intentBacktodesktop);
                displayBottomNavibar(true);
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
