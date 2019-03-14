package com.njust.wanyuan_display.util;

import android.content.Context;
import android.util.DisplayMetrics;

import java.text.DecimalFormat;

/**
 * 屏幕工具类
 */
public class ScreenUtil {
	/**
	 * 取得屏幕的宽度
	 */
	public static String screenWidth(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return String.valueOf(dm.widthPixels);
	}
	/**
	 * 取得屏幕的高度
	 */
	public static String screenHeight(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return String.valueOf(dm.heightPixels);
	}
	/**
	 * 取得屏幕密度：0.75/1.0 /1.5
	 */
	public static String getDensity(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return String.valueOf(dm.density);
	}
	/**
	 * 取得屏幕密度DPI：120/160/240
	 */
	public static String screenDpi(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return String.valueOf(dm.densityDpi);
	}
	/**
	 * 判断是否为平板
	 */
	public static boolean isPad(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		float density = metrics.density;
		float xdpi = metrics.xdpi;
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		double z = Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
		double f = (z / (xdpi * density));
		DecimalFormat decimal = new DecimalFormat("#.#");
		double screenInches = Double.valueOf(decimal.format(f));
		if ( screenInches>= 6.0) {
			return true;
		}
		return false;
	}

	/**
	 * 通过商品id和商品图片网址解析出商品图片本地保存名，保证唯一
	 */
	public static String getGoodImageName(String goodID, String url) {
		if ( StringUtil.isNotNull(goodID) ) {
			return goodID + "-" +
					url.substring(url.lastIndexOf("/") + 1).replace(" ", "")
					.replace("!", "").replace("@", "").replace("#", "").replace("$", "").replace("&", "")
					.replace("(", "").replace(")", "").replace("=", "").replace(";", "").replace("+", "").replace("'", "");
		} else {
			return url.substring(url.lastIndexOf("/") + 1).replace(" ", "")
					.replace("!", "").replace("@", "").replace("#", "").replace("$", "").replace("&", "")
					.replace("(", "").replace(")", "").replace("=", "").replace(";", "").replace("+", "").replace("'", "");
		}
	}
	
}
