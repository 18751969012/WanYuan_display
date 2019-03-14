package com.njust.wanyuan_display.util;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import com.alibaba.fastjson.JSONObject;

import org.apache.log4j.Logger;

import java.util.List;

/**
 * 网络工具类
 */
public class NetWorkUtil {
	
	private static final Logger log = Logger.getLogger(NetWorkUtil.class);

	/**
	 * 判断网络是否已打开
	 */
	public static boolean netIsOpen(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected())
			{
				// 当前网络是连接的
				if (networkInfo.isAvailable() /*networkInfo.getState() == NetworkInfo.State.CONNECTED*/)
				{
					// 当前所连接的网络可用
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * wifi是否打开
	 */
	public static boolean isWifiEnabled(Context context) {
		ConnectivityManager mgrConn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mgrTel = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
				.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
				.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
	}

	/**
	 * 判断是否是wifi连接
	 */
	public static boolean checkWifiState(Context context) {
		boolean isWifiConnect = true;
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
		for (int i = 0; i < networkInfos.length; i++) {
			if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
				if (networkInfos[i].getType() == cm.TYPE_MOBILE) {
					isWifiConnect = false;
				}
				if (networkInfos[i].getType() == cm.TYPE_WIFI) {
					isWifiConnect = true;
				}
			}
		}
		return isWifiConnect;
	}

	/**
	 * WIFI信息
	 */
	public static JSONObject wifiInfo(Context context) {
		JSONObject wifiInfo = new JSONObject();
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiManager.getConnectionInfo();
		if (info.getBSSID() != null) {
			wifiInfo.put("sign", info.getRssi());//RSSI就是接受信号强度指示
			wifiInfo.put("speed", info.getLinkSpeed() + WifiInfo.LINK_SPEED_UNITS);
			wifiInfo.put("ssid", info.getSSID());
			wifiInfo.put("macAddress", info.getMacAddress());
			wifiInfo.put("ipAddress", info.getIpAddress());
			wifiInfo.put("networkId", info.getNetworkId());
		}
		return wifiInfo;
	}


	/**
	 * Gps是否打开
	 */
	public static boolean isGpsEnabled(Context context) {
		LocationManager locationManager = ((LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE));
		List<String> accessibleProviders = locationManager.getProviders(true);
		return accessibleProviders != null && accessibleProviders.size() > 0;
	}


	/**
	 * ping域名是否通
	 */
	public static boolean ping(String domain) {
		try {
			Process process = Runtime.getRuntime().exec("ping -c 1 -w 100 " + domain);
			int status = process.waitFor();
			log.info("ping:" + domain + " " + status);
			
			if (status == 0) {
				return true;
			} else {
				return false;
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	

	
}
