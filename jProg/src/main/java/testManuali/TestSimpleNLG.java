package testManuali;

import java.util.Scanner;

import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

/*
 * 	a = id
	b = pos-tag ?				SENTENCE PLAN
	c = stringa
	d = feature
 */

public class TestSimpleNLG {

	public static void main(String[] args) {

		Scanner myObj = new Scanner(System.in); // Create a Scanner object

		Lexicon lexicon = Lexicon.getDefaultLexicon();
		NLGFactory nlgFactory = new NLGFactory(lexicon);
		Realiser realiser = new Realiser(lexicon);

		System.out.println("Facciamo lavorare simpleNLG");
		SPhraseSpec p = nlgFactory.createClause();
		p.setSubject("Jax");
		p.setVerb("wipe out");
		p.setObject("his Harley");

		// Aggiungiamo la feature relativa al tempo verbale PAST, PRESENT, FUTURE
		p.setFeature(Feature.TENSE, Tense.PAST);

		String output2 = realiser.realiseSentence(p); // Realiser created earlier.
		System.out.println(output2);

		String translated = null;
		String itas[] = { "ha fatto", "una", "mossa", "leale", "gli", "avanzi", "della", "vecchia", "repubblica",
				"sono stati spazzati via", "è", "la", "spada laser", "di tuo", "padre" };
		String eng[] = { "made", "a", "move", "fair", "the", "leftover", "of the", "old", "republic",
				"have been wiped out", "is", "", "lightsaber", "your", "father's" };
		int i;
		int l;
		l = itas.length;
		for (i = 0; i < l; i++) {
			switch (i) {
			case 0:
				translated = eng[0];
				System.out.println(translated);
			}

		}

		SPhraseSpec p2 = nlgFactory.createClause();
		p2.setSubject("He");
		p2.setVerb("make");
		NPPhraseSpec object = nlgFactory.createNounPhrase("a", "move");
		object.addPreModifier("fair");
		p2.setObject(object);
		p2.setFeature(Feature.TENSE, Tense.PAST);
		String out3 = realiser.realiseSentence(p2);
		System.out.println("\nModifier");
		System.out.println(out3);

	}

}
