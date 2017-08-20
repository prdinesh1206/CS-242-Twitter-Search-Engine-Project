package TweetR;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
//Rank the tweets using BM25 model
//Uncomment print statements to see results in console
public class Ranking {
	
	Ranking()
	{
		System.out.println("Started Ranking");
	}
	 class output{
		
		double rank=0.0;
		String s=null;
		String sentiment = null,tweetText,profile_picture,user_name,id_str;
		File directoryPath;
		public output(double rank,String s,String sentiment,File directoryPath,String tweetText,String profile_picture,String user_name,String id_str)
		{
			this.rank=rank;
			this.s=s;
			this.directoryPath=directoryPath;
			this.sentiment= sentiment;
			this.tweetText=tweetText;
			this.profile_picture=profile_picture;
			this.user_name=user_name;
			this.id_str=id_str;
		}
	}
	
	public HashMap<String, HashMap<String, Integer>> Hashing() throws NumberFormatException, IOException
	{
	
		 FileInputStream fstream = new FileInputStream("E:\\IR\\outputFinal\\output.txt");
		 DataInputStream in = new DataInputStream(fstream);
		 BufferedReader br = new BufferedReader(new InputStreamReader(in));
		 String strline;
		
		 HashMap<String, HashMap<String, Integer>> search = new HashMap<String, HashMap<String, Integer>>();

	 while ((strline = br.readLine()) != null)
		 {
		 //System.out.println(strline+" ");
			 String[] a = strline.split("[ \t]+");
			// System.out.print(a.length+"  ");
			 
			// System.out.print("the a[0] is"+a[0]+" ");
			 HashMap<String, Integer> ding = new HashMap<String, Integer>();
			 for(int i=1;i<a.length;i++)
			 {
				// System.out.print(a[i]+" ");
				 String n = a[i].replaceAll("[{,]",""); 
				 String p= n.replace("}=1", "");
				 String d = p.replaceAll("[}]","");
				 //System.out.println(d+"         ");
				String[] b = d.split("=");
				 
			//	 for(int i1=0;i1<b.length;i1++)
				// System.out.print(b[0]+"     "+b[1]);
				 ding.put(b[0], Integer.parseInt(b[1]));
			 }
			 search.put(a[0], ding);
			 //System.out.println("");
		}
	 return search;
	}
	
	public PriorityQueue<output> Rank(HashMap<String, HashMap<String, Integer>> search, String s) {
		// TODO Auto-generated method stub
		JSONParser parser;
		String inputJSON = null;
		JSONObject entities,obj = null,userObj;
		JSONArray hashtagArray;
		String directory1="E:\\IR\\CrawFiles";
		
		PriorityQueue<output> pq = new PriorityQueue<output>(1000000, new Comparator<output>() {
			
			
			@Override
			public int compare(output o1, output o2) {
				// TODO Auto-generated method stub
				if (o1.rank > o2.rank) return -1;
		        if (o1.equals(o2)) return 0;
		        return +1;
				
			}
		});
		HashMap<String, Double> list =new HashMap<String, Double>();
		HashMap<String, Integer> df =new HashMap<String, Integer>();
		HashMap<String, Integer> qf =new HashMap<String, Integer>();
		int N= 1000;
	double rank=0;
		String[] out = s.split("[ \t]+");
		for(int i=0;i<out.length;i++)
		{
			qf.put(out[i], 0);
		}
		for(int i=0;i<out.length;i++)
		{
			int p=qf.get(out[i]);
			qf.put(out[i], p+1);
	//		System.out.println("Term "+ out[i]+"value "+ qf.get(out[i]));
			if(search.get(out[i])==null)
				rank = rank+0.001;
			else if (search.get(out[i])!=null)
			{
				df.put(out[i], search.get(out[i]).size());
				HashMap<String, Integer> temp = search.get(out[i]);
			for(String key: temp.keySet())
			{
				list.put(key,0.0);
			}
			}		
		}
		
		for(String key:list.keySet())
		{
			rank=0;
			//System.out.println(key);	
			
			for(int i=0;i<out.length;i++)
			{
				//System.out.println(out[i]);
				//System.out.println(search.get(out[i]).get(key));
				if(search.get(out[i])!=null&&search.get(out[i]).get(key)!=null)
				{
					int tf = search.get(out[i]).get(key);
					//System.out.println("the value of tf"+tf);
					int Df = df.get(out[i]);
					//System.out.println("The value of df"+Df);
					double dlavdl =0.9;
					double k1 =1.2;
					double k2 = 100;
					double k = 1.11;
					int q =qf.get(out[i]) ;
					//System.out.println("the value of q "+q);
					double m =Math.log((N-Df+0.5)/(Df+0.5));
					//System.out.println(m);
					double g=Math.abs((Math.log((N-Df+0.5)/(Df+0.5))*(k1+1)*tf*(k2+1)*q)/((k+tf)*(k2+q)));
					
					rank = rank+g;
					//System.out.println(rank+" "+"the value of g "+ g);
					list.put(key, rank);
				}
				else{
					rank=rank+0.001;
					list.put(key, rank);
				}
			}
		}
		NLP.init();
		String sentiment,tweetText;
		File directoryPath;
		for(String key:list.keySet())
		{
			
			if(new File(directory1+"\\"+key+".txt").exists()){
				
				directoryPath=new File(directory1+"\\"+key+".txt");
			}
			
			else
				continue;
			parser=new JSONParser();
			try {
				inputJSON=new BufferedReader(new FileReader(directoryPath)).readLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(inputJSON==null)
				continue;
			
			
			try {
				obj = (JSONObject) parser.parse(inputJSON);
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String TweetText=	obj.get("text").toString();
			userObj=(JSONObject) obj.get("user");
			String User_name=userObj.get("screen_name").toString();
			String Profile_picture=userObj.get("profile_image_url").toString();
			String Id_str=obj.get("id_str").toString();
			double d = list.get(key);
			
			
			
			//Add tweet text instead of key
			int v =NLP.findSentiment(TweetText);
			if(v==1)
				sentiment="negative";
			else if(v==2)
				sentiment="neutral";
			else
				sentiment="positive";
			//key
			output a = new output(d,key,sentiment,directoryPath,TweetText,Profile_picture,User_name,Id_str);
			pq.add(a);
		}
		
//		while(!pq.isEmpty())
//		{
//			output b = pq.poll();
//			System.out.println("the text file is  "+ b.s+"  the rank is  "+ b.rank + "sentiment is "+ b.sentiment);
//		}
		return pq;
	 
	}

	public static void main(String[] args) throws NumberFormatException, IOException  {
		// TODO Auto-generated method stub
	Ranking a = new Ranking();
	HashMap<String, HashMap<String, Integer>> search=a.Hashing();
String s= new String("#hillary");
a.Rank(search, s);
//NLP.init();
//String tweet = "Mihir is a boy.";
//System.out.println(tweet + " : " + NLP.findSentiment(tweet));
		 }
	}


