package com.gmail.at.kevinburnseit.organizer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.gmail.at.kevinburnseit.organizer.HolidayRuleFixedDay;
import com.gmail.at.kevinburnseit.records.RecordEditor;
import com.gmail.at.kevinburnseit.swing.DialogResult;
import com.gmail.at.kevinburnseit.swing.NewDialog;
import com.gmail.at.kevinburnseit.swing.RowControl;
import com.gmail.at.kevinburnseit.swing.ValidatingTextBox;
import com.gmail.at.kevinburnseit.swing.ValidatingTextBoxExpectsNonEmptyString;
import com.gmail.at.kevinburnseit.swing.ValidatingTextBoxValidator;
import com.gmail.at.kevinburnseit.swing.calendar.CalendarHelper;

/**
 * An editor for a holiday that always falls on the same day of the same month
 * every year, although is still observable on a weekday if it falls on a weekend.
 * @author Kevin J. Burns
 *
 */
public class HolidayEditorFixedDay extends NewDialog 
		implements RecordEditor<HolidayRuleFixedDay> {
	private static final long serialVersionUID = 2269431486450756531L;
	private final ValidatingTextBoxValidator dayOfMonthValidator =
			new ValidatingTextBoxValidator() {
		@Override
		public boolean validate(String newValue) {
			try {
				int day = Integer.valueOf(newValue);
				if (day < 1) return false;
				int month = 
						HolidayEditorFixedDay.this.monthDropDown.getSelectedIndex();
				int maxDays = CalendarHelper.getDaysInMonth(month, false);
				if (day > maxDays) return false;
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
	};

	private JComboBox<String> monthDropDown;
	private ValidatingTextBox dayTextBox;
	private ValidatingTextBox nameTextBox;
	private JCheckBox weekdayCheckBox;
	private HolidayRuleFixedDay input = null;
	private JButton okBtn;
	private JButton cancelBtn;
	
	public HolidayEditorFixedDay() {
		this.buildUI();
		this.populate();
	}
	
	public HolidayEditorFixedDay(HolidayRuleFixedDay data) {
		this.buildUI();
		this.setData(data);
	}
	
	private void buildUI() {
		this.setTitle("Holiday on Fixed Day");
		BoxLayout layout = new BoxLayout(this.contentPanel, BoxLayout.PAGE_AXIS);
		this.contentPanel.setLayout(layout);
		
		RowControl rc;
		
		rc = new RowControl();
		this.contentPanel.add(rc);
		
		JLabel label;
		label = new JLabel("Name:");
		rc.add(label);
		
		this.nameTextBox = new ValidatingTextBox();
		this.nameTextBox.setValidationKey("name");
		this.nameTextBox.addValidationListener(
				new ValidatingTextBoxExpectsNonEmptyString());
		this.nameTextBox.setColumns(20);
		rc.add(this.nameTextBox);
		
		this.weekdayCheckBox = new JCheckBox("Observed on nearest weekday");
		this.contentPanel.add(this.weekdayCheckBox);
		
		rc = new RowControl();
		this.contentPanel.add(rc);
		
		label = new JLabel("Normally falls on:");
		rc.add(label);
		
		ComboBoxModel<String> monthModel = CalendarHelper.getComboBoxModelOfMonths();
		this.monthDropDown = new JComboBox<String>(monthModel);
		this.monthDropDown.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				HolidayEditorFixedDay.this.dayTextBox.validateNow();
			}
		});
		rc.add(this.monthDropDown);
		
		this.dayTextBox = new ValidatingTextBox();
		this.dayTextBox.setValidationKey("day");
		this.dayTextBox.addValidationListener(this.dayOfMonthValidator);
		this.dayTextBox.setColumns(4);
		rc.add(this.dayTextBox);
		
		this.okBtn = this.addButton("OK", DialogResult.OK, ButtonTypeEnum.DEFAULT);
		this.okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		
		this.cancelBtn = 
				this.addButton("Cancel", DialogResult.CANCEL, ButtonTypeEnum.CANCEL);
		this.cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		this.pack();
	}

	@Override
	public HolidayRuleFixedDay getOutput() {
		if (!this.okBtn.isEnabled()) return null;
		
		HolidayRuleFixedDay ret = new HolidayRuleFixedDay();
		ret.setAlwaysObservedOnWeekday(this.weekdayCheckBox.isSelected());
		ret.setName(this.nameTextBox.getText());
		ret.setMonth(this.monthDropDown.getSelectedIndex());
		ret.setDay(Integer.valueOf(this.dayTextBox.getText()));
		
		return ret;
	}

	@Override
	public void setData(HolidayRuleFixedDay input) {
		this.input = input;
		this.populate();
	}

	private void populate() {
		if (this.input == null) {
			this.weekdayCheckBox.setSelected(true);
			this.nameTextBox.setText("Untitled Holiday");
			this.monthDropDown.setSelectedIndex(0);
			this.dayTextBox.setText("1");
		}
		else {
			this.weekdayCheckBox.setSelected(this.input.isAlwaysObservedOnWeekday());
			this.nameTextBox.setText(this.input.getName());
			this.monthDropDown.setSelectedIndex(this.input.getMonth());
			this.dayTextBox.setText(String.valueOf(this.input.getDay()));
		}
	}
}
