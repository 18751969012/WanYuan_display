package com.njust.wanyuan_display.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.util.ToastManager;

import org.apache.log4j.Logger;

public class AisleRoughlyFragment extends Fragment implements View.OnClickListener {

    private static final Logger log = Logger.getLogger(AisleRoughlyFragment.class);
    private Button left_one_floor;
    private Button left_two_floor;
    private Button left_three_floor;
    private Button left_four_floor;
    private Button left_five_floor;
    private Button left_six_floor;
    private Button right_one_floor;
    private Button right_two_floor;
    private Button right_three_floor;
    private Button right_four_floor;
    private Button right_five_floor;
    private Button right_six_floor;
    private Button save;

    private int[] motorType = new int[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
    private String danTanHuang = "单弹簧";
    private String shuangTanHuang = "双弹簧";
    private String zhaiLvDai = "窄履带";
    private String kuanLvDai = "宽履带";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //让碎片加载一个布局
        View view = inflater.inflate(R.layout.fragment_aisle_roughly, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        left_one_floor = (Button) view.findViewById(R.id.left_one_floor);
        left_one_floor.setOnClickListener(this);
        left_two_floor = (Button) view.findViewById(R.id.left_two_floor);
        left_two_floor.setOnClickListener(this);
        left_three_floor = (Button) view.findViewById(R.id.left_three_floor);
        left_three_floor.setOnClickListener(this);
        left_four_floor = (Button) view.findViewById(R.id.left_four_floor);
        left_four_floor.setOnClickListener(this);
        left_five_floor = (Button) view.findViewById(R.id.left_five_floor);
        left_five_floor.setOnClickListener(this);
        left_six_floor = (Button) view.findViewById(R.id.left_six_floor);
        left_six_floor.setOnClickListener(this);
        right_one_floor = (Button) view.findViewById(R.id.right_one_floor);
        right_one_floor.setOnClickListener(this);
        right_two_floor = (Button) view.findViewById(R.id.right_two_floor);
        right_two_floor.setOnClickListener(this);
        right_three_floor = (Button) view.findViewById(R.id.right_three_floor);
        right_three_floor.setOnClickListener(this);
        right_four_floor = (Button) view.findViewById(R.id.right_four_floor);
        right_four_floor.setOnClickListener(this);
        right_five_floor = (Button) view.findViewById(R.id.right_five_floor);
        right_five_floor.setOnClickListener(this);
        right_six_floor = (Button) view.findViewById(R.id.right_six_floor);
        right_six_floor.setOnClickListener(this);
        save = (Button) view.findViewById(R.id.save);
        save.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_one_floor:
                if(motorType[0] == 1){
                    motorType[0]++;
                    left_one_floor.setText(shuangTanHuang);
                }else if(motorType[0] == 2){
                    motorType[0]++;
                    left_one_floor.setText(zhaiLvDai);
                }else if(motorType[0] == 3){
                    motorType[0]++;
                    left_one_floor.setText(kuanLvDai);
                }else{
                    motorType[0] = 1;
                    left_one_floor.setText(danTanHuang);
                }
                break;
            case R.id.left_two_floor:
                if(motorType[1] == 1){
                    motorType[1]++;
                    left_two_floor.setText(shuangTanHuang);
                }else if(motorType[1] == 2){
                    motorType[1]++;
                    left_two_floor.setText(zhaiLvDai);
                }else if(motorType[1] == 3){
                    motorType[1]++;
                    left_two_floor.setText(kuanLvDai);
                }else{
                    motorType[1] = 1;
                    left_two_floor.setText(danTanHuang);
                }
                break;
            case R.id.left_three_floor:
                if(motorType[2] == 1){
                    motorType[2]++;
                    left_three_floor.setText(shuangTanHuang);
                }else if(motorType[2] == 2){
                    motorType[2]++;
                    left_three_floor.setText(zhaiLvDai);
                }else if(motorType[2] == 3){
                    motorType[2]++;
                    left_three_floor.setText(kuanLvDai);
                }else{
                    motorType[2] = 1;
                    left_three_floor.setText(danTanHuang);
                }
                break;
            case R.id.left_four_floor:
                if(motorType[3] == 1){
                    motorType[3]++;
                    left_four_floor.setText(shuangTanHuang);
                }else if(motorType[3] == 2){
                    motorType[3]++;
                    left_four_floor.setText(zhaiLvDai);
                }else if(motorType[3] == 3){
                    motorType[3]++;
                    left_four_floor.setText(kuanLvDai);
                }else{
                    motorType[3] = 1;
                    left_four_floor.setText(danTanHuang);
                }
                break;
            case R.id.left_five_floor:
                if(motorType[4] == 1){
                    motorType[4]++;
                    left_five_floor.setText(shuangTanHuang);
                }else if(motorType[4] == 2){
                    motorType[4]++;
                    left_five_floor.setText(zhaiLvDai);
                }else if(motorType[4] == 3){
                    motorType[4]++;
                    left_five_floor.setText(kuanLvDai);
                }else{
                    motorType[4] = 1;
                    left_five_floor.setText(danTanHuang);
                }
                break;
            case R.id.left_six_floor:
                if(motorType[5] == 1){
                    motorType[5]++;
                    left_six_floor.setText(shuangTanHuang);
                }else if(motorType[5] == 2){
                    motorType[5]++;
                    left_six_floor.setText(zhaiLvDai);
                }else if(motorType[5] == 3){
                    motorType[5]++;
                    left_six_floor.setText(kuanLvDai);
                }else{
                    motorType[5] = 1;
                    left_six_floor.setText(danTanHuang);
                }
                break;
            case R.id.right_one_floor:
                if(motorType[10] == 1){
                    motorType[10]++;
                    right_one_floor.setText(shuangTanHuang);
                }else if(motorType[10] == 2){
                    motorType[10]++;
                    right_one_floor.setText(zhaiLvDai);
                }else if(motorType[10] == 3){
                    motorType[10]++;
                    right_one_floor.setText(kuanLvDai);
                }else{
                    motorType[10] = 1;
                    right_one_floor.setText(danTanHuang);
                }
                break;
            case R.id.right_two_floor:
                if(motorType[11] == 1){
                    motorType[11]++;
                    right_two_floor.setText(shuangTanHuang);
                }else if(motorType[11] == 2){
                    motorType[11]++;
                    right_two_floor.setText(zhaiLvDai);
                }else if(motorType[11] == 3){
                    motorType[11]++;
                    right_two_floor.setText(kuanLvDai);
                }else{
                    motorType[11] = 1;
                    right_two_floor.setText(danTanHuang);
                }
                break;
            case R.id.right_three_floor:
                if(motorType[12] == 1){
                    motorType[12]++;
                    right_three_floor.setText(shuangTanHuang);
                }else if(motorType[12] == 2){
                    motorType[12]++;
                    right_three_floor.setText(zhaiLvDai);
                }else if(motorType[12] == 3){
                    motorType[12]++;
                    right_three_floor.setText(kuanLvDai);
                }else{
                    motorType[12] = 1;
                    right_three_floor.setText(danTanHuang);
                }
                break;
            case R.id.right_four_floor:
                if(motorType[13] == 1){
                    motorType[13]++;
                    right_four_floor.setText(shuangTanHuang);
                }else if(motorType[13] == 2){
                    motorType[13]++;
                    right_four_floor.setText(zhaiLvDai);
                }else if(motorType[13] == 3){
                    motorType[13]++;
                    right_four_floor.setText(kuanLvDai);
                }else{
                    motorType[13] = 1;
                    right_four_floor.setText(danTanHuang);
                }
                break;
            case R.id.right_five_floor:
                if(motorType[14] == 1){
                    motorType[14]++;
                    right_five_floor.setText(shuangTanHuang);
                }else if(motorType[14] == 2){
                    motorType[14]++;
                    right_five_floor.setText(zhaiLvDai);
                }else if(motorType[14] == 3){
                    motorType[14]++;
                    right_five_floor.setText(kuanLvDai);
                }else{
                    motorType[14] = 1;
                    right_five_floor.setText(danTanHuang);
                }
                break;
            case R.id.right_six_floor:
                if(motorType[15] == 1){
                    motorType[15]++;
                    right_six_floor.setText(shuangTanHuang);
                }else if(motorType[15] == 2){
                    motorType[15]++;
                    right_six_floor.setText(zhaiLvDai);
                }else if(motorType[15] == 3){
                    motorType[15]++;
                    right_six_floor.setText(kuanLvDai);
                }else{
                    motorType[15] = 1;
                    right_six_floor.setText(danTanHuang);
                }
                break;
            case R.id.save:
                int state = 0;
                for(int i=1;i<=200;i++){
                    if((i>60&&i<=100) || (i>160&&i<=200)){
                        state = 1;
                    }else{
                        state = 0;
                    }
                    DBManager.saveAisleInfoManager(getContext(),i,state,motorType[(i-1)/10]);
                    Log.w("happy", "positionID:"+i+"motorType:"+motorType[(i-1)/10]);
                }
                ToastManager.getInstance().showToast(getContext(), "保存成功", Toast.LENGTH_LONG);
                break;
            default:
                break;
        }
    }

}
