package com.njust.wanyuan_display.scm;

import android.content.Context;
import android.util.Log;

import com.njust.wanyuan_display.SerialPort.SerialPort;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.greenDao.AisleInfo;
import com.njust.wanyuan_display.greenDao.SettingInfo;
import com.njust.wanyuan_display.manager.BindingManager;
import com.njust.wanyuan_display.util.CRC16Util;
import com.njust.wanyuan_display.util.SerialPortUtils;

import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * 和XYS-1616通讯控制电机模仿触发光栅，实现自动测试
 */
public class TestAssistControl {
    private final Logger log = Logger.getLogger(TestAssistControl.class);
    private SerialPort S232 = null;
    private boolean threadStatus; //线程状态，为了安全终止线程
    private Timer timer;//定时发送
    private TimerTask timerTask;
    private Timer timer_assist = new Timer();//用于电机转一定时间停止
    private TimerTask timerTask1;//对应四个电机
    private TimerTask timerTask2;
    private TimerTask timerTask3;
    private TimerTask timerTask4;
    private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();//控制指令发送队列
    private boolean send = true;
    private int timeout;
    private int failCount = 0;
    private String currentCommandCode = "";//当前控制指令
    private byte[] buffer = new byte[64];
    private int motorTime = 500;//电机运动时间，用于定时器控制

    /**
     * 打开串口
     */
    public void open(){
        S232 = new SerialPort(3, 19200, 8, 'n', 1);
        threadStatus = false; //线程状态
        timer_startpoll();
        new ReadThread().start(); //开始线程监控是否有数据要接收
    }

    /**
     * 关闭串口
     */
    public void close(){
        stopTest();
        S232.close();
        this.threadStatus = true; //线程状态
        if(timerTask != null){
            timerTask.cancel();
            timerTask = null;
        }
        timer.cancel();
        if(timerTask1 != null){
            timerTask1.cancel();
            timerTask1 = null;
        }
        if(timerTask2 != null){
            timerTask2.cancel();
            timerTask2 = null;
        }
        if(timerTask3 != null){
            timerTask3.cancel();
            timerTask3 = null;
        }
        if(timerTask4 != null){
            timerTask4.cancel();
            timerTask4 = null;
        }
        timer_assist.cancel();
    }

    /**
     * 发送串口指令（字符串）
     */
    private void sendData(byte[] buf){
        S232.sendData(buf, buf.length);
    }

    public void addSendData(String commandCode){
        queue.add(commandCode);
    }
    /**
     * 停止测试，清空发送队列，停止发送
     */
    public void stopTest(){
        queue.clear();
        send = true;
        timeout = 0;
        failCount = 0;
        currentCommandCode = "";
    }
    /**
     * 驱动货道定时任务
     */
    private void timer_startpoll() {
        try {
            timer = new Timer(true);
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        if(send){
                            timeout = 0;
                            if(queue.size()>0){
                                currentCommandCode = queue.poll();
                                Log.w("happy","currentCommandCode:"+currentCommandCode);
                                String arr[] = currentCommandCode.split(",");
                                String commandCode = arr[0];
                                switch (commandCode) {
                                    case "left_pushGoodsRaster_connect":
                                        sendData(singleRelay(1,3,true));
                                        break;
                                    case "left_pushGoodsRaster_disconnect":
                                        sendData(singleRelay(1,3,false));
                                        break;
                                    case "left_outGoodsRaster_connect":
                                        sendData(singleRelay(1,4,true));
                                        break;
                                    case "left_outGoodsRaster_disconnect":
                                        sendData(singleRelay(1,4,false));
                                        break;
                                    case "right_pushGoodsRaster_connect":
                                        sendData(singleRelay(1,5,true));
                                        break;
                                    case "right_pushGoodsRaster_disconnect":
                                        sendData(singleRelay(1,5,false));
                                        break;
                                    case "right_outGoodsRaster_connect":
                                        sendData(singleRelay(1,6,true));
                                        break;
                                    case "right_outGoodsRaster_disconnect":
                                        sendData(singleRelay(1,6,false));
                                        break;
                                    case "all_connect":
                                        sendData(multiRelay(1,(byte)0xFF,(byte)0xFF));
                                        break;
                                    case "all_disconnect":
                                        sendData(multiRelay(1,(byte)0x00,(byte)0x00));
                                        break;
                                }
                                send = false;
                            }
                        }else {
                            timeout = timeout + 1;
                            Log.w("happy", "timeout:"+String.valueOf(timeout));
                            if (timeout>=5) {
                                timeout = 0;
                                Log.w("happy", "failCount:"+String.valueOf(failCount));
                                failCount++;
                                if(failCount >= 5){
                                    failCount = 0;
                                }else{
                                    queue.add(currentCommandCode);
                                }
                                send = true;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            timer.schedule(timerTask,0,5);
        } catch (Exception e) {
            timer_startpoll();
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * 单开一线程，来读数据
     */
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            //判断进程是否在运行，更安全的结束进程
            while (!threadStatus){
                int size; //读取数据的大小
                buffer = S232.receiveData();
                if(buffer != null){
                    StringBuilder str1 = new StringBuilder();
                    for (byte aRec : buffer) {
                        str1.append(Integer.toHexString(aRec & 0xFF)).append(" ");
                    }
                    Log.w("happy","接收数据：" + str1);
                    size = buffer.length;
                    if (size >= 5){
                        if(handleRec(buffer).equals("connect")){
                            failCount = 0;
                            send = true;
                        }else if(handleRec(buffer).equals("disconnect")){
                            failCount = 0;
                            send = true;
                        }else if(handleRec(buffer).equals("all")){
                            failCount = 0;
                            send = true;
                        }
                    }
                }
            }
        }
    }

    private String handleRec(byte[] rec) {
        if (rec[1] == (byte) 0x05 && rec[4] == (byte) 0xFF){
            switch (currentCommandCode) {
                case "left_pushGoodsRaster_connect":
                    if (timerTask1 != null) {
                        timerTask1.cancel();
                        timerTask1 = null;
                    }
                    timerTask1 = new TimerTask() {
                        @Override
                        public void run() {
                            queue.add("left_pushGoodsRaster_disconnect");
                            timerTask1.cancel();
                        }
                    };
                    timer_assist.schedule(timerTask1, motorTime);
                    break;
                case "left_outGoodsRaster_connect":
                    if (timerTask2 != null) {
                        timerTask2.cancel();
                        timerTask2 = null;
                    }
                    timerTask2 = new TimerTask() {
                        @Override
                        public void run() {
                            queue.add("left_outGoodsRaster_disconnect");
                            timerTask2.cancel();
                        }
                    };
                    timer_assist.schedule(timerTask2, motorTime);
                    break;
                case "right_pushGoodsRaster_connect":
                    if (timerTask3 != null) {
                        timerTask3.cancel();
                        timerTask3 = null;
                    }
                    timerTask3 = new TimerTask() {
                        @Override
                        public void run() {
                            queue.add("right_pushGoodsRaster_disconnect");
                            timerTask3.cancel();
                        }
                    };
                    timer_assist.schedule(timerTask3, motorTime);
                    break;
                case "right_outGoodsRaster_connect":
                    if (timerTask4 != null) {
                        timerTask4.cancel();
                        timerTask4 = null;
                    }
                    timerTask4 = new TimerTask() {
                        @Override
                        public void run() {
                            queue.add("right_outGoodsRaster_disconnect");
                            timerTask4.cancel();
                        }
                    };
                    timer_assist.schedule(timerTask4, motorTime);
                    break;
            }
            return "connect";
        }else if(rec[1] == (byte) 0x05 && rec[4] == (byte) 0x00){
            return "disconnect";
        }else if(rec[1] == (byte) 0x0F){
            return "all";
        }
        return "";
    }

    /**
     * 闭合/断开单个继电器指令
     * 说明：写单个线圈(控制继电器输出)
     * @param module 模块地址，需按照提前设置好的
     * @param address 地址: 1-16,17为蜂鸣器
     * */
    private byte[] singleRelay(int module, int address, boolean connect) {
        byte[] TXBuf = new byte[8];
        TXBuf[0] = (byte) module;
        TXBuf[1] = (byte) 0x05;
        TXBuf[2] = (byte) 0x00;
        TXBuf[3] = (byte) (address-1);
        TXBuf[4] = (byte) (connect?0xFF:0x00);
        TXBuf[5] = (byte) 0x00;
        int CRC = CRC16Util.calcCrc16(TXBuf,0,6);
        System.out.println(String.format("0x%04x", CRC));
        TXBuf[6] = (byte) ((CRC >> 8) & 0xFF);
        TXBuf[7] = (byte) (CRC & 0xFF);
        return TXBuf;
    }

    /**
     * 闭合/断开多个继电器指令
     * 说明：同时写16个线圈(控制继电器输出)
     * @param module 模块地址，需按照提前设置好的
     * @param addressH 地址: 9-16
     * @param addressL 地址: 1-8
     * */
    private byte[] multiRelay(int module, byte addressH, byte addressL) {
        byte[] TXBuf = new byte[11];
        TXBuf[0] = (byte) module;
        TXBuf[1] = (byte) 0x0F;
        TXBuf[2] = (byte) 0x00;
        TXBuf[3] = (byte) 0x00;
        TXBuf[4] = (byte) 0x00;
        TXBuf[5] = (byte) 0x10;
        TXBuf[6] = (byte) 0x02;
        TXBuf[7] = addressL;
        TXBuf[8] = addressH;
        int CRC = CRC16Util.calcCrc16(TXBuf,0,9);
        System.out.println(String.format("0x%04x", CRC));
        TXBuf[9] = (byte) ((CRC >> 8) & 0xFF);
        TXBuf[10] = (byte) (CRC & 0xFF);
        return TXBuf;
    }

}


































