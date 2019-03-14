package com.njust.wanyuan_display.action;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njust.wanyuan_display.config.Constant;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.manager.DownloadManager;
import com.njust.wanyuan_display.manager.OkHttp3Manager;
import com.njust.wanyuan_display.util.DateUtil;
import com.njust.wanyuan_display.util.StringUtil;
import com.njust.wanyuan_display.util.TokenUtil;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.MultimediaInfo;

/**
 * 请求广告信息
 */
public class RequestAdvAction {
	
    private final Logger log = Logger.getLogger(RequestAdvAction.class);

	public JSONArray requestAdvObject(Context context, String taskId) {
		JSONObject requestAdvObject = new JSONObject();
		JSONArray advsArray = null;
		try {
			TokenUtil tokenUtil = new TokenUtil();
			String MachineID = new BindingManager().getMachineID(context);
			String time = DateUtil.getCurrentTime();
			String token = tokenUtil.token(MachineID, time,"5","01");

			Map<String, String> params = new HashMap<String, String>();
			params.put("type", "5");
			params.put("gran", "01");
			params.put("machineId", MachineID);
			params.put("_csrf", new BindingManager().getCsrf(context));

			params.put("taskId", taskId);

			params.put("token", token);
			params.put("time", time);

			String resultStr = OkHttp3Manager.getInstance(context).post_json_syn(context, Resources.request_adv_url_njust, params);
			
			if ( StringUtil.isNotNull(resultStr) ) {
				requestAdvObject = JSONObject.parseObject(resultStr);
				if (StringUtil.equals(requestAdvObject.getString("type"), "5")
						&& StringUtil.equals(requestAdvObject.getString("gran"), "02")
						&& StringUtil.equals(requestAdvObject.getString("machineId"), MachineID)
						&& StringUtil.equals(requestAdvObject.getString("token"),token)) {
					if (StringUtil.equals(requestAdvObject.getString("feedback"), "1")) {
						requestAdvObject(context,taskId);
					} else {
						advsArray = requestAdvObject.getJSONArray("adInfo");
						if (advsArray.size() > 0) {
							for (int i = 0; i < advsArray.size(); i++) {
								JSONObject jsonObject = advsArray.getJSONObject(i);
								int adId = Integer.parseInt(jsonObject.getString("adId"));
								String url = jsonObject.getString("url");
								File adv = new File(Environment.getExternalStorageDirectory() ,Resources.adv_filepath);
								if (!adv.exists()) {
									adv.mkdirs();
								}
								String advName = StringUtil.getAdvName(adId,url);
								File advfile = new File(adv.getAbsolutePath(), advName);
								if(advfile.exists()){
									Log.w("happy","advfile.exists()");
									DBManager.saveAdv(context,jsonObject.getString("adId"),url,Resources.advURL()+advName,
											Integer.parseInt(jsonObject.getString("adType")),jsonObject.getString("playTime"), String.valueOf(Constant.download_success));
								}else{
									Log.w("happy","!advfile.exists()");
									DownloadManager.getInstance().download(context,adId, url, Resources.adv_filepath,
											Integer.parseInt(jsonObject.getString("adType")),jsonObject.getString("playTime"), new DownloadManager.OnDownloadListener() {
												@Override
												public void onDownloadSuccess() {
												}
												@Override
												public void onDownloading(int progress) {
												}
												@Override
												public void onDownloadFailed() {
												}
											});
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			
		}
		return advsArray;
	}
}
