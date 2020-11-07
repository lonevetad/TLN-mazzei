package tools;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public final class Comparators {
	public interface MyComparator<E> extends Comparator<E>, Serializable {
	}

	private Comparators() {}

	public static final MyComparator<String> STRING_COMPARATOR = new GenericComparator<>()//
			, STRING_COMPARATOR_2 = //
					(s1, s2) -> ((s1 == s2) ? 0
							: ((s1 == null) ? (s2 == null ? 0 : -1) : (s2 == null ? 1 : s1.compareTo(s2))));
	public static final MyComparator<Integer> INTEGER_COMPARATOR = new GenericComparator<>();
	public static final MyComparator<Long> LONG_COMPARATOR = new GenericComparator<>();
	public static final MyComparator<Double> DOUBLE_COMPARATOR = new GenericComparator<>();
	public static final MyComparator<Character> CHAR_COMPARATOR = new GenericComparator<>();
	public static final MyComparator<File> FILE_COMPARATOR = new GenericComparator<File>();
	public static final MyComparator<Color> COLOR_COMPARATOR = (e1, e2) -> {
		if (e1 == e2)
			return 0;
		if (e1 == null)
			return -1;
		if (e2 == null)
			return 1;
		return Integer.compare(e1.getRGB(), e2.getRGB());
	};
	public static final MyComparator<Object> OBJECT_COMPARATOR = (o1, o2) -> {
		if (o1 == o2)
			return 0;
		if (o1 == null)
			return -1;
		if (o2 == null)
			return 1;
		return Integer.compare(o1.hashCode(), o2.hashCode());
	};
	/**
	 * Sorters use natural order, that is, given an array <code>a</code> holding at
	 * least 2 different values <code>x</code> and <code>y</code>:<br>
	 * {@code x < y -> IndexOf(a,x) < IndexOf(a,y) }.<br>
	 * In other words, if <code>x</code> is lower than <code>y</code>, it will be
	 * placed at a lower index than <code>y</code>'s index after the sort
	 * operation.<br>
	 * To let the {@link Point}s to be sorted from greater y-values from lower, then
	 * the y-value must be compared in reverse order.
	 */
	public static final MyComparator<Point> //
	POINT_COMPARATOR_HIGHEST_FIRST = (p1, p2) -> {
		if (p1 == p2)
			return 0;
		if (p1 == null)
			return -1;
		if (p2 == null)
			return 1;
		return Integer.compare(p2.y, p1.y);
	}//
			, POINT_COMPARATOR_LOWEST_FIRST = (p1, p2) -> {
				if (p1 == p2)
					return 0;
				if (p1 == null)
					return -1;
				if (p2 == null)
					return 1;
				return Integer.compare(p1.y, p2.y);
			} //
			, POINT_COMPARATOR_LEFT_FIRST = (p1, p2) -> {
				if (p1 == p2)
					return 0;
				if (p1 == null)
					return -1;
				if (p2 == null)
					return 1;
				return Integer.compare(p1.x, p2.x);
			}//
			, POINT_COMPARATOR_RIGHT_FIRST = (p1, p2) -> {
				if (p1 == p2)
					return 0;
				if (p1 == null)
					return -1;
				if (p2 == null)
					return 1;
				return Integer.compare(p2.x, p1.x);
			}//
			, POINT_COMPARATOR_HIGHEST_LEFTMOST_FIRST = (p1, p2) -> {
				int c;
				if (p1 == p2)
					return 0;
				if (p1 == null)
					return -1;
				if (p2 == null)
					return 1;
				c = POINT_COMPARATOR_HIGHEST_FIRST.compare(p1, p2);
				return (c == 0) ? Integer.compare(p1.x, p2.x) : c;
			};

	public static final MyComparator<Point2D> //
	POINT_2D_COMPARATOR_HIGHEST_FIRST = (p1, p2) -> {
		if (p1 == p2)
			return 0;
		if (p1 == null)
			return -1;
		if (p2 == null)
			return 1;
		return Double.compare(p2.getY(), p1.getY());
	}//
			, POINT_2D_COMPARATOR_LOWEST_FIRST = (p1, p2) -> {
				if (p1 == p2)
					return 0;
				if (p1 == null)
					return -1;
				if (p2 == null)
					return 1;
				return Double.compare(p1.getY(), p2.getY());
			} //
			, POINT_2D_COMPARATOR_LEFT_FIRST = (p1, p2) -> {
				if (p1 == p2)
					return 0;
				if (p1 == null)
					return -1;
				if (p2 == null)
					return 1;
				return Double.compare(p1.getX(), p2.getX());
			}//
			, POINT_2D_COMPARATOR_RIGHT_FIRST = (p1, p2) -> {
				if (p1 == p2)
					return 0;
				if (p1 == null)
					return -1;
				if (p2 == null)
					return 1;
				return Double.compare(p2.getX(), p1.getX());
			}//
			, POINT_2D_COMPARATOR_HIGHEST_LEFTMOST_FIRST = (p1, p2) -> {
				int c;
				if (p1 == p2)
					return 0;
				if (p1 == null)
					return -1;
				if (p2 == null)
					return 1;
				c = POINT_2D_COMPARATOR_HIGHEST_FIRST.compare(p1, p2);
				return (c == 0) ? Double.compare(p1.getX(), p2.getX()) : c;
			}//
			, POINT_2D_COMPARATOR_LOWEST_LEFTMOST_FIRST = (p1, p2) -> {
				int c;
				if (p1 == p2)
					return 0;
				if (p1 == null)
					return -1;
				if (p2 == null)
					return 1;
				c = POINT_2D_COMPARATOR_LOWEST_FIRST.compare(p1, p2);
				return (c == 0) ? Double.compare(p1.getX(), p2.getX()) : c;
			}//
			, POINT_2D_COMPARATOR_HIGHEST_RIGHTMOST_FIRST = (p1, p2) -> {
				int c;
				if (p1 == p2)
					return 0;
				if (p1 == null)
					return -1;
				if (p2 == null)
					return 1;
				c = POINT_2D_COMPARATOR_HIGHEST_FIRST.compare(p1, p2);
				return (c == 0) ? Double.compare(p2.getX(), p1.getX()) : c;
			}//
			, POINT_2D_COMPARATOR_LOWEST_RIGHTMOST_FIRST = (p1, p2) -> {
				int c;
				if (p1 == p2)
					return 0;
				if (p1 == null)
					return -1;
				if (p2 == null)
					return 1;
				c = POINT_2D_COMPARATOR_HIGHEST_FIRST.compare(p1, p2);
				return (c == 0) ? Double.compare(p2.getX(), p1.getX()) : c;
			};

	public static final MyComparator<Comparable<? extends Object>[]> ARRAY_COMPARABLES_COMPARATOR = (a1, a2) -> {
		int i, l1, l2, len, c;
		Comparable<? extends Object> o1, o2;
		if (a1 == a2)
			return 0;
		if (a1 == null)
			return -1;
		if (a2 == null)
			return 1;
		l1 = a1.length;
		l2 = a2.length;
		len = Math.min(l1, l2);
		i = -1;
		while (++i < len) {
			o1 = a1[i];
			o2 = a2[i];
			c = compareComparableObjects(o1, o2);
			if (c != 0)
				return c;
		}
		return Integer.compare(l1, l2);
	};

	/*
	 * public static final Comparator<Long> LONG_COMPARATOR = (e1, e2) -> { if (e1
	 * == e2) return 0; if (e1 == null) return -1; if (e2 == null) return 1; return
	 * e1.compareTo(e2); }; public static final Comparator<Integer> INT_COMPARATOR =
	 * (e1, e2) -> { if (e1 == e2) return 0; if (e1 == null) return -1; if (e2 ==
	 * null) return 1; return e1.compareTo(e2); };
	 */

	//

	// TODO STATIC METHODS

	public static <K> Comparator<List<K>> newListComparator(Comparator<K> keyComparator) {
		return (l1, l2) -> {
			int s1, s2, c;
			Iterator<K> iter1, iter2;
			if (l1 == l2)
				return 0;
			if (l1 == null)
				return -1;
			if (l2 == null)
				return 1;
			s1 = l1.size();
			s2 = l2.size();
			if (s1 == 0) {
				return s2 == 0 ? 0 : -s2;
			} else if (s2 == 0) { return s1; }
			iter1 = l1.iterator();
			iter2 = l2.iterator();
			c = 0;
			while (iter1.hasNext() && iter2.hasNext() && //
			(c = keyComparator.compare(iter1.next(), iter2.next())) == 0) { // already done
			}
			return (c != 0) ? c : (s1 - s2);
		};
	}

	public static <V> int getHighest(V[] array, Comparator<V> comp) {
		int i, len, index;
		V v, vtemp;
		if (array == null || comp == null || (len = array.length) == 0)
			return -1;
		if (len == 1)
			return 0;
		v = array[i = index = 0];
		while (++i < len) {
			if (((vtemp = array[i]) != null) && (comp.compare(vtemp, v) > 0)) {
				v = vtemp;
				index = i;
			}
		}
		return index;
	}

	public static <V> int getLowest(V[] array, Comparator<V> comp) {
		int i, len, index;
		V v, vtemp;
		if (array == null || comp == null || (len = array.length) == 0)
			return -1;
		if (len == 1)
			return 0;
		v = array[i = index = 0];
		while (++i < len) {
			if (((vtemp = array[i]) != null) && (comp.compare(vtemp, v) < 0)) {
				v = vtemp;
				index = i;
			}
		}
		return index;
	}

	@SuppressWarnings("unchecked")
	public static int compareComparableObjects(Comparable<? extends Object> o1, Comparable<? extends Object> o2) {
		int c;
		Class<?> c1, c2;
		Comparable<Object> cc1, cc2;
		if (o1 == o2)
			return 0;
		if (o1 == null)
			return o2.compareTo(null);
		if (o2 == null)
			return o1.compareTo(null);
		System.out.println(o1 + ", " + o2);
		c1 = o1.getClass();
		c2 = o2.getClass();
		if (c1.isAssignableFrom(c2)// o1.compare(o2)
				|| c2.isAssignableFrom(c1)// o1.compare(o2)
		) {
			cc1 = (Comparable<Object>) o1;
			cc2 = (Comparable<Object>) o2;
			c = cc1.compareTo(cc2);
			return c == 0 ? 0 : (c > 0 ? 1 : -1);
		} else
			throw new IllegalArgumentException("The two ginven object cannot be compared");
		// return 0;
	}

	//

	// TODO CLASS

	static class GenericComparator<E extends Comparable<E>> implements Serializable, MyComparator<E> {
		private static final long serialVersionUID = -56106532L;

		@Override
		public int compare(E e1, E e2) {
			if (e1 == e2)
				return 0;
			if (e1 == null)
				return -1;
			if (e2 == null)
				return 1;
			return e1.compareTo(e2);
		}
	}

	// public static int getNullityTest(Objec)
}