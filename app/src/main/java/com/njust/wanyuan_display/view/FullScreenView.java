package com.njust.wanyuan_display.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

//重写VideoView，让视频能够全屏播放
public class FullScreenView extends VideoView {
	
	private int mVideoWidth;
	private int mVideoHeight;
	
	public FullScreenView(Context context) {
		super(context);
	}
	
	public FullScreenView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public FullScreenView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 下面的代码是让视频的播放的长宽是根据你设置的参数来决定
		
		int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
		int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}
	
}
