package TweetR;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import TweetR.Ranking.output;
//Search Tweets using Lucene Index

public class Searcher {

	public static void main(String[] args) throws Exception{
		File indexDir=new File("E:\\IR\\Lucene_Index");
		Searcher s=new Searcher();
		//s.userSearch(indexDir, 100000);
		s.querySearch("hillary");
	}

	private void userSearch(File indexDir, int hits) throws Exception{
		
			Directory dir=FSDirectory.open(indexDir);
			DirectoryReader directoryReader = DirectoryReader.open(dir);
	        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
	        
	        Term term = new Term(Indexer.HASHTAGS, "#hillary");
	        
	        Query q=new TermQuery(term);
	        
	        TopDocs topDocs = indexSearcher.search(q, hits);

	        printResults(topDocs.scoreDocs, indexSearcher);
	        
	}
	public List<Tweet> querySearch(String query){
		String[] queryArray;
		List<Tweet> tweetList=new ArrayList<Tweet>();
		try{
			Directory dir=FSDirectory.open(new File("E:\\IR\\Lucene_Index"));
		DirectoryReader directoryReader=DirectoryReader.open(dir);
		  IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
		  String tweetText,profile_picture,user_name,id_str,sentiment = null;
		  
			queryArray=query.split(" ");
			Term[] terms=new Term[queryArray.length];
			Query[] queries=new Query[queryArray.length];
			BooleanQuery booleanQuery=new BooleanQuery();
			
			for(int i=0;i<queryArray.length;i++){
				if(queryArray[i].startsWith("#")){
					terms[i]=new Term(Indexer.HASHTAGS,queryArray[i]);
					queries[i]=new TermQuery(terms[i]);
					booleanQuery.add(queries[i],BooleanClause.Occur.MUST);
				}
				else {
					terms[i]=new Term(Indexer.TEXT,queryArray[i]);
					queries[i]=new TermQuery(terms[i]);
					booleanQuery.add(queries[i],BooleanClause.Occur.SHOULD);
				} 
			}
			TopDocs topDocs=indexSearcher.search(booleanQuery,100);
			
			System.out.println(topDocs.totalHits);
			ScoreDoc[] results=topDocs.scoreDocs;
			
			for (int i = 0; i < results.length; i++) {
	            int docId = results[i].doc;
	            Document foundDocument = indexSearcher.doc(docId);
	            tweetText=foundDocument.get(Indexer.TEXT);
	            profile_picture=foundDocument.get(Indexer.PROFILE_PIC);
	            user_name=foundDocument.get(Indexer.USER);
	            id_str=foundDocument.get(Indexer.TWEET_ID);
	            sentiment="neutral";
	            System.out.println(foundDocument.get(Indexer.TWEET_ID) + " : " + foundDocument.get(Indexer.PROFILE_PIC));
	            System.out.println("----------------");
	        	Tweet tweet=new Tweet(user_name,profile_picture,tweetText,id_str,sentiment);
				tweetList.add(tweet);
				
	        }
			
		System.out.println(tweetList.size());
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		return tweetList;
	}
		
	   private void printResults(ScoreDoc[] results, IndexSearcher indexSearcher) throws Exception {
	        System.out.println("----------------------------------------------------------------------");
	        for (int i = 0; i < results.length; i++) {
	            int docId = results[i].doc;
	            Document foundDocument = indexSearcher.doc(docId);
	            System.out.println(foundDocument.get(Indexer.USER) + " : " + foundDocument.get(Indexer.TEXT));
	            System.out.println("----------------");
	        }
	        System.out.println("Found " + results.length + " results");
	        System.out.println("----------------------------------------------------------------------");
	    }

}

