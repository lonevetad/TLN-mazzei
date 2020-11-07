package tools;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import dataStructures.MapTreeAVL;
import dataStructures.SortedSetEnhanced;
import dataStructures.minorUtils.SortedSetEnhancedDelegating;

// TODO GENERALIZE TO A SORTED-SET
/**
 * A set of String representing the same concept.
 * <p>
 * Could be a PoS-Tag or a dependency. For instance, an "object" could be "obj",
 * "dobj" (direct object) or something else. Similarly to "adjective": "adj",
 * "amod", etc.
 */
public class SynonymSet implements SortedSetEnhancedDelegating<String>, Cloneable {

	public static final Comparator<String> COMPARATOR_SINGLE_SYNONYM = Misc.STRING_COMPARATOR;
	/**
	 * Synonyms are set of strings, this comparator is a "low level" comparator used
	 * in {@link #COMPARATOR_SYNONYM_SET}.
	 */
	protected static final Comparator<SortedSetEnhanced<String>> COMP_SET_STRING = //
//			SortedSetEnhanced.ComparatorFactoriesSSE.CASCADE_OF_INTERSECT_MISS_EXCEED_KEY
			SortedSetEnhanced.COMPARATOR_FACTORY_PREFERRED//
					.newComparator(COMPARATOR_SINGLE_SYNONYM);
	/** Low-level difference calculator: of set of strings */
	protected static final DifferenceCalculator<SortedSetEnhanced<String>> DIFF_CALC_SET_STRINGS = SortedSetEnhanced
			.differenceCalcFromSetComparator(COMP_SET_STRING);

	// THE MAIN STATIC STUFFS

	/** Comparator of this class */
	public static final Comparator<SynonymSet> COMPARATOR_SYNONYM_SET = (s1, s2) -> {
		return COMP_SET_STRING.compare(s1.alternatives, s2.alternatives);
	};
	/** Difference computer of synonyms. */
	public static final DifferenceCalculator<SynonymSet> DIFFERENCE_CALCULATOR =
	// all of the following are the same, equally correct solution
	/*
	 * (s1, s2) -> DIFF_CALC_SET_STRINGS .getDifference(s1.alternatives,
	 * s2.alternatives)
	 */
//			DIFF_CALC_SET_STRINGS::getDifference//
			COMPARATOR_SYNONYM_SET::compare//
	;
	public static final CloserGetter<SynonymSet> CLOSER_GETTER = (s1, s2, s3) -> CloserGetter.getCloserTo(s1,
			DIFFERENCE_CALCULATOR, s2, s3);

	//

	public SynonymSet(String... alternatives) {
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
	protected SortedSetEnhanced<String> alternatives; // THE ALTERNATIVEEEEEEEEEES

	@Override
	public SortedSetEnhanced<String> getDelegator() { return alternatives; }

	@Override
	public Comparator<String> getKeyComparator() { return Misc.STRING_COMPARATOR; }

	public int countAlternatives() { return backMap.size(); }

	public boolean contains(String lal) { return alternatives.contains(lal); }

	@Override
	public void forEach(Consumer<? super String> action) { alternatives.forEach(action); }

	public List<String> toList() { return Collections.unmodifiableList(backMap.toList()); }

	@Override
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

	@Override
	public SortedSetEnhanced<String> newSortedSetEnhanced(Comparator<String> comp) { return new SynonymSet(); }

	@Override
	public String toString() {
		StringBuilder sb;
		sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}

	public void toString(StringBuilder sb) {
		boolean[] b = { false };
		sb.append("Synonyms: [");
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

	@Override
	public SynonymSet clone() {
		SynonymSet s;
		s = new SynonymSet();
		this.backMap.forEach((k, v) -> s.addAlternative(k));
		return s;
	}

	//

	//

	//

	@Override
	public ClosestMatch<String> closestMatchOf(String key) { return this.alternatives.closestMatchOf(key); }
}