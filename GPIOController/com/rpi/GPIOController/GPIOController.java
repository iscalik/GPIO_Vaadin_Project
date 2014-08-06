package com.rpi.GPIOController;

import java.util.ArrayList;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public enum GPIOController {
	INSTANCE(true);
	
	//Number of used GPIO pins
	public static final int GPIOPinCount = 17;
	
	//The used GPIO pins' IDs
	private final Pin[] GPIOPinIDs = {RaspiPin.GPIO_00, RaspiPin.GPIO_01, RaspiPin.GPIO_02, RaspiPin.GPIO_03, RaspiPin.GPIO_04, RaspiPin.GPIO_05, RaspiPin.GPIO_06, RaspiPin.GPIO_07, RaspiPin.GPIO_08, RaspiPin.GPIO_09, RaspiPin.GPIO_10, RaspiPin.GPIO_11, RaspiPin.GPIO_12, RaspiPin.GPIO_13, RaspiPin.GPIO_14, RaspiPin.GPIO_15, RaspiPin.GPIO_16};
	//The used GPIO pins
	private GpioPinDigitalOutput[] mGPIOPins = new GpioPinDigitalOutput[GPIOPinCount];
	
	//Constructor
	private GPIOController(boolean setLowOnExit) {
		
		GpioController gpioController = GpioFactory.getInstance();
		
		//Initialize all GPIO pins to output mode
		for (int i = 0; i < GPIOPinCount; ++i) {
			mGPIOPins[i] = gpioController.provisionDigitalOutputPin(GPIOPinIDs[i], "GPIO_Pin_" + i , PinState.LOW);
		}
		
		if (setLowOnExit) {
			//Set shutdown behavior for all pins
			for (int i = 0; i < GPIOPinCount; ++i) {
				mGPIOPins[i].setShutdownOptions(true, PinState.LOW);
			}
		}

	}
	
	//Gets the state of pin #index
	public Boolean getPinState(int index) {
		return mGPIOPins[index].isHigh();
	}
	
	//Sets the state of pin #index
	public void setPinState(int index, Boolean state) {
		if (state) {
			mGPIOPins[index].high();
		}
		else {
			mGPIOPins[index].low();
		}
	}
	
	//Gets the states of all pins
	public ArrayList<Boolean> getPinStates() {
		ArrayList<Boolean> pinStates = new ArrayList<Boolean>();
		
		for (int i = 0; i < GPIOPinCount; ++i) {
			pinStates.add(getPinState(i));
		}
		
		return pinStates;
	}
	
	//Sets the states of all pins
	public void setPinStates(ArrayList<Boolean> states) {
		assert (states.size() == GPIOPinCount);
		
		for (int i = 0; i < GPIOPinCount; ++i) {
			setPinState (i, states.get(i));
		}
	}
	
	//Expose pin #index
	public GpioPinDigitalOutput getPin(int index) {
		return mGPIOPins[index];
	}
	
	//Set all pins to low
	public void setAllLow() {
		for (int i = 0; i < GPIOPinCount; ++i) {
			mGPIOPins[i].low();
		}
	}
	
	//Set all pins to high
	public void setAllHigh() {
		for (int i = 0; i < GPIOPinCount; ++i) {
			mGPIOPins[i].high();
		}
	}
	
	//Toggle all pins
	public void toggleAll() {
		for (int i = 0; i < GPIOPinCount; ++i) {
			mGPIOPins[i].toggle();
		}
	}
	
	//Pulse all pins
	public void pulseAll(long duration) {
		for (int i = 0; i < GPIOPinCount; ++i) {
			mGPIOPins[i].pulse(duration);
		}
	}
	
	//Blink all pins
		public void blinkAll(long delay) {
			for (int i = 0; i < GPIOPinCount; ++i) {
				mGPIOPins[i].blink(delay);
			}
		}
	
	//Blink all pins
	public void blinkAll(long delay, long duration) {
		for (int i = 0; i < GPIOPinCount; ++i) {
			mGPIOPins[i].blink(delay, duration);
		}
	}
}
