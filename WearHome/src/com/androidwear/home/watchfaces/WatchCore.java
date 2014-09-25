package com.androidwear.home.watchfaces;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.format.Time;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public abstract class WatchCore extends RelativeLayout{

	private boolean isBinded = false;
	private boolean isShowAmPm = false;
	private int mHour, mMinute;
	private String mTimeStr = null;
	private SimpleDateFormat mFormat24 = null;
	private SimpleDateFormat mFormat12 = null;
	
	public WatchCore(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public WatchCore(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public WatchCore(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public void onFinishInflate(){
		setupView();
		update();
	}

	private void init(){
		mMinute = 0;
		mHour = 0;
	}

	public void start(){
		bindReceiver();
	}

	public void stop(){
		unbindReceiver();
	}

	public void destroy(){
		stop();
	}

	public abstract void setupView();
	public abstract void updateTimeView();
	public abstract String getTime24Format();
	public abstract String getTime12Format();

	public boolean isShowAmPm(){
		return isShowAmPm;
	}

	public int getHourOfTime(){
		return mHour;
	}

	public int getMinuteOfTime(){
		return mMinute;
	}

	public String getTimeStr(){
		return mTimeStr;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			update();
		}
	};

	private void update(){
		updateTime();
		updateTimeView();
	}

	private void bindReceiver() {
		if (!isBinded) {
			IntentFilter localIntentFilter = new IntentFilter();
			localIntentFilter.addAction(Intent.ACTION_TIME_TICK);
			localIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
			localIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
			localIntentFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
			getContext().getApplicationContext().registerReceiver(mReceiver, localIntentFilter);
			isBinded = true;
		}
	}

	private void unbindReceiver() {
		if (isBinded) {
			getContext().getApplicationContext().unregisterReceiver(mReceiver);
			isBinded = false;
		}
	}

	private void updateTime(){
		Time now = new Time();
        now.setToNow();
        mHour = now.hour;
        mMinute = now.minute;
        if(mHour >= 19 || mHour <= 6){

        }
        if (!android.text.format.DateFormat.is24HourFormat(getContext())){
            if(mHour > 12){
            	mHour -= 12;
            }
            isShowAmPm = true;
            if(mFormat12 == null){
            	mFormat12 = new SimpleDateFormat(getTime12Format());
            }
            mTimeStr = mFormat12.format(new Date());
        }else{
        	isShowAmPm = false;
        	if(mFormat24 == null){
            	mFormat24 = new SimpleDateFormat(getTime24Format());
            }
            mTimeStr = mFormat24.format(new Date());
        }
	}
}
