package com.animation.Animation;

import java.io.BufferedReader;
import java.io.FileReader;

import com.animation.Pattern.Pattern;
import com.rpi.Settings.Settings;
import com.rpi.Utils.Utils;

public abstract class Animation {
	
	private static final String SettingsFilePath = Settings.RootPath + "/animation/animations";
	private static final String SettingsTokenDelimiter = "=";
	
	private static final String SettingName_BaseDuration1 = "BaseDuration1";
	private static final String SettingName_BaseDuration2 = "BaseDuration2";
	private static final String SettingName_ProbabilityWeight = "ProbabilityWeight";
	private static final String SettingName_TotalDurationWeight = "TotalDurationWeight";
	
	private static final int DefaultBaseDuration = 400;
	private static final int DefaultProbabilityWeight = 1;
	private static final double DefaultDurationWeight = 1.0;
	
	private String mName;
	private int mProbabilityWeight = DefaultProbabilityWeight;
	private double mTotalDurationWeight = DefaultDurationWeight;
	
	protected int mBaseDuration1 = DefaultBaseDuration;
	protected int mBaseDuration2 = DefaultBaseDuration;
	protected Pattern mPattern;
	
	private void parseSetting(String settingLine) {
		String[] tokens = settingLine.split(SettingsTokenDelimiter);
		if (tokens.length == 2) {			
			String settingName = tokens[0];
			String settingValue = tokens[1];
			if (settingName.equals(SettingName_BaseDuration1)) {
				mBaseDuration1 = Integer.parseInt(settingValue);
				
				Utils.Output_WriteLn(true, settingName + "=" + mBaseDuration1);
			}
			else if (settingName.equals(SettingName_BaseDuration2)) {
				mBaseDuration2 = Integer.parseInt(settingValue);
				
				Utils.Output_WriteLn(true, settingName + "=" + mBaseDuration2);
			}
			else if (settingName.equals(SettingName_ProbabilityWeight)) {
				mProbabilityWeight = Integer.parseInt(settingValue);
				
				Utils.Output_WriteLn(true, settingName + "=" + mProbabilityWeight);
			}
			else if (settingName.equals(SettingName_TotalDurationWeight)) {
				mTotalDurationWeight = Double.parseDouble(settingValue);
				
				Utils.Output_WriteLn(true, settingName + "=" + mTotalDurationWeight);
			}
			else {
				Utils.Output_WriteLn(true, "Unrecognized setting name: " + settingName);
			}
		}
	}
	
	private void readSettingsFromFile() {
		BufferedReader br = null;
		try {
			String fileName = SettingsFilePath + "/" + this.getClass().getSimpleName();			
			Utils.Output_WriteLn(true, "Reading animation settings from file: " + fileName);
			
			br = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = br.readLine()) != null) {
				parseSetting(line);
			}
		}
		catch (Exception e) {
			Utils.Output_WriteLn(true, e.getMessage());
		}
		finally {
			if (br != null) {
				try {
					br.close();
				}
				catch (Exception ex) {
					
				}
			}
		}
	}
	
	public Animation(String name) {
		mName = name;
		mBaseDuration1 = DefaultBaseDuration;
		mBaseDuration2 = DefaultBaseDuration;
		mProbabilityWeight = DefaultProbabilityWeight;
		mTotalDurationWeight = DefaultDurationWeight;
		mPattern = new Pattern();
		
		readSettingsFromFile(); //Override the default settings with the settings read from file
	}	
	
	//Get the description of the animation
	public String getName() {
		return mName;
	}
	
	//Get the current pattern of the animation
	public Pattern getPattern() {
		return mPattern;
	}
	
	//Get the probability weight of the animation
	public int getProbabilityWeight() {
		return mProbabilityWeight;
	}
	
	//Get the total duration weight of the animation
	public double getTotalDurationWeight() {
		return mTotalDurationWeight;
	}
	
	//Get the duration of the animation
	public int getDuration() {
		return mBaseDuration1;
	}
	
	//Override this to do custom resetting of the animation to its initial state
	//Don't forget to call super::reset()!
	public void reset() {
		mPattern.clear();
	}
	
	//Override this to execute some custom initialization at the beginning of the animation
	public void initialize() {		
	}
	
	//Override this to create an animation which has fixed duration
	public boolean isDurationFixed() {
		return false;
	}	
	
	//Override this to create animations which only say that they are finished after certain cycles
	public boolean isFinished() {
		return true;
	}	

	//Override this to generate the animation-specific pattern
	public abstract void generatePattern();
}
