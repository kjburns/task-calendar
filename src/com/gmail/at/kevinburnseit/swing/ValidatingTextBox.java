package com.gmail.at.kevinburnseit.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.font.TextAttribute;
import java.util.Map;

import javax.accessibility.AccessibleContext;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * This widget provides a self-validating text box.
 * @author Kevin J. Burns
 *
 */
public class ValidatingTextBox extends JTextField {
	private static final long serialVersionUID = -5408530842050135977L;
	private ValidatingTextBoxValidator validator = null;
	private ValidatingTextBoxAction afterValidate = null;
	private boolean lastValidateResult = true;
	private Color normalBackground;
	private Font normalFont;
	private Font invalidFont;
	protected AccessibleContext acc = new AccessibleValidatingTextBox();
	private String explanationText;
	private String validationKey;
	
	protected class AccessibleValidatingTextBox extends AccessibleJTextField {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8630216436522517668L;
	}
	
	@SuppressWarnings("unchecked")
	public ValidatingTextBox() {
		super();
		this.normalBackground = this.getBackground();
		this.normalFont = this.getFont();
		@SuppressWarnings("rawtypes")
		Map attr = this.normalFont.getAttributes();
		attr.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		this.invalidFont = this.normalFont.deriveFont(attr);
		
		this.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// no action
			}
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				ValidatingTextBox.this.validateNow();
			}
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				ValidatingTextBox.this.validateNow();
			}
		});
		
		this.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				ValidatingTextBox th = ValidatingTextBox.this;
				th.selectAll();
			}
			@Override
			public void focusLost(FocusEvent e) {
				ValidatingTextBox.this.setCaretPosition(0);
			}
		});
	}
	
	/**
	 * Adds validation listener
	 * @param v your custom validator
	 * @see ValidatingTextBoxValidator
	 */
	public void addValidationListener(ValidatingTextBoxValidator v) {
		this.validator = v;
	}

	/**
	 * Adds action to take after validation.
	 * @param a Action to take
	 */
	public void addAfterValidationAction(ValidatingTextBoxAction a) {
		this.afterValidate = a;
	}
	
	/**
	 * Forces immediate validation of text box.
	 */
	public void validateNow() {
		if (ValidatingTextBox.this.validator != null) {
			boolean ok = ValidatingTextBox.this.validator.validate(ValidatingTextBox.this.getText());
			ValidatingTextBox.this.setBackground(ok ? ValidatingTextBox.this.normalBackground :	Color.decode("#ff8080"));
			ValidatingTextBox.this.setFont(ok ? ValidatingTextBox.this.normalFont : ValidatingTextBox.this.invalidFont);
			this.lastValidateResult = ok;
			this.updateTooltip();
			if (ValidatingTextBox.this.afterValidate != null) { 
				ValidatingTextBox.this.afterValidate.afterValidation(ok);
			}

			
			Window w = SwingUtilities.getWindowAncestor(this);
			if (w instanceof NewDialog) {
				if (this.validationKey != null) {
					((NewDialog)w).putValidationHold(this.validationKey, !ok);
				}
			}
		}
	}
	
	/**
	 * Returns the last validate result.  Can be helpful if validation action changes the state of a widget
	 * that is dependent on multiple ValidatingTextBoxes.
	 * @return Last validate result.  If no validation has occurred so far, returns true.
	 */
	public boolean getLastValidateResult() {
		return this.lastValidateResult;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JTextField#getAccessibleContext()
	 */
	@Override
	public AccessibleContext getAccessibleContext() {
		return this.acc;
	}

	public void setExplanationText(String string) {
		this.explanationText = string;
		this.updateTooltip();
	}

	private void updateTooltip() {
		String text = "";
		if (!this.lastValidateResult) text = "This value is not allowed. ";
		text += this.explanationText;
		
		this.setToolTipText(text);
	}

	/**
	 * @return the validationKey
	 */
	public String getValidationKey() {
		return validationKey;
	}

	/**
	 * @param validationKey the validationKey to set
	 */
	public void setValidationKey(String validationKey) {
		this.validationKey = validationKey;
	}
}
