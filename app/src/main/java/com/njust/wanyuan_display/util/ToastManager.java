package com.njust.wanyuan_display.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Toast管理工具
 */
public class ToastManager {
	/**
	 * 单例模式控制访问对象有且只有一个
	 */
	private ToastManager() {}

	private static ToastManager toastManager = null;

	public synchronized static ToastManager getInstance() {
		if (toastManager == null) {
			toastManager = new ToastManager();
		}
		return toastManager;
	}

	private static Toast mToast;

	public void showToast(Context context, String msg, int duration) {
		if (mToast == null) {
			mToast = Toast.makeText(context, msg, duration);
			mToast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			mToast.setText(msg);
		}
		mToast.show();
	}

}
