package tools;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.function.Consumer;

import dataStructures.MapTreeAVL;
import translators.secondWay.TransferTranslationRuleBased;

/**
 * A set of String representing the same concept.
 * <p>
 * Could be a PoS-Tag or a dependency. For instance, an "object" could be "obj",
 * "dobj" (direct object) or something else. Similarly to "adjective": "adj",
 * "amod", etc.
 */
public class SynonymSet {
	public static final Comparator<SynonymSet> SYNONYM_COMPARATOR = (eg1, eg2) -> {
		// boolean equal;
		boolean equal;
		int c;
		Iterator<String> i1, i2;
		Comparator<String> comp;
//		Iterator<String> i1, i2;
//		SynonymSet intersection;
		// Comparator<String> comp;
		if (eg1 == eg2)
			return 0;
		if (eg1 == null)
			return -1;
		if (eg2 == null)
			return 1;
		/*
		 * since synonyms are sortable and this set is sorted, then compare the elements
		 * in order
		 */
		i1 = eg1.backMap.iteratorKey();
		i2 = eg2.backMap.iteratorKey();
		equal = true;
		comp = Misc.STRING_COMPARATOR;
		c = 0;
		while (equal && i1.hasNext() && i2.hasNext()) {
			equal = (c = comp.compare(i1.next(), i2.next())) == 0;
		}
		return c != 0 ? c : Integer.compare(eg1.backMap.size(), eg2.backMap.size());
	}, //
			SYNONYM_COMPARATOR_SUBSET_RELATION = (eg1, eg2) -> {
				int c, size2, intersCount;
				if (eg1 == eg2)
					return 0;
				if (eg1 == null)
					return -1;
				if (eg2 == null)
					return 1;
				/*
				 * then, it depends on "subset" relation: the superset is the greatest (collapse
				 * "identity" and "non-subset & non-empty-intersection" onto the same category)
				 */
				c = eg2.backMap.size();
				size2 = eg2.backMap.size();
				if (c == 0) {
					return size2 == 0 ? 0 : -1;
				} else if (size2 == 0)
					return 1;
				// they are not empty ..
				intersCount = eg1.intersectionSize(eg2);
				if (c == intersCount)
					return size2 == intersCount ? 0 : -1; // eg2 is equal or superset
				else if (size2 == intersCount)
					return c == intersCount ? 0 : 1; // eg1 is equal or subset
				else
					return 0; // just a simple intersection
			};

	public SynonymSet(String[] alternatives) {
		this();
		for (String s : alternatives) {
			addAlternative(s);
		}
	}

	protected SynonymSet() {
		backMap = MapTreeAVL.newMap(MapTreeAVL.Optimizations.MinMaxIndexIteration, Misc.STRING_COMPARATOR);
		this.alternatives = backMap.toSetKey();
	}

	protected MapTreeAVL<String, String> backMap;
	protected SortedSet<String> alternatives; // THE ALTERNATIVEEEEEEEEEES

	public int countAlternatives() { return backMap.size(); }

	public boolean contains(String lal) { return alternatives.contains(lal); }

	public void forEach(Consumer<String> action) { alternatives.forEach(action); }

	public List<String> toList() { return Collections.unmodifiableList(backMap.toList()); }

	public Iterator<String> iterator() { return alternatives.iterator(); }

	public boolean hasAlternative(String t) { return this.backMap.containsKey(t); }

	/** USE WITH CAUTION! */
	public void addAlternative(String s) {
		int i;
		MapTreeAVL<String, String> a = this.backMap;
		a.put(s, s);
		i = s.indexOf(':');
		if (i >= 0) { // something like "adj:poss" -> take "adj" also
			s = s.substring(0, i).trim();
			if (!s.isEmpty()) { a.put(s, s); }
		}
	}

	/** USE WITH CAUTION! */
	public void removeAlternative(String t) { this.backMap.remove(t); }

	/**
	 * Computes if there are at least one "alternatives" in common (i.e., those
	 * ElementGrammarWithAlternatives are applicable in the context of
	 * {@link TransferTranslationRuleBased}).
	 */
	public boolean areIntersecting(SynonymSet eg) {
		int s1, s2;
		if (eg == this)
			return true;
		if (eg == null ||
		// se uno è empty -> return false
				((s1 = this.backMap.size()) > 0) != (((s2 = eg.backMap.size()) > 0)))
			return false;
		// since the sets are sorted .. check extremes
		if (Misc.STRING_COMPARATOR.compare(this.backMap.lastKey(), eg.backMap.firstKey()) < 0
				|| Misc.STRING_COMPARATOR.compare(eg.backMap.lastKey(), this.backMap.firstKey()) < 0) {
			return false;
		}
		// basically, compute an intersection.. if they intersects -> true
		if (s1 > s2) {// the tiniest check over the "less than linear" bigger
			for (String s : this.alternatives) {
				if (eg.contains(s))
					return true;
			}
		} else {
			for (String s : eg.alternatives) {
				if (this.contains(s))
					return true;
			}
		}
		return false;
	}

	/**
	 * Count how many items on this set are contained inside the given one, i.e.
	 * invokes {@link #intersectionSize(SynonymSet)} (this method is just a
	 * synonym).
	 */
	public int countIntersectionWith(SynonymSet eg) { return intersectionSize(eg); }

	public SynonymSet intersectionWith(SynonymSet eg) {
		SynonymSet smallerSet, inters;
		MapTreeAVL<String, String> biggerSetMap, intersMap;
		if (eg == null)
			return null;
		if (eg == this)
			return this;
		inters = new SynonymSet();
		intersMap = inters.backMap;
		/*
		 * since iterating is O(n) and "containsKey" is O(log(n)), iterates over the
		 * smallest set
		 */
		if (this.backMap.size() <= eg.backMap.size()) {
			smallerSet = this;
			biggerSetMap = eg.backMap;
		} else {
			smallerSet = eg;
			biggerSetMap = this.backMap;
//			eg=this;
		}
		smallerSet.backMap.forEach((s, s_) -> {
			if (biggerSetMap.containsKey(s))
				intersMap.put(s, s_);
		});
		return inters;
	}

	/** Computes the intersection with the given set and returns its size. */
	public int intersectionSize(SynonymSet eg) {
//		return intersectionWith(eg).countAlternatives();
		// instead of computing the intersection, just count each "intersecting element"
		SynonymSet smallerSet;
		MapTreeAVL<String, String> biggerSetMap;
		int[] countIntersections = { 0 };
		/*
		 * since iterating is O(n) and "containsKey" is O(log(n)), iterates over the
		 * smallest set
		 */
		if (this.backMap.size() <= eg.backMap.size()) {
			smallerSet = this;
			biggerSetMap = eg.backMap;
		} else {
			smallerSet = eg;
			biggerSetMap = this.backMap;
//			eg=this;
		}
		smallerSet.backMap.forEach((s, s_) -> {
			if (biggerSetMap.containsKey(s))
				countIntersections[0]++;
		});
		return countIntersections[0];
	}

	/**
	 * Test if this set is subset of the given one.<br>
	 * No null pointer checks performed.
	 */
	public boolean isSubsetOf(SynonymSet eg) {
		int thisSize;
		if ((thisSize = backMap.size()) == 0)
			return true;
		if (thisSize > eg.countAlternatives())
			return false;
		return thisSize == intersectionSize(eg);
	}

	@Override
	public String toString() {
		StringBuilder sb;
		sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}

	public void toString(StringBuilder sb) {
		boolean[] b = { false };
		sb.append("EG: [");
		this.backMap.forEach((k, v) -> {
			if (b[0]) {
				sb.append(", ");
			} else {
				b[0] = true;
			}
			sb.append(k);
		});
		sb.append(']');
	}
}