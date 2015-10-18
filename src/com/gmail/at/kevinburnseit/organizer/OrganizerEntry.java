package com.gmail.at.kevinburnseit.organizer;

import java.awt.Color;

/**
 * An interface for elements that appear on the calendar views. 
 * @author Kevin J. Burns
 *
 */
public interface OrganizerEntry {
	Color getColor();
	boolean shouldBeSaved();
}
