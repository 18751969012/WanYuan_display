package com.njust.wanyuan_display.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.njust.wanyuan_display.activity.BindingActivity;
import com.njust.wanyuan_display.util.MachineUtil;
import com.njust.wanyuan_display.util.StringUtil;

import org.apache.log4j.Logger;


/**
 * 广播接收
 */
public class StartupReceiver extends BroadcastReceiver {
	
	private final Logger log = Logger.getLogger(StartupReceiver.class);
	
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
				String packageName = intent.getData().getSchemeSpecificPart();
				log.info("安装成功" + packageName + "-----" + new MachineUtil().apkPackageName(context));
				
				if ( StringUtil.equals(packageName, new MachineUtil().apkPackageName(context)) ) {
					startApp(context);	
				}
			} else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
				String packageName = intent.getData().getSchemeSpecificPart();
				log.info("卸载成功" + packageName + "-----" + new MachineUtil().apkPackageName(context));
			} else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
				String packageName = intent.getData().getSchemeSpecificPart();
				log.info("升级成功" + packageName + "-----" + new MachineUtil().apkPackageName(context));
				
				if ( StringUtil.equals(packageName, new MachineUtil().apkPackageName(context)) ) {
					startApp(context);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	/**
     * 监测到升级后执行app的启动
      */
	public void startApp(Context context) {
		try {
			Intent bootStartIntent = new Intent(context, BindingActivity.class);
			bootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(bootStartIntent);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
}
