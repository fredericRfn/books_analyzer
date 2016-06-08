package com.books_analyzer_ws.service;

import java.util.ArrayList;

import com.books_analyzer_ws.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
	import com.fasterxml.jackson.databind.ObjectMapper;

// BooksService provides the JSON needed for responding to the clients
public class BooksService {
		private DBFastInterface dbInterface;
		private RabbitInterface rabbitInterface;
		private ArrayList<Book> books;
		
		// This constructor is called from the search controller
		public BooksService() {
			this.dbInterface = new DBFastInterface();
			this.books = new ArrayList<Book>();
		}
		
		// This function add a book to the Books table and send to the broker RabbitMQ
		// the parameters needed by the jobs to make the analysis and store the results in DB
		private int startBookCreationProcess(String title, String author, String url) {
			dbInterface.addBookData(title,author,url,0);// INSERT INTO Books(title, author, language, url, flag)
			int idJob = dbInterface.getBookId(title,author);
			rabbitInterface.buildAndSendMessage(dbInterface.getBookId(title,author));
			return idJob;
		}
		
		// This function builds and returns the JSON based on the ArrayList books, containing
		// data of every Books that will be returned to the user
		public String getJSON() {
			ObjectMapper mapper = new ObjectMapper();
			try {
				return (("{\"books\":" + mapper.writeValueAsString(books) + "}").replaceAll("#", "'"));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		// FUNCTIONS CALLED BY THE ClientController
		public String getBookID(String t, String a, String c, String u) {
			int id = dbInterface.getBookId(t, a);
			if(id<=0){ id = startBookCreationProcess(t,a,u); }
			return "{id:" + id + "}";
		}

		// FUNCTIONS CALLED BY THE APIController
		// GET /books or GET /books?title=...&author=...
		public String findBooks(String title, String author) {
			ArrayList<String> books = dbInterface.getBooks(title, author);
			String response = "{\"books\":[";
			for(String s: books) { response = response + s + ","; }
			response = response + "]}";
			return response.replace(",]", "]");
		}
		
		// GET /books/id
		public String findBookById(Integer id) {
			String[] titleAuthor = dbInterface.getTitleAuthorById(id);
			books.add(dbInterface.importBookFromDB(titleAuthor[0],titleAuthor[1]));
			return getJSON();
		}

		// DELETE /books/id
		public String deleteBooks(Integer id) {
			return dbInterface.deleteBook(id);
		}
		
		// POST /books?title=...&author=...&url=...
		public String addBook(String title, String author, String url) {
			//books.add(dbInterface.getBook(title,author,null,url));
			
			return getJSON();
		}
		
		// PUT /books/id?title=...&author=...
		public String updateBook(Integer id, String title, String author) {
			dbInterface.editBookById(id, title, author);
			return findBookById(id);
		}

	}

