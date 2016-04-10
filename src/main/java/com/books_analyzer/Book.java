package com.books_analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Book {
	public final String title;
	public ArrayList<Character> characters;
	public ArrayList<Sentence> sentences;
	//public ArrayList<CharacterSentence> characterSentence;
	
	public Book(String t, String content) {
		this.title = t;
		this.characters = new ArrayList<Character>();
		this.sentences = new ArrayList<Sentence>();
		//this.characterSentence = new ArrayList<CharacterSentence>();
		generateSentences(content);
		generateCharacters();
		//generateAssociations();
	}
	

	private void generateSentences(String cont) {
		String c = cont.replace("\r", "");
		String[] rawSentences = c.split("\n|\\.(?!\\d)|(?<!\\d)\\.");
		for (int i=0; i<rawSentences.length; i++) {
			sentences.add(new Sentence(rawSentences[i]));
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
		Iterator iterator = keySet.iterator();
	    String tmp;
		while(iterator.hasNext()) {
	    	tmp = (String) iterator.next();
	    	if ((wordsUpperWithCount.get(tmp) == wordsWithCount.get(tmp)) &&  (wordsUpperWithCount.get(tmp) > 2)) {
	    		characters.add(new Character(tmp));
	    	}
	    }
	}
	
}
