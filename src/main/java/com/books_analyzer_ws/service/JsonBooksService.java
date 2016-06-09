package com.books_analyzer_ws.service;

import java.util.ArrayList;

import com.books_analyzer_ws.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
	import com.fasterxml.jackson.databind.ObjectMapper;

// BooksService provides the JSON needed for responding to the clients
public class JsonBooksService {
		private DBFastInterface dbInterface;
		private ArrayList<Book> books;

		public JsonBooksService() {
			this.dbInterface = new DBFastInterface();
			this.books = new ArrayList<Book>();
		}
		
		// This function builds and returns the JSON based on the ArrayList books, containing
		// data of every Books that will be returned to the user
		public String getBooksJSON() {
			ObjectMapper mapper = new ObjectMapper();
			try {
				return (("{\"books\":" + mapper.writeValueAsString(books) + "}").replaceAll("#", "'"));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		// GET /books or GET /books?title=...&author=...
		public String findBooks(String title, String author) {
			ArrayList<String> books = dbInterface.getCompletedBooks(title, author);
			String response = "{\"books\":[";
			for(String s: books) { response = response + s + ","; }
			response = response + "]}";
			return response.replace(",]", "]");
		}
		
		// GET /books/id
		public String findBookById(String id) {
			String[] titleAuthor = dbInterface.getTitleAuthorById(id);
			int bookState = dbInterface.getBookState(id);
			if(bookState==5) { // If the requested book is completely stored in the db
				books.add(dbInterface.importBookFromDB(id, titleAuthor[0],titleAuthor[1]));
				return getBooksJSON();
			} else {
				return "{\"status\":\"" + getStatusMessage(bookState) + "\"}";
			}
		}

		private String getStatusMessage(int bookState) {
			String status;
			switch(bookState) {
				case 0: status = "The book is being analyzed"; break;
				case 1: status = "The book content has been retrieved"; break;
				case 2: status = "The sentences are being processed"; break;
				case 3: status = "The characters are being stored"; break;
				case 4: status = "The attribution of Sentences to Characters are being stored"; break;
				default: status = "It seems that there is a problem with this book"; break;
			}
			return status;
		}

		// DELETE /books/id
		public String deleteBooks(String id) {
			return dbInterface.deleteBook(id);
		}
		
		// POST /books?title=...&author=...&url=...
		public String addBook(String title, String author, String url) {
			return UrlResourceService.getSingleAnalysisURL(title, author, url);
		}
		
		// PUT /books/id?title=...&author=...
		public String updateBookById(String id, String title, String author) {
			dbInterface.editBookById(id, title, author);
			return findBookById(id);
		}

	}

