package com.animation.AutomationManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.animation.Animator.Animator;
import com.animation.AutomationThread.AutomationThread;
import com.rpi.Settings.Settings;
import com.rpi.Utils.Utils;

public enum AutomationManager {
	INSTANCE();
	
	private enum Actions {
		Unknown,
		Start,
		Stop,
		SetAnimation,
		SetTotalDuration,
	}
	
	private static final String ActionName_Start = "Start";
	private static final String ActionName_Stop = "Stop";
	private static final String ActionName_SetAnimation = "SetAnimation";
	private static final String ActionName_SetTotalDuration = "SetTotalDuration";
	
	private static final String AutomationFileName = Settings.RootPath + "/animation/automation.txt";
		
	private static final String MainTokenDelimiter = " # ";
	private static final String SecondaryTokenDelimiter = "=";
	private static final String ParameterTokenDelimiter = ",";
	private static final String TimeTokenDelimiter = ":";
	
	private static final int AutomationIntervalMilliSec = 60000; //1 minute
	
	private volatile boolean mEnabled = true;
	private volatile ArrayList<Integer> mTimes = null;
	private volatile ArrayList<Actions> mActions = null;
	private volatile ArrayList<String> mActionParameters = null;
	
	private AutomationManager() {
		mEnabled = true;
		mTimes = new ArrayList<Integer>();
		mActions = new ArrayList<Actions>();
		mActionParameters = new ArrayList<String>();
		
		
		readAutomationFromFile();
		
		AutomationThread.startNewThread(AutomationIntervalMilliSec, true);
	}
	
	private int parseTime(String timeString) {
		int t = -1;
		
		String[] tokens = timeString.split(TimeTokenDelimiter);
		if (tokens.length == 2) {
			t = Integer.parseInt(tokens[0]) * 60;
			t += Integer.parseInt(tokens[1]);
		}
		
		return t;
	}
	
	private Actions parseAction(String actionString) {
		Actions action = Actions.Unknown;
		
		if (actionString.equals(ActionName_Start)) {
			action = Actions.Start;
		}
		else if (actionString.equals(ActionName_Stop)) {
			action = Actions.Stop;
		}
		else if (actionString.equals(ActionName_SetAnimation)) {
			action = Actions.SetAnimation;
		}
		else if (actionString.equals(ActionName_SetTotalDuration)) {
			action = Actions.SetTotalDuration;
		}
		
		return action;
	}
	
	private void parseAutomationLine(String automationLine) {
		String[] tokens = automationLine.split(MainTokenDelimiter);
		if (tokens.length == 2) {
			int time = parseTime(tokens[0]);
			if (time >= 0) {
				String[] actionTokens = tokens[1].split(SecondaryTokenDelimiter);
				if ((actionTokens.length == 1) || (actionTokens.length == 2)) {
					Actions action = parseAction(actionTokens[0]);
					if (action != Actions.Unknown) {
						String actionParam = "";
						if (actionTokens.length > 1) {
							actionParam = actionTokens[1];
						}
						
						mTimes.add(time);
						mActions.add(action);
						mActionParameters.add(actionParam);
						
						Utils.Output_WriteLn(true, "Time=" + tokens[0] + " Action=" + actionTokens[0] + " Parameters=" + actionParam);
					}
					else {
						Utils.Output_WriteLn(true, "Unknown automation action: " + actionTokens[0]);
					}
				}
				else {
					Utils.Output_WriteLn(true, "Unable to parse automation action: " + tokens[1]);
				}
			}
			else {
				Utils.Output_WriteLn(true, "Unable to parse automation time: " + tokens[0]);
			}
		}
		else {
			Utils.Output_WriteLn(true, "Unable to parse automation line: " + automationLine);
		}
	}
	
	private void readAutomationFromFile() {
		BufferedReader br = null;
		try {
			Utils.Output_WriteLn(true, "Reading automation from file: " + AutomationFileName);
			
			br = new BufferedReader(new FileReader(AutomationFileName));
			String line;
			while ((line = br.readLine()) != null) {
				parseAutomationLine(line);				
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
	
	private void runAction(Actions action, String params) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		Utils.Output_Write(true, dateFormat.format(cal.getTime()) + ": ");
		
		switch (action) {
			case Unknown:
				Utils.Output_WriteLn(false, "Automation action: Start unknown");
				
				break;
			case Start:
				Utils.Output_WriteLn(false, "Automation action: Start animation");
				
				if (!Animator.INSTANCE.isRunning()) {					
					Animator.INSTANCE.start();
				}
				break;
			case Stop:
				Utils.Output_WriteLn(false, "Automation action: Stop animation");
				
				if (Animator.INSTANCE.isRunning()) {					
					Animator.INSTANCE.stop(true);
				}
				break;
			case SetAnimation:
				Utils.Output_WriteLn(false, "Automation action: Set animation to: " + params);
				
        		Animator.INSTANCE.setCurrentAnimationByName(params);
				break;
			case SetTotalDuration:
				Utils.Output_Write(false, "Automation action: Set total animation duration to: ");
				
				String[] tokens = params.split(ParameterTokenDelimiter);
				if (tokens.length == 2) {
					int minTotalAnimationDuration = Integer.parseInt(tokens[0]);
					int maxTotalAnimationDuration = Integer.parseInt(tokens[1]);
					if ((minTotalAnimationDuration >= 0) && (maxTotalAnimationDuration >= 0) && (minTotalAnimationDuration <= maxTotalAnimationDuration)) {
						Utils.Output_WriteLn(false, "MinTotalAnimationDuration=" + minTotalAnimationDuration + ", MaxTotalAnimationDuration=" + maxTotalAnimationDuration);
						
						Animator.INSTANCE.setTotalAnimationDuration(minTotalAnimationDuration * 1000, maxTotalAnimationDuration * 1000);
					}
					else {
						Utils.Output_WriteLn(false, "Invalid values: MinTotalAnimationDuration, MaxTotalAnimationDuration");
					}
				}
				else {
					Utils.Output_WriteLn(false, "Unable to parse the parameters");
				}
				break;
		}
	}
	
	public synchronized boolean isEnabled() {
		return mEnabled;
	}
	
	public synchronized void setEnabled(boolean value) {
		mEnabled = value;
	}
	
	public synchronized void refresh() {
		readAutomationFromFile();
	}
	
	public synchronized void runActions() {
		if (mEnabled) {
			Calendar calendar = Calendar.getInstance();
			int currentTime = calendar.get(Calendar.HOUR_OF_DAY) * 60;
			currentTime += calendar.get(Calendar.MINUTE);
			
			int actionCount = mActions.size();
			if ((actionCount == mTimes.size()) && (actionCount == mActionParameters.size ())) {
				for (int i = 0; i <  actionCount; ++i) {
					if (mTimes.get(i) == currentTime) {
						runAction(mActions.get(i), mActionParameters.get(i));
					}
				}
			}
		}
	}
}
