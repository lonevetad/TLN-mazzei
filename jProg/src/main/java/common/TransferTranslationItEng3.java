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

import common.TransferTranslationItEng3.NodeSubtreeDependency;
import dataStructures.MapTreeAVL;
import dataStructures.SetMapped;
import edu.emory.mathcs.backport.java.util.Arrays;
import tools.Misc;
import tools.NodeComparable;
import tools.NodeComparable.NodeComparableDefaultAlghoritms;
import tools.Stringable;

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
 * per gestire tuti i figli che non sono considerati dalla regola.</li>
 * <li>Per ogni nodo foglia considerato, invocare ricorsivamente
 * {@link TransferTranslationItEng3#transfer(NodeSubtreeDependency)}
 * ricorsivamente.</li>
 * </ol>
 */
/**/
public class TransferTranslationItEng3 {

	public TransferTranslationItEng3() {
		rulesGroupedByRoot = MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight,
				ElementGrammarWithAlternatives.EG_COMPARATOR);
//	<TransferTranslationItEng3.ElementGrammarWithAlternatives, List<TransferRule>>		
	}

	/**
	 * All rules are stored here but grouped by
	 * <code>TransferRule.lhs.getKeyIdentifier()</code>.
	 */
	protected Map<ElementGrammarWithAlternatives, List<TransferRule>> rulesGroupedByRoot;

	//
	// TODO THE TRANSFER METHOD
	//

	public NodeSubtreeDependency transfer(NodeSubtreeDependency rootSubtree) {
		TransferRule rule;
//		NodeSubtreeDependency transferred;
		rule = this.getBestRuleFor(rootSubtree);
		return rule.applyTransferRule(this, rootSubtree);
	}

	// TODO UTILITIES

	public void addRule(TransferRule rule) {
		ElementGrammarWithAlternatives rootRule;
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

	protected TransferRule getBestRuleFor(NodeSubtreeDependency subtreeToTransfer) {
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

		public ElementGrammarWithAlternatives(String[] alternatives) {
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

		@Override
		public String toString() {
			StringBuilder sb;
			sb = new StringBuilder();
			toString(sb);
			return sb.toString();
		}

		public void toString(StringBuilder sb) {
			sb.append("EG: [");

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
	public static class NodeSubtreeDependency extends NodeComparableDefaultAlghoritms<ElementGrammarWithAlternatives>
			implements Stringable {
		protected static final Function<NodeSubtreeDependency, NodeComparable<ElementGrammarWithAlternatives>> IDENTITY_FUNCTION_JC_GENERICS_TOO_RESTRICTIVE = (
				nstd) -> nstd;
		private static final long serialVersionUID = -1111222233344L;

		public static NodeSubtreeDependency newNSD(ElementGrammarWithAlternatives dep) {
			return new NodeSubtreeDependency(dep);
		}

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

		protected final ElementGrammarWithAlternatives rootSubtreeDep;
		protected final MapTreeAVL<ElementGrammarWithAlternatives, NodeSubtreeDependency> backMapChildren;
		protected final Set<ElementGrammarWithAlternatives> childrenSubtreeDep;
		protected final Set<NodeComparable<ElementGrammarWithAlternatives>> childrenNodeComparable;

		@Override
		public ElementGrammarWithAlternatives getKeyIdentifier() { return rootSubtreeDep; }

		@Override
		public Set<NodeComparable<ElementGrammarWithAlternatives>> getChildrenNC() { return childrenNodeComparable; }

		@Override
		public NodeComparable<ElementGrammarWithAlternatives> getChildNCByKey(ElementGrammarWithAlternatives key) {
			return this.backMapChildren.get(key);
		}

		public NodeComparable<ElementGrammarWithAlternatives> getChildNCBySingleKey(String key) {
			return this.getChildNCByKey(new ElementGrammarWithAlternatives(new String[] { key }));
		}

		@Override
		public String toString() {
			StringBuilder sb;
			sb = new StringBuilder();
			toString(sb);
			return sb.toString();
		}

		public void toString(StringBuilder sb) { this.toString(sb, 0); }

		@Override
		public void toString(StringBuilder sb, int level) {
			int lev;
			addTab(sb, level, false);
			sb.append("NStD: ");
			this.rootSubtreeDep.toString(sb);
			sb.append(" -----> (children:)");
			lev = level + 1;
			backMapChildren.forEach((k, child) -> {
				sb.append('\n');
				child.toString(sb, lev);
			});
		}
	}

//	public static class SubtreeDependency {
//		protected final SubtreeDependency root;
//	}

	// TODO RULE

	/** THE rule. */
	public static abstract class TransferRule {
		protected final NodeSubtreeDependency lhsTemplate;

		public TransferRule(NodeSubtreeDependency lhs) {
			super();
			this.lhsTemplate = lhs;
		}

		/** See {@link NodeComparable#computeDissonanceAsLong(NodeComparable)}. */
		public long scoreDifferences(NodeSubtreeDependency givenSubtree) {
			return givenSubtree.computeDissonanceAsLong(lhsTemplate);
		}

		/**
		 * NOTE: remember to invoke
		 * {@link TransferRule#manageUntouchedChildredUpontransfer(NodeSubtreeDependency, TransferTranslationItEng3, NodeSubtreeDependency)}
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
		 * {@link TransferRule#manageUntouchedChildredUpontransfer(NodeSubtreeDependency, TransferTranslationItEng3, NodeSubtreeDependency)}
		 * passing as first parameter the original node, as third parameter that newly
		 * produced node.</li>
		 * </ul>
		 */
//		* <li>for each newly produced LEAF node {@link TransferRule}</li>
		public abstract NodeSubtreeDependency applyTransferRule(TransferTranslationItEng3 transferer,
				NodeSubtreeDependency originalSubtree);

//		protected final void manageNewlyProducedLeafNodes(NodeSubtreeDependency originalNonleafNode,
//				TransferTranslationItEng3 transferer, NodeSubtreeDependency newlyProducedByLeafNode) {

		/**
		 * See
		 * {@link #applyTransferRule(TransferTranslationItEng3, NodeSubtreeDependency)},
		 * since there You'll find what this method is, what are its parameters and
		 * where and why it MUST bew invoked
		 */
			// protected final void
			// manageUntouchedChildredUpontransfer(NodeSubtreeDependency
			// originalNonleafNode,
//				NodeSubtreeDependency[] childrenOriginalUntouched, TransferTranslationItEng3 transferer,
//				NodeSubtreeDependency newlyProducedByTransferNode) {
		protected final void manageUntouchedChildredUpontransfer(NodeSubtreeDependency originalNode,
				TransferTranslationItEng3 transferer, NodeSubtreeDependency newlyProducedByTransferNode) {
			// todooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
			/*
			 * for each node non "touched" (i.e., not present in newlyBlaBla.children),
			 * transfer it AND wire the newly produced node into the newlyBla's children
			 * set.
			 */
			originalNode.forEachChildNC(child -> {
				if (!newlyProducedByTransferNode.containsChildNC(child.getKeyIdentifier())) {
					NodeSubtreeDependency transferedChild;
					transferedChild = transferer.transfer((NodeSubtreeDependency) child);
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
