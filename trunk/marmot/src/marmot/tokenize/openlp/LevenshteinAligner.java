package marmot.tokenize.openlp;

import java.util.LinkedList;
import java.util.List;

import marmot.util.LevenshteinLattice;
import marmot.util.StringUtils;
import marmot.tokenize.preprocess.LanguageNormalizer;

public class LevenshteinAligner implements Aligner {

	private long timeout_ = 1000;
	private LanguageNormalizer norm_; // instance of normalizer-class
	private String lang_; 			  // language in which this aligner works
	
	public LevenshteinAligner(String lang) { // adding language identifier
		this(1000, lang);
	}
	
	public LevenshteinAligner(long timeout, String lang) { // adding language identifier
		timeout_ = timeout;
		norm_ = new LanguageNormalizer(); // initializing normalizer-class
		lang_ = lang;					  // assigning language identifier	
	}
	
	private static final String TIMEOUT_STRING = "<TIMEOUT>";
	private static final List<Character> TIMEOUT_LIST = new LinkedList<Character>();
	static {
		for (int i=TIMEOUT_STRING.length()- 1; i>=0; i--) {
			TIMEOUT_LIST.add(TIMEOUT_STRING.charAt(i));
		}
	}
	
	class SpecialLevenshteinLattice extends LevenshteinLattice {
		
		
		private long timeout_;
		
		public SpecialLevenshteinLattice(String input, String output, long timeout) {
			super(input, output, 2, 2, 3);
			timeout_ = timeout;
		}

		@Override
		protected int getReplaceCost(char input, char output) {

			if (output == ' ' || input == ' ') {
				return 1000;
			}

			return super.getReplaceCost(input, output);
		}

		class State {
			int input_index;
			int output_index;
			List<Character> current_path;

			public State getNewState(char op, int input_diff, int output_diff) {

				State state = new State();

				state.input_index = input_index + input_diff;
				state.output_index = output_index + output_diff;
				state.current_path = new LinkedList<Character>(current_path);
				state.current_path.add(op);

				return state;

			}
		}

		@Override
		public String searchOperationSequence() {
			init();

			State state = new State();
			state.input_index = input_.length();
			state.output_index = output_.length();
			state.current_path = new LinkedList<Character>();

			List<State> states = new LinkedList<State>();
			states.add(state);

			List<Character> seq = searchOperationSequence(states);

			if (seq == null) {
				return null;
			}
			
			StringBuilder sb = new StringBuilder(seq.size());
			for (char c : seq) {
				sb.append(c);
			}
			sb.reverse();

			return sb.toString();
		}

		private List<Character> searchOperationSequence(List<State> states) {

			System.err.println(input_);
			System.err.println(output_);
			
			long time = System.currentTimeMillis();
			
			while (!states.isEmpty()) {
				State state = states.remove(0);
				
				long current_time = System.currentTimeMillis();
				if (current_time - time > timeout_) {
					return TIMEOUT_LIST;
				}

				short op = op_lattice_[state.input_index][state.output_index];

				if ((op & START) > 0) {
					assert op == START;
					return state.current_path;
				}

				if ((op & COPY) > 0) {
					states.add(state.getNewState('C', -1, -1));
				}

				if ((op & REPLACE) > 0) {
					states.add(state.getNewState('R', -1, -1));
				}

				if ((op & INSERT) > 0) {

					List<Character> current_path = state.current_path;

					if (output_.charAt(state.output_index - 1) == ' ') {
						states.add(state.getNewState('I', 0, -1));
					} else {

						if (current_path.size() > 0) {

							char last_op = current_path
									.get(current_path.size() - 1);

							char last_char = output_.charAt(state.output_index);
							
							if ((last_op == 'I' && last_char != ' ') || last_op == 'R') {
								states.add(state.getNewState('I', 0, -1));
							}

						}
					}

				}

				if ((op & DELETE) > 0) {
					List<Character> current_path = state.current_path;
					
					if (input_.charAt(state.input_index - 1) == ' ') {
						states.add(state.getNewState('D', -1, 0));
					} else {

						if (current_path.size() > 0) {

							char last_op = current_path
									.get(current_path.size() - 1);

							char last_char = input_.charAt(state.input_index);
							
							if ((last_op == 'D' && last_char != ' ') || last_op == 'R') {
								states.add(state.getNewState('D', -1, 0));
							}

						}
					}
				}
			}

			return null;
		}
	}

	@Override
	public Result align(String input, String output) {
		input = norm_.normalize(input, lang_); // normalizing tokenized input before alignment
		input = StringUtils.clean(input);
		output = StringUtils.clean(output);
		
		SpecialLevenshteinLattice lattice = new SpecialLevenshteinLattice(
				input, output, timeout_);

		String operations = lattice.searchOperationSequence();
		
		if (operations == null) {
			return new Result(ResultType.NoAlignmentFound);
		}
		
		if (operations.equals(TIMEOUT_STRING)) {
			return new Result(ResultType.Timeout);
		}

		List<Pair> pairs = new LinkedList<Pair>();

		int input_index = 0;
		int output_index = 0;

		for (int i = 0; i < operations.length(); i++) {
			char op = operations.charAt(i);

			switch (op) {
			case 'C':
				//System.err.format("c(%c,%c)\n", input.charAt(input_index),
				//		output.charAt(output_index));
				pairs.add(new Pair(input_index, output_index));
				input_index++;
				output_index++;
				break;
			case 'R':
				//System.err.format("r(%c,%c)\n", input.charAt(input_index),
				//		output.charAt(output_index));
				pairs.add(new Pair(input_index, output_index));
				input_index++;
				output_index++;
				break;
			case 'D':
				if (input.charAt(input_index) == ' ') {
					pairs.add(new Pair(input_index, - 1));
				} else {
					pairs.add(new Pair(input_index, output_index));
				}
				input_index++;
				break;
			case 'I':
				//System.err.format("i(%c)\n", output.charAt(output_index));
				if (output.charAt(output_index) == ' ') {
					pairs.add(new Pair(-1, output_index));
				} else {
					pairs.add(new Pair(input_index, output_index));
				}
				output_index++;
				break;
			}
		}

		return new Result(ResultType.Standard, pairs);
	}

}
