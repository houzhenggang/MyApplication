package com.androidwear.home;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.support.wearable.view.WearableListView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidwear.home.view.SwipeDismissFrameLayout;

public class LauncherFragment extends Fragment implements
		SwipeDismissFrameLayout.DismissCallbacks {
	private final static String TAG = "LauncherFragment";

	private WearableListView mWheel = null;
	private LauncherItemAdapter mAdapter = null;
	private HomeActivity mHomeActivity = null;

	private void removeFragment() {
		FragmentManager localFragmentManager = getFragmentManager();
		if (localFragmentManager != null) {
			FragmentTransaction localFragmentTransaction = localFragmentManager
					.beginTransaction();
			localFragmentTransaction.remove(this);
			localFragmentTransaction.commitAllowingStateLoss();
		} else {
			Log.w(TAG, "FragmentManager is null.");
		}
	}

	public void onActivityCreated(Bundle bundle) {
		super.onActivityCreated(bundle);
		mHomeActivity = (HomeActivity)getActivity();
		HomeApplication app = (HomeApplication)getActivity().getApplication();
		this.mAdapter = new LauncherItemAdapter(getActivity(), app.getIconCache());
		mAdapter.setAllApps(mHomeActivity.getAllApps());
		mWheel.setAdapter(this.mAdapter);
		mWheel.setClipChildren(false);
		mWheel.setClickListener(new WearableListView.ClickListener() {

			@Override
			public void onClick(ViewHolder holder) {
				ApplicationInfo info = mAdapter.getItem(holder.getPosition());
				startActivitySafely(info.intent, null);
			}

			@Override
			public void onTopEmptyRegionClick() {
				// TODO Auto-generated method stub

			}

		});
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle bundle) {
		return inflater.inflate(R.layout.launcher_fragment, parent, false);
	}

	public void onViewCreated(View paramView, Bundle bundle){
	    super.onViewCreated(paramView, bundle);
	    mWheel = (WearableListView)paramView.findViewById(R.id.app_list);
	}

	public void onPause() {
		super.onPause();
		removeFragment();
	}

	@Override
	public boolean canDismiss() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onDismissed(SwipeDismissFrameLayout layout) {
		// TODO Auto-generated method stub
		removeFragment();
	}

	@Override
	public void onSwipeCancelled() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSwipeStart() {
		// TODO Auto-generated method stub

	}

	public void setAllApps(List<ApplicationInfo> apps){
		if(mAdapter != null){
			mAdapter.setAllApps(apps);
		}
	}

	private boolean startActivitySafely(Intent intent, Object tag) {
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			startActivity(intent);
			return true;
		} catch (ActivityNotFoundException e) {
			Log.e(TAG, "Unable to launch. tag=" + tag + " intent=" + intent, e);
		} catch (SecurityException e) {

		}
		return false;
	}
}
