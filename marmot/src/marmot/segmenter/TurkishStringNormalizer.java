package marmot.segmenter;

public class TurkishStringNormalizer extends StringNormalizer {

	@Override
	public String normalize(String string) {
		
		StringBuilder sb = new StringBuilder(string);
		
		for (int i=0;i<sb.length();i++) {
			
			char c = sb.charAt(i);
			
			switch (c) {
				case 'C':
					c = 'ç';
					break;
				case 'I':
					c = 'ı';
					break;
				case 'O':
					c = 'ö';
					break;
				case 'U':
					c = 'ü';
					break;
				case 'S':
					c = 'ş';
					break;
				case 'G':
					c = 'ğ';
					break;
				default:
			}
			
			assert !Character.isUpperCase(c) : c;
			sb.setCharAt(i, c);
		}
		
		return sb.toString();
	}

}
