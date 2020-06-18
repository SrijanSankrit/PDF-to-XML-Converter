package com.PDFtoXMLConverter;

import java.util.*;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.IOException;

/**
 * General class of documents that an organization can have
 * @author BHAVYA SHARMA
 *  
 */

public abstract class BusinessDoc {

	private PDPage docObj;				
	private List<PDFLine> docLines;								//Lines in the page
	private List<Box> docBoxes;									//Boxes in the page
	private List<MyPDRectangle> docRectangles;					//Rectangles in the page

	
	/**
	 * Function to create XML file of the document
	 * @param xmlCreator Instance of the XMLcreator class
	 * @param pages ArrayList of pages user gave as input
	 * @param pageNo Number of page whose data is to be entered in XML
	 */
	public void createXML(XMLCreator xmlCreator, ArrayList<Integer> pages, int pageNo) throws Exception {

	}

	
	
	/**
	 * Function to extract data in given page
	 * @param pageNo Page to be extracted
	 */
	public void extract(int pageNo) throws IOException {

	}

	//Getters and setters
	
	public PDPage getDocObj() {
		return docObj;
	}

	public void setDocObj(PDPage docObj) {
		this.docObj = docObj;
	}

	public List<PDFLine> getDocLines() {
		return docLines;
	}

	public void setDocLines(List<PDFLine> docLines) {
		this.docLines = docLines;
	}

	public List<Box> getDocBoxes() {
		return docBoxes;
	}

	public void setDocBoxes(List<Box> docBoxes) {
		this.docBoxes = docBoxes;
	}

	public List<MyPDRectangle> getDocRectangles() {
		return docRectangles;
	}

	public void setDocRectangles(List<MyPDRectangle> docRectangles) {
		this.docRectangles = docRectangles;
	}
}
