package translators.secondWay;

import java.util.function.Consumer;

import common.ElemGrammarBase;
import common.NodeParsedSentence;
import tools.SynonymSet;

public class TestTransferItEng3 {

	public static void main(String[] args) { testSingle(); }

	public static void testSingle() {
		NodeParsedSentence root, converted, obj;
//		root = ElemGrammarBase.Verb.newNSD();
//		root.setGloss("coding");
//		root.setPos(pos);
//		root.setDep("ROOT"); // D:
//		root.setLemma("code");
//		root.addFeatures("Tense", "Inf");
		root = nnps(ElemGrammarBase.Verb.eg, //
				"programmare", "V", "ROOT", "programmare", n -> {//
					n.addFeatures("Tense", "Inf");
					n.addFeatures("VerbForm", "Inf");
					n.addFeatures("Person", "1");
					n.addFeatures("Number", "Sing");
				});

		System.out.println("first root");
		System.out.println(root);
		converted = BuilderTransferTranslatorItEng3_ByHand.//
				TRANSFERER_RULE_BASED.transfer(root);

		System.out.println(converted);

		System.out.println("\n now produce more complex stuffs");

		// System.out.println(converted);
//		root= (NodeParsedSentence) ElemGrammarBase.Verb.newNSD()//
		obj = root;
		obj.setDep("xcomp"/* ElemGrammarBase.Comp.eg. */);
		root = (NodeParsedSentence) nnps(ElemGrammarBase.Verb.eg, //
				"voglio", "V", "aux", "volere", n -> {//
					n.addFeatures("Person", "1");
					n.addFeatures("Number", "Sing");
					n.addFeatures("Tense", "Pres");
					n.addFeatures("VerbForm", "Fin");
				}).addChildNC(nnps(ElemGrammarBase.Subject.eg, //
						"Io", "PE", "nsubj", "io", n -> {//
							n.addFeatures("Person", "1");
							n.addFeatures("Number", "Sing");
						})).addChildNC(obj) //
		;

		System.out.println("\n\n now converting heavy stuffs");
		System.out.println(root);
		converted = BuilderTransferTranslatorItEng3_ByHand.//
				TRANSFERER_RULE_BASED.transfer(root);
		System.out.println(converted);

		//
		System.out.println("\n\n\n\n FINE");
	}

	static NodeParsedSentence nnps(String gloss, //
			String pos, String dep, //
			String lemma, Consumer<NodeParsedSentence> featuresSetter, //
			String... syn) {
		return nnps(new SynonymSet(syn), gloss, pos, dep, lemma, featuresSetter);
	}

	static NodeParsedSentence nnps(SynonymSet syn, String gloss, //
			String pos, String dep, //
			String lemma, Consumer<NodeParsedSentence> featuresSetter) {
		NodeParsedSentence n;
		n = new NodeParsedSentence(syn);
		n.setPos(pos);
		n.setGloss(gloss);
		n.setDep(dep);
		n.setLemma(lemma);
		featuresSetter.accept(n);
		return n;

	}
}