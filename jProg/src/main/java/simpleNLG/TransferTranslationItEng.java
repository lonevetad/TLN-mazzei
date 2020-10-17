package simpleNLG;

import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

import common.SentenceParsed;
import common.SentenceParsed.NodeParsedSentFromTint;
import edu.stanford.nlp.coref.data.Dictionaries.Person;
import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.XMLLexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;
import tools.Misc;

/** First non-naive version of a translator. */
public class TransferTranslationItEng {
	public static final Lexicon LEXICON = new XMLLexicon(Paths.get(Misc.RESOURCE_PATH + "LexiconIt.txt").toUri());
	public static final NLGFactory NLG_FACTORY = new NLGFactory(LEXICON);
	public static final Realiser REALISER = new Realiser(LEXICON);

	public static SPhraseSpec transferTranslateItEng(SentenceParsed t, Function<String, String> wordTranslator) {
		SPhraseSpec rootOfPhrase;
		rootOfPhrase = NLG_FACTORY.createClause();
		System.out.println("\n\n\n t.root è .. " + t.getRoot());
		translateSentenceTree(wordTranslator, t.getRoot(), rootOfPhrase);
		return rootOfPhrase;
	}

	protected static void translateSentenceTree(Function<String, String> wordTranslator,
			SentenceParsed.NodeParsedSentFromTint subtreeRoot, final PhraseElement subPhraseRoot) {
//main stuffs
//		boolean tempCacheFlag;
		String posNodeType, nodeGloss, translatedNodeGloss, translatedChildGloss;
		Iterator<NodeParsedSentFromTint> childrenIterator;
		NodeParsedSentFromTint child;
		// the following three could be created
		NPPhraseSpec nounPhrase = null;
		VPPhraseSpec verbPhrase = null;
		PPPhraseSpec prepositionPhrase = null;

		if (subPhraseRoot == null) {
			System.out.println("subPhraseRoot is null .... o.o");
			return;
		}

		posNodeType = subtreeRoot.getDep();
		nodeGloss = subtreeRoot.getGloss();
		translatedNodeGloss = null;

// first of all, check if the root is the "nominal modifier" or "direct object"
		if ("nmod".equals(posNodeType) || "dobj".equals(posNodeType)) {
			nounPhrase = NLG_FACTORY.createNounPhrase(translatedNodeGloss = wordTranslator.apply(nodeGloss));
		}

		System.out.println("subtreeRoo______: " + subtreeRoot.getGloss());
		// child controls, interleaving some if(s
		childrenIterator = subtreeRoot.getChildrenIterator();
		if (childrenIterator != null)
			while (childrenIterator.hasNext()) { // is required to modify local variables: forEach forbids, iterator not
				child = childrenIterator.next();
				translatedChildGloss = null;
				System.out.println("\t child: " + child.getGloss() + ",,,, having posNodeType: " + posNodeType);

				if ("nsubj".equals(posNodeType)) {
					if (translatedNodeGloss == null) { translatedNodeGloss = wordTranslator.apply(nodeGloss); } // c&p
					verbPhrase = NLG_FACTORY.createVerbPhrase();
					verbPhrase.setVerb(translatedNodeGloss);
					setVerbFeaures(verbPhrase, subtreeRoot.getFeatures(), false);
					if (subPhraseRoot instanceof SPhraseSpec) {
						((SPhraseSpec) subPhraseRoot)
								.setSubject(translatedChildGloss = wordTranslator.apply(child.getGloss()));
					} else {
						System.out.print(
								"\t ###AAAAAAAH on " + subtreeRoot.getGloss() + " is " + posNodeType + " and ...");
						System.out.println("and is not a SPhraseSpec: " + subPhraseRoot.getClass().getSimpleName());
					}
					/*
					 * else if (subPhraseRoot instanceof VPPhraseSpec) { ((VPPhraseSpec)
					 * subPhraseRoot) .setSubject(translatedChildGloss =
					 * wordTranslator.apply(child.getGloss())); }
					 */
				}
				if ("aux".equals(posNodeType)) {
					verbPhrase = NLG_FACTORY.createVerbPhrase(translatedNodeGloss);
					setVerbFeaures(verbPhrase, child.getFeatures(), true);
				}
				if ("auxpass".equals(posNodeType)) { verbPhrase.setFeature(Feature.PASSIVE, true); }
				if ("cop".equals(posNodeType)) {
					if (translatedChildGloss == null) { translatedChildGloss = wordTranslator.apply(child.getGloss()); } // c&p
					verbPhrase = NLG_FACTORY.createVerbPhrase(wordTranslator.apply(child.getGloss()));
					setVerbFeaures(verbPhrase, child.getFeatures(), false);
				}
				if (verbPhrase != null && "advmod".equals(posNodeType)) {
					if (translatedNodeGloss == null) { translatedNodeGloss = wordTranslator.apply(nodeGloss); } // c&p
					verbPhrase.setVerb(translatedNodeGloss);
				}
				if ("det".equals(posNodeType) || "det:poss".equals(posNodeType)) {
					// are there other "det:??" ?
					if (nounPhrase == null) {
						if (translatedNodeGloss == null) { translatedNodeGloss = wordTranslator.apply(nodeGloss); } // c&p
						nounPhrase = NLG_FACTORY.createNounPhrase(translatedNodeGloss);
					}
					if (translatedChildGloss == null) { translatedChildGloss = wordTranslator.apply(child.getGloss()); } // c&p
					nounPhrase.setDeterminer(translatedChildGloss);
				}
				if ("compound".equals(posNodeType)) {
					// surely exists
					if (translatedChildGloss == null) { translatedChildGloss = wordTranslator.apply(child.getGloss()); } // c&p
					nounPhrase.addModifier(translatedChildGloss);
				}
				if ("case".equals(posNodeType)) {
					if (translatedChildGloss == null) { translatedChildGloss = wordTranslator.apply(child.getGloss()); } // c&p
					prepositionPhrase = NLG_FACTORY.createPrepositionPhrase(translatedChildGloss);
					prepositionPhrase.addComplement(nounPhrase); // surely exists
				}
				if ("amod".equals(posNodeType)) {
					if (translatedChildGloss == null) { translatedChildGloss = wordTranslator.apply(child.getGloss()); } // c&p
					if (nounPhrase == null) {
						prepositionPhrase.addPreModifier(translatedChildGloss);
					} else {
						nounPhrase.addPreModifier(translatedChildGloss);
					}
				}
			}

		// use the verb/noun/preposition phrase, if any are created
		if (verbPhrase != null) {
			if (nounPhrase != null) {
				verbPhrase.setObject(nounPhrase); // verb's object
			}
			if (subPhraseRoot instanceof VPPhraseSpec) {
				((VPPhraseSpec) subPhraseRoot).setVerb(verbPhrase);
			} else if (subPhraseRoot instanceof SPhraseSpec)
				((SPhraseSpec) subPhraseRoot).setVerbPhrase(verbPhrase);
			else {
				System.out.print("\t ###AAAAAAAH on " + subtreeRoot.getGloss() + " is " + posNodeType + " and ...");
				System.out.println(
						"and is not a SPhraseSpec nor VPPhraseSpec: " + subPhraseRoot.getClass().getSimpleName());
			}
		}
		if (prepositionPhrase != null) {
			subPhraseRoot.addPostModifier(prepositionPhrase);
		} else if (nounPhrase != null) {
			// noun without preposition
			if (subPhraseRoot instanceof VPPhraseSpec) {
				((VPPhraseSpec) subPhraseRoot).setObject(nounPhrase);
			} else if (subPhraseRoot instanceof SPhraseSpec) {
				((SPhraseSpec) subPhraseRoot).setObject(nounPhrase);
			} else if (subPhraseRoot instanceof PPPhraseSpec) {
				((PPPhraseSpec) subPhraseRoot).setObject(nounPhrase);
			} else {
				System.out.print("\t ###AAAAAAAH on " + subtreeRoot.getGloss() + " is " + posNodeType + " and ...");
				System.out.println("and is not a SPhraseSpec nor VPPhraseSpec nor PPPhraseSpec: "
						+ subPhraseRoot.getClass().getSimpleName());
			}
		}

		// RECURSION :D
		final NPPhraseSpec n = nounPhrase;
		final VPPhraseSpec v = verbPhrase;
		subtreeRoot.forEachChild(c -> {
			String childNodeType;
			childNodeType = c.getDep();
			if (("nsubjpass".equals(posNodeType) && "nmod".equals(childNodeType)) || "compound".equals(childNodeType)) {
				translateSentenceTree(wordTranslator, c, n);
			} else if ("nsubjpass".equals(childNodeType) || "dobj".equals(childNodeType)) {
				translateSentenceTree(wordTranslator, c, v);
			} else {
				translateSentenceTree(wordTranslator, c, subPhraseRoot); // AGAIN
			}
		});
	}

	protected static void setVerbFeaures(VPPhraseSpec verbPhrase, Map<String, String[]> features,
			boolean isPerfectForm) {
		String tenseFeature;
		String[] feature;
		if (features == null) {
			System.out.println("no feature for verb: " + verbPhrase.getVerb().toString());
			return; // no feature to put, weirdly
		}
		if (isPerfectForm) { verbPhrase.setFeature(Feature.PERFECT, true); }
		feature = features.get("Tense");
		if (feature != null) {
			tenseFeature = feature[0];
			if ("Pres".equals(tenseFeature)) {
				verbPhrase.setFeature(Feature.TENSE, Tense.PRESENT);
			} else if ("Past".equals(tenseFeature)) { verbPhrase.setFeature(Feature.TENSE, Tense.PAST); }
		} else {
			System.out.println("no feature Tense for verb: " + verbPhrase.getVerb().toString());
		}
		// recycle "tenseFeature":
		feature = features.get("Person");
		if (feature != null && "3".equals(feature[0])) { verbPhrase.setFeature(Feature.PERSON, Person.HE); } // assumption
	}
}