package tools;

import java.util.Comparator;

public class Misc {
	public static final Comparator<String> STRING_COMPARATOR = (s1, s2) -> {
		if (s1 == s2)
			return 0;
		if (s1 == null)
			return -1;
		if (s2 == null)
			return 1;
		return s1.compareTo(s2);
	};
}