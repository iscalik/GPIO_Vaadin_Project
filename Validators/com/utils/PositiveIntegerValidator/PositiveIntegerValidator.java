package com.utils.PositiveIntegerValidator;

import com.vaadin.data.validator.*;

@SuppressWarnings("serial")
public class PositiveIntegerValidator extends IntegerValidator {

    public PositiveIntegerValidator(String errorMessage) {
        super(errorMessage);

    }

    @Override
    protected boolean isValidString(String value) {
        try {
            return Integer.parseInt(value) >= 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void validate(Object value) throws InvalidValueException {
        if ((value != null) && (value instanceof Integer) && ((Integer)value >= 0)) {
            return;
        }

        super.validate(value);
    }
}

