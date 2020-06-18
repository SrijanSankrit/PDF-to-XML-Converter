package com.PDFtoXMLConverter;

import java.io.*;
import java.util.ArrayList;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * 
 * This class checks if the given list of pages <br>
 * holds correct for the PDF specified<br>
 * and prompts for XML Generation.
 * 
 * @author KUSHAGRA and BHAVYA
 * 
 */

public class PDF2XMLTester {

	/**
	* 
	 *  This method checks if the given list of pages <br>
	 * holds correct for the PDF specified<br>
	 * and prompts for XML Generation.
	 * 
	 * @author KUSHAGRA
	 * @param fileName entered
	 * @param arr of Pages in PDF
	 * @param pw password of PDF
	 * @return messages to be displayed on GUI
	 * @throws Exception
	 */
	public static String solver(String fileName,ArrayList<Integer> arr,String pw) throws Exception {

		PDDocument document = null;
		Logger logger = CacheManager.getLogger();
		try {
			File file = new File(fileName);
			//loading the document
			 if(pw!=Messages.getString("PDF2XMLTester.0")) { //checking if password is entered or not
				 document = PDDocument.load(file,pw);
				 
				 document.setAllSecurityToBeRemoved(true);
			 }
			 else{
				 document = PDDocument.load(file);
				 
			 }

			 int size = arr.size();
			 for(int PageC=0;PageC<size;PageC++) {
		    	 if(arr.get(PageC)>document.getNumberOfPages() ||arr.get(PageC)<1) {
		    		 document.close();
		    		 logger.error("Wrong Page indices input!");
				      return Messages.getString("PDF2XMLTester.1"); //prompting incorrect selection of pages
		    	 }
		     }
			 
			PDF2XML PDF2XMLObj = new PDF2XML();
			try {
				 PDF2XMLObj.convert(document,arr,fileName); 
			}
			catch (Exception e1)
			{
				//Logging
				document.close();
				CacheManager.getLogger().error(MyConstants.ERROR_MESSAGE3 + e1.getStackTrace());
				return MyConstants.ERROR_MESSAGE3;				
			}
			
			// Closing the document
			document.close();
			return Messages.getString("PDF2XMLTester.2"); //prompting XML Generation
		} 
		catch (IOException e) {
			document.close();
			logger.error("Error occured while processing whole PDF");
			return Messages.getString("PDF2XMLTester.3"); 
		}
	}

}
