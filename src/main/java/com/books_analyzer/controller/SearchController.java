package com.books_analyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.books_analyzer.Book;
import com.books_analyzer.service.BooksProcessor;


@RestController
public class SearchController {
    BooksProcessor booksProcessor;
    ObjectMapper mapper = new ObjectMapper();
   
    @RequestMapping("/search")
    public String search(@RequestParam("url") String url)  {
    	booksProcessor = new BooksProcessor();
    	booksProcessor.parseBookFromURL(url);
    	Book b = booksProcessor.getBook(0);
    	// It is necessary to return a result in JSON
    	/* A basic idea for the search from 1 book could be:
    	  {
		  		book: {
		  			title: ,
		  			author: ,
		  			language:,
		  			sentences: [] (temporarily)
		  			characters:[]
		  		}
    	  	}
    	  }
    	 */
    	/* However: for now we want to display the raw results too; so
    	  we will make the first 
    	 */
    	String results = "fail";
    	try  {
    		results = mapper.writeValueAsString(b);
    	} catch (JsonProcessingException e) {}
    	return results;
    }
}