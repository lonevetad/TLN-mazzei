package translators.v2;

import java.util.Map.Entry;
import java.util.function.Consumer;

import common.NodeParsedSentence;
import dataStructures.MapTreeAVL;
import tools.CloserGetter;
import tools.ClosestMatch;
import tools.NodeComparableSynonymIndexed;

public class TransferTranslationRuleBased_V4 extends ATransferTranslationRuleBased {

	public TransferTranslationRuleBased_V4() {
		rulesGivenLHS = MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight, NodeParsedSentence.COMPARATOR_NODE_NPS);
	}

	protected MapTreeAVL<NodeParsedSentence, TransferRule> rulesGivenLHS;

	@Override
	public void forEachRule(Consumer<TransferRule> c) { this.rulesGivenLHS.forEach((syn, rule) -> c.accept(rule)); }

	@Override
	public void addRule(TransferRule rule) { rulesGivenLHS.put(rule.lhsTemplate, rule); }

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
//		NodeParsedSentence maybeALhs;
		ClosestMatch<Entry<NodeParsedSentence, TransferRule>> ruleMatched = this.rulesGivenLHS
				.closestMatchOf(subtreeToTransfer);
		if (ruleMatched == null)
			return null;
		// a "ClosestMatch" could have an exact match or just approximation
		return ruleMatched.getClosetsMatchToOriginal((eo, e1, e2) -> CloserGetter.getCloserTo(eo,
				(e11, e22) -> NodeComparableSynonymIndexed.DIFF_COMPUTER_NODE.getDifference(e11.getKey(), e22.getKey()),
				e1, e2)).getValue();
	}
}