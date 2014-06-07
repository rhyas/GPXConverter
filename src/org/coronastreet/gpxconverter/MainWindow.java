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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;

import org.apache.log4j.Logger;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class MainWindow {
	
	static Logger log = Logger.getLogger(MainWindow.class);
	private JFrame frmStravaGpxConverter;
	private JTextField txtSourceFile;
	private JFileChooser fc;
	private JTextArea statusTextArea;
	protected String newline = "\n";
	private JTextField txtActivityName;
	private String authToken;
	private static AccountManager accounts;
	

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
		accounts = new AccountManager(this);
	}
	
	public void showWindow() {
		frmStravaGpxConverter.setVisible(true);
	}
	
	protected void statusLog(String actionDescription) {
        statusTextArea.append(actionDescription + newline);
        statusTextArea.setCaretPosition(statusTextArea.getDocument().getLength()-1);
    }
	

	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		  
		frmStravaGpxConverter = new JFrame();
		frmStravaGpxConverter.getContentPane().setFont(new Font("Tahoma", Font.BOLD, 11));
		frmStravaGpxConverter.setTitle("GPX Importer for Strava & RideWithGPS");
		frmStravaGpxConverter.setBounds(100, 100, 500, 400);
		frmStravaGpxConverter.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmStravaGpxConverter.getContentPane().setLayout(null);
		
		
		fc = new JFileChooser(GPXConverter.getPref("LastLocation"));
		
		JLabel lblThisToolConverts = new JLabel("Upload Ride Data from a GPX file with HR and Cadence data.");
		lblThisToolConverts.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblThisToolConverts.setHorizontalAlignment(SwingConstants.CENTER);
		lblThisToolConverts.setBounds(32, 11, 405, 14);
		frmStravaGpxConverter.getContentPane().add(lblThisToolConverts);
		
		txtSourceFile = new JTextField();
		txtSourceFile.setBounds(46, 36, 286, 20);
		frmStravaGpxConverter.getContentPane().add(txtSourceFile);
		txtSourceFile.setColumns(10);
		
		statusTextArea = new JTextArea();
		statusTextArea.setEditable(false);
		statusTextArea.setColumns(100);
		statusTextArea.setRows(100);
		statusTextArea.setLineWrap(true);
		JScrollPane statusScroller = new JScrollPane(statusTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		statusScroller.setBounds(10, 242, 464, 109);
		frmStravaGpxConverter.getContentPane().add(statusScroller);
		
		JLabel lblSourceGpxFile = new JLabel("Source GPX File");
		lblSourceGpxFile.setBounds(46, 57, 111, 14);
		frmStravaGpxConverter.getContentPane().add(lblSourceGpxFile);
		
		JButton btnNewButton = new JButton("Find Src");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc.setFileFilter(new GPXFilter());
				int returnVal = fc.showDialog(frmStravaGpxConverter, "Choose Source");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					txtSourceFile.setText(fc.getSelectedFile().getPath());
					File f = new File("");
					fc.setSelectedFile(f);
					GPXConverter.setPref("LastLocation", fc.getCurrentDirectory().getPath());
				}
			}
		});
		
		btnNewButton.setBounds(342, 34, 95, 23);
		frmStravaGpxConverter.getContentPane().add(btnNewButton);
		
		JRadioButton typeIsRide = new JRadioButton("Ride");
		typeIsRide.setBounds(132, 135, 57, 23);
		frmStravaGpxConverter.getContentPane().add(typeIsRide);
		if (GPXConverter.getPref("lastType").equals("Ride")) { typeIsRide.setSelected(true); }
		
		JRadioButton typeIsRun = new JRadioButton("Run");
		typeIsRun.setBounds(191, 135, 57, 23);
		frmStravaGpxConverter.getContentPane().add(typeIsRun);
		if (GPXConverter.getPref("lastType").equals("Run")) { typeIsRun.setSelected(true); }
		
		JRadioButton typeIsHike = new JRadioButton("Hike");
		typeIsHike.setBounds(250, 135, 71, 23);
		frmStravaGpxConverter.getContentPane().add(typeIsHike);
		if (GPXConverter.getPref("lastType").equals("Hike")) { typeIsHike.setSelected(true); }
		
		ButtonGroup eventType = new ButtonGroup();
		eventType.add(typeIsHike);
		eventType.add(typeIsRun);
		eventType.add(typeIsRide);
		
		typeIsHike.addActionListener(new TypeAction());
		typeIsRun.addActionListener(new TypeAction());
		typeIsRide.addActionListener(new TypeAction());
		
		JLabel lblAltimeter = new JLabel("Altimeter:");
		lblAltimeter.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAltimeter.setBounds(32, 162, 86, 14);
		frmStravaGpxConverter.getContentPane().add(lblAltimeter);
		
		JRadioButton altYes = new JRadioButton("Yes");
		altYes.setBounds(132, 157, 57, 23);
		frmStravaGpxConverter.getContentPane().add(altYes);
		if (GPXConverter.getPref("lastAltimeter").equals("Yes")) { altYes.setSelected(true); }
		
		JRadioButton altNo = new JRadioButton("No");
		altNo.setBounds(191, 157, 57, 23);
		frmStravaGpxConverter.getContentPane().add(altNo);
		if (GPXConverter.getPref("lastAltimeter").equals("No")) { altNo.setSelected(true); }
		
		ButtonGroup altimeterAvail = new ButtonGroup();
		altimeterAvail.add(altYes);
		altimeterAvail.add(altNo);
		
		altYes.addActionListener(new DeviceAction());
		altNo.addActionListener(new DeviceAction());
		
		JLabel lblActivityType = new JLabel("Activity Type:");
		lblActivityType.setHorizontalAlignment(SwingConstants.RIGHT);
		lblActivityType.setBounds(32, 140, 86, 14);
		frmStravaGpxConverter.getContentPane().add(lblActivityType);
		
		JLabel lblActivityName = new JLabel("Activity Name:");
		lblActivityName.setHorizontalAlignment(SwingConstants.RIGHT);
		lblActivityName.setBounds(32, 109, 86, 14);
		frmStravaGpxConverter.getContentPane().add(lblActivityName);
		
		txtActivityName = new JTextField();
		txtActivityName.setBounds(122, 108, 315, 20);
		frmStravaGpxConverter.getContentPane().add(txtActivityName);
		txtActivityName.setColumns(10);
		
		JLabel lblUploadTo = new JLabel("Upload To:");
		lblUploadTo.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUploadTo.setBounds(32, 183, 86, 14);
		frmStravaGpxConverter.getContentPane().add(lblUploadTo);
		
		final JCheckBox chckbxStrava = new JCheckBox("Strava");
		chckbxStrava.setBounds(132, 178, 64, 23);
		frmStravaGpxConverter.getContentPane().add(chckbxStrava);
		
		final JCheckBox chckbxRidewithgps = new JCheckBox("RideWithGPS");
		chckbxRidewithgps.setBounds(201, 179, 112, 23);
		frmStravaGpxConverter.getContentPane().add(chckbxRidewithgps);
		
		final JCheckBox chckbxGarmin = new JCheckBox("Garmin Connect");
		chckbxGarmin.setBounds(315, 179, 122, 23);
		frmStravaGpxConverter.getContentPane().add(chckbxGarmin);
		
		final JButton btnConvertIt = new JButton("Upload Data");
		btnConvertIt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Converter c = new Converter();
				c.setInFile(txtSourceFile.getText());
				c.setAuthToken(authToken);
				c.setActivityName(txtActivityName.getText());
				c.setActivityType(GPXConverter.getPref("lastType"));
				c.setStatusTextArea(statusTextArea);
				//c.setPassword(new String(passwordVal.getPassword()));
				//c.setEmail(loginVal.getText());
				if (GPXConverter.getPref("lastAltimeter").equals("Yes")) {
				  c.setHasAltimeter(true);
				}
					
				if (chckbxRidewithgps.isSelected()) {
					c.setDoRWGPS(true);
				}
				if (chckbxStrava.isSelected()) {
					c.setDoStrava(true);
				}
				if (chckbxGarmin.isSelected()) {
					c.setDoGarmin(true);
				}
				//c.setOutFile(txtDestFile.getText());
				statusLog("Starting Conversion and Upload...");
				Thread t = new Thread(c, "Converter");
				// Set the button the thread knows to enable when it's done. (yeah...lame way to do this...)
				//btnConvertIt.setEnabled(false);
				t.start();
			}
		});
		btnConvertIt.setBounds(168, 208, 131, 23);
		frmStravaGpxConverter.getContentPane().add(btnConvertIt);
		
		JButton btnAccounts = new JButton("Account Information");
		btnAccounts.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				accounts.setVisible(true);
			}
		});
		btnAccounts.setBounds(147, 74, 166, 23);
		frmStravaGpxConverter.getContentPane().add(btnAccounts);
	}
	
	public class TypeAction implements ActionListener { 
		public void actionPerformed(ActionEvent e) {
			GPXConverter.setPref("lastType", e.getActionCommand());
		}
	}
	
	public class DeviceAction implements ActionListener { 
		public void actionPerformed(ActionEvent e) {
			GPXConverter.setPref("lastAltimeter", e.getActionCommand());
		}
	}
}
