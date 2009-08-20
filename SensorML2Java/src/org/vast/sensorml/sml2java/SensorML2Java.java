/***************************** BEGIN LICENSE BLOCK ***************************

The contents of this file are subject to the Mozilla Public License Version
1.1 (the "License"); you may not use this file except in compliance with
the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/MPL-1.1.html

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
for the specific language governing rights and limitations under the License.

The Original Code is the "SensorML DataProcessing Engine".

The Initial Developer of the Original Code is the
University of Alabama in Huntsville (UAH).
Portions created by the Initial Developer are Copyright (C) 2006
the Initial Developer. All Rights Reserved.

Contributor(s): 
   Dr. Mike Botts <mike.botts@uah.edu>

 ******************************* END LICENSE BLOCK ***************************/

package org.vast.sensorml.sml2java;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.vast.ogc.OGCRegistry;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * <p>
 * <b>Title:SensorML2Java</b><br/>
 * 
 * </p>
 * <p>
 * <b>Description:</b><br/>
 * Creates a java template class conforming to the UAH SensorML Execution Engine
 * API from a SensorML ProcessModel or Component instance. A straightforward but
 * not very elegant approach
 * 
 * </p>
 * <p>
 * Copyright (c) 2008
 * </p>
 * 
 * @author Dr. Mike Botts (University of Alabama in Huntsville)
 * @date April 3, 2009
 * @version 1.1
 * 
 */

public class SensorML2Java {

	PrintWriter writer;
	Document dom;
	DOMHelper domHelp = null;
	String smlNamespace;
	String sweNamespace;
	String gmlNamespace;
	String icNamespace;
	Element root;
	String processName = "NoNameProcess";
	HttpServletResponse response;
	HttpServletRequest request;

	public SensorML2Java(HttpServletRequest request,
			HttpServletResponse response) {

		this.response = response;
		this.request = request;

		// create output stream writer
		try {
			response.setContentType("text/plain");
			writer = response.getWriter();
			writer.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace(System.err);
		}

		ServletFileUpload upload = new ServletFileUpload();
		if (!upload.isMultipartContent(request)) {
			String newURL = request.getParameter("url");
			processName = request.getParameter("processName");
			try {
				// domHelp = new DOMHelper(newURL, true);
				URL url = new URL(newURL);
				domHelp = new DOMHelper(url.openStream(), false);

				if (domHelp == null) {
					System.out.println("domHelper equals null!!");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dom = null;
			dom = (Document) domHelp.getDocument();
			createJava();
		} else {
			// Parse the request
			FileItemIterator iter = null;
			try {
				iter = upload.getItemIterator(request);
			} catch (FileUploadException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			} catch (IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			try {
				while (iter.hasNext()) {
					FileItemStream item = iter.next();
					// String name = item.getFieldName();
					InputStream stream = item.openStream();
					if (item.isFormField()) {
						// The asString closes the steam
						processName = Streams.asString(stream);
					}
					// parameters.put(name, Streams.asString(stream));
					else {
						if (item.getName() != null
								&& item.getName().length() > 0) {
							InputStream fileStream = stream;
							if (fileStream != null) {
								try {
									domHelp = new DOMHelper(fileStream, false);
									if (domHelp == null) {
										System.out
												.println("domHelper equals null!!");
									}
								} catch (DOMHelperException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								dom = null;
								dom = (Document) domHelp.getDocument();
								createJava();
							} else {
								System.out.println("Filestream is null!!");
								return;
							}
						}
					}

				}
			} catch (FileUploadException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

		}

		// TODO: REMOVE DEBUGGING
		/*
		 * if (processName==null) { processName="UnknownProcess"; }
		 */

		/*
		 * else if (processName.equalsIgnoreCase(""))
		 * processName="UnknownProcess";
		 */

		// else
		// writer.println("Process Name = " + processName + "\n");

		// Check to see if there is a parameter called "url"
		// in the request
		/*
		 * String newURL = request.getParameter("url"); String fileName =
		 * request.getParameter("uploadfile"); //writer.println("url = " +
		 * newURL + "\n");
		 * 
		 * 
		 * // If no "url" parameter, then this is coming from a file // use the
		 * parseStream method if (newURL==null){
		 * 
		 * InputStream filestream = parseRequest(request); if (filestream !=
		 * null) { try { domHelp = new DOMHelper(filestream, false); if (domHelp
		 * == null) { System.out.println("domHelper equals null!!"); } } catch
		 * (DOMHelperException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } dom = null; dom = (Document)
		 * domHelp.getDocument(); } else{
		 * System.out.println("Filestream is null!!"); return; } } // if there
		 * is a "url" parameter, this came as a get request // pass the URL to
		 * DomHelp and let it handle it else{ //writer.println("newURL = " +
		 * newURL + "\n");
		 * 
		 * try { //domHelp = new DOMHelper(newURL, true); URL url = new
		 * URL(newURL); domHelp = new DOMHelper(url.openStream(),false);
		 * 
		 * if (domHelp == null) { System.out.println("domHelper equals null!!");
		 * } } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } dom = null; dom = (Document)
		 * domHelp.getDocument(); }
		 * 
		 * createJava();
		 */
	}

	private void createJava() {

		Element rootElement = (Element) dom.getDocumentElement();
		setVersion(rootElement);

		// If document start with with SensorML then check out its member
		// components
		if (rootElement.getLocalName() == "SensorML") {

			NodeList member = domHelp.getElements(rootElement, "sml:member");
			for (int t = 0; t < member.getLength(); t++) {
				Element firstMember = (Element) member.item(t);
				Element component = domHelp.getFirstChildElement(firstMember);

				// check out member element type
				root = handleElements(component);
			}
		} else {
			// see if the document starts with one of the SensorML element types
			root = handleElements(rootElement);
		}

		// Send error message and return if no Components or ProcessModels found
		if (root == null) {
			writer
					.println("ERROR: no recognized ProcessModel or Component element found");
			writer.close();
			return;
		}

		// add blocks to writer
		initialize();
		addCommentBlock();
		addDeclarations();
		addInitMethod();
		addExecuteMethod();

		writer.flush();
		writer.close();
	}

	// check for either Component or ProcessModel
	private Element handleElements(Element component) {
		if (component.getLocalName() == "ProcessModel") {
			return component;
		} else if (component.getLocalName() == "Component") {
			return component;
		} else {
			return null;
		}
	}

	protected void initialize() {

		writer.println();
		writer.println("//change to your package location");
		writer.println("package org.sensorML.process;");
		writer.println();
		writer.println("import org.vast.data.*;");
		writer.println("import org.vast.process.*;");
		writer.println();
		writer.println();
	}

	protected void addCommentBlock() {

		writer.println("/**");
		writer.println("* <p><b>Title:  </b><br/></p>");
		writer.println("*");
		writer.println("* <p><b>Description:</b><br/>");
		writer.println("* </p>");
		writer.println("* @author ");
		writer.println("* @date  ");
		writer.println("* @version ");
		writer.println("*/");
		writer.println();
		writer.println();
	}

	protected void addDeclarations() {
		// first start class declaration
		writer.println();
		writer
				.println("public class " + processName
						+ " extends DataProcess {");
		writer.println();

		// write out all data declarations
		// inputs
		writer.println();
		writer.println("   // declare input components");
		NodeList inputList = domHelp
				.getElements(root, "inputs/InputList/input");
		for (int i = 0; i < inputList.getLength(); i++) {
			String name = domHelp.getAttributeValue(
					(Element) inputList.item(i), "name");
			declare((Element) inputList.item(i), name);
		}
		// outputs
		writer.println();
		writer.println("   // declare output components");
		NodeList outputList = domHelp.getElements(root,
				"outputs/OutputList/output");
		for (int i = 0; i < outputList.getLength(); i++) {
			String name = domHelp.getAttributeValue((Element) outputList
					.item(i), "name");
			declare((Element) outputList.item(i), name);
		}

		// parameters
		writer.println();
		writer.println("   // declare parameters components");
		NodeList parameterList = domHelp.getElements(root,
				"parameters/ParameterList/parameter");
		for (int i = 0; i < parameterList.getLength(); i++) {
			String name = domHelp.getAttributeValue((Element) parameterList
					.item(i), "name");
			declare((Element) parameterList.item(i), name);
		}

		writer.println();
		writer.println("   // declare any other class variables needed");
		writer.println();

		// add constructor
		writer.println();
		writer.println("   public " + processName + "() {");
		writer.println("   }");
		writer.println();

	}

	protected void declare(Element element, String name) {

		Element firstChild = domHelp.getFirstChildElement(element);

		// check if basic parameter (e.g. Quantity, Time, Boolean, etc.)
		// if so declare as DataValue
		if (isBasicType(firstChild)) {
			writer.println("   DataValue " + name + ";");
		}

		// check if Data Array; if so recurse through array components
		// and create a unique cumulative name
		else if (domHelp.existElement(firstChild, "swe:elementCount")) {
			writer.println("   DataArray " + name + ";");
			NodeList components = domHelp.getAllChildElements(firstChild);
			for (int i = 0; i < components.getLength(); i++) {
				// recurse through children and create unique cumulative name
				Element childElement = (Element) components.item(i);
				declare(childElement, name + "_"
						+ domHelp.getAttributeValue(childElement, "name"));
			}
		}

		// assume DataGroup or other composite type and recurse through
		// component
		// no need to declare DataGroup here
		else {
			NodeList components = domHelp.getAllChildElements(firstChild);
			for (int i = 0; i < components.getLength(); i++) {
				// recurse through children and create unique cumulative name
				Element childElement = (Element) components.item(i);
				declare(childElement, name + "_"
						+ domHelp.getAttributeValue(childElement, "name"));
			}
		}
	}

	protected void addInitMethod() {

		writer.println();
		writer.println("   /**");
		writer.println("   * Initializes the process");
		writer.println("   * Get handles to input/output components");
		writer.println("   */");
		writer.println("   public void init() throws ProcessException {");
		writer.println();
		writer.println("      try {");

		// get handles for all data components
		// inputs
		writer.println();
		writer.println("         // initialize input components");
		NodeList inputList = domHelp
				.getElements(root, "inputs/InputList/input");
		for (int i = 0; i < inputList.getLength(); i++) {
			String name = domHelp.getAttributeValue(
					(Element) inputList.item(i), "@name");
			initialize((Element) inputList.item(i), name, "inputData");
		}
		// outputs
		writer.println();
		writer.println("         // initialize output components");
		NodeList outputList = domHelp.getElements(root,
				"outputs/OutputList/output");
		for (int i = 0; i < outputList.getLength(); i++) {
			String name = domHelp.getAttributeValue((Element) outputList
					.item(i), "@name");
			initialize((Element) outputList.item(i), name, "outputData");
		}

		// parameters
		writer.println();
		writer.println("         // initialize parameter components");
		NodeList parameterList = domHelp.getElements(root,
				"parameters/ParameterList/parameter");
		for (int i = 0; i < parameterList.getLength(); i++) {
			String name = domHelp.getAttributeValue((Element) parameterList
					.item(i), "@name");
			initialize((Element) parameterList.item(i), name, "paramData");
		}

		writer.println();
		writer.println("      }");
		writer.println("      catch (ClassCastException e) {");
		writer
				.println("         throw new ProcessException(\"Invalid I/O data\", e);");
		writer.println("      }");

		writer.println();
		writer.println("      // initialize any class variables needed");
		writer.println();
		writer.println("   }");
	}

	protected void initialize(Element element, String name, String typeName) {
		Element firstChild = domHelp.getFirstChildElement(element);

		// check if basis parameter (e.g. Quantity, Time, Boolean, etc.)
		// if so get handle of DataValue
		if (isBasicType(firstChild)) {
			String childName = domHelp.getAttributeValue(element, "@name");
			writer.print("         " + name + " = (DataValue) ");
			writer.println(typeName + ".getComponent(\"" + childName + "\");");
		}
		// check if Data Array; if so recurse through array components
		// and create a unique cumulative name
		else if (domHelp.existAttribute(firstChild, "elementCount")) {
			String childName = domHelp.getAttributeValue(element, "@name");
			writer.print("         " + name + " = (DataArray) ");
			writer.println(typeName + ".getComponent(\"" + childName + "\");");
			NodeList components = domHelp.getAllChildElements(firstChild);
			for (int i = 0; i < components.getLength(); i++) {
				// recurse through children and create unique cumulative name
				Element childElement = (Element) components.item(i);
				String newName = new String(name + "_"
						+ domHelp.getAttributeValue(childElement, "name"));
				initialize(childElement, newName, name);
			}

			// DataGroup outputPosition = (DataGroup)
			// outputData.getComponent("ECI_location");
		}
		// assume DataGroup and recurse through component
		else {
			String childName = domHelp.getAttributeValue(element, "@name");
			writer.print("         DataGroup " + name + " = (DataGroup) ");
			writer.println(typeName + ".getComponent(\"" + childName + "\");");
			NodeList components = domHelp.getAllChildElements(firstChild);
			for (int i = 0; i < components.getLength(); i++) {
				// recurse through children and create unique cumulative name
				Element childElement = (Element) components.item(i);
				String newName = new String(name + "_"
						+ domHelp.getAttributeValue(childElement, "name"));
				initialize(childElement, newName, name);
			}
		}
	}

	protected boolean isBasicType(Element element) {
		String componentType = element.getLocalName();

		if (componentType.equalsIgnoreCase("Quantity"))
			return true;
		else if (componentType.equalsIgnoreCase("Boolean"))
			return true;
		else if (componentType.equalsIgnoreCase("Category"))
			return true;
		else if (componentType.equalsIgnoreCase("Count"))
			return true;
		else if (componentType.equalsIgnoreCase("Time"))
			return true;
		else
			return false;
	}

	protected void addExecuteMethod() {

		writer.println();
		writer.println("   /**");
		writer.println("   * Executes the process");
		writer
				.println("   * Get current values for all components and then executes");
		writer.println("   */");
		writer.println("   public void execute() throws ProcessException {");
		writer.println();

		// get handles for all data components
		// inputs
		writer.println();
		writer.println("         // get values for input components");
		writer
				.println("         // note: you can rename these variable names to match your code");
		NodeList inputList = domHelp
				.getElements(root, "inputs/InputList/input");
		for (int i = 0; i < inputList.getLength(); i++) {
			String name = domHelp.getAttributeValue(
					(Element) inputList.item(i), "name");
			execution((Element) inputList.item(i), name, 0);
		}

		// parameters
		writer.println();
		writer.println("         // get values for parameter components");
		writer
				.println("         // note: you can rename these variable names to match your code");
		NodeList parameterList = domHelp.getElements(root,
				"parameters/ParameterList/parameter");
		for (int i = 0; i < parameterList.getLength(); i++) {
			String name = domHelp.getAttributeValue((Element) parameterList
					.item(i), "name");
			execution((Element) parameterList.item(i), name, 0);
		}

		// output value - re-initialize to 0 or default
		writer.println();
		writer
				.println("         // re-initialize values for output components to zero or default");
		writer
				.println("         // note: you can rename these variable names to match your code");
		NodeList outputList = domHelp.getElements(root,
				"outputs/OutputList/output");
		for (int i = 0; i < outputList.getLength(); i++) {
			String name = domHelp.getAttributeValue((Element) outputList
					.item(i), "name");
			execution((Element) outputList.item(i), name, 2); // re-initialize
		}

		writer.println();

		writer.println();
		writer.println("         /****************************************");
		writer.println("          *    PUT YOUR EXECUTION CODE HERE      *");
		writer.println("          ****************************************/");
		writer.println();
		writer.println();

		// set up outputs to receive values
		writer.println();
		writer.println("         // set values for output components");
		writer
				.println("         // note: you can rename these variable names to match your code");
		// NodeList outputList =
		// dom.getElements(root,"outputs/OutputList/output");
		for (int i = 0; i < outputList.getLength(); i++) {
			String name = domHelp.getAttributeValue((Element) outputList
					.item(i), "name");
			execution((Element) outputList.item(i), name, 1);
		}

		// close the method and the file
		writer.println("   }");
		writer.println("}");
	}

	// type indicates if input or output
	protected void execution(Element element, String name, int type) {

		Element firstChild = domHelp.getFirstChildElement(element);

		// check if basis parameter (e.g. Quantity, Time, Boolean, etc.)
		// if so get handle of DataValue
		if (isBasicType(firstChild)) {
			// writer.print("         " + name + " = (DataValue) ");
			// writer.println(typeName + ".getComponent(\"" + name + "\");");
			String componentType = firstChild.getLocalName();

			if (componentType.equalsIgnoreCase("Quantity")) {
				if (type == 0) { // inputs
					writer.print("         double " + name + "_value = ");
					writer.println(name + ".getData().getDoubleValue();");
				} else if (type == 1) { // output set value
					writer.print("         " + name
							+ ".getData().setDoubleValue(");
					writer.println(name + "_value);");
				} else if (type == 2) { // output initialize value
					writer.print("         double " + name + "_value = ");
					writer.println(name + ".getData().getDoubleValue();");
					writer.println("         " + name + "_value = 0;");
				}
			} else if (componentType.equalsIgnoreCase("Boolean")) {
				if (type == 0) { // inputs
					writer.print("         boolean " + name + "_value = ");
					writer.println(name + ".getData().getBooleanValue();");
				} else if (type == 1) { // output set value
					writer.print("         " + name
							+ ".getData().setBooleanValue(");
					writer.println(name + "_value);");
				} else if (type == 2) { // output initialize value
					writer.print("         boolean " + name + "_value = ");
					writer.println(name + ".getData().getBooleanValue();");
					writer.println("         " + name + "_value = false;");
				}
			} else if (componentType.equalsIgnoreCase("Category")) {
				if (type == 0) { // inputs
					writer.print("         String " + name + "_value = ");
					writer.println(name + ".getData().getStringValue();");
				} else if (type == 1) { // output set value
					writer.print("         " + name
							+ ".getData().setStringValue(");
					writer.println(name + "_value);");
				} else if (type == 2) { // output initialize value
					writer.print("         String " + name + "_value = ");
					writer.println(name + ".getData().getStringValue();");
					writer.println("         " + name + "_value = \"\";");
				}
			} else if (componentType.equalsIgnoreCase("Count")) {
				if (type == 0) { // inputs
					writer.print("         int " + name + "_value = ");
					writer.println(name + ".getData().getIntValue();");
				} else if (type == 1) { // output set value
					writer
							.print("         " + name
									+ ".getData().setIntValue(");
					writer.println(name + "_value);");
				} else if (type == 2) { // output initialize value
					writer.print("         int " + name + "_value = ");
					writer.println(name + ".getData().getIntValue();");
					writer.println("         " + name + "_value = 0;");
				}
			} else if (componentType.equalsIgnoreCase("Time")) {
				if (type == 0) { // inputs
					writer.print("         double " + name + "_value = ");
					writer.println(name + ".getData().getDoubleValue();");
				} else if (type == 1) { // output set value
					writer.print("         " + name
							+ ".getData().setDoubleValue(");
					writer.println(name + "_value);");
				} else if (type == 2) { // output initialize value
					writer.print("         double " + name + "_value = ");
					writer.println(name + ".getData().getDoubleValue();");
					writer.println("         " + name + "_value = 0;");
				}
			}
		}
		// check if Data Array; if so recurse through array components
		// and create a unique cumulative name
		else if (domHelp.existAttribute(firstChild, "elementCount")) {
			NodeList components = domHelp.getAllChildElements(firstChild);
			for (int i = 0; i < components.getLength(); i++) {
				// recurse through children and create unique cumulative name
				Element childElement = (Element) components.item(i);
				String newName = new String(name + "_"
						+ domHelp.getAttributeValue(childElement, "name"));
				execution(childElement, newName, type);
			}

			// DataGroup outputPosition = (DataGroup)
			// outputData.getComponent("ECI_location");
		}
		// assume DataGroup and recurse through component
		else {
			NodeList components = domHelp.getAllChildElements(firstChild);
			for (int i = 0; i < components.getLength(); i++) {
				// recurse through children and create unique cumulative name
				Element childElement = (Element) components.item(i);
				String newName = new String(name + "_"
						+ domHelp.getAttributeValue(childElement, "name"));
				execution(childElement, newName, type);
			}
		}
	}

	// *******************************************************************************************
	// set schema version number and set namespaces accordingly
	private void setVersion(Element root) {
		String version = domHelp.getAttributeValue(root, "@version");

		// if there is no version on root (e.g. document starts with
		// ProcessModel)
		// then set it to 1.0.1
		// TODO: do a better fix, look at namespace designations
		if (version == null)
			version = "1.0.1";

		if (version.equalsIgnoreCase("1.0")) {
			System.out.println("Version = " + version + "(1.0)");
			smlNamespace = "http://www.opengis.net/sensorML/1.0";
			sweNamespace = "http://www.opengis.net/swe/1.0";
			gmlNamespace = "http://www.opengis.net/gml";
			icNamespace = "urn:us:gov:ic:ism:v2";

			domHelp.addUserPrefix("gml", OGCRegistry
					.getNamespaceURI(OGCRegistry.GML));
			domHelp.addUserPrefix("swe", OGCRegistry.getNamespaceURI("SWE",
					"1.0"));
			domHelp.addUserPrefix("sml", OGCRegistry.getNamespaceURI("SML",
					"1.0"));
			domHelp.addUserPrefix("xlink", OGCRegistry.getNamespaceURI(
					OGCRegistry.XLINK, null));
			domHelp.addUserPrefix("ism", OGCRegistry.getNamespaceURI("IC"));

		} else if (version.equalsIgnoreCase("1.0.1")) {
			System.out.println("Version = " + version + "(1.0.1)");
			smlNamespace = "http://www.opengis.net/sensorML/1.0.1";
			sweNamespace = "http://www.opengis.net/swe/1.0.1";
			gmlNamespace = "http://www.opengis.net/gml";
			icNamespace = "urn:us:gov:ic:ism:v2";

			domHelp.addUserPrefix("gml", OGCRegistry
					.getNamespaceURI(OGCRegistry.GML));
			domHelp.addUserPrefix("swe", OGCRegistry.getNamespaceURI("SWE",
					"1.0.1"));
			domHelp.addUserPrefix("sml", OGCRegistry.getNamespaceURI("SML",
					"1.0.1"));
			domHelp.addUserPrefix("xlink", OGCRegistry.getNamespaceURI(
					OGCRegistry.XLINK, null));
			domHelp.addUserPrefix("ism", OGCRegistry.getNamespaceURI("IC"));
		}
	}

	// ***********************************************************************************************************************
	private InputStream parseRequest(HttpServletRequest request) {
		// int BOF = 0, EOF = 0;
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

			InputStream filestream = new StringBufferInputStream(fileData
					.substring(startPos, endPos));

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
}
