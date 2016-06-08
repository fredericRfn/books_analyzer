package com.books_analyzer_ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Book {
	public final String title;
	public final String author;
	public final String language;
	@JsonIgnore private String url;
	public ArrayList<Character> characters;
	@JsonIgnore private ArrayList<Sentence> sentences;
	@JsonIgnore private ArrayList<CharacterSentence> characterSentences;
	
	public Book(String t, String a, String u) {
		this.title = t;
		this.author = a;
		this.url = u;
		this.language = "EN";
		this.sentences = new ArrayList<Sentence>();
		this.characters = new ArrayList<Character>();
		this.characterSentences = new ArrayList<CharacterSentence>();
	}
	
	public String getTitle() { return this.title; }
	public String getAuthor() { return this.author; }
	public String getLanguage() { return this.language; }
	public String getUrl() { return this.url; }
	public ArrayList<Sentence> getSentences() { return this.sentences; }
	public ArrayList<Character> getCharacters() { return this.characters; }
	public ArrayList<CharacterSentence> getCharacterSentences() { return this.characterSentences; }

	public void setCharacters(ArrayList<Character> c) {
		this.characters = c;
	}
}
