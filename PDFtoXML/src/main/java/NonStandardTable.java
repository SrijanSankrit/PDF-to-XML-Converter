import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NonStandardTable extends Table implements BusinessTableInterface {

	public int startLine;
	public static int endLine = -1;
	public static int endRec = -1;
	
	public NonStandardTable(List<PDFLine> lines, List<MyPDRectangle> rectangles)
	{
		super(lines,rectangles);
	}
	
	
	public static int getStartLine(List<MyPDRectangle> rectangles, List<PDFLine> lines)
	{
		
		Collections.sort(rectangles);
		int numRectangles = rectangles.size();
		float tableStartPos ;
		int i =0;
		int count =1;
	
		int numRows = 0;
		int startRect = -1;
		float prevStartY = -1;
		float prevEndY = -1;
		int numTables = 0;
		
		for(i = 0; i<numRectangles ; i++)
		{
				MyPDRectangle rectangle = rectangles.get(i);
				float xl = rectangle.getLowerLeftX();
				float xr = rectangle.getUpperRightX();
				float yl = rectangle.getLowerLeftY();
				float yr = rectangle.getUpperRightY();
		        if(i != 0)
		        {
		        	MyPDRectangle prevRectangle = rectangles.get(i-1);
		        	
		        	if(yl == prevRectangle.getLowerLeftY() && yr == prevRectangle.getUpperRightY())
		        	{
		        		count++;
		        	}
		        	else 
		        		count=1;
		        	
		        	if(count > 2)
		        	{
		        		if(prevStartY == -1 && prevEndY == -1)		//start of a new Table
		        		{
		        			prevStartY = yr;
		        			prevEndY = yl;
		        			startRect = i; 
		        		}
		        		else 			//checking if the new consecutive cells are a part of the table
		        		{
		        			if(yr<prevEndY)		//consecutive cells overlap with previous ones
		        			{
		        				prevEndY = Math.max(prevEndY, yl) ; //expand the cell		        				
		        			}
		        			else if(yr - prevEndY < 10)		//new consecutive cells are close to previous ones and hence a row in the table
		        			{
		        				numTables = 1;
		        				numRows++;
		        				prevStartY = yr;
		        				prevEndY = yl;	        				
		        			}
		        			else					//gap is too much, can't be considered as a row 
		        			{
		        				if(numTables > 0)		//if a table is already found, break
		        				{
		        					endRec = i;
		        					break;
		        				}
		        				else				//resume search
		        				{	startRect = -1;
		        					prevStartY = -1;
		        					prevEndY = -1;
		        				}
		        			}
		        		}
		        	}
		        }	        
		}
		
		if(i == numRectangles && numTables>0)
			endRec = i;
		if(numRows>=1)
		{
			MyPDRectangle cell = rectangles.get(startRect);
			tableStartPos = cell.getUpperRightY();
			colHeadingEndPos = cell.getLowerLeftY() ;
			
			int numLines = lines.size();
			int j = 0;
			
			while(j<numLines && lines.get(j).endY <= prevEndY)
			{
					j++;
			}
			
			endLine = j ;
			
			for(j = 0 ; j<numLines ; j++)
			{
				PDFLine line = lines.get(j);
				
				if(line.startY > tableStartPos)
				{
					return j;
				}
			}
		}
		else 
		{
			colHeadingEndPos = 0;
			return -1;	
		}
		return -1;
	}
	
	public void extract(List<PDFLine> lines) 
	{
		build(this, lines);	
	}
		
	public boolean isEOT(int lineNo, PDFLine line)		//function to check if table ended - specific to items table
	{

			if(startLine + lineNo == endLine)
			{
				return true;
			}
			else
				return false;
	}
	
	public void mergeRows(int rowNum)
	{
		
		return ;
	}
	
	public void mergeRows(int rowNum, PDFLine line1, PDFLine line2)		//merges rows that belong together
	{
		if(line1.startY - line2.endY < 8)
		{
			HashMap<String,String> prevRow = data.get(rowNum-2);
			HashMap<String,String> currRow = data.get(rowNum-1);
			
			Set<String> keySet = prevRow.keySet();

			Iterator<String> iterator = keySet.iterator();
			 
			while(iterator.hasNext())
			{

				String key = iterator.next();
				String prevVal = prevRow.get(key);
				if(currRow.containsKey(key))
				{
					String currVal = null;
					currVal = currRow.get(key);
					prevVal = prevVal + currVal;
					prevRow.put(key,prevVal);
				}
			}			

			data.remove(rowNum-1);
			numRows--;	
		}
		return;		
	}
	

}
