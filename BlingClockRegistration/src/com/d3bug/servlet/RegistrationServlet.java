package com.d3bug.servlet;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.d3bug.net.PageRepository;
import com.d3bug.paypal.PaypalPDT;
import com.d3bug.util.LicenceUtil;
import com.d3bug.util.StringUtil;

public class RegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public RegistrationServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		String site = identifySite(uri);
		String page = identifyPage(uri);

		PaypalPDT pdt = new PaypalPDT();
		pdt.loadFromRequest(request);
		if (pdt.isCompleted()) {
			System.out.println("COMPLETED");
		} else {
			System.out.println("NOT COMPLETED");
			page = "problem-page";
		}
		String pageHtml = PageRepository.getInstance().getPage(site, page);
		if (pageHtml == null) {
			response.getWriter().append("<html><head></head><body>");
			response.getWriter().append("<p>Cannot find page "+uri);
			response.getWriter().append("<p>Site="+site+", page="+page+", uri="+uri);
			response.getWriter().append("</body></html>");
			return;
		}
		String firstName = pdt.getFirstName();
		String lastName = pdt.getLastName();
		String email = pdt.getPayerEmail();
		String when = pdt.getPaymentDate();
		String licence = LicenceUtil.generateLicence(firstName, lastName, email, when, "");
		licence = StringUtil.toCols(licence, 60);
		licence = licence.replace("\n", "<br>");
		pageHtml = LicenceUtil.substitute(pageHtml,pdt.getPaymentRef(),firstName + " " + lastName, email, when, licence, new HashMap<String,String>());
		response.getWriter().append(pageHtml);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
	}
	
	private String identifySite(String uri) {
		int i1 = uri.indexOf("/",1);
		int i2 = uri.indexOf("/",i1+1);
		return uri.substring(i1+1,i2);
	}
	
	private String identifyPage(String uri) {
		int i3 = uri.lastIndexOf("/");
		int i4 = uri.indexOf(".",i3);
		return uri.substring(i3+1,i4);
	}

}
