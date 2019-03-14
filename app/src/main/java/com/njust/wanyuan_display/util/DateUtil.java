package com.njust.wanyuan_display.util;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtil {
	private static DateFormat dateFormat = null;
	/**
	 * 返回时间字符串（NoyyyyMMddHHmmssSSS）,用来创建一个新的订单号
	 */
	@SuppressLint("SimpleDateFormat")
	public static String new_orderNo() {
		dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return "No" + dateFormat.format(new Date());
	}

	/**
	 * 创建一个新的测试订单号
	 */
	@SuppressLint("SimpleDateFormat")
	public static String test_new_orderNo() {
		dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return "TE" + dateFormat.format(new Date());
	}


	/**
	 * 返回时间字符串（MMddHHmmss）,用来生成machineID
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentTimeMachineID() {
		Date date = new Date();
		dateFormat = new SimpleDateFormat("MMddHHmmss");
		return dateFormat.format(date);
	}


	/**
	 * 返回时间字符串（yyyy-MM-dd HH:mm:ss）
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentTime() {
		Date date = new Date();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	return dateFormat.format(date);
    }
	
	/**
	 * 返回日期字符串（yyyy-MM-dd）
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentDate() {
		Date date = new Date();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	return dateFormat.format(date);
    }
	
	/**
	 * 返回时间字符串（yyyy-MM-dd-HH-mm-ss）
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getDBTime() {
		Date date = new Date();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    	return dateFormat.format(date);
    }
	
	/**
	 * 返回时间戳
	 */
	public static long getCurrentTime3() {
    	return System.currentTimeMillis();
    }

	/**
	 * String格式时间转换成（yyyy-MM-dd HH:mm:ss）
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date parsetime3(String timeStr) throws ParseException {
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.parse(timeStr);
	}
	/**
	 * String格式时间转换成（HH:mm:ss）
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date timeStringToDate(String timeStr) throws ParseException {
		dateFormat = new SimpleDateFormat("HH:mm:ss");
		return dateFormat.parse(timeStr);
	}
	/**
	 * Date格式时间转换成（yyyy-MM-dd HH:mm:ss）
	 */
	@SuppressLint("SimpleDateFormat")
	public static String time3(Date date) {
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}

	/**
	 * Date格式时间转换成（HHmm）
	 */
	@SuppressLint("SimpleDateFormat")
	public static String hour_minute(Date date) {
		dateFormat = new SimpleDateFormat("HHmm");
		return dateFormat.format(date);
	}
	
	/**
	 * 增加一秒
	 */
	public static String add1s(String timeStr) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(parsetime3(timeStr));
			calendar.add(Calendar.SECOND, 1);
			
			return DateUtil.time3(calendar.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DateUtil.time3(new Date());
	}
	/**
	 * 判断时间是否在时间段内
	 * @param time System.currentTimeMillis()
	 * @param strDateBegin 开始时间 00:00:00
	 * @param strDateEnd 结束时间 00:05:00
	 */
	public static boolean isInDate(long time, String strDateBegin, String strDateEnd) {
		Calendar calendar = Calendar.getInstance();
		// 处理开始时间
		String[] startTime = strDateBegin.split(":");
		calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(startTime[0]));
		calendar.set(Calendar.MINUTE, Integer.valueOf(startTime[1]));
		calendar.set(Calendar.SECOND, Integer.valueOf(startTime[2]));
		calendar.set(Calendar.MILLISECOND, 0);
		long startTimeL = calendar.getTimeInMillis();
		// 处理结束时间
		String[] endTime = strDateEnd.split(":");
		calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(endTime[0]));
		calendar.set(Calendar.MINUTE, Integer.valueOf(endTime[1]));
		calendar.set(Calendar.SECOND, Integer.valueOf(endTime[2]));
		calendar.set(Calendar.MILLISECOND, 0);
		long endTimeL = calendar.getTimeInMillis();
		return time >= startTimeL && time <= endTimeL;
	}

}
