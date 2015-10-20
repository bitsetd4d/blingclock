package com.d3bug.paypal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.d3bug.registration.AuditTrail;

/*
 * We get called with all the info, we call back to check details
 * not fraudulent
 * https://cms.paypal.com/uk/cgi-bin/?cmd=_render-content&content_ID=developer/e_howto_html_IPNandPDTVariables
 */
public class PaypalIPN extends AbstractPaypalNotification {

	public void loadAndVerifyIPN(HttpServletRequest request) throws ServletException, IOException {
		valid = false;
		audit.out("D3BUG <-- PAYPAL: IPN Notification");
		StringBuilder sb = new StringBuilder();
		sb.append("cmd=_notify-validate");
		sb.append(decodeRequest(request));
		audit.out("D3BUG --> PAYPAL: Asking for verification");
		HttpURLConnection uc = callVerifyUrl(sb.toString());

		audit.out("D3BUG ... PAYPAL: Waiting for response");
		//PayPal responds to the postback with VERIFIED or INVALID
		BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		String result = in.readLine();
		audit.out("D3BUG <--- PAYPAL: IPN Verification response: "+result);
		String x = null;
		while ((x = in.readLine()) != null) {
			audit.out("IPN: "+x);
		}
		in.close();

		audit.out("Status: "+result);

		//check notification validation
		if (result.equals("VERIFIED")) {
			valid = true;
		} else {
			audit.out("PAYPAL -- unverified response for "+sb);
			if (PayPalConfig.isIgnoreVerification()) {
				audit.out("*Failed verify - but assuming OK*");
				valid = true;
			}
		}
	}

	
	/*
	 * PayPal will respond to the post with a single word, "VERIFIED" or "INVALID", in the body of the response. When you receive a VERIFIED response, you need to perform several checks before fulfilling the order:
Confirm that the "payment_status" is "Completed," since IPNs are also sent for other results such as "Pending" or "Failed"
Check that the "txn_id" is not a duplicate to prevent a fraudster from using reusing an old, completed transaction
Validate that the "receiver_email" is an email address registered in your PayPal account, to prevent the payment from being sent to a fraudster's account
Check other transaction details such as the item number and price to confirm that the price has not been changed
	 */

}
