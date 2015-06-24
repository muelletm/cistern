package marmot.segmenter;

public class LowerStringNormalizer extends StringNormalizer {

	@Override
	public String normalize(String string) {
		return string.toLowerCase();
	}

}
