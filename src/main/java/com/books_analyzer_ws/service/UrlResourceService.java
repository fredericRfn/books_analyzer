package com.books_analyzer_ws.service;

// BooksService provides the JSON needed for responding to the clients
public class UrlResourceService {
		private static DBFastInterface dbInterface;
		private final static String urlBase = "https://books-analyzer-ws.herokuapp.com/";

		public UrlResourceService() {
			UrlResourceService.dbInterface = new DBFastInterface();
		}
		
		// Returns {"url":urlToResource} or {"error":explanation}
		public String getResourceURL(String title, String author, String character, String url) {
			if(title.length()>1 && author.length()>1) {
				return getSingleAnalysisURL(title, author, url);
			} 
			else {
				return getCrossedAnalysisURL(title, author, character);
			}
		}
		
		public static String getSingleAnalysisURL(String title, String author, String url) {
			int idBook = (title+author).hashCode();
			if(dbInterface.isPresentInDB(title, author)){ 
				return "{\"url\":" + urlBase + "books/" + idBook + "}";
			}
			else { // The mentioned book IS NOT in our database
				if(url.length()>1) { // If the user provides its content
					dbInterface.addBookData(title,author,url,0);// INSERT INTO Books(...)
					RabbitInterface.requestAnalysis(idBook);
					return "{\"url\":" + urlBase + "books/" + idBook + "}";
				}
				else { // If there is no way for us to process the book (no db, no url)
					return "{\"error\":\"No info about this book "
							+ "available. Add a url containing the book "
							+ "in a .txt format to process it\"}";
				}
			}
		}
		
		public static String getCrossedAnalysisURL(String title, String author, String character) {
			if (character.length()<=1) { // If character is NOT provided
				return "{\"url\":\"" + urlBase + "books?title="
						+ title + "&author=" + author + "\"}";
			}
			else { // The analysis is asked for a character specifically
				 return "{\"url\":\"" + urlBase + "characters/"
						+ character + "?title=" + title + "&author=" + author + "\"}";
			}
		}
	}

