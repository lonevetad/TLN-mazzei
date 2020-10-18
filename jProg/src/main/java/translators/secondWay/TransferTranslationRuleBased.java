package translators.secondWay;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import common.NodeParsedSentence;
import dataStructures.MapTreeAVL;
import tools.NodeComparable;
import tools.NodeComparableSynonymIndexed;
import tools.SynonymSet;

/**
 * Idea: si inizia a tradurre (usando
 * {@link TransferTranslationItEng3#transfer(NodeSubtreeDependency)}, che invoca
 * {@link TransferRule#applyTransferRule(TransferTranslationItEng3, NodeSubtreeDependency)}
 * ) un nodo radice.:
 * <ol>
 * <li>Si cerca la {@link TransferRule} migliore per il dato nodo</li>
 * <li>Si converte il sotto-albero, producendo un nuovo sottoalbero (a mano,
 * secondo l'implementazione della regola)</li>
 * <li>Per ogni nodo non-foglia considerato (e plausibilmente convertito) dalla
 * regola, si invoca
 * {@link TransferRule#manageUntouchedNodes(NodeSubtreeDependency, TransferTranslationItEng3, NodeSubtreeDependency, NodeSubtreeDependency[])}
 * per gestire tutti i figli che non sono considerati dalla regola.</li>
 * <li>Per ogni nodo foglia considerato, invocare ricorsivamente
 * {@link TransferTranslationItEng3#transfer(NodeSubtreeDependency)}
 * ricorsivamente.</li>
 * </ol> 
 */
/**
 * Apply the transfer translation (through
 * {@link #transfer(NodeSubtreeDependency)}) on a given parsed-sentence tree to
 * produce a new one, depending on the rules ({@link TransferRule}) which this
 * instance is based on (added via {@link #addRule(TransferRule)}).
 */
public class TransferTranslationRuleBased {

	public TransferTranslationRuleBased() {
		rulesGroupedByRoot = MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight, SynonymSet.SYNONYM_COMPARATOR);
//	<TransferTranslationItEng3.ElementGrammarWithAlternatives, List<TransferRule>>		
	}

	/**
	 * All rules are stored here but grouped by
	 * <code>TransferRule.lhs.getKeyIdentifier()</code>.
	 */
	protected Map<SynonymSet, List<TransferRule>> rulesGroupedByRoot;

	//
	// TODO THE TRANSFER METHOD
	//

	public NodeParsedSentence transfer(NodeParsedSentence rootSubtree) {
		TransferRule rule;
//		NodeSubtreeDependency transferred;
		rule = this.getBestRuleFor(rootSubtree);
		return rule == null ? null : rule.applyTransferRule(this, rootSubtree);
	}

	// TODO UTILITIES

	public void addRule(TransferRule rule) {
		SynonymSet rootRule;
		List<TransferRule> l;
		rootRule = rule.lhsTemplate.getKeyIdentifier();
		l = rulesGroupedByRoot.get(rootRule);
		if (l == null) {
			l = new ArrayList<>();
			l.add(rule);
			rulesGroupedByRoot.put(rootRule, l);
		} else {
			if (!l.contains(rule))
				l.add(rule);
		}
	}

	protected TransferRule getBestRuleFor(NodeParsedSentence subtreeToTransfer) {
		int len;
		SynonymSet rootST;
		RuleScored[] scores;
		List<TransferRule> l;
		TransferRule currentRule;
		rootST = subtreeToTransfer.getKeyIdentifier();
		l = rulesGroupedByRoot.get(rootST);
		if (l == null)
			return null;
		scores = new RuleScored[len = l.size()];
//score all rules, then pick the best fitting (the lowest);
		while (--len >= 0) {
			// score and collect
			currentRule = l.get(len);
			scores[len] = newRuleScored(currentRule, currentRule.scoreDifferences(subtreeToTransfer));
		}
		return pickTheBest(scores).rule;
	}

	//

	protected RuleScored newRuleScored(TransferRule rule, long score) { return new RuleScored(score, rule); }

	protected RuleScored pickTheBest(RuleScored[] scores) {
		int i;
		RuleScored best, temp;
//		OLD best fitting: sort in descending order, pick the first (best)
//		Arrays.sort(scores, RuleScored.RuleScored_COMPARATOR);
//		return scores[0];
		// NEW: no need to sort it, just scan for best (the lower the better)
		i = scores.length - 1;
		best = scores[i];
		while (--i >= 0) {
			if ((temp = scores[i]).scoreDifference < best.scoreDifference)
				best = temp;
		}
		return best;
	}

	//

	//

	// TODO utilities
	@Override
	public String toString() {
		StringBuilder sb;
		sb = new StringBuilder();
		sb.append("Transfer:\n");
		rulesGroupedByRoot.forEach((rootEG, rulesCollected) -> {
			sb.append("\n\n\n\ncollection of rules grouped with node:\n");
			rootEG.toString(sb);
			sb.append("\nrules:");
			rulesCollected.forEach(tr -> {
				sb.append('\n');
				tr.toString(sb);
			});
		});
		return sb.toString();
	}

	//

	//

	// TODO CLASSES

	//

	//

	//

	// TODO SUBDEPENDENCY

	//

//	public static class SubtreeDependency {
//		protected final NodeSubtreeDependency root;
//	}

	// TODO RULE

	/**
	 * THE rule.<br>
	 * It requires a "template" of a sentence's subtree (like Subject <- Verb ->
	 * Object) that is accepted by this rule and can tell how much a given
	 * sentence's portion is applicable to this rule (through
	 * {@link #scoreDifferences(NodeParsedSentence)}).<br>
	 * The rule can be applied to a given sentence's subtree to produce a new
	 * "transferred" sentence's subtree by calling
	 * {@link #applyTransferRule(TransferTranslationRuleBased, NodeParsedSentence)}).
	 * The first parameter is the "translator" holding this rule-instance.
	 */
	public static abstract class TransferRule {
		protected final NodeParsedSentence lhsTemplate;

		public TransferRule(NodeParsedSentence lhs) {
			super();
			this.lhsTemplate = lhs;
		}

		/** See {@link NodeComparable#computeDissonanceAsLong(NodeComparable)}. */
		public long scoreDifferences(NodeParsedSentence givenSubtree) {
			return givenSubtree.computeDissonanceAsLong(lhsTemplate);
		}

		/**
		 * NOTE: remember to invoke
		 * {@link TransferRule#manageUntouchedChildredUpontransfer(NodeSubtreeDependency, TransferTranslationRuleBased, NodeSubtreeDependency)}
		 * on EACH newly created node.
		 * <p>
		 * Applying a rule means performing the following steps:
		 * <ol>
		 * <li>take a sentence sub-tree to be transfer (by passing the sub-root node as
		 * argument)</li>
		 * <li>produce the new subtree by allocating new nodes (this depends on this
		 * method's implementation).</li>
		 * <li>wire them up (again, this depends on this method's implementation).</li>
		 * </ol>
		 * <br>
		 * After this (REMEMBER TO WIRE the newly created node), You are obligated to
		 * perform the following steps:
		 * <ul>
		 * <li>for each newly produced node, invoke
		 * {@link TransferRule#manageUntouchedChildredUpontransfer(NodeSubtreeDependency, TransferTranslationRuleBased, NodeSubtreeDependency)}
		 * passing as first parameter the original node, as third parameter that newly
		 * produced node.</li>
		 * </ul>
		 */
//		* <li>for each newly produced LEAF node {@link TransferRule}</li>
		public abstract NodeParsedSentence applyTransferRule(TransferTranslationRuleBased transferer,
				NodeParsedSentence originalSubtree);

//		protected final void manageNewlyProducedLeafNodes(NodeSubtreeDependency originalNonleafNode,
//				TransferTranslationItEng3 transferer, NodeSubtreeDependency newlyProducedByLeafNode) {

		/**
		 * See
		 * {@link #applyTransferRule(TransferTranslationRuleBased, NodeSubtreeDependency)},
		 * since there You'll find what this method is, what are its parameters and
		 * where and why it MUST bew invoked
		 */
		// protected final void
		// manageUntouchedChildredUpontransfer(NodeSubtreeDependency
		// originalNonleafNode,
//				NodeSubtreeDependency[] childrenOriginalUntouched, TransferTranslationItEng3 transferer,
//				NodeSubtreeDependency newlyProducedByTransferNode) {
		protected final void manageUntouchedChildredUpontransfer(NodeParsedSentence originalNode,
				TransferTranslationRuleBased transferer, NodeParsedSentence newlyProducedByTransferNode) {
			if (originalNode == null)
				/*
				 * the new node is synthetic (like "soggetto sottointeso" in Italian, that is
				 * missing in English?)
				 */
				return;
			/*
			 * for each node non "touched" (i.e., not present in newlyBlaBla.children),
			 * transfer it AND wire the newly produced node into the newlyBla's children
			 * set.
			 */
			originalNode.forEachChildNC(child -> {
				if (!newlyProducedByTransferNode.containsChildNC(child.getKeyIdentifier())) {
					NodeComparableSynonymIndexed transferedChild;
					transferedChild = transferer.transfer((NodeParsedSentence) child);
					newlyProducedByTransferNode.addChildNC(transferedChild);
				}
			});
		}

		@Override
		public String toString() { return "TransferRule [lhs=" + lhsTemplate + "]"; }

		public void toString(StringBuilder sb) {
			sb.append("++++TransferRule:\n");
			this.lhsTemplate.toString(sb);
		}
	}

	public static class RuleScored {
		public static final Comparator<RuleScored> RuleScored_COMPARATOR = (s1, s2) -> {
			if (s1 == s2)
				return 0;
			if (s1 == null)
				return -1;
			if (s2 == null)
				return 1;
			return Long.compare(s1.scoreDifference, s2.scoreDifference);
		};
		protected Long scoreDifference;
		protected TransferRule rule;

		public RuleScored(Long scoreDifference, TransferRule rule) {
			super();
			this.scoreDifference = scoreDifference;
			this.rule = rule;
		}
	}
}
