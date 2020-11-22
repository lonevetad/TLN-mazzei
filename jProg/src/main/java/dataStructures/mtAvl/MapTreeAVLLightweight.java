package dataStructures.mtAvl;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import dataStructures.MapTreeAVL;
import dataStructures.QueueLightweight;
import dataStructures.SortedSetEnhanced;
import dataStructures.minorUtils.EntryImpl;
import tools.ClosestMatch;

/**REMOVED COMMENTS <p>
 * There's a bunch of ways to iterate all over this map.
 * <ul>
 * <li>{@link Iterator} : Using {@link IteratorReturnType} enumeration's values,
 * instances of {@link Iterator} can provide keys, values or both through
 * {@link Entry} instances. Use, for example, {@link #iteratorEntry()}.<br>
 * Note: iterators can iterate in backward sense: just specify {@code false} in
 * {@link #iterator(IteratorReturnType, boolean)}. on</li>
 * <li>{@link Map#forEach(BiConsumer)}: Use {@link ForEachMode} enumeration's
 * values to specify how to iterate through all keys, values or both through
 * {@link Entry} instances.</li>
 * </ul>
 * <p>
 * */

/**
 * This data structure provides three main features:
 * <ul>
 * <li>it's a {@link Map}</li>
 * <li>linear iteration (performed in {@code O(n)} time, where {@code n} is
 * {@link #size()}})</li>
 * <li>could be seen as a {@link SortedSet} of keys of {@link Entry} or a
 * {@link Queue} or {@link List} of keys, values or {@link Entry}</li>
 * </ul>
 * <p>
 * This class is an implementation of a map ({@link SortedMap} in particular)
 * and a instance of {@link Comparator} is needed to be provided in the
 * constructor to define key's order. If not, a {@link IllegalArgumentException}
 * will be thrown.
 * <p>
 * Specifying a value of {@link MapTreeAVL.BehaviourOnKeyCollision} enumeration
 * on constructor, the map can behave differently on trying to add yet existing
 * keys: the old pair key-value could be conserved or fully replaced.
 * <p>
 * This implementation provides a enhanced iteration: it's performed in linear
 * time, so it takes {@code O(n)} (where {@code n} is {@link #size()}}), at a
 * slight cost of time during {@code add} and {@code remove} operations and of
 * space (more or less {@code 4*X} bytes, where {@code X} is the amount of byte
 * needed to define a pointer).
 * <p>
 */
public class MapTreeAVLLightweight<K, V> implements MapTreeAVL<K, V> {
	// , Streamable<java.util.Map.Entry<K, V>> ,Deque<K>, PartOfToString
	private static final long serialVersionUID = -623052859062948013L;
	protected static final int DEPTH_INITIAL = -1;

	private interface ExtracterValueFromNodeByIRT {
		public <Kk, Vv> Object extract(Entry<Kk, Vv> n);
	}

	/**
	 * Iterator and for-each function are both build in a general way. To specify
	 * what is desired to iterate on, specify
	 */
	public enum IteratorReturnType implements ExtracterValueFromNodeByIRT {
		Key(new ExtracterValueFromNodeByIRT() {
			@Override
			public <Kk, Vv> Object extract(Entry<Kk, Vv> n) { return n.getKey(); }
		}), Value(new ExtracterValueFromNodeByIRT() {
			@Override
			public <Kk, Vv> Object extract(Entry<Kk, Vv> n) { return n.getValue(); }
		}), Entry(new ExtracterValueFromNodeByIRT() {
			@Override
			public <Kk, Vv> Object extract(Entry<Kk, Vv> n) { return n; }
		});

		IteratorReturnType(ExtracterValueFromNodeByIRT e) { this.delegate = e; }

		final ExtracterValueFromNodeByIRT delegate;

		@Override
		public <Kk, Vv> Object extract(Entry<Kk, Vv> n) { return delegate.extract(n); }

		/*
		 * @SuppressWarnings("unchecked") protected static <E, Kk, Vv> E
		 * nodeToElement(IteratorReturnType irt, TreeAVL<Kk, Vv>.NodeAVL n) { E e; e =
		 * null; if (irt == Key) e = (E) n.k; else if (irt == Entry) e = (E) n; else if
		 * (irt == IteratorReturnType.Value) e = (E) n.v; else return null; }
		 */

	}

	// TODO STATIC TO-COLLECTION-WRAPPER

	public static <K> Queue<K> asQueue(Comparator<K> comp) { return asQueue(MapTreeAVL.DEFAULT_BEHAVIOUR, comp); }

	public static <K> Queue<K> asQueue(MapTreeAVL.BehaviourOnKeyCollision behavior, Comparator<K> comp) {
		if (comp == null)
			throw new IllegalArgumentException("Comparator cannot be null");
		if (behavior == null)
			throw new IllegalArgumentException("BehaviourOnKeyCollision cannot be null");
		return (new MapTreeAVLLightweight<K, K>(behavior, comp)).toQueue();
	}

	public static <Kk, Vv> Queue<Entry<Kk, Vv>> asQueueEntry(Comparator<Kk> comp, Class<Vv> clazz) {
		return asQueueEntry(MapTreeAVL.DEFAULT_BEHAVIOUR, comp, clazz);
	}

	public static <Kk, Vv> Queue<Entry<Kk, Vv>> asQueueEntry(MapTreeAVL.BehaviourOnKeyCollision behavior,
			Comparator<Kk> comp, Class<Vv> clazz) {
		if (comp == null)
			throw new IllegalArgumentException("Comparator cannot be null");
		if (behavior == null)
			throw new IllegalArgumentException("BehaviourOnKeyCollision cannot be null");
		return (new MapTreeAVLLightweight<Kk, Vv>(behavior, comp)).toQueueEntry();
	}

	public static <K, V> Queue<V> asQueueValue(Comparator<K> comp, Function<V, K> keyExtractor) {
		return asQueueValue(MapTreeAVL.DEFAULT_BEHAVIOUR, comp, keyExtractor);
	}

	public static <K, V> Queue<V> asQueueValue(MapTreeAVL.BehaviourOnKeyCollision behavior, Comparator<K> comp,
			Function<V, K> keyExtractor) {
		if (comp == null)
			throw new IllegalArgumentException("Comparator cannot be null");
		if (behavior == null)
			throw new IllegalArgumentException("BehaviourOnKeyCollision cannot be null");
		if (keyExtractor == null)
			throw new IllegalArgumentException("Key extractor cannot be null");
		return (new MapTreeAVLLightweight<K, V>(behavior, comp)).toQueueValue(keyExtractor);
	}

	//

	public static <K> List<K> asList(Comparator<K> comp) { return asList(MapTreeAVL.DEFAULT_BEHAVIOUR, comp); }

	public static <K> List<K> asList(MapTreeAVL.BehaviourOnKeyCollision behavior, Comparator<K> comp) {
		if (comp == null)
			throw new IllegalArgumentException("Comparator cannot be null");
		if (behavior == null)
			throw new IllegalArgumentException("BehaviourOnKeyCollision cannot be null");
		return (new MapTreeAVLLightweight<K, K>(behavior, comp)).toList();
	}

	public static <Kk, Vv> List<Entry<Kk, Vv>> asListEntry(Comparator<Kk> comp, Class<Vv> clazz) {
		return asListEntry(MapTreeAVL.DEFAULT_BEHAVIOUR, comp, clazz);
	}

	public static <Kk, Vv> List<Entry<Kk, Vv>> asListEntry(MapTreeAVL.BehaviourOnKeyCollision behavior,
			Comparator<Kk> comp, Class<Vv> clazz) {
		if (comp == null)
			throw new IllegalArgumentException("Comparator cannot be null");
		if (behavior == null)
			throw new IllegalArgumentException("BehaviourOnKeyCollision cannot be null");
		return (new MapTreeAVLLightweight<Kk, Vv>(behavior, comp)).toListEntry();
	}

	public static <K, V> List<V> asListValue(Comparator<K> comp, Function<V, K> keyExtractor) {
		return asListValue(MapTreeAVL.DEFAULT_BEHAVIOUR, comp, keyExtractor);
	}

	public static <K, V> List<V> asListValue(MapTreeAVL.BehaviourOnKeyCollision behavior, Comparator<K> comp,
			Function<V, K> keyExtractor) {
		if (comp == null)
			throw new IllegalArgumentException("Comparator cannot be null");
		if (behavior == null)
			throw new IllegalArgumentException("BehaviourOnKeyCollision cannot be null");
		if (keyExtractor == null)
			throw new IllegalArgumentException("Key extractor cannot be null");
		return (new MapTreeAVLLightweight<K, V>(behavior, comp)).toListValue(keyExtractor);
	}

	//

	public static <K> SortedSet<K> asSortedSetKey(Comparator<K> comp) {
		return asSortedSetKey(MapTreeAVL.DEFAULT_BEHAVIOUR, comp);
	}

	public static <K> SortedSet<K> asSortedSetKey(MapTreeAVL.BehaviourOnKeyCollision behavior, Comparator<K> comp) {
		if (comp == null)
			throw new IllegalArgumentException("Comparator cannot be null");
		if (behavior == null)
			throw new IllegalArgumentException("BehaviourOnKeyCollision cannot be null");
		return (new MapTreeAVLLightweight<K, K>(behavior, comp)).toSetKey();
	}

	public static <Kk, Vv> SortedSet<Entry<Kk, Vv>> asSortedSetEntry(Comparator<Kk> comp, Class<Vv> clazz) {
		return asSortedSetEntry(MapTreeAVL.DEFAULT_BEHAVIOUR, comp, clazz);
	}

	public static <Kk, Vv> SortedSet<Entry<Kk, Vv>> asSortedSetEntry(MapTreeAVL.BehaviourOnKeyCollision behavior,
			Comparator<Kk> comp, Class<Vv> clazz) {
		if (comp == null)
			throw new IllegalArgumentException("Comparator cannot be null");
		if (behavior == null)
			throw new IllegalArgumentException("BehaviourOnKeyCollision cannot be null");
		return (new MapTreeAVLLightweight<Kk, Vv>(behavior, comp)).toSetEntry();
	}

	public static <Kk, Vv> SortedSet<Vv> asSortedSetValue(Comparator<Kk> comp, Function<Vv, Kk> keyExtractor) {
		return asSortedSetValue(MapTreeAVL.DEFAULT_BEHAVIOUR, comp, keyExtractor);
	}

	public static <Kk, Vv> SortedSet<Vv> asSortedSetValue(MapTreeAVL.BehaviourOnKeyCollision behavior,
			Comparator<Kk> comp, Function<Vv, Kk> keyExtractor) {
		if (comp == null)
			throw new IllegalArgumentException("Comparator cannot be null");
		if (behavior == null)
			throw new IllegalArgumentException("BehaviourOnKeyCollision cannot be null");
		if (keyExtractor == null)
			throw new IllegalArgumentException("Key extractor cannot be null");
		return (new MapTreeAVLLightweight<Kk, Vv>(behavior, comp)).toSetValue(keyExtractor);
	}

	// TODO CONSTRUCTOR

	public MapTreeAVLLightweight(Comparator<K> comp) throws IllegalArgumentException {
		this(MapTreeAVL.DEFAULT_BEHAVIOUR, comp);
	}

	public MapTreeAVLLightweight(MapTreeAVL.BehaviourOnKeyCollision b, Comparator<K> comp)
			throws IllegalArgumentException {
		if (comp == null)
			throw new IllegalArgumentException("Comparator cannot be null");
		this.setComparator(comp);
		behaviour = b == null ? MapTreeAVL.DEFAULT_BEHAVIOUR : b;
		size = 0;
		NIL = null;
		root = NIL = newNode(null, null);
		NIL.father = NIL.left = NIL.right = NIL; // redo, just to be sure
		NIL.height = DEPTH_INITIAL;
		optimization = Optimizations.Lightweight;
	}

	// TODO FIELDS

	protected int size; // cache
	protected NodeAVL NIL, root;
	protected Comparator<K> comp;
	protected MapTreeAVL.BehaviourOnKeyCollision behaviour;
	protected Optimizations optimization;

	protected Comparator<Entry<K, V>> compEntry;

	// TODO START METHODS

	protected NodeAVL newNode(K k, V v) {
		NodeAVL n;
		n = new NodeAVL(k, v);
		// n.father = n.left = n.right = NIL;
		return n;
	}

	protected void setComparator(Comparator<K> comp) {
		this.comp = comp;
		compEntry = (e1, e2) -> {
			K k1, k2;
			if (e1 == e2)
				return 0;
			if (e1 == null)
				return -1;
			if (e2 == null)
				return 1;
			k1 = e1.getKey();
			k2 = e2.getKey();
			if (k1 == k2)
				return 0;
			if (k1 == null)
				return -1;
			if (k2 == null)
				return 1;
			return this.comparator().compare(k1, k2);
		};
	}

	@Override
	public Comparator<K> getComparator() { return comp; }

	@Override
	public Comparator<Entry<K, V>> getEntryComparator() { return compEntry; }

	@Override
	public Comparator<? super K> comparator() { return comp; }

	/**
	 * Binary search.
	 *
	 * @return null if the key is not present
	 */
	protected NodeAVL getNode(K k) {
		boolean notFound;
		int c;
		NodeAVL n;
		notFound = true;
		n = root;
		while (notFound && n != NIL) {
			c = comp.compare(k, n.k);
			if (notFound = c != 0) { n = (c > 0) ? n.right : n.left; }
			/**
			 * else { // <br>
			 * if (behavior == BehaviourOnKeyCollision.AddItsNotASet) { // <br>
			 * // c == 0 .. perche'? se la chiave non fosse uguale, allora non si ha ancora
			 * // trovato il nodo // <br>
			 * if (k != n.k || ( // <br>
			 * // not equals .. // <br>
			 * (k == null // && n.k != null // <br>
			 * )// // <br>
			 * || (!k.equals(n.k)))// // <br>
			 * ) { // <br>
			 * n = n.left; // <br>
			 * notFound = true; // <br>
			 * } // <br>
			 * } // <br>
			 * }
			 */
		}
		return n; // == NIL ? null : n;
	}

	protected boolean isNullNIL(NodeAVL n) { return n == NIL || n == null; }

	protected boolean isNotNullNIL(NodeAVL n) { return n != NIL && n != null; }

	@Override
	public void clear() {
		size = 0;
		root = NIL;
		NIL.father = NIL.left = NIL.right = NIL;
		NIL.height = DEPTH_INITIAL;
	}

	@Override
	public int size() { return size; }

	@Override
	public boolean isEmpty() { return size == 0; }

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		NodeAVL n;
		n = NIL;
		try {
			n = getNode((K) key);
		} catch (Exception e) {
//			n = NIL;
			return null;
		}
		return n.v; // n == NIL ? null : n.v;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int indexOf(Object o) {
		NodeAVL n;
		n = this.getNode((K) o);
		return (n == null) ? -1 : n.index();
	}

	@Override
	public V apply(K t) { return get(t); }

	// TODO insertFixup
	protected void insertFixup(NodeAVL n) {
		int hl, hr, delta;
//		NodeAVL father;

		while (n != NIL) {
			// lh= ;rh=;
			// recalculate, just to be sure
			n.height = (((hl = n.left.height) > (hr = n.right.height)) ? hl : hr) + 1;
			delta = hl - hr;

			if (delta < 2 && delta > -2) {
				// no rotation
				n = n.father;
			} else {
//				if(delta >=2)n.rotate(true);else
//				father = n.father;
				n.rotate(delta >= 2);
				n = n.father.father;
//				n = father;
			}
		}
		NIL.height = DEPTH_INITIAL;
	}

	@Override
	public V put(K k, V value) { return put(newNode(k, value)); }

	protected V put(NodeAVL n) {
//		boolean notAdded, side;
		int c;
		K k;
		V oldValue, v;
		NodeAVL x, next;
		k = n.k;
		v = n.v;
		if (size == 0) {
			size = 1;
			root = n;
			return null;
		}
//else
//x is the iterator, next is the next node to move on
		next = x = root; // must not be set to NIL, due to the while condition
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
//					next = x.left;
			}
			next = (c > 0) ? x.right : x.left;
		}
//		if (next == NIL) {
		// end of tree reached: x is a leaf
		if (c > 0)
			x.right = n;
		else
			x.left = n;
		n.father = x;
//		}
		if (size != Integer.MAX_VALUE)
			size++;

		// don't use n: it's height is 0 and it's connected only to NIL -> is balanced
		NIL.father = NIL.left = NIL.right = NIL;
		insertFixup(x);
		return null;
	}

	// TODO DELETE

	@SuppressWarnings("unchecked")
	@Override
	public V remove(Object key) { return delete((K) key); }

	/**
	 * If the given key is stored inside the map, then that key and associated value
	 * will be deleted, returning that value. The value {@code null} will be
	 * returned if the given key is not present or it's associated with the given
	 * key.
	 */
	@Override
	public V delete(K k) {
		if (root == NIL)
			return null;
		return delete(getNode(k));
	}

	/**
	 * Use with care.
	 * <p>
	 * {@inheritDoc}
	 */
	protected V delete(NodeAVL nToBeDeleted) {
		boolean hasLeft, hasRight, notNilFather;
		V v;
		NodeAVL ntbdFather, actionPosition;
		// actionPosition is the parent of the physically deleted node
		if (root == NIL || nToBeDeleted == NIL)
			return null;
		v = null;
//			tempForLinks = nToBeDeleted;
		v = nToBeDeleted.v;
		if (size == 1 && comp.compare(root.k, nToBeDeleted.k) == 0) {
			size = 0;
			v = root.v;
			root = NIL;
			return v;
		}

		// real deletion starts here:
		actionPosition = ntbdFather = nToBeDeleted.father;
		notNilFather = ntbdFather != NIL;
		hasLeft = nToBeDeleted.left != NIL;
		hasRight = nToBeDeleted.right != NIL;

		if (hasLeft && hasRight) {
			// both
			NodeAVL succFather, succ;
			succ = successorSorted(nToBeDeleted); //
			// successor must exists
			// substitute (swap?) value
			nToBeDeleted.k = succ.k;
			nToBeDeleted.v = succ.v;// also the value, to complete the substitution
			actionPosition = succFather = succ.father;
			/*
			 * note: successor must have a father. A successor without a father would be the
			 * root, but the root's predecessor is a leaf or has just one child. It's
			 * because if that predecessor would be "2-children-ed", then its successor
			 * would be the right children, who has a father, clearly.
			 */
			/*
			 * For similar reasons, the successor is the left's father's child if its height
			 * is greater than 2. If not, then the father would have been choose as
			 * successor because of the successor algorithm. Again, the successor cannot
			 * have a left child, because of it would be selected by the successor
			 * algorithm.
			 */
			// SOOOOOO remove the successor and the attach the succ's right, if not NIL
			if (succFather.left == succ)
				succFather.left = succ.right;
			else
				succFather.right = succ.right;
			succ.father = NIL;
			// if successor is not a leaf
			if (succ.right != NIL) // because successor is always a leaf or has a leaf at right
				(succ.right.father = succFather).height = 0;
			succ.right = NIL;
		} else if (hasLeft || hasRight) { // or "^" or "!="
			// just one child
			NodeAVL child;
			if (hasLeft) {
				child = nToBeDeleted.left;
				nToBeDeleted.left = NIL;
			} else {
				child = nToBeDeleted.right;
				nToBeDeleted.right = NIL;
			}
			// DO not move children, move its values
			nToBeDeleted.k = child.k;
			nToBeDeleted.v = child.v;
			nToBeDeleted.height = 0; // DEPTH_INITIAL+1
			child.father = NIL;

			actionPosition = nToBeDeleted;// .father;
		} else {
			// leaf
			if (notNilFather) {
				if (ntbdFather.left == nToBeDeleted)
					ntbdFather.left = NIL;
				else
					ntbdFather.right = NIL;
			} else {// i'm root AND a leaf: this tree will be cleared (leather)
				size = 0;
				root = NIL;
				NIL.father = NIL.left = NIL.right = NIL;
				NIL.height = DEPTH_INITIAL;
				return v;
			}
		}

		// then balance
		// father can't be NIL
//			balanceOnRemove(actionPosition);
		insertFixup(actionPosition);
		actionPosition.height = ((actionPosition.left.height > actionPosition.right.height) ? actionPosition.left.height
				: actionPosition.right.height) + 1;

		// todo 2
		if (--size == 0)
			root = NIL;
		else if (root == NIL)
			throw new RuntimeException("BUG: Root is nil but tree is not empty");
		NIL.father = NIL.left = NIL.right = NIL;
		return v;
	}

	@Override
	public Entry<K, V> getAt(int i) {
		NodeAVL n;
		MySimpleStack nodesCalls;
		if (i < 0 || i >= size)
			throw new IndexOutOfBoundsException("Index: " + i);
		if (size == 0)
			return null;
		if (size == 1)
			return root;
		nodesCalls = new MySimpleStack();
		n = root;

		// simple case: i == 0
		if (i == 0) {
			while (n.left != NIL)
				n = n.left;
			return n;
		}
		// and i == size-1
		if (i == size - 1) {
			while (n.right != NIL)
				n = n.right;
			return n;
		}

		// push the left-most first
		while (n.left != NIL) {
			nodesCalls.push(n);
			n = n.left;
		}
		// "i" is now treated as "nodes amount to discard"
		do {
			// initial case: the root of a sub-tree.
			/*
			 * It's a leaf, so the baic-case, or a sub-root which the left-part does not
			 * holds the desired node, so it could be the chosen one.
			 */
			n = nodesCalls.pop();
			if (i-- > 0) {
				// the root of the sub-tree is not the chosen one - move to the successor:
				/*
				 * Upon pushing the right, push also ALL right's lefts, so the last one will be
				 * the successor
				 */
				if (n.right != NIL) {
					// start recursion: push the right ..
					nodesCalls.push(n = n.right);
					// then the left-most, coming back to the initial case
					while (n.left != NIL) {
						nodesCalls.push(n = n.left);
					}
				}
			} else {
				nodesCalls.clear();
			}
		} while (!nodesCalls.isEmpty());
		return n;
	}

	// TODO ITERATORS and FOR-EACH

	@Override
	public void forEachAndDepth(EntryDepthConsumer<K, V> action) {
		if (root == NIL || action == null)
			return;
		inOrderVisit(action, 0, root);
	}

	protected void inOrderVisit(EntryDepthConsumer<K, V> action, int depth, NodeAVL node) {
		if (node.left != NIL)
			inOrderVisit(action, depth + 1, node.left);
		action.accept(node, depth);
		if (node.right != NIL)
			inOrderVisit(action, depth + 1, node.right);
	}

	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		forEach(ForEachMode.SortedGrowing, e -> action.accept(e.getKey(), e.getValue()));
	}

	@Override
	public void forEach(Consumer<? super Entry<K, V>> action) {
		forEach(ForEachMode.SortedGrowing, e -> action.accept(e));
//		forEach(ForEachMode.SortedGrowing,  action );
	}

	@Override
	public void forEach(ForEachMode mode, Consumer<Entry<K, V>> action) {
		NodeAVL n;
		QueueLightweight<NodeAVL> q;
		if (root != NIL)
			if (action != null) {
				switch (mode) {
				case Queue:
				case Stack:
					throw new UnsupportedOperationException(
							"Cannot be performed by this subclass: " + this.getClass().getName());
				case SortedDecreasing:
					n = root;
					if (n.right != NIL)// descend to maximum
						while ((n = n.right).right != NIL)
							;
					action.accept(n);
					while ((n = predecessorSorted(n)) != NIL)
						action.accept(n);
					break;
				case SortedGrowing:
					n = root;
					if (n.left != NIL)// descend to minimum
						while ((n = n.left).left != NIL)
							;
					action.accept(n);
					while ((n = successorSorted(n)) != NIL)
						action.accept(n);
					break;
				case BreadthGrowing:
					q = new QueueLightweight<>();
					q.add(root);
					while (!q.isEmpty()) {
						action.accept(n = q.poll());
						if (n.left != NIL)
							q.add(n);
						if (n.right != NIL)
							q.add(n);
					}
					return;
				case BreadthDecreasing:
					q = new QueueLightweight<>();
					q.add(root);
					while (!q.isEmpty()) {
						action.accept(n = q.poll());
						if (n.right != NIL)
							q.add(n);
						if (n.left != NIL)
							q.add(n);
					}
					return;
				default:
					forEach(action);
				}
			} else
				forEach(action);
	}

	@Override
	public Iterator<Entry<K, V>> iterator() {
		return // new IteratorAVLEntry<Entry<K, V>>
		iteratorGeneric(IteratorReturnType.Entry, MapTreeAVL.NORMAL_DIRECTION_WALKING//
				, NIL);
	}

	/**
	 * Same as {@link #iterator()}, but iterating entries in decreasing order
	 */
	@Override
	public Iterator<Entry<K, V>> iteratorBackward() {
		return // new IteratorAVLEntry<Entry<K, V>>
		iteratorGeneric(IteratorReturnType.Entry, !MapTreeAVL.NORMAL_DIRECTION_WALKING, NIL);
	}

	/**
	 * Same as {@link #iterator()}, but iterating only keys
	 */
	@Override
	public Iterator<K> iteratorKey() {
		return // new IteratorAVLEntry<K>
		iteratorGeneric(IteratorReturnType.Key, MapTreeAVL.NORMAL_DIRECTION_WALKING//
				, NIL.k);
	}

	/**
	 * Same as {@link #iteratorKey()}, but iterating keys in decreasing order
	 */
	@Override
	public Iterator<K> iteratorKeyBackward() {
		return // new IteratorAVLEntry<K>
		iteratorGeneric(IteratorReturnType.Key, !MapTreeAVL.NORMAL_DIRECTION_WALKING//
				, NIL.k);
	}

	/**
	 * Same as {@link #iterator()}, but iterating only values
	 */
	@Override
	public Iterator<V> iteratorValue() {
		return // new IteratorAVLEntry<V>
		iteratorGeneric(IteratorReturnType.Value, MapTreeAVL.NORMAL_DIRECTION_WALKING//
				, NIL.v);
	}

	/**
	 * Same as {@link #iteratorValue()}, but iterating values in decreasing order
	 */
	@Override
	public Iterator<V> iteratorValueBackward() {
		return // new IteratorAVLEntry<V>
		iteratorGeneric(IteratorReturnType.Value, !MapTreeAVL.NORMAL_DIRECTION_WALKING//
				, NIL.v);
	}

	protected <E> Iterator<E> iteratorGeneric(IteratorReturnType irt, boolean directionIteration, E justANullElement) {
		return new IteratorAVLGeneric<E>(irt, directionIteration);
	}

	/**
	 * Retrieves but not removes the minimum value in this priority.<br>
	 * This method does not alter the collection, rather than
	 * {@link #removeMinimum()}.
	 */
	@Override
	public Entry<K, V> peekMinimum() {
		NodeAVL n;
		if (root == NIL)
			return null;
		n = root;
		while (n.left != NIL)
			n = n.left;
		return n;
	}

	/**
	 * Retrieves but not removes the maximum value in this priority.<br>
	 * This method does not alter the collection, rather than
	 * {@link #removeMaximum()}.
	 */
	@Override
	public Entry<K, V> peekMaximum() {
		NodeAVL n;
		if (root == NIL)
			return null;
		n = root;
		while (n.right != NIL)
			n = n.right;
		return n;
	}

	/**
	 * Retrieves AND removes the minimum value in this priority.<br>
	 * This method alters the collection, rather than {@link #peekMinimum()}.
	 */
	@Override
	public Entry<K, V> removeMinimum() {
		NodeAVL n;
		n = (MapTreeAVLLightweight<K, V>.NodeAVL) this.peekMinimum();
		if (n == NIL)
			return null;
		this.delete(n);
		return n;
	}

	/**
	 * Retrieves AND removes the maximum value in this priority.<br>
	 * This method alters the collection, rather than {@link #peekMaximum()}.
	 */
	@Override
	public Entry<K, V> removeMaximum() {
		NodeAVL n;
		n = (MapTreeAVLLightweight<K, V>.NodeAVL) this.peekMaximum();
		if (n == NIL)
			return null;
		this.delete(n);
		return n;
	}

	// TODO range query, closest match, forEachSimilar ... yay complex stuffs

	@Override
	public MapTreeAVL<K, V> rangeQuery(K lowerBound, boolean isLowerBoundIncluded, K upperBound,
			boolean isUpperBoundIncluded) throws IllegalArgumentException {
		int c;
		MapTreeAVL<K, V> r;
		if (lowerBound == null)
			throw new IllegalArgumentException("Lower bound is null");
		if (upperBound == null)
			throw new IllegalArgumentException("Upper bound is null");
		c = this.comp.compare(lowerBound, upperBound);
		if (c > 0)
			throw new IllegalArgumentException("Lower bound is greater than upper bound");
		if (c == 0) {
			if (isLowerBoundIncluded || isUpperBoundIncluded) {
				r = MapTreeAVL.newMap(optimization, behaviour, comp);
				if (this.containsKey(lowerBound))
					r.put(lowerBound, this.get(lowerBound));
				return r;
			} else
				throw new IllegalArgumentException("The interval is empty: same bounds but bot excluded.");
		}
		r = MapTreeAVL.newMap(optimization, behaviour, comp);
		if (this.isEmpty()) { return r; }
		if (size == 1) {
			if ((comp.compare(lowerBound, root.k) < 0) && (comp.compare(root.k, upperBound) < 0)) {
				r.put(root.k, root.v);
			}
			return r;
		}
		if (comp.compare(((NodeAVL) this.peekMaximum()).k, lowerBound) >= 0 && //
				comp.compare(((NodeAVL) this.peekMinimum()).k, upperBound) <= 0) {
			// recycle the "get" code
			NodeAVL n, temp, firstNode;
			// lower bound is greater: move to the real starting node
			n = temp = root;
			// search for the first node
			firstNode = null;
			while (firstNode == null && n != NIL) {
//				System.out.println("while 1 node: " + n);
				c = comp.compare(lowerBound, n.k);
				if (c == 0 && isLowerBoundIncluded) {
					firstNode = n;
				} else {
					temp = n; // temp used as "previous"
					n = (c < 0) ? n.left : n.right;
				}
			}
			if (firstNode == null) {
				firstNode = temp;
				while (firstNode != NIL && (c = comp.compare(firstNode.k, lowerBound)) < 0) {
//					System.out.println("while 2 first node successing it" + firstNode.k);
					firstNode = successorSorted(firstNode);
				}
			}
			if (firstNode == null || firstNode == NIL) { return r; }
			n = firstNode;
			// add the first node and each subsequent node (lower than upper bound)
			// BUT FIRST: check the starting node "n" and the upper bound
			c = comp.compare(n.k, upperBound);
			if (!(c < 0 || (c == 0 && isUpperBoundIncluded))) { return r; } // no, outside range
			do {
				r.put(n.k, n.v);
				temp = successorSorted(n);
//				System.out.println("while 3temp will is now: " + temp.k);
				if (temp == NIL)
					n = NIL; // no more nodes available -> stop
				else {
					c = comp.compare(temp.k, upperBound);
					if ((c < 0) || (c == 0 && isUpperBoundIncluded)) {
						// add n to interval
						n = temp;
					} else {// ("n" exceeds the upper bound) or ("n" is equal BUT the flag is false)
						n = NIL; // -> stop it
					}
				}
			} while (n != NIL);
		}
		return r;
	}

	@Override
	public ClosestMatch<Entry<K, V>> closestMatchOf(K key) {
		boolean notFound, isExactMatch;
		int c;
		NodeAVL n, closestUpper;
		Comparator<K> co;
		n = root;
		if (n == NIL || n == null)
			return null;
		closestUpper = null;
		co = comp;
		if (notFound = !(isExactMatch = (n.left == NIL && n.right == NIL)) //
		) {
			// search for the nearest lower key: scan node by node.
			do {
				c = co.compare(key, n.k);
				if (c == 0) {
					closestUpper = null;
					notFound = false;
//				return new ClosestMatch<>(n);
					isExactMatch = true;
				} else {
					if (c < 0) {
						// go to the left
						if (n.left != NIL) {
							closestUpper = n;
							n = n.left;
						} else {
							// n is at the bottom of a subtree but could have a predecessor. If so -> pred.
							// == lower bound
							closestUpper = n;
							n = predecessorSorted(n);
							notFound = false;
							if (n == NIL) {
								n = null; // cannot return NIL and a "not existing lower bound" must be marked in some
											// way
							} // else : n is already the lower bound
						}
					} else {
						// similarly, go to right
						if (n.right != NIL) {
							n = n.right;
						} else {
							notFound = false;
							closestUpper = successorSorted(n);
							if (closestUpper == NIL) {
								closestUpper = null; // n is lower bound, no upper bound and NIL cannot be returned
							}
						}
					}
				}
			} while (notFound);
		}
		Entry<K, V> keyWrapper = new EntryImpl<>(key, null);
		Comparator<Entry<K, V>> ec;
		ec = (e1, e2) -> { return co.compare(e1.getKey(), e2.getKey()); };
		return isExactMatch ? new ClosestMatch<>(keyWrapper, ec, n)
				: new ClosestMatch<>(keyWrapper, ec, n, closestUpper);
	}

	@Override
	public void forEachSimilar(K key, Comparator<K> keyComp, Consumer<Entry<K, V>> action) {
		int c;
		NodeAVL n, niter, temp;
		if (keyComp == null) { keyComp = this.getComparator(); }
		if ((n = root) == null || n == NIL)
			return;
		// similar to getNode
		while (n != NIL && (c = keyComp.compare(key, n.k)) != 0) {
			n = (c < 0) ? n.left : n.right;
		}
		if (n == NIL || n == null)
			return;
		/*
		 * we have the starting point: iterate for each predecessor and successor. To be
		 * precise: search for the first similar in sequence, than the last, then scan
		 * the span
		 */
		// the start
		niter = n;
		while ((temp = predecessorSorted(niter)) != NIL && temp != n && keyComp.compare(key, temp.k) == 0) {
			niter = temp;
		}
		// the end
		while ((temp = successorSorted(n)) != NIL && temp != niter && keyComp.compare(key, temp.k) == 0) {
			n = temp;
		}
		if (n == niter) {
			action.accept(niter);
		} else {
			// the span
			// niter == start ; n == end
			temp = niter;
			do {
				action.accept(temp);
			} while ((temp = successorSorted(temp)) != n // until the end (excluded for now)
					&& temp != niter // do not go back to the start
					// just in case
					&& niter != NIL);
			action.accept(n); // do the end
		}
	}

	@Override
	public Entry<K, V> getLastInserted() {
		throw new UnsupportedOperationException("Not enought informations in this class to implement it");
	}

	@Override
	public Entry<K, V> getFirstInserted() {
		throw new UnsupportedOperationException("Not enought informations in this class to implement it");
	}

	@Override
	public Entry<K, V> getLowesCommonAncestor(K k1, K k2) {
		int c1, c2;
		NodeAVL n, prev;
		if (root == NIL)
			return null;
		n = root;
		if (comp.compare(k1, k2) == 0) {
			do {
				prev = n;
				c1 = comp.compare(k1, n.k);
				n = (c1 < 0) ? n.left : n.right;
			} while (n != NIL);
		} else {
			boolean notFound, c1low, c2low;
			notFound = true;
			do {
				prev = n;
				c1 = comp.compare(k1, n.k);
				if (c1 == 0)
					return n;
				c2 = comp.compare(k2, n.k);
				if (c2 == 0)
					return n;
				c1low = c1 < 0;
				c2low = c2 < 0;
				// if all belongs to the same child's subnodes -> go there
				if (c1low && c2low)
					n = n.left;
				else if (!(c1low || c2low))
					n = n.right;
				else
					notFound = false;
			} while (notFound && n != NIL);
		}
		return prev;
	}

	// TODO MERGE

	/**
	 * Merge the smaller tree in the bigger ones. The smaller one will be cleared
	 * after the call.
	 * <P>
	 * Returns the instance of the bigger tree upon success, {@code null} otherwise
	 * (i.e. the given tree is null).
	 */
	@Override
	public MapTreeAVL<K, V> merge(MapTreeAVL<K, V> tOther) {
		boolean sameClasses;
		Iterator<Entry<K, V>> iter;
		NodeAVL n; // , ttNIL
		MapTreeAVL<K, V> tThis;
		if (tOther == null)
			return null;

		if (tOther.size() > 0) {
			tThis = this;

			/*
			 * if (this.size < tOther.size) { tOther.merge(this); this.size = tOther.size;
			 * this.NIL = tOther.NIL; this.root = tOther.root; this.comp = tOther.comp;
			 * this.compEntry = tOther.compEntry; this.behavior = tOther.behavior;
			 * tOther.clear(); }
			 */

			if (this.size == 0) {
				/*
				 * this.size = tOther.size; this.root = tOther.root; this.behavior =
				 * tOther.behavior; this.comp = tOther.comp; this.lastInserted =
				 * tOther.lastInserted; this.minValue = tOther.minValue; n = this.NIL; this.NIL
				 * = tOther.NIL; tOther.NIL = n;// swap NIL tOther.clear();
				 */
				return tOther;
			}
			if (tOther.size() > size) {
				// swap
				tThis = tOther;
				tOther = this;
			}
			// tOther is the smaller now, tThis is the bigger
			sameClasses = this.getClass().equals(tOther.getClass());
			iter = tOther.iterator();
			if (sameClasses) {
				this.mergeOnSameClass(iter, (MapTreeAVLLightweight<K, V>) tThis, (MapTreeAVLLightweight<K, V>) tOther);
			} else {
				while (iter.hasNext()) {
					n = (NodeAVL) iter.next();
					tThis.put(n.k, n.v);
				}
			}
			tOther.clear();
			return tThis;
		}
		return this;
	}

	/** To be overrided */
	protected void mergeOnSameClass(Iterator<Entry<K, V>> iter, MapTreeAVLLightweight<K, V> tThis,
			MapTreeAVLLightweight<K, V> tOther) {
		NodeAVL n, ttNIL;
		ttNIL = tThis.NIL;
		while (iter.hasNext()) {
			n = (NodeAVL) iter.next();
			n.father = n.left = n.right = //
					// n.prevInserted = n.nextInserted = n.nextInOrder = n.prevInOrder =
					ttNIL;
			tThis.put(n);
		}
	}

	/**
	 * If the first key (first parameter), called {@code kOld}, is present, then
	 * that key is removed and its previously associated value will be associated
	 * with the second given key, called {@code kNew}.<br>
	 * Returns a instance of {@link Entry} associated with the previous key (first
	 * parameter).
	 *
	 * @param kOld the previous key, that will be removed
	 * @param kNew the new key that will be associated to {@code kOld}'s value, if
	 *             it's present, replacing it
	 * @return the previous pair key-value, if present
	 */
	@Override
	public Entry<K, V> changeKey(K kOld, K kNew) {
		NodeAVL prevEntry;
		Entry<K, V> copy;
		prevEntry = this.getNode(kOld); // the entry to be deleted
		if (prevEntry == null || prevEntry == NIL)
			return null;
		copy = new EntryImpl<>(kOld, prevEntry.v);
		this.delete(prevEntry);
		this.put(kNew, prevEntry.v);
		return copy;
	}

	/**
	 * If the first key (first parameter), called {@code key}, is present, then that
	 * associated value will be replaced with the given value (second
	 * parameter).<br>
	 * Returns a instance of {@link Entry} associated with the previous value.
	 *
	 * @param key  the key of the previously pair key-value.
	 * @param vNew If the given key is present, then the old value is replaced with
	 *             the new provided ones
	 * @return the previous pair key-value, if present
	 */
	@Override
	public Entry<K, V> changeValue(K key, V vNew) {
		NodeAVL prevEntry;
		Entry<K, V> copy;
		prevEntry = this.getNode(key); // the entry to be deleted
		if (prevEntry == null || prevEntry == NIL)
			return null;
		copy = new EntryImpl<>(key, prevEntry.v);
		prevEntry.v = vNew;
		return copy;
	}
	/*
	 * public V delete_OLD_UNCOMPLETE(K k) { V v; NodeAVL nToBeDeleted, succ; v =
	 * null; nToBeDeleted = getNode(k); if (nToBeDeleted != NIL) { v =
	 * nToBeDeleted.v; succ = successor(nToBeDeleted); if (succ != NIL && succ !=
	 * NIL) { // todo if (succ.father != NIL) { succ.father.left = succ.right; if
	 * (succ.right != NIL && succ.right.father != NIL) succ.right.father =
	 * succ.right; succ.father.height = Math.max(succ.height, succ.right.height) +
	 * 1; } } else { if (nToBeDeleted.father != NIL) { nToBeDeleted.father.right =
	 * NIL; nToBeDeleted.father.height = nToBeDeleted.father.left.height + 1; } } //
	 * todo 2 if (--size == 0) root = NIL; } return v; }
	 */

	@SuppressWarnings("unchecked")
	@Override
	public boolean containsKey(Object key) { return getNode((K) key) != NIL; }

	@Override
	public Object[] toArray() {
		final int s;
		final Object[] a;
		a = new Object[s = size];
		this.forEach(new BiConsumer<K, V>() {
			int ss = s;

			@Override
			public void accept(K k, V v) { a[--ss] = k; }
		});
		return a;
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] aOrig) {
		final int s;
		final T[] a;
		if (aOrig == null)
			throw new NullPointerException("Cannot provide a null array, gives at least an 0-size array");
		s = size;
		if (s == 0)
			return (new ArrayList<T>(s)).toArray(aOrig);
		if (s != aOrig.length) {
			a = (T[]) Array.newInstance(aOrig.getClass().getComponentType(), s);
		} else
			a = aOrig;
		var ct = aOrig.getClass().getComponentType();
		if (ct.isAssignableFrom(Entry.class)) {
			this.forEach(new Consumer<Entry<K, V>>() {
				int ss = s;

				@Override
				public void accept(Entry<K, V> e) { a[--ss] = (T) e; }
			});
		} else {
			this.forEach(new BiConsumer<K, V>() {
				int ss = s;

				@Override
				public void accept(K k, V v) {
					if (k != null) {
						a[--ss] = ct.isAssignableFrom(k.getClass()) ? ((T) k) : null;
					} else if (v != null) {
						a[--ss] = ct.isAssignableFrom(v.getClass()) ? ((T) v) : null;
					} else
						a[--ss] = null;
				}

			});
		}
		return a;
	}

	// public void forEach(BiFunction<K,V> f) {}

	// minor methods

	/**
	 * Warning: may return the first element of the sequence (since it's the
	 * "successor") in some subclasses and overrides.
	 */
	protected NodeAVL successorSorted(NodeAVL n) {
		if (n == NIL)
			return n;
		if (n.right != NIL) {
			if ((n = n.right).left != NIL)
				while ((n = n.left).left != NIL)
					;
			return n;
		}
		// travel fathers
		while (n.father != NIL && n.father.right == n)
			n = n.father;
//			if (n.father == NIL)return NIL;
		return n.father;
	}

	/** See {@link #successorSorted(NodeAVL)} for the warning. */
	protected NodeAVL predecessorSorted(NodeAVL n) {
		if (n == NIL)
			return n;
		if (n.left != NIL) {
			if ((n = n.left).right != NIL)
				while ((n = n.right).right != NIL)
					;
			return n;
		}
		// travel fathers
		while (n.father != NIL && n.father.left == n)
			n = n.father;
//			if (n.father == NIL)return NIL;
		return n.father;
	}

	/** See {@link #successorSorted(NodeAVL)} for the warning. */
	protected NodeAVL successorForIterator(NodeAVL n) {
		return successorSorted(n);
	}

	/** See {@link #successorSorted(NodeAVL)} for the warning. */
	protected NodeAVL predecessorForIterator(NodeAVL n) {
		return predecessorSorted(n);
	}

	@Override
	public boolean containsValue(Object value) {
		boolean valueNull;
		Iterator<Entry<K, V>> iter;
		Entry<K, V> e;
		if (root != NIL) {
			valueNull = value == null;
			iter = this.iterator();
			while (iter.hasNext()) {
				e = iter.next();
				if ((valueNull && value == e.getValue()) || (value.equals(e.getValue())))
					return true;
			}
		}
		return false;
	}

	// TODO START COLLECTION CONVERTITORS

	@Override
	public SortedSet<K> keySet() {
		if (behaviour == MapTreeAVL.BehaviourOnKeyCollision.AddItsNotASet)
			throw new UnsupportedOperationException(
					"Cannot create a set if the behavior is MapTreeAVL.BehaviourOnKeyCollision.AddItsNotASet");
		return new SortedSetKeyWrapper();
	}

	@Override
	public SortedSet<Entry<K, V>> entrySet() {
		if (behaviour == MapTreeAVL.BehaviourOnKeyCollision.AddItsNotASet)
			throw new UnsupportedOperationException(
					"Cannot create a set if the behavior is MapTreeAVL.BehaviourOnKeyCollision.AddItsNotASet");
		return new SortedSetEntryWrapper();
	}

	/**
	 * Convert this map into a back-mapping instance of {@link Queue} of keys.
	 */
	@Override
	public Queue<K> toQueue() { return new QueueEntryWrapper<K>(IteratorReturnType.Key); }

	/**
	 * As like {@link #toQueue()}, but for {@link Entry} of both keys and values.
	 */
	@Override
	public Queue<Entry<K, V>> toQueueEntry() { return new QueueEntryWrapper<Entry<K, V>>(IteratorReturnType.Entry); }

	/**
	 * As like {@link #toQueue()}, but for values.<br>
	 * Since keys are used to map values, a <i>"key extractor"</i> is required to
	 * get the key from a specific value.<br>
	 * e For further details, see this class's documentation:
	 * {@link MapTreeAVLLightweight}.
	 *
	 * @param keyExtractor way to extract the key from a value. Could simply return
	 *                     its hash or itself.
	 */
	@Override
	public Queue<V> toQueueValue(Function<V, K> keyExtractor) {
		if (keyExtractor == null)
			throw new IllegalArgumentException("The values' keys extractor cannot be null");
		return new QueueEntryWrapper<V>(IteratorReturnType.Value, keyExtractor);
	}

	/**
	 * Convert this map into a back-mapping instance of {@link List} of keys.
	 */
	@Override
	public List<K> toList() { return new ListWrapper<K>(IteratorReturnType.Key); }

	/**
	 * As like {@link #toList()}, but for {@link Entry} of both keys and values.
	 */
	@Override
	public List<Entry<K, V>> toListEntry() { return new ListWrapper<Entry<K, V>>(IteratorReturnType.Entry); }

	/**
	 * As like {@link #toValues()}, but for values. <br>
	 * See {@link #toQueueValue(Function)}.
	 */
	@Override
	public List<V> toListValue(Function<V, K> keyExtractor) {
		return new ListWrapper<V>(IteratorReturnType.Value, keyExtractor);
	}

	@Override
	public SortedSetEnhanced<K> toSetKey() { return (SortedSetEnhanced<K>) keySet(); }

	@Override
	public SortedSetEnhanced<Entry<K, V>> toSetEntry() { return (SortedSetEnhanced<Entry<K, V>>) entrySet(); }

	@Override
	public SortedSetEnhanced<V> toSetValue(Function<V, K> keyExtractor) {
		if (behaviour == MapTreeAVL.BehaviourOnKeyCollision.AddItsNotASet)
			throw new UnsupportedOperationException(
					"Cannot create a set if the behavior is MapTreeAVL.BehaviourOnKeyCollision.AddItsNotASet");
		return new SortedSetValueWrapper(keyExtractor);
	}

	// ENDCOLLECTION CONVERTITORS

	@Override
	public Collection<V> values() {
		final List<V> vals;
		vals = new ArrayList<>(size);
		if (size > 0)
			forEach(e -> vals.add(e.getValue()));
		return vals;
	}

	@Override
	public SortedMap<K, V> subMap(K fromKey, K toKey) {
		subMapUnsupportedExc();
		return null;
	}

	@Override
	public SortedMap<K, V> headMap(K toKey) {
		subMapUnsupportedExc();
		return null;
	}

	@Override
	public SortedMap<K, V> tailMap(K fromKey) {
		subMapUnsupportedExc();
		return null;
	}

	protected void subMapUnsupportedExc() throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"Operation forbidden: no way to keep track of original Map AND filter fields so that they will not appear there.\n Make your own wrapper checking, with a comparator, what to insert, delete, etc, also for iteration");
	}

	@Override
	public K firstKey() { return this.peekMinimum().getKey(); }

	@Override
	public K lastKey() { return this.peekMaximum().getKey(); }

	@Override
	public Stream<Entry<K, V>> stream() {
//		return new StreamTreeAVL<Entry<K, V>>();
		throw new UnsupportedOperationException("Operation not implemented yet");
	}

	@Override
	public String toString() {
		StringBuilder sb;
		sb = new StringBuilder(128);
//		toString(sb, 0);
		toString(sb);
		return sb.toString();
	}

	public void toString(StringBuilder sb) {
		Entry<K, V> m;
		sb.append("size: ");
		sb.append(this.size);
		sb.append(", collection-type: ");
		sb.append(this.behaviour.name());
		if (size == 0)
			return;
		sb.append("\nMin: (k: ");
		m = peekMinimum();
		sb.append(m.getKey());
		sb.append(", v: ");
		sb.append(m.getValue());

		sb.append("), Max: (k:");
		m = peekMaximum();
		sb.append(m.getKey());
		sb.append(", v: ");
		sb.append(m.getValue()).append(')');

		sb.append('\n').append('\n');
//		toString(sb, root, 0);
		forEachAndDepth((nnn, level) -> {
			NodeAVL node = (MapTreeAVLLightweight<K, V>.NodeAVL) nnn;
			if (isNotNullNIL(node)) {
				sb.ensureCapacity(sb.length() + level << 2);
				while (level-- > 0) {
					sb.append('\t');
				}
				sb.append(String.valueOf(node));
				sb.append('\n');
			}
		});
	}

//	protected void toString(StringBuilder sb, NodeAVL node, int level) {
//		int tabLevel;
//		if (isNotNullNIL(node)) {
//			level++;
//			toString(sb, node.left, level);
////			addTab(sb, level - 1, false);
//			tabLevel = level - 1;
//			sb.ensureCapacity(sb.length() + tabLevel << 2);
//			while (tabLevel-- > 0) {
//				sb.append('\t');
//			}
//			sb.append(String.valueOf(node));
//			sb.append('\n');
//			toString(sb, node.right, level);
//		}
//	}

	@Override
	public void compact() {
		int s;
		final Object[] nodes;
		s = this.size();
		if (s < 7)// empirical
			return;
		nodes = new Object[s];
		// putt all nodes onto the array
		this.forEach(new ForEacherEntry((i, e) -> nodes[i] = e));
		this.root = balanceRec(nodes, 0, nodes.length - 1);
		this.root.father = NIL;
		NIL.left = NIL.left.right = NIL.father = NIL;
	}

	/** Interval's Boundaries are all inclusive */
	@SuppressWarnings("unchecked")
	protected NodeAVL balanceRec(Object[] nodes, int min, int max) {
		int mid, hl, hr;
		NodeAVL nmid;
		if (min >= max) {
			// recycle nmid as a temporary variable
			nmid = (NodeAVL) nodes[min];
			nmid.left = nmid.right = NIL;
			nmid.height = 0;
			return nmid;
		}
		mid = (min >> 1) + (max >> 1);
		if (((min & 0x1) == 1) && ((max & 0x1) == 1))
			mid++; // both odd
		nmid = (NodeAVL) nodes[mid];
		if (min < mid) {
			nmid.left = balanceRec(nodes, min, mid - 1);
			nmid.left.father = nmid;
		} else {
			// almost empty
			nmid.left = NIL;
		}
		if (mid < max) {
			nmid.right = balanceRec(nodes, mid + 1, max);
			nmid.right.father = nmid;
		} else {
			nmid.right = NIL;
		}
		NIL.left = NIL.right = NIL.father = NIL;
		NIL.height = DEPTH_INITIAL;
		nmid.height = ((hl = nmid.left.height) > (hr = nmid.right.height) ? hl : hr) + 1;
		return nmid;
	}

	//

	// TODO CLASSES

	protected class NodeAVL implements Serializable, Entry<K, V> {
		private static final long serialVersionUID = -5984105125453013609L;
		protected int height;
		protected K k;
		protected V v;
		protected NodeAVL father, left, right;
		// this node can hold informations for the itertaions

		public NodeAVL(K k, V v) {
			super();
			this.k = k;
			this.v = v;
			this.height = 0;
			this.father = this.left = this.right = NIL;
		}

		@Override
		public K getKey() { return k; }

		@Override
		public V getValue() { return v; }

		@Override
		public V setValue(V value) {
			V v;
			v = this.v;
			this.v = v;
			return v;
		}

		public void rotate(boolean isRight) {
			int hl, hr;
			NodeAVL nSide, oldFather;
			oldFather = father;
			if (isRight) {
				// right
				nSide = left;
				if (nSide.right.height > nSide.left.height) {
					// three-rotation : ignoring this difference would cause the tree to be
					// umbalanced again
					final NodeAVL a, b, c;
					a = this;
					b = nSide;
					c = b.right;
//					if (oldFather != NIL) {
					if (oldFather.left == a)
						oldFather.left = c;
					else
						oldFather.right = c;
					c.father = oldFather;
//					}
					a.father = c;
					a.left = c.right;
					if (c.right != NIL)
						c.right.father = a;
					c.right = a;
					if (c.left.father != NIL)
						c.left.father = b;
					b.right = c.left;
					b.father = c;
					c.left = b;

					// recompute height
					c.height++;
					a.height -= 2;
					b.height--;
					NIL.left = NIL.right = NIL.father = NIL;
					if (a == root) {
						root = c;
						c.father = NIL; // not necessary, but done to be sure
					}
					return;
				}
				left = left.right; // i could have put "nSide. .." but the whole piece of code would be less clear
				nSide.right.father = this;
				nSide.right = this;
				// adjust sizes
			} else {
				// left
				nSide = right;
				if (nSide.left.height > nSide.right.height) {
					final NodeAVL a, b, c;
					a = this;
					b = nSide;
					c = b.left;
					// then
//					if (oldFather != NIL) {
					if (oldFather.left == a)
						oldFather.left = c;
					else
						oldFather.right = c;
					c.father = oldFather;
//					}
					a.father = c;
					a.right = c.left;
					if (c.left != NIL)
						c.left.father = a;
					c.left = a;
					if (c.right.father != NIL)
						c.right.father = b;
					b.left = c.right;
					b.father = c;
					c.right = b;

					// recompute height
					c.height++;
					a.height -= 2;
					b.height--;
					NIL.left = NIL.right = NIL.father = NIL;
					if (a == root) {
						root = c;
						c.father = NIL; // not necessary, but done to be sure
					}
					return;
				}
				right = right.left; // i could have put "nSide. .." but the whole piece of code would be less clear
				nSide.left.father = this;
				nSide.left = this;
			}
			if (/* isNullNIL */NIL == (oldFather))
				// i'm root .. ehm, no: i WAS root
				root = nSide;
			else {
				if (oldFather.left == this)
					oldFather.left = nSide;
				else
					oldFather.right = nSide;
			}
			father = nSide;
			nSide.father = oldFather;
			hl = left.height;
			hr = right.height;
			hl = height = (hl > hr ? hl : hr) + 1;
			hr = ((isRight) ? nSide.left : nSide.right).height;
			nSide.height = (hl > hr ? hl : hr) + 1;
		}

		protected int index() {
//			return -1;
			int i;
			NodeAVL n;
//			i = left.size();
//			if (father != NIL && father.right == this)
//				i += father.index();
//in absence of other node's data, use a O(n*log(n)) algorithm: cycle over the tree
			i = 0;
			n = (MapTreeAVLLightweight<K, V>.NodeAVL) peekMinimum();
			while (n != this && n != NIL) { // n!=NIL to avoid infinite cycle on weird concurrency
				n = successorSorted(n);
				i++;
			}
			return n == NIL ? -1 : i;
		}

		protected int size() {
			int s;
			s = 0;
			if (this == NIL)
				return 0;
			if (left != NIL)
				s += left.size();
			if (right != NIL)
				s += right.size();
			return 1 + s;
		}

		@Override
		public String toString() {
			return "k:" + String.valueOf(k) + " - v:" + String.valueOf(v) + ",h:" + height + ",f:"
					+ String.valueOf(father.k);
		}
	}

	/*
	 * public class IteratorAVL implements Iterator<K> { boolean neverStarted;
	 * NodeAVL current, end;
	 *
	 * public IteratorAVL() { neverStarted = true; current = end = minValue; }
	 *
	 * @Override public boolean hasNext() { return current != end || neverStarted; }
	 *
	 * @Override public K next() { K k; if (neverStarted) return null; neverStarted
	 * = false; k = current.k; current = current.nextInOrder; return k; } }
	 */
	// TODO ITERATOR
	public class IteratorAVLGeneric<E> implements ListIterator<E> {

		public IteratorAVLGeneric() { this(true); }

		public IteratorAVLGeneric(boolean normalOrder) { this(IteratorReturnType.Entry, normalOrder); }

		public IteratorAVLGeneric(IteratorReturnType irt) { this(irt, true); }

		public IteratorAVLGeneric(IteratorReturnType irt, boolean normalOrder) {
			super();
			this.irt = irt;
			this.normalOrder = normalOrder;
			this.isEmpty = isEmpty();
			restart();
		}

		// boolean neverStarted;
		protected final boolean normalOrder, isEmpty;
		protected boolean canRemove;
		protected int jumps;
		protected NodeAVL current, end;
		protected final IteratorReturnType irt;

		protected void restart() {
			jumps = 0;
			canRemove = false;
			if (isEmpty)
				return;
			if (normalOrder) {
				current = (NodeAVL) getAt(0);
				end = (NodeAVL) getAt(size - 1);
			} else {
				end = (NodeAVL) getAt(0);
				current = (NodeAVL) getAt(size - 1);
			}
		}

		protected NodeAVL peekNextNode() { return peekNextNode(normalOrder); }

		protected void moveToNext() { moveToNext(normalOrder); }

		protected NodeAVL peekNextNode(boolean order) {
			return order ? successorForIterator(current) : predecessorForIterator(current);
		}

		protected void moveToNext(boolean order) { current = peekNextNode(order); }

		@SuppressWarnings("unchecked")
		protected E getDesiredReturn() {
			E e;
			if (irt == IteratorReturnType.Entry)
				e = (E) current;
			else if (irt == IteratorReturnType.Key)
				e = (E) current.k;
			else // if (irt == IteratorReturnType.Value)
				e = (E) current.v;
			return e;
		}

		//

		@Override
		public boolean hasNext() {
			if (isEmpty)
				return false;
			return (size > 0) && (current != NIL);// end|| (jumps < size;// == 0
//			)); // neverStarted;
		}

		@Override
		public E next() {
			E e;
			if (isEmpty)
				return null;
			if (!hasNext())
				return null;
//			// NOTE: all methods' call shown in comments are explicity copied due to
			// performance enhancements (following idea "one, two, may")
			// NOTE of 18-06-2019: nobody cares: it's more clear and not-so-much costly to
			// invoke that method
			e = getDesiredReturn();
			if (++jumps == 0)
				jumps = 1;
			/*
			 * if (irt == IteratorReturnType.Key) e = (E) current.k; else if (irt ==
			 * IteratorReturnType.Value) e = (E) current.v; else e = (E) current;
			 */
			// nextNode();
			moveToNext();
//					normalOrder ? current.nextInOrder : current.prevInOrder;
			canRemove = true;
			if (e == NIL)
				throw new NullPointerException("WTF WHY I'M NIL??");
			return e;
		}

		@Override
		public boolean hasPrevious() {
			if (isEmpty)
				return false;
			return // hasNext();
			(size > 0) && (current != end) && (jumps > 0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public E previous() {
			NodeAVL supposedPreviousNode;
			E e;
			if (!hasPrevious())
				throw new NoSuchElementException("Cannot get previous element if we are on the starting position");
			e = null;
			if ((supposedPreviousNode = peekNextNode(!normalOrder)) != end) {
				/*
				 * commented because the "movement" is done inside the "if", the value is
				 * retrieved after the movement and the "order" is the opposite of the
				 */
//				next();
				current = supposedPreviousNode;
				e = getDesiredReturn();
				// reset "neverSetted" ?
//			if (current == (ordPrev ? end.nextInOrder : end.prevInOrder))neverStarted = true;
				if (--jumps < 0)
					jumps = 0;
				canRemove = true;
			}
			return e;
		}

		public int index() { return normalOrder ? jumps : (size - (jumps + 1)); }

		@Override
		public int nextIndex() {
			// return index() + 1;
			return normalOrder ? (jumps + 1) : (size - (jumps + 2));
		}

		@Override
		public int previousIndex() {
			// return index() - 1;
			return normalOrder ? (jumps - 1) : (size - jumps);
		}

		@Override
		public void remove() {
			/**
			 * original code, before 18-06-2019 <br>
			 * NodeAVL nextInLine;<br>
			 * nextInLine = peekNextNode();<br>
			 * if (current == end)<br>
			 * end = nextInLine;<br>
			 * delete(current);<br>
			 * current = nextInLine;<br>
			 */
			boolean mustUpdateEnd;
			NodeAVL oldCurrent;
			if (canRemove && jumps > 0) {
				mustUpdateEnd = (current == end);
				oldCurrent = current;
				moveToNext(!normalOrder);
				delete(oldCurrent);
				if (mustUpdateEnd)
					end = oldCurrent;
				jumps--;
				canRemove = false;
			}
		}

		@Override
		public void set(E e) {
			throw new UnsupportedOperationException(
					"Cannot set an arbitrary element. Use the original data structure's setting method");
		}

		@Override
		public void add(E e) {
			throw new UnsupportedOperationException(
					"Cannot add an arbitrary element. Use the original data structure's adding/putting method");
		}
	}

	/*
	 * protected class IteratorAVL_ValueExtractor extends IteratorAVLGeneric<V> {
	 * IteratorAVL_ValueExtractor(IteratorAVLGeneric<Entry<K,V> itBack) {
	 * this.itBack = itBack; }
	 *
	 * IteratorAVLGeneric<Entry<K,V> itBack; public V next() {
	 *
	 * } }
	 */

	//

	public interface TreeAVLDelegator<Kk, Vv> {
		public MapTreeAVLLightweight<Kk, Vv> getBackTree();
	}

	// TODO WrapperCollection
	protected abstract class WrapperCollection<E> implements Serializable, TreeAVLDelegator<K, V>, Collection<E> {
		private static final long serialVersionUID = 9324138419482L;

		protected WrapperCollection(IteratorReturnType irt) { this(irt, null); }

		protected WrapperCollection(IteratorReturnType irt, Function<V, K> keyExtractor) {
//			this.treeBack = t;
			this.irt = irt;
			this.keyExtractor = keyExtractor;
			this.hasKeyExtractor = keyExtractor != null;
		}

		protected final boolean hasKeyExtractor;
		protected final Function<V, K> keyExtractor;
		protected final IteratorReturnType irt;

		@Override
		public MapTreeAVLLightweight<K, V> getBackTree() { return MapTreeAVLLightweight.this; }

		// unused o.o
		protected K getKeyFromNode(NodeAVL n) {
			K k;
			k = null;
			if (n == NIL)
				return null;
			if (irt == IteratorReturnType.Value) {
				k = hasKeyExtractor ? //
						keyExtractor.apply(n.v)//
						: n.k;
			} else {
				k = n.k;
			}
			return k;
		}

		@SuppressWarnings("unchecked")
		protected K getKeyFromObject(Object o) {
			K k;
			k = null;
			if (irt == IteratorReturnType.Entry) {
				k = ((Entry<K, V>) o).getKey();
			} else if (irt == IteratorReturnType.Key) {
				k = (K) o;
			} else if (irt == IteratorReturnType.Value) {
				k = hasKeyExtractor ? //
						((K) (keyExtractor.apply((V) o)))//
						: ((K) o);
			}
			return k;
		}

		@SuppressWarnings("unchecked")
		protected E getElementFromNode(NodeAVL n) {
			E e;
			e = null;
			if (irt == IteratorReturnType.Entry) {
				e = (E) n;
			} else if (irt == IteratorReturnType.Key) {
				e = (E) n.k;
			} else if (irt == IteratorReturnType.Value) { e = (E) n.v; }
			return e;
		}

		/*
		 * @SuppressWarnings("unchecked") protected E getElementFromObject(Object key) {
		 * E k; k = null; if (irt == IteratorReturnType.Entry) { k = ((Entry<K, V>)
		 * o).getKey(); } else if (irt == IteratorReturnType.Key) { k = (K) o; } else if
		 * (irt == IteratorReturnType.Value) { k = hasKeyExtractor ? ((K)
		 * (keyExtractor.apply((V) o))) : ((K) o); } return k; }
		 */
		@SuppressWarnings("unchecked")
		public void forEach(ForEachMode iterMode, Consumer<? super E> action) {
//			this.getBackTree().
			MapTreeAVLLightweight.this.forEach(iterMode,
					(entry) -> { action.accept((E) irt.extract((NodeAVL) entry)); });
		}

		@SuppressWarnings("unchecked")
		@Override
		public Iterator<E> iterator() {
			if (irt == IteratorReturnType.Entry)
				return (Iterator<E>) getBackTree().iterator();
			else if (irt == IteratorReturnType.Key)
				return (Iterator<E>) getBackTree().iteratorKey();
			else if (irt == IteratorReturnType.Value)
				return (Iterator<E>) getBackTree().iteratorValue();
			else
				return null;
		}
	}

	protected class QueueEntryWrapper<E> extends WrapperCollection<E> implements Queue<E> {
		private static final long serialVersionUID = 245354852919L;

		protected QueueEntryWrapper(IteratorReturnType irt) { super(irt); }

		protected QueueEntryWrapper(IteratorReturnType irt, Function<V, K> keyExtractor) { super(irt, keyExtractor); }
//		protected final TreeAVL treeBack;

		// simple delegate-implementations

		@Override
		public void clear() { MapTreeAVLLightweight.this.clear(); }

		@Override
		public int size() { return MapTreeAVLLightweight.this.size(); }

		@Override
		public boolean isEmpty() { return MapTreeAVLLightweight.this.isEmpty(); }

		@Override
		public boolean remove(Object key) {
			NodeAVL n;
			n = getNode(getKeyFromObject(key));
			if (n != NIL) {
				MapTreeAVLLightweight.this.delete(n);
				return true; // n!=NIL;
			}
			return false;
		}

		@Override
		public boolean add(E e) { return offer(e); }

		public Entry<K, V> getAt(int i) { return MapTreeAVLLightweight.this.getAt(i); }

		@Override
		public Object[] toArray() { return MapTreeAVLLightweight.this.toArray(); }

		@Override
		public String toString() { return MapTreeAVLLightweight.this.toString(); }

		@Override
		public boolean contains(Object o) { return MapTreeAVLLightweight.this.containsKey(getKeyFromObject(o)); }

		// other implementations

		@SuppressWarnings("unchecked")
		@Override
		public boolean offer(E e) {
			// Entry
			if (e == null)
				return false;
			if (irt == IteratorReturnType.Entry) {
				Entry<K, V> en;
				en = (Entry<K, V>) e;
				put(en.getKey(), en.getValue());
			} else if (irt == IteratorReturnType.Key) {
				put((K) e, (V) e);
			} else if (irt == IteratorReturnType.Value && hasKeyExtractor) {
				V v;
				v = (V) e;
				put(keyExtractor.apply(v), v);
			} else
				return false;
			return true;
		}

		@Override
		public E remove() {
			if (isEmpty())
				throw new NoSuchElementException("Queue empty");
			return poll();
		}

		@Override
		public E poll() {
			NodeAVL n;
			if (isEmpty())
				return null;
			n = (NodeAVL) getLastInserted();
			MapTreeAVLLightweight.this.delete(n);
			return getElementFromNode(n);
		}

		@Override
		public E element() {
			if (isEmpty())
				throw new NoSuchElementException("Queue empty");
			return peek();
		}

		@Override
		public E peek() { return isEmpty() ? null : getElementFromNode((NodeAVL) getFirstInserted()); }

		public V delete(K k) { return MapTreeAVLLightweight.this.delete(k); }

		@Override
		public void forEach(Consumer<? super E> action) { super.forEach(ForEachMode.Queue, action); }

		@Override
		public <T> T[] toArray(T[] a) { return MapTreeAVLLightweight.this.toArray(a); }

		@Override
		public boolean containsAll(Collection<?> c) {
			throw new UnsupportedOperationException("Too lazy to implement");
		}

		@Override
		public boolean addAll(Collection<? extends E> c) {
			throw new UnsupportedOperationException("Too lazy to implement");
		}

		@Override
		public boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException("Too lazy to implement"); }

		@Override
		public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException("Too lazy to implement"); }

	}

	protected class ListWrapper<E> extends WrapperCollection<E> implements List<E> {
		private static final long serialVersionUID = 982856912381000L;

		protected ListWrapper(IteratorReturnType irt, Function<V, K> keyExtractor) { super(irt, keyExtractor); }

		protected ListWrapper(IteratorReturnType irt) { super(irt); }

		// simple delegate-implementations

		@Override
		public void clear() { MapTreeAVLLightweight.this.clear(); }

		@Override
		public int size() { return MapTreeAVLLightweight.this.size(); }

		@Override
		public boolean isEmpty() { return MapTreeAVLLightweight.this.isEmpty(); }

		@Override
		public void forEach(Consumer<? super E> action) { super.forEach(ForEachMode.SortedGrowing, action); }

		@Override
		public boolean remove(Object key) {
			NodeAVL n;
			n = getNode(getKeyFromObject(key));
			if (n != NIL && n != null) {
				MapTreeAVLLightweight.this.delete(n);
				return true; // n!=NIL;
			}
			return false;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean add(E e) {
			switch (irt) {
			case Entry:
				Entry<K, V> en;
				if (e == null)
					return false;
				en = (Entry<K, V>) e;
				put(en.getKey(), en.getValue());
				break;
			case Key:
				put((K) e, (V) e);
				break;
			case Value:
				if (this.hasKeyExtractor) {
					V v;
					v = (V) e;
					put(keyExtractor.apply(v), v);
				}
				break;
			default:
				return false;
			}
			return true;
		}

		@Override
		public E get(int i) { return getElementFromNode((NodeAVL) MapTreeAVLLightweight.this.getAt(i)); }

		@Override
		public Object[] toArray() { return MapTreeAVLLightweight.this.toArray(); }

		@Override
		public String toString() { return MapTreeAVLLightweight.this.toString(); }

		@Override
		public boolean contains(Object o) { return MapTreeAVLLightweight.this.containsKey(getKeyFromObject(o)); }

		@Override
		public <T> T[] toArray(T[] a) { return MapTreeAVLLightweight.this.toArray(a); }

		@Override
		public boolean containsAll(Collection<?> c) {
			throw new UnsupportedOperationException("Too lazy to implement");
		}

		@Override
		public boolean addAll(Collection<? extends E> c) {
			boolean[] flag;
			final int prevSize;
			List<E> thisList;
			thisList = this;
			prevSize = size();
			flag = new boolean[] { false };
			c.forEach(o -> {
				thisList.add(o);
				flag[0] |= prevSize != thisList.size();
			});
			return flag[0];
		}

		@Override
		public boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException("Too lazy to implement"); }

		@Override
		public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException("Too lazy to implement"); }

		@Override
		public E set(int index, E element) {
			throw new UnsupportedOperationException("Cannot use it directly. Use remove(element) and add(element)");
		}

		@Override
		public void add(int index, E element) {
			throw new UnsupportedOperationException("Cannot use it directly. Use add(element)");
		}

		@Override
		public E remove(int index) {
			NodeAVL n;
			n = (NodeAVL) MapTreeAVLLightweight.this.getAt(index);
			MapTreeAVLLightweight.this.delete(n);
			return getElementFromNode(n);
		}

		/**
		 * The parameter is required to be a instance of {@link Entry} ot the same type
		 * of this {@link List}.
		 * <p>
		 * {@inheritDoc}
		 */
		@Override
		public int indexOf(Object o) {
			NodeAVL n;
			K k;
			try {
				k = getKeyFromObject(o);
				n = getNode(k);
				return (n != NIL && n != null) ? n.index() : 0;
			} catch (ClassCastException cce) {
				return -1;
			}
		}

		@Override
		public int lastIndexOf(Object o) { return 0; }

		@SuppressWarnings("unchecked")
		@Override
		public ListIterator<E> listIterator() {
			if (irt == IteratorReturnType.Entry)
				return (ListIterator<E>) MapTreeAVLLightweight.this.iterator();
			else if (irt == IteratorReturnType.Key)
				return null;// (ListIterator<E>) MapTreeAVL.this.iterator(); // TODO WILL FAIL
			else if (irt == IteratorReturnType.Value)
				return null; // (ListIterator<E>) MapTreeAVL.this.iteratorValue();
			return null;
		}

		@Override
		public ListIterator<E> listIterator(int index) {
			ListIterator<E> iter;
			if (index < 0 || index >= size)
				throw new IndexOutOfBoundsException("Index: " + index);
			iter = listIterator();
			while (--index >= 0)
				iter.next();
			return iter;
		}

		@Override
		public List<E> subList(int fromIndex, int toIndex) {
			throw new UnsupportedOperationException("Too lazy to implement");
		}

		@Override
		public boolean addAll(int index, Collection<? extends E> c) {
			throw new UnsupportedOperationException("Too lazy to implement");
		}
	}

	public abstract class SortedSetWrapper<E> implements SortedSetEnhanced<E>, TreeAVLDelegator<K, V> {

		protected SortedSetWrapper() { super(); }

		@Override
		public MapTreeAVLLightweight<K, V> getBackTree() { return MapTreeAVLLightweight.this; }

		@Override
		public void clear() { MapTreeAVLLightweight.this.clear(); }

		@Override
		public int size() { return MapTreeAVLLightweight.this.size(); }

		@Override
		public boolean isEmpty() { return MapTreeAVLLightweight.this.isEmpty(); }

		@Override
		public String toString() { return MapTreeAVLLightweight.this.toString(); }

		@Override
		public boolean contains(Object o) { return MapTreeAVLLightweight.this.containsKey(o); }

		@Override
		public Object[] toArray() { return MapTreeAVLLightweight.this.toArray(); }

		@Override
		public <T> T[] toArray(T[] a) { return MapTreeAVLLightweight.this.toArray(a); }

		@Override
		public boolean containsAll(Collection<?> c) { return MapTreeAVLLightweight.this.containsAll(c); }

		@Override
		public boolean addAll(Collection<? extends E> c) {
			if (c == null || c == this || c.isEmpty())
				return false;
			c.forEach(key -> add(key));
			return true;
		}

		@Override
		public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException("Too lazy to implement"); }

		@Override
		public boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException("Too lazy to implement"); }

		@Override
		public SortedSet<E> subSet(E fromElement, E toElement) {
			throw new UnsupportedOperationException("Operation forbidden");
		}

		@Override
		public SortedSet<E> headSet(E toElement) { throw new UnsupportedOperationException("Operation forbidden"); }

		@Override
		public SortedSet<E> tailSet(E fromElement) { throw new UnsupportedOperationException("Operation forbidden"); }
	}

	protected class SortedSetKeyWrapper extends SortedSetWrapper<K> {
		protected SortedSetKeyWrapper() { super(); }

		@Override
		public Comparator<K> getKeyComparator() { return MapTreeAVLLightweight.this.comp; }

		@Override
		public ClosestMatch<K> closestMatchOf(K key) {
			ClosestMatch<Entry<K, V>> cm = MapTreeAVLLightweight.this.closestMatchOf(key);
			return cm.isExactMatch()
					? new ClosestMatch<K>(key, this.getKeyComparator(), cm.nearestLowerOrExact.getKey())
					: new ClosestMatch<K>(key, this.getKeyComparator(), cm.nearestLowerOrExact.getKey(),
							cm.nearestUpper.getKey());
		}

		@Override
		public Comparator<? super K> comparator() { return MapTreeAVLLightweight.this.comp; }

		@SuppressWarnings("unchecked")
		@Override
		public boolean add(K e) {
			V v = null;
			try {
				v = (V) e;
			} catch (Exception exc) { /* nothing to do here */
			}
			MapTreeAVLLightweight.this.put(e, v);
			return false;
		}

		@Override
		public boolean contains(Object o) { return MapTreeAVLLightweight.this.containsKey(o); }

		@SuppressWarnings("unchecked")
		@Override
		public boolean remove(Object key) {
			NodeAVL n;
			n = getNode((K) key);
			if (n != NIL) {
				MapTreeAVLLightweight.this.delete(n);
				return true; // n!=NIL;
			}
			return false;
		}

		@Override
		public void forEach(Consumer<? super K> action) {
			MapTreeAVLLightweight.this.forEach(e -> action.accept(e.getKey()));
		}

		@Override
		public void forEachSimilar(K key, Comparator<K> keyComp, Consumer<K> action) {
			MapTreeAVLLightweight.this.forEachSimilar(key, keyComp, e -> action.accept(e.getKey()));
		}

		@Override
		public Iterator<K> iterator() { return MapTreeAVLLightweight.this.iteratorKey(); }

		@Override
		public K first() { return MapTreeAVLLightweight.this.firstKey(); }

		@Override
		public K last() { return MapTreeAVLLightweight.this.lastKey(); }

		/**
		 * Returns one element, if this set is not empty, chosen in a way dependent to
		 * its base implementation. In case no usefull way could be used, it just use
		 * the iterator.
		 */
		public K pickOne() { return isEmpty() ? null : first(); }
	}

	protected class SortedSetEntryWrapper extends SortedSetWrapper<Entry<K, V>> {
		// implements SortedSet<K> {

		protected SortedSetEntryWrapper() {
			super();

		}

		@Override
		public Comparator<Entry<K, V>> getKeyComparator() { return MapTreeAVLLightweight.this.compEntry; }

		@Override
		public Comparator<? super Entry<K, V>> comparator() { return MapTreeAVLLightweight.this.compEntry; }

		@Override
		public boolean add(Entry<K, V> e) {
			MapTreeAVLLightweight.this.put(e.getKey(), e.getValue());
			return false;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean contains(Object o) { return MapTreeAVLLightweight.this.containsKey(((Entry<K, V>) o).getKey()); }

		@SuppressWarnings("unchecked")
		@Override
		public boolean remove(Object key) {
			NodeAVL n;
			n = getNode(((Entry<K, V>) key).getKey());
			if (n != NIL) {
				MapTreeAVLLightweight.this.delete(n);
				return true; // n!=NIL;
			}
			return false;
		}

		@Override
		public void forEach(Consumer<? super Entry<K, V>> action) { MapTreeAVLLightweight.this.forEach(action); }

		@Override
		public void forEachSimilar(Entry<K, V> key, Comparator<Entry<K, V>> keyComp, Consumer<Entry<K, V>> action) {
			MapTreeAVLLightweight.this.forEachSimilar(key.getKey(), //
//					MapTreeAVLLightweight.this.com
					((k1, k2) -> {
						return keyComp.compare(MapTreeAVLLightweight.this.getNode(k1),
								MapTreeAVLLightweight.this.getNode(k2));
					}), action);
		}

		@Override
		public ClosestMatch<Entry<K, V>> closestMatchOf(Entry<K, V> key) {
			return MapTreeAVLLightweight.this.closestMatchOf(key.getKey());
		}

		@Override
		public Iterator<Entry<K, V>> iterator() { return MapTreeAVLLightweight.this.iterator(); }

		@Override
		public Entry<K, V> first() { return MapTreeAVLLightweight.this.peekMinimum(); }

		@Override
		public Entry<K, V> last() { return MapTreeAVLLightweight.this.peekMaximum(); }
	}

	protected class SortedSetValueWrapper extends SortedSetWrapper<V> {
		// implements SortedSet<K> {

		/**
		 * Cannot be efficient as other set implementations (like those returned by
		 * {@link MapTreeAVL#keySet()} and {@link MapTreeAVL#entrySet()}) due to the
		 * lack of efficiency caused by "value-based" search (opposite of "key-based" ).
		 * <p>
		 * {@inheritDoc}
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void forEachSimilar(V key, Comparator<V> keyComp, Consumer<V> action) {
			Consumer<Entry<K, V>> entryConsumer;
			if (root == NIL)
				return;
			entryConsumer = e -> { if (keyComp.compare(key, e.getValue()) == 0) { action.accept(e.getValue()); } };
			try {
				MapTreeAVLLightweight.this.forEachSimilar((K) key, entryConsumer);
			} catch (ClassCastException cce) {
				MapTreeAVLLightweight.this.forEach(entryConsumer);
			}
		}

		protected SortedSetValueWrapper(Function<V, K> keyExtractor) {
			super();
			this.keyExtractor = keyExtractor;
			compValue = (v1, v2) -> {
				K k1, k2;
				if (v1 == v2)
					return 0;
				if (v1 == null)
					return -1;
				if (v2 == null)
					return 1;
				k1 = this.keyExtractor.apply(v1);
				k2 = this.keyExtractor.apply(v2);
				if (k1 == k2)
					return 0;
				if (k1 == null)
					return -1;
				if (k2 == null)
					return 1;
				return MapTreeAVLLightweight.this.comparator().compare(k1, k2);
			};
		}

		protected final Function<V, K> keyExtractor;
		protected final Comparator<V> compValue;

		@Override
		public Comparator<V> getKeyComparator() {
			throw new UnsupportedOperationException("Cannot compare value class's insntaces.");
		}

		@Override
		public ClosestMatch<V> closestMatchOf(V key) {
			throw new UnsupportedOperationException("Cannot compare value class's insntaces.");
		}

		@Override
		public Comparator<? super V> comparator() { return this.compValue; }

		@Override
		public boolean add(V e) {
			MapTreeAVLLightweight.this.put(this.keyExtractor.apply(e), e);
			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean contains(Object o) {
			return MapTreeAVLLightweight.this.containsKey(this.keyExtractor.apply((V) o));
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean remove(Object key) {
			NodeAVL n;
			n = getNode(this.keyExtractor.apply((V) key));
			if (n != NIL) {
				MapTreeAVLLightweight.this.delete(n);
				return true; // n!=NIL;
			}
			return false;
		}

		@Override
		public void forEach(Consumer<? super V> action) {
			MapTreeAVLLightweight.this.forEach(e -> action.accept(e.getValue()));
		}

		@Override
		public Iterator<V> iterator() { return MapTreeAVLLightweight.this.iteratorValue(); }

		@Override
		public V first() { return MapTreeAVLLightweight.this.peekMinimum().getValue(); }

		@Override
		public V last() { return MapTreeAVLLightweight.this.peekMaximum().getValue(); }

	}

	// TODO submaps

//	protected class AbstractSubMap implements SortedMap<K, V>, TreeAVLDelegator<K, V> {
//
//		@Override
//		public MapTreeAVLLightweight<K, V> getBackTree() { return MapTreeAVLLightweight.this; }
//
//		@Override
//		public int size() {
//			int s;
//			Iterator<Entry<K, V>> iter;
//			s = 0;
//			iter = this.iterator();
//			while (iter.hasNext()) {
//				s++;
//				iter.next();
//			}
//			return s;
//		}
//
//		@Override
//		public void clear() { MapTreeAVLLightweight.this.clear(); }
//
//		@Override
//		public boolean isEmpty() { return this.size() == 0; }

//	protected class TailMap extends AbstractSubMap {	}

	//

//	protected class StreamTreeAVL<T> implements Stream<Entry<K, V>> {
//		protected StreamTreeAVL(Iterator<Entry<K, V>> iter) {
//			this();
//			this.iter = (iter != null) ? iter : iterator();
//		}
//
//		protected StreamTreeAVL() {
//			super();
//			opened = true;
//		}
//
//		protected StreamTreeAVL(StreamTreeAVL<? extends Object> toBeCloned) {
//			this();
//			this.iter = toBeCloned.iter;
//			this.closeHandler = toBeCloned.closeHandler;
//			this.filter = toBeCloned.filter;
//			this.mapper = toBeCloned.mapper;
//		}
//
//		boolean opened;
//		Iterator<Entry<K, V>> iter;
//		Runnable closeHandler;
//		Predicate<? extends Object> filter;
//		Function<Entry<K, V>, ? extends Object> mapper;
//
//		//
//
//		public StreamTreeAVL<T> setOpened(boolean opened) {
//			this.opened = opened;
//			return this;
//		}
//
//		public StreamTreeAVL<T> setIter(Iterator<Entry<K, V>> iter) {
//			this.iter = iter;
//			return this;
//		}
//
//		public StreamTreeAVL<T> setCloseHandler(Runnable closeHandler) {
//			this.closeHandler = closeHandler;
//			return this;
//		}
//
//		public StreamTreeAVL<T> setFilter(Predicate<? extends Object> filter) {
//			this.filter = filter;
//			return this;
//		}
//
//		public <R> StreamTreeAVL<T> setMapper(Function<Entry<K, V>, ? extends R> mapper) {
//			this.mapper = mapper;
//			return this;
//		}
//
//		protected StreamTreeAVL<T> cloneProtected() {
//			return new StreamTreeAVL<T>(this);
//		}
//
//		//
//
//		@Override
//		public Iterator<Entry<K, V>> iterator() {
//			return iterator();
//		}
//
//		@Override
//		public Spliterator<Entry<K, V>> spliterator() {
//			throw new UnsupportedOperationException("Not implemented on original tree AVL.");
//		}
//
//		/**
//		 * See the {@link UnsupportedOperationException} thrown by {@link #parallel()},
//		 * having message: {@code "Not enough theory to implement it"}
//		 */
//		@Override
//		public boolean isParallel() {
//			return false;
//		}
//
//		@Override
//		public Stream<Entry<K, V>> sequential() {
//			return this;
//		}
//
//		@Override
//		public Stream<Entry<K, V>> parallel() {
//			throw new UnsupportedOperationException("Not enough theory to implement it");
//		}
//
//		@Override
//		public Stream<Entry<K, V>> unordered() {
////			return cloneProtected().setIter(iteratorEntryQueueOrder());
//			throw new UnsupportedOperationException("TODO DA FAREEEE");
//		}
//
//		@Override
//		public Stream<Entry<K, V>> onClose(Runnable closeHandler) {
//			return cloneProtected().setCloseHandler(closeHandler);
//		}
//
//		@Override
//		public Stream<Entry<K, V>> filter(Predicate<? super Entry<K, V>> predicate) {
//			return cloneProtected().setFilter(predicate);
//		}
//
//		@SuppressWarnings({ "resource", "unchecked" })
//		@Override
//		public <R> Stream<R> map(Function<? super Entry<K, V>, ? extends R> mapper) {
//			return (Stream<R>) (new StreamTreeAVL<R>(this)).setMapper((Function<Entry<K, V>, ? extends R>) mapper);
//		}
//
//		/**
//		 * May throw {@link NullPointerException}.
//		 * <p>
//		 * {@inheritDoc}
//		 */
//		@Override
//		public void close() {
//			if (opened) {
//				opened = false;
//				this.closeHandler.run();
//			}
//		}
//
//		@Override
//		public IntStream mapToInt(ToIntFunction<? super Entry<K, V>> mapper) {
//			return null;
//		}
//
//		@Override
//		public LongStream mapToLong(ToLongFunction<? super Entry<K, V>> mapper) {
//			return null;
//		}
//
//		@Override
//		public DoubleStream mapToDouble(ToDoubleFunction<? super Entry<K, V>> mapper) {
//			return null;
//		}
//
//		@Override
//		public <R> Stream<R> flatMap(Function<? super Entry<K, V>, ? extends Stream<? extends R>> mapper) {
//			return null;
//		}
//
//		@Override
//		public IntStream flatMapToInt(Function<? super Entry<K, V>, ? extends IntStream> mapper) {
//			return null;
//		}
//
//		@Override
//		public LongStream flatMapToLong(Function<? super Entry<K, V>, ? extends LongStream> mapper) {
//			return null;
//		}
//
//		@Override
//		public DoubleStream flatMapToDouble(Function<? super Entry<K, V>, ? extends DoubleStream> mapper) {
//			return null;
//		}
//
//		@Override
//		public Stream<Entry<K, V>> distinct() {
//			return null;
//		}
//
//		@Override
//		public Stream<Entry<K, V>> sorted() {
//			return null;
//		}
//
//		@Override
//		public Stream<Entry<K, V>> sorted(Comparator<? super Entry<K, V>> comparator) {
//			return null;
//		}
//
//		@Override
//		public Stream<Entry<K, V>> peek(Consumer<? super Entry<K, V>> action) {
//			return null;
//		}
//
//		@Override
//		public Stream<Entry<K, V>> limit(long maxSize) {
//			return null;
//		}
//
//		@Override
//		public Stream<Entry<K, V>> skip(long n) {
//			return null;
//		}
//
//		@Override
//		public void forEach(Consumer<? super Entry<K, V>> action) {
//
//		}
//
//		@Override
//		public void forEachOrdered(Consumer<? super Entry<K, V>> action) {
//
//		}
//
//		@Override
//		public Object[] toArray() {
//			return null;
//		}
//
//		@Override
//		public <A> A[] toArray(IntFunction<A[]> generator) {
//			return null;
//		}
//
//		@Override
//		public Entry<K, V> reduce(Entry<K, V> identity, BinaryOperator<Entry<K, V>> accumulator) {
//			return null;
//		}
//
//		@Override
//		public Optional<Entry<K, V>> reduce(BinaryOperator<Entry<K, V>> accumulator) {
//			return null;
//		}
//
//		@Override
//		public <U> U reduce(U identity, BiFunction<U, ? super Entry<K, V>, U> accumulator, BinaryOperator<U> combiner) {
//			return null;
//		}
//
//		@Override
//		public Optional<Entry<K, V>> min(Comparator<? super Entry<K, V>> comparator) {
//			return null;
//		}
//
//		@Override
//		public Optional<Entry<K, V>> max(Comparator<? super Entry<K, V>> comparator) {
//			return null;
//		}
//
//		@Override
//		public long count() {
//			return 0;
//		}
//
//		@Override
//		public boolean anyMatch(Predicate<? super Entry<K, V>> predicate) {
//			return false;
//		}
//
//		@Override
//		public boolean allMatch(Predicate<? super Entry<K, V>> predicate) {
//			return false;
//		}
//
//		@Override
//		public boolean noneMatch(Predicate<? super Entry<K, V>> predicate) {
//			return false;
//		}
//
//		@Override
//		public Optional<Entry<K, V>> findFirst() {
//			return null;
//		}
//
//		@Override
//		public Optional<Entry<K, V>> findAny() {
//			return null;
//		}
//
//		@Override
//		public <R, A> R collect(Collector<? super Entry<K, V>, A, R> collector) {
//			return null;
//		}
//
//		@Override
//		public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super Entry<K, V>> accumulator,
//				BiConsumer<R, R> combiner) {
//			return null;
//		}
//	}

	protected static interface IndexEntryConsumer<Kk, Vv> {
		public void accept(int index, Entry<Kk, Vv> t);
	}

	protected class ForEacherEntry implements Consumer<Entry<K, V>> {
		int i;
		final IndexEntryConsumer<K, V> iec;

		ForEacherEntry(final IndexEntryConsumer<K, V> iec) {
			this.i = 0;
			this.iec = iec;
		}

		@Override
		public void accept(Entry<K, V> t) { iec.accept(i++, t); }
	}

	protected class MySimpleStack {
		protected MySimpleStack() { this.top = null; }

		protected NodeStack top;

		//

		public void clear() { this.top = null; }

		public boolean isEmpty() { return this.top == null; }

		public void push(NodeAVL n) { this.top = new NodeStack(this.top, n); }

		public NodeAVL pop() {
			NodeAVL n;
			if (this.top == null)
				return null;
			n = this.top.cargo;
			this.top = this.top.previous;
			return n;
		}

		protected class NodeStack {
			NodeStack(NodeStack previous, NodeAVL cargo) {
				this.previous = previous;
				this.cargo = cargo;
			}

			protected NodeStack previous;
			protected NodeAVL cargo;
		}
	}

	public static void main(String[] args) {
		int i, len;
		Integer x;
		MapTreeAVLLightweight<Integer, Integer> t;
		MapTreeAVLLightweight<Integer, Integer>.NodeAVL n;

		System.out.println("MapTreeAVL");
		t = new MapTreeAVLLightweight<>(Integer::compareTo);
		len = 10;
		i = -1;
		while (++i < len) {
			x = i;
			t.put(x, x);
		}
		System.out.println(t.toString());

		System.out.println("\niterate ...");
		n = (MapTreeAVLLightweight<Integer, Integer>.NodeAVL) t.getAt(0);
		System.out.println("getting " + n.getKey() + " as start \n");

		i = -1;
		while (++i < len) {
			n = t.successorSorted(n);
			System.out.println("...." + n.getKey());
		}

		System.out.println("\n now predecessor:");
		n = (MapTreeAVLLightweight<Integer, Integer>.NodeAVL) t.getAt(len - 1);
		System.out.println("getting " + n.getKey() + " as end \n");

		i = -1;
		while (++i < len) {
			n = t.predecessorSorted(n);
			System.out.println("...." + n.getKey());
		}

		System.out.println("\n IT WORKS !!!\n\n now iterator");

		System.out.println(t);

		for (Entry<Integer, Integer> e : t) {
			System.out.println("iterating: " + e.getKey());
			if (e.getKey() == null) {
				System.err.println("WTF e.getKey() null");
				break;
			}
		}
		System.out.println("\n END: IT WORKS !!!");

		System.out.println("\n END");
	}
}