package com.muzi.indexer;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class Indexer {

	static int LIMIT = 10000;
	static String PATH = "/home/muzammil/searchEngineData/index/PrimaryIndex/";
	static int cntt = 0;
	static HashMap<String, HashMap<String, String>> bigIndex = new HashMap<String, HashMap<String, String>>();
	static long LIMIT_MBS = 10; 
	static long accessCount = 0;
	// HashMap or arraylist of bufferedWriter
	static HashMap<String, BufferedWriter> writers = new HashMap<String, BufferedWriter>();

	

	public static void addLocalMap(HashMap<String, Entry> localMap) {

		synchronized (Indexer.class) {
			for(Map.Entry<String, Entry> obj : localMap.entrySet()) {
				try {
					Indexer.addWord(obj.getKey(), obj.getValue());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		}
		
	}

	
	public static void addWord(String newWord, Entry newEntryList)
			throws IOException {
		// check if word present
		// merge with existing entry if needed
		// make use of getBin

		Matcher m = Utils.nonEnglishPattern.matcher(newWord);
		if(m.find()) return;
		
		HashMap<String, String> hm = getBin(newWord.substring(0, 2));

		//synchronized (Indexer.class) {
			if (hm.containsKey(newWord)) {
				
				String oldEntry = hm.get(newWord);
				// merge duplicate entry
				hm.put(newWord, oldEntry.toString() + newEntryList.toString());
			} else {
				
				hm.put(newWord, newEntryList.toString());
			}
			accessCount++;
			if (accessCount > 3500000){
				long bigSize = hashsize();
                System.out.println("accessCount " + accessCount + ", bigSize " + bigSize);
				
				if( bigSize > (LIMIT_MBS * 1048576)){
					System.out.println("freed");
					accessCount = 0;
					finalWrite();
				}
			}
		//}
		// should i clear both or only one
		// #ISSUE 1
		if (hm.size() > LIMIT) {
			System.out.println(newWord + " limit of " + LIMIT
					+ " reached. Writing to file.");
			// getWriter(newWord.substring(0, 2));
			// iterate over hashmap
			// write one one line ----> word:<entrylists already colon
			// separated>\n
			// BufferedWriter bw = new BufferedWriter(new
			// FileWriter("/home/muzammil/IRE_Project/PrimaryIndex/",true));

			//synchronized (Indexer.class) {
				
				BufferedWriter bw = getWriter(newWord.substring(0, 2));
				for (Map.Entry<String, String> IndexEntry : hm.entrySet()) {
					String word = IndexEntry.getKey();
					String Index = IndexEntry.getValue();
					bw.write(word.substring(2) + "\t" + Index +"\n");
				}
				cntt+=1;
				System.out.println("Written in file: " + cntt);
				hm.clear();
			//}
		}

		// check if size is > LIMIT then write to BufferedWriter and call flush
		// and clear that hashmap

	}

	private static BufferedWriter getWriter(String twoChars) throws IOException {
		
		
		if (!writers.containsKey(twoChars)) {
			File out = new File(PATH + twoChars + ".txt");
			if(!out.exists()) {
				out.createNewFile();
			}
			writers.put(twoChars, new BufferedWriter(new FileWriter(out)));
		}
		return writers.get(twoChars);

	}

	
	public static void closeBufferWriters() throws IOException{
		for (Map.Entry<String, BufferedWriter> entry : writers.entrySet())
		    entry.getValue().close();
		
	}
	
	private static HashMap<String, String> getBin(String twoChars) throws IOException {
		
		if (!bigIndex.containsKey(twoChars)) {
			bigIndex.put(twoChars, new HashMap<String, String>());
		}
		
		return bigIndex.get(twoChars);
	}

	
	//here is the problem of storing whole data in bigIndex and writing it we need to
	//keep tabs on bigIndex limit also
	
	public static long hashsize() {
		long s = 0;
	    try{
	        //System.out.println("Index Size: " + bigIndex.size());
	        ByteArrayOutputStream baos=new ByteArrayOutputStream();
	        ObjectOutputStream oos=new ObjectOutputStream(baos);
	        oos.writeObject(bigIndex);
	        oos.close();
	        //System.out.println("Data Size: " + baos.size());
	        s = baos.size();
	    }catch(IOException e){
	        e.printStackTrace();
	    }
	    return s;
	}
	
	public static synchronized void finalWrite() throws IOException {
		//System.out.println("**** Inside Final Write ******");
		//size();
		for (Map.Entry<String, HashMap<String, String>> letterEntry : bigIndex
				.entrySet()) {
			String letter = letterEntry.getKey();
			HashMap<String, String> hm = letterEntry.getValue();
			BufferedWriter bw = getWriter(letter);
			
			for (Map.Entry<String, String> IndexEntry : hm.entrySet()) {
				String word = IndexEntry.getKey();
				String Index = IndexEntry.getValue();
				bw.write(word.substring(2) + "\t" + Index+"\n");
			}
			
			//bw.close();
			hm.clear();
		}
		System.out.println("[Indexer] Current Thead Count: "+java.lang.Thread.activeCount());
		
		System.out.println("Runtime Indexer: " + (System.currentTimeMillis() - SAX_Parser.start)/1000 + "s");
		//System.out.println(" ****Written whole bigIndex**** ");
		bigIndex.clear();
	}

	/*
	 * public static void myFlush() { // iterate on all the 26*26 hashmaps //
	 * check if number of items in 1 hashmap is > LIMIT , if then write to file
	 * and clear the hashmap }
	 */

}
