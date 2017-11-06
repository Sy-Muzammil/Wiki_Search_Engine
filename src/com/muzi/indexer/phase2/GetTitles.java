package com.muzi.indexer.phase2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import com.muzi.indexer.Utils;

public class GetTitles {
	static String indexPath = Utils.BASE_PATH + "index/titleSplit/Index";
	static String primaryIndexPath = Utils.BASE_PATH + "index/titleSplit/Primary";
	static String secondaryIndexPath = Utils.BASE_PATH
			+ "index/titleSplit/Secondary";
	
	
static BufferedReader br;
	
	public static String getTitleList(String query) throws IOException {
		
		// random file access
		String twoChar = query.substring(0,2);
		String wordToSearch = query;
		
		//System.out.println("search: " + wordToSearch);
		String secOffset = findInSecondaryIndex(twoChar,wordToSearch);
		if(secOffset == null)
			return null;
		//System.out.println("secondary index search done offset " + secOffset);
		long primOffset = findInPrimaryIndex(twoChar,wordToSearch,secOffset);
		
		System.out.println("primary index search done offset " + primOffset  );
		if (primOffset == -1)
			return null;
		String posting = findPosting(primOffset,twoChar);
		//System.out.println("title index search done: " + posting );
		
		return posting;
	}
	
	public static String findInSecondaryIndex(String twoChar,String s) throws IOException{
		ArrayList<String>offset = new ArrayList<>();
		br = new BufferedReader(new FileReader(secondaryIndexPath + "/" + twoChar + ".txt"));
		
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
		
		RandomAccessFile readPointer = new RandomAccessFile(primaryIndexPath + "/" + twoChar + ".txt", "r");
		readPointer.seek(Long.parseLong(secOffset));
		
		while (true) {
			line = readPointer.readLine();
			//System.out.println("1mary "+line);
			if(line == null)
				return -1;
			if(blockSize>2000) 
				return -1;
			blockSize++;
		
			if (line.split(":")[0].equals(wordToSearch)) {
				//sSystem.out.println("ss: " + line);
				primOffset = Long.parseLong(line.split(":")[1]);
				break;
			}
			
		
		}
		//System.out.println("primoff: " + primOffset);
		
		return primOffset;
	}
	
	
	
	public static String findPosting(long primOffset, String twoChar) throws IOException {
		
		
		RandomAccessFile readPointer = new RandomAccessFile(indexPath +"/" + twoChar + ".txt", "r");
		readPointer.seek(primOffset);
		
		String line = readPointer.readLine(); 
		
		return line;
	}
	public static void main(String[] args) throws IOException {
		//titles9267218 => 2, 9308380 => 1,9342841 => 1,9254786 => 1,9254754 => 1,9418296 => 1,9308067 => 1
//9273453 => 1,9339569 => 1,9255384 => 1,9353336 => 1,9307441 => 1,9308586 => 1,9361524 => 1,9245480 => 1
		String q = "9499078";
		System.out.println("t: " + getTitleList(q));
		String sa = "í";
		System.out.println("ssss: " + sa.length());
	}
	
}

