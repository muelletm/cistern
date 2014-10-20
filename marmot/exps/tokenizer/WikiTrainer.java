package tokenizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.tokenize.TokenSampleStream;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

public class WikiTrainer {
	TokenizerModel model;
	
	WikiTrainer() {
	}
	
	@SuppressWarnings("deprecation")
	public void train() throws IOException {
		Charset charset = Charset.forName("UTF-8");
		ObjectStream<String> lineStream = new PlainTextByLineStream(
				new FileInputStream("data/text/unTokCorpus.txt"), charset);
		ObjectStream<TokenSample> sampleStream = new TokenSampleStream(lineStream);

		try {
		  model = TokenizerME.train("en", sampleStream, true, TrainingParameters.defaultParams());
		}
		finally {
		  sampleStream.close();
		}
//
//		OutputStream modelOut = null;
//		try {
//		  modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));
//		  model.serialize(modelOut);
//		} finally {
//		  if (modelOut != null)
//		     modelOut.close();
//		}
	}
}
