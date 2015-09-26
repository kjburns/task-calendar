package com.gmail.at.kevinburnseit.swing.calendar;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JPanel;

/**
 * Part of an MVC framework, a view which takes care of the physical calendar and
 * provides a viewport for any number of calendar models. A weekly calendar view and
 * a monthly calendar view are included with this package, and this class can be
 * extended to create other types of calendar views.
 * @author Kevin J. Burns
 *
 */
public abstract class CalendarView extends JPanel {
	private static final long serialVersionUID = -4785271767869847392L;

	/**
	 * The {@link CalendarWidget} upon which this view is placed.
	 */
	protected CalendarWidget calWidget;
	/**
	 * The earliest date/time visible on this view. This is a property that extending
	 * classes must manipulate from time to time, as the calendar system depends on
	 * this value being correct. The concrete implementation of
	 * {@link #recalculateVisibleRange()} is responsible for updating this value.
	 */
	protected GregorianCalendar startOfVisibleRange = new GregorianCalendar();
	/**
	 * The latest date/time visible on this view. This is a property that extending
	 * classes must manipulate from time to time, as the calendar system depends on
	 * this value being correct. The concrete implementation of
	 * {@link #recalculateVisibleRange()} is responsible for updating this value.
	 */
	protected GregorianCalendar endOfVisibleRange = new GregorianCalendar();
	
	/**
	 * Constructor. Creates a calendar view as part of a {@link CalendarWidget}.
	 * @param parent Calendar widget where this view is to be placed
	 */
	public CalendarView(CalendarWidget parent) {
		this.calWidget = parent;
		
		this.setupBasicListeners();
	}
	
	private void setupBasicListeners() {
		this.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() < 0) {
					CalendarView.this.scrollUp();
				}
				if (e.getWheelRotation() > 0) {
					CalendarView.this.scrollDown();
				}
			}
		});
	}

	/**
	 * The contract of this method is that extending classes will call the calendar
	 * widget's {@link CalendarWidget#setSelectedDate(Calendar)} method with a new
	 * selected date that the user would expect after rolling the mouse wheel toward
	 * him or herself. Changing the selected date will automatically call
	 * {@link #recalculateVisibleRange()}, so don't do that here.
	 */
	protected abstract void scrollDown();
	/**
	 * The contract of this method is that extending classes will call the calendar
	 * widget's {@link CalendarWidget#setSelectedDate(Calendar)} method with a new
	 * selected date that the user would expect after rolling the mouse wheel away
	 * from him or herself. Changing the selected date will automatically call
	 * {@link #recalculateVisibleRange()}, so don't do that here.
	 */
	protected abstract void scrollUp();

	/**
	 * Checks to see whether the calendar's selected date falls within this view's
	 * visible date range.
	 * @return <code>true</code> if the calendar's selected date falls within this
	 * view's temporal range; <code>false</code> if the calendar's selected date falls
	 * outside this view's temporal range, or if either end of this view's
	 * temporal range is <code>null</code>. 
	 */
	protected final boolean isDisplayedDateWithinVisibleRange() {
		Calendar shown = this.calWidget.getSelectedDate();

		// if these variables are null, comparison functions will return false.
		if (shown.before(this.startOfVisibleRange)) return false;
		if (shown.after(this.endOfVisibleRange)) return false;
		
		return true;
	}
	
	/**
	 * Concrete implementations of this method must examine the calendar's selected
	 * date using {@link CalendarWidget#getSelectedDate()}, and then determine
	 * the earliest and latest dates/times that are to be visible in this view.
	 */
	protected abstract void recalculateVisibleRange();

	/**
	 * Gets the text that is to be displayed on the tab above this calendar view.
	 * Concrete implementations should produce something useful; for example, if
	 * this view is a month, providing the month and year would be a good expected
	 * result.
	 * @return
	 */
	public abstract String getDisplayName();
}
