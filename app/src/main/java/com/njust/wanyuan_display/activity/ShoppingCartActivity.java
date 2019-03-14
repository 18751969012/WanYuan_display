package com.njust.wanyuan_display.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.adapter.GoodsAdapter;
import com.njust.wanyuan_display.adapter.SelectAdapter;
import com.njust.wanyuan_display.adapter.TypeAdapter;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.greenDao.AisleInfo;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.parcelable.AisleInfoParcelable;
import com.njust.wanyuan_display.util.ToastManager;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class ShoppingCartActivity extends BaseDispatchTouchActivity implements View.OnClickListener{

    private ImageView imgCart;
    private ViewGroup anim_mask_layout;
    private RecyclerView rvType,rvSelected;
    private TextView tvCount,tvCost,tvSubmit;
    private BottomSheetLayout bottomSheetLayout;
    private View bottomSheet;
    private StickyGridHeadersGridView gridView;


    private ArrayList<AisleInfo> dataList,typeList;//商品信息列表、分类信息列表
    private ArrayList<AisleInfo> mList = new ArrayList<>();//用来处理分类名称占用格位导致跳转不正确的问题
    public SparseArray<AisleInfo> selectedList = new SparseArray<>();//选中的商品列表
    private SparseArray<AisleInfoParcelable> selectedListParelable = new SparseArray<>();//用来传递数据
    private SparseIntArray groupSelect = new SparseIntArray();//typeID和数量，为了更新本类商品选中的数量

    private GoodsAdapter myAdapter;
    private SelectAdapter selectAdapter;
    private TypeAdapter typeAdapter;

    private DecimalFormat df   =   new DecimalFormat("#####0.0");
    private Handler mHanlder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
//        nf = NumberFormat.getCurrencyInstance();
//        nf.setMaximumFractionDigits(2);
        mHanlder = new Handler(getMainLooper());
        if(new BindingManager().getShopGoodsShowType(getApplicationContext()).equals("saleByCommodity")){
            dataList = DBManager.getAisleInfoListSaleByCommodity(getApplicationContext());
        }else{
            dataList = DBManager.getAisleInfoListSaleByAisle(getApplicationContext());
        }

        if(dataList!=null && dataList.size()>0){
            typeList = new ArrayList<>();
            typeList.add(dataList.get(0));
            for(int i =1; i<dataList.size();i++){
                if(dataList.get(i).getGoodTypeID() != typeList.get(typeList.size()-1).getGoodTypeID()){
                    typeList.add(dataList.get(i));
                }
            }
            AisleInfo fillBlank11 = new AisleInfo(dataList.get(0).getGoodTypeID());
            AisleInfo fillBlank12 = new AisleInfo(dataList.get(0).getGoodTypeID());
            AisleInfo fillBlank13 = new AisleInfo(dataList.get(0).getGoodTypeID());
            AisleInfo fillBlank14 = new AisleInfo(dataList.get(0).getGoodTypeID());
            mList.add(fillBlank11);
            mList.add(fillBlank12);
            mList.add(fillBlank13);
            mList.add(fillBlank14);
            mList.add(dataList.get(0));
            for(int i=1; i < dataList.size(); i++){
                if(dataList.get(i-1).getGoodTypeID() != dataList.get(i).getGoodTypeID()){
                    if(mList.size()%4 == 0){
                        AisleInfo fillBlank15 = new AisleInfo(dataList.get(i).getGoodTypeID());
                        AisleInfo fillBlank16 = new AisleInfo(dataList.get(i).getGoodTypeID());
                        AisleInfo fillBlank17 = new AisleInfo(dataList.get(i).getGoodTypeID());
                        AisleInfo fillBlank18 = new AisleInfo(dataList.get(i).getGoodTypeID());
                        mList.add(fillBlank15);
                        mList.add(fillBlank16);
                        mList.add(fillBlank17);
                        mList.add(fillBlank18);
                        mList.add(dataList.get(i));
                    }else if(mList.size()%4 == 1){
                        AisleInfo fillBlank1 = new AisleInfo(dataList.get(i-1).getGoodTypeID());
                        AisleInfo fillBlank2 = new AisleInfo(dataList.get(i-1).getGoodTypeID());
                        AisleInfo fillBlank3 = new AisleInfo(dataList.get(i-1).getGoodTypeID());
                        mList.add(fillBlank1);
                        mList.add(fillBlank2);
                        mList.add(fillBlank3);
                        AisleInfo fillBlank19 = new AisleInfo(dataList.get(i).getGoodTypeID());
                        AisleInfo fillBlank20 = new AisleInfo(dataList.get(i).getGoodTypeID());
                        AisleInfo fillBlank21 = new AisleInfo(dataList.get(i).getGoodTypeID());
                        AisleInfo fillBlank22 = new AisleInfo(dataList.get(i).getGoodTypeID());
                        mList.add(fillBlank19);
                        mList.add(fillBlank20);
                        mList.add(fillBlank21);
                        mList.add(fillBlank22);
                        mList.add(dataList.get(i));
                    }else if(mList.size()%4 == 2){
                        AisleInfo fillBlank1 = new AisleInfo(dataList.get(i-1).getGoodTypeID());
                        AisleInfo fillBlank2 = new AisleInfo(dataList.get(i-1).getGoodTypeID());
                        mList.add(fillBlank1);
                        mList.add(fillBlank2);
                        AisleInfo fillBlank23 = new AisleInfo(dataList.get(i).getGoodTypeID());
                        AisleInfo fillBlank24 = new AisleInfo(dataList.get(i).getGoodTypeID());
                        AisleInfo fillBlank25 = new AisleInfo(dataList.get(i).getGoodTypeID());
                        AisleInfo fillBlank26 = new AisleInfo(dataList.get(i).getGoodTypeID());
                        mList.add(fillBlank23);
                        mList.add(fillBlank24);
                        mList.add(fillBlank25);
                        mList.add(fillBlank26);
                        mList.add(dataList.get(i));
                    }else if(mList.size()%4 == 3){
                        AisleInfo fillBlank1 = new AisleInfo(dataList.get(i-1).getGoodTypeID());
                        mList.add(fillBlank1);
                        AisleInfo fillBlank27 = new AisleInfo(dataList.get(i).getGoodTypeID());
                        AisleInfo fillBlank28 = new AisleInfo(dataList.get(i).getGoodTypeID());
                        AisleInfo fillBlank29 = new AisleInfo(dataList.get(i).getGoodTypeID());
                        AisleInfo fillBlank30 = new AisleInfo(dataList.get(i).getGoodTypeID());
                        mList.add(fillBlank27);
                        mList.add(fillBlank28);
                        mList.add(fillBlank29);
                        mList.add(fillBlank30);
                        mList.add(dataList.get(i));
                    }
                }else {
                    mList.add(dataList.get(i));
                }
            }
            int count = mList.size();
            if(count != 0){
                if(count%4 == 0){
                }else if(count%4 == 1){
                    AisleInfo fillBlank1 = new AisleInfo(mList.get(count-1).getGoodTypeID());
                    AisleInfo fillBlank2 = new AisleInfo(mList.get(count-1).getGoodTypeID());
                    AisleInfo fillBlank3 = new AisleInfo(mList.get(count-1).getGoodTypeID());
                    mList.add(fillBlank1);
                    mList.add(fillBlank2);
                    mList.add(fillBlank3);
                }else if(count%4 == 2){
                    AisleInfo fillBlank1 = new AisleInfo(mList.get(count-1).getGoodTypeID());
                    AisleInfo fillBlank2 = new AisleInfo(mList.get(count-1).getGoodTypeID());
                    mList.add(fillBlank1);
                    mList.add(fillBlank2);
                }else if(count%4 == 3){
                    AisleInfo fillBlank = new AisleInfo(mList.get(count-1).getGoodTypeID());
                    mList.add(fillBlank);
                }
            }
            //多添加一行无效数据，用来填充显示
            int count2 = dataList.size();
            if(count2 != 0){
                AisleInfo fillBlank = new AisleInfo(dataList.get(count2-1));
                dataList.add(fillBlank);
                dataList.add(fillBlank);
                dataList.add(fillBlank);
                dataList.add(fillBlank);
            }
        }
        initView();
    }

    private void initView(){
        TextView returnBack = (TextView) findViewById(R.id.returnBack);
        returnBack.setOnClickListener(this);

        tvCount = (TextView) findViewById(R.id.tvCount);
        tvCost = (TextView) findViewById(R.id.tvCost);
        tvSubmit  = (TextView) findViewById(R.id.tvSubmit);


        imgCart = (ImageView) findViewById(R.id.imgCart);
        anim_mask_layout = (RelativeLayout) findViewById(R.id.containerLayout);
        bottomSheetLayout = (BottomSheetLayout) findViewById(R.id.bottomSheetLayout);

        gridView = (StickyGridHeadersGridView) findViewById(R.id.itemGridView);


        rvType = (RecyclerView) findViewById(R.id.typeRecyclerView);
        rvType.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        typeAdapter = new TypeAdapter(this,typeList);
        //添加自定义分割线
        DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.shop_type_divider));
        rvType.addItemDecoration(divider);
        rvType.setAdapter(typeAdapter);


        myAdapter = new GoodsAdapter(dataList,this, new BindingManager().getShopGoodsShowType(getApplicationContext()).equals("saleByAisle"));
        gridView.setAreHeadersSticky(false);
        gridView.setAdapter(myAdapter);
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mList.size()>0){
                    AisleInfo item = mList.get(firstVisibleItem);
                    if(typeAdapter.selectTypeId != item.getGoodTypeID()) {
                        typeAdapter.selectTypeId = item.getGoodTypeID();
                        typeAdapter.notifyDataSetChanged();
                        rvType.smoothScrollToPosition(getSelectedGroupPosition(item.getGoodTypeID()));
                    }
                }
            }
        });

    }


    public void playAnimation(ImageView img, int[] start_location){
        addViewToAnimLayout(anim_mask_layout, img, start_location);
        startAnimation(img,start_location);
    }
    private void addViewToAnimLayout(final ViewGroup vg, final View view, int[] location) {
        int x = location[0];
        int y = location[1];
        int[] loc = new int[2];
        vg.getLocationInWindow(loc);
        view.setX(x);
        view.setY(y);
        vg.addView(view);
    }

    public void startAnimation(final View img, int[] start_location)
    {
        int[] des = new int[2];
        imgCart.getLocationInWindow(des);
        int[] end_location = new int[2];
        end_location[0] =des[0] - imgCart.getWidth()/2 + 30;
        end_location[1] =des[1] - imgCart.getHeight()/2;

        ObjectAnimator translateAnimationX = ObjectAnimator.ofFloat(img, "translationX", start_location[0], end_location[0]);
        translateAnimationX.setDuration(600);
        translateAnimationX.setInterpolator(new LinearInterpolator());

        ObjectAnimator translateAnimationY = ObjectAnimator.ofFloat(img, "translationY", start_location[1], end_location[1]);
        translateAnimationY.setDuration(600);
        translateAnimationY.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(img, "scaleX", 1, 0.06f);
        scaleX.setDuration(600);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(img, "scaleY", 1, 0.06f);
        scaleY.setDuration(600);

        ObjectAnimator alpha = ObjectAnimator.ofFloat(img, "alpha", 1, 0.5f);
        alpha.setDuration(600);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(translateAnimationX).with(translateAnimationY).with(scaleX).with(scaleY).with(alpha);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mHanlder.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        anim_mask_layout.removeView(img);
                    }
                },0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.returnBack:
                Intent intent2 = new Intent(ShoppingCartActivity.this, AdvertisingActivity.class);
                startActivity(intent2);
                this.finish();
                break;
            case R.id.bottom:
                showBottomSheet();
                break;
            case R.id.clear:
                clearCart();
                break;
            case R.id.tvSubmit:
                if(selectedList.size()>0){
                    int count =0;
                    double cost = 0;
                    for(int i=0;i<selectedList.size();i++){
                        AisleInfo item =  selectedList.valueAt(i);
                        count += item.getCount();
                        cost += item.getCount()*item.getGoodPrice();
                        AisleInfoParcelable aisleInfoParcelable = new AisleInfoParcelable(item.getPositionID(),item.getCounter(),item.getCapacity(),item.getGoodQuantity(),item.getGoodID(),item.getGoodName(),item.getGoodPrice(),item.getCount(),item.getGoodsImgLocalUrl());
                        selectedListParelable.append(selectedList.keyAt(i),aisleInfoParcelable);
                    }
                    Intent intent =new Intent(ShoppingCartActivity.this,PayActivity.class);
                    //用Bundle携带数据
                    Bundle bundle=new Bundle();
                    bundle.putSparseParcelableArray("selectedListParelable", selectedListParelable);
                    intent.putExtras(bundle);
                    intent.putExtra("totalPrices",String.valueOf(cost));
                    intent.putExtra("totalQuantity",String.valueOf(count));
                    startActivity(intent);
                    selectedListParelable.clear();
                }else{
                    ToastManager.getInstance().showToast(getApplicationContext(),"请选择商品", Toast.LENGTH_LONG);
                }
                break;
            default:
                break;
        }
    }
    //添加商品
    public void add(AisleInfo item, boolean refreshGoodList){
            AisleInfo temp = selectedList.get(item.getPositionID());
            if(temp==null){
                item.setCount(1);
                selectedList.append(item.getPositionID(),item);
            }else{
                temp.count++;
            }
            int groupCount = groupSelect.get(item.getGoodTypeID());
            if(groupCount==0){
                groupSelect.append(item.getGoodTypeID(),1);
            }else{
                groupSelect.append(item.getGoodTypeID(),++groupCount);
            }
            update(refreshGoodList);

    }
    //移除商品
    public void remove(AisleInfo item, boolean refreshGoodList){

        int groupCount = groupSelect.get(item.getGoodTypeID());
        if(groupCount==1){
            groupSelect.delete(item.getGoodTypeID());
        }else if(groupCount>1){
            groupSelect.append(item.getGoodTypeID(),--groupCount);
        }

        AisleInfo temp = selectedList.get(item.getPositionID());
        if(temp!=null){
            if(temp.getCount()<=1){
                selectedList.remove(item.getPositionID());
            }else{
                temp.count--;
            }
        }
        update(refreshGoodList);
    }
    //刷新布局 总价、购买数量等
    private void update(boolean refreshGoodList){
        int size = selectedList.size();
        int count =0;
        double cost = 0;
        for(int i=0;i<size;i++){
            AisleInfo item = selectedList.valueAt(i);
            count += item.getCount();
            cost += item.getCount()*item.getGoodPrice();
        }

        if(count<1){
            tvCount.setVisibility(View.GONE);
        }else{
            tvCount.setVisibility(View.VISIBLE);
        }

        tvCount.setText(String.valueOf(count));

//        tvCost.setText("合计 "+nf.format(cost));
        tvCost.setText(df.format(cost));

        if(myAdapter!=null && refreshGoodList){
            myAdapter.notifyDataSetChanged();
        }
        if(selectAdapter!=null){
            selectAdapter.notifyDataSetChanged();
        }
        if(typeAdapter!=null){
            typeAdapter.notifyDataSetChanged();
        }
        if(bottomSheetLayout.isSheetShowing() && selectedList.size()<1){
            bottomSheetLayout.dismissSheet();
        }
    }
    //清空购物车
    public void clearCart(){
        selectedList.clear();
        groupSelect.clear();
        update(true);

    }
    //根据货道id获取当前商品的采购数量
    public int getSelectedItemCountById(int id){
        AisleInfo temp = selectedList.get(id);
        if(temp==null){
            return 0;
        }
        return temp.getCount();
    }
    //根据类别Id获取属于当前类别的数量
    public int getSelectedGroupCountByTypeId(int typeId){
        return groupSelect.get(typeId);
    }
    //根据类别id获取分类的Position 用于滚动左侧的类别列表
    public int getSelectedGroupPosition(int typeId){
        for(int i=0;i<typeList.size();i++){
            if(typeId==typeList.get(i).getGoodTypeID()){
                return i;
            }
        }
        return 0;
    }

    public void onTypeClicked(int typeId){
        gridView.setSelection(getSelectedPosition(typeId));
    }

    private int getSelectedPosition(int typeId){
        int position = 0;
        for(int i=0;i<mList.size();i++){
            if(mList.get(i).getGoodTypeID() == typeId){
                position =i;
                break;
            }
        }
        return position;
    }

    private View createBottomSheetView(){
        View view = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet,(ViewGroup) getWindow().getDecorView(),false);
        rvSelected = (RecyclerView) view.findViewById(R.id.selectRecyclerView);
        rvSelected.setLayoutManager(new LinearLayoutManager(this));
        rvSelected.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL)); //添加Android自带的分割线
        TextView clear = (TextView) view.findViewById(R.id.clear);
        clear.setOnClickListener(this);
        selectAdapter = new SelectAdapter(this,selectedList);
        rvSelected.setAdapter(selectAdapter);
        return view;
    }

    private void showBottomSheet(){
        if(bottomSheet==null){
            bottomSheet = createBottomSheetView();
        }
        if(bottomSheetLayout.isSheetShowing()){
            bottomSheetLayout.dismissSheet();
        }else {
            if(selectedList.size()!=0){
                bottomSheetLayout.showWithSheetView(bottomSheet);
            }
        }
    }
}
