package translators.secondWay;

import common.ElemGrammarBase;
import common.NodeParsedSentence;
import tools.BuilderTransferTranslatorItEng;
import tools.SynonymSet;

/** Le regole di transfer IT -> ENG sono definite a mano. */
public class BuilderTransferTranslatorItEng3_ByHand extends BuilderTransferTranslatorItEng {
	public static final BuilderTransferTranslatorItEng3_ByHand SINGLETON = new BuilderTransferTranslatorItEng3_ByHand();
	public static final ATransferTranslationRuleBased TRANSFERER_RULE_BASED = SINGLETON.newTransferItEng();

	//

	//

	//

	private BuilderTransferTranslatorItEng3_ByHand() {}

	@Override
	public ATransferTranslationRuleBased newTransferItEng() {
		ATransferTranslationRuleBased t;
//		t = new TransferTranslationRuleBased();
//		t = new TransferTranslationRuleBased_V2();
//		t = new TransferTranslationRuleBased_V3();
		t = new TransferTranslationRuleBased_V4();

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
		// more complexity
		//

		t.addRule(new TransferRule((NodeParsedSentence) ElemGrammarBase.Verb.newNSD()//
				.addChildNC(ElemGrammarBase.Noun.newNSD().addAlternatives(ElemGrammarBase.Subject.getSynonyms()))) {
			@Override
			public SubTransferResult implementsTransferRule(ATransferTranslationRuleBased transferer,
					NodeParsedSentence originalSubtree) {
				SubTransferResult r;
				NodeParsedSentence newVerb, newSubj, oldSubj;
				// prodicing
				newVerb = originalSubtree.clone(); // ElemGrammarBase.Verb.newNSD();
				// (the old child must be taken)
				oldSubj = (NodeParsedSentence) originalSubtree
						.getChildNCMostSimilarTo(ElemGrammarBase.Noun.getSynonyms());
				if (oldSubj == null) {
					oldSubj = (NodeParsedSentence) originalSubtree
							.getChildNCMostSimilarTo(ElemGrammarBase.Subject.getSynonyms());
				}
				newSubj = oldSubj.clone();
				// wiring
				newVerb.addChildNC(newSubj);
				// mandatory invoke .. (bottom-up is better, but You are free)
				r = new SubTransferResult(newVerb);
				r.addPairOldNewNode(oldSubj, newSubj);
				r.addPairOldNewNode(originalSubtree, newVerb);
				return r;
			}
		});

		t.addRule(new TransferRule(//
				(NodeParsedSentence) ElemGrammarBase.Verb.newNSD()//
						.addChildNC(ElemGrammarBase.Objectt.newNSD())//
		) {
			@Override
			public SubTransferResult implementsTransferRule(ATransferTranslationRuleBased transferer,
					NodeParsedSentence originalSubtree) {
				SubTransferResult r;
				NodeParsedSentence newVerb, newSubj, newObj, oldObj;
				// prodicing
				newVerb = ElemGrammarBase.Verb.newNSD();
				// (the old child must be taken)
				oldObj = (NodeParsedSentence) originalSubtree
						.getChildNCMostSimilarTo(ElemGrammarBase.Objectt.getSynonyms());
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
								.getChildNCMostSimilarTo(ElemGrammarBase.Subject.getSynonyms()), //
						newSubj);
				r.addPairOldNewNode(oldObj, newObj);
				r.addPairOldNewNode(originalSubtree, newVerb);
				return r;
			}
		});
		t.addRule(new TransferRule(//
				(NodeParsedSentence) ElemGrammarBase.Subject.newNSD()
						.addAlternatives(ElemGrammarBase.Noun.getSynonyms())//
						.addChildNC(ElemGrammarBase.Adjective.newNSD())//
		) {
			@Override
			public SubTransferResult implementsTransferRule(ATransferTranslationRuleBased transferer,
					NodeParsedSentence originalSubtree) {
				SubTransferResult r;
				NodeParsedSentence newSubj, newAdj, oldAdj;
				// prodicing
				newSubj = ElemGrammarBase.Subject.newNSD();
				// (the old child must be taken)
				oldAdj = (NodeParsedSentence) originalSubtree
						.getChildNCMostSimilarTo(ElemGrammarBase.Adjective.getSynonyms());
				newAdj = ElemGrammarBase.Adjective.newNSD(); // TODO sostituire col clone del nodo Adj figlio di
																// originalSubtree
				// wiring
				newSubj.addChildNC(newAdj);
				// mandatory invoke .. (bottom-up is better, but You are free)
				r = new SubTransferResult(newSubj);
				r.addPairOldNewNode(oldAdj, newAdj);
				r.addPairOldNewNode(originalSubtree, newSubj);
				return r;
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

		/*
		 * TODO WHY IS NOT BEING ADDED?? AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAH
		 */

		t.addRule(new TransferRule(//
				(NodeParsedSentence) ElemGrammarBase.Verb.newNSD()//
						.addChildNC(ElemGrammarBase.Subject.newNSD())//
						.addChildNC(ElemGrammarBase.Objectt.newNSD())//
		) {
			@Override
			public SubTransferResult implementsTransferRule(ATransferTranslationRuleBased transferer,
					NodeParsedSentence originalSubtree) {
				SubTransferResult r;
				NodeParsedSentence newVerb, newSubj, oldSubj, newObj, oldObj;
				System.out.println("----- siiiiii :D mi hanno sceltooooo :D ");
				// prodicing
				newVerb = ElemGrammarBase.Verb.newNSD();
				// (the old child must be taken)
				oldSubj = (NodeParsedSentence) originalSubtree
						.getChildNCMostSimilarTo(ElemGrammarBase.Subject.getSynonyms());
				oldObj = (NodeParsedSentence) originalSubtree
						.getChildNCMostSimilarTo(ElemGrammarBase.Objectt.getSynonyms());
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
				return r;
			}
		});

		t.addRule(new TransferRule(//
				(NodeParsedSentence) ElemGrammarBase.Verb.newNSD()//
						.addChildNC(ElemGrammarBase.Subject.newNSD())//
						.addChildNC(ElemGrammarBase.Aux.newNSD())//
						.addChildNC(ElemGrammarBase.Objectt.newNSD())//
		) {
			@Override
			public SubTransferResult implementsTransferRule(ATransferTranslationRuleBased transferer,
					NodeParsedSentence originalSubtree) {
				SubTransferResult r;
				NodeParsedSentence newVerb, newSubj, oldSubj, newObj, oldObj, newAux, oldAux;
				// prodicing
				newVerb = ElemGrammarBase.Verb.newNSD();
				// (the old child must be taken)
				oldSubj = (NodeParsedSentence) originalSubtree
						.getChildNCMostSimilarTo(ElemGrammarBase.Subject.getSynonyms());
				oldObj = (NodeParsedSentence) originalSubtree
						.getChildNCMostSimilarTo(ElemGrammarBase.Objectt.getSynonyms());
				oldAux = (NodeParsedSentence) originalSubtree
						.getChildNCMostSimilarTo(ElemGrammarBase.Aux.getSynonyms());
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
				return r;
			}
		});
		t.addRule(new TransferRule(//
				(NodeParsedSentence) ElemGrammarBase.Verb.newNSD()//
						.addChildNC(ElemGrammarBase.Aux.newNSD().addAlternatives(ElemGrammarBase.Subject.getSynonyms()))//
		) {
			@Override
			public SubTransferResult implementsTransferRule(ATransferTranslationRuleBased transferer,
					NodeParsedSentence originalSubtree) {
				SubTransferResult r;
				NodeParsedSentence newVerb, newAux, oldAux;
				// prodicing
				newVerb = ElemGrammarBase.Verb.newNSD();
				r = new SubTransferResult(newVerb);
				// (the old child must be taken)
				SynonymSet auxSyns;
				auxSyns = ElemGrammarBase.Aux.getSynonymsClone();
				ElemGrammarBase.Subject.getSynonyms().forEach(auxSyns::addAlternative);
				oldAux = (NodeParsedSentence) originalSubtree.getChildNCMostSimilarTo(auxSyns);
				if (oldAux != null) {

				}
				// TODO sistemare il soggetto del verbo
//				if (oldAux != null && "VA".equals(oldAux.getPos())) {
//					// and then new ones
//					newAux = oldAux.clone(); // new NodeParsedSentence(auxSyns);
//					// wiring
//					newVerb.addChildNC(newAux);
//					r.addPairOldNewNode(oldAux, newAux);
//				} else if (oldAux == null) {
//					System.out.println("\n\n I'M NULL HERE 2, WITH ORIGINAL SUBTREE:");
//					System.out.println(originalSubtree);
//				}
				r.addPairOldNewNode(originalSubtree, newVerb);
				return r;
			}
		});

		// forme verbali composte?
		t.addRule(new TransferRule(//
				(NodeParsedSentence) ElemGrammarBase.Verb.newNSD()//
						.addChildNC(ElemGrammarBase.Aux.newNSD().addAlternatives(ElemGrammarBase.Verb.getSynonyms())
								.addAlternative("VA"))//
		) {
			@Override
			public SubTransferResult implementsTransferRule(ATransferTranslationRuleBased transferer,
					NodeParsedSentence originalSubtree) {
				SubTransferResult r;
				NodeParsedSentence newVerb, newAux, oldAux;
				// prodicing
				newVerb = ElemGrammarBase.Verb.newNSD();
				r = new SubTransferResult(newVerb);
				// (the old child must be taken)
				SynonymSet auxSyns;
				auxSyns = ElemGrammarBase.Aux.getSynonymsClone();
				ElemGrammarBase.Verb.getSynonyms().forEach(auxSyns::addAlternative);
				oldAux = (NodeParsedSentence) originalSubtree.getChildNCMostSimilarTo(auxSyns);
				if (oldAux != null && "VA".equals(oldAux.getPos())) {
					// and then new ones
					newAux = oldAux.clone(); // new NodeParsedSentence(auxSyns);
					// wiring
					newVerb.addChildNC(newAux);
					r.addPairOldNewNode(oldAux, newAux);
				} else if (oldAux == null) {
					System.out.println("\n\n I'M NULL HERE, WITH ORIGINAL SUBTREE:");
					System.out.println(originalSubtree);
				}
				r.addPairOldNewNode(originalSubtree, newVerb);
				return r;
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
//			public  SubTransferResult implementsTransferRule(ATransferTranslationRuleBased transferer,
//					NodeParsedSentence originalSubtree) {SubTransferResult r;
//				NodeParsedSentence newVerb, newSubj, newObj, oldSubj, oldObjs;
//				// prodicing
//				newVerb = ElemGrammarBase.Verb.newNSD();
//				// (the old child must be taken)
//				oldSubj = (NodeParsedSentence) originalSubtree.getChildNCMostSimilarTo(ElemGrammarBase.Subject.getElemGrammarBase());
//				if (oldSubj == null) {
//					oldSubj = (NodeParsedSentence) originalSubtree.getChildNCMostSimilarTo(ElemGrammarBase.Noun.getElemGrammarBase());
//				}
//				oldObjs = (NodeParsedSentence) originalSubtree.getChildNCMostSimilarTo(ElemGrammarBase.Objectt.getElemGrammarBase());
//				if (oldObjs == null) {
//					oldObjs = (NodeParsedSentence) originalSubtree.getChildNCMostSimilarTo(ElemGrammarBase.Noun.getElemGrammarBase());
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
//				return r;
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
		public SubTransferResult implementsTransferRule(ATransferTranslationRuleBased transferer,
				NodeParsedSentence originalSubtree) {
			NodeParsedSentence newNode;
			newNode = originalSubtree.clone(); // egb.newNSD();
			return new SubTransferResult(newNode).addPairOldNewNode(originalSubtree, newNode);
		}
	}
}