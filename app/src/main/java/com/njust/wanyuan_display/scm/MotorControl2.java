package com.njust.wanyuan_display.scm;

import android.content.Context;

import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.greenDao.AisleInfo;
import com.njust.wanyuan_display.manager.BindingManager;


public class MotorControl2 {
    private byte[] MotorControlTXBuf;
    
    /**
     * 垂直移层_货道指令
     * 说明：边柜板驱动Y电机升降云台到指定层位
     * @param counter 货柜号 1=左柜（主柜），2 =右柜（辅柜）
     * @param zhenNumber 本帧编号
     * @param positionID 货物位置的ID号，可以查出对应的实际地址，使用其中的层号信息
     * */
    public byte[] moveFloor(Context mContext, int counter , int zhenNumber , int positionID) {
        MotorControlTXBuf = new byte[10];
        MotorControlTXBuf[0] = (byte) 0xE2;
        MotorControlTXBuf[1] = (byte) 0x0A;
        MotorControlTXBuf[2] = (byte) (0xC0+(counter-1));
        MotorControlTXBuf[3] = (byte) 0x00;
        MotorControlTXBuf[4] = (byte) 0x01;
        MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
        MotorControlTXBuf[6] = (byte) ((DBManager.getFlootPosition(mContext,counter,positionID) >> 8) & 0xFF);
        MotorControlTXBuf[7] = (byte) (DBManager.getFlootPosition(mContext,counter,positionID) & 0xFF);
        MotorControlTXBuf[8] = (byte) 0xF1;
        int sum = 0;
        for (int i = 0; i <9; i++) {
            sum = sum + MotorControlTXBuf[i];
        }
        MotorControlTXBuf[9] = (byte)(sum & 0xFF);
        return MotorControlTXBuf;
    }

    /**
     * 垂直移层_移到指定格位
     * 说明：用于测试指令
     * @param counter 货柜号 1=左柜（主柜），2 =右柜（辅柜）
     * @param zhenNumber 本帧编号
     * @param position 码盘格位
     * */
    public byte[] moveFloorByMapan(int counter ,int zhenNumber ,int position) {
        MotorControlTXBuf = new byte[10];
        MotorControlTXBuf[0] = (byte) 0xE2;
        MotorControlTXBuf[1] = (byte) 0x0A;
        MotorControlTXBuf[2] = (byte) (0xC0+(counter-1));
        MotorControlTXBuf[3] = (byte) 0x00;
        MotorControlTXBuf[4] = (byte) 0x01;
        MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
        if(counter == 1){
            MotorControlTXBuf[6] = (byte) ((position >> 8) & 0xFF);
            MotorControlTXBuf[7] = (byte) (position & 0xFF);
        }else{
            MotorControlTXBuf[6] = (byte) ((position >> 8) & 0xFF);
            MotorControlTXBuf[7] = (byte) (position & 0xFF);
        }
        MotorControlTXBuf[8] = (byte) 0xF1;
        int sum = 0;
        for (int i = 0; i <9; i++) {
            sum = sum + MotorControlTXBuf[i];
        }
        MotorControlTXBuf[9] = (byte)(sum & 0xFF);
        return MotorControlTXBuf;
    }


    /**
     * 垂直移层_出货口指令
     * 说明：边柜板驱动Y电机升降云台到出货口
     * @param counter 货柜号 1=左柜（主柜），2 =右柜（辅柜）
     * @param zhenNumber 本帧编号
     * */
    public byte[] moveFloorOut(Context mContext, int counter , int zhenNumber) {
        MotorControlTXBuf = new byte[10];
        MotorControlTXBuf[0] = (byte) 0xE2;
        MotorControlTXBuf[1] = (byte) 0x0A;
        MotorControlTXBuf[2] = (byte) (0xC0+(counter-1));
        MotorControlTXBuf[3] = (byte) 0x00;
        MotorControlTXBuf[4] = (byte) 0x01;
        MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
        MotorControlTXBuf[6] = (byte) ((DBManager.getOutPosition(mContext,counter)>> 8) & 0xFF);
        MotorControlTXBuf[7] = (byte) (DBManager.getOutPosition(mContext,counter) & 0xFF);
        MotorControlTXBuf[8] = (byte) 0xF1;
        int sum = 0;
        for (int i = 0; i <9; i++) {
            sum = sum + MotorControlTXBuf[i];
        }
        MotorControlTXBuf[9] = (byte)(sum & 0xFF);
        return MotorControlTXBuf;
    }

    /**
     * 垂直移层_出货口（校正）指令
     * 说明：边柜板驱动Y电机升降云台到出货口下方一定的距离位置，用于折返调整Y轴移动误差
     * @param counter 货柜号 1=左柜（主柜），2 =右柜（辅柜）
     * @param zhenNumber 本帧编号
     * @param encoder 出货口下方一定的距离位置，代表码盘数量
     * */
    public byte[] moveFloorOutRevise(Context mContext, int counter , int zhenNumber, int encoder) {
        MotorControlTXBuf = new byte[10];
        MotorControlTXBuf[0] = (byte) 0xE2;
        MotorControlTXBuf[1] = (byte) 0x0A;
        MotorControlTXBuf[2] = (byte) (0xC0+(counter-1));
        MotorControlTXBuf[3] = (byte) 0x00;
        MotorControlTXBuf[4] = (byte) 0x01;
        MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
        MotorControlTXBuf[6] = (byte) (((DBManager.getOutPosition(mContext,counter) - encoder) >> 8) & 0xFF);
        MotorControlTXBuf[7] = (byte) ((DBManager.getOutPosition(mContext,counter) - encoder) & 0xFF);
        MotorControlTXBuf[8] = (byte) 0xF1;
        int sum = 0;
        for (int i = 0; i <9; i++) {
            sum = sum + MotorControlTXBuf[i];
        }
        MotorControlTXBuf[9] = (byte)(sum & 0xFF);
        return MotorControlTXBuf;
    }


    /**
     * 水平移位_错位指令
     * 说明：边柜板驱动X电机左右移动云台履带
     * @param counter 货柜号 1=左柜（主柜），2 =右柜（辅柜）
     * @param zhenNumber 本帧编号
     * @param orientation 边柜X轴电机移动方向 0=原位，1=正转，2=反转
     * @param moveTime 边柜X轴电机移动时间 单位：毫秒，orientation = 0时无意义
     * */
    public byte[] moveHorizontal(int counter ,int zhenNumber ,int orientation ,int moveTime) {
        MotorControlTXBuf = new byte[11];
        MotorControlTXBuf[0] = (byte) 0xE2;
        MotorControlTXBuf[1] = (byte) 0x0B;
        MotorControlTXBuf[2] = (byte) (0xC0+(counter-1));
        MotorControlTXBuf[3] = (byte) 0x00;
        MotorControlTXBuf[4] = (byte) 0x02;
        MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
        MotorControlTXBuf[6] = (byte) orientation;
        MotorControlTXBuf[7] = (byte) ((moveTime >> 8) & 0xFF);
        MotorControlTXBuf[8] = (byte) (moveTime & 0xFF);
        MotorControlTXBuf[9] = (byte) 0xF1;
        int sum = 0;
        for (int i = 0; i <10; i++) {
            sum = sum + MotorControlTXBuf[i];
        }
        MotorControlTXBuf[10] = (byte)(sum & 0xFF);
        return MotorControlTXBuf;
    }

    /**
     * 水平移位_推入出货口指令
     * 说明：边柜板驱动X电机左右移动云台履带
     * @param counter 货柜号 1=左柜（主柜），2 =右柜（辅柜）
     * @param zhenNumber 本帧编号
     * */
    public byte[] moveHorizontalOut(int counter ,int zhenNumber, int orientation ,int moveTime) {
        MotorControlTXBuf = new byte[11];
        MotorControlTXBuf[0] = (byte) 0xE2;
        MotorControlTXBuf[1] = (byte) 0x0B;
        MotorControlTXBuf[2] = (byte) (0xC0+(counter-1));
        MotorControlTXBuf[3] = (byte) 0x00;
        MotorControlTXBuf[4] = (byte) 0x02;
        MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
        MotorControlTXBuf[6] = (byte) orientation;
        MotorControlTXBuf[7] = (byte) ((moveTime >> 8) & 0xFF);
        MotorControlTXBuf[8] = (byte) (moveTime & 0xFF);
        MotorControlTXBuf[9] = (byte) 0xF1;
        int sum = 0;
        for (int i = 0; i <10; i++) {
            sum = sum + MotorControlTXBuf[i];
        }
        MotorControlTXBuf[10] = (byte)(sum & 0xFF);
        return MotorControlTXBuf;
    }


    /**
     * 推送货指令
     * 说明：货道板驱动P电机推送指定货道货物掉落云台
     * @param counter 货柜号 1=左柜（主柜），2 =右柜（辅柜）
     * @param zhenNumber 本帧编号
     * @param positionID 货物位置的ID号
     * */
    public byte[] pushGoods(Context mContext, int counter , int zhenNumber , int positionID ) {
        MotorControlTXBuf = new byte[12];
        AisleInfo aisleInfo = DBManager.getAisleInfo(mContext,counter,positionID);
        MotorControlTXBuf[0] = (byte) 0xE2;
        MotorControlTXBuf[1] = (byte) 0x0C;
        MotorControlTXBuf[2] = (byte) (0x80+(counter-1));
        MotorControlTXBuf[3] = (byte) 0x00;
        MotorControlTXBuf[4] = (byte) 0x03;
        MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
        MotorControlTXBuf[6] = (byte) ((aisleInfo.getPosition1() / 16)-1);
        MotorControlTXBuf[7] = (byte) ((aisleInfo.getPosition1() % 16)-1);
        MotorControlTXBuf[8] = (byte) ((aisleInfo.getPosition2() % 16)-1);
        MotorControlTXBuf[9] = (byte) aisleInfo.getMotorType();
        MotorControlTXBuf[10] = (byte) 0xF1;
        int sum = 0;
        for (int i = 0; i < 11; i++) {
            sum = sum + MotorControlTXBuf[i];
        }
        MotorControlTXBuf[11] = (byte)(sum & 0xFF);
        return MotorControlTXBuf;
    }


    /**
     * 开出货门指令
     * 说明：边柜板驱动Z电机开启出货门
     * @param counter 货柜号 1=左柜（主柜），2 =右柜（辅柜）
     * @param zhenNumber 本帧编号
     * */
    public byte[] openOutGoodsDoor(int counter ,int zhenNumber) {
        MotorControlTXBuf = new byte[9];
        MotorControlTXBuf[0] = (byte) 0xE2;
        MotorControlTXBuf[1] = (byte) 0x09;
        MotorControlTXBuf[2] = (byte) (0xC0+(counter-1));
        MotorControlTXBuf[3] = (byte) 0x00;
        MotorControlTXBuf[4] = (byte) 0x04;
        MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
        MotorControlTXBuf[6] = (byte) 0x00;
        MotorControlTXBuf[7] = (byte) 0xF1;
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            sum = sum + MotorControlTXBuf[i];
        }
        MotorControlTXBuf[8] = (byte)(sum & 0xFF);
        return MotorControlTXBuf;
    }

    /**
     * 关出货门指令
     * 说明：边柜板驱动Z电机关闭出货门
     * @param counter 货柜号 1=左柜（主柜），2 =右柜（辅柜）
     * @param zhenNumber 本帧编号
     * */
    public byte[] closeOutGoodsDoor(int counter ,int zhenNumber) {
        MotorControlTXBuf = new byte[9];
        MotorControlTXBuf[0] = (byte) 0xE2;
        MotorControlTXBuf[1] = (byte) 0x09;
        MotorControlTXBuf[2] = (byte) (0xC0+(counter-1));
        MotorControlTXBuf[3] = (byte) 0x00;
        MotorControlTXBuf[4] = (byte) 0x05;
        MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
        MotorControlTXBuf[6] = (byte) 0x00;
        MotorControlTXBuf[7] = (byte) 0xF1;
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            sum = sum + MotorControlTXBuf[i];
        }
        MotorControlTXBuf[8] = (byte)(sum & 0xFF);
        return MotorControlTXBuf;
    }

    /**
     * 云台归位指令
     * 说明：边柜板驱动Y电机降云台到零位
     * @param zhenNumber 本帧编号
     * */
    public byte[] homing(int counter ,int zhenNumber ) {
        MotorControlTXBuf = new byte[9];
        MotorControlTXBuf[0] = (byte) 0xE2;
        MotorControlTXBuf[1] = (byte) 0x09;
        MotorControlTXBuf[2] = (byte) (0xC0+(counter-1));
        MotorControlTXBuf[3] = (byte) 0x00;
        MotorControlTXBuf[4] = (byte) 0x06;
        MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
        MotorControlTXBuf[6] = (byte) 0x00;
        MotorControlTXBuf[7] = (byte) 0xF1;
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            sum = sum + MotorControlTXBuf[i];
        }
        MotorControlTXBuf[8] = (byte)(sum & 0xFF);
        return MotorControlTXBuf;
    }

    /**
     * 开取货门指令
     * 说明：中柜板驱动M电机开启取货门同时开启取货仓照明灯
     * @param zhenNumber 本帧编号
     * */
    public byte[] openGetGoodsDoor(int zhenNumber ) {
        MotorControlTXBuf = new byte[9];
        MotorControlTXBuf[0] = (byte) 0xE2;
        MotorControlTXBuf[1] = (byte) 0x09;
        MotorControlTXBuf[2] = (byte) 0xE0;
        MotorControlTXBuf[3] = (byte) 0x00;
        MotorControlTXBuf[4] = (byte) 0x07;
        MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
        MotorControlTXBuf[6] = (byte) 0x00;
        MotorControlTXBuf[7] = (byte) 0xF1;
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            sum = sum + MotorControlTXBuf[i];
        }
        MotorControlTXBuf[8] = (byte)(sum & 0xFF);
        return MotorControlTXBuf;
    }

    /**
     * 关取货门指令
     * 说明：中柜板驱动M电机关闭取货门同时关闭取货仓照明灯
     * @param zhenNumber 本帧编号
     * */
    public byte[] closeGetGoodsDoor(int zhenNumber ) {
        MotorControlTXBuf = new byte[9];
        MotorControlTXBuf[0] = (byte) 0xE2;
        MotorControlTXBuf[1] = (byte) 0x09;
        MotorControlTXBuf[2] = (byte) 0xE0;
        MotorControlTXBuf[3] = (byte) 0x00;
        MotorControlTXBuf[4] = (byte) 0x08;
        MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
        MotorControlTXBuf[6] = (byte) 0x00;
        MotorControlTXBuf[7] = (byte) 0xF1;
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            sum = sum + MotorControlTXBuf[i];
        }
        MotorControlTXBuf[8] = (byte)(sum & 0xFF);
        return MotorControlTXBuf;
    }

    /**
     * 开落货门指令
     * 说明：中柜板驱动F电机开启落货门
     * @param zhenNumber 本帧编号
     * */
    public byte[] openDropGoodsDoor(int zhenNumber ) {
        MotorControlTXBuf = new byte[9];
        MotorControlTXBuf[0] = (byte) 0xE2;
        MotorControlTXBuf[1] = (byte) 0x09;
        MotorControlTXBuf[2] = (byte) 0xE0;
        MotorControlTXBuf[3] = (byte) 0x00;
        MotorControlTXBuf[4] = (byte) 0x09;
        MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
        MotorControlTXBuf[6] = (byte) 0x00;
        MotorControlTXBuf[7] = (byte) 0xF1;
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            sum = sum + MotorControlTXBuf[i];
        }
        MotorControlTXBuf[8] = (byte)(sum & 0xFF);
        return MotorControlTXBuf;
    }

    /**
     * 关落货门指令
     * 说明：中柜板驱动F电机关闭落货门
     * @param zhenNumber 本帧编号
     * */
    public byte[] closeDropGoodsDoor(int zhenNumber ) {
        MotorControlTXBuf = new byte[9];
        MotorControlTXBuf[0] = (byte) 0xE2;
        MotorControlTXBuf[1] = (byte) 0x09;
        MotorControlTXBuf[2] = (byte) 0xE0;
        MotorControlTXBuf[3] = (byte) 0x00;
        MotorControlTXBuf[4] = (byte) 0x0A;
        MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
        MotorControlTXBuf[6] = (byte) 0x00;
        MotorControlTXBuf[7] = (byte) 0xF1;
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            sum = sum + MotorControlTXBuf[i];
        }
        MotorControlTXBuf[8] = (byte)(sum & 0xFF);
        return MotorControlTXBuf;
    }

    /**
     * 边柜命令指令
     * 说明：用于下发立即执行的命令
     * @param counter 货柜号 1=左柜（主柜），2 =右柜（辅柜）
     * @param zhenNumber 本帧编号
     * @param code 命令代码1=开启照明灯，2=关闭照明灯，3=开启门加热，4=停止门加热，5=Y轴电机全速上升，6=Y轴电机全速下降，7=Y轴电机慢速上升，8=Y轴电机慢速下降
     *             9=Y轴电机停转，10=X轴电机正转一周，11=X轴电机反转一周
     * */
    public byte[] counterCommand(int counter ,int zhenNumber, int code) {
        MotorControlTXBuf = new byte[9];
        MotorControlTXBuf[0] = (byte) 0xE2;
        MotorControlTXBuf[1] = (byte) 0x09;
        MotorControlTXBuf[2] = (byte) (0xC0+(counter-1));
        MotorControlTXBuf[3] = (byte) 0x00;
        MotorControlTXBuf[4] = (byte) 0x0B;
        MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
        MotorControlTXBuf[6] = (byte) code;
        MotorControlTXBuf[7] = (byte) 0xF1;
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            sum = sum + MotorControlTXBuf[i];
        }
        MotorControlTXBuf[8] = (byte)(sum & 0xFF);
        return MotorControlTXBuf;
    }

    /**
     * 中柜命令指令
     * 说明：用于下发立即执行的命令
     * @param zhenNumber 本帧编号
     * @param code 1=开启照明灯，2=关闭照明灯，3=开启电磁锁
     * */
    public byte[] centerCommand(int zhenNumber ,int code) {
        MotorControlTXBuf = new byte[9];
        MotorControlTXBuf[0] = (byte) 0xE2;
        MotorControlTXBuf[1] = (byte) 0x09;
        MotorControlTXBuf[2] = (byte) 0xE0;
        MotorControlTXBuf[3] = (byte) 0x00;
        MotorControlTXBuf[4] = (byte) 0x0B;
        MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
        MotorControlTXBuf[6] = (byte) code;
        MotorControlTXBuf[7] = (byte) 0xF1;
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum = sum + MotorControlTXBuf[i];
        }
        MotorControlTXBuf[8] = (byte)(sum & 0xFF);
        return MotorControlTXBuf;
    }


    /**
     * 货道电机测试指令
     * 说明：用于测试货道电机，第n行电机依次动作（有开关动作一周，无开关动作固定时间）
     * @param counter 货柜号 1 = 左柜（主柜），2 = 右柜（辅柜）
     * @param zhenNumber 本帧编号
     * @param code 1=指定行（Y）、列（X）电机动作（弹簧电机动作一周，履带电机动作固定时间，动作完毕应答，主机无确认）
     *              2=指定行（Y）电机依次动作动作（一动作一应答，主机无确认）
     *              3=指定列（X）电机依次动作动作（一动作一应答，主机无确认）
     *              4=所有电机依次动作一周，（一动作一应答，主机无确认）
     * @param floor 驱动矩阵行坐标（1～10）
     * @param column 驱动矩阵列坐标（1～10）
     * */
    public byte[] pushTestCommand(int counter ,int zhenNumber, int code, int floor, int column) {
        MotorControlTXBuf = new byte[12];
        MotorControlTXBuf[0] = (byte) 0xE2;
        MotorControlTXBuf[1] = (byte) 0x0C;
        MotorControlTXBuf[2] = (byte) (0x80+counter-1);
        MotorControlTXBuf[3] = (byte) 0x00;
        MotorControlTXBuf[4] = (byte) 0x0C;
        MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
        MotorControlTXBuf[6] = (byte) code;
        MotorControlTXBuf[7] = (byte) floor;
        MotorControlTXBuf[8] = (byte) column;
        MotorControlTXBuf[9] = (byte) 0;
        MotorControlTXBuf[10] = (byte) 0xF1;
        int sum = 0;
        for (int i = 0; i < 11; i++) {
            sum = sum + MotorControlTXBuf[i];
        }
        MotorControlTXBuf[11] = (byte)(sum & 0xFF);
        return MotorControlTXBuf;
    }


    /**
     * 边柜查询指令
     * 说明：设定边柜的温度，并查询压缩机、机柜、机柜顶部温度，压缩机、机柜直流风扇状态，门开关状态，湿度测量值，门加热状态，照明灯状态
     * @param counter 货柜号 1=左柜（主柜），2 =右柜（辅柜）
     * @param zhenNumber 本帧编号
     * @param temperatureMode 温控模式：0x00=制冷,0x01=制热,0x02=常温
     * @param setTemperature 设定温度，温控门限，-20～70
     * */
    public byte[] counterQuery(Context mContext,int counter ,int zhenNumber, int temperatureMode, int setTemperature) {
        if(new BindingManager().getTempUserDefined(mContext).equals("open")){
            MotorControlTXBuf = new byte[13];
            MotorControlTXBuf[0] = (byte) 0xE2;
            MotorControlTXBuf[1] = (byte) 0x0B;
            MotorControlTXBuf[2] = (byte) (0xC0+(counter-1));
            MotorControlTXBuf[3] = (byte) 0x00;
            MotorControlTXBuf[4] = (byte) 0x0D;
            MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
            MotorControlTXBuf[6] = (byte) temperatureMode;
            MotorControlTXBuf[7] = (byte) ((setTemperature >> 8) & 0xff);
            MotorControlTXBuf[8] = (byte) (setTemperature & 0xff);
            MotorControlTXBuf[9] = (byte) Integer.parseInt(new BindingManager().getTempControlHuiCha(mContext));
            MotorControlTXBuf[10] = (byte) Integer.parseInt(new BindingManager().getCompressorMaxWorktime(mContext));
            MotorControlTXBuf[11] = (byte) 0xF1;
            int sum = 0;
            for (int i = 0; i < 12; i++) {
                sum = sum + MotorControlTXBuf[i];
            }
            MotorControlTXBuf[12] = (byte)(sum & 0xFF);
            return MotorControlTXBuf;
        }else{
            MotorControlTXBuf = new byte[11];
            MotorControlTXBuf[0] = (byte) 0xE2;
            MotorControlTXBuf[1] = (byte) 0x0B;
            MotorControlTXBuf[2] = (byte) (0xC0+(counter-1));
            MotorControlTXBuf[3] = (byte) 0x00;
            MotorControlTXBuf[4] = (byte) 0x0D;
            MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
            MotorControlTXBuf[6] = (byte) temperatureMode;
            MotorControlTXBuf[7] = (byte) ((setTemperature >> 8) & 0xff);
            MotorControlTXBuf[8] = (byte) (setTemperature & 0xff);
            MotorControlTXBuf[9] = (byte) 0xF1;
            int sum = 0;
            for (int i = 0; i < 10; i++) {
                sum = sum + MotorControlTXBuf[i];
            }
            MotorControlTXBuf[10] = (byte)(sum & 0xFF);
            return MotorControlTXBuf;
        }
    }


    /**
     * 中柜查询指令
     * 说明：查询中柜门开关状态、照明灯状态、门锁状态、取货光栅状态、落货光栅状态、防夹手光栅状态、取货门开关状态、落货门开关状态
     * @param zhenNumber 本帧编号
     * */
    public byte[] centerQuery(int zhenNumber ) {
        MotorControlTXBuf = new byte[9];
        MotorControlTXBuf[0] = (byte) 0xE2;
        MotorControlTXBuf[1] = (byte) 0x09;
        MotorControlTXBuf[2] = (byte) 0xE0;
        MotorControlTXBuf[3] = (byte) 0x00;
        MotorControlTXBuf[4] = (byte) 0x0D;
        MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
        MotorControlTXBuf[6] = (byte) 0x00;
        MotorControlTXBuf[7] = (byte) 0xF1;
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            sum = sum + MotorControlTXBuf[i];
        }
        MotorControlTXBuf[8] = (byte)(sum & 0xFF);
        return MotorControlTXBuf;
    }

    /**
     * 查询指令
     * 说明：用于查询下位机指令执行状态
     * @param code 要查询状态的指令标识（0x01~0x0C）
     * @param address 查询指令发送的目的地址
     * @param zhenNumber 本帧编号
     * */
    public byte[] query(byte code ,byte address,int zhenNumber) {
        MotorControlTXBuf = new byte[9];
        MotorControlTXBuf[0] = (byte) 0xE2;
        MotorControlTXBuf[1] = (byte) 0x09;
        MotorControlTXBuf[2] = address;
        MotorControlTXBuf[3] = (byte) 0x00;
        MotorControlTXBuf[4] = (byte) 0x0E;
        MotorControlTXBuf[5] = (byte) (zhenNumber & 0xFF);
        MotorControlTXBuf[6] = (byte) code;
        MotorControlTXBuf[7] = (byte) 0xF1;
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            sum = sum + MotorControlTXBuf[i];
        }
        MotorControlTXBuf[8] = (byte)(sum & 0xFF);
        return MotorControlTXBuf;
    }
}


































