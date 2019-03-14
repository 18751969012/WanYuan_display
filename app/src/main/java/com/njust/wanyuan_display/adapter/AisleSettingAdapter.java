package com.njust.wanyuan_display.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.fragment.AisleDetailedFragment;
import com.njust.wanyuan_display.greenDao.AisleInfo;

import java.text.NumberFormat;
import java.util.ArrayList;


public class AisleSettingAdapter extends RecyclerView.Adapter<AisleSettingAdapter.ViewHolder>{
    private AisleDetailedFragment mAisleDetailedFragment;
    private ArrayList<AisleInfo> dataList;
    private NumberFormat nf;
    private LayoutInflater mInflater;
    public AisleSettingAdapter(AisleDetailedFragment mAisleDetailedFragment, ArrayList<AisleInfo> dataList) {
        this.mAisleDetailedFragment = mAisleDetailedFragment;
        this.dataList = dataList;
        mInflater = LayoutInflater.from(mAisleDetailedFragment.getContext());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_manager_aisle_detail,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AisleInfo item = dataList.get(position);
        holder.bindData(item);
    }

    @Override
    public int getItemCount() {
        if(dataList==null) {
            return 0;
        }
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AisleInfo item;
        private TextView aisleID;
        private Button motorType, aisleState;
        private String motor, state;

        public ViewHolder(View itemView) {
            super(itemView);
            aisleID = (TextView) itemView.findViewById(R.id.aisleID);
            motorType = (Button) itemView.findViewById(R.id.motorType);
            motorType.setOnClickListener(this);
            aisleState = (Button) itemView.findViewById(R.id.aisleState);
            aisleState.setOnClickListener(this);
        }

        public void bindData(AisleInfo item) {
            this.item = item;
            switch (item.getMotorType()) {
                case 1:
                    motor = "单弹簧";
                    break;
                case 2:
                    motor = "双弹簧";
                    break;
                case 3:
                    motor = "窄履带";
                    break;
                case 4:
                    motor = "宽履带";
                    break;
                default:
                    break;
            }
            switch (item.getState()) {
                case 0:
                    state = "正常";
                    break;
                case 1:
                    state = "锁死";
                    break;
                case 2:
                    state = "故障";
                    break;
                default:
                    break;
            }
            aisleID.setText("货道" + item.getPositionID());
            motorType.setText(motor);
            aisleState.setText(state);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.motorType: {
                    if(motorType.getText().toString().equals("单弹簧")){
                        motorType.setText("双弹簧");
                        item.setMotorType(2);
                    }else if(motorType.getText().toString().equals("双弹簧")){
                        motorType.setText("窄履带");
                        item.setMotorType(3);
                    }else if(motorType.getText().toString().equals("窄履带")){
                        motorType.setText("宽履带");
                        item.setMotorType(4);
                    }else{
                        motorType.setText("单弹簧");
                        item.setMotorType(1);
                    }
                }
                break;
                case R.id.aisleState: {
                    if(aisleState.getText().toString().equals("正常")){
                        aisleState.setText("锁死");
                        item.setState(1);
                    }else if(aisleState.getText().toString().equals("锁死")){
                        aisleState.setText("故障");
                        item.setState(2);
                    }else{
                        aisleState.setText("正常");
                        item.setState(0);
                    }
                }
                break;
            }
        }
    }
}
