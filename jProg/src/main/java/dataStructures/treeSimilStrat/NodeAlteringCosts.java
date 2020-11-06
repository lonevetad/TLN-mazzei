package dataStructures.treeSimilStrat;

import dataStructures.NodeComparable;

public interface NodeAlteringCosts<E> {
	public long insertNodeCost(NodeComparable<E> node);

	public long deleteNodeCost(NodeComparable<E> node);

	public long renameNodeCost(NodeComparable<E> node, E newLabel);

	//

	public static <T> NodeAlteringCosts<T> newDefaultNAC() {
		return new NodeAlteringCosts<>() {
			@Override
			public long insertNodeCost(NodeComparable<T> node) { return 1; }

			@Override
			public long deleteNodeCost(NodeComparable<T> node) { return 1; }

			@Override
			public long renameNodeCost(NodeComparable<T> node, T newLabel) {
				return node.getKeyComparator().compare(node.getKeyIdentifier(), newLabel) == 0 ? 0 : 1;
			}
		};
	}
}
