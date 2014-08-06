package com.animation.Animation_SequenceParallelLocalLR3;

import com.animation.Pattern.Pattern;

public class Animation_SequenceParallelLocalLR3 extends com.animation.Animation.Animation {

	private int mCurrentIndex = -1;
	
	public Animation_SequenceParallelLocalLR3() {
		super ("Sequence (parallel, local, LR, 3)");
		
		reset();
	}
	
	@Override
	public void reset() {
		super.reset();
		
		mCurrentIndex = -1;
	}
	
	@Override
	public boolean isFinished() {
		return (mCurrentIndex < 0);
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
		else {
			mPattern.set(Pattern.Row1EndIndex, false);
			mPattern.set(Pattern.Row2EndIndex, false);
			mPattern.set(Pattern.Row3EndIndex, false);
		}
		
		++mCurrentIndex;		
		
		if (Pattern.Row1StartIndex + mCurrentIndex <= Pattern.Row1EndIndex) {
			mPattern.set(Pattern.Row1StartIndex + mCurrentIndex, true);
		}
		if (Pattern.Row2StartIndex + mCurrentIndex <= Pattern.Row2EndIndex) {
			mPattern.set(Pattern.Row2StartIndex + mCurrentIndex, true);
		}
		if (Pattern.Row3StartIndex + mCurrentIndex <= Pattern.Row3EndIndex) {
			mPattern.set(Pattern.Row3StartIndex + mCurrentIndex, true);
		}
		
		if (mCurrentIndex == Pattern.MaxRowLength - 1) {
			mCurrentIndex = -1;
		}
	}
}
