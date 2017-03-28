// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class LineIterator implements Iterator<List<String>> {
	
	//private final static String DefaultSeperator_ = "\\s+";
	private final static String DefaultSeperator_ = "\\t";
	private BufferedReader reader_;
	private String seperator_;
	
	public LineIterator(String filename){
		this(filename, DefaultSeperator_);
	}
	
	public LineIterator(InputStream in) {
		this(in, DefaultSeperator_);
	}
	
	public LineIterator(InputStream in, String seperator) {
		seperator_ = seperator;
		try {
			reader_ = FileUtils.openStream(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public LineIterator(String filename, String seperator) {
		seperator_ = seperator;
		try {
			reader_ = FileUtils.openFile(filename);
		} catch(FileNotFoundException e){
			throw new RuntimeException(e);			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean hasNext(){
		try {
			return reader_.ready();
		}
		catch (IOException e){
			throw new RuntimeException("IOException: " + e);
		}
	}
	
	public List<String> next(){
		
		if (!hasNext()){
			throw new NoSuchElementException();
		}
		
		try {
			String line = reader_.readLine();
			if (line.length() > 1 && line.charAt(0) == '#') { 
				//System.out.println(line);
				ArrayList<String> list = new ArrayList<String>(1);
				list.add(line);
				return list;
			}
			String[] tokens = line.split(seperator_);
			if(tokens[0].contains(".")) {
				ArrayList<String> list = new ArrayList<String>(1);
				String[] idxTok = tokens[0].split(".");
				list.add("$|$" + idxTok[0] + "$|$" + line);
				list.add(line);
				return list;
			} else if(tokens[0].contains("-")) {
				String[] idxTok = tokens[0].split("-");
				ArrayList<String> list = new ArrayList<String>(1);
				list.add("|$|" + idxTok[0] + "|$|" + line);
				list.add(line);
				return list;
			}
			ArrayList<String> list = new ArrayList<String>(tokens.length);
			for (int i=0;i<tokens.length;i++){
				if (!tokens[i].isEmpty()){
					list.add(tokens[i]);
				}
			}
			return list;
		}
		catch (IOException e){
			throw new RuntimeException("IOException: " + e);
		}
	}
	
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
