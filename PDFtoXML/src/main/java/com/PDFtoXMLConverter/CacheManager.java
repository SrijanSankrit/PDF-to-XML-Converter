package com.PDFtoXMLConverter;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Cache Manager of Application
 * @author BHAVYA SHARMA
 *  
 */

public class CacheManager {

	private static String invoiceHeadings[];
	private static Logger logger ;
	public static void fillCache() throws Exception
	{
		try {

			setLogger(LogManager.getRootLogger());
			InputStream inpStream;

			Properties prop = new Properties();
			String propFileName = "config.properties";

			inpStream = CacheManager.class.getClassLoader().getResourceAsStream(propFileName);

			if (inpStream != null) 
			{
				prop.load(inpStream);
			
				String headingStr = (String) prop.get("InvoiceItemTableHeadings");
				invoiceHeadings =headingStr.split(":");
				
			} 
		}
		catch (Exception e)
		{
			throw e;

		}
	}
	
	//Getters and Setters
	
	public static String[]  getInvoiceHeadings()
	{
		return invoiceHeadings;
		
	}
	public static Logger getLogger() {
		return logger;
	}
	private static void setLogger(Logger logger) {
		CacheManager.logger = logger;
	}
		
	
}
