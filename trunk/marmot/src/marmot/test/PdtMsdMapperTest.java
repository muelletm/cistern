// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test;

import java.util.Map;

import marmot.morph.mapper.czech.ConllReader;
import marmot.morph.mapper.czech.MsdReader;
import marmot.util.Counter;

import org.junit.Test;

public class PdtMsdMapperTest {

	@Test
	public void test() {

		String msd_file = "/nfs/data1/proj/marmot/treebanks/mteV4-2010-05-14/ana/oana-cs.txt";
		Map<String, Counter<String>> msd_map = MsdReader.getDict(msd_file);

		String ptb_file = "/mounts/data/proj/marmot/treebanks/conll09/cze/CoNLL2009-ST-Czech-train.txt";
		Map<String, Counter<String>> ptb_map = ConllReader.getDict(ptb_file);

		int number = 0;
		int error = 0;

		for (Map.Entry<String, Counter<String>> entry : msd_map.entrySet()) {

			Counter<String> msd_counter = entry.getValue();
			Counter<String> ptb_counter = ptb_map.get(entry.getKey());

			if (ptb_counter != null) {

				double msd_count = msd_counter.totalCount();
				double ptb_count = ptb_counter.totalCount();

				if (msd_count > 5 && ptb_count > 5) {

					if (msd_counter.size() == 1 || ptb_counter.size() == 1) {

						String msd_tag = msd_counter.sortedEntries().iterator()
								.next().getKey();
						String ptb_tag = ptb_counter.sortedEntries().iterator()
								.next().getKey();

						if (msd_tag.charAt(0) == ptb_tag.charAt(0)) {
						
						if (!(msd_tag.startsWith("r") && ptb_tag
								.startsWith("r"))) {

							if (!msd_tag.equals(ptb_tag)) {
								System.err.println(entry.getKey() + " "
										+ msd_tag + " " + ptb_tag);
								error++;
							}

							number++;

						}
						
						}

					}

				}

			}
		}

		System.err.println(error + " / " + number);
	}

}
