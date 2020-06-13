package com.PDFtoXMLConverter;
import java.util.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
//represents special case of table - items table in an invoice doc
public class InvoiceItemsTable extends Table implements BusinessTableInterface {

	int startLine = -1 ;
	int endLine = -1;
	
	public InvoiceItemsTable(List<PDFLine> lines, List<MyPDRectangle> rectangles)
	{
		super(lines,rectangles);
	}
	
	public static int getStartLine(List<PDFLine> lines)	//gets start line of table
	{
		int numLines = lines.size();
		
		for(int i = 0 ;i<numLines ; i++)
		{
			List<StringBlock> blocks = lines.get(i).lineBlocks ;
			int numBlocks = blocks.size();
			
			for(int j = 0 ; j<numBlocks ; j++)
			{
				String sLower = blocks.get(j).text ;
				String s = sLower.toUpperCase(); //if we find the following terms, table starts there
			
				if(s.contains("SNO.") || s.contains("ITEM") || s.contains("DESCRIPTION") || s.contains("DESC.")
							|| s.contains("QUANTITY") || s.contains("QTY") || s.contains("AMOUNT") || s.contains("AMT.") || s.contains("SL."))
				{
					return i;					
				}				
			}			
		}		
		return -1;
	}
	
	public void extract(List<PDFLine> lines) 
	{
		build(this, lines);		
	}
	
	public boolean isEOT(int lineNo, PDFLine line)		//function to check if table ended - specific to items table
	{

		if(super.isEOT(lineNo, line) == false)
		{
			int numBlocks = line.lineBlocks.size();
			
			for(int j = 0 ;j<numBlocks ; j++)
			{
				StringBlock block = line.lineBlocks.get(j);
				String textLower = block.text;
				String text = textLower.toUpperCase();		
				if(text.contains("TOTAL") || text.contains("SUB")		//table ends if these words found in given line 
						|| text.contains("TAX") || text.contains("DISCOUNT") )
				{
					endLine = startLine + lineNo ;
					return true;			
				}
			}	
			return false;
		}
		else 
			return true;			
		
	}
	
	
	public void mergeRows(int rowNum)		//merges rows that belong together
	{
		HashMap<String,String> prevRow = data.get(rowNum-2);
		HashMap<String,String> currRow = data.get(rowNum-1);
		ArrayList<Integer> missingCols = new ArrayList<Integer>();
		
		if(prevRow.size() != currRow.size())		//if the current row has missing values corresponding to the columns
		{
			for(int k = 0 ; k<numCols ; k++)
			{
				Column col = colHeadings.get(k);
				String heading = col.text;
				
				if(currRow.containsKey(heading) == false)
					missingCols.add(col.colNo);		//collect the col numbers corresponding to which values are missing
			}		
			
		}
		
		int numMissingCols = missingCols.size();
		int flag = 0;
		for(int k = 0 ; k<numMissingCols ; k++)
		{
			Column col = colHeadings.get(missingCols.get(k));
			String heading = col.text;
			heading.toUpperCase();					//check if the amount column is missing value		
			if(heading.contains("AMOUNT") || heading.contains("AMT"))
				flag = 1;
		}
		
		
		if(flag == 1)			//if the amount column is missing value, this row must be merged with the previous one
		{
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
					prevRow.remove(key);
					prevRow.put(key,prevVal);
				}				
			}			
			data.remove(rowNum-1);
			numRows--;
		}		
		return;		
	}	
	

	public void mergeRows(int rowNum, PDFLine line1 , PDFLine line2)
	{
		
		return ;
	}
	
}

