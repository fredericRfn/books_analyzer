package com.books_analyzer_ws.service;

import java.util.ArrayList;

import com.books_analyzer_ws.Book;
import com.books_analyzer_ws.BookFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Light class which generates the final JSON response

public class ProcessBookService {
	private ArrayList<Book> books;
	private BookFactory bookFactory;
	
	// This constructor is called from the search controller
	public ProcessBookService() {
		this.bookFactory = new BookFactory();
		this.books = new ArrayList<Book>();
	}

	public String buildResponse(String t, String a, String c, String u) {
		this.books.add(bookFactory.buildBook(t,a,c,u));
		ObjectMapper mapper = new ObjectMapper();
		try {
			return ("{\"books\":" + mapper.writeValueAsString(this.books) + "}".replaceAll("#", "'"));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
