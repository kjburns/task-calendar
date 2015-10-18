package com.gmail.at.kevinburnseit.organizer;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.gmail.at.kevinburnseit.organizer.Appointment.TravelTimeEntry;
import com.gmail.at.kevinburnseit.swing.calendar.CalendarEntry;
import com.gmail.at.kevinburnseit.swing.calendar.CalendarEntryProvider;
import com.gmail.at.kevinburnseit.xml.XmlElementReader;
import com.gmail.at.kevinburnseit.xml.XmlHelper;

public class AppointmentList extends CalendarEntryProvider<CalendarEntry> {
	private static final long serialVersionUID = -4241114840717808881L;
	
	public AppointmentList() {
		
	}
	
	public AppointmentList(String xmlPath) throws Exception {
		Document doc = XmlHelper.readFile(xmlPath);
		Element docElement = doc.getDocumentElement();
		XmlElementReader r = new XmlElementReader(docElement);
		
		for (Element e : r.getChildElements(Appointment.xmlTag)) {
			Appointment a = new Appointment(e);
			this.add(a);
			for (CalendarEntry ce : a.getTravelEntries()) {
				this.add(ce);
			}
		}
	}
	
	public void saveToXml(String path) throws Exception {
		Document doc = XmlHelper.newDocument("appointments");
		Element docElement = doc.getDocumentElement();
		
		for (CalendarEntry ce : this) {
			if (ce instanceof Appointment) {
				Appointment a = (Appointment)ce;
				a.saveToXml(docElement);
			}
		}
		
		XmlHelper.saveFile(doc, path);
	}

	public Appointment getByUid_rNull(String uid) {
		for (CalendarEntry ce : this) {
			if (!(ce instanceof Appointment)) continue;
			Appointment a = (Appointment)ce;
			if (a.getUid().equals(uid)) return a;
		}
		
		return null;
	}
	
	public ArrayList<TravelTimeEntry> getTravelEntriesLinkedTo(Appointment a) {
		ArrayList<TravelTimeEntry> ret = new ArrayList<>();
		
		for (CalendarEntry ce : this) {
			if (!(ce instanceof TravelTimeEntry)) continue;
			TravelTimeEntry tte = (TravelTimeEntry)ce;
			if (tte.getLinkedAppointment() == a) ret.add(tte);
		}
		
		return ret;
	}
}
