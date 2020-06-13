package com.PDFtoXMLConverter;
import java.util.ArrayList;

// Data stored in different boxes inside the pdf.
// This is basically a box with integer dimensions.
public class Box {
	protected int startX, endX, startY, endY;
	
	// Stores the words in the box.
	ArrayList<StringBlock> boxBlocks ;
	
	// id of the box
	protected int boxId;
	
	//class constructor
	public Box(int top, int bottom, int left, int right) {
		boxBlocks = new ArrayList<StringBlock>();
		this.startX = left;
		this.endX = right;
		this.startY = top;
		this.endY = bottom;
	}
}


