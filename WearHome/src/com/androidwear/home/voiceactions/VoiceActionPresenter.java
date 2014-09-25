package com.androidwear.home.voiceactions;

import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.androidwear.home.R;
import com.androidwear.home.cuecard.CueCardAction;

public abstract class VoiceActionPresenter {
	public static final int ACTION_TYPE_ACTIVITY = 0;
	public static final int ACTION_TYPE_BROADCAST = 2;
	public static final int ACTION_TYPE_SERVICE = 1;

	private String mActionSuccessMessage;
	protected VoiceActionContext mContext;
	protected CueCardAction mCueCardAction;

	public VoiceActionPresenter(VoiceActionContext voiceActionContext) {
		this(voiceActionContext, null);
	}

	public VoiceActionPresenter(VoiceActionContext voiceActionContext,
			CueCardAction cueCardAction) {
		mContext = voiceActionContext;
		mCueCardAction = cueCardAction;
	}

	public void start() {

	}

	protected boolean sendIntent(Intent intent, int type) {
		Context context = mContext.getApplicationContext();
		if (type == ACTION_TYPE_ACTIVITY) {
			try {
				if(intent.getComponent() != null){
					String packageName = intent.getComponent().getPackageName();
					if(packageName != null || packageName.equals(mContext.getApplicationContext().getPackageName())){
						int flgs = intent.getFlags();
						if ((flgs & Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED) != 0) {
							flgs &= ~ Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
						}
						intent.setFlags(flgs);
					}else{
						intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					}
				}else{
					intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				}
				context.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				mContext.displayMessageAndFinish(
						context.getString(R.string.voice_action_remote_error),
						false);
				return false;
			}
		} else if (type == ACTION_TYPE_SERVICE) {
			context.startService(intent);
		} else if (type == ACTION_TYPE_BROADCAST) {
			context.sendBroadcast(intent);
		} else {
			throw new IllegalArgumentException(
					"Don't know how to send intent of type " + type);
		}
		return true;
	}

	protected List<ResolveInfo> queryIntentProcessors(Intent intent, int type) {
		PackageManager pm = mContext.getApplicationContext()
				.getPackageManager();
		if (type == ACTION_TYPE_ACTIVITY) {
			return pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		} else if (type == ACTION_TYPE_BROADCAST) {
			return pm.queryBroadcastReceivers(intent, PackageManager.MATCH_DEFAULT_ONLY);
		} else if (type == ACTION_TYPE_SERVICE) {
			return pm.queryIntentServices(intent, PackageManager.MATCH_DEFAULT_ONLY);
		} else {
			throw new IllegalStateException("Unknown type of intent: " + type);
		}
	}

	protected boolean sendIntentIfPossible(Intent intent, int type) {
		List list = queryIntentProcessors(intent, type);
		if (list.size() == 0) {
			return false;
		}
		intent.putExtra("action_title", getActionTitle());
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		return sendIntent(intent, type);
	}

	private String getActionTitle() {
		if ((mCueCardAction != null) && (mCueCardAction.mTextResId != 0)) {
			return mContext.getApplicationContext().getResources()
					.getString(mCueCardAction.mTextResId);
		}
		return null;
	}
}
