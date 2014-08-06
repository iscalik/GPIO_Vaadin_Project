package com.animation.Animation_AllAlternateGroupsUDDU;

import com.animation.AnimationUtils.AnimationUtils;

public class Animation_AllAlternateGroupsUDDU extends com.animation.Animation.Animation {

	private int mGroupInd = AnimationUtils.FirstGroupIndex;
	private int mDirection = 1;
	
	public Animation_AllAlternateGroupsUDDU() {
		super ("All (alternate groups, UD DU)");
		
		reset();
	}
	
	@Override
	public void reset () {
		super.reset();
		
		mGroupInd = AnimationUtils.FirstGroupIndex;
		mDirection = 1;
	}
	
	@Override
	public boolean isFinished() {
		return ((mGroupInd == AnimationUtils.FirstGroupIndex) || (mGroupInd == AnimationUtils.LastGroupIndex));
	}

	@Override
	public void generatePattern() {		
		AnimationUtils.setGroup(mPattern, mGroupInd, false);
		
		mGroupInd += mDirection;
		
		if ((mGroupInd == AnimationUtils.FirstGroupIndex) || (mGroupInd == AnimationUtils.LastGroupIndex)) {
			mDirection *= -1;
		}		
		
		AnimationUtils.setGroup(mPattern, mGroupInd, true);
	}
}
