package com.njust.wanyuan_display.db;

import android.content.Context;

import com.njust.wanyuan_display.greenDao.AdvDao;
import com.njust.wanyuan_display.greenDao.AisleInfoDao;
import com.njust.wanyuan_display.greenDao.AisleMergeDao;
import com.njust.wanyuan_display.greenDao.DaoSession;
import com.njust.wanyuan_display.greenDao.GoodsInfoDao;
import com.njust.wanyuan_display.greenDao.MachineInfoDao;
import com.njust.wanyuan_display.greenDao.MachineStateDao;
import com.njust.wanyuan_display.greenDao.OrderRecordDao;
import com.njust.wanyuan_display.greenDao.SettingInfoDao;
import com.njust.wanyuan_display.greenDao.TransactionDao;

/**
 * 数据库缓存类
 */
public class CacheDBUtil {

	public static String DB_VM = "WanYuanDisplayDB";

	private static CacheDBUtil instance;
	private static Context appContext;
	private DaoSession daoSession;

	private AdvDao advDao;
	private AisleInfoDao aisleInfoDao;
	private AisleMergeDao aisleMergeDao;
	private GoodsInfoDao goodsInfoDao;
	private MachineInfoDao machineInfoDao;
	private MachineStateDao machineStateDao;
	private OrderRecordDao orderRecordDao;
	private SettingInfoDao settingInfoDao;
	private TransactionDao transactionDao;

	
	/**
	 * 采用单例模式
	 */
	public static CacheDBUtil getInstance(Context context) {
		if (instance == null) {
			instance = new CacheDBUtil();
			if (appContext == null) {
				appContext = context.getApplicationContext();
			}
			instance.daoSession = CacheBaseApplication.getDaoSession(context);

			instance.advDao = instance.daoSession.getAdvDao();
			instance.aisleInfoDao = instance.daoSession.getAisleInfoDao();
			instance.aisleMergeDao = instance.daoSession.getAisleMergeDao();
			instance.goodsInfoDao = instance.daoSession.getGoodsInfoDao();
			instance.machineInfoDao = instance.daoSession.getMachineInfoDao();
			instance.machineStateDao = instance.daoSession.getMachineStateDao();
			instance.orderRecordDao = instance.daoSession.getOrderRecordDao();
			instance.settingInfoDao = instance.daoSession.getSettingInfoDao();
			instance.transactionDao = instance.daoSession.getTransactionDao();
		}
		return instance;
	}

	public AdvDao getAdvDao() {
		return advDao;
	}

	public AisleInfoDao getAisleInfoDao() {
		return aisleInfoDao;
	}

	public AisleMergeDao getAisleMergeDao() {
		return aisleMergeDao;
	}

	public GoodsInfoDao getGoodsInfoDao() {
		return goodsInfoDao;
	}

	public MachineInfoDao getMachineInfoDao() {
		return machineInfoDao;
	}

	public MachineStateDao getMachineStateDao() {
		return machineStateDao;
	}

	public OrderRecordDao getOrderRecordDao() {
		return orderRecordDao;
	}

	public SettingInfoDao getSettingInfoDao() {
		return settingInfoDao;
	}

	public TransactionDao getTransactionDao() {
		return transactionDao;
	}

	public void  clearQueryCache_aisleInfoDao() {
		aisleInfoDao.detachAll();
	}

}
