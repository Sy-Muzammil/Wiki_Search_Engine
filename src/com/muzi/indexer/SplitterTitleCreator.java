package com.muzi.indexer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

class SingleTitleFileIndex {
	int ptr = 0;
}

public class SplitterTitleCreator {
	

		private static int SECONDARY_SPLIT_SIZE = 0;
		static String sortedPath = Utils.BASE_PATH + "index/titleSorted/";
		static String indexPath = Utils.BASE_PATH + "index/titleSplit/Index/";
		static String primaryIndexPath = Utils.BASE_PATH + "index/titleSplit/Primary/";
		static String secondaryIndexPath = Utils.BASE_PATH
				+ "index/titleSplit/Secondary/";

		public static void main(String[] args) throws IOException {

			// splitAndIndex(new File(sortedPath + "/zs.txt"));

			SECONDARY_SPLIT_SIZE = Integer.parseInt(args[0]);
			// run for loop for 26*26 files
			File dir = new File(sortedPath);
			File[] directoryListing = dir.listFiles();
			if (directoryListing != null) {
				for (File child : directoryListing) {
					System.out.println("> " + child + " " + child.isDirectory()
							+ ", " + child.isFile());
					splitAndIndex(child);
				}
			}

			// Handle the case where dir is not really a directory.
			// Checking dir.isDirectory() above would not be sufficient
			// to avoid race conditions with another process that deletes //
			// directories. }

			// Processing for 1 file example be.txt which is already sorted via bash
			// command

			// output of this step is
			// 1. File of entryList

			// 2. File of primary index with "word:integer pointer" to it's
			// entryList

			// 3. File of secondary index with "word:integer pointer" to it's
			// location in primaryIndex [for every 10000 words]

			// need to maintain offset for each of these by simple calculation
			// offset += entryList.lenght() + 1

		}

		private static void splitAndIndex(File child) throws IOException {
			RandomAccessFile br = new RandomAccessFile(child, "r");
			String line;

			SingleTitleFileIndex indexOffset = new SingleTitleFileIndex();
			SingleTitleFileIndex primaryOffset = new SingleTitleFileIndex();

			/*StringBuilder indexBuf = new StringBuilder();
			StringBuilder primaryIndexBuf = new StringBuilder();
			StringBuilder secondaryIndexBuf = new StringBuilder();
*/
			BufferedWriter indexBr = new BufferedWriter(new FileWriter(indexPath + child.getName()));
			BufferedWriter primaryIndexBr = new BufferedWriter(new FileWriter(
					primaryIndexPath  + child.getName()));
			BufferedWriter secondaryIndexBr = new BufferedWriter(new FileWriter(
					secondaryIndexPath  + child.getName()));

			boolean lastLine = false;
			int lineCounter = 0;
			String word = null, entry = null,newword,newentry;
			String[] token;

			//System.out.println(child);
			line = br.readLine();
			System.out.println("l: " + line);
			token = line.split(":",2);
			//System.out.println("t: " + token[0] + " " + token[1]);
			newword = token[0];
			newentry = token[1];
			//System.out.println("got 1st line " + line);

			while (true) {

				String tmp = br.readLine();
				if (tmp == null)
					break;
				else {
					token = tmp.split(":",2);
					word = token[0];
					entry = token[1];

				}
				indexBr.write(entry + "\n");
				primaryIndexBr.append(word + ":" + indexOffset.ptr + "\n");

				// every 1000th words
				if (lineCounter % SECONDARY_SPLIT_SIZE == 0) {
				
					secondaryIndexBr.append(word + ":" + primaryOffset.ptr + "\n");
				}
				
				//System.out.println(utf8Bytes.length);
				primaryOffset.ptr += word.length() + (indexOffset.ptr + "").length() + 1; 
																// newline 1 char
				final byte[] utf8Bytes = entry.getBytes("UTF-8");
				indexOffset.ptr += utf8Bytes.length + 1; // newline 1 char

				lineCounter++;

				/*if (lastLine)
					break;*/
				
			}
			br.close();
			indexBr.close();
			primaryIndexBr.close();
			secondaryIndexBr.close();
		}

			

}




