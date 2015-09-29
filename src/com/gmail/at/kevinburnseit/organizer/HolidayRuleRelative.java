package com.gmail.at.kevinburnseit.organizer;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.w3c.dom.Element;

import com.gmail.at.kevinburnseit.organizer.gui.HolidayEditorRelative;
import com.gmail.at.kevinburnseit.records.Record;
import com.gmail.at.kevinburnseit.records.RecordEditor;
import com.gmail.at.kevinburnseit.xml.XmlElementWriter;

/**
 * A holiday that occurs a fixed number of days before or after another holiday.
 * @author Kevin J. Burns
 *
 */
public class HolidayRuleRelative extends HolidayRule {
	static final String ruleType = "relative";
	private HolidayRule reference;
	private int daysAfterReference;
	
	@Override
	public GregorianCalendar getNormalDateOfHoliday(int year) {
		GregorianCalendar ret = this.reference.getNormalDateOfHoliday(year);
		ret.add(Calendar.DAY_OF_MONTH, this.daysAfterReference);
		
		return ret;
	}

	@Override
	public Class<? extends RecordEditor<? extends Record>> getEditorClass() {
		return HolidayEditorRelative.class;
	}

	@Override
	public Element saveToXml(Element attachTo) {
		Element e = this.beginSaveToXml(attachTo.getOwnerDocument(), ruleType);
		XmlElementWriter w = new XmlElementWriter(e);
		
		w.writeStringAttribute("ref", this.reference.getName());
		w.writeIntAttribute("days-after", this.daysAfterReference);
		
		attachTo.appendChild(e);
		return e;
	}

	/**
	 * Gets the holiday that this holiday is referenced to.
	 * @return
	 */
	public final HolidayRule getReference() {
		return reference;
	}

	/**
	 * Sets the holiday that this holiday is referenced to.
	 * @param reference the reference to set
	 */
	public final void setReference(HolidayRule reference) {
		this.reference = reference;
	}

	/**
	 * Gets the number of days that this holiday occurs after the reference.
	 * @return Number of days that this holiday occurs after the reference. If this
	 * holiday occurs before the reference, the return value will be negative.
	 */
	public final int getDaysAfterReference() {
		return daysAfterReference;
	}

	/**
	 * Sets the number of days that this holiday occurs after the reference holiday.
	 * @param daysAfterReference A positive number indicates that this holiday occurs
	 * after the reference holiday; a negative number indicates that this holiday occurs
	 * before the reference holiday.
	 */
	public final void setDaysAfterReference(int daysAfterReference) {
		this.daysAfterReference = daysAfterReference;
	}
}
