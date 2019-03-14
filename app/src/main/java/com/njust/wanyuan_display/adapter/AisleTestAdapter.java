package com.njust.wanyuan_display.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.activity.ManagerAisleTestActivity;
import com.njust.wanyuan_display.greenDao.AisleInfo;

import java.text.NumberFormat;


public class AisleTestAdapter extends RecyclerView.Adapter<AisleTestAdapter.ViewHolder>{
    private ManagerAisleTestActivity mManagerAisleTestActivity;
    private SparseArray<AisleInfo> dataList;
    private NumberFormat nf;
    private LayoutInflater mInflater;
    public AisleTestAdapter(ManagerAisleTestActivity mManagerAisleTestActivity, SparseArray<AisleInfo> dataList) {
        this.mManagerAisleTestActivity = mManagerAisleTestActivity;
        this.dataList = dataList;
        mInflater = LayoutInflater.from(mManagerAisleTestActivity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_manager_aisle_test,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AisleInfo item = dataList.valueAt(position);
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
        private TextView aisleID,aisle_status;
        private Button aisle_test;
        private String state;

        public ViewHolder(View itemView) {
            super(itemView);
            aisleID = (TextView) itemView.findViewById(R.id.aisleID);
            aisle_status = (TextView) itemView.findViewById(R.id.aisle_status);
            aisle_test = (Button) itemView.findViewById(R.id.aisle_test);
            aisle_test.setOnClickListener(this);
        }

        public void bindData(AisleInfo item) {
            this.item = item;
            switch (item.getState()) {
                case 0:
                    state = "正常";
                    aisle_status.setTextColor(mManagerAisleTestActivity.getResources().getColor(R.color.holo_orange_dark));
                    break;
                case 1:
                    state = "锁死";
                    break;
                case 2:
                    state = "故障";
                    aisle_status.setTextColor(mManagerAisleTestActivity.getResources().getColor(R.color.red_light));
                    break;
                default:
                    break;
            }
            aisleID.setText("货道" + item.getPositionID());
            aisle_status.setText(state);
        }

        @Override
        public void onClick(View v) {
            ManagerAisleTestActivity activity = mManagerAisleTestActivity;
            switch (v.getId()) {
                case R.id.aisle_test: {
                    activity.aisleTest(String.valueOf(item.getPositionID()));
                }
                break;
                default:
                    break;
            }
        }
    }
}
