package com.d3bug.paypal;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.d3bug.registration.AuditTrail;
import com.d3bug.registration.PaypalItem;
import com.d3bug.registration.PaypalNotification;

public abstract class AbstractPaypalNotification implements PaypalNotification {
	
	protected AuditTrail audit = new AuditTrail(); 

	protected boolean valid;	
	protected Map<String,String> parameters = new HashMap<String,String>();
	
	private List<PaypalItem> items = new ArrayList<PaypalItem>();

	public boolean isValid() {
		return valid;
	}
	
	public boolean isCompleted() {
		return valid && getPaymentStatus().equalsIgnoreCase("completed");
	}
	
	public AuditTrail getAudit() { 
		return audit; 
	}
	
	public List<PaypalItem> getItems() { return items; }
	
	private void buildItems() {
		items.clear();
		String itemCode = getParam("item_number");
		String itemName = getParam("item_name");
		String quantity = getParam("quantity");
		String grossAmount = getParam("mc_gross");
		items.add(new PaypalItemImpl(itemCode,itemName,quantity,grossAmount));
	} 
	
	public String getFirstName() { return getParam("first_name"); }
	public String getLastName() { return getParam("last_name"); }
	public String getPaymentStatus() { return getParam("payment_status"); }
	public String getPayerEmail() { return getParam("payer_email"); }
	public String getPaymentGross() { return getParam("mc_gross"); }
	public String getMcCurrency() { return getParam("mc_currency"); }
	public String getMcFee() { return getParam("mc_fee"); }
	public String getPaymentDate() { return getParam("payment_date"); } // Example: 23:36:57 Oct 20, 2008 PDT
	public String getPaymentRef() { return getParam("txn_id"); } 
	public String getResidenceCountry() { return getParam("residence_country"); }
	public String getPaymentFee() { return getParam("payment_fee"); } 
	
	protected String getParam(String key) {
		String v = parameters.get(key);
		return (v == null) ? "" : v;
	}
	
	public Map<String, String> getFields() { return parameters; }
	
	protected String decodeRequest(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		Enumeration en = request.getParameterNames();
		while (en.hasMoreElements()) {
			String name = (String)en.nextElement();
			String param = request.getParameter(name);
			System.out.println(name+" !-> "+param);
			sb.append("&" + name + "=" + URLEncoder.encode(param));
			parameters.put(name,param);
		}
		audit.out("DECODE: "+sb);
		buildItems();
		dumpToAudit();
		return sb.toString();
	}
	
	private void dumpToAudit() {
		for (Map.Entry<String, String> en : parameters.entrySet()) {
			audit.out(en.getKey()+" :--> "+en.getValue());
		}
	}

	protected HttpURLConnection callVerifyUrl(String cmd) throws IOException {
		URL u = new URL(PayPalConfig.getVerifyUrl());
		HttpURLConnection uc = (HttpURLConnection)u.openConnection();
		uc.setDoOutput(true);
		uc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		PrintWriter pw = new PrintWriter(uc.getOutputStream());
		pw.println(cmd);
		audit.out("Calling url: "+u+" , Post:"+cmd);
		pw.close();
		return uc;
	}
	
	private static class PaypalItemImpl implements PaypalItem {
		
		private String itemCode;
		private String itemName;
		private String quantity;
		private String amount;
		
		public PaypalItemImpl(String itemCode, String itemName, String quantity, String amount) {
			this.itemCode = itemCode;
			this.itemName = itemName;
			this.quantity = quantity;
			this.amount = amount;
		}
		
		public String getItemCode() { return itemCode; }
		public String getItemDescription() { return itemName; }
		public String getQuantity() { return quantity; }
		public String getAmount() { return amount; }

		@Override
		public String toString() {
			return "PaypalItemImpl [itemCode=" + itemCode + ", itemName="
					+ itemName + ", quantity=" + quantity + ", amount="
					+ amount + "]";
		}
		
	}

	@Override
	public String toString() {
		return "AbstractPaypalNotification [audit=" + audit + ", valid="
				+ valid + ", parameters=" + parameters + ", items=" + items
				+ "]";
	}
	
	


}
