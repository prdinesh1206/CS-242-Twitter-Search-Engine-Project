package TweetR;

import twitter4j.*;
import twitter4j.Query.ResultType;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

//Code to query TwitterAPI and obtain tweets using Twitter4j

public class Crawler {

	private  final String CONSUMER_KEY		= "sZlBfSB7JbeQFfbaewmjP4kK7";
	private  final String CONSUMER_SECRET 	= "OuBPCkwzeOek5ZZ3K5rmDZHpaAidSzOJHZP1jvlqi3wDFocgSW";
	private  final int TWEETS_PER_QUERY		= 100;
	private  final int MAX_QUERIES			= 1048;
	private  final String[] SEARCHTERMS ={"#Election2016"};
	
	private String cleanInput(String text){
		text = text.replace("\n", "\\n");
		text = text.replace("\t", "\\t");
		return text;
	}
	public OAuth2Token getOAuth2Token()
	{
		OAuth2Token token = null;
		ConfigurationBuilder configBuilder;
		configBuilder = new ConfigurationBuilder();
		configBuilder.setApplicationOnlyAuthEnabled(true);
		configBuilder.setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(CONSUMER_SECRET);
		try
		{
			token = new TwitterFactory(configBuilder.build()).getInstance().getOAuth2Token();
		}
		catch (Exception e)
		{
			System.out.println("Could not get OAuth2 token");
			e.printStackTrace();
			System.exit(0);
		}
		return token;
	}
	public  Twitter getTwitterFactory()
	{
		OAuth2Token token;

		token = getOAuth2Token();

		ConfigurationBuilder configBuilder = new ConfigurationBuilder();
		configBuilder.setApplicationOnlyAuthEnabled(true);
		configBuilder.setOAuthConsumerKey(CONSUMER_KEY);
		configBuilder.setOAuthConsumerSecret(CONSUMER_SECRET);
		configBuilder.setOAuth2TokenType(token.getTokenType());
		configBuilder.setOAuth2AccessToken(token.getAccessToken());
		configBuilder.setJSONStoreEnabled(true);
		

		return new TwitterFactory(configBuilder.build()).getInstance();
	}
	public void collectTweets(String directoryPath) throws TwitterException, FileNotFoundException, UnsupportedEncodingException, InterruptedException{
		
		int totalTweets=0;
		PrintWriter writer;
		long previousID=-1;
		Twitter twitterFactory=getTwitterFactory();
		String json;
		Query query;
		QueryResult result;
		
		File directory=new File(directoryPath);
		if(!directory.isDirectory())
		directory.mkdir();
		RateLimitStatus searchTweetsRateLimit;
		Map<String, RateLimitStatus> rateLimitStatus;
		for(String searchTerm: SEARCHTERMS){
			System.out.println("Collecting tweets with search term: "+searchTerm);
			rateLimitStatus = twitterFactory.getRateLimitStatus("search");
			searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");
			System.out.printf("You have %d calls remaining out of %d, Limit resets in %d seconds\n",
					  searchTweetsRateLimit.getRemaining(),
					  searchTweetsRateLimit.getLimit(),
					  searchTweetsRateLimit.getSecondsUntilReset());
			previousID=-1;
			
			for(int queryNumber=0;queryNumber<MAX_QUERIES;queryNumber++){
	            	System.out.println("Running Loop: "+queryNumber);
//				 rateLimitStatus = twitterFactory.getRateLimitStatus("search");
//				 searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");
            	writer = new PrintWriter(directory+"\\"+searchTerm+"_"+queryNumber+".txt", "UTF-8");
	            	
	            	if(searchTweetsRateLimit.getRemaining()==0){
	            		System.out.println("Rate limit exceeded. Sleeping for "+searchTweetsRateLimit.getSecondsUntilReset());
	            		Thread.sleep((searchTweetsRateLimit.getSecondsUntilReset()+2) * 1000l);
	            	}
	            	query=new Query(searchTerm);
	            	query.setCount(TWEETS_PER_QUERY);
	            	query.setLang("en");
	            	if(previousID!=-1)
	            		query.setMaxId(previousID-1);
	            	result=twitterFactory.search(query);
	            	if(result.getTweets().size()==0)
	            		break;
	            	for(Status status: result.getTweets()){
	            		totalTweets++;
	            		if(previousID==-1||previousID> status.getId())
	            			previousID=status.getId();
	            		
	            		String outputTweet="At "+ status.getCreatedAt().toString()+","+status.getUser().getScreenName()+" said: "+cleanInput(status.getText()+" "+status.getId());
	            		//System.out.println(outputTweet);
	            		json=DataObjectFactory.getRawJSON(status);
	            		writer.println(json);
	            	}
	            	searchTweetsRateLimit=result.getRateLimitStatus();
	            	writer.close();
	        }
			
		}
		System.out.println("Total tweets collected: "+totalTweets);
	}
	public static void main(String[] args){
		
		Crawler crawler=new Crawler();
		
		try {
			crawler.collectTweets(args[0]);
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		} catch (TwitterException e) {
			
			e.printStackTrace();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}
}
