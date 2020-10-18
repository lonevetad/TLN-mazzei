package tools;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import dataStructures.MapTreeAVL;
import dataStructures.SetMapped;

/** Default implementation of {@link NodeComparable} useful for */
public class NodeComparableSynonymIndexed extends NodeComparable.NodeComparableDefaultAlghoritms<SynonymSet>
		implements Stringable, Cloneable {
	private static final long serialVersionUID = 5640520473L;
	protected static final Function<NodeComparableSynonymIndexed, NodeComparable<SynonymSet>> IDENTITY_FUNCTION_JavaCompiler_GENERICS_TOO_RESTRICTIVE = (
			nstd) -> nstd;

	protected static final Function<NodeComparable<SynonymSet>, NodeComparableSynonymIndexed> IDENTITY_FUNCTION_REVERSE_MAPPER = (
			nstd) -> (NodeComparableSynonymIndexed) nstd;

	//

	public NodeComparableSynonymIndexed() { this(new String[] {}); }

	public NodeComparableSynonymIndexed(String[] aaaa) { this(new SynonymSet(aaaa)); }

	public NodeComparableSynonymIndexed(SynonymSet defaultSyn) {
		this.alternatives = defaultSyn;
		instantiatesChildrenStructures();
	}

	public NodeComparableSynonymIndexed(NodeComparableSynonymIndexed original) {// copy constructor
		this.alternatives = original.alternatives;
		instantiatesChildrenStructures();
	}

	//

	protected final SynonymSet alternatives; // the "node key"
	protected Map<SynonymSet, NodeComparableSynonymIndexed> childrenByElemGramm;
	protected Set<NodeComparable<SynonymSet>> childrenByElemGrammBackMap; // the "node children"

	protected void instantiatesChildrenStructures() {
		this.childrenByElemGramm = MapTreeAVL.newMap(MapTreeAVL.Optimizations.MinMaxIndexIteration,
				SynonymSet.SYNONYM_COMPARATOR);
		this.childrenByElemGrammBackMap = new SetMapped<>(
				((MapTreeAVL<SynonymSet, NodeComparableSynonymIndexed>) this.childrenByElemGramm)
						.toSetValue(n -> n.getKeyIdentifier()), //
				// generics type converter, need by the Java Compiler
				IDENTITY_FUNCTION_JavaCompiler_GENERICS_TOO_RESTRICTIVE)
						.setReverseMapper(IDENTITY_FUNCTION_REVERSE_MAPPER);
	}

	//

	@Override
	public SynonymSet getKeyIdentifier() { return this.alternatives; }

	@Override
	public Set<NodeComparable<SynonymSet>> getChildrenNC() { return this.childrenByElemGrammBackMap; }

	@Override
	public NodeComparable<SynonymSet> getChildNCByKey(SynonymSet key) { return this.childrenByElemGramm.get(key); }

	public NodeComparable<SynonymSet> getChildNCBySingleKey(String key) {
		return this.getChildNCByKey(new SynonymSet(new String[] { key }));
	}

	// delegators

	public void addAlternative(String s) { this.alternatives.addAlternative(s); }

	public void removeAlternative(String t) { this.alternatives.removeAlternative(t); }

	public boolean hasAlternative(String t) { return this.alternatives.hasAlternative(t); }

	public boolean canBeIdentifiedBy(SynonymSet eg) { return this.alternatives.areIntersecting(eg); }

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
	protected void toStringNonCollectionFields(StringBuilder sb) {
		sb.append(" --> ");
	}

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

	@Override
	public NodeComparableSynonymIndexed clone() { return new NodeComparableSynonymIndexed(this); }
}