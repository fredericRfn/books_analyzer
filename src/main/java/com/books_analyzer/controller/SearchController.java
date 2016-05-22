package com.books_analyzer.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.books_analyzer.service.ProcessBookService;


@RestController
public class SearchController {
	@CrossOrigin
    @RequestMapping(value = "/search", method = { RequestMethod.GET })
    public String search(
		@RequestParam(value = "title", required=false) String title,
		@RequestParam(value = "author", required=false) String author,
		@RequestParam(value = "character", required=false) String character,
		@RequestParam(value = "url", required=false) String url,
		@RequestParam(value = "callback", required=false) String callback
    )  {
    	ProcessBookService processBookService = new ProcessBookService();
    	return processBookService.buildResponse(title, author, character, url);
    }
}