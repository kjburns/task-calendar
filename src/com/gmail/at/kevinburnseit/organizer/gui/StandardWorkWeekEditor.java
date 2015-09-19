package com.gmail.at.kevinburnseit.organizer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;

import com.gmail.at.kevinburnseit.organizer.DayEnum;
import com.gmail.at.kevinburnseit.organizer.Organizer;
import com.gmail.at.kevinburnseit.organizer.StandardWorkDay;
import com.gmail.at.kevinburnseit.organizer.StandardWorkWeek;
import com.gmail.at.kevinburnseit.swing.DialogResult;
import com.gmail.at.kevinburnseit.swing.NewDialog;

public class StandardWorkWeekEditor extends NewDialog {
	private static final long serialVersionUID = -6717771021015962010L;
	private HashMap<DayEnum, StandardWorkDayControl> controlMap = new HashMap<>();
	private JButton okBtn;
	private JButton cancelBtn;
	private Organizer app;

	public StandardWorkWeekEditor(Organizer organizer) {
		this.app = organizer;
		
		this.buildUI();
		this.populate();
	}

	private void populate() {
		StandardWorkWeek ww = this.app.getWorkSchedule();
		if (ww == null) return;
		
		for (DayEnum day : ww.keySet()) {
			this.controlMap.get(day).populate(ww.get(day));
		}
	}

	private void buildUI() {
		BoxLayout layout = new BoxLayout(this.contentPanel, BoxLayout.PAGE_AXIS);
		this.contentPanel.setLayout(layout);
		this.setTitle("Standard Work Schedule");

		for (DayEnum day : DayEnum.values()) {
			StandardWorkDayControl c = new StandardWorkDayControl(day);
			this.contentPanel.add(c);
			this.controlMap.put(day, c);
		}
		
		this.okBtn = this.addButton("OK", DialogResult.OK, ButtonTypeEnum.DEFAULT);
		this.okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				
				StandardWorkWeek sww = new StandardWorkWeek();
				for (DayEnum day : DayEnum.values()) {
					StandardWorkDay swd = controlMap.get(day).getData();
					sww.put(swd.getDay(), swd);
				}
				
				app.setWorkSchedule(sww);
			}
		});
		
		this.cancelBtn = this.addButton("Cancel", DialogResult.CANCEL, 
				ButtonTypeEnum.CANCEL);
		this.cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		this.pack();
	}
}
