package com.PDFtoXMLConverter;

import java.util.*;
//extracts words from the preamble and summary of the document

import org.apache.pdfbox.text.TextPosition;

public class BusinessWordExtractor {

	// takes boxes and string that classifes style of document (colon/bold) as input
	// returns a hashmap of words and their values.
	public HashMap<String, String> extract(List<Box> boxes, String divider) {
		HashMap<String, String> keyAndValuePairs = new HashMap<String, String>();
		int offSet = 0;
		if (divider == MyConstants.ColonSeparation())
			offSet = 1;
		else if (divider == MyConstants.BoldFont() || divider == MyConstants.FontChange())
			offSet = 0;

		// Checks if font name length decreases which marks the start of value.

		if (divider == MyConstants.FontChange()) {
			for (Box box : boxes) { // Assume that inside a box, only one type of font is used. Please make this
									// happen.
				String previousTextFont = null;

				StringBlock lastBlock = null;
				for (StringBlock block : box.getBoxBlocks()) {
					if (previousTextFont == null)
						previousTextFont = block.textPositions.get(0).getFont().toString();

					for (int textIndex = 1; textIndex < block.textPositions.size(); textIndex++) {
						String currFont = block.textPositions.get(textIndex).getFont().toString();

						if (previousTextFont.length() > currFont.length() && previousTextFont.contains(currFont)) {

							if (textIndex == 1) {
								lastBlock.fontChange = lastBlock.text.length();
							} else {
								block.fontChange = textIndex;
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

		// To check if font change generalisation is working.

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
				if (divider == MyConstants.ColonSeparation())
					checker = currStringBlock.hasColon();
				else if (divider == MyConstants.BoldFont()) {

					checker = currStringBlock.isBold();

				} else if (divider == MyConstants.FontChange())
					checker = currStringBlock.fontChange;

				if (checker != -1) {

					// KEY - VALUE in the same line
					if (checker < currStringBlock.text.length()) {
						String key = currStringBlock.text.substring(0, checker - offSet).strip();
						String value = currStringBlock.text.substring(checker).strip();
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
							if (divider == MyConstants.ColonSeparation())
								checker2 = nextStringBlock.hasColon();
							else if (divider == MyConstants.BoldFont())
								checker2 = nextStringBlock.isBold();
							else if (divider == MyConstants.FontChange())
								checker2 = nextStringBlock.fontChange;

							// Now, we add everything to the value string until we encounter a bold
							// StringBlock
							if (checker2 != -1)
								break;
							Value = Value.concat(nextStringBlock.text + " ");
							lastLineOfValue++;

						}
						// Those Lines already traversed. Update i to nextLine.
						lineInBox = lastLineOfValue;
						if (Value != "") {

							String Key = currStringBlock.text.substring(0, currStringBlock.text.length() - offSet)
									.strip();

							keyAndValuePairs.put(Key, Value);

						}

					}
				}
				// If no Bold nor Colon separation.
				else {

					String key = "Text" + textCount;
					textCount++;
					String value = currStringBlock.text;
					keyAndValuePairs.put(key, value);
					lineInBox++;
				}

			}
		}

		return keyAndValuePairs;
	}

}
