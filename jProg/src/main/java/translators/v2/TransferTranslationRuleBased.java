package translators.v2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import common.NodeParsedSentence;
import dataStructures.MapTreeAVL;
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
 * <p>
 * Resemble the "Percolation Table".
 */
@Deprecated
public class TransferTranslationRuleBased extends ATransferTranslationRuleBased {

	public TransferTranslationRuleBased() {
		rulesGroupedByRoot = MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight, SynonymSet.COMPARATOR_SYNONYM_SET);
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

	@Override
	public NodeParsedSentence transfer(NodeParsedSentence rootSubtree) {
		TransferRule rule;
//		NodeSubtreeDependency transferred;
		System.out.println("\n\n searching");
		rule = this.getBestRuleFor(rootSubtree);
		return rule == null ? null : rule.applyTransferRule(this, rootSubtree);
	}

	// TODO UTILITIES

	@Override
	public void addRule(TransferRule rule) {
		SynonymSet rootRule;
		List<TransferRule> l;
		rootRule = rule.lhsTemplate.getKeyIdentifier();
		l = rulesGroupedByRoot.get(rootRule);
		System.out.println("adding rule");
		System.out.println(rule);
		if (l == null) {
			l = new ArrayList<>();
			l.add(rule);
			rulesGroupedByRoot.put(rootRule, l);
		} else {
			if (!l.contains(rule))
				l.add(rule);
		}
	}

	@Override
	protected TransferRule getBestRuleFor(NodeParsedSentence subtreeToTransfer) {
		int len;
		SynonymSet rootST;
		RuleScored[] scores;
		List<TransferRule> l;
		TransferRule currentRule;
		rootST = subtreeToTransfer.getKeyIdentifier();
		l = rulesGroupedByRoot.get(rootST);
		if (l == null) {
			System.out.println("NO RULE FOR " + rootST);
			return null;
		}
		scores = new RuleScored[len = l.size()];
//score all rules, then pick the best fitting (the lowest);
		while (--len >= 0) {
			// score and collect
			currentRule = l.get(len);
			/*
			 * TODO: further developments could rates how much a singlr node is compatible
			 * (inverting the scoring order from the actual one?)
			 */
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
//		System.out.println("best rule: " + best);
		return best;
	}

	@Override
	public void forEachRule(Consumer<TransferRule> c) { this.rulesGroupedByRoot.forEach((ss, l) -> l.forEach(c)); }

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
}