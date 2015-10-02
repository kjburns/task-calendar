package com.gmail.at.kevinburnseit.swing;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class RowControl extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7406960586086496153L;

	public RowControl() {
		BoxLayout layout = new BoxLayout(this, BoxLayout.LINE_AXIS);
		this.setLayout(layout);
	}
}
