package com.javapr.plaintextindex.search;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Prop {
	
	//lesen des zu indizierenden Verzeichnisses(Textdateien) aus properties-Datei	
	public static final String Filestoindex(String FILES_TO_INDEX_DIRECTORY){
		
		try{
			Properties props = new Properties();
			FileInputStream in = new FileInputStream("src\\com\\javapr\\plaintextindex\\search\\indVerzeichnis.properties");
			props.load(in);
			in.close();
			FILES_TO_INDEX_DIRECTORY = props.getProperty("Pfad");
			}catch(IOException e){System.out.println("Output error!");}
			return FILES_TO_INDEX_DIRECTORY;
	}
	
	//lesen des Index-Verzeichnisses aus properties-Datei
	public static final String Indexdirectory(String INDEX_DIRECTORY){
		
		try{
			Properties props = new Properties();
			FileInputStream in = new FileInputStream("src\\com\\javapr\\plaintextindex\\search\\indexVerzeichnis.properties");
			props.load(in);
			in.close();
			INDEX_DIRECTORY = props.getProperty("Pfad");
			}catch(IOException e){System.out.println("Output error!");}
			return INDEX_DIRECTORY;
		}
	

}
