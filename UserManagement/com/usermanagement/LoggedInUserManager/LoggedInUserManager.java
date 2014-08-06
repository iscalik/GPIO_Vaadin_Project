package com.usermanagement.LoggedInUserManager;

import java.util.ArrayList;

import com.usermanagement.User.User;
import com.usermanagement.UserData.UserData;

public enum LoggedInUserManager {
	INSTANCE;
	
	private ArrayList<User> mLoggedInUsers = null;
	
	private int findFirstLoggedInUserOfType(String type) {
		int idx = -1;
		
		int i = 0;
		while ((idx < 0) && (i < mLoggedInUsers.size())) {
			if (mLoggedInUsers.get(i).getData().getType().equals(type)) {
				idx = i;
			}
			else {
				++i;
			}
		}
		
		return idx;
	}
	
	private int findFirstLoggedInSingleLoginUser() {
		int idx = -1;
		
		int i = 0;
		while ((idx < 0) && (i < UserData.UserTypeCount)) {
			if (UserData.isUserTypeSingleLogin(UserData.UserTypes[i])) {
				idx = findFirstLoggedInUserOfType(UserData.UserTypes[i]);
			}
			++i;
		}
		
		return idx;
	}
	
	private LoggedInUserManager() {
		mLoggedInUsers = new ArrayList<User>();
	}

	public String login(User user) {
		String retVal = null;
		User userToLogOut = null;
		
		if (UserData.isUserTypeSingleLogin(user.getData().getType())) {
			int idx = findFirstLoggedInSingleLoginUser();
			if (idx >= 0) {
				User loggedInUser = mLoggedInUsers.get(idx);
				int userImportance = UserData.getUserTypeImportance(user.getData().getType());
				int loggedInUserImportance = UserData.getUserTypeImportance(loggedInUser.getData().getType());
				if (userImportance > loggedInUserImportance) {
					userToLogOut = loggedInUser;
				}
				else if (userImportance == loggedInUserImportance) {
					if (loggedInUser.getData().equals(user.getData())) {
						userToLogOut = loggedInUser;
					}
					else {
						retVal = new String ("Logging in as " + user.getData().getType() + " is not permitted because another user (" + loggedInUser.getData().getName() + ") with higher or equal importance is already logged in.");
					}
				}
				else {
					retVal = new String ("Logging in as " + user.getData().getType() + " is not permitted because another user (" + loggedInUser.getData().getName() + ") with higher or equal importance is already logged in.");
				}
			}
		}
		
		if (retVal == null) {
			mLoggedInUsers.add(user);
			
			if (userToLogOut != null) {
				logoutFromApplication(userToLogOut, user);				
			}
		}
		
		return retVal;
	}
	
	public void logout(User user) {
		mLoggedInUsers.remove (user);
	}
	
	public void logoutFromApplication(User user, User kicker) {
		try {
			logout(user);
			user.getApplication().logout (kicker);
		}
		catch(Exception ex) {
			
		}
	}
	
	public void logAllOut(User kicker) {
		while (mLoggedInUsers.size () > 0) {
			logoutFromApplication(mLoggedInUsers.get(0), kicker);
		}
		mLoggedInUsers.clear();
	}
	
	public void logAllOutExcept(User user, User kicker) {
		int n = mLoggedInUsers.size ();
		for (int i = 0; i < n; ++i) {
			User curUser = mLoggedInUsers.get(i);
			if (!curUser.equals(user)) {
				logoutFromApplication(mLoggedInUsers.get(i), kicker);
				--i;
				--n;
			}
		}
	}
	
	public ArrayList<User> getLoggedInUsers() {
		return mLoggedInUsers;
	}
}
