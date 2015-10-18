package com.gmail.at.kevinburnseit.organizer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import com.gmail.at.kevinburnseit.organizer.ExternalCalendarCollection;
import com.gmail.at.kevinburnseit.organizer.ExternalCalendarDefinition;
import com.gmail.at.kevinburnseit.records.CollectionEditor;
import com.gmail.at.kevinburnseit.swing.DialogResult;

public class ExternalCalendarListEditor 
		extends CollectionEditor<ExternalCalendarDefinition> {
	private static final long serialVersionUID = 1727182905121586705L;
	private ExternalCalendarCollection model;

	public ExternalCalendarListEditor(ExternalCalendarCollection model) {
		super(model);
		
		this.model = model;
		this.setTitle("External Calendars");
	}
	
	@Override
	public ActionListener getAddButtonListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ExternalCalendarEditor ece = new ExternalCalendarEditor();
				if (ece.showDialog() == DialogResult.OK) {
					model.add(ece.getOutput());
				}
				ece.dispose();
			}
		};
	}

	@Override
	public ActionListener getEditButtonListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ExternalCalendarEditor ece = new ExternalCalendarEditor();
				ece.setData(listBox.getSelectedValue());
				if (ece.showDialog() == DialogResult.OK) {
					model.set(listBox.getSelectedIndex(), ece.getOutput());
				}
				ece.dispose();
			}
		};
	}

	@Override
	public ActionListener getDeleteButtonListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ExternalCalendarDefinition selectedCal = listBox.getSelectedValue();
				String msg = "Are you sure you want to delete the calendar '";
				msg += selectedCal.getName();
				msg += "'?";
				int result = JOptionPane.showConfirmDialog(
						ExternalCalendarListEditor.this, msg, "Delete Calendar", 
						JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					model.remove(selectedCal);
				}
			}
		};
	}
}
