package com.gmail.at.kevinburnseit.organizer;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.gmail.at.kevinburnseit.records.Record;
import com.gmail.at.kevinburnseit.records.RecordEditor;
import com.gmail.at.kevinburnseit.xml.XmlElementWriter;

/**
 * Base class for defining a holiday. Because the day that a holiday falls on varies
 * from holiday to holiday, concrete implementations are necessary to accurately 
 * determine the date.
 * @author Kevin J. Burns
 *
 */
public abstract class HolidayRule implements Record {
	private String name;
	private boolean alwaysObservedOnWeekday;
	static final String xmlTag = "holiday-rule";
	
	/**
	 * Calculates the date that this holiday would normally fall on for a particular
	 * year.
	 * @param year The year to calculate this holiday for
	 * @return The date that this holiday falls on
	 */
	public abstract GregorianCalendar getNormalDateOfHoliday(int year);
	
	/**
	 * Calculates the date that this holiday is observed on in a particular year.
	 * @param year The year to calculate this holiday for
	 * @return The date that this holiday is observed on
	 */
	public final GregorianCalendar getObservedDateOfHoliday(int year) {
		GregorianCalendar ret = this.getNormalDateOfHoliday(year);
		
		if (this.alwaysObservedOnWeekday) {
			this.shiftIfOnWeekend(ret);
		}
		
		return ret;
	}
	
	/**
	 * Gets the name of this holiday rule.
	 * @return the name of this holiday rule
	 */
	public final String getName() {
		return name;
	}
	/**
	 * Sets the name of this holiday rule.
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}
	/**
	 * Returns whether this holiday will be observed on the nearest weekday in the
	 * event that it falls on a weekend. If the holiday falls on Saturday, it will be
	 * observed on the previous Friday; if it falls on a Sunday, it will be observed 
	 * on the following Monday.
	 * @return <code>true</code> if the holiday will be observed on a weekday if it
	 * falls on a weekend; <code>false</code> otherwise.
	 */
	public final boolean isAlwaysObservedOnWeekday() {
		return alwaysObservedOnWeekday;
	}
	/**
	 * Sets whether this holiday will be observed on the nearest weekday in the event
	 * that it falls on a weekend.
	 * @param alwaysObservedOnWeekday <code>true</code> if it is always to be observed
	 * on a weekday; <code>false</code> if it can be observed on a weekend.
	 */
	public final void setAlwaysObservedOnWeekday(boolean alwaysObservedOnWeekday) {
		this.alwaysObservedOnWeekday = alwaysObservedOnWeekday;
	}
	
	/**
	 * If this holiday is always observed on a weekday, this function examines the
	 * date supplied and determines if it falls on a weekend. If so, the supplied
	 * date will be shifted to the previous or next day. <b>The original parameter is
	 * modified.</b> If this holiday is allowed to be observed on a weekend, this
	 * function does nothing.
	 * @param date The date to examine
	 * @see {@link #setAlwaysObservedOnWeekday(boolean)},
	 * {@link #isAlwaysObservedOnWeekday()}
	 */
	protected final void shiftIfOnWeekend(GregorianCalendar date) {
		if (!this.isAlwaysObservedOnWeekday()) return;
		
		int dow = date.get(Calendar.DAY_OF_WEEK);
		if (dow == Calendar.SATURDAY) {
			date.add(Calendar.DAY_OF_MONTH, -1);
		}
		else if (dow == Calendar.SUNDAY) {
			date.add(Calendar.DAY_OF_MONTH, 1);
		}
	}
	
	/**
	 * Begins the process for saving this rule to XML. This function saves the
	 * common elements for any holiday rule to an XML element, and then passes that
	 * element back as a return value for saving the specifics in subclasses.
	 * @param doc Document that this element will be saved to
	 * @param ruleType the rule type, which will be saved in the 'type' attribute
	 * @return An element with the common parts of all holiday rules already stored.
	 */
	protected final Element beginSaveToXml(Document doc, String ruleType) {
		Element ret = doc.createElement(xmlTag);
		XmlElementWriter w = new XmlElementWriter(ret);
		
		w.writeStringAttribute("name", this.name);
		w.writeBooleanAttribute("observe-on-weekday-only", 
				this.alwaysObservedOnWeekday);
		w.writeStringAttribute("type", ruleType);
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.gmail.at.kevinburnseit.records.Record#getEditorClass()
	 */
	@Override
	public abstract Class<? extends RecordEditor<? extends Record>> getEditorClass();

	public abstract Element saveToXml(Element attachTo);

	@Override
	public final String toString() {
		return this.getName();
	}
}
