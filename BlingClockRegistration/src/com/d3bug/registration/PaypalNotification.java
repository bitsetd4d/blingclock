package com.d3bug.registration;

import java.util.List;
import java.util.Map;

public interface PaypalNotification {

	public abstract boolean isValid();
	public abstract boolean isCompleted();

	public abstract String getFirstName();
	public abstract String getLastName();

	public abstract String getPaymentStatus();
	public abstract String getPayerEmail();

	public abstract String getPaymentGross();
	public abstract String getMcCurrency();
	public abstract String getMcFee();

	public abstract String getResidenceCountry();
	public abstract String getPaymentDate(); // Example: 23:36:57 Oct 20, 2008 PDT
	public abstract String getPaymentRef();  // eg. 2VJ889157C077341N
	public abstract String getPaymentFee();

	List<PaypalItem> getItems();
	public Map<String,String> getFields();
	
//	public abstract String getQuantity();
//	public abstract String getItemNumber();
//	public abstract String getItemName();
	
	
}