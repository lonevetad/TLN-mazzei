package translators.secondWay;

import java.util.Map;
import java.util.function.Consumer;

import common.NodeParsedSentence;
import dataStructures.MapTreeAVL;
import tools.SynonymSet;

/** Second version of {@link TransferTranslationRuleBased}. */
@Deprecated
public class TransferTranslationRuleBased_V2 extends ATransferTranslationRuleBased {

	public TransferTranslationRuleBased_V2() {
		rulesGivenLHS = MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight, SynonymSet.COMPARATOR_SYNONYM_SET);
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

	@Override
	protected TransferRule getBestRuleFor(NodeParsedSentence subtreeToTransfer) {
		NodeParsedSentence maybeALhs;
		maybeALhs = (NodeParsedSentence) this.ruleCollectorByLHSAsChild.getChildNCMostSimilarTo(subtreeToTransfer);
		return (maybeALhs == null) ? null : rulesGivenLHS.get(maybeALhs.getKeyIdentifier());
	}

	@Override
	public void forEachRule(Consumer<TransferRule> c) { this.rulesGivenLHS.forEach((lhs, r) -> c.accept(r)); }
}