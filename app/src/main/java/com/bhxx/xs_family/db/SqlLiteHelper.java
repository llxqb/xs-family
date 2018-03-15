package com.bhxx.xs_family.db;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.db.dao.SysMessDao;


/**
 * 历史搜索 数据库
 *
 * @author bh1988034
 *
 */
public class SqlLiteHelper extends SQLiteOpenHelper {
	private static final String name = "classtype.db";

	private static class SingletonClass {

		private static final SqlLiteHelper instance = new SqlLiteHelper();
	}

	public static SqlLiteHelper getInstance() {

		return SingletonClass.instance;
	}

	/**
	 * 在SQLiteOpenHelper的子类当中，必须有这个构造函数
	 *
	 *            当前的Activity
	 *            表的名字（而不是数据库的名字，这个类是用来操作数据库的）
	 *            用来在查询数据库的时候返回Cursor的子类，传空值
	 *            当前的数据库的版本，整数且为递增的数
	 */
	public SqlLiteHelper() {
		super(App.app, name, null, 1);
	}

	/**
	 * 该函数是在第一次创建数据库时执行，只有当其调用getreadabledatebase()
	 * 或者getwrittleabledatebase()而且是第一创建数据库是才会执行该函数
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		db.beginTransaction();
		try {
			db.execSQL("CREATE TABLE IF NOT EXISTS "
					+ SysMessDao.TABLE_NAME + " ("
					+ SysMessDao.COLUMN_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ SysMessDao.COLUMN_CONTENT + " TEXT,"
					+ SysMessDao.COLUMN_USERID + " TEXT,"
					+ SysMessDao.COLUMN_TIME + " TEXT)");

			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			db.endTransaction();
		}

	}

	/**
	 * 更新版本
	 * @param db
	 * @param oldVersion
	 * @param newVersion
     */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + SysMessDao.TABLE_NAME;
		db.execSQL(sql);
		this.onCreate(db);
	}

}
