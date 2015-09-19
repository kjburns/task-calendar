package com.gmail.at.kevinburnseit.organizer;

/**
 * An enumeration of the days of the week.
 * @author Kevin J. Burns
 *
 */
public enum DayEnum {
	MONDAY("Monday", "Mon"),
	TUESDAY("Tuesday", "Tue"),
	WEDNESDAY("Wednesday", "Wed"),
	THURSDAY("Thursday", "Thu"),
	FRIDAY("Friday", "Fri"),
	SATURDAY("Saturday", "Sat"),
	SUNDAY("Sunday", "Sun");
	
	private String nameOfDay;
	private String abbreviation;
	
	private DayEnum(String dayName, String abbrev) {
		this.nameOfDay = dayName;
		this.abbreviation = abbrev;
	}

	/**
	 * Gets the full name of the day
	 * @return full name of this day
	 */
	public String getNameOfDay() {
		return nameOfDay;
	}

	/**
	 * Gets the three-letter abbreviation of this day
	 * @return abbreviation for this day
	 */
	public String getAbbreviation() {
		return abbreviation;
	}
	
	public boolean isWeekday() {
		if (this == DayEnum.SATURDAY) return false;
		if (this == DayEnum.SUNDAY) return false;
		return true;
	}
}
