package experimental.analyzer.tagger;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import experimental.analyzer.Analyzer;
import experimental.analyzer.AnalyzerInstance;
import experimental.analyzer.AnalyzerReading;
import experimental.analyzer.AnalyzerTag;

import marmot.core.FeatureVector;
import marmot.core.State;
import marmot.core.lattice.ZeroOrderSumLattice;
import marmot.morph.MorphModel;
import marmot.morph.MorphTagger;
import marmot.morph.MorphWeightVector;
import marmot.morph.Sentence;
import marmot.morph.Word;
import marmot.util.SymbolTable;

public class TaggerAnalyzer implements Analyzer {

	private static final long serialVersionUID = 1L;
	private MorphTagger tagger_;
	private double log_threshold_;

	public TaggerAnalyzer(MorphTagger tagger, double threshold) {
		tagger_ = tagger;
		log_threshold_ = Math.log(threshold);
	}

	@Override
	public Collection<AnalyzerReading> analyze(AnalyzerInstance instance) {
		MorphModel model = (MorphModel) tagger_.getModel();
		
		Word word = new Word(instance.getForm(), null, null);
		model.addIndexes(word, false);
		Sentence sentence = new Sentence(Collections.singletonList(word));
		
		ZeroOrderSumLattice lattice = (ZeroOrderSumLattice) tagger_.getSumLattice(false, sentence);

		List<List<State>> states = lattice.prune(log_threshold_);
		assert states.size() == 2 : states;
		List<State> tags = states.get(0);
		
		SymbolTable<String> pos_table = model.getTagTables().get(0);
		SymbolTable<String> morph_table = model.getTagTables().get(1);
		
		Collection<AnalyzerReading> readings = new LinkedList<>();
		for (State state : tags) {
			int morph_index = state.getIndex();
			int tag_index = state.getSubLevelState().getIndex();
			String pos_tag = pos_table.toSymbol(tag_index);
			String morph_tag = morph_table.toSymbol(morph_index);
			AnalyzerTag tag = new AnalyzerTag(pos_tag, morph_tag);
			AnalyzerReading reading = new AnalyzerReading(tag, null);
			readings.add(reading);
		}
		
		return readings;
	}

	@Override
	public String represent(AnalyzerInstance instance) {
		MorphModel model = (MorphModel) tagger_.getModel();
		Word word = new Word(instance.getForm(), null, null);
		model.addIndexes(word, false);
		Sentence sentence = new Sentence(Collections.singletonList(word));

		MorphWeightVector weights = (MorphWeightVector) tagger_.getWeightVector();
		
		FeatureVector vector = weights.extractStateFeatures(sentence, 0);
		return vector.toString();
	}

	@Override
	public int getNumTags() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isUnknown(AnalyzerInstance instance) {
		MorphModel model = (MorphModel) tagger_.getModel();
		int form_index = model.getWordTable().toIndex(instance.getForm());
		return model.isOOV(form_index);
	}

}
