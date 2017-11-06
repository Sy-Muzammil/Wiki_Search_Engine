//package com.muzi.indexer;

/*import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class Indexing_Words {
	
	class Word_Index{
		int w_freq;
		String Index;
	}
	// contains <word <w_freq,Did/Dtitle>> (for this time) for whole document 
	Map<String,ArrayList<Object>> Index_Map = new HashMap<String, ArrayList<Object>>();	
	
	SAX_Parser S = new SAX_Parser();
	int len = S.getsize();
	Word_Index wi;
	//Pages page = new Pages();
	
	void word_frequency(){
		//contains <word,frequency> for each page each time
		
		HashMap<String,Integer> mymap;
		// Object created of class Word_Index
		wi = new Word_Index();
		
		for(int i = 0; i<len;i++){
			mymap = new HashMap<String,Integer>();
			
			Object obj = S.Info.get(i);
			String Doc = S.get_text((Pages) obj);// Is it True
			StringBuilder builder = new StringBuilder(Doc);
	        String[] words = builder.toString().split("\\s");
	        
	        for (String word : words) {
        	    Integer cnt = mymap.get(word);
        	    if (cnt == null) 
    	            cnt = 0;
        	    ++cnt;
    	        mymap.put(word, cnt);
        	 }
		
		
	        // temporary array of objects()
			ArrayList <Object> arr = new ArrayList<Object>();
			
			for (Entry<String, Integer> entry : mymap.entrySet()) {
			    
				String key = entry.getKey();
			    Integer f = entry.getValue();
			    wi.w_freq = f; 
			    wi.Index = "I" + S.get_id((Pages) obj) + "/" + "T" + S.get_title((Pages) obj);
			    arr.add(wi);
			    
			    Index_Map.put(key,arr);
			}
		}
	}

}
*/