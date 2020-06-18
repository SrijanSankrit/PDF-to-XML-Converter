package com.PDFtoXMLConverter;

public class MyConstants {

	public static final float maxGapBetweenTwoRows = 10;
	public static final double colonPercentage = 0.15;
	public static final double boldPercentage = 0.30;
	public static final int maxGapBetweenBoxes = 20;
	public static final float mergeLinesDistance = 2;
	private static final double COLON_PERCENTAGE = 0.15;
	private static final double BOLD_PERCENTAGE = 0.30;
	// Classifiers
	private static final String BOLD_FONT = "BOLD FONT";
	private static final String COLON_SEPARATION = "COLON_SEPARATION";
	private static final String FONT_CHANGE = "FONT CHANGE";

	public static final String ERROR_MESSAGE1 = "Not able to load config file";
	public static final String ERROR_MESSAGE2 ="Incorrect no. of pages!";
	public static final String ERROR_MESSAGE3 = "Unable to Process. Please contact administrator!";
	public static final String ERROR_MESSAGE4 = "Please check file name!";
	public static final String SUCCESS_MESSAGE = "XML File is generated!";
		
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
