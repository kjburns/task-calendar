package com.gmail.at.kevinburnseit.organizer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import com.gmail.at.kevinburnseit.organizer.HolidayRule;
import com.gmail.at.kevinburnseit.organizer.HolidayRuleCollection;
import com.gmail.at.kevinburnseit.organizer.HolidayRuleRelative;
import com.gmail.at.kevinburnseit.records.RecordEditor;
import com.gmail.at.kevinburnseit.swing.ComboBoxModelAdapter;
import com.gmail.at.kevinburnseit.swing.DialogResult;
import com.gmail.at.kevinburnseit.swing.NewDialog;
import com.gmail.at.kevinburnseit.swing.ValidatingTextBox;
import com.gmail.at.kevinburnseit.swing.ValidatingTextBoxExpectsNonEmptyString;
import com.gmail.at.kevinburnseit.swing.ValidatingTextBoxExpectsPositiveInteger;

/**
 * An editor for holidays relative to another holiday.
 * @author Kevin J. Burns
 *
 */
public class HolidayEditorRelative extends NewDialog 
		implements RecordEditor<HolidayRuleRelative> {
	private static final long serialVersionUID = -4842204962315429098L;
	
	private enum BeforeOrAfterEnum {
		BEFORE(-1), 
		AFTER(1);
		
		private int sign;
		
		private BeforeOrAfterEnum(int sign) {
			this.sign = sign;
		}

		@Override
		public String toString() {
			return this.name().toLowerCase();
		}
	}
	
	private static final DefaultComboBoxModel<BeforeOrAfterEnum> enumModel =
			new DefaultComboBoxModel<>();

	private JComboBox<HolidayRule> holidaysDropDown;
	private JLabel dayLabel;
	private JComboBox<BeforeOrAfterEnum> beforeAfterDropdown;
	private ValidatingTextBox daysTextBox;
	private ValidatingTextBox nameTextBox;
	private JCheckBox weekdayOnlyCheckBox;
	private HolidayRuleCollection allRules;
	private HolidayRuleRelative input = null;
	private JButton okBtn;
	private JButton cancelBtn;

	/**
	 * Constructor. Populates the dialog with default values.
	 * @param rules All holiday rules that currently exist.
	 */
	public HolidayEditorRelative(HolidayRuleCollection rules) {
		this.allRules = rules;
		
		this.buildUI();
		this.populate();
	}

	private void buildUI() {
		HolidayEditorRelative.enumModel.addElement(BeforeOrAfterEnum.BEFORE);
		HolidayEditorRelative.enumModel.addElement(BeforeOrAfterEnum.AFTER);
		
		this.setTitle("Define Relative Holiday");
		BoxLayout layout = new BoxLayout(this.contentPanel, BoxLayout.PAGE_AXIS);
		this.contentPanel.setLayout(layout);
		
		RowControl rc;
		
		rc = new RowControl();
		this.contentPanel.add(rc);
		
		JLabel label;
		label = new JLabel("Name:");
		rc.add(label);
		
		this.nameTextBox = new ValidatingTextBox();
		this.nameTextBox.setValidationKey("name");
		this.nameTextBox.addValidationListener(
				new ValidatingTextBoxExpectsNonEmptyString());
		this.nameTextBox.setColumns(20);
		rc.add(this.nameTextBox);
		
		this.weekdayOnlyCheckBox = new JCheckBox("Observed on nearest weekday");
		this.contentPanel.add(this.weekdayOnlyCheckBox);
		
		rc = new RowControl();
		this.contentPanel.add(rc);
		
		label = new JLabel("Occurs ");
		rc.add(label);
		
		this.daysTextBox = new ValidatingTextBox();
		this.daysTextBox.addValidationListener(
				new ValidatingTextBoxExpectsPositiveInteger());
		this.daysTextBox.setColumns(3);
		this.daysTextBox.setValidationKey("days");
		rc.add(this.daysTextBox);
		
		this.dayLabel = new JLabel(" day(s) ");
		rc.add(this.dayLabel);
		
		this.beforeAfterDropdown = new JComboBox<BeforeOrAfterEnum>(enumModel);
		rc.add(this.beforeAfterDropdown);
		
		ComboBoxModelAdapter<HolidayRule> model = 
				new ComboBoxModelAdapter<>(this.allRules); 
		this.holidaysDropDown = new JComboBox<HolidayRule>(model);
		this.holidaysDropDown.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ev) {
				if (ev.getStateChange() == ItemEvent.DESELECTED) {
					putValidationHold("holiday", true);
				}
				
				if (ev.getStateChange() == ItemEvent.SELECTED) {
					putValidationHold("holiday", ev.getItem() == input);
				}
			}
		});
		rc.add(this.holidaysDropDown);
		
		this.okBtn = this.addButton("OK", DialogResult.OK, ButtonTypeEnum.DEFAULT);
		this.okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		
		this.cancelBtn = 
				this.addButton("Cancel", DialogResult.CANCEL, ButtonTypeEnum.CANCEL);
		this.cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		this.pack();
	}

	private void populate() {
		if (this.input == null) {
			this.nameTextBox.setText("Untitled Holiday");
			this.weekdayOnlyCheckBox.setSelected(true);
			this.daysTextBox.setText("1");
			this.beforeAfterDropdown.setSelectedItem(BeforeOrAfterEnum.AFTER);
			this.holidaysDropDown.setSelectedIndex(0);
		}
		else {
			this.nameTextBox.setText(this.input.getName());
			this.weekdayOnlyCheckBox.setSelected(
					this.input.isAlwaysObservedOnWeekday());
			int daysAfter = this.input.getDaysAfterReference();
			this.daysTextBox.setText("" + Math.abs(daysAfter));
			this.beforeAfterDropdown.setSelectedItem((daysAfter > 0) ? 
					BeforeOrAfterEnum.AFTER : 
					BeforeOrAfterEnum.BEFORE);
			this.holidaysDropDown.setSelectedItem(this.input.getReference());
		}
	}

	@Override
	public HolidayRuleRelative getOutput() {
		if (this.dialogResult == DialogResult.CANCEL) return null;
		
		HolidayRuleRelative ret = new HolidayRuleRelative();
		ret.setName(this.nameTextBox.getText());
		ret.setAlwaysObservedOnWeekday(this.weekdayOnlyCheckBox.isSelected());
		int days = 
				((BeforeOrAfterEnum)this.beforeAfterDropdown.getSelectedItem()).sign * 
				Integer.valueOf(this.daysTextBox.getText());
		ret.setDaysAfterReference(days);
		ret.setReference((HolidayRule)this.holidaysDropDown.getSelectedItem());
		
		return ret;
	}
	
	@Override
	public void setData(HolidayRuleRelative input) {
		this.input = input;
		this.populate();
	}
}
