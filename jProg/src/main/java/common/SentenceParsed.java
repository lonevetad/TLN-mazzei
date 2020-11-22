package common;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import common.TintParserOutput.SentenceDependencyTint;
import common.TintParserOutput.SentenceTint;
import common.TintParserOutput.SentenceTokenTint;
import dataStructures.MapTreeAVL;
import dataStructures.SetMapped;
import edu.emory.mathcs.backport.java.util.Arrays;
import tools.Misc;
import tools.Stringable;

/** SINGLE sentence parsed in some way (for instance, using Tint). */
public class SentenceParsed implements Stringable {
	private static final long serialVersionUID = 1L;

	public SentenceParsed(SentenceTint fromTint) {
		root = null;
		buildFromTint(fromTint);
	}

	protected NodeParsedSentFromTint root;

	public NodeParsedSentFromTint getRoot() { return root; }

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
		Map<String, NodeParsedSentFromTint> allPartsAsNode; // indexed by gloss
		NodeParsedSentFromTint currentNode;

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
				NodeParsedSentFromTint father;
				father = allPartsAsNode.get(node.glossFather); // "glossFather" is defined just before
				if (father != null) { node.setFather(father); }
			}
		});

		// 3) enrich with info

		sentence.forEachToken(sentToken -> {
			NodeParsedSentFromTint relatedNode;
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

	/**
	 * Nodo di una frase dopo che essa è stata parsificata da Tint. La struttura
	 * prodotta da Tint è appunto un albero, più precisamente "a dipendenze", e tale
	 * classe ne è il nodo dell'albero. <br>
	 * Questo nodo incorpora, oltre alle informazioni circa le "dipendenze"
	 * sopracitatem da informazioni derivanti dall'analisi di PoS-Tagging (vedasi
	 * {@link #getPos()} e {@link #getFeatures()}).
	 * <p>
	 * Example of a tree:<br>
	 * <code>( Determinant["The"] <- Subject["child"] -> Adjective["little"]) <- Verb[root, "eats"] -> (Object ["apples"]) -> (Adverb ["slowly"])</code>.<br>
	 * (NN.B.: each point comes from a "governor/father" and goes into a
	 * "dependent/child", so one of them is the root.)
	 */
	public abstract class NodeParsedSentFromTint extends NodeParsedSentence {
		private static final long serialVersionUID = -3000078540408L;
		//
		protected final boolean isRoot;
		protected final Integer indexID; // just a simpleID
		protected String glossFather;
		protected NodeParsedSentFromTint father;
		protected Map<Integer, NodeParsedSentFromTint> children; // redundant set of children, identified by their ID

		protected NodeParsedSentFromTint(Integer indexID, String dep, String gloss, String glossFather) {
			super(gloss);
			this.indexID = indexID;
			this.setDep(dep);
			this.glossFather = glossFather;
			this.isRoot = this.checkIsRoot();
			this.father = null;
			this.children = null;
		}

		// getter

		public boolean isRoot() { return this.isRoot; }

		public Integer getIndexID() { return indexID; }

		public String getGlossFather() { return glossFather; }

		@Override
		public NodeParsedSentFromTint getFather() { return father; }

		public Map<Integer, NodeParsedSentFromTint> getChildren() { return children; }

		// setter

		public void setGlossFather(String glossFather) { this.glossFather = glossFather; }

//		public void setFeatures(Map<String, String[]> features) { this.features = features; }

		protected void checkChildren() {
			if (children == null) {
				children = MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight, Misc.INTEGER_COMPARATOR);
			}
		}

		protected void setFather(NodeParsedSentFromTint f) {
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

		protected void addChild(NodeParsedSentFromTint c) {
			checkChildren();
			if (!this.children.containsKey(c.indexID)) {
				this.children.put(c.indexID, c);
				this.addChildNC(c);
				if (c.father == null)
					c.setFather(this);
			}
		}

		public void forEachChild(Consumer<NodeParsedSentFromTint> action) {
			if (this.children != null && children.size() > 0) { children.forEach((idc, c) -> action.accept(c)); }
		}

		public Iterator<NodeParsedSentFromTint> getChildrenIterator() {
			if (this.children == null)
				return null;
			return new SetMapped<>(this.children.entrySet(), e -> e.getValue()).iterator();
		}

		protected abstract boolean checkIsRoot();

		//

		public String toMiniString() { return indexID + "-" + gloss; }

		@Override
		public String toString() {
			StringBuilder sb;
			sb = new StringBuilder();
			toString(sb);
			return sb.toString();
		}

		@Override
		public void toString(StringBuilder sb, int level) {
			boolean[] isNotFirst = { false };
			addTab(sb, level, false);
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
			if (!this.getChildrenNC().isEmpty()) {
				int lev = level + 1;
				this.forEachChild(//
						(k) -> {
//						if (isNotFirst[0]) {
//							sb.append(", ");
//						} else {
//							isNotFirst[0] = true;
//						}
//						sb.append(k.toMiniString());
							sb.append('\n');
							k.toString(sb, lev);
						});
				addTab(sb, level, false);
			}
			sb.append(']');
		}
	}

	//

	protected class NDTTint extends NodeParsedSentFromTint {
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

	protected void toStringNode(StringBuilder sb, NodeParsedSentFromTint n, int level) {
//		final int newLevel;
		if (n == null) {
			sb.append("null");
			return;
		}
		sb.append('\n').append('\n');
		addTab(sb, level, false);
		n.toString(sb);
//		sb.append(n.toString());
//		newLevel = level + 1;
//		n.forEachChild((child) -> toStringNode(sb, child, newLevel));
	}

//	@Override
//	public void toString(StringBuilder sb, int tabLevel) {
//		addTab(sb, tabLevel);
//		sb.append(this.toString());
//	}

//	protected void addTab(StringBuilder sb, int tabLevel) { addTab(sb, tabLevel, true); }
//
//	protected void addTab(StringBuilder sb, int tabLevel, boolean newLineNeeded) {
//		if (sb != null) {
//			if (newLineNeeded)
//				sb.append('\n');
//			sb.ensureCapacity(sb.length() + tabLevel);
//			while (tabLevel-- > 0) {
//				sb.append('\t');
//			}
//		}
//	}
}