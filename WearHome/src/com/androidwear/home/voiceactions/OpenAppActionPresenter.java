package com.androidwear.home.voiceactions;

public class OpenAppActionPresenter extends VoiceActionPresenter {
	public OpenAppActionPresenter(VoiceActionContext voiceActionContext) {
		super(voiceActionContext);
	}

	public void start() {
		mContext.showAllApps();
	}
}
