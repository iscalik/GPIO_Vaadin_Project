package com.animation.Animation_Random1;

public class Animation_Random1 extends com.animation.Animation.Animation {
	
	public Animation_Random1() {
		super ("Random (1)");
	}

	@Override
	public void generatePattern() {		
		mPattern.clear();
		mPattern.setRandom(1);
	}
}
