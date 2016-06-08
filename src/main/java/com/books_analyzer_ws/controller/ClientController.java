package com.books_analyzer_ws.controller;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.books_analyzer_ws.service.BooksService;

// This controller is responsible for answering the petitions coming from the client MVC app
@RestController
public class ClientController {
    // This function is called when someone clicks on the "Buscar" buttons
	// It returns the id of the requested book in the DB, whether it is fully built or not
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
    	return booksService.getBookID(title, author, character, url);
    }
}