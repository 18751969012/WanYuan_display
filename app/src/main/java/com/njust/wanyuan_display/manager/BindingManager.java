package com.njust.wanyuan_display.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.util.DateUtil;
import com.njust.wanyuan_display.util.MachineUtil;
import com.njust.wanyuan_display.util.MySecurity;

import org.apache.log4j.Logger;

/**
 * 绑定管理工具
 */
public class BindingManager {
	
	private final Logger log = Logger.getLogger(BindingManager.class);

	private final String MachineID = "MachineID";

	private final String CSRF = "csrf";

	private final String SYSORDERNO = "sysorderNo";
	
	private final String TASKID = "TASKID";
	
	private final String GoodsUpdateTime = "GoodsUpdateTime";
	
	private final String MachineShelfUpdateTime = "MachineShelfUpdateTime";
	
	private final String DoorStatus = "DoorStatus";
	
	private final String MergeGoods = "MergeGoods";
	
	private final String Temperature = "Temperature";

	private final String DelayGetDoor = "DelayGetDoor";

	private final String WideCrawlerWiring = "WideCrawlerWiring";

	private final String HumidityLimit = "HumidityLimit";

	private final String AutoLight = "AutoLight";






	/**
	 * 设置当前主售货界面的商品显示方式
	 */
	public void setShopGoodsShowType(Context context,String shopGoodsShowType) {
		try {
			SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("shopGoodsShowType", shopGoodsShowType);
			editor.apply();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	/**
	 * 获得当前主售货界面的商品显示方式
	 * */
	public String getShopGoodsShowType(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
		return sp.getString("shopGoodsShowType", "saleByCommodity");
	}

	/**
	 * 设置当前定位经纬度
	 */
	public void setLocation(Context context,String longitudeAndLatitude) {
		try {
			SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("longitudeAndLatitude", longitudeAndLatitude);
			editor.apply();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	/**
	 * 获得当前定位经纬度
	 * */
	public String getLocation(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
		return sp.getString("longitudeAndLatitude", "");
	}
	/**
	 * 设置当前温控回差、当前压缩机最长连续工作时间、当前温度自定义是否开启
	 */
	public void setTempUserDefined(Context context,String tempControlHuiCha, String compressorMaxWorktime, String tempUserDefined) {
		try {
			SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("TempControlHuiCha", tempControlHuiCha);
			editor.putString("CompressorMaxWorktime", compressorMaxWorktime);
			editor.putString("TempUserDefined", tempUserDefined);
			editor.apply();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	/**
	 * 获得当前温度自定义是否开启
	 * */
	public String getTempUserDefined(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
		return sp.getString("TempUserDefined", "close");
	}
	/**
	 * 获得当前温控回差
	 */
	public String getTempControlHuiCha(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
		return sp.getString("TempControlHuiCha", "");
	}
	/**
	 * 获得当前压缩机最长连续工作时间
	 * */
	public String getCompressorMaxWorktime(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
		return sp.getString("CompressorMaxWorktime", "");
	}



	/**
	 * 获得当前自动控灯开启状态
	 */
	public String getAutoLight(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
		return sp.getString(AutoLight, "close");
	}

	/**
	 * 设置当前自动控灯开启状态
	 */
	public void setAutoLight(Context context, String autoLight) {
		try {
			SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString(AutoLight, autoLight);
			editor.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	/**
	 * 获得当前自动控灯的时间
	 */
	public int[] getAutoLightTime(Context context) {
		int[] time = new int[4];
		SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
		time[0] = sp.getInt("open_hour", -1);
		time[1] = sp.getInt("open_minute", -1);
		time[2] = sp.getInt("close_hour", -1);
		time[3] = sp.getInt("close_minute", -1);
		return time;
	}

	/**
	 * 设置当前自动控灯的时间
	 */
	public void setAutoLightTime(Context context, int open_hour, int open_minute, int close_hour, int close_minute) {
		try {
			SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			if(open_hour != -1){
				editor.putInt("open_hour", open_hour);
			}
			if(open_minute != -1){
				editor.putInt("open_minute", open_minute);
			}
			if(close_hour != -1){
				editor.putInt("close_hour",close_hour);
			}
			if(close_minute != -1){
				editor.putInt("close_minute", close_minute);
			}
			editor.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 获得当前中柜门的延时时间
	 */
	public String getDelayGetDoor(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
		return sp.getString(DelayGetDoor, "200");
	}

	/**
	 * 设置当前中柜门的延时时间
	 */
	public void setDelayGetDoor(Context context, String delayGetDoor) {
		try {
			SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString(DelayGetDoor, delayGetDoor);
			editor.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 获得当前宽履带的布线类型
	 */
	public String getWideCrawlerWiring(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
		return sp.getString(WideCrawlerWiring, "13579_1357");
	}

	/**
	 * 设置当前宽履带的布线类型
	 */
	public void setWideCrawlerWiring(Context context, String wideCrawlerWiring) {
		try {
			SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString(WideCrawlerWiring, wideCrawlerWiring);
			editor.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	/**
	 * 获得当前自动门加热的湿度门限
	 */
	public String getHumidityLimit(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
		return sp.getString(HumidityLimit, "");
	}

	/**
	 * 设置当前自动门加热的湿度门限
	 */
	public void setHumidityLimit(Context context, String humidityLimit) {
		try {
			SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString(HumidityLimit, humidityLimit);
			editor.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 获得设备唯一编码
	 */
	public String getMachineID(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
		return sp.getString(MachineID, "");
	}

	/**
	 * 设置设备唯一编码
	 */
	public void setMachineID(Context context, String a) {
		String no = new MachineUtil().board() + new MachineUtil().serial();
		try {
			String date = DateUtil.getCurrentTimeMachineID();
			String machineID = date+MySecurity.md5(no).substring(2, 10);

			SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			if(a.equals("")){
				editor.putString(MachineID, a);
			}else{
				editor.putString(MachineID, machineID);
			}
			editor.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}


	/**
	 * 获得当前csrf值
	 */
	public String getCsrf(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
		return sp.getString(CSRF, "1");
	}

	/**
	 * 设置当前csrf值
	 */
	public void setCsrf(Context context, String csrf) {
		try {
			SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString(CSRF, csrf);
			editor.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 获得当前已经支付成功的系统订单号
	 */
	public String getSysorderNo(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
		return sp.getString(SYSORDERNO, "00000000000000000000000000000000000");//35位
	}

	/**
	 * 设置当前已经支付成功的系统订单号
	 */
	public void setSysorderNo(Context context, String sysorderNo) {
		try {
			SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString(SYSORDERNO, sysorderNo);
			editor.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	
	/**
	 * 获得任务ID
	 */
	public String getTaskId(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
		return sp.getString(TASKID, "");
	}

	/**
	 * 设置任务ID
	 */
	public void setTaskId(Context context, String taskId) {
		try {
			SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString(TASKID, taskId);
			editor.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 获得商品更新时间
	 */
	public String getGoodsUpdateTime(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
		return sp.getString(GoodsUpdateTime, "");
	}

	/**
	 * 设置商品更新时间
	 */
	public void setGoodsUpdateTime(Context context, String goodsUpdateTime) {
		try {
			SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString(GoodsUpdateTime, goodsUpdateTime);
			editor.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 获得货道更新时间
	 */
	public String getMachineShelfUpdateTime(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
		return sp.getString(MachineShelfUpdateTime, "");
	}

	/**
	 * 设置货道更新时间
	 */
	public void setMachineShelfUpdateTime(Context context, String machineShelfUpdateTime) {
		try {
			SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString(MachineShelfUpdateTime, machineShelfUpdateTime);
			editor.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	

	/**
	 * 获得设备门状态
	 */
	public boolean getDoorStatus(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
		return sp.getBoolean(DoorStatus, false);
	}

	/**
	 * 设置设备门状态
	 */
	public void setDoorStatus(Context context, boolean doorStatus) {
		try {
			SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putBoolean(DoorStatus, doorStatus);
			editor.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 获得合并商品参数
	 */
	public String getMergeGoods(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
		return sp.getString(MergeGoods, "");
	}

	/**
	 * 设置合并商品参数
	 */
	public void setMergeGoods(Context context, String mergeGoods) {
		try {
			SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString(MergeGoods, String.valueOf(mergeGoods));
			editor.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	
	/**
	 * 获得温度
	 */
	public String getTemperature(Context context){
		SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
		return sp.getString(Temperature, "");
	}
	
	/**
	 * 设置温度
	 */
	public void setTemperature(Context context, String temperature) {
		try {
			SharedPreferences sp = context.getSharedPreferences(Resources.SP_BINDING_INFO, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString(Temperature, String.valueOf(temperature));
			editor.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}


	
}
