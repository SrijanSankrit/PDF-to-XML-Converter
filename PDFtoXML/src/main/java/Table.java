import java.util.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.TextPosition;

//Generic Table 
public class Table {

	public int numRows;
	public int numCols;
	public int numHeadingLines = 1;
	public static float colHeadingEndPos = 0;
	public List<Column> colHeadings ;
	public List<HashMap<String,String>> data ;
	
	public Table(List<PDFLine> lines, List<MyPDRectangle> rectangles)
	{
		init();
		createColHeadings(lines, rectangles);
			
	}
	
	public void init()
	{					//initialization function
		colHeadings = new ArrayList<Column>();  
		data = new ArrayList<HashMap<String,String>>();
		numCols = 0;
		numRows = 0;
	}
	
	public void createColHeadings(List<PDFLine> lines, List<MyPDRectangle> rectangles)
	{
		
		PDFLine headingLine = lines.get(0);
	
		int size = headingLine.lineBlocks.size();
		for(int i = 0 ;i<size ; i++)
		{
			StringBlock block = headingLine.lineBlocks.get(i);
			if(i>0)
			{
				Column temp = colHeadings.get(numCols - 1);	//if the block overlaps with the previous heading on the X axis, it belongs to the same column
				if(block.startX >= temp.startX && block.startX < temp.endX)
				{
					temp.text = temp.text + " " + block.text;
					temp.endX = Math.max(temp.endX, block.endX);
					temp.width = temp.endX - temp.startX;
					continue;
				}
				else if(block.endX > temp.startX && block.endX <= temp.endX)
				{
					temp.text = temp.text + " " + block.text;
					temp.startX = Math.min(temp.startX, block.startX);
					temp.width = temp.endX - temp.startX;
					continue;					
				}
			}												//else create a new column heading 	
			
			Column col = new Column();
			numCols++;
			col.text = block.text;
			col.startX = block.startX;
			col.endX = block.endX;
			col.width = block.endX - block.startX;
			col.startY = headingLine.startY;
			col.endY = headingLine.endY;
			col.height = headingLine.endY - headingLine.startY;
			col.colNo = i;
			colHeadings.add(col);			
		}	
		
		//reinforce column boundaries using rectangles. If an extremely thin rectangle is built, it must act as a boundary line
		//if such a boundary line exists before the first and after the last column, change column widths accordingly
		
		int numRectangles = rectangles.size();
		
		for(int i = 0 ; i<numRectangles ; i++)
		{
			PDRectangle cell = rectangles.get(i);
			
			float cellStartX = cell.getLowerLeftX();
			float cellEndX = cell.getUpperRightX();
			float cellWidth = cellEndX - cellStartX;
			Column col1 = colHeadings.get(0);
			Column endCol = colHeadings.get(numCols -1);
			
			//checking is rectangle is thin, it's X is before col 1's startX and it intersects with col 1 on the Y axis
			if(cellWidth < 5 && cellStartX < col1.startX && intersect(cell,col1)) 		
			{
				colHeadings.get(0).startX = Math.min(cell.getLowerLeftX(),colHeadings.get(0).startX);
			}
			//checking is rectangle is thin, it's startX is after last col's endX and it intersects with last col on the Y axis
			else if(cellWidth < 5 && cellStartX > endCol.endX && intersect(cell,endCol))
			{
				colHeadings.get(numCols -1).endX = Math.max(cell.getUpperRightX(),colHeadings.get(numCols-1).endX);
			}				
			
		}
		
		
		if(colHeadings.get(0).endY < colHeadingEndPos)
		{
			int lineNum = 1;
			
			while(lineNum < size && lines.get(lineNum).endY <= colHeadingEndPos)
			{

				PDFLine nextHeadingLine = lines.get(lineNum);
				numHeadingLines++;
				size = nextHeadingLine.lineBlocks.size();
				for(int i = 0 ;i<size ; i++)
				{
					StringBlock block = nextHeadingLine.lineBlocks.get(i);
					for(int k = 0 ; k<numCols ; k++)
					{
						Column col = colHeadings.get(k);
						if(intersect(block,col))
						{
							String text = col.text + " " + block.text ;
							colHeadings.get(k).text = text ;
							break;
						}
					}
				}	
				lineNum++;	
				
			}
		}
	}
	
	public void build(BusinessTableInterface busTable,List<PDFLine> lines)
	{
		PDFLine line  ;
		int size = lines.size();

		for(int i = numHeadingLines ;i<size ; i++)
		{
			line = lines.get(i);
			if(busTable.isEOT(i, line) == true)
				return ;
			HashMap<String,String> row = new HashMap<String,String>();
			int numBlocks = line.lineBlocks.size();
			
			
			for(int j = 0 ;j<numBlocks ; j++)
			{
				StringBlock block = line.lineBlocks.get(j);
			
				//check which columns the block intersects with
				for(int k = 0 ; k<numCols ; k++)
				{
					Column col = colHeadings.get(k);
					if(intersect(block,col))
					{
					
						if((k<numCols-1 && block.endX < colHeadings.get(k+1).startX) || k == numCols-1) //Block fits perfectly in the column 
						{
							if(row.containsKey(col.text))			//Row already contains a block under the same column
							{
								String text = row.get(col.text) + block.text;
								row.remove(col.text);
								adjustColWidth(block,col);
								row.put(col.text, text);								
							}
							else								//Row's first block under this column
							{
								adjustColWidth(block,col);
								row.put(col.text,block.text);
							}
							
						}
						else if(k<numCols-1 && block.endX > colHeadings.get(k+1).startX)		//Block needs to be split into columns
						{
							ArrayList<String> colTexts = splitBlock(block,k);
							int colTextsSize = colTexts.size();
							for(int count = 0 ; count<colTextsSize ; count++)
							{
								row.put(colHeadings.get(k).text, colTexts.get(count));
								k++;								
							}
						}
					}		
				}
			}
			data.add(row);
			numRows++;
		
			if(numRows > 1)
			{
				busTable.mergeRows(numRows);
				busTable.mergeRows(numRows, line, lines.get(i-1));	
			}
			
				
		}			
	}

	public ArrayList<String> splitBlock(StringBlock block, int startColNum)
	{		//splits a block into columns based on X positions, returns names of column headings in which block is spread 
		List<TextPosition> textPos = block.textPositions;
		List<Float> startXList = new ArrayList<Float>();
		int size = textPos.size();
		for(int k = 0 ; k<size ; k++)
			startXList.add(textPos.get(k).getX());		
		
		ArrayList<String> colTexts = new ArrayList<String>();
		float newStartPos = block.startX;
		int count = 0;
		int endPosIndex = 0;
		int startPosIndex;
	
		for(int i = startColNum ; i<numCols ; i++)
		{
			Column col = colHeadings.get(i);
			if(intersect(block,col))
			{
				String s = "";
				startPosIndex = startXList.indexOf(newStartPos);				
				
				if(block.endX > col.endX)
				{
					count = 0;
					while(count<size && startXList.get(count) < col.endX)
						count++;
					count--;
					endPosIndex = count;
				}
				else
				{
					endPosIndex = size - 1;					
				}
				int j =0;
				for(j = startPosIndex ; j <= endPosIndex ; j++)
				{
					s = s + textPos.get(j).toString();					
				}
				colTexts.add(s);

				count++;
				newStartPos = startXList.get(count);				
			}
		}	
			return colTexts;		
	}
	
	public boolean isEOT(int lineNo, PDFLine line)		
	{
		//check for end of table that is common for all tables
		return false;
	}
	
	
	
	public boolean intersect(StringBlock block, Column col) 
	{				//checks if block and column interesect based on X positions
        float l1 = col.startX;
        float r1 = col.endX;

        float l2 = block.startX;
        float r2 = block.endX;

        return ((l1 >= l2 && r1 <= r2) ||
                (l1 >= l2 && l1 <= r2 && r1 > r2) ||
                (l1 < l2 && r1 >= l2 && r1 <= r2) ||
                (l2 >= l1 && r2 <= r1)); 
    }
	
	public boolean intersect(PDRectangle cell, Column col)
	{				//checks if rectangles and column intersect based on Y positions
		float u1 = cell.getUpperRightY();
		float d1 = cell.getLowerLeftY();
		
		float u2 = cell.getUpperRightY();
		float d2 = cell.getLowerLeftY();
		
		return ((u1 >= u2 && d1 <= d2) ||
                (u1 >= u2 && u1 <= d2 && d1 > d2) ||
                (u1 < u2 && d1 >= d2 && d1 <= d2) ||
                (u2 >= u1 && d2 <= d1)); 
		
	}
	
	
	public void adjustColWidth(StringBlock block, Column col)
	{
		float l1 = col.startX;
        float r1 = col.endX;

        float l2 = block.startX;
        float r2 = block.endX;
	        
		if(l2<l1)
			col.startX = l2;
		if(r2>r1)
			col.endX = r2;
	}
	
}
