package com.PDFtoXMLConverter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
/**
 * 
 * This class creates an object of XMLCreator class<br>
 * and adds the XML pages to the generated XML<br>
 * pagewise.
 * 
 * @author KUSHAGRA and BHAVYA
 * 
 */

public class PDF2XML {
	
	private static Logger logger = LogManager.getLogger(PDF2XML.class.getName());
	/**
	 * 
	 *  This method checks if the given list of pages <br>
	 * holds correct for the PDF specified<br>
	 * and prompts for XML Generation.
	 * 
	 * @author KUSHAGRA
	 * @param PDDocument doc file of the PDF file
	 * @param ArrayList of pages
	 * @param String fileName of entered PDF
	 * @throws Exception
	 * 
	 */
	public void convert(PDDocument doc, ArrayList<Integer> pageNos,String fileName) throws Exception
	{
		
		Collections.sort(pageNos);
		int size = pageNos.size();
		logger.info("FileName -->" + fileName);
		XMLCreator xmlCreator = new XMLCreator(pageNos,fileName);		//Send doc to check whether it is an invoice
		
		
				
																			
			
			for(int page = 0; page<size ; page++)						//extract data page by page
			{
				try{
				
				logger.info("\nProcessing Page No. " + page);
				
				BusinessDocCreator docCreator = new BusinessDocCreator();
				
				BusinessDoc bDoc = docCreator.create(doc.getPage(pageNos.get(page)-1),pageNos.get(page) );
				int pageNumber = pageNos.get(page);
				
				bDoc.extract(pageNumber);
				
				bDoc.createXML(xmlCreator,pageNos,pageNumber);
				}
				catch(Exception e) {
					//continuing for next pages in case of exception in prior pages 
					logger.error("Page "+page+" is empty");
				}
		}
	
}
}
