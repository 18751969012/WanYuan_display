package com.njust.wanyuan_display.util;
/**
 * String类工具类
 */
public class StringUtil {
	/**
	 * 判断字符串是否为空
	 */
	public static boolean isNull(String str) {
		return str==null || str.length()<=0;
	}
	/**
	 * 判断字符串是否不为空
	 */
	public static boolean isNotNull(String str) {
		return !isNull(str);
	}
	/**
	 * 字符串如果为空则返回空串
	 */
	public static String str(String str) {
		return isNotNull(str) ?str :"";
	}
	/**
	 * 比较两个字符串是否相等
	 */
	public static boolean equals(String str1, String str2) {
		return isNotNull(str1) && isNotNull(str2) && str1.trim().equals(str2.trim());
	}
	/**
	 * 判断字符串是否全为数字
	 */
	public static boolean isNumber(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 通过商品id和商品图片网址获取商品本地图片的名字，保证唯一
	 */
	public static String getGoodImageName(int goodID, String url) {
		if (goodID>=0) {
			return String.valueOf(goodID) + "-" +
					url.substring(url.lastIndexOf("/") + 1).replace(" ", "")
					.replace("!", "").replace("@", "").replace("#", "").replace("$", "").replace("&", "")
					.replace("(", "").replace(")", "").replace("=", "").replace(";", "").replace("+", "").replace("'", "");
		} else {
			return url.substring(url.lastIndexOf("/") + 1).replace(" ", "")
					.replace("!", "").replace("@", "").replace("#", "").replace("$", "").replace("&", "")
					.replace("(", "").replace(")", "").replace("=", "").replace(";", "").replace("+", "").replace("'", "");
		}
	}
	/**
	 * 通过广告id和广告下载网址获取广告本地保存的名字，保证唯一
	 */
	public static String getAdvName(int advID, String url) {
		if (advID>=0) {
			return String.valueOf(advID) + "-" +
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
