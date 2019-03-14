package com.njust.wanyuan_display.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.njust.wanyuan_display.util.CountTimerUtil;

/**
 * 封装最基本的activity，重写dispatchTouchEvent，实现长时间无动作跳转到广告界面和点击空白退出软键盘
 */
public class BaseDispatchTouchActivity extends BaseActivity{
    private CountTimerUtil countTimerView;
    private boolean isAction = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    private void timeStart(){
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                countTimerView.start();
            }
        });
    }
    private void init() {
        //初始化CountTimer，设置倒计时为两分钟。
        countTimerView=new CountTimerUtil(120000,1000,BaseDispatchTouchActivity.this){
            // 计时完毕时触发
            @Override
            public void onFinish() {
                super.onFinish();
            }
            // 计时过程显示
            @Override
            public void onTick(long millisUntilFinished) {
                super.onTick(millisUntilFinished);
                onTickTime(millisUntilFinished);
            }
        };
    }

    public void onTickTime(long millisUntilFinished){
    }

    public void reClock(){//重新计时
        countTimerView.cancel();
        countTimerView.start();
    }

    public void setAction(boolean action){
        isAction = action;
    }

    @Override
    protected void onPause() {
        super.onPause();
        countTimerView.cancel();
    }
    @Override
    protected void onResume() {
        super.onResume();
        timeStart();
    }
    /**
     * 主要的方法，重写dispatchTouchEvent
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(isAction){
            switch (ev.getAction()){
                //获取触摸动作，如果ACTION_UP，计时开始。
                case MotionEvent.ACTION_UP:
                    countTimerView.start();
                    break;
                //否则其他动作计时取消
                default:countTimerView.cancel();
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }




}

