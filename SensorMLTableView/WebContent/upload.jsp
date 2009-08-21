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
	</td>
	<td align="left"> 
        <input type="submit" name="Submit" value="Upload">
<!--        <input type="reset" name="Reset" value="Cancel">-->
    </td>
    </tr>
  </table>  
</form>
</center>

<br>
<br>
<br><center>
Created By: Amanda Wyatt & Mike Botts<br>
Comments or Suggestions: wyatt@nsstc.uah.edu
</center>
