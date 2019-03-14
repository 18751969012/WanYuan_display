package com.njust.wanyuan_display.manager;

import android.content.Context;
import android.content.Intent;

import org.apache.log4j.Logger;

import java.io.PrintWriter;

/**
 * 机器管理类
 */
public class MachineManager {
	
	private static final Logger log = Logger.getLogger(MachineManager.class);
	
	/**
	 * 同步机器时间
	 */
	public void synctime(Context context, String syncTime) {
		Intent intent = new Intent();
		intent.setAction("ACTION_UPDATE_TIME");
		intent.putExtra("cmd", syncTime);
		context.sendBroadcast(intent, null);
	}
	
	/**
	 * 机器关机
	 */
	public void shutdown(Context context) {
		Intent intent = new Intent();
		intent.setAction("ACTION_RK_SHUTDOWN");
		context.sendBroadcast(intent, null);
	}
	
	/**
	 * 机器重启
	 */
	public boolean restart() {
		PrintWriter PrintWriter = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("su -c /system/bin/reboot");
			PrintWriter = new PrintWriter(process.getOutputStream());
			PrintWriter.flush();
			PrintWriter.close();
			int value = process.waitFor();
			log.info("restart:" + value);
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
	
}
