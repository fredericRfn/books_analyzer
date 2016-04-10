package com.books_analyzer;

public class CharacterSentence {
	public final Character character;
	public final Sentence sentence;
	public final Float probability;
	
	public CharacterSentence(Character c, Sentence s, Float p) {
		this.character = c;
		this.sentence = s;
		this.probability = p;
	}
}
