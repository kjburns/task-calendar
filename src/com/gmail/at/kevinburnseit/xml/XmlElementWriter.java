package com.gmail.at.kevinburnseit.xml;

import org.w3c.dom.Element;

/**
 * A utility class for writing attributes to an xml element.
 * @author Kevin J. Burns
 *
 */
public class XmlElementWriter {
	private Element e; 
	
	public XmlElementWriter(Element writeTo) {
		this.e = writeTo;
	}
	
	public void writeStringAttribute(String attrName, String value) {
		this.e.setAttribute(attrName, value);
	}
	
	public void writeBooleanAttribute(String attrName, boolean value) {
		this.writeStringAttribute(attrName, String.valueOf(value));
	}
	
	public void writeIntAttribute(String attrName, int value) {
		this.writeStringAttribute(attrName, String.valueOf(value));
	}
	
	public void writeFloatAttribute(String attrName, float value) {
		this.writeStringAttribute(attrName, String.valueOf(value));
	}
	
	public void writeDoubleAttribute(String attrName, double value) {
		this.writeStringAttribute(attrName, String.valueOf(value));
	}
	
	public void writeEnumAttribute(String attrName, Enum<?> value) {
		this.writeStringAttribute(attrName, value.name());
	}
}
