package com.gmail.at.kevinburnseit.swing.calendar;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.function.Predicate;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
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
	protected class Day extends JPanel {
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

		public ArrayList<Entry> createGraphicalEntries(ArrayList<CalendarEntry> list) {
			ArrayList<Entry> entriesToShow = new ArrayList<>();
			ArrayList<Entry> ret = new ArrayList<>();
			
			for (Component c : this.getComponents()) {
				if (!(c instanceof Entry)) continue;
				entriesToShow.add((Entry)c);
			}
			for (Entry e : entriesToShow) {
				this.remove(e);
			}
			
			for (CalendarEntry ce : list) {
				entriesToShow.add(new Entry(ce));
			}
			
			Entry nullEntry = new Entry(null);
			Collections.sort(entriesToShow, nullEntry.sorter);
			
			for (Entry e : entriesToShow) {
				this.add(e);
				ret.add(e);
			}
			
			return ret;
		}
	}
	
	private class Entry extends JLabel {
		private static final long serialVersionUID = 5187107495759409796L;

		private CalendarEntry event;
		
		public final Comparator<Entry> sorter = new Comparator<Entry>() {
			@Override
			public int compare(Entry x, Entry y) {
				return x.event.getStartTime().compareTo(y.event.getStartTime());
			}			
		};
		
		public Entry(CalendarEntry event) {
			this.event = event;
			if (this.event == null) return;
			this.setOpaque(true);
			this.setText(CalendarHelper.militaryTimeFormatter.format(
					this.event.getStartTime().getTime()) + " " + 
					this.event.getTitle());
			this.setToolTipText(this.getText());
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
		
		this.refreshAllEntries();
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
		this.startOfVisibleRange.clear();
		this.startOfVisibleRange.set(yr, mo, 1, 0, 0, 0);
		this.endOfVisibleRange.setTime(this.startOfVisibleRange.getTime());
		this.endOfVisibleRange.add(Calendar.MONTH, 1);
		this.endOfVisibleRange.add(Calendar.MILLISECOND, -1);
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
		this.refreshAllEntries();
	}

	@Override
	protected void scrollUp() {
		Calendar date = this.calWidget.getSelectedDate();
		
		date.add(Calendar.MONTH, -1);

		this.calWidget.setSelectedDate(date);
		this.refreshAllEntries();
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

	@Override
	public void refreshEntries(CalendarEntryProvider<? extends CalendarEntry> cep) {
		for (JComponent c : this.eventComponents.get(cep)) {
			c.getParent().remove(c);
		}
		
		this.eventComponents.get(cep).clear();
		
		ArrayList<CalendarEntry> entriesToDraw = new ArrayList<>();
		Predicate<CalendarEntry> withinPredicate = 
				CalendarEntryProvider.getPredicateForTimeRange(
						startOfVisibleRange, endOfVisibleRange);
		for (CalendarEntry ce : cep) {
			if (withinPredicate.test(ce)) {
				entriesToDraw.add(ce);
			}
		}
		
		for (Day day : this.dayPanels.values()) {
			/*
			 * TODO figure out a way to colour the entries, or not. It's not all
			 * that important right now. 
			 */
			GregorianCalendar start = new GregorianCalendar();
			start.setTime(this.startOfVisibleRange.getTime());
			start.set(Calendar.DAY_OF_MONTH, day.day);
			
			GregorianCalendar end = new GregorianCalendar();
			end.setTime(start.getTime());
			end.add(Calendar.HOUR_OF_DAY, 24);
			end.add(Calendar.MILLISECOND, -1);
			
			ArrayList<CalendarEntry> todaysEvents = new ArrayList<>();
			Predicate<CalendarEntry> dayPredicate = 
					CalendarEntryProvider.getPredicateForTimeRange(start, end);
			for (CalendarEntry ce : entriesToDraw) {
				if (dayPredicate.test(ce)) {
					todaysEvents.add(ce);
				}
			}
			
			ArrayList<Entry> entries = day.createGraphicalEntries(todaysEvents);
			this.eventComponents.get(cep).addAll(entries);
		}
	}
}
