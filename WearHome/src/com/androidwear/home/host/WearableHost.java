package com.androidwear.home.host;

import java.util.ArrayList;
import java.util.List;

public class WearableHost {
	private static WearableHost sInstance;

	private List<WearableHostConnectionListener> mListeners = null;

	public static WearableHost getInstance() {
		if (sInstance == null) {
			sInstance = new WearableHost();
		}
		return sInstance;
	}

	private WearableHost(){
		mListeners = new ArrayList<WearableHostConnectionListener>();
	}

	public void addConnectionListener(WearableHostConnectionListener listener) {
		mListeners.add(listener);
	}

	public void removeConnectionListener(WearableHostConnectionListener listener) {
		mListeners.remove(listener);
	}

	public void onPeerConnected() {
		for(WearableHostConnectionListener listener : mListeners){
			listener.onPeerConnected();
		}
	}

	public void onPeerDisconnected() {
		for(WearableHostConnectionListener listener : mListeners){
			listener.onPeerDisconnected();
		}
	}
}
