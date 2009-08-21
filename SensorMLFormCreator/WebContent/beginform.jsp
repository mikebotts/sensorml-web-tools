<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Beginner Form</title>
</head>
<body>

<BODY BGCOLOR= "#99CC99">
<IMG SRC="./images/VASTtopBanner.jpg">
<FORM ACTION="BeanServlet">
<style>
    FIELDSET { border: 3px double black }
    LEGEND { color: black }
</style>

<INPUT TYPE="hidden" NAME="sensorType" VALUE="webCam">
<INPUT TYPE="hidden" NAME="docStatus" VALUE="initialForm">



<FIELDSET>
<LEGEND>SITE DESCRIPTION</LEGEND>
	Site ID:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT TYPE= "TEXT" NAME="siteID"><BR>
	Site Description: <CENTER><TEXTAREA NAME="siteText" ROWS="5" COLS="65"></TEXTAREA></CENTER>
</FIELDSET>
<BR>

<FIELDSET>
<LEGEND>CONTACT INFORMATION</LEGEND>
	Full Name:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT TYPE= "TEXT" NAME="fullName" SIZE="20"><BR>
	Organization:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT TYPE= "TEXT" NAME="organization"><BR>
	Role:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT TYPE= "TEXT" NAME="role"><BR>
	Street Address:&nbsp;&nbsp;<INPUT TYPE= "TEXT" NAME="streetAddress" SIZE="38"><BR>
	City:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT TYPE= "TEXT" NAME="city"><BR>
	State:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT TYPE= "TEXT" NAME="state" SIZE="20"><BR>
	Zip Code:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT TYPE= "TEXT" NAME="zipCode" SIZE="5"><BR>
	Country:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT TYPE= "TEXT" NAME="country"><BR>
	Phone Number:&nbsp;<INPUT TYPE= "TEXT" NAME="phoneNumber"><BR>
	Email Address:&nbsp;&nbsp;<INPUT TYPE= "TEXT" NAME="emailAddress">
</FIELDSET>
<BR>

<FIELDSET>
<LEGEND>WEB CAMERA DESCRIPTION</LEGEND>
	Manufacturer Name: 
	<select name="manufacturerName" onChange="if (this.selectedIndex != 0) populate(this.form,this)" size="1"> 
    <option selected>Select Manufacturer Name</option> 
	<OPTION VALUE="AGNeovo">AG Neovo
	<OPTION VALUE="AGNPRO">AGN PRO
	<OPTION VALUE="Altronix">Altronix
	<OPTION VALUE="AVE">American Video Equipment
	<OPTION VALUE="APPRO">APPRO
	<OPTION VALUE="Axis">Axis
	<OPTION VALUE="Canon">Canon
	<OPTION VALUE="Cantek">Cantek
	<OPTION VALUE="ChannelPlus">Channel Plus
	<OPTION VALUE="ChannelVision">Channel Vision
	<OPTION VALUE="CNB">CNB
	<OPTION VALUE="CPCam">CP Cam
	<OPTION VALUE="Crow">Crow Electronics
	<OPTION VALUE="CTI">CTI
	<OPTION VALUE="CVW">CVW Corp
	<OPTION VALUE="Cyrex">Cyrex Networks
	<OPTION VALUE="ELMO">Elmo
	<OPTION VALUE="Elyssa">Elyssa Corporation
	<OPTION VALUE="Everfocus">Everfocus
	<OPTION VALUE="GeoVision">GeoVision
	<OPTION VALUE="Nuvico">Nuvico
	<OPTION VALUE="OPTEX">OPTEX
	<OPTION VALUE="Panasonic">Panasonic
	<OPTION VALUE="Pelco">Pelco
	<OPTION VALUE="Samsung">Samsung
	<OPTION VALUE="Sanyo">Sanyo
	<OPTION VALUE="SHARP">SHARP
	<OPTION VALUE="Sony">Sony
	<OPTION VALUE="Speco">Speco
	<OPTION VALUE="Toshiba">Toshiba
	</OPTION>
	</SELECT><BR>
	Model Number: <BR>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<SELECT name="modelNumber" SIZE=5 MULTIPLE>
	<optgroup label="Axis">
	<option value="233D">233 D
	<option value="PTZ213">213 PTZ
	</optgroup>
	<optgroup label="Sony">
	<option value="415W">415 W
	<option value="324K">324 K
	</optgroup>
	</SELECT><BR>
	Serial Number:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT TYPE= "TEXT" NAME="serialNumber"><BR>
	
</FIELDSET>
<BR>

<FIELDSET>
<LEGEND>WEB CAMERA POSITION</LEGEND>
	Longitude (deg):&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT TYPE= "TEXT" NAME="locationx"><BR>
	Latitude (deg):&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT TYPE= "TEXT" NAME="locationy"><BR>
	Altitude (m):&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT TYPE= "TEXT" NAME="locationz"><BR>
	Zero Position Direction (degrees from North):&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<INPUT TYPE= "TEXT" NAME="zeroLookDirection"><BR>
</FIELDSET>
<BR>
	<center><INPUT TYPE= "SUBMIT" VALUE="Submit">
	<INPUT TYPE= "RESET" VALUE="Reset"></center>
</FORM>
</body>
</html>