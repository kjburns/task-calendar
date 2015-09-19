package com.gmail.at.kevinburnseit.organizer;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.gmail.at.kevinburnseit.xml.XmlElementReader;
import com.gmail.at.kevinburnseit.xml.XmlHelper;

/**
 * Contains information to describe the user's normal weekly work schedule.
 * @author Kevin J. Burns
 *
 */
public class StandardWorkWeek extends HashMap<DayEnum, StandardWorkDay>{
	private static final long serialVersionUID = -636014377217965162L;

	/**
	 * Constructor. Creates an empty work week. Days will need to be added manually.
	 */
	public StandardWorkWeek() {
		// does nothing
	}
	
	/**
	 * Constructor. Creates a work week schedule from the contents of an xml file on
	 * disk.
	 * @param pathToXmlFile Location of the xml file containing the work schedule
	 * @throws Exception If there is any problem reading the file
	 */
	public StandardWorkWeek(String pathToXmlFile) throws Exception {
		Document doc = XmlHelper.readFile(pathToXmlFile);
		Element root = doc.getDocumentElement();
		XmlElementReader r = new XmlElementReader(root);
		
		ArrayList<Element> dayElements = r.getChildElements(StandardWorkDay.xmlTag);
		for (Element e : dayElements) {
			StandardWorkDay schedule = StandardWorkDay.fromXml(e);
			this.put(schedule.getDay(), schedule);
		}
	}
	
	public void saveToXmlFile(String pathToXmlFile) throws Exception {
		Document doc = XmlHelper.newDocument("work-week");
		Element documentElement = doc.getDocumentElement();

		for (StandardWorkDay day : this.values()) {
			day.toXml(documentElement);
		}
		
		XmlHelper.saveFile(doc, pathToXmlFile);
	}
}
