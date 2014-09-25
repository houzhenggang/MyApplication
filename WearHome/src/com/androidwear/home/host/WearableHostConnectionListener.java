package com.androidwear.home.host;

public abstract interface WearableHostConnectionListener {
	public abstract void onPeerConnected();

	public abstract void onPeerDisconnected();
}