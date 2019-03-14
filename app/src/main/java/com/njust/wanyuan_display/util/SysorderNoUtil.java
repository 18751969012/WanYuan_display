package com.njust.wanyuan_display.util;

import android.content.Context;

import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.greenDao.AisleInfo;

import java.util.Random;

/**
 * 生成系统订单号工具类
 */
public class SysorderNoUtil {
    public String getSysorderNo(String MachineID) {
        String orderNo = DateUtil.new_orderNo();
        orderNo += MachineID.substring(7);
        return orderNo;
    }
    public String getTestSysorderNo(String MachineID) {
        String orderNo = DateUtil.test_new_orderNo();
        orderNo += MachineID.substring(7);
        return orderNo;
    }
    public String getTestPositionID(Context context,String testRange) {
        boolean randomSuccess = false;
        String positionIDs = "";
        Random ran=new Random();
        String[] piece = new String[ran.nextInt(6)+1];//1-6随机数，代表件数
        for(int i=0;i<piece.length;i++){
            randomSuccess = false;
            while (!randomSuccess){
                int p = 0;
                if(testRange.equals("only_left")){
                    p = ran.nextInt(100)+1;
                }else if(testRange.equals("only_right")){
                    p = ran.nextInt(100)+101;
                }else{
                    p = ran.nextInt(200)+1;
                }
                AisleInfo aisleInfo = DBManager.getAisleInfo(context,p<=100?1:2,p);
                if(aisleInfo.getState() == 0){//货道正常（非锁死、非故障）
                    piece[i] = String.valueOf(p);
                    positionIDs = positionIDs + piece[i]+",";
                    randomSuccess = true;
                }
            }
        }
        positionIDs = positionIDs.substring(0,positionIDs.length()-1);
        return positionIDs;
    }
}
