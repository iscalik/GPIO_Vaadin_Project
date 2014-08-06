package com.animation.Animation_ShortSnakeGlobalLRRL;

import java.util.Random;

import com.animation.Pattern.Pattern;

public class Animation_ShortSnakeGlobalLRRL extends com.animation.Animation.Animation {

	private static final int MinSnakeLength = 2;
	private static final int MaxSnakeLength = 5;
	
	private Random mRandomGenerator = null;
	private int mSnakeLength = MinSnakeLength;
	private int mSnakeHeadInd = 0;
	private int[] mSnakeBody = null;
	private int mDirection = 1;
	
	private boolean findSnakeTailInSnakeBody() {
		int snakeTail = mSnakeBody[mSnakeLength - 1];
		boolean notFound = true;
		int i = 0;
		while (notFound && (i < mSnakeLength - 1)) {
			if (mSnakeBody[i] == snakeTail) {
				notFound = false;
			}
			else {
				++i;
			}
		}
		
		return !notFound;
	}
	
	public Animation_ShortSnakeGlobalLRRL() {
		super ("Short snake (global, LR RL)");		
		
		reset();
	}
	
	@Override
	public void reset () {
		super.reset();
		
		mRandomGenerator = new Random();
		mSnakeLength = MinSnakeLength + mRandomGenerator.nextInt (MaxSnakeLength - MinSnakeLength + 1);
		mSnakeHeadInd = 0;
		
		mSnakeBody = new int[mSnakeLength];
		for (int i = 0; i < mSnakeLength; ++i) {
			mSnakeBody[i] = -1;
		}
		
		mDirection = 1;
	}
	
	@Override
	public boolean isFinished() {
		return ((mSnakeHeadInd == 0) || (mSnakeHeadInd == Pattern.BitCount - 1));
	}

	@Override
	public void generatePattern() {
		int snakeTailInd = mSnakeBody[mSnakeLength-1];
		if (snakeTailInd >= 0) {
			if (!findSnakeTailInSnakeBody()) {
				mPattern.set(snakeTailInd, false);
			}
		}
		mPattern.set(mSnakeHeadInd, true);
		
		for (int i = mSnakeLength - 1; i > 0; --i) {
			mSnakeBody[i] = mSnakeBody[i-1];
		}
		mSnakeBody[0] = mSnakeHeadInd;
		
		mSnakeHeadInd += mDirection;		
		
		if ((mSnakeHeadInd == 0) || (mSnakeHeadInd == Pattern.BitCount - 1)) {
			mDirection *= -1;
		}
	}
}
