package com.androidwear.home.watchfaces;

import com.androidwear.home.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DigitalClockView extends WatchCore{

	private View mBackground = null;
	private TextView mTimeText = null;

	public DigitalClockView(Context context) {
		this(context, null);
	}

	public DigitalClockView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DigitalClockView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setupView(){
		mBackground = findViewById(R.id.digital_background);
		mTimeText = (TextView)findViewById(R.id.digital_text);
	}

	@Override
	public void updateTimeView() {
		// TODO Auto-generated method stub
		mTimeText.setText(getTimeStr());
	}

	@Override
	public String getTime24Format() {
		// TODO Auto-generated method stub
		return "HH:mm";
	}

	@Override
	public String getTime12Format() {
		// TODO Auto-generated method stub
		return "hh:mm";
	}
}
