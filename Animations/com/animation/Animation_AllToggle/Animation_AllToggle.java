package com.animation.Animation_AllToggle;

import com.animation.Pattern.Pattern;

public class Animation_AllToggle extends com.animation.Animation.Animation {
	
	public Animation_AllToggle() {
		super ("All (toggle)");
		
		reset();
	}
	
	@Override
	public void reset () {
		super.reset();
		
		for (int i = 1; i < Pattern.BitCount; i+=2) {
			mPattern.set(i, true);
		}
	}

	@Override
	public void generatePattern() {		
		mPattern.toggle();
	}
}