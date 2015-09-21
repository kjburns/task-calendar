package com.gmail.at.kevinburnseit.organizer.gui;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JPanel;

/**
 * Part of an MVC framework, a view which takes care of the physical calendar and
 * provides a viewport for various calendar models.
 * @author Kevin J. Burns
 *
 */
public abstract class CalendarView extends JPanel {
	private static final long serialVersionUID = -4785271767869847392L;

	protected CalendarWidget calWidget;
	protected GregorianCalendar startOfVisibleRange = new GregorianCalendar();
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

	protected abstract void scrollDown();
	protected abstract void scrollUp();

	protected final boolean isDisplayedDateWithinVisibleRange() {
		Calendar shown = this.calWidget.getSelectedDate();
		if (shown.before(this.startOfVisibleRange)) return false;
		if (shown.after(this.endOfVisibleRange)) return false;
		
		return true;
	}
	
	protected abstract void recalculateVisibleRange();

	/**
	 * Gets the text that is to be displayed on the tab above this calendar view.
	 * @return
	 */
	public abstract String getDisplayName();
}
