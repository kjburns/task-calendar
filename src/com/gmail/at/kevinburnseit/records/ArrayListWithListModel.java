package com.gmail.at.kevinburnseit.records;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * An ArrayList which implements ListModel.
 * @author Kevin J. Burns
 *
 * @param <E> Parameter of the ArrayList
 */
public class ArrayListWithListModel<E> extends ArrayList<E> implements ListModel<E> {
	private static final long serialVersionUID = 5988511857016133366L;

	private HashSet<ListDataListener> listeners = new HashSet<>();
	
	@Override
	public void addListDataListener(ListDataListener l) {
		this.listeners.add(l);
	}

	@Override
	public E getElementAt(int index) {
		return this.get(index);
	}

	@Override
	public int getSize() {
		return this.size();
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		this.listeners.remove(l);
	}

	private void notifyListenersOfAdd(int start, int end) {
		this.notifyListeners(
				new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, start, end));
	}
	
	private void notifyListenersOfInsertion(int start, int end) {
		/*
		 * Does the same thing as notifyListenersOfAdd.
		 */
		this.notifyListenersOfAdd(start, end);
	}

	private void notifyListenersOfDeletion(int startIndex, int endIndex) {
		this.notifyListeners(new ListDataEvent(
				this, ListDataEvent.INTERVAL_REMOVED, startIndex, endIndex));
	}

	private void notifyListenersOfContentChange(int start, int end) {
		this.notifyListeners(
				new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, start, end));
	}

	private void notifyListeners(ListDataEvent ev) {
		for (ListDataListener l : this.listeners) {
			switch(ev.getType()) {
			case ListDataEvent.CONTENTS_CHANGED:
				l.contentsChanged(ev);
				break;
			case ListDataEvent.INTERVAL_ADDED:
				l.intervalAdded(ev);
				break;
			case ListDataEvent.INTERVAL_REMOVED:
				l.intervalRemoved(ev);
				break;
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
	@Override
	public boolean add(E e) {
		boolean ret = super.add(e);
		
		int index = this.size() - 1;
		this.notifyListenersOfAdd(index, index);
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#add(int, java.lang.Object)
	 */
	@Override
	public void add(int index, E element) {
		super.add(index, element);
		
		this.notifyListenersOfInsertion(index, index);
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		int startIndex = this.size();
		boolean ret = super.addAll(c);
		int endIndex = this.size() - 1;
		
		if (c.size() > 0) {
			this.notifyListenersOfAdd(startIndex, endIndex);
		}
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#addAll(int, java.util.Collection)
	 */
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		boolean ret = super.addAll(index, c);
		
		if (c.size() > 0) {
			this.notifyListenersOfInsertion(index, index + c.size() - 1);
		}
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#clear()
	 */
	@Override
	public void clear() {
		int endIndex = this.size() - 1;
		super.clear();
		
		this.notifyListenersOfDeletion(0, endIndex);
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#remove(int)
	 */
	@Override
	public E remove(int index) {
		E ret = super.remove(index);
		this.notifyListenersOfDeletion(index, index);
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		int index = this.indexOf(o);
		boolean ret = super.remove(o);
		if (ret) {
			this.notifyListenersOfDeletion(index, index);
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean ret = false;
		for (Object item : c) {
			ret = ret || this.remove(item);
		}
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#removeIf(java.util.function.Predicate)
	 */
	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		ArrayList<E> removeList = new ArrayList<>();
		for (E item : this) {
			if (filter.test(item)) removeList.add(item);
		}
		
		return this.removeAll(removeList);
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#removeRange(int, int)
	 */
	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		super.removeRange(fromIndex, toIndex);
		
		// because of the way toIndex is defined
		this.notifyListenersOfDeletion(fromIndex, toIndex - 1);
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#replaceAll(java.util.function.UnaryOperator)
	 */
	@Override
	public void replaceAll(UnaryOperator<E> operator) {
		super.replaceAll(operator);
		
		this.notifyListenersOfContentChange(0, this.size() - 1);
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		ArrayList<Object> removeList = new ArrayList<>();
		
		for (E item : this) {
			if (!c.contains(item)) removeList.add(item);
		}
		
		return this.removeAll(removeList);
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#set(int, java.lang.Object)
	 */
	@Override
	public E set(int index, E element) {
		E ret = super.set(index, element);
		this.notifyListenersOfContentChange(index, index);
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#sort(java.util.Comparator)
	 */
	@Override
	public void sort(Comparator<? super E> c) {
		super.sort(c);
		
		this.notifyListenersOfContentChange(0, this.size() - 1);
	}
}
