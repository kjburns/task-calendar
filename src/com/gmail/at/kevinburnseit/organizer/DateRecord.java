package com.gmail.at.kevinburnseit.organizer;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.gmail.at.kevinburnseit.organizer.gui.DateSelectorDialog;
import com.gmail.at.kevinburnseit.records.Record;
import com.gmail.at.kevinburnseit.records.RecordEditor;

/**
 * An inane wrapper for java's built-in Date class that allows a date to be treated
 * like a record.
 * @author Kevin J. Burns
 *
 */
public class DateRecord extends Date implements Record {
	private static final long serialVersionUID = 7316892291187976261L;
	private static final SimpleDateFormat formatter = 
			new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * Constructor.
	 * @param date The date to be wrapped.
	 */
	public DateRecord(Date date) {
		this.setTime(date.getTime());
	}

	@Override
	public Class<? extends RecordEditor<? extends Record>> getEditorClass() {
		return DateSelectorDialog.class;
	}

	/* (non-Javadoc)
	 * @see java.util.Date#toString()
	 */
	@Override
	public String toString() {
		return formatter.format(this);
	}
}
