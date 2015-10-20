package com.d3bug.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class PageReader {
	
	public static String readPage(String url) throws IOException {
		URL page = new URL(url);
		BufferedReader in = new BufferedReader(new InputStreamReader(page.openStream()));
		StringBuilder sb = new StringBuilder();	
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			sb.append(inputLine);
			sb.append("\n");
		}
		in.close();
		return sb.toString();
	}

}
