package com.javapr.plaintextindex.search;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class Suchen{
	
	private JFrame frame;
	private JPanel panel;
	private JList list;
	private JPanel panel1;
	private JPanel panelLabel;
	private JScrollPane pane;
	private JLabel labelAnzData;
	private JLabel alert;
	private JLabel suche;
	private JLabel dir;
	private List<Object>list1;
	
	public void sucheString (String line) throws IOException,ParseException
	
	{		
		frame = new JFrame();
		frame.setTitle("Lucene Suche");
		frame.setSize(400,180);
		frame.setLocation(400, 400);
		frame.setVisible(true);
		panel = new JPanel(new BorderLayout(5,5));
		list = new JList();
		panel1 = new JPanel(new GridLayout(1,1));
		panelLabel = new JPanel(new GridLayout(2,1));
		pane = new JScrollPane(panel1);
		labelAnzData = new JLabel();
		alert = new JLabel();
		suche = new JLabel();
		dir = new JLabel();
		panel.add(panelLabel,BorderLayout.WEST);
		panel.add(pane,BorderLayout.CENTER);
		frame.add(panel);
		
		String field = "contents";
		int hitsPerPage = 10;
		
		try{
		IndexReader indexReader = DirectoryReader.open(FSDirectory.open(new File(Prop.Indexdirectory(null))));	
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
		QueryParser parser = new QueryParser(Version.LUCENE_42, field, analyzer);
		
		list1 = new ArrayList<Object>();
		
			try{
			Query query = parser.parse(line);	
			
			suche = new JLabel("Suche nach: " +line);
			panelLabel.add(suche);
	
			TopDocs results = indexSearcher.search(query, 5 * hitsPerPage);
			ScoreDoc[] hits = results.scoreDocs;
			
			int end = hits.length;
			
			//Liste erzeugen und mit gefundenen Dateinamen füllen
			for(int i = 0; i< end; i++){
				Document doc = indexSearcher.doc(hits[i].doc);
				String path = doc.get("filename");
				File file = new File(Prop.Filestoindex(null)+ "\\" + doc.get("filename"));
				 if (path != null) {
					 list1.add(path+", "+file.length()/1024+"kb");
					 }
				}
				
					labelAnzData = new JLabel(hits.length + " Gefundene Datei(en): ");
					panelLabel.add(labelAnzData);
					list = new JList(list1.toArray());
					panel1.add(list);
					
			
		}catch(ParseException p){alert = new JLabel("String eingeben!");panel.add(alert);;}
		}catch(IOException e){dir.setText("<html><body>Index-Datei nicht gefunden!<br>Bitte Index erstellen!</body></html>");panel.add(dir);}
	}
			
}
	

