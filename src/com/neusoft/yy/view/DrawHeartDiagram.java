package com.neusoft.yy.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;

public class DrawHeartDiagram extends View {

	private static int ORIGIN_X = 400;
	private static int ORIGIN_Y = 50;
	private List<Point> mPlist;
	private Paint paint = new Paint();
	public int getY;
	
	public DrawHeartDiagram(Context context) {
		super(context);
		mPlist = new ArrayList<Point>();

		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(3);
		paint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		drawCurve(canvas);
		prepareLine();
//		initPlist();
	}
	
	private void drawCurve(Canvas canvas) {
		if(mPlist.size() >= 2){
			for (int i = 0; i < mPlist.size()-1; i++) {
				canvas.drawLine(mPlist.get(i).x, mPlist.get(i).y,mPlist.get(i+1).x ,mPlist.get(i+1).y, paint);
			}
		}
	}
	
	// 添加新数据
	private void prepareLine(){
		int py = getY + ORIGIN_Y;
		Point p = new Point(ORIGIN_X , py);
		int sumDot = 100;
		int moveLength = 3;
		if(mPlist.size() > sumDot+1){
			mPlist.remove(0);
			for (int i = 0; i < sumDot; i++) {
//				if(i == 0) mPlist.get(i).x -= (X_INTERVAL - 2);
//				else 
					mPlist.get(i).x -= moveLength;
			}
			mPlist.add(p);
		}
		else{
			for (int i = 0; i < mPlist.size(); i++) {
				mPlist.get(i).x -= moveLength;
			}
			mPlist.add(p);
		}
	}
	private void initPlist(){
		int py = getY + ORIGIN_Y;
		Point p = new Point(ORIGIN_X , py);
		mPlist.add(p);
	}

}
