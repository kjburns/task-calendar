package com.gmail.at.kevinburnseit.xml;

import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.gmail.at.kevinburnseit.util.RegexHelper;
import com.gmail.at.kevinburnseit.xml.NumericParseException.NumericTypeEnum;

public class XmlElementReader {
	public static interface AttributeValidator<T> {
		boolean validate(T value);
		String getAcceptableRange();
	}
	
	private Element e;
	public static final AttributeValidator<String> listOfPositiveDoubleValidator = 
			new AttributeValidator<String>() {
		@Override
		public boolean validate(String value) {
			String[] values = value.split(",");
			for (String v : values) {
				try {
					Double val = Double.valueOf(v);
					if (!positiveRealValidator.validate(val)) return false;
				} catch (NumberFormatException e) {
					return false;
				}
			}
			return true;
		}
		@Override
		public String getAcceptableRange() {
			return null;
		}
	};
	public static final AttributeValidator<String> listOfPositiveIntegerValidator = 
			new AttributeValidator<String>() {
		@Override
		public boolean validate(String value) {
			String[] values = value.split(",");
			for (String v : values) {
				try {
					int val = Integer.valueOf(v);
					if (!positiveIntegerValidator.validate(val)) return false;
				} catch (NumberFormatException e) {
					return false;
				}
			}
			return true;
		}
		@Override
		public String getAcceptableRange() {
			return null;
		}
	};
	public static AttributeValidator<String> xmlTimeValidator = new AttributeValidator<String>() {
		@Override
		public boolean validate(String value) {
			Pattern pt = Pattern.compile("^" + RegexHelper.xmlTime + "$");
			Matcher m = pt.matcher(value);
			return m.matches();
		}
		@Override
		public String getAcceptableRange() {
			return null;
		}
	};
	public static final AttributeValidator<Double> positiveRealValidator = 
			new AttributeValidator<Double>() {
		@Override
		public boolean validate(Double value) {
			return (value > 0.);
		}
		@Override
		public String getAcceptableRange() {
			return ">0.0";
		}
	};
	public static final AttributeValidator<Integer> nonNegativeIntegerValidator = 
			new AttributeValidator<Integer>() {
		@Override
		public boolean validate(Integer value) {
			return (value >= 0);
		}
		@Override
		public String getAcceptableRange() {
			return ">=0";
		}
	};
	public static final AttributeValidator<String> nonEmptyStringValidator = 
			new AttributeValidator<String>() {
		@Override
		public boolean validate(String value) {
			return (value.trim().length() > 0);
		}
		@Override
		public String getAcceptableRange() {
			return "Only non-empty strings are allowed.";
		}
	};
	public static final AttributeValidator<Double> nonNegativeRealValidator = 
			new AttributeValidator<Double>() {
		@Override
		public boolean validate(Double value) {
			return (value >= 0.);
		}
		@Override
		public String getAcceptableRange() {
			return ">=0";
		}
	};
	public static final AttributeValidator<Integer> positiveIntegerValidator = 
			new AttributeValidator<Integer>() {
		@Override
		public boolean validate(Integer value) {
			return (value > 0);
		}
		@Override
		public String getAcceptableRange() {
			return ">0";
		}
	};
	
	public XmlElementReader(Element e) {
		this.e = e;
	}
	
	public String getRequiredStringAttribute(String name) throws FileFormatException {
		return this.getRequiredStringAttribute(name, null);
	}
	
	public String getRequiredStringAttribute(String name, AttributeValidator<String> v) throws FileFormatException {
		if (this.e.hasAttribute(name)) return this.getOptionalStringAttribute(name, v, null);
		else {
			throw new MissingAttributeException(this.e, name);
		}
	}
	
	public String getOptionalStringAttribute(String name, String noValue) throws InvalidAttributeException {
		return this.getOptionalStringAttribute(name, null, noValue);
	}
	
	public String getOptionalStringAttribute(String name, AttributeValidator<String> v, String noValue) throws InvalidAttributeException {
		if (!this.e.hasAttribute(name)) return noValue;
		String value = this.e.getAttribute(name);
		if (v != null) {
			if (v.validate(value)) return value;
			else throw new InvalidAttributeException(this.e, name, value);
		}
		else return value;
	}
	
	public int getRequiredIntAttribute(String name) throws FileFormatException {
		return this.getRequiredIntAttribute(name, null);
	}

	public int getRequiredIntAttribute(String name, AttributeValidator<Integer> v) throws FileFormatException {
		if (this.e.hasAttribute(name)) return this.getOptionalIntAttribute(name, v, -1);
		else throw new MissingAttributeException(e, name);
	}
	
	public int getOptionalIntAttribute(String name, int noValue) throws FileFormatException {
		int v = noValue;
		try {
			v = this.getOptionalIntAttribute(name, null, noValue);
		} catch (InvalidAttributeException e1) {
		}
		return v;
	}
	public int getOptionalIntAttribute(String name, AttributeValidator<Integer> v, int noValue) 
			throws FileFormatException {
		if (!this.e.hasAttribute(name)) return noValue;
		int value;
		try {
			value = Integer.valueOf(e.getAttribute(name));
		} catch (NumberFormatException e1) {
			throw new NumericParseException(e, name, NumericTypeEnum.INT);
		}
		if (v != null) {
			if (v.validate(value)) return value;
			else throw new InvalidAttributeException(this.e, name, v.getAcceptableRange());
		}
		else return value;
	}

	public double getRequiredDoubleAttribute(String name) throws FileFormatException {
		return this.getRequiredDoubleAttribute(name, null);
	}
	
	public double getRequiredDoubleAttribute(String name, AttributeValidator<Double> v) throws FileFormatException {
		if (e.hasAttribute(name)) return this.getOptionalDoubleAttribute(name, v, Double.NaN);
		else throw new MissingAttributeException(e, name);
	}
	
	public double getOptionalDoubleAttribute(String name, double noValue) throws FileFormatException {
		double value = noValue;
		try {
			value = this.getOptionalDoubleAttribute(name, null, noValue);
		} catch (InvalidAttributeException e1) {
			// this should never happen
		}
		return value;
	}
	
	public double getOptionalDoubleAttribute(String name, AttributeValidator<Double> v, double noValue) 
			throws FileFormatException {
		if (!this.e.hasAttribute(name)) return noValue;
		
		double value;
		try {
			value = Double.valueOf(e.getAttribute(name));
		} catch (NumberFormatException e1) {
			throw new NumericParseException(e, name, NumericTypeEnum.DOUBLE);
		}
		if (v != null) {
			if (v.validate(value)) return value;
			else throw new InvalidAttributeException(this.e, name, v.getAcceptableRange());
		}
		else return value;
	}
	
	public <T extends Enum<T>> T getRequiredEnumAttribute(String name, Class<T> enumType)
			throws FileFormatException {
		if (!e.hasAttribute(name)) throw new MissingAttributeException(e, name);
		else return this.getOptionalEnumAttribute(name, enumType, null); 
	}
	
	public <T extends Enum<T>> T getOptionalEnumAttribute(String name, Class<T> enumType, T noValue) 
			throws InvalidAttributeException {
		if (!e.hasAttribute(name)) return noValue;
		T ret;
		try {
			ret = Enum.valueOf(enumType, e.getAttribute(name));
		} catch (Exception e2) {
			throw new InvalidAttributeException(e, name, null);
		}
		return ret;
	}
	
	public boolean getRequiredBooleanAttribute(String name) throws MissingAttributeException {
		if (!e.hasAttribute(name)) throw new MissingAttributeException(e, name);
		return this.getOptionalBooleanAttribute(name, false);
	}
	
	public boolean getOptionalBooleanAttribute(String name, boolean noValue) {
		if (!e.hasAttribute(name)) return noValue;
		boolean ret = Boolean.valueOf(e.getAttribute(name));
		return ret;
	}
	
	@Deprecated public Vector<Element> getChildElementsAsVector(String tagName) {
		Vector<Element> ret = new Vector<Element>();
		
		NodeList nl = this.e.getElementsByTagName(tagName);
		for (int i = 0; i < nl.getLength(); i++) {
			Element el = (Element)nl.item(i);
			if (el.getParentNode() == this.e) ret.add(el);
		}
		
		return ret;
	}
	
	@Deprecated public Vector<Element> getChildElementsAsVector() {
		return this.getChildElementsAsVector("*");
	}

	public ArrayList<Element> getChildElements(String tagName) {
		ArrayList<Element> ret = new ArrayList<Element>();
		
		NodeList nl = this.e.getElementsByTagName(tagName);
		for (int i = 0; i < nl.getLength(); i++) {
			Element el = (Element)nl.item(i);
			if (el.getParentNode() == this.e) ret.add(el);
		}
		
		return ret;
	}
	
	public ArrayList<Element> getChildElements() {
		return this.getChildElements("*");
	}

	public Element getElement() {
		return this.e;
	}
}
