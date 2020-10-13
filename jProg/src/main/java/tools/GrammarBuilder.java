package tools;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dataStructures.MapTreeAVL;
import grammar.Grammar;
import grammar.GrammarElement;
import grammar.GrammarRule;
import grammar.GrammarRuleChomskyNF;
import grammar.GrammarRuleMultiRHS;
import grammar.GrammarRuleSimple;
import grammar.RuleElement;
import grammar.RuleElementCompound;
import grammar.RuleElementList;
import grammar.RuleElementPairNonTerminal;
import grammar.RuleElementSingle;

public class GrammarBuilder {
	protected static final Map<String, GrammarElement> cacheGE = MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight,
			Misc.STRING_COMPARATOR);

	public GrammarBuilder() {}

	public static Grammar buildGrammar(LineReader lineReader) {
		Grammar g;
		g = new Grammar();
		lineReader.forEach(line -> {
			if (!(line == null || (line = line.trim()).length() == 0 || line.startsWith("#") || line.isBlank())) {
				// a good line?
				final String lhsRaw;
				String rhsRaw;
				RuleElement lhs, rhs;
				GrammarRule gr;
				String[] hs = line.split("->");
				lhsRaw = hs[0].trim();
				rhsRaw = hs[1].trim();
				lhs = hsToRuleElement(lhsRaw);
				// recycle "hs"
				hs = rhsRaw.split("\\|");
				if (hs.length > 0) {
					int i, len;
					len = hs.length;
					// for each production rule ... "example: S -> A | B C | A ciao B | B B fine ."
					i = -1;
					while (++i < len) {
						rhsRaw = hs[i].trim();
						rhs = hsToRuleElement(rhsRaw);
						switch (rhs.getElementsAmount()) {
						case 1: {
							gr = new GrammarRuleSimple(lhs, rhs);
							break;
						}
						case 2: {
							gr = new GrammarRuleChomskyNF(lhs, (RuleElementCompound) rhs);
							break;
						}
						default: {
							GrammarRuleMultiRHS grMulti;
							gr = grMulti = new GrammarRuleMultiRHS(lhs);
							grMulti.addRhs(rhs);
						}
						}// end switch
						g.addRule(gr);
					} // end while
				} // end if
			}
		});
		return g;
	}

	protected static GrammarElement newge(String ssss) {
		GrammarElement ge;
		if (cacheGE.containsKey(ssss)) {
			ge = cacheGE.get(ssss);
		} else {
			ge = new GrammarElement(ssss);
			cacheGE.put(ssss, ge);
		}
		return ge;
	}

	static String[] filterNonBlank(String[] p) {
		List<String> l;
		l = new LinkedList<>();
		for (String s : p) {
			s = s.trim();
			if (!(s.isBlank() | s.isEmpty())) { l.add(s); }
		}
		return l.toArray(new String[l.size()]);
	}

	protected static RuleElement hsToRuleElement(String hs) {
		RuleElement re;
		String[] parts;
		parts = filterNonBlank(hs.split("\\s+"));

		if (parts.length > 0) {
			if (parts.length == 1) {
				re = new RuleElementSingle(newge(parts[0].trim()));
			} else {
				if (parts.length == 2) {
					re = new RuleElementPairNonTerminal(newge(parts[0].trim()), newge(parts[1].trim()));
				} else {
					int i, len;
					ArrayList<GrammarElement> elems;
					elems = new ArrayList<GrammarElement>(len = parts.length);
					i = -1;
					while (++i < len) {
						elems.add(newge(parts[i].trim()));
					}
					re = new RuleElementList(elems);
				}
			}
		} else
			throw new RuntimeException("So badly formed \"hs\":\n\t:" + hs);
		return re;
	}
}