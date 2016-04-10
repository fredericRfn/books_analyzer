package com.books_analyzer;

public class Sentence {
	public final String content;
	private String[] words;
	
	public Sentence(String s) {
		this.content = s;
		this.words = s.replace("\\", "").replace("'s", "").replace("\"", "").replace(".", "").replace(",", "").replace("?", "").replace("!","").split("\\s|'");
	}
	
	public String getContent() {
		return this.content;
	}
	
	public String[] getWords() {
		return this.words;
	}

}
