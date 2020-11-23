package translators.mainTransl;

import java.util.Map;
import java.util.function.Function;

import common.ElemGrammarBase;
import common.NodeParsedSentence;
import common.SentenceParsed;
import dataStructures.NodeComparable;
import edu.stanford.nlp.coref.data.Dictionaries.Person;
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseElement;
import simplenlg.phrasespec.AdjPhraseSpec;
import simplenlg.phrasespec.AdvPhraseSpec;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import tools.SynonymSet;
import translators.BasicStuffTranslators;
import translators.ITranslator;

// TO BE BROUGHT 

public class TranslatorTransferItEng implements ITranslator {

	static final Function<SynonymSet, NodeComparable<SynonymSet>> NODE_FACTORY = NodeParsedSentence::new;

	//

	//

	public TranslatorTransferItEng() {}

	@Override
	public SPhraseSpec translateItEng(SentenceParsed t, Function<String, String> wordTranslator) {
		SPhraseSpec s;
		s = BasicStuffTranslators.NLG_FACTORY_IT.createClause();
		AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA(wordTranslator, t.getRoot(), s);
		return s;
	}

	protected void AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA(Function<String, String> wordTranslator, //
			NodeParsedSentence subtreeRoot, //
			final PhraseElement treeOnBuilding // it's the father node
	) {
		if ("ROOT".equals(subtreeRoot.getDep())) {
			String depReplacingRoot;
			System.out.println("DEPPPPPP");
			if (hasPosDepOf(subtreeRoot, ElemGrammarBase.Noun) || subtreeRoot.getFullMorpho().contains("+n+")) {
				depReplacingRoot = "S";
			} else if (hasPosDepOf(subtreeRoot, ElemGrammarBase.Verb) || subtreeRoot.getFullMorpho().contains("+v+")) {
				depReplacingRoot = "V"; // that verb cannot be a subject
			} else {
				depReplacingRoot = subtreeRoot.getPos(); // nothing better left ..
			}
			subtreeRoot.setDep(depReplacingRoot);
			System.out.println(
					"now of root is : " + subtreeRoot.getDep() + " .. (it should be " + depReplacingRoot + ")");
		}
		tr(wordTranslator, treeOnBuilding, subtreeRoot, null, null);
	}

	/* returns the current node, hoping to be useful */
	protected NLGElement tr(Function<String, String> wordTranslator, //
			final NLGElement fatherOnSentenceTree, //
			NodeParsedSentence currentNode, //
			NLGElement currentNodeSimpleNLG, // it's the father node
			String translatedLemmaCurrentNode) {
		boolean shouldSetFeatures;
//		String posRoot, depRoot;
//		posRoot = currentNode.getPos();
//		depRoot = currentNode.getDep();

		System.out.println("analizing tr: " + currentNode.getGloss());

		// translate first the word, if needed:
		if (translatedLemmaCurrentNode == null) {
			translatedLemmaCurrentNode = translateSingleWord(wordTranslator, currentNode);
		}

		shouldSetFeatures = false;

		// check what is this node

		if (hasPosDepOf(currentNode, ElemGrammarBase.Verb)) { /////////////////////////
			final VPPhraseSpec verbPhrase;
			if (currentNodeSimpleNLG != null) {
				verbPhrase = (VPPhraseSpec) currentNodeSimpleNLG;
			} else {
				currentNodeSimpleNLG = verbPhrase = BasicStuffTranslators.NLG_FACTORY_IT
						.createVerbPhrase(translatedLemmaCurrentNode);
			}
			// try to set this as verb on father
			if (fatherOnSentenceTree instanceof SPhraseSpec) {
				((SPhraseSpec) fatherOnSentenceTree).setVerb(verbPhrase);
			} else if (fatherOnSentenceTree instanceof VPPhraseSpec) { // both instances have this method ...
				((VPPhraseSpec) fatherOnSentenceTree).setVerb(verbPhrase);
			}
			shouldSetFeatures = false;
			System.out.println("setting features of " + currentNode);
			setVerbFeatures(verbPhrase, currentNode.getDep(), currentNode.getFeatures());
			verbPhrase.setVerb(currentNode.getLemma()); // set the verb as the lemmatized verb

			currentNode.forEachChildNC(child -> {
				final boolean isChildVerb;
				String translatedChild;
				final VPPhraseSpec childVerbSimpleNLG;
				translatedChild = translateSingleWord(wordTranslator, (NodeParsedSentence) child);

				// some cache
				isChildVerb = canBeConsideredAs((NodeParsedSentence) child, ElemGrammarBase.Verb);
				childVerbSimpleNLG = isChildVerb
						? BasicStuffTranslators.NLG_FACTORY_IT.createVerbPhrase(translatedChild)
						: null;

				// look for subject
				if (canBeConsideredAs((NodeParsedSentence) child, ElemGrammarBase.Subject)) {
					if (isChildVerb) {
						// the child is both the verb and the subject
//						verbPhrase.setPostModifier(childVerbSimpleNLG);
						verbPhrase.setPreModifier(childVerbSimpleNLG);
						tr(wordTranslator, verbPhrase, (NodeParsedSentence) child, childVerbSimpleNLG, translatedChild);
					} else if (canBeConsideredAs((NodeParsedSentence) child, ElemGrammarBase.Noun)) {
						NPPhraseSpec nounChild;
						nounChild = BasicStuffTranslators.NLG_FACTORY_IT.createNounPhrase(translatedChild);
						verbPhrase.setPreModifier(nounChild);
						tr(wordTranslator, verbPhrase, (NodeParsedSentence) child, nounChild, translatedChild);
					}
				} else if (isChildVerb || canBeConsideredAs((NodeParsedSentence) child, ElemGrammarBase.Complement)) {
					/*
					 * look for an auxiliary to the verbs: both verb and complement can help the
					 * verb
					 */
					// helper verb like: << io *ho* studiato ...>
					tr(wordTranslator, verbPhrase, (NodeParsedSentence) child, childVerbSimpleNLG, translatedChild);
					verbPhrase.addComplement(childVerbSimpleNLG);
				} else if (canBeConsideredAs((NodeParsedSentence) child, ElemGrammarBase.Negation)) {
					verbPhrase.setFeature("PronType", "Neg");
				} else if (canBeConsideredAs((NodeParsedSentence) child, ElemGrammarBase.Objectt)) {
					NLGElement theChildNLG;
					if (isChildVerb) {
						// the child is both the verb and the subject
						verbPhrase.setPostModifier(childVerbSimpleNLG);
						theChildNLG = childVerbSimpleNLG;
//						verbPhrase.setPreModifier(childVerbSimpleNLG);
						tr(wordTranslator, verbPhrase, (NodeParsedSentence) child, childVerbSimpleNLG, translatedChild);
					} else if (canBeConsideredAs((NodeParsedSentence) child, ElemGrammarBase.Noun)) {
						NPPhraseSpec nounChild;
						theChildNLG = nounChild = BasicStuffTranslators.NLG_FACTORY_IT
								.createNounPhrase(translatedChild);
						verbPhrase.setPostModifier(nounChild);
						tr(wordTranslator, verbPhrase, (NodeParsedSentence) child, nounChild, translatedChild);
					} else {
						theChildNLG = null;
					}
					if (theChildNLG != null && ((NodeParsedSentence) child).getDep().contains("mod")) {
						verbPhrase.setIndirectObject(theChildNLG);
					} else {
						verbPhrase.setObject(theChildNLG);
					}
				} else if (canBeConsideredAs((NodeParsedSentence) child, ElemGrammarBase.Conjugation)) {
					var conj = tr(wordTranslator, verbPhrase, (NodeParsedSentence) child, null, null);
					verbPhrase.setObject(conj);
				} else if (canBeConsideredAs((NodeParsedSentence) child, ElemGrammarBase.Complement)) {
					var compl = tr(wordTranslator, verbPhrase, (NodeParsedSentence) child, null, null);
					verbPhrase.addComplement(compl);
				}
			});

		} else if (hasPosDepOf(currentNode, ElemGrammarBase.Conjugation)) { /////////////////////////
			final CoordinatedPhraseElement conj;
			if (currentNodeSimpleNLG == null) {
				currentNodeSimpleNLG = BasicStuffTranslators.NLG_FACTORY_IT.createCoordinatedPhrase();
			}
			conj = (CoordinatedPhraseElement) currentNodeSimpleNLG; // forcefully cast it
			conj.setConjunction(translatedLemmaCurrentNode);
			currentNode.forEachChildNC(child -> {
				NLGElement childNodeSimpleNLG;
				childNodeSimpleNLG = tr(wordTranslator, conj, (NodeParsedSentence) child, null, null);
				conj.addCoordinate(childNodeSimpleNLG);
			});
		} else if (hasPosDepOf(currentNode, ElemGrammarBase.Negation)) { /////////////////////////
//			BasicStuffTranslators.NLG_FACTORY_IT.creat
			if (fatherOnSentenceTree != null)
				fatherOnSentenceTree.setFeature("PronType", "Neg");

			// no child should have been there -> no recursion
		} else if (hasPosDepOf(currentNode, ElemGrammarBase.Noun)
				|| hasPosDepOf(currentNode, ElemGrammarBase.Objectt)) { /////////////////////////
			NPPhraseSpec nounPhrase;
			if (currentNodeSimpleNLG != null) {
				nounPhrase = (NPPhraseSpec) currentNodeSimpleNLG;
			} else {
				currentNodeSimpleNLG = nounPhrase = BasicStuffTranslators.NLG_FACTORY_IT
						.createNounPhrase(translatedLemmaCurrentNode);
			}

			currentNode.forEachChildNC(child -> {
				NodeParsedSentence npsChild;
				NLGElement childNLG;
				npsChild = (NodeParsedSentence) child;
				if (canBeConsideredAs(npsChild, ElemGrammarBase.Adjective)) {
					childNLG = tr(wordTranslator, nounPhrase, npsChild, null, null);
					nounPhrase.addPostModifier(childNLG);
				} else if (canBeConsideredAs(npsChild, ElemGrammarBase.Adjective)) {
					childNLG = tr(wordTranslator, nounPhrase, npsChild, null, null);
					nounPhrase.addPostModifier(childNLG);
				}
			});
		} else if (hasPosDepOf(currentNode, ElemGrammarBase.Adjective)) { /////////////////////////
			currentNodeSimpleNLG = BasicStuffTranslators.NLG_FACTORY_IT
					.createAdjectivePhrase(translatedLemmaCurrentNode);
			final var cn = currentNodeSimpleNLG;
			currentNode.forEachChildNC(child -> {
//				if(cn instanceof NPPhraseSpec) {
//					NPPhraseSpec THIS_NOD_AS_NOUN_PHRASE;
//					THIS_NOD_AS_NOUN_PHRASE=
//							(NPPhraseSpec) cn ;
//					THIS_NOD_AS_NOUN_PHRASE.
//				}
				addUnUdentifiedChild(cn, tr(wordTranslator, cn, (NodeParsedSentence) child, null, null));
			});
		} else if (hasPosDepOf(currentNode, ElemGrammarBase.Adverb)) { /////////////////////////
			currentNodeSimpleNLG = BasicStuffTranslators.NLG_FACTORY_IT.createAdverbPhrase(translatedLemmaCurrentNode);
			final var cn = currentNodeSimpleNLG;
			currentNode.forEachChildNC(child -> {
				addUnUdentifiedChild(cn, tr(wordTranslator, cn, (NodeParsedSentence) child, null, null));
			});
		} else {
			System.out.println("UNKNOWN node: " + currentNode);
		}

		// else .. ?

		// set features, if not done
		if (shouldSetFeatures && currentNodeSimpleNLG != null) {
//			final PhraseElement 
			final NLGElement thisNode = currentNodeSimpleNLG;
			currentNode.forEachFeature((featureName, features) -> {
				for (String f : features) {
					thisNode.setFeature(featureName, f);
				}
			});
		}
		return currentNodeSimpleNLG;
	}

	//

	protected void addUnUdentifiedChild(NLGElement father, NLGElement child) {
//		if(child.isA(ElementCategory.))
		if (father instanceof VPPhraseSpec) {
			VPPhraseSpec verb = (VPPhraseSpec) father;
			if (child instanceof AdjPhraseSpec) {
				verb.addPreModifier(child);
			} else if (child instanceof AdvPhraseSpec) {
				verb.addPostModifier(child);
			} else {
				verb.addComplement(child); // I'll try
			}
//			verb.
		} else if (father instanceof NPPhraseSpec) {
			NPPhraseSpec noun = (NPPhraseSpec) father;
			if (child instanceof AdjPhraseSpec) {
				noun.addPreModifier(child);
			} else if (child instanceof AdvPhraseSpec) {
				noun.addPostModifier(child);
			} else {
				noun.addComplement(child); // I'll try
			}
		} else if (father instanceof AdvPhraseSpec) {
			AdvPhraseSpec adv = (AdvPhraseSpec) father;
			addUnUdentifiedChild(adv.getParent(), child); // recursion
		} else if (father instanceof AdjPhraseSpec) { //
			AdjPhraseSpec adj = (AdjPhraseSpec) father;
			addUnUdentifiedChild(adj.getParent(), child); // recursion
		}
	}

	//

	//

	protected void setVerbFeatures(VPPhraseSpec verbPhrase, String dep, Map<String, String[]> features) {
		String[] feature;
		if (ElemGrammarBase.Aux.containsDependencyItem(dep) || // "aux"
				ElemGrammarBase.Complement.containsDependencyItem(dep) // "xcomp"
		) {
			verbPhrase.setFeature(Feature.PERFECT, true);
		}
		feature = features.get("Tense");
		if (feature != null) {
			String temp;
			Tense t;
			temp = feature[0];
			if ("Pres".equals(temp)) {
				t = Tense.PRESENT;
			} else {
				t = "Past".equals(temp) ? Tense.PAST : Tense.FUTURE;
			}
			verbPhrase.setFeature(Feature.TENSE, t);
		}
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
			case "Part": {
				verbPhrase.setFeature(Feature.FORM, Form.PAST_PARTICIPLE);
				break;
			}
			default:
//				throw new IllegalArgumentException
				System.out.println("ERROR: Unexpected value: " + feature[0]);
			}
		}
		feature = features.get("Person");
		if (feature != null && "3".equals(feature[0])) { verbPhrase.setFeature(Feature.PERSON, Person.HE); } // assumption
	}

	protected static String translateSingleWord(Function<String, String> wordTranslator, //
			NodeParsedSentence word) {
		String translated = wordTranslator.apply(word.getGloss());
		if (translated == null) {
			// the gloss is useless, try the lemma
			translated = wordTranslator.apply(word.getLemma());
		}
		return translated;
	}

	protected static boolean canBeConsideredAs(NodeParsedSentence node, ElemGrammarBase egb) {
		return node.scoreKeyCompatibilityWith(egb.getSynonyms()) != 0;
	}

	/** Note the direction: is the element */
	protected static boolean hasPosDepOf(NodeParsedSentence node, ElemGrammarBase egb) {
		System.out.println("\n\n has pos dep ..." + node.getGloss() + " --> with egb: " + egb.name());
		return hasPosDepOf(node.getDep(), node.getPos(), egb);
	}

	protected static boolean hasPosDepOf(String dep, String pos, ElemGrammarBase egb) {
		System.out.println("\thas dep <<" + dep + ">>: " + egb.containsDependencyItem(dep) + ",,, has pos<<" + pos
				+ ">>: " + egb.containsPosTag(pos));
		return egb.containsDependencyItem(dep) || egb.containsPosTag(pos);
	}
}