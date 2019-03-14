package com.njust.wanyuan_display.action;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.manager.OkHttp3Manager;
import com.njust.wanyuan_display.service.HandlerService;
import com.njust.wanyuan_display.util.DateUtil;
import com.njust.wanyuan_display.util.StringUtil;
import com.njust.wanyuan_display.util.TokenUtil;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 心跳协议
 */
public class HeartbeatAction {

    private final Logger log = Logger.getLogger(HeartbeatAction.class);
	public static boolean Done = true;//表示交互完毕

	public JSONObject heartbeatObject(Context context) {
		Done = false;
		JSONObject heartbeatObject = new JSONObject();
		JSONObject data = new JSONObject();
		try {
			TokenUtil tokenUtil = new TokenUtil();
			BindingManager bindingManager = new BindingManager();
			String MachineID = bindingManager.getMachineID(context);
			String time = DateUtil.getCurrentTime();
			String token = tokenUtil.token(MachineID, time,"0","01");

			Map<String, String> params = new HashMap<String, String>();
			params.put("type", "0");
			params.put("gran", "01");
			params.put("machineId", MachineID);
			params.put("_csrf", new BindingManager().getCsrf(context));

			params.put("goodsUpdateTime", bindingManager.getGoodsUpdateTime(context));
			params.put("machineShelfUpdateTime", bindingManager.getMachineShelfUpdateTime(context));
			params.put("token", token);
			params.put("time", time);

			log.info("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
			String resultStr = OkHttp3Manager.getInstance(context).post_json_syn(context, Resources.heartbeat_url_njust, params);
            
			if ( StringUtil.isNotNull(resultStr) ) {
            	heartbeatObject = JSONObject.parseObject(resultStr);
				if(StringUtil.equals(heartbeatObject.getString("type"),"0")
						&& StringUtil.equals(heartbeatObject.getString("gran"),"02")
						&& StringUtil.equals(heartbeatObject.getString("machineId"),MachineID)){
					if(StringUtil.equals(heartbeatObject.getString("feedback"),"0")){
//						log.info("心跳正常");
//						如果存在广告下载失败的情况，则重新询问
						if(StringUtil.equals(heartbeatObject.getString("order"),"1")){
							log.info("心跳:查询机器状态");
							Intent handlerIntent = new Intent(context, HandlerService.class);
							handlerIntent.setAction(Resources.status_record);
							handlerIntent.putExtra("taskId",heartbeatObject.getString("taskId"));
							context.startService(handlerIntent);

						}else if(StringUtil.equals(heartbeatObject.getString("order"),"2")){
							log.info("心跳:需要推送广告");
							Intent handlerIntent = new Intent(context, HandlerService.class);
							handlerIntent.setAction(Resources.download_adv);
							handlerIntent.putExtra("taskId",heartbeatObject.getString("taskId"));
							context.startService(handlerIntent);

						}else if(StringUtil.equals(heartbeatObject.getString("order"),"3")){
							log.info("心跳:需要更新补货信息");
							Log.w("happy","心跳:需要更新补货信息");
							Intent handlerIntent = new Intent(context, HandlerService.class);
							handlerIntent.setAction(Resources.update_AisleInfo);
							handlerIntent.putExtra("taskId",heartbeatObject.getString("taskId"));
							context.startService(handlerIntent);
						}
//						else if(StringUtil.equals(heartbeatObject.getString("order"),"4")){
//							log.info("心跳:需要更新商品信息");
//							Intent handlerIntent = new Intent(context, HandlerService.class);
//							handlerIntent.setAction(Resources.load_goods);
//							context.startService(handlerIntent);
//
//						}else if(StringUtil.equals(heartbeatObject.getString("order"),"5")){
//							log.info("心跳:需要更新货道信息");
//							Intent handlerIntent = new Intent(context, HandlerService.class);
//							handlerIntent.setAction(Resources.load_machineShelf);
//							context.startService(handlerIntent);
//
//						}
						else if(StringUtil.equals(heartbeatObject.getString("order"),"6")){
							log.info("心跳:需要上传日志");
							Intent handlerIntent = new Intent(context, HandlerService.class);
							handlerIntent.putExtra("type", "log");
							handlerIntent.putExtra("taskId",heartbeatObject.getString("taskId"));
//							handlerIntent.putExtra("uploaddate", "2018");
							handlerIntent.setAction(Resources.file_upload);
							context.startService(handlerIntent);

						}else if(StringUtil.equals(heartbeatObject.getString("order"),"7")){
							log.info("心跳:需要上传数据库文件");
							Intent handlerIntent = new Intent(context, HandlerService.class);
							handlerIntent.putExtra("type", "db");
							handlerIntent.putExtra("taskId",heartbeatObject.getString("taskId"));
							handlerIntent.setAction(Resources.file_upload);
							context.startService(handlerIntent);

						}else if(StringUtil.equals(heartbeatObject.getString("order"),"8")){
							log.info("心跳:远程测试出货");

						}
					}else if(StringUtil.equals(heartbeatObject.getString("feedback"),"1")){
						log.info("心跳异常，重发心跳");
						log.info("ccccccccccccccccccccccccccccc");
						heartbeatObject(context);

					}
				}
            	
			}
			
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			Done = true;
		}
		return heartbeatObject;
	}

}
