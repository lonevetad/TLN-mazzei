package translators.v2;

import java.util.Map;
import java.util.function.Function;

import common.DependentTags;
import common.ElemGrammarBase;
import common.NodeParsedSentence;
import dataStructures.MapTreeAVL;
import simplenlg.phrasespec.SPhraseSpec;
import tools.Misc;
import tools.NodeComparableSynonymIndexed;
import tools.SynonymSet;

/**
 * Bridge between "our" framework's tree (tree with {@link NodeParsedSentence}
 * as nodes) and SimpleNLG.
 */
public class TransferItEngNLGFromEmbedded {
	protected Map<String, Function<NodeParsedSentence, SPhraseSpec>> managersPoS;

	public TransferItEngNLGFromEmbedded() {
		managersPoS = MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight, Misc.STRING_COMPARATOR);
	}

	protected void fillManagerPosMap() {
		managersPoS.put(ElemGrammarBase.Verb.name(), this::manageVerb);
		managersPoS.put(ElemGrammarBase.Noun.name(), this::manageNoun);
		managersPoS.put(ElemGrammarBase.Adverb.name(), this::manageAdverb);
		managersPoS.put(ElemGrammarBase.Det.name(), this::manageDeterminant);
	}

//

	public SPhraseSpec transfer(NodeComparableSynonymIndexed root) {
		SynonymSet rootAllAlternativeNames;
		DependentTags rootTag;
		rootTag = DependentTags.getFirstTagMatching(root.getKeyIdentifier());
		// TODO THIS WILL BE THE SOLUTION TO BRING TO THE EXAM
		return null;
	}

	// specific managers

	public SPhraseSpec manageVerb(NodeParsedSentence root) { return null; }

	public SPhraseSpec manageNoun(NodeParsedSentence root) { return null; }

	public SPhraseSpec manageAdverb(NodeParsedSentence root) { return null; }

	public SPhraseSpec manageDeterminant(NodeParsedSentence root) { return null; }
}