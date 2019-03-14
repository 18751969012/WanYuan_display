package com.njust.wanyuan_display.listener;
/**
 * 异步回调函数
 */
public interface ReqCallBack {
	/**
	 * 响应成功
	 */
	public void onReqSuccess(String result);
	/**
	 * 响应失败
	 */
	public void onReqFailed(String errorMsg);
}
