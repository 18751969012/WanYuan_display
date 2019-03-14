package com.njust.wanyuan_display.db;

import android.app.Application;
import android.content.Context;

import com.njust.wanyuan_display.greenDao.DaoMaster;
import com.njust.wanyuan_display.greenDao.DaoMaster.DevOpenHelper;
import com.njust.wanyuan_display.greenDao.DaoSession;

/**
 * 将取得DaoMaster对象的方法放到Application层这样避免多次创建生成Session对象。
 */
public class CacheBaseApplication extends Application {
	
	private static CacheBaseApplication mInstance;
	private static DaoMaster daoMaster;
	private static DaoSession daoSession;

	@Override
	public void onCreate() {
		super.onCreate();
		if (mInstance == null) {
			mInstance = this;
		}
	}

	/**
	 * 取得DaoMaster
	 */
	public static DaoMaster getDaoMaster(Context context) {
		if (daoMaster == null) {
			DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, CacheDBUtil.DB_VM, null);
			daoMaster = new DaoMaster(helper.getWritableDatabase());
		}
		return daoMaster;
	}

	/**
	 * 取得DaoSession
	 */
	public static DaoSession getDaoSession(Context context) {
		if (daoSession == null) {
			if (daoMaster == null) {
				daoMaster = getDaoMaster(context);
			}
			daoSession = daoMaster.newSession();
		}
		return daoSession;
	}
	
}
