package dataStructures.treeSimilStrat;

import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import dataStructures.NodeComparable;
import dataStructures.SortedSetEnhanced;
import dataStructures.TreeComparable;

/**
 * General contract to compute a dissonance over two trees (to be precise: two
 * roots, they are NOT {@link TreeComparable} even if they are accepted).
 */
public interface DissonanceTreeAlgorithm<T> {

	public long computeDissonance(NodeAlteringCosts<T> nodeAlteringCost, NodeComparable<T> t1, NodeComparable<T> t2);

	public default long computeDissonance(NodeAlteringCosts<T> nodeAlteringCost, TreeComparable<T> t1,
			TreeComparable<T> t2) {
		return computeDissonance(nodeAlteringCost, t1.getRoot(), t2.getRoot());
	}

	/** Get the whole cost of a given action (depending on the second parameter) */
	public default long getActionCostWholeSubtree(NodeAlteringCosts<T> nodeAlteringCost, boolean isInsert,
			SortedSetEnhanced<NodeComparable<T>> nodes) {
		final long[] ll = { 0 };
		nodes.forEach(n -> ll[0] += getActionCostWholeSubtree(nodeAlteringCost, isInsert, n));
		return ll[0];
	}

	/** Get the whole cost of a given action (depending on the second parameter) */
	public default long getActionCostWholeSubtree(NodeAlteringCosts<T> nodeAlteringCost, boolean isInsert,
			NodeComparable<T> n) {
		ActionCostWholeSubtree<T> acws;
		acws = new ActionCostWholeSubtree<>(isInsert);
		acws.accept(nodeAlteringCost, n);
		return acws.getTotalCost();
	}

	static class ActionCostWholeSubtree<E> implements BiConsumer<NodeAlteringCosts<E>, NodeComparable<E>> {
		protected boolean isInsertAction;
		protected long totalCost;
//		protected NodeAlteringCosts<E> nodeAlteringCost;
//		protected NodeComparable<E> node;

//		public ActionCostWholeSubtree(NodeAlteringCosts<E> nodeAlteringCost, boolean isInsert, NodeComparable<E> n) {
		public ActionCostWholeSubtree(boolean isInsert) {
			super();
			this.isInsertAction = isInsert;
//			this.nodeAlteringCost = nodeAlteringCost;
//			this.node = n;
		}

		public long getTotalCost() { return totalCost; }

		public boolean isInsertAction() { return isInsertAction; }

//		public NodeAlteringCosts<E> getNodeAlteringCost() { return nodeAlteringCost; }
//		public NodeComparable<E> getNode() { return node; }

		public void setInsertAction(boolean isInsert) { this.isInsertAction = isInsert; }

//		public void setNodeAlteringCost(NodeAlteringCosts<E> nodeAlteringCost) { this.nodeAlteringCost = nodeAlteringCost; }
//		public void setNode(NodeComparable<E> n) { this.node = n; }

		@Override
		public void accept(NodeAlteringCosts<E> nac, NodeComparable<E> n) {
			long tc;
			final CostExtractor<E> costExt;
			final Consumer<NodeComparable<E>> nodeAdder;
			final LinkedList<NodeComparable<E>> nodesToBeComputed;
			SortedSetEnhanced<NodeComparable<E>> children;
			nodesToBeComputed = new LinkedList<>();
			costExt = isInsertAction ? nac::insertNodeCost : nac::deleteNodeCost;
			nodeAdder = nodesToBeComputed::add;
			nodesToBeComputed.add(n);
			tc = 0;
			// simple bfs
			while (!nodesToBeComputed.isEmpty()) {
				n = nodesToBeComputed.removeFirst();
				tc += costExt.getCost(n);
				children = n.getChildrenNC();
				if (children != null && (!children.isEmpty())) { children.forEach(nodeAdder); }
			}
			this.totalCost = tc;
		}

		protected static interface CostExtractor<K> {
			public long getCost(NodeComparable<K> n);
		}
	}
}