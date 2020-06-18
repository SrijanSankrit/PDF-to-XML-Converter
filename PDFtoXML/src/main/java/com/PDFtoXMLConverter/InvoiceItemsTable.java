package com.PDFtoXMLConverter;

import java.util.*;


/**
 * Class represents special case of table - items table in an invoice doc
 * @author BHAVYA SHARMA
 * 
 */
public class InvoiceItemsTable extends Table implements BusinessTableInterface {

	private int startLine;						//start line of items table
	private int endLine;						//end line of items table


	/**
	 * Constructor 
	 * @param lines List of lines relevant to table
	 * @param rectangles List of rectangles relevant to table
	 * 
	 */
	public InvoiceItemsTable(List<PDFLine> lines, List<MyPDRectangle> rectangles) {
		super(lines, rectangles);
		this.setStartLine(-1);
		this.setEndLine(-1);
	}
	

	/**
	 * Static method that gives start line of the table. 
	 * @param lines List of lines in PDF
	 * @return Line number of start line. -1 if no table present
	 */
	public static int getStartLine(List<PDFLine> lines) 
	{
		int numLines = lines.size();
		String invoiceHeadings[] = CacheManager.getInvoiceHeadings();
		
		for (int lineIndex = 0; lineIndex < numLines; lineIndex++) {
			List<StringBlock> blocks = lines.get(lineIndex).getLineBlocks();
			int numBlocks = blocks.size();

			for (int blockIndex = 0; blockIndex < numBlocks; blockIndex++) {
	
				String blockText = blocks.get(blockIndex).getText();
				blockText = blockText.toUpperCase(); // if we find the following terms, table starts there
				
				for ( int headingIndex = 0 ; headingIndex < invoiceHeadings.length ; headingIndex++)
				{	
/*					if (s.contains("SNO.") || s.contains("ITEM") || s.contains("DESCRIPTION") || s.contains("DESC.")
							|| s.contains("QUANTITY") || s.contains("QTY") || s.contains("AMOUNT") || s.contains("AMT.")
							|| s.contains("SL.")) {*/
					if ( blockText.contains(invoiceHeadings[headingIndex]))
						return lineIndex;
					
				}
			}
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
	public boolean isEOT(int lineNo, PDFLine line) // function to check if table ended - specific to items table
	{

		if (super.isEOT(lineNo, line) == false) {
			int numBlocks = line.getLineBlocks().size();

			for (int blockIndex = 0; blockIndex < numBlocks; blockIndex++) {
				StringBlock block = line.getLineBlocks().get(blockIndex);
				String textLower = block.getText();
				String text = textLower.toUpperCase();
				if (text.contains("TOTAL") || text.contains("SUB") // table ends if these words found in given line
						|| text.contains("TAX") || text.contains("DISCOUNT")) {
					this.setEndLine(this.getStartLine() + lineNo);
					return true;
				}
			}
			return false;
		} else {
			return true;
		}

	}

	@Override
	public void mergeRows(int rowNum) 
	{
		// merges rows that belong together

		HashMap<String, String> prevRow = this.getData().get(rowNum - 2);
		HashMap<String, String> currRow = this.getData().get(rowNum - 1);
		ArrayList<Integer> missingCols = new ArrayList<Integer>();

		if (prevRow.size() != currRow.size()) // if the current row has missing values corresponding to the columns
		{
			for (int colIndex = 0; colIndex < this.getNumCols(); colIndex++) {
				Column col = this.getColHeadings().get(colIndex);
				String heading = col.getText();

				if (currRow.containsKey(heading) == false) {
					missingCols.add(col.getColNo()); // collect the col numbers corresponding to which values are missing
				}
			}

		}

		int numMissingCols = missingCols.size();
		int flag = 0;
		for (int colIndex = 0; colIndex < numMissingCols; colIndex++) {
			Column col = this.getColHeadings().get(missingCols.get(colIndex));
			String heading = col.getText();
			heading.toUpperCase(); // check if the amount column is missing value
			if (heading.contains("AMOUNT") || heading.contains("AMT")) {
				flag = 1;
			}
		}

		if (flag == 1) // if the amount column is missing value, this row must be merged with the
						// previous one
		{
			Set<String> keySet = prevRow.keySet();
			Iterator<String> iterator = keySet.iterator();

			while (iterator.hasNext()) {
				String key = iterator.next();
				String prevVal = prevRow.get(key);

				if (currRow.containsKey(key)) {
					String currVal = null;
					currVal = currRow.get(key);
					prevVal = prevVal + currVal;
					prevRow.remove(key);
					prevRow.put(key, prevVal);
				}
			}
			this.getData().remove(rowNum - 1);
			this.setNumRows(this.getNumRows() - 1);
		}
		return;
	}

	@Override
	public void mergeRows(int rowNum, PDFLine line1, PDFLine line2) {

		return;
	}

	
	//Getters and Setters
	
	int getStartLine() {
		return this.startLine;
	}

	void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	int getEndLine() {
		return this.endLine;
	}

	void setEndLine(int endLine) {
		this.endLine = endLine;
	}

}
