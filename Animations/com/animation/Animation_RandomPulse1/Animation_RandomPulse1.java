package com.animation.Animation_RandomPulse1;

public class Animation_RandomPulse1 extends com.animation.Animation.Animation {
	
	private boolean mIsOn = true;
	
	public Animation_RandomPulse1() {
		super ("Random pulse (1)");
		
		reset();
	}
	
	@Override
	public void reset () {
		super.reset();
		
		mIsOn = true;
	}
	
	@Override
	public boolean isDurationFixed() {
		return true;
	}

	@Override
	public void generatePattern() {
		if (mIsOn) {
			mPattern.setRandom(1);
		}
		else {
			mPattern.clear();
		}
		
		mIsOn = !mIsOn;
	}
	
	@Override
	public int getDuration() {
		return (mIsOn ? mBaseDuration2 : mBaseDuration1);
	}
}
