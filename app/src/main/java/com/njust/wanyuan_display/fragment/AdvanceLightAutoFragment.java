package com.njust.wanyuan_display.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.service.HandlerService;
import com.njust.wanyuan_display.util.FileUtil;
import com.njust.wanyuan_display.util.TimePickerDialogUtils;
import com.njust.wanyuan_display.util.ToastManager;

import org.apache.log4j.Logger;

import java.util.List;

public class AdvanceLightAutoFragment extends Fragment implements View.OnClickListener {

    private static final Logger log = Logger.getLogger(AdvanceLightAutoFragment.class);
    private TextView auto_light_open_time_TV, auto_light_close_time_TV;
    private Button auto_light_BT;
    private TimePickerDialogUtils mTimePickerDialogUtils;
    private LocationManager locationManager;
    private String locationProvider;
    private TextView a;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //让碎片加载一个布局
        View view = inflater.inflate(R.layout.fragment_advance_light_auto, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        auto_light_BT = view.findViewById(R.id.auto_light_BT);
        auto_light_BT.setOnClickListener(this);
        if (new BindingManager().getAutoLight(getContext()).equals("open")) {
            auto_light_BT.setText(getResources().getString(R.string.auto_light_open));
            auto_light_BT.setTextColor(getResources().getColor(R.color.green_1));
        } else {
            auto_light_BT.setText(getResources().getString(R.string.auto_light_close));
            auto_light_BT.setTextColor(getResources().getColor(R.color.black));
        }
        auto_light_open_time_TV = view.findViewById(R.id.auto_light_open_time_TV);
        auto_light_open_time_TV.setOnClickListener(this);
        int[] time = new BindingManager().getAutoLightTime(getContext());
        if (time[0] != -1 && time[1] != -1) {
            auto_light_open_time_TV.setText("开启时间：" + time[0] + "时" + time[1] + "分");
        } else {
            auto_light_open_time_TV.setText("开启时间：null");
        }
        auto_light_close_time_TV = view.findViewById(R.id.auto_light_close_time_TV);
        auto_light_close_time_TV.setOnClickListener(this);
        if (time[2] != -1 && time[3] != -1) {
            auto_light_close_time_TV.setText("关闭时间：" + time[2] + "时" + time[3] + "分");
        } else {
            auto_light_close_time_TV.setText("关闭时间：null");
        }

//        a = view.findViewById(R.id.a);
//        a.setOnClickListener(this);
//        a.setText("aaaaaaaaaaaaaaaaaaaa");

//        HandlerService.a = true;
//        Intent handlerIntent = new Intent(getContext(), HandlerService.class);
//        handlerIntent.setAction(Resources.test);
//        getActivity().getApplicationContext().startService(handlerIntent);
    }





    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.auto_light_open_time_TV:
                mTimePickerDialogUtils = new TimePickerDialogUtils(this.getContext(),new TimePickerDialogUtils.TimePickerDialogInterface(){
                    @Override
                    public void positiveListener() {
                        int hour = mTimePickerDialogUtils.getHour();
                        int minute = mTimePickerDialogUtils.getMinute();
                        auto_light_open_time_TV.setText("开启时间：" + hour + "时" + minute  + "分");
                        new BindingManager().setAutoLightTime(getContext(),hour,minute,-1,-1);
                    }

                    //时间选择器-------取消
                    @Override
                    public void negativeListener() {

                    }
                });
                mTimePickerDialogUtils.showTimePickerDialog();
                break;
            case R.id.auto_light_close_time_TV:
                mTimePickerDialogUtils = new TimePickerDialogUtils(this.getContext(),new TimePickerDialogUtils.TimePickerDialogInterface(){
                    @Override
                    public void positiveListener() {
                        int hour = mTimePickerDialogUtils.getHour();
                        int minute = mTimePickerDialogUtils.getMinute();
                        auto_light_close_time_TV.setText("关闭时间：" + hour + "时" + minute  + "分");
                        new BindingManager().setAutoLightTime(getContext(),-1,-1,hour,minute);
                    }

                    //时间选择器-------取消
                    @Override
                    public void negativeListener() {

                    }
                });
                mTimePickerDialogUtils.showTimePickerDialog();
                break;
            case R.id.auto_light_BT:
                if(auto_light_BT.getText().toString().equals(getResources().getString(R.string.auto_light_open))){
                    auto_light_BT.setText(getResources().getString(R.string.auto_light_close));
                    auto_light_BT.setTextColor(getResources().getColor(R.color.black));
                    new BindingManager().setAutoLight(getContext(),"close");
                }else{
                    if(!auto_light_open_time_TV.getText().toString().equals("开启时间：null") && !auto_light_close_time_TV.getText().toString().equals("关闭时间：null")){
                        auto_light_BT.setText(getResources().getString(R.string.auto_light_open));
                        auto_light_BT.setTextColor(getResources().getColor(R.color.green_1));
                        new BindingManager().setAutoLight(getContext(),"open");
                    }else{
                        ToastManager.getInstance().showToast(getContext(),"请输入打开关闭时间", Toast.LENGTH_LONG);
                    }
                }
                break;
//            case R.id.a:
//                FileUtil.copyToStorage(getContext(), "7.mp4", Resources.adv_localURL()+"7.mp4");
//                FileUtil.copyToStorage(getContext(), "3.jpg", Resources.adv_localURL()+"3.jpg");
//                FileUtil.copyToStorage(getContext(), "4.jpg", Resources.adv_localURL()+"4.jpg");
//                break;
            default:
                break;
        }
    }
}
