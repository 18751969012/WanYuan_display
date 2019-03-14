package com.njust.wanyuan_display.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.activity.ManagerAisleSettingsActivity;
import com.njust.wanyuan_display.adapter.AisleSettingMergeAdapter;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.greenDao.AisleMerge;
import com.njust.wanyuan_display.util.ToastManager;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AisleMergeFragment extends Fragment implements View.OnClickListener {

    private static final Logger log = Logger.getLogger(AisleMergeFragment.class);

    private RecyclerView aisleSettingMerge;
    private AisleSettingMergeAdapter mAisleSettingMergeAdapter;
    private ArrayList<AisleMerge> dataList = new ArrayList<>();
    private EditText oddAisleID,evenAisleID;
    private Button merge;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //让碎片加载一个布局
        View view = inflater.inflate(R.layout.fragment_aisle_merge, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        dataList = DBManager.getAisleMerge(getContext());
        oddAisleID = (EditText) view.findViewById(R.id.oddAisleID);
        evenAisleID = (EditText) view.findViewById(R.id.evenAisleID);
        merge = (Button) view.findViewById(R.id.merge);
        merge.setOnClickListener(this);

        aisleSettingMerge = (RecyclerView) view.findViewById(R.id.manager_aisle_merge);
        aisleSettingMerge.setLayoutManager(new LinearLayoutManager(getContext()));
        mAisleSettingMergeAdapter = new AisleSettingMergeAdapter(this,dataList);
        aisleSettingMerge.setAdapter(mAisleSettingMergeAdapter);


        //重写主activity的onTouchEvent函数，并进行该Fragment的逻辑处理
        ManagerAisleSettingsActivity.MyTouchListener myTouchListener = new ManagerAisleSettingsActivity.MyTouchListener() {
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
        ((ManagerAisleSettingsActivity)this.getActivity()).registerMyTouchListener(myTouchListener);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.merge:
                String oddID = oddAisleID.getText().toString().trim();
                Pattern p1 = Pattern.compile("[0-9[ ]]*");
                Matcher m1 = p1.matcher(oddID);
                String evenID = evenAisleID.getText().toString().trim();
                Pattern p2 = Pattern.compile("[0-9[ ]]*");
                Matcher m2 = p2.matcher(evenID);
                if (m1.matches() && !oddAisleID.getText().toString().equals("") &&
                        m2.matches() && !evenAisleID.getText().toString().equals("") &&
                        Integer.parseInt(oddAisleID.getText().toString())>=1 && Integer.parseInt(oddAisleID.getText().toString())<=200 &&
                        Integer.parseInt(evenAisleID.getText().toString())>=1 && Integer.parseInt(evenAisleID.getText().toString())<=200) {
                    if(Integer.parseInt(oddAisleID.getText().toString()) < Integer.parseInt(evenAisleID.getText().toString()) &&
                            Integer.parseInt(evenAisleID.getText().toString()) - Integer.parseInt(oddAisleID.getText().toString())==1 &&
                            Integer.parseInt(oddAisleID.getText().toString())%2 != 0 && Integer.parseInt(evenAisleID.getText().toString())%2 == 0){
                        AisleMerge aisleMerge = new AisleMerge();
                        aisleMerge.setPositionID1(Integer.parseInt(oddAisleID.getText().toString()));
                        aisleMerge.setPositionID2(Integer.parseInt(evenAisleID.getText().toString()));
                        dataList.add(aisleMerge);
                        mAisleSettingMergeAdapter.notifyDataSetChanged();
                        DBManager.saveAisleMerge(getContext(), Integer.parseInt(oddAisleID.getText().toString()), Integer.parseInt(evenAisleID.getText().toString()));
                    }else{
                        ToastManager.getInstance().showToast(getContext(), "请输入两个相邻货道，左输入框为单数，右输入框为双数，且左小右大", Toast.LENGTH_LONG);
                    }
                }else{
                    ToastManager.getInstance().showToast(getContext(), "请输入1-200之间的数字", Toast.LENGTH_LONG);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void relieveMerge(AisleMerge aisleMerge){
        dataList.remove(aisleMerge);
        mAisleSettingMergeAdapter.notifyDataSetChanged();
        DBManager.deleteAisleMerge(getContext(),aisleMerge.getPositionID1(),aisleMerge.getPositionID2());
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
