package com.books_analyzer;

public class CharacterSentence {
	private final Character character;
	private final Sentence sentence;
	public final Float probability;
	
	public CharacterSentence(Character c, Sentence s, Float p) {
		this.character = c;
		this.sentence = s;
		this.probability = p;
	}

	public Character getCharacter() {
		return character;
	}

	public Sentence getSentence() {
		return sentence;
	}

	public Float getProbability() {
		return probability;
	}
}
