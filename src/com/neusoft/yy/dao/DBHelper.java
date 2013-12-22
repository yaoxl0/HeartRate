package com.neusoft.yy.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context) {
		super(context, DBData.DATABASE_NAME, null, DBData.VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// �����û���
		db.execSQL("CREATE TABLE " + DBData.USER_TABLENAME + "("
				+ DBData.USER_NAME + " NVARCHAR(200),"
				+ DBData.USER_PASSWORD + " NVARCHAR(200),"
				+ "primary key ("
				+ DBData.USER_NAME
				+ "))");
		// ���������������ݱ�
		db.execSQL("CREATE TABLE " + DBData.HEARTRATE_TABLENAME + "("
				+ DBData.HEARTRATE_USERNAME + " NVARCHAR(200),"
				+ DBData.HEARTRATE_NUMBER + " NVARCHAR(200),"
				+ DBData.HEARTRATE_TIME + " NVARCHAR(50)"
				+ ")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// ɾ����
		db.execSQL("DROP TABLE IF EXISTS " + DBData.USER_TABLENAME);
		db.execSQL("DROP TABLE IF EXISTS " + DBData.HEARTRATE_TABLENAME);
	}

}
