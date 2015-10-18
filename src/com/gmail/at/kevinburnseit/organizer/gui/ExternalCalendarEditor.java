package com.gmail.at.kevinburnseit.organizer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import com.gmail.at.kevinburnseit.organizer.ExternalCalendarDefinition;
import com.gmail.at.kevinburnseit.records.RecordEditor;
import com.gmail.at.kevinburnseit.swing.DialogResult;
import com.gmail.at.kevinburnseit.swing.NewDialog;
import com.gmail.at.kevinburnseit.swing.RowControl;
import com.gmail.at.kevinburnseit.swing.ValidatingTextBox;
import com.gmail.at.kevinburnseit.swing.ValidatingTextBoxExpectsNonEmptyString;
import com.gmail.at.kevinburnseit.swing.ValidatingTextBoxValidator;

/**
 * Dialog allowing the user to define an external calendar.
 * @author Kevin J. Burns
 *
 */
public class ExternalCalendarEditor extends NewDialog 
		implements RecordEditor<ExternalCalendarDefinition> {
	private static final long serialVersionUID = 1803871571894247155L;

	private ExternalCalendarDefinition data;
	private ValidatingTextBox nameTextBox;
	private ValidatingTextBox urlTextBox;
	private JRadioButton acceptAllRadio;
	private JRadioButton askFirstRadio;

	private JButton okBtn;

	private JButton cancelBtn;
	
	public ExternalCalendarEditor() {
		this.buildUI();
		this.populate();
	}
	
	private void buildUI() {
		this.setTitle("External Calendar Definition");
		
		BoxLayout layout = new BoxLayout(this.contentPanel, BoxLayout.PAGE_AXIS);
		this.contentPanel.setLayout(layout);

		RowControl rc;
		JLabel label;
		
		rc = new RowControl();
		this.contentPanel.add(rc);
		
		label = new JLabel("Name:");
		rc.add(label);
		
		this.nameTextBox = new ValidatingTextBox();
		rc.add(this.nameTextBox);
		this.nameTextBox.setValidationKey("name");
		this.nameTextBox.setColumns(15);
		this.nameTextBox.addValidationListener(
				new ValidatingTextBoxExpectsNonEmptyString());
		
		rc = new RowControl();
		this.contentPanel.add(rc);
		
		label = new JLabel("URL:");
		rc.add(label);
		
		this.urlTextBox = new ValidatingTextBox();
		rc.add(this.urlTextBox);
		this.urlTextBox.setValidationKey("url");
		this.urlTextBox.setColumns(15);
		this.urlTextBox.addValidationListener(new ValidatingTextBoxValidator() {
			@Override
			public boolean validate(String newValue) {
				try {
					@SuppressWarnings("unused")
					URL url = new URL(newValue);
					return true;
				} catch (MalformedURLException e) {
					return false;
				}
			};
		});
		
		ButtonGroup bg = new ButtonGroup();
		
		label = new JLabel("When a new event is created on this external calendar:");
		this.contentPanel.add(label);
		
		this.acceptAllRadio = new JRadioButton(
				"Automatically add that event to my calendar here");
		this.contentPanel.add(this.acceptAllRadio);
		bg.add(this.acceptAllRadio);
		
		this.askFirstRadio = new JRadioButton(
				"Let me pick which events appear on the calendar here");
		this.contentPanel.add(this.askFirstRadio);
		bg.add(this.askFirstRadio);
		
		ActionListener closeListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		};
		
		this.okBtn = this.addButton("OK", DialogResult.OK, ButtonTypeEnum.DEFAULT);
		this.okBtn.addActionListener(closeListener);
		
		this.cancelBtn = 
				this.addButton("Cancel", DialogResult.CANCEL, ButtonTypeEnum.CANCEL);
		this.cancelBtn.addActionListener(closeListener);
		
		this.pack();
	}

	@Override
	public ExternalCalendarDefinition getOutput() {
		if (this.dialogResult != DialogResult.OK) return null;

		ExternalCalendarDefinition ret = new ExternalCalendarDefinition();
		ret.setName(this.nameTextBox.getText());
		ret.setUrl(this.urlTextBox.getText());
		ret.setAlwaysAccept(this.acceptAllRadio.isSelected());
		
		return ret;
	}

	@Override
	public void setData(ExternalCalendarDefinition input) {
		this.data = input;
		this.populate();
	}

	private void populate() {
		if (this.data == null) {
			this.nameTextBox.setText("Untitled Calendar");
			this.urlTextBox.setText("");
			this.acceptAllRadio.setSelected(true);
		}
		else {
			this.nameTextBox.setText(this.data.getName());
			this.urlTextBox.setText(this.data.getUrl());
			if (this.data.isAlwaysAccept()) {
				this.acceptAllRadio.setSelected(true);
			}
			else {
				this.askFirstRadio.setSelected(true);
			}
		}
		
		this.urlTextBox.validateNow();
	}
}
