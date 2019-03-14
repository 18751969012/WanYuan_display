package com.njust.wanyuan_display.application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.njust.wanyuan_display.activity.BindingActivity;
import com.njust.wanyuan_display.broadcast.BootBroadcastReceiver;
import com.njust.wanyuan_display.config.ConfigLog4J;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.util.AlarmUtils;
import com.njust.wanyuan_display.util.DateUtil;

import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

//import com.njust.wanyuan_display.util.MachineUtil;
//import com.njust.wanyuan_display.util.ScreenUtil;
//import com.njust.wanyuan_display.util.StringUtil;


public class VMApplication extends Application {
	
	private final Logger log = Logger.getLogger(VMApplication.class);
	
	private static List<Activity> activityList = new ArrayList<Activity>();
	
	protected static VMApplication application;

	public static boolean VMMainThreadFlag = true;
	public static boolean VMMainThreadRunning = true;
	public static boolean mQuery0Flag = true; //查询中柜、左柜、右柜机器状态
	public static boolean mQuery1Flag = true;
	public static boolean mQuery2Flag = true;
	public static boolean mUpdataDatabaseFlag = true;//更新数据库
	public static boolean OutGoodsThreadFlag = false;//主出货线程的while标志
	public static int rimZNum1 = 5;
	public static int rimZNum2 = 5;
	public static int aisleZNum1 = 5;
	public static int aisleZNum2 = 5;
	public static int midZNum = 5;

	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
		activityList = new ArrayList<Activity>();
		try {
//			/* 崩溃日志 */
//			MyCrashHandler myCrashHandler = MyCrashHandler.getInstance();
//			myCrashHandler.init(getApplicationContext());
			/** 初始化日志 */
			ConfigLog4J.configure();
			Log.w("happy", "VMApplication77");
//			new MachineUtil().init_machine(getApplicationContext());
			Log.w("happy", "VMApplication79");
			Thread.setDefaultUncaughtExceptionHandler(restartHandler); // 程序崩溃时触发线程，以下用来捕获程序崩溃异常
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	// 创建服务用于捕获崩溃异常
	private UncaughtExceptionHandler restartHandler = new UncaughtExceptionHandler() {
		public void uncaughtException(Thread thread, Throwable ex) {
			saveCrashLog(ex);
			restartApp();
		}
	};
	
	/**
	 * 保存错误信息
	 */
	private void saveCrashLog(Throwable ex) {
		FileOutputStream fos = null;
		try {
			StringBuffer sb = new StringBuffer();
			
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			ex.printStackTrace(printWriter);
			Throwable cause = ex.getCause();
			while (cause != null) {
				cause.printStackTrace(printWriter);
				cause = cause.getCause();
			}
			printWriter.close();
			String result = writer.toString();
			sb.append(result);
			
			log.error("saveCrashLog:" + sb.toString());
			
			fos = new FileOutputStream(Resources.logURL() + DateUtil.getCurrentDate() + "crash.log");
			fos.write(sb.toString().getBytes());
			
		} catch (Exception e) {
			log.error("saveCrashLog:" + e.getMessage());
		} finally {
			if (fos!=null) {
				try {
					fos.close();
					fos=null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 发生崩溃异常时,重启应用
	 */
	public void restartApp() {
		log.info("myApplication restart app");

		Intent intent = new Intent(this, BindingActivity.class);
		PendingIntent restartIntent = PendingIntent.getActivity(application.getApplicationContext(), 0, intent, 0);

		// 退出程序
		AlarmManager mgr = (AlarmManager) application.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
		
		removeAllActivity();

		android.os.Process.killProcess(android.os.Process.myPid()); // 结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
	}
	
	/**
	 * 单例模式中获取唯一的MyApplication实例
	 */
	public static VMApplication getInstance() {
		try {
			if (null == application) {
				application = new VMApplication();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return application;
	}
	
	/**
	 * 添加Activity到容器中
	 */
	public void addActivity(Activity activity) {
		try {
			if (activityList != null && activityList.size() > 0) {
				if (!activityList.contains(activity)) {
					activityList.add(activity);
				}
			} else {
				activityList.add(activity);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/**
     * 移除Activity
     */
    public void removeActivity(Activity activity) {
        try {
			if (activityList.contains(activity)) {
				activityList.remove(activity.getClass());
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
    }

	/**
	 * 遍历所有Activity并finish
	 */
	public void removeAllActivity() {
		try {
			if (activityList != null && activityList.size() > 0) {
				for (Activity activity : activityList) {
					activity.finish();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public void onLowMemory() {
		try {
			log.info("application low memory");
			super.onLowMemory();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	@Override
	public void onTrimMemory(int level) {
		try {
			log.info("application memory:" + level);
			super.onTrimMemory(level);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}



}
