package tools;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface IterableSized<T> extends Iterable<T> {
	public int size();

	public default int getSize() { return this.size(); }

	//

	public static <K> IterableSized<K> from(Collection<K> l) { return new CollectionDelegator<>(l); }

	public static <K> IterableSized<K> from(K[] a) { return from(Arrays.asList(a)); }

	//

	//

	//

	public static class CollectionDelegator<K> implements IterableSized<K>, Collection<K> {
		protected final Collection<K> delegator;

		public CollectionDelegator(Collection<K> delegator) {
			super();
			this.delegator = delegator;
		}

		@Override
		public void forEach(Consumer<? super K> action) { delegator.forEach(action); }

		@Override
		public int size() { return delegator.size(); }

		@Override
		public boolean isEmpty() { return delegator.isEmpty(); }

		@Override
		public boolean contains(Object o) { return delegator.contains(o); }

		@Override
		public Iterator<K> iterator() { return delegator.iterator(); }

		@Override
		public Object[] toArray() { return delegator.toArray(); }

		@Override
		public <T> T[] toArray(T[] a) { return delegator.toArray(a); }

		@Override
		public boolean add(K e) { return delegator.add(e); }

		@Override
		public boolean remove(Object o) { return delegator.remove(o); }

		@Override
		public boolean containsAll(Collection<?> c) { return delegator.containsAll(c); }

		@Override
		public boolean addAll(Collection<? extends K> c) { return delegator.addAll(c); }

		@Override
		public boolean removeAll(Collection<?> c) { return delegator.removeAll(c); }

		@Override
		public <T> T[] toArray(IntFunction<T[]> generator) { return delegator.toArray(generator); }

		@Override
		public boolean retainAll(Collection<?> c) { return delegator.retainAll(c); }

		@Override
		public void clear() { delegator.clear(); }

		@Override
		public boolean equals(Object o) { return delegator.equals(o); }

		@Override
		public int hashCode() { return delegator.hashCode(); }

		@Override
		public boolean removeIf(Predicate<? super K> filter) { return delegator.removeIf(filter); }

		@Override
		public Spliterator<K> spliterator() { return delegator.spliterator(); }

		@Override
		public Stream<K> stream() { return delegator.stream(); }

		@Override
		public Stream<K> parallelStream() { return delegator.parallelStream(); }

		// public boolean addAll(int index, Collection<? extends K> c) { return
		// delegator.addAll(index, c); }
//		public void replaceAll(UnaryOperator<K> operator) { delegator.replaceAll(operator); }
//		public void sort(Comparator<? super K> c) { delegator.sort(c); }
//		public K get(int index) { return delegator.get(index); }
//		public K set(int index, K element) { return delegator.set(index, element); }
//		public void add(int index, K element) { delegator.add(index, element); }
//		public K remove(int index) { return delegator.remove(index); }
//		public int indexOf(Object o) { return delegator.indexOf(o); }
//		public int lastIndexOf(Object o) { return delegator.lastIndexOf(o); }
//		public ListIterator<K> listIterator() { return delegator.listIterator(); }
//		public ListIterator<K> listIterator(int index) { return delegator.listIterator(index); }
//		public List<K> subList(int fromIndex, int toIndex) { return delegator.subList(fromIndex, toIndex); }
	}
}