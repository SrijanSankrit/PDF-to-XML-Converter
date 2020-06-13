package com.PDFtoXMLConverter;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class MyPDRectangle extends PDRectangle implements Comparable<MyPDRectangle>{

	public int compareTo(MyPDRectangle rectangle)			//comparison with another block based on their Y positions
	{
		if(this.getUpperRightY() < rectangle.getUpperRightY())
			return -1;
		else if(this.getUpperRightY() < rectangle.getUpperRightY())
			return 0;
		else 
			return 1;
		
	}
	
}
