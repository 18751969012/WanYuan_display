package com.njust.wanyuan_display.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.njust.wanyuan_display.config.Constant;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.greenDao.Transaction;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.thread.VMMainThread;
import com.njust.wanyuan_display.util.DateUtil;

import org.apache.log4j.Logger;

import static com.njust.wanyuan_display.application.VMApplication.VMMainThreadFlag;
import static com.njust.wanyuan_display.application.VMApplication.mQuery0Flag;
import static com.njust.wanyuan_display.application.VMApplication.mQuery1Flag;
import static com.njust.wanyuan_display.application.VMApplication.mQuery2Flag;
import static com.njust.wanyuan_display.application.VMApplication.mUpdataDatabaseFlag;


public class VMService extends Service {
    private final Logger log = Logger.getLogger(VMService.class);
    private VMMainThread mainThread;

    @Override
    public void onCreate(){
        super.onCreate();
        Log.w("happy", "VMService启动");
        log.info("VMService启动");
        Transaction transaction =  DBManager.getLastTransaction(getApplicationContext());
        if(transaction != null) {
            if (transaction.getComplete().equals(String.valueOf(Constant.deliveryUnComplete))) {
                DBManager.deliveryHasConfirm(getApplicationContext(),new BindingManager().getSysorderNo(getApplicationContext()),
                        String.valueOf(Constant.deliveryError), DateUtil.getCurrentTime());
            }
        }
        VMMainThreadFlag = true;
        mQuery1Flag = true;
        mQuery2Flag = true;
        mQuery0Flag = true;
        mUpdataDatabaseFlag = true;
        mainThread = new VMMainThread(getApplicationContext());
        mainThread.initMainThread();
        mainThread.start();
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
