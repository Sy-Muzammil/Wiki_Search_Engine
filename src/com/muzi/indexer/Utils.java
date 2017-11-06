package com.muzi.indexer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class Utils {
	
	static final String SPACE_REGEX = "\\s+";
	static final String nonEnglishCharacterSet = "[^A-Za-z0-9]+";
	static final Pattern nonEnglishPattern = Pattern.compile(nonEnglishCharacterSet);
	
	public static final String BASE_PATH = "/home/muzammil/searchEngineData/";
	private static final int ENTRY_LIST_THRESHOLD = 30000;
	static StopWord stopWord = new StopWord();
	static Tokenizer tk = new Tokenizer();
	static PorterStemmer porterStemmer = new PorterStemmer();
	
	
	static String TITLE_PATH = "/home/muzammil/searchEngineData/index/titleIndex/";
	static HashMap<String, BufferedWriter> title_Writers = new HashMap<String, BufferedWriter>();
	static BufferedWriter tw;
	// FileWrite Title
	
	public static boolean isStopWord(String s) {
		return stopWord.isStopWord(s);
	}

	
	private static BufferedWriter getWritertitle(String twoChars) throws IOException {
		
		
		if (!title_Writers.containsKey(twoChars)) {
			File out = new File(TITLE_PATH + twoChars + ".txt");
			if (!out.getParentFile().exists())
			    out.getParentFile().mkdirs();
			if (!out.exists())
			    out.createNewFile();
			title_Writers.put(twoChars, new BufferedWriter(new FileWriter(out)));
		}
		return title_Writers.get(twoChars);

	}
	
	/*public static void printFile(Page page) {
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(BASE_PATH + "/index/raw/" + page.getId() ));
			bw.write(page.getTitle() + "\n");
			bw.write(page.text);
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}*/
	
		public static void writeMainTitleIndex(Page page) {
			
			
			try {
				String did = Integer.toString(page.id);
//				if(did.length() < 2)
//					return;
				//page. = page.id.;
				String twoChars = did.substring(0, 2);
				//System.out.println("t: " + twoChars);
				synchronized (Utils.class) {
					tw  = getWritertitle(twoChars);
					tw.write(did + ":" + page.title.toLowerCase() + "\n");
					}
				} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// write to file 
			// docid:title
			
			// also create secondary index here only with interval os 1000titles
			
			// create 3 files
	}
	

	public static String getSortedEntry(String entry) throws IOException {
		
		
		String[] tokens = entry.split(";");
		int dinominator = tokens.length;
		if(dinominator == 1)
			return entry;
		
		
		int N = 5311; // hard-coded number i.e. total document
		
		double idf = Math.log(N/dinominator);
		
		List<PostingEntry> entries = new ArrayList<PostingEntry>();
		//System.out.println("****** Inside PostingEntry *******");
		for(String s: tokens) {
			entries.add(new PostingEntry(s, idf));
		}
		//System.out.println("***** Sorting ******");
		Collections.sort(entries, new TfIdfSorter());
		
		StringBuilder sb = new StringBuilder();
		int i=0;
		
		
		BufferedWriter bwl = new BufferedWriter(new FileWriter(Utils.BASE_PATH + "xlog",true));
		for(PostingEntry p : entries) {
			if(i>ENTRY_LIST_THRESHOLD) 
				break;
			bwl.write(p.tok + ":" + p.tf + ":" + p.tfidf + ";");
			sb.append(p.tok + ";");
			i++;
		}
		//System.out.println("StringBuilder: " + sb);
		
		
		bwl.write("||\n");
		bwl.close();
		return sb.toString();
	}


	public static String getStem(String word) {
		return Stemmer.getInstance().getStem(word);
	}
	
	
	public static String getStem0(String word) {
		try {
			porterStemmer.add(word.toCharArray(), word.length());
			porterStemmer.stem();
			return porterStemmer.toString();
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage() + " for word " + word);
		}
		return "";
	}
	
	public static int readThrottleSleepTime() {
		int delay = 30;
		try {
			BufferedReader br = new BufferedReader(new FileReader("props.txt"));
			String l = br.readLine();
			delay = Integer.parseInt(l.split("=")[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return delay;
	}
}
