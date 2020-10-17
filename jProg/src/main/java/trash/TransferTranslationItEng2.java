package trash;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import common.SentenceParsed;

/**
 * Traduttore formato da regole generiche, a prescindere che si usi SimpleNLG
 */
@Deprecated
public class TransferTranslationItEng2 {
	/*
	 * 1 public static class ElementTranslationRule { public final String posTag;
	 * public final TreeParsedSentence.NodeDependencyTree node; }
	 * 
	 * public static abstract class TransferRule { protected final
	 * ElementTranslationRule[] lhs;
	 * 
	 * protected TransferRule(ElementTranslationRule[] lhs) { super(); this.lhs =
	 * lhs; }
	 * 
	 * public int getLHSElementsAmount() { return lhs.length; }
	 * 
	 * public ElementTranslationRule getLHSElementAt(int index) { return lhs[index];
	 * }
	 * 
	 * public abstract ElementTranslationRule[] transfer(); }
	 */

//	public static abstract class TransferRule { 2
//		/**
//		 * Tests if the given node (and its dependents, if necessary) is acceptable by
//		 * this rule
//		 */
//		protected abstract boolean isAcceptable(TreeParsedSentence.NodeDependencyTree node);
//
//		/**
//		 * NOTE: DO NOT INVOKE ME, USE
//		 * {@link #testAndApplyRule(common.TreeParsedSentence.NodeDependencyTree)}
//		 * INSTEAD.
//		 * 
//		 * Produce a new node/subtree, depending on this rule and the provided node.
//		 * It's assumed that
//		 * {@link #isAcceptable(common.TreeParsedSentence.NodeDependencyTree)} returns
//		 * <code>true</code>.
//		 */
//		protected abstract TreeParsedSentence.NodeDependencyTree applyTransferRule(
//				TreeParsedSentence.NodeDependencyTree node);
//
//		/** The real rule, the ones to be invoked */
//		public final TreeParsedSentence.NodeDependencyTree testAndApplyRule(
//				TreeParsedSentence.NodeDependencyTree node) {
//			if (isAcceptable(node))
//				return applyTransferRule(node);
//			else
//				return null;
//		}
//	}

	//

	public TransferTranslationItEng2() {

	}

	//

	//

	// TODO utilities

	//

	//

//	public static String getUsefulPos(TreeParsedSentence.NodeDependencyTree node) {
//		final String dep, pos;
//		dep = node.dep;
//		pos = node.pos;
//
//		if (dep.contains("adj"))
//			return "JJ";
//		if (dep.contains("adv")) // like "advmod"
//			return "adv";
//
//		if (dep.contains("adj"))
//
//			//
//			if (pos.equals("FS") || dep.contains("punct"))
//				return "PUNCT";
//		throw new IllegalArgumentException("Unkown dep (" + dep + ") or pos (" + pos + ").");
//	}

	/**
	 * Some parts could be identified in some different ways, for instances Part Of
	 * Speech Tags or Dependencies Tags.<br>
	 * For example: "just some adjective" could be one of the following "{JJ, JJR,
	 * JJS}" or "{adj, nadj, amod}". In this way, it can be easily verified
	 */
	public static class ElementGrammarWithAlternatives_OLD {
		protected final String[] alternatives;

		protected ElementGrammarWithAlternatives_OLD(String[] alternatives) {
			super();
			this.alternatives = alternatives;
			Arrays.sort(alternatives);
		}

		public boolean contains(String lal) { return Arrays.binarySearch(alternatives, lal) >= 0; }

		public void forEach(Consumer<String> action) {
			for (String s : alternatives) {
				action.accept(s);
			}
		}

		public List<String> toList() { return Collections.unmodifiableList(Arrays.asList(alternatives)); }

		public Iterator<String> iterator() { return Arrays.asList(alternatives).iterator(); }
	}

	/**
	 * A rule is composed of a LHS and a RHS.<br>
	 * The RHS is produced by the underlying implementation.<br>
	 * The LHS is a subtree: a root with its direct optional child / children.
	 */
	public static abstract class TransferRule { // 3
		protected final ElementGrammarWithAlternatives_OLD nodeLhs;
		protected final Set<ElementGrammarWithAlternatives_OLD> childrenLhs;

		public TransferRule(ElementGrammarWithAlternatives_OLD nodeLhs) { this(nodeLhs, null); }

		public TransferRule(ElementGrammarWithAlternatives_OLD nodeLhs, Set<ElementGrammarWithAlternatives_OLD> childrenLhs) {
			super();
			this.nodeLhs = nodeLhs;
			this.childrenLhs = childrenLhs;
		}

		/**
		 * Tests if the given node (and its dependents, if necessary) is acceptable by
		 * this rule.
		 * <p>
		 * The default implementation check if the given node (pos or dep). If not,
		 * <code>null</code> is returned.<br>
		 * If not, then the following is returned:
		 * <ul>
		 * <li><code>&#61; 0</code> if the given node's children nodes set matches
		 * perfectly the LHS children nodes</li>
		 * <li><code>&gt; 0</code> if the given node's children nodes set matches all
		 * the LHS children nodes, but some of the given node's children is not matched.
		 * The amount of non-matching children is returned.</li>
		 * <li><code>&lt; 0</code> indicates an opposite situation of the previous one:
		 * t</li>
		 * </ul>
		 */
//		is part of the LHS
		protected Integer isAcceptable(SentenceParsed.NodeParsedSentFromTint node) {
			// check if the node itself can be part of the lhs. If not -> false
			/* After this optimization, checks must be */
			return null;
		}

		/**
		 * NOTE: DO NOT INVOKE ME, USE
		 * {@link #testAndApplyRule(common.SentenceParsed.NodeParsedSentFromTint)}
		 * INSTEAD.
		 * 
		 * Produce a new node/subtree, depending on this rule and the provided node.
		 * It's assumed that
		 * {@link #isAcceptable(common.SentenceParsed.NodeParsedSentFromTint)} returns
		 * <code>true</code>.
		 */
		protected abstract SentenceParsed.NodeParsedSentFromTint applyTransferRule(
				SentenceParsed.NodeParsedSentFromTint node);

		/** The real rule, the ones to be invoked */
		public final SentenceParsed.NodeParsedSentFromTint testAndApplyRule(
				SentenceParsed.NodeParsedSentFromTint node) {
			if (isAcceptable(node))
				return applyTransferRule(node);
			else
				return null;
		}
	}
}