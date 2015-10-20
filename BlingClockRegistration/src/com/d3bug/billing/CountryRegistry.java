package com.d3bug.billing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class CountryRegistry {
	
	private static final CountryRegistry INSTANCE = new CountryRegistry();
	public static CountryRegistry getInstance() { return INSTANCE; }
	
	private Properties countries;

	public CountryRegistry() {
		try {
			loadCountyInfo();
			System.out.println("Countries loaded: "+countries.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public CountryInfo getCountryInfo(String code) {
		String key = "vat."+code.toUpperCase();
		String value = countries.getProperty(key);
		if (value == null) {
			return new CountryInfoImpl(code, "Unknown", "I");
		}
		String[] split = value.split(",");
		return new CountryInfoImpl(code, split[1], split[0]);
	}

	private void loadCountyInfo() throws IOException {
		countries = new Properties();
		countries.load(CountryRegistry.class.getResourceAsStream("countries.properties"));
	}
	

	private static class CountryInfoImpl implements CountryInfo {
		
		private String code;
		private String name;
		private String vatCode;
		
		public CountryInfoImpl(String code, String name, String vatCode) {
			this.code = code;
			this.name = name;
			this.vatCode = vatCode;
		}

		public String getCode() { return code;}
		public String getName() { return name; }
		public String getVATCode() { return vatCode; }

		public double getVATRate() {
			if (isUK()) return 0.2;
			if (isEU()) return 0.2;
			return 0;
		}

		public boolean isUK() { return "U".equals(vatCode); }
		public boolean isEU() { return "E".equals(vatCode); }
		public boolean isInternational() {
			return !isUK() && !isEU();
		}
		
		public Map<String, String> getFields() {
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("country_code", code);
			map.put("country_name", name);
			map.put("country_vatcode", vatCode);
			map.put("country_vatrate", String.valueOf(getVATRate()*100));
			return map;
		}

		@Override
		public String toString() {
			return "CountryInfoImpl [code=" + code + ", name=" + name + ", vatCode=" + vatCode + "]";
		}

		
	}

}
