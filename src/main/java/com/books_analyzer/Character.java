package com.books_analyzer;

import java.util.HashMap;

public class Character {
	public final String name;
	public HashMap<Sentence, Integer> sentences;
	
	public Character(String n) {
		this.name = n;
	}
	
	public void addSentences(Sentence s, Integer probability) {
		sentences.put(s, probability);
	}
}
