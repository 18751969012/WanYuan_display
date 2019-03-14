package com.njust.wanyuan_display.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.njust.wanyuan_display.config.Constant;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.greenDao.Transaction;
import com.njust.wanyuan_display.greenDao.TransactionDao;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.thread.OutGoodsThread;
import com.njust.wanyuan_display.util.DateUtil;

import org.apache.log4j.Logger;

import static com.njust.wanyuan_display.application.VMApplication.OutGoodsThreadFlag;
import static com.njust.wanyuan_display.application.VMApplication.VMMainThreadFlag;
import static com.njust.wanyuan_display.application.VMApplication.VMMainThreadRunning;
import static com.njust.wanyuan_display.application.VMApplication.mQuery0Flag;
import static com.njust.wanyuan_display.application.VMApplication.mQuery1Flag;
import static com.njust.wanyuan_display.application.VMApplication.mQuery2Flag;
import static com.njust.wanyuan_display.application.VMApplication.mUpdataDatabaseFlag;


public class OutGoodsService extends Service {
    private final Logger log = Logger.getLogger(OutGoodsService.class);
    private OutGoodsThread mOutGoodsThread;

    @Override
    public void onCreate(){
        super.onCreate();
        if(!OutGoodsThreadFlag){
            Transaction transaction = DBManager.getLastTransaction(getApplicationContext());
            if(transaction != null){
                if(transaction.getComplete().equals("0")){
                    startOutGoodsThread();
                }
            }else{
                startOutGoodsThread();
            }
        }
    }
    private void startOutGoodsThread(){
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
        mOutGoodsThread = new OutGoodsThread(getApplicationContext());
        mOutGoodsThread.init();
        mOutGoodsThread.start();

        Log.w("happy", "出货主线程开启");
        log.info("出货主线程开启");
    }

    @Override
    public void onDestroy() {
        stop();
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

    public void stop(){
        mOutGoodsThread.rimBoard1 = Constant.wait;
        mOutGoodsThread.rimBoard2 = Constant.wait;
        mOutGoodsThread.aisleBoard1 = Constant.wait;
        mOutGoodsThread.aisleBoard2 = Constant.wait;
        mOutGoodsThread.midBoard = Constant.wait;
        OutGoodsThreadFlag = false;
        mOutGoodsThread.mTimer.cancel();
        mOutGoodsThread.mTimerOff.cancel();
        DBManager.deliveryHasConfirm(getApplicationContext(),new BindingManager().getSysorderNo(getApplicationContext()), String.valueOf(Constant.deliveryUnError), DateUtil.getCurrentTime());
    }
}
