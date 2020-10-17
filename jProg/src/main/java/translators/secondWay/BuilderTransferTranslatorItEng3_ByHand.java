package translators.secondWay;

import common.ElemGrammarBase;
import common.NodeParsedSentence;
import tools.BuilderTransferTranslatorItEng;
import translators.secondWay.TransferTranslationRuleBased.TransferRule;

/** Le regole di transfer IT -> ENG sono definite a mano. */
public class BuilderTransferTranslatorItEng3_ByHand extends BuilderTransferTranslatorItEng {
	public static final BuilderTransferTranslatorItEng3_ByHand SINGLETON = new BuilderTransferTranslatorItEng3_ByHand();

	//

	//

	//

	private BuilderTransferTranslatorItEng3_ByHand() {}

	@Override
	public TransferTranslationRuleBased newTransferItEng() {
		TransferTranslationRuleBased t;
		t = new TransferTranslationRuleBased();

		// identities
		for (ElemGrammarBase egb : ElemGrammarBase.values()) {
			t.addRule(new IdentityTransferRule(egb));
		}

//		t.addRule(new TransferRule(NodeParsedSentence.newNSD(ElemGrammarBase.PUNC.eg)) {
//			@Override
//			public NodeParsedSentence applyTransferRule(TransferTranslationItEng3 transferer,
//					NodeParsedSentence originalSubtree) {
//				NodeParsedSentence newNode;
//				newNode = NodeParsedSentence.newNSD(ElemGrammarBase.PUNC.eg);
//				manageUntouchedChildredUpontransfer(originalSubtree, transferer, newNode);
//				return newNode;
//			}
//		});

		//
		// more complexies
		//

		t.addRule(new TransferRule((NodeParsedSentence) ElemGrammarBase.Verb.newNSD()//
				.addChildNC(ElemGrammarBase.Noun.newNSD())) {
			@Override
			public NodeParsedSentence applyTransferRule(TransferTranslationRuleBased transferer,
					NodeParsedSentence originalSubtree) {
				NodeParsedSentence newVerb, newSubj, oldSubj;
				// prodicing
				newVerb = originalSubtree.clone(); // ElemGrammarBase.Verb.newNSD();
				// (the old child must be taken)
				oldSubj = (NodeParsedSentence) originalSubtree.getChildNCByKey(ElemGrammarBase.Noun.eg);
				newSubj = oldSubj.clone();
				// wiring
				newVerb.addChildNC(newSubj);
				// mandatory invoke .. (bottom-up is better, but You are free)
				manageUntouchedChildredUpontransfer(oldSubj, transferer, newSubj);
				manageUntouchedChildredUpontransfer(originalSubtree, transferer, newVerb);
				return newVerb; // the root
			}
		});

		t.addRule(new TransferRule(//
				(NodeParsedSentence) ElemGrammarBase.Verb.newNSD()//
						.addChildNC(ElemGrammarBase.Objectt.newNSD())//
		) {
			@Override
			public NodeParsedSentence applyTransferRule(TransferTranslationRuleBased transferer,
					NodeParsedSentence originalSubtree) {
				NodeParsedSentence newVerb, newSubj, newObj, oldObj;
				// prodicing
				newVerb = ElemGrammarBase.Verb.newNSD();
				// (the old child must be taken)
				oldObj = (NodeParsedSentence) originalSubtree.getChildNCByKey(ElemGrammarBase.Objectt.eg);
				newObj = oldObj.clone();
				// wiring
				newVerb.addChildNC(newObj);

				/*
				 * in italiano, esiste il soggetto sottointeso, in inglese no -> creiamolo e
				 * mettiamolo di default, con il genere che dipende dall'oggetto
				 */
				newSubj = ElemGrammarBase.Subject.newNSD();
				String[] genderFeature = oldObj.getFeatures().get("Gender");
				if (genderFeature != null) { newSubj.addFeatures("Gender", genderFeature); }
				newVerb.addChildNC(newSubj);

				// mandatory invoke .. (bottom-up is better, but You are free)
				manageUntouchedChildredUpontransfer(
						(NodeParsedSentence) originalSubtree.getChildNCByKey(ElemGrammarBase.Subject.eg), //
						transferer, newSubj);
				manageUntouchedChildredUpontransfer(oldObj, transferer, newObj);
				manageUntouchedChildredUpontransfer(originalSubtree, transferer, newVerb);
				return newVerb; // the root
			}
		});
		t.addRule(new TransferRule(//
				(NodeParsedSentence) ElemGrammarBase.Subject.newNSD()//
						.addChildNC(ElemGrammarBase.Adjective.newNSD())//
		) {
			@Override
			public NodeParsedSentence applyTransferRule(TransferTranslationRuleBased transferer,
					NodeParsedSentence originalSubtree) {
				NodeParsedSentence newSubj, newAdj, oldAdj;
				// prodicing
				newSubj = ElemGrammarBase.Subject.newNSD();
				// (the old child must be taken)
				oldAdj = (NodeParsedSentence) originalSubtree.getChildNCByKey(ElemGrammarBase.Adjective.eg);
				newAdj = ElemGrammarBase.Adjective.newNSD();
				// wiring
				newSubj.addChildNC(newAdj);
				// mandatory invoke .. (bottom-up is better, but You are free)
				manageUntouchedChildredUpontransfer(oldAdj, transferer, newAdj);
				manageUntouchedChildredUpontransfer(originalSubtree, transferer, newSubj);
				return newSubj; // the root
			}
		});

//		t.addRule(new TransferRule((NodeParsedSentence) // casting
//		NodeParsedSentence.newNSD(new ElementGrammarWithAlternatives(new String[] { "V", "verb" }))
//				// 1st level of nesting
//				.addChildNC(
//						NodeParsedSentence.newNSD(new ElementGrammarWithAlternatives(new String[] { "S", "subj" })))
//		//
//		) {
//			@Override
//			public NodeParsedSentence applyTransferRule(NodeParsedSentence originalSubtree) {
////				return NodeParsedSentence.newNSD(new ElementGrammarWithAlternatives(new String[] { "V", "verb" }));
//				return (NodeParsedSentence) //
//				NodeParsedSentence.newNSD(new ElementGrammarWithAlternatives(new String[] { "V", "verb" }))
//						// 1st level of nesting
//						.addChildNC(NodeParsedSentence
//								.newNSD(new ElementGrammarWithAlternatives(new String[] { "S", "subj" })));
//			}
//		});

		//
		// EVEN MORE COMPLEX
		//

		t.addRule(new TransferRule(//
				(NodeParsedSentence) ElemGrammarBase.Verb.newNSD()//
						.addChildNC(ElemGrammarBase.Subject.newNSD())//
						.addChildNC(ElemGrammarBase.Objectt.newNSD())//
		) {
			@Override
			public NodeParsedSentence applyTransferRule(TransferTranslationRuleBased transferer,
					NodeParsedSentence originalSubtree) {
				NodeParsedSentence newVerb, newSubj, oldSubj, newObj, oldObj;
				// prodicing
				newVerb = ElemGrammarBase.Verb.newNSD();
				// (the old child must be taken)
				oldSubj = (NodeParsedSentence) originalSubtree.getChildNCByKey(ElemGrammarBase.Subject.eg);
				oldObj = (NodeParsedSentence) originalSubtree.getChildNCByKey(ElemGrammarBase.Objectt.eg);
				// and then new ones
				newSubj = ElemGrammarBase.Subject.newNSD();
				newObj = ElemGrammarBase.Objectt.newNSD();
				// wiring
				newVerb.addChildNC(newSubj);
				newVerb.addChildNC(newObj);
				// mandatory invoke .. (bottom-up is better, but You are free)
				manageUntouchedChildredUpontransfer(oldSubj, transferer, newSubj);
				manageUntouchedChildredUpontransfer(oldObj, transferer, newObj);
				manageUntouchedChildredUpontransfer(originalSubtree, transferer, newVerb);
				return newVerb; // the root
			}
		});

		t.addRule(new TransferRule(//
				(NodeParsedSentence) ElemGrammarBase.Verb.newNSD()//
						.addChildNC(ElemGrammarBase.Subject.newNSD())//
						.addChildNC(ElemGrammarBase.Aux.newNSD())//
						.addChildNC(ElemGrammarBase.Objectt.newNSD())//
		) {
			@Override
			public NodeParsedSentence applyTransferRule(TransferTranslationRuleBased transferer,
					NodeParsedSentence originalSubtree) {
				NodeParsedSentence newVerb, newSubj, oldSubj, newObj, oldObj, newAux, oldAux;
				// prodicing
				newVerb = ElemGrammarBase.Verb.newNSD();
				// (the old child must be taken)
				oldSubj = (NodeParsedSentence) originalSubtree.getChildNCByKey(ElemGrammarBase.Subject.eg);
				oldObj = (NodeParsedSentence) originalSubtree.getChildNCByKey(ElemGrammarBase.Objectt.eg);
				oldAux = (NodeParsedSentence) originalSubtree.getChildNCByKey(ElemGrammarBase.Aux.eg);
				// and then new ones
				newSubj = ElemGrammarBase.Subject.newNSD();
				newAux = ElemGrammarBase.Aux.newNSD();
				newObj = ElemGrammarBase.Objectt.newNSD();
				// wiring
				newVerb.addChildNC(newSubj);
				newVerb.addChildNC(newObj);
				newVerb.addChildNC(newAux);
				// mandatory invoke .. (bottom-up is better, but You are free)
				manageUntouchedChildredUpontransfer(oldSubj, transferer, newSubj);
				manageUntouchedChildredUpontransfer(oldObj, transferer, newObj);
				manageUntouchedChildredUpontransfer(oldAux, transferer, newAux);
				manageUntouchedChildredUpontransfer(originalSubtree, transferer, newVerb);
				return newVerb; // the root
			}
		});
//
//		t.addRule(new TransferRule(
//				//
//				(NodeParsedSentence) ElemGrammarBase.Verb.newNSD() //
//						.addChildNC( ElemGrammarBase.Noun.newNSD())//
//						// second child of verb:
//						.addChildNC(ElemGrammarBase.Noun.)//
//		) {
//			@Override
//			public NodeParsedSentence applyTransferRule(TransferTranslationRuleBased transferer,
//					NodeParsedSentence originalSubtree) {
//				NodeParsedSentence newVerb, newSubj, newObj, oldSubj, oldObjs;
//				// prodicing
//				newVerb = ElemGrammarBase.Verb.newNSD();
//				// (the old child must be taken)
//				oldSubj = (NodeParsedSentence) originalSubtree.getChildNCByKey(ElemGrammarBase.Subject.eg);
//				if (oldSubj == null) {
//					oldSubj = (NodeParsedSentence) originalSubtree.getChildNCByKey(ElemGrammarBase.Noun.eg);
//				}
//				oldObjs = (NodeParsedSentence) originalSubtree.getChildNCByKey(ElemGrammarBase.Objectt.eg);
//				if (oldObjs == null) {
//					oldObjs = (NodeParsedSentence) originalSubtree.getChildNCByKey(ElemGrammarBase.Noun.eg);
//					// warning : is it the same ov previous?
//				}
//				newSubj = NodeParsedSentence.newNSD(ElemGrammarBase.Subject.eg);
//				newObj = NodeParsedSentence.newNSD(ElemGrammarBase.Objectt.eg);
//				// wiring
//				newVerb.addChildNC(newSubj);
//				newVerb.addChildNC(newObj);
//				// mandatory invoke .. (bottom-up is better, but You are free)
//				manageUntouchedChildredUpontransfer(oldSubj, transferer, newSubj);
//				manageUntouchedChildredUpontransfer(oldObjs, transferer, newObj);
//				manageUntouchedChildredUpontransfer(originalSubtree, transferer, newVerb);
//				return newVerb; // the root
//			}
//		});
		return t;
	}

//

	protected static class IdentityTransferRule extends TransferRule {
		final ElemGrammarBase egb;

		public IdentityTransferRule(ElemGrammarBase egb) {
			super(egb.newNSD());
			this.egb = egb;
		}

		@Override
		public NodeParsedSentence applyTransferRule(TransferTranslationRuleBased transferer,
				NodeParsedSentence originalSubtree) {
			NodeParsedSentence newNode;
			newNode = egb.newNSD();
			manageUntouchedChildredUpontransfer(originalSubtree, transferer, newNode);
			return newNode;
		}

	}

}