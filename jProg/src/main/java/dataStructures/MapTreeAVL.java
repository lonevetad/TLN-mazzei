package dataStructures;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import dataStructures.mtAvl.MapTreeAVLFull;
import dataStructures.mtAvl.MapTreeAVLLightweight;
import dataStructures.mtAvl.MapTreeAVLMinIter;
import dataStructures.mtAvl.MapTreeAVLQueuable;
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
public interface MapTreeAVL<K, V> extends Serializable, SortedMap<K, V>, Function<K, V>, Iterable<Entry<K, V>> {

	public static interface EntryDepthConsumer<Kk, Vv> {
		public void accept(Entry<Kk, Vv> entry, int depth);
	}

	public static interface MapTreeAVLFactory {
		public default <Key, Val> MapTreeAVL<Key, Val> newMap(Comparator<Key> comp) {
			return newMap(DEFAULT_BEHAVIOUR, comp);
		}

		public <Key, Val> MapTreeAVL<Key, Val> newMap(BehaviourOnKeyCollision b, Comparator<Key> comp);
	}

	/***/
	public static final boolean NORMAL_DIRECTION_WALKING = true;
	/**
	 * The default value of {@link BehaviourOnKeyCollision}:
	 * {@link BehaviourOnKeyCollision#Replace}.
	 */
	public static final BehaviourOnKeyCollision DEFAULT_BEHAVIOUR = BehaviourOnKeyCollision.Replace;
	/**
	 * The default value of {@link Optimizations}:
	 * {@link Optimizations#FullButHeavyNodes}.
	 */
	public static final Optimizations DEFAULT_OPTIMIZATION = Optimizations.MinMaxIndexIteration;

	// ENUMS

	/**
	 * Specify the action to perform if the key is yet present.<br>
	 * Currently, two values are designed:
	 * <ul>
	 * <li>KeepPrevious: keep the previous pair key-value, ignoring the given one,
	 * if the given key in comparatively equal.</li>
	 * <li>Replace: forgot and replace the old key-value inserted</li>
	 * <li>AddItsNotASet: put the pair, no matters. WARNING: USE AT YOUR OWN RISK:
	 * this option make the map unpredictable if more entries are added and forbids
	 * to perform {@link MapTreeAVL#keySet()}.</li>
	 * </ul>
	 */
	public static enum BehaviourOnKeyCollision {
		KeepPrevious, Replace,
		/**
		 * Peculiar behavior that means that pairs of key and value with the same key
		 * are added to this map. <br>
		 * (The newly added pair is put onto the left of the yet existing key[s], so
		 * they are considered "lower". That keeps an inverse chronological order.)
		 */
		AddItsNotASet/* it's senseless */
	}

	/**
	 * Specify how to iterate over this map on methods like
	 * {@link MapTreeAVL #forEach(ForEachMode, BiConsumer)} (this is the most common
	 * version).<br>
	 * Pairs of key and values could be provided:
	 * <ul>
	 * <li>in a sorted order (sorted through the key, as would be natural in a
	 * common Binary Search Tree)</li>
	 * <li>ordered through the <i>insertion time</i>: this map could be used as a
	 * {@link java.util.Queue} or a {@link java.util.Stack}</li>
	 * <li>in "breadth first" order, useful for retrieving all element to be stored
	 * (like in a file) end re-added in a further moment optimizing the time
	 * (because the structure won't need to re-balance itself through rotating
	 * internal subtrees).</li>
	 * </ul>
	 */
	public static enum ForEachMode {
		SortedGrowing, SortedDecreasing, Queue, Stack, BreadthGrowing, BreadthDecreasing
	}

	/**
	 * This huge class can alter its behavior to tune some aspects.<br>
	 * Set its behavior choosing from one of this states.
	 * <p>
	 * The default value is {@link MapTreeAVLFactory#DEFAULT_OPTIMIZATION}.
	 */
	public static enum Optimizations implements MapTreeAVLFactory {

		/**
		 * A simple TreeMap, without additional informations to nodes: less memory (RAM)
		 * usage, less heavy.<br>
		 * - WARNING: random access and iterations are NOT optimized, just the memory
		 * usage and add/remove operations are a bit optimized!
		 */
		Lightweight(new MapTreeAVLFactory() {
			@Override
			public <Key, Val> MapTreeAVL<Key, Val> newMap(BehaviourOnKeyCollision b, Comparator<Key> comp) {
				return new MapTreeAVLLightweight<Key, Val>(b, comp);
			}
		}),
		/**
		 * Guarantee:
		 * <ul>
		 * <li>Can be used as a {@link Set} of keys.</li>
		 * <li><i>O(1)</i> time for {@link MapTreeAVL#peekMinimum()} and
		 * {@link MapTreeAVL#peekMaximum()} instead of <i>O(log(n))</i>.</li>
		 * <li><i>O(log(n))</i> time for {@link MapTreeAVL#removeMinimum()},
		 * {@link MapTreeAVL#removeMaximum()} and random access through an index, like
		 * {@link MapTreeAVL#get(int)}. The last ones has been enhanced since it was
		 * <i>O(n)</i>.</li>
		 * <li><i>O(n)</i> iteration through all key-value pairs instead of
		 * <i>O(n*log(n))</i>.</li>
		 * </ul>
		 */
		MinMaxIndexIteration(new MapTreeAVLFactory() {
			@Override
			public <Key, Val> MapTreeAVL<Key, Val> newMap(BehaviourOnKeyCollision b, Comparator<Key> comp) {
				return new MapTreeAVLMinIter<Key, Val>(b, comp);
			}
		}),
		/**
		 * This tree map can be used to create a {@link Queue}, but has not the features
		 * provided by {@link #MinMaxIndexIteration}, except for the iteration: it's
		 * still <i>O(n)</i>, but follows the "FIFO" ordering.<br>
		 * To make this map behave as a {@link Stack}, just call
		 * {@link MapTreeAVL#iteratorBackward()} instead of
		 * {@link MapTreeAVL#iterator()} and call
		 * {@link MapTreeAVL#forEach(ForEachMode, Consumer)} passing
		 * {@link ForEachMode#Stack} instead of {@link ForEachMode#Queue}
		 */
		QueueFIFOIteration(new MapTreeAVLFactory() {
			@Override
			public <Key, Val> MapTreeAVL<Key, Val> newMap(BehaviourOnKeyCollision b, Comparator<Key> comp) {
				return new MapTreeAVLQueuable<Key, Val>(b, comp);
			}
		}),

		/**
		 * All features of {@link #MinMaxIndexIteration} and {@link #QueueFIFOIteration}
		 * together, but at the price of more memory usage due to the additional
		 * informations stored.
		 */
		FullButHeavyNodes(new MapTreeAVLFactory() {
			@Override
			public <Key, Val> MapTreeAVL<Key, Val> newMap(BehaviourOnKeyCollision b, Comparator<Key> comp) {
				return new MapTreeAVLFull<Key, Val>(b, comp);
			}
		});

		//

		//
		Optimizations(MapTreeAVLFactory delegator) { this.delegator = delegator; }

		final MapTreeAVLFactory delegator;

		@Override
		public <Key, Val> MapTreeAVL<Key, Val> newMap(BehaviourOnKeyCollision b, Comparator<Key> comp) {
			return delegator.newMap(b, comp);
		}

	}

	public static <K, V> MapTreeAVL<K, V> newMap(Comparator<K> comp) throws IllegalArgumentException {
		return newMap(DEFAULT_OPTIMIZATION, DEFAULT_BEHAVIOUR, comp);
	}

	public static <K, V> MapTreeAVL<K, V> newMap(BehaviourOnKeyCollision b, Comparator<K> comp)
			throws IllegalArgumentException {
		return newMap(DEFAULT_OPTIMIZATION, b, comp);
	}

	public static <K, V> MapTreeAVL<K, V> newMap(Optimizations optimization, Comparator<K> comp)
			throws IllegalArgumentException {
		return newMap(optimization, DEFAULT_BEHAVIOUR, comp);
	}

	public static <K, V> MapTreeAVL<K, V> newMap(Optimizations optimization, BehaviourOnKeyCollision b,
			Comparator<K> comp) throws IllegalArgumentException {
		if (comp == null)
			throw new IllegalArgumentException("Comparator cannot be null");
		if (optimization == null)
			optimization = DEFAULT_OPTIMIZATION;
		if (b == null)
			b = DEFAULT_BEHAVIOUR;
		return optimization.newMap(b, comp);
	}

	//

	//

	public interface ExtracterValueFromNodeByIRT {
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

	public Comparator<K> getComparator();

	public Comparator<Entry<K, V>> getEntryComparator();

	@Override
	public Comparator<? super K> comparator();

	public V delete(K k);

	/**
	 * Get the pair of key-value having the given index, depending on this
	 * implementation's ordering.<br>
	 * On simpler implementations (like {@link Optimizations#Lightweight} it would
	 * run in <code>O(N*log2(N))</code> while in others could run in
	 * <code>O(log2(N))</code>
	 */
	public Entry<K, V> getAt(int i);

	public default V set(int index, V element) {
		Entry<K, V> e;
		V v;
		e = getAt(index);
		if (e == null)
			return null;
		v = e.getValue();
		e.setValue(element);
		return v;
	}

	public default V removeAt(int index) {
		Entry<K, V> e;
		V v;
		e = getAt(index);
		if (e == null)
			return null;
		v = e.getValue();
		delete(e.getKey());
		return v;
	}

	public int indexOf(Object o);

	// TODO for-each-er and iterators

	/**
	 * Similar to {@link #forEach(java.util.function.Consumer)}, but allow to
	 * iterate over the elements in different ways.<br>
	 * Only one subclass of the sub-package "mtAvl", {@link MapTreeAVLFull},
	 * implements all of those iteration modes, other provided subclasses implements
	 * just a subset of all modes.<br>
	 * If the mode is null, the natural iteration should be provided
	 */
	public void forEach(ForEachMode mode, Consumer<Entry<K, V>> action);

	public void forEachAndDepth(EntryDepthConsumer<K, V> action);

	/**
	 * Invokes {@link #forEachSimilar(Object, Comparator, Consumer)} giving as a
	 * {@link Comparator} the one returned by {@link #getComparator()}.
	 */
	public default void forEachSimilar(K key, Consumer<Entry<K, V>> action) {
		forEachSimilar(key, getComparator(), action);
	}

	/**
	 * Given a key (first parameter) and a optional {@link Comparator} (which can be
	 * the same of the one returned by {@link #getComparator()}), runs an action
	 * ({@link Consumer}) over each {@link Entry} stored which are considered
	 * compatible.<br>
	 * <b>BEWARE</b>: the given Comparator should NOT have a totally different logic
	 * than the one defined by the Comparator provided upon creating this map,
	 * because the behavior of this method could be unpredictable (or just running
	 * on one element, or more, or no one). See the next paragraph.
	 * <p>
	 * Usually, there are two use cases (not mutually exclusive) which this
	 * iteration could run on more than one element:
	 * <ul>
	 * <li>The option {@link BehaviourOnKeyCollision#AddItsNotASet} has been set
	 * upon instantiating this map.</li>
	 * <li>The given Consumer generate a less restrictive partition of the key's
	 * domain than the one provided upon building this map. That means that
	 * theoretically more elements could be considered "equal" under the given
	 * Comparator rather then the one returned by {@link #getComparator()}.<br>
	 * As an example: imagine that a Person is identified by a pair of
	 * {@link String}, i.e. the <i>first name</i> and <i>last name</i>, so that pair
	 * is this map's key. Assume that the map's Comparator compares firstly the
	 * <i>last name</i>s, then the <i>first name</i>. If You provide a Comparator
	 * that compares only the <i>last name</i> then some Persons could be considered
	 * similar: they just need to share <i>last name</i> (like siblings, father and
	 * children, etc). So, under that second Comparator all of "similar" Person will
	 * be consumed by the given {@link Consumer}.</li>
	 * </ul>
	 */
	public void forEachSimilar(K key, Comparator<K> keyComp, Consumer<Entry<K, V>> action);

	/**
	 * Same as {@link #iterator()}, but iterating entries in decreasing order
	 */
	public Iterator<Entry<K, V>> iteratorBackward();

	/**
	 * Same as {@link #iterator()}, but iterating only keys
	 */
	public Iterator<K> iteratorKey();

	/**
	 * Same as {@link #iteratorKey()}, but iterating keys in decreasing order
	 */
	public Iterator<K> iteratorKeyBackward();

	/**
	 * Same as {@link #iterator()}, but iterating only values
	 */
	public Iterator<V> iteratorValue();

	/**
	 * Same as {@link #iteratorValue()}, but iterating values in decreasing order
	 */
	public Iterator<V> iteratorValueBackward();

	/**
	 * Retrieves but not removes the minimum value in this priority.<br>
	 * This method does not alter the collection, rather than
	 * {@link #removeMinimum()}.
	 */
	public Entry<K, V> peekMinimum();

	/**
	 * Retrieves but not removes the maximum value in this priority.<br>
	 * This method does not alter the collection, rather than
	 * {@link #removeMaximum()}.
	 */
	public Entry<K, V> peekMaximum();

	/**
	 * Retrieves AND removes the minimum value in this priority.<br>
	 * This method alters the collection, rather than {@link #peekMinimum()}.
	 */
	public Entry<K, V> removeMinimum();

	/**
	 * Retrieves AND removes the maximum value in this priority.<br>
	 * This method alters the collection, rather than {@link #peekMaximum()}.
	 */
	public Entry<K, V> removeMaximum();

	/** Returns the last pair inserted in chronological order. */
	public Entry<K, V> getLastInserted();

	/** See {@link #getLastInserted()}. */
	public Entry<K, V> getFirstInserted();

	/**
	 * Returns the lowest {@link Entry} that could be the root of the subtree
	 * holding the given pair of keys.<br Special cases:
	 * <ul>
	 * <li>If the tree is empty, <code>null</code> is returned</li>
	 * <li>If the keys are the same (a comparison is performed), then the leaf node
	 * best-matching a possible result is provided</li>
	 * </ul>
	 */
	public Entry<K, V> getLowesCommonAncestor(K k1, K k2);

	// TODO rangeQuery, closestMatch, merge

	/** See {@link #rangeQuery(Object, boolean, Object, boolean)}. */
	public default MapTreeAVL<K, V> rangeQuery(K lowerBound, K upperBound) throws IllegalArgumentException {
		return rangeQuery(lowerBound, true, upperBound, true);
	}

	/**
	 * Returns a subset of this map ("use and discard", i.e. the returned map is NOT
	 * backed by this one!) that includes all elements within the given bounds. <br>
	 * In case the bounds coincides, it returns a map containing the single element
	 * identified by one of those bound's key (if at least one of the boolean
	 * parameters are true) or an empty map. Otherwise, <code>throws</code>
	 * <p>
	 * NOTE: the returned map is NOT backed by this one! It's "use and discard".
	 * 
	 * @throws IllegalArgumentException if the lower bound is greater than upper
	 *                                  bound, or some bound is null
	 */
	public MapTreeAVL<K, V> rangeQuery(K lowerBound, boolean isLowerBoundIncluded, K upperBound,
			boolean isUpperBoundIncluded) throws IllegalArgumentException;

	/**
	 * Search if exists an entry with a compatible key (i.e., the comparator
	 * provided upon instantiation and {@link #getComparator()}) or, in absence, a
	 * pair of the closest entries: the closer lower entry and the closer upper
	 * entry. <br>
	 * It returns an instance of {@link ClosestMatch}. If the match is exact, then
	 * the returned instance's method {@link ClosestMatch#isExactMatch()} will
	 * return <code>true</code>
	 */
	public ClosestMatch<Entry<K, V>> closestMatchOf(K key);

	/**
	 * Merge the smaller tree in the bigger ones. The smaller one will be cleared
	 * after the call.
	 * <P>
	 * Returns the instance of the bigger tree upon success, {@code null} otherwise
	 * (i.e. the given tree is null).
	 */
	public MapTreeAVL<K, V> merge(MapTreeAVL<K, V> tOther);

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
	public Entry<K, V> changeKey(K kOld, K kNew);

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
	public Entry<K, V> changeValue(K key, V vNew);

	public Object[] toArray();

	/**
	 * Convert this map into a back-mapping instance of {@link Queue} of keys.
	 */
	public Queue<K> toQueue();

	/**
	 * As like {@link #toQueue()}, but for {@link Entry} of both keys and values.
	 */
	public Queue<Entry<K, V>> toQueueEntry();

	/**
	 * As like {@link #toQueue()}, but for values.<br>
	 * Since keys are used to map values, a <i>"key extractor"</i> is required to
	 * get the key from a specific value.<br>
	 * e For further details, see this class's documentation: {@link MapTreeAVL}.
	 *
	 * @param keyExtractor way to extract the key from a value. Could simply return
	 *                     its hash or itself.
	 */
	public Queue<V> toQueueValue(Function<V, K> keyExtractor);

	/**
	 * Convert this map into a back-mapping instance of {@link List} of keys.
	 */
	public List<K> toList();

	/**
	 * As like {@link #toList()}, but for {@link Entry} of both keys and values.
	 */
	public List<Entry<K, V>> toListEntry();

	/**
	 * As like {@link #toValues()}, but for values. <br>
	 * See {@link #toQueueValue(Function)}.
	 */
	public List<V> toListValue(Function<V, K> keyExtractor);

	public SortedSetEnhanced<K> toSetKey();

	public SortedSetEnhanced<Entry<K, V>> toSetEntry();

	public SortedSetEnhanced<V> toSetValue(Function<V, K> keyExtractor);

	// ENDCOLLECTION CONVERTITORS

	@Override
	public default void putAll(Map<? extends K, ? extends V> m) {
		MapTreeAVL<K, V> thisMap;
		thisMap = this;
		m.forEach((k, v) -> { thisMap.put(k, v); });
	}

	public default boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!containsKey(o))
				return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public default boolean addAll(Collection<?> c) {
		boolean[] flag;
		final int prevSize;
		MapTreeAVL<K, V> thisList;
		thisList = this;
		prevSize = size();
		flag = new boolean[] { false };
		c.forEach(o -> {
			try {
				Entry<K, V> e;
				e = (Entry<K, V>) o;
				thisList.put(e.getKey(), e.getValue());
			} catch (ClassCastException e1) {
				try {
					thisList.put((K) o, null);
				} catch (ClassCastException e2) {
					try {
						thisList.put(null, (V) o);
					} catch (ClassCastException e3) {
						throw new ClassCastException("Cannot determine and use the class of " + o.getClass());
					}
				}
			}
			flag[0] |= prevSize != thisList.size();
		});
		return flag[0];
	}

	public default boolean retainAll(Collection<?> c) {
		return false; // backMap.reta;
	}

	public default Stream<Entry<K, V>> stream() {
//		return new StreamTreeAVL<Entry<K, V>>();
		throw new UnsupportedOperationException("Operation not implemented yet");
	}

	/**
	 * With some particular inputs, the tree could be nearly unbalanced, making some
	 * operations very heavy, still <code>O(log(n))</code> (where <i>n</i> is the
	 * total size of the tree) but the worst case has huge coefficients. So,
	 * recalculating the root while keeping the balance will enhance the worst case
	 * at the expense of the average, because it sticks to <code>O(log(n))</code>,
	 * by minimizing the maximum height.<br>
	 * This method runs on <code>O(n*log2(n))</code> time. <br>
	 * Three examples making the tree left-tailed (if the elements are added in the
	 * specified order):
	 * <ul>
	 * <li>{ 10, 4, 20, 2, 6, 15, 22, 1, 3, 5, 11, 0 }</li>
	 * <li>{ 99, 10, 140, 4, 20, 120, 160, 2, 6, 15, 22, 110, 103, 150, 1, 3, 5, 11,
	 * 100, 0 }</li>
	 * <li>{ 1000, <br>
	 * 99, 1500, <br>
	 * 10, 140, 1200, 2000, <br>
	 * 4, 20, 110, 160, 1100, 1250, 1700, 2200, <br>
	 * 2, 6, 15, 22, 103, 120, 150, 1050, 1150, 1225, 1600, <br>
	 * 1, 3, 5, 11, 100, 1025, <br>
	 * 0 }</li>
	 * </ul>
	 */
	public void compact();

	//

	//

	//
//
//	/**
//	 * See {@link MapTreeAVL#closestMatchOf(Object)}. If the match is exact (i.e.
//	 * this map hold an entry with the given key), then {@link #isExactMatch()} will
//	 * return <code>true</code> (and {@link #nearestUpper} will be
//	 * <code>null</code>).
//	 */
//	public static class ClosestMatch<Kk, Vv> implements Serializable {
//		private static final long serialVersionUID = -15432L;
//
//		public ClosestMatch(Kk originalValue, Comparator<Kk> keyComparator, Entry<Kk, Vv> nearestLowerOrExact) {
//			this(originalValue, keyComparator, nearestLowerOrExact, null, true);
//		}
//
//		public ClosestMatch(Kk originalValue, Comparator<Kk> keyComparator, Entry<Kk, Vv> nearestLowerOrExact,
//				Entry<Kk, Vv> nearestUpper) {
//			this(originalValue, keyComparator, nearestLowerOrExact, nearestUpper, false
////					(nearestLowerOrExact == null || nearestUpper == null)
//			);
//		}
//
//		protected ClosestMatch(Kk originalValue, Comparator<Kk> keyComparator, Entry<Kk, Vv> nearestLowerOrExact,
//				Entry<Kk, Vv> nearestUpper, boolean isExact) {
//			super();
//			this.isExact = isExact;
//			this.originalValue = originalValue;
//			this.keyComparator = keyComparator;
//			this.nearestLowerOrExact = nearestLowerOrExact;
//			this.nearestUpper = nearestUpper;
//		}
//
//		protected final boolean isExact;
//		protected final Kk originalValue;
//		public final Entry<Kk, Vv> nearestLowerOrExact, nearestUpper;
//		public final Comparator<Kk> keyComparator; // MapTreeAVL<Kk, Vv> mapFromWhichIsComputed; //still not visible to
//													// "user"
//
//		public boolean isExactMatch() { return this.isExact; }
//
//		public boolean hasLowerBound() { return this.nearestLowerOrExact != null; }
//
//		public boolean hasUpperBound() { return this.nearestUpper != null; }
//
//		/** Returns the exact match if any, or <code>null</code> otherwise. */
//		public Entry<Kk, Vv> exactOrNull() {
//			return isExact ? (nearestLowerOrExact != null ? nearestLowerOrExact : nearestUpper) : null;
//		}
//
//		public Entry<Kk, Vv> getAvailableMatchLowerFirst() {
//			return nearestLowerOrExact != null ? nearestLowerOrExact : nearestUpper;
//		}
//
//		public Entry<Kk, Vv> getAvailableMatchUpperFirst() {
//			return nearestUpper != null ? nearestUpper : nearestLowerOrExact;
//		}
//
//		//
//
//		public Entry<Kk, Vv> getClosetsMatch(CloserGetter<Kk> closerGetter) {
//			Kk closerKey, keyLowerExact;
//			if (nearestLowerOrExact == null)
//				return nearestUpper;
//			if (nearestUpper == null)
//				return nearestLowerOrExact;
//			keyLowerExact = nearestLowerOrExact.getKey();
//			closerKey = closerGetter.getCloserTo(originalValue, keyComparator, keyLowerExact, nearestUpper.getKey());
//			return (closerKey == keyLowerExact) ? nearestLowerOrExact : nearestUpper;
//		}
//	}
}