package com.gmail.at.kevinburnseit.swing;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Set;

public class NewDialog extends JDialog {
	public enum ButtonTypeEnum {
		DEFAULT,
		CANCEL,
		NONE;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8285755852813791808L;
	public DialogResult dialogResult = DialogResult.NULL;
	private final JPanel buttonArea = new JPanel();
	protected JPanel buttonPanel;
	protected final int outerMargin = 10;
	protected final int innerMargin = 5;
	protected JButton buttonWithCancelResult;
	private JButton buttonWithDefaultResult;
	private HashMap<String, Boolean> validationHolds = new HashMap<>();
	protected JPanel contentPanel;
	protected JScrollPane contentScrollPane;

	public NewDialog() {
		this((JFrame)null);
	}
	
	public NewDialog(JFrame appWindow) {
		super(appWindow);
		this.setModal(true);
		this.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		this.setResizable(false);
		
		this.rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke("ESCAPE"), "close");
		this.rootPane.getActionMap().put("close", new AbstractAction() {
			private static final long serialVersionUID = 8052223896748273268L;
			@Override
			public void actionPerformed(ActionEvent e) {
				if (buttonWithCancelResult != null) buttonWithCancelResult.doClick();
				else setVisible(false);
			}
		});

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.LINE_AXIS));
		
		JPanel contentArea = new JPanel();
		BorderLayout border = new BorderLayout();
		contentArea.setLayout(border);
		getContentPane().add(contentArea);
		
		Box box = new Box(BoxLayout.LINE_AXIS);
		box.setPreferredSize(new Dimension(this.outerMargin, this.outerMargin));
		contentArea.add(box, BorderLayout.PAGE_START);
		
		box = new Box(BoxLayout.LINE_AXIS);
		box.setPreferredSize(new Dimension(this.outerMargin, this.outerMargin));
		contentArea.add(box, BorderLayout.PAGE_END);
		
		box = new Box(BoxLayout.PAGE_AXIS);
		box.setPreferredSize(new Dimension(this.outerMargin, this.outerMargin));
		contentArea.add(box, BorderLayout.LINE_START);
		
		contentPanel = new JPanel();

		this.contentScrollPane = new JScrollPane(this.contentPanel);
		contentArea.add(this.contentScrollPane, BorderLayout.CENTER);

		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);
		
		getContentPane().add(getButtonArea());
		getButtonArea().setLayout(new FlowLayout(FlowLayout.CENTER, this.outerMargin, this.outerMargin));
		
		buttonPanel = new JPanel();
		buttonPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		getButtonArea().add(buttonPanel);
		buttonPanel.setLayout(new GridLayout(0, 1, 0, this.innerMargin));
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				contentPanel.validate();
				buttonPanel.validate();
				getContentPane().validate();
				pack();
			}
		});
	}

	protected final JButton addButton(String txt, final DialogResult result, ButtonTypeEnum buttonType) {
		JButton ret = new JButton(txt);
		if (result != null) {
			ret.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					NewDialog.this.dialogResult = result;
				}
			});
		}
		this.buttonPanel.add(ret);
		
		switch(buttonType) {
		case DEFAULT:
			this.rootPane.setDefaultButton(ret);
			this.buttonWithDefaultResult = ret;
			break;
		case CANCEL:
			this.buttonWithCancelResult = ret;
			break;
		default:;
		}
	
		return ret;
	}

	public final DialogResult showDialog() {
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		return this.dialogResult;
	}

	public final JPanel getButtonArea() {
		return buttonArea;
	}

	public final void putValidationHold(String key, boolean hold) {
		this.validationHolds.put(key, hold);
		this.checkValidationHolds();
	}
	
	public Set<String> getValidationKeysToCheck() {
		return this.validationHolds.keySet();
	}

	protected final void checkValidationHolds() {
		if (this.buttonWithDefaultResult == null) return;
		
		boolean onHold = false;
		for (String key : this.getValidationKeysToCheck()) {
			if (this.validationHolds.containsKey(key)) {
				onHold = onHold || this.validationHolds.get(key).booleanValue();
			}
		}
		
		this.buttonWithDefaultResult.setEnabled(!onHold);
	}
}
