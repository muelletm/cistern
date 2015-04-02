package marmot.lemma.transducer.exceptions;

@SuppressWarnings("serial")
public class LabelBiasException extends Exception {
	public LabelBiasException() {
		super("The lower right hand context (C4) must be 0 in locally normalized models.");
	}
}
