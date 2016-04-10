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
	
	public Float references(Character character, ArrayList<Character> characters) {
		int involved = 0;
		for(String tmp:this.words) {
			for(Character c:characters){
				if(tmp.toLowerCase() == c.getName()) involved++;
			}
		}
		return (float) (involved > 0 ? 1/involved : 1); 
	}

}
