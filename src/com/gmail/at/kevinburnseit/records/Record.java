package com.gmail.at.kevinburnseit.records;

public interface Record {
	Class<? extends RecordEditor<? extends Record>> getEditorClass();
}
