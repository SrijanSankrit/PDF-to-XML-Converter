package com.PDFtoXMLConverter;
import java.util.*;
import org.apache.pdfbox.text.TextPosition;
//PDFTextStripper divides the text in a document into blocks. Reprsents one such block
public class StringBlock implements Comparable<StringBlock> {

	public float startX;
	public float endX;
	public float startY;
	public float endY;
	public float width;
	public float height;
	public List<TextPosition> textPositions;	//stores all the text positions present in the block
	public String text ;
	public int fontChange = -1;
	
	public String toString()
	{
		String s = text	;
		return s;
	}
	
	public int compareTo(StringBlock block)			//comparision with another block based on their Y positions
	{
		if(this.startY < block.startY)
			return -1;
		else if(this.startY == block.startY)
			return 0;
		else 
			return 1;
		
	}
	public int hasColon()			//checks if the text in this block contains a colon and returns index of char after colon
	{
		if(this.text.contains(":"))
			return this.text.indexOf(":") + 1;
		else 
			return -1;		
	}
	public int isBold()			//checks if the text in this block is bold and returns char after bold ends
	{
		int count = 0;
		int size = textPositions.size();
		while(count < size && textPositions.get(count).getFont().toString().contains("Bold"))
			count++;
		if(count == 0)
			count--;
		return count;
	}
		
}