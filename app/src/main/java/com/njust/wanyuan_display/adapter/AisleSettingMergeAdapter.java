package com.njust.wanyuan_display.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.fragment.AisleMergeFragment;
import com.njust.wanyuan_display.greenDao.AisleMerge;

import java.util.ArrayList;


public class AisleSettingMergeAdapter extends RecyclerView.Adapter<AisleSettingMergeAdapter.ViewHolder>{
    private AisleMergeFragment mAisleMergeFragment;
    private ArrayList<AisleMerge> dataList;
    private LayoutInflater mInflater;
    public AisleSettingMergeAdapter(AisleMergeFragment mAisleMergeFragment, ArrayList<AisleMerge> dataList) {
        this.mAisleMergeFragment = mAisleMergeFragment;
        this.dataList = dataList;
        mInflater = LayoutInflater.from(mAisleMergeFragment.getContext());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_manager_aisle_merge,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AisleMerge aisleMerge = dataList.get(position);
        holder.bindData(aisleMerge);
    }

    @Override
    public int getItemCount() {
        if(dataList==null) {
            return 0;
        }
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AisleMerge item;
        private TextView oddAisleID,evenAisleID;
        private Button relieve_merge;

        public ViewHolder(View itemView) {
            super(itemView);
            oddAisleID = (TextView) itemView.findViewById(R.id.oddAisleID);
            evenAisleID = (TextView) itemView.findViewById(R.id.evenAisleID);
            relieve_merge = (Button) itemView.findViewById(R.id.relieve_merge);
            relieve_merge.setOnClickListener(this);
        }

        public void bindData(AisleMerge item) {
            this.item = item;
            oddAisleID.setText("货道"+item.getPositionID1());
            evenAisleID.setText("货道"+item.getPositionID2());
        }

        @Override
        public void onClick(View v) {
            AisleMergeFragment Fragment = mAisleMergeFragment;
            switch (v.getId()) {
                case R.id.relieve_merge: {
                    Fragment.relieveMerge(item);
                }
                break;
            }
        }
    }
}
