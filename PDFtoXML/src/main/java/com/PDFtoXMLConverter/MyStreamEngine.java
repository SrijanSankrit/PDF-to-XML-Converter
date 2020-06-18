package com.PDFtoXMLConverter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;;

/**
 * Class that extends PDFStreamEngine class of PDFBox. Processes operators used in the creation of the PDF
 * @author BHAVYA SHARMA
 * 
 */


public class MyStreamEngine extends PDFStreamEngine {

	private float pageHeight;
	private float pageWidth;
	private List<MyPDRectangle> cells;
	
	/**
	 * Constructor
	 * 
	 */

	MyStreamEngine() {
		this.setCells(new ArrayList<MyPDRectangle>());
	}

	@Override
	public void processPage(PDPage page) throws IOException {
		//processes a page in the PDF
		this.setPageHeight(page.getMediaBox().getHeight());
		// System.out.println("height " + pageHeight);
		this.setPageWidth(page.getMediaBox().getWidth());
		super.processPage(page);
	}

	@Override
	protected void processOperator(Operator operator, List<COSBase> operands) throws IOException { 
		// stores all the operators in the PDF
		String operation = operator.getName();

		if ("re".equals(operation)) // if the operator to draw a rectangle has been used, save dimension of
									// rectangle
		{
			MyPDRectangle cell = new MyPDRectangle();

			float startX = this.getFloat(operands.get(0));
			float startY = this.getFloat(operands.get(1));
			float width = this.getFloat(operands.get(2));
			float height = this.getFloat(operands.get(3));

			float xl;
			float xr;
			float yl;
			float yr;
			if (height > 0) {
				xl = startX;
				yl = this.getPageHeight() - startY;
				xr = xl + width;
				yr = yl - height;
			} else {
				xl = startX;
				yr = this.getPageHeight() - startY;
				xr = xl + width;
				yl = yr + Math.abs(height);
			}

			cell.setLowerLeftX(xl);
			cell.setLowerLeftY(yl);
			cell.setUpperRightX(xr);
			cell.setUpperRightY(yr);

			PDRectangle cell1;
			boolean exist = false;
			for (MyPDRectangle element : this.getCells()) {
				cell1 = element;
				if (cell1.getLowerLeftX() == xl && cell1.getLowerLeftY() == yl && cell1.getUpperRightX() == xr
						&& cell1.getUpperRightY() == yr) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				this.getCells().add(cell);

			}
		}  else {
			super.processOperator(operator, operands);
		}
	}

	/**
	 * Method that converts a COSBase val, which may be integer or float to a float value
	 * 
	 */
	private float getFloat(COSBase val) {
		float retval;
		COSFloat cosfl;
		COSInteger cosint;
		try {
			cosfl = (COSFloat) val;
			retval = cosfl.floatValue();
		} catch (Exception e) {
			cosint = (COSInteger) val;
			retval = cosint.floatValue();
		}
		return retval;
	}

	
	//Getters and Setters
	
	float getPageHeight() {
		return pageHeight;
	}

	void setPageHeight(float pageHeight) {
		this.pageHeight = pageHeight;
	}

	float getPageWidth() {
		return pageWidth;
	}

	void setPageWidth(float pageWidth) {
		this.pageWidth = pageWidth;
	}

	List<MyPDRectangle> getCells() {
		return cells;
	}

	void setCells(List<MyPDRectangle> cells) {
		this.cells = cells;
	}

}
