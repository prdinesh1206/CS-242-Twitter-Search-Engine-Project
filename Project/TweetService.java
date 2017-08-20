package TweetR;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//Get all tweets which are indexed by hadoop
@Path("/TweetService")
public class TweetService {

   TweetDao tweetDao = new TweetDao();

   @GET
   @Path("/tweets/{query}")
   @Produces(MediaType.APPLICATION_XML)
   public List<Tweet> getTweets(@PathParam("query") String query){
      return tweetDao.getAllTweets1(query);
   }
}
