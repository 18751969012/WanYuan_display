package com.njust.wanyuan_display.action;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.manager.OkHttp3Manager;
import com.njust.wanyuan_display.util.DateUtil;
import com.njust.wanyuan_display.util.StringUtil;
import com.njust.wanyuan_display.util.TokenUtil;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 出货结果上报
 */
public class OutGoodsSubmitAction {
	
    private final Logger log = Logger.getLogger(OutGoodsSubmitAction.class);

	public void outGoodsSubmitObject(Context context, String sysorderNo,String succesResult, String errorResult, String deliverTime) {
		JSONObject outGoodsSubmitObject = new JSONObject();
		try {
			TokenUtil tokenUtil = new TokenUtil();
			String MachineID = new BindingManager().getMachineID(context);
			String time = DateUtil.getCurrentTime();
			String token = tokenUtil.token(MachineID, time,"2","05");

			Map<String, String> params = new HashMap<String, String>();
			params.put("type", "2");
			params.put("gran", "05");
			params.put("machineId", MachineID);
			params.put("_csrf", new BindingManager().getCsrf(context));

			params.put("sysorderNo", sysorderNo);

			params.put("succesResult", succesResult);
			params.put("errorResult", errorResult);

			params.put("token", token);
			params.put("deliverTime", deliverTime);
			params.put("time", time);

			String resultStr = OkHttp3Manager.getInstance(context).post_json_syn(context, Resources.nonCash_finish_outgoods_submit_url_njust, params);

			if ( StringUtil.isNotNull(resultStr) ) {
				outGoodsSubmitObject = JSONObject.parseObject(resultStr);
				if (StringUtil.equals(outGoodsSubmitObject.getString("type"), "2")
						&& StringUtil.equals(outGoodsSubmitObject.getString("gran"), "06")
						&& StringUtil.equals(outGoodsSubmitObject.getString("machineId"), MachineID)
						&& StringUtil.equals(outGoodsSubmitObject.getString("sysorderNo"), sysorderNo)
						&& StringUtil.equals(outGoodsSubmitObject.getString("token"),token)) {
					if(StringUtil.equals(outGoodsSubmitObject.getString("feedback"), "1")){
						outGoodsSubmitObject(context,sysorderNo,succesResult,errorResult,deliverTime);
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
