package com.d3bug.registration;

import java.util.Map;

public interface LicenceCode {
	
	String getInternalId();
	String getLicenceCode();
	String getPaypalRef();
	String getName();
	String getEmail();
	String getSpecial();
	String getOrdered();
	Map<String, String> getFields();

}
