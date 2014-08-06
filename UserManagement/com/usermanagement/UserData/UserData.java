package com.usermanagement.UserData;

public class UserData {
	
	public static final String USER_TYPE_ADMIN = "admin";
	public static final String USER_TYPE_CONTROLLER = "controller";
	public static final String USER_TYPE_VIEWER = "viewer";
	
	public static final int UserTypeCount = 3;
	public static final String[] UserTypes = {USER_TYPE_ADMIN, USER_TYPE_CONTROLLER, USER_TYPE_VIEWER};
	
	public static final int USER_RIGHT_MASK_VIEWER = 1;
	public static final int USER_RIGHT_MASK_CONTROLLER = 2;
	public static final int USER_RIGHT_MASK_USERMANAGER = 4;
	
	private String mName;
	private String mPassword;
	private String mType;
	
	public static int getUserTypeImportance(String type) {
		int importance = -1;
		
		if (type.equals(USER_TYPE_ADMIN)) {
			importance = 2;
		}
		if (type.equals(USER_TYPE_CONTROLLER)) {
			importance = 1;
		}
		if (type.equals(USER_TYPE_VIEWER)) {
			importance = 0;
		}
		
		return importance;
	}
	
	public static int getUserTypeRights(String type) {
		int rights = 0;
		
		if (type.equals(USER_TYPE_ADMIN)) {
			rights = USER_RIGHT_MASK_VIEWER | USER_RIGHT_MASK_CONTROLLER | USER_RIGHT_MASK_USERMANAGER;
		}
		if (type.equals(USER_TYPE_CONTROLLER)) {
			rights = USER_RIGHT_MASK_VIEWER | USER_RIGHT_MASK_CONTROLLER;
		}
		if (type.equals(USER_TYPE_VIEWER)) {
			rights = USER_RIGHT_MASK_VIEWER;
		}
		
		return rights;
	}
	
	public static boolean isUserTypeSingleLogin(String type) {
		return (type.equals(UserData.USER_TYPE_CONTROLLER) ||
				type.equals(UserData.USER_TYPE_ADMIN));
	}
	
	public UserData() {
		mName = "";
		mPassword = "";
		mType = "";
	}
	
	public UserData(String name) {
		mName = name;
		mPassword = "";
		mType = "";
	}
	
	public UserData(String name, String password) {
		mName = name;
		mPassword = password;
		mType = "";
	}
	
	public UserData(String name, String password, String type) {
		mName = name;
		mPassword = password;
		mType = type;
	}
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof UserData))return false;
	    UserData otherUserData = (UserData)other;
	    return (mName.equals(otherUserData.mName));
	}
	
	@Override
	public int hashCode() {
        return mName.hashCode();
    }
	
	public String getName() {
		return mName;
	}
	
	public void setName (String name) {
		mName = name;
	}
	
	public String getPassword() {
		return mPassword;
	}
	
	public void setPassword (String password) {
		mPassword = password;
	}
	
	public String getType() {
		return mType;
	}
	
	public void setType (String type) {
		mType = type;
	}
}