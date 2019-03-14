package com.njust.wanyuan_display.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.activity.ManagerAdvanceSettingsActivity;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.util.FileUtil;
import com.njust.wanyuan_display.util.ToastManager;

import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvanceRestsFragment extends Fragment implements View.OnClickListener {

    private static final Logger log = Logger.getLogger(AdvanceRestsFragment.class);
    private EditText delay_setting_ET;
    private Button delay_setting_Bt,delet_all_local_adv;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //让碎片加载一个布局
        View view = inflater.inflate(R.layout.fragment_advance_rests, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        delay_setting_ET =  (EditText) view.findViewById(R.id.delay_setting_ET);
        delay_setting_ET.setText(new BindingManager().getDelayGetDoor(getContext()));

        delay_setting_Bt =  view.findViewById(R.id.delay_setting_Bt);
        delay_setting_Bt.setOnClickListener(this);

        delet_all_local_adv =  view.findViewById(R.id.delet_all_local_adv);
        delet_all_local_adv.setOnClickListener(this);

        RadioGroup rg = view.findViewById(R.id.rg);
        final RadioButton ten_line = view.findViewById(R.id.ten_line);
        final RadioButton five_line = view.findViewById(R.id.five_line);
        if(new BindingManager().getWideCrawlerWiring(getContext()).equals("13579_1357")){
            ten_line.setChecked(true);
        }else{
            five_line.setChecked(true);
        }
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                if(checkedId == ten_line.getId()){
                    new BindingManager().setWideCrawlerWiring(getContext(),"13579_1357");
                }else if(checkedId == five_line.getId()){
                    new BindingManager().setWideCrawlerWiring(getContext(),"12345_2345");
                }
            }
        });

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
            case R.id.delay_setting_Bt:
                String delay_setting = delay_setting_ET.getText().toString().trim();
                String txtY4 = delay_setting_ET.getText().toString();
                Pattern pY4 = Pattern.compile("[0-9]*");
                Matcher mY4 = pY4.matcher(txtY4);
                if(mY4.matches() && !delay_setting.equals("")){//输入的数字
                    new BindingManager().setDelayGetDoor(getContext(),delay_setting);
                    ToastManager.getInstance().showToast(getContext(),"保存成功", Toast.LENGTH_LONG);
                }else{
                    ToastManager.getInstance().showToast(getContext(),"请输入数字", Toast.LENGTH_LONG);
                    break;
                }
                break;
            case R.id.delet_all_local_adv:
                AlertDialog.Builder message = new AlertDialog.Builder(getContext());
                message.setTitle("注意！");
                message.setMessage("确认删除所有本地广告？删除无法撤销");
                message.setPositiveButton("确定",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface arg0, int arg1){
                        DBManager.deletAllAdvInfo(getContext());
                        FileUtil.deletAllAdvFiles(Resources.advURL());
                        FileUtil.deletAllAdvFiles(Resources.adv_localURL());
                        FileUtil.copyToStorage(getContext(), "background.png", Resources.advURL()+"background.png");
                        ToastManager.getInstance().showToast(getContext(),"删除完毕", Toast.LENGTH_LONG);
                    }
                });
                message.setNegativeButton("取消",null);
                message.show();
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
