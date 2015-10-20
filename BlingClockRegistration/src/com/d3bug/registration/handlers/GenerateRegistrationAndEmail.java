package com.d3bug.registration.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.d3bug.billing.CountryInfo;
import com.d3bug.billing.Receipt;
import com.d3bug.email.Email;
import com.d3bug.email.EmailSender;
import com.d3bug.licence.LicenceGenerator;
import com.d3bug.net.PageRepository;
import com.d3bug.registration.AuditTrail;
import com.d3bug.registration.LicenceCode;
import com.d3bug.registration.PaypalItem;
import com.d3bug.registration.PaypalNotification;
import com.d3bug.registration.RegistrationListener;
import com.d3bug.saver.Saver;
import com.d3bug.util.LicenceUtil;
import com.d3bug.util.StringUtil;

public class GenerateRegistrationAndEmail implements RegistrationListener {

	public void onUserRegistered(String internalId,AuditTrail audit,Receipt receipt,CountryInfo country,PaypalNotification ipn) {
		LicenceCode code = LicenceGenerator.getInstance().generateCode(internalId,audit,ipn);
		Saver.getInstance().persist(code);
		Map<String,String> fields = createFields(receipt,country,ipn);
		generateRegistrationEmail(internalId,audit,code,ipn,fields);
	}
	
	private Map<String, String> createFields(Receipt receipt,CountryInfo country, PaypalNotification ipn) {
		Map<String,String> fields = new HashMap<String,String>();
		fields.putAll(ipn.getFields());
		fields.putAll(receipt.getFieldsWithDisplay());
		fields.putAll(country.getFields());
		fields.putAll(ipn.getFields());
		return fields;
	}

	private void generateRegistrationEmail(String internalId,AuditTrail audit,LicenceCode code,PaypalNotification ipn,Map<String,String> fields) {
		String licence = code.getLicenceCode();
		String licenseCols = StringUtil.toCols(licence,60);
		
		String regEmail = "reg-email";
		List<PaypalItem> items = ipn.getItems();
		if (items.size() > 0) {
			regEmail = ("reg-email-" +items.get(0).getItemCode()).toLowerCase();
		}
		
		System.out.println("Looking for "+regEmail);
		String emailText = PageRepository.getInstance().getPage("blingclock", regEmail);
		if (emailText == null) {
			System.out.println("Fallback to reg-email");
			emailText = PageRepository.getInstance().getPage("blingclock", "reg-email");
		}
		String finalText = LicenceUtil.substitute(emailText, code.getPaypalRef(), code.getName(), code.getEmail(), code.getOrdered(), licenseCols, fields);
		String subject = LicenceUtil.getRegEmailSubject();
		
		Email email = new Email(internalId);
		email.setTo(code.getEmail());
		email.setSubject(subject);
		email.print(finalText);
		
		audit.out("Sending registration email to "+code.getEmail());
		EmailSender.getInstance().send(email);
	}

}
