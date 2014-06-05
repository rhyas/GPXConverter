/* 
*  Copyright 2012-2013 Coronastreet Networks 
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTextArea;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class RideWithGPS {

	// My Developer Key. If you Fork, please get your own. (:
	private String apiKey = "mj8sn128";
	private String apiVersion = "1";
	private String URL = "http://ridewithgps.com/trips.json";
	
	private String email;
	private String password;
	private String tripName;
	private String description;
	private JSONArray ridePoints;
	private String activityType;
	private String rideStartTime;
	
	private JTextArea statusTextArea;
	private List<Trkpt> trackPoints;
	
	public RideWithGPS() {
		ridePoints = new JSONArray();
	}
	
	public boolean processData() {
		boolean success = false;
		int trkCounter = 0;
		Iterator<Trkpt> it = trackPoints.iterator();
		while(it.hasNext()) {
			Trkpt t = (Trkpt)it.next();
			JSONObject tp = createTrackPointElement(t);
			//dumpNode(tp);
			ridePoints.put(tp);
			trkCounter++;
		}
		log("Added " + trkCounter + " trackpoints to JSON data array");
		//log("ridePoints: \n" + ridePoints.toString(2) + "\n");
		if (trkCounter >= 1) { success = true; }
		return success;
	}
	
	public boolean upload() {
		boolean success = false;
		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(URL);
	    try {
	    	// apikey=<apikey>&version=<version>&email=<email>&password=<password>&trip[name]=<name>&trip[description]=<description>&track_points=<JSON>
	    	List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	    	nvps.add(new BasicNameValuePair("apikey", apiKey));
	    	nvps.add(new BasicNameValuePair("version", apiVersion));
	    	nvps.add(new BasicNameValuePair("email", email));
	    	nvps.add(new BasicNameValuePair("password", password));
	    	nvps.add(new BasicNameValuePair("trip[name]", tripName));
	    	nvps.add(new BasicNameValuePair("trip[description]", tripName));
	    	nvps.add(new BasicNameValuePair("track_points", ridePoints.toString()));
	    	UrlEncodedFormEntity ent = new UrlEncodedFormEntity(nvps, "UTF-8");
	    	//log(ent.getContent());
	    	request.setEntity(ent);
	    	
	    	HttpResponse response = client.execute(request);
	    	
	    	//log(response.getEntity().getContent());
	        if (response.getStatusLine().getStatusCode() == 200) {
	        	log("Upload Successful!");
	        	return success;
	        } else {
	        	log("Response: " + response.getStatusLine().getStatusCode());
	        }
	    } catch (Exception e) {
	    	e.printStackTrace();
		} finally {
            request.releaseConnection();
        }
		return success;
	}
	
	private void dumpNode(JSONObject o) throws JSONException {
		log(o.toString(2));
	}
	
	private JSONObject createTrackPointElement(Trkpt tp){
	  /* From Cullen @ RWGPS...
	   * x: lng
       * y: lat
       * e: ele
       * t: time (seconds since epoch, unix timestamp)
       * d: distance (absolute along track), optional
       * h: heartrate
       * c: cadence
       * T: temperature
	   */
	  JSONObject element = new JSONObject();
	  
	  // Parse the Trackpoint stuff to JSON
	  try {
	    element.put("x", Float.parseFloat(tp.getLon()));
	    element.put("y", Float.parseFloat(tp.getLat()));
	    // convert the GPX Date to Timestamp
	    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	    element.put("t", f.parse(tp.getTime()).getTime()/1000);
	    // It's possible the device doesn't do HR/Cadence/Altitude/Temp. Skip em if they aren't there. 
	    if (tp.getElevation() != null) { element.put("e", Float.parseFloat(tp.getElevation())); }
	    if (tp.getHr() != null) { element.put("h", Integer.parseInt(tp.getHr())); }
	    if (tp.getCad() != null) { element.put("c", Integer.parseInt(tp.getCad())); }
	    if (tp.getTemp() != null) { element.put("T", Double.parseDouble(tp.getTemp())); }
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
	  return element;
	}
	
	private void log(String s) {
		this.statusTextArea.append("RWGPS: " + s + "\n");
		this.statusTextArea.repaint(1);
	}
	
	private void log(InputStream is) {
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line = "";
			while ((line = rd.readLine()) != null) {
				log(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public List<Trkpt> getTrackPoints() {
		return trackPoints;
	}
	
	public void setTrackPoints(List<Trkpt> trackPoints) {
		this.trackPoints = trackPoints;
	}
	
	public JTextArea getStatusTextArea() {
		return statusTextArea;
	}
	
	public void setStatusTextArea(JTextArea statusTextArea) {
		this.statusTextArea = statusTextArea;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getTripName() {
		return tripName;
	}
	
	public void setTripName(String tripName) {
		this.tripName = tripName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}
	
	
}
