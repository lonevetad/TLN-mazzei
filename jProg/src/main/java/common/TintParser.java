package common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import eu.fbk.dh.tint.runner.TintPipeline;
import eu.fbk.dh.tint.runner.TintRunner;
import tools.JsonParserSimple;
import tools.OutputStreamCollector;

/**
 * @ARTICLE{palmeromorettitint, author = {{Palmero Aprosio}, A. and {Moretti},
 *                              G.}, title = "{Italy goes to Stanford: a
 *                              collection of CoreNLP modules for Italian}",
 *                              journal = {ArXiv e-prints}, archivePrefix =
 *                              "arXiv", eprint = {1609.06204}, primaryClass =
 *                              "cs.CL", keywords = {Computer Science -
 *                              Computation and Language}, year = 2016, month =
 *                              sep, adsurl =
 *                              {http://adsabs.harvard.edu/abs/2016arXiv160906204P},
 *                              adsnote = {Provided by the SAO/NASA Astrophysics
 *                              Data System} }
 */
public class TintParser {
	private static final TintPipeline pipeline;

	static {
		pipeline = new TintPipeline();
		try {
			pipeline.loadDefaultProperties();
			Properties p = new Properties();
			p.put("charset", "utf-8");
			pipeline.addProperties(p);
			pipeline.load();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static TintParsedAndJSON parseText(String text, JsonParserSimple jsonParser) {
		OutputStreamCollector toJsonCollector;
		toJsonCollector = new OutputStreamCollector();
		text = Character.toLowerCase(text.charAt(0)) + text.substring(1);
		InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
		try {
//			Annotation annotation =
			pipeline.run(stream, toJsonCollector, TintRunner.OutputFormat.JSON);
			String jsonCollected = toJsonCollector.toString();
			toJsonCollector = null;
			TintParserOutput parsifiedStringRepresentation;
			parsifiedStringRepresentation = jsonParser.parseJson(jsonCollected, TintParserOutput.class);
			return new TintParsedAndJSON(jsonCollected, parsifiedStringRepresentation);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static class TintParsedAndJSON {
		public final String json;
		public final TintParserOutput tintParsed;

		protected TintParsedAndJSON(String json, TintParserOutput tintParsed) {
			super();
			this.json = json;
			this.tintParsed = tintParsed;
		}
	}
}