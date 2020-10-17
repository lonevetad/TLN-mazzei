package testManuali;

import simpleNLG.TransferTranslationItEng;
import simplenlg.framework.DocumentElement;
import simplenlg.phrasespec.SPhraseSpec;

public class TestTransfer {

	public static void main(String[] args) {

		SPhraseSpec rootClause=TransferTranslationItEng.transferTranslateItEng(t, wordTranslator)
		DocumentElement sentence =	TransferTranslationItEng.NLG_FACTORY.createSentence(sentence);
		String translated = TransferTranslationItEng.REALISER.realiseSentence(sentence);
		System.out.println(translated);
	}
}
