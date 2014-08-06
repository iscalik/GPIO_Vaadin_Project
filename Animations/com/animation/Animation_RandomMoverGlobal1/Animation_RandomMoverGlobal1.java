package com.animation.Animation_RandomMoverGlobal1;

import java.util.Random;

import com.animation.AnimationUtils.AnimationUtils;
import com.animation.Pattern.Pattern;

public class Animation_RandomMoverGlobal1 extends com.animation.Animation.Animation {
	
	private int mCurPos = -1;
	private Random mRandomGenerator = null;
	
	public Animation_RandomMoverGlobal1() {
		super ("Random mover (global, 1)");
		
		mRandomGenerator = new Random();
		
		reset();
	}
	
	@Override
	public void reset() {
		super.reset();
		
		mCurPos = -1;
	}

	@Override
	public void generatePattern() {
		if (mCurPos >= 0) {
			mPattern.set(mCurPos, false);
		}
		mCurPos = AnimationUtils.generateNextIndexForRandomMover(mRandomGenerator, mCurPos, 0, Pattern.BitCount - 1);
		mPattern.set(mCurPos, true);
	}
}
