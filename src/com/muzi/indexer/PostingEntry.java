package com.muzi.indexer;


import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;

class TfIdfSorter implements Comparator<PostingEntry> {
	
	@Override
	public int compare(PostingEntry one, PostingEntry two) {
		
		return ((int)two.tfidf*100  -  (int)one.tfidf*100);
		
	}
}

public class PostingEntry {
	int bc, cc, tc, ic; // counts
	String id;
	int tf; // sum and custom calculation
	String tok;
	double tfidf;
	
	
	HashMap<String,Integer>tfIdfMap = new HashMap<>();
	BufferedWriter bw; 
	
	public PostingEntry(String s, double idf) throws IOException {
		// parse single entry and populate instance variables
		
		// d4b12c4t1
		//System.out.println("s: " + s);
		tok = s;
		calculateTf(s);
		//System.out.println("tf: " + this.tf);
		calculateTfIdf(idf);
		//System.out.println("tfidf: " + tf +"\t" + idf);
		    		
	}

	private void calculateTfIdf(double idf) {
		this.tfidf = tf * idf;
	}

	void calculateTf(String postingLine) {
		int temp =0; // sum or multiplication with *50, *100
		///
		// category = x50
		// title x100
		//System.out.println("posting: "  + postingLine);
		int freq=0;
		int k = 0;
		for(int j = 0 ; j < postingLine.length();j++){
			//System.out.println("cahrat j : "  +postingLine.charAt(j));
			if( Character.isLetter (postingLine.charAt(j)) )
				continue;
			k =j;
			while(k < postingLine.length() && Character.isDigit(postingLine.charAt(k)) ){
				k++;
			}
			freq = Integer.parseInt( postingLine.substring(j,k));
			//System.out.println("freq: " +  freq);
			
			switch(postingLine.charAt(j-1)){
				case 'b' : 	freq *= 1;
							bc = freq;
							break;
							
				case 'c' : 	freq *= 20;
							cc = freq;
							break;
							
				case 'i' : 	freq *= 10;
							ic = freq;
							break;
							
				case 't' : 	freq *= 100;
							tc = freq;
							break;
				default  : continue;
							
			}
			
		}
		//System.out.println("temp:  " + temp);	
		temp+=bc+cc+ic+tc;
		//System.out.println("temp : " + temp);
		this.tf = temp;
		
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
}
