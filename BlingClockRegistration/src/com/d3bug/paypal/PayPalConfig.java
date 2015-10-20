package com.d3bug.paypal;

public class PayPalConfig {
	
	private static boolean sandbox;
	private static String identityToken;
	private static String receiverEmail;
	private static boolean ignoreVerification;
	
	public static boolean isSandbox() {
		return sandbox;
	}
	public static void setSandbox(boolean sandbox) {
		PayPalConfig.sandbox = sandbox;
		if (sandbox) {
			System.out.println("--- SANDBOX MODE ---");
			System.out.println("--- SANDBOX MODE ---");
			System.out.println("--- SANDBOX MODE ---");
		} else {
			System.out.println("*** LIVE PAYPAL ***       *** LIVE PAYPAL ***       *** LIVE PAYPAL ***");
			System.out.println(" *** LIVE PAYPAL ***       *** LIVE PAYPAL ***       *** LIVE PAYPAL ***");
			System.out.println("  *** LIVE PAYPAL ***       *** LIVE PAYPAL ***       *** LIVE PAYPAL ***");
			System.out.println(" *** LIVE PAYPAL ***       *** LIVE PAYPAL ***       *** LIVE PAYPAL ***");
			System.out.println("*** LIVE PAYPAL ***       *** LIVE PAYPAL ***       *** LIVE PAYPAL ***");
		}
	}
	
	public static String getIdentityToken() {
		return identityToken;
	}
	public static void setIdentityToken(String identityToken) {
		System.out.println("Identity token set: "+identityToken);
		PayPalConfig.identityToken = identityToken;
	}
	
	public static void setReceiverEmail(String receiverEmail) {
		System.out.println("Receiver email set: "+receiverEmail);
		PayPalConfig.receiverEmail = receiverEmail;
	}
	public static String getReceiverEmail() { 
		return receiverEmail;
	}
	
	public static String getVerifyUrl() { 
		return sandbox ? "https://www.sandbox.paypal.com/cgi-bin/webscr" : "https://www.paypal.com/cgi-bin/webscr";
	}
	
	public static void setIgnoreVerification(boolean v) {
		ignoreVerification = v;
	}
	
	public static boolean isIgnoreVerification() { 
		return ignoreVerification;
	}
	
}
