package com.gmail.at.kevinburnseit.organizer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.gmail.at.kevinburnseit.organizer.HolidayRuleNthDay;
import com.gmail.at.kevinburnseit.records.RecordEditor;
import com.gmail.at.kevinburnseit.swing.DialogResult;
import com.gmail.at.kevinburnseit.swing.NewDialog;
import com.gmail.at.kevinburnseit.swing.RowControl;
import com.gmail.at.kevinburnseit.swing.ValidatingTextBox;
import com.gmail.at.kevinburnseit.swing.ValidatingTextBoxExpectsNonEmptyString;
import com.gmail.at.kevinburnseit.swing.calendar.CalendarHelper;

/**
 * An editor for holidays that normally fall on a certain occurence of a certain day
 * of week within a certain month.
 * @author Kevin J. Burns
 *
 */
public class HolidayEditorNthDay extends NewDialog 
		implements RecordEditor<HolidayRuleNthDay> {
	private static final long serialVersionUID = -2386931644295644027L;
	
	private JLabel warningLabel;
	private ValidatingTextBox nameTextBox;
	private JCheckBox weekdayCheckBox;
	private JComboBox<String> occurrenceDropDown;
	private JComboBox<String> dowDropDown;
	private JComboBox<String> monthDropDown;
	private HolidayRuleNthDay input;
	private JButton okBtn;
	private JButton cancelBtn;

	private ImageIcon icon;
	
	/**
	 * Constructor. Assembles this dialog.
	 */
	public HolidayEditorNthDay() {
		this.buildUI();
		this.populate();
	}
	
	private void populate() {
		if (this.input == null) {
			this.nameTextBox.setText("Untitled Holiday");
			this.weekdayCheckBox.setSelected(false);
			this.occurrenceDropDown.setSelectedIndex(0);
			this.dowDropDown.setSelectedIndex(0);
			this.monthDropDown.setSelectedIndex(0);
		}
		else {
			this.nameTextBox.setText(this.input.getName());
			this.weekdayCheckBox.setSelected(this.input.isAlwaysObservedOnWeekday());
			this.occurrenceDropDown.setSelectedIndex(
					this.input.getWhichOccurence() - 1);
			this.dowDropDown.setSelectedIndex(this.input.getDayOfWeek());
			this.monthDropDown.setSelectedIndex(this.input.getMonth());
		}
	}

	private void buildUI() {
		this.setTitle("Define Day of Week Holiday");
		BoxLayout layout = new BoxLayout(this.contentPanel, BoxLayout.PAGE_AXIS);
		this.contentPanel.setLayout(layout);
		
		icon = new ImageIcon(
				this.getClass().getClassLoader().getResource("res/warning.png"));
		this.warningLabel = new JLabel(".");
		this.contentPanel.add(this.warningLabel);
		
		RowControl rc;
		rc = new RowControl();
		this.contentPanel.add(rc);
		
		JLabel label;
		label = new JLabel("Name:");
		rc.add(label);
		
		this.nameTextBox = new ValidatingTextBox();
		rc.add(this.nameTextBox);
		this.nameTextBox.setValidationKey("name");
		this.nameTextBox.addValidationListener(
				new ValidatingTextBoxExpectsNonEmptyString());
		this.nameTextBox.setColumns(15);
		
		this.weekdayCheckBox = new JCheckBox("Observed on nearest weekday");
		this.contentPanel.add(this.weekdayCheckBox);
		
		rc = new RowControl();
		this.contentPanel.add(rc);
		
		label = new JLabel("Normally falls on ");
		rc.add(label);
		
		this.occurrenceDropDown = new JComboBox<String>();
		rc.add(this.occurrenceDropDown);
		this.occurrenceDropDown.addItem("1st");
		this.occurrenceDropDown.addItem("2nd");
		this.occurrenceDropDown.addItem("3rd");
		this.occurrenceDropDown.addItem("4th");
		this.occurrenceDropDown.addItem("5th");
		this.occurrenceDropDown.addItem("last");
		this.occurrenceDropDown.addItemListener(new ItemListener() {
			/*
			 * FIXME this doesn't work
			 */
			@Override
			public void itemStateChanged(ItemEvent ev) {
				if (((String)ev.getItem()).equals("5th")) {
					warningLabel.setIcon(
							ev.getStateChange() == ItemEvent.SELECTED ? icon : null);
					warningLabel.setText(
							ev.getStateChange() == ItemEvent.SELECTED ? 
									"Holiday does not occur every year" : ".");
				}
			}
		});
		
		this.dowDropDown = new JComboBox<String>();
		rc.add(this.dowDropDown);
		this.dowDropDown.setModel(CalendarHelper.getComboBoxModelOfDaysOfWeek());
		
		label = new JLabel(" of ");
		rc.add(label);
		
		this.monthDropDown = new JComboBox<String>();
		rc.add(this.monthDropDown);
		this.monthDropDown.setModel(CalendarHelper.getComboBoxModelOfMonths());
		
		this.okBtn = this.addButton("OK", DialogResult.OK, ButtonTypeEnum.DEFAULT);
		this.okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		
		this.cancelBtn = this.addButton("Cancel", DialogResult.CANCEL, ButtonTypeEnum.CANCEL);
		this.cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		this.pack();
		
		this.warningLabel.setVisible(false);
	}

	@Override
	public HolidayRuleNthDay getOutput() {
		if (this.dialogResult != DialogResult.OK) return null;
		
		HolidayRuleNthDay ret = new HolidayRuleNthDay();
		ret.setName(this.nameTextBox.getText());
		ret.setAlwaysObservedOnWeekday(this.weekdayCheckBox.isSelected());
		ret.setWhichOccurence(this.occurrenceDropDown.getSelectedIndex() + 1);
		ret.setDayOfWeek(this.dowDropDown.getSelectedIndex());
		ret.setMonth(this.monthDropDown.getSelectedIndex());
		
		return ret;
	}

	@Override
	public void setData(HolidayRuleNthDay input) {
		this.input = input;
		this.populate();
	}
}
