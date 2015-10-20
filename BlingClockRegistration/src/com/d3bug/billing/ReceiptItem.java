package com.d3bug.billing;

import java.util.Map;

import com.d3bug.registration.PaypalItem;

public class ReceiptItem {
	
	private String code;
	private String description;
	private int quantity;
	private long amountPence;
		
	public ReceiptItem(String code, String description, int quantity,long amountPence) {
		this.code = code;
		this.description = description;
		this.quantity = quantity;
		this.amountPence = amountPence;
	}
	
	public ReceiptItem(PaypalItem i) {
		code = i.getItemCode();
		description = i.getItemDescription();
		quantity = Integer.parseInt(i.getQuantity());
		amountPence = convertToPence(i.getAmount());
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

	public String getItemCode() { return code; }
	public String getItemDescription() { return description; }
	public int getQuantity()           { return quantity; }
	public long getAmountPence()       { return amountPence; }

	@Override
	public String toString() {
		return "ReceiptItem [code=" + code + ", description=" + description
				+ ", quantity=" + quantity + ", amountPence=" + amountPence
				+ "]";
	}

	public void addFields(int i, Map<String, String> map) {
		map.put("code."+i, code);
		map.put("description."+i,description);
		map.put("quantity."+i, String.valueOf(quantity));
		map.put("amountPence."+i, String.valueOf(amountPence));
	}

	
}
