package com.gmail.at.kevinburnseit.swing;

public class ValidatingTextBoxExpectsInteger implements ValidatingTextBoxValidator {
	@Override
	public boolean validate(String newValue) {
		try {
			@SuppressWarnings("unused")
			int value = Integer.valueOf(newValue);
		} catch (NumberFormatException e) {
			return false;
		}
		
		return true;
	}
}
