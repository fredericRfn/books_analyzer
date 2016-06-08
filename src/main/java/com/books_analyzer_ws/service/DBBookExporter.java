package com.books_analyzer_ws.service;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.books_analyzer_ws.Book;
import com.books_analyzer_ws.Character;
import com.books_analyzer_ws.CharacterSentence;
import com.books_analyzer_ws.Sentence;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Driver;
import com.mysql.jdbc.Statement;

public class DBBookExporter {
	public String url;
	
	public DBBookExporter() {
		try {
			DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		//this.url = "jdbc:mysql://localhost:3306/STG_BOOKS_2?user=root&password=root"; // local configuration
		this.url = "jdbc:mysql://54.191.210.230:3306/STG_BOOKS_2?user=root&password=0"; // EC2 configuration
		
	}

	private ResultSet executeSQLQuery(String sql) throws SQLException {
		Connection connection = (Connection) DriverManager.getConnection(url);
		Statement statement = (Statement) connection.createStatement();
		ResultSet resultSet = statement.executeQuery(sql);
		//resultSet.close();
		//statement.close();
		//connection.close();
		return resultSet;
	}
	private int executeSQLUpdate(String sql) throws SQLException {
		Connection connection = (Connection) DriverManager.getConnection(url);
		Statement statement = (Statement) connection.createStatement();
		int resultSet = statement.executeUpdate(sql);
		statement.close();
		connection.close();
		return resultSet;
	}
	
	public Integer getBookId(String title, String author) {
		// This method will change, the idBook is hash_function(title + author)
		try {
			ResultSet rs = executeSQLQuery("SELECT idBook FROM Books WHERE title='" + title + " AND author='" + author + "';");
			if (rs.next()) {
				return rs.getInt("idBook");
			}
			else return -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	private HashMap<String, Integer> getCharactersAndIds(ArrayList<Character> characters) {
		// This method will change, the idCharacter is hash_function(idBook + name)
		ResultSet rs = null;
		HashMap<String, Integer> namesAndIds = new HashMap<String, Integer>();
		
		String sql = "SELECT idCharacter, name FROM Characters WHERE name IN (";
		for(Character c: characters) { sql = sql + "'" + c.getName() + "',"; }
		sql = sql + ");";
 		try {
			rs = executeSQLQuery(sql.replace(",);", ");").replace("\n", ""));
	 		while(rs.next()) {
				namesAndIds.put(rs.getString("name"), rs.getInt("idCharacter"));
			}
		} catch (SQLException e) {
			System.out.println(sql);
			e.printStackTrace();
		}
		return namesAndIds;
	}

	private HashMap<String, Integer> getSentencesAndIds(ArrayList<Sentence> sentences) {
		// This method will change, the idCharacter is hash_function(idBook + sentence)
		ResultSet rs = null;
		HashMap<String, Integer> sentencesAndIds = new HashMap<String, Integer>();
		
		String sql = "SELECT idSentence, content FROM Sentences WHERE content IN (";
		for(Sentence s: sentences) { sql = sql + "'" + escape(s.getContent()) + "',"; }
		sql = sql + ");";
 		
		try {
			rs = executeSQLQuery(sql.replace(",);",");").replace("\n", "").replace(",''",""));
	 		while(rs.next()) {
	 			sentencesAndIds.put(escape(rs.getString("content")), rs.getInt("idSentence"));
			}
		} catch (SQLException e) {
			System.out.println(sql);
			e.printStackTrace();
		}
		return sentencesAndIds;
	}
	
	public void export(Book book) {
		System.out.println("Exporting book to database");
		String valuesInSQL = "";
		// STEP 1: get the book ID based on the title and the author
		Integer idBook = getBookId(book.getTitle(), book.getAuthor());
		String sql = "";
		
		// STEP 2: Add the sentences to the Sentences table
		System.out.println("Exporting sentences to database");
		ArrayList<Sentence> sentences = book.getSentences(); 
		for(Sentence s: sentences) {
			valuesInSQL = valuesInSQL + "('" + String.join("','", idBook.toString(), escape(s.getContent())) + "')";
		};
		
		sql = "INSERT INTO Sentences(idBook, content) VALUES " + valuesInSQL.replace(")(", "),(") + ";\n";
		try {
			executeSQLUpdate(sql);
		} catch (SQLException e) {
			System.out.println(sql);
			e.printStackTrace();
		}
 		// STEP 3: Add the characters to the Characters table
		System.out.println("Exporting characters to database");
		valuesInSQL ="";
		ArrayList<Character> characters = book.getCharacters(); 
		for(Character c: characters) {
			valuesInSQL = valuesInSQL + "('" + String.join("','", c.getName()) + "')";
		};
		
		sql = "INSERT INTO Characters(name) VALUES " + valuesInSQL.replace(")(", "),(") + ";";
 		try {
			executeSQLUpdate(sql.replace("\"", "#").replace("\n", ""));
		} catch (SQLException e) {
			System.out.println(sql);
			e.printStackTrace();
		}
 		// STEP 4: Export the CharacterSentences
		System.out.println("Exporting characterSentences to database");
		valuesInSQL ="";
		ArrayList<CharacterSentence> characterSentences = book.getCharacterSentences(); 
		System.out.println("Number of CharacterSentences to be exported:" + characterSentences.size());
	
		// Obtain all the ids corresponding to the sentences and characters, with TWO SQL queries only
		HashMap<String, Integer> namesAndIds = getCharactersAndIds(characters);
		HashMap<String, Integer> sentencesAndIds = getSentencesAndIds(sentences);

		for(CharacterSentence cs: characterSentences) {
			System.out.println(cs.getSentence().getContent());
			if (sentencesAndIds.get(escape(cs.getSentence().getContent()))!=null) {
			valuesInSQL = valuesInSQL + "("
					+ String.join(",", 
							namesAndIds.get(cs.getCharacter().getName()).toString(),
							sentencesAndIds.get(escape(cs.getSentence().getContent())).toString())
					+ ")";
			}
	 		
		};
		
		sql = "INSERT INTO CharacterSentence(idCharacter, idSentence) VALUES " + valuesInSQL.replace(")(", "),(") + ";";
		try {
			executeSQLUpdate(sql.replace("\"", "#").replace("\n", ""));
		} catch (SQLException e) {
			System.out.println(sql);
			e.printStackTrace();
		}
	}
	
	private String escape(String s) {
		return s.replace("'", "#").replace("\"","#").replace("\n", "#");
	}
}
