package com.javapr.plaintextindex.search;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

public class Gui extends JFrame{
	
	private JButton index;
	private JButton suchen;
	private JButton exit;
	private JTextField feld;
	private JPanel panel = new JPanel();
	private JPanel panel1 = new JPanel();
	private JPanel panelButton;
	
	public Gui(){
		super("Lucene Suche");
		getContentPane().setLayout(new BorderLayout(5,5));
		
		panel1 = new JPanel(new FlowLayout());
		panelButton = new JPanel(new FlowLayout());
		panel.add(panel1);
		panel.add(panelButton);
	
		
		index = new JButton("Index erstellen");
		panelButton.add(index);
		index.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Index ind = new Index();
						try {
							ind.erstelleIndex();
						} catch (SAXException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (TikaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				  }
				);
		
		suchen =new JButton("Suchen");
		panelButton.add(suchen);
		suchen.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Suchen such = new Suchen();
						try {
							such.sucheString(feld.getText());
						} catch (IOException | ParseException e1) {
						e1.printStackTrace();
						}
						
					}
				  }
				);
		
		exit = new JButton("Abbrechen");
		panelButton.add(exit);
		exit.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				  }
				);
		
	    panel1.add(new JLabel("Suche eingeben:"));
		feld = new JTextField(10);
		panel1.add(feld);
		feld.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Suchen such = new Suchen();
						try {
							such.sucheString(feld.getText());
						} catch (IOException | ParseException e1) {
						e1.printStackTrace();
						}				
					}
				  }
				);
		
			
		getContentPane().add(BorderLayout.CENTER, panel);
		
	}
	
	
}
