package com.PDFtoXMLConverter;
import java.util.*;
import java.io.IOException; 
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;


public class myPDFTextStripper extends PDFTextStripper{
	List<StringBlock> blocks = new ArrayList<StringBlock>();
	public myPDFTextStripper() throws IOException {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void writeString(String text, List<TextPosition> textPositions) throws IOException
	{	
		StringBlock block = new StringBlock();		//creates a block with given textPositions
		block.text = text;
		setTextPositions(textPositions, block);
		blocks.add(block) ;
		
	    writeString(text);
	}
	
	public void setTextPositions(List<TextPosition> textPositions, StringBlock block)
	{
		//stores positions in block object and saves block 
		block.textPositions = textPositions;		
		block.startX = textPositions.get(0).getX();
		
		
		int size = textPositions.size();
		block.endX = textPositions.get(size-1).getEndX();
		block.width = block.endX - block.startX;
	
		float min = textPositions.get(0).getY();
		float max = textPositions.get(0).getHeight();
		for(int i = 1 ; i<size ; i++)			
		{
			
			float startY = textPositions.get(i).getY();
			float height = textPositions.get(i).getHeight();
			if(startY < min) 
				min = startY;
			if(height > max)
				max = height;
		}
		
		block.startY = min ;
		block.endY = block.startY + max;
		block.height = max;
	
	}
	
	public List<StringBlock> getTextPositions()
	{
		return blocks ;		
	}	

	
	public List<PDFLine> createLines()
	{		//string blocks in the same line are grouped together 
		Collections.sort(blocks);			//first, sorts all the blocks based on increasing Y
		int size = blocks.size();
		List<PDFLine> lines = new ArrayList<PDFLine>();
		
		int i = 1;
		
		PDFLine line =  new PDFLine() ;
		
		StringBlock block = blocks.get(0);
		line.startY = block.startY;
		line.endY = block.endY;
		line.lineBlocks.add(block);
		lines.add(line);
		while(i<size)
		{
			block = blocks.get(i);
			PDFLine currLine = lines.get(lines.size()-1);
			/*if the block starts before the previous block ends or starts within a gap of 2 
				after previous block ends, group it in the same line*/
			if(block.startY >= currLine.startY && block.startY <= currLine.endY + MyConstants.getMergelinesdistance())
			{	currLine.lineBlocks.add(block);
				currLine.endY = Math.max(currLine.endY, block.endY);
			}
			//else create a new line
			else
			{
				line = new PDFLine();
				line.lineBlocks.add(block);
				line.startY = block.startY;
				line.endY = block.endY;
				lines.add(line);
			}
			i++;
		}
		
		int numLines = lines.size() ;
		for(i = 0 ; i< numLines ; i++)
		{
			PDFLine tempLine = lines.get(i); //sort the blocks in a line based on increasing X
			lines.remove(i);
			tempLine.sort();
			lines.add(i,tempLine);
			
		}
		
		return lines;
	}
	
	

	protected List<Box> createBoxes(List<PDFLine> lines) {
    	int i = 0;
    	List<Box> boxes = new ArrayList<Box>();
    	for(PDFLine line : lines) {
    		for(StringBlock block : line.lineBlocks) {
   				// using concept that startX border of the text may be inside column or endX border maybe inside column
    			
    			Boolean added = false; // Checks if word is added or not.
    			
    			for(Box box: boxes) {
    				// Checks if the word belongs in any column horizontally.
   					if( ( box.getStartX() <= (int)block.startX && (int)block.startX <= box.getEndX() )
   								|| ( box.getStartX() <= (int)block.endX && (int)block.endX <= box.getEndX() ) ) {
   						
   						// We check the vertical distance now. I assume a distance of 20. If vertical distance is > 20, that is a new column.
   						if(line.startY < box.getEndY() + MyConstants.getMaxgapbetweenboxes()) {
							box.getBoxBlocks().add(block);
        					box.setEndY((int)line.endY);
        					if((int)block.startX < box.getStartX()) box.setStartX((int)block.startX);
        					if((int)block.endX > box.getEndX()) box.setEndX((int)block.endX);
        					added = true;
        					break;
    					}
   					}
   				}
   				
    			// If no column matches, make a new Column with all 4 boundaries.
    			
    			if(added == false) {
    				Box newBox = new Box((int)line.startY, (int)line.endY , (int)block.startX, (int)block.endX);
   					newBox.getBoxBlocks().add(block);
   					newBox.setBoxId(i++);
   					boxes.add(newBox);
   					
   				}
   			}
		}
    	return boxes;
    }
	

	 protected String classifyStyle(List<Box> boxes) 
	 {
		 		// We can check if a colon is used to differentiate key and value OR bold keys and words.
		    	// Having colons is given higher Priority. :)
		    	// Most of the PDFs can be classified in these cases.
		    	
		    	// Steps -->
		    	// Find wordCount in PDF  O(words in PDF)
		    	// Find colonCount O(Words in PDF)
		    	// Find BoldWords O(words in PDF)
		    	
		 		int blockCount = 0;
		 		int boldCount = 0;
		 		int colonCount = 0;
		 
		    	for(Box box : boxes) {
		    		for(StringBlock block : box.getBoxBlocks()) {
		    			blockCount++;
		    			if(block.hasColon() != -1) colonCount++;
		    			if(block.isBold() != -1) boldCount++;
		    		}
		    	}
		    	
		    	// If colonCount is 15% of wordCount, use colon to extract data.
		    	if((int)(blockCount * MyConstants.getColonPercentage()) <= colonCount) return MyConstants.ColonSeparation();
		    	// IF bold Count is 30% of word count, Bold lines for classifying key value pairs.
		    	else if((int)(blockCount * MyConstants.getBoldPercentage()) <= boldCount) return MyConstants.BoldFont();
		    	else return MyConstants.FontChange();
   }
	

}
	
	