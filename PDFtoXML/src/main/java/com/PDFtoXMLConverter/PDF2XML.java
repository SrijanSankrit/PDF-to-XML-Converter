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
 * @author KUSHAGRA, BHAVYA
 * 
 */

public class PDF2XML {
	public void convert(PDDocument doc, ArrayList<Integer> pageNos,String fileName) throws Exception
	{
		Logger logger = CacheManager.getLogger();
		int size = pageNos.size();
		logger.debug("FileName -->" + fileName);

		XMLCreator xmlCreator = new XMLCreator(pageNos,fileName);
		// Send doc to check whether it is an invoice

		// extract data page by page

		for (int page = 0; page < size; page++) {
				
				BusinessDocCreator docCreator = new BusinessDocCreator();

				BusinessDoc bDoc = docCreator.create(doc.getPage(pageNos.get(page) - 1), pageNos.get(page));
				int pageNumber = pageNos.get(page);

				bDoc.extract(pageNumber);

				bDoc.createXML(xmlCreator, pageNos, pageNumber);
				// continuing for next pages
			
		}

	}
}
