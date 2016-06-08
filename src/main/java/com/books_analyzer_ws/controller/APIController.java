package com.books_analyzer_ws.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.books_analyzer_ws.service.APIService;

@RestController
public class APIController {
	@CrossOrigin
    @RequestMapping(value = "/books", method = { RequestMethod.GET })
    public String getBooks(
    	@RequestParam(value = "title", required=false) String title,
    	@RequestParam(value = "author", required=false) String author
    )  {
    	APIService apiService = new APIService();
    	return apiService.findBooks(title,author);
    }
	@CrossOrigin
    @RequestMapping(value = "/books/{id}", method = { RequestMethod.GET })
    public String getBook(@PathVariable("id") Integer id )  {
    	APIService apiService = new APIService();
    	return apiService.findBookById(id);
    }
	@CrossOrigin
    @RequestMapping(value = "/books/{id}", method = { RequestMethod.PUT })
    public String editBook(
    	@PathVariable("id") Integer id,
    	@RequestBody(required=false) String title,
    	@RequestBody(required=false) String author
    )  {
    	APIService apiService = new APIService();
    	return apiService.updateBook(id, title, author);
    }
	@CrossOrigin
    @RequestMapping(value = "/books/{id}", method = { RequestMethod.DELETE })
    public String deleteBook(@PathVariable("id") Integer id)  {
    	APIService apiService = new APIService();
    	return apiService.deleteBooks(id);
    }
	@CrossOrigin
    @RequestMapping(value = "/books", method = { RequestMethod.POST })
    public String createBook (
        @RequestParam(value = "title", required=false) String title,
    	@RequestParam(value = "author", required=false) String author,
    	@RequestParam(value = "url", required=false) String url
    )  {
    	APIService apiService = new APIService();
    	return apiService.addBook(title,author,url);
    }
}