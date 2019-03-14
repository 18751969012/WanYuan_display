package com.njust.wanyuan_display.manager;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.njust.wanyuan_display.config.Constant;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.util.StringUtil;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.String.valueOf;

/**
 * 上传管理器
 */
public class UploadManager {

	private final Logger log = Logger.getLogger(UploadManager.class);
	private static UploadManager uploadManager;//单例引用
	private final OkHttpClient okHttpClient;//okHttpClient实例
//	private Handler okHttpHandler;//全局处理子线程和主线程通信


	public static UploadManager getInstance() {
		if (uploadManager == null) {
			uploadManager = new UploadManager();
		}
		return uploadManager;
	}

	private UploadManager() {
		okHttpClient = new OkHttpClient();
	}

	/**
	 *上传文件
	 */
	public void upLoadFile(String url, HashMap<String, Object> paramsMap, final ReqCallBack callBack) {
		try {
			//补全请求地址
			MultipartBody.Builder builder = new MultipartBody.Builder();
			//设置类型
			builder.setType(MultipartBody.FORM);
			//追加参数
			for (String key : paramsMap.keySet()) {
				Object object = paramsMap.get(key);
				if (!(object instanceof File)) {
					builder.addFormDataPart(key, object.toString());
				} else {
					File file = (File) object;
					builder.addFormDataPart(key, file.getName(), RequestBody.create(null, file));
				}
			}

			//创建RequestBody
			RequestBody body = builder.build();
			//创建Request
			final Request request = new Request.Builder().url(url).post(body).build();
			//单独设置参数 比如读取超时时间
			final Call call = okHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					log.error("上传失败："+e.toString());
					callBack.onReqFailed();
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					if (response.isSuccessful()) {
						String string = response.body().string();
						log.info("上传response ----->" + string);
						callBack.onReqSuccess(string);
					} else {
						callBack.onReqFailed();
					}
				}
			});
		} catch (Exception e) {
			log.error(e.toString());
		}
	}


	public interface ReqCallBack {
		/**响应成功*/
		void onReqSuccess(String result);
		/**响应失败*/
		void onReqFailed();
	}
//	/**
//	 * 统一处理成功信息
//	 */
//	private <T> void successCallBack(final T result, final ReqCallBack<T> callBack) {
//		okHttpHandler.post(new Runnable() {
//			@Override
//			public void run() {
//				if (callBack != null) {
//					callBack.onReqSuccess(result);
//				}
//			}
//		});
//	}
//
//	/**
//	 * 统一处理失败信息
//	 */
//	private <T> void failedCallBack(final String errorMsg, final ReqCallBack<T> callBack) {
//		okHttpHandler.post(new Runnable() {
//			@Override
//			public void run() {
//				if (callBack != null) {
//					callBack.onReqFailed(errorMsg);
//				}
//			}
//		});
//	}


}
