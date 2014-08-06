package com.animation.Animator;

import java.util.ArrayList;
import java.util.Random;

import animation.com.Animation_AllNormal.Animation_AllNormal;

import com.animation.Animation.Animation;
import com.animation.Animation_AllAlternateGroupsDU.Animation_AllAlternateGroupsDU;
import com.animation.Animation_AllAlternateGroupsUD.Animation_AllAlternateGroupsUD;
import com.animation.Animation_AllAlternateGroupsUDDU.Animation_AllAlternateGroupsUDDU;
import com.animation.Animation_AllRandom.Animation_AllRandom;
import com.animation.Animation_AllShort.Animation_AllShort;
import com.animation.Animation_AllToggle.Animation_AllToggle;
import com.animation.Animation_BinaryClock.Animation_BinaryClock;
import com.animation.Animation_RainfallRandom1.Animation_RainfallRandom1;
import com.animation.Animation_RainfallReverseRandom1.Animation_RainfallReverseRandom1;
import com.animation.Animation_Random1.Animation_Random1;
import com.animation.Animation_Random2.Animation_Random2;
import com.animation.Animation_RandomAll.Animation_RandomAll;
import com.animation.Animation_RandomMoverGlobal1.Animation_RandomMoverGlobal1;
import com.animation.Animation_RandomMoverGlobal2.Animation_RandomMoverGlobal2;
import com.animation.Animation_RandomMoverLocal3.Animation_RandomMoverLocal3;
import com.animation.Animation_RandomPulse1.Animation_RandomPulse1;
import com.animation.Animation_SequenceGlobalLR.Animation_SequenceGlobalLR;
import com.animation.Animation_SequenceGlobalLRRL.Animation_SequenceGlobalLRRL;
import com.animation.Animation_SequenceGlobalRL.Animation_SequenceGlobalRL;
import com.animation.Animation_SequenceLocalLRRL3.Animation_SequenceLocalLRRL3;
import com.animation.Animation_SequenceParallelLocalLR3.Animation_SequenceParallelLocalLR3;
import com.animation.Animation_SequenceParallelLocalLRRL3.Animation_SequenceParallelLocalLRRL3;
import com.animation.Animation_SequenceParallelLocalRL3.Animation_SequenceParallelLocalRL3;
import com.animation.Animation_ShortSnakeGlobalLRRL.Animation_ShortSnakeGlobalLRRL;
import com.animation.Animation_SnakeGlobalLR.Animation_SnakeGlobalLR;
import com.animation.Animation_SnakeGlobalRL.Animation_SnakeGlobalRL;
import com.animation.AnimatorThread.AnimatorThread;
import com.animation.Pattern.Pattern;
import com.rpi.GPIOController.GPIOController;
import com.rpi.Utils.Utils;

public enum Animator {
	INSTANCE;
		
	private static final double DefaultAnimationDelayMultiplier = 1.0;
	private static final double DefaultAnimationDelayMultiplierModifier = 1.35;
	private static final int DefaultMinTotalAnimationDuration = 5000;
	private static final int DefaultMaxTotalAnimationDuration = 15000;
	private static final int DefaultMinSpeedRandomizationPercent = 25;
	private static final int DefaultMaxSpeedRandomizationPercent = 175;
	
	private volatile boolean mIsStopped = true;
	private volatile boolean mIsForceStopped = false;
	private boolean mIsRunning = false;
	private volatile int mTotalAnimationDuration = 0;
	private volatile int mMinTotalAnimationDuration = DefaultMinTotalAnimationDuration;
	private volatile int mMaxTotalAnimationDuration = DefaultMaxTotalAnimationDuration;
	private volatile double mAnimationDelayMultiplier = DefaultAnimationDelayMultiplier;
	private volatile long mAnimationStartTime = 0;
	private volatile int mAnimationIndex = -1;
	private volatile boolean mAlternateAnimations = true;
	private volatile boolean mRandomizeSpeed = true;
	private volatile double mSpeedModifier = 1.0;
	
	private volatile Animation mCurrentAnimation = null;
	private volatile ArrayList<Animation> mRegisteredAnimations = null;
	private volatile ArrayList<Animation> mRegisteredAnimationsWithProbabilityWeightMultiplicities = null;
	
	private Pattern mCurrentPattern = null;
	private Random mRandomGenerator = null;
	
	private volatile AnimatorThread mAnimatorThread = null;
	
	private Animator() {
		mIsStopped = true;
		mIsForceStopped = false;
		mIsRunning = false;
		mTotalAnimationDuration = 0;
		mMinTotalAnimationDuration = DefaultMinTotalAnimationDuration;
		mMaxTotalAnimationDuration = DefaultMaxTotalAnimationDuration;
		mAnimationDelayMultiplier = DefaultAnimationDelayMultiplier;
		mAnimationStartTime = 0;
		mAnimationIndex = -1;
		mAlternateAnimations = true;
		mRandomizeSpeed = true;
		mSpeedModifier = 1.0;
		
		mCurrentAnimation = null;
		mRegisteredAnimations = new ArrayList<Animation>();
		mRegisteredAnimationsWithProbabilityWeightMultiplicities = new ArrayList<Animation>();
		
		mCurrentPattern = new Pattern();
		GPIOController.INSTANCE.setPinStates(mCurrentPattern.getBits());
		
		mRandomGenerator = new Random();
		
		mAnimatorThread = null;
		
		registerAnimations();
	}
	
	private void registerAnimation(Animation animation) {
		Utils.Output_WriteLn(true, "Registering animation: " + animation.getName());
		
		mRegisteredAnimations.add(animation);
		for (int i = 0; i < animation.getProbabilityWeight(); ++i) {
			mRegisteredAnimationsWithProbabilityWeightMultiplicities.add (animation);
		}
	}
	
	private void registerAnimations() {
		mRegisteredAnimations.clear ();
		mRegisteredAnimationsWithProbabilityWeightMultiplicities.clear();
		
		registerAnimation(new Animation_BinaryClock(true));
		registerAnimation(new Animation_Random1());
		registerAnimation(new Animation_Random2());
		registerAnimation(new Animation_RandomAll());
		registerAnimation(new Animation_RandomMoverGlobal1());
		registerAnimation(new Animation_RandomMoverGlobal2());
		registerAnimation(new Animation_RandomMoverLocal3());
		registerAnimation(new Animation_SequenceGlobalLR());
		registerAnimation(new Animation_SequenceGlobalRL());
		registerAnimation(new Animation_SequenceGlobalLRRL());
		registerAnimation(new Animation_SequenceLocalLRRL3());
		registerAnimation(new Animation_SequenceParallelLocalLR3());
		registerAnimation(new Animation_SequenceParallelLocalRL3());
		registerAnimation(new Animation_SequenceParallelLocalLRRL3());
		registerAnimation(new Animation_SnakeGlobalLR());
		registerAnimation(new Animation_SnakeGlobalRL());
		registerAnimation(new Animation_ShortSnakeGlobalLRRL());
		registerAnimation(new Animation_AllNormal());
		registerAnimation(new Animation_AllShort());
		registerAnimation(new Animation_AllRandom());
		registerAnimation(new Animation_AllToggle());
		registerAnimation(new Animation_AllAlternateGroupsUD());
		registerAnimation(new Animation_AllAlternateGroupsDU());
		registerAnimation(new Animation_AllAlternateGroupsUDDU());
		registerAnimation(new Animation_RainfallRandom1());
		registerAnimation(new Animation_RainfallReverseRandom1());
		registerAnimation(new Animation_RandomPulse1());
	}
	
	private synchronized void setCurrentAnimationIndex(int index) {
		mAnimationIndex = index;
		mAlternateAnimations = (mAnimationIndex < 0);
	}
	
	private void setPattern(Pattern pattern) {
		if (mCurrentPattern != null) {
			if (pattern != null) {
				for (int i = 0; i < Pattern.BitCount; ++i) {
					boolean desiredPinState = pattern.get(i);
					if (mCurrentPattern.get(i) != desiredPinState) {
						GPIOController.INSTANCE.setPinState(i, desiredPinState);
						mCurrentPattern.set(i, desiredPinState);
					}
				}
			}
			else {
				GPIOController.INSTANCE.setAllLow();
				mCurrentPattern.clear();
			}
		}
	}
	
	private int findRegisteredAnimation(Animation animation) {
		int ind = -1;
		
		int i = 0;
		while ((ind < 0) && (i < mRegisteredAnimations.size())) {
			if (mRegisteredAnimations.get(i) == animation) {
				ind = i;
			}
			else {
				++i;
			}
		}
		
		return ind;
	}
	
	private int findRegisteredAnimationByName(String name) {
		int ind = -1;
		
		int i = 0;
		while ((ind < 0) && (i < mRegisteredAnimations.size())) {
			if (mRegisteredAnimations.get(i).getName().equals(name)) {
				ind = i;
			}
			else {
				++i;
			}
		}		
		
		return ind;
	}
	
	private void runAnimation() {		
		//Determine the running time of the animation
		if ((mMinTotalAnimationDuration > 0) || (mMaxTotalAnimationDuration > 0)) {
			if (mMinTotalAnimationDuration < mMaxTotalAnimationDuration) {
				mTotalAnimationDuration = mMinTotalAnimationDuration + mRandomGenerator.nextInt(mMaxTotalAnimationDuration - mMinTotalAnimationDuration);
			}
			else {
				mTotalAnimationDuration = mMinTotalAnimationDuration;
			}
			
			mTotalAnimationDuration *= mCurrentAnimation.getTotalDurationWeight();			
		}
		else {
			mTotalAnimationDuration = 0; //Run forever
		}
		
		//Determine the speed randomization modifier
		if (mRandomizeSpeed) {
			mSpeedModifier = (double)((DefaultMinSpeedRandomizationPercent + mRandomGenerator.nextInt(DefaultMaxSpeedRandomizationPercent - DefaultMinSpeedRandomizationPercent + 1))) / 100.0;
		}
		
		//Reset animation
		mCurrentAnimation.reset();
		
		//Execute custom animation initialization
		mCurrentAnimation.initialize();
		
		//Execute the cycles of the animation
		while (	(!mIsForceStopped) &&
				(!mCurrentAnimation.isFinished() || (!mIsStopped && ((mTotalAnimationDuration == 0.0) || ((int)(System.currentTimeMillis() - mAnimationStartTime) < mTotalAnimationDuration))))) {
			
			//Generate the animation's pattern (swallow possible exceptions from buggy animations)
			try {
				mCurrentAnimation.generatePattern();
			}
			catch(Exception e) {
			}
			
			//Send the current pattern to the hardware
			setPattern (mCurrentAnimation.getPattern());			
			
			//Sleep until the next cycle
			int sleepTime = mCurrentAnimation.getDuration();
			if (!mCurrentAnimation.isDurationFixed()) {
				sleepTime *= mAnimationDelayMultiplier;
				sleepTime *= mSpeedModifier;
			}
			
			//Suspend the thread until the next cycle
			try {
				Thread.sleep((long)sleepTime);
			}
			catch(InterruptedException e){
			}
		}
	}
	
	private void runAnimations() {		
		long animationCount = 0;
		while (!mIsForceStopped && !mIsStopped && (mAlternateAnimations || (animationCount == 0))) {			
			setPattern(null);
			
			if (mAlternateAnimations) {
				int animationIndex = mAnimationIndex;
				while (animationIndex == mAnimationIndex) {
					animationIndex = mRandomGenerator.nextInt(mRegisteredAnimationsWithProbabilityWeightMultiplicities.size());
					animationIndex = findRegisteredAnimation(mRegisteredAnimationsWithProbabilityWeightMultiplicities.get(animationIndex));
				}
				mAnimationIndex = animationIndex;
			}
			mCurrentAnimation = mRegisteredAnimations.get(mAnimationIndex);
			
			mAnimationStartTime = System.currentTimeMillis();
			
			runAnimation();
			
			++animationCount;
		}
		
		mIsStopped = true;
		mIsForceStopped = false;
		mIsRunning = false;
		mAnimationStartTime = 0;
		mTotalAnimationDuration = 0;
		mCurrentAnimation = null;
		mSpeedModifier = 1.0;
		
		setPattern(null);
	}
	
	//This method should be called only by the animator thread
	public void animate() {
		if (mIsStopped) {
			mIsStopped = false;
			mIsForceStopped = false;
			mIsRunning = true;
			
			runAnimations();
			
			mAnimatorThread = null;
		}
	}
	
	public synchronized void refreshAnimationSettings() {
		forceStop(true);
		registerAnimations();
	}
	
	public synchronized ArrayList<Animation> getRegisteredAnimations() {
		return mRegisteredAnimations;
	}
	
	public synchronized int getMinTotalAnimationDuration() {
		return mMinTotalAnimationDuration;
	}
	
	public synchronized int getMaxTotalAnimationDuration() {
		return mMaxTotalAnimationDuration;
	}
	
	public synchronized int getTotalAnimationDuration() {
		return mTotalAnimationDuration;
	}
	
	//Setting both the minimum and the maximum duration to 0 means run animation forever
	public synchronized void setTotalAnimationDuration(int minTotalAnimationDuration, int maxTotalAnimationDuration) {
		if ((minTotalAnimationDuration >= 0) && (maxTotalAnimationDuration >= 0) &&
			(minTotalAnimationDuration <= maxTotalAnimationDuration)) {
			mMinTotalAnimationDuration = minTotalAnimationDuration;
			mMaxTotalAnimationDuration = maxTotalAnimationDuration;
		}
	}
	
	public synchronized long getCurrentAnimationStartTime() {
		return mAnimationStartTime;
	}
	
	public synchronized void decreaseSpeed() {
		mAnimationDelayMultiplier *= DefaultAnimationDelayMultiplierModifier;
	}
	
	public synchronized void increaseSpeed() {
		mAnimationDelayMultiplier /= DefaultAnimationDelayMultiplierModifier;
	}
	
	public synchronized Animation getCurrentAnimation() {
		return ((mAnimationIndex >= 0) ? mRegisteredAnimations.get(mAnimationIndex) : null);
	}
	
	public synchronized void setCurrentAnimationByName(String currentAnimationName) {
		setCurrentAnimationIndex(findRegisteredAnimationByName(currentAnimationName));
	}
	
	public synchronized String getCurrentAnimationName() {
		Animation currentAnimation = getCurrentAnimation();
		return ((currentAnimation != null) ? currentAnimation.getName() : "");
	}
	
	public synchronized double getCurrentAnimationProgress() {
		double progress = 0.0;
		
		if (mTotalAnimationDuration > 0) {
			progress = ((double)(System.currentTimeMillis() - mAnimationStartTime)) / mTotalAnimationDuration;
			if (progress > 1.0) {
				progress = 1.0;
			}
		}
		
		return progress;
	}
	
	public synchronized boolean getRandomizeSpeed() {
		return mRandomizeSpeed;
	}
	
	public synchronized void setRandomizeSpeed(boolean value) {
		mRandomizeSpeed = value;
	}
	
	public synchronized double getSpeedModifier() {
		return mSpeedModifier;
	}
	
	public synchronized boolean isRunning() {
		return mIsRunning;
	}
	
	public synchronized void start() {
		forceStop(true);
		
		if (mIsStopped && (mAnimatorThread == null)) {
			mAnimatorThread = new AnimatorThread();
			mAnimatorThread.setPriority(Thread.MAX_PRIORITY - 1);
			mAnimatorThread.start();
		}
	}
	
	public synchronized void stop(boolean waitFor) {
		if (!mIsStopped && !mIsForceStopped && (mAnimatorThread != null)) {			
			mIsStopped = true;
			
			if (waitFor) {
				try {
					mAnimatorThread.join(); //Wait for the thread to finish
				}
				catch(InterruptedException e) {
				}
			}
		}
	}
	
	public synchronized void forceStop(boolean waitFor) {
		if (!mIsForceStopped && (mAnimatorThread != null)) {
			mIsForceStopped = true;
			
			if (waitFor) {
				try {
					mAnimatorThread.join(); //Wait for the thread to finish
				}
				catch(InterruptedException e) {
				}
			}
		}
	}
}
