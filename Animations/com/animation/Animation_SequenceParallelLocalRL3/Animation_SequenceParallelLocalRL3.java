package com.animation.Animation_SequenceParallelLocalRL3;

import com.animation.Pattern.Pattern;

public class Animation_SequenceParallelLocalRL3 extends com.animation.Animation.Animation {

	private int mCurrentIndex = Pattern.MaxRowLength;
	
	public Animation_SequenceParallelLocalRL3() {
		super ("Sequence (parallel, local, RL, 3)");
		
		reset();
	}
	
	@Override
	public void reset() {
		super.reset();
		
		mCurrentIndex = Pattern.MaxRowLength;
	}
	
	@Override
	public boolean isFinished() {
		return (mCurrentIndex == Pattern.MaxRowLength);
	}

	@Override
	public void generatePattern() {
		if (mCurrentIndex < Pattern.MaxRowLength) {
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
			mPattern.set(Pattern.Row1StartIndex, false);
			mPattern.set(Pattern.Row2StartIndex, false);
			mPattern.set(Pattern.Row3StartIndex, false);
		}
		
		--mCurrentIndex;		
		
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
			mCurrentIndex = Pattern.MaxRowLength;
		}
	}
}
