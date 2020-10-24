package tools;

import java.util.Map;
import java.util.Set;

import dataStructures.MapTreeAVL;

/**
 * Enhance {@link NodeComparableSynonymIndexed#getChildNCBySingleKey(String)} at
 * the expense of memory usage and time required for executing
 * {@link #addChildNC(NodeComparable)}.
 */
//*  @deprecated because {@link #getChildNCByKey(SynonymSet)}
public class NodeComparableSynonymIndexed_v2 extends NodeComparableSynonymIndexed {
	private static final long serialVersionUID = 58244716398L;

	@Override
	protected void instantiatesChildrenStructures() {
		super.instantiatesChildrenStructures();
		this.childrenByEachSynonyms = MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight, Misc.STRING_COMPARATOR);
	}

	protected Map<String, NodeComparable<SynonymSet>> childrenByEachSynonyms;

	//

	@Override
	public NodeComparable<SynonymSet> addChildNC(NodeComparable<SynonymSet> child) {
		Set<NodeComparable<SynonymSet>> children;
		if (child == null)
			return this;
		children = getChildrenNC();
		if (children == null) { return this; }
		children.add(child);
		child.getKeyIdentifier().forEach(syn -> { childrenByEachSynonyms.put(syn, child); });
		return this;
	}

	@Override
	public NodeComparable<SynonymSet> getChildNCBySingleKey(String key) { return this.childrenByEachSynonyms.get(key); }

//	@Override
//	public NodeComparable<SynonymSet> getChildNCByKey(SynonymSet key) {
//		NodeComparable<SynonymSet> c;
////		Entry<SynonymSet, NodeComparable<SynonymSet>> entryIter;
////		Iterator<Entry<SynonymSet, NodeComparable<SynonymSet>>> iterChildren;
//		Iterator<String> iterSyn;
//		c = this.childrenBySynonymsBackMap.get(key);
//		if (c != null) // well contained
//			return c;
//		iterSyn = key.iterator();
//		while (c == null && iterSyn.hasNext()) {
//			c = this.childrenByEachSynonyms.get(iterSyn.next());
//		}
//		return c;
//	}
}