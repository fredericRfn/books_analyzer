package com.books_analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Book {
	private int id;
	public final String title;
	public final String author;
	public ArrayList<Character> characters;
	private ArrayList<Sentence> sentences;
	private ArrayList<CharacterSentence> characterSentence;
	
	public Book(String t, String author, String content) {
		this.title = t;
		this.author = author;
		this.characters = new ArrayList<Character>();
		this.sentences = new ArrayList<Sentence>();
		this.characterSentence = new ArrayList<CharacterSentence>();
		System.out.println("Step1: generating sentences");
		generateSentences(content);
		System.out.println("Step2: generating characters");
		generateCharacters();
		System.out.println("Step3: generating associations");
		generateAssociation();
	}

	private void generateSentences(String cont) {
		String c = cont.replace("\r", "");
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
