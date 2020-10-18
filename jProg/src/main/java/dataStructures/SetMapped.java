package dataStructures;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Consumer;
import java.util.function.Function;

public class SetMapped<OriginalType, T> implements Set<T> {

	public SetMapped(Set<OriginalType> backSet, Function<OriginalType, T> newTypeExtractor) {
		this.backSet = backSet;
		this.newTypeExtractor = newTypeExtractor;
	}

	protected final Set<OriginalType> backSet;
	protected final Function<OriginalType, T> newTypeExtractor;
	protected Function<T, OriginalType> reverseMapper;

	public Set<OriginalType> getBackSet() { return backSet; }

	public Function<OriginalType, T> getNewTypeExtractor() { return newTypeExtractor; }

	public Function<T, OriginalType> getReverseMapper() { return reverseMapper; }

	public SetMapped<OriginalType, T> setReverseMapper(Function<T, OriginalType> reverseMapper) {
		this.reverseMapper = reverseMapper;
		return this;
	}

	@Override
	public int size() { return backSet.size(); }

	@Override
	public void clear() { backSet.clear(); }

	@Override
	public boolean isEmpty() { return backSet.isEmpty(); }

	@Override
	public boolean contains(Object o) { return backSet.contains(o); }

	@Override
	public Iterator<T> iterator() { return new IteratorMapped(backSet.iterator()); }

	@Override
	public Object[] toArray() {
		int[] i;
		Object[] a;
		a = new Object[backSet.size()];
		i = new int[] { 0 };
		backSet.forEach(o -> a[i[0]++] = newTypeExtractor.apply(o));
		return a;
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		this.backSet.forEach(oldTypeElem -> action.accept(newTypeExtractor.apply(oldTypeElem)));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Tt> Tt[] toArray(final Tt[] a) {
		int len;
//		throw new UnsupportedOperationException("Too lazy to implement");
//        return backSet.toArray(a);
		if (a == null)
			return null;
		if (a.length >= (len = size())) {
			int[] i = { 0 };
			this.forEach(elem -> { a[i[0]++] = (Tt) elem; });
			return a;
		} else {
			int i = 0;
			Tt[] newArray;
			Iterator<T> iter = this.iterator();
			newArray = (Tt[]) Array.newInstance(a.getClass(), len);
//			
			while (i < len && iter.hasNext()) {
				newArray[i++] = (Tt) iter.next();
			}
			return newArray;
		}
	}

	@Override
	public boolean add(T e) {
		if (reverseMapper == null)
			throw new UnsupportedOperationException("Cannot modify the original set without a reverse-mapper");
		return backSet.add(reverseMapper.apply(e));
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {
		if (reverseMapper == null)
			throw new UnsupportedOperationException("Cannot modify the original set without a reverse-mapper");
		return backSet.remove(reverseMapper.apply((T) o));
	}

	/**
	 * Returns one element, if this set is not empty, chosen in a way dependent to
	 * its base implementation. In case no useful way could be used, it just use the
	 * iterator.
	 */
	public T pickOne() {
		T el = null;
		if (isEmpty())
			return null;
		if (this.backSet instanceof SortedSet<?>) {
			OriginalType anElement;
			anElement = ((SortedSet<OriginalType>) this.backSet).first();
			el = this.newTypeExtractor.apply(anElement);
		} else {
			el = this.iterator().next();
		}
		return el;
	}

	public T removeOne() {
		T el = null;
		OriginalType anElement;
		if (isEmpty())
			return null;
		if (this.backSet instanceof SortedSet<?>) {
			anElement = ((SortedSet<OriginalType>) this.backSet).first();
		} else {
			anElement = this.backSet.iterator().next();
		}
		el = this.newTypeExtractor.apply(anElement);
		this.backSet.remove(anElement);
		return el;
	}

	@Override
	public boolean containsAll(Collection<?> c) { return backSet.containsAll(c); }

	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException("Cannot modify the original set");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("Cannot modify the original set");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("Cannot modify the original set");
	}

	//

	protected class IteratorMapped implements Iterator<T> {
		protected final Iterator<OriginalType> iter;

		public IteratorMapped(Iterator<OriginalType> iter) {
			super();
			this.iter = iter;
		}

		@Override
		public boolean hasNext() { return iter.hasNext(); }

		@Override
		public T next() { return newTypeExtractor.apply(iter.next()); }
	}
}