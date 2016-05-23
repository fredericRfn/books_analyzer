package com.books_analyzer.service;

import java.util.ArrayList;

import com.books_analyzer.Book;
import com.books_analyzer.BookFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
	import com.fasterxml.jackson.databind.ObjectMapper;

public class APIService {
		private DBInterface dbInterface;
		
		// This constructor is called from the search controller
		public APIService() {
			this.dbInterface = new DBInterface();
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
