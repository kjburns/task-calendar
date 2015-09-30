package com.gmail.at.kevinburnseit.organizer;

import org.w3c.dom.Element;

import com.gmail.at.kevinburnseit.swing.calendar.CalendarHelper;
import com.gmail.at.kevinburnseit.xml.FileFormatException;
import com.gmail.at.kevinburnseit.xml.XmlElementReader;
import com.gmail.at.kevinburnseit.xml.XmlElementReader.AttributeValidator;

/**
 * A utility class for loading holiday rules from xml elements. This class is
 * necessary because there are many flavors of holiday rules which use the same
 * xml element tag and this class will sort out the flavors.
 * @author Kevin J. Burns
 *
 */
public class HolidayRuleFactory {
	private HolidayRuleCollection collection;
	
	private static final AttributeValidator<Integer> monthValidator =
			new AttributeValidator<Integer>() {
		@Override
		public boolean validate(Integer value) {
			return ((value >= 0) && (value <= 11));
		}
		@Override
		public String getAcceptableRange() {
			return "[0, 11]";
		}
	};
	private static final AttributeValidator<Integer> dayOfWeekValidator =
			new AttributeValidator<Integer>() {
		@Override
		public boolean validate(Integer value) {
			return ((value >= 0) && (value <= 6));
		}
		@Override
		public String getAcceptableRange() {
			return "[0, 6]";
		}
	};
	private static final AttributeValidator<Integer> dayOfWeekInMonthValidator =
			new AttributeValidator<Integer>() {
		@Override
		public boolean validate(Integer value) {
			return ((value >= 1) && (value <= HolidayRuleNthDay.LAST_OCCURENCE));
		}
		@Override
		public String getAcceptableRange() {
			return "[1, " + HolidayRuleNthDay.LAST_OCCURENCE + "]";
		}
	};
	
	private static final class DayValidator implements AttributeValidator<Integer> {
		int month;
		
		public DayValidator(int withinMonth) {
			this.month = withinMonth;
		}
		
		@Override
		public boolean validate(Integer value) {
			return ((value >= 1) && 
					(value <= CalendarHelper.getDaysInMonth(month, false)));
		}

		@Override
		public String getAcceptableRange() {
			return "[1, " + CalendarHelper.getDaysInMonth(month, false) + "]";
		}
	}
	
	private final AttributeValidator<String> holidayExistsValidator =
			new AttributeValidator<String>() {
		@Override
		public boolean validate(String value) {
			HolidayRule rule = collection.getByName(value);
			return (rule != null);
		}

		@Override
		public String getAcceptableRange() {
			return null;
		}
	};
	
	/**
	 * Constructor.
	 * @param c Collection that this is being read into. Some holiday rule types
	 * must make reference to the collection.
	 */
	public HolidayRuleFactory(HolidayRuleCollection c) {
		this.collection = c;
	}
	
	/**
	 * Parses an xml element and creates a HolidayRule, if possible.
	 * @param from xml element to parse
	 * @return A holiday rule, if a valid one can be created from the supplied 
	 * xml element. If no rule can be created, returns <code>null</code>.
	 * @throws FileFormatException If there are issues with the document structure
	 */
	public HolidayRule fromXml(Element from) throws FileFormatException {
		XmlElementReader r = new XmlElementReader(from);
		
		String name = r.getRequiredStringAttribute("name", 
				XmlElementReader.nonEmptyStringValidator);
		boolean weekdayOnly = r.getRequiredBooleanAttribute("observe-on-weekday-only");
		String type = r.getRequiredStringAttribute("type");
		
		if (HolidayRuleFixedDay.ruleType.equals(type)) {
			HolidayRuleFixedDay ret = new HolidayRuleFixedDay();
			ret.setName(name);
			ret.setAlwaysObservedOnWeekday(weekdayOnly);
			int month = r.getRequiredIntAttribute("month", monthValidator);
			ret.setMonth(month);
			ret.setDay(r.getRequiredIntAttribute("day", new DayValidator(month)));
			return ret;
		}
		if (HolidayRuleRelative.ruleType.equals(type)) {
			HolidayRuleRelative ret = new HolidayRuleRelative();
			ret.setName(name);
			ret.setAlwaysObservedOnWeekday(weekdayOnly);
			String refName = r.getRequiredStringAttribute("ref", 
					this.holidayExistsValidator);
			ret.setReference(this.collection.getByName(refName));
			ret.setDaysAfterReference(r.getRequiredIntAttribute("days-after"));
			
			return ret;
		}
		if (HolidayRuleNthDay.ruleType.equals(type)) {
			HolidayRuleNthDay ret = new HolidayRuleNthDay();
			ret.setName(name);
			ret.setAlwaysObservedOnWeekday(weekdayOnly);
			ret.setDayOfWeek(r.getRequiredIntAttribute("day-of-week", 
					dayOfWeekValidator));
			ret.setMonth(r.getRequiredIntAttribute("month", monthValidator));
			ret.setWhichOccurence(r.getRequiredIntAttribute("which", 
					dayOfWeekInMonthValidator));
			
			return ret;
		}
		
		return null;
	}
}
