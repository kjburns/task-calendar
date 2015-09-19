package com.gmail.at.kevinburnseit.records;

import com.gmail.at.kevinburnseit.swing.DialogResult;

public interface RecordEditor<T> {
	DialogResult showDialog();
	T getOutput();
	void setData(T input);
	void init();
}
