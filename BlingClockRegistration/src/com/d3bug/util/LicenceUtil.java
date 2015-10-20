package com.d3bug.util;

import java.util.Map;

import d3bug.licensing.SimpleRegistration;
import d3bug.licensing.server.SignedStringLicenseGenerator;

public class LicenceUtil {
	
	private static String regEmailSubject = "Registration email";
	
	public static String getRegEmailSubject() {
		return regEmailSubject;
	}

	public static void setRegEmailSubject(String regEmailSubject) {
		LicenceUtil.regEmailSubject = regEmailSubject;
	}

	public static String generateLicence(String firstName,String lastName,String email,String when,String special1) {
		String name = firstName + " " + lastName;
		return generateLicence(name, email, when, special1);
	}
	
	public static String generateLicence(String name,String email,String when,String special1) {
		System.out.println("Generating registration email for "+name+", email: "+email+", when: "+when);
		SimpleRegistration reg = new SimpleRegistration();
		reg.setName(name);
		reg.setEmail(email);
		reg.setWhen(when);
		reg.setSpecial1(special1);
		SignedStringLicenseGenerator gen = new SignedStringLicenseGenerator();
		reg.storeLicenseDetails(gen);
		String license = gen.signAndGenerateStringCode();
		return license;
	}
	
	public static String substitute(String page,String payRef,String name,String emailAddress, String when, String license, Map<String, String> map) {
		String x = page;
		for (Map.Entry<String, String> en : map.entrySet()) {
			x = replace(x,en.getKey(),en.getValue());
		}
		x = x.replace("$PAYREF$", payRef);
		x = x.replace("$NAME$", name);
		x = x.replace("$EMAIL$", emailAddress);
		x = x.replace("$WHEN$", when);
		x = x.replace("$LICENCE$", license);
		return x;
	}

	private static String replace(String x, String key, String value) {
		String replaceKey = "$" + key.toUpperCase() + "$";
		return x.replace(replaceKey, value);
	}

}
