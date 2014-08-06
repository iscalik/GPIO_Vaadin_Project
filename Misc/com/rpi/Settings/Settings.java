package com.rpi.Settings;

import java.io.BufferedReader;
import java.io.FileReader;

import com.rpi.Utils.Utils;

public enum Settings {
	INSTANCE;
	
	private static final long DefaultPulseDuration = 1000;
	private static final long DefaultBlinkDuration = 500;
	private static final long DefaultBlinkTotalDuration = 5000;
	private static final String DefaultFeedURL = "localhost";
	private static final String DefaultMJPGStreamerPath = "/soft/mjpg-streamer";
	
	private long mPulseDuration = DefaultPulseDuration;
	private long mBlinkDuration = DefaultBlinkDuration;
	private long mBlinkTotalDuration = DefaultBlinkTotalDuration;
	private String mFeedURL = DefaultFeedURL;
	private String mMJPGStreamerPath = DefaultMJPGStreamerPath;
	
	public static final String RootPath = "/var/lib/tomcat7/webapps/RPI_GPIO_Controller_Data";
	
	public static final int FeedPortNumber = 8084;
	public static final String FeedURLFile = RootPath + "/FeedURL.txt";
	public static final String MJPGStreamerPathFile = RootPath + "/MJPGStreamer.txt";
	public static final String ScriptsPath = RootPath + "/scripts";
	
	private void ReadFeedURLFromFile() {	
		BufferedReader br = null;
		try {
			Utils.Output_WriteLn(true, "Reading feed URL from file: " + FeedURLFile);
			
			br = new BufferedReader(new FileReader(FeedURLFile));
			String line = br.readLine();
			if (line != null) {
				mFeedURL = line;
				Utils.Output_WriteLn(true, "Read feed URL: " + mFeedURL);
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
	
	private void ReadMJPGStreamerPathFromFile() {	
		BufferedReader br = null;
		try {
			Utils.Output_WriteLn(true, "Reading MJPG Streamer Path from file: " + MJPGStreamerPathFile);
			
			br = new BufferedReader(new FileReader(MJPGStreamerPathFile));
			String line = br.readLine();
			if (line != null) {
				mMJPGStreamerPath = line;
				Utils.Output_WriteLn(true, "Read MJPG Streamer path: " + mMJPGStreamerPath);
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
	
	private Settings() {
		mPulseDuration = DefaultPulseDuration;
		mBlinkDuration = DefaultBlinkDuration;
		mBlinkTotalDuration = DefaultBlinkTotalDuration;
		mFeedURL = DefaultFeedURL;
		mMJPGStreamerPath = DefaultMJPGStreamerPath;
		
		ReadFeedURLFromFile ();
		ReadMJPGStreamerPathFromFile();
	}
	
	public long GetPulseDuration() {
		return mPulseDuration;
	}
	
	public void SetPulseDuration(long value) {
		mPulseDuration = value;
	}
	
	public long GetBlinkDuration() {
		return mBlinkDuration;
	}
	
	public void SetBlinkDuration(long value) {
		mBlinkDuration = value;
	}
	
	public long GetBlinkTotalDuration() {
		return mBlinkTotalDuration;
	}
	
	public void SetBlinkTotalDuration(long value) {
		mBlinkTotalDuration = value;
	}
	
	public String GetFeedURL() {
		return mFeedURL;
	}
	
	public String GetMJPGStreamerPath () {
		return mMJPGStreamerPath;
	}
}
