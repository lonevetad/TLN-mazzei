package dataStructures.treeSimilStrat;

import java.util.Comparator;
import java.util.LinkedList;

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
/**
 * Implementation of AP-TED+
 */
public class DissonanceTreeAlgo_AP_TED_Plus<T> implements DissonanceTreeAlgorithm<T> {

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

	//

	/**
	 * Note: <br>
	 * F: a tree <br>
	 * F_v: a tree with root labeled with "v" <br>
	 * y: a subpath (from root to a leaf) <br>
	 * exists, elementof : there are no such ASCII characters for this symbols <br>
	 * p(x): parent node of the node "x" <br>
	 * <p>
	 * F-y: { F_v : exists x . (x elementof y && (not (v elementof y)) && p(x) ==
	 * p(v) } <br>
	 * That means, the result of {@link}
	 * <p>
	 * I condense the cost for left, right and inner path into the same concept.
	 * Further developments could make this difference
	 */
	protected long cost(NodeAlteringCosts<T> nodeAlteringCost, NodeComparable<T> f, NodeComparable<T> g) {
		// may use NodeComparable.removePath to implement "F-y"

		return 0;
	}

	/**
	 * Scan the tree left-to-right deep-first to find the a node so that:
	 * <ol>
	 * <li>it's the first found that respect the other point</li>
	 * <li>it belongs a path so that ..</li>
	 * <li>it has only one child and all its descendants have only one child each
	 * (in short, it's like a {@link LinkedList})</li>
	 * <li>it's either the root or its father has more children (i.e., this node is
	 * the highest of the "linked list")</li>
	 * <li></li>
	 * </ol>
	 * So, removing that node will result in removing ONLY ONE path from the set of
	 * all possible root-to-leaf path of the given node (the parameter).
	 */
	protected NodeComparable<T> firstRemovablePathnode(NodeComparable<T> t) {
		// it proceeds in 2 steps:
		// 1) goes down to a leaf
		// 2) crawl up searching for a multi-child(ren) father

		// V1
//		Object[] nodeAndChildrenIterator;
//		LinkedList<Object[]> stackNodes; // stack of both a node and its children iterator (null if nul or empty set)
//		NodeComparable<T> currentNode;
//		Iterator<NodeComparable<T>> iter;
		SortedSetEnhanced<NodeComparable<T>> children;
//		//
//		children =t.getChildrenNC();
//		if(children==null||children.isEmpty())return t;
//		iter = children.iterator();
//		stackNodes=new LinkedList<>();
//		stackNodes.add(new Object[] {t,iter});
//		
//		t = null; // it's the node we are looking for from here until end
//		while(t==null&&(!stackNodes.isEmpty())) {
//			nodeAndChildrenIterator=stackNodes.peek();
//			currentNode=(NodeComparable<T>) nodeAndChildrenIterator[0];
//			iter=(Iterator<NodeComparable<T>>) nodeAndChildrenIterator[1];
//			if(iter!=null&&iter.hasNext()) {
//				todo
//			} else todo
//		}

		// V2 ---- simplest
		// go straight to left-most
		NodeComparable<T> father;
		while ((children = t.getChildrenNC()) != null && (!children.isEmpty())) {
			t = children.first();
		}
		// now crawl back up
		while ((father = t.getFather()) != null && (children = father.getChildrenNC()).size() == 1) {
			t = father;
		}
		return t;
	}

//	protected NodeAlteringCosts<T> frpn(NodeComparable<T> t){
//		SortedSetEnhanced<NodeComparable<T>> children;
//		children=t.getChildrenNC();
//		if(children==null||children.isEmpty())return t;
//	}

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