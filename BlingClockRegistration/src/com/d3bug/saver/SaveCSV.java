package com.d3bug.saver;

import java.util.ArrayList;
import java.util.List;

public class SaveCSV {

	public List<String> csvList = new ArrayList<String>();
	
	public void saveValue(String value) {
		csvList.add(value);
	}
	
	public void saveQuoted(String value) {
		csvList.add("\""+value+"\"");
	}
	
	public String asCSV() { 
		StringBuilder sb = new StringBuilder();
		for (String x : csvList) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(x.trim());
		}
		sb.append("\n");
		return sb.toString();
	}
	
	
}
