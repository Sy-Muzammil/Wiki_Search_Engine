package com.muzi.indexer.phase2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import com.muzi.indexer.Utils;

public class GetPostingList {
	
	
	static BufferedReader br;
	
	public static String getPostingList(String query) throws IOException {
		
		// random file access
		String twoChar = query.substring(0,2);
		String wordToSearch = query.substring(2,query.length());
		
		//System.out.println("Starting search " + twoChar + ", " + wordToSearch);
		String secOffset = findInSecondaryIndex(twoChar,wordToSearch);
		
		//System.out.println("secondary index search done offset " + secOffset);
		long primOffset = findInPrimaryIndex(twoChar,wordToSearch,secOffset);
		
		//System.out.println("primary index search done offset " + primOffset  );
		if (primOffset == -1)
			return null;
		String posting = findPosting(primOffset,twoChar);
		//System.out.println("main index search done, " + posting );
		
		return posting;
	}
	
	public static String findInSecondaryIndex(String twoChar,String s) throws IOException{
		ArrayList<String>offset = new ArrayList<>();
		br = new BufferedReader(new FileReader(Utils.BASE_PATH + "index/split/secondary/" + twoChar + ".txt"));
		
		String line = br.readLine();
		//System.out.println("secline: " + line);
		//String s1 = "abc", s2="def";
//		s1.compareTo(s2)
		String oldOffset = null;
		while(line != null){
			String[] wordOffset = line.split(":");
			
			if(wordOffset[0].compareTo(s) >= 0) {
				return oldOffset == null? wordOffset[1]: oldOffset;
			}
			oldOffset = wordOffset[1];
			line = br.readLine();
			/*if( searchWord >= curLineWord) {
				break and return offset;
			}*/
//			terms.add(wordOffset[0]);
//			offset.add(wordOffset[1]);
		}
		br.close();/*
		int index =  Collections.binarySearch(terms,s);
		if(index < 0)
			index = Math.abs(index)-2;
		return offset.get(index);
	*/	
		return null;
	
	}
	
	
	public static long findInPrimaryIndex(String twoChar,
			String wordToSearch, String secOffset) throws NumberFormatException, IOException {
		
		long primOffset = -1;
		int blockSize=0;
		String line;
		
		RandomAccessFile readPointer = new RandomAccessFile(Utils.BASE_PATH + "index/split/primary/" + twoChar + ".txt", "r");
		readPointer.seek(Long.parseLong(secOffset));
		
		while (true) {
			line = readPointer.readLine();
			//System.out.println("1mary "+line);
			if(line == null)
				return -1;
			if(blockSize>1000) 
				return -1;
			blockSize++;
		
			if (line.split(":")[0].equals(wordToSearch)) {
				primOffset = Long.parseLong(line.split(":")[1]);
				break;
			}
		
		}
		//System.out.println("primoff: " + primOffset);
		
		return primOffset;
	}
	
	
	
	public static String findPosting(long primOffset, String twoChar) throws IOException {
		
		
		RandomAccessFile readPointer = new RandomAccessFile(Utils.BASE_PATH + "index/split/index/" + twoChar + ".txt", "r");
		readPointer.seek(primOffset);
		
		String line = readPointer.readLine(); 
		
		return line;
	}
	
}
