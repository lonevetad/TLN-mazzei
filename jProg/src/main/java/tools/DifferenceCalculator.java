package tools;

import java.io.Serializable;

public interface DifferenceCalculator<K> extends Serializable {
	public long getDifference(K from, K to);
}