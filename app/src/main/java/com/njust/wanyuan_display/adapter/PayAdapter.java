package com.njust.wanyuan_display.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.activity.PayActivity;
import com.njust.wanyuan_display.parcelable.AisleInfoParcelable;

import java.text.NumberFormat;


public class PayAdapter extends RecyclerView.Adapter<PayAdapter.ViewHolder>{
    private PayActivity activity;
    private SparseArray<AisleInfoParcelable> dataList;
    private NumberFormat nf;
    private LayoutInflater mInflater;
    public PayAdapter(PayActivity activity, SparseArray<AisleInfoParcelable> dataList) {
        this.activity = activity;
        this.dataList = dataList;
        nf = NumberFormat.getCurrencyInstance();
        nf.setMaximumFractionDigits(2);
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_pay_detail,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AisleInfoParcelable item = dataList.valueAt(position);
        holder.bindData(item);
    }

    @Override
    public int getItemCount() {
        if(dataList==null) {
            return 0;
        }
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private AisleInfoParcelable item;
        private TextView goodsName,unitPrice,goodsQuantity,totalPrice;
        private ImageView img_goodsDetail = new ImageView(activity);



        public ViewHolder(View itemView) {
            super(itemView);
            img_goodsDetail = (ImageView) itemView.findViewById(R.id.img_goodsDetail);
            goodsName = (TextView) itemView.findViewById(R.id.goodsName);
            unitPrice = (TextView) itemView.findViewById(R.id.unitPrice);
            goodsQuantity = (TextView) itemView.findViewById(R.id.goodsQuantity);
            totalPrice = (TextView) itemView.findViewById(R.id.totalPrice);
        }



        public void bindData(AisleInfoParcelable item){
            this.item = item;
            Glide.with(activity.getApplicationContext()).load(item.getGoodsImgLocalUrl()).into(img_goodsDetail);
            goodsName.setText(item.getGoodName());
            unitPrice.setText(nf.format(item.getGoodPrice()));
            goodsQuantity.setText(String.valueOf(item.getCount()));
            totalPrice.setText(nf.format(item.getCount()*item.getGoodPrice()));
        }
    }
}
