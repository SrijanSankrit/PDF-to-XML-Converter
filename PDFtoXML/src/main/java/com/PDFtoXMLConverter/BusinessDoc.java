package com.PDFtoXMLConverter;
import java.util.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.IOException;
import org.apache.pdfbox.pdmodel.common.PDRectangle;


//General class of documents that an organization can have
//Holds all the lines, rectangles and boxes present in the doc as fields
public class BusinessDoc {
	
	public PDPage docObj;
	public List<PDFLine> docLines;	
	public List<Box> docBoxes ;
	public List<MyPDRectangle> docRectangles ;
	public void createXML(XMLCreator xmlCreator, ArrayList<Integer> pages,int pageNo) throws Exception
	{
		
		
	}
	public void extract(int pageNo) throws IOException
	{
		
		
	}
}
