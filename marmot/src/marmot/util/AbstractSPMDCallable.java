// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

public abstract class AbstractSPMDCallable<I,O> implements Callable<O> {
	
	private Iterator<I> iterator;
	protected O out;
		
	public void reset(Iterator<I> iter,O out){
		iterator = iter;
		this.out = out;
	}
	
	@Override
	public O call() throws Exception {
				
		while (true) {
			
			I in;
			
			try {
				in = next();
			}catch (NoSuchElementException e){
				break;
			}
			
			out = apply(in,out);		
		}
		
		return out;
	}
	
	protected abstract O apply(I in, O out);	

	public I next(){
		synchronized (iterator) {
			if(iterator.hasNext())
				return iterator.next();
			else 
				throw new NoSuchElementException();
		}
	}

}
