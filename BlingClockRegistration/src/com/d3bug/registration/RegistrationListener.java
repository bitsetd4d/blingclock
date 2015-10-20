package com.d3bug.registration;

import com.d3bug.billing.CountryInfo;
import com.d3bug.billing.Receipt;

public interface RegistrationListener {
	
	void onUserRegistered(String internalId,AuditTrail audit,Receipt receipt,CountryInfo country,PaypalNotification paypal);

}
