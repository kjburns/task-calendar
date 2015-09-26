package com.gmail.at.kevinburnseit.swing.calendar;

import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;

public class ETabbedPane extends JPanel {
	public enum ButtonSide {
		LEADING,
		TRAILING;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -2090869893562246860L;
	private JTabbedPane tabs;
	private JToolBar leadingButtons;
	private JToolBar trailingButtons;
	//private JPanel dropDown;
	private OffsetTabbedPaneUI tabUI;
	
	public ETabbedPane(ComponentOrientation orient) {
		SpringLayout sl = new SpringLayout();
		this.setLayout(sl);
		
		this.leadingButtons = new JToolBar();
		this.leadingButtons.setBorderPainted(false);
		this.leadingButtons.setFloatable(false);
		this.leadingButtons.setOpaque(false);
		this.leadingButtons.setComponentOrientation(orient);
		this.leadingButtons.setLayout(new FlowLayout(FlowLayout.LEADING, 1, 1));
		sl.putConstraint(SpringLayout.NORTH, this.leadingButtons, 0, SpringLayout.NORTH, this);
		String side = ((orient == ComponentOrientation.RIGHT_TO_LEFT) ? SpringLayout.EAST : SpringLayout.WEST);
		sl.putConstraint(side, this.leadingButtons, 0, side, this);
		this.add(leadingButtons);
		
		this.trailingButtons = new JToolBar();
		this.trailingButtons.setBorderPainted(false);
		this.trailingButtons.setFloatable(false);
		this.trailingButtons.setOpaque(false);
		this.trailingButtons.setComponentOrientation(orient);
		this.trailingButtons.setLayout(new FlowLayout(FlowLayout.LEADING, 1, 1));
		sl.putConstraint(SpringLayout.NORTH, this.trailingButtons, 0, SpringLayout.NORTH, this);
		side = ((orient == ComponentOrientation.RIGHT_TO_LEFT) ? SpringLayout.WEST : SpringLayout.EAST);
		sl.putConstraint(side, this.trailingButtons, 0, side, this);
		this.add(trailingButtons);
		
		this.tabs = new JTabbedPane();
		this.tabs.setComponentOrientation(orient);
		sl.putConstraint(SpringLayout.NORTH, this.tabs, 0, SpringLayout.NORTH, this);
		sl.putConstraint(SpringLayout.SOUTH, this.tabs, 0, SpringLayout.SOUTH, this);
		sl.putConstraint(SpringLayout.WEST, this.tabs, 0, SpringLayout.WEST, this);
		sl.putConstraint(SpringLayout.EAST, this.tabs, 0, SpringLayout.EAST, this);
		tabUI = new OffsetTabbedPaneUI();
		this.tabs.setUI(tabUI);
		this.add(tabs);
		
		this.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (tabs.getTabCount() > 0) {
					tabs.getSelectedComponent().requestFocusInWindow();
				}
			}
		});
	}

	/**
	 * @return the tabs
	 */
	public JTabbedPane getJTabbedPane() {
		return tabs;
	}
	
	public void addButton(JButton button, ButtonSide side) {
		button.setBorderPainted(false);
		button.setFocusable(false);
		button.setMargin(new Insets(1, 1, 1, 1));
		((side == ButtonSide.LEADING) ? this.leadingButtons : this.trailingButtons).add(button);
		this.tabUI.setMinHeight(
				Math.max(this.leadingButtons.getPreferredSize().height,
						 this.trailingButtons.getPreferredSize().height));
		this.tabUI.setLeadingOffset(this.leadingButtons.getPreferredSize().width);
		this.tabUI.setTrailingOffset(this.trailingButtons.getPreferredSize().width);
		this.validate();
	}
}
