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
