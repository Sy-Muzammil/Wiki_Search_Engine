package com.muzi.indexer;

/*class Word {
	String word;
	List<Entry> documents;
}
*/

public class Entry {
	
	int docId;
	int body_count = 0;
	int title_count = 0;
	int category_count = 0;
	int infobox_count = 0;
	// external link // references
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "d" + docId +
				"b" + body_count +
				"t" + title_count +
				"c" + category_count +
				"i" + infobox_count +";";
 	}
}
