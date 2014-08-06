package com.animation.AutomationThread;

import com.animation.AutomationManager.AutomationManager;

public class AutomationThread extends Thread {
	
	private static final int DefaultSleepDuration = 60000; //1 minute
	
	private volatile boolean mRun = true;
	private volatile int mSleepDuration = DefaultSleepDuration;
	private volatile boolean mSynchronizeWithMinuteStart = true;
	
	public static AutomationThread startNewThread(int sleepDuration, boolean synchronizeWithMinuteStart) {
		AutomationThread thread = new AutomationThread();
		thread.mSleepDuration = sleepDuration;
		thread.mSynchronizeWithMinuteStart = synchronizeWithMinuteStart;
		thread.start();
		
		return thread;
	}
	
	public AutomationThread() {
		mRun = true;
		mSleepDuration = DefaultSleepDuration;
		mSynchronizeWithMinuteStart = true;
	}
	
	@Override
	public void run() { 
		if (mSynchronizeWithMinuteStart) {
			//Suspend the thread until the beginning of the next minute
			try {
				Thread.sleep(60000 - System.currentTimeMillis() % 60000);
			}
			catch(InterruptedException e){
			}
		}
		
		while (mRun) {
			AutomationManager.INSTANCE.runActions();
			
			//Suspend the thread for the specified time period
			try {
				Thread.sleep(mSleepDuration);
			}
			catch(InterruptedException e){
			}
		}		
	}
	
	public void setStopped() {
		mRun = false;
	}
}
