package com.books_analyzer.service;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.books_analyzer.Book;
import com.books_analyzer.Character;
import com.books_analyzer.CharacterSentence;
import com.books_analyzer.Sentence;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Driver;
import com.mysql.jdbc.Statement;

public class DBInterface {
	public String url;
	
	public DBInterface() {
		try {
			DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//this.url = "jdbc:mysql://localhost:3306/Books?user=root&password=root";
		this.url = "jdbc:mysql://54191.210.230:3306/STG_Books?user=root&password=0";
		
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
		System.out.println("Exporting book to database");
		String values = "";
		
		String sqlBook = "INSERT INTO Books(title, author, language, content) VALUES('"
				+ String.join("','", escape(book.getTitle()), escape(book.getAuthor()), "EN", "") + "');\n";
		System.out.println(sqlBook);
		try {
			executeSQLUpdate(sqlBook);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		Integer idBook = getBookId(book.getTitle(), book.getAuthor());
		String sql = "";
		
		System.out.println("Exporting sentences to database");
		ArrayList<Sentence> sentences = book.getSentences(); 
		for(Sentence s: sentences) {
			values = values + "('" + String.join("','", idBook.toString(), escape(s.getContent())) + "')";
		};
		
		sql = "INSERT INTO Sentences(idBook, content) VALUES " + values.replace(")(", "),(") + ";";
 		try {
			executeSQLUpdate(sql.replace("\"", "#").replace("\n", ""));
		} catch (SQLException e) {
			System.out.println(sql);
			e.printStackTrace();
		}
		
		System.out.println("Exporting characters to database");
		values ="";
		ArrayList<Character> characters = book.getCharacters(); 
		for(Character c: characters) {
			values = values + "('" + String.join("','", c.getName()) + "')";
		};
		
		sql = "INSERT INTO Characters(name) VALUES " + values.replace(")(", "),(") + ";";
 		try {
			executeSQLUpdate(sql.replace("\"", "#").replace("\n", ""));
		} catch (SQLException e) {
			System.out.println(sql);
			e.printStackTrace();
		}
 		
		System.out.println("Exporting characterSentences to database");
		values ="";
		ArrayList<CharacterSentence> characterSentences = book.getCharacterSentences(); 
		for(CharacterSentence cs: characterSentences) {
			values = values + "("
					+ String.join(",", 
							getIdCharacter(cs.getCharacter()).toString(),
							getIdSentence(cs.getSentence()).toString())
					+ ")";
	 		
		};
		sql = "INSERT INTO CharacterSentence(idCharacter, idSentence) VALUES " + values.replace(")(", "),(") + ";";
		try {
			executeSQLUpdate(sql.replace("\"", "#").replace("\n", ""));
		} catch (SQLException e) {
			System.out.println(sql);
			e.printStackTrace();
		}
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
