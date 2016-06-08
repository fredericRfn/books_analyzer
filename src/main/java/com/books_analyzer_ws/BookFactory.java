package com.books_analyzer_ws;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import com.books_analyzer_ws.service.DBBookExporter;

public class BookFactory {
	private DBBookExporter dBBookExporter;
	// This constructor is called from the search controller
	public BookFactory() {
		this.dBBookExporter = new DBBookExporter();
	}
	
	public void buildBook(String title, String author, String character, String url) {
		Book book = new Book(title, author, url);
		book.analyze(getTxtFromUrl(url));
		dBBookExporter.export(book);
	}
	
	private String getTxtFromUrl(String urlString) {
		System.out.println("Fetching content from:" + urlString);
		StringBuilder sb = new StringBuilder();
		URLConnection urlConn = null;
		InputStreamReader in = null;
		try {
			URL url = new URL(urlString);
			urlConn = url.openConnection();
			urlConn.setRequestProperty("User-Agent", "Chrome/23.0.1271.95");
			if (urlConn != null)
				urlConn.setReadTimeout(60000);
			if (urlConn != null && urlConn.getInputStream() != null) {
				in = new InputStreamReader(urlConn.getInputStream(),
						Charset.defaultCharset());
				BufferedReader bufferedReader = new BufferedReader(in);
				if (bufferedReader != null) {
					int cp;
					while ((cp = bufferedReader.read()) != -1) {
						sb.append((char) cp);
					}
					bufferedReader.close();
				}
			}
		in.close();
		} catch (Exception e) {
			throw new RuntimeException("A problem occured while calling URL:"+ urlString, e);
		} 
 
		return sb.toString();
	}
}
