package com.gmail.at.kevinburnseit.time;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.gmail.at.kevinburnseit.records.RecordEditor;
import com.gmail.at.kevinburnseit.swing.DialogResult;
import com.gmail.at.kevinburnseit.swing.NewDialog;
import com.gmail.at.kevinburnseit.swing.ValidatingTextBoxAction;
import com.gmail.at.kevinburnseit.time.IntervalTime.InvalidTimeException;
import com.gmail.at.kevinburnseit.time.TimeTextBox.ValueChangedListener;

public class TimeIntervalEditor extends NewDialog implements
		RecordEditor<IntervalTime> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8824920998632401886L;
	private IntervalTime input = null;
	private IntervalTime output = null;
	private TimeTextBox startTextBox;
	private TimeTextBox endTextBox;
	
	public TimeIntervalEditor(IntervalTime input) {
		this.input = input;
		
		this.init();
		if (this.input != null) this.populate();
		this.startTextBox.validateNow();
		this.endTextBox.validateNow();
	}
	
	public TimeIntervalEditor() {
		this(null);
	}
	
	private void populate() {
		try {
			this.startTextBox.setValue(this.input.getBegin());
			this.endTextBox.setValue(this.input.getEnd());
		} catch (InvalidTimeException e) {
			// this shouldn't happen
		}
	}

	@Override
	public IntervalTime getOutput() {
		return this.output;
	}

	@Override
	public void setData(IntervalTime input) {
		this.input = input;
		this.populate();
	}

	public void init() {
		this.setTitle("Edit time interval");
		this.setResizable(false);
		this.contentPanel.setLayout(new GridLayout(2, 1, 5, 5));
		
		JPanel beginPanel = new JPanel();
		this.contentPanel.add(beginPanel);
		
		beginPanel.setLayout(new FlowLayout());
		JLabel startLabel = new JLabel("Start time:");
		beginPanel.add(startLabel);

		this.startTextBox = new TimeTextBox();
		this.startTextBox.setColumns(7);
		this.startTextBox.setExplanationText("time-interval-editor-start-exp");
//		this.startTextBox.setToolTipText(Locales.getString("time-interval-editor-start-exp"));
		this.startTextBox.setHint(TimeTextBox.nearestToNoon);
		startLabel.setLabelFor(this.startTextBox);
		beginPanel.add(this.startTextBox);
		
		JPanel endPanel = new JPanel();
		this.contentPanel.add(endPanel);
		
		endPanel.setLayout(new FlowLayout());
		JLabel endLabel = new JLabel("End time:");
		endPanel.add(endLabel);
		
		this.endTextBox = new TimeTextBox();
		this.endTextBox.setColumns(7);
		this.endTextBox.setExplanationText("time-interval-editor-end-exp");
//		this.endTextBox.setToolTipText(Locales.getString("time-interval-editor-end-exp"));
		this.endTextBox.setHint(TimeTextBox.nearestToNoon);
		endLabel.setLabelFor(this.endTextBox);
		endPanel.add(this.endTextBox);
		
		final JButton okButton = this.addButton("ok", DialogResult.OK, ButtonTypeEnum.DEFAULT);
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					output = new IntervalTime(startTextBox.getValue(), endTextBox.getValue());
				} catch (InvalidTimeException e) {
					// this shouldn't happen
				}
				setVisible(false);
			}
		});
		JButton cancelButton = this.addButton("cancel", DialogResult.CANCEL, ButtonTypeEnum.CANCEL);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});

		ValidatingTextBoxAction ava = new ValidatingTextBoxAction() {
			@Override
			public void afterValidation(boolean ok) {
				okButton.setEnabled(startTextBox.getLastValidateResult() && endTextBox.getLastValidateResult());
			}
		};
		this.startTextBox.addAfterValidationAction(ava);
		this.endTextBox.addAfterValidationAction(ava);
		
		ValueChangedListener l = new ValueChangedListener() {
			@Override
			public void valueChanged(int oldTime, int newTime) {
				if ((oldTime == -1) && (newTime != -1)) {
					startTextBox.setHint(TimeTextBox.beforeUsing12HourTime);
					startTextBox.setClueProvider(endTextBox);
					endTextBox.setHint(TimeTextBox.afterUsing12HourTime);
					endTextBox.setClueProvider(startTextBox);
				}
			}
		};
		this.startTextBox.addValueChangedListener(l);
		this.endTextBox.addValueChangedListener(l);
		
		this.pack();
	}
	
	public static void test() {
		IntervalTime input;
		try {
			input = new IntervalTime(43200, 46800);
			TimeIntervalEditor tie = new TimeIntervalEditor(input);
			tie.showDialog();
			IntervalTime output = tie.getOutput();
			tie.dispose();
			JOptionPane.showMessageDialog(null, output.toString());
			tie = new TimeIntervalEditor(output);
			tie.showDialog();
			JOptionPane.showMessageDialog(null, tie.getOutput().toString());
			tie.dispose();
		} catch (InvalidTimeException e) {
			e.printStackTrace();
		}
	}
}
