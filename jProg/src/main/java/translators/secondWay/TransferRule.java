package translators.secondWay;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import common.NodeParsedSentence;
import tools.NodeComparable;
import tools.NodeComparableSynonymIndexed;

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
public abstract class TransferRule {
	protected final NodeParsedSentence lhsTemplate;

	public TransferRule(NodeParsedSentence lhs) {
		super();
		this.lhsTemplate = lhs;
	}

	/** See {@link NodeComparable#computeDissonanceAsLong(NodeComparable)}. */
	public long scoreDifferences(NodeParsedSentence givenSubtree) {
		// return givenSubtree.computeDissonanceAsLong(lhsTemplate);
		return lhsTemplate.computeDissonanceAsLong(givenSubtree);
	}

	/**
	 * Invokes
	 * {@link #implementsTransferRule(TransferTranslationRuleBased, NodeParsedSentence)}
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
//	* <li>for each newly produced LEAF node {@link TransferRule}</li>
	public NodeParsedSentence applyTransferRule(ATransferTranslationRuleBased transferer,
			NodeParsedSentence originalSubtree) {
		SubTransferResult str;
		str = implementsTransferRule(transferer, originalSubtree);
		str.oldNodesAndTransferred
				.forEach(p -> manageUntouchedChildredUpontransfer(p.oldNode, transferer, p.newTransferredNode));
		return str.rootTransferedSubtree;
	}

	/**
	 * Applying a rule means performing the following steps:
	 * <ol>
	 * <li>take a sentence sub-tree to be transfer (by passing the sub-root node as
	 * argument)</li>
	 * <li>produce the new subtree by allocating new nodes (this depends on this
	 * method's implementation).</li>
	 * <li>wire them up (again, this depends on this method's implementation).</li>
	 * <li>create an instance of {@link SubTransferResult}, as required, and put all
	 * pairs of old and new node created (in case of purely synthetic nodes, i.e.
	 * nodes created from nothing like: Italian language has "implied subject" in
	 * some verb and in English is required to be specified).</li>
	 * </ol>
	 */
	public abstract SubTransferResult implementsTransferRule(ATransferTranslationRuleBased transferer,
			NodeParsedSentence originalSubtree);

//	protected final void manageNewlyProducedLeafNodes(NodeSubtreeDependency originalNonleafNode,
//			TransferTranslationItEng3 transferer, NodeSubtreeDependency newlyProducedByLeafNode) {

	/**
	 * See
	 * {@link #applyTransferRule(TransferTranslationRuleBased, NodeSubtreeDependency)},
	 * since there You'll find what this method is, what are its parameters and
	 * where and why it MUST bew invoked
	 */
	// protected final void
	// manageUntouchedChildredUpontransfer(NodeSubtreeDependency
	// originalNonleafNode,
//			NodeSubtreeDependency[] childrenOriginalUntouched, TransferTranslationItEng3 transferer,
//			NodeSubtreeDependency newlyProducedByTransferNode) {
	protected final void manageUntouchedChildredUpontransfer(NodeParsedSentence originalNode,
			ATransferTranslationRuleBased transferer, NodeParsedSentence newlyProducedByTransferNode) {
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
		System.out.println("#starting recursion on originalNode: " + originalNode);
		originalNode.forEachChildNC(child -> {
			if (!newlyProducedByTransferNode.containsChildNC(child)) {
				NodeComparableSynonymIndexed transferedChild;
				System.out.println("## recursion on child: " + child);
				transferedChild = transferer.transfer((NodeParsedSentence) child);
				newlyProducedByTransferNode.addChildNC(transferedChild);
			}
		});
	}

//	public static NodeParsedSentence cloneNode(NodeParsedSentence original) {
//		return cloneNode(original, original::clone);
//	}
//
//	/***/
//	public static NodeParsedSentence cloneNode(NodeParsedSentence original, Supplier<NodeParsedSentence> newNodeInstantiator) {
//		NodeParsedSentence newNode;List<String>oldAlternatives
//		newNode=newNodeInstantiator.get();
//		newNode.getKeyIdentifier().toList()
//		return newNode;
//	}

	@Override
	public String toString() { return "TransferRule [lhs=" + lhsTemplate + "]"; }

	public void toString(StringBuilder sb) {
		sb.append("++++TransferRule:\n");
		this.lhsTemplate.toString(sb);
	}

	//

	public static class SubTransferResult implements Serializable {
		private static final long serialVersionUID = -902204796933L;
		public final NodeParsedSentence rootTransferedSubtree;
		public final List<PairOldNewNode> oldNodesAndTransferred;

		public SubTransferResult(NodeParsedSentence rootTransferedSubtree) {
			super();
			this.rootTransferedSubtree = rootTransferedSubtree;
			this.oldNodesAndTransferred = new LinkedList<>();
		}

		public SubTransferResult addPairOldNewNode(NodeParsedSentence oldNode, NodeParsedSentence newTransferredNode) {
			oldNodesAndTransferred.add(new PairOldNewNode(oldNode, newTransferredNode));
			return this;
		}

//
		public static class PairOldNewNode implements Serializable {
			private static final long serialVersionUID = 3210511047988L;
			public final NodeParsedSentence oldNode, newTransferredNode;

			public PairOldNewNode(NodeParsedSentence oldNode, NodeParsedSentence newTransferredNode) {
				super();
				this.oldNode = oldNode;
				this.newTransferredNode = newTransferredNode;
			}
		}
	}
} // end class TransferRule