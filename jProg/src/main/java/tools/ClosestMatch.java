package tools;

import java.io.Serializable;
import java.util.Comparator;

import dataStructures.MapTreeAVL;

/**
 * See {@link MapTreeAVL#closestMatchOf(Object)}. If the match is exact (i.e.
 * this map hold an entry with the given key), then {@link #isExactMatch()} will
 * return <code>true</code> (and {@link #nearestUpper} will be
 * <code>null</code>).
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

	public K getClosetsMatch(CloserGetter<K> closerGetter) {
		K closerKey, keyLowerExact;
		if (nearestLowerOrExact == null)
			return nearestUpper;
		if (nearestUpper == null)
			return nearestLowerOrExact;
		keyLowerExact = nearestLowerOrExact;// .getKey();
		closerKey = closerGetter.getCloserTo(originalValue, keyLowerExact, nearestUpper);
		return (closerKey == keyLowerExact) ? nearestLowerOrExact : nearestUpper;
	}
}