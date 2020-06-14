package com.PDFtoXMLConverter;

import java.util.*;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

//represents an invoice document, which is a type of the general business document

public class InvoiceDocument extends BusinessDoc {

	public InvoiceItemsTable itemsTable;
	public HashMap<String, String> preamble;
	public HashMap<String, String> summary;
	public int tableStartLine;
	public int tableEndLine;
	public int startPage;
	// checking whether given document is of type invoice

	public static BusinessDoc checkNcreate(PDPage doc, int pageNos) throws IOException {

		List<PDFLine> pageLines = null;
		List<MyPDRectangle> pageRectangles = null;

		// collecting lines and rectangles on each page
		// int size = doc.getNumberOfPages();
		PDPage pageObj;

		myPDFTextStripper stripper = new myPDFTextStripper();
		MyStreamEngine engine = new MyStreamEngine();
		pageObj = doc;
		engine.processPage(doc);
		pageRectangles = engine.cells;
		PDDocument temp = new PDDocument();
		temp.addPage(doc);
		stripper.getText(temp);
		temp.close();
		pageLines = stripper.createLines();
		int num = pageLines.size();

		for (int j = 0; j < num; j++) {
			pageLines.get(j).pageNo = 0;
		}

		int lineNumber;
		try {
			lineNumber = InvoiceItemsTable.getStartLine(pageLines);

		} catch (Exception e) {

			lineNumber = -1;
		}

		// if the table column headings exists, checking if rows exist -
		if (lineNumber >= 0) {
			// Creating an instance of the invoice document
			InvoiceDocument invDocObj = new InvoiceDocument();

			// Setting all values for this instance
			invDocObj.docObj = doc;
			invDocObj.docLines = pageLines;
			invDocObj.docRectangles = pageRectangles;
			invDocObj.tableStartLine = lineNumber;
			invDocObj.startPage = pageNos;

			// extracting the table to check the number of rows that the table has

			invDocObj.extractTable(1);

			// if the table contains one row or more, the document is an invoice
			if (invDocObj.itemsTable.numRows > 0) {

				return invDocObj;
			}
			// no row exists, hence document is not an invoice
			else {
				// Creating an instance of the non standard invoice document

				NonStandardInvoiceDocument docObj = new NonStandardInvoiceDocument();

				// Setting all values for this instance
				docObj.docObj = doc;
				docObj.docLines = pageLines;
				docObj.docRectangles = pageRectangles;

				return docObj;

			}

		} else // table column headings don't exist, hence no table exists. Not an invoice
		{
			// Creating an instance of the non standard invoice document

			NonStandardInvoiceDocument docObj = new NonStandardInvoiceDocument();

			// Setting all values for this instance
			docObj.docObj = doc;
			docObj.docLines = pageLines;
			docObj.docRectangles = pageRectangles;

			return docObj;
		}
	}

	public void extract(int pageNo) throws IOException {
		// extracting the table
		if (pageNo != startPage) // Table is already extracted for the first page
		{
			// checking if table exists
			int lineNumber = InvoiceItemsTable.getStartLine(docLines);
			tableStartLine = lineNumber;

			if (tableStartLine >= 0) // extract table if start line found
				extractTable(pageNo);

			else
				itemsTable = null; // no items table exists on this page
		}

		// extracting business words
		BusinessWordExtractor wordExtractor = new BusinessWordExtractor();
		myPDFTextStripper stripper = new myPDFTextStripper();

		if (tableStartLine >= 0) // if a table exists on this page, divide the page into preamble, table and
									// summary
		{
			// Preamble
			List<PDFLine> preambleLines = new ArrayList<PDFLine>();
			List<Box> preambleBoxes = null;

			for (int i = 0; i < this.tableStartLine; i++) // collecting lines from start of page to table start line
			{
				preambleLines.add(this.docLines.get(i));
			}
			preambleBoxes = stripper.createBoxes(preambleLines); // creating boxes out of said lines
			wordExtractor.extract(preambleBoxes, stripper.classifyStyle(preambleBoxes));
			preamble = wordExtractor.getKeyAndValuePairs();

			// Summary
			int numLines = docLines.size();
			List<PDFLine> summaryLines = new ArrayList<PDFLine>();
			List<Box> summaryBoxes = null;
			if (tableEndLine != -1) {
				for (int i = this.tableEndLine; i < numLines; i++) // collecting lines from table end line to end of
																	// page
					summaryLines.add(this.docLines.get(i));

				summaryBoxes = stripper.createBoxes(summaryLines); // creating boxes out of said lines
				wordExtractor.extract(summaryBoxes, stripper.classifyStyle(summaryBoxes));
				summary = wordExtractor.getKeyAndValuePairs();
			}

		} else {
			// create boxes in the entire page
			List<Box> pageBoxes = stripper.createBoxes(docLines);
			wordExtractor.extract(pageBoxes, stripper.classifyStyle(pageBoxes));
			preamble = wordExtractor.getKeyAndValuePairs(); // store everything in
																							// preamble
			summary = null; // no summary exists
		}

	}

	public void extractTable(int pageNo) {
		// collecting lines relevant to the table (lines from column headings onwards on
		// the first page)
		List<PDFLine> tableLines = new ArrayList<PDFLine>();
		List<PDFLine> pageLines = docLines;

		int size = docLines.size();
		for (int i = tableStartLine; i < size; i++)
			tableLines.add(pageLines.get(i));
		// using invoice items table's extract function to build the table

		itemsTable = new InvoiceItemsTable(tableLines, docRectangles);
		itemsTable.startLine = tableStartLine;
		itemsTable.extract(tableLines);
		tableEndLine = itemsTable.endLine;

	}

	public void createXML(XMLCreator xmlCreator, ArrayList<Integer> pages, int pageNo) throws Exception {
		// adding data to XML file
		if (preamble != null) {

			xmlCreator.addData(preamble, pages, pageNo);

		}
		if (itemsTable != null) {

			xmlCreator.addTable(itemsTable.data, pages, pageNo);
		}
		if (summary != null) {

			xmlCreator.addData(summary, pages, pageNo);
		}
	}

}
