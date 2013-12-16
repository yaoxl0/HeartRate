package com.neusoft.yy.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.neusoft.yy.bean.Rate;
import com.neusoft.yy.heart_rate_monitor.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HistoryAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Rate> mRateList;
	
	public HistoryAdapter(Context context, List<Rate> rateList) {
		mInflater = LayoutInflater.from(context);
		mRateList = rateList;
	}
	
	@Override
	public int getCount() {
		return mRateList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.history_adapter, null);
			
			holder = new ViewHolder();
			holder.heartTime = (TextView) convertView.findViewById(R.id.history_list_time);
			holder.heartNum = (TextView) convertView.findViewById(R.id.history_list_heartnum);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Rate rate = mRateList.get(position);
		
		holder.heartTime.setText(DateToString(rate.getHeartTime()));
		holder.heartNum.setText(rate.getHeartNumber());
		
		return convertView;
	}
	
	static class ViewHolder{
		public TextView heartTime;
		public TextView heartNum;
	}
	
	private String DateToString(Date date){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(date);
	}

}
