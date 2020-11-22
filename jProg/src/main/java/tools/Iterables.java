package tools;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Iterables {

	// INTERFACES

	public static interface ForEacher<T> {
		public void forEach(Consumer<T> action);
	}

	//

	// ACTUAL METHODS

	//

	public static <T> ForEacher<T> forEach(Collection<T> c) { return a -> c.forEach(a); }

	public static <T> ForEacher<T> forEach(T[] array) {
		return a -> {
			int i, s;
			s = array.length;
			i = -1;
			while (++i < s) {
				a.accept(array[i]);
			}
		};
	}

	public static <T> ForEacher<T> forEach(Iterator<T> i) {
		return a -> {
			while (i.hasNext())
				a.accept(i.next());
		};
	}

	public static <T> ForEacher<T> forEach(Stream<T> s) { return a -> s.forEach(a); }

	public static <T> ForEacher<T> forEach(Iterable<T> s) { return a -> s.forEach(a); }

	public static <K, V> ForEacher<K> forEach(Map<K, V> m) { return a -> m.forEach((k, v) -> a.accept(k)); }

	//

	public static <T> Iterator<T> iterator(Collection<T> c) { return c.iterator(); }

	public static <T> Iterator<T> iterator(T[] array) { return Arrays.asList(array).iterator(); }

	public static <T> Iterator<T> iterator(Stream<T> s) { return s.iterator(); }

	public static <T> Iterator<T> iterator(Iterable<T> s) { return s.iterator(); }

	public static <K, V> Iterator<K> iterator(Map<K, V> c) { return c.keySet().iterator(); }

	//

	public static <T> IterableSized<T> of(Collection<T> c) { return IterableSized.from(c); }

	public static <T> IterableSized<T> of(T[] array) { return IterableSized.from(array); }

	public static <T> IterableSized<T> of(Stream<T> c) { return IterableSized.from(c.collect(Collectors.toList())); }

	public static <K, V> IterableSized<K> of(Map<K, V> c) { return IterableSized.from(c.keySet()); }
}