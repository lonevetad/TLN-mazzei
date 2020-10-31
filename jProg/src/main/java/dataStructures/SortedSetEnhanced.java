package dataStructures;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

import grammars.transfer.TransferTranslationRuleBased;
import tools.CloserGetter;
import tools.ClosestMatch;
import tools.DifferenceCalculator;

public interface SortedSetEnhanced<E> extends SortedSet<E> {

	//

	// instance stuff

	//

	public Comparator<E> getKeyComparator();

	public SortedSetEnhanced<E> newSortedSetEnhanced(Comparator<E> comp);

	public default SortedSetEnhanced<E> newSortedSetEnhanced() { return this.newSortedSetEnhanced(getKeyComparator()); }

	// add from synonym

	/**
	 * Computes if there are at least one "alternatives" in common (i.e., those
	 * ElementGrammarWithAlternatives are applicable in the context of
	 * {@link TransferTranslationRuleBased}).
	 */
	public default boolean areIntersecting(SortedSetEnhanced<E> eg) {
		int s1, s2;
		Comparator<E> co = getKeyComparator();
		if (eg == this)
			return true;
		if (eg == null ||
		// se uno è empty -> return false
				((s1 = this.size()) > 0) != (((s2 = eg.size()) > 0)))
			return false;
		// since the sets are sorted .. check extremes
		if (co.compare(this.last(), eg.first()) < 0 || co.compare(eg.last(), this.first()) < 0) { return false; }
		// basically, compute an intersection.. if they intersects -> true
		if (s1 > s2) {// the tiniest check over the "less than linear" bigger
			for (E s : this) {
				if (eg.contains(s))
					return true;
			}
		} else {
			for (E s : eg) {
				if (this.contains(s))
					return true;
			}
		}
		return false;
	}

	/**
	 * Count how many items on this set are contained inside the given one, i.e.
	 * invokes {@link #intersectionSize(SortedSetEnhanced<E>)} (this method is just
	 * a synonym).
	 */
	public default int countIntersectionWith(SortedSetEnhanced<E> eg) { return intersectionSize(eg); }

	public default SortedSetEnhanced<E> intersectionWith(SortedSetEnhanced<E> eg) {
		SortedSetEnhanced<E> smallerSet, inters;
		SortedSetEnhanced<E> biggerSetMap;
		if (eg == null)
			return null;
		if (eg == this)
			return this;
		inters = newSortedSetEnhanced();
		/*
		 * since iterating is O(n) and "containsKey" is O(log(n)), iterates over the
		 * smallest set
		 */
		if (this.size() <= eg.size()) {
			smallerSet = this;
			biggerSetMap = eg;
		} else {
			smallerSet = eg;
			biggerSetMap = this;
//			eg=this;
		}
		smallerSet.forEach((elem) -> {
			if (biggerSetMap.contains(elem))
				inters.add(elem);
		});
		return inters;
	}

	/** Computes the intersection with the given set and returns its size. */
	public default int intersectionSize(SortedSetEnhanced<E> eg) {
		int sThis, sEg;
//		return intersectionWith(eg).countAlternatives();
		// instead of computing the intersection, just count each "intersecting element"
		SortedSetEnhanced<E> smallerSet;
		SortedSetEnhanced<E> biggerSetMap;
		int[] countIntersections = { 0 };
		/*
		 * since iterating is O(n) and "containsKey" is O(log(n)), iterates over the
		 * smallest set
		 */
		sThis = this.size();
		sEg = eg.size();
		if (sThis == 0) {
			return sEg == 0 ? 0 : -sEg; // could be directly -sEg, but i'm afraid of "negative zero" ambiguity
		} else if (sEg == 0) {
			return sThis; // the other one is empty ..
		}
		if (sThis <= sEg) {
			smallerSet = this;
			biggerSetMap = eg;
		} else {
			smallerSet = eg;
			biggerSetMap = this;
		}
		smallerSet.forEach((s) -> { if (biggerSetMap.contains(s)) { countIntersections[0]++; } });
		return countIntersections[0];
	}

	/**
	 * Test if this set is subset of the given one.<br>
	 * No null pointer checks performed.
	 */
	public default boolean isSubsetOf(SortedSetEnhanced<E> eg) {
		int thisSize;
		if ((thisSize = size()) == 0)
			return true;
		if (thisSize > eg.size())
			return false;
		return thisSize == intersectionSize(eg);
	}

	/**
	 * Return how many items are NOT present in the given set, i.e. the subtraction
	 * of this set's cardinality and the cardinality of the intersection.
	 */
	public default int countDifferenceWith(SortedSetEnhanced<E> anotherKey) {
		return this.size() - this.intersectionSize(anotherKey);
	}

	/**
	 * Search if exists an entry with a compatible key (i.e., the comparator
	 * provided upon instantiation and {@link #getComparator()}) or, in absence, a
	 * pair of the closest entries: the closer lower entry and the closer upper
	 * entry. <br>
	 * It returns an instance of {@link ClosestMatch}. If the match is exact, then
	 * the returned instance's method {@link ClosestMatch#isExactMatch()} will
	 * return <code>true</code>
	 */
	public ClosestMatch<E> closestMatchOf(E key);

	//

	//

	// TODO STATIC STUFF

	//

	//

	// TODO METHODS

	public static final ComparatorSSEFactory COMPARATOR_FACTORY_PREFERRED = //
			ComparatorFactoriesSSE.KEY_ORDER; // SUBSET_THEN_NON_SHARED_KEYS;

	public static <T> DifferenceCalculator<SortedSetEnhanced<T>> newDifferenceCalc(Comparator<T> keyComparator) {
		return differenceCalcFromSetComparator(COMPARATOR_FACTORY_PREFERRED.newComparator(keyComparator));
	}

	public static <T> DifferenceCalculator<SortedSetEnhanced<T>> differenceCalcFromSetComparator(
			Comparator<SortedSetEnhanced<T>> sortedSetComparator) {
		return DifferenceCalculator.from(sortedSetComparator);
	}

	public static <T> CloserGetter<SortedSetEnhanced<T>> newDefaultCloseGetter(Comparator<T> keyComparator) {
		return newDefaultCloseGetter(keyComparator, //
				newDifferenceCalc(keyComparator));
	}

	/**
	 * Given a comparator of keys and a way to compute of sets .
	 */
	public static <T> CloserGetter<SortedSetEnhanced<T>> newDefaultCloseGetter(Comparator<T> keyComparator, //
			DifferenceCalculator<SortedSetEnhanced<T>> setOfKeysDifferenceCalc//
	) {
		/*
		 * (the last one is required to return values that quantifies how much a set is
		 * different from another one)
		 */
//			Comparator<SortedSetEnhanced<T>> setOfKeysComparator //
		return (orig, o1, o2) -> {
			int c1, c2;
			/*
			 * The comparator does not simply returns a value belonging to {-1; 0; 1}, but a
			 * value representing the real difference from "equal set". So, the difference
			 * form a set and another one can be computed this way.
			 */
//			c1 = setOfKeysComparator.compare(orig, o1);
//			c2 = setOfKeysComparator.compare(orig, o2);
			c1 = (int) setOfKeysDifferenceCalc.getDifference(orig, o1);
			c2 = (int) setOfKeysDifferenceCalc.getDifference(orig, o2);
			/*
			 * turn them to negative: the interval of negative integers covered by 32-bit
			 * integer is greater (the double, more or less) than the positive one. (Turning
			 * to positive a negative value lesser than -(2^31) will result in a wrong
			 * number.)
			 */
			if (c1 > 0) { c1 = -c1; }
			if (c2 > 0) { c2 = -c2; }
			return c1 < c2 ? o2 : o1;
		};
	}

	// TODO CLASS

	public static interface ComparatorSSEFactory extends Serializable {
		public <T> Comparator<SortedSetEnhanced<T>> newComparator(Comparator<T> comp);
	}

	public static enum ComparatorFactoriesSSE implements ComparatorSSEFactory {
		/**
		 * Preferred, since the subset-comparison
		 * {@link #SYNONYM_COMPARATOR_SUBSET_FIRST_SEQUENCE_THEN} (that resolves the
		 * case of "none of those two sets is subset of the other") is slower and
		 * redundant: it computes the intersection and, in case of "non-subset", it
		 * invokes this comparator, that travels through the set looking for the first,
		 * decisive, pair of items to determine the comparison. That travel could be
		 * optimized, but the intersection requires to scan the whole smaller set, so
		 * that optimization could not benefit in an way.
		 */
		KEY_ORDER(ComparatorFactoriesSSE::newComparatorByKeyOrder), //

		/**
		 * Compares the sets and collapse to <code>0</code> both cases of "equals" and
		 * "none of them is a subset".
		 * 
		 * @deprecated May violates transitivity
		 * @see {@link #KEY_ORDER} should be used instead for <b>comparison</b> and see
		 *      {@link #SUBSET_THEN_NON_SHARED_KEYS} to understand more about the
		 *      violation.
		 */
		@Deprecated
		SUBSET_ORDER_COLLAPSE_ID_AND_NONSUBSET(ComparatorSynonymBySubset::new), //

		/**
		 * * @deprecated May violates transitivity
		 * 
		 * @see {@link #KEY_ORDER} should be used instead for <b>comparison</b> and see
		 *      {@link #SUBSET_THEN_NON_SHARED_KEYS} to understand more about the
		 *      violation.
		 */
		@Deprecated
		SUBSET_FIRST_KEY_THEN(new ComparatorSSEFactory() {
			private static final long serialVersionUID = -65013455L;

			@Override
			public <T> Comparator<SortedSetEnhanced<T>> newComparator(Comparator<T> c) {
				return new ComparatorSynonymBySubset<>(c) {
					final Comparator<SortedSetEnhanced<T>> copByKey = newComparatorByKeyOrder(comp);

					@Override
					public int finishCompareOnIntersecting(SortedSetEnhanced<T> s1, SortedSetEnhanced<T> s2) {
						return copByKey.compare(s1, s2);
					}
				};
			}
		}), //

		/**
		 * More general version than {@link SU} because this one gives precedence on
		 * subset relation and how close are the arguments, comparing keys only in case
		 * of a tie.<br>
		 * In fact, the order of comparing the synonyms is:
		 * <ol>
		 * <li>The size of their intersection: it gives one of the fundamental
		 * informations:
		 * <ul>
		 * <li><i> are them the same </i></li>
		 * <li>or, <i>which one, if any, is the subset of which other ones (and tells
		 * how many more items are in the superset)</i></li>
		 * </ul>
		 * </li>
		 * <li>in case of <i>not in sub-set relation</i>, then for both given set, count
		 * how many elements are <b>not</b> present in the intersection computed before.
		 * If that amount is not equal, then returns the <code>1</code> if the smallest
		 * is the first set, <code>-1</code> if it's the second set.</li>
		 * <li>If none of them gives useful informations (so, they fails), then compare
		 * the items in a sorted way: this will determine if the first set is "the
		 * smaller". (at least a non-shared item exist, since no one is a subset of the
		 * other, as checked in step <code>1)</code>).</li>
		 * <li></li>
		 * </ol>
		 * 
		 * @deprecated May violates transitivity
		 * @see {@link #KEY_ORDER} should be used instead for <b>comparison</b> and see
		 *      {@link #SUBSET_THEN_NON_SHARED_KEYS} to understand more about the
		 *      violation.
		 */
		@Deprecated
		CASCADE_OF_INTERSECT_MISS_EXCEED_KEY(new ComparatorSSEFactory() {
			private static final long serialVersionUID = -65013456L;

			@Override
			public <T> Comparator<SortedSetEnhanced<T>> newComparator(Comparator<T> comp) {
				return new Comparator<SortedSetEnhanced<T>>() {
					final Comparator<SortedSetEnhanced<T>> compByKeyOrder = //
//							KEY_ORDER.newComparator(comp);
							newComparatorByKeyOrder(comp);

					@Override
					public int compare(SortedSetEnhanced<T> s1, SortedSetEnhanced<T> s2) {
						int c, c1, c2;
						if (s1 == s2)
							return 0;
						if (s1 == null)
							return -1;
						if (s2 == null)
							return 1;
						c1 = s1.size();
						c2 = s2.size();
						c = s1.intersectionSize(s2);
						if (c == c1) {
							return (c == c2) ? 0 : c - c2;
							// it should be faster, in case of "0", than simply returning "c-c2"
						} else if (c == c2)
							return c1 - c;
						/*
						 * no one is equal or a subset: everyone has something that the other has not
						 * now check the "closest to intersection": which one has fewer elements more to
						 * the intersection.
						 */
						c1 -= c;
						c2 -= c;
						if (c1 == c2) // tie: just compare elements
							return compByKeyOrder.compare(s1, s2);
						else
							return c1 - c2; // (c1 < c2) ? -1 : 1;
					}
				};
			}
		}),

		//

		/**
		 * More stable and reliable than
		 * {@link #CASCADE_OF_INTERSECT_MISS_EXCEED_KEY}.<br>
		 * Still, violating the transitivity:
		 * <p>
		 * Examples of trees violating the transitivity (node's children are wrapped in
		 * brackets):
		 * <ol>
		 * <li><code>1 {0 5 7}</code></li>
		 * <li><code>1 {4 8}</code></li>
		 * <li><code>1 {5 7}</code></li>
		 * </ol>
		 * The first tree is greater than the last one since it's a superset, but it's
		 * lower than the second since no one is superset of the other and the lowest
		 * key of the first tree (i.e. <code>0</code>) is lower than the lowest key of
		 * the second tree (i.e. <code>4</code>). For similar reason, the second tree is
		 * lower than the third.<br>
		 * This creates a circularity, while a {@link Comparator} requires transitivity
		 * and non-circularity.
		 * 
		 * @deprecated May violates transitivity
		 * @see {@link #KEY_ORDER} should be used instead for <b>comparison</b>.
		 */
		@Deprecated
		SUBSET_THEN_NON_SHARED_KEYS(new ComparatorSSEFactory() {
			private static final long serialVersionUID = -65013456L;

			@Override
			public <T> Comparator<SortedSetEnhanced<T>> newComparator(Comparator<T> comp) {
				return new Comparator<SortedSetEnhanced<T>>() {
					final Comparator<T> compKey = comp;

					@Override
					public int compare(SortedSetEnhanced<T> s1, SortedSetEnhanced<T> s2) {
						int c, c1, c2;
						SortedSetEnhanced<T> intersect;
						Iterator<T> i1, i2;
						T t1, t2;
						if (s1 == s2)
							return 0;
						if (s1 == null)
							return -1;
						if (s2 == null)
							return 1;
						c1 = s1.size();
						c2 = s2.size();
//							c = s1.intersectionSize(s2);
						intersect = s1.intersectionWith(s2);
						c = intersect.size();
						if (c == c1) {
							return (c == c2) ? 0 : c - c2;
							// it should be faster, in case of "0", than simply returning "c-c2"
						} else if (c == c2) { return c1 - c; }
//							/*
//							 * no one is equal or a subset: everyone has something that the other has not
//							 * now check the "closest to intersection": which one has fewer elements more to
//							 * the intersection.
//							 */
						c1 -= c;
						c2 -= c;
//							if (c1 == c2) // tie: just compare elements
//								return compByKeyOrder.compare(s1, s2);
//							else
//								return c1 - c2; // (c1 < c2) ? -1 : 1;

						// iterate over the items not in intersection
//						intersect.iterator();
						i1 = s1.iterator();
						i2 = s2.iterator();
						t1 = t2 = null;
						do {
							// pick the first non-shared element from s1
							while (i1.hasNext() && (intersect.contains(t1 = i1.next()))) {
								t1 = null; // nullify this one and move to next, if any
							}
							// same on s2
							while (i2.hasNext() && (intersect.contains(t2 = i2.next()))) {
								t2 = null; // nullify this one and move to next, if any
							}
						} while (((c = compKey.compare(t1, t2)) == 0) && i1.hasNext() && i2.hasNext());
						return c != 0 ? c : (c1 - c2);
					}
				};
			}
		});

		public final ComparatorSSEFactory factoryDelegate;

		private ComparatorFactoriesSSE(ComparatorSSEFactory factoryDelegate) { this.factoryDelegate = factoryDelegate; }

		@Override
		public <T> Comparator<SortedSetEnhanced<T>> newComparator(Comparator<T> comp) {
			return this.factoryDelegate.newComparator(comp);
		}

		// static stuff

		protected static <T> Comparator<SortedSetEnhanced<T>> newComparatorByKeyOrder(Comparator<T> comp) {
			return (s1, s2) -> {
				int c, size1, size2;
				Iterator<T> i1, i2;
				if (s1 == s2)
					return 0;
				if (s1 == null)
					return -1;
				if (s2 == null)
					return 1;
				// smallest
				size1 = s1.size();
				size2 = s2.size();
				if (size1 == 0) {
					return size2 == 0 ? 0 : -size2;
				} else if (size2 == 0) { return size1; }
				/*
				 * since synonyms are sortable and this set is sorted, then compare the elements
				 * in order
				 */
				i1 = s1.iterator();
				i2 = s2.iterator();
				c = 0;
				while (i1.hasNext() && i2.hasNext() && //
				((c = comp.compare(i1.next(), i2.next())) == 0))
					;
				return c != 0 ? c : (size1 >= size2 ? size1 - size2 : size2 - size1);
			};
		}
	} // END ENUM

	//

	//

	public static class ComparatorSynonymBySubset<T> implements Comparator<SortedSetEnhanced<T>> {
		final Comparator<T> comp;

		public ComparatorSynonymBySubset(Comparator<T> comp) {
			super();
			this.comp = comp;
		}

		@Override
		public int compare(SortedSetEnhanced<T> eg1, SortedSetEnhanced<T> eg2) {
			int c, size2, intersCount;
			if (eg1 == eg2)
				return 0;
			if (eg1 == null)
				return -1;
			if (eg2 == null)
				return 1;
			/*
			 * then, it depends on "subset" relation: the superset is the greatest (collapse
			 * "identity" and "non-subset & non-empty-intersection" onto the same category)
			 */
			c = eg1.size();
			size2 = eg2.size();
			if (c == 0)
				return size2 == 0 ? 0 : -size2;
			else if (size2 == 0)
				return c;
			// they are not empty ..
			intersCount = eg1.intersectionSize(eg2);
			if (intersCount == 0)
				return finishCompareOnIntersecting(eg1, eg2);
			if (c == intersCount)
				// eg2 is equal or superset (eg1 == subset) ... if superset -> negative value
				return size2 == intersCount ? 0 : intersCount - size2;
			else if (size2 == intersCount)
				return c - intersCount; // eg1 cannot be equal: it's a superset (eg2 == subset) .. it's a positive value
			else
				return finishCompareOnIntersecting(eg1, eg2);
		}

		/** Override designed */
		public int finishCompareOnIntersecting(SortedSetEnhanced<T> eg1, SortedSetEnhanced<T> eg2) {
			return 0; // just a simple intersection
		}
	}
}