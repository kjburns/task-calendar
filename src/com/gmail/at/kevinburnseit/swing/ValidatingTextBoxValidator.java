package com.gmail.at.kevinburnseit.swing;

/**
 * Interface for providing validation function for validating text box
 * @author Kevin J. Burns
 * @see ValidatingTextBox
 *
 */
public interface ValidatingTextBoxValidator {
	/**
	 * When overriding this function, test newValue to ensure it meets your requirements. 
	 * @param newValue The new value to check
	 * @return true if newValue is ok, false otherwise
	 */
	boolean validate(String newValue);
}
