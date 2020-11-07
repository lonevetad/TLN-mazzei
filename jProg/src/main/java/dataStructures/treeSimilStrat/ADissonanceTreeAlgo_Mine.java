package dataStructures.treeSimilStrat;

import dataStructures.NodeComparable;
import dataStructures.minorUtils.DissonanceWeights;

public abstract class ADissonanceTreeAlgo_Mine<T> implements DissonanceTreeAlgorithm<T> {
	public DissonanceWeights weigtsFrom(NodeAlteringCosts<T> nodeAlteringCost, NodeComparable<T> t1) {
		DissonanceWeights w;
		w = new DissonanceWeights();
		w.setWeightDepth(1);
		w.setWeightMissingNode((int) nodeAlteringCost.insertNodeCost(t1));
		w.setWeightExceedingNode((int) nodeAlteringCost.deleteNodeCost(t1));
		return w;
	}
}