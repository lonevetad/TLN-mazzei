package dataStructures;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

public class ListMapped<OriginalType, T> implements List<T> {

	public ListMapped(List<OriginalType> backList, Function<OriginalType, T> newTypeExtractor) {
		this.backList = backList;
		this.newTypeExtractor = newTypeExtractor;
	}

	protected final List<OriginalType> backList;
	protected final Function<OriginalType, T> newTypeExtractor;

	@Override
	public int size() {
		return backList.size();
	}

	@Override
	public void clear() {
		backList.clear();
	}

	@Override
	public boolean isEmpty() {
		return backList.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return backList.contains(o);
	}

	@Override
	public T get(int index) {
		return newTypeExtractor.apply(backList.get(index));
	}

	@Override
	public Iterator<T> iterator() {
		return new IteratorMapped(backList.iterator());
	}

	@Override
	public Object[] toArray() {
		int[] i;
		Object[] a;
		a = new Object[backList.size()];
		i = new int[] { 0 };
		backList.forEach(o -> a[i[0]++] = newTypeExtractor.apply(o));
		return a;
	}

	@Override
	public <Tt> Tt[] toArray(Tt[] a) {
		throw new UnsupportedOperationException("Too lazy to implement");
	}

	@Override
	public boolean add(T e) {
		throw new UnsupportedOperationException("Cannot modify the original list");
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("Cannot modify the original list");
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return backList.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException("Cannot modify the original list");
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException("Cannot modify the original list");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("Cannot modify the original list");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("Cannot modify the original list");
	}

	@Override
	public T set(int index, T element) {
		throw new UnsupportedOperationException("Cannot modify the original list");
	}

	@Override
	public void add(int index, T element) {
		throw new UnsupportedOperationException("Cannot modify the original list");
	}

	@Override
	public T remove(int index) {
		throw new UnsupportedOperationException("Cannot modify the original list");
	}

	@Override
	public int indexOf(Object o) {
		return backList.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return backList.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return new ListIteratorMapped(backList.listIterator());
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		throw new UnsupportedOperationException("Too lazy to implement");
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return new ListMapped<>(backList.subList(fromIndex, toIndex), newTypeExtractor);
	}

	//

	protected class IteratorMapped implements Iterator<T> {
		protected final Iterator<OriginalType> iter;

		public IteratorMapped(Iterator<OriginalType> iter) {
			super();
			this.iter = iter;
		}

		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public T next() {
			return newTypeExtractor.apply(iter.next());
		}
	}

	protected class ListIteratorMapped implements ListIterator<T> {
		protected final ListIterator<OriginalType> iter;

		public ListIteratorMapped(ListIterator<OriginalType> iter) {
			super();
			this.iter = iter;
		}

		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public T next() {
			return newTypeExtractor.apply(iter.next());
		}

		@Override
		public boolean hasPrevious() {
			return iter.hasPrevious();
		}

		@Override
		public T previous() {
			return newTypeExtractor.apply(iter.previous());
		}

		@Override
		public int nextIndex() {
			return iter.nextIndex();
		}

		@Override
		public int previousIndex() {
			return iter.previousIndex();
		}

		@Override
		public void remove() {
			iter.remove();
		}

		@Override
		public void set(T e) {
			throw new UnsupportedOperationException("Cannot modify the original list");
		}

		@Override
		public void add(T e) {
			throw new UnsupportedOperationException("Cannot modify the original list");
		}
	}
}