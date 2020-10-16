package common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import dataStructures.MapTreeAVL;
import dataStructures.SetMapped;
import edu.emory.mathcs.backport.java.util.Arrays;
import tools.Misc;
import tools.NodeComparable;
import tools.NodeComparable.NodeComparableDefaultAlghoritms;

public class TransferTranslationItEng3 {

	public TransferTranslationItEng3() {}

	/**
	 * All rules are stored here but grouped by
	 * <code>TransferRule.lhs.getKeyIdentifier()</code>.
	 */
	protected Map<ElementGrammarWithAlternatives, List<TransferRule>> rulesGroupedByRoot;

	public void addRule(TransferRule rule) {
		ElementGrammarWithAlternatives rootRule;
		List<TransferRule> l;
		rootRule = rule.lhs.getKeyIdentifier();
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

	public TransferRule getBestRuleFor(NodeSubtreeDependency subtreeToTransfer) {
		int len;
		ElementGrammarWithAlternatives rootST;
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
//best fitting: sort in descending order, pick the first (best)
		Arrays.sort(scores, RuleScored.RuleScored_COMPARATOR);
		return scores[0].rule;
	}

	//

	protected RuleScored newRuleScored(TransferRule rule, long score) { return new RuleScored(score, rule); }

	//

	//

	// TODO utilities

	//

	//

	/** Could be a PoS-Tag or a dependency. */
	public static class ElementGrammarWithAlternatives {
		public static final Comparator<ElementGrammarWithAlternatives> EG_COMPARATOR = (eg1, eg2) -> {
			boolean equal;
			int c;
			Iterator<String> i1, i2;
			Comparator<String> comp;
			if (eg1 == eg2)
				return 0;
			if (eg1 == null)
				return -1;
			if (eg2 == null)
				return 1;
			i1 = eg1.bm.iteratorKey();
			i2 = eg2.bm.iteratorKey();
			equal = true;
			comp = Misc.STRING_COMPARATOR;
			c = 0;
			while (equal && i1.hasNext() && i2.hasNext()) {
				equal = (c = comp.compare(i1.next(), i2.next())) == 0;
			}
			return c;
		};

		protected ElementGrammarWithAlternatives(String[] alternatives) {
			int i;
			bm = MapTreeAVL.newMap(MapTreeAVL.Optimizations.MinMaxIndexIteration, Misc.STRING_COMPARATOR);
			Set<String> a = this.alternatives = bm.toSetKey();
			for (String s : alternatives) {
				a.add(s);
				i = s.indexOf(':');
				if (i >= 0) { // something like "adj:poss" -> take "adj" also
					s = s.substring(0, i).trim();
					if (!s.isEmpty())
						a.add(s);
				}
			}
		}

		protected MapTreeAVL<String, String> bm;
		protected Set<String> alternatives;

		public boolean contains(String lal) { return alternatives.contains(lal); }

		public void forEach(Consumer<String> action) { alternatives.forEach(action); }

		public List<String> toList() { return Collections.unmodifiableList(bm.toList()); }

		public Iterator<String> iterator() { return alternatives.iterator(); }

		public boolean hasApplicableAlternatives(ElementGrammarWithAlternatives eg) {
			int s1, s2;
			if (eg == this)
				return true;
			if (eg == null ||
			// se uno è empty -> return false
					((s1 = this.bm.size()) > 0) != (((s2 = eg.bm.size()) > 0)))
				return false;
			// basically, compute an intersection.. if they intersects -> true
			if (s1 > s2) {// the tiniest check over the "less than linear" bigger
				for (String s : this.alternatives) {
					if (eg.contains(s))
						return true;
				}
			} else {
				for (String s : eg.alternatives) {
					if (this.contains(s))
						return true;
				}
			}
			return false;
		}
	}

	//

	// TODO SUBDEPENDENCY

	//

	/**
	 * Class representing node of a structure for "dependency", like:
	 * <code>("The" <- Subject -> Adjective) <- Verb[root] -> (Object -> Adverb)</code>.<br>
	 * (NN.B.: each point comes from a "governor/father" and goes into a
	 * "dependent/child", so one of them is the root.)
	 */
	public static class NodeSubtreeDependency extends NodeComparableDefaultAlghoritms<ElementGrammarWithAlternatives> {
		protected static final Function<NodeSubtreeDependency, NodeComparable<ElementGrammarWithAlternatives>> IDENTITY_FUNCTION_JC_GENERICS_TOO_RESTRICTIVE = (
				nstd) -> nstd;
		private static final long serialVersionUID = -1111222233344L;
		protected final ElementGrammarWithAlternatives rootSubtreeDep;
		protected final MapTreeAVL<ElementGrammarWithAlternatives, NodeSubtreeDependency> backMapChildren;
		protected final Set<ElementGrammarWithAlternatives> childrenSubtreeDep;
		protected final Set<NodeComparable<ElementGrammarWithAlternatives>> childrenNodeComparable;

//		public SubtreeDependency(ElementGrammarWithAlternatives nodeSubtreeDep) { this(nodeSubtreeDep, null); }

		public NodeSubtreeDependency(ElementGrammarWithAlternatives nodeSubtreeDep) {
//				Set<ElementGrammarWithAlternatives> childrenSubtreeDep
			super();
			this.rootSubtreeDep = nodeSubtreeDep;
//			this.childrenSubtreeDep = childrenSubtreeDep;
			this.backMapChildren = MapTreeAVL.newMap(MapTreeAVL.Optimizations.MinMaxIndexIteration,
					ElementGrammarWithAlternatives.EG_COMPARATOR);
			this.childrenSubtreeDep = this.backMapChildren.keySet();
			this.childrenNodeComparable =
					// need to "run over" Java Compiler's generics ..
					new SetMapped<>(this.backMapChildren.toSetValue(n -> n.getKeyIdentifier())//
							, IDENTITY_FUNCTION_JC_GENERICS_TOO_RESTRICTIVE // .. by using an identity function ...
					);
		}

		@Override
		public ElementGrammarWithAlternatives getKeyIdentifier() { return rootSubtreeDep; }

		@Override
		public Set<NodeComparable<ElementGrammarWithAlternatives>> getChildrenNC() { return childrenNodeComparable; }

		@Override
		public NodeComparable<ElementGrammarWithAlternatives> getChildNCBy(ElementGrammarWithAlternatives key) {
			return this.backMapChildren.get(key);
		}
	}

//	public static class SubtreeDependency {
//		protected final SubtreeDependency root;
//	}

	// TODO RULE

	/** THE rule. */
	public static abstract class TransferRule {
		protected final NodeSubtreeDependency lhs;

		public TransferRule(NodeSubtreeDependency lhs) {
			super();
			this.lhs = lhs;
		}

		/** See {@link NodeComparable#computeDissonanceAsLong(NodeComparable)}. */
		public long scoreDifferences(NodeSubtreeDependency givenSubtree) {
			return lhs.computeDissonanceAsLong(givenSubtree);
		}

		public abstract NodeSubtreeDependency applyTransferRule(NodeSubtreeDependency originalSubtree);
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
