package com.androidwear.home.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceUtils {

	private final static String PACKAGE_NAME = "com.androidwear.home";
	private final static String SHARED_PREFERENCE = PACKAGE_NAME + "_preferences";
	private final static String KEY_WATCHFACE = "watchface.current_face";

	static public void saveWatchFace(Context context, ComponentName watchFace) {
		synchronized (PreferenceUtils.class) {
			SharedPreferences sp = context.getSharedPreferences(
					SHARED_PREFERENCE, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString(KEY_WATCHFACE, watchFace != null ? watchFace.flattenToString() : null);
			editor.commit();
		}
	}

	public static ComponentName getWatchFace(Context context){
		String str = context.getSharedPreferences(SHARED_PREFERENCE,
				Context.MODE_PRIVATE).getString(KEY_WATCHFACE, null);
		if(str != null){
			return ComponentName.unflattenFromString(str);
		}else{
			return null;
		}
	}
}
