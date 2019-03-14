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
 * 订单查询，查询订单的支付状态
 */
public class QueryOrderAction {
	
    private final Logger log = Logger.getLogger(QueryOrderAction.class);

	public boolean queryorderObject(Context context, String sysorderNo, int payType) {
		boolean success = false;
		JSONObject queryorderObject = new JSONObject();
		try {
			TokenUtil tokenUtil = new TokenUtil();
			String MachineID = new BindingManager().getMachineID(context);
			String time = DateUtil.getCurrentTime();
			String token = tokenUtil.token(MachineID, time,"2","03");

			Map<String, String> params = new HashMap<String, String>();
			params.put("type", "2");
			params.put("gran", "03");
			params.put("machineId", MachineID);
			params.put("_csrf", new BindingManager().getCsrf(context));

			params.put("sysorderNo", sysorderNo);
//			params.put("goodsInfo", goodsInfo);
			params.put("payType", String.valueOf(payType));

			params.put("token", token);
			params.put("time", time);

			String resultStr = OkHttp3Manager.getInstance(context).post_json_syn(context, Resources.nonCash_query_url_njust, params);

			if ( StringUtil.isNotNull(resultStr) ) {
				queryorderObject = JSONObject.parseObject(resultStr);
				if (StringUtil.equals(queryorderObject.getString("type"), "2")
						&& StringUtil.equals(queryorderObject.getString("gran"), "04")
						&& StringUtil.equals(queryorderObject.getString("machineId"), MachineID)
						&& StringUtil.equals(queryorderObject.getString("sysorderNo"), sysorderNo)
						&& StringUtil.equals(queryorderObject.getString("token"),token)
						&& StringUtil.equals(queryorderObject.getString("payResult"),"1")) {
					success = true;
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
