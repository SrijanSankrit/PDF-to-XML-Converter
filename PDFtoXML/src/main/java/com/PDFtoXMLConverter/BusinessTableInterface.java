package com.PDFtoXMLConverter;
//for document specific requirements such as checking end of table, merging rows - methods that will change with doc type 
public interface BusinessTableInterface {

	public boolean isEOT(int lineNo, PDFLine line);
	public void mergeRows(int i);
	public void mergeRows(int rowNum, PDFLine line1 , PDFLine line2);
}
