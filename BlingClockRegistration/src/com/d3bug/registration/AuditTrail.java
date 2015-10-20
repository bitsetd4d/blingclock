package com.d3bug.registration;

import java.util.Date;

public class AuditTrail {
	
	private String internalId;
	private StringBuilder sb = new StringBuilder();
	
	public String getInternalId() { return internalId; }
	public void setInternalId(String internalId) { this.internalId = internalId; }

	public void out(String msg) {
		String line = asMessage(msg);
		System.out.println("AUDIT: "+line);
		sb.append(line);
		sb.append("\n");
	}
	
	public String getBody() { return sb.toString(); }
	
	private String asMessage(String msg) {
		return "[" + new Date() + "] "+msg;
	}
	
}
