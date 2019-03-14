package com.njust.wanyuan_display.action;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.manager.OkHttp3Manager;
import com.njust.wanyuan_display.util.DateUtil;
import com.njust.wanyuan_display.util.MachineUtil;
import com.njust.wanyuan_display.util.ScreenUtil;
import com.njust.wanyuan_display.util.StringUtil;
import com.njust.wanyuan_display.util.TokenUtil;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 开机上报协议
 */
public class OpenMachineAction {
	
    private final Logger log = Logger.getLogger(OpenMachineAction.class);

	public boolean openMachine(Context context) {
		boolean success = false;
		try {
			MachineUtil machineUtil = new MachineUtil();
			TokenUtil tokenUtil = new TokenUtil();
			String MachineID = new BindingManager().getMachineID(context);
			String time = DateUtil.getCurrentTime();
			String token = tokenUtil.token(MachineID, time,"1","02");


			Map<String, String> params = new HashMap<String, String>();
			params.put("type", "1");
			params.put("gran", "02");
			params.put("machineId", MachineID);
			params.put("phonelogin", DBManager.userName(context));
			params.put("passlogin", DBManager.userPassword(context));
			params.put("_csrf", new BindingManager().getCsrf(context));

			params.put("board", machineUtil.board());
			params.put("serial", machineUtil.serial());
			params.put("brand", machineUtil.brand());
			params.put("manufacturer", machineUtil.manufacturer());
			params.put("model", machineUtil.model());
			params.put("product", machineUtil.product());

			params.put("androidVersion", machineUtil.androidversion());
			params.put("androidSdk", machineUtil.androidsdk());
			params.put("version", machineUtil.apkVersion(context));
			params.put("hardVersion", "1.0");

			params.put("screenWidth", ScreenUtil.screenWidth(context));
			params.put("screenHeight", ScreenUtil.screenHeight(context));
			params.put("screenDpi", ScreenUtil.screenDpi(context));

			params.put("token", token);
			params.put("time", time);

			String resultStr = OkHttp3Manager.getInstance(context).post_json_syn(context, Resources.open_machine_url_njust, params);
			log.info("开机上报返回:" + resultStr);
			if ( StringUtil.isNotNull(resultStr) ) {
				JSONObject openMachineObject = JSONObject.parseObject(resultStr);
				if(StringUtil.equals(openMachineObject.getString("type"),"1")
						&& StringUtil.equals(openMachineObject.getString("gran"),"03")
						&& StringUtil.equals(openMachineObject.getString("machineId"),MachineID)
						&& StringUtil.equals(openMachineObject.getString("token"),token)
						&& StringUtil.equals(openMachineObject.getString("feedback"),"0")){
					success = true;
				}else{
					openMachine(context);
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {

		}
		return success;
	}

}
