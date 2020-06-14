package com.PDFtoXMLConverter;
import java.util.ArrayList;
import org.apache.pdfbox.pdmodel.PDDocument;


public class PDF2XML {
	public void convert(PDDocument doc, ArrayList<Integer> pageNos,String fileName) throws Exception
	{
		int size = pageNos.size();
		
		XMLCreator xmlCreator = new XMLCreator(pageNos,fileName);
		//Send doc to check whether it is an invoice
		
				
			//extract data page by page
			
			for(int i = 0; i<size ; i++)
			{
				try{
				BusinessDocCreator docCreator = new BusinessDocCreator();
				
				BusinessDoc bDoc = docCreator.create(doc.getPage(pageNos.get(i)-1),pageNos.get(i) );
				int pageNumber = pageNos.get(i);
				
				bDoc.extract(pageNumber);
				
				bDoc.createXML(xmlCreator,pageNos,pageNumber);
				}
				catch(Exception e) {
					//continuing for next pages
					
				}
		}
	
}
}
