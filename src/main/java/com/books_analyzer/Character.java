package com.books_analyzer;

import java.util.HashMap;

public class Character {
	public final String name;
	public HashMap<Sentence, Float> sentences;
	
	public Character(String n) {
		this.name = n;
	}
	
	public String getName() {
		return this.name; 
	}
	
	public void addSentence(Sentence s, Float probability) {
		sentences.put(s, probability); 
	}
}
