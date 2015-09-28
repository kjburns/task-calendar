package com.gmail.at.kevinburnseit.organizer;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.gmail.at.kevinburnseit.records.ArrayListWithListModel;
import com.gmail.at.kevinburnseit.xml.XmlElementReader;
import com.gmail.at.kevinburnseit.xml.XmlHelper;

/**
 * A collection of holiday rules.
 * @author Kevin J. Burns
 *
 */
public class HolidayRuleCollection extends ArrayListWithListModel<HolidayRule> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2214257369395648543L;
	
	/**
	 * Loads the list of holidays from an xml file on disk.
	 * @param path Path to xml file
	 * @return The list of holidays that were stored in the xml file, if there are no
	 * errors.
	 * @throws Exception if there are any errors
	 */
	public static HolidayRuleCollection fromXmlFile(String path) throws Exception {
		HolidayRuleCollection ret = new HolidayRuleCollection();
		Document doc = XmlHelper.readFile(path);
		
		Element docElement = doc.getDocumentElement();
		XmlElementReader r = new XmlElementReader(docElement);
		ArrayList<Element> ruleElements = r.getChildElements(HolidayRule.xmlTag);
		
		HolidayRuleFactory factory = new HolidayRuleFactory(ret);
		for (Element e : ruleElements) {
			HolidayRule rule = factory.fromXml(e);
			ret.add(rule);
		}
		
		return ret;
	}
	
	/**
	 * Saves this collection to an xml file.
	 * @param path location on disk to save the file
	 * @throws Exception if anything goes wrong
	 */
	public void saveToXml(String path) throws Exception {
		Document doc = XmlHelper.newDocument("holiday-rules");
		
		for (HolidayRule rule : this) {
			rule.saveToXml(doc.getDocumentElement());
		}
		
		XmlHelper.saveFile(doc, path);
	}

	/**
	 * Searches the collection for a holiday which has the supplied name.
	 * @param value The holiday name to look for
	 * @return The holiday rule which matches the supplied name, if one exists.
	 * Otherwise, returns <code>null</code>.
	 */
	public HolidayRule getByName(String value) {
		for (HolidayRule rule : this) {
			if (rule.getName().equals(value)) return rule;
		}
		
		return null;
	}
}
