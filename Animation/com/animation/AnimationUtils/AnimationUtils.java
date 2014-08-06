package com.animation.AnimationUtils;

import java.util.Random;

import com.animation.Pattern.Pattern;

public class AnimationUtils {
	
	public static final int FirstGroupIndex = 0;
	public static final int LastGroupIndex = 2;
	
	public static int getGroupStartIndex(int groupIndex) {
		int startInd = -1;
		
		switch(groupIndex) {
			case 0:
				startInd = Pattern.Row1StartIndex;
				break;
			case 1:
				startInd = Pattern.Row2StartIndex;
				break;
			case 2:
				startInd = Pattern.Row3StartIndex;
				break;
		}
		
		return startInd;
	}
	
	public static int getGroupEndIndex(int groupIndex) {
		int startInd = -1;
		
		switch(groupIndex) {
			case 0:
				startInd = Pattern.Row1EndIndex;
				break;
			case 1:
				startInd = Pattern.Row2EndIndex;
				break;
			case 2:
				startInd = Pattern.Row3EndIndex;
				break;
		}
		
		return startInd;
	}
	
	public static void setGroup(Pattern pattern, int groupIndex, boolean value) {
		int startInd = getGroupStartIndex(groupIndex);
		int endInd = getGroupEndIndex(groupIndex);
		
		for (int i = startInd; i<= endInd; ++i) {
			pattern.set(i, value);
		}
	}
	
	public static int generateNextIndexForRandomMover(Random randomGenerator, int currentIndex, int lowerBound, int upperBound) {
		int nextIndex = -1;
		
		if (currentIndex < 0) {
			nextIndex = lowerBound + randomGenerator.nextInt(upperBound - lowerBound + 1);
		}
		else {
			int move = randomGenerator.nextInt(3);
			nextIndex = currentIndex - (move-1);
			if (nextIndex < lowerBound) {
				nextIndex = lowerBound;
			}
			else if (nextIndex > upperBound) {
				nextIndex = upperBound;
			}
		}
		
		return nextIndex;
	}
}
