package com.njust.wanyuan_display.greenDao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by my187 on 2018/11/20.
 */

/**
 * 货道合并信息
 */
@Entity
public class AisleMerge {
    @Id
    private Long id;

    private int positionID1;//相邻的货道号较小的那个，货道号1-200
    private int positionID2;//相邻的货道号较大的那个，货道号1-200
    @Generated(hash = 1683359647)
    public AisleMerge(Long id, int positionID1, int positionID2) {
        this.id = id;
        this.positionID1 = positionID1;
        this.positionID2 = positionID2;
    }
    @Generated(hash = 1274059515)
    public AisleMerge() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getPositionID1() {
        return this.positionID1;
    }
    public void setPositionID1(int positionID1) {
        this.positionID1 = positionID1;
    }
    public int getPositionID2() {
        return this.positionID2;
    }
    public void setPositionID2(int positionID2) {
        this.positionID2 = positionID2;
    }

}
