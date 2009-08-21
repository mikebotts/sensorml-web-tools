<BODY BGCOLOR= "#99CC99">
<center><IMG SRC="./images/VASTtopBanner.jpg"></center>
<title>SensorML TableView Uploader</title>

<b><center><font size="5"><LEGEND>SensorML TableView Uploader</LEGEND></font></center></b>

<td align="left"><br>The SensorML TableView Uploader provides the user with the following options:</td>
<font size="3"><td align="left"><br>1) Use the file upload portion to generate a table view of an already existing 
SensorML description located on your computer.<br>2) Use the url upload portion to generate a table view of a SensorML 
description that is not located on your computer but rather on a site external to your system.<br></td></font>

<center>

<form method="Post" action="TableView" name="upform" enctype="multipart/form-data">
  <table width="60%" border="0" cellspacing="1" cellpadding="1" align="center" class="style1">
    <tr>
      <td align="left">Select a file to upload :</td>
    </tr>
    <tr>
      <td align="left">
        <input type="file" name="uploadfile" size="50">
        <input type="submit" name="Submit" value="Upload">
        </td>
    </tr>
  </table>  
</form>
 
 
 <form method="Post" action="TableView" name="upform" enctype="text">
  <table width="60%" border="0" cellspacing="1" cellpadding="1" align="center" class="style1">
    <tr>  
      <td align="left">Enter url of the Sensor Description:</td>
    </tr>
    <tr>
	<td align="left"> 
		<input type="text" name="url" size="50">
        <input type="submit" name="Submit" value="Upload">
<!--        <input type="reset" name="Reset" value="Cancel">-->
    </td>
    </tr>
  </table>  
</form>
</center>
<br>
<br>
This <a target="_blank" href="http://vast.uah.edu/SensorMLTableView/TableView?url=http://vast.uah.edu/downloads/sensorML/v1.0/examples/sensors/TigersharkUAV/GSI_KCM-HD_System.xml">UAV Example</a> provides an idea of what the Table form should look.
If the table form does not look similar to the image then 
consider using the <a target="_blank" href="http://vast.uah.edu/SensorMLforms/validate.jsp"> validation tool</a> to ensure the XML file is valid. 
NOTE: PrettyView does at present expect that the document starts with [SensorML] element 
with the first member (e.g. System, Component, ProcessModel, or ProcessChain) being the 
element of interest.
<br>
<br><center>
Created by: Amanda Wyatt & Mike Botts<br>
Comments or Suggestions: mailTo:mike.botts@uah.edu
</center>
