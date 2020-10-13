package testManuali;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import edu.stanford.nlp.pipeline.Annotation;
import eu.fbk.dh.tint.runner.TintPipeline;
import eu.fbk.dh.tint.runner.TintRunner;

public class TestPipline {
	public static void main(String[] args) {
		// Initialize the Tint pipeline
		OutputStream os;
		String[] textes;
		TintPipeline pipeline = new TintPipeline();
		os = System.out;

		// Load the default properties
		// see
		// https://github.com/dhfbk/tint/blob/master/tint-runner/src/main/resources/default-config.properties
		try {
			pipeline.loadDefaultProperties();

			// Add a custom property
			// pipeline.setProperty("my_property", "my_value");

			// Load the models
			pipeline.load();

			// Use for example a text in a String
			textes = MockedData.SENTENCES;
			int i = 0;
			long startTime;
			for (String text : textes) {

				// Get the original Annotation (Stanford CoreNLP)
				System.out.println("\n\n\n\n tetsting:");
				System.out.println(text);
				Annotation stanfordAnnotation = pipeline.runRaw(text);
				System.out.println("stanfordAnnotation:");
				System.out.println(stanfordAnnotation);

				// **or**

				// Get the JSON
				// (optionally getting the original Stanford CoreNLP Annotation as return value)
				InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
				os = new FileOutputStream(new File("test TINT" + File.separator + i++ + " -.txt"));
				os.write(("parsing:\n" + text + "\n\n").getBytes());
				os.flush();
				startTime = System.currentTimeMillis();
				Annotation annotation = pipeline.run(stream, os, TintRunner.OutputFormat.JSON);
				startTime = System.currentTimeMillis() - startTime;
				os.write(("\n\n\ntook " + startTime + " milliseconds.").getBytes());
				os.flush();
				System.out.println("annotation:");
				System.out.println(annotation.toString());
				os.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
