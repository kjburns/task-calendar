package com.gmail.at.kevinburnseit.organizer.gui;

import java.awt.GridLayout;

import javax.swing.JPanel;

import com.gmail.at.kevinburnseit.organizer.Organizer;

public class MenuPane extends JPanel {
	private static final long serialVersionUID = -314495275442273150L;
	
	private Organizer app;

	public MenuPane(Organizer app) {
		this.app = app;

		GridLayout layout = new GridLayout(0, 1, 10, 10);
		this.setLayout(layout);
	}
}
