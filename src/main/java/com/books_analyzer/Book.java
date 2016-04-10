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
	
	public Book(String t, String c) {
		this.title = t;
		generateSentences(c);
	}
	

	private void generateSentences(String cont) {
		String c = cont.replace("\r", "");
		String[] rawSentences = c.split("\n|\\.(?!\\d)|(?<!\\d)\\.");
		ArrayList<Sentence> s = new ArrayList<Sentence>();
		ArrayList<Character> ch = new ArrayList<Character>();
		HashMap<String, Integer> wordsAndCount = new HashMap<String, Integer>();
		HashMap<String, Integer> wordsUpperAndCount = new HashMap<String, Integer>();
		int count;
		for (int i=0; i<rawSentences.length; i++) {
			String[] words = rawSentences[i].replace("\\", "").replace("'s", "").replace("\"", "").replace(".", "").replace(",", "").replace("?", "").replace("!","").split("\\s|'");
			if (words.length > 1) {
				for (int j=0; j<words.length; j++) {
					if (!words[j].isEmpty()) {
						if (java.lang.Character.isUpperCase(words[j].charAt(0))) {
							count = wordsUpperAndCount.containsKey(words[j].toLowerCase()) ? wordsUpperAndCount.get(words[j].toLowerCase()) : 0;
							wordsUpperAndCount.put(words[j].toLowerCase(), count + 1); 
						}
					}
					count = wordsAndCount.containsKey(words[j].toLowerCase()) ? wordsAndCount.get(words[j].toLowerCase()) : 0;
					wordsAndCount.put(words[j].toLowerCase(), count + 1); 
				}
			}
			s.add(new Sentence(rawSentences[i]));
		}
		Set<String> keySet = wordsUpperAndCount.keySet();
		Iterator iterator = keySet.iterator();
	    String tmp;
		while(iterator.hasNext()) {
	    	tmp = (String) iterator.next();
	    	if ((wordsUpperAndCount.get(tmp) == wordsAndCount.get(tmp)) &&  (wordsUpperAndCount.get(tmp) > 2)) {
	    		ch.add(new Character(tmp));
	    	}
	    }
		this.sentences = s;
		this.characters = ch;
	}
}
