package com.gmail.at.kevinburnseit.swing.calendar;

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
}
