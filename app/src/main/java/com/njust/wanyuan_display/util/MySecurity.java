package com.njust.wanyuan_display.util;

import android.annotation.SuppressLint;

import java.security.MessageDigest;

/**
 * 加密算法
 */
public class MySecurity {
	
	public static final String SHA_1 = "SHA-1";
	
	public static final String MD5 = "MD5";
	
	@SuppressLint("DefaultLocale")
	public static String md5(String strSrc) {
		return encode(strSrc, MD5).toUpperCase();
	}
	
	public static String encode(String strSrc, String encodeType) {
		MessageDigest md = null;
		String strDes = null;
		try {
			byte[] bt = strSrc.getBytes("UTF-8");
			if (encodeType == null || "".equals(encodeType)) {
				encodeType = MD5;//默认使用MD5
			}
			md = MessageDigest.getInstance(encodeType);
			md.update(bt);
			strDes = bytes2Hex(md.digest());
		} catch (Exception e) {
			return strSrc;
		}
		return strDes;
	}
	
	public static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}
	
	/**
	 * 验证签名。
	 */
	@SuppressLint("DefaultLocale")
	public static String getSignature(String sKey) throws Exception {
		String ciphertext = null;
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] digest = md.digest(sKey.toString().getBytes());
		ciphertext = byteToStr(digest);
		return ciphertext.toLowerCase();
	}

	/**
	 * 将字节数组转换为十六进制字符串
	 */
	private static String byteToStr(byte[] byteArray) {
		String strDigest = "";
		for (int i = 0; i < byteArray.length; i++) {
			strDigest += byteToHexStr(byteArray[i]);
		}
		return strDigest;
	}

	/**
	 * 将字节转换为十六进制字符串
	 */
	private static String byteToHexStr(byte mByte) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] tempArr = new char[2];
		tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
		tempArr[1] = Digit[mByte & 0X0F];
		
		String s = new String(tempArr);
		return s;
	}
	
}