package dataStructures.treeSimilStrat;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dataStructures.NodeComparable;
import testManuali.TreeCompInteger;
import tools.EditDistance;
import tools.EditDistance.EqualityChecker;

/**
 * Simple idea: map each tree to a set of all root-to-leaf path; then sum up the
 * score of each path of the first tree, computing it as the minimum
 * {@link EditDistance} with the paths in the other tree's set.
 * <p>
 * WARNING: Some trees could be considered equal even if the don't.:<br>
 * For instance, imagine a "whip/squid like" tree, i.e. a {@link LinkedList}
 * like tree that ends with a node with tons of leaf-children. Then imagine a
 * second tree, with an amount of linked-list-like subtree all identical (i.e.,
 * sharing all nodes' values in the same height), except for the last node, as
 * for the first tree. As example, taking inspiration from
 * {@link TreeCompInteger#fromString(String)}, they are
 * <ul>
 * <li><code> 77 {55 {22 {33 {1 2 3 4}}}}</code></li>
 * <li>
 * 
 * <pre>
 *  <code> 
 * 77{
 *   55 {22 {33 {1}}}
 *   55 {22 {33 {4}}}
 *   55 {22 {33 {2}}}
 *   55 {22 {33 {3}}}
 * }
 * </code>
 * </pre>
 * 
 * </li>
 * </ul>
 * Those two trees will be mapped to the same set of path, so they will be
 * considered identical.
 */
public class DissonanceTreeAlgo_Mine3_AllPathBruteForce<T> extends ADissonanceTreeAlgo_Mine<T> {

	@Override
	public long computeDissonance(NodeAlteringCosts<T> nodeAlteringCost, NodeComparable<T> ff, NodeComparable<T> gg) {
		long[] cost = { -1 };
		Comparator<NodeComparable<T>> nodeComparatorOnlyByKey;
		EqualityChecker<NodeComparable<T>> equalityCheckerKey;
		nodeComparatorOnlyByKey = (n1, n2) -> {
			return n1.getKeyComparator().compare(n1.getKeyIdentifier(), n2.getKeyIdentifier());
		};
		equalityCheckerKey = EqualityChecker.fromComparator(nodeComparatorOnlyByKey);
		ff.forEachPathNode(l -> {
			long betterCost = -1, diff;
			Iterator<List<NodeComparable<T>>> gpi = gg.iteratorPathNodes();
			while (betterCost != 0 && gpi.hasNext()) {
				diff = EditDistance.editDistance(l, gpi.next(), equalityCheckerKey);
				if (betterCost == -1 || diff < betterCost) { betterCost = diff; }
			}
			if (betterCost < 0)
				betterCost = 0;
			if (cost[0] < 0)
				cost[0] = betterCost;
			else
				cost[0] += betterCost;
		});
		return cost[0];
	}
}