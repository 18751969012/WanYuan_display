package com.njust.wanyuan_display.util;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;

import com.njust.wanyuan_display.activity.AdvertisingActivity;
import com.njust.wanyuan_display.broadcast.BootBroadcastReceiver;

import static android.content.Context.ALARM_SERVICE;


/**
 * 自定义一个闹钟设置函数，用于设定定时闹钟，周期性执行长时间定时任务
 */
public class AlarmUtils {
	/**
	 * 设置定时任务，在广播里响应处理
	 */
	public static void setAlarmManager(Context context){
		try {
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
			Intent alarmIntent = new Intent(context, BootBroadcastReceiver.class).setAction("intent.AlarmManager");
			PendingIntent broadcast = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);//通过广播接收
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				//参数2是开始时间、参数3是允许系统延迟的时间
				alarmManager.setWindow(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60*60*1000, 60*1000, broadcast);
			} else {
				//参数2是首次执行、参数3是闹钟两次执行的间隔时间
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60*60*1000, 60*60*1000,broadcast);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
