package com.gmail.at.kevinburnseit.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FileFormatException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4383809919754572345L;
	
	protected FileFormatException(Element e, String message) {
		this(FileFormatException.buildXpath(e) + "\n" + message);
	}
	
	protected FileFormatException(String newMessage) {
		super(newMessage);
	}

	public FileFormatException(Element e, String string, 
			@SuppressWarnings("unused") int magicNumber) {
		this(e, string);
	}

	protected static String buildXpath(Element e) {
		Element el = e;
		String ret = "";
		for (;;) {
			String id = null;
			if (el.hasAttribute("id")) id = el.getAttribute("id");
			ret = "/" + el.getTagName() + ((id != null) ? "#" + id : "") + ret;
			if (el.getParentNode() == null) break;
			if (el.getParentNode() instanceof Document) break;
			el = (Element)el.getParentNode();
		}
		
		return ret;
	}
}
