package com.gmail.at.kevinburnseit.organizer.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import com.gmail.at.kevinburnseit.organizer.DayEnum;
import com.gmail.at.kevinburnseit.organizer.StandardWorkDay;
import com.gmail.at.kevinburnseit.swing.ValidatingTextBox;
import com.gmail.at.kevinburnseit.swing.ValidatingTextBoxValidator;
import com.gmail.at.kevinburnseit.time.IntervalTime.InvalidTimeException;
import com.gmail.at.kevinburnseit.time.TimeTextBox;

public class StandardWorkDayControl extends JPanel {
	private static final long serialVersionUID = 1896090971677271520L;
	
	private DayEnum day;

	private JCheckBox workTodayCheckBox;
	private TimeTextBox startTimeTextBox;
	private TimeTextBox endTimeTextBox;
	private JCheckBox lunchTodayCheckBox;
	private TimeTextBox lunchTimeTextBox;
	private ValidatingTextBox lunchLengthTextBox;
	
	public StandardWorkDayControl(DayEnum day) {
		this.day = day;
		
		this.buildUI();
		this.populateWithDefaults();
	}

	private void populateWithDefaults() {
		this.workTodayCheckBox.setSelected(this.day.isWeekday());
		this.lunchTodayCheckBox.setSelected(true);
		try {
			this.startTimeTextBox.setValue(
					7 * 3600 + 
					30 * 60);
			this.endTimeTextBox.setValue(
					16 * 3600 + 
					30 * 60);
			this.lunchTimeTextBox.setValue(
					12 * 3600 + 
					0 * 60);
		} catch (InvalidTimeException e) {
			// this will never happen
			e.printStackTrace();
		}
		
		this.lunchLengthTextBox.setText("60");
	}

	private void buildUI() {
		SpringLayout sl = new SpringLayout();
		this.setLayout(sl);
		final int pad = 5;
		final int indent = 10;
		final int textBoxColumnCount = 5;
		
		this.workTodayCheckBox = new JCheckBox(this.day.getNameOfDay());
		sl.putConstraint(SpringLayout.NORTH, this.workTodayCheckBox, 
				pad, SpringLayout.NORTH, this);
		sl.putConstraint(SpringLayout.WEST, this.workTodayCheckBox, 
				pad, SpringLayout.WEST, this);
		this.add(this.workTodayCheckBox);
		this.workTodayCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				StandardWorkDayControl.this.updateGeneralEnabledState();
			}
		});
		
		JLabel label;
		
		label = new JLabel("Start:");
		sl.putConstraint(SpringLayout.NORTH, label, 
				pad, SpringLayout.SOUTH, this.workTodayCheckBox);
		sl.putConstraint(SpringLayout.WEST, label, 
				indent, SpringLayout.WEST, this.workTodayCheckBox);
		this.add(label);
		
		this.startTimeTextBox = new TimeTextBox();
		this.startTimeTextBox.setColumns(textBoxColumnCount);
		sl.putConstraint(SpringLayout.WEST, this.startTimeTextBox, 
				pad, SpringLayout.EAST, label);
		sl.putConstraint(SpringLayout.BASELINE, this.startTimeTextBox, 0, 
				SpringLayout.BASELINE, label);
		this.add(this.startTimeTextBox);
		this.startTimeTextBox.setValidationKey(this.day.getAbbreviation() + "-start");
		
		label = new JLabel("End:");
		sl.putConstraint(SpringLayout.WEST, label, 
				pad, SpringLayout.EAST, this.startTimeTextBox);
		sl.putConstraint(SpringLayout.BASELINE, label, 
				0, SpringLayout.BASELINE, this.startTimeTextBox);
		this.add(label);
		
		this.endTimeTextBox = new TimeTextBox();
		this.endTimeTextBox.setColumns(textBoxColumnCount);
		sl.putConstraint(SpringLayout.WEST, this.endTimeTextBox, 
				pad, SpringLayout.EAST, label);
		sl.putConstraint(SpringLayout.BASELINE, this.endTimeTextBox, 
				0, SpringLayout.BASELINE, label);
		this.add(this.endTimeTextBox);
		this.endTimeTextBox.setValidationKey(this.day.getAbbreviation() + "-end");

		this.lunchTodayCheckBox = new JCheckBox("Lunch:");
		sl.putConstraint(SpringLayout.WEST, this.lunchTodayCheckBox, 
				indent, SpringLayout.WEST, this.workTodayCheckBox);
		sl.putConstraint(SpringLayout.NORTH, this.lunchTodayCheckBox, 
				pad, SpringLayout.SOUTH, this.startTimeTextBox);
		this.add(this.lunchTodayCheckBox);
		this.lunchTodayCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				StandardWorkDayControl.this.updateLunchEnabledState();
			}
		});
		
		this.lunchTimeTextBox = new TimeTextBox();
		this.lunchTimeTextBox.setColumns(textBoxColumnCount);
		sl.putConstraint(SpringLayout.WEST, this.lunchTimeTextBox, 
				pad, SpringLayout.EAST, this.lunchTodayCheckBox);
		sl.putConstraint(SpringLayout.BASELINE, this.lunchTimeTextBox, 
				0, SpringLayout.BASELINE, this.lunchTodayCheckBox);
		this.add(this.lunchTimeTextBox);
		this.lunchTimeTextBox.setValidationKey(this.day.getAbbreviation() + "-lunch");
		
		label = new JLabel("Length:");
		sl.putConstraint(SpringLayout.WEST, label, 
				pad, SpringLayout.EAST, this.lunchTimeTextBox);
		sl.putConstraint(SpringLayout.BASELINE, label, 
				0, SpringLayout.BASELINE, this.lunchTimeTextBox);
		this.add(label);
		
		this.lunchLengthTextBox = new ValidatingTextBox();
		this.lunchLengthTextBox.setColumns(textBoxColumnCount);
		sl.putConstraint(SpringLayout.WEST, this.lunchLengthTextBox, 
				pad, SpringLayout.EAST, label);
		sl.putConstraint(SpringLayout.BASELINE, this.lunchLengthTextBox, 
				0, SpringLayout.BASELINE, label);
		this.add(this.lunchLengthTextBox);
		this.lunchLengthTextBox.addValidationListener(new ValidatingTextBoxValidator() {
			@Override
			public boolean validate(String newValue) {
				int value = 0;
				try {
					value = Integer.parseInt(newValue);
				} catch (NumberFormatException e) {
					return false;
				}
				
				return ((value >= 15) && (value <= 180));
			}
		});
		this.lunchLengthTextBox.setValidationKey(this.day.getAbbreviation() + "-dur");
		this.lunchLengthTextBox.setExplanationText(
				"Lunch must be between 15 and 180 minutes long, inclusive.");
		
		JLabel minutesLabel = new JLabel("minutes");
		sl.putConstraint(SpringLayout.WEST, minutesLabel, 
				pad, SpringLayout.EAST, this.lunchLengthTextBox);
		sl.putConstraint(SpringLayout.BASELINE, minutesLabel, 
				0, SpringLayout.BASELINE, this.lunchLengthTextBox);
		this.add(minutesLabel);
		
		sl.putConstraint(SpringLayout.EAST, this, 
				pad, SpringLayout.EAST, minutesLabel);
		sl.putConstraint(SpringLayout.SOUTH, this, 
				pad, SpringLayout.SOUTH, this.lunchLengthTextBox);
		
		/*
		 * Just to get the proper enabled/disabled state of dependent widgets
		 */
		this.workTodayCheckBox.setSelected(true);
		this.workTodayCheckBox.setSelected(false);
		this.lunchTodayCheckBox.setSelected(true);
		this.lunchTodayCheckBox.setSelected(false);

		/*
		 * Make the time text boxes more intelligent
		 */
		this.startTimeTextBox.setHint(TimeTextBox.beforeUsing12HourTime);
		this.startTimeTextBox.setClueProvider(this.endTimeTextBox);
		
		this.endTimeTextBox.setHint(TimeTextBox.afterUsing12HourTime);
		this.endTimeTextBox.setClueProvider(this.startTimeTextBox);

		this.lunchTimeTextBox.setHint(TimeTextBox.afterUsing12HourTime);
		this.lunchTimeTextBox.setClueProvider(this.startTimeTextBox);
	}
	
	private void updateGeneralEnabledState() {
		boolean en = this.workTodayCheckBox.isSelected();
		
		this.startTimeTextBox.setEnabled(en);
		this.endTimeTextBox.setEnabled(en);
		this.lunchTodayCheckBox.setEnabled(en);
		
		this.updateLunchEnabledState();
	}
	
	private void updateLunchEnabledState() {
		boolean en = this.workTodayCheckBox.isSelected();
		
		if (en) {
			en = this.lunchTodayCheckBox.isSelected();
		}
		
		this.lunchTimeTextBox.setEnabled(en);
		this.lunchLengthTextBox.setEnabled(en);
	}

	public void populate(StandardWorkDay sched) {
		this.workTodayCheckBox.setSelected(sched.isWorkingToday());
		this.lunchTodayCheckBox.setSelected(sched.isTakingLunchToday());
		try {
			this.startTimeTextBox.setValue(sched.getStartTime());
			this.endTimeTextBox.setValue(sched.getEndTime());
			this.lunchTimeTextBox.setValue(sched.getLunchTime());
		} catch (InvalidTimeException e) {
			// this shouldn't happen
			e.printStackTrace();
		}
		this.lunchLengthTextBox.setText(String.valueOf(
				sched.getLunchDurationMinutes()));
	}
	
	public StandardWorkDay getData() {
		StandardWorkDay ret = new StandardWorkDay(this.day);
		ret.setWorkingToday(this.workTodayCheckBox.isSelected());
		ret.setTakingLunchToday(this.lunchTodayCheckBox.isSelected());
		ret.setStartTime(this.startTimeTextBox.getValue());
		ret.setEndTime(this.endTimeTextBox.getValue());
		ret.setLunchTime(this.lunchTimeTextBox.getValue());
		ret.setLunchDurationMinutes(
				Integer.valueOf(this.lunchLengthTextBox.getText()));
		
		return ret;
	}
}
