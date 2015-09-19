package com.gmail.at.kevinburnseit.swing;

/**
 * Interface for supplying an after-validation action for a validating text box.
 * @author Kevin J. Burns
 * @see ValidatingTextBox
 */
public interface ValidatingTextBoxAction {
	/**
	 * Function which will be called after validation
	 * @param ok result of the validation
	 */
	void afterValidation(boolean ok);
}
