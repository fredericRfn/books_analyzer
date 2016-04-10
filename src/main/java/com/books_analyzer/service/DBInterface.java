package com.books_analyzer.service;

import java.util.ArrayList;

import com.books_analyzer.Book;
import com.books_analyzer.Character;
import com.books_analyzer.CharacterSentence;
import com.books_analyzer.Sentence;

public class DBInterface {
	private String ip;
	private String host;
	// ...
	
	public DBInterface() {
		// Open the connection
	}

	public Integer fetchID(String titleParam, String authorParam) {
		
		return null;
	}
	
	public Book importBookFromDatabase(Integer idInDB) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void exportToDatabase(Book book) {
		String sqlBook = "INSERT INTO Books VALUES "
				+ String.join(",", book.getTitle(), book.getAuthor(), "EN") + ";\n";
		executeSQL(sqlBook);
		
		Integer idBook = fetchID(book.getTitle(), book.getAuthor());
		String sql = "";
		
		ArrayList<Sentence> sentences = book.getSentences(); 
		for(Sentence s: sentences) {
			sql = sql + "INSERT INTO Sentences VALUES "
					+ String.join(",", idBook.toString(), s.getContent()) + ";\n";
		};
		
		ArrayList<Character> characters = book.getCharacters(); 
		for(Character c: characters) {
			sql = sql + "INSERT INTO Characters VALUES "
					+ String.join(",", c.getName()) + ";\n";
		};
		
		ArrayList<CharacterSentence> characterSentences = book.getCharacterSentences(); 
		for(CharacterSentence cs: characterSentences) {
			sql = sql + "INSERT INTO CharacterSentence VALUES "
					+ String.join(",", 
							getIdCharacter(cs.getCharacter()),
							getIdSentence(cs.getSentence()),
							cs.getProbability()) + ";\n";
		};
		executeSQL(sql);
		System.out.println("DBInterface: SQL sent to the database:");
		System.out.println(sqlBook);
		System.out.println(sql);
	}

	private Integer getIdSentence(Sentence sentence) {
		// TODO Auto-generated method stub
		return null;
	}

	private Integer getIdCharacter(Character character) {
		// TODO Auto-generated method stub
		return null;
	}

	public void closeConnection() {
		// TODO Auto-generated method stub
		
	}
		
	private boolean executeSQL(String sql) {
		
		return true;
	}
}
