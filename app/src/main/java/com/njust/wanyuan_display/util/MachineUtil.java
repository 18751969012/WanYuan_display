package com.njust.wanyuan_display.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.njust.wanyuan_display.config.Constant;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.manager.BindingManager;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 设备工具类
 */
public class MachineUtil {

	private final Logger log = Logger.getLogger(MachineUtil.class);

	/**
	 * 初始化设备
	 */
	public boolean init_machine(Context context) {
		boolean flag = false;
		try {
//			/** 第一次使用系统则复制系统数据库 */
//			FileUtil.copyDBToData(context, "vm.db", CacheDBUtil.DB_VM);

			init_config(context);

			String MachineID = new BindingManager().getMachineID(context);

			if ( StringUtil.isNull(MachineID) ) {
				/** 生成设备号 */
				new BindingManager().setMachineID(context,"1");
				/** 初始化设备信息 */
				DBManager.saveMachineInfo(context);
				DBManager.initialMachineState(context);
				DBManager.initialSettingInfo(context);
				DBManager.initialAisleInfo(context);
				createFile(context);
			}
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return flag;
	}

	/**
	 * 初始化配置
	 */
	public void init_config(Context context) {
		String mergeGoods = new BindingManager().getMergeGoods(context);
		if (StringUtil.isNull(mergeGoods)) {
			new BindingManager().setMergeGoods(context, String.valueOf(Constant.no));
		}
	}

	public void createFile(Context context) {
		File advDir = new File(Resources.advURL());
		if ( !advDir.exists() ) {
			advDir.mkdirs();
		}

		File adv_localDir = new File(Resources.adv_localURL());
		if ( !adv_localDir.exists() ) {
			adv_localDir.mkdirs();
		}

		File appDir = new File(Resources.appURL());
		if ( !appDir.exists() ) {
			appDir.mkdirs();
		}

		File logDir = new File(Resources.logURL());
		if ( !logDir.exists() ) {
			logDir.mkdirs();
		}

		File dbDir = new File(Resources.dbURL());
		if ( !dbDir.exists() ) {
			dbDir.mkdirs();
		}

		File goodsDir = new File(Resources.goodsURL());
		if ( !goodsDir.exists() ) {
			goodsDir.mkdirs();
		}

		File xmlDir = new File(Resources.xmlURL());
		if ( !xmlDir.exists() ) {
			xmlDir.mkdirs();
		}

		File QRcodeDir = new File(Resources.QRcodeURL());
		if ( !QRcodeDir.exists() ) {
			QRcodeDir.mkdirs();
		}
		FileUtil.copyToStorage(context, "background.png", Resources.advURL()+"background.png");
//		FileUtil.copyToStorage(context, "2.jpg", Resources.advURL()+"2.jpg");
//		FileUtil.copyToStorage(context, "3.jpg", Resources.advURL()+"3.jpg");
//		FileUtil.copyToStorage(context, "4.jpg", Resources.advURL()+"4.jpg");
//		FileUtil.copyToStorage(context, "5.jpg", Resources.advURL()+"5.jpg");
//		FileUtil.copyToStorage(context, "6.jpg", Resources.advURL()+"6.jpg");
//		FileUtil.copyToStorage(context, "7.mp4", Resources.advURL()+"7.mp4");
		FileUtil.copyToStorage(context, "11.png", Resources.goodsURL()+"11.png");
		FileUtil.copyToStorage(context, "12.png", Resources.goodsURL()+"12.png");
		FileUtil.copyToStorage(context, "13.png", Resources.goodsURL()+"13.png");
		FileUtil.copyToStorage(context, "14.png", Resources.goodsURL()+"14.png");
		FileUtil.copyToStorage(context, "15.png", Resources.goodsURL()+"15.png");
		FileUtil.copyToStorage(context, "16.png", Resources.goodsURL()+"16.png");
		FileUtil.copyToStorage(context, "17.png", Resources.goodsURL()+"17.png");
		FileUtil.copyToStorage(context, "18.png", Resources.goodsURL()+"18.png");
		FileUtil.copyToStorage(context, "19.png", Resources.goodsURL()+"19.png");
		FileUtil.copyToStorage(context, "20.png", Resources.goodsURL()+"20.png");
		FileUtil.copyToStorage(context, "21.png", Resources.goodsURL()+"21.png");
		FileUtil.copyToStorage(context, "22.png", Resources.goodsURL()+"22.png");
		FileUtil.copyToStorage(context, "23.png", Resources.goodsURL()+"23.png");
		FileUtil.copyToStorage(context, "24.png", Resources.goodsURL()+"24.png");
		FileUtil.copyToStorage(context, "25.png", Resources.goodsURL()+"25.png");
		FileUtil.copyToStorage(context, "26.png", Resources.goodsURL()+"26.png");
		FileUtil.copyToStorage(context, "27.png", Resources.goodsURL()+"27.png");
		FileUtil.copyToStorage(context, "28.png", Resources.goodsURL()+"28.png");
	}

//	/**
//	 * 获得设备号
//	 */
//	public String getPhonenumber(Context context) {
//		TelephonyManager tm = (TelephonyManager)
//		context.getSystemService(Context.TELEPHONY_SERVICE);
//		return tm.getLine1Number();
//    }
	/**
	 * 获得sim卡号
	 */
	public String getIMSI(Context context) {
		TelephonyManager tm = (TelephonyManager)
		context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSubscriberId();
    }
//	/**
//	 * 获得IMEI
//	 */
//	public String getIMEI(Context context) {
//		TelephonyManager tm = (TelephonyManager)
//		context.getSystemService(Context.TELEPHONY_SERVICE);
//		return tm.getDeviceId();
//    }
//	/**
//	 * 获得ICCID
//	 */
//	public String getICCID(Context context) {
//		TelephonyManager tm = (TelephonyManager)
//		context.getSystemService(Context.TELEPHONY_SERVICE);
//		return tm.getSimSerialNumber();
//    }
	/**
	 * 获得SimCountryIso
	 */
	public String getSimCountryIso(Context context) {
		TelephonyManager tm = (TelephonyManager)
		context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimCountryIso();
    }
	/**
	 * 获得SimOperator
	 */
	public String getSimOperator(Context context) {
		TelephonyManager tm = (TelephonyManager)
		context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimOperator();
    }
	/**
	 * 获得设备版本号
	 */
	public String deviceId() {
		return Build.ID;
    }
	/**
	 * 获得主板号
	 */
	public String board() {
		return Build.BOARD;
    }
	/**
	 * 获得主板品牌
	 */
	public String brand() {
		return Build.BRAND;
    }
	/**
	 * 获得产品名称
	 */
	public String product() {
		return Build.PRODUCT;
    }
	/**
	 * 获得硬件序列号
	 */
	@SuppressLint("NewApi")
	public String serial() {
		return Build.SERIAL;
	}
	/**
	 * 获得安卓版本
	 */
	public String androidversion() {
		return Build.VERSION.RELEASE;
	}
	/**
	 * 获得安卓SDK
	 */
	public String androidsdk() {
		return String.valueOf(Build.VERSION.SDK_INT);
	}
	/**
	 * 获得制造商
	 */
	public String manufacturer() {
		return String.valueOf(Build.MANUFACTURER);
	}
	/**
	 * 获得型号
	 */
	public String model() {
		return String.valueOf(Build.MODEL);
	}
	/**
	 * 获得当前版本
	 */
	public String apkVersion(Context context) {
		String version = "";
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			log.error(e.getMessage());

		}
		return version;
	}
	/**
	 * 获得当前版本
	 */
	public String apkPackageName(Context context) {
		String packageName = "";
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			packageName = packInfo.packageName;
		} catch (NameNotFoundException e) {
			log.error(e.getMessage());

		}
		return packageName;
	}
	/**
	 * 判断手机是否有root权限
	 */
	public boolean hasRootPerssion() {
		PrintWriter PrintWriter = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("su");
			PrintWriter = new PrintWriter(process.getOutputStream());
			PrintWriter.flush();
			PrintWriter.close();
			int value = process.waitFor();
			return value==0;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
		return false;
	}
	
	/**
	 * 获取安装的桌面程序
	 */
	public List<String> getLaunchers(Context context) {
		List<String> packageNames = new ArrayList<String>();
		PackageManager packageManager = context.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		
		List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		
		for (ResolveInfo resolveInfo : resolveInfos) {
			ActivityInfo activityInfo = resolveInfo.activityInfo;
			if (activityInfo != null) {
				packageNames.add(resolveInfo.activityInfo.processName);
				packageNames.add(resolveInfo.activityInfo.packageName);
			}
		}
		return packageNames;
	} 
	
	/**
     * 判断某个界面是否在前台 
     * @param context   Context 
     * @param className 界面的类名 
     * @return 是否在前台显示 
     */
	public static boolean isForeground(Context context, String className) {
		if (context == null || StringUtil.isNull(className)) 
			return false;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = null;
		if (am != null) {
			list = am.getRunningTasks(1);
		}
		if (list != null) {
			for (RunningTaskInfo taskInfo : list) {
                if (taskInfo.topActivity.getShortClassName().contains(className)) { // 说明它已经启动了
                    return true;
                }
            }
		}
		return false;
	}
	
	/**
	 * 判断当前界面是否是桌面
	 */
	public boolean isHome(Context context) {
		boolean isLauncherForeground = false;
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<String> lanuchers = getLaunchers(context);
		
		for (int i = 0; i < lanuchers.size(); i++) {
			log.info("home package:" +  lanuchers.get(i));
		}


		List<RunningTaskInfo> runningTaskInfos = null;
		if (activityManager != null) {
			runningTaskInfos = activityManager.getRunningTasks(1);
		}

		if (runningTaskInfos != null && StringUtil.equals(runningTaskInfos.get(0).baseActivity.getPackageName(), new MachineUtil().apkPackageName(context))) {
			log.info("taskId:" + runningTaskInfos.get(0).id);

			new BindingManager().setTaskId(context, String.valueOf(runningTaskInfos.get(0).id));
		}

		if (runningTaskInfos != null) {
			log.info("runningTaskInfos:" + runningTaskInfos.get(0).baseActivity.getPackageName());
		}

		if (runningTaskInfos != null && lanuchers.contains(runningTaskInfos.get(0).baseActivity.getPackageName())) {
			isLauncherForeground = true;
		}

		return isLauncherForeground;
	}
	
}
