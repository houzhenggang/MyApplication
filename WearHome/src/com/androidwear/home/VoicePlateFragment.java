package com.androidwear.home;

import com.androidwear.home.cuecard.CueCardActionAdapter;
import com.androidwear.home.host.WearableHost;
import com.androidwear.home.host.WearableHostConnectionListener;
import com.androidwear.home.view.AnimationManager;
import com.androidwear.home.view.SoundLevelImageView;
import com.androidwear.home.view.SwipeUpListener;
import com.androidwear.home.view.SwipeDismissFrameLayout;
import com.androidwear.home.voiceactions.VoiceActionContext;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.wearable.view.SimpleAnimatorListener;
import android.support.wearable.view.WearableListView;
import android.support.wearable.view.WearableListView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class VoicePlateFragment extends Fragment implements VoiceActionContext,
		WearableListView.ClickListener, WearableListView.OnScrollListener,
		SwipeDismissFrameLayout.DismissCallbacks,
		WearableHostConnectionListener {

	private final AnimationManager mAnimationManager = new AnimationManager();
	private boolean mOffline;
	private WearableListView mWearableListView = null;
	private CueCardActionAdapter mAdapter = null;
	private HomeActivity mHomeActivity = null;
	private TextView mFieldName = null;
	private SoundLevelImageView mMicIcon = null;
	private int mFieldNameOfflineHeight;
	private int mFieldNameOnlineHeight;
	private int mMicIconOfflineTopMargin;
	private int mMicIconOnlineTopMargin;
	private TextView mTranscriptionView;
	private ImageView mTopChevron;
	private ImageView mBottomChevron;
	private SwipeUpListener mSwipeUpListener;
	private ObjectAnimator mBreathingAnimation;
	private int mTotalScroll;
	private boolean isMicEnable = true;
	private Runnable mDelayedAction;

	private final Handler mUiHandler = new Handler(Looper.getMainLooper());

	public void onViewCreated(View view, Bundle bundle) {
		super.onViewCreated(view, bundle);
		Resources res = getResources();
		mWearableListView = (WearableListView) view
				.findViewById(R.id.voice_action_list_field);
		mWearableListView.setClipChildren(false);
		mWearableListView.setGreedyTouchMode(false);
		mWearableListView.setVisibility(View.INVISIBLE);
		mFieldName = (TextView) view.findViewById(R.id.field_name);
		mMicIcon = (SoundLevelImageView) view.findViewById(R.id.mic_icon);
		mMicIcon.setVisibility(View.GONE);
		mFieldNameOfflineHeight = (int) res
				.getDimension(R.dimen.voice_plate_field_name_offline_height);
		mFieldNameOnlineHeight = (int) res
				.getDimension(R.dimen.voice_plate_field_name_online_height);
		mMicIconOfflineTopMargin = 0;
		mMicIconOnlineTopMargin = (int) res
				.getDimension(R.dimen.orb_margin_top);
		mTranscriptionView = (TextView) view.findViewById(R.id.transcription);
		mBottomChevron = (ImageView) view.findViewById(R.id.bottom_chevron);
		mTopChevron = (ImageView) view.findViewById(R.id.top_chevron);
		mSwipeUpListener = new SwipeUpListener(getActivity()) {
			protected void onSwipeUp() {
				showWheelOfFortuneWithoutMic(true);
			}
		};
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle bundle) {
		return inflater.inflate(R.layout.cue_card_layout, parent, false);
	}

	public void onActivityCreated(Bundle bundle) {
		super.onActivityCreated(bundle);
		mHomeActivity = (HomeActivity) getActivity();
		mAdapter = new CueCardActionAdapter(this);
		mWearableListView.setAdapter(mAdapter);
		mWearableListView.setClipChildren(false);
		mWearableListView.setClickListener(this);
	}

	public static abstract interface OnItemClickedListener {
		public abstract void onItemClicked(int postion);
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayMessageAndFinish(String msg, boolean finish) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finish(boolean finish) {
		// TODO Auto-generated method stub

	}

	@Override
	public Context getApplicationContext() {
		// TODO Auto-generated method stub
		return getActivity().getApplicationContext();
	}

	@Override
	public void showAllApps() {
		// TODO Auto-generated method stub
		mHomeActivity.showLauncher();
	}

	public void onResume() {
		super.onResume();
		WearableHost.getInstance().addConnectionListener(this);
		mOffline = !Utils.isNetworkConnected(getActivity());
		resetViewsBeforeStartAnimation();
		isMicEnable = !mOffline;
		mUiHandler.post(new Runnable() {
			public void run() {
				resetViewsAfterStartAnimation();
				changeConnectionState(!mOffline);
			}
		});
	}

	private void updateOnlineOfflineLayout() {
		ViewGroup.LayoutParams mHeadLayoutParams = mFieldName.getLayoutParams();
		ViewGroup.MarginLayoutParams mMicMarginLayoutParams = (ViewGroup.MarginLayoutParams) mMicIcon
				.getLayoutParams();
		if (mOffline) {
			mHeadLayoutParams.height = mFieldNameOfflineHeight;
			mMicMarginLayoutParams.topMargin = mMicIconOfflineTopMargin;
			mFieldName.setText(R.string.disconnected);
			mFieldName.setBackgroundColor(getResources().getColor(
					R.color.cw_grey));
			mFieldName.setVisibility(View.VISIBLE);
			mWearableListView.setVisibility(View.VISIBLE);
			mMicIcon.setImageResource(R.drawable.ic_orb_nophone);
			mMicIcon.setTranslationY(mFieldName.getTranslationY());
			mMicIcon.setVisibility(View.VISIBLE);
			mMicIcon.setCircleColor(Color.TRANSPARENT);
			mTranscriptionView.setVisibility(View.INVISIBLE);
		} else {
			mMicIcon.setCircleColor(getResources().getColor(R.color.orb_active));
			mFieldName.setVisibility(View.GONE);
			mHeadLayoutParams.height = mFieldNameOnlineHeight;
			mMicMarginLayoutParams.topMargin = mMicIconOnlineTopMargin;
			mFieldName.setBackgroundColor(getResources().getColor(
					R.color.cw_grey));
			mMicIcon.setTranslationY(getResources().getDimension(
					R.dimen.orb_radius_min));
			mMicIcon.setVisibility(View.VISIBLE);
			mMicIcon.setImageResource(R.drawable.ic_orb_g);
		}
		mFieldName.setLayoutParams(mHeadLayoutParams);
		mMicIcon.setLayoutParams(mMicMarginLayoutParams);
		mTotalScroll = 0;
	}

	private int getFieldNameTransitionY(boolean visible) {
		if (visible) {
			return 0;
		} else {
			return -mFieldNameOnlineHeight;
		}
	}

	private void setFieldNameVisible(boolean visible) {
		mFieldName.setTranslationY(getFieldNameTransitionY(visible));
	}

	private void animateFieldNameVisible(boolean visible) {
		mFieldName.animate().setInterpolator(new DecelerateInterpolator())
				.translationY(getFieldNameTransitionY(visible));
	}

	private Animator createResetMicPositionAnimation() {
		AnimatorSet animatorSet = new AnimatorSet();
		Animator[] animatorArr = new Animator[2];
		animatorArr[0] = ObjectAnimator.ofFloat(mMicIcon, "translationX", 0.0f);
		animatorArr[1] = ObjectAnimator.ofFloat(mMicIcon, "translationY", 0.0f);
		animatorSet.playTogether(animatorArr);
		return animatorSet;
	}

	private int getWheelOfFortuneTransitionY(boolean visible) {
		final View view = getView();
		if (visible || view == null) {
			return 0;
		} else {
			return view.getHeight();
		}
	}

	private void setWheelOfFortuneVisible(boolean visible) {
		mWearableListView
				.setTranslationY(getWheelOfFortuneTransitionY(visible));
	}

	private void animateWheelOfFortuneVisible(boolean visible) {
		setWheelOfFortuneListenersEnabled(visible);
		mWearableListView.animate()
				.setInterpolator(new DecelerateInterpolator())
				.translationY(getWheelOfFortuneTransitionY(visible));
	}

	private void changeConnectionState(final boolean online) {
		mUiHandler.post(new Runnable() {
			@Override
			public void run() {
				updateOnlineOfflineLayout();
				if (online) {
					animateChevronsVisible(false, true);
					if(mDelayedAction == null){
						mDelayedAction = new Runnable() {
							public void run() {
								isMicEnable = false;
								showWheelOfFortuneWithoutMic(true);
							}
						};
					}
					if(isMicEnable){
						mTranscriptionView.setText(R.string.speak_now);
						mTranscriptionView.setVisibility(View.VISIBLE);
						mUiHandler.postDelayed(mDelayedAction, 2500);
						startSpeechAnimation();
					}else{
						showWheelOfFortuneWithoutMic();
					}
				} else {
					if(mDelayedAction != null){
						mUiHandler.removeCallbacks(mDelayedAction);
					}
					isMicEnable = false;
					animateFieldNameVisible(true);
					animateChevronsVisible(false, false);
					animateWheelOfFortuneVisible(true);
					Animator animator = createResetMicPositionAnimation();
					animator.setInterpolator(new DecelerateInterpolator());
					animator.start();
				}
			}
		});
	}

	private void animateChevronVisible(final ImageView imageView,
			boolean visible) {
		ViewPropertyAnimator propertyAnimator = imageView.animate()
				.setDuration(150);
		if (visible) {
			imageView.setVisibility(View.VISIBLE);
			propertyAnimator.alpha(1.0f);
		} else {
			propertyAnimator.alpha(0.0f).setListener(
					new SimpleAnimatorListener() {
						public void onAnimationComplete(Animator paramAnimator) {
							imageView.setVisibility(View.GONE);
						}

						public void onAnimationEnd(Animator paramAnimator) {

						}
					});
		}
	}

	private void animateChevronsVisible(boolean topVisible,
			boolean bottomVisible) {
		animateChevronVisible(mBottomChevron, bottomVisible);
		animateChevronVisible(mTopChevron, topVisible);
		View view = getView();
		if (bottomVisible && view != null) {
			view.setOnTouchListener(mSwipeUpListener);
		}
	}

	private void showWheelOfFortuneWithoutMic(boolean visible) {
		mTranscriptionView.setVisibility(View.GONE);
		setWheelOfFortuneVisible(false);
		mWearableListView.setVisibility(View.VISIBLE);
		animateWheelOfFortuneVisible(true);
		animateChevronsVisible(true, false);
		int tY = -mMicIcon.getTop() - mMicIcon.getHeight();
		mTotalScroll = -tY;
		mMicIcon.animate().translationY(tY)
				.setInterpolator(new AccelerateDecelerateInterpolator())
				.start();
		cancelQuery();
		isMicEnable = false;
		mUiHandler.removeCallbacks(mDelayedAction);		
	}

	private void showWheelOfFortuneWithoutMic() {
		mTranscriptionView.setVisibility(View.GONE);
		animateChevronsVisible(true, false);
		int tY = -mMicIcon.getTop() - mMicIcon.getHeight();
		mTotalScroll = -tY;
		mMicIcon.setTranslationY(tY);
		cancelQuery();
	}

	private void startSpeechAnimation() {
		Animator localAnimator = createResetMicPositionAnimation();
		localAnimator.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator paramAnimator) {
				float f = VoicePlateFragment.this.getResources().getDimension(
						R.dimen.orb_radius_max);
				VoicePlateFragment.this.mMicIcon.startSoundLevelAnimation(
						VoicePlateFragment.this.mAnimationManager, f);
				startBreathing();
			}
		});
		this.mAnimationManager.startAnimation(localAnimator);
	}

	private void setWheelOfFortuneListenersEnabled(boolean enable) {
		if (enable) {
			mWearableListView.addOnScrollListener(this);
			mWearableListView.setClickListener(this);
		} else {
			mWearableListView.removeOnScrollListener(this);
			mWearableListView.setClickListener(null);
		}
	}

	private void cancelQuery() {
		mMicIcon.stopSoundAnimation();
		if (mBreathingAnimation != null) {
			mBreathingAnimation.end();
			mBreathingAnimation = null;
		}
	}

	private void resetViewsAfterStartAnimation() {
		setFieldNameVisible(false);
		setWheelOfFortuneVisible(false);
		mFieldName.setVisibility(View.VISIBLE);
		mWearableListView.setVisibility(View.VISIBLE);
	}

	private void resetViewsBeforeStartAnimation() {
		// mTranscriptionView.setAlpha(0.0F);
		mMicIcon.setAlpha(1.0F);
		mMicIcon.setTranslationX(0.0F);
		mMicIcon.setTranslationY(0.0F);
		mTotalScroll = 0;
		mFieldName.setTranslationY(0.0F);
		mWearableListView.setVisibility(View.GONE);
		mFieldName.setVisibility(View.INVISIBLE);
	}

	private void startBreathing() {
		Resources res = getResources();
		float f1 = res.getDimension(R.dimen.orb_radius_min);
		float f2 = res.getDimension(R.dimen.orb_radius_max);
		float[] arrFloat = new float[3];
		arrFloat[0] = f1;
		arrFloat[1] = f2;
		arrFloat[2] = f1;
		mBreathingAnimation = ObjectAnimator.ofFloat(mMicIcon, "circleRadius",
				arrFloat);
		mBreathingAnimation
				.setInterpolator(new AccelerateDecelerateInterpolator());
		mBreathingAnimation.setDuration(2000L);
		mBreathingAnimation.setRepeatMode(2);
		mBreathingAnimation.setRepeatCount(-1);
		mAnimationManager.startAnimation(mBreathingAnimation);
	}

	private void resendQuery() {
		animateWheelOfFortuneVisible(false);
		mTranscriptionView.setVisibility(View.VISIBLE);
		mMicIcon.setTranslationY(0);
		setWheelOfFortuneVisible(false);
		startBreathing();
		animateChevronsVisible(false, true);
		isMicEnable = true;
		if(mDelayedAction == null){
			mDelayedAction = new Runnable() {
				public void run() {
					isMicEnable = false;
					showWheelOfFortuneWithoutMic(true);
				}
			};
		}
		if(isMicEnable){
			mUiHandler.postDelayed(mDelayedAction, 2500);
		}
	}

	@Override
	public void onAbsoluteScrollChange(int absoluteScroll) {
		if (!mOffline) {
			int h = mTopChevron.getHeight();
			if (absoluteScroll < 0) {
				absoluteScroll = Math.abs(absoluteScroll) / 2;
			}
			float aa = 1.0f - (clamp(0.0f, absoluteScroll, h) / h);
			mTopChevron.setAlpha(aa);
		}
	}

	public static float clamp(float arg1, float arg2, float arg3) {
		if (arg1 > arg2) {
			arg2 = arg1;
		}
		if (arg2 > arg3) {
			arg2 = arg3;
		}
		return arg2;
	}

	@Override
	public void onCentralPositionChanged(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onScroll(int dy) {
		mTotalScroll = (dy + mTotalScroll);
		float f = -clamp(mTotalScroll, 0, mFieldName.getHeight());
		if (mOffline) {
			mFieldName.setTranslationY(f);
		}
		mMicIcon.setTranslationY(f);
	}

	@Override
	public void onScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(ViewHolder arg0) {
		// TODO Auto-generated method stub
		if (mWearableListView.getAdapter() instanceof OnItemClickedListener) {
			((OnItemClickedListener) mWearableListView.getAdapter())
					.onItemClicked(arg0.getPosition());
		}
	}

	@Override
	public void onTopEmptyRegionClick() {
		// TODO Auto-generated method stub
		if (mOffline) {
			return;
		}
		resendQuery();
	}

	@Override
	public boolean canDismiss() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onDismissed(SwipeDismissFrameLayout layout) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSwipeCancelled() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSwipeStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPeerConnected() {
		// TODO Auto-generated method stub
		mOffline = !Utils.isNetworkConnected(getActivity());
		changeConnectionState(!mOffline);
	}

	@Override
	public void onPeerDisconnected() {
		// TODO Auto-generated method stub
		mOffline = !Utils.isNetworkConnected(getActivity());
		changeConnectionState(!mOffline);
	}

	public void onStop() {
		super.onStop();
		WearableHost.getInstance().removeConnectionListener(this);
	}
}
