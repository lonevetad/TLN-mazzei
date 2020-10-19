package dataStructures.mtAvl;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import dataStructures.MapTreeAVL;

public class MapTreeAVLQueuable<K, V> extends MapTreeAVLIndexable<K, V> {
	private static final long serialVersionUID = 4806840784056L;

	@SuppressWarnings("unchecked")
	public MapTreeAVLQueuable(MapTreeAVL.BehaviourOnKeyCollision b, Comparator<K> comp)
			throws IllegalArgumentException {
		super(b, comp);
		firstInserted = (NodeAVL_Queuable) NIL;
		((NodeAVL_Queuable) NIL).nextInserted = ((NodeAVL_Queuable) NIL).prevInserted = (NodeAVL_Queuable) NIL;
		optimization = Optimizations.ToQueueFIFOIterating;
	}

	protected NodeAVL_Queuable firstInserted; // stack-like

	//

	// TODO OVERRIDES

	/* inherited overrides yet ready: insertFixup */

	@Override
	protected NodeAVL newNode(K k, V v) {
		NodeAVL n;
		n = new NodeAVL_Queuable(k, v);
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
		firstInserted = (NodeAVL_Queuable) NIL;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected V put(NodeAVL nnn) {
		int prevSize;
		V v;
		NodeAVL_Queuable n;
		n = (NodeAVL_Queuable) nnn;
		v = n.v;
		if (size == 0) {
			super.put(n);
			firstInserted = n;
			n.nextInserted = n.prevInserted = n;// self linking
			return null;
		}
		prevSize = this.size;
		v = super.put(n);
		if (prevSize != Integer.MAX_VALUE && prevSize != size) {
			NodeAVL_Queuable fi;
			fi = firstInserted;
			// node really added
			n.prevInserted = fi.prevInserted;
			n.nextInserted = fi;
			fi.prevInserted.nextInserted = n;
			fi.prevInserted = n;
		}
		n = ((NodeAVL_Queuable) NIL);
		n.nextInserted = n.prevInserted = n;
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
		NodeAVL_Queuable nToBeDeleted, succMaybeDeleted;
		if (root == NIL || nnn == NIL)
			return null;
		v = null;
		nToBeDeleted = (NodeAVL_Queuable) nnn;
		v = nToBeDeleted.v;
		if (size == 1 && comp.compare(root.k, nToBeDeleted.k) == 0) {
			v = super.delete(nToBeDeleted);
			firstInserted = (NodeAVL_Queuable) NIL;
			((NodeAVL_Queuable) NIL).prevInserted = ((NodeAVL_Queuable) NIL).nextInserted = (NodeAVL_Queuable) NIL;
			return v;
		}
		// real deletion starts here:
		hasLeft = nToBeDeleted.left != NIL;
		hasRight = nToBeDeleted.right != NIL;
		succMaybeDeleted = hasRight ? (MapTreeAVLQueuable<K, V>.NodeAVL_Queuable) successorSorted(nnn) : //
				(MapTreeAVLQueuable<K, V>.NodeAVL_Queuable) (hasLeft ? predecessorSorted(nnn) : NIL)//
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
		//
		((NodeAVL_Queuable) NIL).nextInserted = ((NodeAVL_Queuable) NIL).prevInserted = (NodeAVL_Queuable) NIL;
		if (root == NIL) { firstInserted = (NodeAVL_Queuable) NIL; }
		return v;
	}

	@Override
	public Entry<K, V> getAt(int i) {
		NodeAVL_Queuable n;
		if (i < 0 || i >= size)
			throw new IndexOutOfBoundsException("Index: " + i);
		if (size == 0)
			return null;
		if (size == 1)
			return root;
		n = firstInserted;
		// if(i == 0) return n;
		while ((--i >= 0) && ((n = n.nextInserted) != NIL))
			;
		return n;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected NodeAVL successorForIterator(NodeAVL n) { return n == NIL ? NIL : ((NodeAVL_Queuable) n).nextInserted; }

	@Override
	@SuppressWarnings("unchecked")
	protected NodeAVL predecessorForIterator(NodeAVL n) { return n == NIL ? NIL : ((NodeAVL_Queuable) n).prevInserted; }

	@Override
	public boolean containsValue(Object value) {
		boolean valueNull;
		NodeAVL_Queuable n;
		n = firstInserted;
		valueNull = value == null;
		if (n != NIL)
			do {
				if ((valueNull && value == n.v) || (value.equals(n.v)))
					return true;
			} while ((n = n.nextInserted) != NIL);
		return false;
	}

	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		forEach(ForEachMode.Queue, e -> action.accept(e.getKey(), e.getValue()));
	}

	@Override
	public void forEach(Consumer<? super Entry<K, V>> action) {
		forEach(ForEachMode.Queue, e -> action.accept(e));
//		forEach(ForEachMode.Queue,  action );
	}

	@Override
	public void forEach(ForEachMode mode, Consumer<Entry<K, V>> action) {
		NodeAVL_Queuable start, current;
//		NodeAVL n;
		if (root != NIL && action != null) {
			switch (mode) {
			/*
			 * the sorted iterations are copy-pasted from superclass, BUT with a
			 * modification: the predecessor and successor calls are the "super"ones. This
			 * is due to avoid the dynamic binding to call this class's overrides.
			 */
			case SortedDecreasing:
			case SortedGrowing:
			case BreadthGrowing:
			case BreadthDecreasing:
				super.forEach(mode, action);
				return;
			case Queue:
				start = current = firstInserted;
				do {
					action.accept(current);
				} while ((current = current.nextInserted) != start);
				return;
			case Stack:
			default:
//				forEach(action);
				start = current = (firstInserted.prevInserted);
				do {
					action.accept(current);
				} while ((current = current.prevInserted) != start);
				return;
			}
		}
	}

	// TODO iterator cambiare le classi restituite

	@Override
	protected <E> Iterator<E> iteratorGeneric(IteratorReturnType irt, boolean directionIteration, E justANullElement) {
		return new Iterator_Queuable<E>(irt, directionIteration);
	}

	//

	@SuppressWarnings("unchecked")
	@Override
	protected void mergeOnSameClass(Iterator<Entry<K, V>> iter, MapTreeAVLLightweight<K, V> tThis,
			MapTreeAVLLightweight<K, V> tOther) {
		NodeAVL_Queuable n, ttNIL;
		ttNIL = (MapTreeAVLQueuable<K, V>.NodeAVL_Queuable) tThis.NIL;
		while (iter.hasNext()) {
			n = (NodeAVL_Queuable) iter.next();
			n.father = n.left = n.right = n.prevInserted = n.nextInserted = ttNIL;
			tThis.put(n);
		}
	}

	//

	// TODO CLASSES

	protected class NodeAVL_Queuable extends NodeAVL_Indexable {
		private static final long serialVersionUID = 65120329080000L;

		@SuppressWarnings("unchecked")
		public NodeAVL_Queuable(K k, V v) {
			super(k, v);
			prevInserted = nextInserted = (NodeAVL_Queuable) NIL;
		}

		public NodeAVL_Queuable prevInserted, nextInserted;
	}

	public class Iterator_Queuable<E> extends IteratorAVLGeneric<E> {

		public Iterator_Queuable() { this(true); }

		public Iterator_Queuable(boolean normalOrder) { super(normalOrder); }

		public Iterator_Queuable(IteratorReturnType irt) { super(irt); }

		public Iterator_Queuable(IteratorReturnType irt, boolean normalOrder) { super(irt, normalOrder); }

		@Override
		protected void restart() {
			jumps = 0;
			canRemove = false;
			current = end = normalOrder ? firstInserted : firstInserted.prevInserted;
		}

		@Override
		public boolean hasNext() { return (size > 0) && (current != end || jumps == 0); }
	}
}