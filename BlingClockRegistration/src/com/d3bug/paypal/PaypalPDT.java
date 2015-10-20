package com.d3bug.paypal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/*
 * We get called to say theres a transaction.
 * We call paypal to get the details.
 */
public class PaypalPDT extends AbstractPaypalNotification {
	
	//change button to http://www.d3bug.co.uk:8561/registration/blingclock/success/license-page.rtml 
	// for testing
	
	private String at = PayPalConfig.getIdentityToken();

	public void loadFromRequest(HttpServletRequest request) throws ServletException, IOException {
		valid = false;
		audit.out("D3BUG <------< PAYPAL: PDT Notification");		
		decodeRequest(request);
		
		StringBuilder sb = new StringBuilder();
		sb.append("at="+at);
		sb.append("&tx="+getParam("tx"));
		sb.append("&cmd=_notify-synch");

		audit.out("D3BUG >------> PAYPAL: Request transaction details");	
		HttpURLConnection uc = callVerifyUrl(sb.toString());
		
		audit.out("D3BUG ........ PAYPAL: Waiting for response");
		//PayPal responds to the postback with VERIFIED or INVALID
		BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		String result = in.readLine();
		audit.out("D3BUG <------< PAYPAL: PDT Verification response: "+result);
		String x = null;
		parameters.clear();
		while ((x = in.readLine()) != null) {
			System.out.println("X: "+x);
			String[] p = x.split("=");
			if (p.length == 2) {
				parameters.put(p[0], URLDecoder.decode(p[1]));
			}
		}
		in.close();
			
		//check notification validation
		if (result.equals("SUCCESS")) {
			valid = true;
		} else {
			System.err.println("PAYPAL -- failed to get data - "+result);
			//if (PayPalConfig.isIgnoreVerification()) {  PDT could be easily spoofed, only ignore for IPN
//				System.out.println("*Failed verify - but assuming OK*");
//				valid = true;
			//}
		}
	}

	/*
	 * PayPal will respond to the post with a single word, "VERIFIED" or "INVALID", in the body of the response. When you receive a VERIFIED response, you need to perform several checks before fulfilling the order:
Confirm that the "payment_status" is "Completed," since IPNs are also sent for other results such as "Pending" or "Failed"
Check that the "txn_id" is not a duplicate to prevent a fraudster from using reusing an old, completed transaction
Validate that the "receiver_email" is an email address registered in your PayPal account, to prevent the payment from being sent to a fraudster's account
Check other transaction details such as the item number and price to confirm that the price has not been changed
	 */

	public static void main(String[] args) throws UnsupportedEncodingException {
		String x = URLEncoder.encode("AiPC9BjkCyDFQXbSkoZcgqH3hpacA6sMw9TvGr6On5ncpJetbw0XTB-.","UTF-8");
		System.out.println(x);
	}
}
