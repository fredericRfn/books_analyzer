package com.books_analyzer.service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.books_analyzer.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/* This class is responsible for processing the Client's request by 
 * instantiating the Books, Sentences and Characters needed *
 * Here is the work flow:
 * 1) Receive the parameters Title, Author, Character, URL
 * 2) Retrieve data.
 * 		TWO cases :
 * 			a) Everything needed is already in the database => Retrieve, instantiate, return JSON
 * 			b) The data does not exist but we have a URL
 * 				=> Execute parsers to get the metadata (author, language if not provided in the parameters)
 * 				=> Execute algorithms to get the characters, and instantiate everything needed
 * 				=> Return the JSON including the results to the client
 * 				=> Prepare the SQL code (insert into the tables the aforementioned instances)
 * 				=> Execute it
 * 				=> Check if the execution was successful
 */

public class BooksProcessor {
	private ArrayList<Book> books;
	private final String titleParam;
	private final String authorParam;
	private final String characterParam;
	private final String urlParam;
	private String json;
	
    ObjectMapper mapper = new ObjectMapper();
	
	// This constructor is called from the search controller
	public BooksProcessor(String t, String a, String c, String u) {
		this.books = new ArrayList<Book>();
		this.titleParam = t;
		this.authorParam = a;
		this.characterParam = c;
		this.urlParam = u;
	}
	
	// This method executes the computations based on the parameters given to the BooksProcessor
	// Will be implemented as a run() method in a Thread when various petitions will occur at the same time
	public void process() {
		if(!urlParam.isEmpty()) {
			books.add(new Book(this.titleParam, this.authorParam, getTxtFromUrl()));
		}
    	try  {
    		//This is the part generating the JSON
    		System.out.println("Generating the json...");
    		json = "{books:" + mapper.writeValueAsString(this.books) + "}";
    	} catch (JsonProcessingException e) { 
    		json= "{error:\"Unable to generate JSON\"}";
    	}
	}
	
	public String getJSON() {
    	return json;
	}
	
	private String getTxtFromUrl() {
		System.out.println("Fetching content from:" + this.urlParam);
		StringBuilder sb = new StringBuilder();
		URLConnection urlConn = null;
		InputStreamReader in = null;
		try {
			URL url = new URL(this.urlParam);
			urlConn = url.openConnection();
			urlConn.setRequestProperty("User-Agent", "Chrome/23.0.1271.95");
			if (urlConn != null)
				urlConn.setReadTimeout(60000);
			if (urlConn != null && urlConn.getInputStream() != null) {
				in = new InputStreamReader(urlConn.getInputStream(),
						Charset.defaultCharset());
				BufferedReader bufferedReader = new BufferedReader(in);
				if (bufferedReader != null) {
					int cp;
					while ((cp = bufferedReader.read()) != -1) {
						sb.append((char) cp);
					}
					bufferedReader.close();
				}
			}
		in.close();
		} catch (Exception e) {
			throw new RuntimeException("A problem occured while calling URL:"+ this.urlParam, e);
		} 
 
		return sb.toString();
	}
}
