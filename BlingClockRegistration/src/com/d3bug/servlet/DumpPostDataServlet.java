package com.d3bug.servlet;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class DumpPostDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public DumpPostDataServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
		dumpRequest(request);
		response.setStatus(200);
	}
	
	private void dumpRequest(HttpServletRequest request) {
		System.out.println("Request "+request.getRequestURI());
		Enumeration en = request.getHeaderNames();
		while (en.hasMoreElements()) {
			String name = (String)en.nextElement();
			String header = request.getHeader(name);
			System.out.println(name+" --> "+header);
		}
		en = request.getParameterNames();
		while (en.hasMoreElements()) {
			String name = (String)en.nextElement();
			String param = request.getParameter(name);
			System.out.println(name+" !-> "+param);
		}

	}

}
