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
	private DBInterface dbInterface;
	
    ObjectMapper mapper = new ObjectMapper();
	
	// This constructor is called from the search controller
	public BooksProcessor(String t, String a, String c, String u) {
		this.books = new ArrayList<Book>();
		this.titleParam = t;
		this.authorParam = a;
		this.characterParam = c;
		this.urlParam = u;
		this.dbInterface = new DBInterface();
		System.out.println(String.join(",", t,a,c,u));
	}
	
	// This method executes the computations based on the parameters given to the BooksProcessor
	// Will be implemented as a run() method in a Thread when various petitions will occur at the same time
	
	// WARNING : For now, this function is designed only to process ONE BOOK
	public Book process() {
		Integer idInDB; // id of the book in the database
		Book book = null;
		boolean hasSucceeded = false;
		
		// If the book is provided by the client
		if(!urlParam.isEmpty() && titleParam!=null && authorParam!=null) { 
			book = new Book(this.titleParam, this.authorParam, getTxtFromUrl());
			books.add(book);
			hasSucceeded = true;
			// Add to our database the book generously given by the client
			// Beware: we might want to flag this book as "unverified"

		}
		// If the book is not provided by the client
		else if(urlParam.isEmpty()){ // Let's check our database
			idInDB = dbInterface.getBookId(this.titleParam, this.authorParam);
			if(idInDB > 0) { // if the book exists in our database
				book = dbInterface.importBookFromDatabase(idInDB);
				books.add(book);
				hasSucceeded = true;
			}
			// If the book does not exist in our database and is not provided by the client
			else hasSucceeded = false; // We are screwed
		}	
		
		generateJSON(hasSucceeded);
		return book;
	}
	
	public DBInterface getInterface() {
		return dbInterface;
	}
	public String getJSON() {
    	return json;
	}
	
	// This function maps the data of a book into a JSON
	private void generateJSON(boolean dataAvailable) {
		if (dataAvailable) {
	    	try  {
	    		//This is the part generating the JSON
	    		System.out.println("Generating the json...");
	    		json = "{\"books\":" + mapper.writeValueAsString(this.books) + "}";
	    	} catch (JsonProcessingException e) { 
	    		json= "{\"error\":\"Error during the JSON generation\"}";
	    	}
		} else {
			json= "{\"error\":\"Sorry, your request could not be processed because"
					+ "data are lacking.\n "
					+ "Providing the title, the author and the URL to a .txt"
					+ "file containing the book will solve the issue.\"}";
		}
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
