package com.njust.wanyuan_display.manager;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.listener.ReqCallBack;
import com.njust.wanyuan_display.util.MachineUtil;
import com.njust.wanyuan_display.util.NetWorkUtil;
import com.njust.wanyuan_display.util.StringUtil;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * OkHttp访问请求
 */
public class OkHttp3Manager {
	
	private final Logger log = Logger.getLogger(OkHttp3Manager.class);
	
	private static volatile OkHttp3Manager mInstance;
	
	private static final int timeout = 10;

	private OkHttpClient okHttpClient;
	
    private Handler okHttpHandler;

	private Gson mGson = new Gson();

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	
	public OkHttp3Manager(Context context) {
		okHttpClient = new OkHttpClient.Builder()
				.cookieJar(new CookieJar() {
					private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
					@Override
					public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
						cookieStore.put(url.host(), cookies);
					}
					@Override
					public List<Cookie> loadForRequest(HttpUrl url) {
						List<Cookie> cookies = cookieStore.get(url.host());
						return cookies != null ? cookies : new ArrayList<Cookie>();
					}
				})
				.connectTimeout(timeout, TimeUnit.SECONDS)
				.readTimeout(timeout, TimeUnit.SECONDS)
				.writeTimeout(timeout, TimeUnit.SECONDS)
				.addInterceptor(new TokenInterceptor(context))
				.build();

		okHttpHandler = new Handler(context.getMainLooper());
	}
	
	public static OkHttp3Manager getInstance(Context context) {
		if (mInstance == null) {
			synchronized (OkHttp3Manager.class) {
				if (mInstance == null) {
					mInstance = new OkHttp3Manager(context.getApplicationContext());
				}
			}
		}
		return mInstance;
	}
	
	/**
	 * POST请求(同步)
	 */
	public String post_json_syn(Context context, String post_url, Map<String, String> params) {
		log.info("post请求网址:" + post_url);
		String resultStr = "";
		try {
			if ( NetWorkUtil.netIsOpen(context) ) {
				Response response = null;
				try {
					FormBody.Builder builder = new FormBody.Builder();
					for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext();) {
						String param = (String) iterator.next();
						log.info("param:" + param+"-"+ StringUtil.str(params.get(param)));
						builder.add(param, StringUtil.str(params.get(param)));
					}
					RequestBody requestBody = builder.build();
//
//					String json = mGson.toJson(params);
//					RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
//							, json);
					Request request = addHeaders(context).url(post_url).post(requestBody).build();
					response = okHttpClient.newCall(request).execute();

					if (response.isSuccessful()) {
						resultStr = response.body().string();
						log.info("resultStr:" +resultStr);
						return resultStr;
					}
				} catch (Exception e) {
					log.error(e.getMessage());
					e.printStackTrace();
				} finally {
					if (response!=null) {
						response.close();
						response = null;
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * POST请求(异步)
	 */
	public void post_json_asyn(Context context, String post_url, Map<String, String> params, final ReqCallBack callBack) {
		log.info("post_json_asyn:" + post_url);
		try {
			if ( NetWorkUtil.netIsOpen(context) ) {
				FormBody.Builder builder = new FormBody.Builder();
				for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext();) {
					String param = (String) iterator.next();
					log.info("param:" + param+"-"+ StringUtil.str(params.get(param)));
					builder.add(param, StringUtil.str(params.get(param)));
				}
				RequestBody requestBody = builder.build();
		
				Request request = addHeaders(context).url(post_url).post(requestBody).build();
		
				Call call = okHttpClient.newCall(request);
				call.enqueue(new Callback() {
					@Override
					public void onResponse(Call call, Response response) throws IOException {
						String resultStr = response.body().string();
						log.info("post_json_asyn success:" + resultStr);
						
						successCallBack(resultStr, callBack);
					}
					@Override
					public void onFailure(Call call, IOException e) {
						log.info("post_json_asyn failure:" + e.getMessage());
						
						failCallBack(e.getMessage(), callBack);
					}
				});
			} else {
				failCallBack("", callBack);
			}
			
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	


	public String get_token(Context context, String post_url) {
	log.info("get_token_url:" + post_url);
	String resultStr = "";
	try {
		if ( NetWorkUtil.netIsOpen(context) ) {
			Response response = null;
			try {
				return "111111111";
//				Request request = addHeaders(context).url(post_url).get().build();
//				response = okHttpClient.newCall(request).execute();
//				resultStr = response.body().string();
//				Document doc= Jsoup.parse(resultStr);
//				String token = doc.select("meta[name=_csrf]").get(0).attr("content");
//				Log.w("happy", "token:" + token);
//				return token;
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			} finally {
				if (response!=null) {
					response.close();
					response = null;
				}
			}
		}
	} catch (Exception e) {
		log.error(e.getMessage());
		e.printStackTrace();
	}
	return "";
}

	/**
	 * post登录请求
	 */
	public String post_login(Context context, String post_url, Map<String, String> params) {
		log.info("post_login:" + post_url);
		String resultStr = "";
		try {
			if ( NetWorkUtil.netIsOpen(context) ) {
				Response response = null;
				try {
					FormBody.Builder builder = new FormBody.Builder();
					for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext();) {
						String param = (String) iterator.next();
						log.info("param:" + param+"-"+ StringUtil.str(params.get(param)));
						builder.add(param, StringUtil.str(params.get(param)));
					}
					RequestBody requestBody = builder.build();
					Request request = addHeaders(context).url(post_url).post(requestBody).build();
					return "success";
//					response = okHttpClient.newCall(request).execute();
//					if (response.isSuccessful()) {
//						String url = response.networkResponse().request().url().url().toString();
//						Log.w("happy", "url:" + url);
//						if(url.contains("homePage")){
//							return "success";
//						}else{
//							return "fail";
//						}
//					}
				} catch (Exception e) {
					log.error(e.getMessage());
					e.printStackTrace();
				} finally {
					if (response!=null) {
						response.close();
						response = null;
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return "";
	}


	/**
	 * 文件上传
	 */
	public void upload_file(final Context context, final File file, final Map<String, String> params, final ReqCallBack callBack) {
		try {
			if (file.exists()) {
				MultipartBody.Builder builder = new MultipartBody.Builder();
				builder.setType(MultipartBody.FORM);
				
				for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext();) {
					String param = (String) iterator.next();
					
					builder.addFormDataPart(param, params.get(param));
				}
				
				builder.addFormDataPart("filename", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
				
				RequestBody body = builder.build();
				
				final Request request = new Request.Builder().url(Resources.upload_file_url_njust).post(body).build();
				
				Call call = okHttpClient.newCall(request);
				call.enqueue(new Callback() {
					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (response.isSuccessful()) {
							successCallBack(response.body().string(), callBack);
						} else {
							failCallBack("上传失败", callBack);
							upload_file(context, file, params, callBack);
						}
					}
					@Override
					public void onFailure(Call call, IOException e) {
						failCallBack(e.getMessage(), callBack);
					}
				});
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			
		}
	}
	
	/**
	 * 统一同意处理成功信息
	 * @param result
	 * @param callBack
	 */
	private void successCallBack(final String result, final ReqCallBack callBack) {
		okHttpHandler.post(new Runnable() {
			@Override
			public void run() {
				if (callBack != null) {
					callBack.onReqSuccess(result);
				}
			}
		});
	}
	
	/**
     * 统一处理失败信息
     * @param errorMsg
     * @param callBack
     */
    private void failCallBack(final String errorMsg, final ReqCallBack callBack) {
        okHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqFailed(errorMsg);
                }
            }
        });
    }
    
    /**
     * 统一为请求添加头信息
     * @return
     */
	public Request.Builder addHeaders(Context context) {
		MachineUtil machineUtil = new MachineUtil();
		
		Request.Builder builder = new Request.Builder()
			.addHeader("Connection", "keep-alive");

//			.addHeader("sbId", new BindingManager().getPad(context))
//
//			.addHeader("deviceId", machineUtil.deviceId())
//			.addHeader("board", machineUtil.board())
//			.addHeader("serial", machineUtil.serial())
//
//			.addHeader("brand", machineUtil.brand())
//			.addHeader("manufacturer", machineUtil.manufacturer())
//			.addHeader("model", machineUtil.model())
//			.addHeader("product", machineUtil.product())
//
//			.addHeader("androidversion", machineUtil.androidversion())
//			.addHeader("androidsdk", machineUtil.androidsdk())
//			.addHeader("version", machineUtil.getVersion(context))
//			.addHeader("hardversion", DBManager.hardversion(context))
//
//			.addHeader("screenWidth", ScreenUtil.screenWidth(context))
//			.addHeader("screenHeight", ScreenUtil.screenHeight(context))
//			.addHeader("screenDpi", ScreenUtil.screenDpi(context));
		return builder;
	}

}
