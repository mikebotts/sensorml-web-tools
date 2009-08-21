package org.vast.sensorml.validator;

import java.io.*;
import java.util.List;
import org.apache.xerces.parsers.DOMParser;

import java.applet.*;
import java.awt.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.vast.ogc.OGCRegistry;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletInputStream;

import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ValidateSML extends HttpServlet {
	public static final long serialVersionUID = 0;

	String smlNamespace = "http://www.opengis.net/sensorML/1.0.1";
	String sweNamespace = "http://www.opengis.net/swe/1.0.1";
	String gmlNamespace = "http://www.opengis.net/gml";
	String icNamespace = "urn:us:gov:ic:ism:v2";

	int tabcount = 0;

	PrintWriter out;
	HttpServletResponse response;
	HttpServletRequest request;
	Document dom;
	DOMHelper domHelp = null;

	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("I am in the Validator!!");
		doGet(request, response);
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		//System.out.println("I am here");
		if (request == null)
			System.out.println("Request equals null");
		this.request = request;
		this.response = response;

		// create output stream
		createDoc();		
		
		// Check to see if there is a parameter called "url"
		//   in the request
		String newURL = request.getParameter("url");
		// If no "url" parameter, then this is coming from a file
		//   use the parseStream method
		if (newURL==null){
			InputStream filestream = parseRequest();
			if (filestream != null) {
				try {
					domHelp = new DOMHelper(filestream, true);
					if (domHelp == null) {
						System.out.println("domHelper equals null!!");
					}
				} catch (DOMHelperException e) {
					// TODO Auto-generated catch block
					out.print("<P>" + "The following errors occurred: ");
					out.print("<br>" + "<br>");
					out.print( e + "<br>" +e.getCause().toString() + "</P>");
					//out.print(x);
					e.printStackTrace();
					throw new ServletException("<P>" + "The following errors occurred: " + e + "</P>");
					
				//	return;
				}
				out.println("<P>The Document submitted is valid!</P>");
				dom = null;
				dom = (Document) domHelp.getDocument();
			}
			else{
				System.out.println("Filestream is null!!");
				return;
			}
		}
		// if there is a "url" parameter, this came as a get request
		//   pass the URL to DomHelp and let it handle it
		else{
			System.out.println("newURL = " + newURL);		
			try {
				//domHelp = new DOMHelper(newURL, true);
				URL url = new URL(newURL);
				domHelp = new DOMHelper(url.openStream(),true);
				
				if (domHelp == null) {
					System.out.println("domHelper equals null!!");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				out.print("<P>" + "The following errors occurred: ");
				out.print("<br>" + "<br>");
				out.print( e + "</P>");
				e.printStackTrace();
				throw new ServletException("<P>" + "The following errors occurred: " + e + "</P>");
				
			}
			out.println("<P>The Document submitted is valid!</P>");
			dom = null;
			dom = (Document) domHelp.getDocument();
		}
	}


// ************************************************************************************************************************
	private void createDoc() {
		// Create Document and Initial Header
		response.setContentType("text/html");
		try {
			out = response.getWriter();
			out.flush();
			String docType = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 "
					+ "Transitional//EN\">\n";
			out.println(docType + "<HTML>\n"
					+ "<HEAD><TITLE>SensorML Validator</TITLE></HEAD>\n"
					+ "<BODY BGCOLOR=\"#FDF5E6\">\n"
					+ "<H1 ALIGN=center>SensorML Validator</H1>\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
//***************************************************************************************************
	private InputStream parseRequest() {
		int BOF = 0, EOF = 0;
		int startPos = 0;
		int endPos = 0;
		String filename = "";
		String contentType = "", fileData = "", strLocalFileName = "";

		contentType = request.getContentType();
		System.out.println("<br>Content type is :: " + contentType);
		if ((contentType != null)
				&& (contentType.indexOf("multipart/form-data") >= 0)) {
			DataInputStream in;
			DataInputStream in1 = null;
			try {
				in = new DataInputStream(request.getInputStream());
				in1 = in;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			int formDataLength = request.getContentLength();
			byte dataBytes[] = new byte[formDataLength];
			int byteRead = 0;
			int totalBytesRead = 0;
			while (totalBytesRead < formDataLength) {
				try {
					byteRead = in1.read(dataBytes, totalBytesRead,
							formDataLength);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				totalBytesRead += byteRead;
			}
			System.out.println("<br>totalBytesRead : " + totalBytesRead
					+ "    :   formDataLength = " + formDataLength);

			byte[] line = new byte[128];
			if (totalBytesRead < 3) {
				return null; // exit if file length is not sufficiently large
			}

			String boundary = "";
			String s = "";
			int count = 0;
			int pos = 0;

			// loop for extracting boundary of file
			// could also be extracted from request.getContentType()
			do {
				copyByte(dataBytes, line, count, 1); // read 1 byte at a time
				count += 1;
				s = new String(line, 0, 1);
				fileData = fileData + s;
				pos = fileData
						.indexOf("Content-Disposition: form-data; name=\""); // set
				// the
				// file
				// name
				if (pos != -1)
					endPos = pos;
			} while (pos == -1);
			boundary = fileData.substring(startPos, endPos);

			// loop for extracting filename
			startPos = endPos;
			do {
				copyByte(dataBytes, line, count, 1); // read 1 byte at a time
				count += 1;
				s = new String(line, 0, 1);
				fileData = fileData + s;
				pos = fileData.indexOf("filename=\"", startPos); // set the
				// file name
				if (pos != -1)
					startPos = pos;
			} while (pos == -1);
			do {
				copyByte(dataBytes, line, count, 1); // read 1 byte at a time
				count += 1;
				s = new String(line, 0, 1);
				fileData = fileData + s;
				pos = fileData.indexOf("Content-Type: ", startPos);
				if (pos != -1)
					endPos = pos;
			} while (pos == -1);
			filename = fileData.substring(startPos + 10, endPos - 3); // to
			// eliminate
			// "
			// from
			// start
			// & end
			strLocalFileName = filename;
			int index = filename.lastIndexOf("\\");
			if (index != -1)
				filename = filename.substring(index + 1);
			else
				filename = filename;

			// loop for extracting ContentType
			boolean blnNewlnFlag = false;
			startPos = endPos; // added length of "Content-Type: "
			do {
				copyByte(dataBytes, line, count, 1); // read 1 byte at a time
				count += 1;
				s = new String(line, 0, 1);
				fileData = fileData + s;
				pos = fileData.indexOf("\n", startPos);
				if (pos != -1) {
					if (blnNewlnFlag == true)
						endPos = pos;
					else {
						blnNewlnFlag = true;
						pos = -1;
					}
				}
			} while (pos == -1);
			contentType = fileData.substring(startPos + 14, endPos);

			// loop for extracting actual file data (any type of file)
			startPos = count + 1;
			do {
				copyByte(dataBytes, line, count, 1); // read 1 byte at a time
				count += 1;
				s = new String(line, 0, 1);
				fileData = fileData + s;
				pos = fileData.indexOf(boundary, startPos); // check for end of
				// file data i.e
				// boundary value
			} while (pos == -1);
			endPos = count - boundary.length();
			// file data extracted

			InputStream filestream = 
				new StringBufferInputStream(fileData.substring(startPos, endPos));
			
			return filestream;
			
		} else {
			System.out.println("Error in uploading ");
			return null;
		}
	}
// ************************************************************************************************************************

	// copy specified number of bytes from main data buffer to temp data buffer
	void copyByte(byte[] fromBytes, byte[] toBytes, int start, int len) {
		for (int i = start; i < (start + len); i++) {
			toBytes[i - start] = fromBytes[i];
		}
	}
// ************************************************************************************************************************

}
