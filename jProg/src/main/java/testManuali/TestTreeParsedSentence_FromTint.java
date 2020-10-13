package testManuali;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import common.TintParserOutput;
import common.TreeParsedSentence;
import tools.JsonParserSimple;
import tools.LoggerMessages;
import tools.LoggerOnFile;
import tools.TintParser;
import tools.TintParser.TintParsedAndJSON;
import tools.json.JsonParserJackson;

public class TestTreeParsedSentence_FromTint {
	static final String FOLDER_PATH = "." + File.separator + "testTreeParsed";

	public static void main(String[] args) throws IOException {
		int index;
		JsonParserSimple jsonParser;
		TintParserOutput parsifiedText;
		TreeParsedSentence treeParsedSent;
		LoggerMessages log;
		TintParsedAndJSON parsedAndJson;
		jsonParser = new JsonParserJackson();
		File folder = new File(FOLDER_PATH);
		folder.mkdirs();
		index = 0;
		for (String text : MockedData.SENTENCES) {
			log = new LoggerOnFile(FOLDER_PATH + File.separator + index + ".txt");
			log.logAndPrint("START");
//			log.logAndPrint("\n\n\n");
//			for (int i = 0; i < 10; i++) {
//				log.logAndPrint(" --------------------------------------------------------------------------\n");
//			}
			log.logAndPrint("start parsifying text:");
			log.logAndPrint(text);

			parsedAndJson = TintParser.parseText(text, jsonParser);
			if (parsedAndJson == null) {
				log.logAndPrint("parsedAndJson is NULL");
				return;
			}
			parsifiedText = parsedAndJson.tintParsed;
			log.logAndPrint("parsed :D\n\n\n JSON:");
			log.logAndPrint(parsedAndJson.json);
			log.logAndPrint("\n\n Now build the tree:");
			treeParsedSent = new TreeParsedSentence(parsifiedText.getSentences()[0]);

			log.logAndPrint("built :D\n\n");
			log.logAndPrint(Objects.toString(treeParsedSent));
			index++;
		}
	}
}