package dataStructures.mtAvl;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import dataStructures.MapTreeAVL;

public class MapTreeAVLFull<K, V> extends MapTreeAVLMinIter<K, V> {
	private static final long serialVersionUID = 56104909071828007L;

	public static enum IterationOrder {
		SortingKeys, Chronological
	}

	@SuppressWarnings("unchecked")
	public MapTreeAVLFull(MapTreeAVL.BehaviourOnKeyCollision b, Comparator<K> comp) throws IllegalArgumentException {
		super(b, comp);
		firstInserted = (NodeAVL_Full) NIL;
		firstInserted.nextInserted = firstInserted.prevInserted = firstInserted; // that is NIL
	}

	protected NodeAVL_Full firstInserted; // stack-like

	//

	// TODO OVERRIDES

	/* inherited overrides yet ready: insertFixup */

	@Override
	protected NodeAVL newNode(K k, V v) {
		NodeAVL n;
		n = new NodeAVL_Full(k, v);
		// n.father = n.left = n.right = NIL;
		return n;
	}

	@Override
	public Entry<K, V> getLastInserted() {
		// if (size == 0) return null;
		return firstInserted.prevInserted;
	}

	@Override
	public Entry<K, V> getFirstInserted() {
		// if (size == 0) return null;
		return firstInserted;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void clear() {
		super.clear();
		firstInserted = (NodeAVL_Full) NIL;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected V put(NodeAVL nnn) {
		int prevSize;
		V v;
		NodeAVL_Full n;
		n = (NodeAVL_Full) nnn;
		if (size == 0) {
			super.put(n);
			n.nextInserted = n.prevInserted = n;// self linking
			this.firstInserted = n;
			return null;
		}
		prevSize = this.size;
		v = super.put(n);
		if (prevSize != Integer.MAX_VALUE && prevSize != size) {
			// node really added
			n.prevInserted = firstInserted.prevInserted;
			n.nextInserted = firstInserted;
			firstInserted.prevInserted.nextInserted = n;
			firstInserted.prevInserted = n;
		}
		((NodeAVL_Full) NIL).nextInserted = ((NodeAVL_Full) NIL).prevInserted = (NodeAVL_Full) NIL;
		return v;
	}

	/**
	 * Use with care.
	 * <p>
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected V delete(NodeAVL nnn) {
		boolean hasLeft, hasRight;
		V v;
		NodeAVL_Full nToBeDeleted, succMaybeDeleted;
		if (root == NIL || nnn == NIL)
			return null;
		v = null;
		nToBeDeleted = (NodeAVL_Full) nnn;
		v = nToBeDeleted.v;
		if (size == 1 && comp.compare(root.k, nToBeDeleted.k) == 0) {
			v = super.delete(nToBeDeleted);
			firstInserted = (NodeAVL_Full) NIL;
			((NodeAVL_Full) NIL).prevInserted = ((NodeAVL_Full) NIL).nextInserted = (NodeAVL_Full) NIL;
			return v;
		}
		// real deletion starts here:
		hasLeft = nToBeDeleted.left != NIL;
		hasRight = nToBeDeleted.right != NIL;
		succMaybeDeleted = hasRight ? (MapTreeAVLFull<K, V>.NodeAVL_Full) successorSorted(nnn) : //
				(MapTreeAVLFull<K, V>.NodeAVL_Full) (hasLeft ? predecessorSorted(nnn) : NIL)//
		;
		v = super.delete(nnn);
		// adjust connections
		if (hasLeft || hasRight) {
			if (size == 1) {
				firstInserted = nToBeDeleted;
				nToBeDeleted.nextInserted = nToBeDeleted.prevInserted = nToBeDeleted;
			} else {
				// nnn wasn't the removed node ...
				// 1) unlink myself (nnn: nToBeDeleted) because that's me that should be removed
				// 2) then I re-link myself because I took the data held by the
				// node that has been removed in the end (succMaybeDeleted)
				// ..1) unlink myself
				nToBeDeleted.nextInserted.prevInserted = nToBeDeleted.prevInserted;
				nToBeDeleted.prevInserted.nextInserted = nToBeDeleted.nextInserted;
				// 2) then adjust my links to the really-removed-nodes ..
				nToBeDeleted.nextInserted = succMaybeDeleted.nextInserted;
				nToBeDeleted.prevInserted = succMaybeDeleted.prevInserted;
				// .. and the adjacent's nodes to point towards me
				nToBeDeleted.nextInserted.prevInserted = nToBeDeleted;
				nToBeDeleted.prevInserted.nextInserted = nToBeDeleted;
				if (succMaybeDeleted == firstInserted) { firstInserted = succMaybeDeleted.nextInserted; }
			}
		} else {
			if (size == 1) {
				firstInserted = nToBeDeleted;
				nToBeDeleted.nextInserted = nToBeDeleted.prevInserted = nToBeDeleted;
			} else {
				if (nToBeDeleted == firstInserted)
					firstInserted = nToBeDeleted.nextInserted;
				nToBeDeleted.nextInserted.prevInserted = nToBeDeleted.prevInserted;
				nToBeDeleted.prevInserted.nextInserted = nToBeDeleted.nextInserted;
			}
		}

		((NodeAVL_Full) NIL).nextInserted = ((NodeAVL_Full) NIL).prevInserted = (NodeAVL_Full) NIL;
		if (root == NIL) {
			firstInserted = (NodeAVL_Full) NIL;
			return v;
		}
		return v;
	}

	// TODO iterator cambiare le classi restituite

	// TODO ITERATORS and FOR-EACH

	@Override
	public void forEach(ForEachMode mode, Consumer<Entry<K, V>> action) {
		NodeAVL_Full start, current;
		if (root != NIL && action != null) {
			switch (mode) {
			case Stack:
				start = current = (firstInserted.prevInserted);
				do {
					action.accept(current);
				} while ((current = current.prevInserted) != start);
				return;
			case Queue:
				start = current = firstInserted;
				do {
					action.accept(current);
				} while ((current = current.nextInserted) != start);
				return;
			case BreadthGrowing:
			case BreadthDecreasing:
			case SortedGrowing:
			case SortedDecreasing:
			default:
//				forEach(action);
				/*
				 * start = current = (MapTreeAVLFull<K, V>.NodeAVL_Full) minValue; do {
				 * action.accept(current); } while ((current = (MapTreeAVLFull<K,
				 * V>.NodeAVL_Full) current.nextInOrder) != start);
				 */
				super.forEach(mode, action);
				return;
			}
		}
	}

	@Override
	public Iterator<Entry<K, V>> iterator() { return iterator(IterationOrder.SortingKeys); }

	/**
	 * Same as {@link #iterator()}, but iterating entries in decreasing order
	 */
	@Override
	public Iterator<Entry<K, V>> iteratorBackward() { return iteratorBackward(IterationOrder.SortingKeys); }

	/**
	 * Same as {@link #iterator()}, but iterating only keys
	 */
	@Override
	public Iterator<K> iteratorKey() { return iteratorKey(IterationOrder.SortingKeys); }

	/**
	 * Same as {@link #iteratorKey()}, but iterating keys in decreasing order
	 */
	@Override
	public Iterator<K> iteratorKeyBackward() { return iteratorKeyBackward(IterationOrder.SortingKeys); }

	/**
	 * Same as {@link #iterator()}, but iterating only values
	 */
	@Override
	public Iterator<V> iteratorValue() { return iteratorValue(IterationOrder.SortingKeys); }

	/**
	 * Same as {@link #iteratorValue()}, but iterating values in decreasing order
	 */
	@Override
	public Iterator<V> iteratorValueBackward() { return iteratorValueBackward(IterationOrder.SortingKeys); }

	//

	public Iterator<Entry<K, V>> iterator(IterationOrder iterationOrder) {
		return new Iterator_Full<>(IteratorReturnType.Entry, MapTreeAVL.NORMAL_DIRECTION_WALKING,
				iterationOrder != IterationOrder.Chronological);
	}

	/**
	 * Same as {@link #iterator()}, but iterating entries in decreasing order
	 */
	public Iterator<Entry<K, V>> iteratorBackward(IterationOrder iterationOrder) {
		return new Iterator_Full<>(IteratorReturnType.Entry, !MapTreeAVL.NORMAL_DIRECTION_WALKING,
				iterationOrder != IterationOrder.Chronological);
	}

	/**
	 * Same as {@link #iterator()}, but iterating only keys
	 */
	public Iterator<K> iteratorKey(IterationOrder iterationOrder) {
		return new Iterator_Full<>(IteratorReturnType.Key, MapTreeAVL.NORMAL_DIRECTION_WALKING,
				iterationOrder != IterationOrder.Chronological);
	}

	/**
	 * Same as {@link #iteratorKey()}, but iterating keys in decreasing order
	 */
	public Iterator<K> iteratorKeyBackward(IterationOrder iterationOrder) {
		return new Iterator_Full<>(IteratorReturnType.Key, !MapTreeAVL.NORMAL_DIRECTION_WALKING,
				iterationOrder != IterationOrder.Chronological);
	}

	/**
	 * Same as {@link #iterator()}, but iterating only values
	 */
	public Iterator<V> iteratorValue(IterationOrder iterationOrder) {
		return new Iterator_Full<>(IteratorReturnType.Value, MapTreeAVL.NORMAL_DIRECTION_WALKING,
				iterationOrder != IterationOrder.Chronological);
	}

	/**
	 * Same as {@link #iteratorValue()}, but iterating values in decreasing order
	 */
	public Iterator<V> iteratorValueBackward(IterationOrder iterationOrder) {
		return new Iterator_Full<>(IteratorReturnType.Value, !MapTreeAVL.NORMAL_DIRECTION_WALKING,
				iterationOrder != IterationOrder.Chronological);
	}

	//

	@SuppressWarnings("unchecked")
	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		NodeAVL_Full start, current;
		if (size != 0 && action != NIL) {
			start = current = (MapTreeAVLFull<K, V>.NodeAVL_Full) minValue;
			do {
				action.accept(current.k, current.v);
			} while ((current = (MapTreeAVLFull<K, V>.NodeAVL_Full) current.nextInOrder) != start);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void mergeOnSameClass(Iterator<Entry<K, V>> iter, MapTreeAVLLightweight<K, V> tThis,
			MapTreeAVLLightweight<K, V> tOther) {
		NodeAVL_Full n, ttNIL;
		ttNIL = (MapTreeAVLFull<K, V>.NodeAVL_Full) tThis.NIL;
		while (iter.hasNext()) {
			n = (NodeAVL_Full) iter.next();
			n.father = n.left = n.right = n.prevInserted = n.nextInserted = (MapTreeAVLFull<K, V>.NodeAVL_Full) (n.nextInOrder = n.prevInOrder = ttNIL);
			tThis.put(n);
		}
	}

	//

	// TODO CLASSES

	protected class NodeAVL_Full extends NodeAVL_MinIter {
		private static final long serialVersionUID = 56008490606601030L;

		@SuppressWarnings("unchecked")
		public NodeAVL_Full(K k, V v) {
			super(k, v);
			prevInserted = nextInserted = ((NodeAVL_Full) NIL);
		}

		public NodeAVL_Full prevInserted, nextInserted;

	}

	public class Iterator_Full<E> extends Iterator_MinIter<E> {

		public Iterator_Full() { this(true); }

		public Iterator_Full(boolean normalOrder) {
			super(normalOrder);
			this.sortedItems = true;
			resetCurrentEnd();
		}

		public Iterator_Full(IteratorReturnType irt) {
			super(irt);
			this.sortedItems = true;
			resetCurrentEnd();
		}

		public Iterator_Full(IteratorReturnType irt, boolean normalOrder) {
			super(irt, normalOrder);
			this.sortedItems = true;
			resetCurrentEnd();
		}

		public Iterator_Full(IteratorReturnType irt, boolean normalOrder, boolean sortedItems) {
			super(irt, normalOrder);
			this.sortedItems = sortedItems;
			resetCurrentEnd();
		}

		//

		protected final boolean sortedItems;

		//

		protected void resetCurrentEnd() {
			current = end = normalOrder ? //
					(sortedItems ? minValue : firstInserted) : //
					(sortedItems ? minValue.prevInOrder : firstInserted.prevInserted);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected NodeAVL peekNextNode(boolean order) {
			NodeAVL_Full n;
			n = (MapTreeAVLFull<K, V>.NodeAVL_Full) current;
			return sortedItems //
					? (order ? n.nextInOrder : n.prevInOrder)//
					: (order ? n.nextInserted : n.prevInserted);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void moveToNext(boolean order) {
			NodeAVL_Full n;
			n = (MapTreeAVLFull<K, V>.NodeAVL_Full) current;
			current = sortedItems //
					? (order ? n.nextInOrder : n.prevInOrder)//
					: (order ? n.nextInserted : n.prevInserted);
		}
	}
}