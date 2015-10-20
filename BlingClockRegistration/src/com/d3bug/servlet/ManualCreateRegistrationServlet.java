package com.d3bug.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.d3bug.email.Email;
import com.d3bug.email.EmailSender;
import com.d3bug.net.PageRepository;
import com.d3bug.util.LicenceUtil;
import com.d3bug.util.StringUtil;

public class ManualCreateRegistrationServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private String regEmail = "";
	private String regPassword = "";
       
    public ManualCreateRegistrationServlet() {
        super();
    }
    
    public void init() throws ServletException {
        System.out.println("Reading init params for ManualCreateRegistrationServlet");
        regEmail = getServletConfig().getInitParameter("reg-email");
        regPassword = getServletConfig().getInitParameter("reg-password");
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String special = request.getParameter("special");
		String password = request.getParameter("password");		
		if (regPassword.equals(password)) {
			generateRegistrationEmail(name,email,special);
			setMessage(response,"Generated email to usual place ...");
		} else {
			setMessage(response,"Password doesn't match");
		}
	}

	private void setMessage(HttpServletResponse response, String message) throws IOException {
		response.getWriter().append("<html><head><title>Manual License Gen</title></head><body>");
		response.getWriter().append("<p>");
		response.getWriter().append(message);
		response.getWriter().append("</p>");
		response.getWriter().append("</body></html>");
	}

	private void generateRegistrationEmail(String name,String email,String special) {
		System.out.println("MANUAL -> Generating registration email for "+name+", email: "+email+", special: "+special);
		String when = new Date().toGMTString();
		String licence = LicenceUtil.generateLicence(name, email, when, special);
		String licenseCols = StringUtil.toCols(licence,60);
		String emailText = PageRepository.getInstance().getPage("blingclock", "reg-email");
		String finalText = LicenceUtil.substitute(emailText, "", name, email, when, licenseCols, new HashMap<String,String>());
		String subject = LicenceUtil.getRegEmailSubject();
		
		Email e = new Email("internal");
		e.setTo(regEmail);
		e.setSubject(subject);
		e.print(finalText);
		
		EmailSender.getInstance().send(e);
	}

}
