package com.books_analyzer.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.books_analyzer.Book;
import com.books_analyzer.Character;
import com.books_analyzer.CharacterSentence;
import com.books_analyzer.Sentence;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class DBInterface {
	private Connection connection;
	private Statement statement;
	private MysqlDataSource dataSource;
	
	public DBInterface() {
		dataSource = new MysqlDataSource();
		dataSource.setUser("root");
		dataSource.setPassword("root");
		dataSource.setServerName("ubuntu@54.191.210.230");
	}

	public Integer getBookId(String titleParam, String authorParam) {
		ResultSet rs = null;
		try {
			rs = executeSQL("SELECT id FROM Books WHERE title='" + titleParam + "' AND author='" + authorParam + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Integer.parseInt(rs.toString());
	}
	
	private Integer getIdSentence(Sentence sentence) {
		ResultSet rs = null;
		try {
			rs = executeSQL("SELECT id FROM Sentences WHERE content='" + sentence.getContent() + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Integer.parseInt(rs.toString());
	}

	private Integer getIdCharacter(Character character) {
		ResultSet rs = null;
		try {
			rs = executeSQL("SELECT id FROM Characters WHERE name='" + character.getName() + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Integer.parseInt(rs.toString());
	}
	
	public Book importBookFromDatabase(Integer idInDB) {
		ResultSet rs = null;
		try {
			rs = executeSQL("SELECT title,author,content FROM Books WHERE id='" + idInDB.toString() + "';");
			return new Book(rs.getString(0),rs.getString(1),rs.getString(2));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void exportToDatabase(Book book) {
		String sqlBook = "INSERT INTO Books VALUES "
				+ String.join(",", book.getTitle(), book.getAuthor(), "EN") + ";\n";
		try {
			executeSQL(sqlBook);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		Integer idBook = getBookId(book.getTitle(), book.getAuthor());
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
							getIdCharacter(cs.getCharacter()).toString(),
							getIdSentence(cs.getSentence()).toString(),
							cs.getProbability().toString())
					+ ";\n";
		};
		try {
			executeSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("DBInterface: SQL sent to the database:");
		System.out.println(sqlBook);
		System.out.println(sql);
	}
		
	private ResultSet executeSQL(String sql) throws SQLException {
		this.connection = (Connection) dataSource.getConnection();
		this.statement = (Statement) connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT ID FROM USERS");
		resultSet.close();
		this.statement.close();
		this.connection.close();
		return resultSet;
	}
}
