package com.d3bug.email;

import java.util.HashMap;
import java.util.Map;

import com.d3bug.saver.Savable;

public class Email implements Savable {

	private String internalId;
	private String to;
	private String subject;
	private StringBuilder body = new StringBuilder();
	
	public Email(String internalId) { this.internalId = internalId; }
	public String getInternalId() { return internalId; }
	
	public String getTo() { return to; }
	public void setTo(String to) { this.to = to; }
	
	public String getSubject() { return subject; }
	public void setSubject(String subject) { this.subject = subject;}
	
	public void print(String x) {
		body.append(x); 
	}
	
	public void println(String x) {
		body.append(x);
		body.append("\n");
	}
	
	public String getBody() { return body.toString(); }
	public Map<String, String> getFields() {
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("internalId",internalId);
		map.put("to",to);
		map.put("subject",subject);
		map.put("body",body.toString());
		return map;
	}
	
//	public void send() {
//		new EmailSender().send(this);
//	}

}
