package dataStructures.mtAvl;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import dataStructures.MapTreeAVL;
import tools.Comparators;

public class MapTreeAVLMinIter<K, V> extends MapTreeAVLIndexable<K, V> {
	private static final long serialVersionUID = 477874447L;

	@SuppressWarnings("unchecked")
	public MapTreeAVLMinIter(MapTreeAVL.BehaviourOnKeyCollision b, Comparator<K> comp) throws IllegalArgumentException {
		super(b, comp);
		minValue = (NodeAVL_MinIter) NIL;
		minValue.nextInOrder = minValue.prevInOrder = minValue; // that is NIL
		optimization = Optimizations.MinMaxIndexIteration;
	}

	protected NodeAVL_MinIter minValue; // sorted-growing

	//

	// TODO OVERRIDES

	@Override
	protected NodeAVL newNode(K k, V v) {
		NodeAVL n;
		n = new NodeAVL_MinIter(k, v);
		// n.father = n.left = n.right = NIL;
		return n;
	}

	/**
	 * Retrieves but not removes the minimum value in this priority.<br>
	 * This method does not alter the collection, rather than
	 * {@link #removeMinimum()}.
	 */
	@Override
	public Entry<K, V> peekMinimum() {
		Entry<K, V> e;
		return (e = minValue) == NIL ? null : e;
	}

	/**
	 * Retrieves but not removes the maximum value in this priority.<br>
	 * This method does not alter the collection, rather than
	 * {@link #removeMaximum()}.
	 */
	@Override
	public Entry<K, V> peekMaximum() {
		Entry<K, V> e;
		return (e = minValue.prevInOrder) == NIL ? null : e;
	}

	@Override
	public K firstKey() {
		return minValue.k;// getMinimum().getKey();
	}

	@Override
	public K lastKey() {
		return minValue.prevInOrder.k; // getMaximum().getKey();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void clear() {
		super.clear();
		minValue = (NodeAVL_MinIter) NIL;
	}

	// TODO insertFixup
	@Override
	@SuppressWarnings("unchecked")
	protected void insertFixup(NodeAVL nnn) {
		super.insertFixup(nnn);
		((NodeAVL_MinIter) NIL).nextInOrder = ((NodeAVL_MinIter) NIL).prevInOrder = (NodeAVL_MinIter) NIL;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected V put(NodeAVL nnn) {
		int c;
		K k;
		V oldValue, v;
		NodeAVL_MinIter x, next, n;
		n = (NodeAVL_MinIter) nnn;
		k = n.k;
		v = n.v;
		if (root == NIL) {
			size = 1;
			root = n;
			minValue = n;
			n.nextInOrder = n.prevInOrder = n;// self linking
			return null;
		}
//else
//x is the iterator, next is the next node to move on
		next = x = (NodeAVL_MinIter) root; // must not be set to NIL, due to the while condition
		// descend the tree
		c = 0;
		while (next != NIL) {
			x = next;
			c = comp.compare(k, x.k);
			if (c == 0) {
				if (behaviour == MapTreeAVL.BehaviourOnKeyCollision.Replace) {
					oldValue = x.v;
					x.k = k;
					x.v = v;
					return oldValue;
				} else if (behaviour == MapTreeAVL.BehaviourOnKeyCollision.KeepPrevious)
					return x.v;
				else // if (behavior == MapTreeAVL.BehaviorOnKeyCollision.AddItsNotASet) //
						// -> add
					c = -1;
			}
			next = (NodeAVL_MinIter) ((c > 0) ? x.right : x.left);
		}
		if (next == NIL) {
			// end of tree reached: x is a leaf
			if (c > 0)
				x.right = n;
			else
				x.left = n;
			n.father = x;
		} else
			throw new RuntimeException("NOT A END?");
		if (size != Integer.MAX_VALUE)
			size++;

		// adjust links for iterators
		// minValue
		if (c > 0) {
			n.prevInOrder = x;
			x.nextInOrder.prevInOrder = n;
			n.nextInOrder = x.nextInOrder;
			x.nextInOrder = n;
		} else {
			n.nextInOrder = x;
			n.prevInOrder = x.prevInOrder;
			x.prevInOrder.nextInOrder = n;
			x.prevInOrder = n;
			if (x == minValue)
				minValue = n;// in the end
//			if (comp.compare(n.k, minValue.k) < 0) {
//				/*
//				 * all of the following assignement (except for the last one) should not be
//				 * necessary, but tests show off a kind of bug, so that's the hotfix, a
//				 * workaround: simply redo assignements to minValue
//				 */
//				n.nextInOrder = minValue;
//				n.prevInOrder = minValue.prevInOrder;
//				minValue.prevInOrder.nextInOrder = n;
//				minValue.prevInOrder = n;
//				minValue = n;
//			} // else
		}
//		if (comp.compare(n.k, minValue.prevInOrder.k) > 0) {
//			// similary kind of bug for maximum
//			n.nextInOrder = minValue;
//			n.prevInOrder = minValue.prevInOrder;
//			minValue.prevInOrder.nextInOrder = n; // it was minValue, now is n
//			minValue.prevInOrder = n;
//		}

		// don't use n: it's height is 0 and it's connected only to NIL -> is balanced
		insertFixup(x);
		NIL.father = NIL.left = NIL.right = NIL;
		((NodeAVL_MinIter) NIL).nextInOrder = ((NodeAVL_MinIter) NIL).prevInOrder = (NodeAVL_MinIter) NIL;
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected NodeAVL successorSorted(NodeAVL n) { return ((NodeAVL_MinIter) n).nextInOrder; }

	@Override
	@SuppressWarnings("unchecked")
	protected NodeAVL predecessorSorted(NodeAVL n) { return ((NodeAVL_MinIter) n).prevInOrder; }

	/**
	 * Use with care
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected V delete(NodeAVL nnn) {
		boolean hasLeft, hasRight;
		V v;
		NodeAVL_MinIter nToBeDeleted;
		// actionPosition is the parent of the physically deleted node
		if (root == NIL || nnn == NIL)
			return null;
		v = null;
		nToBeDeleted = (NodeAVL_MinIter) nnn;
		v = nToBeDeleted.v;
		if (size == 1 && comp.compare(root.k, nToBeDeleted.k) == 0) {
			v = super.delete(nToBeDeleted);
			// reset
			minValue = (NodeAVL_MinIter) NIL;
			((NodeAVL_MinIter) NIL).prevInOrder = ((NodeAVL_MinIter) NIL).nextInOrder = (NodeAVL_MinIter) NIL;
			return v;
		}
		// real deletion starts here:
		hasLeft = nToBeDeleted.left != NIL;
		hasRight = nToBeDeleted.right != NIL;

		v = super.delete(nnn);

		if (hasLeft || hasRight) {
			if (hasRight) {
				// common code for both cases (2 children or 1)
				// remember: the removed node is "nnn"'s successor,
				NodeAVL_MinIter succ, succRight;

				succRight = (succ = nToBeDeleted.nextInOrder).nextInOrder;
				succRight.prevInOrder = nToBeDeleted;
				nToBeDeleted.nextInOrder = succRight;
				succ.nextInOrder = succ.prevInOrder = (NodeAVL_MinIter) NIL; // break links
			} else {
				// the removed node is on the left, the predecessor: specular to above
				NodeAVL_MinIter pred, predEvenBefore;
				/*
				 * note: it has a left, so that left is lower than this, so this cannot be the
				 * "minValue", so no update of "minValue" needed
				 */
				predEvenBefore = (pred = nToBeDeleted.prevInOrder).prevInOrder;
				predEvenBefore.nextInOrder = nToBeDeleted;
				nToBeDeleted.prevInOrder = predEvenBefore;
				/*
				 * The previous node ("pred") is ment to be deleted because of the strategy
				 * applied to the "single-chided node having the left as only child"
				 * (implemented in the parent-class). That "pred" was the minimum value (because
				 * this node has no right and the left cannot be non-leaf because of AVL
				 * properties [if the left would be non-leaf, then a rotation would have
				 * adjusted it]), so the new node holding the minimum's key (and its associated
				 * value) is exactly "nToBeDeleted" and its "previous even before" is the
				 * maximum, so the maximum's node and "nToBeDeleted" must be linked. What it's
				 * done just 1/2 instructions before is this linkage and the last operation to
				 * perform is to set the minimum value: the node holding that pair,
				 * "nToBeDeleted", as it's just said.
				 */
				if (pred == minValue)
					minValue = nToBeDeleted;

				// clear the removed node
				pred.nextInOrder = pred.prevInOrder = (NodeAVL_MinIter) NIL;
			}
		} else {
			// leaf
			// remember: the removed node is "nnn" itself
			if (nToBeDeleted == minValue)
				minValue = nToBeDeleted.nextInOrder;
			nToBeDeleted.nextInOrder.prevInOrder = nToBeDeleted.prevInOrder;
			nToBeDeleted.prevInOrder.nextInOrder = nToBeDeleted.nextInOrder;
			// clear this leafy node
			nToBeDeleted.nextInOrder = nToBeDeleted.prevInOrder = (NodeAVL_MinIter) NIL;
		}
		if (root == NIL)
			minValue = (NodeAVL_MinIter) NIL;
		((NodeAVL_MinIter) NIL).nextInOrder = ((NodeAVL_MinIter) NIL).prevInOrder = (NodeAVL_MinIter) NIL;
		return v;
	}

	@Override
	public boolean containsValue(Object value) {
		boolean valueNull;
		NodeAVL_MinIter n;
		n = minValue;
		valueNull = value == null;
		if (n != NIL)
			do {
				if ((valueNull && value == n.v) || (value.equals(n.v)))
					return true;
			} while ((n = n.nextInOrder) != NIL);
		return false;
	}

	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		NodeAVL_MinIter start, current;
		if (root != NIL && action != null) {
			start = current = minValue;
			do {
				action.accept(current.k, current.v);
			} while ((current = current.nextInOrder) != start);
		}
	}

	@Override
	public void forEach(Consumer<? super Entry<K, V>> action) {
		NodeAVL_MinIter start, current;
		if (root != NIL && action != null) {
			start = current = minValue;
			do {
				action.accept(current);
			} while ((current = current.nextInOrder) != start);
		}
	}

	@Override
	public void forEach(ForEachMode mode, Consumer<Entry<K, V>> action) {
		NodeAVL_MinIter start, current;
		if (root != NIL && action != null) {
			switch (mode) {
			case Stack:
			case Queue:
				throw new UnsupportedOperationException(
						"Cannot be performed by this subclass: " + this.getClass().getName());
			case BreadthGrowing:
			case BreadthDecreasing:
				super.forEach(mode, action);
				return;
			case SortedDecreasing:
				start = current = minValue.prevInOrder;
				do {
					action.accept(current);
				} while ((current = current.prevInOrder) != start);
				return;
			case SortedGrowing:
			default:
				start = current = minValue;
				do {
					action.accept(current);
				} while ((current = current.nextInOrder) != start);
			}
		}
	}

	// TODO iterator cambiare le classi restituite

	@Override
	protected <E> Iterator<E> iteratorGeneric(IteratorReturnType irt, boolean directionIteration, E justANullElement) {
		return new Iterator_MinIter<E>(irt, directionIteration);
	}

	//

	@SuppressWarnings("unchecked")
	@Override
	protected void mergeOnSameClass(Iterator<Entry<K, V>> iter, MapTreeAVLLightweight<K, V> tThis,
			MapTreeAVLLightweight<K, V> tOther) {
		NodeAVL_MinIter n, ttNIL;
		ttNIL = (MapTreeAVLMinIter<K, V>.NodeAVL_MinIter) tThis.NIL;
		while (iter.hasNext()) {
			n = (MapTreeAVLMinIter<K, V>.NodeAVL_MinIter) iter.next();
			n.father = n.left = n.right = n.nextInOrder = n.prevInOrder = ttNIL;
			tThis.put(n);
		}
	}

	//

	// TODO CLASSES

	protected class NodeAVL_MinIter extends NodeAVL_Indexable {
		private static final long serialVersionUID = -5256145065032054480L;

		@SuppressWarnings("unchecked")
		public NodeAVL_MinIter(K k, V v) {
			super(k, v);
			nextInOrder = prevInOrder = (NodeAVL_MinIter) NIL;
		}

		public NodeAVL_MinIter nextInOrder, prevInOrder;
	}

	public class Iterator_MinIter<E> extends IteratorAVLGeneric<E> {

		public Iterator_MinIter() { this(true); }

		public Iterator_MinIter(boolean normalOrder) { super(normalOrder); }

		public Iterator_MinIter(IteratorReturnType irt) { super(irt); }

		public Iterator_MinIter(IteratorReturnType irt, boolean normalOrder) { super(irt, normalOrder); }

		@Override
		protected void restart() {
			jumps = 0;
			canRemove = false;
			current = end = normalOrder ? minValue : minValue.prevInOrder;
		}

		@Override
		public boolean hasNext() { return (size > 0) && (current != end || jumps == 0); }
	}

	public static void main(String[] args) {
		MapTreeAVL<Integer, Integer> m;
		Set<Integer> s;
		m = MapTreeAVL.newMap(MapTreeAVL.Optimizations.MinMaxIndexIteration, Comparators.INTEGER_COMPARATOR);
		s = m.toSetValue(i -> i);
		s.add(1);
		System.out.println("forEach of map");
		m.forEach((k, v) -> { System.out.println(v); });
		System.out.println("start forEach");
		s.forEach(v -> { System.out.println(v); });
		System.out.println("end forEach");
	}
}