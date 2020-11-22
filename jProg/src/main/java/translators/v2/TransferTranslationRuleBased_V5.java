package translators.secondWay;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import common.NodeParsedSentence;
import dataStructures.MapTreeAVL;

public class TransferTranslationRuleBased_V5 extends ATransferTranslationRuleBased {

	public TransferTranslationRuleBased_V5() {
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
		// V4
//		ClosestMatch<Entry<NodeParsedSentence, TransferRule>> ruleMatched = this.rulesGivenLHS
//				.closestMatchOf(subtreeToTransfer);
//		if (ruleMatched == null)
//			return null;
//		// a "ClosestMatch" could have an exact match or just approximation
//		return ruleMatched.getClosetsMatchToOriginal((eo, e1, e2) -> CloserGetter.getCloserTo(eo,
//				(e11, e22) -> NodeComparableSynonymIndexed.DIFF_COMPUTER_NODE.getDifference(e11.getKey(), e22.getKey()),
//				e1, e2)).getValue();

		//
		// V5
		int[] i;
		List<Entry<NodeParsedSentence, TransferRule>> collected;
		RuleScored[] rulesScored;
		collected = new LinkedList<>();
		this.rulesGivenLHS.forEachSimilar(subtreeToTransfer, //
				(n1, n2) -> {
					// compare just the root's keys
					return NodeParsedSentence.getNPSComparatorKey().compare(n1.getKeyIdentifier(),
							n2.getKeyIdentifier());
				}, //
//				e -> collected.add(e.getKey())
				collected::add);
		if (collected.isEmpty())
			return null;
		// fill the array
		i = new int[] { 0 };
		rulesScored = new RuleScored[collected.size()];
		collected.forEach(e -> {
			rulesScored[i[0]++] = new RuleScored(subtreeToTransfer.computeDissonanceAsLong(e.getKey()), e.getValue());
		});
		return pickTheBest(rulesScored).rule;
	}

	protected RuleScored pickTheBest(RuleScored[] scores) {
		int i;
		RuleScored best, temp;
		i = scores.length - 1;
		best = scores[i];
		while (--i >= 0) {
			if ((temp = scores[i]).scoreDifference < best.scoreDifference)
				best = temp;
		}
//		System.out.println("best rule: " + best);
		return best;
	}
}