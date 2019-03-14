package com.njust.wanyuan_display.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.activity.ManagerReplenishActivity;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.greenDao.AisleInfo;
import com.njust.wanyuan_display.greenDao.GoodsInfo;
import com.njust.wanyuan_display.greenDao.MachineState;
import com.njust.wanyuan_display.manager.DownloadImageManager;
import com.njust.wanyuan_display.util.StringUtil;
import com.njust.wanyuan_display.util.ToastManager;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ReplenishAdapter extends BaseAdapter {
    private ArrayList<AisleInfo> dataList;
    private ArrayList<GoodsInfo> goodslist;
    private ManagerReplenishActivity context;
    private DecimalFormat df   =   new DecimalFormat("#####0.00");

    private String leftTemp="",rightTemp="";

    public ReplenishAdapter(ArrayList<AisleInfo> dataList, ManagerReplenishActivity context) {
        this.dataList = dataList;
        this.context = context;
        MachineState machineState = DBManager.getMachineState(context);
        if(machineState.getLeftCabinetTemp() != 22222){//非故障
            if(machineState.getLeftCabinetTemp() < 50){
                leftTemp = "冷";
            }else if(machineState.getLeftCabinetTemp() > 200){
                leftTemp = "热";
            }else{
                leftTemp = "常温";
            }
        }
        if(machineState.getRightCabinetTemp() != 22222){//非故障
            if(machineState.getRightCabinetTemp() < 50){
                rightTemp = "冷";
            }else if(machineState.getRightCabinetTemp() > 200){
                rightTemp = "热";
            }else{
                rightTemp = "常温";
            }
        }
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder holder = null;
        if(convertView==null){
            convertView = View.inflate(context, R.layout.item_manager_replenishment_goods,null);
            holder = new ItemViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ItemViewHolder) convertView.getTag();
        }
        AisleInfo item = dataList.get(position);
        holder.bindData(item);
        return convertView;
    }

    class ItemViewHolder implements View.OnClickListener {
        private AisleInfo item;
        private RelativeLayout noGoods;
        private LinearLayout hasGoods;
        private TextView aisleID,tvName;
        private ImageView img;
        private TextView tvPriceMinus,tvPriceAdd,tvQuantityMinus,tvQuantityAdd,tvCapacityMinus,tvCapacityAdd;
        private EditText price,quantity,capacity;
        private Button fillGoods,backGoods;
        private Button please_select_good;
        private Button coldHot_BT;
        private TextView coldHot_TV;


        ItemViewHolder(View itemView) {
            aisleID = (TextView) itemView.findViewById(R.id.aisleID);
            noGoods = (RelativeLayout) itemView.findViewById(R.id.noGoods);
            hasGoods = (LinearLayout) itemView.findViewById(R.id.hasGoods);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            img = (ImageView) itemView.findViewById(R.id.img);
            tvPriceMinus = (TextView) itemView.findViewById(R.id.tvPriceMinus);
            tvPriceMinus.setOnClickListener(this);
            tvPriceAdd = (TextView) itemView.findViewById(R.id.tvPriceAdd);
            tvPriceAdd.setOnClickListener(this);
            tvQuantityMinus = (TextView) itemView.findViewById(R.id.tvQuantityMinus);
            tvQuantityMinus.setOnClickListener(this);
            tvQuantityAdd = (TextView) itemView.findViewById(R.id.tvQuantityAdd);
            tvQuantityAdd.setOnClickListener(this);
            tvCapacityMinus = (TextView) itemView.findViewById(R.id.tvCapacityMinus);
            tvCapacityMinus.setOnClickListener(this);
            tvCapacityAdd = (TextView) itemView.findViewById(R.id.tvCapacityAdd);
            tvCapacityAdd.setOnClickListener(this);
            price = (EditText) itemView.findViewById(R.id.price);
            price.setFilters(new InputFilter[]{new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    if(source.equals(".") && dest.toString().length() == 0){
                        return "0.";
                    }
                    if(dest.toString().contains(".")){
                        int index = dest.toString().indexOf(".");
                        int length = dest.toString().substring(index).length();
                        if(length == 3){
                            return "";
                        }
                    }
                    return null;
                }
            }});
            quantity = (EditText) itemView.findViewById(R.id.quantity);
            capacity = (EditText) itemView.findViewById(R.id.capacity);
            fillGoods = (Button) itemView.findViewById(R.id.fillGoods);
            fillGoods.setOnClickListener(this);
            backGoods = (Button) itemView.findViewById(R.id.backGoods);
            backGoods.setOnClickListener(this);
            please_select_good = (Button) itemView.findViewById(R.id.please_select_good);
            please_select_good.setOnClickListener(this);
            coldHot_BT = (Button) itemView.findViewById(R.id.coldHot_BT);
            coldHot_BT.setOnClickListener(this);
            coldHot_TV = (TextView) itemView.findViewById(R.id.coldHot_TV);
        }

        void bindData(AisleInfo item) {
            this.item = item;
            if (item.getPositionID() >= 1 && item.getPositionID() <= 100) {
                aisleID.setText("左柜—" + item.getPositionID() + "货道");
            } else {
                aisleID.setText("右柜—" + item.getPositionID() + "货道");
            }
            if(item.getGoodID()<0){
                hasGoods.setVisibility(View.GONE);
                noGoods.setVisibility(View.VISIBLE);
            }else{
                noGoods.setVisibility(View.GONE);
                hasGoods.setVisibility(View.VISIBLE);
                tvName.setText(item.getGoodName());
                if(item.getGoodsImgLocalUrl()==null || item.getGoodsImgLocalUrl().equals("")){
                    File image = new File(Environment.getExternalStorageDirectory() ,Resources.goods_filepath);
                    if (!image.exists()) {
                        image.mkdirs();
                    }
                    String imageName = StringUtil.getGoodImageName(item.getGoodID(),item.getGoodsImgUrl()) + ".png";
                    if(new File(image.getAbsolutePath(), imageName).exists()){
                        item.setGoodsImgLocalUrl(Resources.goodsURL()+imageName);
                        DBManager.updateGoodImageLocalUrl(context,item.getGoodID(),Resources.goodsURL()+imageName);
                        Glide.with(context.getApplicationContext()).load(item.getGoodsImgLocalUrl()).into(img);
                    }else{
                        onDownLoad(imageName,item.getGoodsImgUrl(), Resources.goods_filepath);
                        Glide.with(context.getApplicationContext()).load(item.getGoodsImgLocalUrl()).into(img);
                    }
                }else{
                    Glide.with(context.getApplicationContext()).load(item.getGoodsImgLocalUrl()).into(img);
                }
                price.setText(String.valueOf(item.getGoodPrice()));
                quantity.setText(item.getGoodQuantity());
                capacity.setText(item.getCapacity());
                if(item.getGoodColdHot()){
                    if (item.getPositionID() >= 1 && item.getPositionID() <= 100) {
                        if(!leftTemp.equals("")){
                            switch (leftTemp) {
                                case "冷":
                                    coldHot_TV.setText("冷");
                                    coldHot_TV.setTextColor(context.getResources().getColor(R.color.blue_45a7ff));
                                    item.setTemp("1");
                                    break;
                                case "热":
                                    coldHot_TV.setText("热");
                                    coldHot_TV.setTextColor(context.getResources().getColor(R.color.font_red));
                                    item.setTemp("2");
                                    break;
                                default:
                                    coldHot_TV.setText("常温");
                                    coldHot_TV.setTextColor(context.getResources().getColor(R.color.black));
                                    item.setTemp("0");
                                    break;
                            }
                        }else{
                            coldHot_TV.setText("常温");
                            coldHot_TV.setTextColor(context.getResources().getColor(R.color.black));
                            item.setTemp("0");
                        }
                    } else {
                        if(!rightTemp.equals("")){
                            switch (rightTemp) {
                                case "冷":
                                    coldHot_TV.setText("冷");
                                    coldHot_TV.setTextColor(context.getResources().getColor(R.color.blue_45a7ff));
                                    item.setTemp("1");
                                    break;
                                case "热":
                                    coldHot_TV.setText("热");
                                    coldHot_TV.setTextColor(context.getResources().getColor(R.color.font_red));
                                    item.setTemp("2");
                                    break;
                                default:
                                    coldHot_TV.setText("常温");
                                    coldHot_TV.setTextColor(context.getResources().getColor(R.color.black));
                                    item.setTemp("0");
                                    break;
                            }
                        }else{
                            coldHot_TV.setText("常温");
                            coldHot_TV.setTextColor(context.getResources().getColor(R.color.black));
                            item.setTemp("0");
                        }
                    }
                }else{
                    coldHot_TV.setText("");
                }
            }
        }

        @Override
        public void onClick(View v) {
            final ManagerReplenishActivity activity = context;
            switch (v.getId()) {
                case R.id.please_select_good: {
                    goodslist = DBManager.getGoodsInfo(context);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("请选择商品");
                    final String titles[] = new String[goodslist.size()];
                    for (int i=0;i<goodslist.size();i++){
                        titles[i] = goodslist.get(i).getGoodName();
                    }
                    builder.setSingleChoiceItems(titles, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            String title = titles[arg1];
                            item.setCapacity("10");
                            item.setGoodQuantity("1");
                            item.setGoodID(goodslist.get(arg1).getGoodID());
                            item.setGoodName(goodslist.get(arg1).getGoodName());
                            item.setGoodTypeID(goodslist.get(arg1).getGoodTypeID());
                            item.setGoodTypeName(goodslist.get(arg1).getGoodTypeName());
                            item.setGoodDescription(goodslist.get(arg1).getGoodDescription());
                            item.setGoodPrice(goodslist.get(arg1).getGoodPrice());
                            item.setPromotion(goodslist.get(arg1).getPromotion());
                            item.setPromotionPrice(goodslist.get(arg1).getPromotionPrice());
                            item.setGoodsImgUrl(goodslist.get(arg1).getGoodsImgUrl());
                            item.setGoodsImgLocalUrl(goodslist.get(arg1).getGoodsImgLocalUrl());
                            item.setGoodColdHot(goodslist.get(arg1).getGoodColdHot());
                            arg0.dismiss();
                            activity.mReplenishAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.show();
                    break;
                }
                case R.id.tvPriceMinus:
                    item.setGoodPrice(Double.parseDouble(price.getText().toString()));
                    double pMinus = item.getGoodPrice();
                    String a = df.format(pMinus - 0.01);
                    item.setGoodPrice(Double.parseDouble(a));
                    price.setText(a);
                    break;
                case R.id.tvPriceAdd:
                    item.setGoodPrice(Double.parseDouble(price.getText().toString()));
                    double pAdd = item.getGoodPrice();
                    String b = df.format(pAdd + 0.01);
                    item.setGoodPrice(Double.parseDouble(b));
                    price.setText(b);
                    break;
                case R.id.tvQuantityMinus:
                    item.setGoodQuantity(quantity.getText().toString());
                    int qMinus = Integer.parseInt( item.getGoodQuantity());
                    qMinus--;
                    item.setGoodQuantity(String.valueOf(qMinus));
                    quantity.setText(String.valueOf(qMinus));
                    break;
                case R.id.tvQuantityAdd:
                    item.setGoodQuantity(quantity.getText().toString());
                    int qAdd = Integer.parseInt(item.getGoodQuantity());
                    qAdd++;
                    item.setGoodQuantity(String.valueOf(qAdd));
                    quantity.setText(String.valueOf(qAdd));
                    break;
                case R.id.tvCapacityMinus:
                    item.setCapacity(capacity.getText().toString());
                    int cMinus = Integer.parseInt(item.getCapacity());
                    cMinus--;
                    item.setCapacity(String.valueOf(cMinus));
                    capacity.setText(String.valueOf(cMinus));
                    break;
                case R.id.tvCapacityAdd:
                    item.setCapacity(capacity.getText().toString());
                    int cAdd = Integer.parseInt(item.getCapacity());
                    cAdd++;
                    item.setCapacity(String.valueOf(cAdd));
                    capacity.setText(String.valueOf(cAdd));
                    break;
                case R.id.fillGoods:
                    int capacity = Integer.parseInt(item.getCapacity());
                    item.setGoodQuantity(String.valueOf(capacity));
                    quantity.setText(String.valueOf(capacity));
                    break;
                case R.id.backGoods:
                    item.setGoodID(-1);
                    item.setGoodName("");
                    item.setGoodTypeID(-1);
                    item.setGoodTypeName("");
                    item.setGoodDescription("");
                    item.setGoodPrice((double)0.01);
                    item.setPromotion("");
                    item.setPromotionPrice((double)0.01);
                    item.setGoodsImgUrl("");
                    item.setGoodsImgLocalUrl("");
                    item.setGoodColdHot(false);
                    item.setTemp("0");
                    activity.mReplenishAdapter.notifyDataSetChanged();
                    break;
                case R.id.coldHot_BT:
                    if(item.getTemp().equals("1")){
                        item.setTemp("2");
                        item.setGoodColdHot(true);
                        coldHot_TV.setText("热");
                        coldHot_TV.setTextColor(context.getResources().getColor(R.color.font_red));
                    }else if(item.getTemp().equals("2")){
                        item.setTemp("0");
                        item.setGoodColdHot(false);
                        coldHot_TV.setText("");
                    }else{
                        item.setTemp("1");
                        item.setGoodColdHot(true);
                        coldHot_TV.setText("冷");
                        coldHot_TV.setTextColor(context.getResources().getColor(R.color.blue_45a7ff));
                    }
                    break;
                default:
                    break;
            }
        }
        /**
         * 启动图片下载线程
         */
        private void onDownLoad(final String imageName, String url, String saveDir) {
            DownloadImageManager downloadImageManager = new DownloadImageManager(context,
                    imageName,url,saveDir,
                    new DownloadImageManager.ImageDownLoadCallBack() {
                        @Override
                        public void onDownLoadSuccess(File file) {
                        }
                        @Override
                        public void onDownLoadSuccess(Bitmap bitmap) {
                            // 在这里执行图片保存方法
                            item.setGoodsImgLocalUrl(Resources.goodsURL()+imageName);
                            DBManager.updateGoodImageLocalUrl(context,item.getGoodID(),Resources.goodsURL()+imageName);
                        }
                        @Override
                        public void onDownLoadFailed() {
                            // 图片保存失败
//                            ToastManager.getInstance().showToast(context,"图片下载失败", Toast.LENGTH_LONG);
                        }
                    });
            //启动图片下载线程
            new Thread(downloadImageManager).start();
        }
    }



}
