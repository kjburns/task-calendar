package com.gmail.at.kevinburnseit.organizer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

import com.gmail.at.kevinburnseit.swing.NewDialog;

public class AbortOrContinueSetupDialog extends NewDialog {
	public enum ResultEnum {
		CONTINUE,
		STOP;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 6513938602185369799L;
	private ResultEnum result = null;
	
	public AbortOrContinueSetupDialog() {
		this.setTitle("Setup Not Complete");
		BoxLayout layout = new BoxLayout(this.contentPanel, BoxLayout.PAGE_AXIS);
		this.contentPanel.setLayout(layout);
		
		JLabel label = new JLabel("Initial setup has not been completed. "
				+ "If you stop now the program will exit.");
		this.contentPanel.add(label);
		
		JButton continueButton = new JButton("continue with initial setup");
		continueButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				result = ResultEnum.CONTINUE;
				setVisible(false);
			}
		});
		this.contentPanel.add(continueButton);
		
		JButton abortButton = new JButton(
				"stop initial setup and exit the application");
		abortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				result = ResultEnum.STOP;
				setVisible(false);
			}
		});
		this.contentPanel.add(abortButton);
		
		this.pack();
	}

	/**
	 * @return the result
	 */
	public ResultEnum getResult() {
		return result;
	}
}
