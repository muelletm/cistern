package marmot.segmenter;

public class GermanStringNormalizer extends StringNormalizer {

	private static final long serialVersionUID = 1L;

	@Override
	public String normalize(String string) {
		string = string.toLowerCase();
		StringBuilder sb = new StringBuilder(string.length());

		for (int i = 0; i < string.length(); i++) {

			char c = string.charAt(i);

			switch (c) {
			case 'ö':
				sb.append("oe");
				break;
			case 'ü':
				sb.append("ue");
				break;
			case 'ä':
				sb.append("ae");
				break;
			case 'ß':
				sb.append("ss");
				break;
			default:
				sb.append(c);
			}
		}

		return sb.toString();
	}

}
