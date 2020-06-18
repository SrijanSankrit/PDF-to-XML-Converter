package com.PDFtoXMLConverter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Catch-all class for documents that are not classified as invoice
 * @author BHAVYA SHARMA
 * 
 */

public class NonStandardInvoiceDocument extends BusinessDoc {
	
	private List<HashMap<String, String>> preambles = null;
	private HashMap<String, String> summary = null;
	private List<NonStandardTable> nsTables = null;
	private int tableStartLine;
	private int tableEndLine;

	/**
	 * Constructor
	 */
	public NonStandardInvoiceDocument() {
		super();
		this.setPreambles(null);
		this.setSummary(null);
		this.setNsTables(null);
		this.setTableStartLine(-1);
		this.setTableEndLine(-1);
	}

	@Override
	public void extract(int pageNo) throws IOException {
		// check if table exists in page
		this.setTableStartLine(NonStandardTable.getStartLine(this.getDocRectangles(), this.getDocLines()));
		this.setTableEndLine(NonStandardTable.getEndLine());
		BusinessWordExtractor wordExtractor = new BusinessWordExtractor();
		myPDFTextStripper stripper = new myPDFTextStripper();

		if (this.getTableStartLine() != -1) { // tables exists
			int i = 0;
			int prevTableEndLine = 0;
			int endRec = 0;

			while (this.getTableStartLine() != -1) {
				// table exists

				// extract the table
				this.extractTable(pageNo);

				// Preamble

				List<PDFLine> preambleLines = new ArrayList<PDFLine>();
				List<Box> preambleBoxes = null;

				for (int k = prevTableEndLine; k < this
						.getTableStartLine(); k++) /*
													 * collecting lines from end of previous table to start line of
													 * current table
													 */
				{
					preambleLines.add(this.getDocLines().get(k));
				}

				preambleBoxes = stripper.createBoxes(preambleLines); // creating boxes out of said lines
				wordExtractor.extract(preambleBoxes,stripper.classifyStyle(preambleBoxes));
				HashMap<String, String> preamble = wordExtractor.getKeyAndValuePairs() ;

				if (this.getPreambles() == null) {
					this.setPreambles(new ArrayList<HashMap<String, String>>());
				}

				this.getPreambles().add(preamble);

				prevTableEndLine = this.getTableEndLine(); // set end line of table

				// check if another table present by using the page rectangles after the last
				// rectangle of previous table
				List<MyPDRectangle> tableRectangles = new ArrayList<MyPDRectangle>();
				int size = this.getDocRectangles().size();
				endRec = endRec + NonStandardTable.getEndRec();

				// collect rectangles after last rectangle of previous table
				for (int j = endRec; j < size; j++) {
					tableRectangles.add(this.getDocRectangles().get(j));
				}

				// check for presence of another table
				this.setTableStartLine(NonStandardTable.getStartLine(tableRectangles, this.getDocLines()));
				this.setTableEndLine(NonStandardTable.getEndLine());
				i++;

			}

			// Summary
			int numLines = this.getDocLines().size();
			List<PDFLine> summaryLines = new ArrayList<PDFLine>();

			List<Box> summaryBoxes = null;
			if (this.getTableEndLine() != -1) {
				for (int x = prevTableEndLine; x < numLines; x++) {
					summaryLines.add(this.getDocLines().get(x));
				}

				summaryBoxes = stripper.createBoxes(summaryLines); // creating boxes out of said lines
				wordExtractor.extract(summaryBoxes, stripper.classifyStyle(summaryBoxes));
				this.setSummary(wordExtractor.getKeyAndValuePairs());
			}
		} else {

			this.setNsTables(null); // no table exists
			this.setTableEndLine(-1);
			// create boxes in the entire page
			if (this.getPreambles() == null) {
				this.setPreambles(new ArrayList<HashMap<String, String>>());
			}
			List<Box> preambleBoxes = stripper.createBoxes(this.getDocLines());

			wordExtractor.extract(preambleBoxes, stripper.classifyStyle(preambleBoxes));
			this.getPreambles().add(wordExtractor.getKeyAndValuePairs()); // store
																													// everything
																													// in
																													// preamble
			this.setSummary(null); // no summary exists
		}

	}

	/**
	 * Method that extracts table from given page
	 * @param pageNo Page number of page to be processed
	 * 
	 */
	public void extractTable(int pageNo) {
		List<PDFLine> pageLines = this.getDocLines();
		List<MyPDRectangle> pageRectangles = this.getDocRectangles();

		List<PDFLine> tableLines = new ArrayList<PDFLine>();

		int numLines = pageLines.size();

		// collecting lines relevant to the table
		for (int i = this.getTableStartLine(); i < this.getTableEndLine(); i++) {
			tableLines.add(pageLines.get(i));
		}

		// using non standard table's extract function to build the table

		if (this.getNsTables() == null) {
			this.setNsTables(new ArrayList<NonStandardTable>());
		}

		NonStandardTable nsTable = new NonStandardTable(tableLines, pageRectangles);
		nsTable.setStartLine(this.getTableStartLine());
		nsTable.extract(tableLines);
		// add table to list of tables on this page
		this.getNsTables().add(nsTable);
	}

	@Override
	public void createXML(XMLCreator xmlCreator, ArrayList<Integer> pages, int pageNo) throws Exception {
		// adding data to XML file
		if (this.getPreambles() != null) {
			for (HashMap<String, String> element : this.getPreambles()) {
				xmlCreator.addData(element, pages, pageNo);
			}
		}
		if (this.getNsTables() != null) {
			for (NonStandardTable element : this.getNsTables()) {
				xmlCreator.addTable(element.getData(), pages, pageNo);
			}
		}
		if (this.getSummary() != null) {
			xmlCreator.addData(this.getSummary(), pages, pageNo);
		}
	}
	
	//Getters and Setters

	public List<HashMap<String, String>> getPreambles() {
		return this.preambles;
	}

	public void setPreambles(List<HashMap<String, String>> preambles) {
		this.preambles = preambles;
	}

	public HashMap<String, String> getSummary() {
		return this.summary;
	}

	public void setSummary(HashMap<String, String> summary) {
		this.summary = summary;
	}

	public List<NonStandardTable> getNsTables() {
		return this.nsTables;
	}

	public void setNsTables(List<NonStandardTable> nsTables) {
		this.nsTables = nsTables;
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

}
