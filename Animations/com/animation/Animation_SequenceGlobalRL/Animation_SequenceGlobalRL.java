package com.animation.Animation_SequenceGlobalRL;

import com.animation.Pattern.Pattern;

public class Animation_SequenceGlobalRL extends com.animation.Animation.Animation {

	private int mCurrentIndex = Pattern.BitCount;
	
	public Animation_SequenceGlobalRL() {
		super ("Sequence (global, RL)");
		
		reset();
	}
	
	@Override
	public void reset() {
		super.reset();
		
		mCurrentIndex = Pattern.BitCount;
	}
	
	@Override
	public boolean isFinished() {
		return (mCurrentIndex == Pattern.BitCount);
	}

	@Override
	public void generatePattern() {
		mPattern.set((mCurrentIndex < Pattern.BitCount) ? mCurrentIndex : 0, false);
		
		--mCurrentIndex;
		
		mPattern.set(mCurrentIndex, true);
		
		if (mCurrentIndex == 0) {
			mCurrentIndex = Pattern.BitCount;
		}
	}
}