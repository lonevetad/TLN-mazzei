package dataStructures;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import dataStructures.MapTreeAVL.ForEachMode;
import dataStructures.minorUtils.DissonanceWeights;
import dataStructures.treeSimilStrat.DissonanceTreeAlgo_Mine1;
import dataStructures.treeSimilStrat.DissonanceTreeAlgo_Mine2;
import dataStructures.treeSimilStrat.DissonanceTreeAlgo_Zhang_Shasha;
import dataStructures.treeSimilStrat.DissonanceTreeAlgorithm;
import dataStructures.treeSimilStrat.NodeAlteringCosts;
import tools.DifferenceCalculator;
import tools.Stringable;

/**
 * Node of a tree-like structure (no check if it's a graph or a single rooted
 * tree) that can be compared (through
 * {@link #computeDissonanceAsLong(NodeComparable)}) to produce a "score": the
 * lower, the better.
 */
public interface NodeComparable<K> extends Stringable {

	//

	// TODO INSTANCE METHODS

	//

	public K getKeyIdentifier();

	public Comparator<K> getKeyComparator();

	public default Comparator<NodeComparable<K>> getNodeComparator() {
		return NodeComparable.newNodeComparatorDefault(getKeyComparator());
	}

	public default int getHeightNode() { return HEIGHT_OF_NEW_NODE; }

	public NodeComparable<K> setHeightNode(int height);

	/** Beware of nulls. */
	public NodeComparable<K> getFather();

	/** @return <code>this</code> instance. */
	public NodeComparable<K> setFather(NodeComparable<K> father);

	/** Returns the set of all children held by this node. */
	public SortedSetEnhanced<NodeComparable<K>> getChildrenNC();

	public default NodeComparable<K> addChildNC(NodeComparable<K> child) {
		int nh, childHeight;
		Set<NodeComparable<K>> children;
		if (child == null)
			return this;
		childHeight = child.getHeightNode();
		if (childHeight != HEIGHT_OF_NEW_NODE && childHeight < getHeightNode()) { return this; } // no cycles allowed
		children = getChildrenNC();
		if (children == null) { return this; }
		children.add(child);
		child.setFather(this);
		nh = getHeightNode();
		if (nh != Integer.MAX_VALUE) { nh++; }
		child.setHeightNode(nh);
		return this;
	}

	public default boolean deleteChildNC(NodeComparable<K> child) {
		boolean removed;
		Set<NodeComparable<K>> children;
		if (child == null || (children = getChildrenNC()) == null)
			return false;
		removed = children.remove(child);
		if (removed)
			child.setFather(null);
		return removed;
	}

	public default boolean isLeafNC() {
		Set<NodeComparable<K>> children;
		children = getChildrenNC();
		return children == null || children.isEmpty();
	}

	public default int getTreeSize() {
		int[] s = { 1 };
		Set<NodeComparable<K>> children;
		children = getChildrenNC();
		if (children != null) { children.forEach(n -> s[0] += n.getTreeSize()); }
		return s[0];
	}

	/**
	 * Constructor-designed method, produces a "backing map" for
	 * {@link #getChildrenNC()}.<br>
	 * Invokes {@link #newChildrenBackmap(Comparator)} passing it
	 * {@link #getNodeComparator()} as parameter;
	 */
	public default MapTreeAVL<NodeComparable<K>, NodeComparable<K>> newChildrenBackmap() {
		return newChildrenBackmap(getNodeComparator());
	}

	public default MapTreeAVL<NodeComparable<K>, NodeComparable<K>> newChildrenBackmap(
			Comparator<NodeComparable<K>> comparatorNode) {
		MapTreeAVL<NodeComparable<K>, NodeComparable<K>> m;
		m = MapTreeAVL.newMap(MapTreeAVL.Optimizations.FullButHeavyNodes,
				MapTreeAVL.BehaviourOnKeyCollision.KeepPrevious, comparatorNode);
		return m;
	}

	/**
	 * Constructor-designed method, should be chained with a value (a field?)
	 * obtained through ways like {@link #newChildrenBackmap(Comparator)}.
	 */
	public default SortedSetEnhanced<NodeComparable<K>> newChildrenSet(
			MapTreeAVL<NodeComparable<K>, NodeComparable<K>> backmap) {
		return backmap.toSetKey();
	}

	/**
	 * Constructor-designed method, chaining {@link #newChildrenBackmap()} and
	 * {@link #newChildrenSet(MapTreeAVL)}
	 */
	public default SortedSetEnhanced<NodeComparable<K>> newChildrenSet() {
		return newChildrenSet(newChildrenBackmap());
	}

	/**
	 * in future releases, the score (compute
	 * througj{@link #computeDissonanceAsLong(NodeComparable)}
	 */
	public default long scoreKeyCompatibilityWith(K anotherKey) { return 1; }

	/**
	 * See {@link #getChildNCMostSimilarTo(NodeComparable)}, giving a node produced
	 * with the given key and a way to instantiate a wrapper node.
	 */
	public default NodeComparable<K> getChildNCMostSimilarTo(K key, Function<K, NodeComparable<K>> nodeGenerator) {
		return getChildNCMostSimilarTo(nodeGenerator.apply(key));
	}

	/**
	 * Beware: could be imprecise: it returns the best matching child for the given
	 * similar node (this node could be produced by
	 * {@link #getChildNCMostSimilarTo(Object, Function)}).
	 */
	public NodeComparable<K> getChildNCMostSimilarTo(NodeComparable<K> copy);

	/**
	 * See {@link #getChildNCMostSimilarTo(Object, Function)} for a explanation
	 * (because it's similar) and {@link #containsChildNC(NodeComparable)}, giving
	 * to the last method a node produced with the given key and a way to
	 * instantiate a wrapper node.
	 */
	public default boolean containsChildNC(K key, Function<K, NodeComparable<K>> nodeGenerator) {
		return this.containsChildNC(nodeGenerator.apply(key));
	}

	public default boolean containsChildNC(NodeComparable<K> copy) {
		return this.getChildNCMostSimilarTo(copy) != null;
	}

	/** Scans all children depending on their sorting. */
	public default void forEachChildNC(Consumer<NodeComparable<K>> action) {
		Set<NodeComparable<K>> children;
		children = getChildrenNC();
		if (children == null)
			return;
		children.forEach(action);
	}

	public void forEachChildNCFIFOOrdering(Consumer<NodeComparable<K>> action);

	public default <I> void forEachPathOfSomething(Function<NodeComparable<K>, I> nodeValueGetter,
			Consumer<List<I>> pathConsumer) {
		SortedSetEnhanced<NodeComparable<K>> children;
		final LinkedList<I> path;
		Consumer<NodeComparable<K>> recursiveFunc;
		Objects.requireNonNull(nodeValueGetter, "A way to obtain a value from a node is mandatory");
		children = getChildrenNC();
		if (children == null)
			return;
		path = new LinkedList<>();
		recursiveFunc = new Consumer<NodeComparable<K>>() {
			final Consumer<SortedSetEnhanced<NodeComparable<K>>> childrenCons = cc -> cc.forEach(this);

			@Override
			public void accept(NodeComparable<K> n) {
				SortedSetEnhanced<NodeComparable<K>> children;
				path.addLast(nodeValueGetter.apply(n));
				if (n.isLeafNC() || (children = n.getChildrenNC()) == null) {
					// we are at the end -> i'
					pathConsumer.accept(path);
				} else {
					childrenCons.accept(children);
				}
				path.removeLast();
			}
		};
		recursiveFunc.accept(this); // I'm the root
	}

	public default void forEachPathKey(Consumer<List<K>> pathConsumer) {
		forEachPathOfSomething(n -> n.getKeyIdentifier(), pathConsumer);
	}

	public default void forEachPathNode(Consumer<List<NodeComparable<K>>> pathConsumer) {
		forEachPathOfSomething(n -> n, pathConsumer);
	}

	public default Iterator<List<NodeComparable<K>>> iteratorPathNodes() {
		return new IteratorPathNodeComp<NodeComparable<K>, K>(this, n -> n);
	}

	public default Iterator<List<K>> iteratorPathKeys() {
		return new IteratorPathNodeComp<K, K>(this, n -> n.getKeyIdentifier());
	}

	//

	// abstract STUFFS

	//

//	

	/**
	 * Compute how much <code>this</code> node differs to the given one, which is
	 * considered as the "base". Default weights (instance of
	 * {@link DissonanceWeights}) is provided.
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
		return computeDissonanceAsLong(nodeBase, DissonanceWeights.WEIGHTS_DEFAULT);
	}

//	public default long computeDissonanceAsLong(NodeComparable<K> nodeBase, boolean checkRecursion) {
//		return computeDissonanceAsLong(nodeBase, WEIGHTS_DEFAULT, checkRecursion);
//	}

//	/** See {@link #computeDissonanceAsLong(NodeComparable)}. */
//	public default long computeDissonanceAsLong(NodeComparable<K> nodeBase, DissonanceWeights weights) {
//		return computeDissonanceAsLong(nodeBase, weights, false); // make it fast
//	}

	/** See {@link #computeDissonanceAsLong(NodeComparable)}. */
	public long computeDissonanceAsLong(NodeComparable<K> nodeBase, DissonanceWeights weights);
	// , boolean checkRecursion);

	//

	/**
	 * See {@link #computeDissonanceAsLong(NodeComparable)}, it's the same but
	 * returning and computing a {@link BigInteger} instead.
	 */
	public default BigInteger computeDissonanceAsBigInt(NodeComparable<K> nodeBase) {
		return computeDissonanceAsBigInt(nodeBase, DissonanceWeights.WEIGHTS_DEFAULT);
	}

//	/** See {@link #computeDissonanceAsLong(NodeComparable)}. */
//	public default BigInteger computeDissonanceAsBigInt(NodeComparable<K> nodeBase, boolean checkRecursion) {
//		return computeDissonanceAsBigInt(nodeBase, WEIGHTS_DEFAULT, checkRecursion);
//	}

//	public default BigInteger computeDissonanceAsBigInt(NodeComparable<K> nodeBase, DissonanceWeights weights) {
//		return computeDissonanceAsBigInt(nodeBase, weights, false); // make it fast
//	}

	/** See {@link #computeDissonanceAsLong(NodeComparable)}. */
	public BigInteger computeDissonanceAsBigInt(NodeComparable<K> nodeBase, DissonanceWeights weights);
//	, boolean checkRecursion);

	//

	//

	@Override
	public default void toString(StringBuilder sb) { this.toString(sb, 0); }

	/** Call <code>super</code> before doing anything. */
	public default void toStringNonCollectionFields(StringBuilder sb) {
		sb.append(" --> ");
	}

	@Override
	public default void toString(StringBuilder sb, int level) {
		int lev;
		K k;
		addTab(sb, level, false);
		sb.append("NC: ");
		k = getKeyIdentifier();
		if (k instanceof Stringable) {
			((Stringable) k).toString(sb);
		} else {
			sb.append(k);
		}
		toStringNonCollectionFields(sb);
		sb.append(" -----> children:");
		lev = level + 1;
		this.forEachChildNC((child) -> child.toString(sb.append('\n'), lev));
	}

	//

	// TODO STATIC STUFFS

	//

	public static final int HEIGHT_OF_NEW_NODE = -1;

	//

	/** Gets a new Node comparator, given a key comparator. */
	public static <T> Comparator<NodeComparable<T>> newNodeComparatorDefault(Comparator<T> keyComp) {
		return new NodeComparatorDefault<>(keyComp);
	}

	/** Provides a simple implementation of the {@link NodeComparable}. */
	public static <T> NodeComparable<T> newDefaultNodeComparable(T value, Comparator<T> comparatorKey) {
		return new DefaultNodeComparable<>(value, comparatorKey);
	}

	public static <T> DifferenceCalculator<NodeComparable<T>> newDifferenceCalculator(Comparator<T> keyComparator) {
		return (s1, s2) -> { return s2.computeDissonanceAsLong(s1); };
	}

	public static <T> SortedSetEnhanced<NodeComparable<T>> removePath(NodeComparable<T> tree,
			List<NodeComparable<T>> path) {
		boolean canIter;
		SortedSetEnhanced<NodeComparable<T>> forest;
		NodeComparable<T> currentNodeOnPath;
		Comparator<T> compKey;
		Iterator<NodeComparable<T>> iter, pathIterator;
		SortedSetEnhanced<NodeComparable<T>> children;
		forest = tree.newChildrenSet();
		pathIterator = path.iterator();
		if (pathIterator == null || (!pathIterator.hasNext()) || // the tree does not start the with the first path's
																	// sequence node
				tree.getKeyComparator().compare(tree.getKeyIdentifier(), //
						(currentNodeOnPath = pathIterator.next()).getKeyIdentifier()) != 0) {
			forest.add(tree);
			return forest;
		}
		// uses "tree" to travel down "the original node" (the "child")
		children = tree.getChildrenNC();
		canIter = children != null && (!children.isEmpty());
		while (canIter && pathIterator.hasNext()) {
			currentNodeOnPath = pathIterator.next();
			compKey = currentNodeOnPath.getKeyComparator();
			iter = children.iterator(); // it surely has next
			// pick all sibling of the node on path
			while (iter.hasNext()) { // for each "tree" child
				tree = iter.next();
				if (compKey.compare(tree.getKeyIdentifier(), currentNodeOnPath.getKeyIdentifier()) == 0) {
					// the path can continue
					children = tree.getChildrenNC();
					canIter = children != null && (!children.isEmpty());
				} else {
					//
					forest.add(tree);
				}
			}
		}
		return forest;
	}

	//

	// TODO CLASSES

	//

	public static abstract class NodeComparableDefaultAlghoritms<T> implements NodeComparable<T> {
		private static final long serialVersionUID = -651032512L;

		protected int heightNode = 0;
		protected NodeComparable<T> father;

		@Override
		public int getHeightNode() { return heightNode; }

		@Override
		public NodeComparable<T> getFather() { return father; }

		@Override
		public NodeComparable<T> setFather(NodeComparable<T> father) {
			this.father = father;
			return this;
		}

		@Override
		public NodeComparable<T> setHeightNode(int height) {
			this.heightNode = height < 0 ? 0 : height;
			return this;
		}

		@Override
		public int getTreeSize() {
			int[] s = { 1 };
			Set<NodeComparable<T>> children;
			children = getChildrenNC();
			if (children != null) { children.forEach(n -> s[0] += n.getTreeSize()); }
			return s[0];
		}

		protected void getTreeSize(int[] accumulator) {
			Set<NodeComparable<T>> children;
			accumulator[0]++;
			children = getChildrenNC();
			if (children != null) {
				children.forEach(n -> ((NodeComparableDefaultAlghoritms<T>) n).getTreeSize(accumulator));
			}
		}

		// TODO dissonance

		@Override
		public long computeDissonanceAsLong(NodeComparable<T> nodeBase, DissonanceWeights weights) {
			return new DissonanceTreeAlgo_Mine1<T>().computeDissonance(weights, this, nodeBase);
//			return computeDissonanceAsLong_NoRecursionCheck(nodeBase, weights, weights.getWeightDepth());
		}

//		protected long computeDissonanceAsLong_NoRecursionCheck(NodeComparable<T> nodeBase, DissonanceWeights weights,
//				long exponentialWeightDepth) {
//		return new DissonanceTreeAlgo_Mine1<>().computeDissonance(weight, this, nodeBase);
//		}

		@SuppressWarnings("deprecation")
		protected long computeDissonanceAsLong_V2(NodeComparable<T> nodeBase, DissonanceWeights weights,
				long exponentialWeightDepth) {
			return new DissonanceTreeAlgo_Mine2<T>().computeDissonance(weights, this, nodeBase);
		}

		//

		@Override
		public BigInteger computeDissonanceAsBigInt(NodeComparable<T> nodeBase, DissonanceWeights weights) {
			return /*
					 * checkRecursion ? computeDissonanceAsBigInt_WithRecursion(nodeBase, weights,
					 * // nodeBase.newChildrenSet(), BigInteger.valueOf(weights.getWeightDepth())) :
					 */
			computeDissonanceAsBigInt_NoRecursionCheck(nodeBase, weights, BigInteger.valueOf(weights.getWeightDepth()));
		}

		protected BigInteger computeDissonanceAsBigInt_NoRecursionCheck(NodeComparable<T> nodeBase,
				DissonanceWeights weights, BigInteger exponentialWeightDepth) {
			BigInteger[] dissonance = {
					Objects.equals(nodeBase.getKeyIdentifier(), this.getKeyIdentifier()) ? BigInteger.ZERO
							: BigInteger.ONE };

			BiConsumer<NodeComparable<T>, NodeComparable<T>> oneSideDifferenceComputation;
			boolean[] isExceedingNode = { true };
			// since the difference is symmetrical
			oneSideDifferenceComputation = (thisNode, otherNode) -> {
				thisNode.forEachChildNC(child -> {
					/**
					 * Children can either be:
					 * <ul>
					 * <li>shared (this child is contained in both this children set and base
					 * children set)</li>
					 * <li>similar: same root, different recursive children</li>
					 * <li>totally absent: the children present in just one node (this or the
					 * "base") are totally different; so their "difference" is the size</li>
					 * </ul>
					 */
					if (!otherNode.containsChildNC(child)) {
						NodeComparable<T> childBase;
						childBase = otherNode.getChildNCMostSimilarTo(child);
						if (childBase != null) {
							// recursion :D
							BigInteger dissGot;
							if (childBase instanceof NodeComparableDefaultAlghoritms<?>) {
								dissGot = ((NodeComparableDefaultAlghoritms<T>) child)
										.computeDissonanceAsBigInt_NoRecursionCheck(childBase, //
												weights, //
												exponentialWeightDepth
														.multiply(BigInteger.valueOf(weights.getWeightDepth())));

							} else {
								dissGot = child.computeDissonanceAsBigInt(childBase, weights);
							}
							if (dissGot != BigInteger.ZERO) {
								dissonance[0] = dissonance[0].add(dissGot.multiply(exponentialWeightDepth));
							}
						} else {
							dissonance[0] = //
									dissonance[0].add(BigInteger
											.valueOf(isExceedingNode[0] ? weights.getWeightExceedingNode()
													: weights.getWeightMissingNode())
											.multiply(BigInteger.valueOf(child.getTreeSize())));
						}
					}
				});
				isExceedingNode[0] = !isExceedingNode[0];
			};
			oneSideDifferenceComputation.accept(this, nodeBase);
			oneSideDifferenceComputation.accept(nodeBase, this);
			return dissonance[0];
		}

		@Override
		public String toString() {
			StringBuilder sb;
			sb = new StringBuilder();
			toString(sb);
			return sb.toString();
		}
	} // END CLASS NodeComparableDefaultAlghoritms

	//

	public static class DefaultNodeComparable<T> extends NodeComparableDefaultAlghoritms<T> {
		private static final long serialVersionUID = 1L;
		protected T keyIdentifier;
		protected MapTreeAVL<NodeComparable<T>, NodeComparable<T>> backMap;
		protected SortedSetEnhanced<NodeComparable<T>> children;
		protected final Comparator<T> comparatorKey;
		protected final Comparator<NodeComparable<T>> nodeComparator;
		protected DissonanceTreeAlgorithm<T> dissonanceComputator;

		public DefaultNodeComparable(T value, Comparator<T> comparatorKey) {
			super();
			this.comparatorKey = comparatorKey;
			this.keyIdentifier = value;
			this.backMap = MapTreeAVL.newMap(MapTreeAVL.Optimizations.MinMaxIndexIteration, // comparatorKey
//					NodeComparable.newNodeComparatorDefault(comparatorKey)
					this.nodeComparator = super.getNodeComparator());
//			this.children = this.backMap.toSetValue(n -> n.getKeyIdentifier());
			this.children = this.backMap.toSetKey();
			this.dissonanceComputator = new DissonanceTreeAlgo_Zhang_Shasha<>();
		}

		@Override
		public T getKeyIdentifier() { return this.keyIdentifier; }

		@Override
		public Comparator<T> getKeyComparator() { return comparatorKey; }

		@Override
		public Comparator<NodeComparable<T>> getNodeComparator() { return this.nodeComparator; }

		@Override
		public SortedSetEnhanced<NodeComparable<T>> getChildrenNC() { return this.children; }

		public DissonanceTreeAlgorithm<T> getDissonanceComputator() { return dissonanceComputator; }

		public NodeComparable<T> setDissonanceComputator(DissonanceTreeAlgorithm<T> dissonanceComputator) {
			this.dissonanceComputator = dissonanceComputator;
			return this;
		}

		public void setKeyIdentifier(T value) { this.keyIdentifier = value; }

		@Override
		public NodeComparable<T> getChildNCMostSimilarTo(NodeComparable<T> copy) { return this.backMap.get(copy); }

		@Override
		public void forEachChildNCFIFOOrdering(Consumer<NodeComparable<T>> action) {
			this.backMap.forEach(ForEachMode.Queue, e -> action.accept(e.getKey()));
		}

		@Override
		public long computeDissonanceAsLong(NodeComparable<T> nodeBase, DissonanceWeights weights) {
			System.out.println(
					"EH EH NodeComparator - compute dissonance " + dissonanceComputator.getClass().getSimpleName());
			return this.dissonanceComputator.computeDissonance(NodeAlteringCosts.newDefaultNAC(), nodeBase, this);
		}
	}

	//

	public static class NodeComparatorDefault<T> implements Comparator<NodeComparable<T>> {
		protected final Comparator<T> keyComp;
		protected final Comparator<SortedSetEnhanced<NodeComparable<T>>> childrenSetComparator; // huuuuge generics :D

		public NodeComparatorDefault(Comparator<T> keyComp) {
//			this(keyComp, null); }
//
//		public NodeComparatorDefault(Comparator<T> keyComp, Comparator<SortedSetEnhanced<NodeComparable<T>>> c) {
			super();
			this.keyComp = keyComp;
			this.childrenSetComparator = /* c != null ? c : */ newChildrenSetComparator();
		}

		protected Comparator<SortedSetEnhanced<NodeComparable<T>>> newChildrenSetComparator() {
			return SortedSetEnhanced.COMPARATOR_FACTORY_PREFERRED.newComparator(this); // RECURSION
		}

		@Override
		public int compare(NodeComparable<T> n1, NodeComparable<T> n2) {
			int c;
			if (n1 == n2) { return 0; }
			if (n1 == null) { return -1; }
			if (n2 == null) { return 1; }
			c = keyComp.compare(n1.getKeyIdentifier(), n2.getKeyIdentifier());
			if (c != 0)
				return c;
			/*
			 * v1: iterate through children, as
			 * SortedSetEnhanced.ComparatorFactoriesSSE.KEY_ORDER.newComparator(this) does
			 */
			return this.childrenSetComparator.compare(n1.getChildrenNC(), n2.getChildrenNC());
		}
	}

	//

	public static class IteratorPathNodeComp<I, T> implements Iterator<List<I>> {
		protected boolean hasNext;
		protected final Function<NodeComparable<T>, I> valueExtractorFromNode;
		protected final NodeComparable<T> node;
		protected final LinkedList<NodeTraversing> stackNodes;
		protected final LinkedList<I> path;

		/*
		 * NOTE: WE NEED TO ADD A FAKE ADDITIONAL "item" to make it work and then use a
		 * cache to the "hasNext"
		 */
		public IteratorPathNodeComp(NodeComparable<T> node, Function<NodeComparable<T>, I> valueExtractorFromNode) {
			super();
			this.node = node;
			this.valueExtractorFromNode = valueExtractorFromNode;
			Objects.requireNonNull(valueExtractorFromNode, "Required a way to produce items");
			this.stackNodes = new LinkedList<>();
			// set up the stack
			NodeTraversing nt;
			SortedSetEnhanced<NodeComparable<T>> children;
			I item;
			this.path = new LinkedList<>();
			item = valueExtractorFromNode.apply(node);
			// base case: this node
			this.path.add(item);
			nt = new NodeTraversing(node);
			this.stackNodes.addFirst(nt);
			this.hasNext = false;
			// inductive case: all of descendants
			// check if it's possible to descend more
			while (!(node.isLeafNC() || (children = node.getChildrenNC()).isEmpty())) {
				nt.iteratorChildren = children.iterator();
				// descend, going down to another level
				node = nt.iteratorChildren.next();
				this.hasNext |= nt.iteratorChildren.hasNext();
				// preparing the next node
				nt = new NodeTraversing(node);
				this.stackNodes.addFirst(nt);
				item = valueExtractorFromNode.apply(node);
				this.path.addLast(item);
			}
			// add fake stuffs
			this.stackNodes.addFirst(new NodeTraversing(null));
		}

		@Override
		public boolean hasNext() { return hasNext; } // !path.isEmpty();

		@Override
		public List<I> next() {
			// remove the "last used token" (at the start of the iteration, that token is a
			// fake one")
			NodeTraversing nt;
			nt = this.stackNodes.peekFirst();
			if (nt.currentNode == null) {
				// i'm a fake node -> remove me
				this.stackNodes.removeFirst();
				nt = this.stackNodes.peekFirst();
				// and the path is already ready
			} else {
				// remove all "exhausted" node (and its childen's iterator)
				if (nt.iteratorChildren == null) {
					// i'm a leaf -> crawl up to a node with still children.
					do {
						// since i'vr been used in the previous iteration, remove me
						this.path.removeLast();
						this.stackNodes.removeFirst();
					} while ((!this.stackNodes.isEmpty())
							&& (!(nt = this.stackNodes.peekFirst()).iteratorChildren.hasNext()));
				}
				// do the remove
				while ((!this.stackNodes.isEmpty()) && (!nt.iteratorChildren.hasNext())) {
					this.path.removeLast();
					this.stackNodes.removeFirst();
					nt = this.stackNodes.peekFirst();
				}
				// we have a ready-to-use node to iterate over its children
				NodeComparable<T> node;
				I item;
				SortedSetEnhanced<NodeComparable<T>> children;
				node = nt.iteratorChildren.next();
				item = this.valueExtractorFromNode.apply(node);
				this.path.addLast(item);
				nt = new NodeTraversing(node);
				this.stackNodes.addFirst(nt);
				// go down searching for the leaf
				while (!(node.isLeafNC() || (children = node.getChildrenNC()).isEmpty())) {
					nt.iteratorChildren = children.iterator();
					// descend, going down to another level
					node = nt.iteratorChildren.next();
					// preparing the next node
					nt = new NodeTraversing(node);
					this.stackNodes.addFirst(nt);
					item = valueExtractorFromNode.apply(node);
					this.path.addLast(item);
				}
			}
			// update cache
			this.hasNext = false;
			Iterator<NodeTraversing> iterNT;
			iterNT = this.stackNodes.iterator();
			while ((!this.hasNext) && iterNT.hasNext()) {
				nt = iterNT.next();
				if (nt.iteratorChildren != null)
					this.hasNext |= nt.iteratorChildren.hasNext();
			}
			return this.path;
		}

		protected class NodeTraversing {
			protected NodeComparable<T> currentNode;
			protected Iterator<NodeComparable<T>> iteratorChildren;

			public NodeTraversing(NodeComparable<T> currentNode) {
				super();
				this.currentNode = currentNode;
				this.iteratorChildren = null;
			}

			@Override
			public String toString() {
				return "[NT: " + currentNode.getKeyIdentifier() + ", "
						+ (iteratorChildren == null ? null : iteratorChildren.hasNext()) + "]";
			}
		}
	}
}