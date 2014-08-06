package com.animation.Animation_SequenceGlobalLR;

import com.animation.Pattern.Pattern;

public class Animation_SequenceGlobalLR extends com.animation.Animation.Animation {

	private int mCurrentIndex = -1;
	
	public Animation_SequenceGlobalLR() {
		super ("Sequence (global, LR)");
		
		reset();
	}
	
	@Override
	public void reset () {
		super.reset();
		
		mCurrentIndex = -1;
	}
	
	@Override
	public boolean isFinished() {
		return (mCurrentIndex < 0);
	}

	@Override
	public void generatePattern() {		
		mPattern.set((mCurrentIndex >= 0) ? mCurrentIndex : Pattern.BitCount - 1, false);
		
		++mCurrentIndex;		
		
		mPattern.set(mCurrentIndex, true);
		
		if (mCurrentIndex == Pattern.BitCount - 1) {
			mCurrentIndex = -1;
		}
	}
}
