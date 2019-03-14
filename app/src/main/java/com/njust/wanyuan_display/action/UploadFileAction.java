package com.njust.wanyuan_display.action;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.manager.OkHttp3Manager;
import com.njust.wanyuan_display.manager.UploadManager;
import com.njust.wanyuan_display.parcelable.AisleInfoParcelable;
import com.njust.wanyuan_display.util.DateUtil;
import com.njust.wanyuan_display.util.FileUtil;
import com.njust.wanyuan_display.util.StringUtil;
import com.njust.wanyuan_display.util.TokenUtil;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 终端上传文件
 */
public class UploadFileAction {
	
    private final Logger log = Logger.getLogger(UploadFileAction.class);

	public void uploadFileObject(final Context context, final String taskId, final String type) {

		try {
			TokenUtil tokenUtil = new TokenUtil();
			final String MachineID = new BindingManager().getMachineID(context);
			String time = DateUtil.getCurrentTime();
			final String token = tokenUtil.token(MachineID, time,"7","01");

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("type", "7");
			params.put("gran", "01");
			params.put("machineId", MachineID);
			params.put("_csrf", new BindingManager().getCsrf(context));
			params.put("uploadTime", time);
			params.put("taskId", taskId);
			params.put("uploadType", String.valueOf(type.equals("log")?1:2));

			params.put("token", token);
			params.put("time", time);

			List<File>  fileList = new ArrayList<>();
			if(type.equals("log")){
				fileList = FileUtil.getAllFiles(Resources.logURL());
			}else if(type.equals("db")){
				fileList = FileUtil.getAllFiles(Resources.dbURL());
			}else{
			}

			for (int i=0;i<fileList.size();i++) {
				params.put("file",fileList.get(i));
			}

			UploadManager.getInstance().upLoadFile(Resources.upload_file_url_njust,params, new UploadManager.ReqCallBack() {
						@Override
						public void onReqSuccess(String result) {
							if ( StringUtil.isNotNull(result) ) {
								JSONObject uploadFileObject = new JSONObject();
								uploadFileObject = JSONObject.parseObject(result);
								if (StringUtil.equals(uploadFileObject.getString("type"), "7")
										&& StringUtil.equals(uploadFileObject.getString("gran"), "02")
										&& StringUtil.equals(uploadFileObject.getString("machineId"), MachineID)
										&& StringUtil.equals(uploadFileObject.getString("token"),token)) {
									if (StringUtil.equals(uploadFileObject.getString("feedback"), "1")) {
										uploadFileObject(context,taskId,type);
									}
								}
							}
						}
						@Override
						public void onReqFailed() {
							uploadFileObject(context,taskId,type);
						}
					});
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			
		}
	}

}
