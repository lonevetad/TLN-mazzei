package tools;

import java.io.File;
import java.util.Comparator;

public class Misc {
	public static final String RESOURCE_PATH = "." + File.separatorChar + "src" + File.separatorChar + "main"
			+ File.separatorChar + "resources" + File.separatorChar;

	public static final Comparator<String> STRING_COMPARATOR = Comparators.STRING_COMPARATOR;
	public static final Comparator<Integer> INTEGER_COMPARATOR = Comparators.INTEGER_COMPARATOR;
}