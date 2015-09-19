package com.gmail.at.kevinburnseit.organizer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import com.gmail.at.kevinburnseit.organizer.Organizer;
import com.gmail.at.kevinburnseit.swing.DialogResult;
import com.gmail.at.kevinburnseit.swing.NewDialog;
import com.gmail.at.kevinburnseit.swing.ValidatingTextBox;
import com.gmail.at.kevinburnseit.swing.ValidatingTextBoxAction;
import com.gmail.at.kevinburnseit.swing.ValidatingTextBoxValidator;

public class DataFolderDialog extends NewDialog {
	private enum FolderValidationFailReasonEnum {
		READ_ONLY("Selected folder is read-only."),
		IS_NOT_DIRECTORY("Selection is not a folder."),
		PARENT_DNE("Parent of proposed folder does not exist."),
		PARENT_READ_ONLY("Parent of proposed folder is read-only."),
		PARENT_IS_NOT_DIRECTORY("Parent of proposed folder is not a folder.");
		
		private String message;
		
		private FolderValidationFailReasonEnum(String msg) {
			this.message = msg;
		}

		/**
		 * @return the message
		 */
		public String getMessage() {
			return message;
		}
	}
	private static final long serialVersionUID = 704172727734836624L;
	private static FolderValidationFailReasonEnum pathFailureReason = null;
	private static final ValidatingTextBoxValidator pathValidator = 
			new ValidatingTextBoxValidator() {
		@Override
		public boolean validate(String newValue) {
			File f = new File(newValue);
			
			if (f.exists()) {
				if (!f.isDirectory()) {
					pathFailureReason = FolderValidationFailReasonEnum.IS_NOT_DIRECTORY;
					return false;
				}
				if (!f.canWrite()) {
					pathFailureReason = FolderValidationFailReasonEnum.READ_ONLY;
					return false;
				}
				return true;
			}
			
			File parent = f.getParentFile();
			if (!parent.exists()) {
				pathFailureReason = FolderValidationFailReasonEnum.PARENT_DNE;
				return false;
			}
			if (!parent.isDirectory()) {
				pathFailureReason = 
						FolderValidationFailReasonEnum.PARENT_IS_NOT_DIRECTORY;
				return false;
			}
			if (!parent.canWrite()) {
				pathFailureReason = FolderValidationFailReasonEnum.PARENT_READ_ONLY;
				return false;
			}
			
			return true;
		}
	};

	private Organizer org;
	private ValidatingTextBox dataPathTextBox;
	private JLabel failureLabel;
	private JButton browseButton;
	private JButton okButton;
	private JButton cancelBtn;
	
	public DataFolderDialog(Organizer app) {
		this.org = app;
		
		this.buildUI();
		this.failureLabel.setVisible(false);
	}

	private void buildUI() {
		BoxLayout layout = new BoxLayout(this.contentPanel, BoxLayout.PAGE_AXIS);
		this.contentPanel.setLayout(layout);
		
		JLabel label = new JLabel("Select folder to store program data:");
		this.contentPanel.add(label);
		
		this.dataPathTextBox = new ValidatingTextBox();
		this.dataPathTextBox.setValidationKey("path-text-box");
		this.dataPathTextBox.addValidationListener(pathValidator);
		this.dataPathTextBox.setColumns(30);
		this.dataPathTextBox.setText(this.org.getAppDataPath());
		this.contentPanel.add(this.dataPathTextBox);
		
		ImageIcon errorIcon = new ImageIcon(
				this.getClass().getClassLoader().getResource("res/error.png"));
		this.failureLabel = new JLabel(" ");
		this.failureLabel.setIcon(errorIcon);
		this.contentPanel.add(this.failureLabel);
		
		this.dataPathTextBox.addAfterValidationAction(new ValidatingTextBoxAction() {
			@Override
			public void afterValidation(boolean ok) {
				if (ok) failureLabel.setVisible(false);
				else {
					failureLabel.setText(pathFailureReason.getMessage());
					failureLabel.setVisible(true);
				}
			}
		});

		this.browseButton = this.addButton(
				"Browse for Folder...", null, ButtonTypeEnum.NONE);
		this.browseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showFolderChooser();
			}
		});
		
		this.okButton = this.addButton("OK", DialogResult.OK, ButtonTypeEnum.DEFAULT);
		this.okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				org.setAppDataPath(dataPathTextBox.getText());
				setVisible(false);
			}
		});
		
		this.cancelBtn = this.addButton("Cancel", DialogResult.CANCEL, 
				ButtonTypeEnum.CANCEL);
		this.cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		this.pack();
	}

	protected void showFolderChooser() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		File initFolder = new File(this.org.getAppDataPath());
		if (!initFolder.exists()) initFolder = initFolder.getParentFile();
		
		fc.setSelectedFile(initFolder);
		int ret = fc.showOpenDialog(this.org);
		if (ret == JFileChooser.APPROVE_OPTION) {
			this.dataPathTextBox.setText(fc.getSelectedFile().getAbsolutePath());
		}
	}
}
