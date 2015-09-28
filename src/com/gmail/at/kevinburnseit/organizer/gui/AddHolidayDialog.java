package com.gmail.at.kevinburnseit.organizer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.gmail.at.kevinburnseit.organizer.HolidayRule;
import com.gmail.at.kevinburnseit.organizer.HolidayRuleCollection;
import com.gmail.at.kevinburnseit.swing.DialogResult;
import com.gmail.at.kevinburnseit.swing.NewDialog;

/**
 * Dialog which asks the user to choose which type of holiday to add so that the correct
 * editor is displayed.
 * @author Kevin J. Burns
 *
 */
public class AddHolidayDialog extends NewDialog {
	private static final long serialVersionUID = 8723596860611327569L;

	private HolidayRule output;
	private HolidayRuleCollection rules;
	private JButton fixedButton;
	private JButton nthDayOfWeekButton;
	private JButton relativeButton;
	private JButton cancelBtn;
	
	public AddHolidayDialog(HolidayRuleCollection rules) {
		this.rules = rules;
		
		this.buildUI();
	}

	private void buildUI() {
		this.setTitle("New Holiday");
		
		this.contentScrollPane.setVisible(false);
		this.fixedButton = this.addButton(
				"Same month and day every year >", null, ButtonTypeEnum.NONE);
		this.fixedButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				HolidayEditorFixedDay e = new HolidayEditorFixedDay();
				if (e.showDialog() == DialogResult.OK) {
					output = e.getOutput();
				}
				e.dispose();
				setVisible(false);
			}
		});
		
		this.nthDayOfWeekButton = this.addButton(
				"Certain day of week every year >", null, ButtonTypeEnum.NONE);
		this.nthDayOfWeekButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		this.nthDayOfWeekButton.setEnabled(false);
		
		this.relativeButton = this.addButton(
				"Defined by another holiday >", null, ButtonTypeEnum.NONE);
		this.relativeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		this.relativeButton.setEnabled(false);
		
		this.cancelBtn = this.addButton(
				"Cancel", DialogResult.CANCEL, ButtonTypeEnum.CANCEL);
		this.cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		this.pack();
	}

	/**
	 * Gets the holiday that the user created.
	 * @return the holiday that the user created if he or she did so. If the user
	 * did not complete the creation of a holiday, returns <code>null</code>.
	 */
	public final HolidayRule getOutput() {
		return output;
	}
}
