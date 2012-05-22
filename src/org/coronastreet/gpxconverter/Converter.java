package org.coronastreet.gpxconverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.FileOutputStream;
import java.io.File;

import javax.swing.JTextArea;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
	private String outFile;
	
	List trackPoints;
	private String StartTime;
	private JTextArea statusTextArea;
	
	public Converter(){
		//create a list to hold the employee objects
		trackPoints = new ArrayList();
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
		
		// Add the track data we imported to the output document
		addTrackData();
	
		// Spit out the TCX file
		printOutFile();
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
		Iterator it = trackPoints.iterator();
		while(it.hasNext()) {
			Trkpt t = (Trkpt)it.next();
			Element tp = createTrackPointElement(t);
			//dumpNode(tp);
			track.appendChild(tp);
			trkCounter++;
		}
		log("Added " + trkCounter + " trackpoints to the template.\n");
		
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

		//get a nodelist of  elements
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
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
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

}
