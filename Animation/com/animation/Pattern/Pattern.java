package com.animation.Pattern;

import java.util.ArrayList;
import java.util.Random;

import com.rpi.GPIOController.GPIOController;

public class Pattern {

	public static final int BitCount = GPIOController.GPIOPinCount;
	public static final int Row1StartIndex = 0;
	public static final int Row1EndIndex = 4;
	public static final int Row2StartIndex = 5;
	public static final int Row2EndIndex = 10;
	public static final int Row3StartIndex = 11;
	public static final int Row3EndIndex = 16;
	public static final int MinRowLength = 5;
	public static final int MaxRowLength = 6;
	public static final int RowCount = 3;
	public static final int ColumnCount = 6;
	public static final int ColumnStep12 = 5;
	public static final int ColumnStep23 = 6;
	
	
	private boolean[] mBits = null;

	public Pattern() {
		mBits = new boolean[BitCount];
		clear ();
	}
	
	public ArrayList<Boolean> getBits() {
		ArrayList<Boolean> bits = new ArrayList<Boolean>();
		
		for (int i = 0; i < BitCount; ++i) {
			bits.add(mBits[i]);
		}
		
		return bits;
	}
	
	public boolean get(int index) {
		return mBits[index];
	}
	
	public void set(int index, boolean value) {
		mBits[index] = value;
	}
	
	public void clear() {
		for (int i = 0; i < BitCount; ++i) {
			mBits[i] = false;
		}
	}
	
	public void toggle() {
		for (int i = 0; i < BitCount; ++i) {
			mBits[i] = !mBits[i];
		}
	}
	
	public void setRandom(int count) {
		Random randomGenerator = new Random();
		for (int i = 0; i < count; ++i) {
			int ind = randomGenerator.nextInt(BitCount);
			mBits[ind] = true;
		}
	}
	
	public void setAllRandom() {
		Random randomGenerator = new Random();
		for (int i = 0; i < BitCount; ++i) {
			mBits[i]  = randomGenerator.nextBoolean();
		}
	}
}
