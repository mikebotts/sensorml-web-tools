<BODY BGCOLOR= "#99CC99">
<center><IMG SRC="./images/VASTtopBanner.jpg"></center>
<title>SensorML Validator</title>

<b><center><font size="5"><LEGEND>SensorML Validator</LEGEND></font></center></b>

<font size="3"><td align="center"><br>The SensorML Validator is used to ensure 
a SensorML description is proper and valid. This currently only supports general XML schema
validation by Xerces. Future versions may include more fine grain validation based on best
practices for SensorML.
<br></td></font>



<form method="Post" action="ValidateSML" name="upform" enctype="multipart/form-data">
  <table width="60%" border="0" cellspacing="1" cellpadding="1" align="center" class="style1">
    <tr>
      <td align="left">Select a file to validate :</td>
    </tr>
    <tr>
      <td align=left>
        <input type="file" name="uploadfile" size="50">
        </td>
    </tr>
    <tr>
      <td align="left">
		<input type="hidden" name="todo" value="upload">
        <input type="submit" name="Submit" value="Validate">
        </td>
    </tr>
  </table>  
</form>


<form method="Post" action="ValidateSML" name="valform" enctype="text">
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
  	</td>
  	</tr>
  </table>  
</form>

<br>
<br>
<br><center>
Created By: Amanda Wyatt & Mike Botts<br>
Comments or Suggestions: wyatt@nsstc.uah.edu
</center>
