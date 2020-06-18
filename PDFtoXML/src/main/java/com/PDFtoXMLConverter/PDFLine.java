package com.PDFtoXMLConverter;
import java.util.*;

/**
 * Class that represents a line in the document
 * @author BHAVYA SHARMA
 */

public class PDFLine {

	private ArrayList<StringBlock> lineBlocks;	 // string blocks on the same line
	private float startY;						// starting Y position of line	
	private float endY;							// ending Y position of line			
	private int pageNo;							// page no that line belongs to

	
	public PDFLine() {
		this.setLineBlocks(new ArrayList<StringBlock>());

	}

	/**
	 * Method that sorts string blocks present in line based on increasing values of starting X
	 */
	public void sort() 
	{
		HashMap<Float, ArrayList<StringBlock>> hm = new HashMap<Float, ArrayList<StringBlock>>();
		ArrayList<Float> startX = new ArrayList<Float>();
		int size = this.getLineBlocks().size();
		for (int i = 0; i < size; i++) {
			StringBlock block = this.getLineBlocks().get(i);

			if (hm.containsKey(block.getStartX())) {
				hm.get(block.getStartX()).add(block);
			} else {
				startX.add(block.getStartX());
				ArrayList<StringBlock> blockList = new ArrayList<StringBlock>();
				blockList.add(block);
				hm.put(block.getStartX(), blockList);
			}
		}

		Collections.sort(startX);
		ArrayList<StringBlock> temp = new ArrayList<StringBlock>();
		for (Float element : startX) {
			ArrayList<StringBlock> blocks = hm.get(element);

			size = blocks.size();
			for (int k = 0; k < size; k++) {
				temp.add(blocks.get(k));
			}
		}

		this.setLineBlocks(temp);
	}
	
	//Getters and Setters

	public ArrayList<StringBlock> getLineBlocks() {
		return lineBlocks;
	}

	public void setLineBlocks(ArrayList<StringBlock> lineBlocks) {
		this.lineBlocks = lineBlocks;
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

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

}