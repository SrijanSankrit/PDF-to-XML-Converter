import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import java.util.List;

public class NonStandardInvoiceDocument extends BusinessDoc {
	
	public List<HashMap<String,String>> preambles = null;
	public HashMap<String,String> summary = null;
	public List<NonStandardTable> nsTables = null;
	public int tableStartLine = -1;
	public int tableEndLine = -1;
	
	
	public void extract(int pageNo) throws IOException
	{
		
			tableStartLine = NonStandardTable.getStartLine(docRectangles, docLines);
			tableEndLine = NonStandardTable.endLine;		
			BusinessWordExtractor wordExtractor = new BusinessWordExtractor();
			myPDFTextStripper stripper = new myPDFTextStripper();
			
			if(tableStartLine != -1)
			{
				int i = 0;
				int prevTableEndLine = 0;
				int endRec = 0;
				
				while(tableStartLine != -1)
				{
					
					extractTable(pageNo);
					
					
					//Preamble
					List<PDFLine> preambleLines = new ArrayList<PDFLine>();
					List<Box> preambleBoxes = null;
				
					for(int k = prevTableEndLine; k<this.tableStartLine ; k++)					//collecting lines from start of page to table start line		
					{
						preambleLines.add(docLines.get(k));	
					}

					preambleBoxes = stripper.createBoxes(preambleLines);		//creating boxes out of said lines
					HashMap<String, String> preamble = wordExtractor.extract(preambleBoxes, stripper.classifyStyle(preambleBoxes));
					
					if(preambles == null)
						preambles = new ArrayList<HashMap<String,String>>();
					
					preambles.add(preamble);
					
					prevTableEndLine = tableEndLine ;
					
					List<MyPDRectangle> tableRectangles = new ArrayList<MyPDRectangle>();
					int size = docRectangles.size();
					endRec = endRec + NonStandardTable.endRec;
					
					
					for(int j = endRec ; j<size ; j++)
						tableRectangles.add(docRectangles.get(j));
					
					tableStartLine = NonStandardTable.getStartLine(tableRectangles, docLines);
					tableEndLine = NonStandardTable.endLine;
					i++;
					
				}
							
				
				
				//Summary
				int numLines = docLines.size();				
				List<PDFLine> summaryLines = new ArrayList<PDFLine>();
				
				List<Box> summaryBoxes = null;								
				if(tableEndLine!=-1)
				{		
					for(int x = prevTableEndLine ; x<numLines ; x++)			//collecting lines from  table end line to end of page
						summaryLines.add(docLines.get(x));	
					
					summaryBoxes = stripper.createBoxes(summaryLines);			//creating boxes out of said lines
					summary = wordExtractor.extract(summaryBoxes, stripper.classifyStyle(summaryBoxes));
				}
			}
			else 
			{	
						
				nsTables = null;	//no table exists
				tableEndLine = -1;
				//create boxes in the entire page
				if(preambles == null)
					preambles = new ArrayList<HashMap<String,String>>();
				List<Box> preambleBoxes = stripper.createBoxes(docLines);
				
				preambles.add(wordExtractor.extract(preambleBoxes, stripper.classifyStyle(preambleBoxes))); //store everything in preamble
				summary = null;	//no summary exists
			}

			
						
	}
	
	
	public void extractTable(int pageNo)
	{
			List<PDFLine> pageLines = docLines ;
			List<MyPDRectangle> pageRectangles = docRectangles ;
			
			List<PDFLine> tableLines = new ArrayList<PDFLine>();

			int numLines = pageLines.size();
			
			for(int i = tableStartLine; i<tableEndLine ; i++)
				tableLines.add(pageLines.get(i));
			
			//using non standard table's extract function to build the table
		
			if(nsTables == null)
				nsTables = new ArrayList<NonStandardTable>();
			
			NonStandardTable nsTable = new NonStandardTable(tableLines, pageRectangles);
			nsTable.startLine = tableStartLine;
			nsTable.extract(tableLines);
			
			
			
			nsTables.add(nsTable);
	}
	public void createXML(XMLCreator xmlCreator, ArrayList<Integer> pages,int pageNo) throws Exception
	{
		//adding data to XML file
		if(preambles != null)
		{
			for(int i = 0 ; i<preambles.size() ; i++)
				xmlCreator.addData(preambles.get(i),pages,pageNo);	
		}
		if(nsTables != null)
		{
			for(int i = 0 ; i<nsTables.size() ; i++)
				xmlCreator.addTable(nsTables.get(i).data,pages,pageNo);
		}
		if(summary!= null)
		{
			xmlCreator.addData(summary,pages,pageNo);		
		}
	}
		
}
