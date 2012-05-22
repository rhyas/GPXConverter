package org.coronastreet.gpxconverter;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.File;

public class MainWindow {

	private JFrame frmStravaGpxConverter;
	private JTextField txtSourceFile;
	private JTextField txtDestFile;
	private JFileChooser fc;
	private JTextArea txtStatusArea;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmStravaGpxConverter.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmStravaGpxConverter = new JFrame();
		frmStravaGpxConverter.setTitle("Strava GPX Converter");
		frmStravaGpxConverter.setBounds(100, 100, 441, 426);
		frmStravaGpxConverter.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmStravaGpxConverter.getContentPane().setLayout(null);
		
		fc = new JFileChooser();
		
		JLabel lblThisToolConverts = new JLabel("This tool converts GPX to TCX for input to Strava");
		lblThisToolConverts.setHorizontalAlignment(SwingConstants.CENTER);
		lblThisToolConverts.setBounds(44, 11, 327, 14);
		frmStravaGpxConverter.getContentPane().add(lblThisToolConverts);
		
		txtSourceFile = new JTextField();
		txtSourceFile.setBounds(24, 54, 286, 20);
		frmStravaGpxConverter.getContentPane().add(txtSourceFile);
		txtSourceFile.setColumns(10);
		
		JLabel lblSourceGpxFile = new JLabel("Source GPX File");
		lblSourceGpxFile.setBounds(27, 75, 111, 14);
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
				}
			}
		});
		btnNewButton.setBounds(320, 53, 78, 23);
		frmStravaGpxConverter.getContentPane().add(btnNewButton);
		
		txtDestFile = new JTextField();
		txtDestFile.setBounds(24, 100, 286, 20);
		frmStravaGpxConverter.getContentPane().add(txtDestFile);
		txtDestFile.setColumns(10);
		
		JLabel lblDestinationTcxFile = new JLabel("Destination TCX File");
		lblDestinationTcxFile.setBounds(24, 119, 114, 14);
		frmStravaGpxConverter.getContentPane().add(lblDestinationTcxFile);
		
		JButton btnSetDst = new JButton("Set Dst");
		btnSetDst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc.setFileFilter(new TCXFilter());
				int returnVal = fc.showDialog(frmStravaGpxConverter, "Choose Destination");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					txtDestFile.setText(fc.getSelectedFile().getPath());
					File f = new File("");
					fc.setSelectedFile(f);
				}				
			}
		});
		btnSetDst.setBounds(320, 99, 78, 23);
		frmStravaGpxConverter.getContentPane().add(btnSetDst);
		
		JButton btnConvertIt = new JButton("CONVERT IT");
		btnConvertIt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Check stuff
				if (txtSourceFile.getText().equals(txtDestFile.getText())) {
					txtStatusArea.append("Can't write to same file you read from!\n");
					return;
				}
				Converter c = new Converter();
				c.setInFile(txtSourceFile.getText());
				c.setOutFile(txtDestFile.getText());
				txtStatusArea.append("Starting conversion from GPX to TCX...\n");
				c.convert(txtStatusArea);		
				txtStatusArea.append("Finished!\n");
			}
		});
		btnConvertIt.setBounds(141, 148, 131, 23);
		frmStravaGpxConverter.getContentPane().add(btnConvertIt);
		
		txtStatusArea = new JTextArea();
		txtStatusArea.setLineWrap(true);
		txtStatusArea.setEditable(false);
		txtStatusArea.setColumns(1);
		txtStatusArea.setBounds(24, 182, 374, 184);
		frmStravaGpxConverter.getContentPane().add(txtStatusArea);
		
	}
}
