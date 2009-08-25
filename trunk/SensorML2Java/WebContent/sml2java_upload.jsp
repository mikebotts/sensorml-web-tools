<html>
<title>SensorML to Java Uploader</title>
<BODY BGCOLOR= "#99CC99">
<center><IMG SRC="./images/VASTtopBanner.jpg"></center>

<b><center><font size="5"><LEGEND>SensorML to Java Uploader</LEGEND></font></center></b>

<br>The SensorML to Java servlet will generate a Java class template from a SensorML (XML) ProcessModel document.
This java class will be compatible with the API of the SensorML execution engine developed at the
University of Alabama in Huntsville. To complete the class, the developer will only need to add
the executable code to support the algorithm. <p>
The uploader provides the user with the following options:
<font size="3"><br>1) Use the file upload portion to generate the Java class from an already existing 
SensorML ProcessModel document located on your computer.<br>2) Use the url upload portion to generate the Java class from a SensorML 
ProcessModel document that is not located on your computer but rather on a site external to your system.<br></font>

<center>

<form method="Post" action="sml2java" name="upform" enctype="multipart/form-data">
<table width="60%" border="0" cellspacing="1" cellpadding="1" align="left" class="style1">
	<tr>
		<th align="left">Option 1: Upload file of the ProcessModel</th>
	</tr>
	<tr>
		<td align="left">Provide a name for the process:</td>
	</tr>
	<tr>
		<td align="left"><input type="text" name="processName"
			size="50"></td>
	</tr>
	<tr>
		<td align="left">Upload file:</td>
	</tr>
	<tr>
		<td align="left"><input type="file" name="uploadfile" size="50">
		<input type="submit" name="Submit" value="Upload"></td>
	</tr>
</table>
</form>

<form method="Post" action="sml2java" name="upform" enctype="text">
  <table width="60%" border="0" cellspacing="1" cellpadding="1" align="left" class="style1">
    <tr>
      <th align="left">Option 2: Enter online URL for the ProcessModel</th>
    </tr>
    <tr>
      <td align="left">Provide a name for the process:</td>
    </tr>
    <tr>
      <td align="left">
        <input type="text" name="processName" size="50">
        </td>
    </tr>
    <tr>  
      <td align="left">Enter URL (can copy and paste from browser):</td>
    </tr>
    <tr>
	<td align="left"> 
		<input type="text" name="url" size="50">
        <input type="submit" name="Submit" value="Upload">
    </td>
    <tr><td><br></br></td>
    </tr>
  </table> 
  <br></br>
  <br></br> 
  <br></br>
  <br></br>
  <br></br>
</form>
</center>

<p align="left">This <a target="_blank"
	href="http://vast.uah.edu/SensorML2Java/sml2java?url=http://vast.uah.edu/downloads/sensorML/v1.0/examples/processes/LLAtoECEF/LLAtoECEF.xml&processName=LLAtoECEF">Process
Model Example</a> provides an idea of how the newly created Java class
should look. If the java class does not look similar to the image then
consider using the <a target="_blank"
	href="http://vast.uah.edu/SensorMLValidator/"> validation tool</a> to
ensure the XML file is valid.
<br>
<br><center>
Created by: Amanda Wyatt & Mike Botts<br>
Comments or Suggestions: mike.botts@uah.edu
</center>
</BODY>
</html>
