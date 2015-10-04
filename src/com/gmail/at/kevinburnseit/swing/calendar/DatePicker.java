package com.gmail.at.kevinburnseit.swing.calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

/**
 * A widget that allows the user to select a date on a calendar-grid interface.
 * @author Kevin J. Burns
 *
 */
public class DatePicker extends JPanel {
	private class Day extends JLabel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3022090711844123900L;
		private int dayNumber;
		private final Border selectedBorder = 
				BorderFactory.createLineBorder(Color.black);
		
		public Day(int dayNumber) {
			super((dayNumber == 0) ? " " : String.valueOf(dayNumber));
			this.dayNumber = dayNumber;
			
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (!SwingUtilities.isLeftMouseButton(e)) {
						return;
					}
					if (Day.this.dayNumber == 0) {
						return;
					}
					int oldDay = selectedDate.get(Calendar.DAY_OF_MONTH);
					dayPanels.get(oldDay).setSelected(false);
					setSelected(true);
					selectedDate.set(Calendar.DAY_OF_MONTH, dayNumber);
				}
			});
		}
		
		public void setSelected(boolean state) {
			this.setBorder(state ? this.selectedBorder : null);
		}
	}
	private class EmptyDay extends Day {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5950703787880338740L;

		public EmptyDay() {
			super(0);
		}
	}
	private static final long serialVersionUID = -7605637035603296719L;

	private GregorianCalendar selectedDate = new GregorianCalendar();
	private JLabel monthLabel;
	private JPanel topArea;
	private JPanel gridArea;
	private HashMap<Integer, Day> dayPanels = new HashMap<>();
	
	/**
	 * Constructor. Creates a date picker with today's date initially selected.
	 */
	public DatePicker() {
		this.buildUI();
		this.removeTimeFromSelectedDate();
	}

	private void removeTimeFromSelectedDate() {
		this.selectedDate.set(Calendar.HOUR, 0);
		this.selectedDate.set(Calendar.MINUTE, 0);
		this.selectedDate.set(Calendar.SECOND, 0);
		this.selectedDate.set(Calendar.MILLISECOND, 0);
	}

	private void buildUI() {
		BorderLayout bl = new BorderLayout();
		this.setLayout(bl);
		
		this.topArea = new JPanel();
		this.add(topArea, BorderLayout.PAGE_START);
		SpringLayout sl = new SpringLayout();
		this.topArea.setLayout(sl);
		sl.putConstraint(SpringLayout.EAST, this.topArea, 
				250, SpringLayout.WEST, this.topArea);
		
		JButton prevYearButton = new JButton("<<");
		this.topArea.add(prevYearButton);
		sl.putConstraint(SpringLayout.WEST, prevYearButton, 
				0, SpringLayout.WEST, this.topArea);
		sl.putConstraint(SpringLayout.NORTH, prevYearButton, 
				0, SpringLayout.NORTH, this.topArea);
		prevYearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				selectedDate.add(Calendar.YEAR, -1);
				updateCalendar();
			}
		});
		
		JButton prevMonthButton = new JButton("<");
		this.topArea.add(prevMonthButton);
		sl.putConstraint(SpringLayout.WEST, prevMonthButton, 0, 
				SpringLayout.EAST, prevYearButton);
		sl.putConstraint(SpringLayout.NORTH, prevMonthButton, 0, 
				SpringLayout.NORTH, this.topArea);
		prevMonthButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedDate.add(Calendar.MONTH, -1);
				updateCalendar();
			}
		});
		
		this.monthLabel = new JLabel("Month Name In 2015");
		this.topArea.add(monthLabel);
		sl.putConstraint(SpringLayout.WEST, this.monthLabel, 0, 
				SpringLayout.EAST, prevMonthButton);
		sl.putConstraint(SpringLayout.NORTH, this.monthLabel, 0, 
				SpringLayout.NORTH, this.topArea);
		
		JButton nextMonthButton = new JButton(">");
		this.topArea.add(nextMonthButton);
		nextMonthButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedDate.add(Calendar.MONTH, 1);
				updateCalendar();
			}
		});
		
		JButton nextYearButton = new JButton(">>");
		this.topArea.add(nextYearButton);
		nextYearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedDate.add(Calendar.YEAR, 1);
				updateCalendar();
			}
		});
		
		sl.putConstraint(SpringLayout.EAST, nextYearButton, 0, 
				SpringLayout.EAST, this.topArea);
		sl.putConstraint(SpringLayout.NORTH, nextYearButton, 0, 
				SpringLayout.NORTH, this.topArea);
		sl.putConstraint(SpringLayout.EAST, nextMonthButton, 0, 
				SpringLayout.WEST, nextYearButton);
		sl.putConstraint(SpringLayout.NORTH, nextMonthButton, 0, 
				SpringLayout.NORTH, this.topArea);
		sl.putConstraint(SpringLayout.SOUTH, this.topArea, 0, 
				SpringLayout.SOUTH, nextYearButton);
		
		this.gridArea = new JPanel();
		this.gridArea.setLayout(new GridLayout(0, 7));
		this.add(this.gridArea, BorderLayout.CENTER);
		this.updateCalendar();
	}

	/**
	 * Rebuilds the calendar to show the month which contains the selected date.
	 */
	protected final void updateCalendar() {
		this.gridArea.removeAll();
		this.dayPanels.clear();
		
		for (int i = 0; i < 7; i++) {
			this.gridArea.add(new JLabel(
					CalendarHelper.daysOfWeek[i].substring(0, 2)));
		}
		
		GregorianCalendar startOfMonth = new GregorianCalendar();
		startOfMonth.setTime(this.selectedDate.getTime());
		startOfMonth.set(Calendar.DAY_OF_MONTH, 1);
		
		int month = startOfMonth.get(Calendar.MONTH);
		int year = startOfMonth.get(Calendar.YEAR);
		this.monthLabel.setText(
				CalendarHelper.monthsOfYear[month] +
				" " + year);
		
		int startDOW = startOfMonth.get(Calendar.DAY_OF_WEEK);
		startDOW -= 2;
		if (startDOW < 0) {
			startDOW += 7;
		}
		
		for (int i = 0; i < startDOW; i++) {
			this.gridArea.add(new EmptyDay());
		}
		
		boolean ly = startOfMonth.isLeapYear(year);
		for (int i = 1; i <= CalendarHelper.getDaysInMonth(month, ly); i++) {
			Day dy = new Day(i);
			this.gridArea.add(dy);
			this.dayPanels.put(i, dy);
		}
		
		this.dayPanels.get(
				this.selectedDate.get(Calendar.DAY_OF_MONTH)).setSelected(true);
		this.validate();
	}

	/**
	 * Returns the date that has been selected by the user.
	 * @return the selectedDate
	 */
	public final GregorianCalendar getSelectedDate() {
		return selectedDate;
	}

	/**
	 * Sets the selected date.
	 * @param selectedDate the selectedDate to set
	 */
	public final void setSelectedDate(GregorianCalendar selectedDate) {
		this.selectedDate = selectedDate;
		this.removeTimeFromSelectedDate();
		this.updateCalendar();
	}
	
	/**
	 * Sets the selected date.
	 * @param date the date to be selected.
	 */
	public final void setSelectedDate(Date date) {
		this.selectedDate.setTime(date);
		this.removeTimeFromSelectedDate();
		this.updateCalendar();
	}
}
