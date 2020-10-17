package common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import tools.SynonymSet;

public enum DependentTags {
	nsubj, nsubjpass, nmod, sub,
	//
	aux, auxpass,
	//
	adv, advmod, amod,
	//
	det, detPoss("det:poss"),
	//
	prep,
	//
	dobj, obj,
	//
	mod, mod_temp,
	//
	compound, case_("case"), cop, comp_ind, neg, arg, punc, comp_temp, comp_loc
	//
	;

	private final String tintName;

	DependentTags() { this.tintName = name(); }

	DependentTags(String t) { this.tintName = t; }

	public String getTintName() { return tintName; }

	private static final Map<String, DependentTags> TAGS_BY_TINT;
	static {
		DependentTags[] v;
		v = values();
		TAGS_BY_TINT = new HashMap<String, DependentTags>(v.length);
		for (DependentTags dt : v)
			TAGS_BY_TINT.put(dt.tintName, dt);
	}

	public static DependentTags getTagByTintTag(String tintTag) { return TAGS_BY_TINT.get(tintTag); }

	public static DependentTags getFirstTagMatching(SynonymSet eg) {
		DependentTags dt;
		final Iterator<String> iter;
		dt = null;
		iter = eg.iterator();
		while (iter.hasNext() && ((dt = getTagByTintTag(iter.next())) == null))
			;
		return dt;
	}
}