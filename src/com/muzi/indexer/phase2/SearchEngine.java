package com.muzi.indexer.phase2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.TreeMap;

import com.muzi.indexer.Stemmer;
import com.muzi.indexer.Utils;

public class SearchEngine {

	static int a  = 3;
	
	static BufferedReader br;
	//static ArrayList postingList;
	static PostingStruct PS0;
	static HashMap<String, HashMap<String, PostingStruct> >word_Hash;
	static HashMap<String,Integer> id_Hash;
	static HashMap<String, String>postHash =  new HashMap<>(); // contains id,posting
	static HashMap<String, Integer>scoreHash;
	//static TreeMap<String, List<Integer>> bac;
	static HashMap<String, String>fieldHash;
	static HashMap<String, HashMap<String, String>>fieldWordHash =new HashMap<>();;
	static HashMap<String,Integer> fieldIdHash =  new HashMap<>();
	static HashMap<String, Integer>fieldWordDoc = new HashMap<>();
	static HashMap<String, PostingStruct>postStr;
	static int TOP_K = 10; 
	static Set<String> fieldWords = new HashSet<String>();
	//static private Pattern p = Pattern.compile();
	static int TOTAL_PAGES = 75311;
	static {
		System.out.println("hi");
		
		// initailization let it take 2 min
		// read the secondary index into hashmap for all the twoChar files
		
		
		// load secondary titles index in TreeMap<Integer, Integer>
		// treemap.Lower(3000000);
		
		// all this inmemory should be <800MB
	}
	
	
	public static void main(String[] args) {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String query;
		try {

			System.out.print("> ");
			while( (query = br.readLine()) != null) {
				System.out.println("your Query : " + query);
				if(query.equals("q")) break;
				
				processQuery(query);

				System.out.print("> ");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	enum QueryType  {
		SINGLE_WORD, MULTI_WORD, FIELD_QUERY
	}
	
	
	
	private static void processQuery(String query) throws IOException {
		
		QueryType queryType = getQueryType(query); 
		switch(queryType) {
			case SINGLE_WORD: 
				
				// plain word
//				Stemmer ST1 = new Stemmer();
				GetPostingList GPL0 = new GetPostingList();
				String stemmedQuery = Utils.getStem(query);
				String postings = GPL0.getPostingList(stemmedQuery);
				
				if(postings ==null)
					break;
				ArrayList<String>ids = new ArrayList<>();
				ids = SingleWordQueryProcessing(postings);
				// search in your title
				GetTitles gt = new GetTitles();
				for(String id : ids){
					
					String title = 	gt.getTitleList(id);
					System.out.println("t: " + title);
				}
				// stem word
//				stemmedPostings
				
				
				// extract first 10 entries
//				printResults( list of 10 doc ids);
				break;
				
			case MULTI_WORD: 
				
//				for each word in query
				// with stem and without stemming
				
				// This is complex part
				//
				//1. get posting list
			 
				String [] queryList = query.split(" ");
				MultiWordQueryProcessing(queryList);
					
				
				break;
			case FIELD_QUERY:
				System.out.println("** In field Query ***");
				processFieldQuery(query);
				//getPostingListField("title", word);
				
				break;
		}
		
		
		
		
		// Output : print 10 titles
		
	}
	
	

	private static ArrayList<String> SingleWordQueryProcessing(String postings) {
		int k;
		String id;
		ArrayList<String>ids = new ArrayList<>();
		for(int  i= 0; i <postings.length();i++){
			if( Character.isLetter (postings.charAt(i)) )
				continue;
			k =i;
			while(k < postings.length() && Character.isDigit(postings.charAt(k)) ){
				k++;
			}
			id = postings.substring(i,k);
			if(postings.charAt(i-1) == 'd'){
				ids.add(id);
			}
		}
		return ids;
	}



	private static void processFieldQuery(String query) throws IOException {
		//re.sub(r' ([a-zA-Z]:)', r'\t\1', q).split('\t')
		Pattern pattern = Pattern.compile(" ([a-zA-Z]:)");
		Matcher matcher = pattern.matcher(query);
		String[] str = matcher.replaceAll("\t$1").split("\t");
		
		///HashMap<Integer,HashMap<String, HashMap<String, String>>>total = new HashMap<>();
		HashMap<String, HashMap<String, String>>total = new HashMap<>();
		int hk = 0;
		
		for(String indvField : str){
			HashMap<String,HashMap <String, String>>ret = new HashMap<>();
			ret = processSingleField(indvField); // ret will contain those id:posting
			//System.out.println("i: " + hk + " " + ret.size());
			total = ret;
			///total.put(hk,ret);//indvfiled no + ret + word +id + posting
			hk++;
		}
		
		HashMap<String, Integer>bac = new HashMap<>();
		bac = sortByValues(fieldIdHash);
		int count= 0; // count to get top_k
		scoreHash = new HashMap<>();
		int max_size = hk;
	//	System.out.println("max: " + max_size);
		///for(int i = 0; i< hk; i++){
		for(Map.Entry<String,Integer> entry : bac.entrySet()) {
			String key = entry.getKey();// key contains id
			int score =0; // calculte score
			count++;
		
			/*if(count > TOP_K || count > bac.size()){
				if(max_size == bac.get(key))
					continue;
				else
					break;
			}*/
			int thk =0;
			//System.out.println("fw: " + fieldWords.size());
			//word id posting
			for(String q : fieldWords){	
				System.out.println("s: " + q);
				System.out.println("f: " + fieldWordDoc.get(q));
				int dinominator =fieldWordDoc.get(q);
				//System.out.println("d: "  +dinominator);
				///HashMap<String, HashMap<String, String>>t = total.get(q);
				HashMap<String, String>t = total.get(q);
				///HashMap<>
				
				
				/*for(Map.Entry<String,String> entry1 : t.entrySet()) {
					  String k = entry1.getKey();
					  String value = entry1.getValue();

					  System.out.println(k + " => " + value);
					}*/
				
			
				String pt = t.get(key);//posting
				if(pt == null)
					continue;
				//System.out.println("pt: " + pt);
				if(pt == null)
					continue;
				double idf = Math.log(TOTAL_PAGES/dinominator);
				GetTfIdf sc = new GetTfIdf(pt, idf);
				score += sc.tfidf;//score calculated for each posting wrt words
			}
			scoreHash.put(key,score);
			//sort wrt score
			}
		///}
		scoreHash = sortByValues(scoreHash);
		getTopKtitles(scoreHash);
		
		
		
		
	}



	private static HashMap<String, HashMap<String,String>> processSingleField(String indvField) throws IOException {
		
		char field = indvField.charAt(0); 
		String query = indvField.substring(2,indvField.length());
		System.out.println("i: " + indvField);
		//fieldWords.add(query);
		//System.out.println("query: " + Query + " " + field);
		QueryType queryType = getQueryType(query);
		switch(queryType) {
		
		case SINGLE_WORD: 
			
			// plain word
			System.out.println("Single Word in Field");
//			Stemmer ST3 = new Stemmer();
			String stemmedQuery = Utils.getStem(query);
			fieldWords.add(stemmedQuery);
			GetPostingList GPL0 = new GetPostingList();
			String p = GPL0.getPostingList(stemmedQuery);
			fieldWordDoc.put(stemmedQuery,p.split(";").length);
			HashMap<String,PostingStruct>tmp  = new HashMap<>();
			tmp = createHashofPostStr(p,stemmedQuery,fieldIdHash);
			
			HashMap<String,HashMap< String, String>>ret = new HashMap<>();//id:postings
			HashMap<String,String>tt = new HashMap<>();
			tt = traversePostingList(tmp,stemmedQuery,field);
			
			if(p ==null)
				break;
			ret.put(stemmedQuery, tt);
			return ret;
			
			
		case MULTI_WORD: 
			System.out.println("Multi Word in Field");
			id_Hash = new HashMap<>();
			String [] queryList = query.split(" ");
			HashMap<String,HashMap<String, String>>ret1 = new HashMap<>();
			ret1 = processMultiWordField(queryList,field);
			
			System.out.println("r: " + ret1.size());
			for (Entry<String, HashMap<String,String>> entry : ret1.entrySet()) {
				  String itemKey = entry.getKey();
				  System.out.println("ok: " + itemKey);
				  for (Entry<String, String> innerEntry : entry.getValue().entrySet()) {
				    String innerKey = innerEntry.getKey();
				    String v = innerEntry.getValue();
				    //System.out.println("***ik: " + innerKey + "v: " + v);
				    // whatever, here you have itemKey, innerKey and o
				  }
				}
			return ret1;	
	
		}
		return null;
	}


	static PostingStruct ps;
	private static HashMap<String,String> traversePostingList(HashMap<String, PostingStruct> tmp, String postings,char field) {
		
		fieldHash = new HashMap<>();
		for(Map.Entry<String,PostingStruct> entry : tmp.entrySet()) {
			String key = entry.getKey();
			ps = entry.getValue();
			//System.out.println( "for key : " + ps.d +" t: " + ps.t );
			switch(field){
			
			case 'b':	if(ps.b > 0){
							String posting = "d"+ps.d+"b"+Integer.toString(ps.b)+"i"+Integer.toString(ps.i)+"c"+Integer.toString(ps.c)+"t"+Integer.toString(ps.t) ;
							fieldHash.put(key,posting);
						}
				break;
				
			case 'i':	if(ps.i > 0){
							String posting = "d"+ps.d+"b"+Integer.toString(ps.b)+"i"+Integer.toString(ps.i)+"c"+Integer.toString(ps.c)+"t"+Integer.toString(ps.t) ;
							fieldHash.put(key,posting);
						}
				break;
			
			case 'c':	if(ps.c > 0){
							String posting = "d"+ps.d+"b"+Integer.toString(ps.b)+"i"+Integer.toString(ps.i)+"c"+Integer.toString(ps.c)+"t"+Integer.toString(ps.t) ;
							fieldHash.put(key,posting);
						}
				break;
			
			case 't':	if(ps.t > 0){
					//System.out.println("in t****");
					String posting = "d"+ps.d+"b"+Integer.toString(ps.b)+"i"+Integer.toString(ps.i)+"c"+Integer.toString(ps.c)+"t"+Integer.toString(ps.t) ;
					fieldHash.put(key,posting);
					}
				break;
			
			default : continue;	
			}
		}
		return fieldHash;
	
	}



	private static HashMap<String, HashMap<String, String>> processMultiWordField(String[] queryList, char field) throws IOException {
		GetPostingList GPL1;
//		Stemmer ST4 = new Stemmer();
		HashMap<String, String>singleWordIdHash = null;
		for(String q : queryList){
			if(Utils.isStopWord(q) || q.length() <= 2) 
				continue;
			//System.out.println("query: " + q);
			GPL1 = new GetPostingList();
			String stemmedQuery = Utils.getStem(q);
			fieldWords.add(stemmedQuery);
			String p = GPL1.getPostingList(stemmedQuery);
			//System.out.println("posting: " + p);
			fieldWordDoc.put(stemmedQuery,p.split(";").length);
			singleWordIdHash = new HashMap<>();
			HashMap<String,PostingStruct>tmp  = new HashMap<>();
			tmp = createHashofPostStr(p,stemmedQuery,fieldIdHash);
			
//			for(Map.Entry<String, PostingStruct> entry : tmp.entrySet()){
//				System.out.println("key: " + entry.getKey() + "t: " + entry.getValue().t);
//			}
			//System.out.println("tmpSize: "  + tmp.size());
			singleWordIdHash = traversePostingList(tmp,stemmedQuery,field);
			//System.out.println("wih: "  + singleWordIdHash.size());
			fieldWordHash.put(stemmedQuery, singleWordIdHash);
			
		}
		//System.out.println("size: " + singleWordIdHash.size());
		return fieldWordHash;
		
	}



	private static void MultiWordQueryProcessing(String[] queryList) throws IOException {
//		Stemmer ST2 = new Stemmer();
		id_Hash = new HashMap<>();
		GetPostingList GPL1;
		ArrayList<String>stemmedQueryList = new ArrayList<>(); 
		word_Hash = new HashMap<>();
		for(String q : queryList){
			if(Utils.isStopWord(q) || q.length() <= 2) 
				continue;
			String stemmedQuery = Utils.getStem(q);
			if(stemmedQuery == null)
				continue;
			//System.out.println("qqqq: " + q);
			stemmedQueryList.add(stemmedQuery);
			//singleWordIdHash = new HashMap<>();
			GPL1 = new GetPostingList();
			String p = GPL1.getPostingList(stemmedQuery);
			//System.out.println(q + ": " + p);
			//2. create a class of d, b,i,c,t
			//3. create a map of <word < d <class> > >
			//4. inside the same function crate another map of <dIds <indexlist>> 
			//indexlist contains position of posting dIds in postinglist
			//United : d6230b1t0c0i0;d3422b1t0c0i0; 
			//States : d772b1t0c0i0;d5385b1t0c0i0;d6230b1t0c0i0
			//tmp = <united <623 <623,,0,0> 3422 <3422,1,0,0>>
			//id_Hash = <623 <0,2> 3422<1>,772<0> 5385<1>>
			
			HashMap<String,PostingStruct>tmp  = new HashMap<>();
			tmp = createHashofPostStr(p,stemmedQuery,id_Hash);
			//
			//singleWordIdHash = traversePostingList(tmp,stemmedQuery,field);
			//System.out.println("wih: "  + singleWordIdHash.size());
			//fieldWordHash.put(stemmedQuery, singleWordIdHash);
			word_Hash.put(stemmedQuery, tmp);
			//System.out.println("tmp: " + id_Hash.size());
			
			}
			
		//process on id_hash sort on the basis of no of tuples in arraylist size
		//Collections.sort(id_Hash,new ListSizeComparator());
		
		
		//get common postings from tmp wrt docids in bac 
		//first put all max-sized arraylist(bac arraylist), if not equal to TOP_K 
		//then add more (with less arraylist size)
		//the do tfidf of each posting, calculate score, and sort 
		//System.out.println("**outside **");
		
		HashMap<String, Integer>bac = new HashMap<>();
		bac = sortByValues(id_Hash);
		//System.out.println("bac: " + bac.size());
		//GetTitles t = new GetTitles();
		/*for(Map.Entry<String,Integer> entry : bac.entrySet()) {
			
			  String key = entry.getKey();
			  Integer value = entry.getValue();

			  System.out.println(key + " => " + value);
			  //t.getTitleList(key);
			}*/
		
		//calculate score for common documentIds and sort them according to that
		//on word hash calculate score
		PostingStruct psw;
		/*for (Entry<String, HashMap<String, PostingStruct>> entry : word_Hash.entrySet()) {
			  String itemKey = entry.getKey();//word
			  int score = 0;
			  String key;
			  System.out.println("item: "  + itemKey);
			  for (Entry<String, PostingStruct> innerEntry : entry.getValue().entrySet()) {
			    String innerKey = innerEntry.getKey();//id
			    psw = innerEntry.getValue();//post
			    int dinominator = ((HashMap<String, PostingStruct>) innerEntry).size();
			    // whatever, here you have itemKey, innerKey and o
			    double idf = Math.log(TOTAL_PAGES/dinominator);
			    String pt = "d"+psw.d+"b"+Integer.toString(psw.b)+"i"+Integer.toString(psw.i)+"c"+Integer.toString(psw.c)+"t"+Integer.toString(psw.t) ;
			    GetTfIdf sc = new GetTfIdf(pt, idf);
				score += sc.tfidf;
				
			  }
			  
			}*/
		
		
		for (Entry<String, String> entry : postHash.entrySet()) {
			String key = entry.getKey();
			  String value = entry.getValue();

			  System.out.println(key + " => " + value);
			
		}
		
		int count= 0; // count to get top_k
		scoreHash = new HashMap<>();
		//int max_size = bac.get(key).size();
		
		Map.Entry<String,Integer> entry1=bac.entrySet().iterator().next();
		int max_size = entry1.getValue();
		//word id posting
		
		
		for(Map.Entry<String,Integer> entry : bac.entrySet()) {
			String key = entry.getKey();// key contains id
			//System.out.println("k: " +key);
			int score =0; // calculte score
			count++;
		
			/*if(count > TOP_K || count > bac.size()){
				if(max_size == bac.get(key))
					continue;
				else
					break;
			}*/
			
			for(String q : stemmedQueryList){
				int dinominator;
				if(word_Hash.get(q) == null)
					dinominator = 6;
				else
					dinominator = word_Hash.get(q).size();
				String pt = postHash.get(key);//posting
				///HashMap<String, String>tmp = fieldWordHash.get(q);
				///String pt = tmp.get(key);
				System.out.println("pt: " + pt);
				double idf = Math.log(TOTAL_PAGES/dinominator);
				String[] sxs = pt.split(";");
				if(sxs.length  >  1)
				{
					System.out.println("l: " + sxs.length );
					for(String tpt : sxs){ 
						GetTfIdf sc = new GetTfIdf(pt, idf);
						score += sc.tfidf;//score calculated for each posting wrt words
					//System.out.println("1 word: " + q + "   posting " + tpt + " : " + " s " + score);
					}
				}
				else{
					GetTfIdf sc = new GetTfIdf(pt, idf);
					score += sc.tfidf;
					//System.out.println("2 word: " + q + "   posting " + pt + " : " + " s " + score);
				}
					
			}
			scoreHash.put(key,score);
			//sort wrt score
		}
		scoreHash = sortByValues(scoreHash);
//		for(Map.Entry<String,Integer>entry : scoreHash.entrySet()){
//			System.out.println("k: " + entry.getKey() + "v: " + entry.getValue());
//		}
		getTopKtitles(scoreHash);
		
	}



	private static void getTopKtitles(
			HashMap<String, Integer> scoreHash2) throws IOException {
			GetTitles  gt = new GetTitles();
			int c =0;
		for (HashMap.Entry<String, Integer> entry : scoreHash2.entrySet()) {
		    String key = entry.getKey();
		    //System.out.println("k: " + key);
		    String title = gt.getTitleList(key);
		    System.out.println("t: " + title + " " + entry.getValue());
		    c++;
		    if(c > TOP_K)
		    	break;
		}
		
	}



	private static HashMap<String, PostingStruct> createHashofPostStr(String p,String q, HashMap<String,Integer> id_Hash) {
		//System.out.println("** Inside createHashOfPostStruct");
		int freq = 0,k=0,index = 0;
		int cnt = 0;
		//postHash = new HashMap<>();
		
		postStr = new HashMap<>();
		PS0 = new PostingStruct();
		
		String Posting = "";
		if (p == null)
			return null;
		for(int i = 0; i < p.length();i++){
			//System.out.println("aaaaaaaa: " + p.charAt(i));
			if( Character.isLetter (p.charAt(i)) ){
			//	System.out.println("qq: " + p.charAt(k));
				//Posting+=p.charAt(i);
				continue;
			}
//			if(p.charAt(p.length()-1) == ';')
//				continue;
			k =i;
			while(k < p.length() && Character.isDigit(p.charAt(k)) ){
			//	System.out.println("pp: " + p.charAt(k));
				//Posting+=p.charAt(k);
				k++;
				
			}
			
			freq = Integer.parseInt( p.substring(i,k));
			//System.out.println("f: " + freq);
			//System.out.println("char: " + p.charAt(i-1));
			
			switch(p.charAt(i-1)){
			case 'd':	PS0.d = Integer.toString(freq);
						Posting += "d" + PS0.d;
						//System.out.println("d: " + freq);
				break;
			case 'i':	PS0.i = freq;
						Posting += "i" + Integer.toString(freq);
						//System.out.println("i: " + freq);
				break;
			case 'b':	PS0.b = freq;
						Posting +="b" + Integer.toString(freq);
						//System.out.println("b: " + freq);
				break;
			case 'c':	PS0.c = freq;
						Posting += "c" + Integer.toString(freq);
						//System.out.println("c: " + freq);
				break;
			case 't':	PS0.t = freq;
						Posting += "t" + Integer.toString(freq);
						//System.out.println("t: " + freq);
				break;

			}
			//System.out.print("q: " + p.charAt(k));
			if(p.charAt(k) == ';'){
				cnt++;
				/*if(postStr.containsKey(PS0.d)){
					System.out.println("gdkfjh");
					System.exit(1);
				}*/
				//System.out.println("kkj: " + PS0.d + " " + PS0.t + " " + postStr.get(9345654).t);
				//System.out.println("1: " + System.identityHashCode(postStr));
				postStr.put(PS0.d, PS0);
				/*for(Map.Entry<String, PostingStruct> entry : postStr.entrySet()){
					System.out.println("3 id: " + entry.getKey() + " t: " + entry.getValue().t);
				}*/
				//System.out.print("PS.id: " + PS0.d + " t: " + PS0.t);
				//System.out.println("v: " + postStr.get(PS0.d).t);
				if(id_Hash.get(PS0.d) == null){
					//ArrayList<Integer>indexList = new ArrayList<>();
					//indexList.add(index);
					id_Hash.put(PS0.d,1);
				}
				else{
					int x = id_Hash.get(PS0.d);
					id_Hash.put(PS0.d, x+1);
				}
				
				//index++;
				
				Posting += p.charAt(k);
				
				if(!postHash.containsKey(PS0.d))
					postHash.put(PS0.d,Posting);
				else{
					String tp = postHash.get(PS0.d);
					Posting += tp;
					postHash.put(PS0.d,Posting);
					
				}
				PS0= new PostingStruct();
				Posting = "";
				i = k;
			}
			i = k;
			/*if(cnt == 5)
				break;*/
		}
		//System.out.println("posting: " + Posting);
		
		//System.out.println();
		
//		for(Map.Entry<String, PostingStruct> entry : postStr.entrySet()){
//			System.out.print(entry.getValue().t);
//		}
		//System.exit(1);
		return postStr;
	}


	private static QueryType getQueryType(String query) {
		
		// parse query and return it type
		//return SINGLE_WORD;
		//QueryType q;
		String q = "";
		if(query.charAt(1) == ':')
			q = "FIELD_QUERY";
		
		else if(query.split(" ").length == 1)
			 q = "SINGLE_WORD";
		
		else 
			q = "MULTI_WORD";
		
		return QueryType.valueOf(q);
		
	}
	
	static <String extends Comparable,Integer extends Comparable> HashMap<String,Integer> sortByValues(HashMap<String,Integer> map){
		   
		List<HashMap.Entry<String,Integer>> entries = new LinkedList<HashMap.Entry<String,Integer>>(map.entrySet());
	  
	    Collections.sort(entries, new Comparator<HashMap.Entry<String,Integer>>() {

	        @Override
	        public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
	            return o2.getValue().compareTo(o1.getValue());
	        }
	    });
	  
	    //LinkedHashMap will keep the keys in the order they are inserted
	    //which is currently sorted on natural ordering
	    HashMap<String,Integer> sortedMap = new LinkedHashMap<String,Integer>();
	  
	    for(HashMap.Entry<String,Integer> entry: entries){
	        sortedMap.put(entry.getKey(), entry.getValue());
	    }
	  
	    return sortedMap;
	}

	
}

/*class ListSizeComparator implements Comparator<String> {

	private final Map<String, ArrayList<Integer>> map;

	public ListSizeComparator(final HashMap<String,ArrayList<Integer>> id_Hash) {
	    this.map = id_Hash;
	}

	@Override
	public int compare(String s1, String s2) {
	    //Here I assume both keys exist in the map.
	    List<Integer> list1 = this.map.get(s1);
	    List<Integer> list2 = this.map.get(s2);
	    Integer length1 = list1.size();
	    Integer length2 = list2.size();
	    return length1.compareTo(length2);
	}

}
*/






