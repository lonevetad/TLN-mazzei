package common;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.BiConsumer;

import dataStructures.MapTreeAVL;
import tools.Misc;
import tools.NodeComparableSynonymIndexed;
import tools.SynonymSet;

/**
 * Node of a parsed sentence, holding some informations took from the PoS-Tag
 * analysis and dependency analysis.
 */
public class NodeParsedSentence extends NodeComparableSynonymIndexed {
	private static final long serialVersionUID = -3000078540407L;

	//

	public NodeParsedSentence(String gloss) {
		super();
		this.gloss = gloss;
	}

	public NodeParsedSentence(SynonymSet defaultSyn) { super(defaultSyn); }

	public NodeParsedSentence(NodeParsedSentence original) { // copy constructor
		super(original);
		this.gloss = original.gloss;
		this.lemma = original.lemma;
		this.dep = original.dep;
		this.pos = original.pos;
		this.features = null;
		checkFeatures();
		original.features.forEach(this::addFeatures);
	}

	protected String gloss;
	protected String lemma;
	protected String dep; // "dependency tag", inspired from Tint's "dependency". USEFUL FOR CONVERSION ""
	protected String pos; // "PoS tag", inspired from Tint's "tokens".
	protected Map<String, String[]> features;

	//

	public String getGloss() { return gloss; }

	public String getLemma() { return lemma; }

	public String getDep() { return dep; }

	public String getPos() { return pos; }

	public Map<String, String[]> getFeatures() { return features; }

	//

	public void setLemma(String lemma) { this.lemma = lemma; }

	public void setGloss(String gloss) { this.gloss = gloss; }

	public void setDep(String dep) {
		super.removeAlternative(this.dep);
		this.dep = dep;
		super.addAlternative(dep);
	}

	public void setPos(String pos) {
		super.removeAlternative(this.pos);
		this.pos = pos;
		super.addAlternative(pos);
	}

	//

	// SOMETHING ELSE

	//

	protected void checkFeatures() {
		if (features == null)
			this.features = MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight, Misc.STRING_COMPARATOR);
	}

	//

	public void addFeatures(String featureName, String[] featureValuess) {
		checkFeatures();
		if (this.features.containsKey(featureName)) {
			int lenOld, lenNew;
			String[] prev, newFeats;
			prev = this.features.get(featureName);
			lenOld = prev.length;
			lenNew = featureValuess.length;
			newFeats = new String[lenOld + lenNew];
			System.arraycopy(prev, 0, newFeats, 0, lenOld);
			System.arraycopy(featureValuess, 0, newFeats, lenOld, lenNew);
			featureValuess = newFeats;
		}
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

	@Override
	public NodeParsedSentence clone() { return new NodeParsedSentence(this); }
}