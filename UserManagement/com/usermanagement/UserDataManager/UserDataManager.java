package com.usermanagement.UserDataManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import com.rpi.Settings.Settings;
import com.rpi.Utils.Utils;
import com.usermanagement.UserData.UserData;

public enum UserDataManager {
	INSTANCE;
	
	private static final String UserDataFileName = Settings.RootPath + "/users/userdata.txt";
	private static final String DataTokenDelimiter = ":";
	
	private static final String LoginError_IncorrectUserName = "No such user: ";
	private static final String LoginError_IncorrectPassword = "The entered password is incorrect.";
	
	private ArrayList<UserData> mDefinedUsers = null;
	
	private void readDefinedUsersFromFile() {
		BufferedReader br = null;
		try {
			Utils.Output_WriteLn(true, "Reading user data from file: " + UserDataFileName);
			
			br = new BufferedReader(new FileReader(UserDataFileName));
			String line;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(DataTokenDelimiter);
				if (tokens.length == 3) {
					UserData userData = new UserData (tokens[0], tokens[1], tokens[2]);
					int idx = mDefinedUsers.indexOf(userData);
					if (idx < 0) {
						mDefinedUsers.add (userData);
					}
					else {
						mDefinedUsers.get(idx).setPassword(tokens[1]);
						mDefinedUsers.get(idx).setType(tokens[2]);
					}
				}
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
	
	private void initDefinedUsers() {
		mDefinedUsers.clear();
		
		readDefinedUsersFromFile();
	}
	
	private UserDataManager() {
		mDefinedUsers = new ArrayList<UserData>();
	}
	
	public String login(String userName, String password) {
		String retVal = null;
		
		initDefinedUsers();
		
		UserData user = new UserData(userName);
		int idx = mDefinedUsers.indexOf(user);
		if (idx >= 0) {
			if (!mDefinedUsers.get(idx).getPassword().equals(password)) {
				retVal = LoginError_IncorrectPassword;
			}			
		}
		else {
			retVal = LoginError_IncorrectUserName + userName;
		}		
		
		return retVal;
	}
	
	public UserData getUserData(String userName) {
		UserData userData = null;
		
		int idx = mDefinedUsers.indexOf(new UserData(userName));
		if (idx >= 0) {
			userData = mDefinedUsers.get(idx);
		}
		
		return userData;
	}
}
