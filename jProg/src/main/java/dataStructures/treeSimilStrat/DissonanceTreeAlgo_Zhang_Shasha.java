package dataStructures.treeSimilStrat;

import java.util.Comparator;

import dataStructures.NodeComparable;
import dataStructures.SortedSetEnhanced;

/**
 * Implementation of the algorithm proposed ... ehm, here: <i>Zhang, K., Shasha,
 * D.: Simple fast algorithms for the editing distance between trees and related
 * problems. SIAM J. Comput. 18(6), 1245–1262 (1989)</i>
 * <p>
 * The general contract accept a "forest", while this implementation starts from
 * two single nodes.<br>
 * If that's an issue, then wrap the two forests in a wrapper node adding those
 * forests as children through
 * {@link NodeComparable#addChildNC(NodeComparable)}.
 */
public class DissonanceTreeAlgo_Zhang_Shasha<T> implements DissonanceTreeAlgorithm<T> {

	/**
	 * Takes the {@link Comparator} from the first node.
	 * <p>
	 * Inherited documentation:<br>
	 * {@inheritDoc}
	 */
	@Override
	public long computeDissonance(NodeAlteringCosts<T> nodeAlteringCost, NodeComparable<T> t1, NodeComparable<T> t2) {
		Comparator<T> compKey;
		compKey = t1.getKeyComparator();
		return (compKey.compare(t1.getKeyIdentifier(), t2.getKeyIdentifier())) + //
				cd(nodeAlteringCost, compKey, t1.getChildrenNC(), t2.getChildrenNC());
	}

	protected long cd(NodeAlteringCosts<T> nodeAlteringCost, Comparator<T> keyComp,
			SortedSetEnhanced<NodeComparable<T>> fv, SortedSetEnhanced<NodeComparable<T>> gw) {
		int sizef, sizeg;
		long res;
		res = 0;
		sizef = fv.size();
		sizeg = gw.size();
		// base cases
		if (sizef == 0 || sizeg == 0) {
			if (sizef == 0 && sizeg == 0) { return 0; }
			return (sizef == 0) ? getActionCostWholeSubtree(nodeAlteringCost, true, gw)
					: getActionCostWholeSubtree(nodeAlteringCost, false, fv);
		}
		if (sizef == 1 && sizeg == 1) { // both trees
			NodeComparable<T> rootFv, rootGw;
			SortedSetEnhanced<NodeComparable<T>> fchildren, gchildren;
			rootFv = fv.first();
			rootGw = gw.first();
			fchildren = rootFv.getChildrenNC();
			gchildren = rootGw.getChildrenNC();
			// removing a node is just considering its children
			res = cd(nodeAlteringCost, keyComp, fchildren, gw) + nodeAlteringCost.deleteNodeCost(rootFv);
			res = Math.min(res, //
					cd(nodeAlteringCost, keyComp, fv, gchildren) + nodeAlteringCost.insertNodeCost(rootGw));
			res = Math.min(res, // this part could handle "leaves root"
					cd(nodeAlteringCost, keyComp, fchildren, gchildren)
							+ nodeAlteringCost.renameNodeCost(rootFv, rootGw.getKeyIdentifier()));
		} else { // at least one is a forest
			NodeComparable<T> fleftmost, gleftmost, frightmost, grightmost;
			fleftmost = fv.first();
			gleftmost = gw.first();
			frightmost = fv.last();
			grightmost = gw.last();

			/*
			 * first of all, the hugest part: the double recursion (added together) that
			 * iterate with the set without the last keys and the sets without the left keys
			 */
			fv.remove(frightmost);
			gw.remove(grightmost);
			res = cd(nodeAlteringCost, keyComp, fv, gw);
			fv.add(frightmost);
			gw.add(grightmost);
			// now the "no left" part
			fv.remove(fleftmost);
			gw.remove(gleftmost);
			res += cd(nodeAlteringCost, keyComp, fv, gw);
			/*
			 * now, let's do the other options .. optimizing a bit: add and remove the
			 * "left node" to the biggest set -> keep the thiner the most untouched as
			 * possible
			 */
			if (fv.size() < gw.size()) {
				gw.add(gleftmost);
				res = Math.min(res, //
						cd(nodeAlteringCost, keyComp, fv, gw) + nodeAlteringCost.deleteNodeCost(fleftmost) //
				);
				gw.remove(gleftmost);
				fv.add(fleftmost);
				res = Math.min(res, //
						cd(nodeAlteringCost, keyComp, fv, gw) + nodeAlteringCost.insertNodeCost(gleftmost) //
				);
			} else {
				fv.add(fleftmost);
				res = Math.min(res, //
						cd(nodeAlteringCost, keyComp, fv, gw) + nodeAlteringCost.insertNodeCost(gleftmost) //
				);
				fv.remove(fleftmost);
				gw.add(gleftmost);
				res = Math.min(res, //
						cd(nodeAlteringCost, keyComp, fv, gw) + nodeAlteringCost.deleteNodeCost(fleftmost) //
				);
			}
		}
		return res;
	}
}