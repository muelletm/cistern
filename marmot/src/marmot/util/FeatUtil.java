package marmot.util;

public class FeatUtil {
	
	public static int getMaxSignature(boolean special_signature) {
		return (special_signature) ? 64 : 32;
	}

	public static int getSignature(String form, boolean special_signature) {
		int signature = 0;

		if (special_signature) {
			if (StringUtils.containsSpecial(form)) {
				signature += 1;
			}
			signature *= 2;
		}

		if (StringUtils.containsDigit(form)) {
			signature += 1;
		}
		signature *= 2;
		if (StringUtils.containsHyphon(form)) {
			signature += 1;
		}
		signature *= 2;
		if (StringUtils.containsUpperCase(form)) {
			signature += 1;
		}
		signature *= 2;
		if (StringUtils.containsLowerCase(form)) {
			signature += 1;
		}
		
		return signature;
	}

	public static short[] getCharIndexes(String form,
			SymbolTable<Character> char_table_, boolean insert) {
		short[] chars = new short[form.length()];
		for (int i = 0; i < form.length(); i++) {
			chars[i] = (short) char_table_.toIndex(form.charAt(i), -1, insert);
		}
		return chars;
	}
	
}
