import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class DocumentParser {
	
	
	public DocumentParser() {
		
	}
	
	public List<SaxDocument> parseDocument(File file) throws IOException {
		Document parsedSgml = Jsoup.parse(file, "ISO-8859-1");
		List<SaxDocument> documentList = new ArrayList<SaxDocument>();
	    
	    int count = 0;
	    
	    Elements content = parsedSgml.getElementsByTag("DOC");
	    for (Element doc : content) {
	    	Elements docIdElements = doc.getElementsByTag("DOCID");
	    	Elements docNoElements = doc.getElementsByTag("DOCNO");
	    	Elements textElements = doc.getElementsByTag("TEXT");
	    	Elements titleElements = doc.getElementsByTag("TITLE");
	    	
	    	if (docIdElements.size() != 0 && textElements.size() != 0) {
	    		SaxDocument document = new SaxDocument();
		    	document.setDocId(docIdElements.get(0).text());
		    	document.setDocNo(docNoElements.get(0).text());
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
	    return documentList;
	}
	
	public List<TextQuery> parseQueries(File file) throws IOException {
		Document parsedSgml = Jsoup.parse(file, "ISO-8859-1");
		List<TextQuery> queryList = new ArrayList<TextQuery>();
	    
	    int count = 0;
	    
	    Elements content = parsedSgml.getElementsByTag("top");
	    for (Element doc : content) {
	    	Elements numElements = doc.getElementsByTag("num");
	    	Elements ESTitleElements = doc.getElementsByTag("ES-title");
	    	Elements ESDescElements = doc.getElementsByTag("ES-desc");
	    	Elements ESNarrElements = doc.getElementsByTag("ES-narr");
	    	
	    	if (numElements.size() != 0 && ESTitleElements.size() != 0) {
	    		TextQuery query = new TextQuery();
	    		query.setNum(numElements.get(0).text());
		    	query.setESTitle(ESTitleElements.get(0).text());
		    	query.setESDesc(ESDescElements.get(0).text());
		    	query.setESNarr(ESNarrElements.get(0).text());
		    	queryList.add(query);
		    	count++;
	    	} else {
	    		System.out.println("doc number is " + count);
	    		System.out.println(file.getCanonicalPath());
	    		count++;
	    	}
	    }
	    return queryList;
	}
}