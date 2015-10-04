package com.gmail.at.kevinburnseit.organizer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.gmail.at.kevinburnseit.records.ArrayListWithListModel;
import com.gmail.at.kevinburnseit.swing.calendar.CalendarHelper;
import com.gmail.at.kevinburnseit.xml.XmlElementReader;
import com.gmail.at.kevinburnseit.xml.XmlElementWriter;
import com.gmail.at.kevinburnseit.xml.XmlHelper;

/**
 * A list of days off which has a ListModel and can be retrieved from and saved to xml.
 * @author Kevin J. Burns
 *
 */
public class DaysOffList extends ArrayListWithListModel<DateRecord> {
	private static final long serialVersionUID = 1288675801079732118L;
	private final SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");

	/**
	 * Constructor. Creates an empty list of dates.
	 */
	public DaysOffList() {
		// does nothing
	}
	
	/**
	 * Constructor. Creates a list of dates from an xml file.
	 * @param fromFile The xml file to use as a source
	 * @throws Exception if anything at all goes wrong.
	 */
	public DaysOffList(String fromFile) throws Exception {
		Document doc = XmlHelper.readFile(fromFile);
		XmlElementReader r = new XmlElementReader(doc.getDocumentElement());
		ArrayList<Element> dateElements = r.getChildElements("date");
		
		for (Element dateElement : dateElements) {
			XmlElementReader r2 = new XmlElementReader(dateElement);
			Date date = this.df.parse(r2.getRequiredStringAttribute("date"));
			this.add(new DateRecord(date));
		}
	}
	
	/**
	 * Saves this list to an xml file.
	 * @param path Where to save the xml file
	 * @throws Exception If anything at all goes wrong.
	 */
	public void saveToXml(String path) throws Exception {
		Document doc = XmlHelper.newDocument("days-off");
		Element docElement = doc.getDocumentElement();
		
		for (Date date : this) {
			Element e = doc.createElement("date");
			XmlElementWriter w = new XmlElementWriter(e);
			w.writeStringAttribute("date", this.df.format(date));
			docElement.appendChild(e);
		}
		
		XmlHelper.saveFile(doc, path);
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		if (o == null) return false;
		if (!(o instanceof DateRecord)) return false;
		
		DateRecord find = (DateRecord)o;
		for (DateRecord d : this) {
			if (CalendarHelper.areDatesEqual(find, d)) return true;
		}
		
		return false;
	}
}
