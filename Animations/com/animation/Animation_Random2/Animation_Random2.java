package com.animation.Animation_Random2;

public class Animation_Random2 extends com.animation.Animation.Animation {
	
	public Animation_Random2() {
		super ("Random (2)");
	}

	@Override
	public void generatePattern() {		
		mPattern.clear();
		mPattern.setRandom(2);
	}
}