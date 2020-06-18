package com.PDFtoXMLConverter;
import java.util.*;
import org.apache.pdfbox.text.TextPosition;

/**
 * PDFTextStripper divides the text in a document into blocks. Class that represents one such block
 * @author BHAVYA SHARMA
 */


public class StringBlock implements Comparable<StringBlock> {

	private float startX;					//Starting X position of block
	private float endX;						//Ending X position of block
	private float startY;					//Starting Y position of block
	private float endY;						//Ending Y position of block
	private float width;					//Width of block	
	private float height;					//Height of block
	private List<TextPosition> textPositions; // stores all the text positions present in the block
	private String text;					//Text present in block
	private int fontChange ;				//Whether block has a font change

	/**
	 * Constructor
	 */
	public StringBlock()
	{
		setFontChange(-1);		
	}
	
	
	@Override
	public int compareTo(StringBlock block) // comparison with another block based on their Y positions
	{
		if (this.getStartY() < block.getStartY()) {
			return -1;
		} else if (this.getStartY() == block.getStartY()) {
			return 0;
		} else {
			return 1;
		}

	}

	/**
	 * Method that checks if the text in this block contains a colon 
	 * @return index of character after colon
	 */
	public int hasColon() 
	{
		if (this.getText().contains(":")) {
			return this.getText().indexOf(":") + 1;
		} else {
			return -1;
		}
	}

	/**
	 * Method that checks if the text in this block is bold
	 * @return index of character after last bold character
	 */
	public int isBold() 
	{
		int count = 0;
		int size = this.getTextPositions().size();
		while (count < size && this.getTextPositions().get(count).getFont().toString().contains("Bold")) {
			count++;
		}
		if (count == 0) {
			count--;
		}
		return count;
	}

	//Getters and Setters
	
	public float getStartX() {
		return startX;
	}

	public void setStartX(float startX) {
		this.startX = startX;
	}

	public float getEndX() {
		return endX;
	}

	public void setEndX(float endX) {
		this.endX = endX;
	}

	public float getStartY() {
		return startY;
	}

	public void setStartY(float startY) {
		this.startY = startY;
	}

	public float getEndY() {
		return endY;
	}

	public void setEndY(float endY) {
		this.endY = endY;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public List<TextPosition> getTextPositions() {
		return textPositions;
	}

	public void setTextPositions(List<TextPosition> textPositions) {
		this.textPositions = textPositions;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getFontChange() {
		return fontChange;
	}

	public void setFontChange(int fontChange) {
		this.fontChange = fontChange;
	}

}