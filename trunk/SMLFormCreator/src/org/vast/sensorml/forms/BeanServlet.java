package org.vast.sensorml.forms;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
//import org.test.*;
//import java.io. *;
//import java.util.Enumeration;
//import org.vast.process.ProcessException;
//import org.apache.commons.beanutils.BeanUtils;


public class BeanServlet extends HttpServlet{
	public static final long serialVersionUID = 0;
	
	public void doGet(HttpServletRequest request,
					  HttpServletResponse response)
		throws ServletException, IOException{
		
		if (request.getParameter("docStatus")!=null){
			if (request.getParameter("docStatus").equalsIgnoreCase("initialForm")){
				System.out.println("I'm in Initial Form");
	
				WebcamInfo bean = new WebcamInfo();
				BeanUtilities.populateBean(bean,request);
				
				request.getSession(true).setAttribute("myWebBean", bean);
				//request.setAttribute("myWebBean", bean);
				
				System.out.println("fromrequest:sensorType = " + request.getParameter("sensorType"));
				System.out.println("fromBean:sensorType = " + bean.getSensorType());
				System.out.println("fromrequest:city = " + request.getParameter("city"));
				System.out.println("fromBean:city = " + bean.getCity());
				System.out.println("fromrequest:state = " + request.getParameter("state"));
				System.out.println("fromBean:state = " + bean.getState());
				
				WebcamInfo myWebBean = (WebcamInfo)request.getSession().getAttribute("myWebBean");
				System.out.println("fromMyWebBean:city = " + myWebBean.getCity());
				System.out.println("fromMyWebBean:sensorType = " + myWebBean.getSensorType());
				
				RequestDispatcher dispatcher =
					getServletContext().getRequestDispatcher("/endform.jsp");
				dispatcher.forward(request,response);
			}
			else if (request.getParameter("docStatus").equalsIgnoreCase("validForm")){
				
				WebcamInfo bean = (WebcamInfo) request.getSession().getAttribute("myWebBean");
				
				System.out.println("I'm in Valid Form");
		
				System.out.println("fromValidBean:sensorType = " + bean.getSensorType());
				System.out.println("fromValidBean:city = " + bean.getCity());
				System.out.println("fromValidBean:state = " + bean.getState());
			
				RequestDispatcher dispatcher =
					getServletContext().getRequestDispatcher("/SubmitWebSensor");
				dispatcher.forward(request,response);
		
			}
		}
	}
}
