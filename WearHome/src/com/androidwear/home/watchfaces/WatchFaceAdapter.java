package com.androidwear.home.watchfaces;

import java.util.ArrayList;
import java.util.List;

import com.androidwear.home.R;
import com.androidwear.home.view.HomeView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class WatchFaceAdapter extends PagerAdapter {

	private static final Intent ENUMERATE_WATCH_FACES_INTENT = new Intent(
			Intent.ACTION_MAIN)
			.addCategory("com.androidwear.home.category.HOME_BACKGROUND");
	private Context mContext;
	private LoaderHandler mLoaderHandler;
	private List<WatchFaceInfo> mWatchFaces = new ArrayList<WatchFaceInfo>();
	private List<WatchFaceOption> mRecycledViews = new ArrayList<WatchFaceOption>();
	private HomeView mHomeView = null;

	public WatchFaceAdapter(Context context, HomeView homeView) {
		mContext = context;
		mLoaderHandler = LoaderHandler.withNewThread(mContext
				.getApplicationContext());
		mHomeView = homeView;
		enumerateWatchFaces();
	}

	private void enumerateWatchFaces() {
		PackageManager pm = mContext.getPackageManager();
		List<ResolveInfo> list = pm.queryIntentActivities(
				ENUMERATE_WATCH_FACES_INTENT, 128);
		mWatchFaces.clear();
		mWatchFaces.add(new WatchFaceInfo(null, getDefaultWatchFacePreview(),
				mContext.getString(R.string.default_watchface_name)));
		collectWatchFaces(pm, list, true);
		collectWatchFaces(pm, list, false);
		Log.e("zhu","sss=" + mWatchFaces.size());
	}

	private void collectWatchFaces(PackageManager pm, List<ResolveInfo> list,
			boolean isInHome) {
		for (ResolveInfo info : list) {
			String packName = info.activityInfo.applicationInfo.packageName;
			if (isInHome != mContext.getPackageName().equals(packName)) {
				continue;
			}
			ComponentName component = new ComponentName(packName,
					info.activityInfo.name);
			Bundle metaBundle = info.activityInfo.metaData;
			String title = info.activityInfo.loadLabel(pm).toString();
			int previewResId = 0;
			if (metaBundle != null) {
				previewResId = metaBundle.getInt(
						"com.google.android.clockwork.home.preview", 0);
				if (previewResId == 0) {
					Log.w("WatchFaceAdapter", "not including watch face "
							+ component.flattenToShortString()
							+ ", you must add a "
							+ "com.google.android.clockwork.home.preview"
							+ " metadata tag.");
				}
				mWatchFaces.add(new WatchFaceInfo(component, previewResId,
						title));
			}
		}
	}

	private int getDefaultWatchFacePreview() {
		return R.drawable.bg_picker_simpleday_nonumber;
	}

	@Override
	public int getCount() {
		return mWatchFaces.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		if (arg0 == arg1) {
			return true;
		}
		return false;
	}

	public int getIndexOf(ComponentName component) {
		if (component == null) {
			return 0;
		}
		for (int i = 0; i < mWatchFaces.size(); i++) {
			WatchFaceInfo info = mWatchFaces.get(i);
			if (info.mComponent != null && info.mComponent.equals(component)) {
				return i;
			}
		}
		return -1;
	}

	private void onOptionClick(WatchFaceOption watchFaceOption) {
		mHomeView.onSelectWatchFace(watchFaceOption.getComponentName());
	}

	public Object instantiateItem(ViewGroup container, int position) {
		WatchFaceInfo watchFaceInfo = (WatchFaceInfo) mWatchFaces.get(position);
		WatchFaceOption watchFaceOption = createViewForFace(watchFaceInfo);
		container.addView(watchFaceOption);
		return watchFaceOption;
	}

	private WatchFaceOption createViewForFace(WatchFaceInfo watchFaceInfo) {
		WatchFaceOption watchFaceOption;
		if (!(mRecycledViews.isEmpty())) {
			watchFaceOption = (WatchFaceOption) mRecycledViews.remove(0);
			watchFaceOption.setFaceInfo(watchFaceInfo);
		} else {
			watchFaceOption = new WatchFaceOption(this.mContext, watchFaceInfo,
					this);
		}
		return watchFaceOption;
	}

	public void destroyItem(ViewGroup container, int position, Object object) {
		WatchFaceOption watchFaceOption = (WatchFaceOption) object;
		container.removeView(watchFaceOption);
		recycleView(watchFaceOption);
	}

	private void recycleView(WatchFaceOption watchFaceOption) {
		((ImageView) watchFaceOption.findViewById(R.id.preview)).setImageDrawable(null);
		mRecycledViews.add(watchFaceOption);
	}

	public void destroy(){
		mLoaderHandler.getLooper().quitSafely();
		mRecycledViews.clear();
		mWatchFaces.clear();
	}

	private static class LoaderHandler extends Handler {
		private Context mContext;
		private Handler mMainHandler;

		public LoaderHandler(Looper looper, Context context) {
			super(looper);
			this.mContext = context;
			this.mMainHandler = new Handler(context.getMainLooper());
		}

		public static LoaderHandler withNewThread(Context context) {
			HandlerThread localHandlerThread = new HandlerThread(
					"WatchFaceAdapter.ImageLoader");
			localHandlerThread.start();
			return new LoaderHandler(localHandlerThread.getLooper(), context);
		}

		public void handleMessage(Message message) {
			ComponentName componentName = (ComponentName) message.getData().getParcelable("component");
			int previewResId = message.arg1;
			final ImageView imageView = (ImageView) message.obj;
			Resources res = null;
			if (componentName != null) {
				try {
					res = mContext.getPackageManager().getResourcesForActivity(
							componentName);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				res = mContext.getResources();
			}
			final Drawable drawable = res.getDrawable(previewResId);
			mMainHandler.post(new Runnable() {
				public void run() {
					imageView.setImageDrawable(drawable);
				}
			});
		}
	}

	private class WatchFaceInfo {
		ComponentName mComponent;
		boolean isDefault;
		String mName;
		int mPreviewResId;

		public WatchFaceInfo(ComponentName component, int previewResId,
				String name) {
			this.mComponent = component;
			this.mPreviewResId = previewResId;
			this.mName = name;
			if (component == null){
				isDefault = true;
			}
		}
	}

	private static class WatchFaceOption extends FrameLayout {
		private View mClock;
		private View mExampleCard;
		private WatchFaceAdapter.WatchFaceInfo mFace;
		private WatchFaceAdapter mMarshall;

		public WatchFaceOption(Context context,
				WatchFaceAdapter.WatchFaceInfo watchFaceInfo,
				WatchFaceAdapter watchFaceAdapter) {
			super(context);
			mMarshall = watchFaceAdapter;
			LayoutInflater.from(context).inflate(R.layout.watchface_option, this, true);
			setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramView) {
					mMarshall.onOptionClick(WatchFaceAdapter.WatchFaceOption.this);
				}
			});
			setFaceInfo(watchFaceInfo);
			return;
		}

		public void applyStyle() {
		}

		public ComponentName getComponentName() {
			return mFace.mComponent;
		}

		public WatchFaceAdapter.WatchFaceInfo getFaceInfo() {
			return mFace;
		}

		public void setFaceInfo(WatchFaceAdapter.WatchFaceInfo watchFaceInfo) {
			mFace = watchFaceInfo;
			findViewById(R.id.peeking_card).setVisibility(View.GONE);
			((TextView) findViewById(R.id.name)).setText(watchFaceInfo.mName);
			ImageView localImageView = (ImageView) findViewById(R.id.preview);
			Message eessage = Message.obtain(mMarshall.mLoaderHandler, 0,
					watchFaceInfo.mPreviewResId, 0, localImageView);
			eessage.getData().putParcelable("component", watchFaceInfo.mComponent);
			eessage.sendToTarget();
			applyStyle();
		}
	}

}
