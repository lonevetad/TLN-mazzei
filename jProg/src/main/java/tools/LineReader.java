package tools;

import java.util.Iterator;
import java.util.function.Consumer;

public interface LineReader {
	public default void forEach(Consumer<String> action) {
		Iterator<String> iter;
		iter = this.iterator();
		while (iter.hasNext())
			action.accept(iter.next());
	}

	public Iterator<String> iterator();
}