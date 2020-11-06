package dataStructures.treeSimilStrat;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import dataStructures.MapTreeAVL;
import dataStructures.NodeComparable;
import dataStructures.SortedSetEnhanced;
import dataStructures.minorUtils.DissonanceWeights;

@Deprecated
public class DissonanceTreeAlgo_Mine2<T> extends DissonanceTreeAlgo_Mine1<T> {

	@Override
	protected long computeDissonance(DissonanceWeights weights, NodeComparable<T> t1, NodeComparable<T> nodeBase,
			long exponentialWeightDepth) {
		// key fields
		boolean[] isExceedingNode = { true };
		long[] dissonance = { Objects.equals(nodeBase.getKeyIdentifier(), t1.getKeyIdentifier()) ? 0 : 1 };
		// helper fields
		MapTreeAVL<T, T> backMapRKU;
		SortedSetEnhanced<T> rootKeysUsedSet;
		// the following are "actions to do", but not re-instantiate the lambdas
		BiConsumer<NodeComparable<T>, NodeComparable<T>> oneSideDifferenceComputation;
		Comparator<NodeComparable<T>> nodeKeyComparatorByNodeItself;
		/*
		 * the following serves to collect children to act recursively (and instantiate
		 * just once to save some memory management)
		 */
		final LinkedList<NodeComparable<T>> nodeSharingRootCollectedThis, nodeSharingRootCollectedBase;
		final MapTreeAVL<NodeComparable<T>, NodeComparable<T>> childrenSharingRootThis, childrenSharingRootBase;
		Comparator<NodeComparable<T>> nodeComparat;
		final NodeCompAdderToMap<T> nodeAdderToMap;
		// HERE WE GO, THE CODE .. to just prepare stuffs
		// the helpers ...
		backMapRKU = MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight, t1.getKeyComparator());
		rootKeysUsedSet = backMapRKU.toSetKey();
		// the collector and so ...
		nodeSharingRootCollectedBase = new LinkedList<>();
		nodeSharingRootCollectedThis = new LinkedList<>();
		nodeComparat = t1.getNodeComparator();
		childrenSharingRootThis = MapTreeAVL.newMap(MapTreeAVL.Optimizations.MinMaxIndexIteration, nodeComparat);
		childrenSharingRootBase = MapTreeAVL.newMap(MapTreeAVL.Optimizations.MinMaxIndexIteration, nodeComparat);
		nodeAdderToMap = new NodeCompAdderToMap<>();
		//
		nodeKeyComparatorByNodeItself = (n1, n2) -> {
			return t1.getKeyComparator().compare(n1.getKeyIdentifier(), n2.getKeyIdentifier());
		};

		// HERE WE GO, THE ACTUAL ALGORITHM

		// since the difference is symmetrical, let's generalize with a consumer
		oneSideDifferenceComputation = (thisNode, otherNode) -> {
			thisNode.forEachChildNC(child -> {
				T keyChild = child.getKeyIdentifier();
				if (!rootKeysUsedSet.contains(keyChild)) {
					// to simplify the code later, i'll add some variable
					int sizeNSRCBase, sizeNSRCThis;

					// add the key to the "used set"
					rootKeysUsedSet.add(keyChild);
					/*
					 * now, we have a fresh key, get all child that has the same key as root and
					 * compute them
					 */
					nodeSharingRootCollectedBase.clear();
					nodeSharingRootCollectedThis.clear();
					thisNode.getChildrenNC().forEachSimilar(child,
							// compare only node's key
							nodeKeyComparatorByNodeItself, nodeSharingRootCollectedThis::add);
					otherNode.getChildrenNC().forEachSimilar(child,
							// compare only node's key
							nodeKeyComparatorByNodeItself, nodeSharingRootCollectedThis::add);

					/*
					 * some children could be "shared" (non-empty intersection): remove them since
					 * the difference is 0 (and reduce the lists' sizes).
					 */
					childrenSharingRootThis.clear();
					childrenSharingRootBase.clear();
					{// run on the smallest : it enhance speed
						// place in this scope some variables to simplify the code
						LinkedList<NodeComparable<T>> smallestChildCollector;
						MapTreeAVL<NodeComparable<T>, NodeComparable<T>> smallestChildrenSet;
						if (nodeSharingRootCollectedBase.size() < nodeSharingRootCollectedThis.size()) {
							// fill the greatest
							nodeAdderToMap.mapOntoAdd = childrenSharingRootThis;
							nodeSharingRootCollectedThis.forEach(nodeAdderToMap);
							// run the smallest
							smallestChildCollector = nodeSharingRootCollectedBase;
							smallestChildrenSet = childrenSharingRootBase;
						} else {
							nodeAdderToMap.mapOntoAdd = childrenSharingRootBase;
							nodeSharingRootCollectedBase.forEach(nodeAdderToMap);
							smallestChildCollector = nodeSharingRootCollectedThis;
							smallestChildrenSet = childrenSharingRootThis;
						}
						smallestChildCollector.forEach(n -> {
							if (nodeAdderToMap.mapOntoAdd.containsKey(n)) {
								// discard and remove it
								nodeAdderToMap.mapOntoAdd.remove(n);
							} else { // add to the smallest
								smallestChildrenSet.put(n, n);
							}
						});
					}
					// free some memory
					nodeSharingRootCollectedBase.clear();
					nodeSharingRootCollectedThis.clear();
					// things go hotter .. let's recurse :D
					// now, we have 2 children sets with non-equal nodes (nodes==subtrees)
					// so, apply some base cases
					sizeNSRCThis = childrenSharingRootThis.size();
					sizeNSRCBase = childrenSharingRootBase.size();
					if (sizeNSRCThis != 0 || sizeNSRCBase != 0) {
						// if someone is empty .. simply add the tree height
						if (sizeNSRCThis == 0) {
							childrenSharingRootBase
									.forEach((kk, vv) -> addDissonanceByWholeSubtree_Long(true, dissonance, weights, kk, //
											exponentialWeightDepth));
						}
					} // else: nothing to do here :D
						// now, apply the calculation:
				}
			});
			isExceedingNode[0] = !isExceedingNode[0];
		}; // end of the consumer
		oneSideDifferenceComputation.accept(t1, nodeBase);
		oneSideDifferenceComputation.accept(nodeBase, t1);
		return dissonance[0];
	}

	/** Traverses the whole subtree to add up its "dissonance" */
	protected void addDissonanceByWholeSubtree_Long(boolean isMissing, long[] dissonance, DissonanceWeights w,
			NodeComparable<T> n, long weightDepthExpon) {
		long newWD;
		newWD = weightDepthExpon * w.getWeightDepth(); // it's delegated here eheh
		dissonance[0] += newWD * (isMissing ? w.getWeightMissingNode() : w.getWeightExceedingNode());
		n.forEachChildNC(c -> addDissonanceByWholeSubtree_Long(isMissing, dissonance, w, c, newWD));
	}

	// double-recursive function
	protected void computeDissonanceOnSimilarChildren(NodeComparable<T> nodeBase, DissonanceWeights weights,
			long exponentialWeightDepth,
			//
			long[] dissonance, final LinkedList<NodeComparable<T>> nodeSharingRootCollectedThis,
			final LinkedList<NodeComparable<T>> nodeSharingRootCollectedBase) {
		int sizeNSRCBase, sizeNSRCThis;
		sizeNSRCThis = nodeSharingRootCollectedThis.size();
		sizeNSRCBase = nodeSharingRootCollectedBase.size();
		// edge cases
//					if(sizeNSRCThis==0&&sizeNSRCBase==0)return;
//					if(sizeNSRCThis)
//						
//						//general cases

		// TODO
	}

	//

	//

	//

	// used in difference computator

	protected static class NodeCompAdderToMap<E> implements Consumer<NodeComparable<E>> {
		protected MapTreeAVL<NodeComparable<E>, NodeComparable<E>> mapOntoAdd;

		@Override
		public void accept(NodeComparable<E> t) { mapOntoAdd.put(t, t); }
	}

	protected static interface DifferenceAdderLong {
		public void add(long[] differenceHolder, long value);
	}

	protected static interface DifferenceAdderBigInt {
		public void add(BigInteger[] differenceHolder, BigInteger value);
	}

	protected static final DifferenceAdderLong DIFF_ADDER_LONG = (dh, v) -> { dh[0] += v; };
	protected static final DifferenceAdderBigInt DIFF_ADDER_BIG_INT = (dh, v) -> {
		if (v != BigInteger.ZERO)
			dh[0] = dh[0].add(v);
	};
}