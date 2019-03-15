package com.njust.wanyuan_display.greenDao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TRANSACTION".
*/
public class TransactionDao extends AbstractDao<Transaction, Long> {

    public static final String TABLENAME = "TRANSACTION";

    /**
     * Properties of entity Transaction.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property SysorderNo = new Property(1, String.class, "sysorderNo", false, "SYSORDER_NO");
        public final static Property PositionIDs = new Property(2, String.class, "positionIDs", false, "POSITION_IDS");
        public final static Property BeginTime = new Property(3, String.class, "beginTime", false, "BEGIN_TIME");
        public final static Property EndTime = new Property(4, String.class, "endTime", false, "END_TIME");
        public final static Property Complete = new Property(5, String.class, "complete", false, "COMPLETE");
        public final static Property Error = new Property(6, String.class, "error", false, "ERROR");
        public final static Property PayType = new Property(7, String.class, "payType", false, "PAY_TYPE");
    }


    public TransactionDao(DaoConfig config) {
        super(config);
    }
    
    public TransactionDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TRANSACTION\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"SYSORDER_NO\" TEXT," + // 1: sysorderNo
                "\"POSITION_IDS\" TEXT," + // 2: positionIDs
                "\"BEGIN_TIME\" TEXT," + // 3: beginTime
                "\"END_TIME\" TEXT," + // 4: endTime
                "\"COMPLETE\" TEXT," + // 5: complete
                "\"ERROR\" TEXT," + // 6: error
                "\"PAY_TYPE\" TEXT);"); // 7: payType
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TRANSACTION\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Transaction entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String sysorderNo = entity.getSysorderNo();
        if (sysorderNo != null) {
            stmt.bindString(2, sysorderNo);
        }
 
        String positionIDs = entity.getPositionIDs();
        if (positionIDs != null) {
            stmt.bindString(3, positionIDs);
        }
 
        String beginTime = entity.getBeginTime();
        if (beginTime != null) {
            stmt.bindString(4, beginTime);
        }
 
        String endTime = entity.getEndTime();
        if (endTime != null) {
            stmt.bindString(5, endTime);
        }
 
        String complete = entity.getComplete();
        if (complete != null) {
            stmt.bindString(6, complete);
        }
 
        String error = entity.getError();
        if (error != null) {
            stmt.bindString(7, error);
        }
 
        String payType = entity.getPayType();
        if (payType != null) {
            stmt.bindString(8, payType);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Transaction entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String sysorderNo = entity.getSysorderNo();
        if (sysorderNo != null) {
            stmt.bindString(2, sysorderNo);
        }
 
        String positionIDs = entity.getPositionIDs();
        if (positionIDs != null) {
            stmt.bindString(3, positionIDs);
        }
 
        String beginTime = entity.getBeginTime();
        if (beginTime != null) {
            stmt.bindString(4, beginTime);
        }
 
        String endTime = entity.getEndTime();
        if (endTime != null) {
            stmt.bindString(5, endTime);
        }
 
        String complete = entity.getComplete();
        if (complete != null) {
            stmt.bindString(6, complete);
        }
 
        String error = entity.getError();
        if (error != null) {
            stmt.bindString(7, error);
        }
 
        String payType = entity.getPayType();
        if (payType != null) {
            stmt.bindString(8, payType);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Transaction readEntity(Cursor cursor, int offset) {
        Transaction entity = new Transaction( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // sysorderNo
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // positionIDs
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // beginTime
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // endTime
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // complete
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // error
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // payType
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Transaction entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSysorderNo(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPositionIDs(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setBeginTime(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setEndTime(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setComplete(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setError(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setPayType(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Transaction entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Transaction entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Transaction entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}