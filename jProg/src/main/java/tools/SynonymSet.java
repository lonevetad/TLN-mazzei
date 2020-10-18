package tools;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
		int c, size2;
		Iterator<String> i1, i2;
		// Comparator<String> comp;
		if (eg1 == eg2)
			return 0;
		if (eg1 == null)
			return -1;
		if (eg2 == null)
			return 1;
		if (eg1.areIntersecting(eg2))
			return 0;
		size2 = eg1.bm.size();
		if ((c = eg2.bm.size()) == 0 && 0 == size2) {
			return 0; // nothing to comoare
		}
		if (c == 0)
			return -1;
		if (size2 == 0)
			return 1;
		i1 = eg1.bm.iteratorKey();
		i2 = eg2.bm.iteratorKey();
		return Misc.STRING_COMPARATOR.compare(i1.next(), i2.next());
//		equal = true;
//		comp = Misc.STRING_COMPARATOR;
//		c = 0;
//		while (equal && i1.hasNext() && i2.hasNext()) {
//			equal = (c = comp.compare(i1.next(), i2.next())) == 0;
//		}*/
//		return c;

	};

	public SynonymSet(String[] alternatives) {
		bm = MapTreeAVL.newMap(MapTreeAVL.Optimizations.MinMaxIndexIteration, Misc.STRING_COMPARATOR);
		this.alternatives = bm.toSetKey();
		for (String s : alternatives) {
			addAlternative(s);
		}
	}

	protected MapTreeAVL<String, String> bm;
	protected Set<String> alternatives; // THE ALTERNATIVEEEEEEEEEES

	public boolean contains(String lal) { return alternatives.contains(lal); }

	public void forEach(Consumer<String> action) { alternatives.forEach(action); }

	public List<String> toList() { return Collections.unmodifiableList(bm.toList()); }

	public Iterator<String> iterator() { return alternatives.iterator(); }

	public boolean hasAlternative(String t) { return this.bm.containsKey(t); }

	/** USE WITH CAUTION! */
	public void addAlternative(String s) {
		int i;
		MapTreeAVL<String, String> a = this.bm;
		a.put(s, s);
		i = s.indexOf(':');
		if (i >= 0) { // something like "adj:poss" -> take "adj" also
			s = s.substring(0, i).trim();
			if (!s.isEmpty()) { a.put(s, s); }
		}
	}

	/** USE WITH CAUTION! */
	public void removeAlternative(String t) { this.bm.remove(t); }

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
				((s1 = this.bm.size()) > 0) != (((s2 = eg.bm.size()) > 0)))
			return false;
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
		this.bm.forEach((k, v) -> {
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