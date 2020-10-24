package tools;

import java.util.Comparator;
import java.util.SortedSet;

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
}