package common;

import edu.emory.mathcs.backport.java.util.Arrays;
import tools.MockedData;

public class TranslatorBasic {

	public static void main(String[] args) {
		System.out.println("START");
		for (String s : MockedData.SENTENCES) {
			System.out.println("\n\n\n\n translating ..");
			System.out.println("----- " + s);
			System.out.println("into ...");
			System.out.println('\t' + translate(s));
		}
		System.out.println("TEST");
	}

	public static String translate(String text) {
		int len, i;
		WordTranslatorItEng dictionary;
		String[] splitted;
		StringBuilder sb;
		dictionary = WordTranslatorItEng.getInstance();
		splitted = text.split("\\s+");
		System.out.println(Arrays.toString(splitted));
		len = splitted.length;
		sb = new StringBuilder(len << 2);
		i = -1;
		while (++i < len) {
			if (i != 0)
				sb.append(' ');
//	splitted[len]=
			sb.append(dictionary.apply(splitted[i]));
		}
		return sb.toString();
	}
}