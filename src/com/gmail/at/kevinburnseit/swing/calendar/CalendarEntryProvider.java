package com.gmail.at.kevinburnseit.swing.calendar;

import java.util.GregorianCalendar;
import java.util.function.Predicate;

import com.gmail.at.kevinburnseit.records.ArrayListWithListModel;

/**
 * A list of calendar entries which can be passed to the calendar widget.
 * @author Kevin J. Burns
 *
 */
public class CalendarEntryProvider<T extends CalendarEntry> 
		extends ArrayListWithListModel<T> {
	private static final long serialVersionUID = 7076328137550355592L;

	/**
	 * Gets a predicate that will test a calendar entry's temporal limits against
	 * a specified start and end time. This predicate will return true if any part
	 * of the calendar entry falls within the specified range.
	 * @param start Earliest time for the range
	 * @param end Latest time for the range
	 * @return
	 */
	public static Predicate<CalendarEntry> getPredicateForTimeRange(
			GregorianCalendar start, GregorianCalendar end) {
		return new Predicate<CalendarEntry>() {
			@Override
			public boolean test(CalendarEntry t) {
				if (end.before(t.getStartTime())) return false;
				if (start.after(t.getEndTime())) return false;
				return true;
			}
		};
	}
	
	/**
	 * Gets a predicate that will test a calendar entry's temporal limits against
	 * a specified earliest time. This predicate will return true if any part of the
	 * calendar entry falls after the earliest time.
	 * @param earliest The earliest time to test against
	 * @return
	 */
	public static Predicate<CalendarEntry> getPredicateForLaterThan(
			GregorianCalendar earliest) {
		return new Predicate<CalendarEntry>() {
			@Override
			public boolean test(CalendarEntry t) {
				return earliest.before(t.getEndTime());
			}
		};
	};
}
