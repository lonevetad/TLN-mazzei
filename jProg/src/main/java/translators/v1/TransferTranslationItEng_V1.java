package translators.v1;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

import common.SentenceParsed;
import common.SentenceParsed.NodeParsedSentFromTint;
import edu.stanford.nlp.coref.data.Dictionaries.Person;
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.Tense;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseElement;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import translators.BasicStuffTranslators;
import translators.ITranslator;

/** First non-naive version of a translator. */
public class TransferTranslationItEng_V1 implements ITranslator {

	@Override
	public SPhraseSpec translateItEng(SentenceParsed t, Function<String, String> wordTranslator) {
		SPhraseSpec rootOfPhrase;
		rootOfPhrase = BasicStuffTranslators.NLG_FACTORY_IT.createClause();
		System.out.println("\n\n\n t.root è .. " + t.getRoot());
		translateSentenceTree(wordTranslator, t.getRoot(), rootOfPhrase);
		return rootOfPhrase;
	}

	protected void translateSentenceTree(Function<String, String> wordTranslator,
			SentenceParsed.NodeParsedSentFromTint subtreeRoot, final PhraseElement subPhraseRoot) {
//main stuffs
//		boolean tempCacheFlag;
		String depNodeType, nodeLemma, translatedNodeGloss, translatedChildGloss;
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

		depNodeType = subtreeRoot.getDep();
//		nodeGloss = subtreeRoot.getGloss();
		nodeLemma = subtreeRoot.getLemma();
		translatedNodeGloss = wordTranslator.apply(nodeLemma);
// first of all, check if the root is the "nominal modifier" or "direct object"
		if ("nmod".equals(depNodeType) || "dobj".equals(depNodeType)) {
			nounPhrase = BasicStuffTranslators.NLG_FACTORY_IT.createNounPhrase(translatedNodeGloss);
		} else if ("ROOT".equals(depNodeType)) {
			String subTreeRootPos = subtreeRoot.getPos();
			switch (subTreeRootPos) {
			case "V":
			case "VP":
			case "VB":
			case "VBD": {
				verbPhrase = BasicStuffTranslators.NLG_FACTORY_IT.createVerbPhrase(translatedNodeGloss);
				break;
			}
			case "N":
			case "NN":
			case "NNS":
			case "NNP":
			case "NNPS": {
				nounPhrase = BasicStuffTranslators.NLG_FACTORY_IT.createNounPhrase(translatedNodeGloss);
				break;
			}
			default:
//				throw new IllegalArgumentException
				System.out.println("Unexpected value: " + subTreeRootPos);
			}
		}

		System.out.println("subtreeRoo______: " + subtreeRoot.getGloss());
		// child controls, interleaving some if(s
		childrenIterator = subtreeRoot.getChildrenIterator();
		if (childrenIterator != null) {
			String childDepNodeType;
			while (childrenIterator.hasNext()) { // is required to modify local variables: forEach forbids, iterator not
				child = childrenIterator.next();
				childDepNodeType = child.getDep();
				translatedChildGloss = null;
				System.out.println("\t child: " + child.getGloss() + ",,,, having posNodeType: " + childDepNodeType);

				if ("nsubj".equals(childDepNodeType)) {
					if (translatedNodeGloss == null) { translatedNodeGloss = wordTranslator.apply(nodeLemma); } // c&p
					if (verbPhrase == null)
						verbPhrase = BasicStuffTranslators.NLG_FACTORY_IT.createVerbPhrase();
					verbPhrase.setVerb(translatedNodeGloss);
					setVerbFeatures(verbPhrase, subtreeRoot.getFeatures(), false);
					System.out.println("\n\n####");
					if (subPhraseRoot instanceof SPhraseSpec) {
						((SPhraseSpec) subPhraseRoot)
								.setSubject(translatedChildGloss = wordTranslator.apply(child.getLemma()));
					} else {
						System.out.print(
								"\t ###AAAAAAAH on " + subtreeRoot.getGloss() + " is " + childDepNodeType + " and ...");
						System.out.println("and is not a SPhraseSpec: " + subPhraseRoot.getClass().getSimpleName());
					}
					/*
					 * else if (subPhraseRoot instanceof VPPhraseSpec) { ((VPPhraseSpec)
					 * subPhraseRoot) .setSubject(translatedChildGloss =
					 * wordTranslator.apply(child.getGloss())); }
					 */
				}
				if ("aux".equals(childDepNodeType)) {
					if (verbPhrase == null)
						verbPhrase = BasicStuffTranslators.NLG_FACTORY_IT.createVerbPhrase(translatedNodeGloss);
					setVerbFeatures(verbPhrase, child.getFeatures(), true);
					System.out.println("\n####2 now verbPhrase is: " + verbPhrase);
				}
				if ("auxpass".equals(childDepNodeType)) { verbPhrase.setFeature(Feature.PASSIVE, true); }
				if ("cop".equals(childDepNodeType)) { // TODO LOOK AT ME FOR VERB
					if (translatedChildGloss == null) { translatedChildGloss = wordTranslator.apply(child.getLemma()); } // c&p
					if (verbPhrase == null)
						verbPhrase = BasicStuffTranslators.NLG_FACTORY_IT
								.createVerbPhrase(wordTranslator.apply(child.getLemma()));
					setVerbFeatures(verbPhrase, child.getFeatures(), false);
				}
				if (verbPhrase != null && "advmod".equals(childDepNodeType)) {
					if (translatedNodeGloss == null) { translatedNodeGloss = wordTranslator.apply(nodeLemma); } // c&p
					verbPhrase.setVerb(translatedNodeGloss);
				}
				if ("det".equals(childDepNodeType) || "det:poss".equals(childDepNodeType)) {
					// are there other "det:??" ?
					if (nounPhrase == null) {
						if (translatedNodeGloss == null) { translatedNodeGloss = wordTranslator.apply(nodeLemma); } // c&p
						nounPhrase = BasicStuffTranslators.NLG_FACTORY_IT.createNounPhrase(translatedNodeGloss);
					}
					if (translatedChildGloss == null) { translatedChildGloss = wordTranslator.apply(child.getGloss()); } // c&p
					nounPhrase.setDeterminer(translatedChildGloss);
				}
				if ("compound".equals(childDepNodeType)) {
					// surely exists
					if (translatedChildGloss == null) { translatedChildGloss = wordTranslator.apply(child.getGloss()); } // c&p
					if (nounPhrase != null && (!"X".equals(child.getPos())))
						nounPhrase.addModifier(translatedChildGloss);
				}
				if ("case".equals(childDepNodeType)) {
					if (translatedChildGloss == null) { translatedChildGloss = wordTranslator.apply(child.getGloss()); } // c&p
					prepositionPhrase = BasicStuffTranslators.NLG_FACTORY_IT
							.createPrepositionPhrase(translatedChildGloss);
					NLGElement father;
					if (nounPhrase != null)
						father = nounPhrase;
					else if (verbPhrase != null)
						father = verbPhrase;
					else
						father = null;
					// it depends on the subject
					if (father != null)
						prepositionPhrase.addComplement(father);
					else
						System.out.println(
								"WHAAAT cannot find a father for preposition phrase of dep \'case\' of: " + child);
				}
				if ("amod".equals(childDepNodeType)) {
					if (translatedChildGloss == null) { translatedChildGloss = wordTranslator.apply(child.getGloss()); } // c&p
					if (nounPhrase == null) {
						if (prepositionPhrase == null) {
							System.out.println("OMG prepositionPhrase is null");
							prepositionPhrase = BasicStuffTranslators.NLG_FACTORY_IT
									.createPrepositionPhrase(translatedNodeGloss);
						}
						prepositionPhrase.addPreModifier(translatedChildGloss);
					} else {
						nounPhrase.addPreModifier(translatedChildGloss);
					}
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
				System.out.print("\t ###AAAAAAAH on " + subtreeRoot.getGloss() + " is " + depNodeType + " and ...");
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
				System.out.print("\t ###AAAAAAAH on " + subtreeRoot.getGloss() + " is " + depNodeType + " and ...");
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
			if (("nsubjpass".equals(depNodeType) && "nmod".equals(childNodeType)) || "compound".equals(childNodeType)) {
				translateSentenceTree(wordTranslator, c, n);
			} else if ("nsubjpass".equals(childNodeType) || "dobj".equals(childNodeType)) {
				translateSentenceTree(wordTranslator, c, v);
			} else {
				translateSentenceTree(wordTranslator, c, subPhraseRoot); // AGAIN
			}
		});
	}

	protected static void setVerbFeatures(VPPhraseSpec verbPhrase, Map<String, String[]> features,
			boolean isPerfectForm) {
		String tenseFeature;
		String[] feature;
		if (features == null) {
			System.out.println("no feature for verb: " + verbPhrase.getVerb().toString());
			return; // no feature to put, weirdly
		}
		if (isPerfectForm) { verbPhrase.setFeature(Feature.PERFECT, true); } // quando è un "aux"
		feature = features.get("Tense");
		if (feature != null) {
			tenseFeature = feature[0];
			if ("Pres".equals(tenseFeature)) {
				verbPhrase.setFeature(Feature.TENSE, Tense.PRESENT);
			} else if ("Past".equals(tenseFeature)) { verbPhrase.setFeature(Feature.TENSE, Tense.PAST); }
		} else {
			feature = features.get("VerbForm");
			if (feature != null) {
				switch (feature[0]) {
				case "Nor":
				case "Norm": {
					verbPhrase.setFeature(Feature.FORM, Form.NORMAL);
					break;
				}
				case "Ger": {
					verbPhrase.setFeature(Feature.FORM, Form.GERUND);
					break;
				}
				case "Fin":
				case "Inf": {
					verbPhrase.setFeature(Feature.FORM, Form.INFINITIVE);
					break;
				}
				default:
//					throw new IllegalArgumentException
					System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE Unexpected value: " + feature[0]);
				}
			}
			System.out.println("no feature Tense for verb: " + verbPhrase.getVerb().toString());
		}
		// recycle "tenseFeature":
		feature = features.get("Person");
		if (feature != null && "3".equals(feature[0])) { verbPhrase.setFeature(Feature.PERSON, Person.HE); } // assumption
	}
}