package com.PDFtoXMLConverter;

import java.util.*;
import java.io.IOException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/**
 * Class that extends PDFTextStripper provided by PDFBox Library. Processes text in a PDF
 * @author BHAVYA SHARMA, SRIJAN SANKRIT
 * 
 */


public class myPDFTextStripper extends PDFTextStripper {
	private List<StringBlock> blocks ;

	/**
	 * Constructor
	 * 
	 */
	public myPDFTextStripper() throws IOException {
		super();
		blocks = new ArrayList<StringBlock>();
	}

	@Override
	protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
		StringBlock block = new StringBlock(); // creates a block with given textPositions
		block.setText(text);
		this.setTextPositions(textPositions, block);
		this.getBlocks().add(block);

		this.writeString(text);
	}

	/**
	 * Method that stores positions of incoming block in block object and saves block
	 * @author BHAVYA SHARMA
	 * 
	 */
	public void setTextPositions(List<TextPosition> textPositions, StringBlock block) {
		
		block.setTextPositions(textPositions);
		block.setStartX(textPositions.get(0).getX());

		int size = textPositions.size();
		block.setEndX(textPositions.get(size - 1).getEndX());
		block.setWidth(block.getEndX() - block.getStartX());

		float min = textPositions.get(0).getY();
		float max = textPositions.get(0).getHeight();
		for (int i = 1; i < size; i++) {

			float startY = textPositions.get(i).getY();
			float height = textPositions.get(i).getHeight();
			if (startY < min) {
				min = startY;
			}
			if (height > max) {
				max = height;
			}
		}

		block.setStartY(min);
		block.setEndY(block.getStartY() + max);
		block.setHeight(max);

	}

	/**
	 * Method that returns list of blocks
	 * @author BHAVYA SHARMA
	 * @return list of string blocks in PDF
	 * 
	 */
	public List<StringBlock> getTextPositions() {
		return this.getBlocks();
	}

	/**
	 * Method that groups string blocks in the same line together
	 * @author BHAVYA SHARMA
	 * @return List of lines in PDF
	 * 
	 */
	public List<PDFLine> createLines() { 
		Collections.sort(this.getBlocks()); // first, sort all the blocks based on increasing Y
		int size = this.getBlocks().size();
		List<PDFLine> lines = new ArrayList<PDFLine>();

		int i = 1;

		PDFLine line = new PDFLine();

		StringBlock block = this.getBlocks().get(0);
		line.setStartY(block.getStartY());
		line.setEndY(block.getEndY());
		line.getLineBlocks().add(block);
		lines.add(line);
		while (i < size) {
			block = this.getBlocks().get(i);
			PDFLine currLine = lines.get(lines.size() - 1);
			/*
			 * if the block starts before the previous block ends or starts within a gap of
			 * 2 after previous block ends, group it in the same line
			 */
			if (block.getStartY() >= currLine.getStartY() && block.getStartY() <= currLine.getEndY() + MyConstants.mergeLinesDistance) {
				currLine.getLineBlocks().add(block);
				currLine.setEndY(Math.max(currLine.getEndY(), block.getEndY()));
			}
			// else create a new line
			else {
				line = new PDFLine();
				line.getLineBlocks().add(block);
				line.setStartY(block.getStartY());
				line.setEndY(block.getEndY());
				lines.add(line);
			}
			i++;
		}

		int numLines = lines.size();
		for (i = 0; i < numLines; i++) {
			PDFLine tempLine = lines.get(i);
			// sort the blocks in a line based on increasing X
			lines.remove(i);
			tempLine.sort();
			lines.add(i, tempLine);

		}

		return lines;
	}
	
	/**
	 * Method that creates boxes out of lines 
	 * @author SRIJAN SANKRIT
	 * 
	 */

	public List<Box> createBoxes(List<PDFLine> lines) {
		int i = 0;
		List<Box> boxes = new ArrayList<Box>();
		for (PDFLine line : lines) {
			for (StringBlock block : line.getLineBlocks()) {
				// using concept that startX border of the text may be inside column or endX
				// border maybe inside column

				Boolean added = false; // Checks if word is added or not.

				for (Box box : boxes) {
					// Checks if the word belongs in any column horizontally.
					if ((box.getStartX() <= (int) block.getStartX() && (int) block.getStartX() <= box.getEndX())
							|| (box.getStartX() <= (int) block.getEndX() && (int) block.getEndX() <= box.getEndX())) {

						// We check the vertical distance now. I assume a distance of 20. If vertical
						// distance is > 20, that is a new column.
						if (line.getStartY() < box.getEndY() + MyConstants.maxGapBetweenBoxes) {
							box.getBoxBlocks().add(block);
							box.setEndY((int) line.getEndY());
							if ((int) block.getStartX() < box.getStartX()) {
								box.setStartX((int) block.getStartX());
							}
							if ((int) block.getEndX() > box.getEndX()) {
								box.setEndX((int) block.getEndX());
							}
							added = true;
							break;
						}
					}
				}

				// If no column matches, make a new Column with all 4 boundaries.

				if (added == false) {
					Box newBox = new Box((int) line.getStartY(), (int) line.getEndY(), (int) block.getStartX(), (int) block.getEndX());
					newBox.getBoxBlocks().add(block);
					newBox.setBoxId(i++);
					boxes.add(newBox);

				}
			}
		}
		return boxes;
	}

	/**
	 * Method that classifies separator that box contains - colon, bold or font change
	 * @author SRIJAN SANKRIT
	 * @param boxes List of boxes to be classified
	 * @return String that tells which style is used
	 * 
	 */
	public String classifyStyle(List<Box> boxes) {
		// We can check if a colon is used to differentiate key and value OR bold keys
		// and words.
		// Having colons is given higher Priority. :)
		// Most of the PDFs can be classified in these cases.

		// Steps -->
		// Find wordCount in PDF O(words in PDF)
		// Find colonCount O(Words in PDF)
		// Find BoldWords O(words in PDF)

		int blockCount = 0;
		int boldCount = 0;
		int colonCount = 0;

		for (Box box : boxes) {
			for (StringBlock block : box.getBoxBlocks()) {
				blockCount++;
				if (block.hasColon() != -1) {
					colonCount++;
				}
				if (block.isBold() != -1) {
					boldCount++;
				}
			}
		}

		// If colonCount is 15% of wordCount, use colon to extract data.
		if ((int) (blockCount * MyConstants.colonPercentage) <= colonCount) {
			return "COLON";
		} else if ((int) (blockCount * MyConstants.boldPercentage) <= boldCount) {
			return "BOLD FONT";
		} else {
			return "FONT CHANGE";
		}
	}

	
	//Getters and Setters
	
	List<StringBlock> getBlocks() {
		return blocks;
	}

	void setBlocks(List<StringBlock> blocks) {
		this.blocks = blocks;
	}

}
