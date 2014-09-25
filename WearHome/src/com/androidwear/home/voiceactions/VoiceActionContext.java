package com.androidwear.home.voiceactions;

import android.app.FragmentManager;
import android.content.Context;

public abstract interface VoiceActionContext {
	public abstract void dismiss();

	public abstract void displayMessageAndFinish(String msg,
			boolean finish);

	public abstract void finish(boolean finish);

	public abstract Context getApplicationContext();

	public abstract FragmentManager getFragmentManager();

	public abstract void showAllApps();
}
