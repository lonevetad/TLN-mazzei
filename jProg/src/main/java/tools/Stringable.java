package tools;

import java.io.Serializable;

public interface Stringable extends Serializable {

	public default void toString(StringBuilder sb, int tabLevel) {
		addTab(sb, tabLevel);
		sb.append(this.toString());
	}

	public default void toString(StringBuilder sb) { this.toString(sb, 0); }

	public default void addTab(StringBuilder sb) { addTab(sb, 0); }

	public default void addTab(StringBuilder sb, int tabLevel) { addTab(sb, tabLevel, true); }

	public default void addTab(StringBuilder sb, int tabLevel, boolean newLineNeeded) {
		if (sb != null) {
			if (newLineNeeded)
				sb.append('\n');
			sb.ensureCapacity(sb.length() + tabLevel);
			while (tabLevel-- > 0) {
				sb.append('\t');
			}
		}
	}
}