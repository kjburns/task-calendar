package com.gmail.at.kevinburnseit.organizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.SwingWorker.StateValue;

import com.gmail.at.kevinburnseit.organizer.Appointment.TravelTimeEntry;
import com.gmail.at.kevinburnseit.organizer.gui.AbortOrContinueSetupDialog;
import com.gmail.at.kevinburnseit.organizer.gui.AbortOrContinueSetupDialog.ResultEnum;
import com.gmail.at.kevinburnseit.organizer.gui.DataFolderDialog;
import com.gmail.at.kevinburnseit.organizer.gui.DaysOffListEditor;
import com.gmail.at.kevinburnseit.organizer.gui.ExternalCalendarListEditor;
import com.gmail.at.kevinburnseit.organizer.gui.HolidayListEditor;
import com.gmail.at.kevinburnseit.organizer.gui.MenuLabel;
import com.gmail.at.kevinburnseit.organizer.gui.MenuLabel.MenuLabelAction;
import com.gmail.at.kevinburnseit.organizer.gui.MenuPane;
import com.gmail.at.kevinburnseit.organizer.gui.StandardWorkWeekEditor;
import com.gmail.at.kevinburnseit.swing.DialogResult;
import com.gmail.at.kevinburnseit.swing.calendar.CalendarEntry;
import com.gmail.at.kevinburnseit.swing.calendar.CalendarWidget;
import com.gmail.at.kevinburnseit.swing.calendar.DailyScheduleProvider;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.util.CompatibilityHints;

/**
 * The main application window for this application.
 * @author Kevin J. Burns
 *
 */
public class Organizer extends JFrame
		implements DailyScheduleProvider {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1532652861021682522L;
	private static final String STG_appDataPath = "data-folder";
	private static final String STG_initialSetupComplete = "init-setup-complete";

	private String appDataPath =
			System.getProperty("user.home") + File.separator + ".organizer";
	private StandardWorkWeek workSchedule;
	private HolidayRuleCollection holidays = new HolidayRuleCollection();
	private DaysOffList daysOff = new DaysOffList();
	private ExternalCalendarCollection extCalendars = new ExternalCalendarCollection();
	private boolean initialSetupComplete = false;
	private static final String scheduleFilename = "schedule.xml";
	private ArrayList<DailyScheduleListener> scheduleListeners = new ArrayList<>();
	private HashMap<ExternalCalendarDefinition, AppointmentList> appointments = 
			new HashMap<>();
	private CalendarWidget calendarWidget;
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		Organizer org = new Organizer();
		org.setVisible(true);
	}
	
	private Organizer() {
		this.setTitle("Organizer");
		this.setMinimumSize(new Dimension(400, 400));
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.loadSettings();
		if (!this.initialSetupComplete) {
			this.initialSetupComplete = this.doInitialSetup();
			if (!this.initialSetupComplete) {
				System.exit(0);
			}
		}
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					saveSettings();
				} catch (BackingStoreException e1) {
					JOptionPane.showMessageDialog(Organizer.this, 
							"Could not save persistent settings.");
					e1.printStackTrace();
				}
			}
		});
		
		this.loadNecessaryFiles();
		
		this.buildUI();
		
		this.loadCalendarsFromXml();
		
		this.addScheduleListener(new DailyScheduleListener() {
			@Override
			public void dailyScheduleChanged(DailyScheduleProvider sched) {
				calendarWidget.rebuildAllCalendars();
			}
		});
	}
	
	private void loadNecessaryFiles() {
		try {
			this.loadWorkSchedule();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, 
					"Could not load work schedule from file.");
		}
		
		try {
			this.loadHolidaysFromDisk();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, 
					"Could not load holidays from file.");
		}
		
		try {
			this.loadDaysOffFromDisk();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Could not load days off from file.");
		}
		
		try {
			this.loadExternalCalendarDefinitions();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, 
					"Could not load external calendar definitions from file.");
		}
	}

	private void loadCalendarsFromXml() {
		for (ExternalCalendarDefinition def : this.extCalendars) {
			String localFile = def.getUid() + ".xml";
			String path = (new File(this.appDataPath, localFile)).getAbsolutePath();
			try {
				AppointmentList al = new AppointmentList(path);
				this.appointments.put(def, al);
				this.calendarWidget.addCalendarEntryProvider(al);
			} catch (Exception e) {
				// TODO reload from ics file
			}
		}
		
		this.calendarWidget.rebuildAllCalendars();
	}

	private void loadExternalCalendarDefinitions() throws Exception {
		File f = new File(this.appDataPath, "external-calendars.xml");
		if (!f.exists()) return;
		
		this.extCalendars = new ExternalCalendarCollection(f.getAbsolutePath());
	}
	
	private void saveExternalCalendarDefinitions() throws Exception {
		File f = new File(this.appDataPath, "external-calendars.xml");
		this.extCalendars.saveToXml(f.getAbsolutePath());
	}

	private void loadDaysOffFromDisk() throws Exception {
		File f = new File(this.appDataPath, "days-off.xml");
		if (!f.exists()) return;
		
		this.daysOff = new DaysOffList(f.getAbsolutePath());
	}

	private void buildUI() {
		GridBagLayout layout = new GridBagLayout();
		this.getContentPane().setLayout(layout);
		
		MenuPane menu = new MenuPane(this);
		menu.setBorder(BorderFactory.createLineBorder(Color.black));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
//		c.gridheight = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.2;
		c.weighty = 1;
		this.getContentPane().add(menu, c);
		
		MenuLabel workDayLabel = new MenuLabel("Define Work Day...");
		workDayLabel.addClickListener(new MenuLabelAction() {
			@Override
			public void itemClicked(MenuLabel source) {
				StandardWorkWeekEditor swwe = 
						new StandardWorkWeekEditor(Organizer.this);
				if (swwe.showDialog() == DialogResult.OK) {
					try {
						Organizer.this.saveWorkSchedule();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(Organizer.this, 
								"Could not write work schedule to disk.");
					}
					notifyScheduleListeners();
				}
				swwe.dispose();
			}
		});
		menu.add(workDayLabel);
		
		MenuLabel holidayLabel = new MenuLabel("Holidays...");
		holidayLabel.addClickListener(new MenuLabelAction() {
			@Override
			public void itemClicked(MenuLabel source) {
				HolidayListEditor ed = new HolidayListEditor(holidays);
				ed.showDialog();
				try {
					saveHolidaysToDisk();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(Organizer.this, 
							"Unable to save holidays to disk.");
				}
				notifyScheduleListeners();
				ed.dispose();
			}
		});
		menu.add(holidayLabel);
		
		MenuLabel daysOffLabel = new MenuLabel("Days off...");
		daysOffLabel.addClickListener(new MenuLabelAction() {
			@Override
			public void itemClicked(MenuLabel source) {
				DaysOffListEditor dole = new DaysOffListEditor(daysOff);
				dole.showDialog();
				try {
					saveDaysOffToDisk();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(Organizer.this, 
							"Unable to save days off to disk.");
				}
				notifyScheduleListeners();
				dole.dispose();
			}
		});
		menu.add(daysOffLabel);
		
		MenuLabel externalCalendarsLabel = new MenuLabel("External Calendars...");
		externalCalendarsLabel.addClickListener(new MenuLabelAction() {
			@Override
			public void itemClicked(MenuLabel source) {
				ExternalCalendarListEditor ecle = 
						new ExternalCalendarListEditor(extCalendars);
				ecle.showDialog();
				try {
					saveExternalCalendarDefinitions();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(Organizer.this, 
							"Unable to save external calendar definitions to disk.");
				}
				/* 
				 * TODO lots of things to do here, like update calendars on screen,
				 * save calendar references to disk, etc.
				 */
				ecle.dispose();
				
				doFullRefresh();
			}
		});
		menu.add(externalCalendarsLabel);

		this.calendarWidget = new CalendarWidget();
		calendarWidget.setScheduleProvider(this);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		this.getContentPane().add(calendarWidget, c);
		
		this.pack();
	}

	protected void doFullRefresh() {
		SwingWorker<HashMap<ExternalCalendarDefinition, String>, Void> sw = 
				this.extCalendars.getRemoteCalendarFetcher(appDataPath);
		sw.execute();
		sw.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent ev) {
				if ("state".equals(ev.getPropertyName())) {
					if (ev.getNewValue().equals(StateValue.DONE)) {
						try {
							readIcsFiles(sw.get());
						} catch (InterruptedException | ExecutionException e) {
							JOptionPane.showMessageDialog(null, "Refresh failed.");
						}
					}
				}
			}
		});
	}
	
	private void readIcsFiles(HashMap<ExternalCalendarDefinition, String> files) {
		HashMap<ExternalCalendarDefinition, net.fortuna.ical4j.model.Calendar> icsMap =
				new HashMap<>();
		CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
		for (ExternalCalendarDefinition key : files.keySet()) {
			try (FileInputStream fis = new FileInputStream(files.get(key))) {
				CalendarBuilder builder = new CalendarBuilder();
				net.fortuna.ical4j.model.Calendar ics = builder.build(fis);
				icsMap.put(key, ics);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			} catch (ParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			} catch (NullPointerException e) {
				/*
				 * An attempt has been made to download this calendar and the 
				 * download failed. Use null as a placeholder for the expected
				 * ics calendar so the program knows not to change any items
				 * from this calendar.
				 */
				JOptionPane.showMessageDialog(null, 
						"Could not download external calendar '" + key.getName() + 
						"' from the internet.");
				icsMap.put(key, null);
			}
		}
		
		this.createCalendarEntries(icsMap);
	}

	private void createCalendarEntries(HashMap<ExternalCalendarDefinition, 
			net.fortuna.ical4j.model.Calendar> icsMap) {
		
		for (ExternalCalendarDefinition def : icsMap.keySet()) {
			if (!this.appointments.containsKey(def)) {
				AppointmentList al = new AppointmentList();
				this.appointments.put(def, al);
				this.calendarWidget.addCalendarEntryProvider(al);
			}
			
			net.fortuna.ical4j.model.Calendar cal = icsMap.get(def);
			AppointmentList al = this.appointments.get(def);
			
			for (CalendarComponent c : cal.getComponents()) {
				if (!(c instanceof VEvent)) continue;
				VEvent ve = (VEvent)c;
				String uid = ve.getUid().getValue();
				Appointment a = al.getByUid_rNull(uid);
				if (a == null) {
					if (def.isAlwaysAccept()) {
						a = new Appointment(ve);
						al.add(a);
						for (CalendarEntry tte : a.getTravelEntries()) {
							al.add(tte);
						}
					}
					else {
						/*
						 * TODO add to notification queue for user acceptance
						 */
					}
				}
				else {
					a.update(ve);
					ArrayList<CalendarEntry> keepTravelEntries = a.getTravelEntries();
					for (TravelTimeEntry tte : al.getTravelEntriesLinkedTo(a)) {
						if (!keepTravelEntries.contains(tte)) {
							al.remove(tte);
						}
					}
				}
			}
			
			String filename = "" + def.getUid() + ".xml";
			String path = (new File(this.appDataPath, filename)).getAbsolutePath();
			try {
				al.saveToXml(path);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}

	private boolean doInitialSetup() {
		DataFolderDialog dfd = new DataFolderDialog(this);
		for (;;) {
			DialogResult result = dfd.showDialog();
			if (result == DialogResult.OK) break;

			AbortOrContinueSetupDialog d = new AbortOrContinueSetupDialog();
			for (;;) {
				d.showDialog();
				if (d.getResult() == null) continue;
				break;
			}
			
			if (d.getResult() == ResultEnum.STOP) {
				dfd.dispose();
				return false;
			}
		}
		dfd.dispose();
		
		StandardWorkWeekEditor swwe = new StandardWorkWeekEditor(this);
		for (;;) {
			DialogResult result = swwe.showDialog();
			if (result == DialogResult.OK) break;
			
			AbortOrContinueSetupDialog d = new AbortOrContinueSetupDialog();
			for (;;) {
				d.showDialog();
				if (d.getResult() == null) continue;
				break;
			}
			
			if (d.getResult() == ResultEnum.STOP) {
				swwe.dispose();
				return false;
			}
		}
		swwe.dispose();
		
		File folder = new File(this.appDataPath);
		if (!folder.exists()) {
			folder.mkdir();
		}
		
		File workScheduleFile = new File(folder, scheduleFilename);
		try {
			this.workSchedule.saveToXmlFile(workScheduleFile.getAbsolutePath());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Couldn't save work schedule to disk.");
			return false;
		}

		this.initialSetupComplete = true;
		try {
			this.saveSettings();
		} catch (BackingStoreException e) {
			JOptionPane.showMessageDialog(null, "Couldn't save persistent settings.");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/*
	 * Loads user's settings from the local preference database
	 */
	private void loadSettings() {
		Preferences p = Preferences.userNodeForPackage(Organizer.class);
		this.appDataPath = p.get(STG_appDataPath, this.appDataPath);
		this.initialSetupComplete = p.getBoolean(STG_initialSetupComplete, false);
	}
	
	private void saveSettings() throws BackingStoreException {
		/*
		 * Don't save any settings if user didn't complete setup 
		 */
		if (!this.initialSetupComplete) return;
		
		Preferences p = Preferences.userNodeForPackage(Organizer.class);
		p.putBoolean(STG_initialSetupComplete, true);
		p.put(STG_appDataPath, this.appDataPath);
		p.flush();
	}

	/**
	 * Gets the folder that the user selected to store application data for this
	 * program, or the default value ([home]/.organizer) if the user hasn't selected
	 * a location.
	 * @return the appDataPath
	 */
	public String getAppDataPath() {
		return appDataPath;
	}

	/**
	 * Sets the folder to store application data for this program. This function will
	 * attempt to save this value in the java settings database, but if the user
	 * has not yet completed initial setup, it will not be saved in the prefs
	 * database.
	 * @param appDataPath the path in which data for this application should be
	 * stored.
	 */
	public void setAppDataPath(String appDataPath) {
		this.appDataPath = appDataPath;
	}

	/**
	 * @return the workSchedule
	 */
	public StandardWorkWeek getWorkSchedule() {
		return workSchedule;
	}

	/**
	 * @param workSchedule the workSchedule to set
	 */
	public void setWorkSchedule(StandardWorkWeek workSchedule) {
		this.workSchedule = workSchedule;
	}
	
	private void loadWorkSchedule() throws Exception {
		File f = new File(this.getAppDataPath(), Organizer.scheduleFilename);
		this.workSchedule = new StandardWorkWeek(f.getAbsolutePath());
	}
	
	private void saveWorkSchedule() throws Exception {
		File f = new File(this.getAppDataPath(), Organizer.scheduleFilename);
		this.workSchedule.saveToXmlFile(f.getAbsolutePath());
	}
	
	private void saveDaysOffToDisk() throws Exception {
		File f = new File(Organizer.this.getAppDataPath(), "days-off.xml");
		this.daysOff.saveToXml(f.getAbsolutePath());
	}

	private void saveHolidaysToDisk() throws Exception {
		File f = new File(Organizer.this.getAppDataPath(), "holidays.xml");
		this.holidays.saveToXml(f.getAbsolutePath());
	}
	
	private void loadHolidaysFromDisk() throws Exception {
		File f = new File(Organizer.this.getAppDataPath(), "holidays.xml");
		if (!f.exists()) {
			this.holidays = new HolidayRuleCollection();
		}
		else {
			this.holidays = HolidayRuleCollection.fromXmlFile(f.getAbsolutePath());
		}
	}

	@Override
	public boolean isAtWorkOn(GregorianCalendar date) {
		if (this.daysOff.contains(new DateRecord(date.getTime()))) return false;
		if (this.holidays.isHoliday(date)) return false;
		
		DayEnum dow = getDayOfWeek(date);
		return this.workSchedule.get(dow).isWorkingToday();
	}

	private DayEnum getDayOfWeek(GregorianCalendar date) {
		int javaDOW = date.get(Calendar.DAY_OF_WEEK);
		int dow = javaDOW - 2;
		if (dow < 0) {
			dow += 7;
		}
		return DayEnum.values()[dow];
	}

	@Override
	public boolean isTakingLunchOn(GregorianCalendar date) {
		DayEnum dow = this.getDayOfWeek(date);
		return this.workSchedule.get(dow).isTakingLunchToday();
	}

	@Override
	public int getWorkStartTime(GregorianCalendar date) {
		DayEnum dow = this.getDayOfWeek(date);
		return this.workSchedule.get(dow).getStartTime();
	}

	@Override
	public int getWorkEndTime(GregorianCalendar date) {
		DayEnum dow = this.getDayOfWeek(date);
		return this.workSchedule.get(dow).getEndTime();
	}

	@Override
	public int getLunchStartTime(GregorianCalendar date) {
		DayEnum dow = this.getDayOfWeek(date);
		return this.workSchedule.get(dow).getLunchTime();
	}

	@Override
	public int getLunchEndTime(GregorianCalendar date) {
		DayEnum dow = this.getDayOfWeek(date);
		StandardWorkDay workDay = this.workSchedule.get(dow);
		return workDay.getLunchTime() + workDay.getLunchDurationMinutes() * 60;
	}
	
	public void addScheduleListener(DailyScheduleListener l) {
		this.scheduleListeners.add(l);
	}
	
	public void removeScheduleListener(DailyScheduleListener l) {
		this.scheduleListeners.remove(l);
	}
	
	private void notifyScheduleListeners() {
		for (DailyScheduleListener l : this.scheduleListeners) {
			l.dailyScheduleChanged(this);
		}
	}
}
