package com.androidwear.home;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.androidwear.home.view.HomeView;
import com.androidwear.home.view.SwipeDismissFrameLayout;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.FragmentTransaction;
import android.app.LocalActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class HomeActivity extends ActivityGroup implements HomeModel.Callbacks,
		OnClickListener {
	private static String TAG = "Home";
	public static final int TYPE_HIDE_IN_MENU = 0;
	public static final int TYPE_NOT_IN_MENU = 1;
	private HomeModel mModel;

	private boolean mWorkspaceLoading = true;

	private boolean mPaused = true;
	private boolean mRestoring;
	private boolean mWaitingForResult;
	private boolean mOnResumeNeedsLoad;
	private FrameLayout mClockView = null;
	private VoicePlateFragment mVoicePlateFragment = null;
	private LauncherFragment mLauncherFragment = null;
	private ImageView mCircleView = null;
	private SwipeDismissFrameLayout mFrame = null;
	private boolean mTouchEable = true;
	private LocalActivityManager mActivityManager;
	private HomeView mHomeView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		HomeApplication app = (HomeApplication) getApplication();
		mModel = app.setLauncher(this);
		mActivityManager = getLocalActivityManager();
		setContentView(R.layout.activity_home);
		setupView();
		if (!mRestoring) {
			mModel.startLoader(this, true);
		}
		AmbientDreamDelegate.setHomeActivity(this);
	}

	private void setupView() {
		mHomeView = (HomeView)findViewById(R.id.homeview);
		mHomeView.setHomeActivity(this);
		mFrame = (SwipeDismissFrameLayout) findViewById(R.id.voice_ui);
		mClockView = (FrameLayout) findViewById(R.id.clock_view);
		mClockView.setOnClickListener(this);
		mCircleView = (ImageView) findViewById(R.id.circle_view);
		mCircleView.setImageBitmap(getCircleBitmap((int) getResources()
				.getDimension(R.dimen.circle_view_raduis)));
		mLauncherFragment = new LauncherFragment();
		mVoicePlateFragment = new VoicePlateFragment();
		mFrame.addCallbacks(new SwipeDismissFrameLayout.DismissCallbacks() {
			public boolean canDismiss() {
				return true;
			}

			public void onDismissed(
					SwipeDismissFrameLayout swipeDismissFrameLayout) {
				removeCueCard();
			}

			public void onSwipeCancelled() {
			}

			public void onSwipeStart() {
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		// getFragmentManager().popBackStack();
		mPaused = false;
		if (mRestoring || mOnResumeNeedsLoad) {
			mWorkspaceLoading = true;
			mModel.startLoader(this, true);
			mRestoring = false;
			mOnResumeNeedsLoad = false;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mPaused = true;
		removeCueCard();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public boolean setLoadOnResume() {
		if (mPaused) {
			Log.i(TAG, "setLoadOnResume");
			mOnResumeNeedsLoad = true;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void startBinding() {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindAllApplications(ArrayList<ApplicationInfo> apps) {
		// TODO Auto-generated method stub
		mLauncherFragment.setAllApps(getAllApps());
	}

	@Override
	public void bindAppsAdded(ArrayList<ApplicationInfo> apps) {
		// TODO Auto-generated method stub
		mLauncherFragment.setAllApps(getAllApps());
	}

	@Override
	public void bindAppsUpdated(ArrayList<ApplicationInfo> apps) {
		// TODO Auto-generated method stub
		mLauncherFragment.setAllApps(getAllApps());
	}

	@Override
	public void bindAppsRemoved(ArrayList<ApplicationInfo> apps,
			boolean permanent) {
		// TODO Auto-generated method stub
		mLauncherFragment.setAllApps(getAllApps());
	}

	@Override
	public void finishBindingItems() {
		// TODO Auto-generated method stub

	}

	private void launchCueCard() {
		mFrame.setVisibility(View.VISIBLE);
		showVoicePlate();
	}

	private void removeCueCard() {
		mFrame.removeAllViews();
		mFrame.setVisibility(View.GONE);
		mFrame.reset();
		removeAllFragment();
	}

	private void removeAllFragment(){
		FragmentTransaction ftransaction = getFragmentManager()
				.beginTransaction();
		ftransaction.remove(mLauncherFragment);
		ftransaction.remove(mVoicePlateFragment);
		ftransaction.commit();
	}

	private void showVoicePlate() {
		FragmentTransaction ftransaction = getFragmentManager()
				.beginTransaction();
		// ftransaction.setCustomAnimations(R.animator.slide_in_bottom,
		// R.animator.slide_out_bottom);
		ftransaction.add(R.id.voice_ui, mVoicePlateFragment);
		ftransaction.commit();
	}

	public void showLauncher() {
		FragmentTransaction ftransaction = getFragmentManager()
				.beginTransaction();
		// ftransaction.setCustomAnimations(R.animator.slide_in_bottom,
		// R.animator.slide_out_bottom);
		ftransaction.add(R.id.voice_ui, mLauncherFragment);
		ftransaction.commit();
	}

	public void playCircleAnimation() {
		mCircleView.setVisibility(View.VISIBLE);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale);
		animation.setInterpolator(new DecelerateInterpolator());
		animation.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation paramAnimation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation paramAnimation) {
				// TODO Auto-generated method stub
				launchCueCard();
				// showLauncher();
				mCircleView.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation paramAnimation) {
				// TODO Auto-generated method stub

			}

		});
		mCircleView.startAnimation(animation);
	}

	private Bitmap getCircleBitmap(int radius) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.WHITE);
		Bitmap target = Bitmap.createBitmap(radius * 2, radius * 2,
				Config.ARGB_8888);
		Canvas canvas = new Canvas();
		canvas.setBitmap(target);
		canvas.drawCircle(radius, radius, radius, paint);
		return target;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
        if (!mTouchEable) {
            return;
        }
		if (v != null) {
			switch (v.getId()) {
			case R.id.clock_view:
				playCircleAnimation();
				break;
			}
		}
	}

	public List<ApplicationInfo> getAllApps() {
		List<ApplicationInfo> allapps = mModel.getAllAppsList().data;
		List<ApplicationInfo> allHideApps = new ArrayList<ApplicationInfo>();
		for (ApplicationInfo info : allapps) {
			if (info.isHideInMenu) {
				allHideApps.add(info);
			}
		}
		Collections.sort(allHideApps, new HomeModel.NameComparator());
		return allHideApps;
	}

	public View getActivityView(ComponentName component){
		Intent intent = new Intent();
		intent.setComponent(component);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		String tag = getActvityTag(component);
		View view = mActivityManager.startActivity(tag, intent).getDecorView();
		view.setTag(component);
		return view;
	}

    public void setTouchEable(boolean eable) {
        this.mTouchEable = eable;
    }

	public String getActvityTag(ComponentName component){
		return component.getPackageName() + "-" + component.getClassName();
	}

	public void destroyActivity(String id, boolean finish){
		mActivityManager.destroyActivity(id, finish);
	}
}