package com.gmail.at.kevinburnseit.organizer.gui;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.GridLayout;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.gmail.at.kevinburnseit.organizer.Organizer;
import com.gmail.at.kevinburnseit.swing.ETabbedPane;

/**
 * A Swing widget which shows a calendar.
 * @author Kevin J. Burns
 *
 */
public class CalendarWidget extends JPanel {
	private static final long serialVersionUID = 131942854060413041L;

	private Calendar selectedDate = new GregorianCalendar();
	@Deprecated private Organizer app;
	private CalendarViewMonthly monthCal;
	private ETabbedPane tabs;
	
	/**
	 * Creates a calendar widget.
	 * @param appInstance
	 */
	public CalendarWidget(Organizer appInstance) {
		this.app = appInstance;
		this.buildUI();
	}
	
	private void buildUI() {
		GridLayout layout = new GridLayout(1, 1, 0, 0);
		this.setLayout(layout);
		
		this.tabs = new ETabbedPane(ComponentOrientation.LEFT_TO_RIGHT);
		this.add(this.tabs);

		this.monthCal = new CalendarViewMonthly(this);
		JTabbedPane jtabs = this.tabs.getJTabbedPane();
		jtabs.add(this.monthCal);
		jtabs.setTitleAt(jtabs.indexOfComponent(this.monthCal), 
				this.monthCal.getDisplayName());
	}

	Calendar getSelectedDate() {
		return this.selectedDate;
	}
	
	void setSelectedDate(Calendar date) {
		this.selectedDate = date;
		JTabbedPane jtabs = this.tabs.getJTabbedPane();
		for (int i = 0; i < jtabs.getComponentCount(); i++) {
			Component c = jtabs.getComponentAt(i);
			if (!(c instanceof CalendarView)) continue;
			
			CalendarView cv = (CalendarView)c;
			cv.recalculateVisibleRange();
			
			jtabs.setTitleAt(i, cv.getDisplayName());
		}
	}

	/**
	 * If this is never used as part of this application, it will be removed in the
	 * interest of publishing a calendar library in the future.
	 * @deprecated
	 * @return the app
	 */
	@Deprecated public Organizer getApp() {
		return app;
	}
}
