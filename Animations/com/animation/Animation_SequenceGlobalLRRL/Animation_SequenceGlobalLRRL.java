package com.animation.Animation_SequenceGlobalLRRL;

import com.animation.Pattern.Pattern;

public class Animation_SequenceGlobalLRRL extends com.animation.Animation.Animation {

	private int mCurrentIndex = -1;
	private int mDirection = 1;
	
	public Animation_SequenceGlobalLRRL() {
		super ("Sequence (global, LR RL)");
		
		reset();
	}
	
	@Override
	public void reset() {
		super.reset();
		
		mCurrentIndex = -1;
		mDirection = 1;
	}
	
	@Override
	public boolean isFinished() {
		return ((mCurrentIndex <= 0) || (mCurrentIndex == Pattern.BitCount - 1));
	}

	@Override
	public void generatePattern() {
		if (mCurrentIndex >= 0) {
			mPattern.set(mCurrentIndex, false);
		}
		
		mCurrentIndex += mDirection;
		
		mPattern.set(mCurrentIndex, true);
		
		if (mCurrentIndex == 0) {
			mDirection = 1;
		}
		else if (mCurrentIndex == Pattern.BitCount - 1) {
			mDirection = -1;
		}		
	}
}