package com.animation.Animation_AllRandom;

import java.util.Random;

public class Animation_AllRandom extends com.animation.Animation.Animation {

	private Random mRandomGenerator = null;
	
	public Animation_AllRandom() {
		super ("All (random)");
		
		mRandomGenerator = new Random();
	}	

	@Override
	public void generatePattern() {		
		mPattern.toggle();
	}

	@Override
	public int getDuration() {
		return (mBaseDuration1 + mRandomGenerator.nextInt(mBaseDuration2 - mBaseDuration1 + 1));
	}
}
