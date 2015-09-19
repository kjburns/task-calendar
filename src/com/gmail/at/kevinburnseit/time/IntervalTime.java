package com.gmail.at.kevinburnseit.time;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

import com.gmail.at.kevinburnseit.records.Record;
import com.gmail.at.kevinburnseit.records.RecordEditor;
import com.gmail.at.kevinburnseit.util.RegexHelper;
import com.gmail.at.kevinburnseit.xml.FileFormatException;
import com.gmail.at.kevinburnseit.xml.XmlElementReader;
import com.gmail.at.kevinburnseit.xml.XmlElementReader.AttributeValidator;
import com.gmail.at.kevinburnseit.xml.XmlElementWriter;

public class IntervalTime implements Record {
	public static class InvalidTimeException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2812546708092875219L;
	}
	public static final String xmlTag = "period";
	private static final AttributeValidator<Integer> timeValidator = 
			new AttributeValidator<Integer>() {
				@Override
				public boolean validate(Integer value) {
					return ((value >= 0) && (value < 86400));
				}
				@Override
				public String getAcceptableRange() {
					return "[0, 86400)";
				}
	};
	private int begin;
	private int end;
	
	public IntervalTime(int begin, int end) throws InvalidTimeException {
		this.setBegin(begin);
		this.setEnd(end);
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) throws InvalidTimeException {
		if ((begin < 0) || (begin >= 86400)) {
			throw new InvalidTimeException();
		}
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) throws InvalidTimeException {
		if ((end < 0) || (end >= 86400)) {
			throw new InvalidTimeException();
		}
		this.end = end;
	}
	
	public boolean contains(int time) {
		if (this.end > this.begin) {
			return ((this.end - time) * (this.begin - time) <= 0);
		}
		else {
			return ((time >= this.begin) || (time <= this.end));
		}
	}
	
	public int getLength() {
		if (this.end > this.begin) return this.end - this.begin;
		else return (this.end + 86400) - this.begin;
	}

	/**
	 * Returns a string representing the interval as formatted time, HH:MM:SS
	 */
	@Override
	public String toString() {
		return formatTime(this.begin) + " to " + formatTime(this.end);
	}
	
	public static String formatTime(int seconds) {
		int hr = seconds / 3600;
		int min = seconds / 60 - 60 * hr;
		int sec = seconds % 60;
		
		return String.format("%02d:%02d:%02d", hr, min, sec);
	}
	
	/**
	 * Attempts to parse a time interval from a string. The string must be composed of two
	 * integers separated by a comma. The integers must be in the range [0, 86399].
	 * @param from the string to parse
	 * @return The time interval upon success, or <code>null</code> if there is a formatting error
	 * @throws InvalidTimeException if either bound of the interval is not in the proper range.
	 */
	public static IntervalTime parse(String from) throws InvalidTimeException {
		Pattern p = Pattern.compile("^" + RegexHelper.integerGroup + "," + RegexHelper.integerGroup + "$");
		Matcher m = p.matcher(from.trim());
		if (!m.matches()) return null;
		
		IntervalTime ret = null;
		try {
			ret = new IntervalTime(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2)));
		} catch (NumberFormatException e) {
			// this should never happen
		} catch (InvalidTimeException e) {
			throw e;
		}
		return ret;
	}
	
	public Element toXml(Element attachTo) {
		Element e = attachTo.getOwnerDocument().createElement(xmlTag);
		XmlElementWriter w = new XmlElementWriter(e);
		w.writeIntAttribute("begin", this.begin);
		w.writeIntAttribute("end", this.end);
		
		attachTo.appendChild(e);
		return e;
	}
	
	public static IntervalTime fromXml(Element from) throws FileFormatException {
		XmlElementReader r = new XmlElementReader(from);
		int start = r.getRequiredIntAttribute("begin", IntervalTime.timeValidator);
		int end = r.getRequiredIntAttribute("end", IntervalTime.timeValidator);
		try {
			return new IntervalTime(start, end);
		} catch (InvalidTimeException e) {
			// should never happen; potential errors caught in validator
			return null;
		}
	}
	
	@Override
	public Class<? extends RecordEditor<? extends Record>> getEditorClass() {
		return TimeIntervalEditor.class;
	}
}
