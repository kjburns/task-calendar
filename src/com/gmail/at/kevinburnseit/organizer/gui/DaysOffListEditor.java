package com.gmail.at.kevinburnseit.organizer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;

import javax.swing.JOptionPane;
import com.gmail.at.kevinburnseit.organizer.DateRecord;
import com.gmail.at.kevinburnseit.organizer.DaysOffList;
import com.gmail.at.kevinburnseit.records.CollectionEditor;
import com.gmail.at.kevinburnseit.swing.DialogResult;

/**
 * A dialog which allows the user to add, edit, and remove days off.
 * @author Kevin J. Burns
 *
 */
public class DaysOffListEditor extends CollectionEditor<DateRecord> {
	private static final long serialVersionUID = -7507687998626080413L;
	private DaysOffList model;
	
	/**
	 * Constructor. Creates a new dialog with the selected list of days off.
	 * @param model List of days off to load initially
	 */
	public DaysOffListEditor(DaysOffList model) {
		super(model);
		this.model = model;
		this.setTitle("Days Off");
	}

	@Override
	public ActionListener getAddButtonListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DateSelectorDialog dsd = new DateSelectorDialog();
				if (dsd.showDialog() == DialogResult.OK) {
					model.add(dsd.getOutput());
					Collections.sort(model);
					Collections.reverse(model);
				}
				dsd.dispose();
			}
		};
	}

	@Override
	public ActionListener getEditButtonListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DateSelectorDialog dsd = new DateSelectorDialog();
				dsd.setData(listBox.getSelectedValue());
				if (dsd.showDialog() == DialogResult.OK) {
					model.set(listBox.getSelectedIndex(), dsd.getOutput());
				}
				Collections.sort(model);
				Collections.reverse(model);
				dsd.dispose();
			}
		};
	}

	@Override
	public ActionListener getDeleteButtonListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DateRecord date = listBox.getSelectedValue();
				if (JOptionPane.showConfirmDialog(
							DaysOffListEditor.this, "Delete selected date?", 
							"Delete", JOptionPane.YES_NO_OPTION)
						== JOptionPane.YES_OPTION) {
					model.remove(date);
				}
			}
		};
	}
}
