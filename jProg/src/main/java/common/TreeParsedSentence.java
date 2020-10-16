package common;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import common.TintParserOutput.SentenceDependencyTint;
import common.TintParserOutput.SentenceTint;
import common.TintParserOutput.SentenceTokenTint;
import dataStructures.MapTreeAVL;
import dataStructures.SetMapped;
import edu.emory.mathcs.backport.java.util.Arrays;
import tools.Misc;
import tools.NodeComparable;

/** Do it for a SINGLE sentence. */
public class TreeParsedSentence {

	public TreeParsedSentence(SentenceTint fromTint) {
		root = null;
		buildFromTint(fromTint);
	}

	protected NodeDependencyTree root;

	public NodeDependencyTree getRoot() { return root; }

	//

	// ora si utilizza l'albero ... eh eh

	//

	//

	// TODO BUILDER

	//

	protected void buildFromTint(SentenceTint sentence) {
		buildFromTint(sentence, SentenceTint.DependencyTypeProduced.Basic);
	}

	protected void buildFromTint(SentenceTint sentence, SentenceTint.DependencyTypeProduced dtp) {

		SentenceDependencyTint[] dependencies;
//		Map<String, SentenceDependencyTint> allParts;
		Map<String, NodeDependencyTree> allPartsAsNode; // indexed by gloss
		NodeDependencyTree currentNode;

		//
		if (dtp == null)
			dtp = SentenceTint.DependencyTypeProduced.Basic;
		switch (dtp) {
		case Basic:
			dependencies = sentence.basicDependencies;
			break;
		case Collapsed:
			dependencies = sentence.collapsedDependencies;
			break;
		case CollapsedCcprocessed:
			dependencies = sentence.collapsedCcprocessedDependencies;
			break;
		default:
			dependencies = sentence.basicDependencies;
			break;
		}
		allPartsAsNode = MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight, Misc.STRING_COMPARATOR);

		// first collect all nodes, then wire them, then enrich with "token"
		// informations

		// 1) produce node
		for (SentenceDependencyTint sdt : dependencies) {
			currentNode = new NDTTint(sdt.dependent, sdt.dep, sdt.dependentGloss, sdt.governorGloss);
			// set the root, just in case
			if (currentNode.isRoot) { this.root = currentNode; }
			allPartsAsNode.put(currentNode.gloss, currentNode);
		}

		// 2) wire them up
		allPartsAsNode.forEach((glossNode, node) -> {
			if (!node.isRoot) {
				NodeDependencyTree father;
				father = allPartsAsNode.get(node.glossFather); // "glossFather" is defined just before
				if (father != null) { node.setFather(father); }
			}
		});

		// 3) enrich with info

		sentence.forEachToken(sentToken -> {
			NodeDependencyTree relatedNode;
			// it's required to retrieve the node... start using "originalText" field
			relatedNode = allPartsAsNode.get(sentToken.originalText);
			if (relatedNode == null) { // if it fails, try with "word" field
				relatedNode = allPartsAsNode.get(sentToken.word);
			}
			if (relatedNode != null) { // got it?
				NDTTint nnnn;
				nnnn = (NDTTint) relatedNode;
				nnnn.setFromTintToken(sentToken);
			}
		});

		// done :D
	}

	//

	// class

	//

	public abstract class NodeDependencyTree extends NodeComparable.NodeComparableDefaultAlghoritms<Integer> {
		private static final long serialVersionUID = -3000078540408L;
		protected final boolean isRoot;
		protected final Integer indexID; // just a simpleID
		protected final String dep; // (took from Tint' "dependency".) -> USEFUL FOR CONVERSION ""
		protected final String gloss;
		protected String lemma;
		protected String pos; // (took from Tint' "tokens".) the pos-tag, sometimes similar to "dep".
		protected String glossFather;
		protected NodeDependencyTree father;
		protected Map<Integer, NodeDependencyTree> children;
		protected Set<NodeComparable<Integer>> childrenBackMap;
		protected Map<String, String[]> features;

		protected NodeDependencyTree(Integer indexID, String dep, String gloss, String glossFather) {
			super();
			this.indexID = indexID;
			this.dep = dep;
			this.gloss = gloss;
			this.glossFather = glossFather;
			this.isRoot = this.checkIsRoot();
			this.father = null;
			this.children = null;
			this.features = null;
		}

		// getter

		public boolean isRoot() { return this.isRoot; }

		public Integer getIndexID() { return indexID; }

		public String getDep() { return dep; }

		public String getGloss() { return gloss; }

		public String getGlossFather() { return glossFather; }

		public NodeDependencyTree getFather() { return father; }

		public Map<Integer, NodeDependencyTree> getChildren() { return children; }

		public Map<String, String[]> getFeatures() { return features; }

		public String getLemma() { return lemma; }

		public String getPos() { return pos; }

		@Override
		public Integer getKeyIdentifier() { return indexID; }

		@Override
		public Set<NodeComparable<Integer>> getChildrenNC() { return null; }

		@Override
		public NodeComparable<Integer> getChildNCByKey(Integer key) { return this.children.get(key); }

		// setter

		public void setLemma(String lemma) { this.lemma = lemma; }

		public void setPos(String pos) { this.pos = pos; }

		public void setGlossFather(String glossFather) { this.glossFather = glossFather; }

		public void setFeatures(Map<String, String[]> features) { this.features = features; }

		protected void checkChildren() {
			if (children == null) {
				children = MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight, Misc.INTEGER_COMPARATOR);
				childrenBackMap = new SetMapped<NodeDependencyTree, NodeComparable<Integer>>(
						((MapTreeAVL<Integer, NodeDependencyTree>) children).toSetValue(n -> n.getKeyIdentifier()), //
						// generics type converter, need by the Java Compiler
						(ndt) -> { return (NodeComparable<Integer>) ndt; });
			}
		}

		protected void checkFreature() {
			if (features == null)
				this.features = MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight, Misc.STRING_COMPARATOR);
		}

		protected void setFather(NodeDependencyTree f) {
			if (this.father != null)
				return;
			this.father = f;
			if (f != null) {
				this.glossFather = f.gloss;
				f.addChild(this);
			} else {
				this.glossFather = null;
			}
		}

		protected void addChild(NodeDependencyTree c) {
			checkChildren();
			if (!this.children.containsKey(c.indexID)) {
				this.children.put(c.indexID, c);
				if (c.father == null)
					c.setFather(this);
			}
		}

		public void forEachChild(Consumer<NodeDependencyTree> action) {
			if (this.children != null && children.size() > 0) { children.forEach((idc, c) -> action.accept(c)); }
		}

		public Iterator<NodeDependencyTree> getChildrenIterator() {
			if (this.children == null)
				return null;
			return new SetMapped<>(this.children.entrySet(), e -> e.getValue()).iterator();
		}

		protected abstract boolean checkIsRoot();

		public void addFeatures(String featureName, String[] featureValuess) {
			checkFreature();
			this.features.put(featureName, featureValuess);
		}

		public void addFeatures(String featureName, Object featureVal) {
			if (featureVal instanceof String[]) {
				addFeatures(featureName, (String[]) featureVal);
			} else if (featureVal instanceof String) {
				addFeatures(featureName, new String[] { (String) featureVal });
			} else if (featureVal instanceof Collection<?>) {
				Collection<?> c;
				c = ((Collection<?>) featureVal);
				addFeatures(featureName, c.toArray(new String[c.size()]));
			} else if (featureVal instanceof Iterable<?>) {
				Iterable<?> i;
				LinkedList<String> l;
				i = (Iterable<?>) featureVal;
				l = new LinkedList<>();
				for (Object o : i) {
					if (o instanceof String)
						l.add((String) o);
				}
				addFeatures(featureName, l.toArray(new String[l.size()]));
			}
		}

		public void forEachFeature(BiConsumer<String, String[]> action) {
			if (this.features != null && features.size() > 0) { features.forEach(action); }
		}

		//
		public String toMiniString() { return indexID + "-" + gloss; }

		@Override
		public String toString() {
			boolean[] isNotFirst = { false };
			StringBuilder sb;
			sb = new StringBuilder();
			sb.append(this.getClass().getSimpleName());
			sb.append(" [");
			sb.append("gloss= ");
			sb.append(gloss);
			sb.append(", pos= ");
			sb.append(pos);
			sb.append(" , lemma=");
			sb.append(lemma);
			sb.append(" , indexID=");
			sb.append(indexID);
			sb.append(", dep= ");
			sb.append(dep);
			sb.append(" , isRoot=");
			sb.append(isRoot ? 'T' : 'F');
			sb.append(", father=");
			if (isRoot || father == null) {
				sb.append("none :D");
			} else {
				sb.append(father.toMiniString());
			}
			sb.append(", features= [");
			this.forEachFeature(//
					(featureName, feature) -> {
						if (isNotFirst[0]) {
							sb.append(", ");
						} else {
							isNotFirst[0] = true;
						}
						sb.append(featureName).append('-').append(Arrays.toString(feature));
					});
			isNotFirst[0] = false;
			sb.append(", children= [");
			this.forEachChild(//
					(k) -> {
						if (isNotFirst[0]) {
							sb.append(", ");
						} else {
							isNotFirst[0] = true;
						}
						sb.append(k.toMiniString());
					});
			sb.append(']');
			return sb.toString();
		}

	}

	//

	protected class NDTTint extends NodeDependencyTree {
		private static final long serialVersionUID = 258632L;

		protected NDTTint(Integer indexID, String dep, String gloss, String glossFather) {
			super(indexID, dep, gloss, glossFather);
		}

		@Override
		protected boolean checkIsRoot() { return SentenceDependencyTint.GLOSS_ROOT.equals(this.glossFather); }

		protected void setFromTintToken(SentenceTokenTint token) {
			super.setLemma(token.lemma);
			token.features.forEach((featureName, featureVal) -> { super.addFeatures(featureName, featureVal); });
			super.setPos(token.pos);
		}
	}

	//

	//

	//

	@Override
	public String toString() {
		StringBuilder sb;
		sb = new StringBuilder();
		sb.append("TreeParsedSentence\nroot:");
		toStringNode(sb, root, 0);
		return sb.toString();
	}
	//

	protected void toStringNode(StringBuilder sb, NodeDependencyTree n, int level) {
		final int newLevel;
		if (n == null) {
			sb.append("null");
			return;
		}
		sb.append('\n').append('\n');
		addTab(sb, level, false);
		sb.append(n.toString());
		newLevel = level + 1;
		n.forEachChild((child) -> toStringNode(sb, child, newLevel));
	}

	public void toString(StringBuilder sb, int tabLevel) {
		addTab(sb, tabLevel);
		sb.append(this.toString());
	}

	protected void addTab(StringBuilder sb, int tabLevel) { addTab(sb, tabLevel, true); }

	protected void addTab(StringBuilder sb, int tabLevel, boolean newLineNeeded) {
		if (sb != null) {
			if (newLineNeeded)
				sb.append('\n');
			sb.ensureCapacity(sb.length() + tabLevel);
			while (tabLevel-- > 0) {
				sb.append('\t');
			}
		}
	}
}