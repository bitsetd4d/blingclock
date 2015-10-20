package com.d3bug.registration;

import java.util.ArrayList;
import java.util.List;

import com.d3bug.billing.CountryInfo;
import com.d3bug.billing.Receipt;

public class RegistrationRegistry implements RegistrationListener {
	
	private static final RegistrationRegistry INSTANCE = new RegistrationRegistry();
	public static RegistrationRegistry getInstance() { return INSTANCE; }

	private List<RegistrationListener> listeners = new ArrayList<RegistrationListener>();
	
	public void addListener(RegistrationListener l) { listeners.add(l); }
	public void removeListener(RegistrationListener l) { listeners.remove(l); }
	
	public void onUserRegistered(String internalId, AuditTrail audit, Receipt receipt, CountryInfo country, PaypalNotification ipn) {		
		for (RegistrationListener l : listeners) {
			try {
				l.onUserRegistered(internalId,audit,receipt,country,ipn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


}
