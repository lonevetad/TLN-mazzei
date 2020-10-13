package testManuali;

import common.TintParserOutput;
import common.TreeParsedSentence;
import tools.JsonParserSimple;
import tools.TintParser;
import tools.json.JsonParserJackson;

public class TestTreeParsedSentence_FromTint {

	public static void main(String[] args) {
		JsonParserSimple jsonParser;
		String text;
		TintParserOutput parsifiedText;
		TreeParsedSentence treeParsedSent;
		jsonParser = new JsonParserJackson();

		text = MockedData.SENTENCES[MockedData.SENTENCES.length - 1];
		System.out.println("start parsifying text:");
		System.out.println(text);
		System.out.println();

		parsifiedText = TintParser.parseText(text, jsonParser);
		if (parsifiedText == null) {
			System.out.println("parsifiedText is NULL");
			return;
		}
		System.out.println("parsed :D\n\n\n\n Now build the tree:");
		treeParsedSent = new TreeParsedSentence(parsifiedText.getSentences()[0]);

		System.out.println("built :D\n\n");
		System.out.println(treeParsedSent);
	}
}