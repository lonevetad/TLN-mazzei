package tools.impl;

import java.util.Iterator;

import dataStructures.CollectionAlteringCosts;
import dataStructures.CollectionAlteringCosts.ActionCost;
import tools.EditDistance;
import tools.IterableSized;

public class EditDistanceLevenshtein implements EditDistance {
	@Override
	public <T> int editDistance(T[] firstSequence, T[] secondSequence, EqualityChecker<T> equalityChecker,
			CollectionAlteringCosts<T> cac) {
		boolean isRowHoldingResult;
		int lf, ls, m, t;
		int[] row, prevRow, temp;
		T elemFromFirst, elemFromSecond;
		final ActionCost<T> ic, dc;
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
			ic = cac::deleteCost;
			dc = cac::insertCost;
		} else {
			ic = cac::insertCost;
			dc = cac::deleteCost;
		}
		row = new int[ls + 1];
		prevRow = new int[ls + 1];
//		for (int c = 0; c <= ls; c++) {
//			prevRow[c] = c;
//		}
		prevRow[0] = 0;
		for (int pc = 0, c = 1; c <= ls; c++, pc++) {
//			prevRow[c] =c;
			prevRow[c] = ((int) dc.getCost(firstSequence[pc])) + prevRow[pc];
		}
		isRowHoldingResult = true;
		for (int r = 1; r <= lf; r++) {
			elemFromFirst = firstSequence[r - 1];
//			row[0] = r;
			row[0] = prevRow[0] + (int) ic.getCost(elemFromFirst);
			for (int c = 1; c <= ls; c++) {
				if (equalityChecker.isEqual(elemFromFirst, elemFromSecond = secondSequence[c - 1])) {
					row[c] = prevRow[c - 1];
				} else {
//					m = row[c - 1]; // insert
//					m = (m < (t = prevRow[c]) ? m : t); // remove
//					row[c] = (m < (t = prevRow[c - 1]) ? m : t) + 1; // replace and then set the value
					m = row[c - 1] + (int) ic.getCost(elemFromSecond); // insert
					m = (m < (t = prevRow[c] + (int) dc.getCost(elemFromFirst)) ? m : t); // remove
					row[c] = (m < (t = prevRow[c - 1] + (int) cac.renameCost(elemFromFirst, elemFromSecond)) ? m : t);
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
	@Override
	public <T> int editDistance(IterableSized<T> firstSequence, IterableSized<T> secondSequence,
			EqualityChecker<T> equalityChecker, CollectionAlteringCosts<T> cac) {
		boolean isRowHoldingResult;
		int lf, ls, m, t;
		int[] row, prevRow, temp;
		Iterator<T> iterFirst, iterSecond;
		T elemFromFirst, elemFromSecond;
		final ActionCost<T> ic, dc;
		lf = firstSequence.size();
		ls = secondSequence.size();
		if (ls == 0)
			return lf;
		else if (lf == 0)
			return ls;
		// let the second sequence be the longest to optimize the memory usage
		if (lf < ls) { // swap
			IterableSized<T> tt;
			t = lf;
			lf = ls;
			ls = t;
			tt = firstSequence;
			firstSequence = secondSequence;
			secondSequence = tt;
			ic = cac::deleteCost;
			dc = cac::insertCost;
		} else {
			ic = cac::insertCost;
			dc = cac::deleteCost;
		}
		row = new int[ls + 1];
		prevRow = new int[ls + 1];
		prevRow[0] = 0;
		iterFirst = firstSequence.iterator();
		for (int pc = 0, c = 1; c <= ls; c++, pc++) {
			prevRow[c] = ((int) dc.getCost(iterFirst.next())) + prevRow[pc];
		}
		isRowHoldingResult = true;
		iterFirst = firstSequence.iterator();
		for (int r = 1; r <= lf; r++) {
			elemFromFirst = iterFirst.next(); // firstSequence.get(r - 1);
			row[0] = prevRow[0] + (int) ic.getCost(elemFromFirst);
			iterSecond = secondSequence.iterator();
			for (int c = 1; c <= ls; c++) {
				if (equalityChecker.isEqual(elemFromFirst, elemFromSecond = iterSecond.next())) {
					row[c] = prevRow[c - 1];
				} else {
					m = row[c - 1] + (int) ic.getCost(elemFromSecond); // insert
					m = (m < (t = prevRow[c] + (int) dc.getCost(elemFromFirst)) ? m : t); // remove
					row[c] = (m < (t = prevRow[c - 1] + (int) cac.renameCost(elemFromFirst, elemFromSecond)) ? m : t);
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
}