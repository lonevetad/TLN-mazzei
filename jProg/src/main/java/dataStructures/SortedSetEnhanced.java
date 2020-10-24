package dataStructures;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

import tools.ClosestMatch;
import translators.secondWay.TransferTranslationRuleBased;

public interface SortedSetEnhanced<E> extends SortedSet<E> {

	public Comparator<E> getKeyComparator();

	public SortedSetEnhanced<E> newSortedSetEnhanced(Comparator<E> comp);

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
		inters = newSortedSetEnhanced(getKeyComparator());
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
//		return intersectionWith(eg).countAlternatives();
		// instead of computing the intersection, just count each "intersecting element"
		SortedSetEnhanced<E> smallerSet;
		SortedSetEnhanced<E> biggerSetMap;
		int[] countIntersections = { 0 };
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
		smallerSet.forEach((s) -> {
			if (biggerSetMap.contains(s))
				countIntersections[0]++;
		});
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

	// TODO COMPARATOR

	//

	//
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
	public static <T> Comparator<SortedSetEnhanced<T>> newComparatorByKeyOrder(Comparator<T> comp) {
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

	/**
	 * Compares the sets and collapse to <code>0</code> both cases of "equals" and
	 * "none of them is a subset".
	 */
	public static <T> Comparator<SortedSetEnhanced<T>> newComparatorSubsetCheckCollapsingElse(Comparator<T> comp) {
		return new ComparatorSynonymBySubset<>();
	}

	public static <T> Comparator<SortedSetEnhanced<T>> newComparatorSubsetFirstKeyorderThen(Comparator<T> comp) {
		return new ComparatorSynonymBySubset<>() {
			@Override
			public int finishCompareOnIntersecting(SortedSetEnhanced<T> s1, SortedSetEnhanced<T> s2) {
				return newComparatorByKeyOrder(comp).compare(s1, s2);
			}
		};
	}

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
	 */
	public static <T> Comparator<SortedSetEnhanced<T>> newComparatorIntersectThenMissThenExceed(Comparator<T> comp) {
		return (s1, s2) -> {
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
			} else if (c == c2)
				return c1 - c;
			// no one is equal or a subset: everyone has something that the other has not
//now check the "closest to intersection": which one has fewer elements more to the inters.
			c1 -= c;
			c2 -= c;
			if (c1 == c2) // tie: just compare elements
				return newComparatorByKeyOrder(comp).compare(s1, s2);
			else
				return c2 - c1; // (c1 < c2) ? 1 : -1;
		};
	}

	//

	//

	public static class ComparatorSynonymBySubset<T> implements Comparator<SortedSetEnhanced<T>> {
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
				return size2 == 0 ? 0 : -1;
			else if (size2 == 0)
				return 1;
			// they are not empty ..
			intersCount = eg1.intersectionSize(eg2);
			/*
			 * System.out.println("eg1 size and set: " + c + " - " + eg1 +
			 * " --- eg2, same: " + size2 + " - " + eg2 + " -----> inters " +
			 * eg1.intersectionWith(eg2));
			 */
			if (intersCount == 0)
				return finishCompareOnIntersecting(eg1, eg2);
			if (c == intersCount)
				return size2 == intersCount ? 0 : -1; // eg2 is equal or superset (eg1 == subset)
			else if (size2 == intersCount)
				return c == intersCount ? 0 : 1; // eg1 is equal or superset (eg2 == subset)
			else
				return finishCompareOnIntersecting(eg1, eg2);
		}

		/** Override designed */
		public int finishCompareOnIntersecting(SortedSetEnhanced<T> eg1, SortedSetEnhanced<T> eg2) {
			// System.out.println("zerooos");
			return 0; // just a simple intersection
		}
	}
}