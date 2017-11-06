package com.muzi.indexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class NewString {
	public String str;

	NewString() {
		str = "";
	}
}

public class Stemmer {
	private static final String SSES = "sses";

	private static final String IES = "ies";

	private static final String EED = "eed";

	private static final String ED = "ed";

	private static final String ING = "ing";

	private static final String AT = "at";

	private static final String BL = "bl";

	private static final String IZ = "iz";

	private static final String Y = "y";

	private static final String E = "e";

	private static final String EMPTY = "";

	private static Stemmer stemmer;

	String[] prefixes = { "kilo", "micro", "milli", "intra", "ultra",
			"mega", "nano", "pico", "pseudo" };

	String[][] suffixes3 = { { "icate", "ic" }, { "ative", EMPTY },
			{ "alize", "al" }, { "alise", "al" }, { "iciti", "ic" },
			{ "ical", "ic" }, { "ful", EMPTY }, { "ness", EMPTY } };
		
	String[][] suffixes2 = { { "ational", "ate" }, { "tional", "tion" },
			{ "enci", "ence" }, { "anci", "ance" }, { "izer", "ize" },
			{ "iser", "ize" }, { "abli", "able" }, { "alli", "al" },
			{ "entli", "ent" }, { "eli", "e" }, { "ousli", "ous" },
			{ "ization", "ize" }, { "isation", "ize" }, { "ation", "ate" },
			{ "ator", "ate" }, { "alism", "al" }, { "iveness", "ive" },
			{ "fulness", "ful" }, { "ousness", "ous" }, { "aliti", "al" },
			{ "iviti", "ive" }, { "biliti", "ble" } };
	
	String[] suffixes4 = { "al", "ance", "ence", "er", "ic", "able", "ible",
			"ant", "ement", "ment", "ent", "sion", "tion", "ou", "ism",
			"ate", "iti", "ous", "ive", "ize", "ise" };

	
	
	private Stemmer() {
		
	}
	
	// Singleton design pattern
	public static Stemmer getInstance() {
		if(stemmer == null) {
			synchronized(Stemmer.class) {
				if(stemmer == null) {
					stemmer = new Stemmer();
				}
			}
		}
		return stemmer;
	}
	
	String Clean(String str) {
		int last = str.length();

		new Character(str.charAt(0));
		String temp = EMPTY;

		for (int i = 0; i < last; i++) {
			if (Character.isLetterOrDigit(str.charAt(i)))
				temp += str.charAt(i);
		}

		return temp;
	} // clean

	boolean hasSuffix(String word, String suffix, NewString stem) {

		String tmp = EMPTY;

		if (word.length() <= suffix.length())
			return false;
		if (suffix.length() > 1)
			if (word.charAt(word.length() - 2) != suffix
					.charAt(suffix.length() - 2))
				return false;

		stem.str = EMPTY;

		for (int i = 0; i < word.length() - suffix.length(); i++)
			stem.str += word.charAt(i);
		tmp = stem.str;

		for (int i = 0; i < suffix.length(); i++)
			tmp += suffix.charAt(i);

		if (tmp.compareTo(word) == 0)
			return true;
		else
			return false;
	}

	boolean vowel(char ch, char prev) {
		switch (ch) {
		case 'a':
		case 'e':
		case 'i':
		case 'o':
		case 'u':
			return true;
		case 'y': {

			switch (prev) {
			case 'a':
			case 'e':
			case 'i':
			case 'o':
			case 'u':
				return false;

			default:
				return true;
			}
		}

		default:
			return false;
		}
	}

	int measure(String stem) {

		int i = 0, count = 0;
		int length = stem.length();

		while (i < length) {
			for (; i < length; i++) {
				if (i > 0) {
					if (vowel(stem.charAt(i), stem.charAt(i - 1)))
						break;
				} else {
					if (vowel(stem.charAt(i), 'a'))
						break;
				}
			}

			for (i++; i < length; i++) {
				if (i > 0) {
					if (!vowel(stem.charAt(i), stem.charAt(i - 1)))
						break;
				} else {
					if (!vowel(stem.charAt(i), '?'))
						break;
				}
			}
			if (i < length) {
				count++;
				i++;
			}
		} // while

		return (count);
	}

	boolean containsVowel(String word) {

		for (int i = 0; i < word.length(); i++)
			if (i > 0) {
				if (vowel(word.charAt(i), word.charAt(i - 1)))
					return true;
			} else {
				if (vowel(word.charAt(0), 'a'))
					return true;
			}

		return false;
	}

	boolean cvc(String str) {
		int length = str.length();

		if (length < 3)
			return false;

		if ((!vowel(str.charAt(length - 1), str.charAt(length - 2)))
				&& (str.charAt(length - 1) != 'w')
				&& (str.charAt(length - 1) != 'x')
				&& (str.charAt(length - 1) != 'y')
				&& (vowel(str.charAt(length - 2), str.charAt(length - 3)))) {

			if (length == 3) {
				if (!vowel(str.charAt(0), '?'))
					return true;
				else
					return false;
			} else {
				if (!vowel(str.charAt(length - 3), str.charAt(length - 4)))
					return true;
				else
					return false;
			}
		}

		return false;
	}

	String step1(String str) {

		NewString stem = new NewString();

		if (str.charAt(str.length() - 1) == 's') {
			if ((hasSuffix(str, SSES, stem)) || (hasSuffix(str, IES, stem))) {
				String tmp = EMPTY;
				for (int i = 0; i < str.length() - 2; i++)
					tmp += str.charAt(i);
				str = tmp;
			} else {
				if ((str.length() == 1)
						&& (str.charAt(str.length() - 1) == 's')) {
					str = EMPTY;
					return str;
				}
				if (str.charAt(str.length() - 2) != 's') {
					String tmp = EMPTY;
					for (int i = 0; i < str.length() - 1; i++)
						tmp += str.charAt(i);
					str = tmp;
				}
			}
		}

		if (hasSuffix(str, EED, stem)) {
			if (measure(stem.str) > 0) {
				String tmp = EMPTY;
				for (int i = 0; i < str.length() - 1; i++)
					tmp += str.charAt(i);
				str = tmp;
			}
		} else {
			if ((hasSuffix(str, ED, stem)) || (hasSuffix(str, ING, stem))) {
				if (containsVowel(stem.str)) {

					String tmp = EMPTY;
					for (int i = 0; i < stem.str.length(); i++)
						tmp += str.charAt(i);
					str = tmp;
					if (str.length() == 1)
						return str;

					if ((hasSuffix(str, AT, stem))
							|| (hasSuffix(str, BL, stem))
							|| (hasSuffix(str, IZ, stem))) {
						str += E;

					} else {
						int length = str.length();
						if ((str.charAt(length - 1) == str.charAt(length - 2))
								&& (str.charAt(length - 1) != 'l')
								&& (str.charAt(length - 1) != 's')
								&& (str.charAt(length - 1) != 'z')) {

							tmp = EMPTY;
							for (int i = 0; i < str.length() - 1; i++)
								tmp += str.charAt(i);
							str = tmp;
						} else if (measure(str) == 1) {
							if (cvc(str))
								str += E;
						}
					}
				}
			}
		}

		if (hasSuffix(str, Y, stem))
			if (containsVowel(stem.str)) {
				String tmp = EMPTY;
				for (int i = 0; i < str.length() - 1; i++)
					tmp += str.charAt(i);
				str = tmp + "i";
			}
		return str;
	}

	String step2(String str) {
		NewString stem = new NewString();

		for (int index = 0; index < suffixes2.length; index++) {
			if (hasSuffix(str, suffixes2[index][0], stem)) {
				if (measure(stem.str) > 0) {
					str = stem.str + suffixes2[index][1];
					return str;
				}
			}
		}

		return str;
	}

	String step3(String str) {

		NewString stem = new NewString();

		for (int index = 0; index < suffixes3.length; index++) {
			if (hasSuffix(str, suffixes3[index][0], stem))
				if (measure(stem.str) > 0) {
					str = stem.str + suffixes3[index][1];
					return str;
				}
		}
		return str;
	}

	String step4(String str) {

		
		NewString stem = new NewString();

		for (int index = 0; index < suffixes4.length; index++) {
			if (hasSuffix(str, suffixes4[index], stem)) {

				if (measure(stem.str) > 1) {
					str = stem.str;
					return str;
				}
			}
		}
		return str;
	}

	String step5(String str) {

		if (str.charAt(str.length() - 1) == 'e') {
			if (measure(str) > 1) {/*
									 * measure(str)==measure(stem) if ends in
									 * vowel
									 */
				String tmp = EMPTY;
				for (int i = 0; i < str.length() - 1; i++)
					tmp += str.charAt(i);
				str = tmp;
			} else if (measure(str) == 1) {
				String stem = EMPTY;
				for (int i = 0; i < str.length() - 1; i++)
					stem += str.charAt(i);

				if (!cvc(stem))
					str = stem;
			}
		}

		if (str.length() == 1)
			return str;
		if ((str.charAt(str.length() - 1) == 'l')
				&& (str.charAt(str.length() - 2) == 'l') && (measure(str) > 1))
			if (measure(str) > 1) {/*
									 * measure(str)==measure(stem) if ends in
									 * vowel
									 */
				String tmp = EMPTY;
				for (int i = 0; i < str.length() - 1; i++)
					tmp += str.charAt(i);
				str = tmp;
			}
		return str;
	}

	String stripPrefixes(String str) {

		int last = prefixes.length;
		for (int i = 0; i < last; i++) {
			if (str.startsWith(prefixes[i])) {
				String temp = EMPTY;
				for (int j = 0; j < str.length() - prefixes[i].length(); j++)
					temp += str.charAt(j + prefixes[i].length());
				return temp;
			}
		}

		return str;
	}

	private String stripSuffixes(String str) {

		str = step1(str);
		if (str.length() >= 1)
			str = step2(str);
		if (str.length() >= 1)
			str = step3(str);
		if (str.length() >= 1)
			str = step4(str);
		if (str.length() >= 1)
			str = step5(str);

		return str;
	}

	public String stripAffixes(String str) {

		str = str.toLowerCase();
		str = Clean(str);

		if ((str != EMPTY) && (str.length() > 2)) {
			str = stripPrefixes(str);

			if (str != EMPTY)
				str = stripSuffixes(str);

		}

		return str;
	} // stripAffixes

	public String getStem(String w) {
//		System.out.println("stemming for word ." + w + ".");
		String s1 = step1(w);
		String s2 = step2(s1);
		String s3 = step3(s2);
		String s4 = step4(s3);
		return step5(s4);
	}
	
	public static ArrayList<String> completeStem(List<String> list) {
		Stemmer St = new Stemmer();
		ArrayList<String> arrstr = new ArrayList<String>();
		for (String i : list) {
			String s1 = St.step1(i);
			String s2 = St.step2(s1);
			String s3 = St.step3(s2);
			String s4 = St.step4(s3);
			String s5 = St.step5(s4);
			System.out.println(i + ": " + s5);
			arrstr.add(s5);
		}
		return arrstr;
	}

	
	public static void main(String[] args) {
		
		String[] words = {
				"beautiful", "generated" , "walking" , "done" , "does", "world", "have",
				"inverted", "excess", "fully", "acess", "roses"

		};
		Stemmer.completeStem( Arrays.asList(words) );
	}
}
