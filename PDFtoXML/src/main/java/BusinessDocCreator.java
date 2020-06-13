import java.io.IOException;
import java.util.ArrayList;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;


//creates an invoice document using its check and create function
//if the document isn't of said type, returns null

public class BusinessDocCreator {

	public BusinessDoc create(PDPage doc, int pageNos) throws IOException 
	{
		BusinessDoc bDoc = InvoiceDocument.checkNcreate(doc, pageNos);
		return bDoc ;		
	}	
	
}
