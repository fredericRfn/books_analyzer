package com.books_analyzer_ws.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.books_analyzer_ws.service.BooksService;

@RestController
public class ClientController {
	@CrossOrigin
    @RequestMapping(value = "/search", method = { RequestMethod.GET })
    public String search(
		@RequestParam(value = "title", required=false) String title,
		@RequestParam(value = "author", required=false) String author,
		@RequestParam(value = "character", required=false) String character,
		@RequestParam(value = "url", required=false) String url,
		@RequestParam(value = "callback", required=false) String callback
    )  {
    	BooksService booksService = new BooksService();
    	return booksService.buildResponse(title, author, character, url);
    }
}