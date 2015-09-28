package com.gmail.at.kevinburnseit.swing;

public class ValidatingTextBoxExpectsNonEmptyString 
		implements ValidatingTextBoxValidator {
	@Override
	public boolean validate(String newValue) {
		if (newValue == null) return false;
		if (newValue.trim().length() == 0) return false;
		
		return true;
	}
}
