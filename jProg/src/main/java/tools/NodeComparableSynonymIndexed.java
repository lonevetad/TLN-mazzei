package tools;

import java.util.Comparator;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;

import dataStructures.MapTreeAVL;
import dataStructures.NodeComparable;
import dataStructures.SortedSetEnhanced;

/** Default implementation of {@link NodeComparable} useful for */
public class NodeComparableSynonymIndexed extends NodeComparable.NodeComparableDefaultAlghoritms<SynonymSet>
		implements Stringable, Cloneable {
	private static final long serialVersionUID = 5640520473L;

	protected static final Function<NodeComparableSynonymIndexed, NodeComparable<SynonymSet>> IDENTITY_FUNCTION_JavaCompiler_GENERICS_TOO_RESTRICTIVE = (
			nstd) -> nstd;
	protected static final Function<NodeComparable<SynonymSet>, NodeComparableSynonymIndexed> IDENTITY_FUNCTION_REVERSE_MAPPER = (
			nstd) -> (NodeComparableSynonymIndexed) nstd;
	protected static final Function<NodeComparable<SynonymSet>, SortedSetEnhanced<NodeComparable<SynonymSet>>> NODE_TO_CHILD_CONVERTER = NodeComparable<SynonymSet>::getChildrenNC;

	// cose statiche interessanti
	/** THE REAL {@link Comparator} for this class. */
	public static final Comparator<NodeComparable<SynonymSet>> COMPARATOR_NODE = NodeComparable
			.newNodeComparatorDefault(SynonymSet.COMPARATOR_SYNONYM_SET);
	/**
	 * Intermediate step: a comparator for
	 * {@link NodeComparableSynonymIndexed#getChildrenNC().}
	 */
	public static final Comparator<SortedSetEnhanced<NodeComparable<SynonymSet>>> COMPARATOR_CHILDREN = SortedSetEnhanced.COMPARATOR_FACTORY_PREFERRED
			.newComparator(COMPARATOR_NODE);
	/** Intermediate step */
	protected static final DifferenceCalculator<SortedSetEnhanced<NodeComparable<SynonymSet>>> DIFF_COMPUTER_CHILD_LOW_LEVEL = SortedSetEnhanced
			.differenceCalcFromSetComparator(COMPARATOR_CHILDREN);
	/** THE REAL {@link DifferenceCalculator} for this class. */
	public static final DifferenceCalculator<NodeComparable<SynonymSet>> DIFF_COMPUTER_NODE = (n1, n2) -> {
		if (n1 == n2)
			return 0;
		if (n1 == null)
			return Integer.MIN_VALUE;
		if (n2 == null)
			return Integer.MAX_VALUE;
		long d;
		d = SynonymSet.DIFFERENCE_CALCULATOR.getDifference(n1.getKeyIdentifier(), n2.getKeyIdentifier());
		if (d != 0)
			return d;
		return DIFF_COMPUTER_CHILD_LOW_LEVEL.getDifference(n1.getChildrenNC(), n2.getChildrenNC());
	};
	/** THE REAL {@link CloserGetter} for this class. */
	public static final CloserGetter<NodeComparable<SynonymSet>> CLOSER_GETTER_NCSI = (norig, n1, n2) -> CloserGetter
			.getCloserTo(norig, DIFF_COMPUTER_NODE, n1, n2);

	//

	//

	//

	public NodeComparableSynonymIndexed() { this(new String[] {}); }

	public NodeComparableSynonymIndexed(String[] aaaa) { this(new SynonymSet(aaaa)); }

	public NodeComparableSynonymIndexed(SynonymSet defaultSyn) {
		this.alternatives = defaultSyn;
		instantiatesChildrenStructures();
	}

	/** It does not add children. */
	public NodeComparableSynonymIndexed(NodeComparableSynonymIndexed original) {// copy constructor
		this.alternatives = original.alternatives;
		instantiatesChildrenStructures();
	}

	//

	protected final SynonymSet alternatives; // the "node key"
	/**
	 * A "radix-graph" or similar (range-trees?) would have been better than a
	 * simple map
	 */
//	protected Map<SynonymSet, NodeComparable<SynonymSet>> childrenBySynonymsBackMap;
//	protected Set<NodeComparable<SynonymSet>> childrenBySynonyms; // the "node children"
	protected MapTreeAVL<NodeComparable<SynonymSet>, NodeComparable<SynonymSet>> childrenBySynonymsBackMap;
	protected SortedSetEnhanced<NodeComparable<SynonymSet>> childrenBySynonyms; // the "node children"

	protected void instantiatesChildrenStructures() {
		this.childrenBySynonymsBackMap = this.newChildrenBackmap(COMPARATOR_NODE);
		this.childrenBySynonyms = this.newChildrenSet(this.childrenBySynonymsBackMap);
	}

	//

	@Override
	public NodeComparableSynonymIndexed clone() { return new NodeComparableSynonymIndexed(this); }

	@Override
	public Comparator<SynonymSet> getKeyComparator() { return SynonymSet.COMPARATOR_SYNONYM_SET; }

	@Override
	public SortedSetEnhanced<NodeComparable<SynonymSet>> getChildrenNC() { return this.childrenBySynonyms; }

	@Override
	public NodeComparable<SynonymSet> getChildNCMostSimilarTo(NodeComparable<SynonymSet> copy) {
		ClosestMatch<Entry<NodeComparable<SynonymSet>, NodeComparable<SynonymSet>>> cm;
		ClosestMatch<NodeComparable<SynonymSet>> cmN;
		if (this.childrenBySynonymsBackMap.isEmpty())
			return null;
		cm = this.childrenBySynonymsBackMap.closestMatchOf(copy);
		if (cm == null) {
			System.out.println("TRYING TO FIND copy " + copy);
			System.out.println("HERE:");
			this.childrenBySynonymsBackMap.forEach((n, nn) -> System.out.println(n));
			throw new RuntimeException("cm = this.childrenBySynonymsBackMap.closestMatchOf(copy); ---> null in "
					+ this.getClass().getName());
		}
		// but the closest match needs to be conferted .. .-.
		cmN = cm.convertTo(COMPARATOR_NODE, e -> e == null ? null : e.getKey()); // Entry::getKey
		// ready to get the real closest match
		return cmN.getClosetsMatchToOriginal(CLOSER_GETTER_NCSI);
	}

	@Override
	public SynonymSet getKeyIdentifier() { return this.alternatives; }

	// DEPRECATED : ALREADY IMPLEMENTED IN CHILDREN'S COMPARATOR
//	@Override
//	public NodeComparable<SynonymSet> getChildNCByKey(SynonymSet key) {
//		NodeComparable<SynonymSet> c, bestChild;
//		Entry<SynonymSet, NodeComparable<SynonymSet>> entryIter;
//		Iterator<Entry<SynonymSet, NodeComparable<SynonymSet>>> iterChildren;
//		if (this.childrenBySynonymsBackMap.isEmpty())
//			return null;
//		c = this.childrenBySynonymsBackMap.get(key);
//		if (c != null) // well contained
//			return c;
//		// not fond? -> scan
//		iterChildren = ((MapTreeAVL<SynonymSet, NodeComparable<SynonymSet>>) this.childrenBySynonymsBackMap).iterator();
//		// V1
////		while (iterChildren.hasNext()) {
////			entryIter = iterChildren.next();
////			if (entryIter.getKey().areIntersecting(key))
////				return entryIter.getValue();
////		}
//		/**
//		 * "Interesting" order:
//		 * <ol>
//		 * <li>intersection size</li>
//		 * <li>lowest missing</li>
//		 * <li>lowest exceeding</li>
//		 * </ol>
//		 */
//		// V2
//		if (!iterChildren.hasNext())
//			return null;
//		int bestMissingElements; // circa l'opposto di "precision"
//		int bestExceedingElements; // circa l'opposto di "recall"
//		int bestIntersSize, intersectionSize, missElem, excElem;
//		SynonymSet synChild;
//		// set the starting point: the first child ever
//		bestChild = iterChildren.next().getValue();
////		scoreBest= bestChild.scoreKeyCompatibilityWith(key);
//		synChild = bestChild.getKeyIdentifier();
//		bestIntersSize = intersectionSize = synChild.intersectionSize(key);
//		bestMissingElements = synChild.countAlternatives() - intersectionSize;
//		bestExceedingElements = key.countAlternatives() - intersectionSize;
//		// then search for the best
//		while (iterChildren.hasNext()) {
//			entryIter = iterChildren.next();
//			c = entryIter.getValue();
//			synChild = c.getKeyIdentifier();
//			intersectionSize = synChild.intersectionSize(key);
//			missElem = synChild.countAlternatives() - intersectionSize;
//			excElem = key.countAlternatives() - intersectionSize;
//			// first check the "subset" relation
//			if (intersectionSize > bestIntersSize || //
//					intersectionSize == bestIntersSize && ((missElem < bestMissingElements) || //
//							(missElem == bestMissingElements && excElem < bestExceedingElements)//
//					)//
//			) {
//				// update
//				bestChild = c;
//				bestMissingElements = missElem;
//				bestExceedingElements = excElem;
//				bestIntersSize = intersectionSize;
//			}
//		}
//		return bestIntersSize == 0 ? null : bestChild;
//	}

	public NodeComparable<SynonymSet> getChildNCMostSimilarTo(SynonymSet key) {
		return this.getChildNCMostSimilarTo(key, NodeComparableSynonymIndexed::new);
	}

	public NodeComparable<SynonymSet> getChildNCBySingleKey(String key) {
		return this.getChildNCMostSimilarTo(new SynonymSet(new String[] { key }), NodeComparableSynonymIndexed::new);
	}

	@Override
	public void forEachChildNCFIFOOrdering(Consumer<NodeComparable<SynonymSet>> action) {
//((	MapTreeAVLFull<>	)this.childrenBySynonymsBackMap).fo
		this.childrenBySynonymsBackMap.forEach(MapTreeAVL.ForEachMode.Queue, e -> action.accept(e.getKey()));
	}

	// delegators

	public NodeComparableSynonymIndexed addAlternative(String s) {
		this.alternatives.addAlternative(s);
		return this;
	}

	public NodeComparableSynonymIndexed addAlternatives(SynonymSet s) {
		s.forEach(this.alternatives::addAlternative);
		return this;
	}

	public void removeAlternative(String t) { this.alternatives.removeAlternative(t); }

	public boolean hasAlternative(String t) { return this.alternatives.hasAlternative(t); }

	public boolean canBeIdentifiedBy(SynonymSet eg) { return this.alternatives.areIntersecting(eg); }

	//

	@Override
	public long scoreKeyCompatibilityWith(SynonymSet anotherKey) {
		// TODO: use the synonym's comparator or not?
		// return this.alternatives.countDifferenceWith(anotherKey);
		return this.alternatives.intersectionSize(anotherKey);
	}

	//

	@Override
	public String toString() {
		StringBuilder sb;
		sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}

	@Override
	public void toString(StringBuilder sb) { this.toString(sb, 0); }

	/** Call <code>super</code> before doing anything. */
	@Override
	public void toStringNonCollectionFields(StringBuilder sb) { sb.append(" --> "); }

	@Override
	public void toString(StringBuilder sb, int level) {
		int lev;
		addTab(sb, level, false);
		sb.append("NStD: ");
		this.alternatives.toString(sb);
		toStringNonCollectionFields(sb);
		sb.append(" -----> (children:)");
		lev = level + 1;
		this.forEachChildNC((child) -> {
			if (child instanceof NodeComparableSynonymIndexed) {
				sb.append('\n');
				((NodeComparableSynonymIndexed) child).toString(sb, lev);
			}
		});
	}
}