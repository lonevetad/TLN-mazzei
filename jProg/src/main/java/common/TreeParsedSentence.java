package common;

import java.util.Map;
import java.util.function.Consumer;

import common.TintParserOutput.SentenceDependencyTint;
import common.TintParserOutput.SentenceTint;
import dataStructures.MapTreeAVL;
import tools.Misc;

/** Do it for a SINGLE sentence. */
public class TreeParsedSentence {

	public TreeParsedSentence(SentenceTint fromTint) {
		root = null;
		buildFromTint(fromTint);
	}

	protected NodeDependencyTree root;

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

		// first collect all nodes, then wire them

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

		// done :D
	}

	//

	// class

	//

	protected abstract class NodeDependencyTree {
		protected final boolean isRoot;
		protected final Integer indexID; // just a simpleID
		protected final String dep; // took from Tint
		protected final String gloss;
		protected String glossFather;
		protected NodeDependencyTree father;
		protected Map<Integer, NodeDependencyTree> children;

		protected NodeDependencyTree(Integer indexID, String dep, String gloss, String glossFather) {
			super();
			this.indexID = indexID;
			this.dep = dep;
			this.gloss = gloss;
			this.glossFather = glossFather;
			this.isRoot = this.checkIsRoot();
			this.father = null;
			this.children = null;
		}

		protected boolean isRoot() { return this.isRoot; }

		protected void checkChildren() {
			if (children == null)
				children = MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight, Misc.INTEGER_COMPARATOR);
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

		protected void forEachChild(Consumer<NodeDependencyTree> action) {
			if (this.children != null && children.size() > 0) { children.forEach((idc, c) -> action.accept(c)); }
		}

		protected abstract boolean checkIsRoot();

		public String toMiniString() { return indexID + "-" + gloss; }

		@Override
		public String toString() {
			StringBuilder sb;
			sb = new StringBuilder();
			sb.append(this.getClass().getSimpleName());
			sb.append(" [isRoot=");
			sb.append(isRoot ? 'T' : 'F');
			sb.append(", indexID=");
			sb.append(indexID);
			sb.append(", dep=");
			sb.append(dep);
			sb.append(", gloss=");
			sb.append(gloss);
			sb.append(", father=");
			if (isRoot || father == null) {
				sb.append("none :D");
			} else {
				sb.append(father.indexID);
				sb.append("-");
				sb.append(father.gloss);
			}
			sb.append(", children= [");
			this.forEachChild(//
					(k) -> sb.append(k.toMiniString())
//					sb::append
			);
			sb.append(']');
			return sb.toString();
		}

	}

	//

	protected class NDTTint extends NodeDependencyTree {

		protected NDTTint(Integer indexID, String dep, String gloss, String glossFather) {
			super(indexID, dep, gloss, glossFather);
		}

		@Override
		protected boolean checkIsRoot() { return SentenceDependencyTint.GLOSS_ROOT.equals(this.glossFather); }
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
		sb.append('\n');
		sb.append(n.toString());
		addTab(sb, level);
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