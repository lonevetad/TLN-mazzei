package simpleNLG;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

import common.TreeParsedSentence;
import common.TreeParsedSentence.NodeDependencyTree;
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

public class TransferTranslationItEng {
	public static final Lexicon LEXICON = new XMLLexicon();
	public static final NLGFactory NLG_FACTORY = new NLGFactory(LEXICON);
	public static final Realiser REALISER = new Realiser(LEXICON);

	public static SPhraseSpec transferTranslateItEng(TreeParsedSentence t, Function<String, String> wordTranslator) {
		SPhraseSpec rootOfPhrase;
		rootOfPhrase = NLG_FACTORY.createClause();
		transfer(wordTranslator, t.getRoot(), rootOfPhrase);
		return rootOfPhrase;
	}

	protected static void transfer(Function<String, String> wordTranslator,
			TreeParsedSentence.NodeDependencyTree subtreeRoot, PhraseElement subPhraseRoot) {
//main stuffs
//		boolean tempCacheFlag;
		String posNodeType, nodeGloss, translatedNodeGloss, translatedChildGloss;
		Iterator<NodeDependencyTree> childrenIterator;
		NodeDependencyTree child;
		// the following three could be created
		NPPhraseSpec nounPhrase = null;
		VPPhraseSpec verbPhrase = null;
		PPPhraseSpec prepositionPhrase = null;

		posNodeType = subtreeRoot.getDep();
		nodeGloss = subtreeRoot.getGloss();
		translatedNodeGloss = null;

// first of all, check if the root is the "nominal modifier" or "direct object"
		if ("nmod".equals(posNodeType) || "dobj".equals(posNodeType)) {
			nounPhrase = NLG_FACTORY.createNounPhrase(translatedNodeGloss = wordTranslator.apply(nodeGloss));
		}

		// child controls, interleaving some if(s
		childrenIterator = subtreeRoot.getChildrenIterator();
		while (childrenIterator.hasNext()) { // iterator required to modify local variables
			child = childrenIterator.next();
			translatedChildGloss = null;

			if ("nsubj".equals(posNodeType)) {
				if (translatedNodeGloss == null) {
					translatedNodeGloss = wordTranslator.apply(nodeGloss);
				} // c&p
				verbPhrase = NLG_FACTORY.createVerbPhrase();
				verbPhrase.setVerb(translatedNodeGloss);
				setVerbFeaures(verbPhrase, subtreeRoot.getFeatures(), false);
				if (subPhraseRoot instanceof SPhraseSpec) {
					((SPhraseSpec) subPhraseRoot)
							.setSubject(translatedChildGloss = wordTranslator.apply(child.getGloss()));
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
			if ("auxpass".equals(posNodeType)) {
				verbPhrase.setFeature(Feature.PASSIVE, true);
			}
			if ("cop".equals(posNodeType)) {
				if (translatedChildGloss == null) {
					translatedChildGloss = wordTranslator.apply(child.getGloss());
				} // c&p
				verbPhrase = NLG_FACTORY.createVerbPhrase(wordTranslator.apply(child.getGloss()));
				setVerbFeaures(verbPhrase, child.getFeatures(), false);
			}
			if (verbPhrase != null && "advmod".equals(posNodeType)) {
				if (translatedNodeGloss == null) {
					translatedNodeGloss = wordTranslator.apply(nodeGloss);
				} // c&p
				verbPhrase.setVerb(translatedNodeGloss);
			}
			if ("det".equals(posNodeType) || "det:poss".equals(posNodeType)) {
				// are there other "det:??" ?
				if (nounPhrase == null) {
					if (translatedNodeGloss == null) {
						translatedNodeGloss = wordTranslator.apply(nodeGloss);
					} // c&p
					nounPhrase = NLG_FACTORY.createNounPhrase(translatedNodeGloss);
				}
				if (translatedChildGloss == null) {
					translatedChildGloss = wordTranslator.apply(child.getGloss());
				} // c&p
				nounPhrase.setDeterminer(translatedChildGloss);
			}
			if ("compound".equals(posNodeType)) {
				// surely exists
				if (translatedChildGloss == null) {
					translatedChildGloss = wordTranslator.apply(child.getGloss());
				} // c&p
				nounPhrase.addModifier(translatedChildGloss);
			}
			if ("case".equals(posNodeType)) {
				if (translatedChildGloss == null) {
					translatedChildGloss = wordTranslator.apply(child.getGloss());
				} // c&p
				prepositionPhrase = NLG_FACTORY.createPrepositionPhrase(translatedChildGloss);
				prepositionPhrase.addComplement(nounPhrase); // surely exists
			}
			if ("amod".equals(posNodeType)) {
				if (translatedChildGloss == null) {
					translatedChildGloss = wordTranslator.apply(child.getGloss());
				} // c&p
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
			}
		}

		// RECURSION :D
		final NPPhraseSpec n = nounPhrase;
		final VPPhraseSpec v = verbPhrase;
		subtreeRoot.forEachChild(c -> {
			String childNodeType;
			childNodeType = c.getDep();
			if (("nsubjpass".equals(posNodeType) && "nmod".equals(childNodeType)) || "compound".equals(childNodeType)) {
				transfer(wordTranslator, c, n);
			} else if ("nsubjpass".equals(childNodeType) || "dobj".equals(childNodeType)) {
				transfer(wordTranslator, c, v);
			} else {
				transfer(wordTranslator, c, subPhraseRoot); // AGAIN
			}
		});
	}

	protected static void setVerbFeaures(VPPhraseSpec verbPhrase, Map<String, String[]> features,
			boolean isPerfectForm) {
		String tenseFeature;
		String[] person;
		if (isPerfectForm) {
			verbPhrase.setFeature(Feature.PERFECT, true);
		}
		tenseFeature = features.get("Tense")[0];
		if ("Pres".equals(tenseFeature)) {
			verbPhrase.setFeature(Feature.TENSE, Tense.PRESENT);
		} else if ("Past".equals(tenseFeature)) {
			verbPhrase.setFeature(Feature.TENSE, Tense.PAST);
		}
		// recycle "tenseFeature":
		person = features.get("Person");
		if (person != null && "3".equals(person[0])) {
			verbPhrase.setFeature(Feature.PERSON, Person.HE);
		} // assumption
	}
}