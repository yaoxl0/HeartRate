package com.neusoft.yy.heart_rate_monitor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import com.neusoft.yy.adapter.HistoryAdapter;
import com.neusoft.yy.bean.Rate;
import com.neusoft.yy.view.CommonDialog;
import com.neusoft.yy.view.DrawHeartDiagram;
import com.neusoft.yy.view.ScrollLayout;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class HeartRateMonitor extends Activity {

	public static final int HEARTRATE_ONE = 1;
    private static final String TAG = "HeartRateMonitor";
    private static final AtomicBoolean processing = new AtomicBoolean(false);
    
    private static final int CURRENT_TIME = 1;
    private static final int HEARTIMAGE = 2;

    private ScrollLayout mScrollLayout;
    
    LinearLayout displayLinearLayout = null;
    
    private static TextView numberOfBPM = null;
    private TextView bpmSign = null;
    private TextView currentTime = null;
    private TextView heart_guide = null;
    private TextView heart_history = null;
    private TextView heart_home = null;
    private TextView heart_logo = null;
    // heart �ײ���ť�����һ�������
    int fromX;
    int toX;
    TranslateAnimation am;
    
    private RelativeLayout power = null;
    
    private ImageView battery = null;
    private ImageView heartImage = null;
    private ImageView power_led = null;
    private ImageView bottom_v1 = null;
    
    private ViewPager mGuideViewPager = null;
    private View view1, view2, view3;
	private final List<View> views = new ArrayList<View>();
	
	private ListView historyList;
	private List<Rate> mRateList = new ArrayList<Rate>();
	private HistoryAdapter historyAdapter;
	
	private boolean beatingFirst = true;
	private int indexto;
	private RelativeLayout home_beating;
	private LinearLayout home_afterbeating;
	private TextView home_afterbeating_resultnumber;
	private ImageView home_afterbeating_point_index;
	private int[] location_point_index = new int[2];
	private LinearLayout afterbeating_cancel;
	private LinearLayout afterbeating_save;
    
    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
		@Override
		public void run() {
			handler.sendEmptyMessage(CURRENT_TIME);
		}
	};
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == CURRENT_TIME){ //��ʾϵͳ��ǰʱ��
				curDate = new Date(System.currentTimeMillis());
				currentTime.setText(format.format(curDate));
				curDate = null;
			} else if (msg.what == HEARTIMAGE) {
				//����ǿ��ͼ���ݽ��յ�����������̬�ı䶯
				if(beatsAvg < 40) {
					heartImage.setBackgroundDrawable(heartDrawable[0]);
				} else if (beatsAvg>=40 && beatsAvg<50) {
					heartImage.setBackgroundDrawable(heartDrawable[1]);
				} else if (beatsAvg>=50 && beatsAvg<60) {
					heartImage.setBackgroundDrawable(heartDrawable[2]);
				} else if (beatsAvg>=60 && beatsAvg<70) {
					heartImage.setBackgroundDrawable(heartDrawable[3]);
				} else if (beatsAvg>=70 && beatsAvg<80) {
					heartImage.setBackgroundDrawable(heartDrawable[4]);
				} else if (beatsAvg>=80 && beatsAvg<90) {
					heartImage.setBackgroundDrawable(heartDrawable[5]);
				} else if (beatsAvg>=90) {
					heartImage.setBackgroundDrawable(heartDrawable[6]);
				}
			}
		}
		
	};

    private static WakeLock wakeLock = null;

    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];

    public static enum TYPE {
        GREEN, RED
    };

    private static TYPE currentType = TYPE.GREEN;

    public static TYPE getCurrent() {
        return currentType;
    }

    private static int beatsIndex = 0;
    private static final int beatsArraySize = 3;
    private static final int[] beatsArray = new int[beatsArraySize];
    private static double beats = 0;
    private static long startTime = 0;
    
    private Bitmap[] heartBitmap = new Bitmap[7];
    private Drawable[] heartDrawable = new Drawable[7];
    private Bitmap[] batteryBitmap = new Bitmap[4];
    private Drawable[] batteryDrawable = new Drawable[4];
    private static int ff = 0;
    private int beatsAvg = 0;
    private SimpleDateFormat format = null;
    private Date curDate = null;
    private SoundPool soundPool = null; //��ť�ȵ���Ч
    private AnimationDrawable anim = null; //Frame Animation ���Ķ���Ч��
    private int click_down_sound = 0;
    private int click_up_sound = 0;
    private int beat_sound = 0;
    private int currentIndex = 2; // Ĭ�� ��ҳ
    private boolean power_on_off = false;
    private LinearLayout lleftLayout = null;
    private RelativeLayout showviewTop = null;
    CameraPreview cameraPreview = null;
    private DrawHeartDiagram diagram = null;
    
    private int guideDotCount;
    private ImageView guideDotImageViews[] = null;
    private int guideDotCurrentIndex;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // test
        Rate r = new Rate();
        r.setHeartNumber("66");
        r.setHeartTime(new Date());
        mRateList.add(r);
        // test end
        
        heart_guide = (TextView) findViewById(R.id.heart_guide);
        heart_guide.setOnClickListener(new MyOnClickListener());
        heart_history = (TextView) findViewById(R.id.heart_history);
        heart_history.setOnClickListener(new MyOnClickListener());
        heart_home = (TextView) findViewById(R.id.heart_home);
        heart_home.setOnClickListener(new MyOnClickListener());
        heart_logo = (TextView) findViewById(R.id.heart_logo);
        heart_logo.setOnClickListener(new MyOnClickListener());
        
        bottom_v1 = (ImageView) findViewById(R.id.bottom_v1);
        
        /*  ��λTextView���޷���onCreate������ֱ�ӻ�ȡview�ĳߴ磬��Ϊ��ʱ�����layout��û�г�ʼ����
        	ͨ��ע����Ҫ������view��viewTreeObserver����ȡ��ߴ磬
        	�ص�����onGlobalLayout�����õ�ʱ�������Ӧ��view�Ѿ���ʼ����
        	��ʱ�Ϳ��Խ�ͼƬ��λ��TextView���м䡣
        */
        final ViewTreeObserver viewTreeObserver = heart_guide.getViewTreeObserver();
        if(viewTreeObserver.isAlive())
        {
            viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
            {
                @Override
                public void onGlobalLayout()
                {
                    heart_guide.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    int initX = heart_guide.getLeft() - (bottom_v1.getWidth() - heart_guide.getWidth())/2;
                    bottom_v1.setPadding(initX, 0, 0, 0);
                    
                    // ��xml��Ĭ�Ͻ�bottom_v1������0λ���ϣ���ҳ����ֵ���Ҫ����2��ҳ�ϣ������ڼ���ҳ��ʱ�Ϳ��ٻ�����2λ����
                    toX = ((RelativeLayout) heart_home.getParent()).getLeft();
			        am = new TranslateAnimation(fromX, toX, 0, 0);
			        am.setDuration(1);
			        am.setFillAfter(true);
			        bottom_v1.clearAnimation();
			        bottom_v1.startAnimation(am);
			        fromX = toX;
                }
            });
        }
        
        initPageScroll();
        
        initGuide();

        initHistory();
        
		initHome();
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
        
        //ע��һ�����ܹ㲥����--�������仯
        registerReceiver(batteryChangedReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        
        showviewTop = (RelativeLayout) this.findViewById(R.id.showViewTop);
		lleftLayout =new LinearLayout(this);
		LinearLayout.LayoutParams lpCamera =  new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lleftLayout.setLayoutParams(lpCamera);
		showviewTop.addView(lleftLayout);
		
		FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, 140);
		params2.gravity = Gravity.TOP|Gravity.LEFT;
		displayLinearLayout =new LinearLayout(this);
		LinearLayout.LayoutParams lpCamera2 =  new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 140);
		displayLinearLayout.setLayoutParams(lpCamera2);
		this.addContentView(displayLinearLayout, params2);
//		diagram = new DrawHeartDiagram(this);
//		displayLinearLayout.addView(diagram);
    }
    
    private void initPageScroll() {
		mScrollLayout = (ScrollLayout) findViewById(R.id.main_scrolllayout);
		mScrollLayout.snapToScreen(currentIndex);
    }	
    
    // guide ��صĴ��� start
    private void initGuide() {
    	mGuideViewPager = (ViewPager) findViewById(R.id.guide_tabpager);
    	LayoutInflater layoutInflater = LayoutInflater.from(this);
    	// ������д
    	view1 = layoutInflater.inflate(R.layout.guide_first, null);
		view2 = layoutInflater.inflate(R.layout.guide_second, null);
		view3 = layoutInflater.inflate(R.layout.guide_third, null);
		
		views.add(view1);
		views.add(view2);
		views.add(view3);
		PagerAdapter mPagerAdapter = new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return views.size();
			}
		
			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager)container).removeView(views.get(position));
			}
		
			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager)container).addView(views.get(position));
				return views.get(position);
			}
			
		};
		mGuideViewPager.setAdapter(mPagerAdapter);
		mGuideViewPager.setOnPageChangeListener(new mGuideViewPagerOnPageChangeListener());
		initGuideDot();
    }
    
    private class mGuideViewPagerOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int index) {
			setCurrentGuideDotPoint(index);
		}
    }
    
    private void initGuideDot() {
    	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.guide_dot_layout);
    	guideDotCount = linearLayout.getChildCount();
    	
    	guideDotImageViews = new ImageView[guideDotCount];
    	
    	for(int i = 0; i < guideDotCount; i++) {
    		guideDotImageViews[i] = (ImageView) linearLayout.getChildAt(i);
    		guideDotImageViews[i].setEnabled(true);
    		guideDotImageViews[i].setTag(i);
    	}
    	
    	guideDotCurrentIndex = 0;
    	guideDotImageViews[guideDotCurrentIndex].setEnabled(false);
    	
    }
    
    private void setCurrentGuideDotPoint(int index) {
    	if (index<0 || index>guideDotCount-1 || guideDotCurrentIndex==index) {
    		return ;
    	}
    	
    	guideDotImageViews[guideDotCurrentIndex].setEnabled(true);
    	guideDotImageViews[index].setEnabled(false);
    	
    	guideDotCurrentIndex = index;
    }
    // guide ��صĴ��� end
    
    private void initHistory() {
    	historyList = (ListView) findViewById(R.id.history_list);
    	historyAdapter = new HistoryAdapter(this, mRateList);
    	historyList.setAdapter(historyAdapter);
    	historyList.setOnItemClickListener(new HistoryListOnItemClickListener());
    	historyList.setOnItemLongClickListener(new HistoryListOnItemLongClickListener());
    }
    
    private class HistoryListOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent(HeartRateMonitor.this, HistoryResultShowActivity.class);
			intent.putExtra("rate", mRateList.get(position));
			intent.putExtra("position", position);
			startActivityForResult(intent, HEARTRATE_ONE);
		}
    }
 
    private class HistoryListOnItemLongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			showHistoryListOnItemLongClickDialog(HeartRateMonitor.this, position);
			return true;
		}
    	
    }
    
    private void showHistoryListOnItemLongClickDialog(Context context, final int position){
    	new CommonDialog.Builder(context)
    		.setTitle(R.string.share_trash)
    		.setNegativeButton(R.string.trash, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					mRateList.remove(position);
					historyAdapter.notifyDataSetChanged();
					dialog.cancel();
				}
			})
			.setPositiveButton(R.string.share, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(HeartRateMonitor.this, "share", Toast.LENGTH_SHORT).show();
				}
			}).create().show();
    }
    
    private void initHome() {
    	Typeface fontFace = Typeface.createFromAsset(getAssets(), "fonts/dsdig.ttf"); //��������
    	//��ǰ��������
        numberOfBPM = (TextView) findViewById(R.id.numberOfBPM);
        numberOfBPM.setTypeface(fontFace);
        numberOfBPM.setTextColor(Color.BLACK);
        //����Ĭ����ʾ����0
        numberOfBPM.setText("0");
        
        //���ʵ�λ/bpm
        bpmSign = (TextView) findViewById(R.id.bpmSign);
        bpmSign.setTypeface(fontFace);
        bpmSign.setTextColor(Color.BLACK);
        bpmSign.setText("BPM");
        
        //��ǰ��ص���
        battery = (ImageView) findViewById(R.id.battery);
        //��ص�����ͼƬ�Ľ��������Ŵ�1.5��
        int batteryBitmapLong = batteryBitmap.length;
        for(int frame=0; frame<batteryBitmapLong; frame++){
			batteryBitmap[frame] = zoomImage( 
											Bitmap.createBitmap(
												BitmapFactory.decodeResource(this.getResources(), R.drawable.battery),
												0, frame*14, 30, 14), //��ȡԭͼ
											45, //��ȷŴ�1.5
											21); //�߶ȷŴ�1.5
			batteryDrawable[frame] = new BitmapDrawable(batteryBitmap[frame]);
		}
        //����Ĭ����ʾ�����ĵ���(��С)
        battery.setBackgroundDrawable(batteryDrawable[3]);
        
        //ϵͳ��ǰʱ��
        currentTime = (TextView) findViewById(R.id.currentTime);
        currentTime.setTextColor(Color.BLACK);
        format = new SimpleDateFormat("HH:mm:ss");
        curDate = new Date(System.currentTimeMillis());
        String dateString = format.format(curDate);
        //�ȼ�����ʾʱ�䣬֮���ÿ1s��̬����һ��
        currentTime.setText(dateString);
        
        //����ǿ��ͼ
        heartImage = (ImageView) findViewById(R.id.heartImage);
        int heartBitmapLong = heartBitmap.length;
        //����ǿ��ͼ�Ľ���,���Ŵ�1.5��
        for(int frame=0; frame<heartBitmapLong; frame++){
			heartBitmap[frame] = zoomImage(
											Bitmap.createBitmap(
													BitmapFactory.decodeResource(this.getResources(), R.drawable.accuracy),
													0, frame*49, 53, 49),  //��ȡԭͼ
											80, //��ȷŴ�
											74);//�߶ȷŴ�
			heartDrawable[frame] = new BitmapDrawable(heartBitmap[frame]);
		}
        //Ĭ����ʾ����ǿ��ͼ��������ͼ
        heartImage.setBackgroundDrawable(heartDrawable[0]);
        
        timer.schedule(timerTask, 1000, 1000);
        
        //power��/��
        power = (RelativeLayout) findViewById(R.id.power);
        power_led = (ImageView) findViewById(R.id.power_led);
        power_led.setBackgroundResource(R.drawable.led_off);
        power.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					soundPool.play(click_down_sound, 1, 1, 1, 0, 1);
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					soundPool.play(click_up_sound, 1, 1, 1, 0, 1);
					if(power_on_off){
						power_on_off = false;
						power_led.setBackgroundResource(R.drawable.led_off);
						
						//cameraPreview start
						lleftLayout.removeViewInLayout(cameraPreview);
						//cameraPreview end
						
						// ����̬����������ͼremove��
						displayLinearLayout.removeView(diagram);
						
						if(beatingFirst) {
							home_afterbeating_point_index.getLocationOnScreen(location_point_index);
							beatingFirst = false;
						}
						indexto = 0;
						if(beatsAvg <= 60){
							indexto = 100/60*beatsAvg;
						} else if (beatsAvg>60 && beatsAvg<90) {
							indexto = 100 + 100/30*(beatsAvg-60);
						} else {
							indexto = 200 + beatsAvg-90;
						}
						float scale = getResources().getDisplayMetrics().density; 
						// �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
						indexto = (int)(indexto*scale+0.5f);
						
						afterBeatingPointToArrival();
					} else {
						power_on_off = true;
						power_led.setBackgroundResource(R.drawable.led_on);
						
						//cameraPreview start
						cameraPreview = new CameraPreview(HeartRateMonitor.this);
					    cameraPreview.setZOrderOnTop(true);
					    lleftLayout.addView(cameraPreview);
						//cameraPreview end
					    
					    // ��̬��������ͼ
					    diagram = new DrawHeartDiagram(HeartRateMonitor.this);
						displayLinearLayout.addView(diagram);
					}
					return true;
				}
				return false;
			}
		});
        
        //soundpool��Ч��ʼ��
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 100);
        click_down_sound = soundPool.load(this, R.raw.click_down, 1);
        click_up_sound = soundPool.load(this, R.raw.click_up, 1);
        beat_sound = soundPool.load(this, R.raw.beat, 1);
    	
    	home_beating = (RelativeLayout) findViewById(R.id.home_beating);
    	home_afterbeating = (LinearLayout) findViewById(R.id.home_afterbeating);
    	home_afterbeating_resultnumber = (TextView) findViewById(R.id.afterbeating_resultnumber);
    	home_afterbeating_point_index = (ImageView) findViewById(R.id.afterbeating_point_index);
    	
    	afterbeating_cancel = (LinearLayout) findViewById(R.id.home_afterbeating_cancel);
    	afterbeating_cancel.setOnClickListener(new AfterbeatingOnClickListener());
    	afterbeating_save = (LinearLayout) findViewById(R.id.home_afterbeating_save);
    	afterbeating_save.setOnClickListener(new AfterbeatingOnClickListener());
    }
    
    public class AfterbeatingOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.home_afterbeating_cancel:
				afterBeatingPointToOriginal();
				break;

			case R.id.home_afterbeating_save:
				Rate rate = new Rate();
				rate.setHeartNumber(String.valueOf(beatsAvg));
				rate.setHeartTime(new Date(System.currentTimeMillis()));
				mRateList.add(0, rate); // ÿ�εõ��������ݶ���ӵ���λ
				historyAdapter.notifyDataSetChanged();
				
				// ������historyҳ��
				currentIndex = 1;
				move(heart_history);
				mScrollLayout.snapToScreen(currentIndex);
				afterBeatingPointToOriginal();
				break;
			}
		}
    	
    }
    
    public class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.heart_guide:
				currentIndex = 0;
				break;
			case R.id.heart_history:
				currentIndex = 1;
				break;
			case R.id.heart_home:
				currentIndex = 2;
				break;
			case R.id.heart_logo:
				currentIndex = 3;
				break;
			}
			
			move(v);
			mScrollLayout.snapToScreen(currentIndex);
		}

	}
    
    private void move(View v) {
        toX = ((RelativeLayout) v.getParent()).getLeft();
        am = new TranslateAnimation(fromX, toX, 0, 0);
        am.setDuration(300);
        am.setFillAfter(true);
        bottom_v1.clearAnimation();
        bottom_v1.startAnimation(am);
        fromX = toX;
    }
    
    // ��������α�Ķ�̬����
    private void afterBeatingPointToArrival(){
    	home_beating.setVisibility(View.GONE);
		home_afterbeating.setVisibility(View.VISIBLE);
		home_afterbeating_resultnumber.setText(String.valueOf(beatsAvg));
    	Animation animation = new TranslateAnimation(location_point_index[0], location_point_index[0]+indexto, 0, 0);
		animation.setFillAfter(true);
		animation.setDuration(300L);
		home_afterbeating_point_index.startAnimation(animation);
    }
    
    // homeҳ�滹ԭΪԭʼ��ʽ
	// ʹhome_afterbeating_point_index��λ���ٻص�ԭʼ�ط�
    private void afterBeatingPointToOriginal(){
    	home_beating.setVisibility(View.VISIBLE);
		home_afterbeating.setVisibility(View.GONE);
		home_afterbeating_resultnumber.setText("00");
    	Animation animation = new TranslateAnimation(location_point_index[0]+indexto, location_point_index[0], 0, 0);
		animation.setFillAfter(true);
		animation.setDuration(1L);
		home_afterbeating_point_index.startAnimation(animation);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        
        wakeLock.acquire();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(wakeLock != null) {
        	wakeLock.release();
        }
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(HEARTRATE_ONE == resultCode) {
			Bundle mBundle = data.getExtras();
			int position = mBundle.getInt("position");
			mRateList.remove(position);
			historyAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		timerTask.cancel();
		timer.cancel();
		//ȡ��ע��ĵ�����С������
		unregisterReceiver(batteryChangedReceiver);
	}

	private PreviewCallback previewCallback = new PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
		
//			//yaoxl start
//			ff++;
//			long ffStartTime = System.currentTimeMillis();
//			//yaoxl end
		
            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();

            if (!processing.compareAndSet(false, true)) return;

            int width = size.width;
            int height = size.height;

//			//yaoxl start
//			long imgBTime = System.currentTimeMillis();
//			//yaoxl end
			
            // The core algorithm is called to get the imgavg value
            int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width);
//            if(imgAvg < 200)
            	diagram.getY = 50;
//            else
//            	diagram.getY = (imgAvg-210) * 5;
            diagram.postInvalidate();
            
//			//yaoxl start
//			long imgETime = System.currentTimeMillis();
//			float scale = getResources ( ). getDisplayMetrics ( ). density ; 
//			String response = "ff="+ff+",ffStartTime="+ffStartTime+",imgAvg="+imgAvg+",imgBTime="+imgBTime+",imgETime"+imgETime+"&&&&&midu:" + scale;
//			//yaoxl end
			
//			//yaoxl start  ���ڲ���
//			System.out.println("----------------------------");
//			System.out.println("������֪�� response = " + response);
//			
//			 try {
//				   String pathName = "/sdcard/";
//				   String fileName = "logheartrate.txt";
//				   File path = new File(pathName);
//				   File file = new File(pathName + fileName);
//				   if (!path.exists()) {
//					Log.d("TestFile", "Create the path:" + pathName);
//					path.mkdir();
//				   }
//				   if (!file.exists()) {
//					Log.d("TestFile", "Create the file:" + fileName);
//					file.createNewFile();
//				   }
//				   RandomAccessFile raf = new RandomAccessFile(file, "rw");
//				   raf.seek(file.length());
//				   raf.write(response.getBytes());
//				   raf.write("\n".getBytes()); //����
//				   raf.close();
//			 } catch (Exception e) {
//				   Log.e("TestFile", "Error on writeFilToSD.");
//			 }
//			//yaoxl  end  ���ڲ���
			
            if (imgAvg < 200 || imgAvg > 255) {
                processing.set(false);
                return;
            }

            int averageArrayAvg = 0;
            int averageArrayCnt = 0;
            for (int i = 0; i < averageArray.length; i++) {
                if (averageArray[i] > 0) {
                    averageArrayAvg += averageArray[i];
                    averageArrayCnt++;
                }
            }

            int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
            TYPE newType = currentType;
            if (imgAvg < rollingAverage) {
                newType = TYPE.RED;
                if (newType != currentType) {
                    beats++;
                    soundPool.play(beat_sound, 1, 1, 1, 0, 1); //����һ�Σ���һ����������
                    diagram.getY = (imgAvg-210) * 5;
                    diagram.postInvalidate();
                }
            } else if (imgAvg > rollingAverage) {
                newType = TYPE.GREEN;
            }

            if (averageIndex == averageArraySize) averageIndex = 0;
            averageArray[averageIndex] = imgAvg;
            averageIndex++;

            // Transitioned from one state to another to the same
            if (newType != currentType) {
                currentType = newType;
//                image.postInvalidate();
            }

            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d;
            if (totalTimeInSecs >= 5) { //ÿ��5s��ʾ����һ��
                double bps = (beats / totalTimeInSecs);
                int dpm = (int) (bps * 60d);
                if (dpm < 30 || dpm > 180) { //����������ʹ��ͻ���ߣ�������ȷ�����¿�ʼ���
                	numberOfBPM.setText("0"); //���ͻ���ߣ���ʾ0
                	beatsAvg = 0; //��1
                	handler.sendEmptyMessage(HEARTIMAGE); //�������ݸ���
                    startTime = System.currentTimeMillis();
                    beats = 0;
                    processing.set(false);
                    return;
                }

                if (beatsIndex == beatsArraySize) beatsIndex = 0;
                beatsArray[beatsIndex] = dpm; //ÿ�λ�õ���Ч���ݶ����棬ѭ������
                beatsIndex++;

                int beatsArrayAvg = 0;
                int beatsArrayCnt = 0;
                for (int i = 0; i < beatsArray.length; i++) {
                    if (beatsArray[i] > 0) {
                        beatsArrayAvg += beatsArray[i];
                        beatsArrayCnt++;
                    }
                }
                beatsAvg = (beatsArrayAvg / beatsArrayCnt); //ȡǰ������������ƽ��ֵ
//                text.setText(String.valueOf(beatsAvg));
                numberOfBPM.setText(String.valueOf(beatsAvg)); //��ʾ����������
                handler.sendEmptyMessage(HEARTIMAGE); //�������ݸ���
                startTime = System.currentTimeMillis();
                beats = 0;
            }
            processing.set(false);
        }
    };

    /**
     * SurfaceView
     * @author YXL
     *
     */
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
		SurfaceHolder mHolder;
		private Camera camera;
		public CameraPreview(Context context) {
			super(context);
			mHolder = getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		
		public void surfaceCreated(SurfaceHolder holder) {
			camera = Camera.open();
			camera.setPreviewCallback(previewCallback);
			try {
				camera.setPreviewDisplay(holder);
			} catch (IOException exception) {
				camera.release();
				camera = null;
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			Camera.Parameters parameters = camera.getParameters();
			parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH); //�������
			Camera.Size size = getSmallestPreviewSize(width, height, parameters);
			if (size != null) {
				parameters.setPreviewSize(size.width, size.height);
			}
			camera.setParameters(parameters);
			camera.startPreview();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			camera.setPreviewCallback(null) ;
			camera.stopPreview();
			camera.release();
			camera = null;
		}
    }
    
    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea < resultArea) 
                    	result = size;
                }
            }
        }
        return result;
    }
    
    //����ͼƬ�Ĵ�С
    public static Bitmap zoomImage(Bitmap bgimage, int newWidth, int newHeight) {
		// ��ȡ���ͼƬ�Ŀ�͸�
		int width = bgimage.getWidth();
		int height = bgimage.getHeight();
		// ��������ͼƬ�õ�matrix����
		Matrix matrix = new Matrix();
		// ���������ʣ��³ߴ��ԭʼ�ߴ�
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// ����ͼƬ����
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height, matrix, true);
		return bitmap;
	}
    
    //���ܹ㲥--�������仯
    private BroadcastReceiver batteryChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
				int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
				if(BatteryManager.BATTERY_STATUS_CHARGING == status) { //���ڳ��
					anim = new AnimationDrawable();
			        for (int i = 3; i >= 0; i--) {  
			            //����֡��ӵ�AnimationDrawable��  
			            anim.addFrame(batteryDrawable[i], 300);  
			        }  
			        anim.setOneShot(false); //����Ϊloop  
			        battery.setBackgroundDrawable(anim);  //����������ΪImageView����  
			        anim.start();   //��ʼ���� 
				} else { //û�г��
					if(anim != null && anim.isRunning()){
						anim.stop(); //ֹͣ����
						anim = null;
					}
					int level = intent.getIntExtra("level", 0);
				    int scale = intent.getIntExtra("scale", 100);
				    int levelPercent = level * 100 / scale;
				    //������СͼƬ�������С����̬�䶯
				    if(levelPercent > 90){
				    	battery.setBackgroundDrawable(batteryDrawable[0]);
				    } else if(levelPercent>50 && levelPercent<=90) {
				    	battery.setBackgroundDrawable(batteryDrawable[1]);
				    } else if(levelPercent>25 && levelPercent<=50) {
				    	battery.setBackgroundDrawable(batteryDrawable[2]);
				    } else {
				    	battery.setBackgroundDrawable(batteryDrawable[3]);
				    }
				}
			}
		} 
    };
    
    /**
	 * �����������η���/�˳�
	 */
	private long firstTime;
	@Override
	public void onBackPressed() {
		if(System.currentTimeMillis() - firstTime < 3000) {
			finish();
		} else {
			firstTime = System.currentTimeMillis();
			Toast toast = Toast.makeText(this, R.string.press_again_exist, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			
		}
	}
    
}
