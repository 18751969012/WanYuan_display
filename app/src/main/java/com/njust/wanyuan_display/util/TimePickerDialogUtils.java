package com.njust.wanyuan_display.util;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.broadcast.BootBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;


/**
 * 自定义一个闹钟设置函数，用于设定定时闹钟，周期性执行长时间定时任务
 */
public class TimePickerDialogUtils {

	private Context mContext;
	private AlertDialog.Builder mAlertDialog;
	private int mHour, mMinute;
	private TimePickerDialogInterface timePickerDialogInterface;
	private TimePicker mTimePicker;
	private DatePicker mDatePicker;
	private int mTag = 0;
	private int mYear, mDay, mMonth;

	public TimePickerDialogUtils(Context context,TimePickerDialogInterface timePickerDialogInterface ) {
		super();
		mContext = context;
		this.timePickerDialogInterface = timePickerDialogInterface;
	}


	/**
	 * 创建dialog
	 */
	private void initDialog(View view) {
		mAlertDialog.setPositiveButton("确定",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						if (mTag == 0) {
							getTimePickerValue();
						} else if (mTag == 1) {
							getDatePickerValue();
						} else if (mTag == 2) {
							getDatePickerValue();
							getTimePickerValue();
						}
						timePickerDialogInterface.positiveListener();

					}
				});
		mAlertDialog.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						timePickerDialogInterface.negativeListener();
						dialog.dismiss();
					}
				});
		mAlertDialog.setView(view);
	}

	/**
	 * 显示时间选择器
	 */
	public void showTimePickerDialog() {
		mTag=0;
		View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_time_picker, null);
		mTimePicker = (TimePicker) inflate.findViewById(R.id.dateAndTimePicker_timePicker);
		mTimePicker.setIs24HourView(true);
		resizePikcer(mTimePicker);
		mAlertDialog = new AlertDialog.Builder(mContext);
		mAlertDialog.setTitle("选择时间");
		initDialog(inflate);
		mAlertDialog.show();

	}

	/**
	 * 显示日期选择器
	 */
	public void showDatePickerDialog() {
		mTag=1;
		View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_date_picker, null);
		mDatePicker = (DatePicker) inflate.findViewById(R.id.dateAndTimePicker_datePicker);
		resizePikcer(mDatePicker);
		mAlertDialog = new AlertDialog.Builder(mContext);
		mAlertDialog.setTitle("选择时间");
		initDialog(inflate);
		mAlertDialog.show();
	}

	/*
    * 调整numberpicker大小
    */
	private void resizeNumberPicker(NumberPicker np) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(10, 0, 10, 0);
		np.setLayoutParams(params);
	}

	/**
	 * 调整FrameLayout大小
	 *
	 * @param tp
	 */
	private void resizePikcer(FrameLayout tp) {
		List<NumberPicker> npList = findNumberPicker(tp);
		for (NumberPicker np : npList) {
			resizeNumberPicker(np);
		}
	}

	/**
	 * 得到viewGroup里面的numberpicker组件
	 *
	 * @param viewGroup
	 * @return
	 */
	private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
		List<NumberPicker> npList = new ArrayList<NumberPicker>();
		View child = null;
		if (null != viewGroup) {
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				child = viewGroup.getChildAt(i);
				if (child instanceof NumberPicker) {
					npList.add((NumberPicker) child);
				} else if (child instanceof LinearLayout) {
					List<NumberPicker> result = findNumberPicker((ViewGroup) child);
					if (result.size() > 0) {
						return result;
					}
				}
			}
		}
		return npList;
	}

	public int getYear() {
		return mYear;
	}

	public int getDay() {
		return mDay;
	}

	public int getMonth() {
		//返回的时间是0-11
		return mMonth+1;
	}

	public int getMinute() {
		return mMinute;
	}

	public int getHour() {
		return mHour;
	}

	/**
	 * 获取日期选择的值
	 */
	private void getDatePickerValue() {
		mYear = mDatePicker.getYear();
		mMonth = mDatePicker.getMonth();
		mDay= mDatePicker.getDayOfMonth();
	}

	/**
	 * 获取时间选择的值
	 */
	private void getTimePickerValue() {
		// api23这两个方法过时
		mHour = mTimePicker.getCurrentHour();// timePicker.getHour();
		mMinute = mTimePicker.getCurrentMinute();// timePicker.getMinute();
	}



	public interface TimePickerDialogInterface {
		public void positiveListener();
		public void negativeListener();
	}

}

