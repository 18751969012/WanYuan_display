package com.njust.wanyuan_display.action;

import android.content.Context;

import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.manager.OkHttp3Manager;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 机器初始化，用于首次绑定
 */
public class InitMachineAction {
	
    private final Logger log = Logger.getLogger(InitMachineAction.class);

	public boolean initMachine(Context context, String userName, String userPassword) {
		boolean success = false;
		try {
			String MachineID = new BindingManager().getMachineID(context);
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("type", "1");
			params.put("gran", "01");
			params.put("machineId", MachineID);
			params.put("phonelogin", userName);
			params.put("passlogin", userPassword);
			params.put("_csrf", new BindingManager().getCsrf(context));

			String resultStr = OkHttp3Manager.getInstance(context).post_login(context, Resources.init_machine_url_njust, params);
			if (resultStr.equals("success")) {
            		DBManager.updateMachineInfo(context, "1",userName, userPassword);
            		success = true;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			
		}
		return success;
	}

}
