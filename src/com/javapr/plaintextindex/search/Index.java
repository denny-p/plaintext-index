/*
Copyright 2013 Denny Placzek

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.javapr.plaintextindex.search;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


public class Index {

	private final static List<Object> list = new ArrayList<Object>();
	private JFrame frame;
	private JPanel panel;
	private JPanel panel1;
	private JPanel panelLabel;
	private JScrollPane pane;
	private JList liste;
	private JLabel doc;
	private JLabel indexdir;
	
	public void erstelleIndex()throws SAXException, TikaException
	{
		frame = new JFrame();
		frame.setTitle("Lucene Suche");
		frame.setSize(400,280);
		frame.setLocation(400, 400);
		frame.setVisible(true);
		panel = new JPanel(new BorderLayout(5,5));
		panel1 = new JPanel(new GridLayout(1,1));
		panelLabel = new JPanel(new GridLayout(1,1));
		pane = new JScrollPane(panel1);
		liste = new JList();
		doc = new JLabel();
		indexdir = new JLabel();
		
		panel.add(panelLabel,BorderLayout.NORTH);
		panel.add(pane,BorderLayout.CENTER);
		panel.setLayout(new GridLayout(2,1));
		frame.add(panel);
		
				
		//überprüfen ob Verzeichnis mit Text-Files vorhanden ist
		final File docDir = new File(Prop.Filestoindex(null));
		if (!docDir.exists() || !docDir.canRead()) {
			doc = new JLabel("Dokumenten Verzeichnis '" +docDir.getAbsolutePath()+ "' kann nicht gelesen werden, bitte Pfad überprüfen!");
			panelLabel.add(doc);
		}
		
		//Textdateien einlesen
		if((docDir.exists() || docDir.canRead())) {
		Date start = new Date();
		try{
		indexdir = new JLabel("Index Verzeichnis: '" + Prop.Indexdirectory(null) + "'...");
		panelLabel.add(indexdir);	
		
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_42, analyzer);
		Directory directory = FSDirectory.open(new File(Prop.Indexdirectory(null)));
		IndexWriter writer = new IndexWriter(directory, iwc);
		
		//Methodenaufruf indexDocs und schreiben in Index-Verzeichnis
		indexDocs(writer, docDir);
		writer.close();	
		
		Date end = new Date();
		System.out.println(end.getTime() - start.getTime() + " millisekunden");
		
		}catch(IOException e){System.out.println(" caught a " + e.getClass() + "with message: " + e.getMessage());}
		
		}
		liste = new JList(list.toArray());
		panel1.add(liste);
	}	
	
		
	//Methode um Textdateien zu indizieren
	public static void indexDocs(IndexWriter writer, File file) throws IOException,SAXException, TikaException{
		
			// nur lesbare Dateien verwenden
		    if (file.canRead()) 
		    {
		    	if (file.isDirectory()) 
		    	{
		    		String[] files = file.list();
		    		
		    		if (files != null) 
		    		{
		    			for (int i = 0; i < files.length; i++) 
		    			{
		    				
		    				indexDocs(writer, new File(file, files[i]));
		    				
		    			}
		    		}
		    	} else {
		    			FileInputStream fis;
			       try {
			    	   fis = new FileInputStream(file);
			        	}catch (FileNotFoundException fnfe) {
			         
			          return;
			            }
		
			       try {
			    	   
			    	   //Word Dokumente mit Tika parsen
			    	    ContentHandler contenthandler = new BodyContentHandler();
			      	    Metadata metadata = new Metadata();
	    				metadata.set(Metadata.RESOURCE_NAME_KEY, file.getName());
	    				Parser parser = new AutoDetectParser();
	    				parser.parse(fis, contenthandler, metadata, new ParseContext());
	    			
		
			          // Lucene Dokumenten-Objekt erstellen und geparsten Tika-Inhalt speichern
			          Document doc = new Document();
	
			          Field pathField = new StringField("path", file.getPath(), Field.Store.YES);
			          doc.add(pathField);
			          
			          Field filename = new StringField("filename", file.getName(), Field.Store.YES);
			          doc.add(filename);
		
			          doc.add(new LongField("modified", file.lastModified(), Field.Store.NO));
			          		
			          doc.add(new TextField("contents", contenthandler.toString(),Field.Store.NO));
			          	
			          
			          if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
			        	  //neuer Index, wenn neues Dokument
			        	  System.out.println("adding " + file);
				          writer.addDocument(doc);
			          } else {
			        	long size = file.length()/1024;  
			        	list.add(file+", "+size+"kb"); 
			        	//Index updaten, wenn älteres Index-Dokument schon vorhanden
			        	System.out.println("updating " + file);
			            writer.updateDocument(new Term("path", file.getPath()), doc);
			          }
			          
			          } finally {
			          fis.close();
		        }
		      }
		    }
		  }
}
