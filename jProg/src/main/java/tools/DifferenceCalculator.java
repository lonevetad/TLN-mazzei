package tools;

import java.io.Serializable;
import java.util.Comparator;

public interface DifferenceCalculator<K> extends Serializable {
	/**
	 * Compute a numerical difference over two values. A negative value could have
	 * the same meaning of {@link Comparator}.
	 */
	public long getDifference(K from, K to);

	public static <T> DifferenceCalculator<T> from(Comparator<T> comp) {
		return (e1, e2) -> {
			if (e1 == e2)
				return 0;
			if (e1 == null || e2 == null)
				return Integer.MAX_VALUE;
			return comp.compare(e1, e2);
		};
	}
}