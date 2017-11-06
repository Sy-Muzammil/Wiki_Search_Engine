package com.muzi.indexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class StopWord {
//	HashSet<String> stopwords = new HashSet<String>();
	HashMap<String, Integer> stopwords = new HashMap<String, Integer> ();
	int numOfWords = 0;
	BufferedReader br;
	String currentS;
	
	StopWord() {
		populateStopWords();
	}

	private void populateStopWords() {
		try {
			FileReader fr = new FileReader("/home/muzammil/searchEngineData/readonly/stoplist.txt");
			br = new BufferedReader(fr);
			while ((currentS = br.readLine()) != null) {
				stopwords.put(currentS, 1);
				numOfWords++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(numOfWords + " stopwords loaded in set.");
	}
	
	boolean isStopWord(String s) {
		return stopwords.containsKey(s);
	}

}
