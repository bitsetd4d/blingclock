package com.d3bug.licence;

import java.util.HashMap;
import java.util.Map;

import com.d3bug.registration.AuditTrail;
import com.d3bug.registration.LicenceCode;
import com.d3bug.registration.PaypalNotification;
import com.d3bug.util.LicenceUtil;


public class LicenceGenerator {
	
	private static final LicenceGenerator INSTANCE = new LicenceGenerator();
	public static LicenceGenerator getInstance() { return INSTANCE; }
	
	public LicenceCode generateCode(String internalId,AuditTrail audit,PaypalNotification ipn) {
		String payRef = ipn.getPaymentRef();
		String firstName = ipn.getFirstName();
		String lastName = ipn.getLastName();
		String email = ipn.getPayerEmail();
		String when = ipn.getPaymentDate();
		String name = firstName + " " + lastName;
		String special = "";
		audit.out("Generating registration email for "+name+", email: "+email+", when: "+when);
		String licence = LicenceUtil.generateLicence(firstName, lastName, email, when, special);
		return new LicenceCodeImpl(internalId, licence, payRef, name, email, special, when);
	}
	
	private static class LicenceCodeImpl implements LicenceCode {
		
		private String internalId;
		private String licenceCode;
		private String paypalRef;
		private String name;
		private String email;
		private String special;
		private String ordered;
		
		public LicenceCodeImpl(String internalId, String licenceCode,
				String paypalRef, String name, String email, String special,
				String ordered) {
			super();
			this.internalId = internalId;
			this.licenceCode = licenceCode;
			this.paypalRef = paypalRef;
			this.name = name;
			this.email = email;
			this.special = special;
			this.ordered = ordered;
		}
		public String getInternalId() { return internalId; }
		public String getLicenceCode() { return licenceCode; }
		public String getPaypalRef() { return paypalRef; }
		public String getName() { return name; }
		public String getEmail() { return email; }
		public String getSpecial() { return special; }
		public String getOrdered() { return ordered; }
		
		public Map<String, String> getFields() {
			Map<String,String> map = new HashMap<String,String>();
			map.put("internalId",internalId);
			map.put("licenceCode",licenceCode);
			map.put("paypalRef",paypalRef);
			map.put("name",name);
			map.put("email",email);
			map.put("special",special);
			map.put("ordered",ordered);
			return map;
		}
			
	}

}
