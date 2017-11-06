package com.muzi.indexer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageBunchExecutor extends Thread {

    long threadId;
	ArrayList<Page> pages;
	Tokenizer tk;
	
	// for holding count of words for one document per unique word
	HashMap<String, Entry> localMap;
	private boolean last_execution;
	private BufferedWriter rt;
	//static ArrayList<HashMap<String, Entry>> localMapArray = new ArrayList<HashMap<String,Entry>>();
	
	public PageBunchExecutor(List<Page> old,long threadId) {
        this.threadId = threadId;
		this.tk = Utils.tk;
		this.pages = new ArrayList<Page>();
//		Collections.copy(this.pages, pages);
		for(Page p : old) {
		    try {
				this.pages.add((Page)(p.clone()));
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		last_execution = false;
	}
	
	public PageBunchExecutor(ArrayList<Page> allPages, boolean last_execution) {
		this(allPages, 0);
		this.last_execution = last_execution;
	}

	@Override
	public void run() {
	
		System.out.println("Current Thead Count: "+java.lang.Thread.activeCount());
		/*here are two options 
		 * either to make array of localMap or 
		 * update Indexer.bigindex after every iteration 
		 * of for loop*/
		
//		System.out.println("PageBunchExecutor received " + pages.size() + " pages");
		
		try {
			for (int i = 0; i < pages.size(); i++) {
	//			System.out.println("running for page " + i + " with id " + pages.get(i).id);
				localMap = new HashMap<String,Entry>();
					//Utils.printFile( pages.get(i));
					
					Utils.writeMainTitleIndex(pages.get(i));
				
	
					/// write title index
					
				// 4. iterate on local map and call Indexer.addWord(word, entry)
					Page page = pages.get(i);
					processSinglePage(page);
					page.text = null;
					page = null;
					Indexer.addLocalMap(localMap);
					
					
			}
			//rt.close();
		} catch (IOException e) {
			e.printStackTrace();
		
		}
		
		super.run();
		
		if(last_execution) {
			try {
				Indexer.finalWrite();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	    System.out.println("Thread " + threadId + " ended");	
	}
	
	
	
	private void processSinglePage(Page page) throws IOException {
		
		// 1. tokenize
		// can be optimized here by calling tokenizer once for text only that includes category and infobox
		
		// write title to TitleIndex
		
		//System.out.println("Proceesing Single Page with PID: " + page.id + " and title as : " + page.title);
		
		page.cleanedText = tk.getActualText(page.text);
		page.tok_catg = tk.getCategory(page.text);
		page.tok_info = tk.getinfobox(page.text);
		page.text = null;

		page.cleanedText = page.cleanedText.toLowerCase();
		page.tok_catg = page.tok_catg.toLowerCase();
		page.tok_info = page.tok_info.toLowerCase();
		page.title = page.title.toLowerCase();
		/*String s = "";
		for(String word : page.title.split(" ")){
			s += word.replaceAll("[^a-zA-Z0-9]"," ");
			
		}
		page.title = s.strip();*/
		/*BufferedWriter bbb =new BufferedWriter(new FileWriter(Utils.BASE_PATH + "logs",true));
		bbb.write(page.tok_info);
		bbb.write("\n\n*********************************************************************\n\n");
		bbb.close();*/
		
		//2. create a file of title and Page_id
		//here i have to store according to the id but while retrieving i need to have id in sorted manner
		//So to keep it in sorted i need either k-way-merge sort or i need to write in sorted order 
		  
		/*rt = new BufferedWriter(new FileWriter(Utils.BASE_PATH + "ID_Title_Index/idti.txt",true));
		rt.write(page.id +"\t" + page.title + "\n");*/
		
		
		
		
		//4.update localMap
		if(page.cleanedText != null) {
			updateLocalMapBody(page.cleanedText,page.id);
			page.cleanedText = null;
		}
		
		if(page.tok_catg != null){
			//System.out.println("** In update Category **");
			updateLocalMapCategory(page.tok_catg,page.id);
		}
		
		//we donot need to remove stop words from the title in order to retrieve data efficiently
		//or i think i need to save title and ids in seperate file (saved in same manner ie by first twoChars)
		//and search the query first from that file. 
		//and then from secondary and primary index.
		
		if(page.title != null){
			//System.out.println("** In update title **");
			updateLocalMapTitle(page.title,page.id);
		}
		if(page.tok_info != null){
			//System.out.println("** In update infbox **");
			updateLocalMapInfo(page.tok_info,page.id);
		}
		
	}
	
	void updateLocalMapBody(String text, int pageId){
		 
		String[] words = text.replaceAll("\\s+", " ").split(Utils.SPACE_REGEX);
//		System.out.println("words size  " + words.length);
		String stemmedWord;
		for (String word : words){
//			System.out.println(words);
			//
			if(word.matches("^[0-9]+$"))
				continue;
			// check if word is stopWord, if then continue
			if(Utils.isStopWord(word) || word.length() <= 2) 
				continue;

			// stem it
//			stemmedWord = Stemmer.getInstance().getStem(word);
			stemmedWord = Utils.getStem(word);
			if(stemmedWord.length() < 3) continue;
			
			Entry ent = null;	
			if(!localMap.containsKey(stemmedWord)) {
				ent = new Entry();
				ent.docId = pageId;
				//localMap.put(word,ent);//check whether to initialize the contents of entry to zero or not 
			} else {
				ent = localMap.get(stemmedWord);
			}
			ent.body_count++;
			localMap.put(stemmedWord,ent);
			//System.out.println("Stemmed for pageId: " + pageId);
		}
	}
	
	void updateLocalMapCategory(String text, int pageId){
		 //System.out.println(text);
		String[] words = text.split(Utils.SPACE_REGEX);
		String stemmedWord;
		for (String word : words){
			
			// check if word is stopWord, if then continue
			if(Utils.isStopWord(word) || word.length() <= 2)
				continue;

			// stem it
			stemmedWord = Utils.getStem(word);
			if(stemmedWord.length() < 3) continue;
			Entry ent = null;	
			if(!localMap.containsKey(stemmedWord)) {
				ent = new Entry();
				ent.docId = pageId;
				//localMap.put(word,ent);//check whether to initialize the contents of entry to zero or not 
			} else {
				ent = localMap.get(stemmedWord);
			}
			ent.category_count++;
			localMap.put(stemmedWord,ent);
		}
	}
	
	void updateLocalMapTitle(String text, int pageId){
		 
		String[] words = text.split(Utils.SPACE_REGEX);
		String stemmedWord;
		for (String word : words){
			
			// check if word is stopWord, if then continue
			if(Utils.isStopWord(word) || word.length() <= 2) 
				continue;

			// stem it
			stemmedWord = Utils.getStem(word);
			if(stemmedWord.length() < 3) continue;
			
			Entry ent = null;	
			if(!localMap.containsKey(stemmedWord)) {
				ent = new Entry();
				ent.docId = pageId;
				//localMap.put(word,ent);//check whether to initialize the contents of entry to zero or not 
			} else {
				ent = localMap.get(stemmedWord);
			}
			ent.title_count++;
			localMap.put(stemmedWord,ent);
		}
	}
	
	void updateLocalMapInfo(String text, int pageId){
		 
		String[] words = text.split(Utils.SPACE_REGEX);
		String stemmedWord;
		for (String word : words){
			
			// check if word is stopWord, if then continue
			if(Utils.isStopWord(word) || word.length() <= 2) 
				continue;

			// stem it
			stemmedWord = Utils.getStem(word);
			if(stemmedWord.length() < 3) continue;
			
			Entry ent = null;	
			if(!localMap.containsKey(stemmedWord)) {
				ent = new Entry();
				ent.docId = pageId;
				//localMap.put(word,ent);//check whether to initialize the contents of entry to zero or not 
			} else {
				ent = localMap.get(stemmedWord);
			}
			ent.infobox_count++;
			localMap.put(stemmedWord,ent);
		}
	}
	
	
	
}
