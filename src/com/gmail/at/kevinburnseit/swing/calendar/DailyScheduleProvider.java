package com.gmail.at.kevinburnseit.swing.calendar;

import java.util.GregorianCalendar;

/**
 * Interface which supplies the user's work schedule information for a particular
 * day.
 * @author Kevin J. Burns
 *
 */
public interface DailyScheduleProvider {
	/**
	 * Returns whether the user is scheduled to be at work on the supplied day.
	 * @param date Date in question
	 * @return <code>true</code> if the user is scheduled to be at work; 
	 * <code>false</code> otherwise.
	 */
	boolean isAtWorkOn(GregorianCalendar date);
	/**
	 * Returns whether the user is scheduled to take a lunch break on the supplied day.
	 * <b>Note:</b> this value is only expected to be meaningful when
	 * {@link #isAtWorkOn(GregorianCalendar)} returns <code>true</code>.
	 * @param date Date in question
	 * @return <code>true</code> if the user is scheduled to take a lunch;
	 * <code>false</code> otherwise.
	 */
	boolean isTakingLunchOn(GregorianCalendar date);
	/**
	 * Returns the time that the user is scheduled to arrive at work.
	 * <b>Note:</b> this value is only expected to be meaningful when
	 * {@link #isAtWorkOn(GregorianCalendar)} returns <code>true</code>.
	 * @param date Date in question
	 * @return The number of seconds after midnight that the user is scheduled to
	 * arrive at work.
	 */
	int getWorkStartTime(GregorianCalendar date);
	/**
	 * Returns the time that the user is scheduled to leave work.
	 * <b>Note:</b> this value is only expected to be meaningful when
	 * {@link #isAtWorkOn(GregorianCalendar)} returns <code>true</code>.
	 * @param date Date in question
	 * @return The number of seconds after midnight that the user is scheduled to
	 * leave work. 
	 */
	int getWorkEndTime(GregorianCalendar date);
	/**
	 * Returns the time that the user is scheduled to go to lunch.
	 * <b>Note:</b> this value is only expected to be meaningful when both
	 * {@link #isAtWorkOn(GregorianCalendar)} and
	 * {@link #isTakingLunchOn(GregorianCalendar)} return <code>true</code>.
	 * @param date Date in question
	 * @return The number of seconds after midnight that the user is scheduled to
	 * go to lunch.
	 */
	int getLunchStartTime(GregorianCalendar date);
	/**
	 * Returns the time that the user is scheduled to come back from lunch.
	 * <b>Note:</b> this value is only expected to be meaningful when both
	 * {@link #isAtWorkOn(GregorianCalendar)} and
	 * {@link #isTakingLunchOn(GregorianCalendar)} return <code>true</code>.
	 * @param date Date in question
	 * @return The number of seconds after midnight that the user is scheduled to
	 * come back from lunch.
	 */
	int getLunchEndTime(GregorianCalendar date);
}