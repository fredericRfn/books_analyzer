package com.books_analyzer;

import java.util.ArrayList;

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
	
	public boolean references(Character character) {
		return this.content.toLowerCase().contains(character.getName().toLowerCase()); 
	}
}
