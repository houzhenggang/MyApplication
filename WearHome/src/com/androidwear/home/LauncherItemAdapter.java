package com.androidwear.home;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.wearable.view.WearableListView;
import android.support.wearable.view.WearableListView.ViewHolder;
import android.util.Pair;
import android.view.ViewGroup;

public class LauncherItemAdapter extends WearableListView.Adapter {

	private List<ApplicationInfo> mAppList = null;
	private IconCache mIconCache = null;
	private Context mContext = null;

	public LauncherItemAdapter(Context context, IconCache cache) {
		mIconCache = cache;
		mContext = context;
	}

	public void setAllApps(List<ApplicationInfo> apps){
		mAppList = apps;
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return mAppList.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder arg0, int arg1) {
		// TODO Auto-generated method stub
		((LauncherItem) arg0.itemView).setApplicationInfo(getItem(arg1), mIconCache);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		return new WearableListView.ViewHolder(new LauncherItem(
				arg0.getContext()));
	}

	public ApplicationInfo getItem(int postion) {
		return mAppList.get(postion);
	}
}
