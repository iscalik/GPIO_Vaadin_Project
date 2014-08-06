package com.utils.LessThanOrEqualToIntegerValidator;

import com.vaadin.data.validator.IntegerValidator;

@SuppressWarnings("serial")
public class LessThanOrEqualToIntegerValidator extends IntegerValidator {

	private int mValue = 0;
	private boolean mEnabled = false;

	public LessThanOrEqualToIntegerValidator(String errorMessage) {
        super(errorMessage);
        
        mValue = 0;
        mEnabled = false;
    }

    @Override
    protected boolean isValidString(String value) {
        try {
            return ((!mEnabled) || (Integer.parseInt(value) <= mValue));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void validate(Object value) throws InvalidValueException {
    	if (mEnabled) {
	        if ((value != null) && (value instanceof Integer) && (((Integer)value).intValue() <= mValue)) {
	            return;
	        }
    	}

        super.validate(value);
    }

    public void setOtherValue(int value) {
		mValue = value;
	}
    
    public void enable() {
		mEnabled = true;
	}
	
	public void disable() {
		mEnabled = false;
	}
}
