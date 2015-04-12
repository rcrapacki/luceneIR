import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class DocumentParser {
	List<SaxDocument> documentList;
	
	public DocumentParser(File file) throws IOException {
		Document parsedSgml = Jsoup.parse(file, "UTF-8");
	    documentList = new ArrayList<SaxDocument>();
	    
	    int count = 0;
	    
	    Elements content = parsedSgml.getElementsByTag("DOC");
	    for (Element doc : content) {
	    	Elements docIdElements = doc.getElementsByTag("DOCID");
	    	Elements textElements = doc.getElementsByTag("TEXT");
	    	Elements titleElements = doc.getElementsByTag("TITLE");
	    	
	    	if (docIdElements.size() != 0 && textElements.size() != 0) {
	    		SaxDocument document = new SaxDocument();
		    	document.setDocId(docIdElements.get(0).text());
		    	document.setText(textElements.get(0).text());
		    	document.setTitle(titleElements.get(0).text());
		    	documentList.add(document);
		    	count++;
	    	} else {
	    		System.out.println("doc number is " + count);
	    		System.out.println(file.getCanonicalPath());
	    		count++;
	    	}
	    }
	}
	
	public List<SaxDocument> getDocumentList() {
		return documentList;
	}
}