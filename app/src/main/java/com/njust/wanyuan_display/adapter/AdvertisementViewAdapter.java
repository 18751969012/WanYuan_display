package com.njust.wanyuan_display.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.njust.wanyuan_display.greenDao.Adv;
import com.njust.wanyuan_display.view.FullScreenView;

import java.util.ArrayList;
import java.util.List;

//import android.widget.VideoView;

public class AdvertisementViewAdapter extends PagerAdapter {

    private Context context;
    private List<Adv> listBean;
    private SparseArray<FullScreenView> videoList = new SparseArray<>();
    private FullScreenView currentFullScreenView;
    private boolean firstPlay = true;

    public AdvertisementViewAdapter(Activity context, List<Adv> list) {
        this.context = context;
        if (list == null || list.size() == 0) {
            this.listBean = new ArrayList<>();
        } else {
            this.listBean = list;
        }
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        Log.w("happy","position："+position);
        if (listBean.get(position).getAdType() == 1) {//图片
            ImageView imageView = new ImageView(context);
            Glide.with(context).load(listBean.get(position).getLocalUrl())
//                    .skipMemoryCache(true)
                    .into(imageView);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            container.addView(imageView);
            return imageView;
        }else{//视频
            FullScreenView videoView = new FullScreenView(context);
//            videoView.setVideoURI(Uri.parse(listBean.get(position).getLocalUrl()));
            videoView.setVideoPath(listBean.get(position).getLocalUrl());
            if(position == 0 && firstPlay){//只有第一次创建页面时首播是视频才开始，因为addOnPageChangeListener无反应
                videoView.start();
                firstPlay = false;
            }
            videoList.append(position,videoView);
            container.addView(videoView);
            return videoView;
        }
    }
    /*
    * 当视频广告出现在当前主页面后再开始播放，而不是开始预缓存就开始播放
    */
    public void startVideo(int position) {
        videoList.get(position).start();
    }
    /*
    * 判断当前视频广告的播放状态
    */
    public boolean isVideoPlaying(int position) {
        return videoList.get(position).isPlaying();
    }
    /*
    * 停止当前预缓存列表中所有的视频，除了当前正在播放的视频
    */
    public void pauseVideo(int position) {
        if(videoList.size() > 0){
            for(int i=0;i<videoList.size();i++){
                if(videoList.keyAt(i) != position){
                    if(videoList.valueAt(i).isPlaying()){
                        videoList.valueAt(i).resume();
                    }
                    videoList.valueAt(i).pause();
                }
            }
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        videoList.remove(position);
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return listBean.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (View) object;
    }
}
