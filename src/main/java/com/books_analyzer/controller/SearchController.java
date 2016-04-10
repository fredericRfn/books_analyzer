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
    	System.out.println("SeachController: booksProcessor ready, parsing the book from url");
    	booksProcessor.parseBookFromURL(url);
    	Book b = booksProcessor.getBook(0);
    	String results = "fail";
    	try  {
    		//This is the part generating the JSON
    		System.out.println("Generating the json...");
    		results = mapper.writeValueAsString(b);
    	} catch (JsonProcessingException e) {}
    	return results;
    }
}