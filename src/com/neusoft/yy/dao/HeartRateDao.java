package com.neusoft.yy.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.neusoft.yy.bean.Rate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HeartRateDao {

	private DBHelper dbHelper;
	
	public HeartRateDao(Context context) {
		dbHelper = new DBHelper(context);
	}
	
	public List<Rate> searchRateByUserName(String username) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		List<Rate> rates = new ArrayList<Rate>();
		Rate rate = null;
		Cursor cursor = db.rawQuery("select * from " + DBData.HEARTRATE_TABLENAME
					+ " where " + DBData.HEARTRATE_USERNAME + " = ?"
					+ " order by " + DBData.HEARTRATE_TIME + " desc", new String[]{username});
		while(cursor.moveToNext()){
			rate = new Rate();
			String name = cursor.getString(cursor.getColumnIndex(DBData.HEARTRATE_USERNAME));
			rate.setUsername(name);
			String number = cursor.getString(cursor.getColumnIndex(DBData.HEARTRATE_NUMBER));
			rate.setHeartNumber(number);
			String time = cursor.getString(cursor.getColumnIndex(DBData.HEARTRATE_TIME));
			try {
				rate.setHeartTime(sdf.parse(time));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			rates.add(rate);
			rate = null;
		}
		
		cursor.close();
		db.close();
		return rates;
	}
	
	public long addRate(Rate rate) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBData.HEARTRATE_USERNAME, rate.getUsername());
		contentValues.put(DBData.HEARTRATE_NUMBER, rate.getHeartNumber());
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String time = sdf.format(rate.getHeartTime());
		contentValues.put(DBData.HEARTRATE_TIME, time);
		long result = db.insert(DBData.HEARTRATE_TABLENAME, null, contentValues);
		db.close();
		return result;
	}
	
	public int deleteRate(String username, String time) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int result = db.delete(DBData.HEARTRATE_TABLENAME, 
						DBData.HEARTRATE_USERNAME + " =? " + " and " + DBData.HEARTRATE_TIME + "=?", 
						new String[]{username, time});
		db.close();
		return result;
	}
}
