package com.njust.wanyuan_display.config;
/**
 * 常量资源类
 */
public class Constant {

	/**测试*/
	public static int test = 0;
	/**支付宝*/
	public static int alipay = 1;
	/**微信*/
	public static int weixin = 2;

	/**货道状态正常*/
	public static int aisleState_normal = 0;
	/**货道状态锁死不让购买*/
	public static int aisleState_locked = 1;
	/**货道状态故障*/
	public static int aisleState_fault = 2;

	/**订单未支付*/
	public static int unPay = 0;
	/**订单已支付*/
	public static int hasPay = 1;

	/**出货未完成*/
	public static int deliveryUnComplete = 0;
	/**出货完成*/
	public static int deliveryComplete = 1;

	/**本次出货故障中途停止*/
	public static int deliveryError = 1;
	/**本次出货无故障*/
	public static int deliveryUnError = 0;

	/**界面商品个数*/
	public static int pagesize = 12;
	

	
	/**出货成功*/
	public static int delivery_success = 2;
	/**出货失败*/
	public static int delivery_fail = 4;


	/**下载成功*/
	public static int download_success = 0;
	/**下载失败*/
	public static int download_fail = 1;

	/**货道信息更新结果_成功*/
	public static int machineShelf_update_success = 0;
	/**货道信息更新结果_失败*/
	public static int machineShelf_update_fail = 1;

	/**广告图片*/
	public static int adv_picture = 1;
	/**广告视频*/
	public static int adv_video = 2;

	/**商品*/
	public static int goods = 3;
	/**安装包*/
	public static int app = 4;

	/**上传类型—数据库*/
	public static int upload_db = 2;
	/**上传类型—日志*/
	public static int upload_log= 1;




	
	/**是*/
	public static int yes = 1;
	/**否*/
	public static int no = 0;
	
	/**正常*/
	public static int normal = 0;
	/**故障*/
	public static int error = 1;
	/**未上送*/
	public static int unkwon = 2;


	
	/**温度常温*/
	public static String temp_normal = "normal";
	/**温度加热*/
	public static String hot = "hot";
	/**温度制冷*/
	public static String freeze = "freeze";
	
	/**开启*/
	public static String open = "open";
	/**禁用*/
	public static String forbidden = "forbidden";
	
	/**最小现金*/
	public static String min_cash = "5";
	
	/**反向*/
	public static String reverse = "reverse";
	/**正向*/
	public static String positive = "positive";








	/**等待*/
	public static int wait = 0;
	/**移层_货道位置*/
	public static int moveFloor = 1;
	/**移层_出货口位置*/
	public static int moveFloorOut = 2;
	/**水平移位_错位*/
	public static int moveHorizontal = 3;
	/**水平移位_推入出货口*/
	public static int moveHorizontalOut = 4;
	/**开出货门*/
	public static int openOutGoodsDoor = 5;
	/**关出货门*/
	public static int closeOutGoodsDoor = 6;
	/**云台归位*/
	public static int homing = 7;
	/**查询移层_货道位置*/
	public static int queryMoveFloor = 8;
	/**查询移层_出货口位置*/
	public static int queryMoveFloorOut = 9;
	/**查询水平移位_错位*/
	public static int queryMoveHorizontal = 10;
	/**查询水平移位_推入出货口*/
	public static int queryMoveHorizontalOut = 11;
	/**查询开出货门*/
	public static int queryOpenOutGoodsDoor = 12;
	/**查询关出货门*/
	public static int queryCloseOutGoodsDoor = 13;
	/**查询云台归位*/
	public static int queryHoming = 14;
	/**移层_出货口位置（折返动作校正传送带导致的Y轴误差）*/
	public static int moveFloorOutRevise = 15;
	/**查询移层_出货口位置（折返动作校正传送带导致的Y轴误差）*/
	public static int queryMoveFloorOutRevise = 16;



	/**推送货*/
	public static int pushGoods = 1;
	/**查询推送货*/
	public static int queryPushGoods = 2;


	/**开取货门*/
	public static int openGetGoodsDoor = 1;
	/**关取货门*/
	public static int closeGetGoodsDoor = 2;
	/**开落货门*/
	public static int openDropGoodsDoor = 3;
	/**关落货门*/
	public static int closeDropGoodsDoor = 4;
	/**查询开取货门*/
	public static int queryOpenGetGoodsDoor = 5;
	/**查询关取货门*/
	public static int queryCloseGetGoodsDoor = 6;
	/**查询开落货门*/
	public static int queryOpenDropGoodsDoor = 7;
	/**查询关落货门*/
	public static int queryCloseDropGoodsDoor = 8;
	
}