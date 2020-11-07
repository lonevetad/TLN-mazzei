package dataStructures.treeSimilStrat;

import java.util.Objects;
import java.util.function.BiConsumer;

import dataStructures.NodeComparable;
import dataStructures.NodeComparable.NodeComparableDefaultAlghoritms;
import dataStructures.minorUtils.DissonanceWeights;

/** @deprecated use {@link DissonanceTreeAlgo_Zhang_Shasha} instead. */
@Deprecated
public class DissonanceTreeAlgo_Mine1<T> extends ADissonanceTreeAlgo_Mine<T> {

	@Override
	public long computeDissonance(NodeAlteringCosts<T> nodeAlteringCost, NodeComparable<T> t1,
			NodeComparable<T> nodeBase) {
		return computeDissonance(this.weigtsFrom(nodeAlteringCost, t1), t1, nodeBase);
	}

	public long computeDissonance(DissonanceWeights weights, NodeComparable<T> t1, NodeComparable<T> nodeBase) {
		return computeDissonance(weights, t1, nodeBase, weights.getWeightDepth());
	}

	protected long computeDissonance(DissonanceWeights weights, NodeComparable<T> t1, NodeComparable<T> nodeBase,
			long exponentialWeightDepth) {
		BiConsumer<NodeComparable<T>, NodeComparable<T>> oneSideDifferenceComputation;
		boolean[] isExceedingNode = { true };
		long[] dissonance = { Objects.equals(nodeBase.getKeyIdentifier(), t1.getKeyIdentifier()) ? 0 : 1 };
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
				System.out.println("\n\n ___child:");
				System.out.println(child);

				if (!otherNode.containsChildNC(child)) {
					NodeComparable<T> childBase;
					childBase = otherNode.getChildNCMostSimilarTo(child);
					System.out.println("child base:");
					System.out.println(childBase);
					if (childBase != null) {
						// recursion :D
						if (childBase instanceof NodeComparableDefaultAlghoritms<?>) {
							dissonance[0] += exponentialWeightDepth * computeDissonance(weights, //
									child, childBase, //
									exponentialWeightDepth * weights.getWeightDepth());
						} else {
							dissonance[0] += exponentialWeightDepth * child.computeDissonanceAsLong(childBase, //
									weights);
						}
					} else {
						System.out.println("not contained:");
						dissonance[0] += //
								(isExceedingNode[0] ? weights.getWeightExceedingNode() : weights.getWeightMissingNode())
										* child.getTreeSize();
					}
				}
			});
			isExceedingNode[0] = !isExceedingNode[0];
		};
		oneSideDifferenceComputation.accept(t1, nodeBase);
		oneSideDifferenceComputation.accept(nodeBase, t1);
		return dissonance[0];
	}

}