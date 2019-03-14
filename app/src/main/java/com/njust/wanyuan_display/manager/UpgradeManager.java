package com.njust.wanyuan_display.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.njust.wanyuan_display.config.Constant;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.util.MachineUtil;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;

/**
 * 升级管理器
 */
@SuppressLint("NewApi")
public class UpgradeManager {
	
	private final Logger log = Logger.getLogger(UpgradeManager.class);
	
	/**
	 * 下载APK文件
	 */
	public void downloadAPK(final Context context, final String downloadUrl) {
		DownloadManager.getInstance().download(context,-1, downloadUrl, Resources.app_filepath, Constant.app,"", new DownloadManager.OnDownloadListener() {
					@Override
					public void onDownloadSuccess() {
						String apkPath = Environment.getExternalStorageDirectory() + Resources.app_filepath + Resources.app_name;
						log.info("download success apkPath:" + apkPath);

						installAPK(context, apkPath);
					}
					@Override
					public void onDownloading(int progress) {

					}
					@Override
					public void onDownloadFailed() {
					}
				});
	}
	
	/**
     * 安装APK文件
     */
	private boolean installAPK(Context context, String apkPath) {
		// 先判断手机是否有root权限
		if (new MachineUtil().hasRootPerssion()) {
			installSlient(context, apkPath);
			return true;
		} else {
			// 没有root权限，利用意图进行安装
			File file = new File(apkPath);
			if (!file.exists())
				return false;
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
			context.startActivity(intent);
			return true;
		}
	}

	/**
	 * 有root权限，利用静默安装实现
	 */
	public void installSlient(Context context, String apkPath) {
		String cmd = "pm install -r " + apkPath;
		Process process = null;
		DataOutputStream os = null;
		BufferedReader successResult = null;
		BufferedReader errorResult = null;
		StringBuilder successMsg = null;
		StringBuilder errorMsg = null;
		try {
			// 静默安装需要root权限
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.write(cmd.getBytes());
			os.writeBytes("\n");
			os.writeBytes("exit\n");
			os.flush();
			// 执行命令
			process.waitFor();
			// 获取返回结果
			successMsg = new StringBuilder();
			errorMsg = new StringBuilder();
			successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
			errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String s;
			while ((s = successResult.readLine()) != null) {
				successMsg.append(s);
			}
			while ((s = errorResult.readLine()) != null) {
				errorMsg.append(s);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (process != null) {
					process.destroy();
				}
				if (successResult != null) {
					successResult.close();
				}
				if (errorResult != null) {
					errorResult.close();
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
}
