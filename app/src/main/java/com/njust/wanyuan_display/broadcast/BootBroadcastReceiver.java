package com.njust.wanyuan_display.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import com.njust.wanyuan_display.activity.BindingActivity;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.service.HandlerService;
import com.njust.wanyuan_display.util.AlarmUtils;

import org.apache.log4j.Logger;

/**
 * 广播接收
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

	private final Logger log = Logger.getLogger(BootBroadcastReceiver.class);
	
	private String action_boot = "android.intent.action.BOOT_COMPLETED";

	private String action_connectivity_change = "android.net.conn.CONNECTIVITY_CHANGE";

	private String action_intent_AlarmManager = "intent.AlarmManager";

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			log.info( "bootbroadcast action:" + intent.getAction() );
			if (intent.getAction().equals(action_boot)) {/*自重启*/
				log.info("restart boot completed");
				Intent bootStartIntent = new Intent(context, BindingActivity.class);
				bootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(bootStartIntent);
			} else if (intent.getAction().equals(action_connectivity_change)) {/*网络状态改变*/
				log.info("network status change");
				Intent handlerIntent = new Intent(context, HandlerService.class);
				handlerIntent.setAction(Resources.network_change);
				context.startService(handlerIntent);
			}else if (intent.getAction().equals(action_intent_AlarmManager)) {/*定时任务到时间*/
				log.info("AlarmManager");
				//因为setWindow只执行一次，所以要重新定义闹钟实现循环。
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					AlarmUtils.setAlarmManager(context);
				}
				Intent handlerIntent = new Intent(context, HandlerService.class);
				handlerIntent.setAction(Resources.AlarmManager);
				context.startService(handlerIntent);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

}
