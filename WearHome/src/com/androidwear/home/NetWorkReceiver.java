package com.androidwear.home;

import com.androidwear.home.host.WearableHost;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetWorkReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (Utils.isNetworkConnected(context)) {
			WearableHost.getInstance().onPeerConnected();
		} else {
			WearableHost.getInstance().onPeerDisconnected();
		}
	}

}
