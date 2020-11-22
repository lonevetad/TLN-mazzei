package translators;

import java.util.function.Function;

import common.SentenceParsed;
import simplenlg.phrasespec.SPhraseSpec;

public interface ITranslator {
	public SPhraseSpec translateItEng(SentenceParsed t, Function<String, String> wordTranslator);
}
