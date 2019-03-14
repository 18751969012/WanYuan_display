package com.njust.wanyuan_display.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.activity.ShoppingCartActivity;
import com.njust.wanyuan_display.greenDao.AisleInfo;
import com.njust.wanyuan_display.util.ToastManager;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;


public class GoodsAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter {

    private ArrayList<AisleInfo> dataList;
    private ShoppingCartActivity mContext;
    private NumberFormat nf;
    private LayoutInflater mInflater;
    private boolean saleByAisle;


    public GoodsAdapter(ArrayList<AisleInfo> dataList, ShoppingCartActivity mContext,boolean saleByAisle) {
        this.mContext = mContext;
        this.dataList = dataList;
        this.saleByAisle = saleByAisle;
        nf = NumberFormat.getCurrencyInstance();
        nf.setMaximumFractionDigits(2);
        mInflater = LayoutInflater.from(mContext);

    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView = mInflater.inflate(R.layout.item_header_view, parent, false);
        }
        TextView headerName = convertView.findViewById(R.id.headerName);
        String myText=dataList.get(position).getGoodTypeName();
        if(myText.length() == 2){
            String type1 = myText.substring(0,1);
            String type2 = myText.substring(1,2);
            headerName.setText(type1+" "+type2);
        }else{
            headerName.setText(myText);
        }
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return dataList.get(position).getGoodTypeID();
    }

    @Override
    public int getCount() {
        if(dataList==null){
            return 0;
        }
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder holder = null;
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.item_goods,parent,false);

            holder = new ItemViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ItemViewHolder) convertView.getTag();
        }
        AisleInfo item = dataList.get(position);
        holder.bindData(item);
        return convertView;
    }

    class ItemViewHolder implements View.OnClickListener{
        ShoppingCartActivity activity = mContext;
        private TextView name1,price1,tvAdd1,tvMinus1,tvCount1,tvAisle;
        private AisleInfo item1;
        private ImageView mImageView,imgAnimation;
        private TextView coldHot;
        private LinearLayout mLinearLayout;

        public ItemViewHolder(View itemView) {
            mImageView = (ImageView) itemView.findViewById(R.id.img1);
            mImageView.setOnClickListener(this);
            name1 = (TextView) itemView.findViewById(R.id.tvName1);
            tvAisle = (TextView) itemView.findViewById(R.id.tvAisle);
            price1 = (TextView) itemView.findViewById(R.id.tvPrice1);
            tvCount1 = (TextView) itemView.findViewById(R.id.count1);
            tvMinus1 = (TextView) itemView.findViewById(R.id.tvMinus1);
            tvAdd1 = (TextView) itemView.findViewById(R.id.tvAdd1);
            tvMinus1.setOnClickListener(this);
            tvAdd1.setOnClickListener(this);
            coldHot = (TextView) itemView.findViewById(R.id.coldHot);
            mLinearLayout = itemView.findViewById(R.id.mLinearLayout);
        }

        public void bindData(AisleInfo item1){
            this.item1 = item1;
            if(item1.getGoodID() == -1){
                mLinearLayout.setVisibility(View.GONE);
            }else{
                mLinearLayout.setVisibility(View.VISIBLE);
                name1.setText(item1.getGoodName());
                item1.setCount(activity.getSelectedItemCountById(item1.getPositionID()));
                price1.setText(nf.format(item1.getGoodPrice()));
                Glide.with(activity.getApplicationContext()).load(item1.getGoodsImgLocalUrl()).into(mImageView);
                if(item1.getGoodColdHot()){
                    if(item1.getTemp().equals("1")){
                        coldHot.setText("冷");
                        coldHot.setTextColor(mContext.getResources().getColor(R.color.blue_45a7ff));
                    }else if(item1.getTemp().equals("2")){
                        coldHot.setText("热");
                        coldHot.setTextColor(mContext.getResources().getColor(R.color.font_red));
                    }
                }
                if(saleByAisle){
                    tvAisle.setText("货道"+item1.getPositionID());
                }else{
                    tvAisle.setText("");
                }
                if(item1.count<1){
                    tvCount1.setVisibility(View.GONE);
                    tvMinus1.setVisibility(View.GONE);
                }else{
                    tvCount1.setText(String.valueOf(item1.count));
                    tvCount1.setVisibility(View.VISIBLE);
                    tvMinus1.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onClick(View v) {
            ShoppingCartActivity activity = mContext;
            switch (v.getId()){
                case R.id.tvAdd1: {
                    int count1 = 0;
                    if(activity.selectedList.size()>0) {
                        for (int i = 0; i < activity.selectedList.size(); i++) {
                            AisleInfo item = activity.selectedList.valueAt(i);
                            count1 += item.getCount();
                        }
                    }
                    if (count1 >= 6) {
                        ToastManager.getInstance().showToast(activity, "最多同时购买6件商品", Toast.LENGTH_LONG);
                    } else {
                        int count = activity.getSelectedItemCountById(item1.getPositionID());
                        if(count >= Integer.parseInt(item1.getGoodQuantity())){
                            ToastManager.getInstance().showToast(activity, "库存不足", Toast.LENGTH_LONG);
                        }else{
                            if (count < 1) {
                                tvMinus1.setAnimation(getShowAnimation());
                                tvMinus1.setVisibility(View.VISIBLE);
                                tvCount1.setVisibility(View.VISIBLE);
                            }
                            activity.add(item1, false);
                            count++;
                            tvCount1.setText(String.valueOf(count));
                            int[] loc = new int[2];
//                          v.getLocationInWindow(loc);
                            mImageView.getLocationInWindow(loc);
                            imgAnimation = new ImageView(activity);
                            imgAnimation.setImageDrawable(mImageView.getDrawable());
                            activity.playAnimation(imgAnimation, loc);
                        }
                    }
                }
                break;
                case R.id.tvMinus1: {
                    int count = activity.getSelectedItemCountById(item1.getPositionID());
                    if (count < 2) {
                        tvMinus1.setAnimation(getHiddenAnimation());
                        tvMinus1.setVisibility(View.GONE);
                        tvCount1.setVisibility(View.GONE);
                    }
                    count--;
                    activity.remove(item1, false);//activity.getSelectedItemCountById(item.id)
                    tvCount1.setText(String.valueOf(count));
                }
                break;
                case R.id.img1: {
                    int count1 = 0;
                    if(activity.selectedList.size()>0) {
                        for (int i = 0; i < activity.selectedList.size(); i++) {
                            AisleInfo item = activity.selectedList.valueAt(i);
                            count1 += item.getCount();
                        }
                    }
                    if (count1 >= 6) {
                        ToastManager.getInstance().showToast(activity, "最多同时购买6件商品", Toast.LENGTH_LONG);
                    } else {
                        int count = activity.getSelectedItemCountById(item1.getPositionID());
                        if(count >= Integer.parseInt(item1.getGoodQuantity())){
                            ToastManager.getInstance().showToast(activity, "库存不足", Toast.LENGTH_LONG);
                        }else {
                            if (count < 1) {
                                tvMinus1.setAnimation(getShowAnimation());
                                tvMinus1.setVisibility(View.VISIBLE);
                                tvCount1.setVisibility(View.VISIBLE);
                            }
                            activity.add(item1, false);
                            count++;
                            tvCount1.setText(String.valueOf(count));
                            int[] loc = new int[2];
//                          v.getLocationInWindow(loc);
                            mImageView.getLocationInWindow(loc);
                            imgAnimation = new ImageView(activity);
                            imgAnimation.setImageDrawable(mImageView.getDrawable());
                            activity.playAnimation(imgAnimation, loc);
                        }
                    }
                }
                break;
                default:
                    break;
            }
        }
    }

    private Animation getShowAnimation(){
        AnimationSet set = new AnimationSet(true);
        RotateAnimation rotate = new RotateAnimation(0,720, RotateAnimation.RELATIVE_TO_SELF,0.5f, RotateAnimation.RELATIVE_TO_SELF,0.5f);
        set.addAnimation(rotate);
        TranslateAnimation translate = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF,2f
                , TranslateAnimation.RELATIVE_TO_SELF,0
                , TranslateAnimation.RELATIVE_TO_SELF,0
                , TranslateAnimation.RELATIVE_TO_SELF,0);
        set.addAnimation(translate);
        AlphaAnimation alpha = new AlphaAnimation(0,1);
        set.addAnimation(alpha);
        set.setDuration(500);
        return set;
    }

    private Animation getHiddenAnimation(){
        AnimationSet set = new AnimationSet(true);
        RotateAnimation rotate = new RotateAnimation(0,720, RotateAnimation.RELATIVE_TO_SELF,0.5f, RotateAnimation.RELATIVE_TO_SELF,0.5f);
        set.addAnimation(rotate);
        TranslateAnimation translate = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF,0
                , TranslateAnimation.RELATIVE_TO_SELF,2f
                , TranslateAnimation.RELATIVE_TO_SELF,0
                , TranslateAnimation.RELATIVE_TO_SELF,0);
        set.addAnimation(translate);
        AlphaAnimation alpha = new AlphaAnimation(1,0);
        set.addAnimation(alpha);
        set.setDuration(500);
        return set;
    }

}
