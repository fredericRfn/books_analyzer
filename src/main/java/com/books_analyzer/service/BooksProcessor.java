package com.books_analyzer.service;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.springframework.boot.autoconfigure.web.ServerProperties.Session;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.books_analyzer.Book;

@Service
public class BooksProcessor {
	ArrayList<Book> books;
	public String url;
	
	public BooksProcessor() {
		this.books = new ArrayList<Book>();
	}
	
	public void parseBookFromURL(String u) {
		books = new ArrayList<Book>();
		this.url = u;
		books.add(new Book("Harry Potter And The Sorcerer's Stone", "J.K Rowling", getTxtFromUrl(u)));
	}
	
	public Book getBook(int index) {
		return books.get(index);
	}
	
	public static String getTxtFromUrl(String myURL) {
		System.out.println("Requested URL:" + myURL);
		StringBuilder sb = new StringBuilder();
		URLConnection urlConn = null;
		InputStreamReader in = null;
		try {
			URL url = new URL(myURL);
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
			throw new RuntimeException("Exception while calling URL:"+ myURL, e);
		} 
 
		return sb.toString();
	}
}
