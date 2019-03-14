package com.njust.wanyuan_display.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njust.wanyuan_display.action.CloseOrderAction;
import com.njust.wanyuan_display.action.OpenMachineAction;
import com.njust.wanyuan_display.action.OutGoodsSubmitAction;
import com.njust.wanyuan_display.action.QueryOrderAction;
import com.njust.wanyuan_display.action.RequestAdvAction;
import com.njust.wanyuan_display.action.RequestGoodsBankAction;
import com.njust.wanyuan_display.action.RequestReplenishInfoAction;
import com.njust.wanyuan_display.action.UploadFileAction;
import com.njust.wanyuan_display.action.UploadMachineStatus;
import com.njust.wanyuan_display.action.UploadReplenishAction;
import com.njust.wanyuan_display.action.nonCashOrderAction;
import com.njust.wanyuan_display.config.Constant;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.greenDao.Adv;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.manager.MachineManager;
import com.njust.wanyuan_display.parcelable.AisleInfoParcelable;
import com.njust.wanyuan_display.util.DateUtil;
import com.njust.wanyuan_display.util.NetWorkUtil;
import com.njust.wanyuan_display.util.StringUtil;
import com.njust.wanyuan_display.util.SysorderNoUtil;

import org.apache.log4j.Logger;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 处理耗时操作的服务
 */
public class HandlerService extends IntentService {
	
	private final Logger log = Logger.getLogger(HandlerService.class);
	
	public static MediaPlayer mp;
	
	public Timer timer = null;
	
    public static boolean weixin = true;//退出支付页面之后，通知服务器关闭订单、关闭查询订单的子线程，
	public static boolean alipay = true;
    
    public HandlerService() {
		super("HandlerService");
	}
    
    @Override
    public void onCreate() {
    	log.info("create hander service");
    	
    	super.onCreate();
    }
    

	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	log.info("start hander service");
    	
    	flags = Service.START_FLAG_REDELIVERY;
		return super.onStartCommand(intent, flags, startId);
    }
    
	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			String action = intent.getAction();
			log.info("handler service:" + action);
			if ( StringUtil.equals(action, Resources.open_machine) ) {
				new OpenMachineAction().openMachine(getApplicationContext());

			} else if( StringUtil.equals(action, Resources.network_change) ) {
				timer = new Timer(true);
	    		timer.schedule(new TimerTask() {
	    			@Override
	    			public void run() {
	    				try {
	    					boolean flag = false;
	    					if ( NetWorkUtil.netIsOpen(getApplicationContext()) ) {
	    						if ( NetWorkUtil.ping(Resources.domain) ) {
	    							flag = true;
	    						}
	    					}
	    					
	    					if (!flag) {
	    						log.info("restart android for network error");
	    						new MachineManager().restart();
	    					}
	    				} catch (Exception e) {
	    					log.error(e.getMessage());
	    					e.printStackTrace();
	    				} finally {
							timer.cancel();
							timer = null;
	    				}
	    			}
	    		}, 2*60*1000, 1);
	    		
			}else if ( StringUtil.equals(action, Resources.alipay_qrcode) ) {
				final String goodsInfos = intent.getStringExtra("goodsInfos");
				final String sysorderNo = intent.getStringExtra("sysorderNo");

				Log.w("happy","alipay_sysorderNo:"+sysorderNo);
				final JSONArray goodsArray = JSONArray.parseArray(goodsInfos);
				final String codeUrl = new nonCashOrderAction().nonCashOrderObject(getApplicationContext(), sysorderNo, goodsInfos, Constant.alipay);

				/*保存订单记录*/
				if (codeUrl != null && !codeUrl.equals("")) {
					/*通过广播通知前台更新二维码*/
					Intent mIntent = new Intent(Resources.alipay_qrcode_show);
					mIntent.putExtra("qrcode", codeUrl);
					mIntent.putExtra("sysorderNo", sysorderNo);
					getApplicationContext().sendBroadcast(mIntent);
					if ( goodsArray.size()>0 ) {
						final String time = DateUtil.getCurrentTime();
						for (int i = 0; i < goodsArray.size(); i++) {
							final JSONObject goodInfo = goodsArray.getJSONObject(i);
							DBManager.saveOrderRecord(getApplicationContext(),sysorderNo,Constant.alipay,goodInfo.getString("goodID"),
									goodInfo.getString("goodName"), Double.parseDouble(goodInfo.getString("goodPrice")),
									goodInfo.getString("goodQuantity"), goodInfo.getString("goodCounter"),
									goodInfo.getString("channelNo"), time );
						}
					}
					alipay = true;
					/*查询是否支付*/
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < 180; i++) {
								try {
									if(alipay){
										boolean success = new QueryOrderAction().queryorderObject(getApplicationContext(), sysorderNo, Constant.alipay);
										if (success) {
										/*数据库更新*/
											if ( goodsArray.size()>0 ) {
												final String time = DateUtil.getCurrentTime();
											/*订单确认支付*/
												DBManager.orderHasConfirm(getApplicationContext(), sysorderNo, time);
												new BindingManager().setSysorderNo(getApplicationContext(), sysorderNo);
												String positionIDs = "";
												for (int j = 0; j < goodsArray.size(); j++) {
													JSONObject goodInfo = goodsArray.getJSONObject(j);
													positionIDs = positionIDs + goodInfo.getString("channelNo") + ",";
												}
												positionIDs = positionIDs.substring(0,positionIDs.length()-1);
											/*生成出货记录*/
												DBManager.saveDelivery(getApplicationContext(),sysorderNo,positionIDs,time,Constant.alipay);
											}
											/*通知开始出货，屏幕等待完成*/
											Intent mIntent = new Intent(Resources.out_goods_start);
											mIntent.putExtra("sysorderNo", sysorderNo);
											getApplicationContext().sendBroadcast(mIntent);
											Intent service = new Intent(getApplicationContext(), OutGoodsService.class);
											try{
												getApplicationContext().stopService(service);
											}catch (Exception e){
												e.printStackTrace();
											}
											getApplicationContext().startService(service);
											break;
										} else {
											Thread.sleep(1000);
										}
									}else{
										boolean success = new CloseOrderAction().closeorderObject(getApplicationContext(), sysorderNo, Constant.alipay);
										if(success){
											break;
										}
									}
								} catch (Exception e) {
									log.error(e.getMessage());
									e.printStackTrace();
								}
							}
						}
					});
					thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
						@Override
						public void uncaughtException(Thread thread, Throwable e) {

						}
					});
					thread.start();
				}
			} else if ( StringUtil.equals(action, Resources.weixin_qrcode) ) {
				final String goodsInfos = intent.getStringExtra("goodsInfos");
				final String sysorderNo = intent.getStringExtra("sysorderNo");

				Log.w("happy","weixin_sysorderNo:"+sysorderNo);
				final JSONArray goodsArray = JSONArray.parseArray(goodsInfos);
				final String codeUrl = new nonCashOrderAction().nonCashOrderObject(getApplicationContext(), sysorderNo, goodsInfos, Constant.weixin);

				/*保存订单记录*/
				if ( codeUrl != null && !codeUrl.equals("") ) {
					/*通过广播通知前台更新二维码*/
					Intent mIntent = new Intent(Resources.weixin_qrcode_show);
					mIntent.putExtra("qrcode", codeUrl);
					mIntent.putExtra("sysorderNo", sysorderNo);
					getApplicationContext().sendBroadcast(mIntent);
					if ( goodsArray.size()>0 ) {
						final String time = DateUtil.getCurrentTime();
						for (int i = 0; i < goodsArray.size(); i++) {
							final JSONObject goodInfo = goodsArray.getJSONObject(i);
							DBManager.saveOrderRecord(getApplicationContext(),sysorderNo,Constant.weixin,goodInfo.getString("goodID"),
									goodInfo.getString("goodName"), Double.parseDouble(goodInfo.getString("goodPrice")),
									goodInfo.getString("goodQuantity"), goodInfo.getString("goodCounter"),
									goodInfo.getString("channelNo"), time );
						}
					}
					weixin = true;
					/*查询是否支付*/
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < 180; i++) {
								try {
									if(weixin){
										boolean success = new QueryOrderAction().queryorderObject(getApplicationContext(), sysorderNo, Constant.weixin);
										if (success) {
										/*数据库更新*/
											if ( goodsArray.size()>0 ) {
												final String time = DateUtil.getCurrentTime();
											/*订单确认支付*/
												DBManager.orderHasConfirm(getApplicationContext(), sysorderNo, time);
												new BindingManager().setSysorderNo(getApplicationContext(), sysorderNo);
												String positionIDs = "";
												for (int j = 0; j < goodsArray.size(); j++) {
													JSONObject goodInfo = goodsArray.getJSONObject(j);
													positionIDs = positionIDs + goodInfo.getString("channelNo") + ",";
												}
												positionIDs = positionIDs.substring(0,positionIDs.length()-1);
											/*生成出货记录*/
												DBManager.saveDelivery(getApplicationContext(),sysorderNo,positionIDs,time,Constant.weixin);
											}
											/*广播通知开始出货，屏幕等待完成*/
											Intent mIntent = new Intent(Resources.out_goods_start);
											getApplicationContext().sendBroadcast(mIntent);
											Intent service = new Intent(getApplicationContext(), OutGoodsService.class);
											try{
												getApplicationContext().stopService(service);
											}catch (Exception e){
												e.printStackTrace();
											}
											getApplicationContext().startService(service);
											break;
										}  else {
											Thread.sleep(1000);
										}
									}else{
										boolean success = new CloseOrderAction().closeorderObject(getApplicationContext(), sysorderNo, Constant.weixin);
										if(success){
											break;
										}
									}
								} catch (Exception e) {
									log.error(e.getMessage());
									e.printStackTrace();
								}
							}
						}
					});
					thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
						@Override
						public void uncaughtException(Thread thread, Throwable e) {

						}
					});
					thread.start();
				}
			}else if ( StringUtil.equals(action, Resources.request_goods_bank)){
				/*删除本地商品库*/
				DBManager.deletAllGoodsInfo(getApplicationContext());
				/*获取商品库*/
				new RequestGoodsBankAction().requestGoodsBankObject(getApplicationContext());
				/*通过广播通知前台更新商品库完成*/
				Intent mIntent = new Intent(Resources.request_goods_bank_done);
				getApplicationContext().sendBroadcast(mIntent);
			}else if ( StringUtil.equals(action, Resources.local_replenish_goods_upload)){
				ArrayList<AisleInfoParcelable> replenishList = intent.getParcelableArrayListExtra("replenishList");
				String startReplenishTime = intent.getStringExtra("startReplenishTime");
				/*上传补货信息*/
				new UploadReplenishAction().uploadReplenishObject(getApplicationContext(),startReplenishTime,replenishList);
				/*通过广播通知前台补货信息上传完成*/
				Intent mIntent = new Intent(Resources.local_replenish_goods_upload_done);
				getApplicationContext().sendBroadcast(mIntent);
			}else if ( StringUtil.equals(action, Resources.update_AisleInfo)){
				String taskId = intent.getStringExtra("taskId");
				/*终端前往对应网址请求服务器端的补货下发信息*/
				new RequestReplenishInfoAction().requestReplenishInfoObject(getApplicationContext(),taskId);
				/*控制页面跳转到广告页面，便于重新刷新,现在是倒计时自动跳转，如果不满足要求再改成主动*/

			}else if ( StringUtil.equals(action, Resources.out_goods_result_submit) ) {
				String succesResult = intent.getStringExtra("succesResult");
				String errorResult = intent.getStringExtra("errorResult");
				JSONArray succesAisle = JSONArray.parseArray(intent.getStringExtra("succesAisle"));
				JSONArray errorAisle = JSONArray.parseArray(intent.getStringExtra("errorAisle"));
				String time = intent.getStringExtra("time");
				/*上报出货信息*/
				String sysorderNo = new BindingManager().getSysorderNo(getApplicationContext());
				new OutGoodsSubmitAction().outGoodsSubmitObject(getApplicationContext(), sysorderNo, succesResult,errorResult,time);
				/*更新库存*/
				DBManager.updateAisleInfoStock(getApplicationContext(),succesAisle,errorAisle);
			}else if ( StringUtil.equals(action, Resources.download_adv)){
				String taskId = intent.getStringExtra("taskId");
				/*请求广告信息*/
				new RequestAdvAction().requestAdvObject(getApplicationContext(),taskId);
			}else if ( StringUtil.equals(action, Resources.status_record)){
				String taskId = intent.getStringExtra("taskId");
				new UploadMachineStatus().uploadMachineStatusObject(getApplicationContext(),taskId);
			}else if ( StringUtil.equals(action, Resources.file_upload)){
				String taskId = intent.getStringExtra("taskId");
				String type = intent.getStringExtra("type");
				new UploadFileAction().uploadFileObject(getApplicationContext(),taskId,type);
			}else if ( StringUtil.equals(action, Resources.AlarmManager)){
				ArrayList<Adv> list = new ArrayList<>();
				list = DBManager.getAdvsInfo(getApplicationContext());
				for(int i=0;i<list.size();i++){
					Adv adv = list.get(i);
					String[] time = adv.getPlayTime().split(" - ");
					if(time.length == 2){
						Date currentTime = new Date();
						if (/*(currentTime.getTime() - DateUtil.parsetime3(time[0]).getTime()) <= 0 ||*/ (DateUtil.parsetime3(time[1]).getTime() - currentTime.getTime()) <= 0) {
							File file = new File(adv.getLocalUrl());
							if(file.exists()){
								file.delete();//定时查看广告是否过期，过期删除
								DBManager.deletAdvInfo(getApplicationContext(),adv.getAdId());
							}
						}
					}
				}
			}else if ( StringUtil.equals(action, Resources.play_voice) ) {
				String voiceId = intent.getStringExtra("voiceId");
				if (StringUtil.isNotNull(voiceId)) {
					if ( mp!=null ) {
						mp.reset();
						mp.release();
					}
					mp = MediaPlayer.create(getApplicationContext(), Integer.parseInt(voiceId));
					mp.start();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
}
