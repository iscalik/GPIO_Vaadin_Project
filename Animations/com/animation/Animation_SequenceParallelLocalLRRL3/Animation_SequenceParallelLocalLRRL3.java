package com.animation.Animation_SequenceParallelLocalLRRL3;

import com.animation.Pattern.Pattern;

public class Animation_SequenceParallelLocalLRRL3 extends com.animation.Animation.Animation {

	private int mCurrentIndex = -1;
	private int mDirection = 1;
	
	public Animation_SequenceParallelLocalLRRL3() {
		super ("Sequence (parallel, local, LR RL, 3)");
		
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
		return ((mCurrentIndex <= 0) || (mCurrentIndex >= Pattern.MaxRowLength));
	}

	@Override
	public void generatePattern() {
		if (mCurrentIndex >= 0) {
			if (Pattern.Row1StartIndex + mCurrentIndex <= Pattern.Row1EndIndex) {
				mPattern.set(Pattern.Row1StartIndex + mCurrentIndex, false);
			}
			if (Pattern.Row2StartIndex + mCurrentIndex <= Pattern.Row2EndIndex) {
				mPattern.set(Pattern.Row2StartIndex + mCurrentIndex, false);
			}
			if (Pattern.Row3StartIndex + mCurrentIndex <= Pattern.Row3EndIndex) {
				mPattern.set(Pattern.Row3StartIndex + mCurrentIndex, false);
			}
		}
		
		mCurrentIndex += mDirection;		
		
		if (Pattern.Row1StartIndex + mCurrentIndex <= Pattern.Row1EndIndex) {
			mPattern.set(Pattern.Row1StartIndex + mCurrentIndex, true);
		}
		if (Pattern.Row2StartIndex + mCurrentIndex <= Pattern.Row2EndIndex) {
			mPattern.set(Pattern.Row2StartIndex + mCurrentIndex, true);
		}
		if (Pattern.Row3StartIndex + mCurrentIndex <= Pattern.Row3EndIndex) {
			mPattern.set(Pattern.Row3StartIndex + mCurrentIndex, true);
		}
		
		if (mCurrentIndex == 0) {
			mDirection = 1;
		}
		else if (mCurrentIndex == Pattern.MaxRowLength - 1) {
			mDirection = -1;
		}
	}
}