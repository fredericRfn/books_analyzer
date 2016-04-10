package com.books_analyzer;

public class CharacterSentence {
	private int id_character;
	private int id_sentence;
	public final Float probability;
	
	public CharacterSentence(int idc, int ids, Float p) {
		this.id_character = idc;
		this.id_sentence = ids;
		this.probability = p;
	}
}
