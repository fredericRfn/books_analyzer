package com.books_analyzer.service;

import com.books_analyzer.Book;

public class ExportThread extends Thread{
	Book book; 
	DBInterface dbi;
	public ExportThread(Book b, DBInterface d) {
			super();
	        book = b;
	        dbi = d;
	    }
	    public void run() {
	        dbi.exportToDatabase(book);
	    }
}
