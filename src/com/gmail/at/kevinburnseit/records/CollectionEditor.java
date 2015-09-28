package com.gmail.at.kevinburnseit.records;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.gmail.at.kevinburnseit.swing.DialogResult;
import com.gmail.at.kevinburnseit.swing.NewDialog;

public abstract class CollectionEditor<T extends Record> extends NewDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1616222688017146110L;
	protected JList<T> listBox;
	protected JButton btnAdd;
	protected JButton btnEdit;
	protected JButton btnDelete;
	protected JButton closeButton;
	protected JScrollPane scrollPaneForListBox;
	
	/**
	 * Creates a collection editor.
	 * @param model A list model which includes the collection to be edited. For
	 * convenience, creating an instance of (or subclassing) 
	 * {@link ArrayListWithListModel} fulfills this requirement.
	 */
	public CollectionEditor(ListModel<T> model) { 
		this.setResizable(false);
		
		this.contentPanel.setLayout(new BorderLayout());
		
		listBox = new JList<T>();
		if (model != null) listBox.setModel(model);
		scrollPaneForListBox = new JScrollPane(this.listBox);
		scrollPaneForListBox.setPreferredSize(new Dimension(300, 200));
		this.contentPanel.add(scrollPaneForListBox);
		
		btnAdd = this.addButton("Add...", DialogResult.NULL, ButtonTypeEnum.NONE);
		btnAdd.addActionListener(this.getAddButtonListener());
		
		btnEdit = this.addButton("Edit...", DialogResult.NULL, ButtonTypeEnum.NONE);
		btnEdit.addActionListener(this.getEditButtonListener());
		btnEdit.setEnabled(false);
		
		btnDelete = this.addButton("Delete...", DialogResult.NULL, 
				ButtonTypeEnum.NONE);
		btnDelete.addActionListener(this.getDeleteButtonListener());
		btnDelete.setEnabled(false);
		
		this.closeButton = this.addButton("Close", DialogResult.CANCEL, 
				ButtonTypeEnum.CANCEL);
		this.closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		final ListSelectionModel lsm = listBox.getSelectionModel();
		lsm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lsm.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) return;
				
				boolean enabled = !lsm.isSelectionEmpty();
				btnEdit.setEnabled(enabled);
				btnDelete.setEnabled(enabled);
			}
		});
		listBox.getModel().addListDataListener(new ListDataListener() {
			@Override
			public void contentsChanged(ListDataEvent arg0) {
				this.handleChange();
			}
			@Override
			public void intervalAdded(ListDataEvent arg0) {
				this.handleChange();
			}
			@Override
			public void intervalRemoved(ListDataEvent arg0) {
				this.handleChange();
			}
			private void handleChange() {
				boolean enabled = ((lsm.getMinSelectionIndex() >= 0) && 
						(lsm.getMinSelectionIndex() < listBox.getModel().getSize()));
				enabled = enabled && (!lsm.isSelectionEmpty());
				btnEdit.setEnabled(enabled);
				btnDelete.setEnabled(enabled);
			}
		});
		
		this.pack();
	}

	/**
	 * Gets the listener which will be alerted when the Add button is clicked.
	 * It is the responsibility of the listener to add any record to the list
	 * as necessary.
	 * @return
	 */
	public abstract ActionListener getAddButtonListener();

	/**
	 * Gets the listener which will be alerted when the Edit button is clicked.
	 * It is the responsibility of the listener to edit any record 
	 * as necessary.
	 * @return
	 */
	public abstract ActionListener getEditButtonListener();

	/**
	 * Gets the listener which will be alerted when the Delete button is clicked.
	 * It is the responsibility of the listener to delete any record from the list
	 * as necessary.
	 * @return
	 */
	public abstract ActionListener getDeleteButtonListener();

	/**
	 * Removes the Add button.
	 */
	protected final void removeAddButton() {
		this.getButtonArea().remove(this.btnAdd);
	}

	/**
	 * Removes the Edit button.
	 */
	protected final void removeEditButton() {
		this.getButtonArea().remove(this.btnEdit);
	}

	/**
	 * Removes the Delete button.
	 */
	protected final void removeDeleteButton() {
		this.getButtonArea().remove(this.btnDelete);
	}
}
