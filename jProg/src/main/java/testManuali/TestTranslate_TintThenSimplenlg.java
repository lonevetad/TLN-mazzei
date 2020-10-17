package testManuali;

import common.TintParser;
import common.TintParser.TintParsedAndJSON;
import common.SentenceParsed;
import simpleNLG.TransferTranslationItEng;
import simplenlg.framework.DocumentElement;
import simplenlg.phrasespec.SPhraseSpec;
import tools.JsonParserSimple;
import tools.MockedData;
import tools.json.JsonParserJackson;
import translators.WordTranslatorItEng;

/**
 * THE MAIN TESTEEEEEEEER *
 */
public class TestTranslate_TintThenSimplenlg {

	public static void main(String[] args) {
//		int sentenceIndex;
		WordTranslatorItEng dictionary;
		TintParsedAndJSON parsedAndJson;
		JsonParserSimple jsonParser;
		SentenceParsed treeParsedSent;
		String text;
		System.out.println("start test");
		//
		dictionary = WordTranslatorItEng.getInstance();
//		dictionary.forEachTranslation((from, to) -> { System.out.println(from + " -> " + to); });

		jsonParser = new JsonParserJackson();
//		sentenceIndex = 19;
//		sentenceIndex = MockedData.SENTENCES.length - 1; // 2
		for (int sentenceIndex : new int[] { 18, 19, 20 }) {
			System.out.println("\n\n\n--------------------------------\n start parsing :D");
			text = MockedData.SENTENCES[sentenceIndex];
			parsedAndJson = TintParser.parseText(text, jsonParser);
			System.out.println("parsing:::: " + text);

			treeParsedSent = new SentenceParsed(parsedAndJson.tintParsed.getSentences()[0]);
			System.out.println("parsed tree: ");
			System.out.println(treeParsedSent);
			//
			System.out.println("\n\n\n now transfering");
			SPhraseSpec rootClause = TransferTranslationItEng.transferTranslateItEng(treeParsedSent, dictionary);

			System.out.println("transfered:");
			System.out.println(rootClause);
			DocumentElement sentence = TransferTranslationItEng.NLG_FACTORY.createSentence(rootClause);
			System.out.println("\n\n\n\n sentence created:");
			System.out.println(sentence);

			System.out.println("\n\n\n realize translation");
			String translated = TransferTranslationItEng.REALISER.realiseSentence(sentence);
			System.out.println("translated:");
			System.out.println(translated);
		}
		System.out.println("FINISH\n\n\n END");
	}
}