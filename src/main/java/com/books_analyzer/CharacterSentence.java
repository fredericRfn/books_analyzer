package com.books_analyzer;

public class CharacterSentence {
	private final Character character;
	private final Sentence sentence;
	
	public CharacterSentence(Character c, Sentence s, float p) {
		this.character = c;
		this.sentence = s;
	}

	public Character getCharacter() {
		return character;
	}

	public Sentence getSentence() {
		return sentence;
	}
}
