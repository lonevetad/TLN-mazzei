package tools;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import dataStructures.MapTreeAVL;

/**
 * Node of a tree-like structure (no check if it's a graph or a single rooted
 * tree) that can be compared (through
 * {@link #computeDissonanceAsLong(NodeComparable)}) to produce a "score": the
 * lower, the better.
 */
public interface NodeComparable<K> extends Serializable {

	// STATIC STUFFS

	public static final DissonanceWeights WEIGHTS_DEFAULT = new DissonanceWeights() {
		private static final long serialVersionUID = -5410794L;

		@Override
		public DissonanceWeights setWeightMissingNode(int weightMissingNode) { return this; }

		@Override
		public DissonanceWeights setWeightExceedingNode(int weightExceedingNode) { return this; }

		@Override
		public DissonanceWeights setWeightDepth(int weightDepth) { return this; }
	};

	//

	public static <K> NodeComparable<K> newDefaultNodeComparable(K value, Comparator<K> comparatorKey) {
		return new DefaultNodeComparable<>(value, comparatorKey);
	}

	//

	// TODO INSTANCE METHODS

	//

	public K getKeyIdentifier();

	/** Returns the set of all children held by this node. */
	public Set<NodeComparable<K>> getChildrenNC();

	public NodeComparable<K> getChildNCByKey(K key);

	public default boolean isLeafNC() {
		Set<NodeComparable<K>> children;
		children = getChildrenNC();
		return children == null || children.isEmpty();
	}

	public default boolean containsChildNC(K key) { return this.getChildNCByKey(key) != null; }

	public default void forEachChildNC(Consumer<NodeComparable<K>> action) {
		Set<NodeComparable<K>> children;
		children = getChildrenNC();
		if (children == null)
			return;
		children.forEach(action);
	}

	public default NodeComparable<K> addChildNC(NodeComparable<K> child) {
		Set<NodeComparable<K>> children;
		if (child == null)
			return this;
		children = getChildrenNC();
		if (children == null) { return this; }
		children.add(child);
		return this;
	}

	//

	// PUBLIC STUFFS

	//

//	

	/**
	 * Compute how much <code>this</code> node differs to the given one, which is
	 * considered as the "base". Default weights (instance of
	 * {@link DissonanceWeights}) is provided
	 * <p>
	 * {@link DissonanceWeights#getWeightMissingNode()} weights the nodes that are
	 * missing in <code>this</code> "children set" (obtained by
	 * <code>this.{@link #getChildrenNC()}</code>), while
	 * {@link DissonanceWeights#getWeightExceedingNode()} weights the nodes that are
	 * present in <code>this</code> "children set" but not contained in the "base".
	 * {@link DissonanceWeights#getWeightDepth()} instead weights exponentially the
	 * recursion depth: deeper dependency could be considered having a higher weight
	 * ("less than one" exponent base are not implemented yet).
	 */
	public default long computeDissonanceAsLong(NodeComparable<K> nodeBase) {
		return computeDissonanceAsLong(nodeBase, WEIGHTS_DEFAULT);
	}

	public default long computeDissonanceAsLong(NodeComparable<K> nodeBase, boolean checkRecursion) {
		return computeDissonanceAsLong(nodeBase, WEIGHTS_DEFAULT, checkRecursion);
	}

	/** See {@link #computeDissonanceAsLong(NodeComparable)}. */
	public default long computeDissonanceAsLong(NodeComparable<K> nodeBase, DissonanceWeights weights) {
		return computeDissonanceAsLong(nodeBase, weights, false); // make it fast
	}

	/** See {@link #computeDissonanceAsLong(NodeComparable)}. */
	public long computeDissonanceAsLong(NodeComparable<K> nodeBase, DissonanceWeights weights, boolean checkRecursion);

	//

	/**
	 * See {@link #computeDissonanceAsLong(NodeComparable)}, it's the same but
	 * returning and computing a {@link BigInteger} instead.
	 */
	public default BigInteger computeDissonanceAsBigInt(NodeComparable<K> nodeBase) {
		return computeDissonanceAsBigInt(nodeBase, WEIGHTS_DEFAULT);
	}

	/** See {@link #computeDissonanceAsLong(NodeComparable)}. */
	public default BigInteger computeDissonanceAsBigInt(NodeComparable<K> nodeBase, boolean checkRecursion) {
		return computeDissonanceAsBigInt(nodeBase, WEIGHTS_DEFAULT, checkRecursion);
	}

	public default BigInteger computeDissonanceAsBigInt(NodeComparable<K> nodeBase, DissonanceWeights weights) {
		return computeDissonanceAsBigInt(nodeBase, weights, false); // make it fast
	}

	/** See {@link #computeDissonanceAsLong(NodeComparable)}. */
	public BigInteger computeDissonanceAsBigInt(NodeComparable<K> nodeBase, DissonanceWeights weights,
			boolean checkRecursion);

	//

	// TODO CLASSES

	//

	/** See {@link NodeComparable#computeDissonanceAsLong(NodeComparable)}. */
	public static class DissonanceWeights implements Serializable {
		private static final long serialVersionUID = 23263214070008L;
		protected int weightMissingNode, weightExceedingNode, weightDepth;
		protected BigInteger weightMissingNodeBigInt, weightExceedingNodeBigInt, weightDepthBigInt;

		public DissonanceWeights() {
			this.weightMissingNode = this.weightExceedingNode = this.weightDepth = 1;
			this.weightMissingNodeBigInt = this.weightExceedingNodeBigInt = this.weightDepthBigInt = BigInteger.ONE;
		}

		public int getWeightMissingNode() { return weightMissingNode; }

		public int getWeightExceedingNode() { return weightExceedingNode; }

		public int getWeightDepth() { return weightDepth; }

		public DissonanceWeights setWeightMissingNode(int weightMissingNode) {
			if (weightMissingNode >= 0) {
				this.weightMissingNode = weightMissingNode;
				this.weightMissingNodeBigInt = BigInteger.valueOf(weightMissingNode);
			}
			return this;
		}

		public DissonanceWeights setWeightExceedingNode(int weightExceedingNode) {
			if (weightExceedingNode >= 0) {
				this.weightExceedingNode = weightExceedingNode;
				this.weightExceedingNodeBigInt = BigInteger.valueOf(weightExceedingNode);
			}
			return this;
		}

		public DissonanceWeights setWeightDepth(int weightDepth) {
			if (weightDepth >= 1) {
				this.weightDepth = weightDepth;
				this.weightDepthBigInt = BigInteger.valueOf(weightDepth);
			}
			return this;
		}
	}

	//

	public static abstract class NodeComparableDefaultAlghoritms<T> implements NodeComparable<T> {
		private static final long serialVersionUID = -651032512L;

		@Override
		public long computeDissonanceAsLong(NodeComparable<T> nodeBase, DissonanceWeights weights,
				boolean checkRecursion) {
			return checkRecursion
					? computeDissonanceAsLong_WithRecursion(nodeBase, weights, new HashMap<>(), weights.weightDepth)
					: computeDissonanceAsLong_NoRecursion(nodeBase, weights, weights.weightDepth);
		}

		protected long computeDissonanceAsLong_NoRecursion(NodeComparable<T> nodeBase, DissonanceWeights weights,
				long exponentialWeightDepth) {
			long[] dissonance = { Objects.equals(nodeBase.getKeyIdentifier(), this.getKeyIdentifier()) ? 0 : 1 };
			this.forEachChildNC(child -> {
				T key;
				key = child.getKeyIdentifier();
				if (nodeBase.containsChildNC(key)) {
					// recursion :D
					dissonance[0] += ((DefaultNodeComparable<T>) child).computeDissonanceAsLong_NoRecursion(
							nodeBase.getChildNCByKey(key), //
							weights, //
							exponentialWeightDepth * weights.weightDepth);
				} else {
					dissonance[0] += weights.weightMissingNode * exponentialWeightDepth;
				}
			});
			nodeBase.forEachChildNC(child -> {
				T key;
				key = child.getKeyIdentifier();
				if (!this.containsChildNC(key)) {
					dissonance[0] += weights.weightExceedingNode * exponentialWeightDepth;
				}
			});
			return dissonance[0];
		}

		protected long computeDissonanceAsLong_WithRecursion(NodeComparable<T> nodeBase, DissonanceWeights weights,
				Map<T, NodeComparable<T>> nodesVisited, long exponentialWeightDepth) {
//			if(nodesVisited.containsKey(this.value) || nodesVisited.containsKey(nodeBase).)
			T thisKey;
			thisKey = this.getKeyIdentifier();
			long[] dissonance = { Objects.equals(nodeBase.getKeyIdentifier(), thisKey) ? 0 : 1 };
			nodesVisited.put(thisKey, this);
			this.forEachChildNC(child -> {
				T key;
				key = child.getKeyIdentifier();
				if (nodeBase.containsChildNC(key)) {
					// recursion :D
					if (!nodesVisited.containsKey(key)) {
						dissonance[0] += ((DefaultNodeComparable<T>) child).computeDissonanceAsLong_WithRecursion(
								nodeBase.getChildNCByKey(key), //
								weights, //
								nodesVisited, //
								exponentialWeightDepth * weights.weightDepth);
					}
				} else {
					dissonance[0] += weights.weightMissingNode * exponentialWeightDepth;
				}
			});
			nodeBase.forEachChildNC(child -> {
				T key;
				key = child.getKeyIdentifier();
				if (!this.containsChildNC(key)) {
					dissonance[0] += weights.weightExceedingNode * exponentialWeightDepth;
				}
			});
			return dissonance[0];
		}

		//

		@Override
		public BigInteger computeDissonanceAsBigInt(NodeComparable<T> nodeBase, DissonanceWeights weights,
				boolean checkRecursion) {
			return checkRecursion
					? computeDissonanceAsBigInt_WithRecursion(nodeBase, weights, new HashMap<>(),
							BigInteger.valueOf(weights.weightDepth))
					: computeDissonanceAsBigInt_NoRecursion(nodeBase, weights, BigInteger.valueOf(weights.weightDepth));
		}

		protected BigInteger computeDissonanceAsBigInt_NoRecursion(NodeComparable<T> nodeBase,
				DissonanceWeights weights, BigInteger exponentialWeightDepth) {
			BigInteger[] dissonance = {
					Objects.equals(nodeBase.getKeyIdentifier(), this.getKeyIdentifier()) ? BigInteger.ZERO
							: BigInteger.ONE };
			this.forEachChildNC(child -> {
				T key;
				key = child.getKeyIdentifier();
				if (nodeBase.containsChildNC(key)) {
					// recursion :D
					dissonance[0] = dissonance[0].add(((DefaultNodeComparable<T>) child)
							.computeDissonanceAsBigInt_NoRecursion(nodeBase.getChildNCByKey(key), //
									weights, //
									exponentialWeightDepth.multiply(weights.weightDepthBigInt))//
					);
				} else {
					dissonance[0] = dissonance[0].add(exponentialWeightDepth.multiply(weights.weightMissingNodeBigInt));
				}
			});
			nodeBase.forEachChildNC(child -> {
				T key;
				key = child.getKeyIdentifier();
				if (!this.containsChildNC(key)) {
					dissonance[0] = dissonance[0]
							.add(exponentialWeightDepth.multiply(weights.weightExceedingNodeBigInt));
				}
			});
			return dissonance[0];
		}

		protected BigInteger computeDissonanceAsBigInt_WithRecursion(NodeComparable<T> nodeBase,
				DissonanceWeights weights, Map<T, NodeComparable<T>> nodesVisited, BigInteger exponentialWeightDepth) {
			T thisKey;
			thisKey = this.getKeyIdentifier();
			BigInteger[] dissonance = {
					Objects.equals(nodeBase.getKeyIdentifier(), thisKey) ? BigInteger.ZERO : BigInteger.ONE };
			this.forEachChildNC(child -> {
				T key;
				key = child.getKeyIdentifier();
				if (nodeBase.containsChildNC(key)) {
					// recursion :D
					if (!nodesVisited.containsKey(key)) {
						BigInteger dissGot;
						dissGot = ((DefaultNodeComparable<T>) child).computeDissonanceAsBigInt_WithRecursion(
								nodeBase.getChildNCByKey(key), //
								weights, //
								nodesVisited, //
								exponentialWeightDepth.multiply(weights.weightDepthBigInt));
						if (dissGot != BigInteger.ZERO)
							dissonance[0] = dissonance[0].add(dissGot);
					}
				} else {
					dissonance[0] = dissonance[0].add(exponentialWeightDepth.multiply(weights.weightMissingNodeBigInt));
				}
			});
			nodeBase.forEachChildNC(child -> {
				T key;
				key = child.getKeyIdentifier();
				if (!this.containsChildNC(key)) {
					BigInteger dissGot;
					dissGot = exponentialWeightDepth.multiply(weights.weightExceedingNodeBigInt);
					if (dissGot != BigInteger.ZERO)
						dissonance[0] = dissonance[0].add(dissGot);
				}
			});
			return dissonance[0];
		}
	}

	public static class DefaultNodeComparable<T> extends NodeComparableDefaultAlghoritms<T> {
		private static final long serialVersionUID = 1L;
		protected T value;
		protected MapTreeAVL<T, NodeComparable<T>> backMap;
		protected Set<NodeComparable<T>> children;

		public DefaultNodeComparable(T value, Comparator<T> comparatorKey) {
			super();
			this.value = value;
			this.backMap = MapTreeAVL.newMap(MapTreeAVL.Optimizations.MinMaxIndexIteration, comparatorKey);
			this.children = this.backMap.toSetValue(n -> n.getKeyIdentifier());
		}

		@Override
		public T getKeyIdentifier() { return this.value; }

		@Override
		public Set<NodeComparable<T>> getChildrenNC() { return this.children; }

		@Override
		public NodeComparable<T> getChildNCByKey(T key) { return this.backMap.get(key); }
	}
}