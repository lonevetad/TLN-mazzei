package dataStructures;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A {@link List} implementation that guarantee a <code>O(log2(N))</code>
 * performance over {@link #add(Object)}, {@link #add(int, Object)},
 * {@link #addFirst(Object)}, {@link #addLast(Object)}, {@link #get(int)},
 * {@link #remove(int)}, {@link #remove(Object)}.<br>
 * Sadly, {@link #indexOf(Object)} and {@link #lastIndexOf(Object)} runs in
 * <code>O(n)</code>. Further implementations could enhance those two methods if
 * the given generic class is implementing {@link Comparable}. <br>
 * It uses a binary balanced AVL-like tree.
 */
public class ListTree<E> implements List<E> {
	protected static final int DEPTH_INITIAL = -1;

	public ListTree() {
		NIL = new NodeListTree(null);
		NIL.height = DEPTH_INITIAL;
		NIL.nullifyReferences();
		clear();
	}

	protected int size;
	protected NodeListTree root, first, last, NIL = null;

	@Override
	public void clear() {
		size = 0;
		root = first = last = NIL;
		NIL.height = DEPTH_INITIAL;
		NIL.nullifyReferences();
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return root == NIL;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object o) {
		NodeListTree n;
		n = getNode((E) o);
		return n != null && n != NIL;
	}

	@Override
	public Iterator<E> iterator() {
		return new IteratorListTree();
	}

	@Override
	public E get(int index) {
		NodeListTree n;
		n = getAt(index);
		return n == NIL ? null : n.element;
	}

	@Override
	public E set(int index, E element) {
		NodeListTree n;
		E e;
		n = getAt(index);
		if (n == NIL)
			return null;
		e = n.element;
		n.element = element;
		return e;
	}

	@Override
	public E remove(int index) {
		NodeListTree n;
		E e;
		n = getAt(index);
		e = n.element;
		n = remove(n);
		return e;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int indexOf(Object o) {
		NodeListTree n;
		n = getNode((E) o);
		return n == NIL ? -1 : n.index();
	}

	@SuppressWarnings("unchecked")
	@Override
	public int lastIndexOf(Object o) {
		int i;
		E e;
		e = (E) o;
		NodeListTree n;
		if (root == NIL)
			return -1;
		if (size == 0)
			return e == root.element ? 0 : -1;
		n = last;
		i = size - 1;
		while ((n != NIL) && (!Objects.equals(e, n.element))) {
			n = n.prev;
			i--;
		}
		return i;
	}

	@Override
	public boolean add(E element) {
		addLast(element);
		return true;
	}

	public void addFirst(E element) {
		add(0, element);
	}

	public void addLast(E element) {
		add(size, element);
	}

	@Override
	public void add(int index, E element) {
		addAt(index, new NodeListTree(element));
	}

	protected void addAt(int index, NodeListTree n) {
		NodeListTree x;
		if (size == 0) {
			size = 1;
			root = last = first = n;
			return;
		}
//		if (index == 0) {
//			x = first;
//			first = n;
//			x.left = n;
//			n.father = x;
//			n.next = x;
////			x.height++;
////			x.sizeLeft++;
//			insertFixup(n);
//		} else {
//			if (index == size) {
//				x = last;
//				x.right = n;
//			} else {
//				x = getAt(index);
//			}
//			x.next = n;
//			n.father = x;
//			n.prev = x;
////				last.height++;
////				x.sizeRight++;
//			x = n;
//			insertFixup(n);
//		}

		if (index == size) {
			x = last;
			last = n;
			x.right = n;
			x.next = n;
			n.father = x;
			n.prev = x;
			n.next = NIL;
//			last.height++;
//			x.sizeRight++;	
			x = n;
			insertFixup(x); // n is yet balanced: it's a leaf
		} else {
			boolean addToLeft;
			// get the node and put it on its left (by default)
			addToLeft = true;
			if (index == 0) {
				x = first;
				first = n;
				n.next = x;
				x.prev = n;
			} else {
				x = getAt(index);
				/*
				 * since it's required to put the new node at the given idex, the node at that
				 * index must be pushed deeper, so it's needed to find
				 * "the first place available before that node"
				 */
//				hasRight = x.right != NIL;
				if (x.left != NIL) {
					System.out.println("________________adding " + n.element + " at" + index + " BUT LEFT NOT NIL: "
							+ x.left.element + " ... and x is " + x.element);
					/*
					 * a left exists, then also a previous exists (since all "next"s are placed at
					 * the right)
					 */
					addToLeft = false;
					n.prev = x.prev;
					n.next = x;
					x.prev.next = n;
					x.prev = n;
					System.out.println("----- and x.prev is " + x.prev.element);
					x = n.prev;
				} else {
					/*
					 * linked-list-like insertion. In case of index == 0, then prev would be NIL, so
					 * this two lines would be useless, that's why they are in this "if"
					 */
					n.prev = x.prev;
					n.next = x;
					x.prev.next = n;
					x.prev = n;
				}
			}
			if (addToLeft)
				x.left = n;
			else
				x.right = n;
			n.father = x;
//			x.height++;
//			x.sizeLeft++;
//			insertFixup(n);
			insertFixup(x); // n is yet balanced: it's a leaf
		}
		if (size != Integer.MAX_VALUE)
			size++;
		// don't use n: it's height is 0 and it's connected only to NIL -> is balanced
		NIL.nullifyReferences();
		return;
	}

	// TODO DELETE

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object key) {
		return delete((E) key);
	}

	/**
	 * If the given key is stored inside the map, then that key and associated value
	 * will be removed, returning that value. The value {@code null} will be
	 * returned if the given key is not present or it's associated with the given
	 * key.
	 */
	public boolean delete(E k) {
		if (root == NIL)
			return false;
		return remove(getNode(k)) != NIL;
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		NodeListTree n, nil;
		nil = NIL;
		if (root == nil)
			return;
		n = first;
		do {
			action.accept(n.element);
		} while ((n = n.next) != nil);
	}

	public void forEachReversed(Consumer<? super E> action) {
		NodeListTree n, nil;
		nil = NIL;
		if (root == nil)
			return;
		n = last;
		do {
			action.accept(n.element);
		} while ((n = n.prev) != nil);
	}

	//

	// TODO protected

	protected NodeListTree successor(NodeListTree n) {
		return n == NIL ? n : n.next;
	}

	protected NodeListTree predecessor(NodeListTree n) {
		return n == NIL ? n : n.prev;
	}

	protected NodeListTree successorSorted(NodeListTree n) {
		if (n == NIL)
			return n;
		if (n.right != NIL) {
			if ((n = n.right).left != NIL)
				while ((n = n.left).left != NIL)
					;
			return n;
		}
		// travel fathers
		while (n.father != NIL && n.father.right == n)
			n = n.father;
//			if (n.father == NIL)return NIL;
		return n.father;
	}

	protected NodeListTree getAt(int i) {
		NodeListTree n;
		if (i < 0 || i >= size)
			throw new IndexOutOfBoundsException("Index: " + i);
		if (size == 0)
			return NIL;
		if (size == 1)
			return root;
		if (size < 8) { // optimization
			int s;
			if (i <= (size >> 1)) {
				n = first;
				while (--i >= 0)
					n = n.right;
			} else {
				s = size;
				n = last;
				while (--s > i)
					n = n.right;
			}
			return n;
		}
		// a O(log2(size)) approach
		n = root;
		while (i >= 0 && n != NIL) {
			if (i < n.sizeLeft) {
				n = n.left;
			} else {
				i -= n.sizeLeft;
				if (i == 0)
					return n; // me :D
				else {
					i--; // another node ("n") has been "surpassed"
					n = n.right;
				}
			}
		}
		return n;
	}

	protected NodeListTree getNode(E e) {
		NodeListTree n;
		if (root == NIL)
			return NIL;
		if (size == 0)
			return e == root.element ? root : NIL;
		n = first;
		while ((n != NIL) && (!Objects.equals(e, n.element)))
			n = n.next;
		return n;
	}

	protected void insertFixup(NodeListTree n) {
		int hl, hr, delta;
		NodeListTree temp;
		while (n != NIL) {
			// recalculate, just to be sure
			System.out.println("fixin up with " + n.element);
			n.height = (((hl = n.left.height) > (hr = n.right.height)) ? hl : hr) + 1;
			delta = hl - hr;
			System.out.println("new height: " + n.height + ", hl: " + hl + ", hr: " + hr);
			// adjust sizes
			n.sizeLeft = hl = (temp = n.left) == NIL ? 0 : (temp.sizeLeft + temp.sizeRight + 1);
			n.sizeRight = hr = (temp = n.right) == NIL ? 0 : (temp.sizeLeft + temp.sizeRight + 1);
			// update father's size, could be usefull
			if ((temp = n.father) != NIL) {
				if (temp.left == n)
					temp.sizeLeft = (hl + hr + 1);
				else
					temp.sizeRight = (hl + hr + 1);
			}
			if (delta < 2 && delta > -2) {
				// no rotation
				n = n.father;
			} else {
				System.out.println("rotating with delta: " + delta);// + ", hl: " + hl + ", hr: " + hr);
//				if ((hr - hl) != (-delta))
//					throw new RuntimeException("WTF");
				n.rotate(delta >= 2);
				n = n.father.father;

				// VERSION 2, in case of bugs try it:
				//
//				temp = n;
//				n = n.father;
//				temp.rotate(delta >= 2);
				System.out.println("the result is");
				System.out.println(this);
			}
		}
		NIL.height = DEPTH_INITIAL;
	}

	/**
	 * Use with care.
	 * <p>
	 * {@inheritDoc}
	 */
	protected NodeListTree remove(NodeListTree nToBeDeleted) {
		boolean hasLeft, hasRight, notNilFather;
		NodeListTree v;
		NodeListTree ntbdFather, actionPosition;
		// actionPosition is the parent of the physically removed node
		if (root == NIL || nToBeDeleted == NIL)
			return NIL;
//			tempForLinks = nToBeDeleted;
		v = nToBeDeleted;
		if (size == 1 && Objects.equals(root.element, nToBeDeleted.element)) {
			size = 0;
			v = root;
			root = last = first = NIL;
			NIL.nullifyReferences();
			return v;
		}

		// real deletion starts here:
		actionPosition = ntbdFather = nToBeDeleted.father;
		notNilFather = ntbdFather != NIL;
		hasLeft = nToBeDeleted.left != NIL;
		hasRight = nToBeDeleted.right != NIL;

		if (hasLeft && hasRight) {
			// both
			NodeListTree succFather, succ;
			succ = nToBeDeleted.next; //
			// successor must exists
			// substitute (swap?) value
			nToBeDeleted.element = succ.element;
			actionPosition = succFather = succ.father;
			/*
			 * note: successor must have a father. A successor without a father would be the
			 * root, but the root's predecessor is a leaf or has just one child. It's
			 * because if that predecessor would be "2-children-ed", then its successor
			 * would be the right children, who has a father, clearly.
			 */
			/*
			 * For similar reasons, the successor is the left's father's child if its height
			 * is greater than 2. If not, then the father would have been choose as
			 * successor because of the successor algorithm. Again, the successor cannot
			 * have a left child, because of it would be selected by the successor
			 * algorithm.
			 */
			// SOOOOOO remove the successor and the attach the succ's right, if not NIL
			if (succFather.left == succ)
				succFather.left = succ.right;
			else
				succFather.right = succ.right;
			succ.father = NIL;
			// if successor is not a leaf
			if (succ.right != NIL)
				(succ.right.father = succFather).height = 0;
			succ.right = NIL;
			v = actionPosition;

//			update first and last
//			if (nToBeDeleted == first) // not needed
//				first = ntbdFather;
			if (succ == last)
				last = succFather;
			// unlink
			succ.next.prev = succ.prev;
			succ.prev.next = succ.next;
		} else if (hasLeft || hasRight) { // or "^" or "!="
			// just one child: delete it stealing from it its element
			NodeListTree child;
			if (hasLeft) {
				child = nToBeDeleted.left;
				nToBeDeleted.left = NIL;
			} else {
				child = nToBeDeleted.right;
				nToBeDeleted.right = NIL;
			}
			// DO not move children, move its values
			nToBeDeleted.element = child.element;
			nToBeDeleted.height = 0; // DEPTH_INITIAL+1
			child.father = NIL;
//			update first and last
			if (child == first) {
				first = nToBeDeleted;
				nToBeDeleted.prev = NIL;
			}
			if (child == last) {
				last = nToBeDeleted;
				nToBeDeleted.next = NIL;
			}
			actionPosition = nToBeDeleted;
			v = child;
			child.prev.next = child.next;
			child.next.prev = child.prev;
		} else {
			// leaf
			if (notNilFather) {
				if (ntbdFather.left == nToBeDeleted)
					ntbdFather.left = NIL;
				else
					ntbdFather.right = NIL;
//				update first and last
				if (nToBeDeleted == first) {
					first = ntbdFather;
					ntbdFather.prev = NIL;
				}
				if (nToBeDeleted == last) {
					last = ntbdFather;
					ntbdFather.next = NIL;
				}
				nToBeDeleted.prev.next = nToBeDeleted.next;
				nToBeDeleted.next.prev = nToBeDeleted.prev;
				// do not clear nToBeDeleted's fields to save time
//				actionPosition=ntbdFather; // removed because it's redundant
			} else {// i'm root AND a leaf: this tree will be cleared (later)
				clear();
				return v;
			}
		}

		// then balance
		// father can't be NIL
//			balanceOnRemove(actionPosition);
		insertFixup(actionPosition);
		actionPosition.height = ((actionPosition.left.height > actionPosition.right.height) ? actionPosition.left.height
				: actionPosition.right.height) + 1;

		// todo 2
		if (size == Integer.MAX_VALUE) {
			size = (1 + root.sizeLeft + root.sizeRight);
			// is there an overflow?
			if (size < 0 || root.sizeLeft < 0 || root.sizeRight < 0) {
				size = Integer.MAX_VALUE;
			}
		} else if (--size == 0) {
			root = last = first = NIL;
		} else if (root == NIL)
			throw new RuntimeException("BUG: Root is nil but tree is not empty");
		NIL.nullifyReferences();
		return v;
	}

	//

	// TODO INHERITED FROM LIST (but uglier than other more common methods

	@Override
	public Object[] toArray() {
		if (size == 0)
			return null;
		if (size == 1)
			return new Object[] { root.element };
		return toArray(new Object[size]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		int[] index;
		final T[] tempArray;
		if (size == 0 || a == null)
			return a;
		if (a.length < size)
			a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
		tempArray = a;
		index = new int[] { 0 };
		this.forEach(e -> tempArray[index[0]++] = (T) e);
		return tempArray;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o))
				return false;
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
//		boolean[] flag;
		final int prevSize;
		final ListTree<E> thisList;
		thisList = this;
		prevSize = size();
//		flag = new boolean[] { false };
		c.forEach(o -> {
			thisList.add(o);
//			flag[0] |= prevSize != thisList.size();
		});
		return prevSize != thisList.size();
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		final int prevSize, i[];
		final ListTree<E> thisList;
		thisList = this;
		prevSize = size();
//		flag = new boolean[] { false };
		i = new int[] { index };
		c.forEach(o -> {
			thisList.add(i[0]++, o);
//			flag[0] |= prevSize != thisList.size();
		});
		return prevSize != thisList.size();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		final int prevSize;
		final ListTree<E> thisList;
		thisList = this;
		prevSize = size();
		c.forEach(o -> remove(o));
		return prevSize != thisList.size();
	}

	//

	//

	@Override
	public String toString() {
		StringBuilder sb;
		sb = new StringBuilder(128);
		toString(sb, 0);
		return sb.toString();
	}

	public void toString(StringBuilder sb) {
		toString(sb, 0);
	}

	protected void toString(StringBuilder sb, int tabLevel) {
		NodeListTree m;
		sb.append("size: ");
		sb.append(this.size);
		sb.append("\nMin: ");
		m = first;
		sb.append(m.element);
		sb.append(", Max: ");
		m = last;
		sb.append(m.element).append(')');
		sb.append('\n');
		toString(sb, root, tabLevel);
	}

	protected void toString(StringBuilder sb, NodeListTree node, int level) {
		int tabLevel;
		if (node != NIL) {
			level++;
			toString(sb, node.left, level);
//			addTab(sb, level - 1, false);
			tabLevel = level - 1;
			sb.ensureCapacity(sb.length() + tabLevel << 2);
			while (tabLevel-- > 0) {
				sb.append('\t');
			}
			sb.append(String.valueOf(node));
			sb.append('\n');
			toString(sb, node.right, level);
		}
	}

	//

	// TODO class

	protected class NodeListTree {
		protected int height, sizeLeft, sizeRight;
		protected E element;
		protected NodeListTree father, left, right, prev, next;

		public NodeListTree(E t) {
			this.element = t;
			sizeRight = sizeLeft = 0;
			height = 0;
			nullifyReferences();
		}

		void nullifyReferences() {
			father = left = right = prev = next = NIL;
		}

		public void rotate(boolean isRight) {
			int hl, hr;
			NodeListTree nSide, oldFather;
			oldFather = father;
			if (isRight) {
				// right
				nSide = left;
				if (nSide.right.height > nSide.left.height) {
					// three-rotation : ignoring this difference would cause the tree to be
					// umbalanced again
					final NodeListTree a, b, c;
					a = this;
					b = nSide;
					c = b.right;
					if (oldFather != NIL) {
						if (oldFather.left == a)
							oldFather.left = c;
						else
							oldFather.right = c;
						c.father = oldFather;
					}
					a.father = c;
					a.left = c.right;
					if (c.right != NIL)
						c.right.father = a;
					c.right = a;
					if (c.left.father != NIL)
						c.left.father = b;
					b.right = c.left;
					b.father = c;
					c.left = b;

					// recompute height
					c.height++;
					a.height -= 2;
					b.height--;
					NIL.left = NIL.right = NIL.father = NIL;
					if (a == root) {
						root = c;
						c.father = NIL; // not necessary, but done to be sure
					}
					// adjust sizes
//					if (c.right == NIL) c.sizeRight = 0;
//					if (c.left == NIL) c.sizeLeft = 0;
//					if (a.right == NIL) a.sizeRight = 0;
//					if (b.left == NIL) b.sizeLeft = 0;
					a.sizeLeft = c.sizeRight;
					b.sizeRight = c.sizeLeft;
					c.sizeRight += 1 + a.sizeRight;
					c.sizeLeft += 1 + b.sizeLeft;
					return;
				}
				left = left.right; // i could have put "nSide. .." but the whole piece of code would be less clear
//				if (left == NIL)sizeLeft = 0;
				nSide.right.father = this;
				nSide.right = this;
				// adjust sizes
				sizeLeft = nSide.sizeRight;
				nSide.sizeRight += 1 + sizeRight;
			} else {
				// left
				nSide = right;
				if (nSide.left.height > nSide.right.height) {
					final NodeListTree a, b, c;
					a = this;
					b = nSide;
					c = b.left;
					// then
					if (oldFather != NIL) {
						if (oldFather.left == a)
							oldFather.left = c;
						else
							oldFather.right = c;
						c.father = oldFather;
					}
					a.father = c;
					a.right = c.left;
					if (c.left != NIL)
						c.left.father = a;
					c.left = a;
					if (c.right.father != NIL)
						c.right.father = b;
					b.left = c.right;
					b.father = c;
					c.right = b;

					// recompute height
					c.height++;
					a.height -= 2;
					b.height--;
					NIL.left = NIL.right = NIL.father = NIL;
					if (a == root) {
						root = c;
						c.father = NIL; // not necessary, but done to be sure
					}
					// adjust sizes
//					if (c.right == NIL) c.sizeRight = 0;
//					if (c.left == NIL) c.sizeLeft = 0;
//					if (b.right == NIL) b.sizeRight = 0;
//					if (a.left == NIL) a.sizeLeft = 0;
					a.sizeRight = c.sizeLeft;
					b.sizeLeft = c.sizeRight;
					c.sizeLeft += 1 + a.sizeLeft;
					c.sizeRight += 1 + b.sizeRight;
					return;
				}
				right = right.left; // i could have put "nSide. .." but the whole piece of code would be less clear
//				if (right == NIL) sizeRight = 0;
				nSide.left.father = this;
				nSide.left = this;
				// adjust sizes
				sizeRight = nSide.sizeLeft;
				nSide.sizeLeft += 1 + sizeLeft;
			}
			if (/* isNullNIL */NIL == (oldFather))
				// i'm root .. ehm, no: i WAS root
				root = nSide;
			else {
				if (oldFather.left == this)
					oldFather.left = nSide;
				else
					oldFather.right = nSide;
			}
			father = nSide;
			nSide.father = oldFather;
			hl = left.height;
			hr = right.height;
			hl = height = (hl > hr ? hl : hr) + 1;
			hr = ((isRight) ? nSide.left : nSide.right).height;
			nSide.height = (hl > hr ? hl : hr) + 1;
		}

		protected int index() {
			int i;
			i = 0;
			if (sizeLeft > 0)
				i = sizeLeft + 1;
			if (father != NIL && father.right == this)
				i += father.index();
			return i;
		}

		@Override
		public String toString() {
			return String.valueOf(element) + ",h:" + height + ",f:" + String.valueOf(father.element) + ", sl:"
					+ sizeLeft + ",sr:" + sizeRight;
		}
	}

	//

	//

	@Override
	public boolean retainAll(Collection<?> c) {
		LinkedList<E> toBeRemoved;
		if (c == null || this.isEmpty())
			return false;
		if (c.isEmpty()) {
			this.clear();
			return true;
		}
		toBeRemoved = new LinkedList<>();
		this.forEach(e -> {
			if (!c.contains(e))
				toBeRemoved.add(e);
		});
		if (toBeRemoved.isEmpty())
			return false;
		while (!toBeRemoved.isEmpty())
			this.remove(toBeRemoved.removeFirst());
		return true;
	}

	@Override
	public ListIterator<E> listIterator() {
		throw new UnsupportedOperationException("Operation not implemented yet");
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		throw new UnsupportedOperationException("Operation not implemented yet");
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		/*
		 * could be achieved through "proxy" methods (also, clear() could act as a
		 * <code>loop of remove(index)</code>, in a similar way getAt(i) could be
		 * <code>backList.getAt(baseIndex+i)</code>, and so on).
		 */
		throw new UnsupportedOperationException("Operation not implemented yet");
	}

	//

	//

	class IteratorListTree implements Iterator<E> {
		NodeListTree n = first;

		@Override
		public boolean hasNext() {
			return n != NIL;
		}

		@Override
		public E next() {
			E e;
			e = n.element;
			n = n.next;
			return e;
		}

	}

}