package com.PDFtoXMLConverter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Class that represents a table in a non standard invoice document
 * @author BHAVYA SHARMA
 * 
 */

public class NonStandardTable extends Table implements BusinessTableInterface {

	// represents tables present in a non standard document
	private int startLine;
	private static int endLine = -1;
	private static int endRec = -1;

	/**
	 * Constructor
	 * @param lines Lines in PDF relevant to table
	 * @param rectangles Rectangles in PDF relevant to table
	 * 
	 */
	
	public NonStandardTable(List<PDFLine> lines, List<MyPDRectangle> rectangles) {
		super(lines, rectangles);
	}

	/**
	 * Static method to get the starting line of the table 
	 * @param lines Lines in PDF 
	 * @param rectangles Rectangles in PDF 
	 * 
	 */
	public static int getStartLine(List<MyPDRectangle> rectangles, List<PDFLine> lines) {
		
		Collections.sort(rectangles);
		int numRectangles = rectangles.size();
		float tableStartPos;
		int i = 0;
		int count = 1;

		int numRows = 0;
		int startRect = -1;
		float prevStartY = -1;
		float prevEndY = -1;
		int numTables = 0;
		// find rectangle that starts and end the table, along with starting and ending
		// positions of the table
		for (i = 0; i < numRectangles; i++) { // loop through all the rectangles
			MyPDRectangle rectangle = rectangles.get(i);
			float xl = rectangle.getLowerLeftX();
			float xr = rectangle.getUpperRightX();
			float yl = rectangle.getLowerLeftY();
			float yr = rectangle.getUpperRightY();
			if (i != 0) {
				MyPDRectangle prevRectangle = rectangles.get(i - 1);
				// check if consecutive rectangles with same startY and endY
				if (yl == prevRectangle.getLowerLeftY() && yr == prevRectangle.getUpperRightY()) {
					count++;
				} else {
					count = 1;
				}

				if (count > 2) // if more than two such consecutive rectangles found, consider this to be a row
				{
					if (prevStartY == -1 && prevEndY == -1) // if previous row's start and endY not set, start of a new
															// Table
					{
						prevStartY = yr;
						prevEndY = yl;
						startRect = i;
					} else // checking if the new consecutive cells are a part of the existing table
					{
						if (yr < prevEndY) // new consecutive cells overlap with previous ones
						{
							prevEndY = Math.max(prevEndY, yl); // Thus, expand the cell
						} else if (yr - prevEndY < MyConstants.maxGapBetweenTwoRows) // new consecutive cells are close
																						// to previous ones and hence a
																						// row in the table
						{
							numTables = 1;
							numRows++;
							prevStartY = yr;
							prevEndY = yl;
						} else // gap is too much, can't be considered as a row
						{
							if (numTables > 0) // if a table is already found, break
							{
								setEndRec(i); // set last rectangle of table
								break;
							} else // resume search
							{
								startRect = -1;
								prevStartY = -1;
								prevEndY = -1;
							}
						}
					}
				}
			}
		}

		if (i == numRectangles && numTables > 0) {
			setEndRec(i);
		}
		if (numRows >= 1) {
			MyPDRectangle cell = rectangles.get(startRect);
			tableStartPos = cell.getUpperRightY();
			setColHeadingEndPos(cell.getLowerLeftY());

			int numLines = lines.size();
			int j = 0;

			// search for the line number that ends the table by comparing end position of
			// line and end position of table
			while (j < numLines && lines.get(j).getEndY() <= prevEndY) {
				j++;
			}

			setEndLine(j);

			for (j = 0; j < numLines; j++) {
				PDFLine line = lines.get(j);
				// search for the line number that starts the table by comparing start position
				// of line and start position of table
				if (line.getStartY() > tableStartPos) {
					return j;
				}
			}
		} else {
			setColHeadingEndPos(0);
			return -1;
		}
		return -1;
	}

	/**
	 * Method that extracts the table data
	 * @param lines List of lines relevant to table
	 */
	public void extract(List<PDFLine> lines) {
		this.build(this, lines);
	}

	@Override
	public boolean isEOT(int lineNo, PDFLine line) // function to check if table ended
	{

		if (this.getStartLine() + lineNo == getEndLine()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void mergeRows(int rowNum) {

		return;
	}

	@Override
	public void mergeRows(int rowNum, PDFLine line1, PDFLine line2) // merges rows that belong together
	{
		// if lines are two close together, merge current row with previous one
		if (line1.getStartY() - line2.getEndY() < MyConstants.maxGapBetweenTwoRows) {
			HashMap<String, String> prevRow = this.getData().get(rowNum - 2);
			HashMap<String, String> currRow = this.getData().get(rowNum - 1);

			Set<String> keySet = prevRow.keySet();

			Iterator<String> iterator = keySet.iterator();

			while (iterator.hasNext()) {

				String key = iterator.next();
				String prevVal = prevRow.get(key);
				if (currRow.containsKey(key)) {
					String currVal = null;
					currVal = currRow.get(key);
					prevVal = prevVal + currVal;
					prevRow.put(key, prevVal);
				}
			}

			this.getData().remove(rowNum - 1);
			this.setNumRows(this.getNumRows() - 1);
		}
		return;
	}

	
	//Getters and Setters
	
	public int getStartLine() {
		return startLine;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public static int getEndLine() {
		return endLine;
	}

	public static void setEndLine(int endLine) {
		NonStandardTable.endLine = endLine;
	}

	public static int getEndRec() {
		return endRec;
	}

	public static void setEndRec(int endRec) {
		NonStandardTable.endRec = endRec;
	}

}
