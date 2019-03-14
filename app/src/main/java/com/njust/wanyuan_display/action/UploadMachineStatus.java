package com.njust.wanyuan_display.action;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.greenDao.MachineInfo;
import com.njust.wanyuan_display.greenDao.MachineState;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.manager.OkHttp3Manager;
import com.njust.wanyuan_display.parcelable.AisleInfoParcelable;
import com.njust.wanyuan_display.util.DateUtil;
import com.njust.wanyuan_display.util.NetWorkUtil;
import com.njust.wanyuan_display.util.StringUtil;
import com.njust.wanyuan_display.util.TokenUtil;

import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 终端上传设备状态
 */
public class UploadMachineStatus {
	
    private final Logger log = Logger.getLogger(UploadMachineStatus.class);
	private DecimalFormat df   =   new DecimalFormat("#####0.0");
	public static boolean Done = true;//表示交互完毕

	public void uploadMachineStatusObject(Context context, String taskId) {
		Done = false;
		JSONObject uploadMachineStatusObject = new JSONObject();
		try {
			MachineInfo machineInfo = DBManager.getMachineInfo(context);
			MachineState machineState = DBManager.getMachineState(context);
			JSONArray counterStatus = new JSONArray();

			JSONObject counter1 = new JSONObject();
			counter1.put("counterNo","1");
			counter1.put("status", String.valueOf(machineState.getLeftState()));
			counter1.put("compressorStatus",String.valueOf(machineState.getLeftCompressorFanState()));
			counter1.put("temp",df.format((double)machineState.getLeftCabinetTemp()/10));
			counterStatus.add(counter1);
			JSONObject counter2 = new JSONObject();
			counter2.put("counterNo","2");
			counter2.put("status", String.valueOf(machineState.getRightState()));
			counter2.put("compressorStatus",String.valueOf(machineState.getRightCompressorFanState()));
			counter2.put("temp",df.format((double)machineState.getRightCabinetTemp()/10));
			counterStatus.add(counter2);


			TokenUtil tokenUtil = new TokenUtil();
			String MachineID = new BindingManager().getMachineID(context);
			String time = DateUtil.getCurrentTime();
			String token = tokenUtil.token(MachineID, time,"3","01");

			Map<String, String> params = new HashMap<String, String>();
			params.put("type", "3");
			params.put("gran", "01");
			params.put("machineId", MachineID);
			params.put("_csrf", new BindingManager().getCsrf(context));

			params.put("version", machineInfo.getVersion());
			params.put("machineStatus", String.valueOf(machineState.getVmState()));
//			params.put("isOnline", String.valueOf(NetWorkUtil.netIsOpen(context)));
			params.put("location", new BindingManager().getLocation(context));
			params.put("counterStatus", counterStatus != null ? counterStatus.toJSONString() : null);

			params.put("taskId", taskId);
			params.put("token", token);
			params.put("time", time);

			String resultStr = OkHttp3Manager.getInstance(context).post_json_syn(context, Resources.upload_machine_status, params);

			if ( StringUtil.isNotNull(resultStr) ) {
				uploadMachineStatusObject = JSONObject.parseObject(resultStr);
				if (StringUtil.equals(uploadMachineStatusObject.getString("type"), "3")
						&& StringUtil.equals(uploadMachineStatusObject.getString("gran"), "02")
						&& StringUtil.equals(uploadMachineStatusObject.getString("machineId"), MachineID)
						&& StringUtil.equals(uploadMachineStatusObject.getString("token"),token)) {
					if (StringUtil.equals(uploadMachineStatusObject.getString("feedback"), "1")) {
						uploadMachineStatusObject(context,taskId);
					}
				}
			}
			
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			Done = true;
		}
	}

}
