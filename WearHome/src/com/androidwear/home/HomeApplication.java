package com.androidwear.home;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;

public class HomeApplication extends Application {

	private static boolean sIsScreenLarge;
	private static float sScreenDensity;

	private IconCache mIconCache;
	private HomeModel mModel;

	@Override
	public void onCreate() {
		super.onCreate();
		final int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		sIsScreenLarge = screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
		sScreenDensity = getResources().getDisplayMetrics().density;

		mIconCache = new IconCache(this);
		mModel = new HomeModel(this, mIconCache);

		// Register intent receivers
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addDataScheme("package");
		registerReceiver(mModel, filter);
		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
		filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
		filter.addAction(Intent.ACTION_LOCALE_CHANGED);
		filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
		registerReceiver(mModel, filter);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();

		unregisterReceiver(mModel);
	}

	HomeModel setLauncher(HomeActivity launcher) {
        mModel.initialize(launcher);
        return mModel;
    }

	public static boolean isScreenLarge() {
		return sIsScreenLarge;
	}

	public static float getScreenDensity() {
		return sScreenDensity;
	}

	public IconCache getIconCache() {
		return mIconCache;
	}
}
