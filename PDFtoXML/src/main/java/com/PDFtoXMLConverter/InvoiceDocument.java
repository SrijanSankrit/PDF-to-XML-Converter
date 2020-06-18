package com.PDFtoXMLConverter;


import java.util.*;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

/**
 * 
 * Class that represents an invoice page
 * 
 * @author BHAVYA SHARMA
 * 
 */

public class InvoiceDocument extends BusinessDoc {

	private InvoiceItemsTable itemsTable; // Items Table in invoice doc
	private HashMap<String, String> preamble; // Data in part above the items table
	private HashMap<String, String> summary; // Data in part below the items table
	private int tableStartLine; // Line number of start line of table
	private int tableEndLine; // Line number of end line of table
	private int startPage; // Page Number of first page to be extracted

	/**
	 * Method that checks whether given page is an invoice, by checking for presence
	 * of an invoice table
	 * 
	 * @param doc    Page to be processed
	 * @param pageNo Page number of said page
	 * @return Instance of invoice or non standard invoice document, depending on
	 *         what the doc is classified as
	 * 
	 */
	public static BusinessDoc checkNcreate(PDPage doc, int pageNo) throws Exception{
		
		Logger logger = CacheManager.getLogger();
		
		// collecting lines on given page
		try {
			List<PDFLine> pageLines = null;
			myPDFTextStripper stripper = new myPDFTextStripper();
			PDDocument temp = new PDDocument();
			temp.addPage(doc);
			stripper.getText(temp);
			temp.close();
			pageLines = stripper.createLines();
			int numLines = pageLines.size();
			for (int lineIndex  = 0; lineIndex < numLines; lineIndex++) {
				pageLines.get(lineIndex).setPageNo(pageNo - 1);
			}

			// collecting rectangles on given page

			List<MyPDRectangle> pageRectangles = null;
			MyStreamEngine engine = new MyStreamEngine();
			engine.processPage(doc);
			pageRectangles = engine.getCells();

			// checking for start line of items table

			int lineNumber;
			lineNumber = InvoiceItemsTable.getStartLine(pageLines);

			// if the table column headings exists, i.e start line is not -1, check if rows
			// exist by extracting the table -
			if (lineNumber >= 0) 
			{
				logger.debug("Invoice Document !");
				logger.debug("Table Found!");
				// Creating an instance of the invoice document
				InvoiceDocument invDocObj = new InvoiceDocument();

				// Setting all values for this instance
				invDocObj.setDocObj(doc);
				invDocObj.setDocLines(pageLines);
				invDocObj.setDocRectangles(pageRectangles);
				invDocObj.setTableStartLine(lineNumber);
				invDocObj.setStartPage(pageNo);

				// extracting the table to check the number of rows that the table has

				invDocObj.extractTable(pageNo);

				// if the table contains one row or more, the document is an invoice, return its
				// instance
				if (invDocObj.getItemsTable().getNumRows() > 0) {

					return invDocObj;
				}

				// no row exists, hence document is not an invoice
				else {
					// Creating an instance of the non standard invoice document
					logger.debug("Non Standard Invoice Document !");
					NonStandardInvoiceDocument docObj = new NonStandardInvoiceDocument();

					// Setting all values for this instance
					docObj.setDocObj(doc);
					docObj.setDocLines(pageLines);
					docObj.setDocRectangles(pageRectangles);

					return docObj;

				}

			} 
			else // table column headings don't exist, hence no table exists. Not an invoice
			{
				// Creating an instance of the non standard invoice document
				logger.debug("Non Standard Invoice Document !");
				NonStandardInvoiceDocument docObj = new NonStandardInvoiceDocument();

				// Setting all values for this instance
				docObj.setDocObj(doc);
				docObj.setDocLines(pageLines);
				docObj.setDocRectangles(pageRectangles);

				return docObj;
			}
		}
		catch (Exception e){

			throw e;
		}

	}

	@Override

	public void extract(int pageNo) throws IOException {
		Logger logger = CacheManager.getLogger();
		// extracting the table
		if (pageNo != this.getStartPage()) // Table is already extracted for the first page
		{
			// checking if table exists
			int lineNumber = InvoiceItemsTable.getStartLine(this.getDocLines());
			this.setTableStartLine(lineNumber);

			if (this.getTableStartLine() >= 0) {
				this.extractTable(pageNo);
			} else {
				this.setItemsTable(null); // no items table exists on this page
			}
		}

		// extracting business words
		BusinessWordExtractor wordExtractor = new BusinessWordExtractor();
		myPDFTextStripper stripper = new myPDFTextStripper();

		if (this.getTableStartLine() >= 0) // if a table exists on this page, divide the page into preamble, table and
											// summary
		{
			logger.debug("Preamble Extracted!");
			// Preamble
			List<PDFLine> preambleLines = new ArrayList<PDFLine>();
			List<Box> preambleBoxes = null;

			for (int lineIndex = 0; lineIndex < this.getTableStartLine(); lineIndex++) // collecting lines from start of page to table start
																// line
			{
				preambleLines.add(this.getDocLines().get(lineIndex));
			}
			preambleBoxes = stripper.createBoxes(preambleLines); // creating boxes out of said lines
			wordExtractor.extract(preambleBoxes, stripper.classifyStyle(preambleBoxes));
			this.setPreamble(wordExtractor.getKeyAndValuePairs());

			// Summary
			int numLines = this.getDocLines().size();
			List<PDFLine> summaryLines = new ArrayList<PDFLine>();
			List<Box> summaryBoxes = null;
			
			if (this.getTableEndLine() != -1) {
				for (int lineIndex = this.getTableEndLine(); lineIndex < numLines; lineIndex++) {
					summaryLines.add(this.getDocLines().get(lineIndex));
				}

				summaryBoxes = stripper.createBoxes(summaryLines); // creating boxes out of said lines
				wordExtractor.extract(summaryBoxes, stripper.classifyStyle(summaryBoxes));
				this.setSummary(wordExtractor.getKeyAndValuePairs());
				logger.debug("Summary Extracted!");
			}

		} else {
			// create boxes in the entire page
			List<Box> pageBoxes = stripper.createBoxes(this.getDocLines());
			wordExtractor.extract(pageBoxes, stripper.classifyStyle(pageBoxes));
			this.setPreamble(wordExtractor.getKeyAndValuePairs()); // store everything
																									// in
																									// preamble
			this.setSummary(null); // no summary exists
			logger.info("Only Preamble Extracted!");
		}

	}
	
	/**
	 * Method that extracts table from given page
	 * @param pageNo Page number of page to be processed
	 * 
	 */

	public void extractTable(int pageNo) {
		// collecting lines relevant to the table (lines from column headings onwards on
		// the first page)
		List<PDFLine> tableLines = new ArrayList<PDFLine>();
		List<PDFLine> pageLines = this.getDocLines();

		int size = this.getDocLines().size();
		for (int lineIndex = this.getTableStartLine(); lineIndex < size; lineIndex++) {
			tableLines.add(pageLines.get(lineIndex));
			// using invoice items table's extract function to build the table
		}

		this.setItemsTable(new InvoiceItemsTable(tableLines, this.getDocRectangles()));
		this.getItemsTable().setStartLine(this.getTableStartLine());
		this.getItemsTable().extract(tableLines);
		this.setTableEndLine(this.getItemsTable().getEndLine());

	}

	@Override
	public void createXML(XMLCreator xmlCreator, ArrayList<Integer> pages, int pageNo) throws Exception {
		// adding data to XML file
		Logger logger = CacheManager.getLogger();
		if (preamble != null) {

			xmlCreator.addData(preamble, pages, pageNo);
			logger.debug("Preamble is getting added to the XML");

		}
		if (itemsTable != null) {

			xmlCreator.addTable(itemsTable.getData(), pages, pageNo);
			logger.debug("Table is getting added to the XML");
		}
		if (summary != null) {

			xmlCreator.addData(summary, pages, pageNo);
			logger.debug("Summary is getting added to the XML");
		}
	}

	
	//Getters and Setters
	
	public InvoiceItemsTable getItemsTable() {
		return this.itemsTable;
	}

	public void setItemsTable(InvoiceItemsTable itemsTable) {
		this.itemsTable = itemsTable;
	}

	public HashMap<String, String> getPreamble() {
		return this.preamble;
	}

	public void setPreamble(HashMap<String, String> preamble) {
		this.preamble = preamble;
	}

	public HashMap<String, String> getSummary() {
		return this.summary;
	}

	public void setSummary(HashMap<String, String> summary) {
		this.summary = summary;
	}

	public int getTableStartLine() {
		return this.tableStartLine;
	}

	public void setTableStartLine(int tableStartLine) {
		this.tableStartLine = tableStartLine;
	}

	public int getTableEndLine() {
		return this.tableEndLine;
	}

	public void setTableEndLine(int tableEndLine) {
		this.tableEndLine = tableEndLine;
	}

	public int getStartPage() {
		return this.startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

}
