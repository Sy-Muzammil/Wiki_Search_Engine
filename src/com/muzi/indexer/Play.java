package com.muzi.indexer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Play {

	public static void main(String[] args) {
		String t = "some text  with 	multi";
		t = t.replaceAll("\\s+", " ");
		System.out.println("."  + t + ". ");

		
		int i=3;
		System.out.println("some" + i + 5);
		System.out.println(i + 5 + " some");
		System.out.println(i + 5);
		
		
		String w = "bc3ás4s";
		String normalCharacterRegex = "[^A-Za-z0-9]+"; 
//				"\\p{Alpha}+"; 
		
		Pattern normalCharacterPattern = Pattern.compile(normalCharacterRegex);
		Matcher m = normalCharacterPattern.matcher(w);
		if(!m.find())  {
			System.out.println("not matched");
		} else {
			System.out.println("Matched");
		}
		
	}
}

