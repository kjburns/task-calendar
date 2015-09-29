package com.gmail.at.kevinburnseit.records;

import com.gmail.at.kevinburnseit.swing.DialogResult;

public interface RecordEditor<T> {
	/**
	 * Causes the modal dialog to show. If concrete implementations are subclasses
	 * of {@link NewDialog}, implementation will be automatic.
	 * @return One of the {@link DialogResult} values
	 */
	DialogResult showDialog();
	/**
	 * Gets the data that was set by the user as part of this dialog. It is recommended
	 * that, for consistency, this function should return <code>null</code> if the user 
	 * pressed Cancel.
	 * @return The data, as set by the user in this dialog. This may return
	 * <code>null</code> if the user pressed Cancel on the dialog. 
	 */
	T getOutput();
	/**
	 * Sets the data that is used to seed the dialog.
	 * @param input Data to be used to seed the dialog.
	 */
	void setData(T input);
	void dispose();
}
