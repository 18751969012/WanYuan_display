package com.njust.wanyuan_display.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.activity.ShoppingCartActivity;
import com.njust.wanyuan_display.greenDao.AisleInfo;

import java.util.ArrayList;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> {
    public int selectTypeId;
    public ShoppingCartActivity activity;
    public ArrayList<AisleInfo> dataList;

    public TypeAdapter(ShoppingCartActivity activity, ArrayList<AisleInfo> dataList) {
        this.activity = activity;
        this.dataList = dataList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AisleInfo item = dataList.get(position);

        holder.bindData(item,position);
    }

    @Override
    public int getItemCount() {
        if(dataList==null){
            return 0;
        }
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvCount,type;
        RelativeLayout up_type;
        private AisleInfo item;


        public ViewHolder(View itemView) {
            super(itemView);
            tvCount = itemView.findViewById(R.id.tvCount);
            type = itemView.findViewById(R.id.type);
            up_type = itemView.findViewById(R.id.up_type);
            itemView.setOnClickListener(this);
        }

        public void bindData(AisleInfo item,int position){
            this.item = item;
            String myText=item.getGoodTypeName().trim();
            if(myText.length() == 2){
                String type1 = myText.substring(0,1);
                String type2 = myText.substring(1,2);
                type.setText(type1+" "+type2);
            }else{
                type.setText(item.getGoodTypeName());
            }
            int count = activity.getSelectedGroupCountByTypeId(item.getGoodTypeID());
            tvCount.setText(String.valueOf(count));
            if(count<1){
                tvCount.setVisibility(View.GONE);
            }else{
                tvCount.setVisibility(View.VISIBLE);
            }
            if(item.getGoodTypeID()==selectTypeId){
                type.setBackgroundColor(Color.TRANSPARENT);
//                itemView.setBackground(activity.getResources().getDrawable(R.drawable.shop_type_select));
                switch (myText.length()){
                    case 1:
                        up_type.setBackground(activity.getResources().getDrawable(R.drawable.shop_type_select_underpainting_oneword));
                        break;
                    case 2:
                        up_type.setBackground(activity.getResources().getDrawable(R.drawable.shop_type_select_underpainting_twoword));
                        break;
                    case 3:
                        up_type.setBackground(activity.getResources().getDrawable(R.drawable.shop_type_select_underpainting_threeword));
                        break;
                    case 4:
                        up_type.setBackground(activity.getResources().getDrawable(R.drawable.shop_type_select_underpainting_fourword));
                        break;
                    case 5:
                        up_type.setBackground(activity.getResources().getDrawable(R.drawable.shop_type_select_underpainting_fiveword));
                        break;
                }
            }else{
                if(position == 0){
                    type.setBackground(activity.getResources().getDrawable(R.drawable.shop_type_underpainting_left));
                }else if(position == getItemCount()-1){
                    type.setBackground(activity.getResources().getDrawable(R.drawable.shop_type_underpainting_right));
                }else{
                    type.setBackground(activity.getResources().getDrawable(R.drawable.shop_type_underpainting));
                }
                up_type.setBackgroundColor(Color.TRANSPARENT);
            }

        }

        @Override
        public void onClick(View v) {
            activity.onTypeClicked(item.getGoodTypeID());
        }
    }
}
