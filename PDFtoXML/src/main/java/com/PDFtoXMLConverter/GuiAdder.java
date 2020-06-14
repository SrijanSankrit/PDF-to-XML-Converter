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
/**
 * 
 * This class serves as the Base class for the package <br>
 * and takes GUI input from user and call classes<br>
 * as required for XML Generation.
 * 
 * @author KUSHAGRA
 * 
 */
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
		frame.getContentPane().setFont(new Font(Messages.getString("GuiAdder.FontTahoma"), Font.PLAIN, 20)); 
		frame.setBounds(100, 100, 858, 562);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton(Messages.getString("GuiAdder.1")); 
		final JButton btnNewFile = new JButton(Messages.getString("GuiAdder.2")); 
		
		
		
		
		//Initializing the File Chooser and setting filter, title and extensions allowed
		this.fc = new JFileChooser();
		fc.setDialogTitle(Messages.getString("GuiAdder.3")); 
		FileNameExtensionFilter filter = new FileNameExtensionFilter(Messages.getString("GuiAdder.4"),Messages.getString("GuiAdder.5"));
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
				if(pw!=Messages.getString("GuiAdder.6")){ 
					try {
						 PDDocument document ;
						 File file = new File(S);
						 
						 document = PDDocument.load(file,pw);
						 
						 document.close();
					}
					catch(IOException e1) {
						JOptionPane.showMessageDialog(null, Messages.getString("GuiAdder.7")); 
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
						JOptionPane.showMessageDialog(null, Messages.getString("GuiAdder.8"));
					}
				}
				
				
				ArrayList<Integer> al = new ArrayList<Integer>();
				
				if((pgs.charAt(0)=='*' && pgs.length()==1)) {
					  
					 PDDocument document ;
					 try{
						 File file = new File(S);
						 if(pw!=Messages.getString("GuiAdder.9")) { 
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
						JOptionPane.showMessageDialog(null, Messages.getString("GuiAdder.10")); 
						
					}
				}
				else
				{
					String str[] = pgs.split(Messages.getString("GuiAdder.11"));
					
					for(int i=0;i<str.length;i++) {
						try {
							al.add(Integer.parseInt(str[i]));
						}
						catch(NumberFormatException nfe) {
							JOptionPane.showMessageDialog(null, Messages.getString("GuiAdder.12")); 
							return ;
						}
					}
				
					String s1=PDF2XMLTester.solver(S,al,pw);
					JOptionPane.showMessageDialog(null, s1);
				}
				}
				catch(Exception e1) {
					JOptionPane.showMessageDialog(null, Messages.getString("GuiAdder.13")); 
				}
			}
		});
		btnNewButton.setFont(new Font(Messages.getString("GuiAdder.14"), Font.PLAIN, 20)); 
		btnNewButton.setBounds(306, 442, 202, 54);
		frame.getContentPane().add(btnNewButton);
		
		txtEnterFileName = new JTextField();
		txtEnterFileName.setFont(new Font(Messages.getString("GuiAdder.15"), Font.PLAIN, 25)); 
		txtEnterFileName.setBounds(400, 81, 338, 40);
		frame.getContentPane().add(txtEnterFileName);
		txtEnterFileName.setColumns(10);
		
		JLabel labelFileName = new JLabel(Messages.getString("GuiAdder.16")); 
		labelFileName.setFont(new Font(Messages.getString("GuiAdder.17"), Font.PLAIN, 25)); 
		labelFileName.setBounds(73, 81, 287, 40);
		frame.getContentPane().add(labelFileName);
		
		textEnterPages = new JTextField();
		textEnterPages.setText(Messages.getString("GuiAdder.18")); 
		textEnterPages.setFont(new Font(Messages.getString("GuiAdder.19"), Font.PLAIN, 15)); 
		textEnterPages.setBounds(400, 147, 422, 40);
		frame.getContentPane().add(textEnterPages);
		textEnterPages.setColumns(10);
		
		JLabel labelPageNum = new JLabel(Messages.getString("GuiAdder.20"));
		labelPageNum.setFont(new Font(Messages.getString("GuiAdder.21"), Font.PLAIN, 15)); 
		labelPageNum.setBounds(73, 146, 309, 40);
		frame.getContentPane().add(labelPageNum);
		
		JLabel title = new JLabel(Messages.getString("GuiAdder.22")); 
		title.setBackground(new Color(0, 128, 0));
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font(Messages.getString("GuiAdder.23"), Font.BOLD | Font.ITALIC, 30)); 
		title.setBounds(21, 10, 801, 54);
		frame.getContentPane().add(title);
		
		JLabel example = new JLabel(Messages.getString("GuiAdder.24")); 
		example.setFont(new Font(Messages.getString("GuiAdder.25"), Font.PLAIN, 15));
		example.setBounds(73, 184, 287, 32);
		frame.getContentPane().add(example);
		
		JLabel allPages = new JLabel(Messages.getString("GuiAdder.26"));
		allPages.setFont(new Font(Messages.getString("GuiAdder.27"), Font.PLAIN, 15));
		allPages.setBounds(73, 206, 293, 32);
		frame.getContentPane().add(allPages);
		
		
		btnNewFile.setBounds(737, 81, 85, 40);
		frame.getContentPane().add(btnNewFile);
		
		JLabel pwdEntry = new JLabel(Messages.getString("GuiAdder.28"));
		pwdEntry.setFont(new Font(Messages.getString("GuiAdder.29"), Font.PLAIN, 15)); 
		pwdEntry.setBounds(73, 279, 276, 40);
		frame.getContentPane().add(pwdEntry);
		
		password = new JPasswordField();
		password.setBounds(400, 282, 422, 40);
		frame.getContentPane().add(password);
		password.setColumns(10);
		
		
	}
}

