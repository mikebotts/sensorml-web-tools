package org.vast.sensorml.tableview;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.vast.ogc.OGCRegistry;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TableWriter 
{
	PrintWriter out;
	Document dom;
	DOMHelper domHelp = null;
	String smlNamespace;
	String sweNamespace;
	String gmlNamespace;
	String icNamespace;

	int tabcount = 0;

	public TableWriter(HttpServletRequest request, HttpServletResponse response){
		
		// create output stream
		try {
			out = createDoc(response);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace(System.err);
		}

		// Check to see if there is a parameter called "url"
		//   in the request
		String newURL = request.getParameter("url");

		// If no "url" parameter, then this is coming from a file
		//   use the parseStream method
		if (newURL==null){
			InputStream filestream = parseRequest(request);
			if (filestream != null) {
				try {
					domHelp = new DOMHelper(filestream, false);
					if (domHelp == null) {
						System.err.println("domHelper equals null!!");
					}
				} catch (DOMHelperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dom = null;
				dom = (Document) domHelp.getDocument();
			}
			else{
				System.err.println("Filestream is null!!");
				return;
			}
		}
		// if there is a "url" parameter, this came as a get request
		//   pass the URL to DomHelp and let it handle it
		else{
			try {
				//domHelp = new DOMHelper(newURL, true);
				URL url = new URL(newURL);
				domHelp = new DOMHelper(url.openStream(),false);

				if (domHelp == null) {
					System.err.println("domHelper equals null!!");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dom = null;
			dom = (Document) domHelp.getDocument();
		}

		Element rootElement = (Element) dom.getDocumentElement();
		//String name = rootElement.getLocalName();

		// check schema version number
		setVersion(rootElement);

		// If document start with with SensorML then check out its member components		
		if (rootElement.getLocalName() == "SensorML") {
		
			NodeList member = domHelp.getElements(rootElement, "sml:member");
			for (int t = 0; t < member.getLength(); t++) {
				Element firstMember = (Element) member.item(t);
				Element component = domHelp.getFirstChildElement(firstMember);
				
				// check out member element type
				handleElements(rootElement, component);
			}
		}
		else {
			// see if the document starts with one of the SensorML element types
			handleElements(rootElement, rootElement);
		}
	}
	
	private void handleElements(Element rootElement, Element component){
		if (component.getLocalName() == "Component") {
			// Handle the Components
			handleComps(rootElement, component);
		} 
		else if (component.getLocalName() == "System") {
			// Handle System
			handleSystem(rootElement, component);
		} 
		else if (component.getLocalName() == "ProcessModel") {
			// handle Process Model
			handleProcessModel(rootElement, component);
		}
		else if (component.getLocalName() == "ProcessChain") {
			// handle Process Chain
			handleProcessChain(rootElement, component);
		}
		else {
			out.println("<H2> ERROR: no recognized SensorML elements found </H2>");
			out.close();
			return;
		}
		
	}

	//*******************************************************************************************
	// set schema version number and set namespaces accordingly
	private void setVersion(Element root){
		String version = domHelp.getAttributeValue(root, "@version");		
		
		// if there is no version on root (e.g. document starts with ProcessModel)
		// then set it to 1.0.1
		// TODO: do a better fix, look at namespace designations
		if (version==null) version="1.0.1";

		if (version.equalsIgnoreCase("1.0")){
			////System.out.println("Version = " + version + "(1.0)");
			smlNamespace = "http://www.opengis.net/sensorML/1.0";
			sweNamespace = "http://www.opengis.net/swe/1.0";
			gmlNamespace = "http://www.opengis.net/gml";
			icNamespace = "urn:us:gov:ic:ism:v2";
			
			domHelp.addUserPrefix("gml", OGCRegistry
					.getNamespaceURI(OGCRegistry.GML));
			domHelp.addUserPrefix("swe", OGCRegistry.getNamespaceURI(
					"SWE", "1.0"));
			domHelp.addUserPrefix("sml", OGCRegistry.getNamespaceURI(
					"SML", "1.0"));
			domHelp.addUserPrefix("xlink", OGCRegistry.getNamespaceURI(
					OGCRegistry.XLINK, null));
			domHelp.addUserPrefix("ism", OGCRegistry
					.getNamespaceURI("IC"));
			
		} else if (version.equalsIgnoreCase("1.0.1")){
			smlNamespace = "http://www.opengis.net/sensorML/1.0.1";
			sweNamespace = "http://www.opengis.net/swe/1.0.1";
			gmlNamespace = "http://www.opengis.net/gml";
			icNamespace = "urn:us:gov:ic:ism:v2";
			
			domHelp.addUserPrefix("gml", OGCRegistry
					.getNamespaceURI(OGCRegistry.GML));
			domHelp.addUserPrefix("swe", OGCRegistry.getNamespaceURI("SWE", "1.0.1"));
			domHelp.addUserPrefix("sml", OGCRegistry.getNamespaceURI(
					"SML", "1.0.1"));
			domHelp.addUserPrefix("xlink", OGCRegistry.getNamespaceURI(
					OGCRegistry.XLINK, null));
			domHelp.addUserPrefix("ism", OGCRegistry.getNamespaceURI("IC"));		
		}
	}

	// *******************************************************************************************************
	private void handleComps(Element rootElement, Element comp) {

		// Handle gml:description
		// Find all "ident" elements
		String ident = domHelp.getAttributeValue(comp, "gml:id");
		out.println("<b>Id</b> = " + ident);
		out.println("<br>");
		// Find all "name" elements
		NodeList names = domHelp.getElements(comp, "gml:name");
		if (names.getLength() > 0) {
			Element id = (Element) names.item(0); // name
			String name = domHelp.getElementValue(id);
			out.println("<b>Name</b> = " + name);
			out.println("<br>");
		}
		// Find all "description" elements
		NodeList descriptions = domHelp.getElements(comp, "gml:description");
		if (descriptions.getLength() > 0) {
			Element id = (Element) descriptions.item(0); // description
			String description = domHelp.getElementValue(id);
			out.println("<b>Description</b> = " + description);
			out.println("<br>");
			out.println("<br>");
		}
		
		// TODO: look over this; why doing this again, its been done above?
		NodeList member = domHelp.getElements(rootElement, "sml:member");
		Element firstMember = (Element) member.item(0);
		Element component = domHelp.getFirstChildElement(firstMember);
		
		handleCommon(component, firstMember);
		handlePhysical(component, firstMember);
	}

	// *******************************************************************************************************
	private void handleSystem(Element rootElement, Element comp) {
		// Handle gml:description
		// Find all "ident" elements
		String ident = domHelp.getAttributeValue(comp, "gml:id");
		out.println("<b>Id</b> = " + ident);
		out.println("<br>");
		
		// Find all "name" elements
		NodeList names = domHelp.getElements(comp, "gml:name");
		if (names.getLength() > 0) {
			Element id = (Element) names.item(0); // name
			String name = domHelp.getElementValue(id);
			out.println("<b>Name</b> = " + name);
			out.println("<br>");
		}
		// Find all "description" elements
		NodeList descriptions = domHelp.getElements(comp, "gml:description");
		if (descriptions.getLength() > 0) {
			Element id = (Element) descriptions.item(0); // description
			String description = domHelp.getElementValue(id);
			out.println("<b>Description</b> = " + description);
			out.println("<br>");
			out.println("<br>");
		}
		NodeList member = domHelp.getElements(rootElement, "sml:member");
		Element firstMember = (Element) member.item(0);
		Element component = domHelp.getFirstChildElement(firstMember);

		handleCommon(component, firstMember);
		handlePhysical(component, firstMember);
		handleComponent(firstMember);
		handlePositionList(component, firstMember);
	}

	// *******************************************************************************************************
	private void handleProcessModel(Element rootElement, Element comp) {
		// Handle gml:description
		// Find all "ident" elements
		String ident = domHelp.getAttributeValue(comp, "gml:id");
		out.println("<b>Id</b> = " + ident);
		out.println("<br>");
		// Find all "name" elements
		NodeList names = domHelp.getElements(comp, "gml:name");

		if (names.getLength() > 0) {
			Element id = (Element) names.item(0); // name
			String name = domHelp.getElementValue(id);
			out.println("<b>Name</b> = " + name);
			out.println("<br>");
		}
		// Find all "description" elements
		NodeList descriptions = domHelp.getElements(comp, "gml:description");
		if (descriptions.getLength() > 0) {
			Element id = (Element) descriptions.item(0); // description
			String description = domHelp.getElementValue(id);
			out.println("<b>Description</b> = " + description);
			out.println("<br>");
			out.println("<br>");
		}
		NodeList member = domHelp.getElements(rootElement, "sml:member");
		Element firstMember = (Element) member.item(0);
		Element component = domHelp.getFirstChildElement(firstMember);
		
		handleCommon(component, firstMember);
		handleMethod(component, firstMember);
	}

	// *******************************************************************************************************
	private void handleProcessChain(Element rootElement, Element comp) {
		// Handle gml:description
		// Find all "ident" elements
		String ident = domHelp.getAttributeValue(comp, "gml:id");
		out.println("<b>Id</b> = " + ident);
		out.println("<br>");
		// Find all "name" elements
		NodeList names = domHelp.getElements(comp, "gml:name");

		if (names.getLength() > 0) {
			Element id = (Element) names.item(0); // name
			String name = domHelp.getElementValue(id);
			out.println("<b>Name</b> = " + name);
			out.println("<br>");
		}
		// Find all "description" elements
		NodeList descriptions = domHelp.getElements(comp, "gml:description");

		if (descriptions.getLength() > 0) {
			Element id = (Element) descriptions.item(0); // description
			String description = domHelp.getElementValue(id);
			out.println("<b>Description</b> = " + description);
			out.println("<br>");
			out.println("<br>");
		}
		NodeList member = domHelp.getElements(rootElement, "sml:member");
		Element firstMember = (Element) member.item(0);
		Element component = domHelp.getFirstChildElement(firstMember);
		handleCommon(component, firstMember);
		handleComponent(firstMember);
	}
	
	//********** get role ************
	private String getRole(Element element){
		// check for role
		String role = domHelp.getAttributeValue(element, "@xlink:role");
		// check for arcrole
		if (role == null) {
			role = domHelp.getAttributeValue(element,"@xlink:arcrole");
		}
		// return space if still null
		if (role == null) return "&nbsp";
		
		return role;
	}

	// ******************************************************************************************************
	private void handleCommon(Element startElement, Element firstMember) {

		out.flush();

		handleSecurityConstraints(startElement);

		handleLegalConstraint(startElement);

		handleKeywords(startElement);

		handleIdentifiers(startElement);

		handleClassifiers(startElement);

		//handleValidTime(startElement);

		handleCapabilities(startElement);

		handleCharacteristics(startElement);

		handleContacts(startElement);

		handleDocumentation(startElement);

		handleInputs(startElement);

		handleOutputs(startElement);

		handleParameters(startElement);

		handleHistory(startElement);

		handleComponent(startElement); //This one is written already!MOVE!It doesn't belong here!!

		out.flush();
	}	

	// ******************************************************************************************************
	private void handlePhysical(Element startElement, Element firstMember) {

		out.flush();

		// Call all methods to handle cases

		// Handle Spatial Reference Frame
		handleSpatialReferenceFrame(startElement);

		// Handle Temporal Reference Frame
		handleTemporalReferenceFrame(startElement);

		// Handle Location
		handleLocation(startElement);

		// Handle Position
		handlePositions(startElement);

		// Handle Time Position
		handleTimePosition(startElement);

		// Handle Interfaces
		handleInterfaces(startElement);

		out.flush();
	}

	// ******************************************************************************************************
	private void handlePositionList(Element startElement, Element firstMember) {

		// Call all methods to handle cases

		// Handle Position
		// handlePosition(startElement);

		// Handle Time Position
		// handleTimePosition(startElement);

		out.flush();
	}

	// ******************************************************************************************************
	private void handleMethod(Element startElement, Element firstMember) {



		out.flush();
	}

	// ************************************************************************************************************************

	// copy specified number of bytes from main data buffer to temp data buffer
	void copyByte(byte[] fromBytes, byte[] toBytes, int start, int len) {
		for (int i = start; i < (start + len); i++) {
			toBytes[i - start] = fromBytes[i];
		}
	}

	// **********************************************************************************************************************
	// LIST OF ALL METHODS CALLED IN PROGRAM AND METHOD CODE ARE BELOW
	// ***********************************************************************************************************************
	// parseRequest();
	// createDoc();
	// handleIdentifiers();
	// handleCapabilities();
	// handleClassifiers();
	// handleDataAggregates(Element element, String elementName);
	// handleAnyData(Element element, String elementName);
	// handleDataRecord(Element element, String elementName);
	// handleConditionalValue(Element element, String elementName);

	// ***********************************************************************************************************************
	private InputStream parseRequest(HttpServletRequest request) {
		//int BOF = 0, EOF = 0;
		int startPos = 0;
		int endPos = 0;
		String filename = "";
		String contentType = "", fileData = "";//, strLocalFileName = "";

		contentType = request.getContentType();
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
			//strLocalFileName = filename;
			int index = filename.lastIndexOf("\\");
			if (index != -1) {
				filename = filename.substring(index + 1);
			}
			

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
			//System.out.println("Error in uploading ");
			return null;
		}
	}

	// ************************************************************************************************************************
	String getTabs() {
		String tab = " ";
		for (int i = 0; i < tabcount; i++) {
			tab = tab + "&nbsp;&nbsp;&nbsp;&nbsp;";
		}
		return tab;
	}

	// ************************************************************************************************************************
	private PrintWriter createDoc(HttpServletResponse response) throws IOException {
		// Create Document and Initial Header
		response.setContentType("text/html");
		PrintWriter out;
		out = response.getWriter();
		out.flush();
		String docType = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 "
			+ "Transitional//EN\">\n";
		out.println(docType + "<HTML>\n"
				+ "<HEAD><TITLE>SensorML Table View</TITLE></HEAD>\n"
				+ "<BODY BGCOLOR=\"#FDF5E6\">\n"
				+ "<H2 ALIGN=center>SensorML Table View</H2>\n");

		return out;

	}

	// **********************************************************************************************************************
	private void handleKeywords(Element rootElement) {

		// Find all "keywords" elements
		NodeList keywords = domHelp.getElements(rootElement, "sml:keywords");
//		//System.out.println("number of keywords lists = " + keywords.getLength());

		if (keywords.getLength()==0){
			return;
		}
		else{

			for (int i = 0; i < keywords.getLength(); i++) {
				Element id = (Element) keywords.item(i); // keywords

				//String link = domHelp.getAttributeValue(id, "@xlink:href");
				//String role = getRole(id);

				NodeList KeywordList = id.getElementsByTagNameNS(smlNamespace,
				"KeywordList"); // KeywordList
				Element KeywordListEl = (Element) KeywordList.item(i);

				String idatt = domHelp.getAttributeValue(KeywordListEl, "@id");
				String codeSpace = domHelp.getAttributeValue(KeywordListEl,"@codeSpace");

				// Add each keyword
				NodeList children = KeywordListEl.getElementsByTagNameNS(
						smlNamespace, "keyword"); // keyword

				// Start Fieldset and Table
				out.println("<FIELDSET>");
				out.println("<font size=5>");
				out.println("<ALIGN = CENTER>");
				out.println("<LEGEND>Keywords</LEGEND>");
				out.println("<TABLE BORDER=1 ALIGN=\"LEFT\">");
				out.println("<COLGROUP SPAN=1 ALIGN=\"LEFT\">");
				out.println("<TR BGCOLOR=\"#BFBFBF\">\n");
				out.println("<TH>Keyword ");
				if (codeSpace != null) {
					out
					.println("<a href=" + codeSpace + ">CodeSpace</a>"
							+ "&nbsp");
				}
				if (idatt != null) {
					out.println(idatt);
				}
				out.println("<br>");

				for (int j = 0; j < children.getLength(); j++) {
					Element child = (Element) children.item(j);
					String keyword = domHelp.getElementValue(child);
					out.println("<TR><TD>" + keyword);
				}
				out.println("</TABLE>");
				out.println("</FIELDSET>");
				out.println("<BR>");
			}
		}
	}
	// **********************************************************************************************************************
	private void handleSecurityConstraints(Element rootElement) {

		// Find all "securityConstraint" elements
		NodeList constraints = domHelp.getElements(rootElement, "sml:securityConstraint");
//		//System.out.println("number of security constraints = " + constraints.getLength());

		if (constraints.getLength()==0){
			return;
		}
		else{
			for (int i = 0; i < constraints.getLength(); i++) {
				Element id = (Element) constraints.item(i); // constraint

				// Add each security constraint
				NodeList SecurityList = id.getElementsByTagNameNS(smlNamespace,
				"Security"); // SecurityList
				Element SecurityListEl = (Element) SecurityList.item(i);

				// Start Fieldset and Table
				out.println("<FIELDSET>");
				out.println("<font size=5>");
				out.println("<ALIGN = CENTER>");
				out.println("<LEGEND>Security Constraint</LEGEND>");
				out.println("<TABLE BORDER=1 ALIGN=\"LEFT\">");
				out.println("<COLGROUP SPAN=2 ALIGN=\"LEFT\">");
				out.println("<TR BGCOLOR=\"#BFBFBF\">\n");
				out.println("<TH>Name<TH>Value");

				String classification = domHelp.getAttributeValue(SecurityListEl, "@ism:classification");
				if (classification == null) {
					//out.println("<TR><TD>" + "classification" + "<TH>" + "No Security Constraints");
				}
				else{
					out.println("<TR><TD>" + "classification"+ "<TH>" + classification);
				}
				String ownerProducer = domHelp.getAttributeValue(SecurityListEl, "@ism:ownerProducer");
				if (ownerProducer == null) {
					//out.println("<TR><TD>" + "ownerProducer" + "<TH>" + "No Owner Producer");
				}
				else{
					out.println("<TR><TD>" + "ownerProducer"+ "<TH>" + ownerProducer);
				}
				String SCIcontrols = domHelp.getAttributeValue(SecurityListEl, "@ism:SCIcontrols");
				if (SCIcontrols == null) {
					//out.println("<TR><TD>" + "SCIcontrols" + "<TH>" + "No SCI Controls");
				}
				else{
					out.println("<TR><TD>" + "SCIcontrols"+ "<TH>" + SCIcontrols);
				}
				String SARIdentifier = domHelp.getAttributeValue(SecurityListEl, "@ism:SARIdentifier");
				if (SARIdentifier == null) {
					//out.println("<TR><TD>" + "SARIdentifier" + "<TH>" + "No SAR Identifiers");
				}
				else{
					out.println("<TR><TD>" + "SARIdentifier"+ "<TH>" + SARIdentifier);
				}
				String disseminationControls = domHelp.getAttributeValue(SecurityListEl, "@ism:disseminationControls");
				if (disseminationControls == null) {
					//out.println("<TR><TD>" + "disseminationControls" + "<TH>" + "No Dissemination Controls");
				}
				else{
					out.println("<TR><TD>" + "disseminationControls"+ "<TH>" + disseminationControls);
				}
				String FGIsourceOpen = domHelp.getAttributeValue(SecurityListEl, "@ism:FGIsourceOpen");
				if (FGIsourceOpen == null) {
					//out.println("<TR><TD>" + "FGIsourceOpen" + "<TH>" + "No FGI Source");
				}
				else{
					out.println("<TR><TD>" + "FGIsourceOpen"+ "<TH>" + FGIsourceOpen);
				}
				String FGIsourceProtected = domHelp.getAttributeValue(SecurityListEl, "@ism:FGIsourceProtected");
				if (FGIsourceProtected == null) {
					//out.println("<TR><TD>" + "FGIsourceProtected" + "<TH>" + "No Protected FGI Source");
				}
				else{
					out.println("<TR><TD>" + "FGIsourceProtected"+ "<TH>" + FGIsourceProtected);
				}
				String releasableTo = domHelp.getAttributeValue(SecurityListEl, "@ism:releasableTo");
				if (releasableTo == null) {
					//out.println("<TR><TD>" + "releasableTo" + "<TH>" + "No Release Information");
				}
				else{
					out.println("<TR><TD>" + "releasableTo"+ "<TH>" + releasableTo);
				}
				String nonICmarkings = domHelp.getAttributeValue(SecurityListEl, "@ism:nonICmarkings");
				if (nonICmarkings == null) {
					//out.println("<TR><TD>" + "nonICmarkings" + "<TH>" + "No Additional Markings");
				}
				else{
					out.println("<TR><TD>" + "nonICmarkings"+ "<TH>" + nonICmarkings);
				}
				String classifiedBy = domHelp.getAttributeValue(SecurityListEl, "@ism:classifiedBy");
				if (classifiedBy == null) {
					//out.println("<TR><TD>" + "classifiedBy" + "<TH>" + "Unknown");
				}
				else{
					out.println("<TR><TD>" + "classifiedBy"+ "<TH>" + classifiedBy);
				}
				String classificationReason = domHelp.getAttributeValue(SecurityListEl, "@ism:classificationReason");
				if (classificationReason == null) {
					//out.println("<TR><TD>" + "classificationReason" + "<TH>" + "No Classification Reason Provided");
				}
				else{
					out.println("<TR><TD>" + "classificationReason"+ "<TH>" + classificationReason);
				}
				String derivedFrom = domHelp.getAttributeValue(SecurityListEl, "@ism:derivedFrom");
				if (derivedFrom == null) {
					//out.println("<TR><TD>" + "derivedFrom" + "<TH>" + "Unknown Derivation");
				}
				else{
					out.println("<TR><TD>" + "derivedFrom"+ "<TH>" + derivedFrom);
				}
				String declassDate = domHelp.getAttributeValue(SecurityListEl, "@ism:declassDate");
				if (declassDate == null) {
					//out.println("<TR><TD>" + "declassDate" + "<TH>" + "No Declassification Date Provided");
				}
				else{
					out.println("<TR><TD>" + "declassDate"+ "<TH>" + declassDate);
				}
				String declassEvent = domHelp.getAttributeValue(SecurityListEl, "@ism:declassEvent");
				if (declassEvent == null) {
					//out.println("<TR><TD>" + "declassEvent" + "<TH>" + "No Declassification Event Provided");
				}
				else{
					out.println("<TR><TD>" + "declassEvent"+ "<TH>" + declassEvent);
				}
				String declassException = domHelp.getAttributeValue(SecurityListEl, "@ism:declassException");
				if (declassException == null) {
					//out.println("<TR><TD>" + "declassException" + "<TH>" + "No Declassification Exceptions");
				}
				else{
					out.println("<TR><TD>" + "declassException"+ "<TH>" + declassException);
				}
				String typeOfExemptedSource = domHelp.getAttributeValue(SecurityListEl, "@ism:typeOfExemptedSource");
				if (typeOfExemptedSource == null) {
					//out.println("<TR><TD>" + "typeOfExemptedSource" + "<TH>" + "&nbsp");
				}
				else{
					out.println("<TR><TD>" + "typeOfExemptedSource"+ "<TH>" + typeOfExemptedSource);
				}
				String dateOfExemptedSource = domHelp.getAttributeValue(SecurityListEl, "@ism:dateOfExemptedSource");
				if (dateOfExemptedSource == null) {
					//out.println("<TR><TD>" + "dateOfExemptedSource" + "<TH>" + "&nbsp");
				}
				else{
					out.println("<TR><TD>" + "dateOfExemptedSource"+ "<TH>" + dateOfExemptedSource);
				}
				String declassManualReview = domHelp.getAttributeValue(SecurityListEl, "@ism:declassManualReview");
				if (declassManualReview == null) {
					//out.println("<TR><TD>" + "declassManualReview" + "<TH>" + "&nbsp");
				}
				else{
					out.println("<TR><TD>" + "declassManualReview"+ "<TH>" + declassManualReview);
				}


				out.println("</TABLE>");
				out.println("</FIELDSET>");
				out.println("<BR>");
			}
		}
	}
	// **********************************************************************************************************************
	private void handleIdentifiers(Element rootElement) {

		// Find all "identifiers" elements
		NodeList identifiers = domHelp.getElements(rootElement,
		"sml:identification");

		if (identifiers.getLength()==0){
			return;
		}
		else{

			for (int i = 0; i < identifiers.getLength(); i++) {
				Element id = (Element) identifiers.item(i); // identification

				//String link = domHelp.getAttributeValue(id, "@xlink:href");
				String role = getRole(id);
				
				NodeList identifierList = id.getElementsByTagNameNS(smlNamespace,
				"IdentifierList"); // IdentifierList
				Element identifierListEl = (Element) identifierList.item(0);

				// Add each identifier
				NodeList children = identifierListEl.getElementsByTagNameNS(
						smlNamespace, "identifier"); // identifier

				// Start Fieldset and Table
				out.println("<FIELDSET>");
				out.println("<font size=5>");
				out.println("<ALIGN = CENTER>");
				out.println("<LEGEND>Identifiers</LEGEND>"); // Name Attribute?

				out.println("<TABLE BORDER=1 ALIGN=\"LEFT\">");
				out.println("<COLGROUP SPAN=4 ALIGN=\"LEFT\">");
				out.println("<TR BGCOLOR=\"#BFBFBF\">\n");
				out.println("<TH>Name<TH>Role<TH>Value<TH>Definition");
				out.println("<br>");

				for (int j = 0; j < children.getLength(); j++) {
					Element child = (Element) children.item(j);

					String name = domHelp.getAttributeValue(child, "name");

					// String value = child.getNodeValue();
					String value = domHelp.getElementValue(child,
					"sml:Term/sml:value");

					String definition = domHelp.getAttributeValue(child,
					"sml:Term/@definition");

					// Print table entry"
					out.println("<TR><TD>" + name);
					out.println("<TD>" + role);
					if (value == null) {
						out.println("<TD>" + "&nbsp");
					} else {
						out.println("<TD>" + value);
					}
					if (definition == null) {
						out.println("<TD>" + "&nbsp");
					}
					else{
						handleURI(definition);
					}

				}
				out.println("</TABLE>");
				out.println("</FIELDSET>");
				out.println("<BR>");
			}
		}
	}

	// *******************************************************************************************************************************
	private void handleClassifiers(Element rootElement) {

		// Find all "classifiers" elements
		NodeList classifiers = domHelp.getElements(rootElement,
		"classification");

		if (classifiers.getLength()==0){
			return;
		}
		else{
			for (int i = 0; i < classifiers.getLength(); i++) {
				Element id = (Element) classifiers.item(i); // classification

				//String link = domHelp.getAttributeValue(id, "@xlink:href");
				String role = getRole(id);

				NodeList classifierList = id.getElementsByTagNameNS(smlNamespace,
				"ClassifierList"); // ClassifierList
				Element classifierListEl = (Element) classifierList.item(0);

				// Start Fieldset and Table
				out.println("<FIELDSET>");
				out.println("<font size=5>");
				out.println("<ALIGN = CENTER>");
				out.println("<LEGEND>Classifiers</LEGEND>"); // Name Attribute?
				out.println("<TABLE BORDER=1 ALIGN=\"LEFT\">");
				out.println("<COLGROUP SPAN=4 ALIGN=\"LEFT\">");
				out.println("<TR BGCOLOR=\"#BFBFBF\">\n");
				out.println("<TH>Name<TH>Role<TH>Value<TH>Definition");
				out.println("<br>");

				// Add each classifier
				NodeList children = classifierListEl.getElementsByTagNameNS(
						smlNamespace, "classifier"); // classifier

				for (int j = 0; j < children.getLength(); j++) {
					Element child = (Element) children.item(j);
					String name = domHelp.getAttributeValue(child, "name");
					String value = domHelp.getElementValue(child,
					"sml:Term/sml:value");

					String definition = domHelp.getAttributeValue(child,
					"sml:Term/@definition");

					// Print table entry"
					out.println("<TR><TD>" + name);
					out.println("<TD>" + role);

					if (value == null) {
						out.println("<TD>" + "&nbsp");
					} else {
						out.println("<TD>" + value);
					}
					if (definition == null) {
						out.println("<TD>" + "&nbsp");
					}
					else{
						handleURI(definition);
					}

				}
				out.println("</TABLE>");
				out.println("</FIELDSET>");
				out.println("<BR>");
			}
		}
	}

	// **********************************************************************************************************************
	private void handleCapabilities(Element rootElement) {

		// Find all "capabilities" elements
		NodeList capabilities = domHelp.getElements(rootElement,
		"sml:capabilities");

		if (capabilities.getLength()==0){
			return;
		}
		else{
			for (int i = 0; i < capabilities.getLength(); i++) {
				
				Element caps = (Element) capabilities.item(i);

				String capabilityName = domHelp.getAttributeValue(caps, "@name");

				String link = domHelp.getAttributeValue(caps, "@xlink:href");

				String role = getRole(caps);

				// Start Fieldset and Table
				out.println("<FIELDSET>");
				out.println("<font size=5>");
				out.println("<ALIGN = CENTER>");
				out.println("<LEGEND>Capabilities");
				if (capabilityName == null) {
					out.println("&nbsp" + "</LEGEND>");
				} else {
					out.println(": " + capabilityName + "</LEGEND>"); // Name
					// Attribute?
				}
				// out.println("Print this out");
				out.println("<TABLE BORDER=1 ALIGN=\"LEFT\">");
				out.println("<COLGROUP SPAN=9 ALIGN=\"LEFT\">");
				out.println("<TR BGCOLOR=\"#BFBFBF\">\n");
				out.println("<TH>Name<TH>Role<TH>Type<TH>Value<TH>UOM<TH>Definition<TH>Constraints<TH>Quality<TH>Description");

				if (link != null) {
					handleLink(caps, capabilityName,link, role);
				} else {
					NodeList element = domHelp.getAllChildElements(caps);

					for (int j = 0; j < element.getLength(); j++) {
						handleAnyData((Element) element.item(j), capabilityName,
								role, rootElement);
					}
				}
				out.println("</TABLE>");
				out.println("<BR>");
				out.println("</FIELDSET>");
				out.println("<BR>");
			}
		}
	}

	// **********************************************************************************************************************
	private void handleCharacteristics(Element rootElement) {

		// Find all "characteristics" elements
		NodeList characteristics = domHelp.getElements(rootElement,
		"sml:characteristics");

		if (characteristics.getLength()==0){
			return;
		}
		else{
			for (int i = 0; i < characteristics.getLength(); i++) {
				
				Element chars = (Element) characteristics.item(i);

				String characteristicsName = domHelp.getAttributeValue(chars, "@name");
				String link = domHelp.getAttributeValue(chars, "@xlink:href");
				String role = getRole(chars);

				// Start Fieldset and Table
				out.println("<FIELDSET>");
				out.println("<font size=5>");
				out.println("<ALIGN = CENTER>");
				out.println("<LEGEND>Characteristics"); // Name
				if (characteristicsName == null) {
					out.println("&nbsp" + "</LEGEND>");
				} else {
					out.println(": " + characteristicsName + "</LEGEND>"); // Name
					// Attribute?
				}
				out.println("<TABLE BORDER=1 ALIGN=\"LEFT\">");
				out.println("<COLGROUP SPAN=9 ALIGN=\"LEFT\">");
				out.println("<TR BGCOLOR=\"#BFBFBF\">\n");
				out.println("<TH>Name<TH>Role<TH>Type<TH>Value<TH>UOM<TH>Definition<TH>Constraints<TH>Quality<TH>Description");

				if (link != null) {
					handleLink(chars, characteristicsName,
							link, role);
				} else {
					NodeList element = domHelp.getAllChildElements(chars);

					for (int j = 0; j < element.getLength(); j++) {
						handleAnyData((Element) element.item(j), characteristicsName,
								role, rootElement);
					}
				}
				out.println("</TABLE>");
				out.println("<BR>");
				out.println("</FIELDSET>");
				out.println("<BR>");
			}
		}
	}

	// **********************************************************************************************************************
	private void handleInputs(Element rootElement) {

		// Find all "inputs" elements

		NodeList inputs = domHelp.getElements(rootElement, "sml:inputs");

		if (inputs.getLength()==0){
			return;
		}
		else{
			for (int i = 0; i < inputs.getLength(); i++) {
				Element id = (Element) inputs.item(i); // inputs
				NodeList InputList = id.getElementsByTagNameNS(smlNamespace,
				"InputList"); // inputList
				Element InputListEl = (Element) InputList.item(0);

				// Add each input
				NodeList children = InputListEl.getElementsByTagNameNS(
						smlNamespace, "input"); // input

				// Start Fieldset and Table
				out.println("<FIELDSET>");
				out.println("<font size=5>");
				out.println("<ALIGN = CENTER>");
				out.println("<LEGEND>Inputs</LEGEND>"); // Name Attribute?
				out.println("<TABLE BORDER=1 ALIGN=\"LEFT\">");
				out.println("<COLGROUP SPAN=9 ALIGN=\"LEFT\">");
				out.println("<TR BGCOLOR=\"#BFBFBF\">\n");
				out
				.println("<TH>Name<TH>Role<TH>Type<TH>Value<TH>UOM<TH>Definition<TH>Constraints<TH>Quality<TH>Description");

				for (int l = 0; l < children.getLength(); l++) {
					Element child = (Element) children.item(l);

					//String link = domHelp.getAttributeValue(child, "@xlink:href");
					String role = getRole(child);

					String name = domHelp.getAttributeValue(child, "@name");

					NodeList element = domHelp.getAllChildElements(child);
					for (int k = 0; k < element.getLength(); k++) {
						handleAnyData((Element) element.item(k), name, role,
								rootElement);
					}
				}
				out.println("</TABLE>");
				out.println("<BR>");
				out.println("</FIELDSET>");
			}
			out.println("<BR>");
		}
	}

	// **********************************************************************************************************************
	private void handleOutputs(Element rootElement) {

		// Find all "outputs" elements
		NodeList outputs = domHelp.getElements(rootElement, "sml:outputs");

		if (outputs.getLength()==0){
			return;
		}
		else{
			for (int i = 0; i < outputs.getLength(); i++) {
				Element id = (Element) outputs.item(i); // outputs
				NodeList OutputList = id.getElementsByTagNameNS(smlNamespace,
				"OutputList"); // OutputList
				Element OutputListEl = (Element) OutputList.item(0);

				// Add each input
				NodeList children = OutputListEl.getElementsByTagNameNS(
						smlNamespace, "output"); // output

				// Start Fieldset and Table
				out.println("<FIELDSET>");
				out.println("<font size=5>");
				out.println("<ALIGN = CENTER>");
				out.println("<LEGEND>Outputs</LEGEND>"); // Name Attribute?
				out.println("<TABLE BORDER=1 ALIGN=\"LEFT\">");
				out.println("<COLGROUP SPAN=9 ALIGN=\"LEFT\">");
				out.println("<TR BGCOLOR=\"#BFBFBF\">\n");
				out
				.println("<TH>Name<TH>Role<TH>Type<TH>Value<TH>UOM<TH>Definition<TH>Constraints<TH>Quality<TH>Description");

				for (int l = 0; l < children.getLength(); l++) {
					Element child = (Element) children.item(l);

					//String link = domHelp.getAttributeValue(child, "@xlink:href");
					String role = getRole(child);

					String name = domHelp.getAttributeValue(child, "@name");

					// String outputsName =
					// domHelp.getAttributeValue((Element)children.item(j),
					// "@name");
					NodeList element = domHelp.getAllChildElements(child);
					for (int k = 0; k < element.getLength(); k++) {
						handleAnyData((Element) element.item(k), name, role,
								rootElement);
					}
				}
				out.println("</TABLE>");
				out.println("<BR>");
				out.println("</FIELDSET>");
			}
			out.println("<BR>");
		}
	}

	// **********************************************************************************************************************
	private void handleParameters(Element rootElement) {

		// Find all "parameters" elements
		NodeList parameters = domHelp
		.getElements(rootElement, "sml:parameters");

		if (parameters.getLength()==0){
			return;
		}
		else{
			for (int i = 0; i < parameters.getLength(); i++) {
				Element id = (Element) parameters.item(i); // parameters
				NodeList ParameterList = id.getElementsByTagNameNS(smlNamespace,
				"ParameterList"); // ParameterList
				Element ParameterListEl = (Element) ParameterList.item(0);

				// Add each parameter
				NodeList children = ParameterListEl.getElementsByTagNameNS(
						smlNamespace, "parameter"); // parameters

				// Start Fieldset and Table
				out.println("<FIELDSET>");
				out.println("<font size=5>");
				out.println("<ALIGN = CENTER>");
				out.println("<LEGEND>Parameters</LEGEND>"); // Name Attribute?
				out.println("<TABLE BORDER=1 ALIGN=\"LEFT\">");
				out.println("<COLGROUP SPAN=9 ALIGN=\"LEFT\">");
				out.println("<TR BGCOLOR=\"#BFBFBF\">\n");
				out.println("<TH>Name<TH>Role<TH>Type<TH>Value<TH>UOM<TH>Definition<TH>Constraints<TH>Quality<TH>Description");

				for (int l = 0; l < children.getLength(); l++) {
					Element child = (Element) children.item(l);

				//	String link = domHelp.getAttributeValue(child, "@xlink:href");
					String role = getRole(child);

					String name = domHelp.getAttributeValue(child, "@name");

					//String value = domHelp.getElementValue(child,"sml:Term/sml:value");
				//	String definition = domHelp.getAttributeValue(child,
				//		"sml:Term/@definition");

					NodeList element = domHelp.getAllChildElements(child);

					for (int k = 0; k < element.getLength(); k++) {
						handleAnyData((Element) element.item(k), name, role,
								rootElement);
					}

				}
				out.println("</TABLE>");
				out.println("<BR>");
				out.println("</FIELDSET>");
			}
			out.println("<BR>");
		}
	}

	// ******************************************************************************************************************************
	private void handleDataAggregates(Element element, String elementName,
			String role, Element rootElement) {

		String localName = element.getLocalName();

		// if DataRecord then call handleDataRecord(node)
		if (localName.equalsIgnoreCase("DataRecord")) {
			handleDataRecord(element, elementName, role, rootElement);
		}

		// if DataArray then call handleDataArray(node)
		if (localName.equalsIgnoreCase("DataArray")) {
			handleDataArray(element, elementName, role, rootElement);
		}

		// if ConditionalValue then call handleConditionalValue(node)
		else if (localName.equalsIgnoreCase("ConditionalValue")) {
			handleConditionalValue(element, elementName, role, rootElement);
		}

		// if ConditionalData then call handleConditionalData(node)
		else if (localName.equalsIgnoreCase("ConditionalData")) {
			handleConditionalData(element, elementName, role, rootElement);
		}

		// if NormalizedCurve then call handleNormalizedCurve(node)
		else if (localName.equalsIgnoreCase("NormalizedCurve")) {
			handleNormalizedCurve(element, elementName, role, rootElement);
		}

		// if Position then call handlePosition(node)
		else if (localName.equalsIgnoreCase("Position")) {
			handlePosition(element, elementName, role, rootElement);
		}

		// if SimpleDateRecord then call handleSimpleDataRecord(node)
		else if (localName.equalsIgnoreCase("SimpleDataRecord")) {
			handleSimpleDataRecord(element, elementName, role, rootElement);
		}

		// if Vector then call handleVector(node)
		else if (localName.equalsIgnoreCase("Vector")) {
			handleVector(element, elementName, role, rootElement);
		}

		// if Envelope then call handleEnvelope
		else if (localName.equalsIgnoreCase("Envelope")) {
			handleEnvelope(element, elementName, role, rootElement);
		}

		// if GeoLocationArea then call handleGeoLocationArea
		else if (localName.equalsIgnoreCase("GeoLocationArea")) {
			handleGeoLocationArea(element, elementName, role, rootElement);
		}

		else if (localName == null) {
//			//System.out.println("There are no children.");
			return;
		}

		// else print out that the data aggregate is unknown
		else {
			System.err.println("Unknown Data Aggregate = " + localName);
		}
	}

	// ******************************************************************************************************************************
	private void handleAnyData(Element element, String elementName,
			String role, Element rootElement) {

		String localName = element.getLocalName();

		// if Quantity then call Quantity(element)
		if (localName.equalsIgnoreCase("Quantity")) {
			handleQuantity(element, elementName, role);
		}

		// if Count then call handleCount(element)
		else if (localName.equalsIgnoreCase("Count")) {
			handleCount(element, elementName, role, rootElement);
		}

		// if Time then call handleTime(element)
		else if (localName.equalsIgnoreCase("Time")) {
			handleTime(element, elementName, role);
		}

		// if Boolean then call handleBoolean(element)
		else if (localName.equalsIgnoreCase("Boolean")) {
			handleBoolean(element, elementName, role);
		}

		// if Category then call handleCategory(element)
		else if (localName.equalsIgnoreCase("Category")) {
			handleCategory(element, elementName, role);
		}

		// if Text then call handleText(element)
		else if (localName.equalsIgnoreCase("Text")) {
			handleText(element, elementName, role);
		}

		// if QuantityRange then call handleQuantityRange(element)
		else if (localName.equalsIgnoreCase("QuantityRange")) {
			handleQuantityRange(element, elementName, role);
		}

		// if CountRange then call handleCountRange(element)
		else if (localName.equalsIgnoreCase("CountRange")) {
			handleCountRange(element, elementName, role);
		}

		// if TimeRange then call handleTimeRange(element)
		else if (localName.equalsIgnoreCase("TimeRange")) {
			handleTimeRange(element, elementName, role);
		}

		// if ObservableProperty then call handleObservableProperty(element)
		else if (localName.equalsIgnoreCase("ObservableProperty")) {
			handleObservableProperty(element, elementName, role);
		}

		else {
			handleDataAggregates(element, elementName, role, rootElement);
		}

	}

	// *******************************************************************************************************************************
	private void handleDataRecord(Element element, String elementName,
			String role, Element rootElement) {
		// --------------------------------------------------------------------------------------
		// Find all "description" elements
		NodeList descriptions = domHelp.getElements(element, "gml:description");

		// ----------------------------------------------------------------------------------------

		String childName;

		String definition = domHelp.getAttributeValue(element, "@definition");

		out.println("<TR>");
		out.println("<TD>" + getTabs() + elementName);
		if (role == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + role);
		}
		out.println("<TD>" + element.getLocalName());
		out.println("<TD>" + "----"); // Value
		out.println("<TD>" + "----"); // UOM
		if (definition == null) {
			out.println("<TD>" + "&nbsp");
		}
		else{
			handleURI(definition);
		}
		out.println("<TD>" + "----"); // Constraints
		out.println("<TD>" + "----"); // Quality
		if (descriptions.getLength() > 0) {
			Element id = (Element) descriptions.item(0); // description
			//String link = domHelp.getAttributeValue(id, "@xlink:href");
			String description = domHelp.getElementValue(id);
			out.println("<TD>" + description);
		} else if (descriptions.getLength() == 0) {
			out.println("<TD>" + "&nbsp");
		}
		out.println("</TR>");

		// Get children list of type "field"
		NodeList fieldList = domHelp.getElements(element, "swe:field");

		tabcount++;
		// For each child get its children should only have one
		for (int k = 0; k < fieldList.getLength(); k++) {

			Element field = (Element) fieldList.item(k);
			
			String link = domHelp.getAttributeValue(field, "@xlink:href");
			String childRole = getRole(field);

			if (link != null) {
				handleLink(element, elementName, link, childRole);
			} else {
				NodeList childList = domHelp.getAllChildElements(field);
				childName = domHelp.getAttributeValue(field, "@name");

				for (int m = 0; m < childList.getLength(); m++) {
					tabcount++;
					handleAnyData((Element) childList.item(m), childName, childRole,
							rootElement);
					tabcount--;
				}
			}
		}
		tabcount--;
	}

	// *******************************************************************************************************************************
	private void handleDataArray(Element element, String elementName,
			String role, Element rootElement) {
		// --------------------------------------------------------------------------------------
		// Find all "description" elements
		NodeList descriptions = domHelp.getElements(element, "gml:description");

		// ----------------------------------------------------------------------------------------
		String childName;

		String definition = domHelp.getAttributeValue(element, "@definition");
		String uom = domHelp.getAttributeValue(element, "swe:uom/@code");
		if (uom == null) {
			uom = domHelp.getAttributeValue(element, "swe:uom/@xlink:href");
		}

		out.println("<TR>");
		out.println("<TD>" + getTabs() + elementName);
		out.println("<TD>" + role);

		out.println("<TD>" + element.getLocalName());
		out.println("<TD>" + "----"); // Value
		if (uom == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + uom);
		}
		if (definition == null) {
			out.println("<TD>" + "&nbsp");
		}
		else{
			handleURI(definition);
		}
		out.println("<TD>" + "----"); // Constraints
		out.println("<TD>" + "----"); // Quality
		if (descriptions.getLength() > 0) {
			Element id = (Element) descriptions.item(0); // description
			//String link = domHelp.getAttributeValue(id, "@xlink:href");
			String description = domHelp.getElementValue(id);
			out.println("<TD>" + description);
		} else if (descriptions.getLength() == 0) {
			out.println("<TD>" + "&nbsp");
		}
		out.println("</TR>");

		// Get children list of type "field"
		NodeList fieldList = domHelp.getElements(element, "swe:field");

		tabcount++;
		// For each child get its children should only have one
		for (int k = 0; k < fieldList.getLength(); k++) {

			NodeList childList = domHelp.getAllChildElements((Element) fieldList.item(k));
			childName = domHelp.getAttributeValue((Element) fieldList.item(k),
					"@name");

			for (int m = 0; m < childList.getLength(); m++) {
				handleAnyData((Element) childList.item(m), childName, role,
						rootElement);
			}
		}
		tabcount--;
	}

	// *****************************************************************************************************************************
	private void handleConditionalValue(Element element, String elementName,
			String role, Element rootElement) {
		// --------------------------------------------------------------------------------------
		// Find all "description" elements
		NodeList descriptions = domHelp.getElements(element, "gml:description");
		// NodeList descriptions = dom.getElementsByTagNameNS(gmlNamespace,
		// "description");

		// ----------------------------------------------------------------------------------------

		String childName;
		String value = domHelp.getElementValue(element, "swe:value");
		String definition = domHelp.getAttributeValue(element, "@definition");
		//String quality = domHelp.getAttributeValue(element, "swe:quality");
		String uom = domHelp.getAttributeValue(element, "swe:uom/@code");
		if (uom == null) {
			uom = domHelp.getAttributeValue(element, "swe:uom/@xlink:href");
		}

		out.println("<TR>");
		out.println("<TD>" + getTabs() + elementName);
		out.println("<TD>" + role);

		out.println("<TD>" + element.getLocalName());
		if (value == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + value);
		}
		if (uom == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + uom);
		}
		if (definition == null) {
			out.println("<TD>" + "&nbsp");
		}
		else{
			handleURI(definition);
		}
		out.println("<TD>" + "----"); // Constraints Not Applicable
		out.println("<TD>" + "----"); // Quality Not Applicable
		if (descriptions.getLength() > 0) {
			Element id = (Element) descriptions.item(0); // description
			//String link = domHelp.getAttributeValue(id, "@xlink:href");
			String description = domHelp.getElementValue(id);
			out.println("<TD>" + description);
		} else if (descriptions.getLength() == 0) {
			out.println("<TD>" + "&nbsp");
		}
		out.println("</TR>");

		// Get children list of type "field"
		NodeList fieldList = domHelp.getAllElements(element, "field");

		tabcount++;
		// For each child get its children (should only have one
		for (int i = 0; i < fieldList.getLength(); i++) {
			NodeList childList = domHelp.getAllChildElements((Element) fieldList.item(i));
			childName = domHelp.getAttributeValue((Element) fieldList.item(i),
					"@name");
			handleAnyData((Element) childList.item(0), childName, role,
					rootElement);
		}
		tabcount--;
	}

	// ******************************************************************************************************************************
	private void handleConditionalData(Element element, String elementName,
			String role, Element rootElement) {

		String childName;

		out.println("<TR>");
		out.println("<TD>" + getTabs() + elementName);
		out.println("<TD>" + role);

		out.println("<TD>" + element.getLocalName());
		out.println("<TD>" + "&nbsp;");
		out.println("<TD>" + "&nbsp;");
		out.println("</TR>");

		// Get children list of type "field"
		NodeList fieldList = domHelp.getAllElements(element, "field");

		// For each child get its children (should only have one
		for (int i = 0; i < fieldList.getLength(); i++) {
			NodeList childList = domHelp.getAllChildElements((Element) fieldList.item(i));
			childName = domHelp.getAttributeValue((Element) fieldList.item(i),
					"@name");
			handleAnyData((Element) childList.item(0), childName, role,
					rootElement);
		}
	}

	// ******************************************************************************************************************************
	private void handleNormalizedCurve(Element element, String elementName,
			String role, Element rootElement) {

		String childName;

		out.println("<TR>");
		out.println("<TD>" + getTabs() + elementName);
		out.println("<TD>" + role);

		out.println("<TD>" + element.getLocalName());
		out.println("<TD>" + "&nbsp;");
		out.println("<TD>" + "&nbsp;");
		out.println("</TR>");

		// Get children list of type "field"
		NodeList fieldList = domHelp.getAllElements(element, "field");

		// For each child get its children (should only have one
		for (int i = 0; i < fieldList.getLength(); i++) {
			NodeList childList = domHelp.getAllChildElements((Element) fieldList.item(i));
			childName = domHelp.getAttributeValue((Element) fieldList.item(i),"@name");
			handleAnyData((Element) childList.item(0), childName, role, rootElement);
		}
	}

	// ******************************************************************************************************************************
	private void handleEnvelope(Element element, String elementName,
			String role, Element rootElement) {

		String childName;

		out.println("<TR>");
		out.println("<TD>" + getTabs() + elementName);
		out.println("<TD>" + role);

		out.println("<TD>" + element.getLocalName());
		out.println("<TD>" + "&nbsp;");
		out.println("<TD>" + "&nbsp;");
		out.println("</TR>");

		// Get children list of type "field"
		NodeList fieldList = domHelp.getAllElements(element, "field");

		// For each child get its children (should only have one
		for (int i = 0; i < fieldList.getLength(); i++) {
			NodeList childList = domHelp.getAllChildElements((Element) fieldList.item(i));
			childName = domHelp.getAttributeValue((Element) fieldList.item(i),"@name");
			handleAnyData((Element) childList.item(0), childName, role,	rootElement);
		}
	}

	// ******************************************************************************************************************************
	private void handleGeoLocationArea(Element element, String elementName,
			String role, Element rootElement) {

		String childName;

		out.println("<TR>");
		out.println("<TD>" + getTabs() + elementName);
		out.println("<TD>" + role);

		out.println("<TD>" + element.getLocalName());
		out.println("<TD>" + "&nbsp;");
		out.println("<TD>" + "&nbsp;");
		out.println("</TR>");

		// Get children list of type "field"
		NodeList fieldList = domHelp.getAllElements(element, "field");

		// For each child get its children (should only have one
		for (int i = 0; i < fieldList.getLength(); i++) {
			NodeList childList = domHelp.getAllChildElements((Element) fieldList.item(i));
			childName = domHelp.getAttributeValue((Element) fieldList.item(i),
					"@name");
			handleAnyData((Element) childList.item(0), childName, role, rootElement);
		}
	}

	// ********************************************************************************
	private void handlePosition(Element element, String elementName,
			String role, Element rootElement) {
		// --------------------------------------------------------------------------------------
		// Find all "description" elements
		NodeList descriptions = domHelp.getElements(element, "gml:description");
		// NodeList descriptions = dom.getElementsByTagNameNS(gmlNamespace,
		// "description");

		// ----------------------------------------------------------------------------------------
		out.println("element = " + element);
		out.println("elementName =" + elementName);
		out.println("rootElement =" + rootElement);
		String childName;
		String definition = domHelp.getAttributeValue(element, "@definition");

		out.println("<TR>");
		out.println("<TD>" + getTabs() + elementName);
		out.println("<TD>" + role);

		out.println("<TD>" + element.getLocalName());
		out.println("<TD>" + "----"); // Value Not Applicable
		out.println("<TD>" + "----"); // Units Not Applicable
		if (definition == null) {
			out.println("<TD>" + "&nbsp");
		}
		else{
			handleURI(definition);
		}
		out.println("<TD>" + "----"); // Constraints Not Applicable
		out.println("<TD>" + "----"); // Quality Not Applicable
		if (descriptions.getLength() > 0) {
			Element id = (Element) descriptions.item(0); // description
			//String link = domHelp.getAttributeValue(id, "@xlink:href");
			String description = domHelp.getElementValue(id);
			out.println("<TD>" + description);
		} else if (descriptions.getLength() == 0) {
			out.println("<TD>" + "&nbsp");
		}
		out.println("</TR>");

		// Get children list of type "field"
		NodeList fieldList = domHelp.getAllElements(element, "field");

		tabcount++;
		// For each child get its children (should only have one
		for (int i = 0; i < fieldList.getLength(); i++) {
			NodeList childList = domHelp.getAllChildElements((Element) fieldList.item(i));
			childName = domHelp.getAttributeValue((Element) fieldList.item(i),"@name");
			handleAnyData((Element) childList.item(0), childName, role,	rootElement);
		}
		tabcount--;
	}

	// ********************************************************************************
	private void handleSimpleDataRecord(Element element, String elementName,
			String role, Element rootElement) {
		// --------------------------------------------------------------------------------------
		// Find all "description" elements
		NodeList descriptions = domHelp.getElements(element, "gml:description");
		// NodeList descriptions = dom.getElementsByTagNameNS(gmlNamespace,
		// "description");

		// ----------------------------------------------------------------------------------------
		String childName;
		String definition = domHelp.getAttributeValue(element, "@definition");

		out.println("<TR>");
		out.println("<TD>" + getTabs() + elementName);
		out.println("<TD>" + role);

		out.println("<TD>" + element.getLocalName());
		out.println("<TD>" + "----"); // Value Not Applicable
		out.println("<TD>" + "----"); // Units Not Applicable
		if (definition == null) {
			out.println("<TD>" + "&nbsp");
		}
		else{
			handleURI(definition);
		}
		out.println("<TD>" + "----"); // Constraints Not Applicable
		out.println("<TD>" + "----"); // Quality Not Applicable
		if (descriptions.getLength() > 0) {
			Element id = (Element) descriptions.item(0); // description
			//String link = domHelp.getAttributeValue(id, "@xlink:href");
			String description = domHelp.getElementValue(id);
			out.println("<TD>" + description);
		} else if (descriptions.getLength() == 0) {
			out.println("<TD>" + "&nbsp");
		}
		out.println("</TR>");

		// Get children list of type "field"
		NodeList fieldList = domHelp.getAllElements(element, "field");

		tabcount++;
		// For each child get its children (should only have one
		for (int i = 0; i < fieldList.getLength(); i++) {
			NodeList childList = domHelp.getAllChildElements((Element) fieldList.item(i));
			childName = domHelp.getAttributeValue((Element) fieldList.item(i),"@name");
			handleAnyData((Element) childList.item(0), childName, role, rootElement);
		}
		tabcount--;
	}

	// ********************************************************************************
	private void handleVector(Element element, String elementName, String role,
			Element rootElement) {
		// --------------------------------------------------------------------------------------
		// Find all "description" elements
		NodeList descriptions = domHelp.getElements(element, "gml:description");
		// NodeList descriptions = dom.getElementsByTagNameNS(gmlNamespace,
		// "description");

		// ----------------------------------------------------------------------------------------
		String childName;
		String definition = domHelp.getAttributeValue(element, "@definition");

		out.println("<TR>");
		out.println("<TD>" + getTabs() + elementName);
		out.println("<TD>" + role);

		out.println("<TD>" + element.getLocalName());
		out.println("<TD>" + "----"); // Value Not Applicable
		out.println("<TD>" + "----"); // Units Not Applicable
		if (definition == null) {
			out.println("<TD>" + "&nbsp");
		}
		else{
			handleURI(definition);
		}
		out.println("<TD>" + "----"); // Constraints Not Applicable
		out.println("<TD>" + "----"); // Quality Not Applicable
		if (descriptions.getLength() > 0) {
			Element id = (Element) descriptions.item(0); // description
			//String link = domHelp.getAttributeValue(id, "@xlink:href");
			String description = domHelp.getElementValue(id);
			out.println("<TD>" + description);
		} else if (descriptions.getLength() == 0) {
			out.println("<TD>" + "&nbsp");
		}
		out.println("</TR>");

		// Get children list of type "field"
		NodeList fieldList = domHelp.getAllElements(element, "field");

		tabcount++;
		// For each child get its children (should only have one
		for (int i = 0; i < fieldList.getLength(); i++) {
			NodeList childList = domHelp.getAllChildElements((Element) fieldList.item(i));
			childName = domHelp.getAttributeValue((Element) fieldList.item(i),"@name");
			handleAnyData((Element) childList.item(0), childName, role, rootElement);
		}
		tabcount--;
	}

	// ************************************************************************************
	/*private void Count(Element element, String elementName, String role,
			Element rootElement) {

		String childName;

		out.println("<TR>");
		out.println("<TD>" + elementName);
		out.println("<TD>" + role);
		out.println("<TD>" + element.getLocalName());
		out.println("<TD>" + "&nbsp;");
		out.println("<TD>" + "&nbsp;");
		out.println("</TR>");

		// Get children list of type "field"
		NodeList fieldList = domHelp.getAllElements(element, "field");

		// For each child get its children (should only have one
		for (int i = 0; i < fieldList.getLength(); i++) {
			NodeList childList = domHelp.getAllChildElements((Element) fieldList.item(i));
			childName = domHelp.getAttributeValue((Element) fieldList.item(i), "@name");
			handleAnyData((Element) childList.item(0), childName, role, rootElement);
		}
	}
*/
	// ***************************************************************************************
	private void handleCount(Element element, String elementName, String role,
			Element rootElement) {
		// --------------------------------------------------------------------------------------
		// Find all "description" elements
		NodeList descriptions = domHelp.getElements(element, "gml:description");

		// ----------------------------------------------------------------------------------------
		String value = domHelp.getElementValue(element, "swe:value");
		String definition = domHelp.getAttributeValue(element, "@definition");
		String quality = domHelp.getAttributeValue(element, "swe:quality");
		out.println("<TR>");
		out.println("<TD>" + getTabs() + elementName);
		out.println("<TD>" + role);

		out.println("<TD>" + element.getLocalName()); // Type
		if (value == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + value);
		}
		out.println("<TD>" + "----"); // UOM Not Applicable
		if (definition == null) {
			out.println("<TD>" + "&nbsp");
		}
		else{
			handleURI(definition);
		}

		Element constraint = domHelp.getElement(element,
		"swe:constraint/swe:AllowedValues");
		if (constraint != null) {
			NodeList children = domHelp.getAllChildElements(constraint);

			for (int i = 0; i < children.getLength(); i++) {
				Element child = (Element) children.item(i);
				String localName = child.getLocalName();
				if (localName.equalsIgnoreCase("min")) {
					out.println("<TD>" + ">" + domHelp.getElementValue(child));

				}
				if (localName.equalsIgnoreCase("max")) {
					out.println("<TD>" + "<" + domHelp.getElementValue(child));
				}
				if (localName.equalsIgnoreCase("interval")) {
					out.println("<TD>" + "(" + domHelp.getElementValue(child)
							+ ")");
				}
				if (localName.equalsIgnoreCase("valueList")) {
					out.println("<TD>" + "[" + domHelp.getElementValue(child)
							+ "]");
				}
			}
		} else if (constraint == null) {
			out.println("<TD>" + "&nbsp");
		}

		if (quality == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + quality);
		}
		if (descriptions.getLength() > 0) {
			Element id = (Element) descriptions.item(0); // description
			//String link = domHelp.getAttributeValue(id, "@xlink:href");
			String description = domHelp.getElementValue(id);
			out.println("<TD>" + description);
		} else if (descriptions.getLength() == 0) {
			out.println("<TD>" + "&nbsp");
		}
		out.println("</TR>");
	}

	// ***************************************************************************************
	private void handleQuantity(Element element, String elementName, String role) {

		// --------------------------------------------------------------------------------------
		// Find all "description" elements
		NodeList descriptions = domHelp.getElements(element, "gml:description");
		// NodeList descriptions = dom.getElementsByTagNameNS(gmlNamespace,
		// "description");

		// ----------------------------------------------------------------------------------------

		String value = domHelp.getElementValue(element, "swe:value");
		String definition = domHelp.getAttributeValue(element, "@definition");
		String uom = domHelp.getAttributeValue(element, "swe:uom/@code");
		if (uom == null) {
			uom = domHelp.getAttributeValue(element, "swe:uom/@xlink:href");
		}
		String quality = domHelp.getAttributeValue(element, "swe:quality");

		out.println("<TR>");
		out.println("<TD>" + getTabs() + elementName);
		out.println("<TD>" + role);

		out.println("<TD>" + element.getLocalName());
		if (value == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + value);
		}
		if (uom == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + uom);
		}
		if (definition == null) {
			out.println("<TD>" + "&nbsp");
		}
		else{
			handleURI(definition);
		}	

		Element constraint = domHelp.getElement(element,
		"swe:constraint/swe:AllowedValues");
		if (constraint != null) {
			NodeList children = domHelp.getAllChildElements(constraint);
			//System.out.println("List of kids =" + children.getLength());
			for (int i = 0; i < children.getLength(); i++) {
				Element child = (Element) children.item(i);
				String localName = child.getLocalName();
				if (localName.equalsIgnoreCase("min")) {
					out.println("<TD>" + ">" + domHelp.getElementValue(child));
				}
				if (localName.equalsIgnoreCase("max")) {
					out.println("<TD>" + "<" + domHelp.getElementValue(child));
				}
				if (localName.equalsIgnoreCase("interval")) {
					out.println("<TD>" + "(" + domHelp.getElementValue(child)
							+ ")");
					//System.out.println("<TD>" + "("
					//		+ domHelp.getElementValue(child) + ")");

				}
				if (localName.equalsIgnoreCase("valueList")) {
					out.println("<TD>" + "[" + domHelp.getElementValue(child)
							+ "]");
				}
			}
		} else if (constraint == null) {
			out.println("<TD>" + "&nbsp");
		}
		if (quality != null) {
			out.println("<TD>" + quality);
		} else if (quality == null) {
			out.println("<TD>" + "&nbsp");
		}
		if (descriptions.getLength() > 0) {
			Element id = (Element) descriptions.item(0); // description
			//String link = domHelp.getAttributeValue(id, "@xlink:href");
			String description = domHelp.getElementValue(id);
			out.println("<TD>" + description);
		} else if (descriptions.getLength() == 0) {
			out.println("<TD>" + "&nbsp");
		}

		out.println("</TR>");
	}

	// ********************************************************************************
	private void handleTime(Element element, String elementName, String role) {
		// --------------------------------------------------------------------------------------
		// Find all "description" elements
		NodeList descriptions = domHelp.getElements(element, "gml:description");
		// NodeList descriptions = dom.getElementsByTagNameNS(gmlNamespace,
		// "description");
		// ----------------------------------------------------------------------------------------
		String value = domHelp.getElementValue(element, "swe:value");
		String definition = domHelp.getAttributeValue(element, "@definition");
		String quality = domHelp.getAttributeValue(element, "swe:quality");
		String uom = domHelp.getAttributeValue(element, "swe:uom/@code");
		if (uom == null) {
			uom = domHelp.getAttributeValue(element, "swe:uom/@xlink:href");
		}
		out.println("<TR>");
		out.println("<TD>" + getTabs() + elementName);
		out.println("<TD>" + role);

		out.println("<TD>" + element.getLocalName());
		if (value == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + value);
		}
		if (uom == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + uom);
		}
		if (definition == null) {
			out.println("<TD>" + "&nbsp");
		}
		else{
			handleURI(definition);
		}
		Element constraint = domHelp.getElement(element,
		"swe:constraint/swe:AllowedValues");
		if (constraint != null) {
			NodeList children = domHelp.getAllChildElements(constraint);
			//System.out.println("List of kids =" + children.getLength());
			for (int i = 0; i < children.getLength(); i++) {
				Element child = (Element) children.item(i);
				String localName = child.getLocalName();
				if (localName.equalsIgnoreCase("min")) {
					out.println("<TD>" + ">" + domHelp.getElementValue(child));
				}
				if (localName.equalsIgnoreCase("max")) {
					out.println("<TD>" + "<" + domHelp.getElementValue(child));
				}
				if (localName.equalsIgnoreCase("interval")) {
					out.println("<TD>" + "(" + domHelp.getElementValue(child)
							+ ")");
				}
				if (localName.equalsIgnoreCase("valueList")) {
					out.println("<TD>" + "[" + domHelp.getElementValue(child)
							+ "]");
				}
			}
		} else if (constraint == null) {
			out.println("<TD>" + "&nbsp");
		}

		if (quality == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + quality);
		}
		if (descriptions.getLength() > 0) {
			Element id = (Element) descriptions.item(0); // description
			//String link = domHelp.getAttributeValue(id, "@xlink:href");
			String description = domHelp.getElementValue(id);
			out.println("<TD>" + description);
		} else if (descriptions.getLength() == 0) {
			out.println("<TD>" + "&nbsp");
		}
		out.println("</TR>");
	}

	// ********************************************************************************
	private void handleBoolean(Element element, String elementName, String role) {

		// --------------------------------------------------------------------------------------
		// Find all "description" elements
		NodeList descriptions = domHelp.getElements(element, "gml:description");
		// NodeList descriptions = dom.getElementsByTagNameNS(gmlNamespace,
		// "description");

		// ----------------------------------------------------------------------------------------

		String value = domHelp.getElementValue(element, "swe:value");
		String definition = domHelp.getAttributeValue(element, "@definition");
		String quality = domHelp.getAttributeValue(element, "swe:quality");

		out.println("<TR>");
		out.println("<TD>" + getTabs() + elementName);
		out.println("<TD>" + role);

		out.println("<TD>" + element.getLocalName());
		if (value == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + value);
		}
		out.println("<TD>" + "----");// UOM Not Applicable
		if (definition == null) {
			out.println("<TD>" + "&nbsp");
		}
		else{
			handleURI(definition);
		}
		out.println("<TD>" + "----"); // Constraints Not Applicable
		if (quality == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + quality);
		}
		if (descriptions.getLength() > 0) {
			Element id = (Element) descriptions.item(0); // description
			//String link = domHelp.getAttributeValue(id, "@xlink:href");
			String description = domHelp.getElementValue(id);
			out.println("<TD>" + description);
		} else if (descriptions.getLength() == 0) {
			out.println("<TD>" + "&nbsp");
		}
		out.println("</TR>");
	}

	// ********************************************************************************
	private void handleCategory(Element element, String elementName, String role) {
		// --------------------------------------------------------------------------------------
		// Find all "description" elements
		NodeList descriptions = domHelp.getElements(element, "gml:description");
		// NodeList descriptions = dom.getElementsByTagNameNS(gmlNamespace,
		// "description");

		// ----------------------------------------------------------------------------------------
		String value = domHelp.getElementValue(element, "swe:value");
		String definition = domHelp.getAttributeValue(element, "@definition");
		String quality = domHelp.getAttributeValue(element, "swe:quality");
		String codeSpace = domHelp.getAttributeValue(element, "swe:codeSpace");

		out.println("<TR>");
		out.println("<TD>" + getTabs() + elementName);
		out.println("<TD>" + role);

		out.println("<TD>" + element.getLocalName());
		if (value == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + value);
		}
		if (codeSpace == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + codeSpace + "(codespace)");// CodeSpace
		}
		if (definition == null) {
			out.println("<TD>" + "&nbsp");
		}
		else{
			handleURI(definition);
		}

		Element constraint = domHelp.getElement(element,
		"swe:constraint/swe:AllowedTokens");
		if (constraint != null) {
			NodeList children = domHelp.getAllChildElements(constraint);
			//System.out.println("List of kids =" + children.getLength());
			for (int i = 0; i < children.getLength(); i++) {
				Element child = (Element) children.item(i);
				String localName = child.getLocalName();
				if (localName.equalsIgnoreCase("valueList")) {
					out.println("<TD>" + "[" + domHelp.getElementValue(child)
							+ "]");
				}
			}
		} else if (constraint == null) {
			out.println("<TD>" + "&nbsp");
		}

		if (quality == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + quality);
		}
		if (descriptions.getLength() > 0) {
			Element id = (Element) descriptions.item(0); // description
			//String link = domHelp.getAttributeValue(id, "@xlink:href");
			String description = domHelp.getElementValue(id);
			out.println("<TD>" + description);
		} else if (descriptions.getLength() == 0) {
			out.println("<TD>" + "&nbsp");
		}
		out.println("</TR>");

	}

	// ********************************************************************************
	private void handleText(Element element, String elementName, String role) {

		String definition = domHelp.getAttributeValue(element, "@definition");
		String value = domHelp.getElementValue(element, "swe:value");

		out.println("<TR>");
		out.println("<TD>" + getTabs() + elementName);
		out.println("<TD>" + role);

		out.println("<TD>" + element.getLocalName());
		if (value == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + value);
		}
		out.println("<TD>" + "----"); // UOM Not Applicable
		if (definition == null) {
			out.println("<TD>" + "&nbsp");
		}
		else{
			handleURI(definition);
		}
		out.println("<TD>" + "----"); // Constraints Not Applicable
		out.println("<TD>" + "----"); // Quality Not Applicable
		out.println("</TR>");

	}

	// ********************************************************************************
	private void handleQuantityRange(Element element, String elementName,
			String role) {
		// --------------------------------------------------------------------------------------
		// Find all "description" elements
		NodeList descriptions = domHelp.getElements(element, "gml:description");
		// NodeList descriptions = dom.getElementsByTagNameNS(gmlNamespace,
		// "description");

		// ----------------------------------------------------------------------------------------

		String value = domHelp.getElementValue(element, "swe:value");
		String definition = domHelp.getAttributeValue(element, "@definition");
		String uom = domHelp.getAttributeValue(element, "swe:uom/@code");
		if (uom == null) {
			uom = domHelp.getAttributeValue(element, "swe:uom/@xlink:href");
		}
		String quality = domHelp.getAttributeValue(element, "swe:quality");

		out.println("<TR>");
		out.println("<TD>" + getTabs() + elementName);
		out.println("<TD>" + role);

		out.println("<TD>" + element.getLocalName());
		if (value == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + value);
		}
		if (uom == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + uom);
		}
		if (definition == null) {
			out.println("<TD>" + "&nbsp");
		}
		else{
			handleURI(definition);
		}
		Element constraint = domHelp.getElement(element,
		"swe:constraint/swe:AllowedValues");
		if (constraint != null) {
			NodeList children = domHelp.getAllChildElements(constraint);
			//System.out.println("List of kids =" + children.getLength());
			for (int i = 0; i < children.getLength(); i++) {
				Element child = (Element) children.item(i);
				String localName = child.getLocalName();
				if (localName.equalsIgnoreCase("min")) {
					out.println("<TD>" + ">" + domHelp.getElementValue(child));
				}
				if (localName.equalsIgnoreCase("max")) {
					out.println("<TD>" + "<" + domHelp.getElementValue(child));
				}
				if (localName.equalsIgnoreCase("interval")) {
					out.println("<TD>" + "(" + domHelp.getElementValue(child)
							+ ")");

				}
				if (localName.equalsIgnoreCase("valueList")) {
					out.println("<TD>" + "[" + domHelp.getElementValue(child)
							+ "]");
				}
			}
		} else if (constraint == null) {
			out.println("<TD>" + "&nbsp");
		}

		if (quality == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + quality);
		}
		if (descriptions.getLength() > 0) {
			Element id = (Element) descriptions.item(0); // description
			//String link = domHelp.getAttributeValue(id, "@xlink:href");
			String description = domHelp.getElementValue(id);
			out.println("<TD>" + description);
		} else if (descriptions.getLength() == 0) {
			out.println("<TD>" + "&nbsp");
		}
		out.println("</TR>");

	}

	// ********************************************************************************
	private void handleCountRange(Element element, String elementName,
			String role) {
		// --------------------------------------------------------------------------------------
		// Find all "description" elements
		NodeList descriptions = domHelp.getElements(element, "gml:description");
		// NodeList descriptions = dom.getElementsByTagNameNS(gmlNamespace,
		// "description");
		// ----------------------------------------------------------------------------------------

		String value = domHelp.getElementValue(element, "swe:value");
		//System.out.println("value = " + value);
		String definition = domHelp.getAttributeValue(element, "@definition");
		String quality = domHelp.getAttributeValue(element, "swe:quality");
		out.println("<TR>");
		out.println("<TD>" + getTabs() + elementName);
		out.println("<TD>" + role);

		out.println("<TD>" + element.getLocalName()); // Type
		if (value == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + value);
		}
		out.println("<TD>" + "----"); // UOM Not Applicable
		if (definition == null) {
			out.println("<TD>" + "&nbsp");
		}
		else{
			handleURI(definition);
		}

		Element constraint = domHelp.getElement(element,
		"swe:constraint/swe:AllowedValues");
		if (constraint != null) {
			NodeList children = domHelp.getAllChildElements(constraint);

			for (int i = 0; i < children.getLength(); i++) {
				Element child = (Element) children.item(i);
				String localName = child.getLocalName();
				if (localName.equalsIgnoreCase("min")) {
					out.println("<TD>" + ">" + domHelp.getElementValue(child));
				}
				if (localName.equalsIgnoreCase("max")) {
					out.println("<TD>" + "<" + domHelp.getElementValue(child));
				}
				if (localName.equalsIgnoreCase("interval")) {
					out.println("<TD>" + "(" + domHelp.getElementValue(child)
							+ ")");
					//System.out.println("<TD>" + "("
					//		+ domHelp.getElementValue(child) + ")");

				}
				if (localName.equalsIgnoreCase("valueList")) {
					out.println("<TD>" + "[" + domHelp.getElementValue(child)
							+ "]");
				}
			}
		} else if (constraint == null) {
			out.println("<TD>" + "&nbsp");
		}

		if (quality == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + quality);
		}
		if (descriptions.getLength() > 0) {
			Element id = (Element) descriptions.item(0); // description
//			String type = domHelp.getAttributeValue(id, "@xlink:type");
			//String link = domHelp.getAttributeValue(id, "@xlink:href");
//			String title = domHelp.getAttributeValue(id, "@xlink:title");
//			String show = domHelp.getAttributeValue(id, "@xlink:show");
//			String actuate = domHelp.getAttributeValue(id, "@xlink:actuate");
//			String remoteSchema = domHelp.getAttributeValue(id,"@gml:remoteSchema");
			String description = domHelp.getElementValue(id);
			out.println("<TD>" + description);
		} else if (descriptions.getLength() == 0) {
			out.println("<TD>" + "&nbsp");
		}
		out.println("</TR>");

	}

	// ********************************************************************************
	private void handleTimeRange(Element element, String elementName,
			String role) {
		// --------------------------------------------------------------------------------------
		// Find all "description" elements
		NodeList descriptions = domHelp.getElements(element, "gml:description");
		// NodeList descriptions = dom.getElementsByTagNameNS(gmlNamespace,
		// "description");

		// ----------------------------------------------------------------------------------------

		String value = domHelp.getElementValue(element, "swe:value");
		String definition = domHelp.getAttributeValue(element, "@definition");
		String quality = domHelp.getAttributeValue(element, "swe:quality");
		String uom = domHelp.getAttributeValue(element, "swe:uom/@code");
		if (uom == null) {
			uom = domHelp.getAttributeValue(element, "swe:uom/@xlink:href");
		}

		out.println("<TR>");
		out.println("<TD>" + getTabs() + elementName);
		out.println("<TD>" + role);

		out.println("<TD>" + element.getLocalName());
		if (value == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + value);
		}
		if (uom == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + uom);
		}
		if (definition == null) {
			out.println("<TD>" + "&nbsp");
		}
		else{
			handleURI(definition);
		}

		Element constraint = domHelp.getElement(element,
		"swe:constraint/swe:AllowedValues");
		if (constraint != null) {
			NodeList children = domHelp.getAllChildElements(constraint);
			//System.out.println("List of kids =" + children.getLength());
			for (int i = 0; i < children.getLength(); i++) {
				Element child = (Element) children.item(i);
				String localName = child.getLocalName();
				if (localName.equalsIgnoreCase("min")) {
					out.println("<TD>" + ">" + domHelp.getElementValue(child));
				}
				if (localName.equalsIgnoreCase("max")) {
					out.println("<TD>" + "<" + domHelp.getElementValue(child));
				}
				if (localName.equalsIgnoreCase("interval")) {
					out.println("<TD>" + "(" + domHelp.getElementValue(child)
							+ ")");

				}
				if (localName.equalsIgnoreCase("valueList")) {
					out.println("<TD>" + "[" + domHelp.getElementValue(child)
							+ "]");
				}
			}
		} else if (constraint == null) {
			out.println("<TD>" + "&nbsp");
		}

		if (quality == null) {
			out.println("<TD>" + "&nbsp");
		} else {
			out.println("<TD>" + quality);
		}
		if (descriptions.getLength() > 0) {
			Element id = (Element) descriptions.item(0); // description
//			String type = domHelp.getAttributeValue(id, "@xlink:type");
			//String link = domHelp.getAttributeValue(id, "@xlink:href");
//			String title = domHelp.getAttributeValue(id, "@xlink:title");
//			String show = domHelp.getAttributeValue(id, "@xlink:show");
//			String actuate = domHelp.getAttributeValue(id, "@xlink:actuate");
//			String remoteSchema = domHelp.getAttributeValue(id,"@gml:remoteSchema");
			String description = domHelp.getElementValue(id);
			out.println("<TD>" + description);
		} else if (descriptions.getLength() == 0) {
			out.println("<TD>" + "&nbsp");
		}
		out.println("</TR>");

	}

	// ********************************************************************************
	/* TODO: clean this up; handleContacts should look for multiple instances of
	 * ContactList, Person, or ResponsibleParty and handle it accordingly
	 */
	private void handleContacts(Element rootElement) {

		// Find all "contacts" elements
		NodeList contacts = domHelp.getElements(rootElement, "sml:contact");

		if (contacts.getLength()==0){
			return;
		}
		else{

			//for (int i = 0; i < contacts.getLength(); i++) {
			// Start Fieldset and Table
			out.println("<FIELDSET>");
			out.println("<font size=5>");
			out.println("<ALIGN = CENTER>");
			out.println("<LEGEND>Contacts</LEGEND>"); // Name Attribute?
			out.println("<TABLE BORDER=1 ALIGN=\"LEFT\">");
			out.println("<COLGROUP SPAN=2 ALIGN=\"LEFT\">");
			out.println("<TR BGCOLOR=\"#BFBFBF\">\n");
			out.println("<TH>Role<TH>Information");

			Element contact = (Element) contacts.item(0);
			String localName = contact.getLocalName();
			String role = getRole(contact);
			
			String link = domHelp.getAttributeValue((Element) contacts.item(0),
			"@xlink:href");
			//System.out.println("Link =" + link);
			if (link != null) {
				handleLink((Element) contacts.item(0), localName, link, role);
			}
			//System.out.println("firstchild =" + contact);
			//System.out.println("Contact Name =" + localName);

			NodeList contactName = domHelp.getChildElements(contact);
			if (contactName == null) {
				return;
			}
			//System.out.println("Contact Name List =" + contactName.getLength());

			for (int j = 0; j < contactName.getLength(); j++) {
				
				Element contact2 = (Element) contactName.item(j);
				String role2 = getRole(contact2);
				String link2 = domHelp.getAttributeValue(contact2, "@xlink:href");

				if (link2 != null) {
					handleLink(contact2, localName, link2,role2);
				}
				Element firstchild = (Element) contactName.item(0);
				String localName2 = firstchild.getLocalName();

				// if Responsible Party then do
				if (localName2.equalsIgnoreCase("ResponsibleParty")) {
					handleResponsibleParty(firstchild);
				}
				// if Person then do
				else if (localName2.equalsIgnoreCase("Person")) {
					handlePerson();
				}
				// if ContactList then do
				else if (localName2.equalsIgnoreCase("ContactList")) {
					handleContactList();
				}
			}
			out.println("</TABLE>");
			out.println("<BR>");
			out.println("</FIELDSET>");
			out.println("<BR>");
		}
		//}

	}

	// ********************************************************************************
	/*
	 * private void handleDocumentation(){
	 * 
	 * //Find all "documentation" elements NodeList documentation =
	 * dom.getElementsByTagNameNS(smlNamespace,"documentation");
	 * //System.out.println("number of documentation lists = " +
	 * documentation.getLength());
	 * 
	 * for (int i=0; i<documentation.getLength(); i++){ Element id = (Element)
	 * documentation.item(i); // documentation NodeList documentList =
	 * id.getElementsByTagNameNS(smlNamespace, "DocumentList"); //documentList
	 * Element documentListEl = (Element) documentList.item(0);
	 * //System.out.println("DocumentList = " + documentListEl.getNodeName());
	 * 
	 * //Start Fieldset and Table out.println("<FIELDSET>"); out.println("<font
	 * size=5>"); out.println("<ALIGN = CENTER>"); out.println("<LEGEND>Documentation</LEGEND>");
	 * //Name Attribute? out.println("<TABLE BORDER=1 ALIGN=\"LEFT\">");
	 * out.println("<COLGROUP SPAN=3 ALIGN=\"LEFT\">"); out.println("<TR BGCOLOR=\"#BFBFBF\">\n");
	 * out.println("<TH>Name<TH>Value<TH>Definition"); out.println("<br>");
	 * 
	 * //Add each classifier NodeList children =
	 * classifierListEl.getElementsByTagNameNS(smlNamespace, "classifier"); //
	 * classifier //System.out.println("No of classifiers = " +
	 * children.getLength()); for(int j=0; j<children.getLength();j++){ Element
	 * child = (Element) children.item(j); String name =
	 * domHelp.getAttributeValue(child, "name"); //System.out.println("name = " +
	 * name); String value = domHelp.getElementValue(child,
	 * "sml:Term/sml:value"); //System.out.println("value = " + value); String
	 * definition = domHelp.getAttributeValue(child, "sml:Term/@definition");
	 * 
	 * //Print table entry" out.println("<TR><TD>" + name); out.println("<TD>" +
	 * value); out.println("<TD>" + definition); } out.println("</TABLE>");
	 * out.println("</FIELDSET>"); out.println("<BR>"); } }
	 */
	// ****************************************************************************************
	private void handleObservableProperty(Element element, String elementName,
			String role) {
		// --------------------------------------------------------------------------------------
		// Find all "description" elements
		NodeList descriptions = domHelp.getElements(element, "gml:description");
		// ----------------------------------------------------------------------------------------
		String definition = domHelp.getAttributeValue(element, "@definition");

		out.println("<TR>");
		out.println("<TD>" + getTabs() + elementName);
		out.println("<TD>" + role);

		out.println("<TD>" + element.getLocalName());
		out.println("<TD>" + "----"); // Value Not Applicable
		out.println("<TD>" + "----"); // UOM Not Applicable
		if (definition == null) {
			out.println("<TD>" + "&nbsp");
		}
		else{
			handleURI(definition);
		}
		out.println("<TD>" + "----"); // Constraints Not Applicable
		out.println("<TD>" + "----"); // Quality Not Applicable
		if (descriptions.getLength() > 0) {
			Element id = (Element) descriptions.item(0); // description
			//String link = domHelp.getAttributeValue(id, "@xlink:href");
			String description = domHelp.getElementValue(id);
			out.println("<TD>" + description);
		} else if (descriptions.getLength() == 0) {
			out.println("<TD>" + "&nbsp");
		}
		out.println("</TR>");
	}

	// ****************************************************************************************
	private void handleResponsibleParty(Element rootElement) {

		NodeList ResponsibleParty = dom.getElementsByTagNameNS(smlNamespace, "ResponsibleParty");
		//Element ResponsiblePartyEl = (Element) ResponsibleParty.item(0);

		for (int j=0; j<ResponsibleParty.getLength(); j++){

			Element child = (Element) ResponsibleParty.item(j);
			String individualName = domHelp.getElementValue(child,
				"sml:individualName");
			String organizationName = domHelp.getElementValue(child,
				"sml:organizationName");
			String positionName = domHelp.getElementValue(child,
				"sml:positionName");
			String voice = domHelp.getElementValue(child,
				"sml:contactInfo/sml:phone/sml:voice");
			String facsimile = domHelp.getElementValue(child,
				"sml:contactInfo/sml:phone/sml:facsimile");
			String deliveryPoint = domHelp.getElementValue(child,
				"sml:contactInfo/sml:address/sml:deliveryPoint");
			String city = domHelp.getElementValue(child,
				"sml:contactInfo/sml:address/sml:city");
			String administrativeArea = domHelp.getElementValue(child,
				"sml:contactInfo/sml:address/sml:administrativeArea");
			String postalCode = domHelp.getElementValue(child,
				"sml:contactInfo/sml:address/sml:postalCode");
			String country = domHelp.getElementValue(child,
				"sml:contactInfo/sml:address/sml:country");
			String electronicMailAddress = domHelp.getElementValue(child,
				"sml:contactInfo/sml:address/sml:electronicMailAddress");
			String hoursOfService = domHelp.getElementValue(child,
				"sml:contactInfo/sml:hoursOfService");
			String contactInstructions = domHelp.getElementValue(child,
				"sml:contactInfo/sml:contactInstructions");

			// Print table entry"
			out.println("<TR><TD>" + "----");
			out.println("<TD>");
			if (individualName != null) {
				out.println(individualName + "<br>");
			}
			if (organizationName != null) {
				out.println(organizationName + "<br>");
			}
			if (positionName != null) {
				out.println(positionName + "<br>");
			}
			if (voice != null) {
				out.println(voice + "<br>");
			}
			if (facsimile != null) {
				out.println(facsimile + "<br>");
			}
			if (deliveryPoint != null) {
				out.println(deliveryPoint + "<br>");
			}
			if (city != null) {
				out.println(city + ", " + administrativeArea + " " + postalCode
						+ "<br>");
			}
			// if(administrativeArea!=null){
			// out.println(+ "<br>");
			// }
			// if(postalCode!=null){
			// out.println(postalCode+ "<br>");
			// }
			if (country != null) {
				out.println(country + "<br>");
			}
			if (electronicMailAddress != null) {
				out.println(electronicMailAddress + "<br>");
			}
			if (hoursOfService != null) {
				out.println(hoursOfService + "<br>");
			}
			if (contactInstructions != null) {
				out.println(contactInstructions);
			}

		}

	}

	// *************************************************************************************
	private void handleContactList() {
		// Element id = (Element) contacts.item(0); // contact
		NodeList ContactList = dom.getElementsByTagNameNS(smlNamespace,
		"ContactList"); // ContactList
		Element ContactListEl = (Element) ContactList.item(0);

		for (int m = 0; m < ContactList.getLength(); m++) {
			// Add each member
			//NodeList children = ContactListEl.getElementsByTagNameNS(
			//		smlNamespace, "member"); // member
			// Element childrenEl = (Element) children.item(0);

			NodeList next = domHelp.getAllChildElements(ContactListEl);
			Element firstchild2 = (Element) next.item(m);
			String newlocalName = firstchild2.getLocalName();

			// if Person then do
			if (newlocalName.equalsIgnoreCase("Person")) {
				handlePerson();
			}
			// if ResponsibleParty then do
			else if (newlocalName.equalsIgnoreCase("ResponsibleParty")) {
				handleResponsibleParty(firstchild2);
			}
		}
	}

	// **************************************************************************************
	private void handlePerson() {

		NodeList Person = dom.getElementsByTagNameNS(smlNamespace, "Person"); // Person
		//Element PersonEl = (Element) Person.item(0);
		//System.out.println("Person = " + PersonEl.getNodeName());

		for (int j = 0; j < Person.getLength(); j++) {

			Element child = (Element) Person.item(j);
			String surname = domHelp.getElementValue(child, "sml:surname");
			String name = domHelp.getElementValue(child, "sml:name");
			String userID = domHelp.getElementValue(child, "sml:userID");
			String affiliation = domHelp.getElementValue(child,	"sml:affiliation");
			String phoneNumber = domHelp.getElementValue(child,	"sml:phoneNumber");
			String email = domHelp.getElementValue(child, "sml:email");

			// Print table entry"
			out.println("<TR><TD>" + "---");
			out.println("<TD>");
			if (surname != null) {
				out.println(name + "&nbsp" + surname + "<br>");
			}
			if (userID != null) {
				out.println(userID + "<br>");
			}
			if (affiliation != null) {
				out.println(affiliation + "<br>");
			}
			if (phoneNumber != null) {
				out.println(phoneNumber + "<br>");
			}
			if (email != null) {
				out.println(email);
			}
		}
	}

	// ************************************************************************************************************
	private void handleLink(Element element, String elementName, String link,
			String role) {

		if (elementName.equalsIgnoreCase("contact")) {
			out.println("<TR>");
			out.println("<TD>" + getTabs() + role);
			out.println("<TD><a href=" + link + ">" + link + "</a>");
		}

		else if (elementName.equalsIgnoreCase("documentation")) {
			out.println("<TR>");
			out.println("<TD>" + getTabs() + role);
			out.println("<TD><a href=" + link + ">" + link + "</a>");
		}

		else if (elementName.equalsIgnoreCase("capabilities")) {
			out.println("<TR>");
			out.println("<TD>" + getTabs() + elementName);
			out.println("<TD>" + role);

			out.println("<TD>" + "----");
			out.println("<TD><a href=" + link + ">" + link + "</a>");
			out.println("<TD>" + "----");
			out.println("<TD>" + "----");
			out.println("<TD>" + "----");
			out.println("<TD>" + "&nbsp");
			out.println("<TD>" + "&nbsp");
		}

		else if (elementName.equalsIgnoreCase("onlineResource")) {
			//out.println("<TR>");
			out.println("<a href=" + link + ">" + link + "</a>");
		}


		else {
			out.println("<TR>");
			out.println("<TD>" + getTabs() + elementName);
			out.println("<TD>" + role);

			out.println("<TD>" + "----");
			out.println("<TD><a href=" + link + ">" + link + "</a>");
			out.println("<TD>" + "----");
			out.println("<TD>" + "----");
			out.println("<TD>" + "----");
		}
	}

	// *********************************************************************************************************
	private void handleDescription(Element rootElement) {

		// Find all "description" elements
		// NodeList descriptions = domHelp.getElements(rootElement,
		// "gml:description");
		NodeList descriptions = dom.getElementsByTagNameNS(gmlNamespace,
		"description");

		// Start Fieldset and Table
		out.println("<FIELDSET>");
		out.println("<font size=5>");
		out.println("<ALIGN = CENTER>");
		out.println("<LEGEND>Description</LEGEND>");
		out.println("<TABLE BORDER=1 ALIGN=\"LEFT\">");
		out.println("<COLGROUP SPAN=1 ALIGN=\"LEFT\">");
		out.println("<TR BGCOLOR=\"#BFBFBF\">\n");
		out.println("<TH>Description ");
		out.println("<TR><TD>");

		for (int i = 0; i < descriptions.getLength(); i++) {
			Element id = (Element) descriptions.item(i); // description

//			String type = domHelp.getAttributeValue(id, "@xlink:type");
			String link = domHelp.getAttributeValue(id, "@xlink:href");
			String role = getRole(id);
			
			//String title = domHelp.getAttributeValue(id, "@xlink:title");
//			String show = domHelp.getAttributeValue(id, "@xlink:show");
//			String actuate = domHelp.getAttributeValue(id, "@xlink:actuate");
//			String remoteSchema = domHelp.getAttributeValue(id,"@gml:remoteSchema");
			String description = domHelp.getElementValue(id);

			if (description != null) {
				out.println("Description = " + description + "<br>");
			}
//			if (type != null) {
//				out.println("Type = " + type + "<br>");
//			}
			if (link != null) {
				handleURI(link);	
			}
			if (role != null) {
				out.println("Role = " + role + "<br>");
			}
//			if (title != null) {
//				out.println("Title = " + title + "<br>");
//			}
//			if (show != null) {
//				out.println("Show = " + show + "<br>");
//			}
//			if (actuate != null) {
//				out.println("Actuate = " + actuate + "<br>");
//			}
//			if (actuate != null) {
//				out.println("Actuate = " + actuate + "<br>");
//			}
//			if (remoteSchema != null) {
//				out.println("<a href=" + remoteSchema + ">Remote Schema</a>");
//			}

		}
		out.println("<br>");
		out.println("</TABLE>");
		out.println("</FIELDSET>");
		out.println("<BR>");
	}

	// *********************************************************************************************************
	private void handleDocumentation(Element Doc) {
		// TODO: FIX ALL THIS MESS 
		// Find all "documentation" elements
		NodeList documentation = dom.getElementsByTagNameNS(smlNamespace,
		"documentation");

		if (documentation.getLength()==0){
			return;
		}
		else{

			// Start Fieldset and Table
			out.println("<FIELDSET>");
			out.println("<font size=5>");
			out.println("<ALIGN = CENTER>");
			out.println("<LEGEND>Documentation</LEGEND>"); // Name Attribute?
			out.println("<TABLE BORDER=1 ALIGN=\"LEFT\">");
			out.println("<COLGROUP SPAN=2 ALIGN=\"LEFT\">");
			out.println("<TR BGCOLOR=\"#BFBFBF\">\n");
			out.println("<TH>Role<TH>Information");

			// Print table entry"
			out.println("<TR><TD>");
			String link = domHelp.getAttributeValue((Element) documentation.item(0), "@xlink:href");
			if (link!=null){
				handleURI(link);
			}
			
			// TODO: use new role
			//String role = getRole(child);

			String role = domHelp.getAttributeValue((Element) documentation.item(0), "@xlink:role");
			if (role == null) {
				role = domHelp.getAttributeValue((Element) documentation.item(0), "@xlink:arcrole");
			}
			if (role!=null){
				out.println(role);
				out.println("<TD>");
			}

			for (int i = 0; i < documentation.getLength(); i++) {
				Element firstchild = (Element) documentation.item(i);
				//String localName = firstchild.getLocalName();
				//System.out.println("localName =" + localName);
				//System.out.println("firstchild =" + firstchild);
				NodeList children = domHelp.getChildElements(firstchild);
				//System.out.println("List of kids =" + children.getLength());

				for (int j = 0; j<children.getLength(); j++) {
					Element child = (Element) children.item(j);
					//String localName2 = child.getLocalName();
					NodeList kids = domHelp.getChildElements(child);
					String description = domHelp.getElementValue(child, "gml:description");
					String date = domHelp.getElementValue(child, "sml:date");
					String format = domHelp.getElementValue(child, "sml:format");
					//String onlineResource = domHelp.getElementValue(child, "sml:onlineResource");
					if (description != null) {
						out.println(description + "<br>");
					}
					if (date != null) {
						out.println(date + "<br>");
					}
					if (format != null) {
						out.println(format + "<br>");
					}

					for(int m=0; m<kids.getLength(); m++){
						Element kid = (Element)kids.item(m);
						String kidName = kid.getLocalName();
						if (kidName.equalsIgnoreCase("Contact")) {
							handleContacts(child);
						}
						String xrole = getRole(kid);
						String xlink = domHelp.getAttributeValue(kid, "@xlink:href");
						if (xlink!=null){
							handleLink(kid, kidName, xlink, xrole);	
						}
						
						out.println("xRole = " + xrole + "<br>");
							
					}	
					//out.println("<TD>");
				}
			}
			//out.println("<TD>");
			out.println("</TABLE>");
			out.println("<BR>");
			out.println("</FIELDSET>");
			out.println("<BR>");
		}
	}

	// **************************************************************************************
/*	private void handleDocumentList() {
		// Element id = (Element) contacts.item(0); // document
		NodeList DocumentList = dom.getElementsByTagNameNS(smlNamespace,
		"DocumentList"); // DocumentList
		Element DocumentListEl = (Element) DocumentList.item(0);

		for (int m = 0; m < DocumentList.getLength(); m++) {
			// Add each member
			NodeList children = DocumentListEl.getElementsByTagNameNS(
					smlNamespace, "member"); // member
			// Element childrenEl = (Element) children.item(0);

			NodeList next = domHelp.getAllChildElements(DocumentListEl);
			Element firstchild2 = (Element) next.item(m);
			String newlocalName = firstchild2.getLocalName();

			// if Document then do
			if (newlocalName.equalsIgnoreCase("Document")) {
				handleDocumentation(firstchild2);
			}

		}
	}
*/
	// *******************************************************************************************
	private void handleComponent(Element rootElement) {

		// TODO: fix this logic; only one component allowed
		// Find all "components" elements
		NodeList components = domHelp.getElements(rootElement, "sml:components");


		for (int i = 0; i < components.getLength(); i++) {
			Element id = (Element) components.item(i);
			NodeList ComponentList = id.getElementsByTagNameNS(smlNamespace,
					"ComponentList");
			Element ComponentListEl = (Element) ComponentList.item(0);


			NodeList children = ComponentListEl.getElementsByTagNameNS(
					smlNamespace, "component");

			// Start Fieldset and Table
			out.println("<FIELDSET>");
			out.println("<font size=5>");
			out.println("<ALIGN = CENTER>");
			out.println("<LEGEND>Components</LEGEND>"); // Name Attribute?
			out.println("<TABLE BORDER=1 ALIGN=\"LEFT\">");
			out.println("<COLGROUP SPAN=2 ALIGN=\"LEFT\">");
			out.println("<TR BGCOLOR=\"#BFBFBF\">\n");
			out.println("<TH>Name<TH>Component Information<TD>");

			// TODO: we going through this again; just redundantly call appropriate method?
			for (int j = 0; j < children.getLength(); j++) {
				Element child = (Element) children.item(j);
				//String localName = child.getLocalName();

				//String role = getRole(child);				
				String link = domHelp.getAttributeValue(child, "@xlink:href");

				String name = domHelp.getAttributeValue(child, "@name");

				out.println("<TR>");
				out.println("<TD>" + name);
				out.println("<TD>");
				if (link != null) {
					out.println("<A HREF=http://vast.uah.edu/SensorMLTableView/TableView?url="+link 
							+ ">" + link +"</A>");
				}
				
				NodeList element = domHelp.getChildElements(child);

				for (int m = 0; m < element.getLength(); m++) {
					Element nextChild = (Element) element.item(m);
					//String nextName = nextChild.getLocalName();
					NodeList Component = domHelp.getAllChildElements(nextChild);

					for (int k = 0; k < Component.getLength(); k++) {
						Element finalChild = (Element) Component.item(k);
						String newName = finalChild.getNodeName();

						if (newName.equalsIgnoreCase("sml:keywords")) {
							handleKeywords(nextChild);
						} else if (newName
								.equalsIgnoreCase("sml:identification")) {
							handleIdentifiers(nextChild);
						} else if (newName
								.equalsIgnoreCase("sml:classification")) {
							handleClassifiers(nextChild);
						} else if (newName
								.equalsIgnoreCase("sml:characteristics")) {
							handleCharacteristics(nextChild);
						} else if (newName.equalsIgnoreCase("sml:capabilities")) {
							handleCapabilities(nextChild);
						} else if (newName.equalsIgnoreCase("sml:contact")) {
							handleContacts(nextChild);
						} else if (newName.equalsIgnoreCase("sml:inputs")) {
							handleInputs(nextChild);
						} else if (newName.equalsIgnoreCase("sml:outputs")) {
							handleOutputs(nextChild);
						} else if (newName.equalsIgnoreCase("sml:parameters")) {
							handleParameters(nextChild);
						} else {
							//System.out.println("Child not a supported type");
						}
					}
				}
			}
			out.println("</TABLE>");
			out.println("<BR>");
			out.println("</FIELDSET>");
		}
		out.println("<BR>");

	}
	//*********************************************************************************************

/*	private void handleValidTime(Element startElement){
		// Find all "validTime" elements
		NodeList validTime = domHelp.getElements(startElement,
		"validTime");
		//System.out.println("number of validTime lists = "
		//		+ validTime.getLength());

		if (validTime.getLength()==0){
			return;
		}
		else{

			// Start Fieldset and Table
			out.println("<FIELDSET>");
			out.println("<font size=5>");
			out.println("<ALIGN = CENTER>");
			out.println("<LEGEND>Valid Time</LEGEND>"); // Name
			out.println("<TABLE BORDER=1 ALIGN=\"LEFT\">");
			out.println("<COLGROUP SPAN=5 ALIGN=\"LEFT\">");
			out.println("<TR BGCOLOR=\"#BFBFBF\">\n");
			out.println("<TH>Id<TH>Value<TH>Value<TH>Role<TH>Link");
			out.println("<TR><TD>");

			for (int i = 0; i < validTime.getLength(); i++) {
				Element firstchild = (Element) validTime.item(i);
				String localName = firstchild.getLocalName();
				NodeList children = domHelp.getChildElements(firstchild);

				for (int j = 0; j < children.getLength(); j++) {
					Element child = (Element) children.item(j);
					String localName2 = child.getLocalName();
					String name = domHelp.getAttributeValue((Element) children.item(j),
						"@name");
					NodeList kids = domHelp.getChildElements(child);
					Element kid = (Element)kids.item(j);
					String kidName = kid.getLocalName();
					String gmlName = domHelp.getAttributeValue((Element) children.item(j),
					"@gml:name");
					if(kidName.equalsIgnoreCase("timePosition")){

					}
					if(kidName.equalsIgnoreCase("relatedTime")){

					}


					String id = domHelp.getAttributeValue(child, "@gml:id");
					if (id == null) {
						out.println("&nbsp" + "<TD>");
					} else {
						out.println(id + "<TD>");
					}
					String srs = domHelp.getAttributeValue(child, "@srsName");
					if (srs == null) {
						out.println("&nbsp" + "<TD>");
					} else {
						out.println(srs + "<TD>");
					}

				}
				out.println("</TABLE>");
				out.println("<BR>");
				out.println("</FIELDSET>");
				out.println("<BR>");
			}
		}
	}*/
	//***************************************************************************************************
	private void handleLegalConstraint(Element startElement){
		
		//Find all "legalConstraints" elements
		NodeList legal = domHelp.getElements(startElement,
		"legalConstraint");
		
		for (int i = 0; i < legal.getLength(); i++) {
			Element child = (Element) legal.item(i);
		
			// Find all "Rights" elements
			NodeList Rights = domHelp.getElements(child,
			"Rights");
	
			out.println("<FIELDSET>");
			out.println("<font size=5>");
			out.println("<ALIGN = CENTER>");
			out.println("<LEGEND>Legal Constraints</LEGEND>"); // Name
			out.println("<TABLE BORDER=1 ALIGN=\"LEFT\">");
			out.println("<COLGROUP SPAN=2 ALIGN=\"LEFT\">");
			out.println("<TR BGCOLOR=\"#BFBFBF\">\n");
			out.println("<TH>Role<TH>Information");
			for (int j = 0; j < Rights.getLength(); j++) {
				Element documents = (Element) Rights.item(j); // documentation
				handleLegalDocs(documents);
			}
			out.println("</TABLE>");
			out.println("<BR>");
			out.println("</FIELDSET>");
			out.println("<BR>");
		}
	}
	//***************************************************************************************************
	private void handleHistory(Element startElement){
		// Find all "history" elements
		NodeList history = domHelp.getElements(startElement, "sml:history");

		if (history.getLength()==0){
			return;
		}
		else{
			for (int i = 0; i < history.getLength(); i++) {
				Element id = (Element) history.item(i); 
	
				//String link = domHelp.getAttributeValue(id, "@xlink:href");
				//String role = getRole(id);
	
				NodeList eventList = id.getElementsByTagNameNS(smlNamespace,
						"EventList"); 
				Element eventListEl = (Element) eventList.item(0);

				//String id2 = domHelp.getAttributeValue(eventListEl, "id");
	
				// Start Fieldset and Table
				out.println("<FIELDSET>");
				out.println("<font size=5>");
				out.println("<ALIGN = CENTER>");
				out.println("<LEGEND>History</LEGEND>");
				out.println("<TABLE BORDER=1 ALIGN=\"LEFT\">");
				out.println("<COLGROUP SPAN=4 ALIGN=\"LEFT\">");
				out.println("<TR BGCOLOR=\"#BFBFBF\">\n");
				out.println("<TH>Name<TH>Role<TH>Value<TH>Definition");
				out.println("<br>");
	
				NodeList children = eventListEl.getElementsByTagNameNS(
						smlNamespace, "member"); // member

				for (int j = 0; j < children.getLength(); j++) {
					Element child = (Element) children.item(j);
					String childName = child.getLocalName();
					String xrole = getRole(child);

					String xlink = domHelp.getAttributeValue(child, "@xlink:href");

					if (xlink!=null){
						handleLink(child, childName, xlink, xrole);	
					}
					
					out.println("xRole = " + xrole + "<br>");
						
	
					NodeList nextChild = child.getElementsByTagNameNS(
							smlNamespace, "Event"); // Event
					//System.out.println("No of Events = " + nextChild.getLength());
					for (int k = 0; k < nextChild.getLength(); k++) {
						Element finalChild = (Element) nextChild.item(k);
						String finalChildName = child.getLocalName();
						if(finalChildName == "description"){
							handleDescription(finalChild);
						}
						if(finalChildName == "keywords"){
							handleKeywords(finalChild);
						}
						if(finalChildName == "identification"){
							handleIdentifiers(finalChild);
						}
						if(finalChildName == "classification"){
							handleClassifiers(finalChild);
						}
						if(finalChildName == "contact"){
							handleContacts(finalChild);
						}
						if(finalChildName == "documentation"){
							handleDocumentation(finalChild);
						}	
					}
					out.println("</TABLE>");
					out.println("</FIELDSET>");
					out.println("<BR>");
				}
			}
		}
	}
	//***************************************************************************************************
	private void handleSpatialReferenceFrame(Element startElement){
		//System.out.println("engineering start element is:" + startElement);
		NodeList sRF = domHelp.getElements(startElement,
		"spatialReferenceFrame");
		// System.out.println("number of Spatial Reference Frame lists = "
		// + sRF.getLength());
		
		if (sRF.getLength()==0){
			return;
		}
		else{
			for (int i = 0; i < sRF.getLength(); i++) {
				Element id = (Element) sRF.item(i); 
				NodeList EngineeringCRS = id.getElementsByTagNameNS(gmlNamespace,
					"EngineeringCRS");
				Element EngineeringCRSEl = (Element) EngineeringCRS.item(0);
		
				// Start Fieldset and Table
				out.println("<FIELDSET>");
				out.println("<font size=5>");
				out.println("<ALIGN = CENTER>");
				out.println("<LEGEND>Spatial Reference Frame</LEGEND>"); 
				out.println("<TABLE BORDER=1 ALIGN=\"LEFT\">");
				out.println("<COLGROUP SPAN=1 ALIGN=\"LEFT\">");
				out.println("<TR BGCOLOR=\"#BFBFBF\">\n");
				out.println("<TH>Information");
				out.println("<br>");
				
				String id2 = domHelp.getAttributeValue(EngineeringCRSEl, "id");
				String name = domHelp.getElementValue(EngineeringCRSEl, "name");
				String description = domHelp.getElementValue(EngineeringCRSEl, "description");
				String remarks = domHelp.getElementValue(EngineeringCRSEl, "remarks");
				String scope = domHelp.getElementValue(EngineeringCRSEl, "scope");
						
				out.println("<TR><TD>");
				if (id2 != null) {
					out.println(id2 + "<br>");
				}
				if (name != null) {
					out.println(name + "<br>");
				}
				if (description != null) {
					out.println(description + "<br>");
				}
				if (remarks != null) {
					out.println(remarks + "<br>");
				}if (scope != null) {
					out.println(scope + "<br>");
				}
				
				NodeList kids = domHelp.getChildElements(EngineeringCRSEl);
				
				for(int m=0; m<kids.getLength(); m++){
					Element kid = (Element)kids.item(m);
					String kidName = kid.getLocalName();
					if (kidName.equalsIgnoreCase("coordinateSystem")) {
						//System.out.println("I am in coordinate system");
					}
					if (kidName.equalsIgnoreCase("coordinateSystem")) {
						//System.out.println("I am in coordinate system");
					}
					if (kidName.equalsIgnoreCase("engineeringDatum")) {
						//System.out.println("I am in engineering datum");
					}
				}
				
				
			}
		out.println("</TABLE>");
		out.println("</FIELDSET>");
		out.println("<BR>");
		}
	}

	//***************************************************************************************************
	private void handleTemporalReferenceFrame(Element startElement){
		//System.out.println("temporal start element is:" + startElement);
		NodeList tRF = domHelp.getElements(startElement,
		"temporalReferenceFrame");
		
		if (tRF.getLength()==0){
			return;
		}
		else{
			for (int i = 0; i < tRF.getLength(); i++) {
				Element id = (Element) tRF.item(i); 
				NodeList TemporalCRS = id.getElementsByTagNameNS(gmlNamespace,
				"TemporalCRS");
				Element TemporalCRSEl = (Element) TemporalCRS.item(0);
		
				// Start Fieldset and Table
				out.println("<FIELDSET>");
				out.println("<font size=5>");
				out.println("<ALIGN = CENTER>");
				out.println("<LEGEND>Temporal Reference Frame</LEGEND>"); 
				out.println("<TABLE BORDER=1 ALIGN=\"LEFT\">");
				out.println("<COLGROUP SPAN=1 ALIGN=\"LEFT\">");
				out.println("<TR BGCOLOR=\"#BFBFBF\">\n");
				out.println("<TH>Information");
				out.println("<br>");
				
				String id2 = domHelp.getAttributeValue(TemporalCRSEl, "id");
				String name = domHelp.getElementValue(TemporalCRSEl, "name");
				String description = domHelp.getElementValue(TemporalCRSEl, "description");
				String remarks = domHelp.getElementValue(TemporalCRSEl, "remarks");
				String scope = domHelp.getElementValue(TemporalCRSEl, "scope");
						
				out.println("<TR><TD>");
				if (id2 != null) {
					out.println(id2 + "<br>");
				}
				if (name != null) {
					out.println(name + "<br>");
				}
				if (description != null) {
					out.println(description + "<br>");
				}
				if (remarks != null) {
					out.println(remarks + "<br>");
				}if (scope != null) {
					out.println(scope + "<br>");
				}
				
				NodeList kids = domHelp.getChildElements(TemporalCRSEl);
				
				for(int m=0; m<kids.getLength(); m++){
					Element kid = (Element)kids.item(m);
					String kidName = kid.getLocalName();
					//System.out.println("kidName = " + kidName);
					if (kidName.equalsIgnoreCase("temporalDatum")) {
						//System.out.println("I am in temporal Datum");
					}
					if (kidName.equalsIgnoreCase("timeCS")) {
						//System.out.println("I am in timeCS");
					}
					if (kidName.equalsIgnoreCase("usesTemporalCS")) {
						//System.out.println("I am in uses Temporal CS");
					}
				}
				
				
			}
		out.println("</TABLE>");
		out.println("</FIELDSET>");
		out.println("<BR>");
		}
	}
	//***************************************************************************************************
	private void handleLocation(Element startElement){
		// Find all "location" elements
		NodeList locations = domHelp.getElements(startElement,
		"location");

		for (int i = 0; i < locations.getLength(); i++) {
			//String parentName = domHelp.getAttributeValue(
			//		(Element) locations.item(i), "@name");
			NodeList element = domHelp.getAllChildElements((Element) locations.item(i));
			Element parent = (Element) element.item(i);
			NodeList children = domHelp.getChildElements(parent);

			for (int j = 0; j < children.getLength(); j++) {
				// Start Fieldset and Table
				out.println("<FIELDSET>");
				out.println("<font size=5>");
				out.println("<ALIGN = CENTER>");
				out.println("<LEGEND>Location</LEGEND>"); // Name
				// Attribute?
				out.println("<TABLE BORDER=1 ALIGN=\"LEFT\">");
				out.println("<COLGROUP SPAN=5 ALIGN=\"LEFT\">");
				out.println("<TR BGCOLOR=\"#BFBFBF\">\n");
				out.println("<TH>Role<TH>Link<TH>Id<TH>Name<TH>Value");

				for (int l = 0; l < children.getLength(); l++) {
					
					//Element child1 = (Element) children.item(i);
					//String locationName = domHelp.getAttributeValue(child1, "@name");
					//NodeList elements = domHelp.getAllChildElements(child1);

					// TODO: fix all this !!!
					Element child = (Element) element.item(l);
					String role = domHelp.getAttributeValue((Element) children
							.item(l), "@xlink:role");
					if (role == null) {
						role = domHelp.getAttributeValue((Element) children
								.item(l), "@xlink:arcrole");
					}
					out.println("<TR>");
					out.println("<TD>" + role);

					String link = domHelp.getAttributeValue((Element) children
							.item(l), "@xlink:href");
					if (link == null) {
						out.println("<TD>" + "&nbsp");
					}
					else{
						handleURI(link);
					}	
					String id = domHelp.getAttributeValue(child, "@gml:id");
					if (id == null) {
						out.println("<TD>" + "&nbsp");
					} else {
						out.println("<TD>" + id);
					}
					String srs = domHelp.getAttributeValue(child, "@srsName");
					if (srs == null) {
						out.println("<TD>" + "&nbsp");
					} else {
						out.println("<TD>" + srs);
					}
					//NodeList kids = domHelp.getAllChildElements((Element) element.item(l));
					Element Pointchild = (Element) element.item(l);
					String value = domHelp.getElementValue(Pointchild, "gml:coordinates");
					if (value == null) {
						out.println("<TD>" + "&nbsp");
					} else {
						out.println("<TD>" + value);
					}
				}
				out.println("</TABLE>");
				out.println("<BR>");
				out.println("</FIELDSET>");
			}
		}
	}
	//***************************************************************************************************
	private void handleTimePosition(Element startElement){

	}
	//***************************************************************************************************
	private void handleInterfaces(Element startElement){

	}
	//***************************************************************************************************
	private void handlePositions(Element startElement){

	}
	//***************************************************************************************************
/*	private void handleValidation(){
		File docFile = new File("component.xml");

		try {

			DOMParser parser = new DOMParser();

			parser.setFeature("http://xml.org/sax/features/validation", true); 
			parser.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
			"component.xsd"); 
			parser.parse("component.xml");
		} catch (Exception e) {
			//System.out.print("Problem parsing the file.");
		}   
	}	*/
	//***************************************************************************************************
	private void handleLegalDocs(Element Doc){
		// Find all "documentation" elements
		
		NodeList documentation = domHelp.getAllChildElements(Doc);

		// Print table entry"
		out.println("<TR><TD>");
		String link = domHelp.getAttributeValue((Element) documentation.item(0), "@xlink:href");
		if (link!=null){
			handleURI(link);
		}
		String role = getRole((Element) documentation.item(0));

		out.println(role);
		out.println("<TD>");

		for (int i = 0; i < documentation.getLength(); i++) {
			Element firstchild = (Element) documentation.item(i);
			//String localName = firstchild.getLocalName();

			NodeList children = domHelp.getChildElements(firstchild);

			for (int j = 0; j<children.getLength(); j++) {
				Element child = (Element) children.item(j);
				//String localName2 = child.getLocalName();
				NodeList kids = domHelp.getChildElements(child);
				String description = domHelp.getElementValue(child, "gml:description");
				String date = domHelp.getElementValue(child, "sml:date");
				String format = domHelp.getElementValue(child, "sml:format");
				//String onlineResource = domHelp.getElementValue(child, "sml:onlineResource");
				if (description != null) {
					out.println(description + "<br>");
				}
				if (date != null) {
					out.println(date + "<br>");
				}
				if (format != null) {
					out.println(format + "<br>");
				}

				for(int m=0; m<kids.getLength(); m++){
					Element kid = (Element)kids.item(m);
					String kidName = kid.getLocalName();
					if (kidName.equalsIgnoreCase("Contact")) {
						handleContacts(kid);
					}
					String xrole = getRole(kid);
					
					String xlink = domHelp.getAttributeValue(kid, "@xlink:href");
					if (xlink!=null){
						handleLink(kid, kidName, xlink, xrole);	
					}
					
					out.println("xRole = " + xrole + "<br>");			
				}	
				//out.println("<TD>");
			}
		}
	}
	//***************************************************************************************************
	private void handleURI(String uri){
		//deinition or xlink:href - check to see if urn then print, if url return/print as an <a href>
		String definitionPrefix = uri.substring(0, 3);
		if (definitionPrefix.equals("urn")){
			out.println("<TD>" + uri);
		}
		else if (definitionPrefix.equals("htt")){
			out.println("<TD>" + "<A HREF=" + uri + ">" + uri + "</A>");

		}		
	}
}

