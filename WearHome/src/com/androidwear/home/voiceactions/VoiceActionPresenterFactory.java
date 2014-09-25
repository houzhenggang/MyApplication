package com.androidwear.home.voiceactions;

import android.content.Intent;

import com.androidwear.home.cuecard.CueCardAction;

public class VoiceActionPresenterFactory {

	private static final String ACTION_SETTINGS = "android.settings.SETTINGS";

	public static VoiceActionPresenter buildPresenterIfAny(
			VoiceActionContext voiceActionContext, CueCardAction cueCardAction) {
		switch (cueCardAction.mType) {
			case CueCardAction.TYPY_LAUNCHER:
				return new OpenAppActionPresenter(voiceActionContext);
			case CueCardAction.TYPY_SETTINGS:
				return IntentVoiceActionPresenter.withActivity(voiceActionContext,
						new Intent(ACTION_SETTINGS), cueCardAction);
		}
		return null;
	}
}
