package TweetR;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
//Details of a single Tweet
@XmlRootElement(name = "user")
public class Tweet implements Serializable{

	private static final long serialVersionUID = 1L;
	private String user_name,profile_picture,tweet_text,tweet_id,sentiment;
	public Tweet(){}
	public Tweet(String user_name,String profile_picture,String tweet_text,String tweet_id,String sentiment){
		this.user_name=user_name;
		this.profile_picture=profile_picture;
		this.tweet_text=tweet_text;
		this.tweet_id=tweet_id;
		this.sentiment=sentiment;
		
	}
	public String getSentiment() {
		return sentiment;
	}
	@XmlElement
	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}
	public String getTweet_id() {
		return tweet_id;
	}
	@XmlElement
	public void setTweet_id(String tweet_id) {
		this.tweet_id = tweet_id;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getUser_name() {
		return user_name;
	}
	@XmlElement
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getProfile_picture() {
		return profile_picture;
	}
	@XmlElement
	public void setProfile_picture(String profile_picture) {
		this.profile_picture = profile_picture;
	}
	public String getTweet_text() {
		return tweet_text;
	}
	@XmlElement
	public void setTweet_text(String tweet_text) {
		this.tweet_text = tweet_text;
	}
	
	
	
	
}
