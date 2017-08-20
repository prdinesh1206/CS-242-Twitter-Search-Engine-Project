package TweetR;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NoLockFactory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


//Index Tweets using Apache Lucene
//Uncomment print statements to see output in console
public class Indexer {

	static final String COMMA="\",\"";
	static final String USER="user";
	static final String TEXT="text";
	static final String DATE="date";
	static final String PROFILE_PIC="profile_picture";
	static final String TWEET_ID="id_str";
	static final String HASHTAGS="hashtags";
	
	
	
	
	public static void main (String[] args) throws IOException{
		
		Indexer ti=new Indexer();
		File indexDir=new File("E:\\IR\\Lucene_Index");
		
		
		if(!indexDir.isDirectory())
		indexDir.mkdir();
		
		int count;
		
		File directory=new File("E:\\IR\\CrawFiles");
		final long start=System.currentTimeMillis();
		
		
		
		count=ti.index(indexDir, directory);

		System.out.println(count);
		System.out.println("Time Taken for the whole process: "+(System.currentTimeMillis()-start));
		
		
	}

	
	private int index(File indexDir,File directory) throws IOException{
		Directory index = null;
 		index=FSDirectory.open(indexDir);
		
		Map<String,Analyzer> analyzerPerField = new HashMap<String,Analyzer>();
		 analyzerPerField.put(HASHTAGS, new WhitespaceAnalyzer());
        PerFieldAnalyzerWrapper aWrapper =
		   new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);
		
        
        IndexWriterConfig iwConf = new IndexWriterConfig(Version.LUCENE_36, aWrapper); 

        iwConf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter iWriter=new IndexWriter(index,iwConf);
        int count = indexFile(iWriter, directory);
        iWriter.close();
        return count;
    }
	private int indexFile(IndexWriter iWriter, File directory) throws IOException{
		
		String hashtagString,inputJSON="",userName,tweetText,tweetDate,inReplyTo,profile_picture,id_str;
		FieldType fieldtype=new FieldType();
		fieldtype.setStored(true); 
		fieldtype.setStoreTermVectors(true);
		fieldtype.setIndexed(true);
		int count=0;
		Iterator iterator;
		Document d;
		JSONParser parser;
		JSONObject entities,obj,userObj;
		JSONArray hashtagArray;
		BufferedReader br;
//		PrintWriter writer;
//		PrintWriter writeCounts=new PrintWriter("TimeTakenforCounts.txt");;
        long start=System.currentTimeMillis(); 
		
		for(File file: directory.listFiles())
		{
		br=new BufferedReader(new FileReader(file));
		while((inputJSON=br.readLine())!=null)
		{
		//System.out.println(count+": "+inputJSON);
		parser=new JSONParser();
		
		try {
			hashtagString="";
			//System.out.println(inputJSON.toString());
			obj = (JSONObject) parser.parse(inputJSON);
			//Obtaining Hashtags
			 entities=(JSONObject) obj.get("entities");
	 		 hashtagArray=(JSONArray)entities.get("hashtags");
			 iterator=hashtagArray.iterator();
			JSONObject hashtags;
			while(iterator.hasNext()){
				hashtags=(JSONObject) iterator.next();
				
				hashtagString+="#"+hashtags.get("text")+" ";
			}
			//System.out.println(hashtagString);
			tweetText=obj.get("text").toString();
			tweetDate=obj.get("created_at").toString();
			//inReplyTo=obj.get("in_reply_to_screen_name").toString();
      
			userObj=(JSONObject) obj.get("user");
            userName=userObj.get("screen_name").toString();
            
            profile_picture=userObj.get("profile_image_url").toString();
			id_str=obj.get("id_str").toString();
            //System.out.println(userName);
            d=new Document();
            
            d.add(new Field(HASHTAGS,hashtagString, fieldtype));
            //System.out.println(hashtagString);
            d.add(new Field(TEXT,tweetText, fieldtype));
            //d.add(new StoredField(TWEET_ID,tweetDate));
            System.out.println(profile_picture);
            d.add(new StoredField(PROFILE_PIC,profile_picture));
            d.add(new StoredField(TWEET_ID,id_str));
            d.add(new StoredField(USER,userName));
            
            
            iWriter.addDocument(d);
			 count++;
			
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		}
		}
		
		
		return count;
	}
}