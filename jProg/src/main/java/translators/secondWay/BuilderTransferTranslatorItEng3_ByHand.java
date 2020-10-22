package translators.secondWay;

import common.ElemGrammarBase;
import common.NodeParsedSentence;
import tools.BuilderTransferTranslatorItEng;
import translators.secondWay.TransferTranslationRuleBased.TransferRule;

/** Le regole di transfer IT -> ENG sono definite a mano. */
public class BuilderTransferTranslatorItEng3_ByHand extends BuilderTransferTranslatorItEng {
	public static final BuilderTransferTranslatorItEng3_ByHand SINGLETON = new BuilderTransferTranslatorItEng3_ByHand();
	public static final TransferTranslationRuleBased TRANSFERER_RULE_BASED = SINGLETON.newTransferItEng();

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

//		t.addRule(new TransferRule(NodeParsedSentence.newNSD(ElemGrammarBase.PUNC.getElemGrammarBase())) {
//			@Override
//			public  SubTransferResult implementsTransferRule(TransferTranslationItEng3 transferer,
//					NodeParsedSentence originalSubtree) {SubTransferResult r;
//				NodeParsedSentence newNode;
//				newNode = NodeParsedSentence.newNSD(ElemGrammarBase.PUNC.getElemGrammarBase());
//				r.addPairOldNewNode(originalSubtree, newNode);
//				return newNode;
//			}
//		});

		//
		// more complexies
		//

		t.addRule(new TransferRule((NodeParsedSentence) ElemGrammarBase.Verb.newNSD()//
				.addChildNC(ElemGrammarBase.Noun.newNSD())) {
			@Override
			public SubTransferResult implementsTransferRule(TransferTranslationRuleBased transferer,
					NodeParsedSentence originalSubtree) {
				SubTransferResult r;
				NodeParsedSentence newVerb, newSubj, oldSubj;
				// prodicing
				newVerb = originalSubtree.clone(); // ElemGrammarBase.Verb.newNSD();
				// (the old child must be taken)
				oldSubj = (NodeParsedSentence) originalSubtree
						.getChildNCByKey(ElemGrammarBase.Noun.getElemGrammarBase());
				newSubj = oldSubj.clone();
				// wiring
				newVerb.addChildNC(newSubj);
				// mandatory invoke .. (bottom-up is better, but You are free)
				r = new SubTransferResult(newVerb);
				r.addPairOldNewNode(oldSubj, newSubj);
				r.addPairOldNewNode(originalSubtree, newVerb);
				return r; // the root
			}
		});

		t.addRule(new TransferRule(//
				(NodeParsedSentence) ElemGrammarBase.Verb.newNSD()//
						.addChildNC(ElemGrammarBase.Objectt.newNSD())//
		) {
			@Override
			public SubTransferResult implementsTransferRule(TransferTranslationRuleBased transferer,
					NodeParsedSentence originalSubtree) {
				SubTransferResult r;
				NodeParsedSentence newVerb, newSubj, newObj, oldObj;
				// prodicing
				newVerb = ElemGrammarBase.Verb.newNSD();
				// (the old child must be taken)
				oldObj = (NodeParsedSentence) originalSubtree
						.getChildNCByKey(ElemGrammarBase.Objectt.getElemGrammarBase());
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
				r = new SubTransferResult(newVerb);
				r.addPairOldNewNode(
						(NodeParsedSentence) originalSubtree
								.getChildNCByKey(ElemGrammarBase.Subject.getElemGrammarBase()), //
						newSubj);
				r.addPairOldNewNode(oldObj, newObj);
				r.addPairOldNewNode(originalSubtree, newVerb);
				return r; // the root
			}
		});
		t.addRule(new TransferRule(//
				(NodeParsedSentence) ElemGrammarBase.Subject.newNSD()//
						.addChildNC(ElemGrammarBase.Adjective.newNSD())//
		) {
			@Override
			public SubTransferResult implementsTransferRule(TransferTranslationRuleBased transferer,
					NodeParsedSentence originalSubtree) {
				SubTransferResult r;
				NodeParsedSentence newSubj, newAdj, oldAdj;
				// prodicing
				newSubj = ElemGrammarBase.Subject.newNSD();
				// (the old child must be taken)
				oldAdj = (NodeParsedSentence) originalSubtree
						.getChildNCByKey(ElemGrammarBase.Adjective.getElemGrammarBase());
				newAdj = ElemGrammarBase.Adjective.newNSD(); // TODO sostituire col clone del nodo Adj figlio di
																// originalSubtree
				// wiring
				newSubj.addChildNC(newAdj);
				// mandatory invoke .. (bottom-up is better, but You are free)
				r = new SubTransferResult(newSubj);
				r.addPairOldNewNode(oldAdj, newAdj);
				r.addPairOldNewNode(originalSubtree, newSubj);
				return r; // the root
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
//			public  SubTransferResult implementsTransferRule(NodeParsedSentence originalSubtree) {SubTransferResult r;
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
			public SubTransferResult implementsTransferRule(TransferTranslationRuleBased transferer,
					NodeParsedSentence originalSubtree) {
				SubTransferResult r;
				NodeParsedSentence newVerb, newSubj, oldSubj, newObj, oldObj;
				System.out.println("----- siiiiii :D mi hanno sceltooooo :D ");
				// prodicing
				newVerb = ElemGrammarBase.Verb.newNSD();
				// (the old child must be taken)
				oldSubj = (NodeParsedSentence) originalSubtree
						.getChildNCByKey(ElemGrammarBase.Subject.getElemGrammarBase());
				oldObj = (NodeParsedSentence) originalSubtree
						.getChildNCByKey(ElemGrammarBase.Objectt.getElemGrammarBase());
				// and then new ones
				newSubj = ElemGrammarBase.Subject.newNSD();
				newObj = ElemGrammarBase.Objectt.newNSD();
				// wiring
				newVerb.addChildNC(newSubj);
				newVerb.addChildNC(newObj);
				// mandatory invoke .. (bottom-up is better, but You are free)
				r = new SubTransferResult(newVerb);
				r.addPairOldNewNode(oldSubj, newSubj);
				r.addPairOldNewNode(oldObj, newObj);
				r.addPairOldNewNode(originalSubtree, newVerb);
				return r; // the root
			}
		});

		t.addRule(new TransferRule(//
				(NodeParsedSentence) ElemGrammarBase.Verb.newNSD()//
						.addChildNC(ElemGrammarBase.Subject.newNSD())//
						.addChildNC(ElemGrammarBase.Aux.newNSD())//
						.addChildNC(ElemGrammarBase.Objectt.newNSD())//
		) {
			@Override
			public SubTransferResult implementsTransferRule(TransferTranslationRuleBased transferer,
					NodeParsedSentence originalSubtree) {
				SubTransferResult r;
				NodeParsedSentence newVerb, newSubj, oldSubj, newObj, oldObj, newAux, oldAux;
				// prodicing
				newVerb = ElemGrammarBase.Verb.newNSD();
				// (the old child must be taken)
				oldSubj = (NodeParsedSentence) originalSubtree
						.getChildNCByKey(ElemGrammarBase.Subject.getElemGrammarBase());
				oldObj = (NodeParsedSentence) originalSubtree
						.getChildNCByKey(ElemGrammarBase.Objectt.getElemGrammarBase());
				oldAux = (NodeParsedSentence) originalSubtree.getChildNCByKey(ElemGrammarBase.Aux.getElemGrammarBase());
				// and then new ones
				newSubj = ElemGrammarBase.Subject.newNSD();
				newAux = ElemGrammarBase.Aux.newNSD();
				newObj = ElemGrammarBase.Objectt.newNSD();
				// wiring
				newVerb.addChildNC(newSubj);
				newVerb.addChildNC(newObj);
				newVerb.addChildNC(newAux);
				// mandatory invoke .. (bottom-up is better, but You are free)
				r = new SubTransferResult(newVerb);
				r.addPairOldNewNode(oldSubj, newSubj);
				r.addPairOldNewNode(oldObj, newObj);
				r.addPairOldNewNode(oldAux, newAux);
				r.addPairOldNewNode(originalSubtree, newVerb);
				return r; // the root
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
//			public  SubTransferResult implementsTransferRule(TransferTranslationRuleBased transferer,
//					NodeParsedSentence originalSubtree) {SubTransferResult r;
//				NodeParsedSentence newVerb, newSubj, newObj, oldSubj, oldObjs;
//				// prodicing
//				newVerb = ElemGrammarBase.Verb.newNSD();
//				// (the old child must be taken)
//				oldSubj = (NodeParsedSentence) originalSubtree.getChildNCByKey(ElemGrammarBase.Subject.getElemGrammarBase());
//				if (oldSubj == null) {
//					oldSubj = (NodeParsedSentence) originalSubtree.getChildNCByKey(ElemGrammarBase.Noun.getElemGrammarBase());
//				}
//				oldObjs = (NodeParsedSentence) originalSubtree.getChildNCByKey(ElemGrammarBase.Objectt.getElemGrammarBase());
//				if (oldObjs == null) {
//					oldObjs = (NodeParsedSentence) originalSubtree.getChildNCByKey(ElemGrammarBase.Noun.getElemGrammarBase());
//					// warning : is it the same ov previous?
//				}
//				newSubj = NodeParsedSentence.newNSD(ElemGrammarBase.Subject.getElemGrammarBase());
//				newObj = NodeParsedSentence.newNSD(ElemGrammarBase.Objectt.getElemGrammarBase());
//				// wiring
//				newVerb.addChildNC(newSubj);
//				newVerb.addChildNC(newObj);
//				// mandatory invoke .. (bottom-up is better, but You are free)
//				r.addPairOldNewNode(oldSubj, newSubj);
//				r.addPairOldNewNode(oldObjs, newObj);
//				r.addPairOldNewNode(originalSubtree, newVerb);
//				return r; // the root
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
		public SubTransferResult implementsTransferRule(TransferTranslationRuleBased transferer,
				NodeParsedSentence originalSubtree) {
			NodeParsedSentence newNode;
			newNode = originalSubtree.clone(); // egb.newNSD();
			return new SubTransferResult(newNode).addPairOldNewNode(originalSubtree, newNode);
		}
	}
}