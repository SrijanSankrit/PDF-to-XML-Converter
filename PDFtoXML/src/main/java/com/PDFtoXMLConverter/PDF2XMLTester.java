package com.PDFtoXMLConverter;

import java.io.*;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;

public class PDF2XMLTester {

	public static String solver(String fileName, ArrayList<Integer> arr, String pw) throws Exception{

		PDDocument document = null;
		try {
			File file = new File(fileName);
			// loading the document
			if (pw != "") {
				document = PDDocument.load(file, pw);
				document.setAllSecurityToBeRemoved(true);
			} else {
				document = PDDocument.load(file);
			}

			int size = arr.size();
			for (int i = 0; i < size; i++) {
				if (arr.get(i) > document.getNumberOfPages() || arr.get(i) < 1) {
					document.close();
					return "Incorrect no. of pages!";
				}
			}

			PDF2XML PDF2XMLObj = new PDF2XML();
			PDF2XMLObj.convert(document, arr, fileName);

			// Closing the document
			document.close();
			return "XML File is generated!";
		} catch (IOException e) {
			e.printStackTrace();
			document.close();
			return "check file name!";
		}
	}

}
