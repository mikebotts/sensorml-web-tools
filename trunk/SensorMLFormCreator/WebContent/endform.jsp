<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>End Form</title>
</head>
<body>

<BODY BGCOLOR= "#99CC99">
<IMG SRC="./images/VASTtopBanner.jpg">

<!-- Try to call dispatch method defined below on Submit action -->
<!-- this automatically calls the dispatch method rather than waiting for a button push -->
<!-- This is automatically firing off the servlet without waiting for submit button  -->

<!--  This FORM action is not forwarding the current request,response -->
<!--<FORM ACTION=invokeMethod("/SubmitWebSensor.doGet(request,response)")>-->


<FORM ACTION="BeanServlet">

<jsp:useBean id="myWebBean" class="org.vast.sensorml.forms.WebcamInfo" scope="session"/>

<!--<FORM ACTION=<forward page="/SubmitWebSensor">-->

<INPUT TYPE="hidden" NAME="docStatus" VALUE="validForm">

<style>
    FIELDSET { border: 3px double black }
    LEGEND { color: black }
    TEXT {font: normal 14pt "Nueva Roman", "Lithios Regular"}
</style>

<FIELDSET>
<b>Sensor Type: </b><%= myWebBean.getSensorType() %><BR>
<%System.out.println("SensorType: " + myWebBean.getSensorType()); %>
</FIELDSET>
<BR>

<FIELDSET>
<LEGEND>SITE DESCRIPTION</LEGEND>
<b>Site ID: </b><%= myWebBean.getSiteID() %><BR>
<b>Site Description: </b><%= myWebBean.getSiteText() %>
</FIELDSET>
<BR>

<FIELDSET>
<LEGEND>CONTACT INFORMATION</LEGEND>
<b>Full Name: </b><%= myWebBean.getFullName() %><BR>
<b>Organization: </b><%= myWebBean.getOrganization() %><BR>
<b>Role: </b><%= myWebBean.getRole() %><BR>	
<b>Street Address: </b><%= myWebBean.getStreetAddress() %><BR>
<b>City: </b><%= myWebBean.getCity() %><BR>
<b>State: </b><%= myWebBean.getState() %><BR>
<b>Zip Code: </b><%= myWebBean.getZipCode() %><BR>
<b>Country: </b><%= myWebBean.getCountry() %><BR>
<b>Phone Number: </b><%= myWebBean.getPhoneNumber() %><BR>
<b>Email Address: </b><%= myWebBean.getEmailAddress() %>
</FIELDSET>
<BR>

<FIELDSET>
<LEGEND>WEB CAMERA DESCRIPTION</LEGEND>
<b>Manufacturer Name: </b><%= myWebBean.getManufacturerName() %><BR>
<b>Model Number: </b><%= myWebBean.getModelNumber() %><BR>
<b>Serial Number: </b><%= myWebBean.getSerialNumber() %><BR>
</FIELDSET>
<BR>

<FIELDSET>
<LEGEND>WEB CAMERA LOCATION</LEGEND>
<b>Longitude: </b><%= myWebBean.getLocationx() %> degrees<BR>
<b>Latitude: </b><%= myWebBean.getLocationy() %> degrees<BR>
<b>Altitude: </b><%= myWebBean.getLocationz() %> meters<BR>
<b>Zero Position Direction: </b><%= myWebBean.getZeroLookDirection() %> degrees
</FIELDSET>
<% myWebBean.setDocStatus("formValid"); %>
<BR>

	<center><INPUT TYPE= "SUBMIT" VALUE="Submit"></center>
	<center><INPUT TYPE="button" VALUE="Edit" onClick="history.go(-1)"></center>

</FORM>

<%!
public void dispatch(HttpServletRequest request,HttpServletResponse response){
//System.out.println("Mike was here"); 
RequestDispatcher dispatcher =
		getServletContext().getRequestDispatcher("/SubmitWebSensor");
	try {
		dispatcher.forward(request,response);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}%>
</body>
</html>
