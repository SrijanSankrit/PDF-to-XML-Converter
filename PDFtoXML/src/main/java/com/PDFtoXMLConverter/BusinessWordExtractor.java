package com.PDFtoXMLConverter;

import java.util.*;
//extracts words from the preamble and summary of the document

import org.apache.pdfbox.text.TextPosition;

/**
 * 
 * This class extracts the value data from the PDF Document excluding the table part.
 * It divides the unstructured part into : <br>
 * (i) Key and Value Pairs.<br>
 * (ii) Texts which are not classified as above.<br>
 * 
 * 
 * 
 */
public class BusinessWordExtractor {
	
	
	
	// Hash Table which stores key and value pairs found in the PDf page.
	private HashMap<String, String> keyAndValuePairs = new HashMap<String, String>();
	
	public HashMap<String, String> getKeyAndValuePairs() {
		return keyAndValuePairs;
	}

	public void setKeyAndValuePairs(HashMap<String, String> keyAndValuePairs) {
		this.keyAndValuePairs = keyAndValuePairs;
	}

	
	/**
	 * 
	 * This function extracts the key and value pairs from the page and stores it in the hash table.
	 * 
	 * 
	 * @param boxes List of boxes found in the PDF page.
	 * @param classifier Carries the information about how the key and value are separated in the page.
	 * 
	 */
	
	protected void extract(List<Box> boxes, String classifier) {

		
		// Offset takes care of not taking the colon while extracting key and value.
		int offSet = 0;
		if (classifier == MyConstants.ColonSeparation())
			offSet = 1;
		else if (classifier == MyConstants.BoldFont() || classifier == MyConstants.FontChange())
			offSet = 0;

		// Checks if font name length decreases which marks the start of value.

		if (classifier == MyConstants.FontChange()) {
			for (Box box : boxes) { // Assume that inside a box, only one type of font is used. Please make this
									// happen.
				String previousTextFont = null;

				StringBlock lastBlock = null;
				for (StringBlock block : box.getBoxBlocks()) {
					if (previousTextFont == null)
						previousTextFont = block.getTextPositions().get(0).getFont().toString();

					for (int textIndex = 1; textIndex < block.getTextPositions().size(); textIndex++) {
						String currFont = block.getTextPositions().get(textIndex).getFont().toString();

						if (previousTextFont.length() > currFont.length() && previousTextFont.contains(currFont)) {

							if (textIndex == 1) {
								lastBlock.setFontChange(lastBlock.getText().length());
							} else {
								block.setFontChange(textIndex);
							}

							previousTextFont = currFont;
							break;
						}
						previousTextFont = currFont;
					}
					lastBlock = block;
				}

			}
		}

		// To check if font change generalization is working.

		/*
		 * for(Box box : boxes) { // Assume that inside a box, only one type of font is
		 * used. Please make this happen. for(StringBlock block : box.boxBlocks) {
		 * System.out.println(block.text + " " + block.fontChange); }
		 * 
		 * }
		 */

		for (Box box : boxes) {
			int numBoxBlocks = box.getBoxBlocks().size();
			int lineInBox = 0;
			int textCount = 1;
			while (lineInBox < numBoxBlocks) {
				StringBlock currStringBlock = box.getBoxBlocks().get(lineInBox);
				int checker = -1;
				if (classifier == MyConstants.ColonSeparation())
					checker = currStringBlock.hasColon();
				else if (classifier == MyConstants.BoldFont()) {

					checker = currStringBlock.isBold();

				} else if (classifier == MyConstants.FontChange())
					checker = currStringBlock.getFontChange();

				if (checker != -1) {

					// KEY - VALUE in the same line
					if (checker < currStringBlock.getText().length()) {
						String key = currStringBlock.getText().substring(0, checker - offSet).strip();
						String value = currStringBlock.getText().substring(checker).strip();
						keyAndValuePairs.put(key, value);
						lineInBox++;
					}
					// Multiple Lines case
					else {
						String Value = "";

						int lastLineOfValue = lineInBox + 1;
						while (lastLineOfValue < numBoxBlocks) {

							StringBlock nextStringBlock = box.getBoxBlocks().get(lastLineOfValue);
							int checker2 = -1;
							if (classifier == MyConstants.ColonSeparation())
								checker2 = nextStringBlock.hasColon();
							else if (classifier == MyConstants.BoldFont())
								checker2 = nextStringBlock.isBold();
							else if (classifier == MyConstants.FontChange())
								checker2 = nextStringBlock.getFontChange();

							// Now, we add everything to the value string until we encounter a bold
							// StringBlock
							if (checker2 != -1)
								break;
							Value = Value.concat(nextStringBlock.getText() + " ");
							lastLineOfValue++;

						}
						// Those Lines already traversed. Update i to nextLine.
						lineInBox = lastLineOfValue;
						if (Value != "") {

							String Key = currStringBlock.getText().substring(0, currStringBlock.getText().length() - offSet)
									.strip();

							keyAndValuePairs.put(Key, Value);

						}

					}
				}
				// If not classified as a key and value pair.
				else {

					String key = "Text" + textCount;
					textCount++;
					String value = currStringBlock.getText();
					keyAndValuePairs.put(key, value);
					lineInBox++;
				}

			}
		}
	}

}
