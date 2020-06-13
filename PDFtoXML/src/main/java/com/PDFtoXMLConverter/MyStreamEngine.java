package com.PDFtoXMLConverter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;;

public class MyStreamEngine extends PDFStreamEngine {

	int imageNumber= 1;
	float pageHeight;
	float pageWidth ;
	List<MyPDRectangle> cells;
	List<GraphicalLine> graphicalLines ;
	
	MyStreamEngine()
	{
		cells = new ArrayList<MyPDRectangle>();
		graphicalLines = new ArrayList<GraphicalLine>();
	}
	
	
	public void processPage(PDPage page) throws IOException
	{
		pageHeight = page.getMediaBox().getHeight();
		//System.out.println("height " + pageHeight);
		pageWidth = page.getMediaBox().getWidth();
		super.processPage(page);
	}
	
	
	@Override
    protected void processOperator( Operator operator, List<COSBase> operands) throws IOException
    {			//stores all the operators in the pdf
	     
        String operation = operator.getName();
        imageNumber++;
        if( "re".equals(operation) )	// if the operator to draw a rectangle has been used, save dimension of rectangle
        {
        	MyPDRectangle cell = new MyPDRectangle();

           	float startX = getFloat(operands.get(0));
        	float startY = getFloat(operands.get(1));
        	float width = getFloat(operands.get(2));
        	float height = getFloat(operands.get(3));

          	float xl;
        	float xr;
        	float yl;
        	float yr;
        	if(height > 0)
        	{
        		xl = startX;
            	yl = pageHeight - startY;
            	xr = xl +  width;
            	yr = yl - height;        
        	}
        	else
        	{
        		xl = startX;
            	yr = pageHeight - startY;
            	xr = xl +  width;
            	yl = yr + Math.abs(height);     		
        	}
            
        	cell.setLowerLeftX(xl);
          	cell.setLowerLeftY(yl);
          	cell.setUpperRightX(xr);
          	cell.setUpperRightY(yr);
            
        	PDRectangle cell1; 
        	boolean exist = false;
        	for ( int i = 0 ; i < cells.size(); i ++)
        	{
        		cell1 = cells.get(i);
        		if ( cell1.getLowerLeftX() == xl &&  cell1.getLowerLeftY() == yl &&
        			cell1.getUpperRightX() == xr && cell1.getUpperRightY() == yr)	
        		{
        			exist = true;
        			break;
        		}
        	}
        	if ( !exist)
        	{
        			cells.add(cell);

        	}
        }
        else if("l".contentEquals(operation))
        {
        	int numGlines = graphicalLines.size();   

          //  System.out.println(operation);
        //	System.out.println(operands);
        	GraphicalLine line = graphicalLines.get(numGlines -1); 
        	if(line.X2 == -1 && line.Y2 == -1)
        	{
        		graphicalLines.get(numGlines -1).X2 = getFloat(operands.get(0));
        		graphicalLines.get(numGlines -1).Y2 = getFloat(operands.get(1));        	
        		graphicalLines.get(numGlines -1).init();
        		//System.out.println("INIT " + graphicalLines.get(numGlines -1).Y1 + " " + graphicalLines.get(numGlines -1).Y2);

        	}
        }
        else if("m".contentEquals(operation))
        {
        	//System.out.println(operands);
        	GraphicalLine line = new GraphicalLine();
        	line.X1 = getFloat(operands.get(0));
        	line.Y1 = getFloat(operands.get(1)); 
        	graphicalLines.add(line);
        }
        else
        {
            super.processOperator( operator, operands);
        }
    }
 
	private float getFloat(COSBase val )
	{
		float retval ;
    	COSFloat cosfl;
    	COSInteger cosint;
    	try  {
    		cosfl =(COSFloat) val ;
    		retval  = cosfl.floatValue();
    	}
    	catch ( Exception e)
    	{
    		cosint =(COSInteger) val ;
    		retval  = cosint.floatValue();
    	}
    	return retval;
	}
}
