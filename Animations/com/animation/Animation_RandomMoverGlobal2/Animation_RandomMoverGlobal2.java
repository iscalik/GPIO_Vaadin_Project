package com.animation.Animation_RandomMoverGlobal2;

import java.util.Random;

import com.animation.AnimationUtils.AnimationUtils;
import com.animation.Pattern.Pattern;

public class Animation_RandomMoverGlobal2 extends com.animation.Animation.Animation {
	
	private int mCurPos1 = -1;
	private int mCurPos2 = -1;
	private Random mRandomGenerator = null;
	
	public Animation_RandomMoverGlobal2() {
		super ("Random mover (global, 2)");
		
		mRandomGenerator = new Random();
		
		reset();		
	}
	
	@Override
	public void reset() {
		super.reset();
		
		mCurPos1 = -1;
		mCurPos2 = -1;
	}

	@Override
	public void generatePattern() {		
		if (mCurPos1 >= 0) {
			mPattern.set(mCurPos1, false);
		}
		if (mCurPos2 >= 0) {
			mPattern.set(mCurPos2, false);
		}
		
		mCurPos1 = AnimationUtils.generateNextIndexForRandomMover(mRandomGenerator, mCurPos1, 0, Pattern.BitCount - 1);				
		mCurPos2 = AnimationUtils.generateNextIndexForRandomMover(mRandomGenerator, mCurPos2, 0, Pattern.BitCount - 1);
		
		mPattern.set(mCurPos1, true);
		mPattern.set(mCurPos2, true);
	}
}
