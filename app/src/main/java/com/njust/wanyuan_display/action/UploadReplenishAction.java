package com.njust.wanyuan_display.action;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.manager.OkHttp3Manager;
import com.njust.wanyuan_display.parcelable.AisleInfoParcelable;
import com.njust.wanyuan_display.util.DateUtil;
import com.njust.wanyuan_display.util.StringUtil;
import com.njust.wanyuan_display.util.TokenUtil;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 终端上传补货信息
 */
public class UploadReplenishAction {
	
    private final Logger log = Logger.getLogger(UploadReplenishAction.class);

	public void uploadReplenishObject(Context context, String startReplenishTime, ArrayList<AisleInfoParcelable> replenishList) {
		JSONObject uploadReplenishObject = new JSONObject();
		try {
			JSONArray replenishContent = new JSONArray();
			if(replenishList != null){
				for(int i=0;i<replenishList.size();i++){
					JSONObject replenish = new JSONObject();
					replenish.put("counterNo",replenishList.get(i).getCounter());
					replenish.put("channelNo",replenishList.get(i).getPositionID());
					replenish.put("goodId",replenishList.get(i).getGoodID());
					replenish.put("goodName",replenishList.get(i).getGoodName());
					replenish.put("goodPrice",replenishList.get(i).getGoodPrice());
					replenish.put("capacity",replenishList.get(i).getCapacity());
					replenish.put("goodQuantity",replenishList.get(i).getGoodQuantity());
					replenishContent.add(replenish);
				}
			}
			TokenUtil tokenUtil = new TokenUtil();
			String MachineID = new BindingManager().getMachineID(context);
			String time = DateUtil.getCurrentTime();
			String token = tokenUtil.token(MachineID, time,"4","03");

			Map<String, String> params = new HashMap<String, String>();
			params.put("type", "4");
			params.put("gran", "03");
			params.put("machineId", MachineID);
			params.put("_csrf", new BindingManager().getCsrf(context));

			params.put("replenishTime",startReplenishTime);
			params.put("replenishContent", replenishContent != null ? replenishContent.toJSONString() : null);
			params.put("repStaff", "");//补货人员信息

			params.put("token", token);
			params.put("time", time);

			String resultStr = OkHttp3Manager.getInstance(context).post_json_syn(context, Resources.local_upload_replenish_url_njust, params);

			if ( StringUtil.isNotNull(resultStr) ) {
				uploadReplenishObject = JSONObject.parseObject(resultStr);
				if (StringUtil.equals(uploadReplenishObject.getString("type"), "4")
						&& StringUtil.equals(uploadReplenishObject.getString("gran"), "04")
						&& StringUtil.equals(uploadReplenishObject.getString("machineId"), MachineID)
						&& StringUtil.equals(uploadReplenishObject.getString("token"),token)) {
					if (StringUtil.equals(uploadReplenishObject.getString("feedback"), "1")) {
						uploadReplenishObject(context,startReplenishTime,replenishList);
					}
				}
			}
			
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			
		}
	}

}
