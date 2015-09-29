package com.gmail.at.kevinburnseit.organizer.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import com.gmail.at.kevinburnseit.organizer.HolidayRule;
import com.gmail.at.kevinburnseit.organizer.HolidayRuleCollection;
import com.gmail.at.kevinburnseit.organizer.HolidayRuleRelative;
import com.gmail.at.kevinburnseit.organizer.Organizer;
import com.gmail.at.kevinburnseit.records.CollectionEditor;
import com.gmail.at.kevinburnseit.records.RecordEditor;
import com.gmail.at.kevinburnseit.swing.DialogResult;

public class HolidayListEditor extends CollectionEditor<HolidayRule> {
	private HolidayRuleCollection rules;
	protected Organizer app;

	public HolidayListEditor(HolidayRuleCollection model) {
		super(model);
		this.scrollPaneForListBox.setPreferredSize(new Dimension(200, 50));
		this.rules = model;
		this.setTitle("Holidays");
	}

	private static final long serialVersionUID = -1167289976011379394L;

	@Override
	public ActionListener getAddButtonListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AddHolidayDialog ahd = new AddHolidayDialog(rules);
				ahd.showDialog();
				
				HolidayRule output = ahd.getOutput();
				if (output != null) rules.add(output);
				ahd.dispose();
			}
		};
	}

	@Override
	public ActionListener getEditButtonListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = HolidayListEditor.this.listBox.getSelectedIndex();
				HolidayRule rule = rules.get(index);
				if (rule instanceof HolidayRuleRelative) {
					HolidayEditorRelative ed = new HolidayEditorRelative(rules);
					ed.setData((HolidayRuleRelative)rule);
					if (ed.showDialog() == DialogResult.OK) {
						rules.set(index, ed.getOutput());
					}
					ed.dispose();
					return;
				}
				try {
					@SuppressWarnings("unchecked")
					RecordEditor<HolidayRule> editor = 
							(RecordEditor<HolidayRule>)
								rule.getEditorClass().newInstance();
					editor.setData(rule);
					if (editor.showDialog() == DialogResult.OK) {
						rules.set(index, editor.getOutput());
					}
					editor.dispose();
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
			}
		};
	}

	@Override
	public ActionListener getDeleteButtonListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = HolidayListEditor.this.listBox.getSelectedIndex();
				HolidayRule rule = rules.get(index);
				String message = "You are about to delete '" 
						+ rule.getName() + "'. Continue?";
				int result = JOptionPane.showConfirmDialog(
						HolidayListEditor.this, message, 
						"organizer", JOptionPane.YES_NO_OPTION);
				if (result != JOptionPane.YES_OPTION) return;
				
				rules.remove(index);
			}
		};
	}

}
