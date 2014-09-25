package com.androidwear.home.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

public class AnimationManager extends AnimatorListenerAdapter {
	private List<Animator> runningAnimations = new ArrayList();

	public void onAnimationEnd(Animator animator) {
		this.runningAnimations.remove(animator);
	}

	public void startAnimation(Animator animator) {
		animator.addListener(this);
		runningAnimations.add(animator);
		animator.start();
	}

	public void stopAllAnimations() {
		Iterator localIterator = this.runningAnimations.iterator();
		while (localIterator.hasNext()) {
			Animator localAnimator = (Animator) localIterator.next();
			localAnimator.removeAllListeners();
			localAnimator.cancel();
		}
		runningAnimations.clear();
	}

}
