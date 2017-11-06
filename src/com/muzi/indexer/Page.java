package com.muzi.indexer;


public class Page implements Cloneable {
	
		int id;
		
		String title;
		String text;
		
		String category;
		String references;
		String infboxes;
		
		String cleanedText;
		String tok_catg;
		String tok_info;

		@Override
		protected Object clone() throws CloneNotSupportedException {
			Page p = new Page();
			p.id = this.id;
			p.title = this.title;
			p.text = this.text;
			return p;
		}		
		
		Page() {
//			System.out.println("Creating page object");
		}
		
		@Override
		public String toString() {

			return "<doc_title>: " + getTitle() + "\n" + 
			"<doc_id>: " + getId() + "\n" +
			"<body>: " + getText() + "\n";

		}
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		

		
}
