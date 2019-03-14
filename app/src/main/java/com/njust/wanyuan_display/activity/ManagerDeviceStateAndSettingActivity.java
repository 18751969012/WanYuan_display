package com.njust.wanyuan_display.activity;


import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.njust.wanyuan_display.R;
import com.njust.wanyuan_display.application.VMApplication;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.greenDao.SettingInfo;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.util.SerialPortUtils;
import com.njust.wanyuan_display.util.ToastManager;

import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.njust.wanyuan_display.application.VMApplication.VMMainThreadRunning;
import static com.njust.wanyuan_display.application.VMApplication.VMMainThreadFlag;
import static com.njust.wanyuan_display.application.VMApplication.mQuery0Flag;
import static com.njust.wanyuan_display.application.VMApplication.mQuery1Flag;
import static com.njust.wanyuan_display.application.VMApplication.mQuery2Flag;
import static com.njust.wanyuan_display.application.VMApplication.mUpdataDatabaseFlag;
import static com.njust.wanyuan_display.application.VMApplication.OutGoodsThreadFlag;

public class ManagerDeviceStateAndSettingActivity extends BaseExitKeyboardActivity implements View.OnClickListener {
    private static final Logger log = Logger.getLogger(ManagerDeviceStateAndSettingActivity.class);
    private TextView returnBack;
    private TextView left_cabinet_temp,left_cabinet_top_temp,/*left_compressor_dcfan_state,left_cabinet_dcfan_state,*/
            left_doorheat,left_humidity,left_light,left_push_goods_raster,left_out_goods_raster,left_out_goods_door;
    private TextView right_cabinet_temp,right_cabinet_top_temp,/*right_compressor_dcfan_state,right_cabinet_dcfan_state,*/
            right_doorheat,right_humidity,right_light,right_push_goods_raster,right_out_goods_raster,right_out_goods_door;
    private TextView mid_light,mid_door,mid_get_goods_raster,mid_anti_pinch_hand_raster,mid_get_door;
    
    private TextView leftTempControlAlternatPower,leftRefrigerationCompressorState,leftHeatingWireState,leftRecirculatAirFanState,
            rightTempControlAlternatPower,rightRefrigerationCompressorState,rightHeatingWireState,rightRecirculatAirFanState,
            leftLiftPlatformDownSwitch,leftLiftPlatformUpSwitch,rightLiftPlatformDownSwitch,rightLiftPlatformUpSwitch,
            leftOutGoodsDoorDownSwitch,leftOutGoodsDoorUpSwitch,rightOutGoodsDoorDownSwitch,rightOutGoodsDoorUpSwitch,
            midGetGoodsDoorDownSwitch,midGetGoodsDoorUpSwitch;
    private Button refresh_state;
    private Button stop_test;


    private Button left_fast_up,left_slow_up,left_fast_down,left_slow_down,left_stop,left_query_Y,left_move_Y, left_open_out_door,
            left_close_out_door,left_x_motor_zheng,left_x_motor_fan,left_open_side_light,left_close_side_light,left_open_door_heat,left_close_door_heat;
    private Button right_fast_up,right_slow_up,right_fast_down,right_slow_down,right_stop,right_query_Y,right_move_Y, right_open_out_door,
            right_close_out_door,right_x_motor_zheng,right_x_motor_fan,right_open_side_light,right_close_side_light,right_open_door_heat,right_close_door_heat;

    private EditText left_grid_position_ET,left_please_input_grid_position;
    private EditText right_grid_position_ET,right_please_input_grid_position;
    private TextView left_floot_position_1,left_floot_position_2,left_floot_position_3,left_floot_position_4,left_floot_position_5,left_floot_position_6,left_out_position;
    private TextView right_floot_position_1,right_floot_position_2,right_floot_position_3,right_floot_position_4,right_floot_position_5,right_floot_position_6,right_out_position;

    private TextView info_TV;

    private Button open_get_door,close_get_door,open_center_light,close_center_light;
    private EditText positionID_ET;
    private Button aisle_test;
    private EditText humidity_limit_ET;
    private Button humidity_limit_BT;

    private TextView left_temp_mode,left_temp_minus_TV,left_temp_setting,left_temp_add_TV;
    private TextView right_temp_mode,right_temp_minus_TV,right_temp_setting,right_temp_add_TV;
    private Button save_setting;
    
    private SerialPortUtils serialPortUtils = new SerialPortUtils();
    private Handler handler = new Handler();
    private String currentCommandCode;
    private int[] state = new int[42];//本来是32位，后来更改协议加了10位
    private int[] stateOld = new int[42];
    private DecimalFormat df   =   new DecimalFormat("#####0.0");
    private String Response;
    private int left_grid_position = 0;
    private int right_grid_position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_device_state_and_setting);
        initView();
        initSerial();
        initData();
    }

    private void initView() {
        returnBack = findViewById(R.id.returnBack);
        returnBack.setOnClickListener(this);

        info_TV =  findViewById(R.id.info_TV);

        left_cabinet_temp =  findViewById(R.id.left_cabinet_temp);
        left_cabinet_top_temp =  findViewById(R.id.left_cabinet_top_temp);
//        left_compressor_temp =  findViewById(R.id.left_compressor_temp);
//        left_compressor_dcfan_state =  findViewById(R.id.left_compressor_dcfan_state);
//        left_cabinet_dcfan_state =  findViewById(R.id.left_cabinet_dcfan_state);
//        left_out_cabinet_temp =  findViewById(R.id.left_out_cabinet_temp);
        left_doorheat =  findViewById(R.id.left_doorheat);
        left_humidity =  findViewById(R.id.left_humidity);
        left_light =  findViewById(R.id.left_light);
        left_push_goods_raster =  findViewById(R.id.left_push_goods_raster);
        left_out_goods_raster =  findViewById(R.id.left_out_goods_raster);
        left_out_goods_door =  findViewById(R.id.left_out_goods_door);
        
        right_cabinet_temp =  findViewById(R.id.right_cabinet_temp);
        right_cabinet_top_temp =  findViewById(R.id.right_cabinet_top_temp);
//        right_compressor_temp =  findViewById(R.id.right_compressor_temp);
//        right_compressor_dcfan_state =  findViewById(R.id.right_compressor_dcfan_state);
//        right_cabinet_dcfan_state =  findViewById(R.id.right_cabinet_dcfan_state);
//        right_out_cabinet_temp =  findViewById(R.id.right_out_cabinet_temp);
        right_doorheat =  findViewById(R.id.right_doorheat);
        right_humidity =  findViewById(R.id.right_humidity);
        right_light =  findViewById(R.id.right_light);
        right_push_goods_raster =  findViewById(R.id.right_push_goods_raster);
        right_out_goods_raster =  findViewById(R.id.right_out_goods_raster);
        right_out_goods_door =  findViewById(R.id.right_out_goods_door);

        mid_light =  findViewById(R.id.mid_light);
//        mid_door_lock =  findViewById(R.id.mid_door_lock);
        mid_door =  findViewById(R.id.mid_door);
        mid_get_goods_raster =  findViewById(R.id.mid_get_goods_raster);
//        mid_drop_goods_raster =  findViewById(R.id.mid_drop_goods_raster);
        mid_anti_pinch_hand_raster =  findViewById(R.id.mid_anti_pinch_hand_raster);
        mid_get_door =  findViewById(R.id.mid_get_door);
//        mid_drop_door =  findViewById(R.id.mid_drop_door);

        leftTempControlAlternatPower =  findViewById(R.id.leftTempControlAlternatPower);
        leftRefrigerationCompressorState =  findViewById(R.id.leftRefrigerationCompressorState);
        leftHeatingWireState =  findViewById(R.id.leftHeatingWireState);
        leftRecirculatAirFanState =  findViewById(R.id.leftRecirculatAirFanState);
        rightTempControlAlternatPower =  findViewById(R.id.rightTempControlAlternatPower);
        rightRefrigerationCompressorState =  findViewById(R.id.rightRefrigerationCompressorState);
        rightHeatingWireState =  findViewById(R.id.rightHeatingWireState);
        rightRecirculatAirFanState =  findViewById(R.id.rightRecirculatAirFanState);
        leftLiftPlatformDownSwitch =  findViewById(R.id.leftLiftPlatformDownSwitch);
        leftLiftPlatformUpSwitch =  findViewById(R.id.leftLiftPlatformUpSwitch);
        rightLiftPlatformDownSwitch =  findViewById(R.id.rightLiftPlatformDownSwitch);
        rightLiftPlatformUpSwitch =  findViewById(R.id.rightLiftPlatformUpSwitch);
        leftOutGoodsDoorDownSwitch =  findViewById(R.id.leftOutGoodsDoorDownSwitch);
        leftOutGoodsDoorUpSwitch =  findViewById(R.id.leftOutGoodsDoorUpSwitch);
        rightOutGoodsDoorDownSwitch =  findViewById(R.id.rightOutGoodsDoorDownSwitch);
        rightOutGoodsDoorUpSwitch =  findViewById(R.id.rightOutGoodsDoorUpSwitch);
        midGetGoodsDoorDownSwitch =  findViewById(R.id.midGetGoodsDoorDownSwitch);
        midGetGoodsDoorUpSwitch =  findViewById(R.id.midGetGoodsDoorUpSwitch);


        refresh_state =  findViewById(R.id.refresh_state);
        refresh_state.setOnClickListener(this);
        stop_test =  findViewById(R.id.stop_test);
        stop_test.setOnClickListener(this);

        left_fast_up =  findViewById(R.id.left_fast_up);
        left_fast_up.setOnClickListener(this);
        left_slow_up =  findViewById(R.id.left_slow_up);
        left_slow_up.setOnClickListener(this);
        left_fast_down =  findViewById(R.id.left_fast_down);
        left_fast_down.setOnClickListener(this);
        left_slow_down =  findViewById(R.id.left_slow_down);
        left_slow_down.setOnClickListener(this);
        left_stop =  findViewById(R.id.left_stop);
        left_stop.setOnClickListener(this);
        left_query_Y =  findViewById(R.id.left_query_Y);
        left_query_Y.setOnClickListener(this);
        left_move_Y =  findViewById(R.id.left_move_Y);
        left_move_Y.setOnClickListener(this);
        left_open_out_door =  findViewById(R.id.left_open_out_door);
        left_open_out_door.setOnClickListener(this);
        left_close_out_door =  findViewById(R.id.left_close_out_door);
        left_close_out_door.setOnClickListener(this);
        left_x_motor_zheng =  findViewById(R.id.left_x_motor_zheng);
        left_x_motor_zheng.setOnClickListener(this);
        left_x_motor_fan =  findViewById(R.id.left_x_motor_fan);
        left_x_motor_fan.setOnClickListener(this);
        left_open_side_light =  findViewById(R.id.left_open_side_light);
        left_open_side_light.setOnClickListener(this);
        left_close_side_light =  findViewById(R.id.left_close_side_light);
        left_close_side_light.setOnClickListener(this);
        left_open_door_heat =  findViewById(R.id.left_open_door_heat);
        left_open_door_heat.setOnClickListener(this);
        left_close_door_heat =  findViewById(R.id.left_close_door_heat);
        left_close_door_heat.setOnClickListener(this);
        left_grid_position_ET =  findViewById(R.id.left_grid_position_ET);
        left_please_input_grid_position =  findViewById(R.id.left_please_input_grid_position);

        right_fast_up =  findViewById(R.id.right_fast_up);
        right_fast_up.setOnClickListener(this);
        right_slow_up =  findViewById(R.id.right_slow_up);
        right_slow_up.setOnClickListener(this);
        right_fast_down =  findViewById(R.id.right_fast_down);
        right_fast_down.setOnClickListener(this);
        right_slow_down =  findViewById(R.id.right_slow_down);
        right_slow_down.setOnClickListener(this);
        right_stop =  findViewById(R.id.right_stop);
        right_stop.setOnClickListener(this);
        right_query_Y =  findViewById(R.id.right_query_Y);
        right_query_Y.setOnClickListener(this);
        right_move_Y =  findViewById(R.id.right_move_Y);
        right_move_Y.setOnClickListener(this);
        right_open_out_door =  findViewById(R.id.right_open_out_door);
        right_open_out_door.setOnClickListener(this);
        right_close_out_door =  findViewById(R.id.right_close_out_door);
        right_close_out_door.setOnClickListener(this);
        right_x_motor_zheng =  findViewById(R.id.right_x_motor_zheng);
        right_x_motor_zheng.setOnClickListener(this);
        right_x_motor_fan =  findViewById(R.id.right_x_motor_fan);
        right_x_motor_fan.setOnClickListener(this);
        right_open_side_light =  findViewById(R.id.right_open_side_light);
        right_open_side_light.setOnClickListener(this);
        right_close_side_light =  findViewById(R.id.right_close_side_light);
        right_close_side_light.setOnClickListener(this);
        right_open_door_heat =  findViewById(R.id.right_open_door_heat);
        right_open_door_heat.setOnClickListener(this);
        right_close_door_heat =  findViewById(R.id.right_close_door_heat);
        right_close_door_heat.setOnClickListener(this);
        right_grid_position_ET =  findViewById(R.id.right_grid_position_ET);
        right_please_input_grid_position =  findViewById(R.id.right_please_input_grid_position);

        open_get_door =  findViewById(R.id.open_get_door);
        open_get_door.setOnClickListener(this);
        close_get_door =  findViewById(R.id.close_get_door);
        close_get_door.setOnClickListener(this);
        open_center_light =  findViewById(R.id.open_center_light);
        open_center_light.setOnClickListener(this);
        close_center_light =  findViewById(R.id.close_center_light);
        close_center_light.setOnClickListener(this);

        positionID_ET =  findViewById(R.id.positionID_ET);
        aisle_test =  findViewById(R.id.aisle_test);
        aisle_test.setOnClickListener(this);
        humidity_limit_ET =  findViewById(R.id.humidity_limit_ET);
        humidity_limit_BT =  findViewById(R.id.humidity_limit_BT);
        humidity_limit_BT.setOnClickListener(this);


        left_floot_position_1 =  findViewById(R.id.left_floot_position_1);
        left_floot_position_1.setOnClickListener(this);
        left_floot_position_2 =  findViewById(R.id.left_floot_position_2);
        left_floot_position_2.setOnClickListener(this);
        left_floot_position_3 =  findViewById(R.id.left_floot_position_3);
        left_floot_position_3.setOnClickListener(this);
        left_floot_position_4 =  findViewById(R.id.left_floot_position_4);
        left_floot_position_4.setOnClickListener(this);
        left_floot_position_5 =  findViewById(R.id.left_floot_position_5);
        left_floot_position_5.setOnClickListener(this);
        left_floot_position_6 =  findViewById(R.id.left_floot_position_6);
        left_floot_position_6.setOnClickListener(this);
        left_out_position =  findViewById(R.id.left_out_position);
        left_out_position.setOnClickListener(this);

        right_floot_position_1 =  findViewById(R.id.right_floot_position_1);
        right_floot_position_1.setOnClickListener(this);
        right_floot_position_2 =  findViewById(R.id.right_floot_position_2);
        right_floot_position_2.setOnClickListener(this);
        right_floot_position_3 =  findViewById(R.id.right_floot_position_3);
        right_floot_position_3.setOnClickListener(this);
        right_floot_position_4 =  findViewById(R.id.right_floot_position_4);
        right_floot_position_4.setOnClickListener(this);
        right_floot_position_5 =  findViewById(R.id.right_floot_position_5);
        right_floot_position_5.setOnClickListener(this);
        right_floot_position_6 =  findViewById(R.id.right_floot_position_6);
        right_floot_position_6.setOnClickListener(this);
        right_out_position =  findViewById(R.id.right_out_position);
        right_out_position.setOnClickListener(this);
        
        left_temp_mode =  findViewById(R.id.left_temp_mode);
        left_temp_mode.setOnClickListener(this);
        left_temp_minus_TV =  findViewById(R.id.left_temp_minus_TV);
        left_temp_minus_TV.setOnClickListener(this);
        left_temp_setting =  findViewById(R.id.left_temp_setting);
        left_temp_add_TV =  findViewById(R.id.left_temp_add_TV);
        left_temp_add_TV.setOnClickListener(this);
        right_temp_mode =  findViewById(R.id.right_temp_mode);
        right_temp_mode.setOnClickListener(this);
        right_temp_minus_TV =  findViewById(R.id.right_temp_minus_TV);
        right_temp_minus_TV.setOnClickListener(this);
        right_temp_setting =  findViewById(R.id.right_temp_setting);
        right_temp_add_TV =  findViewById(R.id.right_temp_add_TV);
        right_temp_add_TV.setOnClickListener(this);
        save_setting =  findViewById(R.id.save_setting);
        save_setting.setOnClickListener(this);
    }
    private void initData() {
        refresh_state.performClick();
        SettingInfo settingInfo = DBManager.getSettingInfo(getApplicationContext());
        String[] str1 = settingInfo.getLeftFlootPosition().split(",");
        String[] str2 = settingInfo.getRightFlootPosition().split(",");
        if(str1[0].equals("0")){
            left_floot_position_1.setText(getResources().getString(R.string.left_floot_position_1));
        }else{
            left_floot_position_1.setText(str1[0]);
        }
        if(str1[1].equals("0")){
            left_floot_position_2.setText(getResources().getString(R.string.left_floot_position_2));
        }else{
            left_floot_position_2.setText(str1[1]);
        }
        if(str1[2].equals("0")){
            left_floot_position_3.setText(getResources().getString(R.string.left_floot_position_3));
        }else{
            left_floot_position_3.setText(str1[2]);
        }
        if(str1[3].equals("0")){
            left_floot_position_4.setText(getResources().getString(R.string.left_floot_position_4));
        }else{
            left_floot_position_4.setText(str1[3]);
        }
        if(str1[4].equals("0")){
            left_floot_position_5.setText(getResources().getString(R.string.left_floot_position_5));
        }else{
            left_floot_position_5.setText(str1[4]);
        }
        if(str1[5].equals("0")){
            left_floot_position_6.setText(getResources().getString(R.string.left_floot_position_6));
        }else{
            left_floot_position_6.setText(str1[5]);
        }
        if(str2[0].equals("0")){
            right_floot_position_1.setText(getResources().getString(R.string.right_floot_position_1));
        }else{
            right_floot_position_1.setText(str2[0]);
        }
        if(str2[1].equals("0")){
            right_floot_position_2.setText(getResources().getString(R.string.right_floot_position_2));
        }else{
            right_floot_position_2.setText(str2[1]);
        }
        if(str2[2].equals("0")){
            right_floot_position_3.setText(getResources().getString(R.string.right_floot_position_3));
        }else{
            right_floot_position_3.setText(str2[2]);
        }
        if(str2[3].equals("0")){
            right_floot_position_4.setText(getResources().getString(R.string.right_floot_position_4));
        }else{
            right_floot_position_4.setText(str2[3]);
        }
        if(str2[4].equals("0")){
            right_floot_position_5.setText(getResources().getString(R.string.right_floot_position_5));
        }else{
            right_floot_position_5.setText(str2[4]);
        }
        if(str2[5].equals("0")){
            right_floot_position_6.setText(getResources().getString(R.string.right_floot_position_6));
        }else{
            right_floot_position_6.setText(str2[5]);
        }
        left_out_position.setText(settingInfo.getLeftOutPosition());
        right_out_position.setText(settingInfo.getRightOutPosition());
        if(settingInfo.getLeftTempState() == 0){
            left_temp_mode.setText("制冷");
        }else if(settingInfo.getLeftTempState() == 1){
            left_temp_mode.setText("制热");
        }else if(settingInfo.getLeftTempState() == 2){
            left_temp_mode.setText("常温");
        }else{
            left_temp_mode.setText("恒温");
        }
        left_temp_setting.setText(df.format(((double)settingInfo.getLeftSetTemp()/10)));
        if(settingInfo.getRightTempState() == 0){
            right_temp_mode.setText("制冷");
        }else if(settingInfo.getRightTempState() == 1){
            right_temp_mode.setText("制热");
        }else if(settingInfo.getRightTempState() == 2){
            right_temp_mode.setText("常温");
        }else{
            right_temp_mode.setText("恒温");
        }
        right_temp_setting.setText(df.format(((double)settingInfo.getRightSetTemp()/10)));
        if(!new BindingManager().getHumidityLimit(getApplicationContext()).equals("")){
            humidity_limit_ET.setText(new BindingManager().getHumidityLimit(getApplicationContext())+"%");
            humidity_limit_BT.setText(getResources().getString(R.string.humidity_limit_BT_open));
            humidity_limit_BT.setTextColor(getResources().getColor(R.color.green_1));
        }else{
            humidity_limit_ET.setText("");
            humidity_limit_BT.setText(getResources().getString(R.string.humidity_limit_BT_close));
            humidity_limit_BT.setTextColor(getResources().getColor(R.color.black));
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.returnBack:
                serialPortUtils.close();
                this.finish();
                VMMainThreadFlag = true;
                mQuery1Flag = true;
                mQuery2Flag = true;
                mQuery0Flag = true;
                mUpdataDatabaseFlag = true;
                break;
            case R.id.refresh_state:
                serialPortUtils.addSendData("refresh_left");
                serialPortUtils.addSendData("refresh_right");
                serialPortUtils.addSendData("refresh_mid");
                serialPortUtils.addSendData("query_immediately_Y_left");
                serialPortUtils.addSendData("query_immediately_Y_right");
                serialPortUtils.addSendData("query_immediately_aisle_left");
                serialPortUtils.addSendData("query_immediately_aisle_right");
                serialPortUtils.addSendData("query_immediately_X_left");
                serialPortUtils.addSendData("query_immediately_X_right");
                serialPortUtils.addSendData("query_immediately_outGoodsDoor_left");
                serialPortUtils.addSendData("query_immediately_outGoodsDoor_right");
                serialPortUtils.addSendData("query_immediately_getGoodsDoor");
                break;
            case R.id.stop_test:
               serialPortUtils.stopTest();
                break;
            case R.id.left_fast_up:
               serialPortUtils.addSendData("left_fast_up");
                break;
            case R.id.left_slow_up:
               serialPortUtils.addSendData("left_slow_up");
                break;
            case R.id.left_fast_down:
               serialPortUtils.addSendData("left_fast_down");
                break;
            case R.id.left_slow_down:
               serialPortUtils.addSendData("left_slow_down");
                break;
            case R.id.left_stop:
               serialPortUtils.addSendData("left_stop");
                break;
            case R.id.left_query_Y:
               serialPortUtils.addSendData("left_stop");
                SystemClock.sleep(100);
               serialPortUtils.addSendData("left_query_Y");
                break;
            case R.id.left_move_Y:
                String left_grid_position = left_please_input_grid_position.getText().toString().trim();
                String txtY = left_please_input_grid_position.getText().toString();
                Pattern pY = Pattern.compile("[0-9]*");
                Matcher mY = pY.matcher(txtY);
                if(mY.matches() && !left_grid_position.equals("")){//输入的数字
                    if(Integer.parseInt(left_grid_position) > 0 && Integer.parseInt(left_grid_position) < 1000){
                       serialPortUtils.addSendData("left_move_Y"+","+left_grid_position);
                    }else{
                        ToastManager.getInstance().showToast(getApplicationContext(),"请输入大于0，小于1000的数字", Toast.LENGTH_LONG);
                        break;
                    }
                }else{
                    ToastManager.getInstance().showToast(getApplicationContext(),"请输入数字", Toast.LENGTH_LONG);
                    break;
                }
                break;
            case R.id.left_open_out_door:
               serialPortUtils.addSendData("left_open_out_door");
                break;
            case R.id.left_close_out_door:
               serialPortUtils.addSendData("left_close_out_door");
                break;
            case R.id.left_x_motor_zheng:
               serialPortUtils.addSendData("left_x_motor_zheng");
                break;
            case R.id.left_x_motor_fan:
               serialPortUtils.addSendData("left_x_motor_fan");
                break;
            case R.id.left_open_side_light:
               serialPortUtils.addSendData("left_open_side_light");
                break;
            case R.id.left_close_side_light:
               serialPortUtils.addSendData("left_close_side_light");
                break;
            case R.id.left_open_door_heat:
               serialPortUtils.addSendData("left_open_door_heat");
                break;
            case R.id.left_close_door_heat:
               serialPortUtils.addSendData("left_close_door_heat");
                break;
            case R.id.right_fast_up:
               serialPortUtils.addSendData("right_fast_up");
                break;
            case R.id.right_slow_up:
               serialPortUtils.addSendData("right_slow_up");
                break;
            case R.id.right_fast_down:
               serialPortUtils.addSendData("right_fast_down");
                break;
            case R.id.right_slow_down:
               serialPortUtils.addSendData("right_slow_down");
                break;
            case R.id.right_stop:
               serialPortUtils.addSendData("right_stop");
                break;
            case R.id.right_query_Y:
               serialPortUtils.addSendData("right_stop");
                SystemClock.sleep(100);
               serialPortUtils.addSendData("right_query_Y");
                break;
            case R.id.right_move_Y:
                String right_grid_position = right_please_input_grid_position.getText().toString().trim();
                String txtY2 = right_please_input_grid_position.getText().toString();
                Pattern pY2 = Pattern.compile("[0-9]*");
                Matcher mY2 = pY2.matcher(txtY2);
                if(mY2.matches() && !right_grid_position.equals("")){//输入的数字
                    if(Integer.parseInt(right_grid_position) > 0 && Integer.parseInt(right_grid_position) < 1000){
                       serialPortUtils.addSendData("right_move_Y"+","+right_grid_position);
                    }else{
                        ToastManager.getInstance().showToast(getApplicationContext(),"请输入大于0，小于1000的数字", Toast.LENGTH_LONG);
                        break;
                    }
                }else{
                    ToastManager.getInstance().showToast(getApplicationContext(),"请输入数字", Toast.LENGTH_LONG);
                    break;
                }
                break;
            case R.id.right_open_out_door:
               serialPortUtils.addSendData("right_open_out_door");
                break;
            case R.id.right_close_out_door:
               serialPortUtils.addSendData("right_close_out_door");
                break;
            case R.id.right_x_motor_zheng:
               serialPortUtils.addSendData("right_x_motor_zheng");
                break;
            case R.id.right_x_motor_fan:
               serialPortUtils.addSendData("right_x_motor_fan");
                break;
            case R.id.right_open_side_light:
               serialPortUtils.addSendData("right_open_side_light");
                break;
            case R.id.right_close_side_light:
               serialPortUtils.addSendData("right_close_side_light");
                break;
            case R.id.right_open_door_heat:
               serialPortUtils.addSendData("right_open_door_heat");
                break;
            case R.id.right_close_door_heat:
               serialPortUtils.addSendData("right_close_door_heat");
                break;
            case R.id.open_center_light:
               serialPortUtils.addSendData("open_center_light");
                break;
            case R.id.close_center_light:
               serialPortUtils.addSendData("close_center_light");
                break;
            case R.id.open_get_door:
               serialPortUtils.addSendData("open_get_door");
                break;
            case R.id.close_get_door:
               serialPortUtils.addSendData("close_get_door");
                break;
            case R.id.aisle_test:
                String positionID = positionID_ET.getText().toString().trim();
                String txtY3 = positionID_ET.getText().toString();
                Pattern pY3 = Pattern.compile("[0-9]*");
                Matcher mY3 = pY3.matcher(txtY3);
                if(mY3.matches() && !positionID.equals("")){//输入的数字
                    if(Integer.parseInt(positionID) >= 1 && Integer.parseInt(positionID) <= 200){
                       serialPortUtils.addSendData("aisle_test"+","+positionID);
                    }else{
                        ToastManager.getInstance().showToast(getApplicationContext(),"请输入1-200之间的数字", Toast.LENGTH_LONG);
                        break;
                    }
                }else{
                    ToastManager.getInstance().showToast(getApplicationContext(),"请输入数字", Toast.LENGTH_LONG);
                    break;
                }
                break;
            case R.id.humidity_limit_BT:
                if(humidity_limit_BT.getText().toString().equals(getResources().getString(R.string.humidity_limit_BT_close))){
                    String humidity_limit = humidity_limit_ET.getText().toString().trim();
                    String txtY4 = positionID_ET.getText().toString();
                    Pattern pY4 = Pattern.compile("[0-9]*");
                    Matcher mY4 = pY4.matcher(txtY4);
                    if(mY4.matches() && !humidity_limit.equals("")){//输入的数字
                        if(Integer.parseInt(humidity_limit) >= 1 && Integer.parseInt(humidity_limit) <= 100){
                            new BindingManager().setHumidityLimit(getApplicationContext(),humidity_limit);
                            humidity_limit_ET.setText(new BindingManager().getHumidityLimit(getApplicationContext())+"%");
                            humidity_limit_BT.setText(getResources().getString(R.string.humidity_limit_BT_open));
                            humidity_limit_BT.setTextColor(getResources().getColor(R.color.green_1));
                        }else{
                            ToastManager.getInstance().showToast(getApplicationContext(),"请输入1-100，代表湿度百分比", Toast.LENGTH_LONG);
                            break;
                        }
                    }else{
                        ToastManager.getInstance().showToast(getApplicationContext(),"请输入数字", Toast.LENGTH_LONG);
                        break;
                    }
                }else{
                    new BindingManager().setHumidityLimit(getApplicationContext(),"");
                    humidity_limit_ET.setText("");
                    humidity_limit_BT.setText(getResources().getString(R.string.humidity_limit_BT_close));
                    humidity_limit_BT.setTextColor(getResources().getColor(R.color.black));
                }
                break;
            case R.id.left_floot_position_1:
                if(left_floot_position_1.getText().toString().equals(getResources().getString(R.string.left_floot_position_1))){
                    left_floot_position_1.setText(left_grid_position_ET.getText().toString());
                }else{
                    left_floot_position_1.setText(getResources().getString(R.string.left_floot_position_1));
                }
                break;
            case R.id.left_floot_position_2:
                if(left_floot_position_2.getText().toString().equals(getResources().getString(R.string.left_floot_position_2))){
                    left_floot_position_2.setText(left_grid_position_ET.getText().toString());
                }else{
                    left_floot_position_2.setText(getResources().getString(R.string.left_floot_position_2));
                }
                break;
            case R.id.left_floot_position_3:
                if(left_floot_position_3.getText().toString().equals(getResources().getString(R.string.left_floot_position_3))){
                    left_floot_position_3.setText(left_grid_position_ET.getText().toString());
                }else{
                    left_floot_position_3.setText(getResources().getString(R.string.left_floot_position_3));
                }
                break;
            case R.id.left_floot_position_4:
                if(left_floot_position_4.getText().toString().equals(getResources().getString(R.string.left_floot_position_4))){
                    left_floot_position_4.setText(left_grid_position_ET.getText().toString());
                }else{
                    left_floot_position_4.setText(getResources().getString(R.string.left_floot_position_4));
                }
                break;
            case R.id.left_floot_position_5:
                if(left_floot_position_5.getText().toString().equals(getResources().getString(R.string.left_floot_position_5))){
                    left_floot_position_5.setText(left_grid_position_ET.getText().toString());
                }else{
                    left_floot_position_5.setText(getResources().getString(R.string.left_floot_position_5));
                }
                break;
            case R.id.left_floot_position_6:
                if(left_floot_position_6.getText().toString().equals(getResources().getString(R.string.left_floot_position_6))){
                    left_floot_position_6.setText(left_grid_position_ET.getText().toString());
                }else{
                    left_floot_position_6.setText(getResources().getString(R.string.left_floot_position_6));
                }
                break;
            case R.id.left_out_position:
                if(left_out_position.getText().toString().equals(getResources().getString(R.string.Null))){
                    left_out_position.setText(left_grid_position_ET.getText().toString());
                }else{
                    left_out_position.setText(getResources().getString(R.string.Null));
                }
                break;
            case R.id.right_floot_position_1:
                if(right_floot_position_1.getText().toString().equals(getResources().getString(R.string.right_floot_position_1))){
                    right_floot_position_1.setText(right_grid_position_ET.getText().toString());
                }else{
                    right_floot_position_1.setText(getResources().getString(R.string.right_floot_position_1));
                }
                break;
            case R.id.right_floot_position_2:
                if(right_floot_position_2.getText().toString().equals(getResources().getString(R.string.right_floot_position_2))){
                    right_floot_position_2.setText(right_grid_position_ET.getText().toString());
                }else{
                    right_floot_position_2.setText(getResources().getString(R.string.right_floot_position_2));
                }
                break;
            case R.id.right_floot_position_3:
                if(right_floot_position_3.getText().toString().equals(getResources().getString(R.string.right_floot_position_3))){
                    right_floot_position_3.setText(right_grid_position_ET.getText().toString());
                }else{
                    right_floot_position_3.setText(getResources().getString(R.string.right_floot_position_3));
                }
                break;
            case R.id.right_floot_position_4:
                if(right_floot_position_4.getText().toString().equals(getResources().getString(R.string.right_floot_position_4))){
                    right_floot_position_4.setText(right_grid_position_ET.getText().toString());
                }else{
                    right_floot_position_4.setText(getResources().getString(R.string.right_floot_position_4));
                }
                break;
            case R.id.right_floot_position_5:
                if(right_floot_position_5.getText().toString().equals(getResources().getString(R.string.right_floot_position_5))){
                    right_floot_position_5.setText(right_grid_position_ET.getText().toString());
                }else{
                    right_floot_position_5.setText(getResources().getString(R.string.right_floot_position_5));
                }
                break;
            case R.id.right_floot_position_6:
                if(right_floot_position_6.getText().toString().equals(getResources().getString(R.string.right_floot_position_6))){
                    right_floot_position_6.setText(right_grid_position_ET.getText().toString());
                }else{
                    right_floot_position_6.setText(getResources().getString(R.string.right_floot_position_6));
                }
                break;
            case R.id.right_out_position:
                if(right_out_position.getText().toString().equals(getResources().getString(R.string.Null))){
                    right_out_position.setText(right_grid_position_ET.getText().toString());
                }else{
                    right_out_position.setText(getResources().getString(R.string.Null));
                }
                break;
            case R.id.left_temp_mode:
                if(left_temp_mode.getText().toString().trim().equals("常温")){
                    left_temp_mode.setText("制冷");
                }else if(left_temp_mode.getText().toString().trim().equals("制冷")){
                    left_temp_mode.setText("制热");
                }else if(left_temp_mode.getText().toString().trim().equals("制热")){
                    left_temp_mode.setText("恒温");
                }else{
                    left_temp_mode.setText("常温");
                }
                break;
            case R.id.left_temp_minus_TV:
                left_temp_setting.setText(df.format(Double.parseDouble(left_temp_setting.getText().toString().trim()) - 0.5));
                break;
            case R.id.left_temp_add_TV:
                left_temp_setting.setText(df.format(Double.parseDouble(left_temp_setting.getText().toString().trim()) + 0.5));
                break;
            case R.id.right_temp_mode:
                if(right_temp_mode.getText().toString().trim().equals("常温")){
                    right_temp_mode.setText("制冷");
                }else if(right_temp_mode.getText().toString().trim().equals("制冷")){
                    right_temp_mode.setText("制热");
                }else if(right_temp_mode.getText().toString().trim().equals("制热")){
                    right_temp_mode.setText("恒温");
                }else{
                    right_temp_mode.setText("常温");
                }
                break;
            case R.id.right_temp_minus_TV:
                right_temp_setting.setText(df.format(Double.parseDouble(right_temp_setting.getText().toString().trim()) - 0.5));
                break;
            case R.id.right_temp_add_TV:
                right_temp_setting.setText(df.format(Double.parseDouble(right_temp_setting.getText().toString().trim()) + 0.5));
                break;
            case R.id.save_setting:
                int leftTempState = 0;
                int leftSetTemp = 0;
                int rightTempState = 0;
                int rightSetTemp = 0;
                if(left_temp_mode.getText().toString().trim().equals("常温")){
                    leftTempState = 2;
                }else if(left_temp_mode.getText().toString().trim().equals("制冷")){
                    leftTempState = 0;
                }else if(left_temp_mode.getText().toString().trim().equals("制热")){
                    leftTempState = 1;
                }else{
                    leftTempState = 3;
                }
                leftSetTemp = (int)(Double.parseDouble(left_temp_setting.getText().toString().trim())*10);
                if(right_temp_mode.getText().toString().trim().equals("常温")){
                    rightTempState = 2;
                }else if(right_temp_mode.getText().toString().trim().equals("制冷")){
                    rightTempState = 0;
                }else if(right_temp_mode.getText().toString().trim().equals("制热")){
                    rightTempState = 1;
                }else{
                    rightTempState = 3;
                }
                rightSetTemp = (int)(Double.parseDouble(right_temp_setting.getText().toString().trim())*10);
                DBManager.saveTemp(getApplicationContext(),leftTempState,leftSetTemp,rightTempState,rightSetTemp);
                String left_floot_position = "";
                String left_out = "";
                int left_floot_no = 0;
                if(left_floot_position_1.getText().toString().trim().equals(getResources().getString(R.string.left_floot_position_1))){
                    left_floot_position += "0,";
                }else{
                    left_floot_position += left_floot_position_1.getText().toString().trim();
                    left_floot_position += ",";
                    left_floot_no++;
                }
                if(left_floot_position_2.getText().toString().trim().equals(getResources().getString(R.string.left_floot_position_2))){
                    left_floot_position += "0,";
                }else{
                    left_floot_position += left_floot_position_2.getText().toString().trim();
                    left_floot_position += ",";
                    left_floot_no++;
                }
                if(left_floot_position_3.getText().toString().trim().equals(getResources().getString(R.string.left_floot_position_3))){
                    left_floot_position += "0,";
                }else{
                    left_floot_position += left_floot_position_3.getText().toString().trim();
                    left_floot_position += ",";
                    left_floot_no++;
                }
                if(left_floot_position_4.getText().toString().trim().equals(getResources().getString(R.string.left_floot_position_4))){
                    left_floot_position += "0,";
                }else{
                    left_floot_position += left_floot_position_4.getText().toString().trim();
                    left_floot_position += ",";
                    left_floot_no++;
                }
                if(left_floot_position_5.getText().toString().trim().equals(getResources().getString(R.string.left_floot_position_5))){
                    left_floot_position += "0,";
                }else{
                    left_floot_position += left_floot_position_5.getText().toString().trim();
                    left_floot_position += ",";
                    left_floot_no++;
                }
                if(left_floot_position_6.getText().toString().trim().equals(getResources().getString(R.string.left_floot_position_6))){
                    left_floot_position += "0";
                }else{
                    left_floot_position += left_floot_position_6.getText().toString().trim();
                    left_floot_no++;
                }
                if(left_out_position.getText().toString().trim().equals(getResources().getString(R.string.Null))){
                    left_out = "0";
                }else{
                    left_out = left_out_position.getText().toString().trim();
                }
                DBManager.setFloot(getApplicationContext(),1, String.valueOf(left_floot_no),left_out,left_floot_position);
                String right_floot_position = "";
                String right_out = "";
                int right_floot_no = 0;
                if(right_floot_position_1.getText().toString().trim().equals(getResources().getString(R.string.right_floot_position_1))){
                    right_floot_position += "0,";
                }else{
                    right_floot_position += right_floot_position_1.getText().toString().trim();
                    right_floot_position += ",";
                    right_floot_no++;
                }
                if(right_floot_position_2.getText().toString().trim().equals(getResources().getString(R.string.right_floot_position_2))){
                    right_floot_position += "0,";
                }else{
                    right_floot_position += right_floot_position_2.getText().toString().trim();
                    right_floot_position += ",";
                    right_floot_no++;
                }
                if(right_floot_position_3.getText().toString().trim().equals(getResources().getString(R.string.right_floot_position_3))){
                    right_floot_position += "0,";
                }else{
                    right_floot_position += right_floot_position_3.getText().toString().trim();
                    right_floot_position += ",";
                    right_floot_no++;
                }
                if(right_floot_position_4.getText().toString().trim().equals(getResources().getString(R.string.right_floot_position_4))){
                    right_floot_position += "0,";
                }else{
                    right_floot_position += right_floot_position_4.getText().toString().trim();
                    right_floot_position += ",";
                    right_floot_no++;
                }
                if(right_floot_position_5.getText().toString().trim().equals(getResources().getString(R.string.right_floot_position_5))){
                    right_floot_position += "0,";
                }else{
                    right_floot_position += right_floot_position_5.getText().toString().trim();
                    right_floot_position += ",";
                    right_floot_no++;
                }
                if(right_floot_position_6.getText().toString().trim().equals(getResources().getString(R.string.right_floot_position_6))){
                    right_floot_position += "0";
                }else{
                    right_floot_position += right_floot_position_6.getText().toString().trim();
                    right_floot_no++;
                }
                if(right_out_position.getText().toString().trim().equals(getResources().getString(R.string.Null))){
                    right_out = "0";
                }else{
                    right_out = right_out_position.getText().toString().trim();
                }
                DBManager.setFloot(getApplicationContext(),2, String.valueOf(right_floot_no),right_out,right_floot_position);
                refresh_state.performClick();
                ToastManager.getInstance().showToast(getApplicationContext(),"保存成功", Toast.LENGTH_LONG);
                break;
            default:
                break;
        }
    }
    private void initSerial() {
        VMMainThreadFlag = false;
        mQuery1Flag = false;
        mQuery2Flag = false;
        mQuery0Flag = false;
        mUpdataDatabaseFlag = false;
        OutGoodsThreadFlag = true;
        SystemClock.sleep(20);
        while(VMMainThreadRunning){
            SystemClock.sleep(20);
        }
       serialPortUtils.open(getApplicationContext());
       serialPortUtils.setOnDataReceiveListener(new SerialPortUtils.OnDataReceiveListener() {
            @Override
            public void onDataReceive(String code, byte[] rec, int size) {
                currentCommandCode = code;
                if (rec[0] == (byte) 0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte) 0x0F && rec[rec.length - 2] == (byte) 0xF1 && rec[6] == (byte) 0x65 && rec[3] == (byte) 0xC0) {
                    if (rec[7] == (byte) 0xEF && rec[8] == (byte) 0xFF) {//压缩机温度，有符号数，0xEFFF=故障
                        state[0] = 22222;
                    } else {
                        state[0] = ((rec[7] << 8) | (rec[8] & 0xFF));
                    }
                    if (rec[9] == (byte) 0xEF && rec[10] == (byte) 0xFF) {//边柜温度，有符号数，0xEFFF=故障
                        state[1] = 22222;
                    } else {
                        state[1] = ((rec[9] << 8) | (rec[10] & 0xFF));
                    }
                    if (rec[11] == (byte) 0xEF && rec[12] == (byte) 0xFF) {//边柜顶部温度，有符号数，0xEFFF=故障
                        state[2] = 22222;
                    } else {
                        state[2] = ((rec[11] << 8) | (rec[12] & 0xFF));
                    }
                    state[3] = rec[13] == (byte) 0x11 ? 2 : (int) rec[13];//压缩机直流风扇状态，0x00=未启动，0x01=正常，0x11=异常
                    state[4] = rec[14] == (byte) 0x11 ? 2 : (int) rec[14];//边柜直流风扇状态，0x00=未启动，0x01=正常，0x11=异常
                    if(rec[16] == (byte) 0xFF){
                        state[5] = 22222;
                        state[6] = 22222;
                    }else{
                        state[5] = (int) rec[15];//外部温度测量值，有符号数
                        state[6] = (int) rec[16];//湿度测量值，%RH，0xFF=温湿度模块故障
                    }
                    state[7] = (int) rec[17];//边柜门加热状态，0=关，1=开
                    state[8] = (int) rec[18];//照明灯状态，0=关，1=开
                    state[9] = (int) rec[19];//下货光栅状态，0=正常，1=故障
                    state[10] = (int) rec[20];//X轴出货光栅状态，0=正常，1=故障
                    state[11] = (int) rec[21];//出货门开关状态，0=关，1=开，2=半开半关

                    state[32] = (int) rec[22];//温控交流总电源状态（0=断开，1=接通）
                    state[33] = (int) rec[23];//制冷压缩机工作状态（0=停止，1=运转）
                    state[34] = (int) rec[24];//压缩机风扇工作状态（0=停止，1=运转）
                    state[35] = (int) rec[25];//制热电热丝工作状态（0=断开，1=接通）
                    state[36] = (int) rec[26];//循环风风扇工作状态（0=停止，1=运转）
                }
                if (rec[0] == (byte) 0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte) 0x0F && rec[rec.length - 2] == (byte) 0xF1 && rec[6] == (byte) 0x65 && rec[3] == (byte) 0xC1) {
                    if (rec[7] == (byte) 0xEF && rec[8] == (byte) 0xFF) {//压缩机温度，有符号数，0xEFFF=故障
                        state[12] = 22222;
                    } else {
                        state[12] = ((rec[7] << 8) | (rec[8] & 0xFF));
                    }
                    if (rec[9] == (byte) 0xEF && rec[10] == (byte) 0xFF) {//边柜温度，有符号数，0xEFFF=故障
                        state[13] = 22222;
                    } else {
                        state[13] = ((rec[9] << 8) | (rec[10] & 0xFF));
                    }
                    if (rec[11] == (byte) 0xEF && rec[12] == (byte) 0xFF) {//边柜顶部温度，有符号数，0xEFFF=故障
                        state[14] = 22222;
                    } else {
                        state[14] = ((rec[11] << 8) | (rec[12] & 0xFF));
                    }
                    state[15] = rec[13] == (byte) 0x11 ? 2 : (int) rec[13];//压缩机直流风扇状态，0x00=未启动，0x01=正常，0x11=异常
                    state[16] = rec[14] == (byte) 0x11 ? 2 : (int) rec[14];//边柜直流风扇状态，0x00=未启动，0x01=正常，0x11=异常
                    if(rec[16] == (byte) 0xFF){
                        state[17] = 22222;
                        state[18] = 22222;
                    }else{
                        state[17] = (int) rec[15];//外部温度测量值，有符号数
                        state[18] = (int) rec[16];//湿度测量值，%RH，0xFF=温湿度模块故障
                    }
                    state[19] = (int) rec[17];//边柜门加热状态，0=关，1=开
                    state[20] = (int) rec[18];//照明灯状态，0=关，1=开
                    state[21] = (int) rec[19];//下货光栅状态，0=正常，1=故障
                    state[22] = (int) rec[20];//X轴出货光栅状态，0=正常，1=故障
                    state[23] = (int) rec[21];//出货门开关状态，0=关，1=开，2=半开半关

                    state[37] = (int) rec[22];//温控交流总电源状态（0=断开，1=接通）
                    state[38] = (int) rec[23];//制冷压缩机工作状态（0=停止，1=运转）
                    state[39] = (int) rec[24];//压缩机风扇工作状态（0=停止，1=运转）
                    state[40] = (int) rec[25];//制热电热丝工作状态（0=断开，1=接通）
                    state[41] = (int) rec[26];//循环风风扇工作状态（0=停止，1=运转）
                }
                if (rec[0] == (byte) 0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte) 0x0F && rec[rec.length - 2] == (byte) 0xF1 && rec[6] == (byte) 0x6D && rec[3] == (byte) 0xE0) {
                    state[24] = (int) rec[7];//照明灯状态，0=关，1=开
                    state[25] = (int) rec[8];//门锁状态，0=上锁，1=开锁
                    state[26] = (int) rec[9];//门开关状态，0=关门，1=开门
                    state[27] = (int) rec[10];//取货光栅状态，0=正常，1=故障
                    state[28] = (int) rec[11];//落货光栅状态，0=正常，1=故障
                    state[29] = (int) rec[12];//防夹手光栅状态，0=正常，1=故障
                    state[30] = (int) rec[13];//取货门开关状态，0=关，1=开，2=半开半关
                    state[31] = (int) rec[14];//落货门开关状态，0=关，1=开，2=半开半关
                }
                if(stateOld[26] != state[26] || stateOld[25] != state[25]){//中间门门开关、中间门门锁状态
                    DBManager.updateDoorState(getApplicationContext(),state[26],state[25]);
                    stateOld[26] = state[26]; stateOld[25] = state[25];
                    log.info("更新门开关、中间门门锁状态");
                }
                if(stateOld[0] != state[0] || stateOld[1] != state[1] || stateOld[2] != state[2] || stateOld[5] != state[5] || stateOld[6] != state[6] ||
                        stateOld[12] != state[12] || stateOld[13] != state[13] || stateOld[14] != state[14] || stateOld[17] != state[17] || stateOld[18] != state[18]){//压缩机温度、边柜温度、边柜顶部温度、外部温度、湿度
                    DBManager.updateMeasureTemp(getApplicationContext(),state[0],state[1],state[2],state[5],state[6],state[12],state[13],state[14],state[17],state[18]);
                    stateOld[0] = state[0]; stateOld[1] = state[1]; stateOld[2] = state[2]; stateOld[5] = state[5]; stateOld[6] = state[6];
                    stateOld[12] = state[12]; stateOld[13] = state[13]; stateOld[14] = state[14]; stateOld[17] = state[17]; stateOld[18] = state[18];
                    log.info("更新压缩机温度、边柜温度、边柜顶部温度、外部温度、湿度");
                }
                if(stateOld[3] != state[3] || stateOld[4] != state[4] ||
                        stateOld[15] != state[15] || stateOld[16] != state[16]){//压缩机直流风扇状态和边柜直流风扇状态
                    DBManager.updateDCfan(getApplicationContext(),state[3],state[4],state[15],state[16]);
                    stateOld[3] = state[3]; stateOld[4] = state[4]; stateOld[15] = state[15]; stateOld[16] = state[16];
                    log.info("更新压缩机直流风扇状态和边柜直流风扇状态");
                }
                if(stateOld[7] != state[7] || stateOld[19] != state[19]){//门加热
                    DBManager.updateCounterDoorState(getApplicationContext(),state[7],state[19]);
                    stateOld[7] = state[7]; stateOld[19] = state[19];
                    log.info("更新门加热");
                }
                if(stateOld[8] != state[8] || stateOld[20] != state[20] || stateOld[24] != state[24]){//灯状态有变化
                    DBManager.updateLight(getApplicationContext(),state[8],state[20],state[24]);
                    stateOld[8] = state[8]; stateOld[20] = state[20]; stateOld[24] = state[24];
                    log.info("更新灯");
                }
                if(stateOld[9] != state[9] || stateOld[10] != state[10] || stateOld[21] != state[21] || stateOld[22] != state[22] ||
                        stateOld[27] != state[27] || stateOld[28] != state[28] || stateOld[29] != state[29]){//边柜下货光栅状态、X轴出货光栅状态，中柜取货光栅状态、落货光栅状态、防夹手光栅状态
                    DBManager.updateRasterState(getApplicationContext(),state[9],state[10],state[21],state[22],state[27],state[28],state[29]);
                    stateOld[9] = state[9]; stateOld[10] = state[10]; stateOld[21] = state[21]; stateOld[22] = state[22];
                    stateOld[27] = state[27]; stateOld[28] = state[28]; stateOld[29] = state[29];
                    log.info("更新光栅状态");
                }
                if(stateOld[11] != state[11] || stateOld[23] != state[23] || stateOld[30] != state[30] || stateOld[31] != state[31]){//出货门、取货门、落货门开关状态
                    DBManager.updateOtherDoorState(getApplicationContext(),state[11],state[23],state[30],state[31]);
                    stateOld[11] = state[11]; stateOld[23] = state[23]; stateOld[30] = state[30];stateOld[31] = state[31];
                    log.info("更新出货门、取货门、落货门开关状态");
                }
                if(stateOld[32] != state[32] || stateOld[33] != state[33] || stateOld[34] != state[34] || stateOld[35] != state[35] || stateOld[36] != state[36] ||
                        stateOld[37] != state[37] || stateOld[38] != state[38] || stateOld[39] != state[39] || stateOld[40] != state[40] || stateOld[41] != state[41]){//温控交流总电源状态、制冷压缩机工作状态、压缩机风扇工作状态、制热电热丝工作状态、循环风风扇工作状态
                    DBManager.updateFan(getApplicationContext(),state[32],state[33],state[34],state[35],state[36],state[37],state[38],state[39],state[40],state[41]);
                    stateOld[32] = state[32]; stateOld[33] = state[33]; stateOld[34] = state[34];stateOld[35] = state[35];stateOld[36] = state[36];
                    stateOld[37] = state[37]; stateOld[38] = state[38]; stateOld[39] = state[39];stateOld[40] = state[40];stateOld[41] = state[41];
                    log.info("温控交流总电源状态、制冷压缩机工作状态、压缩机风扇工作状态、制热电热丝工作状态、循环风风扇工作状态");
                }
                if(rec[0] == (byte) 0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte) 0x0F && rec[rec.length - 2] == (byte) 0xF1 && rec[6] == (byte) 0x79 && rec[7] == (byte) 0x59 ){
                    if(rec[3] == (byte)0xC0){
                        left_grid_position = (rec[16] & 0xff) * 256 + (rec[17] & 0xff);
                        DBManager.updateYState(getApplicationContext(),1,(rec[8]&0x04)==0x00? 0:1,(rec[8]&0x08)==0x00? 0:1);
                    }else{
                        right_grid_position = (rec[16] & 0xff) * 256 + (rec[17] & 0xff);
                        DBManager.updateYState(getApplicationContext(),2,(rec[8]&0x04)==0x00? 0:1,(rec[8]&0x08)==0x00? 0:1);
                    }
                    Response = "";
                    byte error1[];
                    error1 = byteTo8Byte(rec[9]);
                    if (error1[0] != (byte) 0x00) {
                        Response += "Y轴电机已执行动作 ";
                    } else {
                        Response += "Y轴电机未执行动作 ";
                    }
                    if (error1[1] == (byte) 0x01) {
                        Response += "Y轴电机过流，";
                    }
                    if (error1[2] == (byte) 0x01) {
                        Response += "Y轴电机断路，";
                    }
                    if (error1[3] == (byte) 0x01) {
                        Response += "Y轴上止点开关故障，";
                    }
                    if (error1[4] == (byte) 0x01) {
                        Response += "Y轴下止点开关故障，";
                    }
                    if (error1[5] == (byte) 0x01) {
                        Response += "Y轴电机超时，";
                    }
                    if (error1[6] == (byte) 0x01) {
                        Response += "Y轴码盘故障，";
                    }
                    if (error1[7] == (byte) 0x01) {
                        Response += "Y轴出货门定位开关故障，";
                    }
                    Response += "\r\n";
                    Response += "电机实际动作时间（毫秒）:" + ((rec[10] & 0xff) * 256 + (rec[11] & 0xff)) + "\r\n";
                    Response += "电机最大电流（毫安）:" + ((rec[12] & 0xff) * 256 + (rec[13] & 0xff)) + "\r\n";
                    Response += "电机平均电流（毫安）:" + ((rec[14] & 0xff) * 256 + (rec[15] & 0xff)) + "\r\n";
                }
                if(rec[0] == (byte)0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte)0x0F && rec[rec.length-2] == (byte)0xF1 && (rec[6] == (byte)0x6F || rec[6] == (byte)0x63) && rec[7] == (byte)0x5A && rec[16] == (byte)0x02){
                    if(rec[3] == (byte)0xC0){
                        DBManager.updateOutGoodsDoorSwitch(getApplicationContext(),1,(rec[8]&0x04)==0x00? 0:1,(rec[8]&0x08)==0x00? 0:1);
                    }else{
                        DBManager.updateOutGoodsDoorSwitch(getApplicationContext(),2,(rec[8]&0x04)==0x00? 0:1,(rec[8]&0x08)==0x00? 0:1);
                    }
                    Response = "";
                    byte error1[];
                    error1 = byteTo8Byte(rec[9]);
                    if (error1[0] != (byte)0x00) {
                        Response += "出货门电机已执行动作 ";
                    } else {
                        Response += "出货门电机未执行动作 ";
                    }
                    if(error1[1] == (byte)0x01){
                        Response += "出货门电机过流，";
                    }
                    if(error1[2] == (byte)0x01){
                        Response += "出货门电机断路，";
                    }
                    if(error1[3] == (byte)0x01){
                        Response += "出货门前止点开关故障，";
                    }
                    if(error1[4] == (byte)0x01){
                        Response += "出货门后止点开关故障，";
                    }
                    if(error1[5] == (byte)0x01){
                        Response += "开、关出货门超时，";
                    }
                    if(error1[6] == (byte)0x01){
                        Response += "出货门半开、半关，";
                    }
                    Response += "\r\n";
                    Response += "出货门电机实际动作时间（毫秒）:" + ((rec[10]&0xff) * 256 + (rec[11]&0xff)) + "\r\n";
                    Response += "出货门电机最大电流（毫安）:" + ((rec[12]&0xff) * 256 + (rec[13]&0xff)) + "\r\n";
                    Response += "出货门电机平均电流（毫安）:" + ((rec[14]&0xff) * 256 + (rec[15]&0xff)) + "\r\n";
                }
                if(rec[0] == (byte)0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte)0x0F && rec[rec.length-2] == (byte)0xF1 && rec[6] == (byte)0x78 && rec[7] == (byte)0x58 && rec[16] == (byte)0x02){
                    if(rec[3] == (byte)0xC0){
                        DBManager.updateOutGoodsRaster(getApplicationContext(),1,(rec[8]&0x04)==0x00? 0:1);
                    }else{
                        DBManager.updateOutGoodsRaster(getApplicationContext(),2,(rec[8]&0x04)==0x00? 0:1);
                    }
                    Response = "";
                    byte error1[];
                    error1 = byteTo8Byte(rec[9]);
                    if (error1[0] != (byte)0x00) {
                        Response += "X轴电机已执行动作 ";
                    } else {
                        Response += "X轴电机未执行动作 ";
                    }
                    if(error1[1] == (byte)0x01){
                        Response += "X轴电机过流，";
                    }
                    if(error1[2] == (byte)0x01){
                        Response += "X轴电机断路，";
                    }
                    if(error1[3] == (byte)0x01){
                        Response += "X轴出货光栅未检测到货物，";
                    }
                    if(error1[4] == (byte)0x01){
                        Response += "X轴出货光栅货物遮挡超时，";
                    }
                    if(error1[5] == (byte)0x01){
                        Response += "X轴出货光栅故障，";
                    }
                    if(error1[6] == (byte)0x01){
                        Response += "X轴电机超时，";
                    }
                    Response += "\r\n";
                    Response += "X轴电机实际动作时间（毫秒）:" + ((rec[10]&0xff) * 256 + (rec[11]&0xff)) + "\r\n";
                    Response += "X轴电机最大电流（毫安）:" + ((rec[12]&0xff) * 256 + (rec[13]&0xff)) + "\r\n";
                    Response += "X轴电机平均电流（毫安）:" + ((rec[14]&0xff) * 256 + (rec[15]&0xff)) + "\r\n";
                }
                if((rec[0] == (byte)0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte)0x0F && rec[rec.length-2] == (byte)0xF1 && rec[6] == (byte)0x64 && rec[3] == (byte)0xE0 && rec[7] == (byte)0x4D && rec[16] == (byte)0x02)||
                        (rec[0] == (byte)0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte)0x0F && rec[rec.length-2] == (byte)0xF1 && rec[6] == (byte)0x75 && rec[3] == (byte)0xE0 && rec[7] == (byte)0x4D && rec[16] == (byte)0x02)){
                    byte getGoodsDoorSwith = rec[8];
                    DBManager.updateGetGoodsDoorSwitch(getApplicationContext(),(getGoodsDoorSwith&0x40)==0x00? 0:1,(getGoodsDoorSwith&0x80)==0x00? 0:1);
                    Response = "";
                    byte error1[];
                    byte error2[];
                    error1 = byteTo8Byte(rec[9]);
                    error2 = byteTo8Byte(rec[8]);
                    if (error1[0] != (byte)0x00) {
                        Response += "取货门电机已执行动作 ";
                    } else {
                        Response += "取货门电机未执行动作 ";
                    }
                    if(error1[1] == (byte)0x01){
                        Response += "取货门电机过流，";
                    }
                    if(error1[2] == (byte)0x01){
                        Response += "取货门电机断路，";
                    }
                    if(error1[3] == (byte)0x01){
                        Response += "取货门上止点开关故障，";
                    }
                    if(error1[4] == (byte)0x01){
                        Response += "取货门下止点开关故障，";
                    }
                    if(error1[5] == (byte)0x01){
                        Response += "开、关取货门超时，";
                    }
                    if(error1[6] == (byte)0x01){
                        Response += "取货门半开、半关，";
                    }
                    if(error1[7] == (byte)0x01){
                        Response += "取货仓光栅检测无货物，";
                    }
                    if(error2[0] == (byte)0x01){
                        Response += "取货仓光栅检测有货物，";
                    }
                    if(error2[1] == (byte)0x01){
                        Response += "取货篮光栅故障，";
                    }
                    if(error2[2] == (byte)0x01){
                        Response += "防夹手光栅检测到遮挡物，";
                    }
                    if(error2[3] == (byte)0x01){
                        Response += "防夹手光栅故障，";
                    }
                    Response += "\r\n";
                    Response += "取货门电机实际动作时间（毫秒）:" + ((rec[10]&0xff) * 256 + (rec[11]&0xff)) + "\r\n";
                    Response += "取货门电机最大电流（毫安）:" + ((rec[12]&0xff) * 256 + (rec[13]&0xff)) + "\r\n";
                    Response += "取货门电机平均电流（毫安）:" + ((rec[14]&0xff) * 256 + (rec[15]&0xff)) + "\r\n";
                }
                if(rec[0] == (byte)0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte)0x0F && rec[rec.length-2] == (byte)0xF1 && rec[6] == (byte)0x70 && rec[7] == (byte)0x50 && rec[16] == (byte)0x02){
                    if(rec[3] == (byte)0x80){
                        DBManager.updatePushGoodsRaster(getApplicationContext(),1,(rec[8]&0x04)==0x00? 0:1);
                    }else{
                        DBManager.updatePushGoodsRaster(getApplicationContext(),2,(rec[8]&0x04)==0x00? 0:1);
                    }
                    Response = "";
                    byte error1[];
                    error1 = byteTo8Byte(rec[9]);
                    if (error1[0] != (byte)0x00) {
                        Response += "货道电机已执行动作 ";
                    } else {
                        Response += "货道电机未执行动作 ";
                    }
                    if(error1[1] == (byte)0x01){
                        Response += "货道电机过流，";
                    }
                    if(error1[2] == (byte)0x01){
                        Response += "货道电机断路，";
                    }
                    if(error1[3] == (byte)0x01){
                        Response += "货道超时（无货物输出的超时），";
                    }
                    if(error1[4] == (byte)0x01){
                        Response += "商品超时（货物遮挡光栅超时），";
                    }
                    if(error1[5] == (byte)0x01){
                        Response += "弹簧电机1反馈开关故障，";
                    }
                    if(error1[6] == (byte)0x01){
                        Response += "弹簧电机2反馈开关故障，";
                    }
                    if(error1[7] == (byte)0x01){
                        Response += "货道下货光栅故障，";
                    }
                    Response += "\r\n";
                    Response += "货道电机实际动作时间（毫秒）:" + ((rec[10]&0xff) * 256 + (rec[11]&0xff)) + "\r\n";
                    Response += "货道电机最大电流（毫安）:" + ((rec[12]&0xff) * 256 + (rec[13]&0xff)) + "\r\n";
                    Response += "货道电机平均电流（毫安）:" + ((rec[14]&0xff) * 256 + (rec[15]&0xff)) + "\r\n";
                }
                handler.post(runnable);
            }
            //开线程更新UI
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    switch (currentCommandCode) {
                        case "queryState":
                            if (state[1] == 22222) {
                                left_cabinet_temp.setText("故障");
                                left_cabinet_temp.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
                            } else {
                                left_cabinet_temp.setText(df.format((double) (state[1] / 10)));
                                left_cabinet_temp.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
                            if (state[2] == 22222) {
                                left_cabinet_top_temp.setText("故障");
                                left_cabinet_top_temp.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
                            } else {
                                left_cabinet_top_temp.setText(df.format((double) (state[2] / 10)));
                                left_cabinet_top_temp.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
//                            if (state[0] == 22222) {
//                                left_compressor_temp.setText("故障");
//                                left_compressor_temp.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
//                            } else {
//                                left_compressor_temp.setText(df.format((double) (state[0] / 10)));
//                                left_compressor_temp.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
//                            }
//                            if (state[3] == 0) {
//                                left_compressor_dcfan_state.setText("未启动");
//                                left_compressor_dcfan_state.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
//                            } else if (state[3] == 1) {
//                                left_compressor_dcfan_state.setText("正常");
//                                left_compressor_dcfan_state.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
//                            } else {
//                                left_compressor_dcfan_state.setText("异常");
//                                left_compressor_dcfan_state.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
//                            }
//                            if (state[4] == 0) {
//                                left_cabinet_dcfan_state.setText("未启动");
//                                left_cabinet_dcfan_state.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
//                            } else if (state[4] == 1) {
//                                left_cabinet_dcfan_state.setText("正常");
//                                left_cabinet_dcfan_state.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
//                            } else {
//                                left_cabinet_dcfan_state.setText("异常");
//                                left_cabinet_dcfan_state.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
//                            }
//                            if (state[5] == 22222) {
//                                left_out_cabinet_temp.setText("故障");
//                                left_out_cabinet_temp.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
//                            } else {
//                                left_out_cabinet_temp.setText(df.format((double) (state[5] / 10)));
//                                left_out_cabinet_temp.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
//                            }
                            if (state[6] == 22222) {
                                left_humidity.setText("故障");
                                left_humidity.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
                            } else {
                                left_humidity.setText(String.valueOf(state[6]) + "%");
                                left_humidity.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
                            if (state[7] == 0) {
                                left_doorheat.setText("关");
                                left_doorheat.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            } else {
                                left_doorheat.setText("开");
                                left_doorheat.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
                            if (state[8] == 0) {
                                left_light.setText("关");
                                left_light.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            } else {
                                left_light.setText("开");
                                left_light.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
//                            if (state[9] == 0) {
//                                left_push_goods_raster.setText("正常");
//                                left_push_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
//                            } else {
//                                left_push_goods_raster.setText("故障");
//                                left_push_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
//                            }
//                            if (state[10] == 0) {
//                                left_out_goods_raster.setText("正常");
//                                left_out_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
//                            } else {
//                                left_out_goods_raster.setText("故障");
//                                left_out_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
//                            }
                            if (state[11] == 0) {
                                left_out_goods_door.setText("关");
                                left_out_goods_door.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            } else if (state[11] == 1) {
                                left_out_goods_door.setText("开");
                                left_out_goods_door.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            } else {
                                left_out_goods_door.setText("半开半关");
                                left_out_goods_door.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
                            }

                            if (state[13] == 22222) {
                                right_cabinet_temp.setText("故障");
                                right_cabinet_temp.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
                            } else {
                                right_cabinet_temp.setText(df.format((double) (state[13] / 10)));
                                right_cabinet_temp.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
                            if (state[14] == 22222) {
                                right_cabinet_top_temp.setText("故障");
                                right_cabinet_top_temp.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
                            } else {
                                right_cabinet_top_temp.setText(df.format((double) (state[14] / 10)));
                                right_cabinet_top_temp.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
//                            if (state[12] == 22222) {
//                                right_compressor_temp.setText("故障");
//                                right_compressor_temp.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
//                            } else {
//                                right_compressor_temp.setText(df.format((double) (state[12] / 10)));
//                                right_compressor_temp.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
//                            }
//                            if (state[15] == 0) {
//                                right_compressor_dcfan_state.setText("未启动");
//                                right_compressor_dcfan_state.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
//                            } else if (state[15] == 1) {
//                                right_compressor_dcfan_state.setText("正常");
//                                right_compressor_dcfan_state.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
//                            } else {
//                                right_compressor_dcfan_state.setText("异常");
//                                right_compressor_dcfan_state.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
//                            }
//                            if (state[16] == 0) {
//                                right_cabinet_dcfan_state.setText("未启动");
//                                right_cabinet_dcfan_state.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
//                            } else if (state[16] == 1) {
//                                right_cabinet_dcfan_state.setText("正常");
//                                right_cabinet_dcfan_state.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
//                            } else {
//                                right_cabinet_dcfan_state.setText("异常");
//                                right_cabinet_dcfan_state.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
//                            }
//                            if (state[17] == 22222) {
//                                right_out_cabinet_temp.setText("故障");
//                                right_out_cabinet_temp.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
//                            } else {
//                                right_out_cabinet_temp.setText(df.format((double) (state[17] / 10)));
//                                right_out_cabinet_temp.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
//                            }
                            if (state[18] == 22222) {
                                right_humidity.setText("故障");
                                right_humidity.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
                            } else {
                                right_humidity.setText(String.valueOf(state[18]) + "%");
                                right_humidity.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
                            if (state[19] == 0) {
                                right_doorheat.setText("关");
                                right_doorheat.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            } else {
                                right_doorheat.setText("开");
                                right_doorheat.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
                            if (state[20] == 0) {
                                right_light.setText("关");
                                right_light.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            } else {
                                right_light.setText("开");
                                right_light.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
//                            if (state[21] == 0) {
//                                right_push_goods_raster.setText("正常");
//                                right_push_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
//                            } else {
//                                right_push_goods_raster.setText("故障");
//                                right_push_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
//                            }
//                            if (state[22] == 0) {
//                                right_out_goods_raster.setText("正常");
//                                right_out_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
//                            } else {
//                                right_out_goods_raster.setText("故障");
//                                right_out_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
//                            }
                            if (state[23] == 0) {
                                right_out_goods_door.setText("关");
                                right_out_goods_door.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            } else if (state[23] == 1) {
                                right_out_goods_door.setText("开");
                                right_out_goods_door.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            } else {
                                right_out_goods_door.setText("半开半关");
                                right_out_goods_door.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
                            }

                            if (state[24] == 0) {
                                mid_light.setText("关");
                                mid_light.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            } else {
                                mid_light.setText("开");
                                mid_light.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
//                            if (state[25] == 0) {
//                                mid_door_lock.setText("上锁");
//                                mid_door_lock.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
//                            } else {
//                                mid_door_lock.setText("开锁");
//                                mid_door_lock.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
//                            }
                            if (state[26] == 0) {
                                mid_door.setText("关门");
                                mid_door.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            } else {
                                mid_door.setText("开门");
                                mid_door.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
                            }
                            if (state[27] == 0) {
                                mid_get_goods_raster.setText("正常");
                                mid_get_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            } else {
                                mid_get_goods_raster.setText("故障");
                                mid_get_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
                            }
//                            if (state[28] == 0) {
//                                mid_drop_goods_raster.setText("正常");
//                                mid_drop_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
//                            } else {
//                                mid_drop_goods_raster.setText("故障");
//                                mid_drop_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
//                            }
                            if (state[29] == 0) {
                                mid_anti_pinch_hand_raster.setText("正常");
                                mid_anti_pinch_hand_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            } else {
                                mid_anti_pinch_hand_raster.setText("故障");
                                mid_anti_pinch_hand_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
                            }
                            if (state[30] == 0) {
                                mid_get_door.setText("关");
                                mid_get_door.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            } else if (state[30] == 1) {
                                mid_get_door.setText("开");
                                mid_get_door.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            } else {
                                mid_get_door.setText("半开半关");
                                mid_get_door.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
                            }
//                            if (state[31] == 0) {
//                                mid_drop_door.setText("关");
//                                mid_drop_door.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
//                            } else if (state[23] == 1) {
//                                mid_drop_door.setText("开");
//                                mid_drop_door.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
//                            } else {
//                                mid_drop_door.setText("半开半关");
//                                mid_drop_door.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
//                            }
                            if (state[32] == 0) {
                                leftTempControlAlternatPower.setText("断开");
                                leftTempControlAlternatPower.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            }else {
                                leftTempControlAlternatPower.setText("接通");
                                leftTempControlAlternatPower.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
                            if (state[33] == 0) {
                                leftRefrigerationCompressorState.setText("停止");
                                leftRefrigerationCompressorState.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            }else {
                                leftRefrigerationCompressorState.setText("运转");
                                leftRefrigerationCompressorState.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
                            if (state[35] == 0) {
                                leftHeatingWireState.setText("断开");
                                leftHeatingWireState.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            }else {
                                leftHeatingWireState.setText("接通");
                                leftHeatingWireState.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
                            if (state[36] == 0) {
                                leftRecirculatAirFanState.setText("停止");
                                leftRecirculatAirFanState.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            }else {
                                leftRecirculatAirFanState.setText("运转");
                                leftRecirculatAirFanState.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
                            if (state[37] == 0) {
                                rightTempControlAlternatPower.setText("断开");
                                rightTempControlAlternatPower.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            }else {
                                rightTempControlAlternatPower.setText("接通");
                                rightTempControlAlternatPower.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
                            if (state[38] == 0) {
                                rightRefrigerationCompressorState.setText("停止");
                                rightRefrigerationCompressorState.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            }else {
                                rightRefrigerationCompressorState.setText("运转");
                                rightRefrigerationCompressorState.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
                            if (state[40] == 0) {
                                rightHeatingWireState.setText("断开");
                                rightHeatingWireState.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            }else {
                                rightHeatingWireState.setText("接通");
                                rightHeatingWireState.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
                            if (state[41] == 0) {
                                rightRecirculatAirFanState.setText("停止");
                                rightRecirculatAirFanState.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            }else {
                                rightRecirculatAirFanState.setText("运转");
                                rightRecirculatAirFanState.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
                            break;
                        case "query_immediately":
                            if (DBManager.getMachineState(getApplicationContext()).getLeftLiftPlatformDownSwitch()==0) {
                                leftLiftPlatformDownSwitch.setText("合");
                                leftLiftPlatformDownSwitch.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }else {
                                leftLiftPlatformDownSwitch.setText("开");
                                leftLiftPlatformDownSwitch.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            }
                            if (DBManager.getMachineState(getApplicationContext()).getLeftLiftPlatformUpSwitch()==0) {
                                leftLiftPlatformUpSwitch.setText("合");
                                leftLiftPlatformUpSwitch.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }else {
                                leftLiftPlatformUpSwitch.setText("开");
                                leftLiftPlatformUpSwitch.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            }
                            if (DBManager.getMachineState(getApplicationContext()).getRightLiftPlatformDownSwitch()==0) {
                                rightLiftPlatformDownSwitch.setText("合");
                                rightLiftPlatformDownSwitch.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }else {
                                rightLiftPlatformDownSwitch.setText("开");
                                rightLiftPlatformDownSwitch.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            }
                            if (DBManager.getMachineState(getApplicationContext()).getRightLiftPlatformUpSwitch()==0) {
                                rightLiftPlatformUpSwitch.setText("合");
                                rightLiftPlatformUpSwitch.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }else {
                                rightLiftPlatformUpSwitch.setText("开");
                                rightLiftPlatformUpSwitch.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            }
                            if(DBManager.getMachineState(getApplicationContext()).getLeftPushGoodsRaster() == 0){
                                if (DBManager.getMachineState(getApplicationContext()).getLeftPushGoodsRasterImmediately()==0) {
                                    left_push_goods_raster.setText("透光");
                                    left_push_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                                }else {
                                    left_push_goods_raster.setText("遮光");
                                    left_push_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
                                }
                            }else{
                                left_push_goods_raster.setText("故障");
                                left_push_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
                            }
                            if(DBManager.getMachineState(getApplicationContext()).getRightPushGoodsRaster() == 0){
                                if (DBManager.getMachineState(getApplicationContext()).getRightPushGoodsRasterImmediately()==0) {
                                    right_push_goods_raster.setText("透光");
                                    right_push_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                                }else {
                                    right_push_goods_raster.setText("遮光");
                                    right_push_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
                                }
                            }else{
                                right_push_goods_raster.setText("故障");
                                right_push_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
                            }
                            if(DBManager.getMachineState(getApplicationContext()).getLeftOutGoodsRaster() == 0){
                                if (DBManager.getMachineState(getApplicationContext()).getLeftOutGoodsRasterImmediately()==0) {
                                    left_out_goods_raster.setText("透光");
                                    left_out_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                                }else {
                                    left_out_goods_raster.setText("遮光");
                                    left_out_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
                                }
                            }else{
                                left_out_goods_raster.setText("故障");
                                left_out_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
                            }
                            if(DBManager.getMachineState(getApplicationContext()).getRightOutGoodsRaster() == 0){
                                if (DBManager.getMachineState(getApplicationContext()).getRightOutGoodsRasterImmediately()==0) {
                                    right_out_goods_raster.setText("透光");
                                    right_out_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                                }else {
                                    right_out_goods_raster.setText("遮光");
                                    right_out_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
                                }
                            }else{
                                right_out_goods_raster.setText("故障");
                                right_out_goods_raster.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_fault));
                            }
                            if (DBManager.getMachineState(getApplicationContext()).getLeftOutGoodsDoorDownSwitch()==0) {
                                leftOutGoodsDoorDownSwitch.setText("开");
                                leftOutGoodsDoorDownSwitch.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            }else {
                                leftOutGoodsDoorDownSwitch.setText("合");
                                leftOutGoodsDoorDownSwitch.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
                            if (DBManager.getMachineState(getApplicationContext()).getLeftOutGoodsDoorUpSwitch()==0) {
                                leftOutGoodsDoorUpSwitch.setText("开");
                                leftOutGoodsDoorUpSwitch.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            }else {
                                leftOutGoodsDoorUpSwitch.setText("合");
                                leftOutGoodsDoorUpSwitch.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
                            if (DBManager.getMachineState(getApplicationContext()).getRightOutGoodsDoorDownSwitch()==0) {
                                rightOutGoodsDoorDownSwitch.setText("开");
                                rightOutGoodsDoorDownSwitch.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            }else {
                                rightOutGoodsDoorDownSwitch.setText("合");
                                rightOutGoodsDoorDownSwitch.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
                            if (DBManager.getMachineState(getApplicationContext()).getRightOutGoodsDoorUpSwitch()==0) {
                                rightOutGoodsDoorUpSwitch.setText("开");
                                rightOutGoodsDoorUpSwitch.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            }else {
                                rightOutGoodsDoorUpSwitch.setText("合");
                                rightOutGoodsDoorUpSwitch.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
                            if (DBManager.getMachineState(getApplicationContext()).getMidGetDoorDownSwitch()==0) {
                                midGetGoodsDoorDownSwitch.setText("开");
                                midGetGoodsDoorDownSwitch.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            }else {
                                midGetGoodsDoorDownSwitch.setText("合");
                                midGetGoodsDoorDownSwitch.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
                            if (DBManager.getMachineState(getApplicationContext()).getMidGetDoorUpSwitch()==0) {
                                midGetGoodsDoorUpSwitch.setText("开");
                                midGetGoodsDoorUpSwitch.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview_unstart));
                            }else {
                                midGetGoodsDoorUpSwitch.setText("合");
                                midGetGoodsDoorUpSwitch.setBackground(getResources().getDrawable(R.drawable.manager_device_state_textview));
                            }
                            break;
                        case "query_Y_left":
                            left_grid_position_ET.setText(String.valueOf(left_grid_position));
                            info_TV.setText(Response);
                            break;
                        case "query_Y_right":
                            right_grid_position_ET.setText(String.valueOf(right_grid_position));
                            info_TV.setText(Response);
                            break;
                        case "out_door_query_Done":
                        case "x_query_Done":
                        case "open_get_door_query_Done":
                        case "close_get_door_query_Done":
                        case "aisle_test_query_Done":
                            info_TV.setText(Response);
                            break;
                    }


                }
            };
        });
    }
    /**
     * 将byte转换为一个长度为8的byte数组，数组每个值代表bit
     * @param b 1个字节byte数据
     * */
    private static byte[] byteTo8Byte(byte b) {
        byte[] array = new byte[8];
        for (int i = 0; i <= 7; i++) {
            array[i] = (byte)(b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

}
