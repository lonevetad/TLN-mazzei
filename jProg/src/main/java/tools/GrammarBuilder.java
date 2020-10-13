package tools;

import java.util.ArrayList;

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
						rhsRaw = hs[0];
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

	protected static RuleElement hsToRuleElement(String hs) {
		RuleElement re;
		String[] parts;
		parts = hs.split("\\s+");
		if (parts.length > 0) {
			if (parts.length == 1) {
				re = new RuleElementSingle(new GrammarElement(parts[0].trim()));
			} else {
				if (parts.length == 2) {
					re = new RuleElementPairNonTerminal(new GrammarElement(parts[0].trim()),
							new GrammarElement(parts[1].trim()));
				} else {
					int i, len;
					ArrayList<GrammarElement> elems;
					elems = new ArrayList<GrammarElement>(len = parts.length);
					i = -1;
					while (++i < len) {
						elems.add(new GrammarElement(parts[i].trim()));
					}
					re = new RuleElementList(elems);
				}
			}
		} else
			throw new RuntimeException("So badly formed \"hs\":\n\t:" + hs);
		return re;
	}
}