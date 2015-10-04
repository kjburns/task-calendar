package com.gmail.at.kevinburnseit.swing.calendar;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

/**
 * A calendar view which displays a full month. This view starts each week with
 * Monday and ends each week with Sunday. Days in adjacent months are not shown.
 * @author Kevin J. Burns
 *
 */
public class CalendarViewMonthly extends CalendarView {
	private static final long serialVersionUID = 4124782752255800387L;
	
	/**
	 * An empty calendar cell. This is required for the cells before the first day
	 * of the month because the calendar view is laid out with a GridLayout, and
	 * there is no way to place the first element anywhere other than the first
	 * cell in the GridLayout. 
	 * @author Kevin J. Burns
	 *
	 */
	private class EmptyDay extends Day {
		private static final long serialVersionUID = -4028745004518438010L;
		
		public EmptyDay() {
			super(0);
			
			this.setBorder(null);
		}
	}
	
	/**
	 * A visual calendar cell with a day number in it.
	 * @author Kevin J. Burns
	 *
	 */
	private class Day extends JPanel {
		private static final long serialVersionUID = 5898330689560812820L;
		
		private int day;
		private JLabel dayLabel;

		private final Border normalBorder = 
				BorderFactory.createLineBorder(Color.black);
		private final Border selectedBorder =
				BorderFactory.createLineBorder(Color.decode("#8080ff"), 3);
		
		/**
		 * Constructor. Creates a new calendar cell with the specified day number.
		 * @param day The day number. Only pass positive numbers if you intend this
		 * to act as a date. Nonpositive numbers will result in most of the 
		 * functionality being disabled. However, this object is rather agnostic
		 * to the month that it is part of, so there is no real upper limit, although
		 * there are certainly practical limits.
		 */
		public Day(int day) {
			this.day = day;
			
			this.buildUI();
		}

		private void buildUI() {
			BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
			this.setLayout(layout);
			
			this.setBorder(normalBorder);
			
			if (this.day != 0) {
				dayLabel = new JLabel(String.valueOf(this.day));
				this.add(dayLabel);
			}
			
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (SwingUtilities.isLeftMouseButton(e)) {
						if (Day.this.day > 0) {
							deselectCurrentDate();
							selectDayThisMonth(day);
						}
					}
				}
			});
		}

		/**
		 * Causes this day to appear to be selected or unselected. However, apart
		 * from the visual effects, there is no underlying binding here.
		 * @param state <code>true</code> if this day is to appear selected;
		 * <code>false</code> otherwise.
		 */
		public void setSelected(boolean state) {
			if (state) {
				this.setBorder(this.selectedBorder);
			}
			else {
				this.setBorder(this.normalBorder);
			}
		}
	}
	
	private JPanel dayNameArea;
	private JPanel gridArea;
	private HashMap<Integer, Day> dayPanels = new HashMap<>();

	/**
	 * Constructor. Creates a calendar view which displays a full month.
	 * @param parent The calendar widget that this will be displayed in.
	 */
	public CalendarViewMonthly(CalendarWidget parent) {
		super(parent);
		
		this.buildUI();
	}

	private void buildUI() {
		SpringLayout sl = new SpringLayout();
		this.setLayout(sl);
		
		this.dayNameArea = new JPanel();
		GridLayout dayNameLayout = new GridLayout(1, 7, 0, 0);
		this.dayNameArea.setLayout(dayNameLayout);
		for (int i = 0; i < 7; i++) {
			dayNameArea.add(new JLabel(CalendarHelper.daysOfWeek[i]));
		}
		sl.putConstraint(SpringLayout.WEST, this.dayNameArea, 
				0, SpringLayout.WEST, this);
		sl.putConstraint(SpringLayout.NORTH, this.dayNameArea, 
				0, SpringLayout.NORTH, this);
		sl.putConstraint(SpringLayout.EAST, this.dayNameArea, 
				0, SpringLayout.EAST, this);
		this.add(this.dayNameArea);
		
		this.gridArea = new JPanel();
		sl.putConstraint(SpringLayout.WEST, this.gridArea, 
				0, SpringLayout.WEST, this.dayNameArea);
		sl.putConstraint(SpringLayout.NORTH, this.gridArea, 
				0, SpringLayout.SOUTH, this.dayNameArea);
		sl.putConstraint(SpringLayout.EAST, this.gridArea, 
				0, SpringLayout.EAST, this);
		sl.putConstraint(SpringLayout.SOUTH, this.gridArea, 
				0, SpringLayout.SOUTH, this);
		GridLayout layout1 = new GridLayout(0, 7, 0, 0);
		this.gridArea.setLayout(layout1);
		this.add(this.gridArea);
		this.rebuildCalendar();
	}

	private void rebuildCalendar() {
		GregorianCalendar date = (GregorianCalendar)this.calWidget.getSelectedDate();
		int mo = date.get(Calendar.MONTH);
		
		// day of week for selected date [1..7] = [Sunday..Saturday]
		int firstWday = date.get(Calendar.DAY_OF_WEEK);
		// day of week for selected date [-1..5] = [Sunday..Saturday]
		firstWday -= 2;
		// day of week for first day of month
		firstWday -= (date.get(Calendar.DAY_OF_MONTH) - 1);
		// normalize to [0..6] = [Monday..Sunday]
		while (firstWday < 0) {
			firstWday += 7;
		}
		
		// build calendar
		int lastDayInMonth = CalendarHelper.getDaysInMonth(
				mo, date.isLeapYear(date.get(Calendar.YEAR)));
		this.dayPanels.clear();
		this.gridArea.removeAll();
		
		for (int i = 0; i < firstWday; i++) {
			this.gridArea.add(this.new EmptyDay());
		}
		
		for (int i = 1; i <= lastDayInMonth; i++) {
			Day dayPanel = this.new Day(i);
			if (date.get(Calendar.DAY_OF_MONTH) == i) {
				dayPanel.setSelected(true);
			}
			this.gridArea.add(dayPanel);
			this.dayPanels.put(i, dayPanel);
		}
	}

	@Override
	protected void recalculateVisibleRange() {
		this.rebuildCalendar();
		this.revalidate();
		this.repaint();
		
		GregorianCalendar selDate = 
				(GregorianCalendar)this.calWidget.getSelectedDate();
		int mo = selDate.get(Calendar.MONTH);
		int yr = selDate.get(Calendar.YEAR);
		int lastDay = CalendarHelper.getDaysInMonth(mo,	selDate.isLeapYear(yr));
		this.startOfVisibleRange.set(yr, mo, 1);
		this.endOfVisibleRange.set(yr, mo, lastDay);
	}

	@Override
	public String getDisplayName() {
		Calendar date = this.calWidget.getSelectedDate();
		int mo = date.get(Calendar.MONTH);
		int yr = date.get(Calendar.YEAR);
		
		return CalendarHelper.monthsOfYear[mo] + " " + yr;
	}

	@Override
	protected void scrollDown() {
		Calendar date = this.calWidget.getSelectedDate();
		
		date.add(Calendar.MONTH, 1);

		this.calWidget.setSelectedDate(date);
	}

	@Override
	protected void scrollUp() {
		Calendar date = this.calWidget.getSelectedDate();
		
		date.add(Calendar.MONTH, -1);

		this.calWidget.setSelectedDate(date);
	}
	
	/**
	 * A convenience function which examines the currently selected date on the
	 * calendar widget and unselects that day on this view. It is expected that
	 * this function will be called after the user has moved to select another
	 * date, but the new selected date hasn't been registered with the
	 * calendar widget yet.
	 */
	protected final void deselectCurrentDate() {
		int dy = this.calWidget.getSelectedDate().get(Calendar.DAY_OF_MONTH);
		this.dayPanels.get(dy).setSelected(false);
	}
	
	/**
	 * A convenience function which selects a day in the displayed month on the
	 * calendar widget and shows that day as being selected on this view.
	 * @param dy Day of the month. While no error checking is performed, it is
	 * important that this parameter actually represent a day that exists in the
	 * displayed month. 
	 */
	protected void selectDayThisMonth(int dy) {
		GregorianCalendar c = new GregorianCalendar();
		c.set(this.startOfVisibleRange.get(Calendar.YEAR), 
				this.startOfVisibleRange.get(Calendar.MONTH), dy);
		this.calWidget.setSelectedDate(c);
		this.dayPanels.get(dy).setSelected(true);
	}
}
