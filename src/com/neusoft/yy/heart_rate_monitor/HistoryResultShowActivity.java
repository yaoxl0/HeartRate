package com.neusoft.yy.heart_rate_monitor;

import com.neusoft.yy.bean.Rate;
import com.neusoft.yy.util.Common;
import com.neusoft.yy.view.CommonDialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryResultShowActivity extends Activity {

	private Rate mRate;
	private int position = -1;
	private TextView mHistoryTime;
	private TextView mHistoryHeartnum;
	private ImageView mHistoryPointIndex;
	private ImageView mHistoryShare;
	private ImageView mHistoryTrash;
	
	private int indexto;
	private int heartNum;
	private int[] location_point_index = new int[2];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.historyresultshow_main);
		
		Bundle mBundle = getIntent().getExtras();
		mRate = (Rate) mBundle.getSerializable("rate");
		heartNum = Integer.parseInt(mRate.getHeartNumber());
		position = mBundle.getInt("position");
		
		mHistoryTime = (TextView) findViewById(R.id.history_time);
		mHistoryTime.setText(Common.dateToString(mRate.getHeartTime()));
		
		mHistoryHeartnum = (TextView) findViewById(R.id.history_heartnum);
		mHistoryHeartnum.setText(mRate.getHeartNumber());
		
		mHistoryPointIndex = (ImageView) findViewById(R.id.history_point_index);
		mHistoryPointIndex.getLocationOnScreen(location_point_index);
		indexto = 0;
		if(heartNum <= 60){
			indexto = 100/60*heartNum;
		} else if (heartNum>60 && heartNum<90) {
			indexto = 100 + 100/30*(heartNum-60);
		} else {
			indexto = 200 + heartNum-90;
		}
		float scale = getResources().getDisplayMetrics().density; 
		// 根据手机的分辨率从 dp 的单位 转成为 px(像素)
		indexto = (int)(indexto*scale+0.5f);
		Animation animation = new TranslateAnimation(location_point_index[0], location_point_index[0]+indexto, 0, 0);
		animation.setFillAfter(true);
		animation.setDuration(600L);
		mHistoryPointIndex.startAnimation(animation);
		
		// 该按钮gone了，先不实现这个功能
		mHistoryShare = (ImageView) findViewById(R.id.historyresultshow_share);
		mHistoryShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(HistoryResultShowActivity.this, "weihsixian", Toast.LENGTH_SHORT).show();
			}
		});
		
		mHistoryTrash = (ImageView) findViewById(R.id.historyresultshow_trash);
		mHistoryTrash.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new CommonDialog
					.Builder(HistoryResultShowActivity.this)
					.setTitle(R.string.trash)
					.setMessage(R.string.trashdescript)
					.setNegativeButton(R.string.canceltrash, null)
					.setPositiveButton(R.string.trash, new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//判断
							if(position != -1) {
								// 将数据返回到上一个activity的onActivityResult方法中进一步处理
					            Intent data=new Intent();  
					            data.putExtra("position", position);  
					            setResult(HeartRateMonitor.HEARTRATE_ONE, data); 
							}
							finish();
						}
						
					})
					.create()
					.show();
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		Intent data=new Intent();
        data.putExtra("position", "");
        setResult(HeartRateMonitor.HEARTRATE_ZERO, data);
        finish();
	}
	
}
