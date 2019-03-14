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
 * DAO for table "AISLE_MERGE".
*/
public class AisleMergeDao extends AbstractDao<AisleMerge, Long> {

    public static final String TABLENAME = "AISLE_MERGE";

    /**
     * Properties of entity AisleMerge.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property PositionID1 = new Property(1, int.class, "positionID1", false, "POSITION_ID1");
        public final static Property PositionID2 = new Property(2, int.class, "positionID2", false, "POSITION_ID2");
    }


    public AisleMergeDao(DaoConfig config) {
        super(config);
    }
    
    public AisleMergeDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"AISLE_MERGE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"POSITION_ID1\" INTEGER NOT NULL ," + // 1: positionID1
                "\"POSITION_ID2\" INTEGER NOT NULL );"); // 2: positionID2
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"AISLE_MERGE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, AisleMerge entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getPositionID1());
        stmt.bindLong(3, entity.getPositionID2());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, AisleMerge entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getPositionID1());
        stmt.bindLong(3, entity.getPositionID2());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public AisleMerge readEntity(Cursor cursor, int offset) {
        AisleMerge entity = new AisleMerge( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // positionID1
            cursor.getInt(offset + 2) // positionID2
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, AisleMerge entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPositionID1(cursor.getInt(offset + 1));
        entity.setPositionID2(cursor.getInt(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(AisleMerge entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(AisleMerge entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(AisleMerge entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
