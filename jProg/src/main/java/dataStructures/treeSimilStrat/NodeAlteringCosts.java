package dataStructures.treeSimilStrat;

import dataStructures.CollectionAlteringCosts;
import dataStructures.NodeComparable;

public interface NodeAlteringCosts<E> extends CollectionAlteringCosts<NodeComparable<E>> {
	public default long insertNodeCost(NodeComparable<E> node) { return insertCost(node); }

	public default long deleteNodeCost(NodeComparable<E> node) { return deleteCost(node); }

	public long renameNodeCost(NodeComparable<E> node, E newLabel);

	@Override
	public default long renameCost(NodeComparable<E> node, NodeComparable<E> newNode) {
		return renameNodeCost(node, newNode.getKeyIdentifier());
	}

	//

	public static <T> NodeAlteringCosts<T> newDefaultNAC() {
		return new NodeAlteringCosts<>() {
			@Override
			public long insertCost(NodeComparable<T> node) { return 1; }

			@Override
			public long deleteCost(NodeComparable<T> node) { return 1; }

			@Override
			public long renameNodeCost(NodeComparable<T> node, T newLabel) {
				return node.getKeyComparator().compare(node.getKeyIdentifier(), newLabel) == 0 ? 0 : 1;
			}
		};
	}

	/**
	 * Auxiliary class, to transform methods like
	 * {@link NodeAlteringCosts#insertNodeCost(NodeComparable)} and
	 * {@link NodeAlteringCosts#deleteNodeCost(NodeComparable)} into functional
	 * interfaces.
	 */
	public static interface ActionOnNodeCost<K> extends ActionCost<NodeComparable<K>> {
		public default long getNodeCost(NodeComparable<K> node) { return getCost(node); }
	}
}