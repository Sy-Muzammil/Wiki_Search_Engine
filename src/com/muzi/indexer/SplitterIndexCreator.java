package com.muzi.indexer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class SingleFileIndex {
	int ptr;
}

public class SplitterIndexCreator {

	private static int SECONDARY_SPLIT_SIZE = 0;
	static String sortedPath = Utils.BASE_PATH + "index/sorted/";
	static String indexPath = Utils.BASE_PATH + "index/split/index";
	static String primaryIndexPath = Utils.BASE_PATH + "index/split/primary";
	static String secondaryIndexPath = Utils.BASE_PATH
			+ "index/split/secondary";

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
		BufferedReader br = new BufferedReader(new FileReader(child));
		String line;

		SingleFileIndex indexOffset = new SingleFileIndex();
		SingleFileIndex primaryOffset = new SingleFileIndex();

		//StringBuilder indexBuf = new StringBuilder();
		//StringBuilder primaryIndexBuf = new StringBuilder();
		//StringBuilder secondaryIndexBuf = new StringBuilder();

		BufferedWriter indexBr = new BufferedWriter(new FileWriter(indexPath
				+ "/" + child.getName()));
		BufferedWriter primaryIndexBr = new BufferedWriter(new FileWriter(
				primaryIndexPath + "/" + child.getName()));
		BufferedWriter secondaryIndexBr = new BufferedWriter(new FileWriter(
				secondaryIndexPath + "/" + child.getName()));

		boolean lastLine = false;
		int lineCounter = 0;
		String word, newWord = null, entry, newEntry = null, entries = null;
		String[] token;

		//System.out.println(child);
		line = br.readLine();
		System.out.println("l: " + line);
		if(line == null){
			return;
		}
		token = line.split("\t");
		//System.out.println("t: " + token[0] + " " + token[1]);
		word = token[0];
		entry = token[1];
	
		//System.out.println("got 1st line " + line);

		while (true) {

			String tmp = br.readLine();
			if (tmp == null)
				lastLine = true;
			else {
				token = tmp.split("\t");
				newWord = token[0];
				newEntry = token[1];

				entries = null;

				while (newWord.equals(word)) {
					//System.out.println("duplicate word found " + word);
					if (entries == null) {
						entries = entry;
					}
					entries += newEntry;

					tmp = br.readLine();
					if (tmp == null) {
						lastLine = true;
						break;
					}
					token = tmp.split("\t");
					newWord = token[0];
					newEntry = token[1];

				}
			}

			// 1. write entry to index
			// 2. write word to primaryIndex with pointer to index
			// 3. write word to secondaryIndex with pointer to primaryIndex
			if (entries != null)
				entry = entries;

			// sort entry here using custom comparator for tfidf
			//System.out.println("inside getSortedEntry: " + entry);
			String modifiedEntryString = Utils.getSortedEntry(entry);
			indexBr.write(modifiedEntryString + "\n");
			//indexBuf.append(modifiedEntryString + "\n");

			//primaryIndexBuf.append(word + ":" + indexOffset.ptr + "\n");
			primaryIndexBr.append(word + ":" + indexOffset.ptr + "\n");

			// every 1000th words
			if (lineCounter % SECONDARY_SPLIT_SIZE == 0) {
				//secondaryIndexBuf.append(word + ":" + primaryOffset.ptr + "\n");
				secondaryIndexBr.append(word + ":" + primaryOffset.ptr + "\n");
			}
			primaryOffset.ptr += word.length() + 1
					+ (indexOffset.ptr + "").length() + 1; // colon 1 char and
															// newline 1 char

			indexOffset.ptr += entry.length() + 1; // newline 1 char

			lineCounter++;

			if (lastLine)
				break;
			word = newWord;
			entry = newEntry;
			entries = null;
		}

		/*
		 * System.out.println("Index file contents");
		 * System.out.println(indexBuf.toString() + "\n\n\n\n");
		 * 
		 * System.out.println("PrimaryIndex contents");
		 * System.out.println(primaryIndexBuf.toString() + "\n\n\n");
		 * 
		 * System.out.println("SecondaryIndex contents");
		 * System.out.println(secondaryIndexBuf.toString());
		 */
		br.close();
		indexBr.close();
		primaryIndexBr.close();
		secondaryIndexBr.close();
	}

		

}

