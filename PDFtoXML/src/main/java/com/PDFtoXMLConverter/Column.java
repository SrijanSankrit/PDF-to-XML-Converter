package com.PDFtoXMLConverter;


/**
 * Class that denotes a column heading in the table
 * @author BHAVYA SHARMA
 */


public class Column {

	private String text;				//text in the column heading
	private float startX;				//X coordinate of starting position of column heading
	private float startY;				//Y coordinate of starting position of column heading
	private float endX;					//X coordinate of ending position of column heading
	private float endY;					//Y coordinate of ending position of column heading
	private float width;				//width of the column
	private float height;				//height of the column		
	private int colNo;					//column number

	
	//Getters and Setters	 
	
	String getText() {
		return this.text;
	}

	void setText(String text) {
		this.text = text;
	}

	float getStartX() {
		return this.startX;
	}

	void setStartX(float startX) {
		this.startX = startX;
	}

	float getStartY() {
		return this.startY;
	}

	void setStartY(float startY) {
		this.startY = startY;
	}

	float getEndX() {
		return this.endX;
	}

	void setEndX(float endX) {
		this.endX = endX;
	}

	float getEndY() {
		return this.endY;
	}

	void setEndY(float endY) {
		this.endY = endY;
	}

	float getWidth() {
		return this.getEndX() - this.getStartX();
	}

	void setWidth(float width) {
		this.width = width;
	}

	float getHeight() {
		return height;
	}

	void setHeight(float height) {
		this.height = height;
	}

	int getColNo() {
		return colNo;
	}

	void setColNo(int colNo) {
		this.colNo = colNo;
	}


}
