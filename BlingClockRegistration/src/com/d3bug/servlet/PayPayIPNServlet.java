package com.d3bug.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.d3bug.billing.CountryInfo;
import com.d3bug.billing.CountryRegistry;
import com.d3bug.billing.Receipt;
import com.d3bug.paypal.PaypalIPN;
import com.d3bug.registration.AuditTrail;
import com.d3bug.registration.PaypalItem;
import com.d3bug.registration.RegistrationRegistry;
import com.d3bug.saver.SaveCSV;
import com.d3bug.saver.Saver;

public class PayPayIPNServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public PayPayIPNServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PaypalIPN ipn = new PaypalIPN();
		ipn.loadAndVerifyIPN(request);
		if (!ipn.isValid()) {
			logInvalidIPN();
			return;
		} 
		if (!ipn.isCompleted()) {
			logNotCompletedStatus();
			return;
		}
		if (!checkItemsOK(ipn)) return;

		String internalId = Saver.getInstance().getNextInternalId(ipn.getPaymentRef());
		CountryInfo country = CountryRegistry.getInstance().getCountryInfo(ipn.getResidenceCountry());
		AuditTrail audit = ipn.getAudit();
		audit.setInternalId(internalId);
		Receipt receipt = createReceipt(internalId,audit,country,ipn);
		Saver.getInstance().persist(receipt);
		RegistrationRegistry.getInstance().onUserRegistered(internalId, audit, receipt, country, ipn);
		Saver.getInstance().persist(audit);
		
		SaveCSV csv = createCSV(receipt);
		Saver.getInstance().persist(internalId,csv);
		
	}

	private SaveCSV createCSV(Receipt receipt) {
		SaveCSV csv = new SaveCSV();
		csv.saveQuoted(receipt.getInvoiceUKDate());
		csv.saveQuoted(receipt.getInvoiceDate());
		csv.saveValue(receipt.getInternalId());
		csv.saveValue(receipt.getPaypalReference());
		csv.saveQuoted(receipt.getRecipientName());
		csv.saveValue(receipt.getRecipientEmail());
		csv.saveValue(receipt.getCountryCode());
		csv.saveValue(receipt.getCountryDescription());
		csv.saveValue(receipt.getVatCode());
		csv.saveValue(String.valueOf(receipt.getVatRate()));
		csv.saveValue(receipt.getCurrency());
		csv.saveValue(receipt.getDisplayNet());
		csv.saveValue(receipt.getDisplayVat());
		csv.saveValue(receipt.getDisplayTotal());
		return csv;
	}

	private boolean checkItemsOK(PaypalIPN ipn) {
		List<PaypalItem> items = ipn.getItems();
		if (items.isEmpty()) {
			System.out.println("Items empty! : "+ipn);
			return false;
		}
		PaypalItem i = items.get(0);
		if (i.getItemCode().startsWith("B")) return true;
		System.out.println("Problem with item: "+i);
		return false;
	}

	private Receipt createReceipt(String internalId, AuditTrail audit,CountryInfo country, PaypalIPN ipn) {
		Receipt r = new Receipt(internalId);
		r.setFrom(audit,country,ipn);
		return r;
	}

	private void logNotCompletedStatus() { System.out.println("NOT-COMPLETED: The IPN indicated payment was not completed"); }
	private void logInvalidIPN() { System.out.println("INVALID-IPN: The IPN was not valid"); }

}
