package com.njust.wanyuan_display.fragment;

import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.util.TimePickerDialogUtils;
import com.njust.wanyuan_display.util.ToastManager;

import org.apache.log4j.Logger;

public class AdvanceGoodsShowTypeFragment extends Fragment implements View.OnClickListener {

    private static final Logger log = Logger.getLogger(AdvanceGoodsShowTypeFragment.class);


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //让碎片加载一个布局
        View view = inflater.inflate(R.layout.fragment_advance_goods_show_type, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        RadioGroup rg = view.findViewById(R.id.rg);
        final RadioButton saleByCommodity = view.findViewById(R.id.saleByCommodity);
        final RadioButton saleByAisle = view.findViewById(R.id.saleByAisle);
        if(new BindingManager().getShopGoodsShowType(getContext()).equals("saleByCommodity")){
            saleByCommodity.setChecked(true);
        }else{
            saleByAisle.setChecked(true);
        }
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                if(checkedId == saleByCommodity.getId()){
                    new BindingManager().setShopGoodsShowType(getContext(),"saleByCommodity");
                }else if(checkedId == saleByAisle.getId()){
                    new BindingManager().setShopGoodsShowType(getContext(),"saleByAisle");
                }
            }
        });
    }





    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            default:
                break;
        }
    }
}
