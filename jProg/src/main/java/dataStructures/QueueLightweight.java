package dataStructures;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.Consumer;

/** The most light version of a {@link Queue} */
public class QueueLightweight<T> implements Queue<T> {

	public QueueLightweight() {
		clear();
	}

	int size;
	NodeQLW<T> first, last;

	protected static class NodeQLW<E> {
		E item;
		NodeQLW<E> next;

		public NodeQLW(E item) {
			super();
			this.item = item;
		}
	}

	//

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public void clear() {
		size = 0;
		first = last = null;
	}

	@Override
	public boolean add(T e) {
		NodeQLW<T> n;
		n = new NodeQLW<>(e);
		if (size == 0) {
			first = last = n;
			size = 1;
		} else {
			last.next = n;
			last = n;
			size++;
		}
		return true;
	}

	@Override
	public boolean offer(T e) {
		return add(e);
	}

	@Override
	public T remove() {
		if (size == 0)
			throw new NoSuchElementException("The queue is empty, cannot remove.");
		return poll();
	}

	@Override
	public T poll() {
		NodeQLW<T> n;
		if (size == 0) {
			return null;
		} else {
			n = first;
			if (size == 1) {
				first = last = null;
				size = 0;
			} else {
				first = first.next;
				size--;
			}
			return n.item;
		}
	}

	@Override
	public T element() {
		if (size == 0)
			throw new NoSuchElementException("The queue is empty, cannot remove.");
		return peek();
	}

	@Override
	public T peek() {
		return size == 0 ? null : first.item;
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return new IteratorQLW<>(this);
	}

	@Override
	public Object[] toArray() {
		int i;
		NodeQLW<T> n;
		Object[] a;
		a = new Object[size];
		n = first;
		if (size > 0) {
			a[i = 0] = n.item;
			while ((n = n.next) != null)
				a[++i] = n.item;
		}
		return a;
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		NodeQLW<T> n;
		if (action != null && (n = first) != null) {
			do
				action.accept(n.item);
			while ((n = n.next) != null);
		}
	}

	@Override
	public <S> S[] toArray(S[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		/*
		 * distinghuish from having 0,1 and more elements. In case of "n", keep the
		 * pointer of the current node and the previous node. If the current node's item
		 * is not present in "c", then
		 * "prev = prev.next; current = prev.next; and continue"
		 */
		throw new UnsupportedOperationException();
	}

	protected static class IteratorQLW<E> implements Iterator<E> {
		public IteratorQLW(QueueLightweight<E> q) {
			super();
			this.q = q;
			n = q.first;
		}

		QueueLightweight<E> q;
		NodeQLW<E> n;

		@Override
		public boolean hasNext() {
			return n != null;
		}

		@Override
		public E next() {
			E e;
			e = null;
			if (n != null) {
				e = n.item;
				n = n.next;
			}
			return e;
		}
	}
}