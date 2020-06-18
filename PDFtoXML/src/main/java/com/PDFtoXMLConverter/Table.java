package com.PDFtoXMLConverter;
import java.util.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.TextPosition;

/**
 * Class that represents a generic table in any document that an organization handles
 * @author BHAVYA SHARMA
 */
public class Table {

	private int numRows;							//Number of rows in the table
	private int numCols;							//Number of columns in the table
	private int numHeadingLines ;					//Number of heading lines in the table
	private static float colHeadingEndPos = 0;		//Ending Y position of column heading	
	private List<Column> colHeadings;				//List of column heading in the table
	private List<HashMap<String, String>> data;		//Data that the rows of the table contain

	/**
	 * Constructor 
	 * @param lines List of lines relevant to table
	 * @param rectangles List of rectangles relevant to table
	 * 
	 */
	public Table(List<PDFLine> lines, List<MyPDRectangle> rectangles) {
		this.init();
		this.createColHeadings(lines, rectangles);
		
	}

	/**
	 * Initialization function
	 * 
	 */
	public void init() { 
		this.setColHeadings(new ArrayList<Column>());
		this.setData(new ArrayList<HashMap<String, String>>());
		this.setNumCols(0);
		this.setNumRows(0);
		this.setNumHeadingLines(1);
	}

	/**
	 * Method that extracts the column headings in the table 
	 * @param lines List of lines relevant to table
	 * @param rectangles List of rectangles relevant to table
	 * 
	 */
	
	public void createColHeadings(List<PDFLine> lines, List<MyPDRectangle> rectangles) {

		PDFLine headingLine = lines.get(0);

		int numBlocks = headingLine.getLineBlocks().size();
		for (int blockIndex = 0; blockIndex < numBlocks; blockIndex++) {
			StringBlock block = headingLine.getLineBlocks().get(blockIndex);
			if (blockIndex > 0) {
				Column temp = this.getColHeadings().get(this.getNumCols() - 1); // if the block overlaps with the previous heading
																		// on the X axis, it belongs to the same column
				if (block.getStartX() >= temp.getStartX() && block.getStartX() < temp.getEndX()) {
					temp.setText(temp.getText() + " " + block.getText());
					temp.setEndX(Math.max(temp.getEndX(), block.getEndX()));
					temp.setWidth(temp.getEndX() - temp.getStartX());
					continue;
				} else if (block.getEndX() > temp.getStartX() && block.getEndX() <= temp.getEndX()) {
					temp.setText(temp.getText() + " " + block.getText());
					temp.setStartX(Math.min(temp.getStartX(), block.getStartX()));
					temp.setWidth(temp.getEndX() - temp.getStartX());
					continue;
				}
			} // else create a new column heading

			Column col = new Column();
			this.setNumCols(this.getNumCols() + 1);
			col.setText(block.getText());
			col.setStartX(block.getStartX());
			col.setEndX(block.getEndX());
			col.setWidth(block.getEndX() - block.getStartX());
			col.setStartY(headingLine.getStartY());
			col.setEndY(headingLine.getEndY());
			col.setHeight(headingLine.getEndY() - headingLine.getStartY());
			col.setColNo(blockIndex);
			this.getColHeadings().add(col);
		}

		// reinforce column boundaries using rectangles. If an extremely thin rectangle
		// is built, it must act as a boundary line
		// if such a boundary line exists before the first and after the last column,
		// change column widths accordingly

		int numRectangles = rectangles.size();

		for (int rectangleIndex = 0; rectangleIndex < numRectangles; rectangleIndex++) {
			PDRectangle cell = rectangles.get(rectangleIndex);

			float cellStartX = cell.getLowerLeftX();
			float cellEndX = cell.getUpperRightX();
			float cellWidth = cellEndX - cellStartX;
			Column col1 = this.getColHeadings().get(0);
			Column endCol = this.getColHeadings().get(this.getNumCols() - 1);

			// checking is rectangle is thin, it's X is before col 1's startX and it
			// intersects with col 1 on the Y axis
			if (cellWidth < 5 && cellStartX < col1.getStartX() && this.intersect(cell, col1)) {
				this.getColHeadings().get(0).setStartX(Math.min(cell.getLowerLeftX(), this.getColHeadings().get(0).getStartX()));
			}
			// checking is rectangle is thin, it's startX is after last col's endX and it
			// intersects with last col on the Y axis
			else if (cellWidth < 5 && cellStartX > endCol.getEndX() && this.intersect(cell, endCol)) {
				this.getColHeadings().get(this.getNumCols() - 1).setEndX(Math.max(cell.getUpperRightX(),
						this.getColHeadings().get(this.getNumCols() - 1).getEndX()));
			}

		}
		
		//If the extracted column headings end Y position is smaller than end Y position of column headings
		//of the table, it means there are more than one column heading lines
		
		if (this.getColHeadings().get(0).getEndY() < getColHeadingEndPos()) {
			int lineNum = 1;
			
			//find all the lines that come within the end Y position of column heading
			while (lineNum < numBlocks && lines.get(lineNum).getEndY() <= getColHeadingEndPos()) {

				PDFLine nextHeadingLine = lines.get(lineNum);
				this.setNumHeadingLines(this.getNumHeadingLines() + 1);
				numBlocks = nextHeadingLine.getLineBlocks().size();
				
				//add the text of these lines to the appropriate column it overlaps with
				for (int blockIndex = 0; blockIndex < numBlocks; blockIndex++) {
					StringBlock block = nextHeadingLine.getLineBlocks().get(blockIndex);
					for (int colIndex = 0; colIndex < this.getNumCols(); colIndex++) {
						Column col = this.getColHeadings().get(colIndex);
						if (this.intersect(block, col)) {
							String text = col.getText() + " " + block.getText();
							this.getColHeadings().get(colIndex).setText(text);
							break;
						}
					}
				}
				lineNum++;

			}
		}
	}

	/**
	 * Method that extracts all the rows in a table
	 * @param busTable interface that is specific to type of document
	 * @param lines List of lines relevant to the table 
	 */
	
	public void build(BusinessTableInterface busTable, List<PDFLine> lines) {
		PDFLine line;
		int numLines = lines.size();

		for (int lineIndex = this.getNumHeadingLines(); lineIndex < numLines; lineIndex++) {
			line = lines.get(lineIndex);
			//check if the line is the last line of the table
			if (busTable.isEOT(lineIndex, line) == true) {
				return;
			}
			HashMap<String, String> row = new HashMap<String, String>();
			int numBlocks = line.getLineBlocks().size();

			for (int blockIndex = 0; blockIndex < numBlocks; blockIndex++) {
				StringBlock block = line.getLineBlocks().get(blockIndex);

				// check which columns the block intersects with
				for (int colIndex = 0; colIndex < this.getNumCols(); colIndex++) {
					Column col = this.getColHeadings().get(colIndex);
					if (this.intersect(block, col)) {

						if ((colIndex < this.getNumCols() - 1 && block.getEndX() < this.getColHeadings().get(colIndex + 1).getStartX())
								|| colIndex == this.getNumCols() - 1) // Block fits perfectly in the column
						{
							if (row.containsKey(col.getText())) // Row already contains a block under the same column
							{
								String text = row.get(col.getText()) + block.getText();
								row.remove(col.getText());
								this.adjustColWidth(block, col);
								row.put(col.getText(), text);
							} else // Row's first block under this column
							{
								this.adjustColWidth(block, col);
								row.put(col.getText(), block.getText());
							}

						} else if (colIndex < this.getNumCols() - 1 && block.getEndX() > this.getColHeadings().get(colIndex + 1).getStartX()) // Block
						// needs to be split into columns
						{
							ArrayList<String> colTexts = this.splitBlock(block, colIndex);
							int colTextsSize = colTexts.size();
							for (int colTextsIndex = 0; colTextsIndex < colTextsSize; colTextsIndex++) {
								row.put(this.getColHeadings().get(colIndex).getText(), colTexts.get(colTextsIndex));
								colIndex++;
							}
						}
					}
				}
			}
			this.getData().add(row);
			this.setNumRows(this.getNumRows() + 1);

			if (this.getNumRows() > 1) {
				busTable.mergeRows(this.getNumRows());
				busTable.mergeRows(this.getNumRows(), line, lines.get(lineIndex - 1));
			}

		}
	}
	
	/**
	 * Method that splits a block into columns based on X positions 
	 * @param block Block to be split
	 * @param col First column that block overlaps with 
	 * @return names of column headings in which block is spread 	 * 
	 */

	public ArrayList<String> splitBlock(StringBlock block, int startColNum) { 
		List<TextPosition> textPos = block.getTextPositions();
		List<Float> startXList = new ArrayList<Float>();
		int numTextPos = textPos.size();
		for (int textPosIndex = 0; textPosIndex < numTextPos; textPosIndex++) {
			startXList.add(textPos.get(textPosIndex).getX());
		}

		ArrayList<String> colTexts = new ArrayList<String>();
		float newStartPos = block.getStartX();
		int count = 0;
		int endPosIndex = 0;
		int startPosIndex;

		for (int colIndex = startColNum; colIndex < this.getNumCols(); colIndex++) {
			Column col = this.getColHeadings().get(colIndex);
			if (this.intersect(block, col)) {
				String s = "";
				startPosIndex = startXList.indexOf(newStartPos);

				if (block.getEndX() > col.getEndX()) {
					count = 0;
					while (count < numTextPos && startXList.get(count) < col.getEndX()) {
						count++;
					}
					count--;
					endPosIndex = count;
				} else {
					endPosIndex = numTextPos - 1;
				}
				int textPosIndex = 0;
				for (textPosIndex = startPosIndex; textPosIndex <= endPosIndex; textPosIndex++) {
					s = s + textPos.get(textPosIndex).toString();
				}
				colTexts.add(s);

				count++;
				newStartPos = startXList.get(count);
			}
		}
		return colTexts;
	}

	/**
	 * Method that checks if table ends on given row
	 * 
	 */
	public boolean isEOT(int lineNo, PDFLine line) {
		// check for end of table that is common for all tables
		return false;
	}

	public boolean intersect(StringBlock block, Column col) { // checks if block and column interesect based on X
																// positions
		float l1 = col.getStartX();
		float r1 = col.getEndX();

		float l2 = block.getStartX();
		float r2 = block.getEndX();

		return ((l1 >= l2 && r1 <= r2) || (l1 >= l2 && l1 <= r2 && r1 > r2) || (l1 < l2 && r1 >= l2 && r1 <= r2)
				|| (l2 >= l1 && r2 <= r1));
	}

	/**
	 * Method that checks if rectangles and column intersect based on Y positions
	 * @param cell Rectangle to be checked
	 * @param col Column to be checked
	 * @return true/false
	 */
	public boolean intersect(PDRectangle cell, Column col) { 
		float u1 = cell.getUpperRightY();
		float d1 = cell.getLowerLeftY();

		float u2 = cell.getUpperRightY();
		float d2 = cell.getLowerLeftY();

		return ((u1 >= u2 && d1 <= d2) || (u1 >= u2 && u1 <= d2 && d1 > d2) || (u1 < u2 && d1 >= d2 && d1 <= d2)
				|| (u2 >= u1 && d2 <= d1));

	}
	
	/**
	 * Method that adjusts the width of the column based on block's dimensions
	 * @param block Block to be accounted for
	 * @param col Column whose width has to be adjusted
	 * 
	 */

	public void adjustColWidth(StringBlock block, Column col) {
		float l1 = col.getStartX();
		float r1 = col.getEndX();

		float l2 = block.getStartX();
		float r2 = block.getEndX();

		if (l2 < l1) {
			col.setStartX(l2);
		}
		if (r2 > r1) {
			col.setEndX(r2);
		}
	}

	//Getters and Setters
	
	public int getNumRows() {
		return numRows;
	}

	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}

	public int getNumCols() {
		return numCols;
	}

	public void setNumCols(int numCols) {
		this.numCols = numCols;
	}

	public int getNumHeadingLines() {
		return numHeadingLines;
	}

	public void setNumHeadingLines(int numHeadingLines) {
		this.numHeadingLines = numHeadingLines;
	}

	public static float getColHeadingEndPos() {
		return colHeadingEndPos;
	}

	public static void setColHeadingEndPos(float colHeadingEndPos) {
		Table.colHeadingEndPos = colHeadingEndPos;
	}

	public List<Column> getColHeadings() {
		return colHeadings;
	}

	public void setColHeadings(List<Column> colHeadings) {
		this.colHeadings = colHeadings;
	}

	public List<HashMap<String, String>> getData() {
		return data;
	}

	public void setData(List<HashMap<String, String>> data) {
		this.data = data;
	}

}
