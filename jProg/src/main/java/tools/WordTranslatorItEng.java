package tools;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

import tools.lineReader.LineReaderFromFile;

public class WordTranslatorItEng implements Function<String, String> {
	protected static WordTranslatorItEng SINGLETON_1;

	public static WordTranslatorItEng getInstance() {
		if (SINGLETON_1 == null)
			SINGLETON_1 = new WordTranslatorItEng();
		return SINGLETON_1;
	}

	protected WordTranslatorItEng() {
		this.dictionary = newDictionary();
		loadDictionary();
	}

	protected Map<String, String> dictionary;

	@Override
	public String apply(String t) {
		String tr;
		tr = dictionary.get(t);
		return tr != null ? tr : t;
	}

	public void forEachTranslation(BiConsumer<String, String> translationCosumer) {
		dictionary.forEach(translationCosumer);
	}

	///

	protected Map<String, String> newDictionary() { return new TreeMap<>(Misc.STRING_COMPARATOR); }

	protected void loadDictionary() {
		LineReader lineReader;
		lineReader = new LineReaderFromFile(Misc.RESOURCE_PATH + "dictionaryItEng1.txt");
		lineReader.forEach(this::parseDictionaryLine);
	}

	protected void addTranslation(String from, String to) { this.dictionary.put(from, to); }

	protected void parseDictionaryLine(String line) {
		int optionalWordSignIndex;
		String[] splitted;
		String lhs, rhs;
		if ((line = line.trim()).length() == 0 || line.isBlank() || line.isEmpty())
			return;
		splitted = line.split("->");
		if (splitted.length < 2)
			return;

		lhs = splitted[0].trim();
		rhs = splitted[1].trim();
		// rhs is simple, lhs .. no
		splitted = lhs.split("\\|");
		for (String pseudoSynonim : splitted) {
			pseudoSynonim = pseudoSynonim.trim();
			// look for optional words: using a + sign
			optionalWordSignIndex = pseudoSynonim.indexOf('+');
			if (optionalWordSignIndex >= 0) {
				String mandatoryWord;
				mandatoryWord = pseudoSynonim.substring(0, optionalWordSignIndex).trim();
				addTranslation(mandatoryWord, rhs);
				addTranslation(mandatoryWord + " " + pseudoSynonim.substring(optionalWordSignIndex + 1).trim(), rhs);
			} else {
				addTranslation(pseudoSynonim, rhs);
			}
		}
	}

}