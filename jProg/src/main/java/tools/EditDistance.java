package tools;

import java.util.Comparator;
import java.util.List;

import dataStructures.CollectionAlteringCosts;

public interface EditDistance {
	public static interface EqualityChecker<T> {
		public boolean isEqual(T e1, T e2);

		public static <K> EqualityChecker<K> fromComparator(Comparator<K> comp) {
			return (e1, e2) -> comp.compare(e1, e2) == 0;
		}
	}

	public default <T> int editDistance(T[] firstSequence, T[] secondSequence, EqualityChecker<T> equalityChecker) {
		return editDistance(firstSequence, secondSequence, equalityChecker, CollectionAlteringCosts.newDefaultCAC());
	}

//	/**
//	 * If the access is of the given {@link List} does NOT offer good <i>random access</i> performances, then
//	 * {@link #editDistance(Object[], Object[], EqualityChecker)} should be used
//	 * instead.
//	 */
	public default <T> int editDistance(List<T> firstSequence, List<T> secondSequence,
			EqualityChecker<T> equalityChecker) {
		return editDistance(IterableSized.from(firstSequence), IterableSized.from(secondSequence), equalityChecker);
	}

	public default <T> int editDistance(IterableSized<T> firstSequence, IterableSized<T> secondSequence,
			EqualityChecker<T> equalityChecker) {
		return editDistance(firstSequence, secondSequence, equalityChecker, CollectionAlteringCosts.newDefaultCAC());
	}

	//

	public <T> int editDistance(T[] firstSequence, T[] secondSequence, EqualityChecker<T> equalityChecker,
			CollectionAlteringCosts<T> cac);

	public <T> int editDistance(IterableSized<T> firstSequence, IterableSized<T> secondSequence,
			EqualityChecker<T> equalityChecker, CollectionAlteringCosts<T> cac);
}