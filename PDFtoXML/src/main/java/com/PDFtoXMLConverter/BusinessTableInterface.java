package com.PDFtoXMLConverter;

/**
 * Interface for document specific requirements such as checking end of table,
 * merging rows - methods that will change with doc type
 * @author BHAVYA SHARMA
 * 
 */


public interface BusinessTableInterface {

	/**
	 * Checks whether given line is the line where table ends
	 * @param lineNo line number of line to be checked
	 * @param line line to be checked 
	 * 
	 */
	public boolean isEOT(int lineNo, PDFLine line);

	/**
	 * Checks if given row needs to be merged with previous. If yes, merges row with previous
	 * @param i line number of row to be checked
	 * @return true/false 
	 * 
	 */
	
	public void mergeRows(int i);

	/**
	 * Checks if given row needs to be merged with previous. If yes, merges row with previous
	 * @param line1 line number of current row
	 * @param line2 line number of previous row 
	 * 
	 */
	public void mergeRows(int rowNum, PDFLine line1, PDFLine line2);
}
