package common;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import dataStructures.MapTreeAVL;
import tools.Misc;
import tools.SynonymSet;

public enum ElemGrammarBase {
	PUNC("F", "FS", "Punc", "Punct", "PUNC", "PUNCT"), //
	Verb("V", "VP", "verb"), //
	Adjective("JJ", "JJR", "JJS", "adj", "amod"), //
	Noun("NN", "NNS", "NNP", "NNPS", "N", "nsubj", "nsubjpass", "noun", "nobj"), //
	Adverb("RB", "RBR", "RBS"), //
	Subject("S", "subj", "nsubj", "nsubjpass"), //
	Objectt("O", "obj", "dobj", "iobj"), //
	Aux("aux", "auxpass"), //
	Det("det"), //
	Complement("comp", "xcomp", "cop"/* sure? */), //
	Conjugation("and", "cc", "CC"), //
	Negation("not", "neg", "BN") //
	// conjugation
	;

	private final SynonymSet eg;

	private ElemGrammarBase(String... aa) { this(new SynonymSet(aa)); }

	private ElemGrammarBase(SynonymSet eg) { this.eg = eg; }

	public NodeParsedSentence newNSD() { return new NodeParsedSentence(this.getSynonymsClone()); }

	//

	private static final Map<String, ElemGrammarBase> TAGS_BY_Identifier;

	public SynonymSet getSynonymsClone() { return eg.clone(); }

	public SynonymSet getSynonyms() { return eg; }

	public boolean containsDependencyItem(String dep) { return eg.contains(dep); }

	public boolean containsPosTag(String pos) { return eg.contains(pos); }

	//

	//

	//

	static {
		ElemGrammarBase[] v;
		EGBAdderToMap agbAdder;
		v = values();
		TAGS_BY_Identifier = MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight, Misc.STRING_COMPARATOR);
		agbAdder = new EGBAdderToMap();
		for (ElemGrammarBase egb : v) {
			// map each egb's alternatives to itself
			agbAdder.egb = egb;
			egb.eg.forEach(agbAdder);
		}
	}

	public static ElemGrammarBase getTagByTintTag(String tintTag) { return TAGS_BY_Identifier.get(tintTag); }

	public static ElemGrammarBase getFirstTagMatching(SynonymSet eg) {
		ElemGrammarBase dt;
		final Iterator<String> iter;
		dt = null;
		iter = eg.iterator();
		while (iter.hasNext() && ((dt = getTagByTintTag(iter.next())) == null))
			;
		return dt;
	}

	//

	private static class EGBAdderToMap implements Consumer<String> {
		ElemGrammarBase egb;

		@Override
		public void accept(String dtAltern) { TAGS_BY_Identifier.put(dtAltern, egb); }
	}
}