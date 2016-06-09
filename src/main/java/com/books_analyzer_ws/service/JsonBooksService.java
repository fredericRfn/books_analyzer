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
			String response = "{\"books\":[\"";
			for(String s: books) { response = "\"" + response + s + "\","; }
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
				return getMessage(id, bookState);
			}
		}

		private String getMessage(String id, int bookState) {
			String message;
			String type = "status";
			switch(bookState) {
				case 0: message = "The book has been queued, waiting for a worker to process it"; break;
				case 1: message = "The book content has been retrieved"; break;
				case 2: message = "The sentences are being processed"; break;
				case 3: message = "The characters are being stored"; break;
				case 4: message = "The attribution of Sentences to Characters are being stored"; break;
				default: 
					message = "Sorry, in the end, it was not possible to process this book";
					type = "error";
					deleteBooks(id);
					break;
			}
			return "{\"" + type + "\":\""+ message + "\"}";
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

