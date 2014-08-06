package com.usermanagement.User;

import com.rpi.rpi_gpio_controller.Rpi_gpio_controllerApplication;
import com.usermanagement.LoggedInUserManager.LoggedInUserManager;
import com.usermanagement.UserData.UserData;
import com.usermanagement.UserDataManager.UserDataManager;

public class User {
	private UserData mData = null;
	private Rpi_gpio_controllerApplication mApplication = null;
	
	public User(UserData data, Rpi_gpio_controllerApplication application) {
		mData = data;
		mApplication = application;
	}
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof User))return false;
	    User otherUser = (User)other;
	    return (getID().equals(otherUser.getID()));
	}
	
	@Override
	public int hashCode() {
        return getID().hashCode();
    }
	
	public UserData getData() {
		return mData;
	}
	
	public void setData(UserData data) {
		mData = data;
	}
	
	public Rpi_gpio_controllerApplication getApplication() {
		return mApplication;
	}
	
	public void setApplication(Rpi_gpio_controllerApplication application) {
		mApplication = application;
	}
	
	public String getID() {
		return mData.getName() + "-" + mApplication.getID().toString();
	}
	
	public String login() {
		String retVal = UserDataManager.INSTANCE.login(mData.getName(), mData.getPassword());
		if (retVal == null) {
			mData = UserDataManager.INSTANCE.getUserData(mData.getName());
			retVal = LoggedInUserManager.INSTANCE.login(this);
			if (retVal == null) {
				mApplication.login(this);
			}
		}
		
		return retVal;
	}
	
	public void logout() {
		LoggedInUserManager.INSTANCE.logout(this);
		mApplication.logout(this);
	}
}