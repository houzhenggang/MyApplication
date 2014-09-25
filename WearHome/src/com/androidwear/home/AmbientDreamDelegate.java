package com.androidwear.home;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.service.dreams.DreamService;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class AmbientDreamDelegate implements DreamDelegate {
	private static String TAG = "AmbientMode";
	private static HomeActivity sHomeActivity;
	private AmbientMode mService;

	public static void setHomeActivity(HomeActivity homeActivity) {
		sHomeActivity = homeActivity;
	}

	@Override
	public boolean dispatchKeyEvent(DreamService dreamService, KeyEvent keyEvent) {
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(DreamService dreamService,
			MotionEvent motionEvent) {
		return false;
	}

	@Override
	public void onAttachedToWindow(DreamService dreamService) {
		mService = ((AmbientMode) dreamService);
		wakeUpHomeActivity();
		dreamService.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		dreamService.getWindow().setFormat(1);
		dreamService.setInteractive(false);
		dreamService.setFullscreen(true);
		dreamService.setScreenBright(false);
	}

	@Override
	public void onCreate(DreamService dreamService) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestroy(DreamService dreamService) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDetachedFromWindow(DreamService dreamService) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDreamingStarted(DreamService dreamService) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDreamingStopped(DreamService dreamService) {
		// TODO Auto-generated method stub

	}

	public void wakeUpHomeActivity() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.addFlags(268435456);
		mService.startActivity(intent);
	}
}
