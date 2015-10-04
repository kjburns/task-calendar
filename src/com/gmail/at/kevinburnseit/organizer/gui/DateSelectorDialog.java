package com.gmail.at.kevinburnseit.organizer.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

import com.gmail.at.kevinburnseit.organizer.DateRecord;
import com.gmail.at.kevinburnseit.records.RecordEditor;
import com.gmail.at.kevinburnseit.swing.DialogResult;
import com.gmail.at.kevinburnseit.swing.NewDialog;
import com.gmail.at.kevinburnseit.swing.calendar.DatePicker;

/**
 * A simple dialog that allows a user to select a date.
 * @author Kevin J. Burns
 *
 */
public class DateSelectorDialog extends NewDialog implements RecordEditor<DateRecord> {
	private static final long serialVersionUID = 2899947258547535263L;

	private DateRecord input;
	private DatePicker datePicker;

	private JButton okBtn;

	private JButton cancelBtn;
	
	public DateSelectorDialog() {
		this.buildUI();
	}
	
	private void buildUI() {
		this.setTitle("Select Date");
		this.contentPanel.setLayout(new GridLayout(1, 1));
		
		this.datePicker = new DatePicker();
		this.contentPanel.add(datePicker);
		
		this.okBtn = this.addButton("OK", DialogResult.OK, ButtonTypeEnum.DEFAULT);
		ActionListener closeListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		};
		this.okBtn.addActionListener(closeListener);
		
		this.cancelBtn = this.addButton(
				"Cancel", DialogResult.CANCEL, ButtonTypeEnum.CANCEL);
		this.cancelBtn.addActionListener(closeListener);
		
		this.pack();
	}
	@Override
	public DateRecord getOutput() {
		if (this.dialogResult != DialogResult.OK) return null;
		
		DateRecord ret = new DateRecord(this.datePicker.getSelectedDate().getTime());
		return ret;
	}

	@Override
	public void setData(DateRecord input) {
		this.input = input;
		this.populate();
	}

	private void populate() {
		this.datePicker.setSelectedDate(this.input);
	}
}
