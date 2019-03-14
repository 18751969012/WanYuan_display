package com.njust.wanyuan_display.fragment;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.activity.BindingActivity;
import com.njust.wanyuan_display.activity.ManagerAdvanceSettingsActivity;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.util.ToastManager;

import org.apache.log4j.Logger;

import java.text.DecimalFormat;

public class AdvanceTempFragment extends Fragment implements View.OnClickListener {

    private static final Logger log = Logger.getLogger(AdvanceTempFragment.class);
    private TextView temp_minus_TV,temp_setting,temp_add_TV;
    private EditText compressor_max_continuous_worktime;
    private Button user_defined;
    private DecimalFormat df   =   new DecimalFormat("#####0.0");


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //让碎片加载一个布局
        View view = inflater.inflate(R.layout.fragment_advance_temp, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        temp_minus_TV =   view.findViewById(R.id.temp_minus_TV);
        temp_minus_TV.setOnClickListener(this);
        temp_add_TV =   view.findViewById(R.id.temp_add_TV);
        temp_add_TV.setOnClickListener(this);
        temp_setting =   view.findViewById(R.id.temp_setting);
        if(!(new BindingManager().getTempControlHuiCha(getContext()).equals(""))){
            temp_setting.setText(df.format((double)Integer.parseInt(new BindingManager().getTempControlHuiCha(getContext()))/10));
        }else{
            temp_setting.setText("1.0");
        }
        compressor_max_continuous_worktime =   view.findViewById(R.id.compressor_max_continuous_worktime);
        if(!(new BindingManager().getCompressorMaxWorktime(getContext()).equals(""))){
            compressor_max_continuous_worktime.setText(new BindingManager().getCompressorMaxWorktime(getContext()));
        }else{
            compressor_max_continuous_worktime.setText("");
        }
        user_defined =   view.findViewById(R.id.user_defined);
        user_defined.setOnClickListener(this);
        if(new BindingManager().getTempUserDefined(getContext()).equals("open")){
            user_defined.setText(getResources().getString(R.string.user_defined_open));
            user_defined.setTextColor(getResources().getColor(R.color.green_1));
        }else{
            user_defined.setText(getResources().getString(R.string.user_defined_close));
            user_defined.setTextColor(getResources().getColor(R.color.black));
        }


        //重写主activity的onTouchEvent函数，并进行该Fragment的逻辑处理
        ManagerAdvanceSettingsActivity.MyTouchListener myTouchListener = new ManagerAdvanceSettingsActivity.MyTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）  
                    View v = getActivity().getCurrentFocus();
                    if (isShouldHideInput(v, event)) {
                        hideSoftInput(v.getWindowToken());
                    }
                }
            }
        };
        ((ManagerAdvanceSettingsActivity)this.getActivity()).registerMyTouchListener(myTouchListener);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.temp_minus_TV:
                if(Double.parseDouble(temp_setting.getText().toString().trim()) > 1.0 && Double.parseDouble(temp_setting.getText().toString().trim()) <= 5.0){
                    temp_setting.setText(df.format(Double.parseDouble(temp_setting.getText().toString().trim()) - 0.5));
                }else{
                    temp_setting.setText("5.0");
                }
                break;
            case R.id.temp_add_TV:
                if(Double.parseDouble(temp_setting.getText().toString().trim()) >= 1.0 && Double.parseDouble(temp_setting.getText().toString().trim()) < 5.0){
                    temp_setting.setText(df.format(Double.parseDouble(temp_setting.getText().toString().trim()) + 0.5));
                }else{
                    temp_setting.setText("1.0");
                }
                break;
            case R.id.user_defined:
                if(user_defined.getText().toString().equals(getResources().getString(R.string.user_defined_close))) {
                    int temp = (int) (Double.parseDouble(temp_setting.getText().toString().trim()) * 10);
                    if(!compressor_max_continuous_worktime.getText().toString().trim().equals("")){
                        int worktime = Integer.parseInt(compressor_max_continuous_worktime.getText().toString().trim());
                        if (temp >= 10 && temp <= 50 && worktime >=1 && worktime<=10) {
                            new BindingManager().setTempUserDefined(getContext(),String.valueOf(temp),String.valueOf(worktime),"open");
                            user_defined.setText(getResources().getString(R.string.user_defined_open));
                            user_defined.setTextColor(getResources().getColor(R.color.green_1));
                        } else {
                            ToastManager.getInstance().showToast(getContext(), "温控回差请输入1.0-5.0度，压缩机最长连续工作时间1-10小时", Toast.LENGTH_LONG);
                        }
                    }else{
                        ToastManager.getInstance().showToast(getContext(), "请输入压缩机最长连续工作时间，范围1-10小时", Toast.LENGTH_LONG);
                    }
                }else{
                    temp_setting.setText("1.0");
                    compressor_max_continuous_worktime.setText("");
                    new BindingManager().setTempUserDefined(getContext(),"","","close");
                    user_defined.setText(getResources().getString(R.string.user_defined_close));
                    user_defined.setTextColor(getResources().getColor(R.color.black));
                }
                break;
            default:
                break;
        }
    }


    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            return !(event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom);
        }// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (im != null) {
                im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
