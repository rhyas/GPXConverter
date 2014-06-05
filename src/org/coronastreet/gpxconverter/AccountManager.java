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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

public class AccountManager extends JFrame {

	static Logger log = Logger.getLogger(AccountManager.class);
	private static final long serialVersionUID = -3082970655365067290L;
	private JPanel frmAccounts;
	public JTextField strava_username;
	public JPasswordField strava_password;
	public JTextField rwgps_username;
	public JPasswordField rwgps_password;
	public JTextField garmin_username;
	public JPasswordField garmin_password;
	
    private static final char[] PASSWORD = "enfldsgbnlsngdlksdsgm".toCharArray();
    private static final byte[] SALT = {
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    };
    
	public AccountManager(MainWindow window) {
		setTitle("Account Settings");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 199);
		frmAccounts = new JPanel();
		frmAccounts.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(frmAccounts);
		frmAccounts.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(5, 5, 424, 118);
		frmAccounts.add(tabbedPane);
		
		JPanel stravaPanel = new JPanel();
		stravaPanel.setLayout(null);

		tabbedPane.addTab("Strava", stravaPanel);
		
		strava_username = new JTextField();
		strava_username.setBounds(79, 11, 235, 20);
		stravaPanel.add(strava_username);
		strava_username.setColumns(10);
		
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(10, 14, 69, 14);
		stravaPanel.add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(10, 45, 69, 14);
		stravaPanel.add(lblPassword);
		
		strava_password = new JPasswordField();
		strava_password.setColumns(10);
		strava_password.setBounds(79, 42, 235, 20);
		stravaPanel.add(strava_password);
		
		JPanel rwgpsPanel = new JPanel();
		rwgpsPanel.setLayout(null);
		tabbedPane.addTab("RideWithGPS", rwgpsPanel);
		
		JLabel label = new JLabel("Username:");
		label.setBounds(10, 14, 69, 14);
		rwgpsPanel.add(label);
		
		rwgps_username = new JTextField();
		rwgps_username.setColumns(10);
		rwgps_username.setBounds(79, 11, 235, 20);
		rwgpsPanel.add(rwgps_username);
		
		rwgps_password = new JPasswordField();
		rwgps_password.setColumns(10);
		rwgps_password.setBounds(79, 42, 235, 20);
		rwgpsPanel.add(rwgps_password);
		
		JLabel label_1 = new JLabel("Password:");
		label_1.setBounds(10, 45, 69, 14);
		rwgpsPanel.add(label_1);
		
		JPanel garminPanel = new JPanel();
		garminPanel.setLayout(null);
		tabbedPane.addTab("Garmin Connect", garminPanel);
		
		JLabel label_2 = new JLabel("Username:");
		label_2.setBounds(10, 14, 69, 14);
		garminPanel.add(label_2);
		
		garmin_username = new JTextField();
		garmin_username.setColumns(10);
		garmin_username.setBounds(79, 11, 235, 20);
		garminPanel.add(garmin_username);
		
		garmin_password = new JPasswordField();
		garmin_password.setColumns(10);
		garmin_password.setBounds(79, 42, 235, 20);
		garminPanel.add(garmin_password);
		
		
		JLabel label_3 = new JLabel("Password:");
		label_3.setBounds(10, 45, 69, 14);
		garminPanel.add(label_3);

		loadAccountPrefs();
		
		JButton btnSave = new JButton("SAVE");
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				saveAccountInfo();
			}
		});
		btnSave.setBounds(106, 127, 89, 23);
		frmAccounts.add(btnSave);
		
		JButton btnCancel = new JButton("CANCEL");
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				cancel();
			}
		});
		btnCancel.setBounds(242, 127, 89, 23);
		frmAccounts.add(btnCancel);
		
	}
	
	private void saveAccountInfo() {
		
		GPXConverter.setPref("strava_username", strava_username.getText());
		GPXConverter.setPref("rwgps_username", rwgps_username.getText());
		GPXConverter.setPref("garmin_username", garmin_username.getText());
		
		try {
			GPXConverter.setPref("strava_password", encrypt(String.valueOf(strava_password.getPassword())));
		} catch (Exception e) {
			// Error with saving the password, let's wipe it and throw an error.
			GPXConverter.delPref("strava_password");
			JOptionPane.showMessageDialog(this,
				    "Strava Password Encryption failed Please try again or restart the program.",
				    "Password Encryption Failed.",
				    JOptionPane.WARNING_MESSAGE);
		}
		try {
			GPXConverter.setPref("rwgps_password", encrypt(String.valueOf(rwgps_password.getPassword())));
		} catch (Exception e) {
			// Error with saving the password, let's wipe it and throw an error.
			GPXConverter.delPref("rwgps_password");
			JOptionPane.showMessageDialog(this,
				    "RideWithGPS Password Encryption failed Please try again or restart the program.",
				    "Password Encryption Failed.",
				    JOptionPane.WARNING_MESSAGE);
		}
		try {
			GPXConverter.setPref("garmin_password", encrypt(String.valueOf(garmin_password.getPassword())));
		} catch (Exception e) {
			// Error with saving the password, let's wipe it and throw an error.
			GPXConverter.delPref("garmin_password");
			JOptionPane.showMessageDialog(this,
				    "Garmin Password Encryption failed Please try again or restart the program.",
				    "Password Encryption Failed.",
				    JOptionPane.WARNING_MESSAGE);
		}
		
		this.setVisible(false);
	}
	
	private void cancel() {
		// Populate stuff with old values...
		loadAccountPrefs();
		this.setVisible(false);
	}
	
	private void loadAccountPrefs() {
		strava_username.setText(GPXConverter.getPref("strava_username"));
		rwgps_username.setText(GPXConverter.getPref("rwgps_username"));
		garmin_username.setText(GPXConverter.getPref("garmin_username"));
		
		try {
			strava_password.setText(decrypt(GPXConverter.getPref("strava_password")));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
				    "Loading Strava Encrypted Password failed. Please check the Strava password and try saving it again.",
				    "Password Decryption Failed.",
				    JOptionPane.WARNING_MESSAGE);
		}
		try {
			rwgps_password.setText(decrypt(GPXConverter.getPref("rwgps_password")));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
				    "Loading RideWithGPS Encrypted Password failed. Please check the RideWithGPS password and try saving it again.",
				    "Password Decryption Failed.",
				    JOptionPane.WARNING_MESSAGE);
		}
		try {
			garmin_password.setText(decrypt(GPXConverter.getPref("garmin_password")));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
				    "Loading Garmin Encrypted Password failed. Please check the Garmin password and try saving it again.",
				    "Password Decryption Failed.",
				    JOptionPane.WARNING_MESSAGE);
		}
		
	}
	
    public static String encrypt(String property) throws GeneralSecurityException, UnsupportedEncodingException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
    }

    private static String base64Encode(byte[] bytes) {
        // NB: This class is internal, and you probably should use another impl
    	return Base64.encodeBase64String(bytes);
    }

    public static String decrypt(String property) throws GeneralSecurityException, IOException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    private static byte[] base64Decode(String property) throws IOException {
        // NB: This class is internal, and you probably should use another impl
    	return Base64.decodeBase64(property);
    }
}
