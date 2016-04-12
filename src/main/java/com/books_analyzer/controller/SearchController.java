package com.books_analyzer.controller;

import java.awt.print.Book;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.books_analyzer.service.BooksProcessor;
import com.books_analyzer.service.ExportThread;


@RestController
public class SearchController {
    BooksProcessor booksProcessor;
    
    @RequestMapping(value = "/search", method = { RequestMethod.GET })
    public String search(
		@RequestParam(value = "title", required=false) String title,
		@RequestParam(value = "author", required=false) String author,
		@RequestParam(value = "character", required=false) String character,
		@RequestParam(value = "url", required=false) String url
    )  {
    	System.out.println("SeachController: The parameters received are-");
    	System.out.println("SeachController: " + String.join(",", title, author, character, url));
    	booksProcessor = new BooksProcessor(title, author, character, url);
    	com.books_analyzer.Book book = booksProcessor.process();
    	ExportThread sqlExport = new ExportThread(book, booksProcessor.getInterface());
    	sqlExport.start();
    	System.out.println("SeachController: Analysis complete");
    	return booksProcessor.getJSON();
    	
    }
}