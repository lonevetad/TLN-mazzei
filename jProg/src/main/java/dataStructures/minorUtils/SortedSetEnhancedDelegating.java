package dataStructures.minorUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.function.Consumer;

import dataStructures.SortedSetEnhanced;

public interface SortedSetEnhancedDelegating<E> extends SortedSetEnhanced<E> {

	//
	public SortedSet<E> getDelegator();
	//

	@Override
	public default int size() { return getDelegator().size(); }

	@Override
	public default boolean isEmpty() { return getDelegator().isEmpty(); }

	@Override
	public default boolean contains(Object o) { return getDelegator().contains(o); }

	@Override
	public default Iterator<E> iterator() { return getDelegator().iterator(); }

	@Override
	public default Object[] toArray() { return getDelegator().toArray(); }

	@Override
	public default <T> T[] toArray(T[] a) { return getDelegator().toArray(a); }

	@Override
	public default boolean add(E e) { return getDelegator().add(e); }

	@Override
	public default void clear() { getDelegator().clear(); }

	@Override
	public default void forEachSimilar(E key, Comparator<E> keyComp, Consumer<E> action) {
		SortedSet<E> ss = this.getDelegator();
		if (ss instanceof SortedSetEnhanced<?>) { ((SortedSetEnhanced<E>) ss).forEachSimilar(key, keyComp, action); }
	}

	@Override
	public default boolean remove(Object o) { return getDelegator().remove(o); }

	@Override
	public default boolean containsAll(Collection<?> c) { return getDelegator().containsAll(c); }

	@Override
	public default boolean addAll(Collection<? extends E> c) { return getDelegator().containsAll(c); }

	@Override
	public default boolean retainAll(Collection<?> c) { return getDelegator().retainAll(c); }

	@Override
	public default boolean removeAll(Collection<?> c) { return getDelegator().retainAll(c); }

	@Override
	public default Comparator<? super E> comparator() { return getDelegator().comparator(); }

	@Override
	public default SortedSet<E> subSet(E fromElement, E toElement) {
		return getDelegator().subSet(fromElement, toElement);
	}

	@Override
	public default SortedSet<E> headSet(E toElement) { return getDelegator().headSet(toElement); }

	@Override
	public default SortedSet<E> tailSet(E fromElement) { return getDelegator().tailSet(fromElement); }

	@Override
	public default E first() { return getDelegator().first(); }

	@Override
	public default E last() { return getDelegator().last(); }
}