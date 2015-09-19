package com.gmail.at.kevinburnseit.xml;

import org.w3c.dom.Element;

public class MissingAttributeException extends FileFormatException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7471365413454531659L;

	public MissingAttributeException(Element e, String attrName) {
		super(MissingAttributeException.getNewMessage(e, attrName));
	}
	
	private static String getNewMessage(Element e, String attrName) {
		String msg = "Required attribute was missing: ";
		int firstBlank = msg.indexOf("$1");
		int secondBlank = msg.indexOf("$2");
		
		String newMsg = msg.substring(0, firstBlank) + FileFormatException.buildXpath(e) + 
				msg.substring(firstBlank + 2, secondBlank) + "@" + attrName + 
				msg.substring(secondBlank + 2);

		return newMsg;
	}
}
