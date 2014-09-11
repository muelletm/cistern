// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.latin;

import marmot.morph.mapper.MorphTag;
import marmot.morph.mapper.latin.LdtMorphTag.Pos;

public class LdtTagReader {


	public MorphTag read(String pos, String feats) {
		LdtMorphTag tag = new LdtMorphTag();
		
		assert pos.length() == 1;
		
		if (pos.equals("_") || pos.equals("-")) {
			tag.pos_ = Pos.Undef;
		} else {
			tag.pos_ = Pos.valueOf(pos);
		}
		
		
		return tag;
	}

}
