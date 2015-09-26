package com.gmail.at.kevinburnseit.swing.calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

/**
 * A calendar view which displays a full week. This view starts each week with
 * Monday and ends each week with Sunday. Because week boundaries and month boundaries
 * are not always the same, the display of days in adjacent months is likely. Days are
 * in columns and time advances from top to bottom for each day.
 * @author Kevin J. Burns
 *
 */
public class CalendarViewWeekly extends CalendarView {
	private static final long serialVersionUID = 1845297077193705145L;

	/**
	 * A day in the weekly view.
	 * @author Kevin J. Burns
	 *
	 */
	private class Day extends JPanel {
		private static final long serialVersionUID = 5898330689560812820L;
		
		private GregorianCalendar date;
		private int place;
		private JLabel dayLabel;
		private JPanel contentArea;

		private final Border normalBorder = 
				BorderFactory.createLineBorder(this.getBackground(), 3);
		private final Border selectedBorder =
				BorderFactory.createLineBorder(Color.decode("#8080ff"), 3);
		
		/**
		 * Constructor. Creates a day for the weekly view. The actual date that this
		 * represents is calculated from the weekly view's temporal range and the
		 * place number of this day.
		 * @param place The place number [0..6], where 0 corresponds to Monday and
		 * 6 corresponds to Saturday.
		 */
		public Day(int place) {
			this.place = place;
			this.date = (GregorianCalendar)startOfVisibleRange.clone();
			this.date.add(Calendar.DAY_OF_MONTH, this.place);
			
			this.buildUI();
		}

		private void buildUI() {
			this.setLayout(new BorderLayout());
			
			this.setBorder(normalBorder);
			
			dayLabel = new JLabel(CalendarHelper.daysOfWeek[this.place] + " " 
						+ this.date.get(Calendar.DAY_OF_MONTH));
			this.add(dayLabel, BorderLayout.PAGE_START);
			
			this.contentArea = new JPanel() {
				private static final long serialVersionUID = -7438492182577939077L;
				
				/* (non-Javadoc)
				 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
				 */
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);

					int eTime = CalendarViewWeekly.this.earliestTime;
					int lTime = CalendarViewWeekly.this.latestTime;
					
					int firstHour = eTime / 3600;
					if (eTime % 3600 > 0) firstHour++;
					
					int lastHour = lTime / 3600;
					
					Rectangle2D cb = g.getClipBounds();

					for (int hr = firstHour; hr <= lastHour; hr++) {
						int y = CalendarViewWeekly.this.timeToYOrdinate(hr * 3600);
						g.drawLine((int)cb.getX(), y, (int)(cb.getX() + cb.getWidth()), y);
					}
				}
			};
			this.contentArea.setLayout(null); // absolute layout
			this.add(this.contentArea, BorderLayout.CENTER);
			
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (SwingUtilities.isLeftMouseButton(e)) {
						deselectCurrentDate();
						calWidget.setSelectedDate(date);
						Day.this.setSelected(true);
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

	/**
	 * A display of hours in the day for the weekly view. The upper and lower bounds
	 * are determined by the earliest and latest time to be displayed, as maintained
	 * by the weekly view itself.
	 * @author Kevin J. Burns
	 *
	 */
	private final class TimeColumn extends JPanel {
		private static final long serialVersionUID = -6543984888581917354L;
		
		private JPanel timeArea1;

		public TimeColumn() {
			this.buildUI();
		}

		private void buildUI() {
			BorderLayout bl = new BorderLayout();
			this.setLayout(bl);
			
			JLabel l = new JLabel(" ");
			this.add(l, BorderLayout.NORTH);
			
			this.timeArea1 = new JPanel() {
				private static final long serialVersionUID = -4800763494296785733L;
				
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					
					int eTime = CalendarViewWeekly.this.earliestTime;
					int lTime = CalendarViewWeekly.this.latestTime;
					
					int firstHour = eTime / 3600;
					if (eTime % 3600 > 0) firstHour++;
					
					int lastHour = lTime / 3600;
					
					FontMetrics fm = g.getFontMetrics();
					int totalWidth = g.getClipBounds().width;
					
					for (int hr = firstHour; hr <= lastHour; hr++) {
						String time = String.valueOf(hr);
						Rectangle2D bounds = fm.getStringBounds(time, g);
						
						int y = CalendarViewWeekly.this.timeToYOrdinate(hr * 3600);
						y += bounds.getHeight() / 2;
						g.drawString(time, totalWidth - (int)bounds.getWidth() - 5, y);
					}
				}
			};
			SpringLayout sl = new SpringLayout();
			this.timeArea1.setLayout(sl); // absolute layout
			sl.putConstraint(SpringLayout.EAST, this.timeArea1, 
					30, SpringLayout.WEST, this.timeArea1);
			this.add(this.timeArea1, BorderLayout.CENTER);
		}
	}
	
	private JPanel timeArea;
	private TimeColumn timeColumn;
	private JPanel gridArea;
	private HashMap<Integer, Day> dayPanels = new HashMap<>();
	private int earliestTime;
	private int latestTime;

	/**
	 * Constructor. Creates a calendar view which displays a full week.
	 * @param parent The calendar widget that this will be displayed in.
	 */
	public CalendarViewWeekly(CalendarWidget parent) {
		super(parent);
		
		this.buildUI();
	}

	private void buildUI() {
		SpringLayout sl = new SpringLayout();
		this.setLayout(sl);
		
		this.timeArea = new JPanel();
		this.timeArea.setLayout(new GridLayout(1, 1, 0, 0));
		this.timeColumn = new TimeColumn();
		this.timeArea.add(this.timeColumn);

		sl.putConstraint(SpringLayout.WEST, this.timeArea, 
				0, SpringLayout.WEST, this);
		sl.putConstraint(SpringLayout.NORTH, this.timeArea, 
				0, SpringLayout.NORTH, this);
		sl.putConstraint(SpringLayout.SOUTH, this.timeArea, 
				0, SpringLayout.SOUTH, this);
		this.add(this.timeArea);
		
		this.gridArea = new JPanel();
		sl.putConstraint(SpringLayout.WEST, this.gridArea, 
				0, SpringLayout.EAST, this.timeArea);
		sl.putConstraint(SpringLayout.NORTH, this.gridArea, 
				0, SpringLayout.NORTH, this);
		sl.putConstraint(SpringLayout.EAST, this.gridArea, 
				0, SpringLayout.EAST, this);
		sl.putConstraint(SpringLayout.SOUTH, this.gridArea, 
				0, SpringLayout.SOUTH, this);
		GridLayout layout1 = new GridLayout(0, 7, 0, 0);
		this.gridArea.setLayout(layout1);
		this.add(this.gridArea);
		this.recalculateVisibleRange();
		//this.rebuildCalendar();
	}
	
	/**
	 * A convenience function which examines the currently selected date on the
	 * calendar widget and unselects that day on this view. It is expected that
	 * this function will be called after the user has moved to select another
	 * date, but the new selected date hasn't been registered with the
	 * calendar widget yet.
	 */
	protected final void deselectCurrentDate() {
		this.dayPanels.get(this.getDayOfWeekMonThruSun(
				this.calWidget.getSelectedDate()
			)).setSelected(false);;
	}
	
	/**
	 * Recalculates the proper values for {@link #earliestTime} and 
	 * {@link #latestTime}. Make sure to call {@link #recalculateVisibleRange()}
	 * before calling this function.
	 */
	private void updateDailyBounds() {
		DailyScheduleProvider p = this.calWidget.getScheduleProvider_rNull();
		if (p != null) {
			int early = 86400;
			int late = 0;
			for (GregorianCalendar date = 
						(GregorianCalendar)this.startOfVisibleRange.clone();
					!date.after(this.endOfVisibleRange); 
					date.add(1, Calendar.DAY_OF_MONTH)) {
				if (!p.isAtWorkOn(date)) continue;
				
				early = Math.min(early, p.getWorkStartTime(date));
				late = Math.max(late, p.getWorkEndTime(date));
			}
			
			this.earliestTime = early;
			this.latestTime = late;
		}
		else {
			this.earliestTime = this.calWidget.getEarliestTimeOnDailyView();
			this.latestTime = this.calWidget.getLatestTimeOnDailyView();
		}
	}

	private void rebuildCalendar() {
		this.updateDailyBounds();
	
		this.gridArea.removeAll();
		this.dayPanels.clear();
		for (int i = 0; i < 7; i++) {
			Day day = this.new Day(i);
			this.gridArea.add(day);
			this.dayPanels.put(i, day);
		}
		
		int selected = this.getDayOfWeekMonThruSun(this.calWidget.getSelectedDate());
		this.dayPanels.get(selected).setSelected(true);
		this.invalidate();
		this.repaint();
	}

	@Override
	protected void scrollDown() {
		this.calWidget.getSelectedDate().add(Calendar.DATE, 7);
		this.calWidget.setSelectedDate(this.calWidget.getSelectedDate());
	}

	@Override
	protected void scrollUp() {
		this.calWidget.getSelectedDate().add(Calendar.DATE, -7);
		this.calWidget.setSelectedDate(this.calWidget.getSelectedDate());
	}

	@Override
	protected void recalculateVisibleRange() {
		Calendar sel = this.calWidget.getSelectedDate();
		int normalDOW = this.getDayOfWeekMonThruSun(sel);
		
		this.startOfVisibleRange = (GregorianCalendar)sel.clone();
		this.startOfVisibleRange.add(Calendar.DAY_OF_MONTH, -normalDOW);
		
		this.endOfVisibleRange = (GregorianCalendar)this.startOfVisibleRange.clone();
		this.endOfVisibleRange.add(Calendar.DAY_OF_MONTH, 6);
		
		this.rebuildCalendar();
	}

	@Override
	public String getDisplayName() {
		return this.formatDate(this.startOfVisibleRange) + " to " +
				this.formatDate(this.endOfVisibleRange);
	}
	
	private int getDayOfWeekMonThruSun(Calendar date) {
		int javaDOW = date.get(Calendar.DAY_OF_WEEK); // [1..7] = [Sun..Sat]
		int normalDOW = javaDOW - 2; // [-1..5] = [Sun..Sat]
		                             // [0..6] = [Mon..Sun]
		if (normalDOW < 0) normalDOW += 7;
		
		return normalDOW;
	}
	
	private String formatDate(Calendar date) {
		return "" + date.get(Calendar.DAY_OF_MONTH) + " " + 
				CalendarHelper.monthsOfYear[date.get(Calendar.MONTH)];
	}
	
	/**
	 * Finds the <i>y</i>-ordinate in the drawable area of a day panel for a provided
	 * time.
	 * @param time The number of seconds since midnight.
	 * @return The <i>y</i>-ordinate in the drawable area of a day panel. Note that if
	 * the provided time is outside the range prescribed by 
	 * {@link #earliestTime} and {@link #latestTime}, the returned y value will
	 * represent a point that will not be drawn on the screen.
	 */
	protected final int timeToYOrdinate(int time) {
		int bufferHeight = (this.dayPanels.get(0).dayLabel.getBounds().height + 1) / 2;
		int dayHeight = this.dayPanels.get(0).contentArea.getBounds().height;
		dayHeight -= 2 * bufferHeight;
		
		int timeSinceBeginOfDay = time - this.earliestTime;
		
		int y = timeSinceBeginOfDay * dayHeight / (this.latestTime - this.earliestTime);
		y += bufferHeight;
		
		return y;
	}
	
	/**
	 * Finds the time the corresponds to a supplied <i>y</i>-ordinate on the drawable
	 * area of a day panel. 
	 * @param y <i>y</i>-ordinate of a point on a day panel
	 * @return The time, in seconds since midnight, that is located at the specified
	 * location.
	 */
	protected final int yOrdinateToTime(int y) {
		int bufferHeight = (this.dayPanels.get(0).dayLabel.getBounds().height + 1) / 2;
		int dayHeight = this.dayPanels.get(0).contentArea.getBounds().height;
		dayHeight -= 2 * bufferHeight;
		
		int distanceToTop = y - bufferHeight;
		int time = distanceToTop * (this.latestTime - this.earliestTime) / dayHeight;
		time += this.earliestTime;
		
		return time;
	}
}
