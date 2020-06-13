import java.util.*;
//extracts words from the preamble and summary of the document

import org.apache.pdfbox.text.TextPosition;



public class BusinessWordExtractor {
		
		//takes boxes and string that classifes style of document (colon/bold) as input
		//returns a hashmap of words and their values
	   public HashMap<String, String> extract(List<Box> boxes, String divider) 
	   {
	    	HashMap<String,String> keyValue = new HashMap<String, String>();
	    	int offSet = 0;
	    	if(divider == "COLON") offSet = 1;
	    	else if(divider == "FONT CHANGE" || divider == "BOLD FONT") offSet = 0;
	    	
	    	
	    	// Checks if font name length decreases which marks the start of value.
	    	
	    	if(divider == "FONT CHANGE") {
	    		for(Box box : boxes) { // Assume that inside a box, only one type of font is used. Please make this happen.
	    			String prevFont = null;
	    			
	    			StringBlock prevBlock = null;
	    			for(StringBlock block : box.boxBlocks) {
	    				if(prevFont == null) prevFont = block.textPositions.get(0).getFont().toString();
	    				
	    				for(int i=1;i<block.textPositions.size();i++) {
	    					String currFont = block.textPositions.get(i).getFont().toString();
	    					
	    					if(prevFont.length() > currFont.length() && prevFont.contains(currFont)) {
	    						
	    						if(i == 1) {
	    							prevBlock.fontChange = prevBlock.text.length();
	    						}
	    						else {
	    							block.fontChange = i;
	    						}
	    						
	    						prevFont = currFont;
	    						break;
	    					}
	    					prevFont = currFont;
	    				}
	    				prevBlock = block;
	    			}
	    			
	    		}
	    	}
	    	
	    	// To check if font change generalisation is working.
	    	
	    	/*for(Box box : boxes) { // Assume that inside a box, only one type of font is used. Please make this happen.
    			for(StringBlock block : box.boxBlocks) {
    				System.out.println(block.text   + " " + block.fontChange);
    			}
    			
    		}*/
	    	
	
	    	for(Box box : boxes) {
	    		int numBoxBlocks = box.boxBlocks.size();
	    		int i=0;
	    		int textCount=1;
	    		while(i < numBoxBlocks) {
	    			StringBlock currStringBlock = box.boxBlocks.get(i);
	    			int checker = -1;
	    			if(divider == "COLON") checker = currStringBlock.hasColon();
	    			else if(divider == "BOLD FONT") {
	    				
	    				checker = currStringBlock.isBold();
	    				
	    			}
	    			else if(divider == "FONT CHANGE") checker = currStringBlock.fontChange;
	    			
	    			if(checker != -1) {

	    				// KEY - VALUE in the same line
	    				if(checker < currStringBlock.text.length()) {
	    					String key = currStringBlock.text.substring(0, checker-offSet).strip();
	    					String value = currStringBlock.text.substring(checker).strip();
	    					keyValue.put(key, value); i++;
	    				}
	    				// Multiple Lines case
	    				else {
	    					String Value = "";
	    				
	    					int j = i+1;
       					while(j < numBoxBlocks) {
       						
       						StringBlock nextStringBlock = box.boxBlocks.get(j);
       						int checker2 = -1;
       						if(divider == "COLON") checker2 = nextStringBlock.hasColon();
       		    			else if(divider == "BOLD FONT") checker2 = nextStringBlock.isBold();
       		    			else if(divider == "FONT CHANGE") checker2 = nextStringBlock.fontChange;
       						
       						// Now, we add everything to the value string until we encounter a bold StringBlock
       						if(checker2 != -1) break;
       						Value = Value.concat(nextStringBlock.text + " ");
       						j++;
       						
       					}
       					// Those Lines already traversed. Update i to nextLine.
       					i=j;
       					if(Value != "") {
       						
       						String Key = currStringBlock.text.substring(0, currStringBlock.text.length()-offSet).strip();
       						
           					keyValue.put(Key, Value);
           					
       					}
       					
	    				}	
	    			}
	    			// If no Bold nor Colon separation.
	    			else {
	    				
	    				String key = "Text"+textCount;
	    				textCount++;
    					String value = currStringBlock.text;
    					keyValue.put(key, value);
	    				i++;
	    			}
	    			
	    		}
	    	}	
	    	
           return keyValue;
	   }
	
	
	
}
