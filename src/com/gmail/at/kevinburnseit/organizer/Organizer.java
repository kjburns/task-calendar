package com.gmail.at.kevinburnseit.organizer;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.gmail.at.kevinburnseit.organizer.gui.AbortOrContinueSetupDialog;
import com.gmail.at.kevinburnseit.organizer.gui.AbortOrContinueSetupDialog.ResultEnum;
import com.gmail.at.kevinburnseit.organizer.gui.DataFolderDialog;
import com.gmail.at.kevinburnseit.organizer.gui.StandardWorkWeekEditor;
import com.gmail.at.kevinburnseit.swing.DialogResult;

/**
 * The main application window for this application.
 * @author Kevin J. Burns
 *
 */
public class Organizer extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1532652861021682522L;
	private static final String STG_appDataPath = "data-folder";
	private static final String STG_initialSetupComplete = "init-setup-complete";

	private String appDataPath =
			System.getProperty("user.home") + File.separator + ".organizer";
	private StandardWorkWeek workSchedule;
	private boolean initialSetupComplete = false;
	
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
		
		File workScheduleFile = new File(folder, "schedule.xml");
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
}
