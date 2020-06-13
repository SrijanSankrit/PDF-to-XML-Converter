package com.PDFtoXMLConverter;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.Font;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.Color;
import javax.swing.JTree;

public class GuiAdder {

	private JFrame frame;
	private JTextField txtEnterFileName;
	private JTextField textEnterPages;
	private JFileChooser fc;
	private JFileChooser destn;
	private JTextField password;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiAdder window = new GuiAdder();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GuiAdder() {
		
		
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 20));
		frame.setBounds(100, 100, 858, 562);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("Generate XML");
		final JButton btnNewFile = new JButton("Choose");
		
		
		//Initializing the File Chooser and setting filter, title and extensions allowed
		this.fc = new JFileChooser();
		fc.setDialogTitle("Select PDF File to be extracted from");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("pdf","PDF");
		fc.setFileFilter(filter);
		fc.changeToParentDirectory();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		
		
		
		btnNewFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    //Handle open button action.
			    if (e.getSource() == btnNewFile) {
			        int returnVal = fc.showOpenDialog(null);
			        
			        

			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			        	 txtEnterFileName.setText(fc.getSelectedFile().toString());
			            //This is where a real application would open the file.
			            
			        }
			   } 
			}
		});
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
				String S=txtEnterFileName.getText() ;
				String pgs=textEnterPages.getText();
				String pw=password.getText();
				if(pw!=""){
					try {
						 PDDocument document ;
						 File file = new File(S);
						 
						 document = PDDocument.load(file,pw);
						 
						 document.close();
					}
					catch(IOException e1) {
						JOptionPane.showMessageDialog(null, "Enter the correct password!");
					}
				}
				else {
					try {
						 PDDocument document ;
						 File file = new File(S);
						 document = PDDocument.load(file,pw);
						 document.close();
					}
					catch(IOException e1) {
						JOptionPane.showMessageDialog(null, "Enter the password!");
					}
				}
				
				
				ArrayList<Integer> al = new ArrayList<Integer>();
				
				if(pgs.charAt(0)=='*' && pgs.length()==1) {
					  
					 PDDocument document ;
					 try{
						 File file = new File(S);
						 if(pw!="") {
							 document = PDDocument.load(file,pw);
							 document.setAllSecurityToBeRemoved(true);
						 }
						 else{
							 document = PDDocument.load(file);
						 }
						 int total=document.getNumberOfPages();
						 document.close();
						 for(int pageC=1;pageC<=total;pageC++) {
							 al.add(pageC);
						 }
						 
							String s1=PDF2XMLTester.solver(S,al,pw);
							JOptionPane.showMessageDialog(null, s1);
							
					 }
					 
					catch(IOException eio) {
						JOptionPane.showMessageDialog(null, "Retry!");
						
					}
				}
				else
				{
					String str[] = pgs.split(",");
					
					for(int i=0;i<str.length;i++) {
						try {
							al.add(Integer.parseInt(str[i]));
						}
						catch(NumberFormatException nfe) {
							JOptionPane.showMessageDialog(null, "Enter a valid List!");
							return ;
						}
					}
				
					String s1=PDF2XMLTester.solver(S,al,pw);
					JOptionPane.showMessageDialog(null, s1);
				}
				}
				catch(Exception e1) {
					JOptionPane.showMessageDialog(null, "Retry!");
				}
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnNewButton.setBounds(306, 442, 202, 54);
		frame.getContentPane().add(btnNewButton);
		
		txtEnterFileName = new JTextField();
		txtEnterFileName.setFont(new Font("Tahoma", Font.PLAIN, 25));
		txtEnterFileName.setBounds(400, 81, 338, 40);
		frame.getContentPane().add(txtEnterFileName);
		txtEnterFileName.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Enter the PDF File: ");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 25));
		lblNewLabel.setBounds(73, 81, 287, 40);
		frame.getContentPane().add(lblNewLabel);
		
		textEnterPages = new JTextField();
		textEnterPages.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textEnterPages.setBounds(400, 147, 422, 40);
		frame.getContentPane().add(textEnterPages);
		textEnterPages.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Enter the no. of pages (comma seperated) :");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_1.setBounds(73, 146, 309, 40);
		frame.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("PDFtoXML Converter");
		lblNewLabel_2.setBackground(new Color(0, 128, 0));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 30));
		lblNewLabel_2.setBounds(21, 10, 801, 54);
		frame.getContentPane().add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Like : 1,2,3");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_3.setBounds(73, 184, 287, 32);
		frame.getContentPane().add(lblNewLabel_3);
		
		JLabel lblNewLabel_4 = new JLabel("Or mention * for all pages");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_4.setBounds(73, 206, 293, 32);
		frame.getContentPane().add(lblNewLabel_4);
		
		
		btnNewFile.setBounds(737, 81, 85, 40);
		frame.getContentPane().add(btnNewFile);
		
		JLabel lblNewLabel_5 = new JLabel("Enter PDF Password (if applicable) :");
		lblNewLabel_5.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_5.setBounds(73, 279, 276, 40);
		frame.getContentPane().add(lblNewLabel_5);
		
		password = new JPasswordField();
		password.setBounds(400, 282, 422, 40);
		frame.getContentPane().add(password);
		password.setColumns(10);
		
		
	}
}

