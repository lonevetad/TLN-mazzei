package tools;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import common.TintParserOutput;
import eu.fbk.dh.tint.runner.TintPipeline;
import eu.fbk.dh.tint.runner.TintRunner;

public class TintParser {
	private static final TintPipeline pipeline;

	static {
		pipeline = new TintPipeline();
		try {
			pipeline.loadDefaultProperties();
			pipeline.load();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static TintParserOutput parseText(String text, JsonParserSimple jsonParser) {
		OutputStreamCollector toJsonCollector;
		toJsonCollector = new OutputStreamCollector();
		InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
		try {
//			Annotation annotation =
			pipeline.run(stream, toJsonCollector, TintRunner.OutputFormat.JSON);
			String jsonCollected = toJsonCollector.toString();
			toJsonCollector = null;
			TintParserOutput parsifiedStringRepresentation;
			parsifiedStringRepresentation = jsonParser.parseJson(jsonCollected, TintParserOutput.class);
			return parsifiedStringRepresentation;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}