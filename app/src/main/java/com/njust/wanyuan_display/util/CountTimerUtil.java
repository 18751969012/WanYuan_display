package com.njust.wanyuan_display.util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

import com.njust.wanyuan_display.activity.AdvertisingActivity;


/**
 * 自定义一个定时器CountTimer继承CountDownTimer,实现长时间无操作退回广告页面
 */
public class CountTimerUtil extends CountDownTimer {

	private Context context;

	/**
	 * 参数 millisInFuture       倒计时总时间（如60S，120s等）
	 * 参数 countDownInterval    渐变时间（每次倒计1s）
	 */
	public CountTimerUtil(long millisInFuture, long countDownInterval, Context context) {
		super(millisInFuture, countDownInterval);
		this.context=context;
	}

	// 计时完毕时触发
	@Override
	public void onFinish() {
//		((Activity)context).finish();
		Intent intentAdvertisingActivity = new Intent(context, AdvertisingActivity.class);
		intentAdvertisingActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		((Activity)context).startActivity(intentAdvertisingActivity);
	}
	// 计时过程显示
	@Override
	public void onTick(long millisUntilFinished) {

	}
	
}
