package translators;

import java.io.File;
import java.io.IOException;

import common.SentenceParsed;
import common.TintParser;
import common.TintParser.TintParsedAndJSON;
import simplenlg.framework.DocumentElement;
import simplenlg.phrasespec.SPhraseSpec;
import testManuali.MockedData;
import tools.JsonParserSimple;
import tools.LoggerMessages;
import tools.impl.LoggerOnFile;
import tools.json.JsonParserJackson;
import translators.mainTransl.TranslatorTransferItEng;
import translators.v1.TransferTranslationItEng_V1;
import translators.v2.TransferTranslationRuleBased_V5;

/**
 * ======= THE MAIN TESTER
 * <p>
 * <ol>
 * <li>legge da file la frase</li>
 * <li>la parsifica con Tint e ne raccoglie l'output (JSON)</li>
 * <li>converte il Json in un oggetto utilizzabile (il tutto, nella funzione
 * {@link TintParser#parseText(String, JsonParserSimple)}</li>
 * <li>con "TransferTranslationItEng.NLG_FACTORY.createSentence" si fa il
 * transfer per ottenere la frase parsificata, in inglese ora, ma che sia un
 * oggetto utilizzabile dalla libreria SimpleNlg</li>
 * <li>usando SimpleNLG, si usa il Realizer per generare la frase tradotta</li>
 * <li>visualizzazione della frase tradotta</li>
 * </ol>
 */
public class TestTranslate_TintThenSimplenlg {
	static final String FOLDER_PATH = "." + File.separator + "translationFirstWay" + File.separator;

	static final ITranslator[] translatorsVersions = { // 0 index always hold the final version
			new TranslatorTransferItEng(), //
			new TransferTranslationItEng_V1(), //
			new TransferTranslationRuleBased_V5() //
	};

	public static void main(String[] args) throws IOException {
//		int sentenceIndex;
		int translatorIndex;
		WordTranslatorItEng dictionary;
		TintParsedAndJSON parsedAndJson;
		JsonParserSimple jsonParser;
		SentenceParsed treeParsedSent;
		String text;
		File folder;
		LoggerMessages log;

		System.out.println("start test");
		translatorIndex = 0;
		folder = new File(FOLDER_PATH);
		if (!folder.exists()) { folder.mkdirs(); }
		//
		dictionary = WordTranslatorItEng.getInstance();
//		dictionary.forEachTranslation((from, to) -> { log.logAndPrint(from + " -> " + to); });

		jsonParser = new JsonParserJackson();
//		sentenceIndex = 19;
//		sentenceIndex = MockedData.SENTENCES.length - 1; // 2

//		for (int sentenceIndex : new int[] { 18, 19, 20 }) {
		for (int sentenceIndex = 0; sentenceIndex < MockedData.SENTENCES.length; sentenceIndex++) {
			log = new LoggerOnFile(FOLDER_PATH + sentenceIndex + ".txt");
			log.logAndPrint("\n\n\n--------------------------------\n start parsing :D");
			text = MockedData.SENTENCES[sentenceIndex];
			parsedAndJson = TintParser.parseText(text, jsonParser);
			log.logAndPrint("parsing:::: " + text);

			treeParsedSent = new SentenceParsed(parsedAndJson.tintParsed.getSentences()[0]);
			log.logAndPrint("parsed tree: ");
			log.logAndPrint(String.valueOf(treeParsedSent));
			//
			log.logAndPrint("\n\n\n now translating into SimpleNLP");
			SPhraseSpec rootClause = translatorsVersions[translatorIndex].translateItEng(treeParsedSent, dictionary);

			log.logAndPrint("nlp future input:");
			log.logAndPrint(String.valueOf(rootClause));
			DocumentElement sentence = BasicStuffTranslators.NLG_FACTORY_IT.createSentence(rootClause);
			log.logAndPrint("\n\n\n\n sentence created:");
			log.logAndPrint(String.valueOf(sentence));

			log.logAndPrint("\n\n\n realize translation");
			String translated = BasicStuffTranslators.REALISER_IT.realiseSentence(sentence);
			log.logAndPrint("translated:");
			log.logAndPrint(translated);
		}
		System.out.println("FINISH\n\n\n END");
	}
}