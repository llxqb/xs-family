package com.bhxx.xs_family.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.bhxx.xs_family.beans.SystemMessageBean;
import com.bhxx.xs_family.db.SqlLiteHelper;


public class SysMessDao {
    public static final String TABLE_NAME = "sysmess";// 表名
    public static final String COLUMN_ID = "_id";// 主键自动增长
    public static final String COLUMN_TIME = "time";// id标示
    public static final String COLUMN_CONTENT = "content";// 类型名
    public static final String COLUMN_USERID = "userid";// 用户

    /**
     * 插入一条数据
     * @param type
     * @return
     */
    public static boolean insertType(SystemMessageBean type,String userid) {

        SQLiteDatabase db = SqlLiteHelper.getInstance().getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SysMessDao.COLUMN_TIME, type.getTime());
        values.put(SysMessDao.COLUMN_CONTENT, type.getContent());
        values.put(SysMessDao.COLUMN_USERID,userid);

        return db.insert(SysMessDao.TABLE_NAME, null, values) != -1;
    }

    /**
     * 以集合形式插入
     * @param types
     */
    public static void insertTypes(List<SystemMessageBean> types,String userid) {

        SQLiteDatabase db = SqlLiteHelper.getInstance().getWritableDatabase();

        db.beginTransaction();

        try {
            for (int i = 0; i < types.size(); i++) {
                SystemMessageBean type = types.get(i);
                ContentValues values = new ContentValues();
                values.put(SysMessDao.COLUMN_CONTENT, type.getContent());
                values.put(SysMessDao.COLUMN_TIME, type.getTime());
                values.put(SysMessDao.COLUMN_USERID,userid);
                db.insert(SysMessDao.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     *
     *
     * @return
     */
    public static void clearTable() {

        SQLiteDatabase db = SqlLiteHelper.getInstance().getWritableDatabase();

        db.beginTransaction();

        try {
            db.execSQL("DELETE FROM " + SysMessDao.TABLE_NAME);
            db.execSQL("DELETE FROM " + "sqlite_sequence");

            // db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = " +
            // TABLE_NAME);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 清除表并插入新数据
     *
     * @param types
     * @return
     */
    public static void clearAndInsert(List<SystemMessageBean> types,String user) {

        clearTable();
        insertTypes(types,user);
    }

    public static List<SystemMessageBean> queryAllType(String userid) {

        SQLiteDatabase db = SqlLiteHelper.getInstance().getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE userid="+userid , null);

        List<SystemMessageBean> types = new ArrayList<SystemMessageBean>();
        while (cursor.moveToNext()) {

            SystemMessageBean type = new SystemMessageBean();
            type.setContent(cursor.getString(cursor
                    .getColumnIndex(SysMessDao.COLUMN_CONTENT)));
            type.setTime(cursor.getString(cursor
                    .getColumnIndex(SysMessDao.COLUMN_TIME)));
            types.add(type);
        }
        return types;
    }
}
