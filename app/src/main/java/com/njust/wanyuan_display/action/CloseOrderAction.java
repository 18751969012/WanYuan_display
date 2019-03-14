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
 * 关闭订单
 */
public class CloseOrderAction {
	
    private final Logger log = Logger.getLogger(CloseOrderAction.class);

	public boolean closeorderObject(Context context, String sysorderNo, int payType) {
		boolean success = false;
		JSONObject closeorderObject = new JSONObject();
		try {
			TokenUtil tokenUtil = new TokenUtil();
			String MachineID = new BindingManager().getMachineID(context);
			String time = DateUtil.getCurrentTime();
			String token = tokenUtil.token(MachineID, time,"2","09");

			Map<String, String> params = new HashMap<String, String>();
			params.put("type", "2");
			params.put("gran", "09");
			params.put("machineId", MachineID);
			params.put("_csrf", new BindingManager().getCsrf(context));

			params.put("sysorderNo", sysorderNo);
			params.put("payType", String.valueOf(payType));//交易类型（1支付宝 2微信）

			params.put("token", token);
			params.put("time", time);

			String resultStr = OkHttp3Manager.getInstance(context).post_json_syn(context, Resources.nonCash_close_order_url_njust, params);

			if ( StringUtil.isNotNull(resultStr) ) {
				closeorderObject = JSONObject.parseObject(resultStr);
				if (StringUtil.equals(closeorderObject.getString("type"), "2")
						&& StringUtil.equals(closeorderObject.getString("gran"), "10")
						&& StringUtil.equals(closeorderObject.getString("machineId"), MachineID)
						&& StringUtil.equals(closeorderObject.getString("sysorderNo"), sysorderNo)
						&& StringUtil.equals(closeorderObject.getString("token"),token)
						&& StringUtil.equals(closeorderObject.getString("feedback"),"0")
						&& StringUtil.equals(closeorderObject.getString("result"),"0")) {//撤销结果。（0 成功 1 失败）
					success = true;
				}
//				else{
//					closeorderObject(context,sysorderNo,payType);
//				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			
		}
		return success;
	}

}
