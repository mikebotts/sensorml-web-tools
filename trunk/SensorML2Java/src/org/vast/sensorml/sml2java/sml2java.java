package org.vast.sensorml.sml2java;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class sml2java extends HttpServlet {
	public static final long serialVersionUID = 0;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		//System.out.println("I am here");
		if (request == null)
			System.out.println("Request equals null");

//		java output class ;
		SensorML2Java javaClass = new SensorML2Java(request, response);
	}
}
