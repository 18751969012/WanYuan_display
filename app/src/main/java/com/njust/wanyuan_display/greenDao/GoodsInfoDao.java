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
 * DAO for table "GOODS_INFO".
*/
public class GoodsInfoDao extends AbstractDao<GoodsInfo, Long> {

    public static final String TABLENAME = "GOODS_INFO";

    /**
     * Properties of entity GoodsInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property GoodID = new Property(1, int.class, "goodID", false, "GOOD_ID");
        public final static Property GoodName = new Property(2, String.class, "goodName", false, "GOOD_NAME");
        public final static Property GoodTypeID = new Property(3, int.class, "goodTypeID", false, "GOOD_TYPE_ID");
        public final static Property GoodTypeName = new Property(4, String.class, "goodTypeName", false, "GOOD_TYPE_NAME");
        public final static Property GoodDescription = new Property(5, String.class, "goodDescription", false, "GOOD_DESCRIPTION");
        public final static Property GoodPrice = new Property(6, double.class, "goodPrice", false, "GOOD_PRICE");
        public final static Property Promotion = new Property(7, String.class, "promotion", false, "PROMOTION");
        public final static Property PromotionPrice = new Property(8, double.class, "promotionPrice", false, "PROMOTION_PRICE");
        public final static Property GoodsImgUrl = new Property(9, String.class, "goodsImgUrl", false, "GOODS_IMG_URL");
        public final static Property GoodsImgLocalUrl = new Property(10, String.class, "goodsImgLocalUrl", false, "GOODS_IMG_LOCAL_URL");
        public final static Property GoodColdHot = new Property(11, boolean.class, "goodColdHot", false, "GOOD_COLD_HOT");
        public final static Property GoodImgDownload = new Property(12, String.class, "goodImgDownload", false, "GOOD_IMG_DOWNLOAD");
        public final static Property UpdateTime = new Property(13, String.class, "updateTime", false, "UPDATE_TIME");
    }


    public GoodsInfoDao(DaoConfig config) {
        super(config);
    }
    
    public GoodsInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"GOODS_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"GOOD_ID\" INTEGER NOT NULL ," + // 1: goodID
                "\"GOOD_NAME\" TEXT," + // 2: goodName
                "\"GOOD_TYPE_ID\" INTEGER NOT NULL ," + // 3: goodTypeID
                "\"GOOD_TYPE_NAME\" TEXT," + // 4: goodTypeName
                "\"GOOD_DESCRIPTION\" TEXT," + // 5: goodDescription
                "\"GOOD_PRICE\" REAL NOT NULL ," + // 6: goodPrice
                "\"PROMOTION\" TEXT," + // 7: promotion
                "\"PROMOTION_PRICE\" REAL NOT NULL ," + // 8: promotionPrice
                "\"GOODS_IMG_URL\" TEXT," + // 9: goodsImgUrl
                "\"GOODS_IMG_LOCAL_URL\" TEXT," + // 10: goodsImgLocalUrl
                "\"GOOD_COLD_HOT\" INTEGER NOT NULL ," + // 11: goodColdHot
                "\"GOOD_IMG_DOWNLOAD\" TEXT," + // 12: goodImgDownload
                "\"UPDATE_TIME\" TEXT);"); // 13: updateTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"GOODS_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, GoodsInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getGoodID());
 
        String goodName = entity.getGoodName();
        if (goodName != null) {
            stmt.bindString(3, goodName);
        }
        stmt.bindLong(4, entity.getGoodTypeID());
 
        String goodTypeName = entity.getGoodTypeName();
        if (goodTypeName != null) {
            stmt.bindString(5, goodTypeName);
        }
 
        String goodDescription = entity.getGoodDescription();
        if (goodDescription != null) {
            stmt.bindString(6, goodDescription);
        }
        stmt.bindDouble(7, entity.getGoodPrice());
 
        String promotion = entity.getPromotion();
        if (promotion != null) {
            stmt.bindString(8, promotion);
        }
        stmt.bindDouble(9, entity.getPromotionPrice());
 
        String goodsImgUrl = entity.getGoodsImgUrl();
        if (goodsImgUrl != null) {
            stmt.bindString(10, goodsImgUrl);
        }
 
        String goodsImgLocalUrl = entity.getGoodsImgLocalUrl();
        if (goodsImgLocalUrl != null) {
            stmt.bindString(11, goodsImgLocalUrl);
        }
        stmt.bindLong(12, entity.getGoodColdHot() ? 1L: 0L);
 
        String goodImgDownload = entity.getGoodImgDownload();
        if (goodImgDownload != null) {
            stmt.bindString(13, goodImgDownload);
        }
 
        String updateTime = entity.getUpdateTime();
        if (updateTime != null) {
            stmt.bindString(14, updateTime);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, GoodsInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getGoodID());
 
        String goodName = entity.getGoodName();
        if (goodName != null) {
            stmt.bindString(3, goodName);
        }
        stmt.bindLong(4, entity.getGoodTypeID());
 
        String goodTypeName = entity.getGoodTypeName();
        if (goodTypeName != null) {
            stmt.bindString(5, goodTypeName);
        }
 
        String goodDescription = entity.getGoodDescription();
        if (goodDescription != null) {
            stmt.bindString(6, goodDescription);
        }
        stmt.bindDouble(7, entity.getGoodPrice());
 
        String promotion = entity.getPromotion();
        if (promotion != null) {
            stmt.bindString(8, promotion);
        }
        stmt.bindDouble(9, entity.getPromotionPrice());
 
        String goodsImgUrl = entity.getGoodsImgUrl();
        if (goodsImgUrl != null) {
            stmt.bindString(10, goodsImgUrl);
        }
 
        String goodsImgLocalUrl = entity.getGoodsImgLocalUrl();
        if (goodsImgLocalUrl != null) {
            stmt.bindString(11, goodsImgLocalUrl);
        }
        stmt.bindLong(12, entity.getGoodColdHot() ? 1L: 0L);
 
        String goodImgDownload = entity.getGoodImgDownload();
        if (goodImgDownload != null) {
            stmt.bindString(13, goodImgDownload);
        }
 
        String updateTime = entity.getUpdateTime();
        if (updateTime != null) {
            stmt.bindString(14, updateTime);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public GoodsInfo readEntity(Cursor cursor, int offset) {
        GoodsInfo entity = new GoodsInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // goodID
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // goodName
            cursor.getInt(offset + 3), // goodTypeID
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // goodTypeName
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // goodDescription
            cursor.getDouble(offset + 6), // goodPrice
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // promotion
            cursor.getDouble(offset + 8), // promotionPrice
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // goodsImgUrl
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // goodsImgLocalUrl
            cursor.getShort(offset + 11) != 0, // goodColdHot
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // goodImgDownload
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13) // updateTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, GoodsInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setGoodID(cursor.getInt(offset + 1));
        entity.setGoodName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setGoodTypeID(cursor.getInt(offset + 3));
        entity.setGoodTypeName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setGoodDescription(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setGoodPrice(cursor.getDouble(offset + 6));
        entity.setPromotion(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setPromotionPrice(cursor.getDouble(offset + 8));
        entity.setGoodsImgUrl(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setGoodsImgLocalUrl(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setGoodColdHot(cursor.getShort(offset + 11) != 0);
        entity.setGoodImgDownload(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setUpdateTime(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(GoodsInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(GoodsInfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(GoodsInfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}