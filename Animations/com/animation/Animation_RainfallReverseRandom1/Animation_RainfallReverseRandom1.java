package com.animation.Animation_RainfallReverseRandom1;

import java.util.Random;

import com.animation.AnimationUtils.AnimationUtils;
import com.animation.Pattern.Pattern;

public class Animation_RainfallReverseRandom1 extends com.animation.Animation.Animation {

	private Random mRandomGenerator = null;
	private int mRowInd = Pattern.RowCount;
	private int mColInd = 0;
	
	public Animation_RainfallReverseRandom1() {
		super ("Rainfall (reverse, random, 1)");
	
		mRandomGenerator = new Random();
		reset();
	}
	
	@Override
	public void reset () {
		super.reset();
	
		mRowInd = Pattern.RowCount;
		mColInd = mRandomGenerator.nextInt(Pattern.MinRowLength);
	}
	
	@Override
	public boolean isFinished() {
		return (mRowInd == Pattern.RowCount);
	}

	@Override
	public void generatePattern() {
		if (mRowInd < Pattern.RowCount) {
			mPattern.set(AnimationUtils.getGroupStartIndex(mRowInd) + mColInd, false);
		}
		else {
			mPattern.set(AnimationUtils.getGroupStartIndex(0) + mColInd, false);
			
			mColInd = mRandomGenerator.nextInt(Pattern.MinRowLength);
		}
		
		--mRowInd;		
		
		mPattern.set(AnimationUtils.getGroupStartIndex(mRowInd) + mColInd, true);
		
		if (mRowInd == 0) {
			mRowInd = Pattern.RowCount;			
		}
	}
}
