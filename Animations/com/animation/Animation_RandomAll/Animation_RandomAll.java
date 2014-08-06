package com.animation.Animation_RandomAll;

public class Animation_RandomAll extends com.animation.Animation.Animation {
	
	public Animation_RandomAll() {
		super ("Random (all)");
	}

	@Override
	public void generatePattern() {
		mPattern.setAllRandom();
	}
}
