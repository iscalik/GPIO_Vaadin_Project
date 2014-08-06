package com.animation.Animation_SnakeGlobalRL;

import com.animation.Pattern.Pattern;

public class Animation_SnakeGlobalRL extends com.animation.Animation.Animation {

	private int mCurrentIndex = Pattern.BitCount - 1;
	private boolean mCurrentValue = true;
	
	public Animation_SnakeGlobalRL() {
		super ("Snake (global, RL)");
		
		reset();		
	}
	
	@Override
	public void reset () {
		super.reset();
		
		mCurrentIndex = Pattern.BitCount - 1;
		mCurrentValue = true;
	}
	
	@Override
	public boolean isFinished() {
		return (mCurrentIndex == Pattern.BitCount - 1);
	}

	@Override
	public void generatePattern() {		
		mPattern.set(mCurrentIndex, mCurrentValue);
		
		--mCurrentIndex;				
		if (mCurrentIndex <= 0) {
			mCurrentIndex = Pattern.BitCount - 1;
			mCurrentValue = !mCurrentValue;
		}
	}
}
