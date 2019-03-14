package com.njust.wanyuan_display.action;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.manager.DownloadImageManager;
import com.njust.wanyuan_display.manager.OkHttp3Manager;
import com.njust.wanyuan_display.util.DateUtil;
import com.njust.wanyuan_display.util.StringUtil;
import com.njust.wanyuan_display.util.ToastManager;
import com.njust.wanyuan_display.util.TokenUtil;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 终端前往对应网址请求服务器端的补货下发信息
 */
public class RequestReplenishInfoAction {
	
    private final Logger log = Logger.getLogger(RequestReplenishInfoAction.class);

	public void requestReplenishInfoObject(Context context, String taskId) {
		JSONObject requestReplenishInfoObject = new JSONObject();
		JSONArray aisleInfosArray = null;

		try {
			TokenUtil tokenUtil = new TokenUtil();
			String MachineID = new BindingManager().getMachineID(context);
			String time = DateUtil.getCurrentTime();
			String token = tokenUtil.token(MachineID, time,"4","05");

			Map<String, String> params = new HashMap<String, String>();
			params.put("type", "4");
			params.put("gran", "05");
			params.put("machineId", MachineID);

			params.put("taskId", taskId);

			params.put("_csrf", new BindingManager().getCsrf(context));

			params.put("token", token);
			params.put("time", time);

			String resultStr = OkHttp3Manager.getInstance(context).post_json_syn(context, Resources.request_replenish_url_njust, params);
			
			if ( StringUtil.isNotNull(resultStr) ) {
				requestReplenishInfoObject = JSONObject.parseObject(resultStr);
				if (StringUtil.equals(requestReplenishInfoObject.getString("type"), "4")
						&& StringUtil.equals(requestReplenishInfoObject.getString("gran"), "06")
						&& StringUtil.equals(requestReplenishInfoObject.getString("machineId"), MachineID)
						&& StringUtil.equals(requestReplenishInfoObject.getString("token"),token)) {
					if (StringUtil.equals(requestReplenishInfoObject.getString("feedback"), "1")) {
						requestReplenishInfoObject(context,taskId);
					}else{
						aisleInfosArray = requestReplenishInfoObject.getJSONArray("replenishContent");
						if (aisleInfosArray.size() > 0) {
							DBManager.updateAisleRequestReplenish(context,aisleInfosArray);
							for (int i = 0; i < aisleInfosArray.size(); i++) {
								JSONObject jsonObject = aisleInfosArray.getJSONObject(i);
								int goodID = Integer.parseInt(jsonObject.getString("goodID"));
								String goodUrl = jsonObject.getString("goodUrl");
								File image = new File(Environment.getExternalStorageDirectory() ,Resources.goods_filepath);
								if (!image.exists()) {
									image.mkdirs();
								}
								String imageName = StringUtil.getGoodImageName(goodID,goodUrl) + ".png";
								if(new File(image.getAbsolutePath(), imageName).exists()){
									DBManager.updateAisleGoodImageLocalUrl(context,goodID,Resources.goodsURL()+imageName);
								}else{
									onDownLoad(context, goodID,imageName,goodUrl, Resources.goods_filepath);
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
	}

	/**
	 * 启动图片下载线程
	 */
	private void onDownLoad(final Context context,final int goodId, final String imageName, String url, String saveDir) {
		DownloadImageManager downloadImageManager = new DownloadImageManager(context,
				imageName,url,saveDir,
				new DownloadImageManager.ImageDownLoadCallBack() {
					@Override
					public void onDownLoadSuccess(File file) {
					}
					@Override
					public void onDownLoadSuccess(Bitmap bitmap) {
						// 在这里执行图片保存方法
						DBManager.updateAisleGoodImageLocalUrl(context,goodId,Resources.goodsURL()+imageName);
					}
					@Override
					public void onDownLoadFailed() {
						// 图片保存失败
					}
				});
		//启动图片下载线程
		new Thread(downloadImageManager).start();
	}

}
