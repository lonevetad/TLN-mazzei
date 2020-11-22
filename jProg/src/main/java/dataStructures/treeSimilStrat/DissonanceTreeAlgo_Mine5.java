package dataStructures.treeSimilStrat;

import java.util.Comparator;

import dataStructures.CollectionAlteringCosts;
import dataStructures.NodeComparable;
import dataStructures.SortedSetEnhanced;
import tools.EditDistance;
import tools.EditDistance.EqualityChecker;
import tools.IterableSized;
import tools.impl.EditDistanceLevenshtein;

/** Uses the {@link EditDistance}. */
public class DissonanceTreeAlgo_Mine5<T> extends ADissonanceTreeAlgo_Mine<T> {
	protected EditDistance editDistance;

	public DissonanceTreeAlgo_Mine5() {}

	@Override
	public long computeDissonance(NodeAlteringCosts<T> nodeAlteringCost, NodeComparable<T> t1, NodeComparable<T> t2) {
		long[] totalDissonance = { 0 };
		computeDissonance_PrepareStuffs(totalDissonance, nodeAlteringCost, t1, t2);
		return totalDissonance[0];
	}

	protected void computeDissonance_PrepareStuffs(long[] totalDissonance, NodeAlteringCosts<T> nodeAlteringCost,
			NodeComparable<T> t1, NodeComparable<T> t2) {
//		EqualityChecker<T> equalityChecker;
//		CollectionAlteringCosts<T> cac;
//		equalityChecker = EqualityChecker.fromComparator(t1.getKeyComparator());
		EqualityChecker<NodeComparable<T>> equalityChecker;
		CollectionAlteringCosts<NodeComparable<T>> cac;
		final EditDistance ed;
//		Comparator<NodeComparable<T>> usefullNodeComparator;
		ed = new EditDistanceLevenshtein();
		equalityChecker = (n1, n2) -> false;// need to perform always the "cac". If equal nodes -> diss == 0
//				EqualityChecker.fromComparator(
//				(n1, n2) -> n1.getKeyComparator().compare(n1.getKeyIdentifier(), n2.getKeyIdentifier()));
//		usefullNodeComparator =NodeComparable.newNodeComparatorDefault(keyComp);
		cac = // here comes the bigger part
				new CollectionAlteringCosts<>() {

					@Override
					public long insertCost(NodeComparable<T> element) {
						return nodeAlteringCost.insertCost(element) + //
						getActionCostWholeSubtree(nodeAlteringCost, true, element.getChildrenNC());
					}

					@Override
					public long deleteCost(NodeComparable<T> element) {
						return nodeAlteringCost.deleteCost(element) + //
						getActionCostWholeSubtree(nodeAlteringCost, false, element.getChildrenNC());
					}

					@Override
					public long renameCost(NodeComparable<T> element, NodeComparable<T> newLabel) {
						// delegate to recursion
						long[] d = { 0 };
						computeDissonance_on_nodes(d, nodeAlteringCost, element, newLabel, equalityChecker, this, ed); // RECURSION
						return d[0];
					}
				};
		computeDissonance_on_nodes(totalDissonance, nodeAlteringCost, t1, t2, equalityChecker, cac, ed);
	}

	protected void computeDissonance_on_nodes(long[] totalDissonance, NodeAlteringCosts<T> nodeAlteringCost,
			NodeComparable<T> t1, NodeComparable<T> t2, EqualityChecker<NodeComparable<T>> equalityChecker,
			CollectionAlteringCosts<NodeComparable<T>> cac, EditDistance ed) {
		totalDissonance[0] += t1.getKeyComparator().compare(t1.getKeyIdentifier(), t2.getKeyIdentifier()) == 0 //
				? 0
				: //
				nodeAlteringCost.renameNodeCost(t1, t2.getKeyIdentifier());
		// do there the most important part
		computeDissonance_on_children(totalDissonance, nodeAlteringCost, t1.getChildrenNC(), t2.getChildrenNC(),
				equalityChecker, cac, ed);
	}

	// the most important part
	protected void computeDissonance_on_children(long[] totalDissonance, NodeAlteringCosts<T> nodeAlteringCost,
			SortedSetEnhanced<NodeComparable<T>> ff, SortedSetEnhanced<NodeComparable<T>> fg,
			EqualityChecker<NodeComparable<T>> equalityChecker, CollectionAlteringCosts<NodeComparable<T>> cac,
			EditDistance ed) {
		boolean ffEmpty, fgEmpty;
		Comparator<NodeComparable<T>> nodeComp;
		final SortedSetEnhanced<NodeComparable<T>> subforestF_SameRootsKey, subforestG_SameRootsKey;
		// first of all, check the forest
		ffEmpty = (ff == null || ff.isEmpty());
		fgEmpty = (fg == null || fg.isEmpty());
		if (ffEmpty || fgEmpty) {
			if (ffEmpty && fgEmpty)
				return;
			else if (ffEmpty) {
				handleForestWithMissingCounterpart(totalDissonance, nodeAlteringCost, fg, false);
				return;
			} else if (fgEmpty) {
				handleForestWithMissingCounterpart(totalDissonance, nodeAlteringCost, ff, true);
				return;
			}
		} else if (ff.size() == 1 && fg.size() == 1) { // recursion
			computeDissonance_on_nodes(totalDissonance, nodeAlteringCost, ff.first(), fg.first(), equalityChecker, cac,
					ed);
			return;
		}
		// else ...

		totalDissonance[0] += ed.editDistance(IterableSized.from(ff), IterableSized.from(fg), equalityChecker, cac);

		// old idea

//		nodeComp = ff.getKeyComparator();
//		// recycle them to save space upon allocating
//		subforestF_SameRootsKey = ff.newSortedSetEnhanced();
//		subforestG_SameRootsKey = ff.newSortedSetEnhanced();

		// IDEA:
		/**
		 * Each forest (which is a set of trees) can be partitioned into "equivalence
		 * classes" (i.e. sub-forests), whose pair distinct elements (pair of trees) are
		 * "equivalent" if that pair's trees has the same key in their root (it's a
		 * weakened for of comparison, restricted to the roots' key). Let's define the
		 * key of each "equivalence class" the "equivalence key".
		 * <p>
		 * So,
		 * <ol>
		 * <li>Form those partitions.</li>
		 * <li>Each "equivalence class" from a given partition absent in the other
		 * partition is treated as "totally missing" (i.e., compute the "insert" or
		 * "delete" cost).</li>
		 * <li>Otherwise (i.e., exists a "equivalence key" that has generated an
		 * "equivalence class" in each partition), compute the {@link EditDistance} of
		 * those two sub-forests.</li>
		 * <li></li>
		 * </ol>
		 * Run over the forest looking for
		 */
	}

	protected void handleForestWithMissingCounterpart(long[] totalDiss, NodeAlteringCosts<T> nodeAlteringCost,
			SortedSetEnhanced<NodeComparable<T>> forest, boolean isInsert) {
		if (forest == null || forest.isEmpty())
			return;
		totalDiss[0] += getActionCostWholeSubtree(nodeAlteringCost, isInsert, forest);
	}

	//

	// OLD

	//

	//

	// helper
//	protected void computeDissonance(long[] totalDissonance, NodeAlteringCosts<T> nodeAlteringCost,
//			NodeComparable<T> t1, NodeComparable<T> t2) {
//		totalDissonance[0] += t1.getKeyComparator().compare(t1.getKeyIdentifier(), t2.getKeyIdentifier()) == 0 //
//				? 0
//				: //
//				nodeAlteringCost.renameNodeCost(t1, t2.getKeyIdentifier());
//		computeDissonance(totalDissonance, nodeAlteringCost, t1.getChildrenNC(), t2.getChildrenNC());
//	}
//
//
//	// THE METHOD
//
//	/** Compares two forest using */
//	protected void computeDissonance(long[] totalDissonance, NodeAlteringCosts<T> nodeAlteringCost, //
//			SortedSetEnhanced<NodeComparable<T>> ff, SortedSetEnhanced<NodeComparable<T>> fg) {
//		boolean ffEmpty, fgEmpty;
//		ffEmpty = (ff == null || ff.isEmpty());
//		fgEmpty = (fg == null || fg.isEmpty());
//		if (ffEmpty || fgEmpty) {
//			if (ffEmpty && fgEmpty)
//				return;
//			else if (ffEmpty) {
//				handleForestWithMissingCounterpart(totalDissonance, nodeAlteringCost, fg, false);
//				return;
//			} else if (fgEmpty) {
//				handleForestWithMissingCounterpart(totalDissonance, nodeAlteringCost, ff, true);
//				return;
//			}
//		} else if (ff.size() == 1 && fg.size() == 1) { // recursion
//			computeDissonance(totalDissonance, nodeAlteringCost, ff.first(), fg.first());
//			return;
//		}
//		// else
//		Comparator<NodeComparable<T>> nodeComp;
//		final SortedSetEnhanced<NodeComparable<T>> subforestF_SameRootsKey, subforestG_SameRootsKey;
//		nodeComp = ff.getKeyComparator();
//		// recycle them to save space upon allocating
//		subforestF_SameRootsKey = ff.newSortedSetEnhanced();
//		subforestG_SameRootsKey = ff.newSortedSetEnhanced();
//
//		// IDEA:
//		/* Run over the forest looking for */
//	}
}