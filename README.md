# CS-242-Twitter-Search-Engine-Project
Twitter: Our goal was to build a search engine for tweets
Crawled twitter using Twitter API and obtained tweets in JSON format

We first used lucene and then Hadoop, map reduce algorithm to index the tweets.

Built a lucene index on the tweets..
Lucene index will have fields such as User, Date, tweet text and hashtags.
Different analysers for different fields --> analysers are used to tokenize the field in differnt ways

Used Map reduce for  indexing the tweets
Each tweet is stored in a single file, with tweet_id as the file name
Map function-- (key,value): (word, tweet_file_name)
Reduce function -- (word, List<tweet_file_name>)

Rank the tweets using BM25 model
Used Stanford NLP API to perform sentiment analysis. by the words in the tweet it determines sentiment of the tweet
