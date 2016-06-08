package com.books_analyzer_ws.service;

import java.util.ArrayList;

import com.books_analyzer_ws.Book;
import com.books_analyzer_ws.BookFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
	import com.fasterxml.jackson.databind.ObjectMapper;

public class BooksService {
		private DBInterface dbInterface;
		private ArrayList<Book> books;
		private BookFactory bookFactory;
		
		// This constructor is called from the search controller
		public BooksService() {
			this.dbInterface = new DBInterface();
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


		public String findBooks(String title, String author) {
			ArrayList<String> books = dbInterface.getBooks(title, author);
			String response = "{\"books\":[";
			for(String s: books) { response = response + s + ","; }
			response = response + "]}";
			return response.replace(",]", "]");
		}

		public String deleteBooks(Integer id) {
			return dbInterface.deleteBook(id);
		}

		public String addBook(String title, String author, String url) {
			Book book = new BookFactory().buildBook(title,author,null,url);
			ObjectMapper mapper = new ObjectMapper();
			try {
				return ("{\"books\":" + mapper.writeValueAsString(book) + "}".replaceAll("#", "'"));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return null;
			}
		}

		public String findBookById(Integer id) {
			String[] titleAuthor = dbInterface.getTitleAuthorById(id);
			Book book = new BookFactory().buildBook(titleAuthor[0],titleAuthor[1], null,null);
			ObjectMapper mapper = new ObjectMapper();
			try {
				return ("{\"books\":" + mapper.writeValueAsString(book) + "}".replaceAll("#", "'"));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return null;
			}
		}

		public String updateBook(Integer id, String title, String author) {
			dbInterface.editBookById(id, title, author);
			return findBookById(id);
		}


	}

