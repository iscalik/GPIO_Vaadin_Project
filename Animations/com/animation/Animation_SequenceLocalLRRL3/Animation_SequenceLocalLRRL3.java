package com.animation.Animation_SequenceLocalLRRL3;

import java.util.Random;

import com.animation.Pattern.Pattern;

public class Animation_SequenceLocalLRRL3 extends com.animation.Animation.Animation {

	private int mCurrentIndex1 = Pattern.Row1StartIndex -1;
	private int mCurrentIndex2 = Pattern.Row2StartIndex -1;
	private int mCurrentIndex3 = Pattern.Row3StartIndex -1;
	private int mDirection1 = 1;
	private int mDirection2 = 1;
	private int mDirection3 = 1;
	
	public Animation_SequenceLocalLRRL3() {
		super ("Sequence (local, LR RL, 3)");

		reset();
	}
	
	@Override
	public void reset() {
		super.reset();
		
		Random randomGenerator = new Random();
		mCurrentIndex1 = Pattern.Row1StartIndex + randomGenerator.nextInt(Pattern.Row1EndIndex - Pattern.Row1StartIndex + 1) - 1;
		mCurrentIndex2 = Pattern.Row2StartIndex + randomGenerator.nextInt(Pattern.Row2EndIndex - Pattern.Row2StartIndex + 1) - 1;
		mCurrentIndex3 = Pattern.Row3StartIndex + randomGenerator.nextInt(Pattern.Row3EndIndex - Pattern.Row3StartIndex + 1) - 1;
		mDirection1 = 1;
		mDirection2 = 1;
		mDirection3 = 1;
	}

	@Override
	public void generatePattern() {	
		if (mCurrentIndex1 >= Pattern.Row1StartIndex) {
			mPattern.set(mCurrentIndex1, false);
		}
		if (mCurrentIndex2 >= Pattern.Row2StartIndex) {
			mPattern.set(mCurrentIndex2, false);
		}
		if (mCurrentIndex3 >= Pattern.Row3StartIndex) {
			mPattern.set(mCurrentIndex3, false);
		}
		
		mCurrentIndex1 += mDirection1;
		mCurrentIndex2 += mDirection2;
		mCurrentIndex3 += mDirection3;
		
		mPattern.set(mCurrentIndex1, true);
		mPattern.set(mCurrentIndex2, true);
		mPattern.set(mCurrentIndex3, true);
		
		if (mCurrentIndex1 == Pattern.Row1StartIndex) {
			mDirection1 = 1;
		}
		else if (mCurrentIndex1 == Pattern.Row1EndIndex) {
			mDirection1 = -1;
		}
		if (mCurrentIndex2 == Pattern.Row2StartIndex) {
			mDirection2 = 1;
		}
		else if (mCurrentIndex2 == Pattern.Row2EndIndex) {
			mDirection2 = -1;
		}
		if (mCurrentIndex3 == Pattern.Row3StartIndex) {
			mDirection3 = 1;
		}
		else if (mCurrentIndex3 == Pattern.Row3EndIndex) {
			mDirection3 = -1;
		}
	}
}