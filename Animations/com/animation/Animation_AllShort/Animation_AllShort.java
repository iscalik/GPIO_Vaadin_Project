package com.animation.Animation_AllShort;

public class Animation_AllShort extends com.animation.Animation.Animation {

	private boolean mIsOn = false;
	
	public Animation_AllShort() {
		super ("All (short)");
		
		reset();
	}
	
	@Override
	public void reset () {
		super.reset();
		
		mIsOn = false;
	}

	@Override
	public void generatePattern() {		
		mPattern.toggle();
		mIsOn = !mIsOn;
	}

	@Override
	public int getDuration() {
		return (mIsOn ? mBaseDuration1 : mBaseDuration2);
	}
}
