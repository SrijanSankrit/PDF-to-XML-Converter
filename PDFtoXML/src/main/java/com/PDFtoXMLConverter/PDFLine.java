package com.PDFtoXMLConverter;
import java.util.*;
//Represents a line in the document
public class PDFLine {

	public ArrayList<StringBlock> lineBlocks ;		//string blocks on the same line
	public float startY;
	public float endY;
	public int pageNo ;
	public PDFLine()
	{
		lineBlocks = new ArrayList<StringBlock>();
		
	}	
	public String toString()
	{
		int size = lineBlocks.size();
		String s = null;
		if(size > 0)
			s = lineBlocks.get(0).text ;
		for(int i = 1 ; i<size; i++)
			s = s + " " + lineBlocks.get(i).text ;
		return s;
	}
	
	public void sort()		//sorts string blocks present in line based on increasing values of their starting X
	{
		HashMap<Float,ArrayList<StringBlock>> hm = new HashMap<Float,ArrayList<StringBlock>>();
		ArrayList<Float> startX = new ArrayList<Float>();
		int size = lineBlocks.size();
		for(int i = 0; i<size ; i++)
		{
			StringBlock block = lineBlocks.get(i);
	
			if(hm.containsKey(block.startX))
				hm.get(block.startX).add(block);
			else 
			{
				startX.add(block.startX);
				ArrayList<StringBlock> blockList = new ArrayList<StringBlock>();
				blockList.add(block);
				hm.put(block.startX,blockList);
			}		
		}
		
		Collections.sort(startX);
		ArrayList<StringBlock> temp = new ArrayList<StringBlock>();
		for(int j = 0 ; j< startX.size() ; j++)
		{
			ArrayList<StringBlock> blocks = hm.get(startX.get(j));
		
			size = blocks.size();
			for(int k = 0 ;k<size ; k++)
				temp.add(blocks.get(k));			
		}
		
		lineBlocks = temp;
	}
	
}