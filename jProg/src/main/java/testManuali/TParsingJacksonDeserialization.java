package testManuali;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;

import common.TintParserOutput;
import edu.stanford.nlp.pipeline.Annotation;
import eu.fbk.dh.tint.runner.TintPipeline;
import eu.fbk.dh.tint.runner.TintRunner;
import tools.OutputStreamCollector;

public class TParsingJacksonDeserialization {

	public static void main(String[] args) {
		String[] textes;
		OutputStreamCollector toJsonCollector;
		textes = MockedData.SENTENCES;

		TintPipeline pipeline = new TintPipeline();

		// Load the default properties
		// see
		// https://github.com/dhfbk/tint/blob/master/tint-runner/src/main/resources/default-config.properties
		try {
			pipeline.loadDefaultProperties();

			// Add a custom property
			// pipeline.setProperty("my_property", "my_value");

			// Load the models
			pipeline.load();

			// AND NOW
			toJsonCollector = new OutputStreamCollector();
			InputStream stream = new ByteArrayInputStream(textes[textes.length - 1].getBytes(StandardCharsets.UTF_8));
			Annotation annotation = pipeline.run(stream, toJsonCollector, TintRunner.OutputFormat.JSON);

			System.out.println("collected a json");
			String jsonCollected = toJsonCollector.toString();
			toJsonCollector = null;
			System.gc();
			System.out.println(jsonCollected);
			System.out.println("now deserialize");
			TintParserOutput parsifiedStringRepresentation;
			parsifiedStringRepresentation = (new ObjectMapper()).readValue(jsonCollected, TintParserOutput.class);
			System.out.println("done:");
			System.out.println(parsifiedStringRepresentation);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//

	static class OSForJson1 extends OutputStream {
		// ObjectOutputStream
		int size;
		int[] backArray; // like arraylist

		OSForJson1() { this(16); }

		OSForJson1(int startSize) {
			this.size = 0;
			this.backArray = new int[Math.max(4, startSize)];
		}

		@Override
		public void write(int b) throws IOException {
			try {
				int i;
				int[] ba;
				ba = backArray;
				if ((i = size) == ba.length) {
					ba = new int[i + (i >> 1)];
					System.arraycopy(backArray, 0, ba, 0, i); // grow
					this.backArray = ba;
				}
				ba[size++] = b;
			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}

	}
}