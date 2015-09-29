package com.gmail.at.kevinburnseit.swing;

public class ValidatingTextBoxExpectsPositiveInteger 
		extends ValidatingTextBoxExpectsInteger {
	@Override
	public boolean validate(String newValue) {
		if (!super.validate(newValue)) return false;
		
		int value = Integer.valueOf(newValue);
		return (value > 0);
	}
}
