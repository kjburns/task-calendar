package com.gmail.at.kevinburnseit.organizer;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

import com.gmail.at.kevinburnseit.organizer.gui.AbortOrContinueSetupDialog;
import com.gmail.at.kevinburnseit.organizer.gui.AbortOrContinueSetupDialog.ResultEnum;
import com.gmail.at.kevinburnseit.organizer.gui.DataFolderDialog;
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
	private boolean initialSetupComplete = false;
	
	public static void main(String[] args) {
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
					// TODO Auto-generated catch block
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
		
		return false;
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
		p.put(STG_appDataPath, this.appDataPath);
		p.flush();
	}

	/**
	 * @return the appDataPath
	 */
	public String getAppDataPath() {
		return appDataPath;
	}

	/**
	 * @param appDataPath the appDataPath to set
	 */
	public void setAppDataPath(String appDataPath) {
		this.appDataPath = appDataPath;
	}
}
