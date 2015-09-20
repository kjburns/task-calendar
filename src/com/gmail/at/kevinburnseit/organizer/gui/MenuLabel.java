package com.gmail.at.kevinburnseit.organizer.gui;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;

public class MenuLabel extends JLabel {
	private static final long serialVersionUID = -2297792020866504411L;

	public static interface MenuLabelAction {
		void itemClicked(MenuLabel source);
	}
	
	private Font normalFont;
	private Font hoverFont;
	private ArrayList<MenuLabelAction> clickListeners = new ArrayList<>();
	
	public MenuLabel() {
		this(null, null);
	}
	
	public MenuLabel(String text) {
		this(text, null);
	}
	
	public MenuLabel(Icon img) {
		this(null, img);
	}
	
	public MenuLabel(String text, Icon img) {
		super();
		this.setText(text);
		this.setIcon(img);
		
		this.normalFont = this.getFont();
		Map<TextAttribute, Object> attrMap = new Hashtable<TextAttribute, Object>();
		attrMap.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		this.hoverFont = this.normalFont.deriveFont(attrMap);
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				for (MenuLabelAction a : clickListeners) {
					a.itemClicked(MenuLabel.this);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				setFont(hoverFont);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				setFont(normalFont);
			}
		});
	}
	
	public void addClickListener(MenuLabelAction a) {
		if (!this.clickListeners.contains(a)) this.clickListeners.add(a);
	}
	
	public void removeClickListener(MenuLabelAction a) {
		this.clickListeners.remove(a);
	}
}
