package com.neusoft.yy.dao;

import com.neusoft.yy.bean.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDao {

	private DBHelper dbHelper;
	
	public UserDao(Context context) {
		dbHelper = new DBHelper(context);
	}
	
	/**
	 * 添加用户
	 * @param user
	 * @return
	 */
	public long addUser(User user) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBData.USER_NAME, user.getUsername());
		contentValues.put(DBData.USER_PASSWORD, user.getPassword());
		long result = db.insert(DBData.USER_TABLENAME, null, contentValues);
		db.close();
		return result;
	}
	
	/**
	 * 获取username的数量
	 * @param username
	 * @return
	 */
	public int getCountUser(String username) {
		int count = 0;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cr = db.rawQuery(
				"SELECT COUNT(*) FROM " + DBData.USER_TABLENAME + " WHERE " + DBData.USER_NAME + " = ?", new String[] {username});
		if (cr.moveToNext()) {
			count = cr.getInt(0);
		}
		cr.close();
		db.close();
		return count;
	}
	
	public int getCountUser(String username, String password) {
		int count = 0;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cr = db.rawQuery(
				"SELECT COUNT(*) FROM " + DBData.USER_TABLENAME 
				+ " WHERE " + DBData.USER_NAME + " = ?"
				+ " AND " + DBData.USER_PASSWORD + " = ?", new String[] {username, password});
		if (cr.moveToNext()) {
			count = cr.getInt(0);
		}
		cr.close();
		db.close();
		return count;
	}
}
