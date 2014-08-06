package com.animation.Animation_AllAlternateGroupsUD;

import com.animation.AnimationUtils.AnimationUtils;

public class Animation_AllAlternateGroupsUD extends com.animation.Animation.Animation {

	private int mGroupInd = AnimationUtils.LastGroupIndex;
	
	public Animation_AllAlternateGroupsUD() {
		super ("All (alternate groups, UD)");
		
		reset();
	}
	
	@Override
	public void reset () {
		super.reset();
		
		mGroupInd = AnimationUtils.LastGroupIndex;
	}
	
	@Override
	public boolean isFinished() {
		return (mGroupInd == AnimationUtils.LastGroupIndex);
	}

	@Override
	public void generatePattern() {		
		AnimationUtils.setGroup(mPattern, mGroupInd, false);
		
		++mGroupInd;
		if (mGroupInd > AnimationUtils.LastGroupIndex) {
			mGroupInd = AnimationUtils.FirstGroupIndex;
		}
		
		AnimationUtils.setGroup(mPattern, mGroupInd, true);
	}
}
