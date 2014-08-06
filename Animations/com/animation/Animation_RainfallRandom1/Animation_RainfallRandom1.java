package com.animation.Animation_RainfallRandom1;

import java.util.Random;

import com.animation.AnimationUtils.AnimationUtils;
import com.animation.Pattern.Pattern;

public class Animation_RainfallRandom1 extends com.animation.Animation.Animation {

	private Random mRandomGenerator = null;
	private int mRowInd = -1;
	private int mColInd = 0;
	
	public Animation_RainfallRandom1() {
		super ("Rainfall (random, 1)");
	
		mRandomGenerator = new Random();
		reset();
	}
	
	@Override
	public void reset () {
		super.reset();
	
		mRowInd = -1;
		mColInd = mRandomGenerator.nextInt(Pattern.MinRowLength);
	}
	
	@Override
	public boolean isFinished() {
		return (mRowInd < 0);
	}

	@Override
	public void generatePattern() {
		if (mRowInd >= 0) {
			mPattern.set(AnimationUtils.getGroupStartIndex(mRowInd) + mColInd, false);
		}
		else {
			mPattern.set(AnimationUtils.getGroupStartIndex(Pattern.RowCount - 1) + mColInd, false);
			
			mColInd = mRandomGenerator.nextInt(Pattern.MinRowLength);
		}
		
		++mRowInd;		
		
		mPattern.set(AnimationUtils.getGroupStartIndex(mRowInd) + mColInd, true);
		
		if (mRowInd == Pattern.RowCount - 1) {
			mRowInd = -1;			
		}
	}
}