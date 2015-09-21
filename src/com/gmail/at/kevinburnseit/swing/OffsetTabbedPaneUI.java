package com.gmail.at.kevinburnseit.swing;

import java.awt.Insets;

import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class OffsetTabbedPaneUI extends BasicTabbedPaneUI {
	private int leadingOffset = 0;
	private int minHeight = 0;
	private int trailingOffset;
	
	public OffsetTabbedPaneUI() {
		super();
	}

	/* (non-Javadoc)
	 * @see javax.swing.plaf.basic.BasicTabbedPaneUI#calculateTabHeight(int, int, int)
	 */
	@Override
	protected int calculateTabHeight(int tabPlacement, int tabIndex,
			int fontHeight) {
		return Math.max(super.calculateTabHeight(tabPlacement, tabIndex, fontHeight), this.minHeight);
	}

	/* (non-Javadoc)
	 * @see javax.swing.plaf.basic.BasicTabbedPaneUI#getTabAreaInsets(int)
	 */
	@Override
	protected Insets getTabAreaInsets(int tabPlacement) {
		// ignores tab placement for now
		return new Insets(0, this.leadingOffset, 0, this.trailingOffset);
	}

	/**
	 * @param offset the offset to set
	 */
	public void setLeadingOffset(int offset) {
		this.leadingOffset = offset;
	}

	/**
	 * @param minHeight the minHeight to set
	 */
	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}

	public void setTrailingOffset(int offset) {
		this.trailingOffset = offset;
	}
}
