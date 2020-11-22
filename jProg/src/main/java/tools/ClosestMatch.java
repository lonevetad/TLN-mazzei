package tools;

import java.io.Serializable;
import java.util.Comparator;
import java.util.function.Function;

import dataStructures.MapTreeAVL;

/**
 * See {@link MapTreeAVL#closestMatchOf(Object)}. If the match is exact (i.e.
 * this map hold an entry with the given key), then {@link #isExactMatch()} will
 * return <code>true</code> (and {@link #nearestUpper} will be
 * <code>null</code>).<br>
 * See {@link #getClosetsMatchToOriginal(CloserGetter)} to retrieve the best
 * result.
 */
public class ClosestMatch<K> implements Serializable {
	private static final long serialVersionUID = -15432L;

	public ClosestMatch(K originalValue, Comparator<K> keyComparator, K nearestLowerOrExact) {
		this(originalValue, keyComparator, nearestLowerOrExact, null, true);
	}

	public ClosestMatch(K originalValue, Comparator<K> keyComparator, K nearestLowerOrExact, K nearestUpper) {
		this(originalValue, keyComparator, nearestLowerOrExact, nearestUpper, false
//				(nearestLowerOrExact == null || nearestUpper == null)
		);
	}

	protected ClosestMatch(K originalValue, Comparator<K> keyComparator, K nearestLowerOrExact, K nearestUpper,
			boolean isExact) {
		super();
		this.isExact = isExact;
		this.originalValue = originalValue;
		this.keyComparator = keyComparator;
		this.nearestLowerOrExact = nearestLowerOrExact;
		this.nearestUpper = nearestUpper;
	}

	protected final boolean isExact;
	protected final K originalValue;
	public final K nearestLowerOrExact, nearestUpper;
	public final Comparator<K> keyComparator; // MapTreeAVL<K mapFromWhichIsComputed; //still not visible to
												// "user"

	/**
	 * Returns if this query's result has found an exact match or just an esteem. An
	 * exact match or a closest lower value are stored in the same "place",
	 * {@link #nearestLowerOrExact} (better to be obtained through
	 * {@link #getAvailableMatchLowerFirst()}), and this method helps to check out
	 * what that situation is.
	 */
	public boolean isExactMatch() { return this.isExact; }

	public boolean hasLowerBound() { return this.nearestLowerOrExact != null; }

	public boolean hasUpperBound() { return this.nearestUpper != null; }

	/** Returns the exact match if any, or <code>null</code> otherwise. */
	public K exactOrNull() {
		return isExact ? (nearestLowerOrExact != null ? nearestLowerOrExact : nearestUpper) : null;
	}

	public K getAvailableMatchLowerFirst() { return nearestLowerOrExact != null ? nearestLowerOrExact : nearestUpper; }

	public K getAvailableMatchUpperFirst() { return nearestUpper != null ? nearestUpper : nearestLowerOrExact; }

	//

	/**
	 * A closest lower value and an closest upper value could be found thanks to
	 * some algorithm like {@link MapTreeAVL#closestMatchOf(Object)} (or, an exact
	 * match, sees {@link #isExactMatch()}). Those values are just approximations
	 * (or an exact match, as early said). This method is designed to get the
	 * closest value to the "query's original one".
	 */
	public K getClosetsMatchToOriginal(CloserGetter<K> closerGetter) {
		K closerKey;
		if (nearestLowerOrExact == null)
			return nearestUpper;
		if (nearestUpper == null || isExact)
			return nearestLowerOrExact;
//		keyLowerExact = nearestLowerOrExact;// .getKey();
		closerKey = closerGetter.getCloserTo(originalValue, nearestLowerOrExact, nearestUpper);
		return (closerKey == nearestLowerOrExact) ? nearestLowerOrExact : nearestUpper;
	}

	public <T> ClosestMatch<T> convertTo(Comparator<T> newKeyComparator, Function<K, T> converter) {
		return new ClosestMatch<T>(converter.apply(originalValue), newKeyComparator, //
				converter.apply(nearestLowerOrExact), //
				converter.apply(nearestUpper), isExact);
	}
}