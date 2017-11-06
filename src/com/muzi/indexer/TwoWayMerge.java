package com.muzi.indexer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class TwoWayMerge {
	
	static void ArrangeWords(BufferedReader in, BufferedWriter out) throws IOException{
		
		String line;
		
		ArrayList<String> list_of_words = new ArrayList<String> ();
 
		while (( line = in.readLine()) != null) {
			String[] words = line.split(" ");
			
			list_of_words.addAll(Arrays.asList(words));
			
			}
		Collections.sort(list_of_words);
		
		for(String str: list_of_words) {
			out.write(str + "\n");
			//System.out.println(str);
			}
		out.close();
		}
	
	static int comp(String a,String b){
		int compare = a.compareTo(b);
		if(compare < 0)
			return 1;
		else if(compare > 0)
			return 2;
		else
			return 3;
	}
	
	public static void mergeSortedFiles(BufferedReader in1,BufferedReader in2) throws IOException{
//		BufferedReader in1;
//		BufferedReader in2;
		String line1,line2;
		line1 = in1.readLine();
	    line2 = in2.readLine();
	    BufferedWriter out = new BufferedWriter(new FileWriter("/home/muzammil/Desktop/sorted.txt"));
		while (in1 != null || in1 != null) {
		    
		    int compare = comp(line1,line2);
		    
		    if(compare == 1){
		    	out.write(line1+"\n");
		    	System.out.println(line1);
		    	line1 = in1.readLine();
		    	
		    }
		    else if(compare == 2){
		    	out.write(line2+"\n");
		    	System.out.println(line2);
		    	line2 = in2.readLine();
		    }
		    else{
		    	out.write(line1+"\n");
		    	System.out.println(line1);
		    	line1 = in1.readLine();
		    	line2 = in2.readLine();
		    }
		    
		    
		    if(line1 == null || line2 == null){
		    	if(line1 == null){
		    		out.write(line2+"\n");
		    		System.out.println(line2);
		    		String tmp;
		    		while((tmp = in2.readLine()) != null){
		    			System.out.println(tmp);
		    			out.write(tmp+"\n");
		    		}
		    		
		    	}
		    	else if(line2 == null){
		    		String tmp;
		    		out.write(line1+"\n");
		    		System.out.println(line1);
		    		while((tmp = in1.readLine()) != null){
		    			out.write(tmp + "\n");
		    			System.out.println(tmp);
		    		}
		    	}
		    	out.close();
		    	//return 0;
		    }
		    
		    
		}
		//return 0;
    }

	
	
	
	public static void main(String[] args) throws IOException {
		BufferedReader in1 = null,in2 = null;
		BufferedWriter out1 = null,out2 = null;
		try {
			in1 = new BufferedReader(new FileReader("/home/muzammil/Desktop/input1.txt"));
			out1 = new BufferedWriter(new FileWriter("/home/muzammil/Desktop/sorted1.txt"));
			in2 = new BufferedReader(new FileReader("/home/muzammil/Desktop/input2.txt"));
			out2 = new BufferedWriter(new FileWriter("/home/muzammil/Desktop/sorted2.txt"));
		} catch (Exception e) {
		
			// TODO: handle exception
			System.out.println("Error: " + e);
		}
		ArrangeWords(in1,out1);
		ArrangeWords(in2,out2);
		in1 = new BufferedReader(new FileReader("/home/muzammil/Desktop/sorted1.txt"));
		in2 = new BufferedReader(new FileReader("/home/muzammil/Desktop/sorted2.txt"));
		mergeSortedFiles(in1,in2);
	}
		
}

