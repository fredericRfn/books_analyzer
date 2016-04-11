package com.books_analyzer.service;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.books_analyzer.Book;
import com.books_analyzer.Character;
import com.books_analyzer.CharacterSentence;
import com.books_analyzer.Sentence;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Driver;
import com.mysql.jdbc.Statement;
import com.sun.javafx.collections.MappingChange.Map;

public class DBInterface {
	public String url;
	
	public DBInterface() {
		try {
			DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// EN LOCAL this.url = "jdbc:mysql://localhost:3306/Books?user=root&password=root";
		this.url = "jdbc:mysql://54.191.210.230:3306/STG_Books?user=root&password=0";
		
	}

	public Integer getBookId(String titleParam, String authorParam) {
		try {
			ResultSet rs = executeSQLQuery("SELECT idBook FROM Books WHERE title='" + titleParam + "' AND author='" + authorParam + "';");
			rs.next();
			return rs.getInt("idBook");
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		
	}
	
	private Integer getIdSentence(Sentence sentence) {
		ResultSet rs = null;
		try {
			rs = executeSQLQuery("SELECT idSentence FROM Sentences WHERE content='" + escape(sentence.getContent().replace("\"", "#").replace("\n", "")) + "';");
			rs.next();
			return rs.getInt("idSentence");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Integer.parseInt(rs.toString());
	}

	private Integer getIdCharacter(Character character) {
		ResultSet rs = null;
		try {
			rs = executeSQLQuery("SELECT idCharacter FROM Characters WHERE name='" + character.getName() + "';");
			rs.next();
			return rs.getInt("idCharacter");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Integer.parseInt(rs.toString());
	}
	
	public Book importBookFromDatabase(Integer idInDB) {
		ResultSet rsBook = null;
		ResultSet rsSentences = null;
		Book book = null;
		ArrayList<Character> characters = new ArrayList<Character>();
		HashMap<String, ArrayList<String>> charactersMap = new HashMap<String, ArrayList<String>>();
		try {
			rsBook = executeSQLQuery("SELECT title,author,content FROM Books WHERE idBook='" + idInDB.toString() + "';");
			rsBook.next();
			rsSentences = executeSQLQuery(""
					+ "SELECT * FROM Characters "
					+ "INNER JOIN CharacterSentence ON Characters.idCharacter = CharacterSentence.idCharacter "
					+ "INNER JOIN Sentences ON CharacterSentence.idSentence = Sentences.idSentence "
					+ "WHERE idBook = '" + idInDB + "';");
			ArrayList<String> contents = new ArrayList<String>();
			while(rsSentences.next()) {
				contents = charactersMap.get(rsSentences.getString("name"));
				if(contents == null) contents = new ArrayList<String>();
				contents.add(rsSentences.getString("content"));
				charactersMap.put(rsSentences.getString("name"), contents);
			}
			for(String key : charactersMap.keySet()) {
				characters.add(new Character(key, charactersMap.get(key)));
			}
			book = new Book(rsBook.getString("title"),rsBook.getString("author"),rsBook.getString("content"),characters);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return book;
	}
	
	public void exportToDatabase(Book book) {
		String sqlBook = "INSERT INTO Books(title, author, language, content) VALUES('"
				+ String.join("','", escape(book.getTitle()), escape(book.getAuthor()), "EN", "") + "');\n";
		System.out.println("DBInterface: SQL sent to the database:");
		System.out.println(sqlBook);
		try {
			executeSQLUpdate(sqlBook);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		Integer idBook = getBookId(book.getTitle(), book.getAuthor());
		String sql = "";
		
		ArrayList<Sentence> sentences = book.getSentences(); 
		for(Sentence s: sentences) {
			sql = "INSERT INTO Sentences(idBook, content) VALUES('"
					+ String.join("','", idBook.toString(), escape(s.getContent())) + "');\n";
	 		try {
				executeSQLUpdate(sql.replace("\"", "#").replace("\n", ""));
			} catch (SQLException e) {
				System.out.println(sql);
				e.printStackTrace();
			}
		};
		
		ArrayList<Character> characters = book.getCharacters(); 
		for(Character c: characters) {
			sql = "INSERT INTO Characters(name) VALUES('"
					+ String.join("','", c.getName()) + "');\n";
	 		try {
				executeSQLUpdate(sql.replace("\"", "#").replace("\n", ""));
			} catch (SQLException e) {
				System.out.println(sql);
				e.printStackTrace();
			}
		};
		
		ArrayList<CharacterSentence> characterSentences = book.getCharacterSentences(); 
		for(CharacterSentence cs: characterSentences) {
			sql = "INSERT INTO CharacterSentence(idCharacter, idSentence) VALUES('"
					+ String.join("','", 
							getIdCharacter(cs.getCharacter()).toString(),
							getIdSentence(cs.getSentence()).toString())
					+ "');\n";
	 		try {
				executeSQLUpdate(sql.replace("\"", "#").replace("\n", ""));
			} catch (SQLException e) {
				System.out.println(sql);
				e.printStackTrace();
			}
		};
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
	
	private String escape(String s) {
		return s.replace("'", "#");
	}
}
