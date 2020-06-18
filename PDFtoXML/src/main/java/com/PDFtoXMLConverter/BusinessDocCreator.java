package com.PDFtoXMLConverter;

import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDPage;

/**
 * Class that checks what type of document is received and returns an instance of the same
 * Checks whether the document is an invoice. If not, returns instance of non standard invoice doc
 * @author BHAVYA SHARMA
 * 
 */

public class BusinessDocCreator {

	/**
	 *@param doc Page to be processed 
	 *@param pageNo Page number of said page 
	 *@return Instance of specific subclass of business document 
	 */
	
	public BusinessDoc create(PDPage doc, int pageNo) throws Exception {
		BusinessDoc bDoc = InvoiceDocument.checkNcreate(doc, pageNo);
		return bDoc;
	}

}
