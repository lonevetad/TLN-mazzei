package testPers;
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
			textes = new String[] { "I topi non avevano nipoti.", //
					"Ho sostenuto l'esame con successo.", //
					"Sembra difficile il laboratorio.", //
					"Sembra che il laboratorio sia difficile.", //
					"Talvolta parsificare le frasi richiede un profondo livello di ricorsione.", //
					"Ma a per mio, una nave cotale, renderebbe ques'altra un po' superflua, non è vero?", //
					"Il film da cui la frase precedente è stata tratta è un successo mondiale.", //
					"Parlè?", //
					"Ho visto il film ma non so se sono mai stati scritti dei libri a proposito o se la storia è stata inventata di sana pianta.", //
					"Paolo ama Francesca.", //
					"Paolo ama il calcio.", //
					"Paolo ha una carenza di calcio.", //
					"Il calcio ha una carenza di Paolo", //
					"Ho un amico di penna.", //
					"Il giorno in cui passerò l'esame mi ubriacherò.", //
					"Ho imparato a risolvere il cubo di Rubik agilmente durante le scuole superiori.", //
					"Ho poca fantasia." //
			};
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
