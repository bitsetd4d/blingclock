package com.d3bug.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.d3bug.email.EmailSender;
import com.d3bug.net.PageRepository;
import com.d3bug.paypal.PayPalConfig;
import com.d3bug.registration.RegistrationRegistry;
import com.d3bug.registration.handlers.GenerateRegistrationAndEmail;
import com.d3bug.util.LicenceUtil;


public class InitialiseRegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public void init() throws ServletException {
    	showBanner();
        System.out.println("Reading init params");
//        String licence = getServletConfig().getInitParameter("licence-page");
//        String problem = getServletConfig().getInitParameter("problem-page");
//        String regEmail = getServletConfig().getInitParameter("reg-email");
        String regEmailSubject = getServletConfig().getInitParameter("reg-email-subject");
        String pages = getServletConfig().getInitParameter("pages-to-cache");
        loadPagesToCache(pages);
//        PageRepository.getInstance().registerPage("blingclock","license-page", licence);
//        PageRepository.getInstance().registerPage("blingclock","problem-page", problem);
//        PageRepository.getInstance().registerPage("blingclock","reg-email", regEmail);
        
//        System.out.println("licence-page is -> "+licence);
//        System.out.println("problem-page is -> "+problem);
        
        initMailSettings();
        initPaypalSettings();
        LicenceUtil.setRegEmailSubject(regEmailSubject);
        initRegistrationListeners();
    }

	private void loadPagesToCache(String pages) {
		String[] split = pages.split("\n");
		for (String x : split) {
			String[] pair = x.split("=");
			String key = pair[0].trim();
			String url = pair[1].trim();
			System.out.println("Register page "+key+" -> "+url);
	        PageRepository.getInstance().registerPage("blingclock",key, url);

		}
	}

	private void showBanner() {
		System.out.println("*********************************************************");
		System.out.println("   ##    #####    #    ###    #####  ###    #  #    ##   ");
		System.out.println("  #  #     #     # #   #  #     #     #     ## #   #  #  ");
		System.out.println("   #       #    #   #  #  #     #     #     # ##   #     ");
		System.out.println("    #      #    #####  ###      #     #     #  #   # ##  ");
		System.out.println("  #  #     #    #   #  #  #     #     #     #  #   #  #  ");
		System.out.println("   ##      #    #   #  #  #     #    ###    #  #    ##   ");
		System.out.println("*********************************************************");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
	}
	
	private void initMailSettings() {
		String from = getServletConfig().getInitParameter("mail-from");
		String mailer = getServletConfig().getInitParameter("mailer");
		String mailHost = getServletConfig().getInitParameter("mailhost");
		String mailFake = getServletConfig().getInitParameter("mailfake");
		String bcc = getServletConfig().getInitParameter("mailbcc");
		EmailSender.getInstance().setMailerSettings(from,mailer,mailHost,mailFake.equals("true"),bcc);
	}
	
	private void initPaypalSettings() {
		boolean sandbox = getServletConfig().getInitParameter("paypal-sandbox").equals("true");
		String identity = getServletConfig().getInitParameter("paypal-my-identity");
		String receiverEmail = getServletConfig().getInitParameter("paypal-my-email");
		boolean ignoreVerification = getServletConfig().getInitParameter("ignore-ipn-verification").equals("true");
		PayPalConfig.setSandbox(sandbox);
		PayPalConfig.setIdentityToken(identity);
		PayPalConfig.setReceiverEmail(receiverEmail);
		PayPalConfig.setIgnoreVerification(ignoreVerification);
	}
	
	private void initRegistrationListeners() {
		RegistrationRegistry.getInstance().addListener(new GenerateRegistrationAndEmail());
	}

}
