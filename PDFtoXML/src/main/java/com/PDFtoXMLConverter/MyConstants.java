package com.PDFtoXMLConverter;

public class MyConstants {

	private static final float maxGapBetweenTwoRows = 10;
	private static final double COLON_PERCENTAGE = 0.15;
	private static final double BOLD_PERCENTAGE = 0.30;
	private static final int maxGapBetweenBoxes = 20;
	private static final float mergeLinesDistance = 2;
	
	
	
	// Classifiers
	private static final String BOLD_FONT = "BOLD FONT";
	private static final String COLON_SEPARATION = "COLON_SEPARATION";
	private static final String FONT_CHANGE = "FONT CHANGE";
	
	
	public static float getMaxgapbetweentworows() {
		return maxGapBetweenTwoRows;
	}
	public static double getColonPercentage() {
		return COLON_PERCENTAGE;
	}
	public static double getBoldPercentage() {
		return BOLD_PERCENTAGE;
	}
	public static int getMaxgapbetweenboxes() {
		return maxGapBetweenBoxes;
	}
	public static float getMergelinesdistance() {
		return mergeLinesDistance;
	}
	
	
	
	public static String BoldFont() {
		return BOLD_FONT;
	}
	public static String ColonSeparation() {
		return COLON_SEPARATION;
	}
	public static String FontChange() {
		return FONT_CHANGE;
	}
	
}
