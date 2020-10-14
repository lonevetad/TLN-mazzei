package testManuali;

import common.TintParser;
import common.TintParser.TintParsedAndJSON;
import common.TreeParsedSentence;
import simpleNLG.TransferTranslationItEng;
import simplenlg.framework.DocumentElement;
import simplenlg.phrasespec.SPhraseSpec;
import tools.JsonParserSimple;
import tools.WordTranslatorItEng;
import tools.json.JsonParserJackson;

public class TestTransfer {

	public static void main(String[] args) {
		WordTranslatorItEng dictionary;
		TintParsedAndJSON parsedAndJson;
		JsonParserSimple jsonParser;
		TreeParsedSentence treeParsedSent;
		String text;
		//
		dictionary = WordTranslatorItEng.getInstance();
		dictionary.forEachTranslation((from, to) -> { System.out.println(from + " -> " + to); });

		jsonParser = new JsonParserJackson();
		text = MockedData.SENTENCES[2];
		parsedAndJson = TintParser.parseText(text, jsonParser);
		System.out.println("parsing:::: " + text);

		treeParsedSent = new TreeParsedSentence(parsedAndJson.tintParsed.getSentences()[0]);
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
}