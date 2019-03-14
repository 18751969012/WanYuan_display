package com.njust.wanyuan_display.config;

import android.os.Environment;

/**
 * 常量资源类
 */
public class Resources {

	/***************文档位置***************/
	
	public static final String adv_filepath = "/njust_display/adv/";

	public static final String adv_local_filepath = "/njust_display/adv_local/";
	
	public static final String log_filepath = "/njust_display/log/";
	
	public static final String db_filepath = "/njust_display/db/";
	
	public static final String app_filepath = "/njust_display/app/";
	
	public static final String xml_filepath = "/njust_display/xml/";
	
	public static final String goods_filepath = "/njust_display/goods/";

	public static final String QRcode_filepath = "/njust_display/qrcode/";

	public static String appURL() {
		return Environment.getExternalStorageDirectory() + Resources.app_filepath;
	}

	public static String advURL() {
		return Environment.getExternalStorageDirectory() + Resources.adv_filepath;
	}
	public static String adv_localURL() {
		return Environment.getExternalStorageDirectory() + Resources.adv_local_filepath;
	}

	public static String dbURL() {
		return Environment.getExternalStorageDirectory() + Resources.db_filepath;
	}

	public static String goodsURL() {
		return Environment.getExternalStorageDirectory() + Resources.goods_filepath;
	}

	public static String logURL() {
		return Environment.getExternalStorageDirectory() + Resources.log_filepath;
	}

	public static String xmlURL() {
		return Environment.getExternalStorageDirectory() + Resources.xml_filepath;
	}

	public static String QRcodeURL() {
		return Environment.getExternalStorageDirectory() + Resources.QRcode_filepath;
	}

	/***************http地址***************/
	public static String public_web_sites = "http://47.102.41.128:8080/vendor/";

	public static String get_token = public_web_sites+"login.do";

	public static String init_machine_url_njust = public_web_sites+"checklogin.do";

	public static String open_machine_url_njust = public_web_sites+"http/bootReport.do";

	public static String heartbeat_url_njust = public_web_sites+"http/heartbeat.do";

	public static String nonCash_qrcode_url_njust = public_web_sites+"http/codeRequest.do";

	public static String nonCash_query_url_njust =public_web_sites+"http/payResultQuery.do";

//	public static String nonCash_query_url_njust =  public_web_sites+"http/payResultQuery.do";

	public static String nonCash_close_order_url_njust =public_web_sites+"http/tradeCancel.do";

	public static String request_goods_bank_url_njust = public_web_sites+"http/goodsRequest.do";

	public static String local_upload_replenish_url_njust = public_web_sites+"http/replenishReport.do";

	public static String request_replenish_url_njust = public_web_sites+"http/requestReplenishInfo.do";

	public static String request_adv_url_njust = public_web_sites+"http/adPush.do";

	public static String nonCash_finish_outgoods_submit_url_njust = public_web_sites+"http/shippingReport.do";

	public static String upload_machine_status = public_web_sites+"http/machineStatusReport.do";

	public static String upload_file_url_njust = public_web_sites+"";

	/***************默认后台登录账号密码***************/
	public static final String initial_login_name = "123456";

	public static final String initial_login_password = "888888";

	/***************网络状态***************/

	public static String network_change = "network_change";

	public static String domain = "vm.wanyuantech.com";

	/***************开机上报***************/

	public static String open_machine = "open_machine";

	/***************心跳***************/

	public static String status_record = "status_record";

	public static String download_adv = "download_adv";

	public static String update_AisleInfo = "update_AisleInfo";

	public static String load_machineShelf = "load_machineShelf";

	public static String load_goods = "load_goods";

	public static String file_upload = "file_upload";


	/***************补货相关***************/
	public static String request_goods_bank = "request_goods_bank";

	public static String request_goods_bank_done = "request_goods_bank_done";

	public static String local_replenish_goods_upload = "local_replenish_goods_upload";

	public static String local_replenish_goods_upload_done = "local_replenish_goods_upload_done";

	/***************支付相关***************/

	public static String alipay_qrcode = "alipay_qrcode";

	public static String weixin_qrcode = "weixin_qrcode";

	public static String alipay_qrcode_show = "alipay_qrcode_show";

	public static String weixin_qrcode_show = "weixin_qrcode_show";


	/***************出货相关***************/

	public static String out_goods_start = "out_goods_start";

	public static String out_goods_end = "out_goods_end";

	public static String out_goods_result_submit = "out_goods_result_submit";

	public static String njust_outgoods_complete = "njust_outgoods_complete";



	/***************其他***************/

	public static String AlarmManager = "AlarmManager";

	public static String play_voice = "play_voice";

	public static String app_name = "万沅售卖机";

	public static String test = "test";





	public static String finish_order_alipay = "Finish_Order_Alipay";

	public static String finish_order_weixin = "Finish_Order_Weixin";

	public static String finish_order_cash = "Finish_Order_Cash";

	public static String test_network = "Test_Network";

	public static String test_finish = "Test_Finish";

	public static String test_result = "Test_Result";

	public static String update_huodao = "Update_Huodao";

	public static String replenish_huodao = "Replenish_Huodao";

	public static String Submit_Replenish_Huodao = "Submit_Replenish_Huodao";

	public static String finish_replenishfill_huodao = "Finish_Replenishfill_Huodao";

	public static String begin_deliver_Goods = "Avm.START_OUTGOODS";

	public static String poll_huodao = "Poll_Huodao";

	public static String poll_stop = "Poll_Stop";

	public static String poll_temperature = "Poll_Temperature";

	public static String temperature_change = "Temperature_Change";

	public static String poll_set_temperature = "Poll_Set_Temperature";

	public static String buy_finish = "Buy_Finish";

	public static String control_light =  "Control_Light";


	/**
	 * SP文件名
	 */
	public static String SP_BINDING_INFO = "binding_info";

}