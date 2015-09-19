package com.gmail.at.kevinburnseit.util;

public class RegexHelper {
	public static final String integer = "-?\\d+";
	public static final String integerGroup = getGroup(integer);
	
	public static final String doubl_ = "-?\\d*\\.\\d+";
	public static final String doubleGroup = getGroup(doubl_);
	
	public static final String xmlTime = "\\d{4}\\-\\d{2}\\-\\d{2}T\\d{2}:\\d{2}:\\d{2}";
	public static final String xmlTimeGroup = getGroup(xmlTime);
	
	private static String getGroup(String str) {
		return "(" + str + ")";
	}
}
