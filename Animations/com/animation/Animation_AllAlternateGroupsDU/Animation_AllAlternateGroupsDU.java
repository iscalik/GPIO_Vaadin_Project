package com.animation.Animation_AllAlternateGroupsDU;

import com.animation.AnimationUtils.AnimationUtils;

public class Animation_AllAlternateGroupsDU extends com.animation.Animation.Animation {

	private int mGroupInd = AnimationUtils.FirstGroupIndex;
	
	public Animation_AllAlternateGroupsDU() {
		super ("All (alternate groups, DU)");
		
		reset();
	}
	
	@Override
	public void reset () {
		super.reset();
		
		mGroupInd = AnimationUtils.FirstGroupIndex;
	}
	
	@Override
	public boolean isFinished() {
		return (mGroupInd == AnimationUtils.FirstGroupIndex);
	}

	@Override
	public void generatePattern() {		
		AnimationUtils.setGroup(mPattern, mGroupInd, false);
		
		--mGroupInd;
		if (mGroupInd < AnimationUtils.FirstGroupIndex) {
			mGroupInd = AnimationUtils.LastGroupIndex;
		}
		
		AnimationUtils.setGroup(mPattern, mGroupInd, true);
	}
}
