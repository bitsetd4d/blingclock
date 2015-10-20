package com.d3bug.billing;

import java.util.Map;

public interface CountryInfo {
	
	String getCode();
	String getName();
	String getVATCode();
	boolean isUK();
	boolean isEU();
	boolean isInternational();
	double getVATRate();
	
	Map<String,String> getFields();
	
}
