package com.neusoft.yy.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

	public static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public static String dateToString(Date date){
		String dateString = sdf.format(date);
		return dateString;
	}
	
	public static Date stringToDate(String dateString){
		try {
			return sdf.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
