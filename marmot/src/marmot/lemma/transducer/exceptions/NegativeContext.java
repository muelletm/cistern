package marmot.lemma.transducer.exceptions;


public class NegativeContext extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NegativeContext() {
		super("Negative context makes no sense, bro.");
	}
}
