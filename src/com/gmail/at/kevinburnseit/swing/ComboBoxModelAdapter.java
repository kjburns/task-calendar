package com.gmail.at.kevinburnseit.swing;

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 * An adapter for using a ListModel as a ComboBoxModel.
 * @author Kevin J. Burns
 *
 * @param <T> Type of item in ListModel and ComboBoxModel
 */
public class ComboBoxModelAdapter<T> implements ComboBoxModel<T> {
	private ListModel<T> listModel;
	private T selectedItem = null;
	
	/**
	 * Constructor. Creates a new ComboBoxModel from an existing ListModel.
	 * @param model The ListModel to turn into a ComboBoxModel.
	 */
	public ComboBoxModelAdapter(ListModel<T> model) {
		this.listModel = model;
	}
	
	@Override
	public void addListDataListener(ListDataListener l) {
		this.listModel.addListDataListener(l);
	}

	@Override
	public T getElementAt(int index) {
		return this.listModel.getElementAt(index);
	}

	@Override
	public int getSize() {
		return this.listModel.getSize();
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		this.listModel.removeListDataListener(l);
	}

	@Override
	public Object getSelectedItem() {
		return this.selectedItem;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSelectedItem(Object arg0) {
		this.selectedItem = (T)arg0;
	}
}
