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

public class Trkpt {

	private String elevation;
	private String lon;
	private String lat;
	private String hr;
	private String cad;
	private String time;
	
	public void dump() {
		System.out.println("Trackpoint Object:");
		System.out.println("\tLat = " + lat);
		System.out.println("\tLon = " + lon);
		System.out.println("\tTime = " + time);
		System.out.println("\tHeartRate = " + hr);
		System.out.println("\tCadence = " + cad);
		System.out.println("\tElevation = " + elevation);
		System.out.println("");
	}
	
	public String getElevation() {
		return elevation;
	}
	public void setElevation(String elevation) {
		this.elevation = elevation;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getHr() {
		return hr;
	}
	public void setHr(String hr) {
		this.hr = hr;
	}
	public String getCad() {
		return cad;
	}
	public void setCad(String cad) {
		this.cad = cad;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
