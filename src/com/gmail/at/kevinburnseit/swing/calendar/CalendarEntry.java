package com.gmail.at.kevinburnseit.swing.calendar;

import java.util.GregorianCalendar;

/**
 * Class for a basic entry that appears in a calendar view.
 * @author Kevin J. Burns
 *
 */
public abstract class CalendarEntry {
	private GregorianCalendar startTime;
	private GregorianCalendar endTime;
	private boolean allDay = false;
	private String title;
	/**
	 * @return the startTime
	 */
	public final GregorianCalendar getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public final void setStartTime(GregorianCalendar startTime) {
		this.startTime = startTime;
	}
	/**
	 * @return the endTime
	 */
	public final GregorianCalendar getEndTime() {
		return endTime;
	}
	/**
	 * @param endTime the endTime to set
	 */
	public final void setEndTime(GregorianCalendar endTime) {
		this.endTime = endTime;
	}
	/**
	 * @return the allDay
	 */
	public final boolean isAllDay() {
		return allDay;
	}
	/**
	 * @param allDay the allDay to set
	 */
	public final void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}
	/**
	 * @return the title
	 */
	public final String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public final void setTitle(String title) {
		this.title = title;
	}
}