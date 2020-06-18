package com.PDFtoXMLConverter;

import java.util.ArrayList;

/**
 * This class stores the contents of a box and its integer dimensions. used to
 * denote a box inside the PDF page.
 * 
 * @author SRIJAN
 *
 */

public class Box {
	// Dimensions of the box.
	private int startX, endX, startY, endY;

	// Stores the words in the box.
	private ArrayList<StringBlock> boxBlocks;

	// id of the box
	private int boxId;

	// class constructor
	public Box(int top, int bottom, int left, int right) {
		boxBlocks = new ArrayList<StringBlock>();
		this.startX = left;
		this.endX = right;
		this.startY = top;
		this.endY = bottom;
	}

	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getEndX() {
		return endX;
	}

	public void setEndX(int endX) {
		this.endX = endX;
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public int getEndY() {
		return endY;
	}

	public void setEndY(int endY) {
		this.endY = endY;
	}

	public ArrayList<StringBlock> getBoxBlocks() {
		return boxBlocks;
	}

	public void setBoxBlocks(ArrayList<StringBlock> boxBlocks) {
		this.boxBlocks = boxBlocks;
	}

	public int getBoxId() {
		return boxId;
	}

	public void setBoxId(int boxId) {
		this.boxId = boxId;
	}

}