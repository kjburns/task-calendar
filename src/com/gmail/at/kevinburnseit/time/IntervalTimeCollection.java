package com.gmail.at.kevinburnseit.time;

import java.util.ArrayList;
import org.w3c.dom.Element;

import com.gmail.at.kevinburnseit.xml.FileFormatException;
import com.gmail.at.kevinburnseit.xml.XmlElementReader;

/*
 * FIXME Originally, this class extended ListModelList. May need to create an
 * equivalent here based on how things work out.
 */
public class IntervalTimeCollection extends ArrayList<IntervalTime> {
	private static final long serialVersionUID = -5528941381303604141L;
	public static final String xmlTag = IntervalTime.xmlTag + "-list";
	public boolean appliesNow(int clock) { 
		for (IntervalTime it : this) {
			if (it.contains(clock)) return true;
		}
		return false; 
	}
	
	public static IntervalTimeCollection fromXml(Element from) throws FileFormatException {
		XmlElementReader r = new XmlElementReader(from);
		IntervalTimeCollection ret = new IntervalTimeCollection();
		
		ArrayList<Element> elements = r.getChildElements(IntervalTime.xmlTag);
		for (Element e : elements) {
			IntervalTime it = IntervalTime.fromXml(e);
			ret.add(it);
		}
		
		return ret;
	}
	
	public Element toXml(Element attachTo) {
		Element e = attachTo.getOwnerDocument().createElement(xmlTag);
		
		for (IntervalTime it : this) {
			it.toXml(e);
		}
		
		attachTo.appendChild(e);
		return e;
	}
}
