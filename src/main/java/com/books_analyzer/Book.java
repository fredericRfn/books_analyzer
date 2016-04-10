package com.books_analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Book {
	private int id;
	public final String title;
	public final String author;
	public final String content;
	public final String language;
	public ArrayList<Character> characters;
	private ArrayList<Sentence> sentences;
	private ArrayList<CharacterSentence> characterSentences;
	
	public Book(String t, String author, String c) {
		this.title = t;
		this.author = author;
		this.content = c;
		this.language = "EN";
		this.characters = new ArrayList<Character>();
		this.sentences = new ArrayList<Sentence>();
		this.characterSentences = new ArrayList<CharacterSentence>();
		System.out.println("Step1: generating sentences");
		generateSentences();
		System.out.println("Step2: generating characters");
		generateCharacters();
		System.out.println("Step3: generating associations");
		generateAssociation();
	}
	
	public String getTitle() {
		return this.title;
	}
	public String getAuthor() {
		return this.title;
	}
	public String getLanguage() {
		return this.title;
	}
	public String getContent() {
		return this.content;
	}
	public ArrayList<Sentence> getSentences() {
		return sentences;
	}
	
	public ArrayList<Character> getCharacters() {
		return characters;
	}
	
	public ArrayList<CharacterSentence> getCharacterSentences() {
		return characterSentences;
	}

	private void generateSentences() {
		String c = this.content.replace("\r", "");
		String[] rawSentences = c.split("\n|\\.(?!\\d)|(?<!\\d)\\.");
		for (int i=0; i<rawSentences.length; i++) {
			this.sentences.add(new Sentence(rawSentences[i]));
		}
	}
	
	public void generateCharacters() {
		String[] words;
		int count;
		HashMap<String, Integer> wordsWithCount = new HashMap<String, Integer>();
		HashMap<String, Integer> wordsUpperWithCount = new HashMap<String, Integer>();
		for(Sentence tmp: this.sentences) {
			words = tmp.getWords();
			if (words.length > 1) {
				for (int j=0; j<words.length; j++) {
					if (!words[j].isEmpty()) {
						if (java.lang.Character.isUpperCase(words[j].charAt(0))) {
							count = wordsUpperWithCount.containsKey(words[j].toLowerCase()) ? wordsUpperWithCount.get(words[j].toLowerCase()) : 0;
							wordsUpperWithCount.put(words[j].toLowerCase(), count + 1); 
						}
					}
					count = wordsWithCount.containsKey(words[j].toLowerCase()) ? wordsWithCount.get(words[j].toLowerCase()) : 0;
					wordsWithCount.put(words[j].toLowerCase(), count + 1); 
				}
			}
		}
		Set<String> keySet = wordsUpperWithCount.keySet();
		Iterator<String> iterator = keySet.iterator();
	    String tmp;
		while(iterator.hasNext()) {
	    	tmp = (String) iterator.next();
	    	if ((wordsUpperWithCount.get(tmp) == wordsWithCount.get(tmp)) &&  (wordsUpperWithCount.get(tmp) > 2)) {
	    		this.characters.add(new Character(tmp));
	    	}
	    }
	}
	public void generateAssociation() {
		for(Character c: this.characters) {
			System.out.println("generateAssociation(): character:" + c.getName());
			for(Sentence s: this.sentences) {
				if (s.references(c)) { //references will return a probability of the character to be the focus point of the sentence
					//characterSentence.add(new CharacterSentence(c,s,s.references(c, this.characters)));
					c.addSentence(s);
					System.out.println("Sentence:" + s.getContent() + " added");
				}
			}
		}
	}
}
