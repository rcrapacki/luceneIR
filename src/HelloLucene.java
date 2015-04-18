import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;

public class HelloLucene {
	private final static String CONTENTS="contents";
    private static CharArraySet stopSet;
	private final static String DOCUMENT_PATH = "C:\\Users\\raul.barth\\Downloads\\efe94\\efe2\\";
	private final static String QUERY_PATH = "C:\\Users\\raul.barth\\Downloads\\Consultas\\Consultas.txt";
	private final static String OUTPUT_PATH = "C:\\Users\\raul.barth\\Downloads\\LuceneResult.txt";
	
	public static void main(String[] args) throws IOException, ParseException {
            
        creatStopWordsList();
                                       
	    // 0. Specify the analyzer for tokenizing text.
	    //    The same analyzer should be used for indexing and searching
	    SpanishAnalyzer analyzer = new SpanishAnalyzer();
	
	    // 1. create the index
	    Directory index = new RAMDirectory();
	
	    IndexWriterConfig config = new IndexWriterConfig( analyzer);
	
	    IndexWriter w = new IndexWriter(index, config);
	    DocumentParser docParser = new DocumentParser();
	    addDocCollection(w, docParser);    
	    w.close();
	    
	    File queryFile = new File(QUERY_PATH); 
	    List<TextQuery> queries = docParser.parseQueries(queryFile);
	
	    // 2. query
	    //String queryStr = args.length > 0 ? args[0] : "Copa";
	    
	    IndexReader reader = DirectoryReader.open(index);
	    IndexSearcher searcher = new IndexSearcher(reader);
	    
	    int queryCount = 1;
	    
	    Writer fileWriter = null;
	    for (TextQuery query: queries) {
		    int hitsPerPage = 100;
		    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
		    
            String queryStr = tokenizeText(query.getESTitle());
                                 
            // the "title" arg specifies the default field to use
		    // when no field is explicitly specified in the query.
		    Query q = new QueryParser("text", analyzer).parse(queryStr);
		
		    // 3. search

		    searcher.search(q, collector);
		    ScoreDoc[] hits = collector.topDocs().scoreDocs;
		   
		    // 4. display results
			  try {
				  fileWriter = new BufferedWriter(new OutputStreamWriter(
			            new FileOutputStream(OUTPUT_PATH), "utf-8"));
			  } catch (IOException ex) {
			    // report
			  }	    	    
			fileWriter.append("\n\n"+"Found " + hits.length + " hits in " + query.getNum());
		    
			System.out.println("Found " + hits.length + " hits in " + query.getNum() );	    
		    for(int i=0;i<hits.length;++i) {
		      int docId = hits[i].doc;
		      float docScore = hits[i].score;
		      
		      Document d = searcher.doc(docId);
		      System.out.println(queryCount +
		    		  "	Q0	" +
		    		  d.get("docno") +
		    		  "	" +
		    		  i + 
		    		  "	" +
		    		  docScore +
		    		  " ricardo e raul");
		      
		      fileWriter.append("\n"+queryCount +
		    		  "	Q0	" +
		    		  d.get("docno") +
		    		  "	" +
		    		  i + 
		    		  "	" +
		    		  docScore +
		    		  " ricardo e raul");
		    }
		    queryCount++;
	    }
	
	    // reader can only be closed when there
	    // is no need to access the documents any more.
	    reader.close();
	    fileWriter.close();
  }

  private static void addDocCollection(IndexWriter w, DocumentParser docParser) throws IOException {    
    File dir = new File(DOCUMENT_PATH); 
    File[] files = dir.listFiles();
	for (File file : files) {		
		for (SaxDocument saxDocument : docParser.parseDocument(file)) {
			Document document = new Document();

			String path = file.getCanonicalPath();
		    // use a string field for path because we don't want it tokenized
			document.add(new StringField("path", path, Field.Store.YES));
			
			document.add(new TextField("text", tokenizeText(saxDocument.getText()), Field.Store.YES));                      
 
			document.add(new TextField("title", saxDocument.getTitle(), Field.Store.YES));
			document.add(new TextField("docno", saxDocument.getDocNo(), Field.Store.YES));

			w.addDocument(document);
		}
	}
  }
  
  //Tokenizing and filtering stopwords
  private static String tokenizeText(String text) throws IOException{
	  String tokenizedText = "";
	  Analyzer analyzer = new SpanishAnalyzer();
	  
	  //System.out.println(text); //testar a saída da string
	  
	  TokenStream tokenStream   = analyzer.tokenStream(CONTENTS, text);  
	  
	  tokenStream = new StopFilter(tokenStream, stopSet); // Stopwords
	  tokenStream = new PorterStemFilter(tokenStream); // Steaming            
	  //display string tokenized without stopwords    
	  
	  tokenStream.reset();
	  
	  CharTermAttribute cattr = tokenStream.addAttribute(CharTermAttribute.class);
	  while (tokenStream.incrementToken()) {
		  tokenizedText += cattr.toString() + " ";
	      //System.out.println(cattr.toString());
	  }
	  
	  tokenStream.end();
	  tokenStream.close();
	  return tokenizedText;
  }
  
  private static void creatStopWordsList(){
      stopSet = CharArraySet.copy(SpanishAnalyzer.getDefaultStopSet());
  }
  

}