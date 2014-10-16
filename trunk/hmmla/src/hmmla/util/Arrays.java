// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.util;

public class Arrays {

	static public void multiArrayCopy(double[][] source,double[][] destination)
	{
		for (int a=0;a<source.length;a++)
		{
			System.arraycopy(source[a],0,destination[a],0,source[a].length);
		}
	}
	
}
