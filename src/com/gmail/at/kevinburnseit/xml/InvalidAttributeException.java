package com.gmail.at.kevinburnseit.xml;

import org.w3c.dom.Element;

public class InvalidAttributeException extends FileFormatException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7403828803550465117L;

	public InvalidAttributeException(Element e, String attr, String required) {
		super(e, InvalidAttributeException.getNewMessage(e, attr, required));
	}

	private static String getNewMessage(Element e, String attrName, String required) {
		String msg = "Invalid attribute:";
		int firstBlank = msg.indexOf("$1");
		int secondBlank = msg.indexOf("$2");
		int thirdBlank = msg.indexOf("$3");

		String newMsg = msg.substring(0, firstBlank) + FileFormatException.buildXpath(e) + 
				msg.substring(firstBlank + 2, secondBlank) + "@" + attrName + 
				msg.substring(secondBlank + 2, thirdBlank) + required;

		return newMsg;
	}
}