package com.gmail.at.kevinburnseit.organizer;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.w3c.dom.Element;

import com.gmail.at.kevinburnseit.records.Record;
import com.gmail.at.kevinburnseit.records.RecordEditor;
import com.gmail.at.kevinburnseit.xml.XmlElementWriter;

/**
 * A holiday which occurs on the nth xxxday of the month. For example, in the USA
 * Thanksgiving is the fourth Friday of the month.
 * @author Kevin J. Burns
 *
 */
public class HolidayRuleNthDay extends HolidayRule {
	private int dayOfWeek;
	private int whichOccurence;
	private int month;
	public static final int LAST_OCCURENCE = 6;
	static final String ruleType = "nth-day";
	
	@Override
	public GregorianCalendar getNormalDateOfHoliday(int year) {
		GregorianCalendar ret = new GregorianCalendar();
		ret.clear();
		ret.set(Calendar.YEAR, year);
		ret.set(Calendar.MONTH, this.month);
		// this program uses Monday = 0, so must convert to Monday = 2
		ret.set(Calendar.DAY_OF_WEEK, (this.dayOfWeek + 2) % 7);
		ret.set(Calendar.DAY_OF_WEEK_IN_MONTH, this.whichOccurence);
		if (this.whichOccurence == HolidayRuleNthDay.LAST_OCCURENCE) {
			// whichOccurence could have been 'last'; resolve this
			while (ret.get(Calendar.MONTH) != month) {
				ret.add(Calendar.DAY_OF_MONTH, -7);
			} 
		}
		return ret;
	}

	@Override
	public Class<? extends RecordEditor<? extends Record>> getEditorClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element saveToXml(Element attachTo) {
		Element e = this.beginSaveToXml(attachTo.getOwnerDocument(), ruleType);
		XmlElementWriter w = new XmlElementWriter(e);
		w.writeIntAttribute("month", this.month);
		w.writeIntAttribute("day-of-week", this.dayOfWeek);
		w.writeIntAttribute("which", this.whichOccurence);
		
		attachTo.appendChild(e);
		return e;
	}

	/**
	 * Gets the day of the week that this holiday falls on, where [0..6] =
	 * [Monday..Saturday].
	 * @return the dayOfWeek
	 */
	public final int getDayOfWeek() {
		return dayOfWeek;
	}

	/**
	 * Sets the day of the week that this holiday falls on.
	 * @param dayOfWeek Day of the week, where [0..6] = [Monday..Saturday]. If the
	 * supplied value is outside this range, nothing happens.
	 */
	public final void setDayOfWeek(int dayOfWeek) {
		if (dayOfWeek < 0) return;
		if (dayOfWeek > 6) return;
		this.dayOfWeek = dayOfWeek;
	}

	/**
	 * Gets the ordinal number of the day of the week within the month of the holiday.
	 * For example, for a holiday which occurs on the third friday in June, returns 3.
	 * @return the occurence number within the month. For the last occurence of a day
	 * within a month, returns {@link #LAST_OCCURENCE}.
	 */
	public final int getWhichOccurence() {
		return whichOccurence;
	}

	/**
	 * Sets the ordinal number of the day of the week within the month of the holiday.
	 * @param whichOccurence Ordinal number to set. For the first Friday of a month,
	 * for example, pass 1 in this parameter. For the last occurence of a day within
	 * a month, pass {@link #LAST_OCCURENCE}. If this value is less than 1, or
	 * greater than 5, and not LAST_OCCURENCE, nothing happens.
	 */
	public final void setWhichOccurence(int whichOccurence) {
		if (whichOccurence < 1) return;
		if (whichOccurence > 6) return;
		this.whichOccurence = whichOccurence;
	}

	/**
	 * Gets the month in which this holiday occurs, where [0..11] = [January..December].
	 * @return the month
	 */
	public final int getMonth() {
		return month;
	}

	/**
	 * Sets the month in which this holiday occurs.
	 * @param month the month to set, where [0..11] = [January..December]. If the
	 * value is outside this range, nothing happens.
	 */
	public final void setMonth(int month) {
		if (month < 0) return;
		if (month > 11) return;
		this.month = month;
	}
}
