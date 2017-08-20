package TweetR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import TweetR.Ranking.output;
//Dao service to obtain the tweets that are indexed by Hadoop
public class TweetDao {

	JSONParser parser;
	JSONObject entities,obj,userObj;
	JSONArray hashtagArray;
	BufferedReader br;
	File directory,directory1,directory2,directory3,file;
	String inputJSON;
	String[] outputJSON;
	Ranking a;
	HashMap<String, HashMap<String, Integer>> search;
	int i;
	public TweetDao(){
		
	}
	public List<Tweet> getAllTweets1(String query){

		
		List<Tweet> tweetList=null;
		directory1=new File("D:\\1"); //Repository of the stored tweets
		directory2=new File("D:\\IR Project\\Crawled1"); //Repository of the stored tweets
		directory3=new File("D:\\IR Project\\Crawled1"); //Repository of the stored tweets
		parser=new JSONParser();
		tweetList=new ArrayList<Tweet>();
		File file;
		PriorityQueue<output> outputObjectQueue;
		output outputObject;
		try{
			System.out.println("Searching: "+query);
			a = new Ranking();
			search=a.Hashing();
			String s= new String(query);
			
			
			outputObjectQueue=a.Rank(search, s);
			System.out.println(outputObjectQueue.isEmpty());
			while(!outputObjectQueue.isEmpty()){
				outputObject=outputObjectQueue.poll();
				String tweetText,profile_picture,user_name,id_str,sentiment = null;
				sentiment=outputObject.sentiment;
				user_name=outputObject.user_name;
				tweetText=outputObject.tweetText;
				profile_picture=outputObject.profile_picture;
				id_str=outputObject.id_str;
				Tweet tweet=new Tweet(user_name,profile_picture,tweetText,id_str,sentiment);
				tweetList.add(tweet);
			}	
		 System.out.println(tweetList.size());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return tweetList; 
			
		
	}


	
	}

