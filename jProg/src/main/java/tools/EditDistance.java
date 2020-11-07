package tools;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class EditDistance {
	public static interface EqualityChecker<T> {
		public boolean isEqual(T e1, T e2);

		public static <K> EqualityChecker<K> fromComparator(Comparator<K> comp) {
			return (e1, e2) -> comp.compare(e1, e2) == 0;
		}
	}

	/** It uses the Levenshtein distance. */
	public static <T> int editDistance(T[] firstSequence, T[] secondSequence, EqualityChecker<T> equalityChecker) {
		boolean isRowHoldingResult;
		int lf, ls, m, t;
		int[] row, prevRow, temp;
		T elemFromFirst;
		lf = firstSequence.length;
		ls = secondSequence.length;
		if (ls == 0)
			return lf;
		else if (lf == 0)
			return ls;
		// let the second sequence be the longest to optimize the memory usage
		if (lf < ls) { // swap
			T[] tt;
			t = lf;
			lf = ls;
			ls = t;
			tt = firstSequence;
			firstSequence = secondSequence;
			secondSequence = tt;
		}
		row = new int[ls + 1];
		prevRow = new int[ls + 1];
		for (int c = 0; c <= ls; c++) {
			prevRow[c] = c;
		}
		isRowHoldingResult = true;
		for (int r = 1; r <= lf; r++) {
			row[0] = r;
			elemFromFirst = firstSequence[r - 1];
			for (int c = 1; c <= ls; c++) {
				if (equalityChecker.isEqual(elemFromFirst, secondSequence[c - 1])) {
					row[c] = prevRow[c - 1];
				} else {
					m = row[c - 1]; // insert
					m = (m < (t = prevRow[c]) ? m : t); // remove
					row[c] = (m < (t = prevRow[c - 1]) ? m : t) + 1; // replace and then set the value
				}
			}
			// swap
			temp = prevRow;
			prevRow = row;
			row = temp;
			isRowHoldingResult = !isRowHoldingResult;
		}
		return prevRow[ls];
	}

//	/**
//	 * If the access is of the given {@link List} does NOT offer good <i>random access</i> performances, then
//	 * {@link #editDistance(Object[], Object[], EqualityChecker)} should be used
//	 * instead.
//	 */
	public static <T> int editDistance(List<T> firstSequence, List<T> secondSequence,
			EqualityChecker<T> equalityChecker) {
		boolean isRowHoldingResult;
		int lf, ls, m, t;
		int[] row, prevRow, temp;
		Iterator<T> iterFirst, iterSecond;
		T elemFromFirst;
		lf = firstSequence.size();
		ls = secondSequence.size();
		if (ls == 0)
			return lf;
		else if (lf == 0)
			return ls;
		// let the second sequence be the longest to optimize the memory usage
		if (lf < ls) { // swap
			List<T> tt;
			t = lf;
			lf = ls;
			ls = t;
			tt = firstSequence;
			firstSequence = secondSequence;
			secondSequence = tt;
		}
		row = new int[ls + 1];
		prevRow = new int[ls + 1];
		for (int c = 0; c <= ls; c++) {
			prevRow[c] = c;
		}
		isRowHoldingResult = true;
		iterFirst = firstSequence.iterator();
		for (int r = 1; r <= lf; r++) {
			row[0] = r;
			elemFromFirst = iterFirst.next(); // firstSequence.get(r - 1);
			iterSecond = secondSequence.iterator();
			for (int c = 1; c <= ls; c++) {
				if (equalityChecker.isEqual(elemFromFirst, iterSecond.next())) { // secondSequence.get(c - 1)
					row[c] = prevRow[c - 1];
				} else {
					m = row[c - 1]; // insert
					m = (m < (t = prevRow[c]) ? m : t); // remove
					row[c] = (m < (t = prevRow[c - 1]) ? m : t) + 1; // replace and then set the value
				}
			}
			// swap
			temp = prevRow;
			prevRow = row;
			row = temp;
			isRowHoldingResult = !isRowHoldingResult;
		}
		return prevRow[ls];
	}

	/** Only insertion, deletion and substitution are the allowed operations. */
	public static <T> int levenshtein(T[] firstSequence, T[] secondSequence, EqualityChecker<T> equalityChecker) {
		return editDistance(firstSequence, secondSequence, equalityChecker);
	}
}