package com.njust.wanyuan_display.util;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.njust.wanyuan_display.SerialPort.SerialPort;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.greenDao.MachineState;
import com.njust.wanyuan_display.greenDao.SettingInfo;
import com.njust.wanyuan_display.scm.MotorControl2;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import static com.njust.wanyuan_display.application.VMApplication.aisleZNum1;
import static com.njust.wanyuan_display.application.VMApplication.aisleZNum2;
import static com.njust.wanyuan_display.application.VMApplication.midZNum;
import static com.njust.wanyuan_display.application.VMApplication.rimZNum1;
import static com.njust.wanyuan_display.application.VMApplication.rimZNum2;

public class SerialPortUtils {

    private final Logger log = Logger.getLogger(SerialPortUtils.class);
    private Context mContext;
    private MotorControl2 mMotorControl2 = new MotorControl2();
    private boolean serialPortStatus = false; //是否打开串口标志
    private boolean threadStatus; //线程状态，为了安全终止线程
    public boolean stopThreadStatus = false; //线程状态，为了出货中暂停线程，不占用485通讯
    private SerialPort serialPort = null;
    private Timer timer;
    private TimerTask timerTask;
    private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();//控制指令发送队列
    private LinkedBlockingQueue<String> queryQueue = new LinkedBlockingQueue<>();//查询指令发送队列
    private boolean send = true;
    private boolean isQuery = false;//用于区分两个队列
    private boolean isCommand = false;//边柜和中柜的命令指令不检测接收反馈，只是发送，标志位用来切换发送定时器的状态，同样适用于行列测试货道
    private boolean isReTransmission = false;//当通过重发机制添加发送队列时，接收数据可能会在两次定时启动中处理完毕，就会产生多余的发送队列数据导致故障，标志位判断这段时间
    private String reTransmission = "";//配合isReTransmission使用，假如发生移除多余数据
    private int timeout;
    private int failCount = 0;
    private String currentCommandCode = "";//当前控制指令
    private String currentAisleCode = "";//用于几个不同类型的货道测试的区分
    private int currentAisleID;
    private int current_row_no = 1;
    private String row = "1";
    private int current_column_no = 1;
    private String column = "1";
    private boolean moveFloorByMapan;//因为反馈信息和查询一样，所以用来区分开，此指令不更新ui
    private ArrayList<Integer> dataList= new ArrayList<>();
    private int test_all_no = 0;
    private byte[] buffer = new byte[64];

    /**
     * 打开串口
     */
    public void open(Context mContext){
        this.mContext = mContext;
        serialPort =  new SerialPort(1, 38400, 8, 'n', 1);
        this.serialPortStatus = true;
        threadStatus = false; //线程状态
        stopThreadStatus = false;
        timer_startpoll();
        new ReadThread().start(); //开始线程监控是否有数据要接收
    }

    /**
     * 关闭串口
     */
    public void close(){
//        serialPort.close();
        this.serialPortStatus = false;
        this.threadStatus = true; //线程状态
        this.stopThreadStatus = true;
        if(timerTask != null){
            timerTask.cancel();
            timerTask = null;
        }
        timer.cancel();
    }

    /**
     * 发送串口指令（字符串）
     */
    private void sendData(byte[] buf){
        serialPort.sendData(buf, buf.length);
    }

    public void addSendData(String commandCode){
        if(commandCode.equals("test_all_aisle")){
            dataList = DBManager.getAisleInfoListUnlocked2(mContext);
            test_all_no = 0;
        }
        queue.add(commandCode);
    }
    /**
     * 停止测试，清空发送队列，停止发送
     */
    public void stopTest(){
        queue.clear();
        queryQueue.clear();
        send = true;
        isQuery = false;
        timeout = 0;
        failCount = 0;
        currentCommandCode = "";
        currentAisleCode = "";
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
                        if(!stopThreadStatus){
                            if(send){
                                timeout = 0;
                                if(queryQueue.size()>0){
                                    currentCommandCode = queryQueue.poll();
                                    Log.w("happy","queryQueue-currentCommandCode:"+currentCommandCode);
                                    String arr[] = currentCommandCode.split(",");
                                    String commandCode = arr[0];
                                    switch (commandCode) {
                                        case "aisle_query":
                                            if (currentAisleID >= 1 && currentAisleID <= 100) {
                                                sendData(mMotorControl2.query((byte) 0x03, (byte) 0x80, aisleZNum1++));
                                            } else {
                                                sendData(mMotorControl2.query((byte) 0x03, (byte) 0x81, aisleZNum2++));
                                            }
                                            break;
                                        case "open_out_door_query_left":
                                            sendData(mMotorControl2.query((byte) 0x04, (byte) 0xC0, rimZNum1++));
                                            break;
                                        case "open_out_door_query_right":
                                            sendData(mMotorControl2.query((byte) 0x04, (byte) 0xC1, rimZNum2++));
                                            break;
                                        case "close_out_door_query_left":
                                            sendData(mMotorControl2.query((byte) 0x05, (byte) 0xC0, rimZNum1++));
                                            break;
                                        case "close_out_door_query_right":
                                            sendData(mMotorControl2.query((byte) 0x05, (byte) 0xC1, rimZNum2++));
                                            break;
                                        case "x_query_left":
                                            sendData(mMotorControl2.query((byte) 0x02, (byte) 0xC0, rimZNum1++));
                                            break;
                                        case "x_query_right":
                                            sendData(mMotorControl2.query((byte) 0x02, (byte) 0xC1, rimZNum2++));
                                            break;
                                        case "open_get_door_query":
                                            sendData(mMotorControl2.query((byte) 0x07, (byte) 0xE0, midZNum++));
                                            break;
                                        case "close_get_door_query":
                                            sendData(mMotorControl2.query((byte) 0x08, (byte) 0xE0, midZNum++));
                                            break;
                                    }
                                    send = false;
                                    isQuery = true;
                                }else{
                                    if(queue.size()>0){
                                        currentCommandCode = queue.poll();
                                        Log.w("happy","queue-currentCommandCode:"+currentCommandCode);
                                        String arr[] = currentCommandCode.split(",");
                                        String commandCode = arr[0];
                                        isCommand = false;
                                        switch (commandCode) {
                                            case "refresh_left": {
                                                SettingInfo settingInfo = DBManager.getSettingInfo(mContext);
                                                if (settingInfo.getLeftTempState() == 3) {
                                                    MachineState machineState = DBManager.getMachineState(mContext);
                                                    if (machineState.getLeftCabinetTemp() == 22222) {
                                                        sendData(mMotorControl2.counterQuery(mContext, 1, rimZNum1++, 2, settingInfo.getLeftSetTemp()));
                                                    } else {
                                                        if (machineState.getLeftCabinetTemp() > (settingInfo.getLeftSetTemp() + 50)) {
                                                            sendData(mMotorControl2.counterQuery(mContext, 1, rimZNum1++, 0, settingInfo.getLeftSetTemp()));
                                                        } else if (machineState.getLeftCabinetTemp() < (settingInfo.getLeftSetTemp() - 50)) {
                                                            sendData(mMotorControl2.counterQuery(mContext, 1, rimZNum1++, 1, settingInfo.getLeftSetTemp()));
                                                        } else if (machineState.getLeftCabinetTemp() > (settingInfo.getLeftSetTemp() - 20) && machineState.getLeftCabinetTemp() < (settingInfo.getLeftSetTemp() + 20)) {
                                                            sendData(mMotorControl2.counterQuery(mContext, 1, rimZNum1++, 2, settingInfo.getLeftSetTemp()));
                                                        }
                                                    }
                                                } else {
                                                    sendData(mMotorControl2.counterQuery(mContext, 1, rimZNum1++, settingInfo.getLeftTempState(), settingInfo.getLeftSetTemp()));
                                                }
                                                break;
                                            }
                                            case "refresh_right": {
                                                SettingInfo settingInfo = DBManager.getSettingInfo(mContext);
                                                if (settingInfo.getRightTempState() == 3) {
                                                    MachineState machineState = DBManager.getMachineState(mContext);
                                                    if (machineState.getRightCabinetTemp() == 22222) {
                                                        sendData(mMotorControl2.counterQuery(mContext, 2, rimZNum2++, 2, settingInfo.getRightSetTemp()));
                                                    } else {
                                                        if (machineState.getRightCabinetTemp() > (settingInfo.getRightSetTemp() + 50)) {
                                                            sendData(mMotorControl2.counterQuery(mContext, 2, rimZNum2++, 0, settingInfo.getRightSetTemp()));
                                                        } else if (machineState.getRightCabinetTemp() < (settingInfo.getRightSetTemp() - 50)) {
                                                            sendData(mMotorControl2.counterQuery(mContext, 2, rimZNum2++, 1, settingInfo.getRightSetTemp()));
                                                        } else if (machineState.getRightCabinetTemp() > (settingInfo.getRightSetTemp() - 20) && machineState.getRightCabinetTemp() < (settingInfo.getRightSetTemp() + 20)) {
                                                            sendData(mMotorControl2.counterQuery(mContext, 2, rimZNum2++, 2, settingInfo.getRightSetTemp()));
                                                        }
                                                    }
                                                } else {
                                                    sendData(mMotorControl2.counterQuery(mContext, 2, rimZNum2++, settingInfo.getRightTempState(), settingInfo.getRightSetTemp()));
                                                }
                                                break;
                                            }
                                            case "refresh_mid":
                                                sendData(mMotorControl2.centerQuery(midZNum++));
                                                break;
                                            case "query_immediately_Y_left":
                                                sendData(mMotorControl2.query((byte) 0x01, (byte) 0xC0, aisleZNum1++));
                                                break;
                                            case "query_immediately_Y_right":
                                                sendData(mMotorControl2.query((byte) 0x01, (byte) 0xC1, aisleZNum2++));
                                                break;
                                            case "query_immediately_aisle_left":
                                                sendData(mMotorControl2.query((byte) 0x03, (byte) 0x80, aisleZNum1++));
                                                break;
                                            case "query_immediately_aisle_right":
                                                sendData(mMotorControl2.query((byte) 0x03, (byte) 0x81, aisleZNum2++));
                                                break;
                                            case "query_immediately_X_left":
                                                sendData(mMotorControl2.query((byte) 0x02, (byte) 0xC0, aisleZNum1++));
                                                break;
                                            case "query_immediately_X_right":
                                                sendData(mMotorControl2.query((byte) 0x02, (byte) 0xC1, aisleZNum2++));
                                                break;
                                            case "query_immediately_outGoodsDoor_left":
                                                sendData(mMotorControl2.query((byte) 0x05, (byte) 0xC0, aisleZNum1++));
                                                break;
                                            case "query_immediately_outGoodsDoor_right":
                                                sendData(mMotorControl2.query((byte) 0x05, (byte) 0xC1, aisleZNum2++));
                                                break;
                                            case "query_immediately_getGoodsDoor":
                                                sendData(mMotorControl2.query((byte) 0x08, (byte) 0xE0, midZNum++));
                                                break;
                                            case "left_fast_up":
                                                sendData(mMotorControl2.counterCommand(1, rimZNum1++, 5));
                                                isCommand = true;
                                                break;
                                            case "left_slow_up":
                                                sendData(mMotorControl2.counterCommand(1, rimZNum1++, 7));
                                                isCommand = true;
                                                break;
                                            case "left_fast_down":
                                                sendData(mMotorControl2.counterCommand(1, rimZNum1++, 6));
                                                isCommand = true;
                                                break;
                                            case "left_slow_down":
                                                sendData(mMotorControl2.counterCommand(1, rimZNum1++, 8));
                                                isCommand = true;
                                                break;
                                            case "left_stop":
                                                sendData(mMotorControl2.counterCommand(1, rimZNum1++, 9));
                                                isCommand = true;
                                                break;
                                            case "right_fast_up":
                                                sendData(mMotorControl2.counterCommand(2, rimZNum2++, 5));
                                                isCommand = true;
                                                break;
                                            case "right_slow_up":
                                                sendData(mMotorControl2.counterCommand(2, rimZNum2++, 7));
                                                isCommand = true;
                                                break;
                                            case "right_fast_down":
                                                sendData(mMotorControl2.counterCommand(2, rimZNum2++, 6));
                                                isCommand = true;
                                                break;
                                            case "right_slow_down":
                                                sendData(mMotorControl2.counterCommand(2, rimZNum2++, 8));
                                                isCommand = true;
                                                break;
                                            case "right_stop":
                                                sendData(mMotorControl2.counterCommand(2, rimZNum2++, 9));
                                                isCommand = true;
                                                break;
                                            case "left_query_Y":
                                                sendData(mMotorControl2.query((byte) 0x0B, (byte) 0xC0, rimZNum1++));
                                                break;
                                            case "right_query_Y":
                                                sendData(mMotorControl2.query((byte) 0x0B, (byte) 0xC1, rimZNum2++));
                                                break;
                                            case "left_move_Y":
                                                moveFloorByMapan = true;
                                                sendData(mMotorControl2.moveFloorByMapan(1, rimZNum1++, Integer.parseInt(arr[1])));
                                                break;
                                            case "right_move_Y":
                                                moveFloorByMapan = true;
                                                sendData(mMotorControl2.moveFloorByMapan(2, rimZNum2++, Integer.parseInt(arr[1])));
                                                break;
                                            case "left_open_out_door":
                                                sendData(mMotorControl2.openOutGoodsDoor(1, rimZNum1++));
                                                break;
                                            case "right_open_out_door":
                                                sendData(mMotorControl2.openOutGoodsDoor(2, rimZNum2++));
                                                break;
                                            case "left_close_out_door":
                                                sendData(mMotorControl2.closeOutGoodsDoor(1, rimZNum1++));
                                                break;
                                            case "right_close_out_door":
                                                sendData(mMotorControl2.closeOutGoodsDoor(2, rimZNum2++));
                                                break;
                                            case "left_x_motor_zheng":
                                                sendData(mMotorControl2.moveHorizontal(1, rimZNum1++, 1, 3600));
                                                break;
                                            case "left_x_motor_fan":
                                                sendData(mMotorControl2.moveHorizontal(1, rimZNum1++, 2, 3600));
                                                break;
                                            case "right_x_motor_zheng":
                                                sendData(mMotorControl2.moveHorizontal(2, rimZNum2++, 1, 3600));
                                                break;
                                            case "right_x_motor_fan":
                                                sendData(mMotorControl2.moveHorizontal(2, rimZNum2++, 2, 3600));
                                                break;
                                            case "left_open_side_light":
                                                sendData(mMotorControl2.counterCommand(1, rimZNum1++, 1));
                                                isCommand = true;
                                                break;
                                            case "left_close_side_light":
                                                sendData(mMotorControl2.counterCommand(1, rimZNum1++, 2));
                                                isCommand = true;
                                                break;
                                            case "left_open_door_heat":
                                                sendData(mMotorControl2.counterCommand(1, rimZNum1++, 3));
                                                isCommand = true;
                                                break;
                                            case "left_close_door_heat":
                                                sendData(mMotorControl2.counterCommand(1, rimZNum1++, 4));
                                                isCommand = true;
                                                break;
                                            case "right_open_side_light":
                                                sendData(mMotorControl2.counterCommand(2, rimZNum2++, 1));
                                                isCommand = true;
                                                break;
                                            case "right_close_side_light":
                                                sendData(mMotorControl2.counterCommand(2, rimZNum2++, 2));
                                                isCommand = true;
                                                break;
                                            case "right_open_door_heat":
                                                sendData(mMotorControl2.counterCommand(2, rimZNum2++, 3));
                                                isCommand = true;
                                                break;
                                            case "right_close_door_heat":
                                                sendData(mMotorControl2.counterCommand(2, rimZNum2++, 4));
                                                isCommand = true;
                                                break;
                                            case "open_center_light":
                                                sendData(mMotorControl2.centerCommand(midZNum++, 1));
                                                isCommand = true;
                                                break;
                                            case "close_center_light":
                                                sendData(mMotorControl2.centerCommand(midZNum++, 2));
                                                isCommand = true;
                                                break;
                                            case "open_get_door":
                                                sendData(mMotorControl2.openGetGoodsDoor(midZNum++));
                                                break;
                                            case "close_get_door":
                                                sendData(mMotorControl2.closeGetGoodsDoor(midZNum++));
                                                break;
                                            case "aisle_test":
                                                currentAisleCode = currentCommandCode;
                                                if (Integer.parseInt(arr[1]) >= 1 && Integer.parseInt(arr[1]) <= 100) {
                                                    sendData(mMotorControl2.pushGoods(mContext, 1, rimZNum1++, Integer.parseInt(arr[1])));
                                                } else {
                                                    sendData(mMotorControl2.pushGoods(mContext, 2, rimZNum2++, Integer.parseInt(arr[1])));
                                                }
                                                currentAisleID = Integer.parseInt(arr[1]);
                                                break;
                                            case "aisleTestActivity":
                                                currentAisleCode = currentCommandCode;
                                                if (Integer.parseInt(arr[1]) >= 1 && Integer.parseInt(arr[1]) <= 100) {
                                                    sendData(mMotorControl2.pushGoods(mContext, 1, rimZNum1++, Integer.parseInt(arr[1])));
                                                } else {
                                                    sendData(mMotorControl2.pushGoods(mContext, 2, rimZNum2++, Integer.parseInt(arr[1])));
                                                }
                                                currentAisleID = Integer.parseInt(arr[1]);
                                                break;
                                            case "test_by_row":
                                                currentAisleCode = currentCommandCode;
                                                currentAisleID = (Integer.parseInt(arr[1]) - 1) * 10 + Integer.parseInt(arr[2]);
                                                current_row_no = Integer.parseInt(arr[2]);
                                                row = arr[1];
                                                if (DBManager.getAisleInfo(mContext, ((currentAisleID >= 1 && currentAisleID <= 100) ? 1 : 2), currentAisleID).getState() != 1) {
                                                    if (Integer.parseInt(row) >= 1 && Integer.parseInt(row) <= 6) {
                                                        sendData(mMotorControl2.pushGoods(mContext, 1, rimZNum1++, currentAisleID));
                                                    } else {
                                                        sendData(mMotorControl2.pushGoods(mContext, 2, rimZNum2++, currentAisleID));
                                                    }
                                                } else {
                                                    isCommand = true;
                                                    queue.add("test_by_row" + "," + row + "," + String.valueOf(current_row_no + 1));
                                                }
                                                break;
                                            case "test_by_column":
                                                currentAisleCode = currentCommandCode;
                                                currentAisleID = (Integer.parseInt(arr[2]) - 1) * 10 + Integer.parseInt(arr[1]);
                                                current_column_no = Integer.parseInt(arr[2]);
                                                column = arr[1];
                                                if (DBManager.getAisleInfo(mContext, ((currentAisleID >= 1 && currentAisleID <= 100) ? 1 : 2), currentAisleID).getState() != 1) {
                                                    if (currentAisleID >= 1 && currentAisleID <= 100) {
                                                        sendData(mMotorControl2.pushGoods(mContext, 1, rimZNum1++, currentAisleID));
                                                    } else {
                                                        sendData(mMotorControl2.pushGoods(mContext, 2, rimZNum2++, currentAisleID));
                                                    }
                                                } else {
                                                    isCommand = true;
                                                    queue.add("test_by_column" + "," + column + "," + String.valueOf(current_column_no + 1));
                                                }
                                                break;
                                            case "test_all_aisle":
                                                currentAisleCode = currentCommandCode;
                                                currentAisleID = dataList.get(test_all_no);
                                                if (currentAisleID >= 1 && currentAisleID <= 100) {
                                                    sendData(mMotorControl2.pushGoods(mContext, 1, rimZNum1++, currentAisleID));
                                                } else {
                                                    sendData(mMotorControl2.pushGoods(mContext, 2, rimZNum2++, currentAisleID));
                                                }
                                                break;
                                        }
                                        isQuery = false;
                                        send = isCommand;
                                    }
                                }
                            }else {
                                isReTransmission = false;
                                if(isQuery){
                                    timeout = timeout + 1;
                                    Log.w("happy", "query-timeout:"+String.valueOf(timeout));
                                    if (timeout>=8) {
                                        timeout = 0;
                                        Log.w("happy", "query-failCount:"+String.valueOf(failCount));
                                        failCount++;
                                        if(failCount >= 10){
                                            failCount = 0;
                                        }else{
                                            queryQueue.add(currentCommandCode);
                                        }
                                        send = true;
                                    }
                                }else{
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
                                            isReTransmission = true;
                                            reTransmission = currentCommandCode;
                                        }
                                        send = true;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            timer.schedule(timerTask,0,50);
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
                if(!stopThreadStatus){
                    int size; //读取数据的大小
                    buffer = serialPort.receiveData();
                    if(buffer != null){
                        StringBuilder str1 = new StringBuilder();
                        for (byte aRec : buffer) {
                            str1.append(Integer.toHexString(aRec & 0xFF)).append(" ");
                        }
                        Log.w("happy","接收数据：" + str1);
                        size = buffer.length;
                        if (size >= 8){
                            if(handleRec(buffer).equals("queryState")){
                                failCount = 0;
                                send = true;
                                onDataReceiveListener.onDataReceive("queryState",buffer,size);
                            }else if(handleRec(buffer).equals("counterCommand")){
                                failCount = 0;
                                queue.clear();
                                send = true;
                            }else if(handleRec(buffer).equals("centerCommand")){
                                failCount = 0;
                                queue.clear();
                                send = true;
                            }else if(handleRec(buffer).equals("query_Y_left")){
                                failCount = 0;
                                if(!moveFloorByMapan){
                                    send = true;
                                    onDataReceiveListener.onDataReceive("query_Y_left",buffer,size);
                                }else {
                                    send = true;
                                    moveFloorByMapan = false;
                                }
                            }else if(handleRec(buffer).equals("query_Y_right")){
                                failCount = 0;
                                if(!moveFloorByMapan){
                                    send = true;
                                    onDataReceiveListener.onDataReceive("query_Y_right",buffer,size);
                                }else {
                                    send = true;
                                    moveFloorByMapan = false;
                                }
                            }else if(handleRec(buffer).equals("out_door_query")){
                                if(isReTransmission){
                                    if(queue.contains(reTransmission)){
                                        queue.remove(reTransmission);
                                    }
                                }
                                failCount = 0;
                                send = true;
                            }else if(handleRec(buffer).equals("out_door_query_Done")){
                                failCount = 0;
                                send = true;
                                queryQueue.clear();
                                onDataReceiveListener.onDataReceive("out_door_query_Done",buffer,size);
                            }else if(handleRec(buffer).equals("x_query")){
                                if(isReTransmission){
                                    if(queue.contains(reTransmission)){
                                        queue.remove(reTransmission);
                                    }
                                }
                                failCount = 0;
                                send = true;
                            }else if(handleRec(buffer).equals("x_query_Done")){
                                failCount = 0;
                                send = true;
                                queryQueue.clear();
                                onDataReceiveListener.onDataReceive("x_query_Done",buffer,size);
                            }else if(handleRec(buffer).equals("open_get_door_query")){
                                if(isReTransmission){
                                    if(queue.contains(reTransmission)){
                                        queue.remove(reTransmission);
                                    }
                                }
                                failCount = 0;
                                send = true;
                            }else if(handleRec(buffer).equals("open_get_door_query_Done")){
                                failCount = 0;
                                send = true;
                                queryQueue.clear();
                                onDataReceiveListener.onDataReceive("open_get_door_query_Done",buffer,size);
                            }else if(handleRec(buffer).equals("close_get_door_query")){
                                if(isReTransmission){
                                    if(queue.contains(reTransmission)){
                                        queue.remove(reTransmission);
                                    }
                                }
                                failCount = 0;
                                send = true;
                            }else if(handleRec(buffer).equals("close_get_door_query_Done")){
                                failCount = 0;
                                send = true;
                                queryQueue.clear();
                                onDataReceiveListener.onDataReceive("close_get_door_query_Done",buffer,size);
                            }else if(handleRec(buffer).equals("aisle_query")){
                                if(isReTransmission){
                                    if(queue.contains(reTransmission)){
                                        queue.remove(reTransmission);
                                    }
                                }
                                String arr[] = currentAisleCode.split(",");
                                String commandCode = arr[0];
                                switch (commandCode) {
                                    case "test_by_row":
                                    case "test_by_column":
                                    case "test_all_aisle":
                                        queue.clear();
                                        break;
                                    default:
                                        break;
                                }
                                failCount = 0;
                                send = true;
                            }else if(handleRec(buffer).equals("aisle_test_query_Done")){
                                queryQueue.clear();
                                failCount = 0;
                                onDataReceiveListener.onDataReceive("aisle_test_query_Done",buffer,size);
                                send = true;
                            }else if(handleRec(buffer).equals("aisleTestActivity_query_Done")){
                                queryQueue.clear();
                                failCount = 0;
                                onDataReceiveListener.onDataReceive(String.valueOf(currentAisleID),buffer,size);
                                send = true;
                            }else if(handleRec(buffer).equals("test_by_row_query_Done")){
                                queryQueue.clear();
                                failCount = 0;
                                onDataReceiveListener.onDataReceive(String.valueOf(currentAisleID),buffer,size);
                                send = true;
                            }else if(handleRec(buffer).equals("test_by_column_query_Done")){
                                queryQueue.clear();
                                failCount = 0;
                                onDataReceiveListener.onDataReceive(String.valueOf(currentAisleID),buffer,size);
                                send = true;
                            }else if(handleRec(buffer).equals("test_all_aisle_query_Done")){
                                queryQueue.clear();
                                failCount = 0;
                                onDataReceiveListener.onDataReceive(String.valueOf(currentAisleID),buffer,size);
                                send = true;
                            }else if(handleRec(buffer).equals("query_immediately")){
                                failCount = 0;
                                send = true;
                                onDataReceiveListener.onDataReceive("query_immediately",buffer,size);
                            }
                        }
                    }
                }
            }
        }
    }

    private String handleRec(byte[] rec) {
        if ((rec[0] == (byte) 0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte) 0x0F && rec[rec.length - 2] == (byte) 0xF1 && rec[6] == (byte) 0x65 && rec[3] == (byte) 0xC0)
                || (rec[0] == (byte) 0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte) 0x0F && rec[rec.length - 2] == (byte) 0xF1 && rec[6] == (byte) 0x65 && rec[3] == (byte) 0xC1)
                || (rec[0] == (byte) 0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte) 0x0F && rec[rec.length - 2] == (byte) 0xF1 && rec[6] == (byte) 0x6D && rec[3] == (byte) 0xE0)){//查询状态的反馈
                return "queryState";
        }
//        else if(rec[0] == (byte) 0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte) 0x0F && rec[rec.length - 2] == (byte) 0xF1 && rec[6] == (byte) 0x31){//边柜命令指令的应答
//            return "counterCommand";
//        }else if(rec[0] == (byte) 0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte) 0x0F && rec[rec.length - 2] == (byte) 0xF1 && rec[6] == (byte) 0x35){//中柜命令指令的应答
//            return "centerCommand";
//        }
        else if(rec[0] == (byte) 0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte) 0x0F && rec[rec.length - 2] == (byte) 0xF1 && rec[6] == (byte) 0x79 && rec[7] == (byte) 0x59 && rec[3] == (byte)0xC0){//查询Y的反馈
            if(!currentCommandCode.equals("query_immediately_Y_left")){
                return "query_Y_left";
            }else{
                return "query_immediately";
            }
        }else if(rec[0] == (byte) 0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte) 0x0F && rec[rec.length - 2] == (byte) 0xF1 && rec[6] == (byte) 0x79 && rec[7] == (byte) 0x59 && rec[3] == (byte)0xC1){//查询Y的反馈
            if(!currentCommandCode.equals("query_immediately_Y_right")){
                return "query_Y_right";
            }else{
                return "query_immediately";
            }
        }else if(rec[0] == (byte)0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte)0x0F && rec[rec.length-2] == (byte)0xF1 && rec[6] == (byte)0x6F && rec[7] == (byte)0x5A && (rec[16] == (byte)0x00 || rec[16] == (byte)0x01 || rec[16] == (byte)0x03)){//开出货门反馈
            if(currentCommandCode.equals("left_open_out_door") || currentCommandCode.equals("right_open_out_door")){
                if(rec[3] == (byte)0xC0){
                    queryQueue.add("open_out_door_query_left");
                }else{
                    queryQueue.add("open_out_door_query_right");
                }
                return "out_door_query";
            }
        }else if(rec[0] == (byte)0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte)0x0F && rec[rec.length-2] == (byte)0xF1 && rec[6] == (byte)0x6F && rec[7] == (byte)0x5A && rec[16] == (byte)0x02){//开出货门查询反馈
            if(currentCommandCode.equals("open_out_door_query_left") || currentCommandCode.equals("open_out_door_query_right")){
                return "out_door_query_Done";
            }else if(currentCommandCode.equals("query_immediately_outGoodsDoor_left") || currentCommandCode.equals("query_immediately_outGoodsDoor_right")){
                return "query_immediately";
            }
        }else if(rec[0] == (byte)0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte)0x0F && rec[rec.length-2] == (byte)0xF1 && rec[6] == (byte)0x63 && rec[7] == (byte)0x5A && (rec[16] == (byte)0x00 || rec[16] == (byte)0x01 || rec[16] == (byte)0x03)){//关出货门反馈
            if(currentCommandCode.equals("left_close_out_door") || currentCommandCode.equals("right_close_out_door")){
                if(rec[3] == (byte)0xC0){
                    queryQueue.add("close_out_door_query_left");
                }else{
                    queryQueue.add("close_out_door_query_right");
                }
                return "out_door_query";
            }
        }else if(rec[0] == (byte)0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte)0x0F && rec[rec.length-2] == (byte)0xF1 && rec[6] == (byte)0x63 && rec[7] == (byte)0x5A && rec[16] == (byte)0x02){//关出货门查询反馈
            if(currentCommandCode.equals("close_out_door_query_left") || currentCommandCode.equals("close_out_door_query_right")){
                return "out_door_query_Done";
            }else if(currentCommandCode.equals("query_immediately_outGoodsDoor_left") || currentCommandCode.equals("query_immediately_outGoodsDoor_right")){
                return "query_immediately";
            }
        }else if(rec[0] == (byte)0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte)0x0F && rec[rec.length-2] == (byte)0xF1 && rec[6] == (byte)0x78 && rec[7] == (byte)0x58 && (rec[16] == (byte)0x00 || rec[16] == (byte)0x01 || rec[16] == (byte)0x03)){//X轴反馈
            if(currentCommandCode.equals("left_x_motor_zheng") || currentCommandCode.equals("left_x_motor_fan") ||
                    currentCommandCode.equals("right_x_motor_zheng") || currentCommandCode.equals("right_x_motor_fan")){
                if(rec[3] == (byte)0xC0){
                    queryQueue.add("x_query_left");
                }else{
                    queryQueue.add("x_query_right");
                }
                return "x_query";
            }
        }else if(rec[0] == (byte)0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte)0x0F && rec[rec.length-2] == (byte)0xF1 && rec[6] == (byte)0x78 && rec[7] == (byte)0x58 && rec[16] == (byte)0x02){//X轴查询反馈
            if(currentCommandCode.equals("x_query_left") || currentCommandCode.equals("x_query_right")){
                return "x_query_Done";
            }else if(currentCommandCode.equals("query_immediately_X_left") || currentCommandCode.equals("query_immediately_X_right")){
                return "query_immediately";
            }
        }else if(rec[0] == (byte)0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte)0x0F && rec[rec.length-2] == (byte)0xF1 && rec[6] == (byte)0x64 && rec[3] == (byte)0xE0 && rec[7] == (byte)0x4D && (rec[16] == (byte)0x00 || rec[16] == (byte)0x01 || rec[16] == (byte)0x03)){//开取货门反馈
            if(currentCommandCode.equals("open_get_door")){
                queryQueue.add("open_get_door_query");
                return "open_get_door_query";
            }
        }else if(rec[0] == (byte)0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte)0x0F && rec[rec.length-2] == (byte)0xF1 && rec[6] == (byte)0x64 && rec[3] == (byte)0xE0 && rec[7] == (byte)0x4D && rec[16] == (byte)0x02){//开取货门查询反馈
            if(currentCommandCode.equals("open_get_door_query")){
                return "open_get_door_query_Done";
            }else if(currentCommandCode.equals("query_immediately_getGoodsDoor")){
                return "query_immediately";
            }
        }else if(rec[0] == (byte)0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte)0x0F && rec[rec.length-2] == (byte)0xF1 && rec[6] == (byte)0x75 && rec[3] == (byte)0xE0 && rec[7] == (byte)0x4D && (rec[16] == (byte)0x00 || rec[16] == (byte)0x01 || rec[16] == (byte)0x03)){//关取货门反馈
            if(currentCommandCode.equals("close_get_door")){
                queryQueue.add("close_get_door_query");
                return "close_get_door_query";
            }
        }else if(rec[0] == (byte)0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte)0x0F && rec[rec.length-2] == (byte)0xF1 && rec[6] == (byte)0x75 && rec[3] == (byte)0xE0 && rec[7] == (byte)0x4D && rec[16] == (byte)0x02){//关取货门查询反馈
            if(currentCommandCode.equals("close_get_door_query")){
                return "close_get_door_query_Done";
            }else if(currentCommandCode.equals("query_immediately_getGoodsDoor")){
                return "query_immediately";
            }
        }else if(rec[0] == (byte)0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte)0x0F && rec[rec.length-2] == (byte)0xF1 && rec[6] == (byte)0x70 && rec[7] == (byte)0x50 && (rec[16] == (byte)0x00 || rec[16] == (byte)0x01 || rec[16] == (byte)0x03)){//货道测试反馈
            String arr[] = currentCommandCode.split(",");
            String commandCode = arr[0];
            if(commandCode.equals("aisle_test") || commandCode.equals("aisleTestActivity") || commandCode.equals("test_by_row")||
                    commandCode.equals("test_by_column") || commandCode.equals("test_all_aisle")){
                queryQueue.add("aisle_query");
                return "aisle_query";
            }
        }else if(rec[0] == (byte)0xE2 && rec[1] == rec.length && rec[2] == 0x00 && rec[4] == (byte)0x0F && rec[rec.length-2] == (byte)0xF1 && rec[6] == (byte)0x70 && rec[7] == (byte)0x50 && rec[16] == (byte)0x02){//货道测试查询反馈
            if(currentCommandCode.equals("aisle_query")){
                String arr[] = currentAisleCode.split(",");
                String commandCode = arr[0];
                switch (commandCode) {
                    case "aisle_test":
                        return "aisle_test_query_Done";
                    case "aisleTestActivity":
                        return "aisleTestActivity_query_Done";
                    case "test_by_row":
                        if (current_row_no >= 10) {
                            return "test_by_row_query_Done";
                        } else {
                            queue.add("test_by_row" + "," + row + "," + String.valueOf(current_row_no + 1));
                            return "test_by_row_query_Done";
                        }
                    case "test_by_column":
                        if (current_column_no >= 16) {
                            return "test_by_column_query_Done";
                        } else if ((current_column_no >= 1 && current_column_no <= 5) || (current_column_no >= 11 && current_column_no <= 15)) {
                            queue.add("test_by_column" + "," + column + "," + String.valueOf(current_column_no + 1));
                            return "test_by_column_query_Done";
                        } else {
                            queue.add("test_by_column" + "," + column + "," + "11");
                            return "test_by_column_query_Done";
                        }
                    case "test_all_aisle":
                        test_all_no++;
                        if ((test_all_no + 1) > dataList.size()) {
                            return "test_all_aisle_query_Done";
                        } else {
                            queue.add("test_all_aisle");
                            return "test_all_aisle_query_Done";
                        }
                }
            }else if(currentCommandCode.equals("query_immediately_aisle_left") || currentCommandCode.equals("query_immediately_aisle_right")){
                return "query_immediately";
            }
        }
        return "";
    }

    private OnDataReceiveListener onDataReceiveListener = null;
    public interface OnDataReceiveListener {
        void onDataReceive(String code, byte[] rec, int size);
    }
    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }

}
