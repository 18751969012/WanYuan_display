package com.njust.wanyuan_display.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * 封装最基本的activity
 */
public class BaseActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayBottomNavibar(false);
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

