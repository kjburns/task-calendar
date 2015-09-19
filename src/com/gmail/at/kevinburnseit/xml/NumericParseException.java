package com.gmail.at.kevinburnseit.xml;

import org.w3c.dom.Element;

public class NumericParseException extends FileFormatException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1409883812958325264L;

	public enum NumericTypeEnum {
		INT,
		DOUBLE;
	}
	public NumericParseException(Element e, String attrName, NumericTypeEnum type) {
		super(e, NumericParseException.getErrorMessage(e, attrName, type));
	}

	private static String getErrorMessage(Element e, String attrName, NumericTypeEnum type) {
		String msg;
		
		switch(type) {
		case INT:
			msg = "Error parsing integer.";
			break;
		case DOUBLE:
			msg = "Error parsing double.";
			break;
		default:
			msg = "Error parsing value.";	
		}
		
		int firstBlank = msg.indexOf("$1");
		int secondBlank = msg.indexOf("$2");

		String newMsg = msg.substring(0, firstBlank) + FileFormatException.buildXpath(e) + 
				msg.substring(firstBlank + 2, secondBlank) + "@" + attrName + 
				msg.substring(secondBlank + 2);

		return newMsg;
	}
}
