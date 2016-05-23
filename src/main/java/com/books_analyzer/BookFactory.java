package com.books_analyzer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import com.books_analyzer.service.DBInterface;

public class BookFactory {
	private DBInterface dbInterface;
	// This constructor is called from the search controller
	public BookFactory() {
		this.dbInterface = new DBInterface();
	}
	
	public Book buildBook(String title, String author, String character, String url) {
		Book book = null;
		// If the book is provided by the client
		if(url!=null && title!=null && author!=null) { 
			book = new Book(title, author);
			book.analyzeCharacters(getTxtFromUrl(url));
			dbInterface.exportToDatabase(book);
		}
		// If the book is not provided by the client
		else if(url==null){ // Let's check our database
			book = new Book(title, author);
			book.addCharactersFromDB(dbInterface.importCharactersFromDB(title, author));
		}
		return book;
	}
	
	private String getTxtFromUrl(String urlString) {
		System.out.println("Fetching content from:" + urlString);
		StringBuilder sb = new StringBuilder();
		URLConnection urlConn = null;
		InputStreamReader in = null;
		try {
			URL url = new URL(urlString);
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
			throw new RuntimeException("A problem occured while calling URL:"+ urlString, e);
		} 
 
		return sb.toString();
	}
}
