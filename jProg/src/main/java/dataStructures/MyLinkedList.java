package dataStructures;

import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;

public class MyLinkedList<E> implements Serializable, List<E>, Deque<E> {
	private static final long serialVersionUID = 480921201212L;

	public MyLinkedList() {
		// sentinel = new NodeList(null);
	}

	int size = 0;
	NodeList<E> head, tail;

	@Override
	public int size() {
		return size;
	}

	public NodeList<E> getHead() {
		return head;
	}

	public NodeList<E> getTail() {
		return tail;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean contains(Object o) {
		if (o // instanceof GraphNode
		!= null) {
			if (size == 0) {
				return false;
			}
			int i = indexOf(o);
			// System.out.println("\t\t\t contains gives : " + i);
			return i >= 0 && i < size;
		}
		return false;
	}

	@Override
	public void clear() {
		size = 0;
		tail = head = null;
	}

	@Override
	public boolean add(E e) {
		NodeList<E> n;
		if (e != null) {
			if (size == 0) {
				addFirst(e);
			} else {
				n = new NodeList<E>(e);
				n.prev = tail;
				if (tail != null) {
					tail.next = n;
				}
				tail = n;
				size++;
			}
			return true;
		}
		return false;
	}

	/** Remove ALL instaces of "o" */
	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {
		E g;
		NodeList<E> n, temp;
		boolean someoneRemoved;

		someoneRemoved = false;
		if (o // instanceof GraphNode
		!= null && size > 0) {

			g = (E) o;
			n = head;
			if (n != null) {
				do {
					if (n.item.equals(g)) {
						// if (n != head && n != tail) {
						temp = n.next;
						// }
						if (n == head) {
							temp = head = n.next;
						}
						if (n == tail) {
							tail = n.prev;
							temp = null;
						}
						n.unlink();
						n = temp;
						size--;
						someoneRemoved = true;
					}
				} while ((n = n.next) != null);
			}
		}
		return someoneRemoved;
	}

	@Override
	public E get(int index) {
		if (index >= 0 && index < size) {
			NodeList<E> n = getAt(index);
			if (n != null) {
				return n.item;
			}
		}
		return null;
	}

	private NodeList<E> getAt(int index) {
		NodeList<E> iterator = null;
		boolean notFound = true;
		int i = 0;
		if (index >= 0 && index < size) {
			if (index == 0) {
				return head;
			}
			if (index == size - 1) {
				return tail;
			}
			iterator = head;
			while (notFound && iterator != null) {
				notFound = i < index;
				if (notFound) {
					i++;
					iterator = iterator.next;
				}
			}
		}
		return iterator;
	}

	public boolean swapAt(int firstIndex, int secondIndex) {
		boolean firstLowerThanHalf, secondLowerThanHalf, searchNotEnded;
		int nodesVisited, limit, halfSize;
		E temp;
		NodeList<E> fn, sn;// first and second node

		if (firstIndex == secondIndex)
			return false;
		if (firstIndex >= 0 && firstIndex < size && secondIndex >= 0 && secondIndex < size) {
			limit = Math.max(firstIndex, secondIndex);

			// set "first" as lower, to make work more handy
			firstLowerThanHalf = firstIndex < (halfSize = size >> 1);
			secondLowerThanHalf = secondIndex < halfSize;
			if (firstIndex > secondIndex) {
				// swap
				nodesVisited = secondIndex;
				secondIndex = firstIndex;
				firstIndex = nodesVisited;
			}

			nodesVisited = -1;
			fn = sn = null;
			searchNotEnded = true;
			// try to optimize�
			if (firstLowerThanHalf && secondLowerThanHalf) {
				// both lower than half
				// use "fn" as the iterator
				fn = sn = this.head;
				while (searchNotEnded && ++nodesVisited < limit) {
					if (nodesVisited == firstIndex)
						sn = fn;
					else if (nodesVisited == secondIndex)
						searchNotEnded = false;
					// else : is just a random element
					if (searchNotEnded)
						fn = fn.next;
				}
			} else if (!(firstLowerThanHalf || secondLowerThanHalf)) {// both false
				// both greater
				fn = sn = this.tail;
				nodesVisited = size; // without "-1" because of the "--" on the cycle's condition
				while (searchNotEnded && --nodesVisited >= 0) { /// >= half, but like this is faster
//use "fn" as the iterator
					if (nodesVisited == secondIndex) {
						sn = fn;
					} else if (nodesVisited == firstIndex)
						searchNotEnded = false;
					// else: is just a random element
					if (searchNotEnded)
						fn = fn.prev;
				}
			} else {
				// one greater, the other no ..let be "first" the lower ones
				fn = this.head;
				sn = this.tail;
				halfSize++;// better not to skip a item due to bug
				if (firstIndex != (nodesVisited = 0))
					while (++nodesVisited <= firstIndex)
						fn = fn.next;
				if (secondIndex != (nodesVisited = size - 1))
					while (--nodesVisited >= secondIndex)
						sn = sn.prev;
			}
			// do the swap
			temp = fn.item;
			fn.item = sn.item;
			sn.item = temp;
			return true; // done .. D
		}
		return false;
	}

	public boolean swapItems(E first, E second) {
		NodeList<E> fn, sn, iter;
		if (first != second && (!Objects.equals(first, second)) && size > 1) {
			iter = head;
			fn = sn = null;
			while ((fn == null || sn == null) && ((iter == head) || ((iter = iter.next) != null))) {
				if (Objects.equals(first, iter.item))
					fn = iter;
				if (Objects.equals(second, iter.item))
					sn = iter;
			}
			if (fn != sn && fn != null && sn != null) {
				E temp;
				temp = fn.item;
				fn.item = sn.item;
				sn.item = temp;
				return true;
			}
		}
		return false;
	}

	public void unlink(NodeList<E> n) {
		if (n != null) {
			if (n == head) {
				head = head.next;
			}
			if (n == tail) {
				tail = tail.prev;
			}
			if (n.prev != null) {
				n.prev.next = n.next;
			}
			if (n.next != null) {
				n.next.prev = n.prev;
			}
			n.prev = null;
			n.next = null;
			size--;
		}
	}

	public NodeList<E> getHaving(E e) {
		NodeList<E> iterator = null;
		boolean notFound = true;
		if (e != null) {
			iterator = tail;
			while (notFound && iterator != null) {
				notFound = !e.equals(iterator.item);
				if (notFound) {
					iterator = iterator.prev;
				}
			}
		}
		return iterator;
	}

	@Override
	public E set(int index, E element) {
		NodeList<E> n;
		E g = null;
		if (element != null && index >= 0 && index < size) {
			n = getAt(index);
			if (n != null) {
				g = n.item;
				n.item = element;
			}
			return g;
		}
		return null;
	}

	@Override
	public void add(int index, E element) {
		NodeList<E> nn, n;

		if (element != null && index >= 0 && index < size) {
			nn = new NodeList<E>(element);
			if (index == 0) {
				nn.prev = null;
				nn.next = head;
				if (head != null) {
					head.prev = nn;
					head = nn;
				}
			} else if (index == size - 1) {
				if (tail != null) {
					nn.prev = tail.prev;
					tail.prev.next = nn;
					nn.next = tail;
					tail.prev = nn;
				}
			} else {
				n = getAt(index);
				if (n != null) {
					nn.next = n;
					nn.prev = n.prev;
					if (n.prev != null) {
						n.prev.next = nn;
					}
					n.prev = nn;
				}
			}
			size++;
		}
	}

	@Override
	public E remove(int index) {
		E e = null;
		NodeList<E> n;
		if (index >= 0 && index < size) {
			if (index == 0) {
				e = removeFirst();
			} else if (index == size - 1) {
				e = removeLast();
			} else {
				n = getAt(index);
				if (n != null) {
					e = n.item;
					n.unlink();
					size--;
				}
			}
		}
		return e;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int indexOf(Object o) {
		E g;
		boolean notFound;
		int i;
		NodeList<E> iterator;

		if (o // instanceof GraphNode
		!= null && size > 0) {
			g = (E) o;
			notFound = true;
			i = size - 1;
			iterator = tail;
			while (notFound && iterator != null) {
				notFound = !g.equals(iterator.item);
				if (notFound) {
					i--;
					iterator = iterator.prev;
				}
			}
			if (notFound) {
				i = -1;
			}
			return i;
		}
		return -1;
	}

	/**
	 * public int indexOf(Object o) { // <br>
	 * if (o instanceof GraphNode && size > 0) { // <br>
	 * GraphNode g = (GraphNode) o; // <br>
	 * boolean notFound = true; // <br>
	 * int i = 0; // <br>
	 * NodeList iterator = head; // <br>
	 * while (notFound && iterator != null) { // <br>
	 * notFound = !g.equals(iterator.gn); // <br>
	 * if (notFound) { // <br>
	 * i++; // <br>
	 * iterator = iterator.next; // <br>
	 * } // <br>
	 * } // <br>
	 * if (notFound) { // <br>
	 * i = -1; // <br>
	 * } // <br>
	 * System.out.println("having g (" + g.xCenter + ',' + g.yCenter + ") i found :
	 * " + i); // <br>
	 * return i; // <br>
	 * } // <br>
	 * return -1; // <br>
	 * } // <br>
	 * 
	 */

	/** If not present, this method return -1 */
	@SuppressWarnings("unchecked")
	@Override
	public int lastIndexOf(Object o) {
		E g;
		boolean notFound;
		int i;
		NodeList<E> iterator;

		if (// o instanceof GraphNode
		o != null) {
			if (size > 0) {
				g = (E) o;
				notFound = true;
				i = size - 1;
				iterator = tail;
				while (notFound && iterator != null) {
					notFound = !g.equals(iterator.item);
					if (notFound) {
						i--;
						iterator = iterator.prev;
					}
				}
				if (notFound) {
					i = -1;
				}
				return i;
			}
		}
		return -1;
	}

	@Override
	public void addFirst(E g) {
		if (g != null) {
			NodeList<E> n = new NodeList<E>(g);
			if (size == 0) {
				head = tail = n;
				size = 1;
			}
			/*
			 * else if (size == 1) { // head == tail -> true tail.prev = n; n.next = tail;
			 * head = n; size = 2; }
			 */else {
				n.next = head;
				if (head != null) {
					head.prev = n;
				}
				head = n;
				size++;
			}
		}
	}

	@Override
	public void addLast(E e) {
		add(e);
	}

	@Override
	public boolean offerFirst(E e) {
		addFirst(e);
		return true;
	}

	@Override
	public boolean offerLast(E e) {
		addLast(e);
		return true;
	}

	@Override
	public E removeFirst() {
		if (size > 0) {
			E g = head.item;
			if (size == 1) {
				tail = head = null;
				size = 0;
			} else {
				head = head.next;
				head.prev = null;
				size--;
			}
			return g;
		}
		return null;
	}

	@Override
	public E removeLast() {
		if (size > 0) {
			E g = tail.item;
			if (size == 1) {
				tail = head = null;
				size = 0;
			} else {
				tail = tail.prev;
				tail.next = null;
				size--;
			}
			return g;
		}
		return null;
	}

	@Override
	public E pollFirst() {
		return removeFirst();
	}

	@Override
	public E pollLast() {
		return removeLast();
	}

	@Override
	public E getFirst() {
		if (head != null) {
			return head.item;
		}
		return null;
	}

	@Override
	public E getLast() {
		if (tail != null) {
			return tail.item;
		}
		return null;
	}

	@Override
	public E peekFirst() {
		return getFirst();
	}

	@Override
	public E peekLast() {
		return getLast();
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		// return remove(o);
		throw new UnsupportedOperationException("operation not supported in MyLinkedList");
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		throw new UnsupportedOperationException("operation not supported in MyLinkedList");
	}

	@Override
	public boolean offer(E e) {
		addFirst(e);
		return e != null;
		// return e instanceof E.;
	}

	@Override
	public E remove() {
		return removeFirst();
	}

	@Override
	public E poll() {
		return removeFirst();
	}

	@Override
	public E element() {
		System.err.println("operation not supported in ListGraphNodes");
		return null;
	}

	@Override
	public E peek() {
		return getFirst();
	}

	@Override
	public void push(E e) {
		addFirst(e);
	}

	@Override
	public E pop() {
		return removeFirst();
	}

	@SuppressWarnings("unchecked")
	@Override
	public E[] toArray() {
		E[] ret;
		NodeList<E> iter;
		int i;
		// ArrayList<Integer> err;

		ret = (E[]) new Object[size];
		iter = head;
		i = 0;
		// err = new ArrayList<Integer>();
		if (iter != null) {
			do {
				ret[i++] = iter.item;
				/**
				 * if (iter.item == null) {//<br>
				 * // System.out.println("error in toArray [" + i + "] : // iter.gn is null
				 * !");//<br>
				 * err.add(--i); }//<br>
				 */
			} while ((iter = iter.next) != null);
		}
		/**
		 * if (err.size() != 0) {//<br>
		 * int s = err.size();//<br>
		 * i = -1;//<br>
		 * while (++i < s) {//<br>
		 * System.out.println("error in toArray [" + err.get(i) + "] : iter.gn is null
		 * !");//<br>
		 * }//<br>
		 * System.exit(1); }
		 */
		return ret;
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("operation not supported in MyLinkedList");
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException("operation not supported in MyLinkedList");
	}

	@Override
	public Iterator<E> iterator() {
		IteratorMyLinkedList<E> iter = new IteratorMyLinkedList<E>(head);
		iter.size = this.size;
		return iter;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		System.err.println("operation not supported in ListGraphNodes");
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		System.err.println("operation not supported in ListGraphNodes");
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		System.err.println("operation not supported in ListGraphNodes");
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		System.err.println("operation not supported in ListGraphNodes");
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		System.err.println("operation not supported in ListGraphNodes");
		return false;
	}

	@Override
	public ListIterator<E> listIterator() {
		return (ListIterator<E>) iterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		System.err.println("listIterator(int index) operation not supported in ListGraphNodes");
		return null;
	}

	@Override
	public Iterator<E> descendingIterator() {
		System.err.println("descendingIterator operation not supported in ListGraphNodes");
		return null;
	}

	@Override
	public String toString() {
		int i;
		StringBuilder sb;
		String separator;
		NodeList<E> n;

		i = size << 3;
		sb = new StringBuilder(i > 0 ? i : 10000);
		sb.append("List of size: ");

		sb.append(size);
		sb.append('\n');

		i = -1;
		n = head;
		separator = " : ";

		if (n != null) {
			do {
				sb.append(++i);
				sb.append(separator);
				sb.append(n.item);
				sb.append('\n');
			} while ((n = n.next) != null);
		}
		return sb.append("end of list\n").toString();
	}

	// TODO CLASS

	// TODO class NodeList<E>

	public static class NodeList<E> implements Serializable {
		@SuppressWarnings("unused")
		private NodeList() {
		}

		public NodeList(E gn) {
			this.item = gn;
		}

		private static final long serialVersionUID = 65840009806036741L;
		NodeList<E> next = null, prev = null;
		E item;

		public E getItem() {
			return item;
		}

		public NodeList<E> getNext() {
			return next;
		}

		public NodeList<E> getPrev() {
			return prev;
		}

		public void unlink() {
			if (prev != null) {
				prev.next = next;
			}
			if (next != null) {
				next.prev = prev;
			}
			next = prev = null;
		}
	}

	// TODO class IteratorMyLinkedList<E>

	public static class IteratorMyLinkedList<E> implements Iterator<E>, ListIterator<E>, Serializable {

		private IteratorMyLinkedList() {
			i = -1;
		}

		IteratorMyLinkedList(NodeList<E> n) {
			this();
			// this.n = new NodeList<E>(); // sentinella
			// this.n.next = n;
			this.n = n;
		}

		private static final long serialVersionUID = 64063335415800L;
		private NodeList<E> n;
		private int i, size;
		// ti uso per evitare il GC overhead per creare la sentinella
		boolean nullNextNeverAsked = true, canAskAgainHasNext = true;

		@Override
		public boolean hasNext() {
			boolean b = n != null && n.next != null;
			if (!b) {
				b &= canAskAgainHasNext;
				canAskAgainHasNext = false;
			}
			return b;
		}

		public E getE() {
			return n.item;
		}

		/**
		 * If the current node isn't null and the next node exists, this methods Returns
		 * the node's element currently pointed to the iterator , else throw a
		 * {@link NoSuchElementException}
		 */
		@Override
		public E next() throws NoSuchElementException {
			if (n != null) {
				// if (n.next != null) {
				// E g = null;
				// g = n.gn;
				if (nullNextNeverAsked) {
					nullNextNeverAsked = false;
				} else {
					n = n.next;
				}
				i++;
				return n.item;
				/*
				 * } else { if (nullNextNeverAsked) { nullNextNeverAsked = false; return null; }
				 * }
				 */
			}
			throw new NoSuchElementException("No further element in the IteratorGraphNode");
		}

		@Override
		public boolean hasPrevious() {
			return n != null && n.prev != null;
		}

		@Override
		public E previous() {
			E g = null;
			if (n != null) {
				n = n.prev;
				g = n.item;
				i--;
			}
			return g;
		}

		@Override
		public int nextIndex() {
			return i + 1;
		}

		public int getIndex() {
			return i;
		}

		@Override
		public int previousIndex() {
			return i - 1;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("operation not supported in IteratorMyLinkedList");
		}

		@Override
		public void set(E e) {
			throw new UnsupportedOperationException("operation not supported in IteratorMyLinkedList");
		}

		@Override
		public void add(E e) {
			throw new UnsupportedOperationException("operation not supported in IteratorMyLinkedList");
		}

		public int getSize() {
			return size;
		}

	}

	// TODO MAIN

	public static void main(String[] args) {
		int n, i, s;
		MyLinkedList<Integer> l;
		Random r;

		r = new Random();
		l = new MyLinkedList<Integer>();

		printList(l);

		i = -1;
		s = 20;
		while (++i < s) {
			n = r.nextInt(s);
			System.out.println(i + " = " + n);
			l.add(n);
		}
		System.out.println();

		printList(l);

		System.out.println("\n getting the element at the half : " + (n = l.get(s >>> 1)));

		System.out.println("\n removing the element at the half : " + l.remove(Integer.valueOf(n)));
		printList(l);

		System.out.println("\n Removing the element having index 3 : " + l.remove(3));
		printList(l);

		System.out.println("\n Removing the element having index 0 : " + l.remove(0));
		printList(l);

		System.out.println("\n Removing the element having index size-1 : " + l.remove(l.size - 1));
		printList(l);

		System.out.println("\n Removing the element having index 1 : " + l.remove(1));
		printList(l);

		System.out.println("\n Removing the element having index size-2 : " + l.remove(l.size - 2));
		printList(l);

		System.out.println("\n Now swap indexes 3 and 7.");
		l.swapAt(3, 3);
		printList(l);
	}

	public static final void printList(MyLinkedList<? extends Object> l) {
		printList(l, "", new OutputStreamWriter(System.out));
	}

	protected static final void printList(MyLinkedList<? extends Object> l, OutputStreamWriter ps) {
		printList(l, "", ps);
	}

	@SuppressWarnings("unchecked")
	public static final void printList(MyLinkedList<? extends Object> l, String pretext, OutputStreamWriter ps) {
		int i;
		NodeList<? extends Object> n;

		i = -1;
		n = l.head;
		// for(Object o)
		try {
			ps.append(pretext);
			ps.append("-----printing list having size:");
			ps.append(Integer.toString(l.size));
			ps.append('\n');
			ps.flush();
			if (n != null) {

				// caso base : un solo elemento
				ps.append('\t' + pretext + ++i + "\t: ");
				if (n.item instanceof MyLinkedList) {
					ps.append('\n');
					ps.flush();
					printList((MyLinkedList<? extends Object>) n.item, pretext + '\t', ps);
				} else {
					ps.append(String.valueOf(n.item));
				}
				ps.flush();

				// passo induttivo : piu' elementi
				if ((n = n.next) != null)
					do {
						// ps.append('\n');
						ps.append('\n');
						ps.append('\t');
						ps.append(pretext);
						ps.append(Integer.toString(++i));
						ps.append("\t: ");
						ps.flush();

						if (n.item instanceof MyLinkedList) {
							ps.append('\n');
							ps.flush();
							printList((MyLinkedList<? extends Object>) n.item, pretext + '\t', ps);
						} else {
							ps.append(String.valueOf(n.item));
							ps.flush();
						}
					} while ((n = n.next) != null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}// fine class ListGraph