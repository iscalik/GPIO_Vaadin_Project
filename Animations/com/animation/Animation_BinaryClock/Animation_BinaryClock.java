package com.animation.Animation_BinaryClock;

import java.util.Calendar;

public class Animation_BinaryClock extends com.animation.Animation.Animation {
	
	private boolean mSecondsOn = true;
	
	public Animation_BinaryClock(boolean secondsOn) {
		super ("Binary clock");
		
		mSecondsOn = secondsOn;
	}
	
	@Override
	public void initialize() {
		//Sleep until the beginning of the next second to start with whole second
		try {
			Thread.sleep ((long)(1000 - System.currentTimeMillis() % 1000));
		}
		catch(InterruptedException e){
		}		
	}
	
	@Override
	public boolean isDurationFixed() {
		return true;
	}

	@Override
	public void generatePattern() {
		Calendar cal = Calendar.getInstance();	    
		int hours = cal.get(Calendar.HOUR_OF_DAY);
		int minutes = cal.get(Calendar.MINUTE);
		int seconds = cal.get(Calendar.SECOND);
		
		//Hours
	    for (int i= 0; i <= 4; ++i) {
	    	mPattern.set(i, ((hours & (1 << (4 - i))) >> (4 - i)) > 0);
	    }

	    //Minutes
	    for (int i = 5; i <= 10; ++i) {
	    	mPattern.set(i, ((minutes & (1 << (10 - i))) >> (10 - i)) > 0);
	    }

	    //Seconds
	    if (mSecondsOn) {
	    	for (int i = 11; i <= 16; ++i) {
	    		mPattern.set(i, ((seconds & (1 << (16 - i))) >> (16 - i)) > 0);
	    	}
	    } else {
	    	for (int i = 11; i<= 16; ++i) {
	    		mPattern.set(i, false);
	    	}
	    }
	}

	@Override
	public int getDuration() {
		return (int)(1000 - System.currentTimeMillis() % 1000); //Milliseconds until the next second
	}

}
