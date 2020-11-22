package translators.mainTransl;

import java.util.function.Function;

import common.ElemGrammarBase;
import common.NodeParsedSentence;
import common.SentenceParsed;
import dataStructures.NodeComparable;
import simplenlg.framework.PhraseElement;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import tools.SynonymSet;
import translators.BasicStuffTranslators;
import translators.ITranslator;

public class TranslatorTransferItEng implements ITranslator {

	static final Function<SynonymSet, NodeComparable<SynonymSet>> NODE_FACTORY = NodeParsedSentence::new;

	//

	//

	public TranslatorTransferItEng() {}

	@Override
	public SPhraseSpec translateItEng(SentenceParsed t, Function<String, String> wordTranslator) {
		SPhraseSpec s;
		s = BasicStuffTranslators.NLG_FACTORY_IT.createClause();
		tr(wordTranslator, t.getRoot(), s);
		return s;
	}

	protected void tr(Function<String, String> wordTranslator, //
			NodeParsedSentence subtreeRoot, //
			final PhraseElement treeOnBuilding // it's the father node
	) {
		tr(wordTranslator, subtreeRoot, treeOnBuilding, null);
	}

	protected void tr(Function<String, String> wordTranslator, //
			NodeParsedSentence subtreeRoot, //
			final PhraseElement treeOnBuilding // it's the father node
			, String translatedLemma) {
		boolean shouldSetFeatures;
		String posRoot, depRoot;
		posRoot = subtreeRoot.getPos();
		depRoot = subtreeRoot.getDep();

		// translate first the word, if needed:
		if (translatedLemma == null) { translatedLemma = translateSingleWord(wordTranslator, subtreeRoot); }

		shouldSetFeatures = false;

		// check what is this node

		if (ElemGrammarBase.Verb.containsPosTag(posRoot) || ElemGrammarBase.Verb.containsDependencyItem(depRoot)) {
			final VPPhraseSpec verbPhrase;
			verbPhrase = (VPPhraseSpec) treeOnBuilding;
			shouldSetFeatures = true;
			verbPhrase.setVerb(subtreeRoot.getLemma()); // set the verb as the lemmatized verb
			subtreeRoot.forEachChildNC(child -> {
				final boolean isChildVerb;

				isChildVerb = child.scoreKeyCompatibilityWith(ElemGrammarBase.Verb.getSynonyms()) != 0;

				// look for subject
				if (//
				child.scoreKeyCompatibilityWith(ElemGrammarBase.Subject.getSynonyms()) != 0) {

				} else if ( // look for an auxiliary to the verbs: both verb and complement can help the
							// verb
				isChildVerb || (child.scoreKeyCompatibilityWith(ElemGrammarBase.Complement.getSynonyms()) != 0)
//				NodeComparable<SynonymSet> auxiliarComplementNode; // V1
//				if((		(ElemGrammarBase.Verb.getSynonyms(), NODE_FACTORY)
//						&& (auxiliarComplementNode = subtreeRoot
//								.getChildNCMostSimilarTo(ElemGrammarBase.Verb.getSynonyms(), NODE_FACTORY)) != null)
//						|| (subtreeRoot.containsChildNC(ElemGrammarBase.Complement.getSynonyms(), NODE_FACTORY)
//								&& (auxiliarComplementNode = subtreeRoot.getChildNCMostSimilarTo(
//										ElemGrammarBase.Complement.getSynonyms(), NODE_FACTORY)) != null)
				) {
					// helper verb like: << io *ho* studiato ...>
					VPPhraseSpec auxiliarComplementSimpleNLG = null;
					String translatedVerb;
					translatedVerb = translateSingleWord(wordTranslator, (NodeParsedSentence) child);
					auxiliarComplementSimpleNLG = BasicStuffTranslators.NLG_FACTORY_IT.createVerbPhrase(translatedVerb);
					tr(wordTranslator, (NodeParsedSentence) child, auxiliarComplementSimpleNLG, translatedVerb);
					verbPhrase.addComplement(auxiliarComplementSimpleNLG);
				} // else if()
			});

			// in the end
		} // else .. TODO

		// set features, if not done
		if (shouldSetFeatures && treeOnBuilding != null) {
			subtreeRoot.forEachFeature((featureName, features) -> {
				for (String f : features) {
					treeOnBuilding.setFeature(featureName, f);
				}
			});
		}
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
}