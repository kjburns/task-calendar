package com.gmail.at.kevinburnseit.swing.calendar;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * A Swing widget which shows a calendar. By default, this includes a monthly and
 * a weekly view, but these default views can be removed.
 * @author Kevin J. Burns
 *
 */
public class CalendarWidget extends JPanel {
	private static final long serialVersionUID = 131942854060413041L;

	private Calendar selectedDate = new GregorianCalendar();
	private CalendarViewMonthly monthCal;
	private CalendarViewWeekly weekCal;
	private ETabbedPane tabs;
	private DailyScheduleProvider scheduleProvider = null;
	private int earliestTimeOnDailyView = 7 * 3600;
	private int latestTimeOnDailyView = 18 * 3600;
	
	/**
	 * Creates a calendar widget.
	 * @param appInstance
	 */
	public CalendarWidget() {
		this.buildUI();
	}
	
	/**
	 * Adds a new view to this calendar widget.
	 * @param view The view to add. If null, nothing will happen.
	 */
	public void addCalendarView(CalendarView view) {
		if (view == null) return;
		
		JTabbedPane jtabs = this.tabs.getJTabbedPane();
		
		jtabs.add(view.getDisplayName(), view);
		
		this.refreshView(view);
	}
	
	/**
	 * <b>This function still needs to be coded.</b> The intent of this function is
	 * to get the view to update itself with the contents of all attached
	 * calendar models.
	 * @param view The view to update
	 */
	protected final void refreshView(CalendarView view) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Notifies all calendar views to refresh themselves.
	 */
	public void rebuildAllCalendars() {
		for (Component c : this.tabs.getJTabbedPane().getComponents()) {
			if (!(c instanceof CalendarView)) continue;
			((CalendarView)c).recalculateVisibleRange();
		}
	}

	/**
	 * Removes an existing view from this calendar widget.
	 * @param view The view to remove. If null, nothing will happen.
	 */
	public void removeCalendarView(CalendarView view) {
		if (view == null) return;
		
		JTabbedPane jtabs = this.tabs.getJTabbedPane();
		jtabs.remove(view);
	}
	
	/**
	 * Removes the default monthly view.
	 */
	public final void removeDefaultMonthlyView() {
		this.removeCalendarView(this.monthCal);
		this.monthCal = null;
	}
	
	/**
	 * Removes the default weekly view.
	 */
	public final void removeDefaultWeeklyView() {
		this.removeCalendarView(this.weekCal);
		this.weekCal = null;
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
		
		this.weekCal = new CalendarViewWeekly(this);
		jtabs.add(this.weekCal);
		jtabs.setTitleAt(jtabs.indexOfComponent(this.weekCal), 
				this.weekCal.getDisplayName());

		/*
		 * FIXME this doesn't work. 
		 */
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				super.keyTyped(e);
				
				if ((e.getModifiers() == 0) && 
						(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)) {
					CalendarWidget.this.scrollDown();
				}
				if ((e.getModifiers() == 0) && (e.getKeyCode() == KeyEvent.VK_PAGE_UP)) {
					CalendarWidget.this.scrollUp();
				}
			}
		});
	}

	protected void scrollUp() {
		for (Component c : this.getComponents()) {
			if (!(c instanceof CalendarView)) continue;
			((CalendarView)c).scrollUp();
		}
	}

	protected void scrollDown() {
		for (Component c : this.getComponents()) {
			if (!(c instanceof CalendarView)) continue;
			((CalendarView)c).scrollDown();
		}
	}

	/**
	 * Returns <u>a clone</u> of the currently selected date.
	 * @return
	 */
	Calendar getSelectedDate() {
		return (Calendar)this.selectedDate.clone();
	}
	
	void setSelectedDate(Calendar date) {
		this.selectedDate = date;
		JTabbedPane jtabs = this.tabs.getJTabbedPane();
		for (int i = 0; i < jtabs.getComponentCount(); i++) {
			Component c = jtabs.getComponentAt(i);
			if (!(c instanceof CalendarView)) continue;
			
			CalendarView cv = (CalendarView)c;
			if (!cv.isDisplayedDateWithinVisibleRange()) {
				cv.recalculateVisibleRange();
			}
			
			jtabs.setTitleAt(i, cv.getDisplayName());
		}
	}

	/**
	 * If a DailyScheduleProvider has been registered with this widget, returns
	 * that provider. This function will return null if a provider has not been
	 * registered. In that case, call {@link #getEarliestTimeOnDailyView()}
	 * and {@link #getLatestTimeOnDailyView()} to determine the bounds that should
	 * be displayed on the calendar.
	 * @return the DailyScheduleProvider that should be consulted to determine the
	 * user's work schedule, if one has been provided. Otherwise, <code>null</code>.
	 */
	public DailyScheduleProvider getScheduleProvider_rNull() {
		return scheduleProvider;
	}

	/**
	 * Registers a DailyScheduleProvider that this widget will use to determine the
	 * user's work schedule.
	 * @param scheduleProvider the scheduleProvider to set. To remove the existing
	 * schedule provider (if any), pass <code>null</code>.
	 */
	public void setScheduleProvider(DailyScheduleProvider scheduleProvider) {
		this.scheduleProvider = scheduleProvider;
		for (Component c : this.tabs.getJTabbedPane().getComponents()) {
			if (!(c instanceof CalendarView)) continue;
			
			((CalendarView)c).recalculateVisibleRange();
		}
	}

	/**
	 * Gets the earliest time to display on daily views, in seconds after midnight.
	 * If {@link #getScheduleProvider_rNull()} doesn't return null, use that instead.
	 * @return the earliestTimeOnDailyView
	 */
	public int getEarliestTimeOnDailyView() {
		return earliestTimeOnDailyView;
	}

	/**
	 * Sets the earliest time to display on daily views, in seconds after midnight.
	 * This value will only be used if {@link #getScheduleProvider_rNull()}
	 * returns null.
	 * @param earliestTimeOnDailyView the earliestTimeOnDailyView to set
	 */
	public void setEarliestTimeOnDailyView(int earliestTimeOnDailyView) {
		this.earliestTimeOnDailyView = earliestTimeOnDailyView;
	}

	/**
	 * Gets the latest time to display on daily views, in seconds after midnight.
	 * if {@link #getScheduleProvider_rNull()} doesn't return null, use that instead.
	 * @return the latestTimeOnDailyView
	 */
	public int getLatestTimeOnDailyView() {
		return latestTimeOnDailyView;
	}

	/**
	 * Sets the latest time to display on daily views, in seconds after midnight.
	 * This value will only be used if {@link #getScheduleProvider_rNull()}
	 * returns null.
	 * @param latestTimeOnDailyView the latestTimeOnDailyView to set
	 */
	public void setLatestTimeOnDailyView(int latestTimeOnDailyView) {
		this.latestTimeOnDailyView = latestTimeOnDailyView;
	}
}
