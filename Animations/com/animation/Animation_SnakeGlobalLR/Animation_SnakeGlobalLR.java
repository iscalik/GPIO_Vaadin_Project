package com.animation.Animation_SnakeGlobalLR;

import com.animation.Pattern.Pattern;

public class Animation_SnakeGlobalLR extends com.animation.Animation.Animation {

	private int mCurrentIndex = 0;
	private boolean mCurrentValue = true;
	
	public Animation_SnakeGlobalLR() {
		super ("Snake (global, LR)");
		
		reset();
	}
	
	@Override
	public void reset () {
		super.reset();
		
		mCurrentIndex = 0;
		mCurrentValue = true;
	}
	
	@Override
	public boolean isFinished() {
		return (mCurrentIndex == 0);
	}

	@Override
	public void generatePattern() {		
		mPattern.set(mCurrentIndex, mCurrentValue);
		
		++mCurrentIndex;				
		if (mCurrentIndex >= Pattern.BitCount) {
			mCurrentIndex = 0;
			mCurrentValue = !mCurrentValue;
		}
	}
}
