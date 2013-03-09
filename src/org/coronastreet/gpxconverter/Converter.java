/* 
*  Copyright 2012 Coronastreet Networks 
*  Licensed under the Apache License, Version 2.0 (the "License"); 
*  you may not use this file except in compliance with the License. 
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0 
*
*  Unless required by applicable law or agreed to in writing, software 
*  distributed under the License is distributed on an "AS IS" BASIS, 
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
*  implied. See the License for the specific language governing 
*  permissions and limitations under the License 
*/
package org.coronastreet.gpxconverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.FileOutputStream;
import java.io.File;
import java.io.StringWriter;

import javax.swing.JTextArea;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;


@SuppressWarnings("deprecation")
public class Converter {

	private Document inDoc;
	private Document outDoc;
	private String inFile;
	private String outFile = "C:\\Temp\\temp.tcx";
	
	List<Trkpt> trackPoints;
	private String StartTime;
	private JTextArea statusTextArea;
	protected String newline = "\n";
	private String authToken;
	private String ActivityType;
	private String ActivityName;
	private String DeviceType;
	
	public Converter(){
		//create a list to hold the employee objects
		trackPoints = new ArrayList<Trkpt>();
	}

	protected void statusLog(String actionDescription) {
        statusTextArea.append(actionDescription + newline);
        statusTextArea.setCaretPosition(statusTextArea.getDocument().getLength());
    }
	
	public void convert (JTextArea txtArea) {
		this.statusTextArea = txtArea;
		
		//parse the GPX file and get the dom object
		loadInFile(inFile);
		
		//load the Tracks from the GPX File
		parseInFile();

		//load the output template
		loadOutFile();
		
		setIdAndStartTime();
		setDeviceType();
		
		// Add the track data we imported to the output document
		addTrackData();
	
		// Spit out the TCX file
		//printOutFile();
		uploadActivity();
		
		
	}
		
	private void printOutFile(){
		try	{
			OutputFormat format = new OutputFormat(outDoc);
			format.setIndenting(true);
			XMLSerializer serializer = new XMLSerializer(new FileOutputStream(new File(outFile)), format);

			log("Writing out TCX file.\n");
			serializer.serialize(outDoc);

		} catch(IOException ie) {
		    ie.printStackTrace();
		}
	}
	
	private String convertDoc() {
        OutputFormat format = new OutputFormat(outDoc);
		format.setIndenting(true);
		StringWriter stringOut = new StringWriter ();
		XMLSerializer serializer = new XMLSerializer(stringOut, format);
	    try {
			serializer.serialize(outDoc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return stringOut.toString();
	}
	
	private void uploadActivity() {
		HttpClient httpClient = new DefaultHttpClient();
	    try {
	        HttpPost request = new HttpPost("http://www.strava.com/api/v2/upload");
	        JSONObject activityObject = new JSONObject();
	        activityObject.put("token", authToken);
	        activityObject.put("type", "TCX");
	        activityObject.put("activity_type", ActivityType);
	        activityObject.put("activity_name", ActivityName);
	        String xmlData = convertDoc();
	        activityObject.put("data", xmlData);
	        
	        StringEntity params = new StringEntity(activityObject.toString());
	        //statusLog("Sending Entity: " + activityObject.toString());
	        request.addHeader("content-type", "application/json");
	        request.setEntity(params);
	        HttpResponse response = httpClient.execute(request);

	        if (response.getStatusLine().getStatusCode() != 200) {
	        	statusLog("Failed to Upload");
	        	HttpEntity entity = response.getEntity();
	        	if (entity != null) {
	        		String output = EntityUtils.toString(entity);
	        		statusLog(output);
	        	}
			}
	 
	        HttpEntity entity = response.getEntity();
	 
			if (entity != null) {
				String output = EntityUtils.toString(entity);
				statusLog(output);
				JSONObject userInfo = new JSONObject(output);
				statusLog("Successful Uploaded. ID is " + userInfo.get("upload_id"));
		    }
			
			
	    }catch (Exception ex) {
	        // handle exception here
	    } finally {
	        httpClient.getConnectionManager().shutdown();
	    }
		
	}
	
	private void addTrackData() {
		// Get the Track element
		Element track = null;
		Element docEle = outDoc.getDocumentElement();

		// GRabbing the track node. in theory, the node list should always return 1
		NodeList nl = docEle.getElementsByTagName("Track");
		if(nl != null && nl.getLength() > 0) {
			track = (Element)nl.item(0);
		}
		
		int trkCounter = 0;
		Iterator<Trkpt> it = trackPoints.iterator();
		while(it.hasNext()) {
			Trkpt t = (Trkpt)it.next();
			Element tp = createTrackPointElement(t);
			//dumpNode(tp);
			track.appendChild(tp);
			trkCounter++;
		}
		log("Added " + trkCounter + " trackpoints to the template.\n");
		
	}
	
	private void setDeviceType() {
		NodeList nl = outDoc.getElementsByTagName("Activity");
		NodeList nl1 = ((Element) nl.item(0)).getElementsByTagName("Creator");
		NodeList nl2 = ((Element) nl1.item(0)).getElementsByTagName("Name");
		if(nl2 != null && nl2.getLength() > 0) {
			Element el = (Element)nl2.item(0);
			el.appendChild(outDoc.createTextNode(DeviceType));
		}
	}
	
	private void setIdAndStartTime() {
		NodeList nl = outDoc.getElementsByTagName("Activity");
		
		
		NodeList nl2 = ((Element) nl.item(0)).getElementsByTagName("Id");
		if(nl2 != null && nl2.getLength() > 0) {
			Element el = (Element)nl2.item(0);
			el.appendChild(outDoc.createTextNode(StartTime));
		}
		
		NodeList nl3 = ((Element) nl.item(0)).getElementsByTagName("Lap");
		if(nl3 != null && nl3.getLength() > 0) {
			Element el = (Element)nl3.item(0);
			el.setAttribute("StartTime", StartTime);
		}
		
	}
	
	private Element createTrackPointElement(Trkpt tp){

		Element eTrackpoint = outDoc.createElement("Trackpoint");
		
		//create time element and time text node and attach it to the trackpoint
		Element eTime = outDoc.createElement("Time");
		eTime.appendChild(outDoc.createTextNode(tp.getTime()));
		eTrackpoint.appendChild(eTime);
		
		//create elevation element and elevation text node and attach it to the trackpoint
		Element eElevation = outDoc.createElement("AltitudeMeters");
		eElevation.appendChild(outDoc.createTextNode(tp.getElevation()));
		eTrackpoint.appendChild(eElevation);

		//create Speed Sensor element and attach it to the Trackpoint
		Element eSensorState = outDoc.createElement("SensorState");
		eSensorState.appendChild(outDoc.createTextNode("Absent"));
		eTrackpoint.appendChild(eSensorState);
		
		// Create Lat/Long and add them to Position
		Element ePosition = outDoc.createElement("Position");
		Element eLatitudeDegrees = outDoc.createElement("LatitudeDegrees");
		eLatitudeDegrees.appendChild(outDoc.createTextNode(tp.getLat()));
		Element eLongitudeDegrees = outDoc.createElement("LongitudeDegrees");
		eLongitudeDegrees.appendChild(outDoc.createTextNode(tp.getLon()));
		ePosition.appendChild(eLongitudeDegrees);
		ePosition.appendChild(eLatitudeDegrees);
		eTrackpoint.appendChild(ePosition);
		
		//create HeartRate and add it to the Trackpoint
		Element eHR = outDoc.createElement("HeartRateBpm");
		eHR.setAttribute("xsi:type", "HeartRateInBeatsPerMinute_t");
		Element eHRValue = outDoc.createElement("Value");
		eHRValue.appendChild(outDoc.createTextNode(tp.getHr()));
		eHR.appendChild(eHRValue);
		eTrackpoint.appendChild(eHR);
		
		//create Cadence element text node and add it to the Trackpoint
		Element eCad = outDoc.createElement("Cadence");
		eCad.appendChild(outDoc.createTextNode(tp.getCad()));
		eTrackpoint.appendChild(eCad);
		
		return eTrackpoint;

	}
	
	private void dumpNode(Element e) {
		Document document = e.getOwnerDocument();
		DOMImplementationLS domImplLS = (DOMImplementationLS) document
		    .getImplementation();
		LSSerializer serializer = domImplLS.createLSSerializer();
		String str = serializer.writeToString(e);
		log("XML::: " + str);
	}
	
	private void parseInFile(){
		log("Parsing Input File...\n");
		//get the root element
		Element docEle = (Element) inDoc.getDocumentElement();

		//get a node list of  elements
		NodeList nl = docEle.getElementsByTagName("trkpt");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {

				//get the Trackpoint element
				Element el = (Element)nl.item(i);
				
				Trkpt tp = new Trkpt();

				//get the Trackpoint object
				tp.setTime(getTextValue(el, "time"));
				tp.setLon(el.getAttribute("lon"));
				tp.setLat(el.getAttribute("lat"));
				tp.setElevation(getTextValue(el, "ele"));
				tp.setHr(getTextValue(el, "gpxtpx:hr"));
				tp.setCad(getTextValue(el, "gpxtpx:cad"));
				
				//tp.dump();
				
				//add it to list
				trackPoints.add(tp);
			}
		}
		log("Imported " + trackPoints.size() + " trackpoints.\n");
		
		// set StartTime
		NodeList ml = docEle.getElementsByTagName("metadata");
		StartTime = getTextValue((Element)ml.item(0), "time");
		log("Importing start time as " + StartTime + "\n");
		
		
	}
	
	private void log(String s) {
		this.statusTextArea.append(s);
		this.statusTextArea.repaint();
	}
	
	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue(); 
		} 

		return textVal;
	}
	
	private void loadInFile(String file){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			inDoc = db.parse(file);
			
		}catch(ParserConfigurationException pce) {
			log(pce.getMessage());
		}catch(SAXException se) {
			log(se.getMessage());
		}catch(IOException ioe) {
			log(ioe.getMessage());
		}
		
	}

	private void loadOutFile(){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			log("Loading TCX template file.\n");			
			outDoc = db.parse(this.getClass().getResourceAsStream("/org/coronastreet/gpxconverter/tcxtemplate.xml"));
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
	}
	
	public String getInFile() {
		return inFile;
	}

	public void setInFile(String inFile) {
		this.inFile = inFile;
	}

	public String getOutFile() {
		return outFile;
	}

	public void setOutFile(String outFile) {
		this.outFile = outFile;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getActivityType() {
		return ActivityType;
	}

	public void setActivityType(String activityType) {
		ActivityType = activityType;
	}

	public String getActivityName() {
		return ActivityName;
	}

	public void setActivityName(String activityName) {
		ActivityName = activityName;
	}

	public String getDeviceType() {
		return DeviceType;
	}

	public void setDeviceType(String deviceType) {
		DeviceType = deviceType;
	}

}
