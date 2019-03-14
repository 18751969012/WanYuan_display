package com.njust.wanyuan_display.action;

import android.content.Context;
import android.util.Log;

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
 * 非现金交易请求二维码
 */
public class nonCashOrderAction {
	
    private final Logger log = Logger.getLogger(nonCashOrderAction.class);

	public String nonCashOrderObject(Context context, String sysorderNo, String goodsInfo, int payType) {
		String codeUrl = "";
		JSONObject nonCashOrderObject = new JSONObject();
		try {
			TokenUtil tokenUtil = new TokenUtil();
			String MachineID = new BindingManager().getMachineID(context);
			String time = DateUtil.getCurrentTime();
			String token = tokenUtil.token(MachineID, time,"2","01");

			Map<String, String> params = new HashMap<String, String>();
			params.put("type", "2");
			params.put("gran", "01");
			params.put("machineId", MachineID);
			params.put("_csrf", new BindingManager().getCsrf(context));
			
			params.put("sysorderNo", sysorderNo);
			params.put("goodsInfo", goodsInfo);
			params.put("payType", String.valueOf(payType));

			params.put("token", token);
			params.put("time", time);
			
			String resultStr = OkHttp3Manager.getInstance(context).post_json_syn(context, Resources.nonCash_qrcode_url_njust, params);

			if ( StringUtil.isNotNull(resultStr) ) {
				nonCashOrderObject = JSONObject.parseObject(resultStr);
				if (StringUtil.equals(nonCashOrderObject.getString("type"), "2")
						&& StringUtil.equals(nonCashOrderObject.getString("gran"), "02")
						&& StringUtil.equals(nonCashOrderObject.getString("machineId"), MachineID)
						&& StringUtil.equals(nonCashOrderObject.getString("sysorderNo"), sysorderNo)
						&& StringUtil.equals(nonCashOrderObject.getString("token"),token)) {
					if(StringUtil.equals(nonCashOrderObject.getString("state"),"SUCCESS")){
						codeUrl = nonCashOrderObject.getString("code_url");
					}else{
						String state = nonCashOrderObject.getString("state");
						Log.w("happy","二维码请求错误原因："+state);
						log.info("二维码请求错误原因："+state);
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			
		}
		return codeUrl;
	}

}
