package tools;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import dataStructures.MapTreeAVL;
import dataStructures.SetMapped;

/** Default implementation of {@link NodeComparable} useful for */
public class NodeComparableSynonymIndexed extends NodeComparable.NodeComparableDefaultAlghoritms<SynonymSet>
		implements Stringable {
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

	protected final SynonymSet alternatives; // the "node key"
	protected Map<SynonymSet, NodeComparableSynonymIndexed> childrenByElemGramm;
	protected Set<NodeComparable<SynonymSet>> childrenByElemGrammBackMap; // the "node children"

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
	public void toString(StringBuilder sb) { this.toString(sb, 0); }

	@Override
	public void toString(StringBuilder sb, int level) {
		int lev;
		addTab(sb, level, false);
		sb.append("NStD: ");
		this.alternatives.toString(sb);
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