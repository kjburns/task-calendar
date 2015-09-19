package com.gmail.at.kevinburnseit.organizer;

import org.w3c.dom.Element;

import com.gmail.at.kevinburnseit.xml.FileFormatException;
import com.gmail.at.kevinburnseit.xml.XmlElementReader;
import com.gmail.at.kevinburnseit.xml.XmlElementReader.AttributeValidator;
import com.gmail.at.kevinburnseit.xml.XmlElementWriter;

/**
 * Contains information about the user's standard work schedule for a day.
 * @author Kevin J. Burns
 *
 */
public class StandardWorkDay {
	private static final AttributeValidator<Integer> timeValidator = 
			new AttributeValidator<Integer>() {
		@Override
		public boolean validate(Integer value) {
			if (value < 0) return false;
			if (value >= 86400) return false;
			
			return true;
		}
		@Override
		public String getAcceptableRange() {
			return "[0, 86400)";
		}
	};
	public static final String xmlTag = "work-schedule";
	private DayEnum day;
	private boolean workingToday;
	private boolean takingLunchToday;
	private int startTime;
	private int endTime;
	private int lunchTime;
	private int lunchDurationMinutes;
	
	public StandardWorkDay(DayEnum day) {
		this.day = day;
	}
	
	public Element toXml(Element attachTo) {
		Element e = attachTo.getOwnerDocument().createElement(xmlTag);
		XmlElementWriter w = new XmlElementWriter(e);
		
		w.writeEnumAttribute("day", this.day);
		w.writeIntAttribute("start", this.startTime);
		w.writeIntAttribute("end", this.endTime);
		w.writeIntAttribute("lunch", this.lunchTime);
		w.writeIntAttribute("lunch-length", this.lunchDurationMinutes);
		w.writeBooleanAttribute("work", this.workingToday);
		w.writeBooleanAttribute("take-lunch", this.takingLunchToday);
		
		attachTo.appendChild(e);
		return e;
	}
	public static StandardWorkDay fromXml(Element from) throws FileFormatException {
		XmlElementReader r = new XmlElementReader(from);
		
		DayEnum d = r.getRequiredEnumAttribute("day", DayEnum.class);
		StandardWorkDay ret = new StandardWorkDay(d);
		
		ret.startTime = r.getRequiredIntAttribute("start", timeValidator);
		ret.endTime = r.getRequiredIntAttribute("end", timeValidator);
		ret.lunchTime = r.getRequiredIntAttribute("lunch", timeValidator);
		ret.workingToday = r.getRequiredBooleanAttribute("work");
		ret.takingLunchToday = r.getRequiredBooleanAttribute("take-lunch");
		ret.lunchDurationMinutes = r.getRequiredIntAttribute("lunch-length", 
				new AttributeValidator<Integer>() {
			@Override
			public boolean validate(Integer value) {
				if (value < 15) return false;
				if (value > 180) return false;
				return true;
			}
			@Override
			public String getAcceptableRange() {
				return "[15, 180]";
			}
		});
		
		return ret;
	}
	
	/**
	 * @return the workingToday
	 */
	public boolean isWorkingToday() {
		return workingToday;
	}
	/**
	 * @param workingToday the workingToday to set
	 */
	public void setWorkingToday(boolean workingToday) {
		this.workingToday = workingToday;
	}
	/**
	 * @return the takingLunchToday
	 */
	public boolean isTakingLunchToday() {
		return takingLunchToday;
	}
	/**
	 * @param takingLunchToday the takingLunchToday to set
	 */
	public void setTakingLunchToday(boolean takingLunchToday) {
		this.takingLunchToday = takingLunchToday;
	}
	/**
	 * @return the startTime
	 */
	public int getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	/**
	 * @return the endTime
	 */
	public int getEndTime() {
		return endTime;
	}
	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
	/**
	 * @return the lunchTime
	 */
	public int getLunchTime() {
		return lunchTime;
	}
	/**
	 * @param lunchTime the lunchTime to set
	 */
	public void setLunchTime(int lunchTime) {
		this.lunchTime = lunchTime;
	}
	/**
	 * @return the lunchDurationMinutes
	 */
	public int getLunchDurationMinutes() {
		return lunchDurationMinutes;
	}
	/**
	 * @param lunchDurationMinutes the lunchDurationMinutes to set
	 */
	public void setLunchDurationMinutes(int lunchDurationMinutes) {
		this.lunchDurationMinutes = lunchDurationMinutes;
	}
	/**
	 * @return the day
	 */
	public DayEnum getDay() {
		return day;
	}
}
