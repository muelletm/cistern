// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.zip.GZIPInputStream;


public class LineIterator implements Iterator<List<String>> {
	
	private BufferedReader reader_;
	
	public LineIterator(String filename){
		try {
			InputStream stream = new FileInputStream(filename);
			if (filename.endsWith(".gz")) {
				stream = new GZIPInputStream(stream);
			}		
			reader_ = new BufferedReader(new InputStreamReader(stream));
		} catch(FileNotFoundException e){
			throw new RuntimeException(e);			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public LineIterator(InputStream in) {
		reader_ = new BufferedReader(new InputStreamReader(in));
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
						
			String[] tokens = line.split("\\s+");
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