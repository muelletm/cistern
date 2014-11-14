package marmot.morph.cmd;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import marmot.core.Sequence;
import marmot.core.Tagger;
import marmot.morph.MorphEvaluator;
import marmot.morph.MorphModel;
import marmot.morph.MorphOptions;
import marmot.morph.MorphResult;
import marmot.morph.io.SentenceReader;
import marmot.util.FakeWriter;
import marmot.util.ListUtils;

public class CrossAnnotator {

	public static void main(String[] args) throws IOException {
		MorphOptions options = new MorphOptions();
		options.setPropertiesFromStrings(args);
		options.dieIfPropertyIsEmpty(MorphOptions.TRAIN_FILE);	
		annotate(options, options.getTrainFile(), options.getPredFile() , options.getNumChunks());
	}

	public static void annotate(MorphOptions options, String infile,
			String outfile, int num_chunks) throws IOException {

		List<Sequence> sequences = new LinkedList<Sequence>();
		for (Sequence sequence : new SentenceReader(infile)) {
			sequences.add(sequence);
		}

		Writer writer = null;
		
		if (outfile == null || outfile.isEmpty()) {
			writer = new FakeWriter();
		} else {
			writer = new BufferedWriter(new FileWriter(outfile));	
		}
		
		annotate(options, sequences, num_chunks, writer);
		writer.close();

	}

	public static void annotate(MorphOptions options, List<Sequence> sequences,
			int num_chunks, Writer writer) throws IOException {

		List<List<Sequence>> chunks = ListUtils.chunk(sequences, num_chunks);

		MorphResult result = null;
		
		for (int i = 0; i < num_chunks; i++) {
			
			if (options.getVerbose()) {
				System.err.format("Processing chunk %d\n", i);
			}

			List<Sequence> chunk = chunks.get(i);
			List<Sequence> complement = ListUtils.complement(chunks, i);

			Tagger tagger = MorphModel.train(options, complement, chunk);

			for (Sequence sequence : chunk) {
				Annotator.annotate(tagger, sequence, writer);
			}
			
			if (options.getVerbose()) {
				MorphEvaluator eval = new MorphEvaluator(chunk);
				
				MorphResult chunk_result = eval.eval(tagger);
				if (result == null) {
					result = chunk_result;
				} else {
					result.increment(chunk_result);
				}
			}
			
			if (result != null) {
				System.err.println();
				System.err.println();
				System.err.println("Overall results:");
				System.err.println(result);
			}

		}
	}

}
