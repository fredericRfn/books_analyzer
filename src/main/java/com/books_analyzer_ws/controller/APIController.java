package com.books_analyzer_ws.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.books_analyzer_ws.service.JsonBooksService;

//This controller is responsible for answering the requests coming from the API
//Except GET /books/id, called both by the API and the client
@RestController
public class APIController {
	@CrossOrigin
    @RequestMapping(value = "/books", method = { RequestMethod.GET })
    public String getBooks(
    	@RequestParam(value = "title", required=false) String title,
    	@RequestParam(value = "author", required=false) String author
    )  {
		JsonBooksService booksService = new JsonBooksService();
    	return booksService.findBooks(title,author);
    }
	@CrossOrigin
    @RequestMapping(value = "/books/{id}", method = { RequestMethod.GET })
    public String getBook(@PathVariable("id") Integer id )  {
		JsonBooksService booksService = new JsonBooksService();
    	return booksService.findBookById(id);
    }
	@CrossOrigin
    @RequestMapping(value = "/books/{id}", method = { RequestMethod.PUT })
    public String editBook(
    	@PathVariable("id") Integer id,
    	@RequestBody(required=false) String title,
    	@RequestBody(required=false) String author
    )  {
		JsonBooksService booksService = new JsonBooksService();
    	return booksService.updateBookById(id, title, author);
    }
	@CrossOrigin
    @RequestMapping(value = "/books/{id}", method = { RequestMethod.DELETE })
    public String deleteBook(@PathVariable("id") Integer id)  {
		JsonBooksService booksService = new JsonBooksService();
    	return booksService.deleteBooks(id);
    }
	@CrossOrigin
    @RequestMapping(value = "/books", method = { RequestMethod.POST })
    public String createBook (
        @RequestParam(value = "title", required=false) String title,
    	@RequestParam(value = "author", required=false) String author,
    	@RequestParam(value = "url", required=false) String url
    )  {
		JsonBooksService booksService = new JsonBooksService();
    	return booksService.addBook(title,author,url);
    }
}