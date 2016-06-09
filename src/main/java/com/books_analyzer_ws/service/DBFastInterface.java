package com.books_analyzer_ws.service;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.books_analyzer_ws.Book;
import com.books_analyzer_ws.Character;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Driver;
import com.mysql.jdbc.Statement;

public class DBFastInterface {
	public String url;
	
	public DBFastInterface() {
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
	
	public boolean isPresentInDB(String title, String author) {
		try {
			ResultSet rs = executeSQLQuery("SELECT idBook FROM Books WHERE title='" + title + "' AND author='" + author + "';");
			if (rs.next()) {
				return true;
			}
			else return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
	}
	
	public int getBookState(String id) {
		ResultSet rs;
		String sql = "SELECT flag FROM Books WHERE idBook=" + id + ";";
		try {
			rs = executeSQLQuery(sql);
			rs.next();
			return rs.getInt("flag");
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public Book importBookFromDB(String id, String title, String author) {
		ResultSet rsBook = null;
		ResultSet rsSentences = null;
		ArrayList<Character> characters = new ArrayList<Character>();
		ArrayList<String> sentences = new ArrayList<String>();

		// This object is storing the name of a character, and all the sentences referencing it
		HashMap<String, ArrayList<String>> charactersMap = new HashMap<String, ArrayList<String>>();
		try {
			System.out.println("Retrieve from Database the book: " + id);
			// Get book data
			rsBook = executeSQLQuery("SELECT title,author,language FROM Books WHERE idBook='" + id + "';");
			rsBook.next();
			// Get sentences and characters data by joining Character CharacterSentence Sentence
			rsSentences = executeSQLQuery(""
					+ "SELECT * FROM Characters "
					+ "INNER JOIN CharacterSentence ON Characters.idCharacter = CharacterSentence.idCharacter "
					+ "INNER JOIN Sentences ON CharacterSentence.idSentence = Sentences.idSentence "
					+ "WHERE idBook = '" + id + "';");
			// Build the character objects (which include sentences)
			// For each sentence referencing a character (obtained thanks to the join)
			while(rsSentences.next()) {
				sentences = charactersMap.get(rsSentences.getString("name"));
				// Add the sentence referencing the character to the character object
				if(sentences == null) sentences = new ArrayList<String>();
				sentences.add(rsSentences.getString("content"));
				charactersMap.put(rsSentences.getString("name"), sentences);
			}
			// For each character referenced
			for(String key : charactersMap.keySet()) {
				// Build the new character object, and all the sentences referencing the character 
				characters.add(new Character(key, charactersMap.get(key)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Book book = new Book(title,author,null);
		book.setCharacters(characters);
		return book;
	}
	public void addBookData(String title, String author, String url, int flag) {
		String id = IdCreator.createIdFromText(title + author);
		String sqlBook = "INSERT INTO Books(idBook, title, author, language, url, flag) VALUES('"
		  + String.join("','", id, escape(title), escape(author), "EN",escape(url),"0") + "');\n";
		System.out.println(sqlBook);
		try {
			executeSQLUpdate(sqlBook);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	// FUNCTIONS NEEDED BY THE API
	// Function called with GET /books?
	public ArrayList<String> getCompletedBooks(String title, String author) {
		String sql = "SELECT * FROM Books WHERE flag=5 ";
		ArrayList<String> formattedBooks = new ArrayList<String>();
		if (author!=null) {
			sql = sql + "AND author='" + author + "' ";
		}
		if (title!=null) {
			sql = sql + "AND title='" + title + "' ";
		}
		sql = sql + ";";
		ResultSet rs;
		try {
			rs = executeSQLQuery(sql);
			while(rs.next()) {
				formattedBooks.add(
						"{\"title\":\"" + rs.getString("title") + "\""
						+ "\"author\":\"" + rs.getString("author") + "\""
						+ "\"language\":\"" + rs.getString("language") + "\"}");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return formattedBooks;
	}
	// Function called with GET /books/id
	public String[] getTitleAuthorById(String id) {
		String sql = "SELECT * FROM Books WHERE idBook="+id + ";";
		try {
			ResultSet rs = executeSQLQuery(sql);
			if(rs.next()) {
				String[] result = {rs.getString("title"), rs.getString("author")};
				return result;
			}
			else return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	// Function called with DELETE /books/id
	public String deleteBook(String id) {
		String sqlDeleteCharacterSentences = 
				"DELETE FROM Sentences, CharacterSentence, Characters " +
				"USING Sentences INNER JOIN CharacterSentence INNER JOIN Characters " +
				"WHERE Sentences.idSentence=CharacterSentence.idSentence " +
				"AND Characters.idCharacter=CharacterSentence.idCharacter " +
				"AND Sentences.idBook=" + id + ";";
		String sqlDeleteSentences = "DELETE FROM Sentences WHERE idBook=" + id + ";";
		String sqlCleanup = "DELETE FROM Characters " +
				"WHERE idCharacter NOT IN (SELECT idCharacter FROM CharacterSentence);";
		String sqlDeleteBook = "DELETE FROM Books WHERE idBook=" + id + ";";
		try {
			executeSQLUpdate(sqlDeleteCharacterSentences);
			executeSQLUpdate(sqlDeleteSentences);
			executeSQLUpdate(sqlCleanup);
			executeSQLUpdate(sqlDeleteBook);
			return "{\"status\":\"Delete successful\"}";
		} catch (SQLException e) {
			e.printStackTrace();
			return "{\"status\":\"Delete failed\"}";
		}
	}
	
	// Function called with PUT /books/id?author= title= language=
	// FOR NOW, does not work (the whole process of update)
	public void editBookById(String id, String title, String author) {
		String sql = "UPDATE Books SET title='" + title + "', author='" + author + "' WHERE idBook="+id + ";";
		try {
			executeSQLQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String escape(String s) {
		return s.replace("'", "#").replace("\"","#").replace("\n", "#");
	}
}
