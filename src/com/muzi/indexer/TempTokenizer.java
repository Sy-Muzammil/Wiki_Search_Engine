package com.muzi.indexer;

import java.io.BufferedReader;

import java.io.FileReader;

import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TempTokenizer {
	static BufferedReader br;
	static String line;
	static String out;
	
	static String INFO_PATTERN = "{{Infobox";
	static private Matcher m;
	static private Pattern p;
	
	
	static String remove_tags_urls_(String prtext){
		return null;
	}
	
	/*void RegexPatterns(){
		//this.start
	}
	
	*/
	
	
	static int initialcnt = 2;
	static int finalcnt = 0;
	
	static String getinfobox(String PrText){
		
		StringBuilder tmp = new StringBuilder();
		int StartIndex = 0;
		int Start;
		String tmpinfotext = "";
		int flag = 0;
		
		while (true) {
			
			
			initialcnt = 2;
			finalcnt = 0;
			flag = 0;
			tmpinfotext = "";
			
			Start = PrText.indexOf(INFO_PATTERN, StartIndex);
			if (Start < 0) 
				break;
			
			StartIndex = Start + INFO_PATTERN.length();
			if(StartIndex > (Start + PrText.length()))
				break;
				
			int CounterIndex = Start + INFO_PATTERN.length();
			
			for (; CounterIndex < PrText.length(); CounterIndex++) {
				if(PrText.charAt(CounterIndex) == '{')
					initialcnt++;
				else if(PrText.charAt(CounterIndex) == '}')
					finalcnt--;
				else
					tmpinfotext+=PrText.charAt(CounterIndex);
					
				if (initialcnt+finalcnt == 0){
					flag = 1;
					break;
				}
			}
			
			if (CounterIndex + 1 >= PrText.length()) 
				break;
			
			if (flag == 1){
				StartIndex = CounterIndex++; 
			}
			tmpinfotext += " ";
			tmp.append(tmpinfotext);
		}
		return tmp.toString().replaceAll("(\\P{Alpha})", " ").replaceAll("\\p{Blank}", " ");
		
		
		
	}
	
	static String getCategory(String PrText){
		
		StringBuilder tmp = new StringBuilder();
		
		int indexTmp = PrText.indexOf("[[Category");
		if(indexTmp == -1) 
			return "";
		
		PrText = PrText.substring(indexTmp);
		
		p = Pattern.compile("\\[\\[:?Category:(.*?)\\]\\]");
		m = p.matcher(PrText);
		while (m.find()){
			tmp.append(m.group(0));
		
		}
		
		out = tmp.toString().replaceAll("(\\[\\[:?Category:)|(\\]\\])", " ");
		return out.replaceAll("(\\P{Alnum})", " ").replaceAll("\\p{Blank}", " ");
	}
	
	
	static String remove_tags_urls(String text){
	
	String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
    Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(text);
    
    StringBuffer sb = new StringBuffer(text.length());
    
    while (m.find()) {
        m.appendReplacement(sb, " ");
    }
    
    return sb.toString().replaceAll("(\\P{Alnum})", " ").replaceAll("\\p{Blank}", " ").replaceAll("\\<.*?>", " ");
	}
	
	
	static void textprocessing(String PrText) throws IOException{
		//acttext is passed to stopwords and caculating the word indexing of document
		String acttext = remove_tags_urls(PrText);
		acttext = acttext.replaceAll("( )+", " ");
//		try {
//			BufferedWriter br = new BufferedWriter(new FileWriter("/home/muzammil/Desktop/out.txt"));
//			br.write(acttext);
//		} catch (Exception e) {
//			// TODO: handle exception
//			System.err.println("Error: Writing the text");
//		}
		
		
		//infotext will help in calculating the infobox word count for indexing 
		String infotext = getinfobox(PrText);
		infotext = infotext.replaceAll("( )+", " ");
		
		
		//catgtext will help in calculating category word count of document for indexing
		String catgtext = getCategory(PrText);
		catgtext = catgtext.replaceAll("( )+", " ");
		 
		 
	}
	
	
	public static void main(String[] args) throws IOException {
		
		String text = "";
		br = new BufferedReader(new FileReader("/home/muzammil/IRE-Project/less_wiki-search-small.xml"));
		
		while((line = br.readLine()) != null){
			text += line; 
		}
		
		textprocessing(text);
		br.close();
		
	}

}
