package com.njust.wanyuan_display.adapter;

import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.activity.ShoppingCartActivity;
import com.njust.wanyuan_display.greenDao.AisleInfo;
import com.njust.wanyuan_display.util.ToastManager;

import java.text.NumberFormat;


public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ViewHolder>{
    private ShoppingCartActivity activity;
    private SparseArray<AisleInfo> dataList;
    private NumberFormat nf;
    private LayoutInflater mInflater;
    public SelectAdapter(ShoppingCartActivity activity, SparseArray<AisleInfo> dataList) {
        this.activity = activity;
        this.dataList = dataList;
        nf = NumberFormat.getCurrencyInstance();
        nf.setMaximumFractionDigits(2);
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_selected_goods,parent,false);
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

     class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private AisleInfo item;
        private TextView tvCost,tvCount,tvAdd,tvMinus,tvName;
        private ImageView IVPicture;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvCost = itemView.findViewById(R.id.tvCost);
            tvCount = itemView.findViewById(R.id.count);
            tvMinus = itemView.findViewById(R.id.tvMinus);
            tvAdd = itemView.findViewById(R.id.tvAdd);
            IVPicture = itemView.findViewById(R.id.IVPicture);
            tvMinus.setOnClickListener(this);
            tvAdd.setOnClickListener(this);
        }
        public void bindData(AisleInfo item){
            this.item = item;
            tvName.setText(item.getGoodName());
            tvCost.setText(nf.format(item.getCount()*item.getGoodPrice())+"元");
            tvCount.setText(String.valueOf(item.getCount()));
            Glide.with(activity.getApplicationContext()).load(item.getGoodsImgLocalUrl()).into(IVPicture);
        }
        @Override
        public void onClick(View v) {
            ShoppingCartActivity shoppingCartActivity = activity;
            switch (v.getId()){
                case R.id.tvAdd:
                    int count1 = 0;
                    if(shoppingCartActivity.selectedList.size()>0) {
                        for (int i = 0; i < shoppingCartActivity.selectedList.size(); i++) {
                            AisleInfo item = shoppingCartActivity.selectedList.valueAt(i);
                            count1 += item.getCount();
                        }
                    }
                    if (count1 >= 6) {
                        ToastManager.getInstance().showToast(shoppingCartActivity, "最多同时购买6件商品", Toast.LENGTH_LONG);
                    } else {
                        int count = activity.getSelectedItemCountById(item.getPositionID());
                        if(count >= Integer.parseInt(item.getGoodQuantity())){
                            ToastManager.getInstance().showToast(activity, "库存不足", Toast.LENGTH_LONG);
                        }else {
                            shoppingCartActivity.add(item, true);
                        }
                    }
                    break;
                case R.id.tvMinus:
                    shoppingCartActivity.remove(item, true);
                    break;
                default:
                    break;
            }
        }


    }
}
