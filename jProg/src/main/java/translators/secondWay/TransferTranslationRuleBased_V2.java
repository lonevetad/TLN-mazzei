package translators.secondWay;

import java.util.Map;

import common.NodeParsedSentence;
import dataStructures.MapTreeAVL;
import tools.SynonymSet;

/** Second version of {@link TransferTranslationRuleBased}. */
public class TransferTranslationRuleBased_V2 extends ATransferTranslationRuleBased {

	public TransferTranslationRuleBased_V2() {
		rulesGivenLHS = MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight, SynonymSet.COMPARATOR);
		ruleCollectorByLHSAsChild = new NodeParsedSentence("i'm just a root");
//	<TransferTranslationItEng3.ElementGrammarWithAlternatives, List<TransferRule>>		
	}

	/**
	 * All rules are stored here in its child, because
	 * {@link NodeParsedSentence#getChildNCByKey(SynonymSet)} performs a good job of
	 * looking up nodes
	 */
	protected NodeParsedSentence ruleCollectorByLHSAsChild;
	protected Map<SynonymSet, TransferRule> rulesGivenLHS;

	@Override
	public void addRule(TransferRule rule) {
		/*
		 * add the LHS to the rule collector and identifies the rule by the LHS's
		 * synonym's set
		 */
		SynonymSet lhsSyn;
		lhsSyn = rule.lhsTemplate.getKeyIdentifier();
		ruleCollectorByLHSAsChild.addChildNC(rule.lhsTemplate);
		rulesGivenLHS.put(lhsSyn, rule);
	}

	@Override
	public NodeParsedSentence transfer(NodeParsedSentence rootSubtree) {
		TransferRule rule;
//		NodeSubtreeDependency transferred;
		System.out.println("\n searching");
		rule = this.getBestRuleFor(rootSubtree);
		System.out.println("best rule is");
		if (rule == null) {
			System.out.println(".......null");
		} else {
			System.out.println(rule);
		}
		return rule == null ? null : rule.applyTransferRule(this, rootSubtree);
	}

	// TODO USE MapTreeAVL's bestMatch to create a comparator or query to extract
	// rules on rulesGivenLHS
	@Override
	protected TransferRule getBestRuleFor(NodeParsedSentence subtreeToTransfer) {
		NodeParsedSentence maybeALhs;
		maybeALhs = (NodeParsedSentence) this.ruleCollectorByLHSAsChild
				.getChildNCByKey(subtreeToTransfer.getKeyIdentifier());
		return (maybeALhs == null) ? null : rulesGivenLHS.get(maybeALhs.getKeyIdentifier());
	}
}