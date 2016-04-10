package com.books_analyzer;

import java.util.ArrayList;

public class Character {
	public final String name;
	public ArrayList<String> sentences;
	
	public Character(String n) {
		this.name = n;
		this.sentences = new ArrayList<String>();
	}
	
	public String getName() {
		return this.name; 
	}
	
	public void addSentence(Sentence s) {
		System.out.println(s.getContent() + " added to character " + name);
		sentences.add(s.getContent()); 
	}
}
