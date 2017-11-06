package com.muzi.indexer;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.XMLConstants;

public class SAX_Parser extends DefaultHandler {

	private static boolean debug = false;
	private int BUNCH_SIZE = 100;
	 /*
	 * state 0 : outside
	 * state 1 : in-page	page_flag = 0 : out-page
	 * 						page_flag = 1 :in-page 
	 * state 2 : title 
	 * state 3 : id			id_flag = 0 : out-page
	 * 						id_flag = 1 :in-page 
	 * 						id_flag = 2 :dont-acess
	 * state 4 : in-text	text_flag = 0 : out-text
	 * 						text_flag = 1 :in-text
	 * state 0 : outside
	 * */
	static long start;
	int state = 0;
	int page_flag = 0;
	int id_flag = 0;
	int text_flag = 0;
	int title_flag = 0;
	long pageCounter = 0;
	//static File file = new File("English_Wiki.txt");
	PageBunchExecutor exe;
	List<Thread> allThreads;
	FileWriter fp;
	BufferedWriter bw;
	static BufferedWriter tw; 
	ArrayList<Page> allPages;
	StringBuilder holdText;
	Page page ; //= new Page();
	//ExecutorService executor;
    ThreadPoolExecutor executor;
    int THREAD_POOL_SIZE = 30;
	
	public SAX_Parser() {
		
		allPages = new ArrayList<Page>();
		/*fp = new FileWriter(file);
		bw = new BufferedWriter(fp);*/
		allThreads = new ArrayList<Thread>();
	}

    public void initThreadPool() {
        BlockingQueue<Runnable> q = new ArrayBlockingQueue<Runnable>(20);
        executor = new ThreadPoolExecutor(4, 8, 20, TimeUnit.SECONDS, q);
		//executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }
	
	@Override
	public void startElement(String uri, String name, String qName,
			Attributes atts) {
		
		// if want to restart from some middle page xml tag
		// hardcode if statement with recorded id and set state = 0
		// do same in characters and in endElement
		
		if (qName.equals("page") && state == 0) {
			state += 1;
			page_flag = 1;
			if(debug)
				System.out.println("got page start");
			page = new Page();
		} else if (qName.equals("title") && state == 1) {
			holdText = new StringBuilder();
			if(debug)
				System.out.println("Got title start");
			state += 1;
			title_flag = 1;
		} else if (qName.equals("id") && state == 2) {
			if(debug)
				System.out.println("got id start");
			holdText = new StringBuilder();
			state += 1;
			id_flag += 1;
			if (id_flag == 2)
				System.err.println("Error: Wrong ID Accessed");
		} else if (qName.equals("text") && state == 3) {
			if(debug)
				System.out.println("got text start");
			holdText = new StringBuilder();
			state += 1;
			text_flag = 1;

		}
	}

	/**
	 * This method is used to stop printing information once the element has
	 * been read. It will also reset the onVacation variable for the next
	 * element.
	 */
	@Override
	public void endElement(String uri, String name, String qName){
		// reset flag
		if (qName.equals("text") && state == 4) {
			text_flag = 0;
			page.text = holdText.toString();
		} else if (qName.equals("title") && state == 2 && title_flag == 1) {
			title_flag = 0;
			page.title = holdText.toString();
//			System.out.println("title " + page.getTitle());
		} else if (qName.equals("id") && state == 3 && id_flag == 1) {
			id_flag -= 1;
			page.id = Integer.parseInt(holdText.toString());
			/*if(pageCounter%1000 == 0) {
				System.out.println("page id done : " +page.id);  // record this manually
			}*/
		} else if (qName.equals("page") && state == 4) {
			state = 0;
			page_flag = 0;
			if(page != null ) {
//				System.err.println("page object" + page);
				allPages.add(page);
//				System.out.println("allPages size " + allPages.size());
				if(allPages.size() == BUNCH_SIZE) {
					// begin thread processing
					PageBunchExecutor exe = new PageBunchExecutor(allPages, pageCounter);
//					exe.start(); // thread starting
					allThreads.add(exe);
					
                    
                    boolean added = false;
                    int attempt = 1;
                    int sleepDelay = Utils.readThrottleSleepTime();
                    while( added == false ) {
                        try {
                            executor.execute(exe);
                            added = true;
                        } catch(RejectedExecutionException e) {
                        	
                            System.out.println("Throttled, trying for attempt no " + attempt);
                            attempt++;
                            System.out.println("Sleeping for" + sleepDelay +" secs to backoff, current pageCounter = " + (pageCounter+1));
	                        try {   
	                            Thread.sleep(sleepDelay*1*1000);
	                        } catch (InterruptedException e2) {
	                        }
                        }
                    }
                    /*if((pageCounter+1)%8000 == 0 ) {
                        try {
                            System.out.println("Sleeping for 1 min 40 secs to backoff, current pageCounter = " + (pageCounter+1));
                            Thread.sleep(60*1*1000 +  40*1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }*/
                    allPages = null;
					//allPages.clear();
                    allPages = new ArrayList<Page>();
					
					
					System.out.println("Number of pages done " + (pageCounter+1) + ", last pageId processed " + page.id);
					/*System.out.println("**Sleeping**");
					try {
						Thread.sleep(30*1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("**woke up**");*/
				}
				// disabled, enable if need to debug SAX_Parser
				//writePageToFile(page);
			}
			pageCounter++;
			
		}
		// reinitialize
		if (state == 0) {
			holdText = new StringBuilder();
		}
	}

	/*private void writePageToFile(Page page) {
		try {
			bw.write( page.toString() + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * This method is triggered to store and print the values between the XML
	 * tags. It will only print those values if onVacation == true.
	 */
	@Override
	public void characters(char[] ch, int start, int length) {
		if(state > 1) {
			if (state == 2 && page_flag == 1 && title_flag == 1){
	        	for (int i = start; i < start + length; i++)
	                holdText.append(ch[i]);
	        }
	        
	        else if (state == 3 && id_flag == 1){
	        	for (int i = start; i < start + length; i++)
	        		holdText.append(ch[i]);   
	        	
	        }
	        
	        else if (state == 4 && page_flag == 1 ){
	        	if(text_flag == 1)
		        	for (int i = start; i < start + length; i++)
		        		holdText.append(ch[i]);
	        }
		}
	}

	public int getsize() {
		// TODO Auto-generated method stub
		return allPages.size();
	}

	@Override
	public void endDocument() throws SAXException {
//		System.out.println("allPages size " + allPages.size() );
		PageBunchExecutor exe = new PageBunchExecutor(allPages, true);
		allThreads.add(exe);
//		exe.start(); // thread starting
		System.out.println("Number of pages done (resedue) " + allPages.size() + ", total done:" + (pageCounter+1) + ", last pageId processed " + page.id);

		executor.execute(exe);
		allPages.clear();
		
		
		executor.shutdown();
		while (!executor.isTerminated()) {
            try { 
                Thread.sleep(1000*15);
            } catch(Exception e) {
                System.out.println("woke up thread.sleep");
            }
		}
		
	}
	
	
	
	
	public static void main(String[] args) throws InterruptedException, IOException {
		System.out.println("version number " + 8);
		//tw = new BufferedWriter(new FileWriter(TITLE_PATH,true));
		start = System.currentTimeMillis();
		SAX_Parser handler = new SAX_Parser();
		handler.BUNCH_SIZE = Integer.parseInt(args[0]);
//		handler.THREAD_POOL_SIZE = Integer.parseInt(args[1]);
        handler.initThreadPool();
		try {
			// create a SAX parser from the Xerces package
			XMLReader xml = XMLReaderFactory.createXMLReader();
			xml.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			xml.setContentHandler(handler);
			xml.setErrorHandler(handler);
			FileReader r = new FileReader(
					Utils.BASE_PATH + 
                        "readonly/enwiki-latest-pages-articles-multistream.xml"
                    );
				xml.parse(new InputSource(r));
			//enwiki-20170820-pages-articles15.xml-p9244803p95180
            //"readonly/enwiki-20170820-pages-articles15.xml-p9244803p95180"
			//handler.processReducePages();
		
			System.out.println("Total pages done " + handler.pageCounter);
			//int tc = 0;
			for(Thread t : handler.allThreads) {
				//System.out.println("page done with: "+ t);
				//System.out.println("Runtime" + tc +": " + (System.currentTimeMillis() - start)/1000 + "s");
				//tc++;
				t.join();	
			}
			//close all bufferWriters
			Indexer.closeBufferWriters();
			//tw.close();
			for (HashMap.Entry<String, BufferedWriter> entry : Utils.title_Writers.entrySet()){
				entry.getValue().close();
			}
			System.out.println("Runtime " + (System.currentTimeMillis() - start)/1000 + "s");
			
		} catch (SAXException se) {
			System.out.println("XML Parsing Error: " + se);
		} catch (IOException io) {
			System.out.println("File I/O Error: " + io);
		} finally {
			/*try {
				handler.bw.close();
				handler.fp.close();
			} catch(Exception e) {
				e.printStackTrace();
			}*/
		}
	}
	

}
