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
			e.printStackTrace();
		}
		//this.url = "jdbc:mysql://localhost:3306/STG_Books?user=root&password=root"; // local configuration
		this.url = "jdbc:mysql://54.191.210.230:3306/STG_Books?user=root&password=0"; // EC2 configuration
		
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
	
	public Integer getBookId(String titleParam, String authorParam) {
		try {
			ResultSet rs = executeSQLQuery("SELECT idBook FROM Books WHERE title='" + titleParam + "' AND author='" + authorParam + "';");
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
		ResultSet rs = null;
		ArrayList<String> names = new ArrayList<String>();
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
	
	public ArrayList<Character> importCharactersFromDB(String title, String author) {
		Integer idInDB = getBookId(title,author);
		System.out.println("Book found with id " + idInDB );
		if(idInDB<0) { return null; }
		ResultSet rsBook = null;
		ResultSet rsSentences = null;
		ArrayList<Character> characters = new ArrayList<Character>();
		
		// This object is storing the name of a character, and all the sentences referencing it
		HashMap<String, ArrayList<String>> charactersMap = new HashMap<String, ArrayList<String>>();
		try {
			System.out.println("Retrieve from Database the book: " + title + " by " + author);
			// Get book data
			rsBook = executeSQLQuery("SELECT title,author,content FROM Books WHERE idBook='" + idInDB.toString() + "';");
			rsBook.next();
			
			// Get sentences and characters data by joining Character CharacterSentence Sentence
			rsSentences = executeSQLQuery(""
					+ "SELECT * FROM Characters "
					+ "INNER JOIN CharacterSentence ON Characters.idCharacter = CharacterSentence.idCharacter "
					+ "INNER JOIN Sentences ON CharacterSentence.idSentence = Sentences.idSentence "
					+ "WHERE idBook = '" + idInDB + "';");
			ArrayList<String> contents = new ArrayList<String>();
			
			// Build the character objects (which include sentences)
			// For each sentence referencing a character (obtained thanks to the join)
			while(rsSentences.next()) {
				// Get character name
				contents = charactersMap.get(rsSentences.getString("name"));
				// Add the sentence referencing the character to the character object
				if(contents == null) contents = new ArrayList<String>();
				contents.add(rsSentences.getString("content"));
				charactersMap.put(rsSentences.getString("name"), contents);
			}
			// For each character referenced
			for(String key : charactersMap.keySet()) {
				// Build the new character object, and all the sentences referencing the character 
				characters.add(new Character(key, charactersMap.get(key)));
				System.out.println("Character added:" + key);
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return characters;
	}
	
	public void exportToDatabase(Book book) {
		System.out.println("Exporting book to database");
		String valuesInSQL = "";
		// STEP 1: Add the book to the Books table
		String sqlBook = "INSERT INTO Books(title, author, language) VALUES('"
				+ String.join("','", escape(book.getTitle()), escape(book.getAuthor()), "EN") + "');\n";
		System.out.println(sqlBook);
		try {
			executeSQLUpdate(sqlBook);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		// STEP 1 bis: get the book ID based on the title and the author
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
	
	// FUNCTIONS NECESSARY FOR THE API
	
	// Function called with url: GET /books?author=author&character=character
	public ArrayList<String> getBooks(String title, String author) {
		String sql = "SELECT * FROM Books ";
		ArrayList<String> formattedBooks = new ArrayList<String>();
		if(author != null && title != null) {
			sql = sql + "WHERE author='" + author + "' AND title='" + title + "';";;
		}
		else if (author!=null) {
			sql = sql + "WHERE author='" + author + "';";
		}
		else if (title!=null) {
			sql = sql + "WHERE title='" + title + "';";
		}
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return formattedBooks;
	}
	// Function called with url:  GET /books/id
	public String[] getTitleAuthorById(Integer id) {
		String sql = "SELECT * FROM Books WHERE idBook="+id.toString() + ";";
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
	
	// Function called with url: DELETE /books/id
	public String deleteBook(Integer id) {
		String sqlDeleteCharacterSentences = "DELETE FROM Sentences, CharacterSentence, Characters " +
				"USING Sentences INNER JOIN CharacterSentence INNER JOIN Characters " +
				"WHERE Sentences.idSentence=CharacterSentence.idSentence " +
				"AND Characters.idCharacter=CharacterSentence.idCharacter " +
				"AND Sentences.idBook=" + id.toString() + ";";
		String sqlDeleteSentences = "DELETE FROM Sentences WHERE idBook=" + id.toString() + ";";
		String sqlCleanup = "DELETE FROM Characters " +
				"WHERE idCharacter NOT IN (SELECT idCharacter FROM CharacterSentence);";
		String sqlDeleteBook = "DELETE FROM Books WHERE idBook=" + id.toString() + ";";
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
	
	// Function called with url: POST /books?...all 4
	// Actually this one needs all the analysis to be done to then call exportToDatabase
	
	// Function called with url: PUT /books/id?author= title= language=
	// Solely used to make the metadata accurate
	// Maybe it would be useful to make a flag: analyze, if true, all the analisis process is done
	public void editBookById(Integer id, String title, String author) {
		String sql = "UPDATE Books SET title='" + title + "', author='" + author + "' WHERE idBook="+id.toString() + ";";
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
