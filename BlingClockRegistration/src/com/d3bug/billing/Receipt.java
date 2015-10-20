package com.d3bug.billing;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.d3bug.paypal.PaypalIPN;
import com.d3bug.registration.AuditTrail;
import com.d3bug.registration.PaypalItem;

public class Receipt {
	
	private String internalId;
	private String invoiceDate;
	private String invoiceUKDate;
	private String paypalReference;
	private String currency;
	
	private String recipient;
	private String recipientEmail;

	private String countryCode;
	private String countryDescription;
	
	private long netAmountPence;
	private String vatCode;
	private double vatRate;
	private long vatAmountPence;
	private long grossAmountPence;
	
	private List<ReceiptItem> items = new ArrayList<ReceiptItem>();
	public List<ReceiptItem> getItems() { return items; }

	public Map<String, String> getFields() {
		Map<String,String> map = new HashMap<String,String>();
		map.put("internalId", internalId);
		map.put("invoiceDate", invoiceDate);
		map.put("invoiceUKDate", invoiceUKDate);
		map.put("paypalReference", paypalReference);
		map.put("currency", currency);
		map.put("recipient", recipient);
		map.put("recipientEmail", recipientEmail);
		map.put("countryCode", countryCode);
		map.put("countryDescription", countryDescription);
		map.put("netAmountPence", String.valueOf(netAmountPence));
		map.put("vatCode", vatCode);
		map.put("vatRate", String.valueOf(vatRate));
		map.put("vatAmountPence", String.valueOf(vatAmountPence));
		map.put("grossAmountPence", String.valueOf(grossAmountPence));
		int i = 1;
		for (ReceiptItem item : items) {
			item.addFields(i,map);
		}
		return map;
	}
	
	public Map<String,String> getFieldsWithDisplay() {
		Map<String,String> fields = getFields();
		fields.put("formatted_net", formatMoney(netAmountPence));
		fields.put("formatted_vat", formatMoney(vatAmountPence));
		fields.put("formatted_vatrate", getFormattedVATRate());
		fields.put("formatted_total", formatMoney(grossAmountPence));
		return fields;
	}
	
	public String getDisplayNet() { return formatMoney(netAmountPence); }  
	public String getDisplayVat() { return formatMoney(vatAmountPence); }  
	public String getDisplayTotal() { return formatMoney(grossAmountPence); }  
	
	private String getFormattedVATRate() { 
		String v = String.valueOf(vatRate*100);
		return (v + "      ").substring(0,5);
	}
	
	private String formatMoney(long m) {
		DecimalFormat df = new DecimalFormat("######0.00");
		return leftPad(df.format(m/100d));
	}
	
	public static void main(String[] args) {
		DecimalFormat df = new DecimalFormat("######0.00");
		System.out.println(df.format(0f));
		System.out.println(df.format(0.01));
		System.out.println(df.format(0.11));
		System.out.println(df.format(1.11));
		System.out.println(df.format(1000.11));
		System.out.println(leftPad(""));
		System.out.println(leftPad("X"));
		System.out.println(leftPad("X.XX"));
		System.out.println(leftPad("XXXXX.XX"));
	}
	
	private static String leftPad(String x) {
		return "            ".substring(0,10-x.length())+x;
	}

	public Receipt(String internalId) {
		this.internalId = internalId;
	}

	public String getInternalId() { return internalId; }
	public String getInvoiceUKDate() { return invoiceUKDate; }
	public String getInvoiceDate() { return invoiceDate; }
	public String getPaypalReference() { return paypalReference; }
	public String getCurrency() { return currency; }
	public String getRecipientName() { return recipient; }
	public String getRecipientEmail() { return recipientEmail; }
	public String getCountryCode() { return countryCode; }
	public String getCountryDescription() { return countryDescription; }
	public long getNetAmountPence() { return netAmountPence; }
	public String getVatCode() { return vatCode; }
	public double getVatRate() { return vatRate; }
	public long getVatPence() { return vatAmountPence; }
	public long getGrossAmountPence() { return grossAmountPence; }

	public void setFrom(AuditTrail audit, CountryInfo country, PaypalIPN ipn) {
		invoiceDate = ipn.getPaymentDate();
		paypalReference = ipn.getPaymentRef();
		currency = ipn.getMcCurrency();
		recipient = ipn.getFirstName() + " " + ipn.getLastName();
		recipientEmail = ipn.getPayerEmail();
		countryCode = country.getCode();
		countryDescription = country.getName();
		vatCode = country.getVATCode();
		vatRate = country.getVATRate();
		grossAmountPence = convertToPence(ipn.getPaymentGross());
		VATCalculator c = new VATCalculator(grossAmountPence, vatRate);
		netAmountPence = c.getNetAmountPence();
		vatAmountPence = c.getVATAmountPence();
		
		for (PaypalItem i : ipn.getItems()) {			
			ReceiptItem item = new ReceiptItem(i);
			items.add(item);
		}
		
		invoiceUKDate = getUKDate();
		
		audit.out("Country  : "+country);
		audit.out("VAT Calc : "+c);
		audit.out("Receipt  : "+this);
		
	}

	private String getUKDate() {
		SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
		return df.format(new Date());
	}

	private long convertToPence(String x) {
		try {
			Double d = Double.parseDouble(x);
			return (long)(d*100);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public String toString() {
		return "Receipt [internalId=" + internalId + ", invoiceDate="
				+ invoiceDate + ", paypalReference=" + paypalReference
				+ ", currency=" + currency + ", recipient=" + recipient
				+ ", recipientEmail=" + recipientEmail + ", countryCode="
				+ countryCode + ", countryDescription=" + countryDescription
				+ ", netAmountPence=" + netAmountPence + ", vatCode=" + vatCode
				+ ", vatRate=" + vatRate + ", vatAmountPence=" + vatAmountPence
				+ ", grossAmountPence=" + grossAmountPence + ", items=" + items
				+ "]";
	}

	
	
}
