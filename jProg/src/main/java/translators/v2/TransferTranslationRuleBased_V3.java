package translators.v2;

import java.util.Map.Entry;
import java.util.function.Consumer;

import common.NodeParsedSentence;
import dataStructures.MapTreeAVL;
import dataStructures.NodeComparable;
import tools.CloserGetter;
import tools.ClosestMatch;
import tools.NodeComparableSynonymIndexed;
import tools.SynonymSet;

/** @deprecated use {@link TransferTranslationRuleBased_V4} */
@Deprecated
public class TransferTranslationRuleBased_V3 extends ATransferTranslationRuleBased {

	public TransferTranslationRuleBased_V3() {
		rulesGivenLHS = MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight, SynonymSet.COMPARATOR_SYNONYM_SET);
//		ruleCollectorByLHSAsChild = new NodeParsedSentence("i'm just a root");
//	<TransferTranslationItEng3.ElementGrammarWithAlternatives, List<TransferRule>>		
	}

	/**
	 * All rules have, as a key, a {@link NodeParsedSentence} <code>lhs</code>
	 * (<i>Left Hand Side</i>) field (and it's publicly known and available).<br>
	 * That <code>lhs</code> has a {@link SynonymSet} as a key (as specified by
	 * {@link NodeComparableSynonymIndexed} and
	 * {@link NodeComparable#getKeyIdentifier()}), so a {@link TransferRule} can be
	 * "transitively" identified by that {@link SynonymSet}.
	 */
	// protected MapTreeAVL<SynonymSet, TransferRule> rulesGivenLHS;
	protected MapTreeAVL<SynonymSet, TransferRule> rulesGivenLHS;

	@Override
	public void forEachRule(Consumer<TransferRule> c) { this.rulesGivenLHS.forEach((syn, rule) -> c.accept(rule)); }

	@Override
	public void addRule(TransferRule rule) {
		/*
		 * add the LHS to the rule collector and identifies the rule by the LHS's
		 * synonym's set
		 */
		SynonymSet lhsSyn;
		lhsSyn = rule.lhsTemplate.getKeyIdentifier();
//		ruleCollectorByLHSAsChild.addChildNC(rule.lhsTemplate);
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
		ClosestMatch<Entry<SynonymSet, TransferRule>> ruleMatched = this.rulesGivenLHS
				.closestMatchOf(subtreeToTransfer.getKeyIdentifier());
		if (ruleMatched == null)
			return null;
		System.out.println("____ best closest match:\n\t lower value: ");
		System.out.println(ruleMatched.nearestLowerOrExact);
		System.out.print("\t upper value: ");
		System.out.println(ruleMatched.nearestLowerOrExact);
		System.out.println();
		// a "ClosestMatch" could have an exact match or just approximation
		return ruleMatched.getClosetsMatchToOriginal((eo, e1, e2) -> CloserGetter.getCloserTo(eo,
				(e11, e22) -> SynonymSet.DIFFERENCE_CALCULATOR.getDifference(e11.getKey(), e22.getKey()), e1, e2))
				.getValue();
	}
}