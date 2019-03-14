package com.njust.wanyuan_display.action;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.manager.OkHttp3Manager;
import com.njust.wanyuan_display.util.DateUtil;
import com.njust.wanyuan_display.util.StringUtil;
import com.njust.wanyuan_display.util.TokenUtil;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求商品库
 */
public class RequestGoodsBankAction {
	
    private final Logger log = Logger.getLogger(RequestGoodsBankAction.class);

	public JSONArray requestGoodsBankObject(Context context) {
		JSONObject requestGoodsBankObject = new JSONObject();
		JSONArray goodsArray = null;
		try {
			TokenUtil tokenUtil = new TokenUtil();
			String MachineID = new BindingManager().getMachineID(context);
			String time = DateUtil.getCurrentTime();
			String token = tokenUtil.token(MachineID, time,"4","01");

			Map<String, String> params = new HashMap<String, String>();
			params.put("type", "4");
			params.put("gran", "01");
			params.put("machineId", MachineID);
			params.put("_csrf", new BindingManager().getCsrf(context));

			params.put("repStaff", "");//补货人员信息

			params.put("token", token);
			params.put("time", time);

			String resultStr = OkHttp3Manager.getInstance(context).post_json_syn(context, Resources.request_goods_bank_url_njust, params);
			
			if ( StringUtil.isNotNull(resultStr) ) {
				requestGoodsBankObject = JSONObject.parseObject(resultStr);
				if (StringUtil.equals(requestGoodsBankObject.getString("type"), "4")
						&& StringUtil.equals(requestGoodsBankObject.getString("gran"), "02")
						&& StringUtil.equals(requestGoodsBankObject.getString("machineId"), MachineID)
						&& StringUtil.equals(requestGoodsBankObject.getString("token"),token)) {
					if (StringUtil.equals(requestGoodsBankObject.getString("feedback"), "1")) {
						requestGoodsBankObject(context);
					} else {
						goodsArray = requestGoodsBankObject.getJSONArray("goodsArray");
						if (goodsArray.size() > 0) {
							DBManager.requestGoodsBank(context,goodsArray);
						}
					}
				}
			}
			
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			
		}
		return goodsArray;
	}

}
