package com.androidwear.home.cuecard;

import java.util.ArrayList;
import java.util.List;

import com.androidwear.home.R;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.wearable.view.WearableListView;
import android.support.wearable.view.WearableListView.ViewHolder;
import android.view.ViewGroup;

import com.androidwear.home.VoicePlateFragment;
import com.androidwear.home.voiceactions.VoiceActionContext;
import com.androidwear.home.voiceactions.VoiceActionPresenter;
import com.androidwear.home.voiceactions.VoiceActionPresenterFactory;

public class CueCardActionAdapter extends WearableListView.Adapter implements VoicePlateFragment.OnItemClickedListener{
	private VoiceActionContext mVoiceActionContext = null;
	private List<CueCardAction> mActions = null;
	private boolean mOffline = false;

	public CueCardActionAdapter(VoiceActionContext context) {
		mVoiceActionContext = context;
		mActions = createOnlineActions(mVoiceActionContext.getApplicationContext().getResources(),
				mVoiceActionContext.getApplicationContext().getPackageManager());
	}

	public static List<CueCardAction> createOnlineActions(Resources res,
			PackageManager pm) {
		List<CueCardAction> list = new ArrayList<CueCardAction>();
		list.add(new CueCardAction(CueCardAction.TYPE_AGENDA, R.drawable.ic_cc_agenda, R.string.cue_menu_agenda));
		list.add(new CueCardAction(CueCardAction.TYPE_TIMER, R.drawable.ic_cc_timer, R.string.cue_menu_timer));
		list.add(new CueCardAction(CueCardAction.TYPE_STOPWATCH, R.drawable.ic_cc_stopwatch, R.string.cue_menu_stopwatch));
		list.add(new CueCardAction(CueCardAction.TYPE_SETALARM, R.drawable.ic_cc_setalarm, R.string.cue_menu_alarm));
		list.add(new CueCardAction(CueCardAction.TYPE_SHOWALARMS, R.drawable.ic_cc_showalarms, R.string.cue_menu_show_alarms));
		list.add(new CueCardAction(CueCardAction.TYPY_SETTINGS, R.drawable.ic_cc_settings, R.string.cue_menu_settings));
		list.add(new CueCardAction(CueCardAction.TYPY_LAUNCHER, R.drawable.ic_cc_start, R.string.cue_menu_apps));
		return list;
	}

	public CueCardAction getItem(int pos){
	    return mActions.get(pos);
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return mActions.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder arg0, int arg1) {
		// TODO Auto-generated method stub
		((CueCardActionView)arg0.itemView).setCueCardAction(getItem(arg1), this.mOffline);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
		// TODO Auto-generated method stub
		CueCardActionView cardActionView = new CueCardActionView(parent.getContext());
	    int padding = (int)parent.getResources().getDimension(R.dimen.cue_card_action_top_bottom_padding);
	    cardActionView.setPadding(0, padding, 0, padding);
	    cardActionView.setClipToPadding(false);
	    return new WearableListView.ViewHolder(cardActionView);
	}

	@Override
	public void onItemClicked(int postion) {
		CueCardAction cueCardAction = getItem(postion);
	    VoiceActionPresenter vactionPresenter = VoiceActionPresenterFactory.buildPresenterIfAny(mVoiceActionContext, cueCardAction);
	    if (vactionPresenter != null){
	    	vactionPresenter.start();
	    }else{
	    	mVoiceActionContext.displayMessageAndFinish(mVoiceActionContext.getApplicationContext().getString(R.string.voice_action_remote_error), false);
	    }
	}

}
