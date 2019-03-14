package com.njust.wanyuan_display.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.njust.wanyuan_display.R;


public class PayDialog extends Dialog  {

	private Button confirm;
	private TextView titleTv,messageTv;
	private String titleStr,messageStr,confirmStr;

    /*  -------------------------------- 接口监听 -------------------------------------  */

	private confirmOnclickListener confirmOnclickListener;

	public interface confirmOnclickListener {
		void onConfirmClick();
	}


	public void setConfirmOnclickListener(String str, confirmOnclickListener confirmOnclickListener) {
		if (str != null) {
			confirmStr = str;
		}
		this.confirmOnclickListener = confirmOnclickListener;
	}


    /*  ---------------------------------- 构造方法 -------------------------------------  */

	public PayDialog(Context context) {
		super(context, R.style.MyDialog);//风格主题
	}


    /*  ---------------------------------- onCreate-------------------------------------  */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_pay_dialog);//自定义布局

        setCanceledOnTouchOutside(false);//按空白处不能取消动画

		//初始化界面控件
		initView();
		//初始化界面数据
		initData();
		//初始化界面控件的事件
		initEvent();

	}

	/**
	 * 初始化界面的确定和取消监听器
	 */
	private void initEvent() {
		//设置确定按钮被点击后，向外界提供监听
		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (confirmOnclickListener != null) {
					confirmOnclickListener.onConfirmClick();
				}
			}
		});
	}

	/**
	 * 初始化界面控件的显示数据
	 */
	private void initData() {
		//如果用户自定了title和message
		if (titleStr != null) {
			titleTv.setText(titleStr);
		}
		if (messageStr != null) {
			messageTv.setText(messageStr);
		}
		//如果设置按钮的文字
		if (confirmStr != null) {
			confirm.setText(confirmStr);
		}
	}

	/**
	 * 初始化界面控件
	 */
	private void initView() {
		confirm = (Button) findViewById(R.id.confirm);
		titleTv = (TextView) findViewById(R.id.title);
		messageTv = (TextView) findViewById(R.id.message);
	}

	/*  ---------------------------------- set方法 传值-------------------------------------  */
	//为外界设置一些public 公开的方法，来向自定义的dialog传递值
	public void setTitle(String title) {
		titleStr = title;
	}

	public void setMessage(String message) {
		messageStr = message;
	}
	
}