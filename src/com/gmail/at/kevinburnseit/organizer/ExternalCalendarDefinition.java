package com.gmail.at.kevinburnseit.organizer;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Element;

import com.gmail.at.kevinburnseit.organizer.gui.ExternalCalendarEditor;
import com.gmail.at.kevinburnseit.records.Record;
import com.gmail.at.kevinburnseit.records.RecordEditor;
import com.gmail.at.kevinburnseit.xml.FileFormatException;
import com.gmail.at.kevinburnseit.xml.XmlElementReader;
import com.gmail.at.kevinburnseit.xml.XmlElementWriter;

/**
 * Stores necessary information regarding external ics calendars.
 * @author Kevin J. Burns
 *
 */
public class ExternalCalendarDefinition implements Record {
	public static final String xmlTag = "external-calendar";
	private String name;
	private String url;
	private int uid = 0;
	private boolean alwaysAccept;
	
	/**
	 * Constructor. Creates a new, empty, external calendar definition.
	 */
	public ExternalCalendarDefinition() {
		// does nothing
	}
	
	/**
	 * Constructor. Creates an external calendar definition from an xml element
	 * @param from xml element to read
	 * @throws FileFormatException if the xml element is poorly structured
	 */
	public ExternalCalendarDefinition(Element from) throws FileFormatException {
		XmlElementReader r = new XmlElementReader(from);
		
		this.url = r.getRequiredStringAttribute("url");
		this.name = r.getRequiredStringAttribute("name", 
				XmlElementReader.nonEmptyStringValidator);
		this.uid = r.getOptionalIntAttribute("uid", this.generateUid());
		this.alwaysAccept = r.getRequiredBooleanAttribute("auto-accept");
	}
	
	@Override
	public Class<? extends RecordEditor<? extends Record>> getEditorClass() {
		return ExternalCalendarEditor.class;
	}

	/**
	 * Gets the user-defined name associated with this calendar.
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Sets the name of this calendar, as provided by the user.
	 * @param name the name to set. The name cannot be empty, nor can it be null; in
	 * these cases, nothing is changed.
	 */
	public final void setName(String name) {
		if (name == null) return;
		if (name.trim().length() == 0) return;
		
		this.name = name;
	}

	/**
	 * Gets the url where the external calendar is stored.
	 * @return the url
	 */
	public final String getUrl() {
		return url;
	}

	/**
	 * Sets the url where the external calendar can be found.
	 * @param url the url to set. If this value is null or empty nothing will change.
	 */
	public final void setUrl(String url) {
		if (url == null) return;
		if (url.trim().length() == 0) return;
		
		this.url = url;
		this.uid = this.generateUid();
	}

	/**
	 * Gets whether new appointments added to the external calendar will automatically
	 * be added to the calendar view.
	 * @return <code>true<code> if new events will be added automatically; 
	 * <code>false</code> if the user must confirm new events as they come in.
	 */
	public final boolean isAlwaysAccept() {
		return alwaysAccept;
	}

	/**
	 * Sets whether new events which are added to the external calendar will 
	 * automatically appear on the calendar view.
	 * @param alwaysAccept <code>true</code> if new events will appear automatically;
	 * <code>false</code> if the user will need to confirm events before they
	 * appear on the calendar view.
	 */
	public final void setAlwaysAccept(boolean alwaysAccept) {
		this.alwaysAccept = alwaysAccept;
	}
	
	/**
	 * Creates a worker thread which, when executed, will download a copy of this
	 * remote calendar and save it locally. After the download finishes, the worker
	 * thread will return the location the downloaded calendar can be found on disk.
	 * If the worker thread runs into problems, it will return <code>null</code>.
	 * @param saveInFolder local folder where the calendar will be stored
	 * @return A worker thread. Remember that a SwingWorker can only be executed
	 * once, so do not store the return value with the intention of calling it again
	 * in the future.
	 */
	public SwingWorker<String, Void> getRemoteCalendarFetcher(String saveInFolder) {
		return new SwingWorker<String, Void>() {
			@Override
			protected String doInBackground() throws Exception {
				String upstream = url;
				File localFile = new File(saveInFolder, getLocalFilename());
				String local = localFile.getAbsolutePath();
				
				try {
					FileUtils.copyURLToFile(
							new URL(upstream), localFile, 10000, 10000);
				} catch (IOException e) {
					return null;
				}
				
				return local;
			}
		};
	}
	
	/**
	 * Gets the filename which will be used for this calendar when downloaded from the
	 * internet. 
	 * @return Local filename where downloaded ics file is stored. This will not
	 * include the folder path.
	 */
	protected String getLocalFilename() {
		if (this.uid == 0) {
			this.uid = this.generateUid();
		}
		return "" + this.uid + ".ics";
	}

	/**
	 * Saves this calendar definition to an xml document fragment, and attaches it
	 *  to its parent.
	 * @param attachTo element to attach to
	 * @return the element which was created
	 */
	public Element saveToXml(Element attachTo) {
		Element e = attachTo.getOwnerDocument().createElement(xmlTag);
		XmlElementWriter w = new XmlElementWriter(e);
		
		w.writeBooleanAttribute("auto-accept", this.alwaysAccept);
		w.writeStringAttribute("url", this.url);
		w.writeStringAttribute("name", this.name);
		w.writeIntAttribute("uid", this.uid);
		
		attachTo.appendChild(e);
		
		return e;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getName() + " @ " + this.getUrl();
	}
	
	private int generateUid() {
		return this.url.hashCode();
	}

	/**
	 * Gets the unique id for this external calendar definition. The uid is only
	 * dependent on the remote url, so changing the url will change the uid; however,
	 * changing the name of the calendar or any other attribute will not change
	 * the uid.
	 * @return the uid
	 */
	public final int getUid() {
		return uid;
	}
}
