package tools;

import common.TransferTranslationItEng3;
import common.TransferTranslationItEng3.ElementGrammarWithAlternatives;
import common.TransferTranslationItEng3.NodeSubtreeDependency;
import common.TransferTranslationItEng3.TransferRule;

public class BuilderTransferTranslatorItEng3_ByHand extends BuilderTransferTranslatorItEng {
	public static final BuilderTransferTranslatorItEng3_ByHand SINGLETON = new BuilderTransferTranslatorItEng3_ByHand();

	public static enum ElemGrammarBase {
		PUNC("F", "FS", "Punc", "Punct", "PUNC", "PUNCT"), //
		Verb("V", "verb"), //
		Adjective(new String[] { "JJ", "JJR", "JJS", "adj", "amod" }), //
		Noun(new String[] { "NN", "NNS", "NNP", "NNPS", "N", "nsubj", "nsubjpass" }), //
		Adverb("RB", "RBR", "RBS"), //
		Subject(new String[] { "S", "subj", "nsubj", "nsubjpass" }), //
		Objectt(new String[] { "O", "obj" }) //
		//
		;

		public final ElementGrammarWithAlternatives eg;

		private ElemGrammarBase(String... aa) { this(new ElementGrammarWithAlternatives(aa)); }

		private ElemGrammarBase(ElementGrammarWithAlternatives eg) { this.eg = eg; }
	}

	//

	//

	//

	private BuilderTransferTranslatorItEng3_ByHand() {}

	@Override
	public TransferTranslationItEng3 newTransferItEng() {
		TransferTranslationItEng3 t;
		t = new TransferTranslationItEng3();

		// identities

		t.addRule(new TransferRule(NodeSubtreeDependency.newNSD(ElemGrammarBase.PUNC.eg)) {
			@Override
			public NodeSubtreeDependency applyTransferRule(TransferTranslationItEng3 transferer,
					NodeSubtreeDependency originalSubtree) {
				NodeSubtreeDependency newNode;
				newNode = NodeSubtreeDependency.newNSD(ElemGrammarBase.PUNC.eg);
				manageUntouchedChildredUpontransfer(originalSubtree, transferer, newNode);
				return newNode;
			}
		});

		t.addRule(new TransferRule(
				NodeSubtreeDependency.newNSD(new ElementGrammarWithAlternatives(new String[] { "V", "verb" }))) {

		});

		t.addRule(new TransferRule(
				NodeSubtreeDependency.newNSD(new ElementGrammarWithAlternatives(new String[] { "O", "obj" }))) {
			@Override
			public NodeSubtreeDependency applyTransferRule(NodeSubtreeDependency originalSubtree) {
				return NodeSubtreeDependency.newNSD(new ElementGrammarWithAlternatives(new String[] { "O", "obj" }));
			}
		});

		t.addRule(new TransferRule(NodeSubtreeDependency.newNSD(new ElementGrammarWithAlternatives(new String[] {}))) {
			@Override
			public NodeSubtreeDependency applyTransferRule(NodeSubtreeDependency originalSubtree) {
				return NodeSubtreeDependency.newNSD(ElemGrammarBase.Adjective.eg);
			}
		});
		t.addRule(new TransferRule(NodeSubtreeDependency.newNSD(ElemGrammarBase.Adjective.eg)) {
			@Override
			public NodeSubtreeDependency applyTransferRule(NodeSubtreeDependency originalSubtree) {
				return NodeSubtreeDependency
						.newNSD(new ElementGrammarWithAlternatives(new String[] { "JJ", "JJR", "JJS", "adj" }));
			}
		});

		//
		// more complexies
		//

		t.addRule(new TransferRule((NodeSubtreeDependency) NodeSubtreeDependency.newNSD(ElemGrammarBase.Verb.eg)//
				.addChildNC(NodeSubtreeDependency.newNSD(ElemGrammarBase.Noun.eg))) {
			@Override
			public NodeSubtreeDependency applyTransferRule(TransferTranslationItEng3 transferer,
					NodeSubtreeDependency originalSubtree) {
				NodeSubtreeDependency newVerb, newSubj, oldSubj;
				// prodicing
				newVerb = NodeSubtreeDependency.newNSD(ElemGrammarBase.Verb.eg);
				// (the old child must be taken)
				oldSubj = (NodeSubtreeDependency) originalSubtree.getChildNCByKey(ElemGrammarBase.Noun.eg);
				newSubj = NodeSubtreeDependency.newNSD(ElemGrammarBase.Noun.eg);
				// wiring
				newVerb.addChildNC(newSubj);
				// mandatory invoke .. (bottom-up is better, but You are free)
				manageUntouchedChildredUpontransfer(oldSubj, transferer, newSubj);
				manageUntouchedChildredUpontransfer(originalSubtree, transferer, newVerb);
				return newVerb; // the root
			}
		});

//		t.addRule(new TransferRule((NodeSubtreeDependency) // casting
//		NodeSubtreeDependency.newNSD(new ElementGrammarWithAlternatives(new String[] { "V", "verb" }))
//				// 1st level of nesting
//				.addChildNC(
//						NodeSubtreeDependency.newNSD(new ElementGrammarWithAlternatives(new String[] { "S", "subj" })))
//		//
//		) {
//			@Override
//			public NodeSubtreeDependency applyTransferRule(NodeSubtreeDependency originalSubtree) {
////				return NodeSubtreeDependency.newNSD(new ElementGrammarWithAlternatives(new String[] { "V", "verb" }));
//				return (NodeSubtreeDependency) //
//				NodeSubtreeDependency.newNSD(new ElementGrammarWithAlternatives(new String[] { "V", "verb" }))
//						// 1st level of nesting
//						.addChildNC(NodeSubtreeDependency
//								.newNSD(new ElementGrammarWithAlternatives(new String[] { "S", "subj" })));
//			}
//		});

		//
		// EVEN MORE COMPLEX
		//

		t.addRule(new TransferRule((NodeSubtreeDependency) // casting
		NodeSubtreeDependency.newNSD(new ElementGrammarWithAlternatives(new String[] { "V", "verb" }))
				// 1st level of nesting
				.addChildNC(
						NodeSubtreeDependency.newNSD(new ElementGrammarWithAlternatives(new String[] { "S", "subj" })))
				.addChildNC(
						NodeSubtreeDependency.newNSD(new ElementGrammarWithAlternatives(new String[] { "S", "subj" })))
		//
		) {
			@Override
			public NodeSubtreeDependency applyTransferRule(NodeSubtreeDependency originalSubtree) {
//				return NodeSubtreeDependency.newNSD(new ElementGrammarWithAlternatives(new String[] { "V", "verb" }));
				return (NodeSubtreeDependency) //
				NodeSubtreeDependency.newNSD(new ElementGrammarWithAlternatives(new String[] { "V", "verb" }))
						// 1st level of nesting
						.addChildNC(NodeSubtreeDependency
								.newNSD(new ElementGrammarWithAlternatives(new String[] { "S", "subj" })));
			}
		});

		t.addRule(new TransferRule(
				//
				(NodeSubtreeDependency) NodeSubtreeDependency.newNSD(ElemGrammarBase.Verb.eg)//
						.addChildNC(NodeSubtreeDependency.newNSD(ElemGrammarBase.Noun.eg))//
						// second child of verb:
						.addChildNC(NodeSubtreeDependency.newNSD(ElemGrammarBase.Noun.eg))//
		) {
			@Override
			public NodeSubtreeDependency applyTransferRule(TransferTranslationItEng3 transferer,
					NodeSubtreeDependency originalSubtree) {
				NodeSubtreeDependency newVerb, newSubj, newObj, oldSubj, oldObjs;
				// prodicing
				newVerb = NodeSubtreeDependency.newNSD(ElemGrammarBase.Verb.eg);
				// (the old child must be taken)
				oldSubj = (NodeSubtreeDependency) originalSubtree.getChildNCByKey(ElemGrammarBase.Subject.eg);
				if (oldSubj == null) {
					oldSubj = (NodeSubtreeDependency) originalSubtree.getChildNCByKey(ElemGrammarBase.Noun.eg);
				}

				oldObjs = (NodeSubtreeDependency) originalSubtree.getChildNCByKey(ElemGrammarBase.Objectt.eg);
				if (oldObjs == null) {
					oldObjs = (NodeSubtreeDependency) originalSubtree.getChildNCByKey(ElemGrammarBase.Noun.eg);
					// warning : is it the same ov previous?
				}
				newSubj = NodeSubtreeDependency.newNSD(ElemGrammarBase.Subject.eg);
				newObj = NodeSubtreeDependency.newNSD(ElemGrammarBase.Objectt.eg);
				// wiring
				newVerb.addChildNC(newSubj);
				newVerb.addChildNC(newObj);
				// mandatory invoke .. (bottom-up is better, but You are free)
				manageUntouchedChildredUpontransfer(oldSubj, transferer, newSubj);
				manageUntouchedChildredUpontransfer(oldObjs, transferer, newObj);
				manageUntouchedChildredUpontransfer(originalSubtree, transferer, newVerb);
				return newVerb; // the root
			}
		});
		return t;
	}

}
