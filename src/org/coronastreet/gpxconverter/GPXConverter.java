/* 
*  Copyright 2012-2014 Coronastreet Networks 
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

import java.awt.EventQueue;
import java.util.prefs.Preferences;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class GPXConverter {

	static Logger log = Logger.getLogger(MainWindow.class);
	private static Preferences prefs;
	private static String OS = System.getProperty("os.name").toLowerCase();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		setupLogger();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				Preferences userRoot = Preferences.userRoot();
			    prefs = userRoot.node( "com/coronastreet/gpxconverter/settings" );
			        
				try {
					MainWindow window = new MainWindow();
					window.showWindow();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
	}
        
	public static void setPref(String name, String value) {
		if (value == null) value = "";
		prefs.put(name, value);
	}
	
	public static void setPref(String name, boolean value) { 
		prefs.putBoolean(name, value);
	}
	
	public static boolean getBoolPref(String name) {
		return prefs.getBoolean(name, false);
	}
	
	public static String getPref(String name) {
		return prefs.get(name, "");
	}
	
	public static void delPref(String name) {
		prefs.remove(name);
	}
	
	// Some utility functions for saving files...
	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}
 
	public static boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}
 
	public static boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
	}
	
	static private void setupLogger() {
		ConsoleAppender console = new ConsoleAppender(); //create appender
		//configure the appender
		String PATTERN = "%d [%p|%C{1}] %m%n";
		console.setLayout(new PatternLayout(PATTERN)); 
		console.setThreshold(Level.INFO);
		console.activateOptions();
		//add appender to any Logger (here is root)
		Logger.getRootLogger().addAppender(console);

		//FileAppender fa = new FileAppender();
		//fa.setName("FileLogger");
		//fa.setFile("mylog.log");
		//fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
		//fa.setThreshold(Level.DEBUG);
		//fa.setAppend(true);
		//fa.activateOptions();

		//add appender to any Logger (here is root)
		//Logger.getRootLogger().addAppender(fa)
		//repeat with all other desired appenders
	}

}
