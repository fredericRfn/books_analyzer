package com.books_analyzer_ws.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//This controller is only here to provide a really basic GUI in order to make some tests
//It will be erased, with all the HTML/CSS/Js contents when the webservice is fully implemented
@Controller
public class MVCDummyController {

    @RequestMapping("/")
    public String greeting() {
        return "index";
    }
}