package com.animation.Animation_RandomMoverLocal3;

import java.util.Random;

import com.animation.AnimationUtils.AnimationUtils;
import com.animation.Pattern.Pattern;

public class Animation_RandomMoverLocal3 extends com.animation.Animation.Animation {
	
	private int mCurPos1 = -1;
	private int mCurPos2 = -1;
	private int mCurPos3 = -1;
	private Random mRandomGenerator = null;
	
	public Animation_RandomMoverLocal3() {
		super ("Random mover (local, 3)");
		
		mRandomGenerator = new Random();
		
		reset();
	}
	
	@Override
	public void reset() {
		super.reset();
		
		mCurPos1 = -1;
		mCurPos2 = -1;
		mCurPos2 = -3;
	}

	@Override
	public void generatePattern() {		
		if (mCurPos1 >= 0) {
			mPattern.set(mCurPos1, false);
		}
		if (mCurPos2 >= 0) {
			mPattern.set(mCurPos2, false);
		}
		if (mCurPos3 >= 0) {
			mPattern.set(mCurPos3, false);
		}
		
		mCurPos1 = AnimationUtils.generateNextIndexForRandomMover(mRandomGenerator, mCurPos1, Pattern.Row1StartIndex, Pattern.Row1EndIndex);				
		mCurPos2 = AnimationUtils.generateNextIndexForRandomMover(mRandomGenerator, mCurPos2, Pattern.Row2StartIndex, Pattern.Row2EndIndex);
		mCurPos3 = AnimationUtils.generateNextIndexForRandomMover(mRandomGenerator, mCurPos3, Pattern.Row3StartIndex, Pattern.Row3EndIndex);
		
		mPattern.set(mCurPos1, true);
		mPattern.set(mCurPos2, true);
		mPattern.set(mCurPos3, true);
	}
}