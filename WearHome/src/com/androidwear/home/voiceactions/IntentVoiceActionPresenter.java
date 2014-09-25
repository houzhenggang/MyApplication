package com.androidwear.home.voiceactions;

import com.androidwear.home.cuecard.CueCardAction;

import android.content.Intent;

public class IntentVoiceActionPresenter extends VoiceActionPresenter {
	private final int mActionType;
	private final Intent mOriginalIntent;

	protected IntentVoiceActionPresenter(
			VoiceActionContext voiceActionContext, Intent paramIntent,
			CueCardAction cueCardAction, int type) {
		super(voiceActionContext, cueCardAction);
		mOriginalIntent = paramIntent;
		mActionType = type;
	}

	public static IntentVoiceActionPresenter withActivity(
			VoiceActionContext voiceActionContext, Intent intent,
			CueCardAction cueCardAction) {
		return new IntentVoiceActionPresenter(voiceActionContext,
				intent, cueCardAction, VoiceActionPresenter.ACTION_TYPE_ACTIVITY);
	}

	public static IntentVoiceActionPresenter withBroadcast(
			VoiceActionContext voiceActionContext, Intent intent,
			CueCardAction cueCardAction) {
		return new IntentVoiceActionPresenter(voiceActionContext,
				intent, cueCardAction, VoiceActionPresenter.ACTION_TYPE_BROADCAST);
	}

	protected boolean sendIntent(Intent intent, int type) {
		boolean bool = super.sendIntent(intent, type);
		if (bool){
			mContext.finish(true);
		}
		return bool;
	}

	public void start() {
		sendIntentIfPossible(mOriginalIntent, mActionType);
	}
}
