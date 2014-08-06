package com.rpi.rpi_gpio_controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

import com.usermanagement.LoggedInUserManager.LoggedInUserManager;
import com.usermanagement.User.User;
import com.usermanagement.UserData.UserData;
import com.usermanagement.UserDataManager.UserDataManager;
import com.utils.GreaterThanOrEqualToIntegerValidator.GreaterThanOrEqualToIntegerValidator;
import com.utils.LessThanOrEqualToIntegerValidator.LessThanOrEqualToIntegerValidator;
import com.utils.NonZeroIntegerValidator.NonZeroIntegerValidator;
import com.utils.PositiveIntegerValidator.PositiveIntegerValidator;
import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.Window.Notification;
import com.animation.Animation.Animation;
import com.animation.Animator.Animator;
import com.animation.AutomationManager.AutomationManager;
import com.github.wolfie.refresher.Refresher;
import com.github.wolfie.refresher.Refresher.RefreshListener;
import com.rpi.GPIOController.GPIOController;
import com.rpi.Settings.Settings;
import com.rpi.Utils.Utils;


@SuppressWarnings("serial")
public class Rpi_gpio_controllerApplication extends Application {
	
	private static final int RefreshIntervalMilliSec = 1000;
	
	private static final int FeedWidth=640;
	private static final int FeedHeight=480;
	
	private static final String LoginErrorMessage = "Failed to log in";
	private static final String KickoutMessageTitle = "You have been logged out";
	private static final String KickoutMessageText = "by user ";
	private static final String AnimationStoppedText = "Animation not running.";
	private static final String Tooltip_AutoStartStopCheckBox = "The start/stop times are read from file";
	
	private static final String AnimationName_Random = "RANDOM";
	private static final String AnimationDuration_Infinite = "Infinite";
	
	private UUID mID = UUID.randomUUID();	
	private User mUser = null;
	private User mKicker = null;
	private ArrayList<User> mLoggedInUsers = new ArrayList<User>();
	private ArrayList<Boolean> mGPIOPinStates = null;
	
	private AbstractOrderedLayout mMainContentLayout = null;
	private GridLayout mBasicControlGridLayout = null;
	private AbstractOrderedLayout mFeedLayout = null;
	private GridLayout mUserLayout = null;
	private GridLayout mLoggedInUsersLayout = null;
	private GridLayout mLoggedInUsersManagementLayout = null;
	private NativeSelect mAnimationSelector = null;
	private Label mAnimationStatusLabel = null;
	private ProgressIndicator mAnimationProgressIndicator = null;
	private TextField mMinAnimationDurationTextField = null;
	private TextField mMaxAnimationDurationTextField = null;
	
	private LessThanOrEqualToIntegerValidator mMinAnimationDurationTextFieldComparatorValidator = null;
	private GreaterThanOrEqualToIntegerValidator mMaxAnimationDurationTextFieldComparatorValidator = null;
	
	private Refresher mRefresher = null;
	
	private int getIntValueFromTextField(TextField textField) {
		int v = 0;
		
		Object tfValue = textField.getValue();
		
		if (tfValue instanceof Integer) {
			v = ((Integer)tfValue).intValue();
		}
		else {
			v = Integer.parseInt((String)tfValue);
		}
		
		return v;
	}
	
	private void periodicRefresh() {
		//Logout if requested
		if (mKicker != null) {
    		String kickMessage = KickoutMessageText + mKicker.getData().getName();            		
    		mKicker = null;
    		logoutCore();
    		getMainWindow().showNotification(KickoutMessageTitle, kickMessage, Notification.TYPE_WARNING_MESSAGE);
    	}
		
		//Refresh logged in users
		refreshLoggedInUsers();
		
		//Refresh GPIO pin states
		refreshGPIOPinStates();
		
		//Refresh animation status
		refreshAnimationStatus();
	}
	
	private synchronized void logoutCore() {
		mUser = null;
		createUIForUser();
	}
	
	private void stopAll() {
		Utils.ExecExternalProgram("sudo pkill binaryclock", true, false);
		Utils.ExecExternalProgram("sudo pkill lightshow", true, false);
		GPIOController.INSTANCE.setAllLow();
	}
	
	@SuppressWarnings("deprecation")
	private synchronized void refreshGPIOPinState(int index) {
		if (mBasicControlGridLayout != null) {
			mBasicControlGridLayout.removeComponent(1, index + 1);
			
			Label label = new Label (GPIOController.INSTANCE.getPin(index).getState().getName());
			label.setWidth(Sizeable.SIZE_UNDEFINED);
			
			if (index < 5) {
				if (GPIOController.INSTANCE.getPin(index).isHigh()) {
					label.addStyleName("v-label-blueBackText");
				}
				else {
					label.addStyleName("v-label-transparentBackText");
				}
			}
			else if (index < 11) {
				if (GPIOController.INSTANCE.getPin(index).isHigh()) {
					label.addStyleName("v-label-orangeBackText");
				}
				else {
					label.addStyleName("v-label-transparentBackText");
				}
			}
			else {
				if (GPIOController.INSTANCE.getPin(index).isHigh()) {
					label.addStyleName("v-label-lightgreyBackText");
				}
				else {
					label.addStyleName("v-label-transparentBackText");
				}
			}
			
			mBasicControlGridLayout.addComponent(label, 1, index + 1);
			mBasicControlGridLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		}
	}
	
	private synchronized void refreshGPIOPinStates () {
		if (mGPIOPinStates == null) {
			mGPIOPinStates = new ArrayList<Boolean>();
			for (int i = 0; i < GPIOController.GPIOPinCount; ++i) {
				Boolean curGPIOPinState = GPIOController.INSTANCE.getPin(i).isHigh();
				mGPIOPinStates.add(curGPIOPinState);
				refreshGPIOPinState(i);
			}
		}
		else {
			for (int i = 0; i < GPIOController.GPIOPinCount; ++i) {
				Boolean curGPIOPinState = GPIOController.INSTANCE.getPin(i).isHigh();
				if (!mGPIOPinStates.get(i).equals(curGPIOPinState)) {
					mGPIOPinStates.set(i, curGPIOPinState);
					refreshGPIOPinState(i);
				}
			}
		}
	}
	
	private synchronized void refreshFeed() {
		if (mFeedLayout != null) {
			mFeedLayout.removeAllComponents();
			
			Label feedLabel = new Label ("<img src=\"" + Settings.INSTANCE.GetFeedURL() + ":" + Settings.FeedPortNumber + "/?action=stream\" width=\"" + FeedWidth + "\" height=\"" + FeedHeight + "\"/>");
			feedLabel.setContentMode(Label.CONTENT_XHTML);
			feedLabel.setWidth (FeedWidth + 10, Sizeable.UNITS_PIXELS);
			feedLabel.setHeight (FeedHeight + 20, Sizeable.UNITS_PIXELS);
			mFeedLayout.addComponent(feedLabel);
			mFeedLayout.setComponentAlignment(feedLabel, Alignment.MIDDLE_CENTER);
		}
	}
	
	private synchronized void refreshUser() {
		if (mUserLayout != null) {
			mUserLayout.removeAllComponents();
			if (mUser != null) {
				Label userLabel = new Label(mUser.getData().getName());
				mUserLayout.addComponent(userLabel, 0, 0);
				mUserLayout.setComponentAlignment(userLabel, Alignment.MIDDLE_CENTER);
				
				Button buttonLogout = new Button("Log out", new Button.ClickListener () {
					public void buttonClick(Button.ClickEvent event)
					{				
						mUser.logout();
					}
					
				});
				mUserLayout.addComponent(buttonLogout, 1, 0);		
				mUserLayout.setComponentAlignment(buttonLogout, Alignment.MIDDLE_CENTER);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private synchronized void refreshLoggedInUsers() {
		ArrayList<User> curLoggedInUsers = LoggedInUserManager.INSTANCE.getLoggedInUsers();
		
		if (!curLoggedInUsers.equals(mLoggedInUsers)) {
			mLoggedInUsers.clear();
			mLoggedInUsers.addAll(curLoggedInUsers);
		
			if (mLoggedInUsersLayout != null) {
				if (mLoggedInUsersManagementLayout != null) {
					mLoggedInUsersLayout.removeComponent(mLoggedInUsersManagementLayout);
				}
				
				int loggedInUserCount = mLoggedInUsers.size();
				
				mLoggedInUsersManagementLayout = new GridLayout (3, loggedInUserCount + 1);
				mLoggedInUsersManagementLayout.setWidth("98%");
				mLoggedInUsersManagementLayout.addStyleName("tightComponentSpacing");
				mLoggedInUsersLayout.addComponent(mLoggedInUsersManagementLayout, 0, 0);
				mLoggedInUsersLayout.setComponentAlignment(mLoggedInUsersManagementLayout, Alignment.MIDDLE_CENTER);
		
				createLoggedInUsersGridHeader();
				
				for (int i = 0; i < loggedInUserCount; ++i) {
					final User loggedInUser = mLoggedInUsers.get(i);
					if (loggedInUser != null) {
						Label loggedInUserNameLabel = new Label (loggedInUser.getData().getName());
						loggedInUserNameLabel.setWidth(Sizeable.SIZE_UNDEFINED);
						mLoggedInUsersManagementLayout.addComponent(loggedInUserNameLabel, 0, i + 1);
						mLoggedInUsersManagementLayout.setComponentAlignment(loggedInUserNameLabel, Alignment.MIDDLE_CENTER);
						
						Label loggedInUserRoleLabel = new Label (loggedInUser.getData().getType());
						loggedInUserRoleLabel.setWidth(Sizeable.SIZE_UNDEFINED);
						mLoggedInUsersManagementLayout.addComponent(loggedInUserRoleLabel, 1, i + 1);
						mLoggedInUsersManagementLayout.setComponentAlignment(loggedInUserRoleLabel, Alignment.MIDDLE_CENTER);
						
						Button loggedInUserLogoutButton = new Button("Log out", new Button.ClickListener () {
							private User mLoggedInUser = loggedInUser;
							private User mCurrentUser = mUser;
		
							public void buttonClick(Button.ClickEvent event)
							{
								LoggedInUserManager.INSTANCE.logoutFromApplication(mLoggedInUser, mCurrentUser);
								refreshLoggedInUsers();
							}
							
						});
						mLoggedInUsersManagementLayout.addComponent(loggedInUserLogoutButton, 2, i + 1);
						mLoggedInUsersManagementLayout.setComponentAlignment(loggedInUserLogoutButton, Alignment.MIDDLE_CENTER);
					}
				}
			}
		}
	}
	
	private synchronized void refreshAnimationStatus() {
		if (Animator.INSTANCE.isRunning()) {
			if (mAnimationStatusLabel != null) {
				String statusString = Animator.INSTANCE.getCurrentAnimationName();
				
				if (Animator.INSTANCE.getTotalAnimationDuration() > 0) {
					statusString += " (" + Math.round((double)(Animator.INSTANCE.getTotalAnimationDuration()) / 1000.0) + " s)";
				}
				else {
					statusString += " (" + AnimationDuration_Infinite + ")";
				}
				
				if (Animator.INSTANCE.getCurrentAnimation() != null) {
					if (!Animator.INSTANCE.getCurrentAnimation().isDurationFixed()) {
						DecimalFormat formatter = new DecimalFormat("0.00");				
						statusString += "(" + formatter.format(Animator.INSTANCE.getSpeedModifier()) + "x)";
					}
					else {
						statusString += "(1x)";
					}
				}
				
				mAnimationStatusLabel.setValue(statusString);
			}
			
			if (mAnimationProgressIndicator != null) {
				mAnimationProgressIndicator.setEnabled(true);
				mAnimationProgressIndicator.setIndeterminate(Animator.INSTANCE.getTotalAnimationDuration() == 0);
				mAnimationProgressIndicator.setValue(Animator.INSTANCE.getCurrentAnimationProgress());
			}
		}
		else {
			if (mAnimationStatusLabel != null) {
				mAnimationStatusLabel.setValue(AnimationStoppedText);
			}
			
			if (mAnimationProgressIndicator != null) {
				mAnimationProgressIndicator.setEnabled(false);
				mAnimationProgressIndicator.setIndeterminate(Animator.INSTANCE.getTotalAnimationDuration() == 0);
				mAnimationProgressIndicator.setValue(0.0);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void createLoggedInUsersGridHeader() {
		if (mLoggedInUsersManagementLayout != null) {
			Label loggedInUserNameHeaderLabel = new Label ("User name");
			loggedInUserNameHeaderLabel.setWidth(Sizeable.SIZE_UNDEFINED);
			loggedInUserNameHeaderLabel.addStyleName("v-label-smallTitleText");
			mLoggedInUsersManagementLayout.addComponent(loggedInUserNameHeaderLabel, 0, 0);
			mLoggedInUsersManagementLayout.setComponentAlignment(loggedInUserNameHeaderLabel, Alignment.MIDDLE_CENTER);
			
			Label loggedInUserRoleHeaderLabel = new Label ("User role");
			loggedInUserRoleHeaderLabel.setWidth(Sizeable.SIZE_UNDEFINED);
			loggedInUserRoleHeaderLabel.addStyleName("v-label-smallTitleText");
			mLoggedInUsersManagementLayout.addComponent(loggedInUserRoleHeaderLabel, 1, 0);
			mLoggedInUsersManagementLayout.setComponentAlignment(loggedInUserRoleHeaderLabel, Alignment.MIDDLE_CENTER);
			
			Button buttonLogEverybodyElseOut = new Button("Log all out", new Button.ClickListener () {
				public void buttonClick(Button.ClickEvent event)
				{
					LoggedInUserManager.INSTANCE.logAllOutExcept(mUser, mUser);
					refreshLoggedInUsers();
				}
				
			});
			buttonLogEverybodyElseOut.addStyleName("v-label-smallTitleText");
			mLoggedInUsersManagementLayout.addComponent(buttonLogEverybodyElseOut, 2, 0);
			mLoggedInUsersManagementLayout.setComponentAlignment(buttonLogEverybodyElseOut, Alignment.MIDDLE_CENTER);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void createTitle(final AbstractOrderedLayout parentLayout) {
		GridLayout layout = new GridLayout (6, 1);
		layout.setSpacing(true);
		layout.setWidth("100%");
		layout.setHeight(80, Sizeable.UNITS_PIXELS);
		parentLayout.addComponent(layout);
		parentLayout.setComponentAlignment(layout, Alignment.TOP_CENTER);
		
		Embedded image = new Embedded("", new ClassResource("Icon_25x32.png", this));
		layout.addComponent(image, 2, 0);
		layout.setComponentAlignment(image, Alignment.TOP_RIGHT);
		
		Label titleLabel = new Label("Raspberry Pi GPIO Control Center");
		titleLabel.setWidth(Sizeable.SIZE_UNDEFINED);
		titleLabel.addStyleName("v-label-largeTitleText");
		layout.addComponent(titleLabel, 3, 0);
		layout.setComponentAlignment(titleLabel, Alignment.MIDDLE_LEFT);
		
		mUserLayout = new GridLayout(2, 1);
		mUserLayout.setSpacing(true);
		layout.addComponent(mUserLayout, 5, 0);
		layout.setComponentAlignment(mUserLayout, Alignment.MIDDLE_CENTER);
	}
	
	private void createBasicGlobalButtons(final GridLayout layout) {
		Button buttonRefreshStatus = new Button("Refresh status", new Button.ClickListener () {
			public void buttonClick(Button.ClickEvent event)
			{				
				refreshGPIOPinStates();
			}
			
		});
		layout.addComponent(buttonRefreshStatus, 0, 0);		
		layout.setComponentAlignment(buttonRefreshStatus, Alignment.BOTTOM_CENTER);
		
		Button buttonAllLow = new Button("Set all LOW", new Button.ClickListener () {
			public void buttonClick(Button.ClickEvent event)
			{
				GPIOController.INSTANCE.setAllLow();
				refreshGPIOPinStates();
			}
			
		});
		layout.addComponent(buttonAllLow, 0, 1);		
		layout.setComponentAlignment(buttonAllLow, Alignment.BOTTOM_CENTER);
		
		Button buttonAllHigh = new Button("Set all HIGH", new Button.ClickListener () {
			public void buttonClick(Button.ClickEvent event)
			{
				GPIOController.INSTANCE.setAllHigh();
				refreshGPIOPinStates();
			}
			
		});
		layout.addComponent(buttonAllHigh, 0, 2);		
		layout.setComponentAlignment(buttonAllHigh, Alignment.BOTTOM_CENTER);
		
		Button buttonToggleAll = new Button("Toggle all", new Button.ClickListener () {
			public void buttonClick(Button.ClickEvent event)
			{
				GPIOController.INSTANCE.toggleAll();
				refreshGPIOPinStates();
			}
			
		});
		layout.addComponent(buttonToggleAll, 1, 0);		
		layout.setComponentAlignment(buttonToggleAll, Alignment.BOTTOM_CENTER);
		
		Button buttonPulseAll = new Button("Pulse all", new Button.ClickListener () {
			public void buttonClick(Button.ClickEvent event)
			{
				GPIOController.INSTANCE.pulseAll(Settings.INSTANCE.GetPulseDuration ());
			}
			
		});
		layout.addComponent(buttonPulseAll, 1, 1);		
		layout.setComponentAlignment(buttonPulseAll, Alignment.BOTTOM_CENTER);
		
		Button buttonBlinkAll = new Button("Blink all", new Button.ClickListener () {
			public void buttonClick(Button.ClickEvent event)
			{
				GPIOController.INSTANCE.blinkAll(Settings.INSTANCE.GetBlinkDuration (), Settings.INSTANCE.GetBlinkTotalDuration ());
			}
			
		});
		layout.addComponent(buttonBlinkAll, 1, 2);		
		layout.setComponentAlignment(buttonBlinkAll, Alignment.BOTTOM_CENTER);
	}
	
	private void createBasicGlobalInputs(final GridLayout layout) {
		final TextField textFieldPulseDuration = new TextField("Pulse duration (ms):");
		textFieldPulseDuration.setValue(Settings.INSTANCE.GetPulseDuration ());
		textFieldPulseDuration.setImmediate(true);
		textFieldPulseDuration.setRequiredError("Value required");
		textFieldPulseDuration.addListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		    	try {
		    		textFieldPulseDuration.validate();
		    		Settings.INSTANCE.SetPulseDuration (Long.valueOf((String)textFieldPulseDuration.getValue()));
		    	}
		    	catch(Exception e) {		    		
		    	}
		    }
		});
		textFieldPulseDuration.addValidator(new PositiveIntegerValidator("The value must be a positive integer number"));
		textFieldPulseDuration.addValidator(new NonZeroIntegerValidator("The value must be a non-zero integer number"));
		layout.addComponent(textFieldPulseDuration, 3, 0);		
		layout.setComponentAlignment(textFieldPulseDuration, Alignment.BOTTOM_CENTER);
		   
		final TextField textFieldBlinkDuration = new TextField("Blink duration (ms):");
		textFieldBlinkDuration.setValue(Settings.INSTANCE.GetBlinkDuration ());
		textFieldBlinkDuration.setImmediate(true);
		textFieldPulseDuration.setRequiredError("Value required");
		textFieldBlinkDuration.addListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		    	try {
		    		textFieldBlinkDuration.validate();
		    		Settings.INSTANCE.SetBlinkDuration (Long.valueOf((String)textFieldBlinkDuration.getValue()));
		    	}
		    	catch(Exception e) {
		    	}
		    }
		});
		textFieldBlinkDuration.addValidator(new PositiveIntegerValidator("The value must be a positive integer number"));
		textFieldBlinkDuration.addValidator(new NonZeroIntegerValidator("The value must be a non-zero integer number"));
		layout.addComponent(textFieldBlinkDuration, 3, 1);		
		layout.setComponentAlignment(textFieldBlinkDuration, Alignment.BOTTOM_CENTER);
		
		final TextField textFieldBlinkTotalDuration = new TextField("Blink total duration (ms):");
		textFieldBlinkTotalDuration.setValue(Settings.INSTANCE.GetBlinkTotalDuration ());
		textFieldBlinkTotalDuration.setImmediate(true);
		textFieldBlinkTotalDuration.setRequiredError("Value required");
		textFieldBlinkTotalDuration.addListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		    	try {
		    		textFieldBlinkTotalDuration.validate();
		    		Settings.INSTANCE.SetBlinkTotalDuration (Long.valueOf((String)textFieldBlinkTotalDuration.getValue()));
		    	}
		    	catch(Exception e) {
		    	}
		    }
		});
		textFieldBlinkTotalDuration.addValidator(new PositiveIntegerValidator("The value must be a positive integer number"));
		textFieldBlinkTotalDuration.addValidator(new NonZeroIntegerValidator("The value must be a non-zero integer number"));
		layout.addComponent(textFieldBlinkTotalDuration, 3, 2);		
		layout.setComponentAlignment(textFieldBlinkTotalDuration, Alignment.BOTTOM_CENTER);
	}
	
	private void createBasicGlobalControls(final AbstractOrderedLayout parentLayout) {
		GridLayout layout = new GridLayout (4, 3);
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setWidth("98%");
		layout.addStyleName("componentBorder");
		parentLayout.addComponent(layout);
		parentLayout.setComponentAlignment(layout, Alignment.MIDDLE_CENTER);
		
		createBasicGlobalButtons(layout);
		createBasicGlobalInputs(layout);
	}	

	@SuppressWarnings("deprecation")
	private void createGridHeader(final GridLayout layout) {
		Label GPIOPinLabel = new Label ("GPIO Pin");
		GPIOPinLabel.setWidth(Sizeable.SIZE_UNDEFINED);
		GPIOPinLabel.addStyleName("v-label-smallTitleText");
		layout.addComponent(GPIOPinLabel, 0, 0);
		layout.setComponentAlignment(GPIOPinLabel, Alignment.MIDDLE_CENTER);

		Label statusLabel = new Label ("Status");
		statusLabel.setWidth(Sizeable.SIZE_UNDEFINED);
		statusLabel.addStyleName("v-label-smallTitleText");
		layout.addComponent(statusLabel, 1, 0);
		layout.setComponentAlignment(statusLabel, Alignment.MIDDLE_CENTER);

		Label toggleLabel = new Label ("Toggle");
		toggleLabel.setWidth(Sizeable.SIZE_UNDEFINED);
		toggleLabel.addStyleName("v-label-smallTitleText");
		layout.addComponent(toggleLabel, 2, 0);
		layout.setComponentAlignment(toggleLabel, Alignment.MIDDLE_CENTER);

		Label pulseLabel = new Label ("Pulse");
		pulseLabel.setWidth(Sizeable.SIZE_UNDEFINED);
		pulseLabel.addStyleName("v-label-smallTitleText");
		layout.addComponent(pulseLabel, 3, 0);
		layout.setComponentAlignment(pulseLabel, Alignment.MIDDLE_CENTER);

		Label blinkLabel = new Label ("Blink");
		blinkLabel.setWidth(Sizeable.SIZE_UNDEFINED);
		blinkLabel.addStyleName("v-label-smallTitleText");
		layout.addComponent(blinkLabel, 4, 0);
		layout.setComponentAlignment(blinkLabel, Alignment.MIDDLE_CENTER);
	}

	@SuppressWarnings("deprecation")
	private void createGPIONumLabels(final GridLayout layout) {
		DecimalFormat formatter = new DecimalFormat("00");
		for (int i = 0; i < GPIOController.GPIOPinCount; ++i) {
			Label GPIONumLabel = new Label (formatter.format (i));
			GPIONumLabel.setWidth(Sizeable.SIZE_UNDEFINED);
			layout.addComponent(GPIONumLabel, 0, i + 1);
			layout.setComponentAlignment(GPIONumLabel, Alignment.MIDDLE_CENTER);
		}
	}

	@SuppressWarnings("deprecation")
	private void createGPIOStateIndicators(final GridLayout layout) {	
		for (int i = 0; i < GPIOController.GPIOPinCount; ++i) {
			Label label = new Label ("");
			label.setWidth(Sizeable.SIZE_UNDEFINED);
			layout.addComponent(label, 1, i + 1);
			layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		}
		
		refreshGPIOPinStates();
	}

	private Button createToggleButton(final int index) {
		Button button = new Button("Toggle", new Button.ClickListener () {
			private int mIndex = index;
			
			public void buttonClick(Button.ClickEvent event)
			{
				GPIOController.INSTANCE.getPin(mIndex).toggle();
				refreshGPIOPinState(mIndex);				
			}
			
		});
		
		return button;
	}
	
	private void createToggleButtons(final GridLayout layout) {
		for (int i = 0; i < GPIOController.GPIOPinCount; ++i) {
			Button button = createToggleButton(i);
			layout.addComponent(button, 2, i + 1);		
			layout.setComponentAlignment(button, Alignment.MIDDLE_CENTER);
		}
	}
	
	private Button createPulseButton(final int index) {
		Button button = new Button("Pulse", new Button.ClickListener () {
			private int mIndex = index;
			
			public void buttonClick(Button.ClickEvent event)
			{
				GPIOController.INSTANCE.getPin(mIndex).pulse(Settings.INSTANCE.GetPulseDuration());
			}
			
		});
		
		return button;
	}
	
	private void createPulseButtons(final GridLayout layout) {
		for (int i = 0; i < GPIOController.GPIOPinCount; ++i) {
			Button button = createPulseButton(i);
			layout.addComponent(button, 3, i + 1);		
			layout.setComponentAlignment(button, Alignment.MIDDLE_CENTER);
		}
	}
	
	private Button createBlinkButton(final int index) {
		Button button = new Button("Blink", new Button.ClickListener () {
			private int mIndex = index;

			public void buttonClick(Button.ClickEvent event)
			{
				GPIOController.INSTANCE.getPin(mIndex).blink(Settings.INSTANCE.GetBlinkDuration(), Settings.INSTANCE.GetBlinkTotalDuration());
			}
			
		});
		
		return button;
	}
	
	private void createBlinkButtons(final GridLayout layout) {
		for (int i = 0; i < GPIOController.GPIOPinCount; ++i) {
			Button button = createBlinkButton(i);
			layout.addComponent(button, 4, i + 1);		
			layout.setComponentAlignment(button, Alignment.MIDDLE_CENTER);
		}
	}
	
	private void createBasicControlLayout(final GridLayout layout) {
		VerticalLayout basicControlLayout = new VerticalLayout();
		basicControlLayout.setWidth("100%");
		layout.addComponent(basicControlLayout, 0, 0);
		layout.setComponentAlignment(basicControlLayout, Alignment.MIDDLE_CENTER);
		
		createBasicGlobalControls(basicControlLayout);
		
		mBasicControlGridLayout = new GridLayout (5, 18);		
		mBasicControlGridLayout.setMargin(true);
		mBasicControlGridLayout.setWidth("98%");
		mBasicControlGridLayout.addStyleName("componentBorder");
		mBasicControlGridLayout.addStyleName("tightComponentSpacing");
		basicControlLayout.addComponent(mBasicControlGridLayout);
		basicControlLayout.setComponentAlignment(mBasicControlGridLayout, Alignment.MIDDLE_CENTER);

		createGridHeader(mBasicControlGridLayout);
		createGPIONumLabels(mBasicControlGridLayout);
		createGPIOStateIndicators(mBasicControlGridLayout);
		createToggleButtons(mBasicControlGridLayout);
		createPulseButtons(mBasicControlGridLayout);
		createBlinkButtons(mBasicControlGridLayout);
	}
	
	private void createFeedButtons(final AbstractOrderedLayout parentLayout) {
		final GridLayout feedButtonsGridLayout = new GridLayout (2, 1);
		feedButtonsGridLayout.setWidth ("50%");
		parentLayout.addComponent(feedButtonsGridLayout);
		parentLayout.setComponentAlignment(feedButtonsGridLayout, Alignment.MIDDLE_CENTER);
		
		Button buttonStartFeed = new Button("Start live feed", new Button.ClickListener () {			
			public void buttonClick(Button.ClickEvent event)
			{
				Utils.ExecExternalProgram("sudo " + Settings.INSTANCE.GetMJPGStreamerPath() + "/mjpg-streamer.sh start " + Settings.FeedPortNumber + " " + FeedWidth + "x" + FeedHeight, true, true);
				refreshFeed();
			}
			
		});
		feedButtonsGridLayout.addComponent(buttonStartFeed, 0, 0);
		feedButtonsGridLayout.setComponentAlignment(buttonStartFeed, Alignment.MIDDLE_CENTER);
		
		Button buttonStopFeed = new Button("Stop live feed", new Button.ClickListener () {
			public void buttonClick(Button.ClickEvent event)
			{
				Utils.ExecExternalProgram("sudo /soft/mjpg-streamer/mjpg-streamer.sh stop", true, true);
				Utils.ExecExternalProgram("sudo pkill mjpg-streamer.sh", true, false);
				refreshFeed();
			}
			
		});
		feedButtonsGridLayout.addComponent(buttonStopFeed, 1, 0);
		feedButtonsGridLayout.setComponentAlignment(buttonStopFeed, Alignment.MIDDLE_CENTER);
	}
	
	private void createFeed(final AbstractOrderedLayout parentLayout) {
		mFeedLayout = new VerticalLayout ();
		mFeedLayout.setWidth (650, Sizeable.UNITS_PIXELS);
		mFeedLayout.setHeight (500, Sizeable.UNITS_PIXELS);
		parentLayout.addComponent(mFeedLayout);
		parentLayout.setComponentAlignment(mFeedLayout, Alignment.MIDDLE_CENTER);
		
		refreshFeed ();
	}
	
	private void createFeedLayout(final GridLayout layout) {
		VerticalLayout feedGroupLayout = new VerticalLayout();
		feedGroupLayout.setMargin(true);
		feedGroupLayout.setSpacing(true);
		feedGroupLayout.setWidth("98%");
		feedGroupLayout.setHeight("98%");
		feedGroupLayout.addStyleName("componentBorder");
		layout.addComponent(feedGroupLayout, 0, 2);
		layout.setComponentAlignment(feedGroupLayout, Alignment.TOP_CENTER);
		
		createFeedButtons (feedGroupLayout);
		createFeed (feedGroupLayout);
	}
	
	private void createUserManagementControls(final GridLayout layout) {
		if ((mUser != null) && ((UserData.getUserTypeRights(mUser.getData().getType()) & UserData.USER_RIGHT_MASK_USERMANAGER) != 0)) {
			mLoggedInUsersLayout = new GridLayout(1, 1);
			mLoggedInUsersLayout.setWidth ("98%");
			mLoggedInUsersLayout.setSpacing (true);
			mLoggedInUsersLayout.addStyleName("componentBorder");
			layout.addComponent(mLoggedInUsersLayout, 0, 3);
			layout.setComponentAlignment(mLoggedInUsersLayout, Alignment.MIDDLE_CENTER);
			
			refreshLoggedInUsers();
		}
	}
	
	private void createExternalControls(final GridLayout layout) {
		GridLayout externalLayout = new GridLayout (4, 1);
		externalLayout.setWidth ("98%");
		externalLayout.setMargin(true);
		externalLayout.setSpacing (true);
		externalLayout.addStyleName("componentBorder");
		layout.addComponent(externalLayout, 0, 1);
		layout.setComponentAlignment(externalLayout, Alignment.MIDDLE_CENTER);
		
		Label externalLabel = new Label("External scripts:");
		externalLabel.setWidth("180px");
		externalLabel.addStyleName("v-label-smallTitleText");
		externalLayout.addComponent(externalLabel, 0, 0);		
		externalLayout.setComponentAlignment(externalLabel, Alignment.MIDDLE_CENTER);
		
		Button buttonStartBinaryClock = new Button("Start binary clock", new Button.ClickListener () {
			public void buttonClick(Button.ClickEvent event)
			{
				stopAll();
				Utils.ExecExternalProgram("sudo " + Settings.ScriptsPath + "/binaryclock.bash", false, false);
			}
			
		});
		externalLayout.addComponent(buttonStartBinaryClock, 1, 0);
		externalLayout.setComponentAlignment(buttonStartBinaryClock, Alignment.MIDDLE_CENTER);
		
		Button buttonStartLightShow = new Button("Start light show", new Button.ClickListener () {
			public void buttonClick(Button.ClickEvent event)
			{
				stopAll();
				Utils.ExecExternalProgram("sudo " + Settings.ScriptsPath + "/lightshow.bash", false, false);
			}
			
		});
		externalLayout.addComponent(buttonStartLightShow, 2, 0);
		externalLayout.setComponentAlignment(buttonStartLightShow, Alignment.MIDDLE_CENTER);
		
		Button buttonStopScripts = new Button("Stop running scripts", new Button.ClickListener () {
			public void buttonClick(Button.ClickEvent event)
			{
				stopAll();
			}
			
		});
		externalLayout.addComponent(buttonStopScripts, 3, 0);
		externalLayout.setComponentAlignment(buttonStopScripts, Alignment.MIDDLE_CENTER);
	}
	
	private void refreshAnimationDurationSettings() {
		mMinAnimationDurationTextField.setValue(Animator.INSTANCE.getMinTotalAnimationDuration() / 1000);
		mMaxAnimationDurationTextField.setValue(Animator.INSTANCE.getMaxTotalAnimationDuration() / 1000);
	}
	
	private void createAnimationSelector(final AbstractOrderedLayout parentLayout) {		
		ArrayList<Animation> animations = Animator.INSTANCE.getRegisteredAnimations();
		
		mAnimationSelector = new NativeSelect("Animation");
		mAnimationSelector.addItem(AnimationName_Random);
        for (int i = 0; i < animations.size(); ++i) {
        	mAnimationSelector.addItem(animations.get(i).getName());
        }

        mAnimationSelector.setNullSelectionAllowed(false);
        mAnimationSelector.setValue(AnimationName_Random);
        mAnimationSelector.setImmediate(true);
        mAnimationSelector.addListener(new Property.ValueChangeListener () {
        	public void valueChange(ValueChangeEvent event) {
            }
        });

        parentLayout.addComponent(mAnimationSelector);
        parentLayout.setComponentAlignment(mAnimationSelector, Alignment.MIDDLE_CENTER);
	}
	
	private void createAnimationControlButtons(final AbstractOrderedLayout parentLayout) {
		Button buttonStart = new Button("Start", new Button.ClickListener () {
			public void buttonClick(Button.ClickEvent event)
			{
				Animator.INSTANCE.forceStop(true);
				
				refreshAnimationDurationSettings();
				
        		Animator.INSTANCE.setCurrentAnimationByName(mAnimationSelector.getValue().toString());
        		
				Animator.INSTANCE.start();
			}
			
		});
		parentLayout.addComponent(buttonStart);
		parentLayout.setComponentAlignment(buttonStart, Alignment.BOTTOM_CENTER);
		
		Button buttonStop = new Button("Stop", new Button.ClickListener () {
			public void buttonClick(Button.ClickEvent event)
			{
				Animator.INSTANCE.stop(false);
			}
			
		});
		parentLayout.addComponent(buttonStop);
		parentLayout.setComponentAlignment(buttonStop, Alignment.BOTTOM_CENTER);
		
		Button buttonForceStop = new Button("Instant Stop", new Button.ClickListener () {
			public void buttonClick(Button.ClickEvent event)
			{
				Animator.INSTANCE.forceStop(false);
			}
			
		});
		parentLayout.addComponent(buttonForceStop);
		parentLayout.setComponentAlignment(buttonForceStop, Alignment.BOTTOM_CENTER);
	}
	
	private void createAnimationDurationSettings(final AbstractOrderedLayout parentLayout) {		
		mMinAnimationDurationTextField = new TextField("Min duration (s):");
		mMinAnimationDurationTextField.setWidth("100px");
		mMinAnimationDurationTextField.setValue(Animator.INSTANCE.getMinTotalAnimationDuration() / 1000);
		mMinAnimationDurationTextField.setImmediate(true);
		mMinAnimationDurationTextField.setRequiredError("Value required");
		
		mMinAnimationDurationTextField.addListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		    	try {
		    		int minDuration = getIntValueFromTextField(mMinAnimationDurationTextField);
		    		int maxDuration = getIntValueFromTextField(mMaxAnimationDurationTextField);
		    				
		    		mMinAnimationDurationTextFieldComparatorValidator.setOtherValue(maxDuration);
		    		mMaxAnimationDurationTextFieldComparatorValidator.setOtherValue(minDuration);
		    		mMinAnimationDurationTextFieldComparatorValidator.enable();
		    		mMaxAnimationDurationTextFieldComparatorValidator.enable();
		    		
		    		mMinAnimationDurationTextField.validate();
		    		mMaxAnimationDurationTextField.validate();
		    		
		    		Animator.INSTANCE.setTotalAnimationDuration(minDuration * 1000, maxDuration * 1000);
		    	}
		    	catch(Exception e) {		    		
		    	}		    	
		    }
		});
		
		mMinAnimationDurationTextField.addValidator(new PositiveIntegerValidator("The value must be a positive integer number"));
		mMinAnimationDurationTextFieldComparatorValidator = new LessThanOrEqualToIntegerValidator("The minimum animation duration cannot be more than the maximum animation duration");		
		mMinAnimationDurationTextField.addValidator(mMinAnimationDurationTextFieldComparatorValidator);
		
		parentLayout.addComponent(mMinAnimationDurationTextField);		
		parentLayout.setComponentAlignment(mMinAnimationDurationTextField, Alignment.BOTTOM_CENTER);
		
		mMaxAnimationDurationTextField = new TextField("Max duration (s):");
		mMaxAnimationDurationTextField.setWidth("100px");
		mMaxAnimationDurationTextField.setValue(Animator.INSTANCE.getMaxTotalAnimationDuration() / 1000);
		mMaxAnimationDurationTextField.setImmediate(true);
		mMaxAnimationDurationTextField.setRequiredError("Value required");
		
		mMaxAnimationDurationTextField.addListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		    	try {
		    		int minDuration = getIntValueFromTextField(mMinAnimationDurationTextField);
		    		int maxDuration = getIntValueFromTextField(mMaxAnimationDurationTextField);
		    				    		
		    		mMinAnimationDurationTextFieldComparatorValidator.setOtherValue(maxDuration);
		    		mMaxAnimationDurationTextFieldComparatorValidator.setOtherValue(minDuration);
		    		mMinAnimationDurationTextFieldComparatorValidator.enable();
		    		mMaxAnimationDurationTextFieldComparatorValidator.enable();
		    		
		    		mMinAnimationDurationTextField.validate();
		    		mMaxAnimationDurationTextField.validate();		    		
		    		
		    		Animator.INSTANCE.setTotalAnimationDuration(minDuration * 1000, maxDuration * 1000);
		    	}
		    	catch(Exception e) {		    		
		    	}
		    }
		});		
		
		mMaxAnimationDurationTextField.addValidator(new PositiveIntegerValidator("The value must be a positive integer number"));
		mMaxAnimationDurationTextFieldComparatorValidator = new GreaterThanOrEqualToIntegerValidator("The maximum animation duration cannot be less than the minimum animation duration");
		mMaxAnimationDurationTextField.addValidator(mMaxAnimationDurationTextFieldComparatorValidator);
		
		parentLayout.addComponent(mMaxAnimationDurationTextField);		
		parentLayout.setComponentAlignment(mMaxAnimationDurationTextField, Alignment.BOTTOM_CENTER);
	}
	
	private void createAnimationSpeedControls(final AbstractOrderedLayout parentLayout) {
		Button buttonIncreaseSpeed = new Button("Faster", new Button.ClickListener () {
			public void buttonClick(Button.ClickEvent event)
			{
				Animator.INSTANCE.increaseSpeed();
			}
			
		});
		parentLayout.addComponent(buttonIncreaseSpeed);
		parentLayout.setComponentAlignment(buttonIncreaseSpeed, Alignment.BOTTOM_CENTER);
		
		Button buttonDecreaseSpeed = new Button("Slower", new Button.ClickListener () {
			public void buttonClick(Button.ClickEvent event)
			{
				Animator.INSTANCE.decreaseSpeed();
			}
			
		});
		parentLayout.addComponent(buttonDecreaseSpeed);
		parentLayout.setComponentAlignment(buttonDecreaseSpeed, Alignment.BOTTOM_CENTER);
	}
	
	private void createAnimationStatus(final AbstractOrderedLayout parentLayout) {
		mAnimationStatusLabel = new Label();
		mAnimationStatusLabel.setWidth("250px");
		mAnimationStatusLabel.addStyleName("v-label-smallTitleText");
		mAnimationStatusLabel.addStyleName("v-label-middleALignedText");
		parentLayout.addComponent(mAnimationStatusLabel);		
		parentLayout.setComponentAlignment(mAnimationStatusLabel, Alignment.BOTTOM_CENTER);
		
		mAnimationProgressIndicator = new ProgressIndicator();		
	    mAnimationProgressIndicator.setWidth("150px");
		parentLayout.addComponent(mAnimationProgressIndicator);		
		parentLayout.setComponentAlignment(mAnimationProgressIndicator, Alignment.MIDDLE_CENTER);
		
		refreshAnimationStatus();
	}
	
	private void createAnimationAdditionalSettings(final AbstractOrderedLayout parentLayout) {
		CheckBox checkBoxRandomizeSpeed = new CheckBox("Randomize speed");
        checkBoxRandomizeSpeed.setValue(Animator.INSTANCE.getRandomizeSpeed());
        checkBoxRandomizeSpeed.setImmediate(true);
        checkBoxRandomizeSpeed.addListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		    	Animator.INSTANCE.setRandomizeSpeed((Boolean)event.getProperty().getValue());
		    }
		});
        parentLayout.addComponent(checkBoxRandomizeSpeed);
        parentLayout.setComponentAlignment(checkBoxRandomizeSpeed, Alignment.MIDDLE_CENTER);
        
        CheckBox checkBoxAutoStartStop = new CheckBox("Automatic actions at specified times");
        checkBoxAutoStartStop.setValue(AutomationManager.INSTANCE.isEnabled());
        checkBoxAutoStartStop.setDescription(Tooltip_AutoStartStopCheckBox);
        checkBoxAutoStartStop.setImmediate(true);
        checkBoxAutoStartStop.addListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		    	AutomationManager.INSTANCE.setEnabled((Boolean)event.getProperty().getValue());
		    }
		});
        parentLayout.addComponent(checkBoxAutoStartStop);
        parentLayout.setComponentAlignment(checkBoxAutoStartStop, Alignment.MIDDLE_CENTER);
        
        Button buttonReadFromFile = new Button("Re-read settings from files", new Button.ClickListener () {
			public void buttonClick(Button.ClickEvent event) {
				Animator.INSTANCE.refreshAnimationSettings();
				AutomationManager.INSTANCE.refresh();				
			}
			
		});
		parentLayout.addComponent(buttonReadFromFile);
		parentLayout.setComponentAlignment(buttonReadFromFile, Alignment.BOTTOM_CENTER);
	}
	
	private void createAnimationLayout(final GridLayout layout) {
		GridLayout animationLayout = new GridLayout (1, 3);
		animationLayout.setWidth ("98%");
		animationLayout.setSpacing (true);
		animationLayout.setMargin(true);
		animationLayout.addStyleName("componentBorder");
		layout.addComponent(animationLayout, 0, 0);
		layout.setComponentAlignment(animationLayout, Alignment.MIDDLE_CENTER);
		
		HorizontalLayout animationLayout1 = new HorizontalLayout();
		animationLayout1.setWidth ("100%");
		animationLayout1.setSpacing (true);
		animationLayout.addComponent(animationLayout1, 0, 0);
		animationLayout.setComponentAlignment(animationLayout1, Alignment.MIDDLE_CENTER);
		
		createAnimationSelector(animationLayout1);
		createAnimationControlButtons(animationLayout1);
		createAnimationSpeedControls(animationLayout1);
		
		HorizontalLayout animationLayout2 = new HorizontalLayout();
		animationLayout2.setWidth ("100%");
		animationLayout2.setSpacing (true);
		animationLayout.addComponent(animationLayout2, 0, 1);
		animationLayout.setComponentAlignment(animationLayout2, Alignment.MIDDLE_CENTER);
		
		createAnimationDurationSettings(animationLayout2);
		createAnimationStatus(animationLayout2);
		
		HorizontalLayout animationLayout3 = new HorizontalLayout();
		animationLayout3.setWidth ("100%");
		animationLayout3.setSpacing (true);
		animationLayout.addComponent(animationLayout3, 0, 2);
		animationLayout.setComponentAlignment(animationLayout3, Alignment.MIDDLE_CENTER);
		
		createAnimationAdditionalSettings(animationLayout3);
	}
	
	private void createAdvancedControlLayout(final GridLayout layout) {
		GridLayout advancedControlLayout = new GridLayout (1, 4);
		advancedControlLayout.setSpacing(true);
		advancedControlLayout.setWidth("100%");
		advancedControlLayout.setHeight("100%");
		layout.addComponent(advancedControlLayout, 1, 0);
		layout.setComponentAlignment(advancedControlLayout, Alignment.TOP_CENTER);
			
		createAnimationLayout(advancedControlLayout);
		createExternalControls (advancedControlLayout);
		createFeedLayout (advancedControlLayout);	
		createUserManagementControls(advancedControlLayout);
	}
	
	private void createControllerAndAdminUI(final AbstractOrderedLayout parentLayout) {
		GridLayout mainBodyLayout = new GridLayout(2, 1);
		mainBodyLayout.setMargin(true);
		mainBodyLayout.setSpacing(true);
		mainBodyLayout.setWidth("98%");
		mainBodyLayout.setHeight("98%");
		mainBodyLayout.setColumnExpandRatio(0, 4);
		mainBodyLayout.setColumnExpandRatio(1, 6);
		mainBodyLayout.addStyleName("componentBorder");
		parentLayout.addComponent(mainBodyLayout);
		
		createBasicControlLayout(mainBodyLayout);
		createAdvancedControlLayout (mainBodyLayout);
	}
	
	private void createViewerUI(final AbstractOrderedLayout parentLayout) {
		createFeed(parentLayout);
	}
	
	private void createLoginUI(final AbstractOrderedLayout parentLayout) {
		final Rpi_gpio_controllerApplication application = this;
		
		LoginForm loginForm = new LoginForm();
		loginForm.addListener(new LoginForm.LoginListener() {
			Rpi_gpio_controllerApplication mApplication = application;
			
            public void onLogin(LoginEvent event) {
            	String loginErrorMessage = new User(new UserData (event.getLoginParameter("username"), event.getLoginParameter("password")), mApplication).login();
            	if (loginErrorMessage != null) {            		            		
                	Notification notification = new Notification(LoginErrorMessage, loginErrorMessage, Notification.TYPE_ERROR_MESSAGE);
                	notification.setDelayMsec(1000);
                	getMainWindow().showNotification(notification);
                }
            }
        });
		
		Panel loginPanel = new Panel("Log in");
		loginPanel.setWidth("200px");
		loginPanel.setHeight("250px");
		loginPanel.addComponent(loginForm);
		
        parentLayout.addComponent(loginPanel);
        parentLayout.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
	}
	
	private synchronized void createUIForUser() {
		mMainContentLayout.removeAllComponents();
		
		refreshUser();

    	if (mUser != null) {
    		String userType = mUser.getData().getType();
    		if ((UserData.getUserTypeRights(userType) & UserData.USER_RIGHT_MASK_CONTROLLER) != 0) {            			
            	createControllerAndAdminUI(mMainContentLayout);
    		}
    		else if ((UserData.getUserTypeRights(userType) & UserData.USER_RIGHT_MASK_VIEWER) != 0) {            			
            	createViewerUI(mMainContentLayout);
        	}
    	}
    	else {
        	createLoginUI(mMainContentLayout);
        }
	}
	
	private void createUI() {
		final Window mainWindow = new Window("Raspberry Pi GPIO Control Center");		
		setMainWindow(mainWindow);				

		VerticalLayout mainLayout = (VerticalLayout) mainWindow.getContent();
		mainLayout.setWidth("100%");
		
		mRefresher = new Refresher();
        mRefresher.setRefreshInterval(RefreshIntervalMilliSec);
        mRefresher.addListener(new RefreshListener() {
            private static final long serialVersionUID = 1L;
            
            public void refresh(Refresher source) {
            	periodicRefresh();
            }
        });
        mainLayout.addComponent(mRefresher);
		
		createTitle(mainLayout);		
		
		mMainContentLayout = new VerticalLayout();
		mMainContentLayout.setWidth("100%");
		mainLayout.addComponent(mMainContentLayout);
		mainLayout.setComponentAlignment(mMainContentLayout, Alignment.MIDDLE_CENTER);
		
		createUIForUser();
	}
	
	@SuppressWarnings("unused")
	@Override
	public void init() {
		//Force the creation of the necessary singleton instances
		UserDataManager userDataManager = UserDataManager.INSTANCE;
		Animator animator =  Animator.INSTANCE;
		AutomationManager automationManager = AutomationManager.INSTANCE;
		
		setTheme("theme01");		
		createUI ();
	}
	
	public void login(User user) {
		mUser = user;
		createUIForUser();
	}
	
	public synchronized void logout(User kicker) {
		if ((kicker == null) || (kicker.equals(mUser))) {			
			logoutCore();
		}
		else {
			mKicker = kicker;
		}	
	}
	
	public UUID getID() {
		return mID;
	}
}
