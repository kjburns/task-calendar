package com.gmail.at.kevinburnseit.organizer;

import java.util.GregorianCalendar;

import org.w3c.dom.Element;

import com.gmail.at.kevinburnseit.organizer.gui.HolidayEditorFixedDay;
import com.gmail.at.kevinburnseit.records.Record;
import com.gmail.at.kevinburnseit.records.RecordEditor;
import com.gmail.at.kevinburnseit.xml.XmlElementWriter;

/**
 * A holiday which falls on the same day of the same month every year.
 * @author Kevin J. Burns
 *
 */
public class HolidayRuleFixedDay extends HolidayRule {
	private int month;
	private int day;
	static final String ruleType = "fixed-day";
	
	@Override
	public GregorianCalendar getNormalDateOfHoliday(int year) {
		GregorianCalendar ret = new GregorianCalendar();
		ret.clear();
		ret.set(year, this.month, this.day);
		
		return ret;
	}
	
	@Override
	public Element saveToXml(Element attachTo) {
		Element e = this.beginSaveToXml(attachTo.getOwnerDocument(), ruleType);
		
		XmlElementWriter w = new XmlElementWriter(e);
		w.writeIntAttribute("month", this.month);
		w.writeIntAttribute("day", this.day);
		
		attachTo.appendChild(e);
		
		return e;
	}

	@Override
	public Class<? extends RecordEditor<? extends Record>> getEditorClass() {
		return HolidayEditorFixedDay.class;
	}

	/**
	 * Gets the month that this holiday normally falls on. Return value will be
	 * [0..11] for [Jan..Dec].
	 * @return the month
	 */
	public final int getMonth() {
		return month;
	}

	/**
	 * Sets the month that this holiday normally falls on.
	 * @param month The month, with 0 for January and 11 for December.
	 */
	public final void setMonth(int month) {
		this.month = month;
	}

	/**
	 * Gets the day that this holiday normally falls on.
	 * @return the day
	 */
	public final int getDay() {
		return day;
	}

	/**
	 * Sets the day that this holiday normally falls on. No error checking is performed
	 * regarding the number of days in a month.
	 * @param day the day to set
	 */
	public final void setDay(int day) {
		this.day = day;
	}
}
