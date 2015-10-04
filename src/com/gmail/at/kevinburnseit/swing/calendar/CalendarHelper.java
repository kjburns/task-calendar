package com.gmail.at.kevinburnseit.swing.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 * Contains helpful information about timekeeping that I can't find already written
 * somewhere else.
 * @author Kevin J. Burns
 *
 */
public class CalendarHelper {
	public static final String[] daysOfWeek = 
		{ "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
	public static final String[] monthsOfYear =
		{ "January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December" };
	
	private static ComboBoxModel<String> monthModel = null;
	private static ComboBoxModel<String> dayOfWeekModel = null;
	/**
	 * Returns the number of days in a month.
	 * @param month Month number in the range [0, 11] (following Calendar class's
	 * convention). 0 = January, 11 = December
	 * @param leapYear <code>true</code> if the month is in a leap year;
	 * <code>false</code> otherwise. This parameter only really matters if month == 1.
	 * @return The number of days in the specified month. If the month number is invalid,
	 * returns zero.
	 */
	public static int getDaysInMonth(int month, boolean leapYear) {
		switch(month + 1) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			return 31;
		case 4:
		case 6: 
		case 9:
		case 11:
			return 30;
		case 2:
			return (leapYear ? 29 : 28);
		default:
			return 0;
		}
	}
	
	public static ComboBoxModel<String> getComboBoxModelOfMonths() {
		if (CalendarHelper.monthModel != null) return CalendarHelper.monthModel;
		
		monthModel = new ComboBoxModel<String>() {
			private ArrayList<ListDataListener> listeners = new ArrayList<>();
			private int selectedIndex = 0;
			
			@Override
			public void addListDataListener(ListDataListener l) {
				this.listeners.add(l);
			}
			@Override
			public String getElementAt(int index) {
				return CalendarHelper.monthsOfYear[index];
			}
			@Override
			public int getSize() {
				return 12;
			}
			@Override
			public void removeListDataListener(ListDataListener l) {
				this.listeners.remove(l);
			}
			@Override
			public Object getSelectedItem() {
				return this.getElementAt(this.selectedIndex);
			}
			@Override
			public void setSelectedItem(Object item) {
				if (item == null) {
					this.selectedIndex = -1;
					return;
				}
				
				for (int i = 0; i < 12; i++) {
					if (item.equals(CalendarHelper.monthsOfYear[i])) {
						this.selectedIndex = i;
						return;
					}
				}
				
				this.selectedIndex = -1;
				return;
			}
		};
		
		return monthModel;
	}
	
	public static ComboBoxModel<String> getComboBoxModelOfDaysOfWeek() {
		if (CalendarHelper.dayOfWeekModel != null) return CalendarHelper.dayOfWeekModel;

		dayOfWeekModel = new ComboBoxModel<String>() {
			private ArrayList<ListDataListener> listeners = new ArrayList<>();
			private int selectedIndex = 0;

			@Override
			public void addListDataListener(ListDataListener l) {
				this.listeners.add(l);
			}
			@Override
			public String getElementAt(int index) {
				return CalendarHelper.daysOfWeek[index];
			}
			@Override
			public int getSize() {
				return 7;
			}
			@Override
			public void removeListDataListener(ListDataListener l) {
				this.listeners.remove(l);
			}
			@Override
			public Object getSelectedItem() {
				return this.getElementAt(this.selectedIndex);
			}
			@Override
			public void setSelectedItem(Object item) {
				if (item == null) {
					this.selectedIndex = -1;
					return;
				}

				for (int i = 0; i < 7; i++) {
					if (item.equals(CalendarHelper.daysOfWeek[i])) {
						this.selectedIndex = i;
						return;
					}
				}

				this.selectedIndex = -1;
				return;
			}
		};

		return dayOfWeekModel;
	}
	
	public static boolean areDatesEqual(Date x, Date y) {
		GregorianCalendar gcx = new GregorianCalendar();
		gcx.setTime(x);
		
		GregorianCalendar gcy = new GregorianCalendar();
		gcy.setTime(y);
		
		return areDatesEqual(gcx, gcy);
	}
	
	public static boolean areDatesEqual(GregorianCalendar x, GregorianCalendar y) {
		if (x.get(Calendar.YEAR) != y.get(Calendar.YEAR)) {
			return false;
		}
		if (x.get(Calendar.MONTH) != y.get(Calendar.MONTH)) {
			return false;
		}
		if (x.get(Calendar.DAY_OF_MONTH) != y.get(Calendar.DAY_OF_MONTH)) {
			return false;
		}
		
		return true;
	}
}
