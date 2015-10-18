package com.gmail.at.kevinburnseit.organizer;

import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.w3c.dom.Element;

import com.gmail.at.kevinburnseit.swing.calendar.CalendarEntry;
import com.gmail.at.kevinburnseit.swing.calendar.CalendarHelper;
import com.gmail.at.kevinburnseit.xml.FileFormatException;
import com.gmail.at.kevinburnseit.xml.NumericParseException;
import com.gmail.at.kevinburnseit.xml.XmlElementReader;
import com.gmail.at.kevinburnseit.xml.XmlElementWriter;

import net.fortuna.ical4j.model.component.VEvent;

public class Appointment extends CalendarEntry
		implements OrganizerEntry {
	enum BeforeAfterEnum {
		BEFORE,
		AFTER;
	}
	
	public class TravelTimeEntry extends CalendarEntry 
			implements OrganizerEntry {
		private BeforeAfterEnum when;
		public TravelTimeEntry(BeforeAfterEnum when) {
			this.when = when;
			
			String title = "Travel ";
			title += (this.when == BeforeAfterEnum.BEFORE) ? "to" : "from";
			title += " ";
			title += CalendarHelper.militaryTimeFormatter.format(
					Appointment.this.meetingStartTime);
			title += " appointment";
			
			this.setTitle(title);
			this.update();
		}
		@Override
		public Color getColor() {
			return Appointment.this.getColor();
		}
		@Override
		public boolean shouldBeSaved() {
			return false;
		}
		void update() {
			if (this.when == BeforeAfterEnum.BEFORE) {
				GregorianCalendar end = getGC(meetingStartTime);
				this.setEndTime(end);
				
				GregorianCalendar begin = new GregorianCalendar();
				begin.setTime(end.getTime());
				begin.add(Calendar.SECOND, travelTimeBefore);
				this.setStartTime(begin);
			}
			if (this.when == BeforeAfterEnum.AFTER) {
				GregorianCalendar begin = getGC(meetingEndTime);
				this.setStartTime(begin);
				
				GregorianCalendar end = new GregorianCalendar();
				end.setTime(begin.getTime());
				end.add(Calendar.SECOND, travelTimeAfter);
				this.setEndTime(end);
			}
		}
		public Appointment getLinkedAppointment() {
			return Appointment.this;
		}
	}
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat();
	public static final String xmlTag = "appointment";
	
	private String uid;
	private Date meetingStartTime;
	private Date meetingEndTime;
	private int travelTimeBefore = 0;
	private int travelTimeAfter = 0;
	private int prepTime = 0;
	private int debriefTime = 0;
	private boolean ignored = false;
	private TravelTimeEntry travelBefore = null;
	private TravelTimeEntry travelAfter = null;

	public Appointment(VEvent ics) {
		this.uid = ics.getUid().getValue();
		this.update(ics);
	}
	
	public Appointment(Element from) throws FileFormatException {
		XmlElementReader r = new XmlElementReader(from);
		
		this.uid = r.getRequiredStringAttribute("uid", 
				XmlElementReader.nonEmptyStringValidator);
		try {
			this.meetingStartTime = dateFormatter.parse(
					r.getRequiredStringAttribute("mtg-start"));
		} catch (ParseException e) {
			throw new NumericParseException(from, "mtg-start", null);
		}
		this.setStartTime(getGC(this.meetingStartTime));
		try {
			this.meetingEndTime = dateFormatter.parse(
					r.getRequiredStringAttribute("mtg-end"));
		} catch (ParseException e) {
			throw new NumericParseException(from, "mtg-end", null);
		}
		this.setEndTime(getGC(this.meetingEndTime));
		this.prepTime = r.getRequiredIntAttribute("prep", 
				XmlElementReader.nonNegativeIntegerValidator);
		this.debriefTime = r.getRequiredIntAttribute("debrief", 
				XmlElementReader.nonNegativeIntegerValidator);
		this.setTravelTimeBefore(r.getRequiredIntAttribute("travel-before", 
				XmlElementReader.nonNegativeIntegerValidator));
		this.setTravelTimeAfter(r.getRequiredIntAttribute("travel-after", 
				XmlElementReader.nonNegativeIntegerValidator));
		this.ignored = r.getOptionalBooleanAttribute("ignore", false);
		this.setTitle(r.getOptionalStringAttribute("title", ""));
	}
	
	public Element saveToXml(Element attachTo) {
		Element e = attachTo.getOwnerDocument().createElement(xmlTag);
		XmlElementWriter w = new XmlElementWriter(e);
		
		w.writeStringAttribute("uid", this.uid);
		w.writeStringAttribute("mtg-start", 
				dateFormatter.format(this.meetingStartTime));
		w.writeStringAttribute("mtg-end", dateFormatter.format(this.meetingEndTime));
		w.writeIntAttribute("prep", this.prepTime);
		w.writeIntAttribute("debrief", this.debriefTime);
		w.writeIntAttribute("travel-before", this.travelTimeBefore);
		w.writeIntAttribute("travel-after", this.travelTimeAfter);
		w.writeBooleanAttribute("ignore", this.ignored);
		w.writeStringAttribute("title", this.getTitle());
		
		attachTo.appendChild(e);
		return e;
	}
	
	/**
	 * Gets the unique id for this appointment. This is the same uid in the source
	 * ics calendar.
	 * @return the uid
	 */
	public final String getUid() {
		return uid;
	}
	
	private GregorianCalendar getGC(Date date) {
		GregorianCalendar ret = new GregorianCalendar();
		ret.setTime(date);
		return ret;
	}
	/**
	 * Gets the travel time before the appointment, in seconds.
	 * @return the travelTimeBefore
	 */
	public final int getTravelTimeBefore() {
		return travelTimeBefore;
	}
	/**
	 * Sets the travel time before the appointment, in seconds. If less than zero,
	 * no changes are made.
	 * @param travelTimeBefore the travelTimeBefore to set
	 */
	public final void setTravelTimeBefore(int travelTimeBefore) {
		if (travelTimeBefore < 0) return;
		
		this.travelTimeBefore = travelTimeBefore;
		createOrUpdateTravelTimeBefore();
	}

	private void createOrUpdateTravelTimeBefore() {
		if (this.travelTimeBefore > 0) {
			if (this.travelBefore == null) {
				this.travelBefore = new TravelTimeEntry(BeforeAfterEnum.BEFORE);
			}
			else {
				this.travelBefore.update();
			}
		}
	}
	/**
	 * Gets the travel time after the appointment, in seconds.
	 * @return the travelTimeAfter
	 */
	public final int getTravelTimeAfter() {
		return travelTimeAfter;
	}
	/**
	 * Sets the travel time after the appointment, in seconds. If less than zero,
	 * no changes are made.
	 * @param travelTimeAfter the travelTimeAfter to set
	 */
	public final void setTravelTimeAfter(int travelTimeAfter) {
		if (travelTimeAfter < 0) return;
		
		this.travelTimeAfter = travelTimeAfter;
		createOrUpdateTravelTimeAfter();
	}

	private void createOrUpdateTravelTimeAfter() {
		if (this.travelTimeAfter > 0) {
			if (this.travelAfter == null) {
				this.travelAfter = new TravelTimeEntry(BeforeAfterEnum.AFTER);
			}
			else {
				this.travelAfter.update();
			}
		}
	}
	/**
	 * Gets the preparation time, in seconds, that the user has specified for a meeting.
	 * @return the prepTime
	 */
	public final int getPrepTime() {
		return prepTime;
	}
	/**
	 * Sets the preparation time, in seconds, for this meeting. 
	 * @param prepTime the prepTime to set
	 */
	public final void setPrepTime(int prepTime) {
		this.prepTime = prepTime;
	}
	/**
	 * Gets the debrief time, in seconds, for this meeting.
	 * @return the debriefTime
	 */
	public final int getDebriefTime() {
		return debriefTime;
	}
	/**
	 * Sets the debrief time, in seconds, for this meeting.
	 * @param debriefTime the debriefTime to set
	 */
	public final void setDebriefTime(int debriefTime) {
		this.debriefTime = debriefTime;
	}
	
	@Override
	public Color getColor() {
		int hash = this.uid.hashCode();
		
		int b = hash % 64;
		hash -= b;
		hash /= 64;
		
		int g = hash % 64;
		hash -= g;
		hash /= 64;
		
		int r = hash % 64;
		
		return new Color(r + 192, g + 192, b + 192);
	}

	/**
	 * Returns whether the user has elected to ignore this appointment.
	 * @return <code>true</code> if the user has elected to ignore this appointment;
	 * <code>false</code> otherwise.
	 */
	public final boolean isIgnored() {
		return ignored;
	}

	/**
	 * Sets a flag regarding whether the user has ignored this appointment.
	 * @param ignored <code>true</code> if the user has elected to ignore this
	 * appointment; <code>false</code> otherwise.
	 */
	public final void setIgnored(boolean ignored) {
		this.ignored = ignored;
	}
	
	public ArrayList<CalendarEntry> getTravelEntries() {
		ArrayList<CalendarEntry> ret = new ArrayList<>();
		
		if (this.travelTimeBefore > 0) {
			this.createOrUpdateTravelTimeBefore();
			ret.add(this.travelBefore);
		}
		if (this.travelTimeAfter > 0) {
			this.createOrUpdateTravelTimeAfter();
			ret.add(this.travelAfter);
		}
		
		return ret;
	}

	@Override
	public boolean shouldBeSaved() {
		return true;
	}

	public void update(VEvent ics) {
		this.meetingStartTime = ics.getStartDate().getDate();
		this.meetingEndTime = ics.getEndDate().getDate();
		this.setStartTime(this.getGC(this.meetingStartTime));
		this.setEndTime(this.getGC(this.meetingEndTime));
		this.setTitle(ics.getSummary().getValue());
		
		this.createOrUpdateTravelTimeBefore();
		this.createOrUpdateTravelTimeAfter();
	}
}
