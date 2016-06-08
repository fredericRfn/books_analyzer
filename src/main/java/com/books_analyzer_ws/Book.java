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
	public ArrayList<Character> characters;
	@JsonIgnore private ArrayList<Sentence> sentences;
	@JsonIgnore private ArrayList<CharacterSentence> characterSentences;
	
	public Book(String t, String a) {
		this.title = t;
		this.author = a;
		this.language = "EN";
		this.sentences = new ArrayList<Sentence>();
		this.characters = new ArrayList<Character>();
		this.characterSentences = new ArrayList<CharacterSentence>();
	}
	
	public String getTitle() { return this.title; }
	public String getAuthor() { return this.author; }
	public String getLanguage() { return this.language; }
	public ArrayList<Sentence> getSentences() { return this.sentences; }
	public ArrayList<Character> getCharacters() { return this.characters; }
	public ArrayList<CharacterSentence> getCharacterSentences() { return this.characterSentences; }
	
	public void analyzeCharacters(String content) {
		System.out.println("Analisis requested of:" + this.title + " written by " + this.author);
		
		System.out.println("Step1: generating sentences");
		generateSentences(content);
		
		System.out.println("Step2: generating characters");
		generateCharacters();
		
		System.out.println("Step3: generating associations");
		generateAssociations(); 
		
		System.out.println("Analisis of:" + this.title + " written by " + this.author + " complete.");
	}
	
	public void addCharactersFromDB(ArrayList<Character> c) {
		this.characters = c;
	}

	private void generateSentences(String content) {
		String c = content.replace("\r", " ").replace("\n", " ").replace("\t", "").replace("\"", "#").replace("'", "#");
		String[] rawSentences = c.split("\\.(?!\\d)|(?<!\\d)\\.");
		for (int i=0; i<rawSentences.length; i++) {
			sentences.add(new Sentence(rawSentences[i].trim()));
		}
	}
	
	public void generateCharacters() {
		String[] words;
		int count;
		HashMap<String, Integer> wordsWithCount = new HashMap<String, Integer>();
		HashMap<String, Integer> wordsUpperWithCount = new HashMap<String, Integer>();
		for(Sentence tmp: sentences) {
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
	    	if ((wordsUpperWithCount.get(tmp) == wordsWithCount.get(tmp)) &&  (wordsUpperWithCount.get(tmp) > 2
	    			&& tmp.length()>1)) {
	    		characters.add(new Character(tmp));
	    	}
	    }
	}
	
	public void generateAssociations() {
		for(Character c: characters) {
			System.out.println("generateAssociation(): character:" + c.getName());
			for(Sentence s: sentences) {
				if (s.references(c)) { //references will return a probability of the character to be the focus point of the sentence
					characterSentences.add(new CharacterSentence(c,s, (float) 1));
					c.addSentence(s);
				}
			}
		}
	}
}
