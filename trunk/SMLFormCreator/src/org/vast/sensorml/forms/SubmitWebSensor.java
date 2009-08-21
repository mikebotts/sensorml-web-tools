/**
 * 
 */
package org.vast.sensorml.forms;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.vast.sensorML.SMLSerializer;

public class SubmitWebSensor extends HttpServlet {
	public static final long serialVersionUID = 0;
	protected SMLSerializer smlDoc;
	String manufacturer, model, template;
	HttpServletRequest request;
	WebcamInfo myBean;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request != null) {
			myBean = (WebcamInfo) request.getSession()
					.getAttribute("myWebBean");
			System.out.println("fromSubmitWebForm:sensorType = "
					+ myBean.getSensorType());

			this.request = request;
			// smlDoc = new SMLSerializer(response.getOutputStream());

			// HttpServletRequest newReq = HttpServletRequest
			smlDoc = new SMLSerializer(response.getOutputStream());
			
			if (this.getTemplate()!=null){
				smlDoc.setTemplate(this.getTemplate());
				setValues(request);
			}
			finish();
		}
		// dispatch(request, response);
	}

	protected InputStream getTemplate() {
		/**
		 * Ultimately, we will use manufacturerName and model to determine which
		 * template to use
		 **/
		// manufacturer = request.getParameter("ManufacturerName");
		// model = request.getParameter("ModelNumber");

		// if (manufacturer==null)
		// System.out.println("NO MANUFACTURER NAME");
		// else if (manufacturer.equalsIgnoreCase("Axis"))
		// {
		// do test for models etc and select appropriate template file
		template = "Axis213PTZ.xml";
		// }

		InputStream templateFile = SubmitWebSensor.class
				.getResourceAsStream(template);
		return templateFile;
	}

	public void setValues(HttpServletRequest request) {

		// TODO: assign a unique ID for the site ID
		// smlDoc.setID("UAH0076q23");
		smlDoc.setID(myBean.getSiteID());

		smlDoc.setName(myBean.getSiteID());

		smlDoc.setDescription(myBean.getSiteText());
		// System.out.println("Site Text = " +
		// request.getParameter("SiteText"));

		smlDoc.addContactValue(myBean.getFullName(), myBean.getOrganization(),
				myBean.getRole(), myBean.getStreetAddress(), myBean.getCity(),
				myBean.getState(), myBean.getZipCode(), myBean.getCountry(),
				myBean.getPhoneNumber(), myBean.getEmailAddress());

		// set value of identifier with attribute name="modelNumber" and
		// Term attribute definition="urn.ogc.def,property,OGC::modelNumber"
		// to the value given by request.getParameter("ModelNumber")

		// reuse this boolean
		boolean result;

		String value = myBean.getModelNumber();
		if (value != "") {
			result = smlDoc.setIdentifierValue("ModelNumber", value);
			// if identifier not found in template, then add it
			if (!result)
				smlDoc.addIdentifier("ModelNumber",
						"urn:ogc:def:identifier:OGC:modelNumber", value);
		}

		value = myBean.getManufacturerName();
		if (value != "") {
			result = smlDoc.setIdentifierValue("ManufacturerName", value);
			// if identifier not found in template, then add it
			if (!result)
				smlDoc.addIdentifier("ManufacturerName",
						"urn:ogc:def:identifier:OGC:manufacturerName", value);
		}

		value = myBean.getSerialNumber();
		if (value != "") {
			result = smlDoc.setIdentifierValue("SerialNumber", value);
			// if identifier not found in template, then add it
			if (!result)
				smlDoc.addIdentifier("SerialNumber",
						"urn:ogc:def:identifier:OGC:serialNumber", value);
		}

		// Test identifier Adding
		smlDoc.addIdentifier("TestIdentifier",
				"urn:ogc:def:identifier:OGC:junk", "Billy Bob");
		smlDoc.addClassifier("TestClassifier",
				"urn:ogc:def:identifier:OGC:junk", "Jo Bob", "codespaceHere");

		// if (request.getParameter("SensorType")!="")
		// smlDoc.setClassifierValue("SensorType",
		// request.getParameter("SensorType"));

		// smlDoc.setIdentifierValue("Location.x + ",
		// request.getParameter("Location.x + "));

		// smlDoc.setIdentifierValue("Location.y + ",
		// request.getParameter("Location.y + "));

		// smlDoc.setIdentifierValue("Location.z + ",
		// request.getParameter("Location.z + "));
		// add location etc.

	}

	public void finish() {

		try {
			smlDoc.writeResponse();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void dispatch(HttpServletRequest request,
			HttpServletResponse response) {
		System.out.println("Does it make it?");
		RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher("/PrettyView");
		try {
			dispatcher.forward(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ?? return something for display and saving?
	// do what you want once SenosrML is done

}
