package com.gmail.at.kevinburnseit.organizer;

import java.util.HashMap;

import javax.swing.SwingWorker;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.gmail.at.kevinburnseit.records.ArrayListWithListModel;
import com.gmail.at.kevinburnseit.xml.XmlElementReader;
import com.gmail.at.kevinburnseit.xml.XmlHelper;

/**
 * A collection of external calendar definitions.
 * @author Kevin J. Burns
 *
 */
public class ExternalCalendarCollection extends 
		ArrayListWithListModel<ExternalCalendarDefinition> {
	private static final long serialVersionUID = 4868937778169264722L;

	/**
	 * Constructor. Creates a new collection.
	 */
	public ExternalCalendarCollection() {
	}
	
	/**
	 * Constructor. Creates a collection from a file stored on disk.
	 * @param filename Filename to read from
	 * @throws Exception if anything goes wrong
	 */
	public ExternalCalendarCollection(String filename) throws Exception {
		Document doc = XmlHelper.readFile(filename);
		Element docElement = doc.getDocumentElement();
		XmlElementReader r = new XmlElementReader(docElement);
		
		for (Element e : r.getChildElements(ExternalCalendarDefinition.xmlTag)) {
			ExternalCalendarDefinition item =
					new ExternalCalendarDefinition(e);
			this.add(item);
		}
	}
	
	/**
	 * Saves this collection to an xml file.
	 * @param filename path to save to
	 * @throws Exception if anything goes wrong
	 */
	public void saveToXml(String filename) throws Exception {
		Document doc = XmlHelper.newDocument("calendars");
		Element docElement = doc.getDocumentElement();
		
		for (ExternalCalendarDefinition def : this) {
			def.saveToXml(docElement);
		}
		
		XmlHelper.saveFile(doc, filename);
	}
	
	/**
	 * Creates a worker thread to download all calendars from remote. The resultant
	 * worker thread spawns other worker threads to download the individual files.
	 * The resultant worker thread returns a HashMap, with the external calendar
	 * definitions as the keys and their location on disk as the values. Any remote
	 * calendar that can't download for any reason will return <code>null</code> as
	 * the value in the map. <b>Don't save this worker thread for re-use</b>, as 
	 * a SwingWorker can only be executed once.
	 * @param saveInFolder folder in which all downloaded files are to be saved.
	 * @return
	 */
	public SwingWorker<HashMap<ExternalCalendarDefinition, String>, Void>
			getRemoteCalendarFetcher(String saveInFolder) {
		return new SwingWorker<HashMap<ExternalCalendarDefinition, String>, Void>() {
			@Override
			protected HashMap<ExternalCalendarDefinition, String> doInBackground() 
					throws Exception {
				HashMap<ExternalCalendarDefinition, SwingWorker<String, Void>> 
						workers = new HashMap<>();
				HashMap<ExternalCalendarDefinition, Boolean> doneYet = new HashMap<>();
				for (ExternalCalendarDefinition def : ExternalCalendarCollection.this) {
					SwingWorker<String, Void> sw = 
							def.getRemoteCalendarFetcher(saveInFolder);
					sw.execute();
					workers.put(def, sw);
					doneYet.put(def, false);
				}
				
				for (;;) {
					for (ExternalCalendarDefinition cal : workers.keySet()) {
						SwingWorker<?, ?> w = workers.get(cal);
						doneYet.put(cal, w.isDone());
					}
					
					boolean done = true;
					int doneCount = 0;
					for (Boolean state : doneYet.values()) {
						done = done && state;
						if (state) doneCount++;
					}
					this.setProgress(doneCount * 100 / workers.size());
					if (done) break;
					
					Thread.sleep(100);
				}
				
				// all threads should be done by now
				HashMap<ExternalCalendarDefinition, String> ret = new HashMap<>();
				for (ExternalCalendarDefinition cal : workers.keySet()) {
					ret.put(cal, workers.get(cal).get()); 
				}
				
				return ret;
			}
		};
	}
	
	/**
	 * Searches through the collection to find an external calendar definition
	 * with the given uid
	 * @param uid unique identifier for external calendar definition
	 * @return the external calendar definition with the supplied uid if it exists;
	 * otherwise, returns null.
	 */
	public ExternalCalendarDefinition getByUid_rNull(int uid) {
		for (ExternalCalendarDefinition def : this) {
			if (def.getUid() == uid) return def;
		}
		
		return null;
	}
}
