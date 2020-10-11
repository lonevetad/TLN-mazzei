package testPers;

import java.io.IOException;
import java.io.OutputStream;

import eu.fbk.dh.tint.runner.TintPipeline;

public class TParsingJacksonDeserialization {

	public static void main(String[] args) {
		OutputStream os;
		String[] textes;
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}