package d3bug.licensing.server;

import d3bug.licensing.SimpleRegistration;
import d3bug.licensing.client.SignedStringLicenseValidator;

public class Test1 {
	
	public static void main(String[] args) {
		SimpleRegistration reg = new SimpleRegistration();
		reg.setName("Some much longer name");
		reg.setEmail("test.something@emailaddress.com");
		reg.setRegType("standard");
		reg.setWhen("2009-09-01");
		reg.setFeatureCodes("A");
		
		SignedStringLicenseGenerator gen = new SignedStringLicenseGenerator();
		reg.storeLicenseDetails(gen);
		String x = gen.signAndGenerateStringCode();
		System.out.println("License: "+x);
			
		SimpleRegistration sr = new SimpleRegistration();
		SignedStringLicenseValidator v = new SignedStringLicenseValidator(x);
		sr.restoreFrom(v);
		System.out.println(sr.isValid());
		
	}
	
	
}
