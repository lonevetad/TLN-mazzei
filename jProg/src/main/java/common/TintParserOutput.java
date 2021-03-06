package common;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import eu.fbk.dh.tint.runner.TintPipeline;
import tools.Misc;
import tools.OutputStreamCollector;

/**
 * Output of a
 * {@link TintPipeline#run(java.io.InputStream, java.io.OutputStream, eu.fbk.dh.tint.runner.TintRunner.OutputFormat)},
 * collected from {@link OutputStream} using the utilities
 * {@link OutputStreamCollector}.
 */
public class TintParserOutput implements Serializable {
	private static final long serialVersionUID = 86540524040541L;
	/** Date of form YYYY-MM-DD */
	protected String docDate;
	protected String timings; // dunno
	protected SentenceTint[] sentences;

//	protected String docDate;

	//

	public String getDocDate() { return docDate; }

	public String getTimings() { return timings; }

	public SentenceTint[] getSentences() { return sentences; }

	public void setDocDate(String docDate) { this.docDate = docDate; }

	public void setTimings(String timings) { this.timings = timings; }

	public void setSentences(SentenceTint[] sentences) { this.sentences = sentences; }

	//

	//

	public static class SentenceTint implements Serializable {
		private static final long serialVersionUID = 86540524040542L;

		public static enum DependencyTypeProduced {
			Basic, Collapsed, CollapsedCcprocessed
		}

		//
		protected int index; // indice (partendo da 1) nell'array "tokens"
		protected int characterOffsetBegin; // estremo INCLUSO
		protected int characterOffsetEnd; // estremo ESCLUSO
		protected String text;
		@JsonAlias({ "basic-dependencies" })
		protected SentenceDependencyTint[] basicDependencies;
		@JsonAlias({ "collapsed-dependencies" })
		protected SentenceDependencyTint[] collapsedDependencies;
		@JsonAlias({ "collapsed-ccprocessed-dependencies" })
		protected SentenceDependencyTint[] collapsedCcprocessedDependencies;
		protected String parse;
		protected SentenceTokenTint[] tokens;

		//

		@JsonGetter("basicDependencies")
		public SentenceDependencyTint[] getBasicDependencies() { return basicDependencies; }

		@JsonGetter("basic-dependencies")
		public SentenceDependencyTint[] getBasicDependencies2() { return basicDependencies; }

		@JsonGetter("collapsedDependencies")
		public SentenceDependencyTint[] getCollapsedDependencies() { return collapsedDependencies; }

		@JsonGetter("collapsed-dependencies")
		public SentenceDependencyTint[] getCollapsedDependencies2() { return collapsedDependencies; }

		@JsonGetter("collapsedCcprocessedDependencies")
		public SentenceDependencyTint[] getCollapsedCcprocessedDependencies() { return collapsedDependencies; }

		@JsonGetter("collapsed-ccprocessed-dependencies")
		public SentenceDependencyTint[] getCollapsedCcprocessedDependencies2() { return collapsedDependencies; }

		public int getIndex() { return index; }

		public int getCharacterOffsetBegin() { return characterOffsetBegin; }

		public int getCharacterOffsetEnd() { return characterOffsetEnd; }

		public String getText() { return text; }

		public String getParse() { return parse; }

		public SentenceTokenTint[] getTokens() { return tokens; }

		// setter

		public void setIndex(int index) { this.index = index; }

		public void setCharacterOffsetBegin(int characterOffsetBegin) {
			this.characterOffsetBegin = characterOffsetBegin;
		}

		public void setCharacterOffsetEnd(int characterOffsetEnd) { this.characterOffsetEnd = characterOffsetEnd; }

		public void setText(String text) { this.text = text; }

		public void setParse(String parse) { this.parse = parse; }

		public void setTokens(SentenceTokenTint[] tokens) { this.tokens = tokens; }

		@JsonSetter("basicDependencies")
		public void setBasicDependencies(SentenceDependencyTint[] basicDependencies) {
			this.basicDependencies = basicDependencies;
		}

		@JsonSetter("basic-dependencies")
		public void setBasicDependencies2(SentenceDependencyTint[] basicDependencies) {
			this.basicDependencies = basicDependencies;
		}

		@JsonSetter("collapsedDependencies")
		public void setCollapsedDependencies(SentenceDependencyTint[] collapsedDependencies) {
			this.collapsedDependencies = collapsedDependencies;
		}

		@JsonSetter("collapsed-dependencies")
		public void setCollapsedDependencies2(SentenceDependencyTint[] collapsedDependencies) {
			this.collapsedDependencies = collapsedDependencies;
		}

		@JsonSetter("collapsedCcprocessedDependencies")
		public void setCollapsedCcprocessedDependencies(SentenceDependencyTint[] collapsedCcprocessedDependencies) {
			this.collapsedCcprocessedDependencies = collapsedCcprocessedDependencies;
		}

		@JsonSetter("collapsed-ccprocessed-dependencies")
		public void setCollapsedCcprocessedDependencies2(SentenceDependencyTint[] collapsedCcprocessedDependencies) {
			this.collapsedCcprocessedDependencies = collapsedCcprocessedDependencies;
		}

		public void forEachToken(Consumer<SentenceTokenTint> action) {
			for (SentenceTokenTint t : tokens)
				action.accept(t);
		}
	}

	//
	public static class SentenceDependencyTint implements Serializable {
		private static final long serialVersionUID = 86540524040543L;
		public static final String GLOSS_ROOT = "ROOT";
		//
		protected String dep; // is it a PoS?
		protected int governor; // ID of the "father node"
		protected String governorGloss; // gloss of the "father node"
		protected int dependent; // works as an ID
		protected String dependentGloss; // gloss of this node

		// getter

		public String getDep() { return dep; }

		public int getGovernor() { return governor; }

		public String getGovernorGloss() { return governorGloss; }

		public int getDependent() { return dependent; }

		public String getDependentGloss() { return dependentGloss; }

		// setter

		public void setDep(String dep) { this.dep = dep; }

		public void setGovernor(int governor) { this.governor = governor; }

		public void setGovernorGloss(String governorGloss) { this.governorGloss = governorGloss; }

		public void setDependent(int dependent) { this.dependent = dependent; }

		public void setDependentGloss(String dependentGloss) { this.dependentGloss = dependentGloss; }
	}

	//
	public static class SentenceTokenTint implements Serializable {
		private static final long serialVersionUID = 86540524040544L;
		/** Starts from 1, index on array "tokens. */
		protected int index;
		protected String word;
		protected String originalText;
		protected String lemma;
		protected int characterOffsetBegin; // estremo INCLUSO
		protected int characterOffsetEnd; // estremo ESCLUSO
		protected String pos;
		protected String featuresText;
		protected String ner;
		protected String full_morpho;
		protected String selected_morpho;
		protected boolean guessed_lemma;
		protected Map<String, Object> features = new TreeMap<>(Misc.STRING_COMPARATOR);;

		public Object getFeature(String featureName) { return this.features.get(featureName); }

		public String[] getFeatureAsArrayString(String featureName) {
			return (String[]) this.features.get(featureName);
		}

		//
//		protected Map<String, String[]> features = new TreeMap<>(Misc.STRING_COMPARATOR);;
//		@JsonAnyGetter
//		public Map<String, String[]> getFeatures() { return this.features; }
//		@JsonAnySetter
//		public void add(String key, String[] value) { features.put(key, value); }
//		public void setFeature(String key, String[] value) { add(key, value); }

		//

		@JsonAnyGetter
		public Map<String, Object> getFeatures() { return this.features; }

		public int getIndex() { return index; }

		public String getWord() { return word; }

		public String getOriginalText() { return originalText; }

		public String getLemma() { return lemma; }

		public int getCharacterOffsetBegin() { return characterOffsetBegin; }

		public int getCharacterOffsetEnd() { return characterOffsetEnd; }

		public String getPos() { return pos; }

		public String getFeaturesText() { return featuresText; }

		public String getNer() { return ner; }

		public String getFull_morpho() { return full_morpho; }

		public String getSelected_morpho() { return selected_morpho; }

		public boolean isGuessed_lemma() { return guessed_lemma; }

// setter

		public void setIndex(int index) { this.index = index; }

		public void setWord(String word) { this.word = word; }

		public void setOriginalText(String originalText) { this.originalText = originalText; }

		public void setLemma(String lemma) { this.lemma = lemma; }

		public void setCharacterOffsetBegin(int characterOffsetBegin) {
			this.characterOffsetBegin = characterOffsetBegin;
		}

		public void setCharacterOffsetEnd(int characterOffsetEnd) { this.characterOffsetEnd = characterOffsetEnd; }

		public void setPos(String pos) { this.pos = pos; }

		public void setFeaturesText(String featuresText) { this.featuresText = featuresText; }

		public void setNer(String ner) { this.ner = ner; }

		public void setFull_morpho(String full_morpho) { this.full_morpho = full_morpho; }

		public void setSelected_morpho(String selected_morpho) { this.selected_morpho = selected_morpho; }

		public void setGuessed_lemma(boolean guessed_lemma) { this.guessed_lemma = guessed_lemma; }

		public void setFeatures(Map<String, Object> features) { this.features = features; }

		//

		@JsonAnySetter
		public void add(String key, Object value) {
			if (value instanceof String) {
				boolean notPut = true;
				if (features.containsKey(key)) {
					Object oldVal;
					oldVal = features.get(key);
					if (oldVal instanceof String[]) {
						String[] old, newSet;
						// add the stuff
						old = (String[]) oldVal;
						newSet = new String[old.length + 1];
						System.arraycopy(old, 0, newSet, 0, old.length);
						newSet[old.length] = (String) value;
						notPut = false;
						features.remove(key);
						features.put(key, newSet);
					}
				}
				if (notPut) { features.put(key, new String[] { (String) value }); }
			} else
				features.put(key, value);
		}

		public void setFeature(String key, Object value) { add(key, value); }

		/*
		 * protected STFueatureTint features; public static class STFueatureTint
		 * implements Serializable{ protected String[] Definite; protected String[]
		 * Gender; protected String[] Number; protected String[] PronType; }
		 */
	}
}