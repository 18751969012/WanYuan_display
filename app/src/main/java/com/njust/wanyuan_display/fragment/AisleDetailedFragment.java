package com.njust.wanyuan_display.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.adapter.AisleSettingAdapter;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.greenDao.AisleInfo;
import com.njust.wanyuan_display.util.ToastManager;

import org.apache.log4j.Logger;

import java.util.ArrayList;

public class AisleDetailedFragment extends Fragment implements View.OnClickListener {

    private static final Logger log = Logger.getLogger(AisleDetailedFragment.class);

    private RecyclerView aisleSettingDetailed;
    private AisleSettingAdapter mAisleSettingAdapter;
    private ArrayList<AisleInfo> dataList = new ArrayList<>();
    private Button save;

    private int[] motorType = new int[]{1,1,1,1,1,1,1,1,1,1,1,1};
    private String danTanHuang = "单弹簧";
    private String shuangTanHuang = "双弹簧";
    private String zhaiLvDai = "窄履带";
    private String kuanLvDai = "宽履带";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //让碎片加载一个布局
        View view = inflater.inflate(R.layout.fragment_aisle_detailed, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        save = (Button) view.findViewById(R.id.save);
        save.setOnClickListener(this);
        dataList.clear();
        dataList = DBManager.getAisleInfoList(getContext());
        aisleSettingDetailed = (RecyclerView) view.findViewById(R.id.manager_aisle_detailed);
        aisleSettingDetailed.setLayoutManager(new LinearLayoutManager(getContext()));
        mAisleSettingAdapter = new AisleSettingAdapter(this,dataList);
        aisleSettingDetailed.setAdapter(mAisleSettingAdapter);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                DBManager.saveAisleDetaildeManager(getContext(),dataList);
                ToastManager.getInstance().showToast(getContext(), "保存成功", Toast.LENGTH_LONG);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataList.clear();
        mAisleSettingAdapter.notifyDataSetChanged();
        Log.w("happy", "onDestroy");
    }
}
