package marmot.segmenter;

public class LowerStringNormalizer extends StringNormalizer {

	private static final long serialVersionUID = 1L;

	@Override
	public String normalize(String string) {
		return string.toLowerCase();
	}

}
